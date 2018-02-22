package vin.kell.newegg.watch;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;


import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;


public class Watch {
	public static void main(String[] args) {
		if (args.length < 4) {
			System.out.println("Need params: email_sender password keyword watch_price");
			System.exit(-1);
		}
		
		File file = new File("subsribers.txt");
		if(!file.exists()) {
			System.out.println("No one subscribe the watch.");
			System.exit(-2);
		}
		
		ArrayList<String> toArray = new ArrayList<>();

		try {
			FileInputStream fileInputStream = new FileInputStream(file);
			BufferedReader reader = new BufferedReader(new InputStreamReader(fileInputStream));

			String line = null;
			while((line=reader.readLine())!=null) {				
				toArray.add(line);
			}
			reader.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		String[] tos = new String[toArray.size()];
		tos = toArray.toArray(tos);
		
		Watch watch = new Watch();
		ArrayList<Item> items = watch.getItems(args[2], Double.parseDouble(args[3]));
		if (items.size() > 0) {
			StringBuilder builder = new StringBuilder();
			for (Item item : items) {
				log("%s\n%.2f\n%s", item.getProductName(), item.getPrice(), item.linkToBuy);
				String msg = String.format("%s\n%.2f\n%s", item.getProductName(), item.getPrice(), item.linkToBuy);
				builder.append(msg);
				builder.append("\n");
				builder.append("============================================\n");
			}
			watch.sendEmails(args[0], args[1], tos, builder.toString());
		}
		else {
			System.out.println("No items matches your price.");
		}
	}

	public static void log(String format, Object... params) {
		System.out.println(String.format(format, params));
	}

	public void sendEmails(String from, String password, String[] tos, String msg) {
		String username = from;
		Properties props = new Properties();
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.smtp.host", "smtp.gmail.com");
		props.put("mail.smtp.port", "587");

		Session session = Session.getInstance(props, new javax.mail.Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(username, password);
			}
		});

		try {

			Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress(from));
			StringBuilder builder = new StringBuilder();
			for (String to : tos) {
				builder.append(to);
				builder.append(",");
			}
			InternetAddress[] addresses = InternetAddress.parse(builder.toString(), true);
			message.setRecipients(Message.RecipientType.TO, addresses);
			message.setSubject("Newegg has videocards for you");
			message.setText(msg);

			Transport.send(message);

			System.out.println("Done");

		} catch (MessagingException e) {
			throw new RuntimeException(e);
		}
	}

	public ArrayList<Item> getItems(String keyword, double watch_price) {
		ArrayList<Item> items = new ArrayList<>();
		try {
			Document doc = Jsoup.connect(
					"https://www.newegg.com/Product/ProductList.aspx?Submit=ENE&DEPA=0&Order=BESTMATCH&Description="
							+ keyword + "&ignorear=0&N=-1&isNodeId=1")
					.get();
			System.out.println(doc.title());
			Elements itElements = doc.select("div[class=item-container]");
			for (Element itemDiv : itElements) {
				Item item = new Item();
				item.price = 9999999.99;
				/****** Filter Price ********/
				Element price = itemDiv.selectFirst("li[class=price-current]");
				if (price == null)
					continue;
				Element intPart = price.selectFirst("strong");
				Element decimalPart = price.selectFirst("sup");
				if (intPart == null || decimalPart == null)
					continue;
				String priceString = intPart.text() + decimalPart.text();
				item.setPrice(Double.parseDouble(priceString.replace(",", "")));
				if (item.price > watch_price)
					continue;

				/****** Filter Stock info ********/
				Element inStock = itemDiv.selectFirst("p[class=item-promo]");
				item.setInStock(!inStock.text().contains("OUT"));
				if (!item.isInStock())
					continue;
				Element brand = itemDiv.selectFirst("a[class=item-brand]");
				if (brand != null)
					item.setBrand(brand.selectFirst("img").attr("title"));
				Element title = itemDiv.selectFirst("a[class=item-title]");
				if (title != null) {
					item.setLinkToBuy(title.attr("href").toString());
					item.setProductName(title.text());
				}
				if (item.getProductName().contains("8GB"))
					item.setMemsize("8GB");
				else if (item.getProductName().contains("4GB"))
					item.setMemsize("4GB");
				else {
					item.setMemsize("Other");
				}
				if (item.getProductName().contains("570"))
					item.setModel("RX570");
				else if (item.getProductName().contains("580"))
					item.setModel("RX580");
				else {
					item.setModel("Other");
				}
				item.setOc(item.getProductName().contains(" OC "));

				items.add(item);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return items;
	}

}
