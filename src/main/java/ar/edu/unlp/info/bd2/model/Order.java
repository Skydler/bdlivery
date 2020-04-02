package ar.edu.unlp.info.bd2.model;

import java.util.ArrayList;
import java.util.Date;

public class Order {
	
	private Date orderDate;
	
	private String address;
	
	private Float coordX;
	
	private Float coordY;
	
	private ArrayList<Items> items = new ArrayList<Items> ();

	private ArrayList<OrderStatus> statesRecord = new ArrayList<OrderStatus>();
	
	private User client;
	
	private User delivery;

	public Date getOrderDate() {
		return orderDate;
	}

	public void setOrderDate(Date orderDate) {
		this.orderDate = orderDate;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public Float getCoordX() {
		return coordX;
	}

	public void setCoordX(Float coordX) {
		this.coordX = coordX;
	}

	public Float getCoordY() {
		return coordY;
	}

	public void setCoordY(Float coordY) {
		this.coordY = coordY;
	}

	public ArrayList<Items> getItems() {
		return items;
	}

	public void setItems(ArrayList<Items> items) {
		this.items = items;
	}

	public ArrayList<OrderStatus> getStatesRecord() {
		return statesRecord;
	}

	public void setStatesRecord(ArrayList<OrderStatus> statesRecord) {
		this.statesRecord = statesRecord;
	}

	public User getClient() {
		return client;
	}

	public void setClient(User client) {
		this.client = client;
	}

	public User getDelivery() {
		return delivery;
	}

	public void setDelivery(User delivery) {
		this.delivery = delivery;
	}
	
	
	
	
	
}
