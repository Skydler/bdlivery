package ar.edu.unlp.info.bd2.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table(name = "Orders")
public class Order {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(nullable = false)
	private Date orderDate;

	@Column(nullable = false)
	private String address;

	@Column(nullable = false)
	private Float coordX;

	@Column(nullable = false)
	private Float coordY;
	
	@Column(nullable = false)
	private Float amount;
	
	@Column(nullable = false)
	private String currentStatus;

	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	@JoinColumn(name = "id_item")
	private List<Item> items;

	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	@JoinColumn(name = "id_orderStatus")
	private List<OrderStatus> statesRecord;

	@ManyToOne()
	@JoinColumn(name = "id_client")
	private User client;

	@ManyToOne()
	@JoinColumn(name = "id_delivery")
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

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
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

	public List<Item> getProducts() {
		return items;
	}

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

	public Boolean isItemsEmpty() {
		return this.items.isEmpty();
	}

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
	
}
