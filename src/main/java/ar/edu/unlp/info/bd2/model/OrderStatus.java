package ar.edu.unlp.info.bd2.model;

import java.util.Date;


import org.bson.types.ObjectId;

public class OrderStatus implements Comparable<OrderStatus> {

	private ObjectId id;

	private String status;

	private Date statusDate;

	public OrderStatus() {

	}

	public OrderStatus(String status) {
		this.status = status;
		this.statusDate = new Date();
	}

	public OrderStatus(String status, Date date) {
		this.status = status;
		this.statusDate = date;
	}

	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
		this.id = id;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Date getStatusDate() {
		return statusDate;
	}

	public void setStatusDate(Date statusDate) {
		this.statusDate = statusDate;
	}
	
	@Override
	public int compareTo(OrderStatus ord) {
		return this.getStatusDate().compareTo(ord.getStatusDate());
	}
	
	@Override
	public String toString() {
		return "OrderStatus: " + this.getStatus();
	}

}
