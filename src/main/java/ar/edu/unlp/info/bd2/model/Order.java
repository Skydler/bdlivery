package ar.edu.unlp.info.bd2.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Date;

import org.bson.codecs.pojo.annotations.BsonId;
import org.bson.codecs.pojo.annotations.BsonIgnore;
import org.bson.codecs.pojo.annotations.BsonProperty;
import org.bson.types.ObjectId;

import ar.edu.unlp.info.bd2.mongo.PersistentObject;

public class Order implements PersistentObject {

	@BsonId
	private ObjectId objectId;

	private Date orderDate;

	private String address;

	private Float coordX;

	private Float coordY;

	private Float amount;

	private String currentStatus;

	private List<Item> items;

	private List<OrderStatus> statesRecord;

//	@BsonIgnore
	private User client;

	@BsonIgnore
	private User delivery;

//	Constants
	public static final String CANCELLED = "Cancelled";
	public static final String PENDING = "Pending";
	public static final String SENDING = "Sending";
	public static final String DELIVERED = "Delivered";

	public Order(Date orderDate, String address, Float coordX, Float coordY, User client) {
		this.orderDate = orderDate;
		this.address = address;
		this.coordX = coordX;
		this.coordY = coordY;
		this.client = client;
		this.amount = 0F;
		this.currentStatus = Order.PENDING;
		this.items = new ArrayList<Item>();
		this.statesRecord = new ArrayList<OrderStatus>();
		this.statesRecord.add(new OrderStatus(Order.PENDING, orderDate));
	}

	public Order() {

	}

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

	@BsonProperty(value = "items")
	public List<Item> getProducts() {
		return items;
	}

	@BsonProperty(value = "items")
	public void setProducts(ArrayList<Item> items) {
		this.items = items;
	}

	public List<OrderStatus> getStatus() {
		Collections.sort(this.statesRecord);
		return statesRecord;
	}

	public void setStatus(ArrayList<OrderStatus> statesRecord) {
		this.statesRecord = statesRecord;
	}

	public String getCurrentStatus() {
		return currentStatus;
	}

	public void setCurrentStatus(String currentStatus) {
		this.currentStatus = currentStatus;
	}

	public User getClient() {
		return client;
	}

	public void setClient(User client) {
		this.client = client;
	}

	public User getDeliveryUser() {
		return delivery;
	}

	public void setDeliveryUser(User delivery) {
		this.delivery = delivery;
	}

	@BsonIgnore
	public Boolean isItemsEmpty() {
		return this.items.isEmpty();
	}

	@BsonIgnore
	public OrderStatus getActualStatus() {
		Collections.sort(this.statesRecord);
		return this.statesRecord.get(this.statesRecord.size() - 1);
	}

	public Float getAmount() {
		return amount;
	}

	public void setAmount(Float amount) {
		this.amount = amount;
	}

	public void addProduct(Item item) {
		this.items.add(item);
		this.amount += item.getProduct().getPriceAt(this.orderDate) * item.getQuantity();
	}

	@Override
	public ObjectId getObjectId() {
		return objectId;
	}

	@Override
	public void setObjectId(ObjectId objectId) {
		this.objectId = objectId;
	}

	public ObjectId getId() {
		return objectId;
	}

}
