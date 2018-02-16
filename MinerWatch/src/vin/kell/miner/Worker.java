package vin.kell.miner;

import java.util.ArrayList;

public class Worker {
	private String name;
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	private ArrayList<Miner> miners;
	
	public Worker() {
		setMiners(new ArrayList<>());
		setName("");
	}

	public ArrayList<Miner> getMiners() {
		return miners;
	}

	private void setMiners(ArrayList<Miner> miners) {
		this.miners = miners;
	}
	
	public boolean need_reboot() {
		for (Miner miner : miners) {
			if(miner.getHashrate() < Miner.MinHash) {
				return true;
			}
		}
		return false;
	}
}
