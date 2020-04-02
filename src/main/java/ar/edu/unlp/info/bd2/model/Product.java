package ar.edu.unlp.info.bd2.model;

import java.util.ArrayList;

public class Product {
	
	private String name;
	
	private Float weight;
	
	private Supplier supplier;
	
	private ArrayList<Price> prices = new ArrayList<Price>();

	public Supplier getSupplier() {
		return supplier;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Float getWeight() {
		return weight;
	}

	public void setWeight(Float weight) {
		this.weight = weight;
	}

	public ArrayList<Price> getPrices() {
		return prices;
	}

	public void setPrices(ArrayList<Price> prices) {
		this.prices = prices;
	}

	public void setSupplier(Supplier supplier) {
		this.supplier = supplier;
	}

	
	
	
}
