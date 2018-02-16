package vin.kell.miner;

public class Miner {
	public static final double MinHash = 21.0;
	
	private double temp;
	private double hashrate;

	public double getTemp() {
		return temp;
	}

	public double getHashrate() {
		return hashrate;
	}

	public void setHashrate(double hashrate) {
		this.hashrate = hashrate;
	}

	public void setTemp(double temp) {
		this.temp = temp;
	}
	
}
