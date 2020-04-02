package ar.edu.unlp.info.bd2.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
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

	@OneToMany(cascade = CascadeType.ALL)
	private List<Item> items;

	@OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
	private List<OrderStatus> statesRecord;

	@ManyToOne()
	private User client;

	@ManyToOne()
	private User delivery;

	public Order(Date orderDate, String address, Float coordX, Float coordY, User client) {
		this.orderDate = orderDate;
		this.address = address;
		this.coordX = coordX;
		this.coordY = coordY;
		this.client = client;
		this.statesRecord = new ArrayList<OrderStatus>();
		this.statesRecord.add(new Pending(this));
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
		return statesRecord;
	}

	public void setStatus(ArrayList<OrderStatus> statesRecord) {
		this.statesRecord = statesRecord;
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

}
