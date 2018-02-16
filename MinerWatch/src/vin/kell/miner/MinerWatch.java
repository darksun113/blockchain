package vin.kell.miner;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Set;

import org.apache.http.ConnectionReuseStrategy;
import org.apache.http.HttpException;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.impl.DefaultBHttpClientConnection;
import org.apache.http.impl.DefaultConnectionReuseStrategy;
import org.apache.http.message.BasicHttpRequest;
import org.apache.http.protocol.HttpCoreContext;
import org.apache.http.protocol.HttpProcessor;
import org.apache.http.protocol.HttpProcessorBuilder;
import org.apache.http.protocol.HttpRequestExecutor;
import org.apache.http.protocol.RequestConnControl;
import org.apache.http.protocol.RequestContent;
import org.apache.http.protocol.RequestExpectContinue;
import org.apache.http.protocol.RequestTargetHost;
import org.apache.http.protocol.RequestUserAgent;
import org.apache.http.util.EntityUtils;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;


import java.util.*;
import javax.mail.*;
import javax.mail.internet.*;

public class MinerWatch {

	public Worker[] getWorks(String url) throws HttpException, IOException {
		HttpProcessor httpproc = HttpProcessorBuilder.create().add(new RequestContent()).add(new RequestTargetHost())
				.add(new RequestConnControl()).add(new RequestUserAgent("Test/1.1"))
				.add(new RequestExpectContinue(true)).build();

		HttpRequestExecutor httpexecutor = new HttpRequestExecutor();

		HttpCoreContext coreContext = HttpCoreContext.create();
		HttpHost host = new HttpHost(url, 80);
		coreContext.setTargetHost(host);

		DefaultBHttpClientConnection conn = new DefaultBHttpClientConnection(8 * 1024);
		ConnectionReuseStrategy connStrategy = DefaultConnectionReuseStrategy.INSTANCE;
		ArrayList<Worker> list_workers = new ArrayList<>();
		try {

			String target = "/?json=yes";

			if (!conn.isOpen()) {
				Socket socket = new Socket(host.getHostName(), host.getPort());
				conn.bind(socket);
			}
			BasicHttpRequest request = new BasicHttpRequest("GET", target);
			System.out.println(">> Request URI: " + request.getRequestLine().getUri());

			httpexecutor.preProcess(request, httpproc, coreContext);
			HttpResponse response = httpexecutor.execute(request, conn, coreContext);
			httpexecutor.postProcess(response, httpproc, coreContext);

			System.out.println("<< Response: " + response.getStatusLine());
			String returnText = EntityUtils.toString(response.getEntity());
			//System.out.println(returnText);
			JSONParser parser = new JSONParser();
			Object obj = parser.parse(returnText);
			JSONObject jobj = (JSONObject)obj;
			JSONObject workers = (JSONObject)jobj.get("rigs");
			Set rigs = workers.keySet();
			for (Object rig : rigs) {
				JSONObject jworker = (JSONObject)workers.get(rig);
				Worker worker = new Worker();
				worker.setName((String)rig);
				//String[] temp_token = jworker.get("temp").toString().split(" ");
				String[] hash_token = jworker.get("miner_hashes").toString().split(" ");
				for (String hash_str : hash_token) {
					Miner miner = new Miner();
					miner.setHashrate(Double.parseDouble(hash_str));
					//System.out.println(miner.getHashrate());
					worker.getMiners().add(miner);
				}
				list_workers.add(worker);
			}
			
			System.out.println("==============");
			if (!connStrategy.keepAlive(response, coreContext)) {
				conn.close();
			} else {
				System.out.println("Connection kept alive...");
			}
			
			
		} catch (ParseException e) {
			System.out.println("position: " + e.getPosition());
			System.out.println(e);
		} finally {
			conn.close();
			System.out.println("Connection closed...");
		}
		Worker[] return_workers = new Worker[list_workers.size()];
		return list_workers.toArray(return_workers);
	}
	
	public void sendEmail(String[] to, String from, String text, String pass) {
		Properties props = System.getProperties();
        String host = "smtp.gmail.com";
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", host);
        props.put("mail.smtp.user", from);
        props.put("mail.smtp.password", pass);
        props.put("mail.smtp.port", "587");
        props.put("mail.smtp.auth", "true");

        Session session = Session.getDefaultInstance(props);
        MimeMessage message = new MimeMessage(session);

        try {
            message.setFrom(new InternetAddress(from));
            InternetAddress[] toAddress = new InternetAddress[to.length];

            // To get the array of addresses
            for( int i = 0; i < to.length; i++ ) {
                toAddress[i] = new InternetAddress(to[i]);
            }

            for( int i = 0; i < toAddress.length; i++) {
                message.addRecipient(Message.RecipientType.TO, toAddress[i]);
            }

            String subject = "Notification - Miner is down.";
			message.setSubject(subject );
            message.setText(text);
            Transport transport = session.getTransport("smtp");
            transport.connect(host, from, pass);
            transport.sendMessage(message, message.getAllRecipients());
            transport.close();
        }
        catch (AddressException ae) {
            ae.printStackTrace();
        }
        catch (MessagingException me) {
            me.printStackTrace();
        }
        
	}

	public static void main(String[] args) throws HttpException, IOException {
		if (args.length < 3) {
			System.err.println("Need a url, email, password");
		}
		
		
		
		MinerWatch watch = new MinerWatch();
		String[] to = {"darksun113@gmail.com"};
		
		Worker[] workers = watch.getWorks(args[0]);
		for (Worker worker : workers) {
			if(worker.need_reboot()) {
				String message = String.format("Miner \"%s\" is down. Please reboot.", worker.getName());
				watch.sendEmail(to, args[1], message, args[2]);
			}
		}
	}

}
