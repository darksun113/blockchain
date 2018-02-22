package vin.kell.newegg.watch;

public class Item {
	String productName;
	String model;
	String brand;
	String memsize;
	boolean oc;
	double price;
	boolean inStock;
	String linkToBuy;
	
	public String getLinkToBuy() {
		return linkToBuy;
	}
	public void setLinkToBuy(String linkToBuy) {
		this.linkToBuy = linkToBuy;
	}
	public String getProductName() {
		return productName;
	}
	public void setProductName(String productName) {
		this.productName = productName;
	}
	public String getModel() {
		return model;
	}
	public void setModel(String model) {
		this.model = model;
	}
	public String getBrand() {
		return brand;
	}
	public void setBrand(String brand) {
		this.brand = brand;
	}
	public String getMemsize() {
		return memsize;
	}
	public void setMemsize(String memsize) {
		this.memsize = memsize;
	}
	public boolean isOc() {
		return oc;
	}
	public void setOc(boolean oc) {
		this.oc = oc;
	}
	public double getPrice() {
		return price;
	}
	public void setPrice(double price) {
		this.price = price;
	}
	public boolean isInStock() {
		return inStock;
	}
	public void setInStock(boolean inStock) {
		this.inStock = inStock;
	}
	
	
}
