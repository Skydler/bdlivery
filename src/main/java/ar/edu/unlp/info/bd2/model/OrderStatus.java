package ar.edu.unlp.info.bd2.model;

import java.util.Date;

import org.bson.codecs.pojo.annotations.BsonId;
import org.bson.types.ObjectId;

import ar.edu.unlp.info.bd2.mongo.PersistentObject;

public class OrderStatus implements Comparable<OrderStatus>, PersistentObject {

	@BsonId
	private ObjectId objectId;

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

	@Override
	public ObjectId getObjectId() {
		return objectId;
	}

	@Override
	public void setObjectId(ObjectId objectId) {
		this.objectId = objectId;
	}

}
