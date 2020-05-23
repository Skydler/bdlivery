package ar.edu.unlp.info.bd2.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Date;

import org.bson.types.ObjectId;

import ar.edu.unlp.info.bd2.mongo.PersistentObject;

public class User implements PersistentObject {

	private ObjectId id;

	private String username;

	private String password;

	private String name;

	private String email;

	private Date birthDate;

	private List<Order> orders;

	private List<Order> deliveredOrders;

	public User() {
	}

	public User(String email, String password, String username, String name, Date birthDate) {
		this.email = email;
		this.password = password;
		this.username = username;
		this.name = name;
		this.birthDate = birthDate;
		this.orders = new ArrayList<Order>();
		this.deliveredOrders = new ArrayList<Order>();
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public Date getBirthDate() {
		return birthDate;
	}

	public void setBirthDate(Date birthDate) {
		this.birthDate = birthDate;
	}

	public List<Order> getOrders() {
		return orders;
	}

	public void setOrders(List<Order> orders) {
		this.orders = orders;
	}

	public List<Order> getDeliveredOrders() {
		return deliveredOrders;
	}

	public void setDeliveredOrders(List<Order> deliveredOrders) {
		this.deliveredOrders = deliveredOrders;
	}

	@Override
	public ObjectId getObjectId() {
		return id;
	}

	@Override
	public void setObjectId(ObjectId objectId) {
		this.id = objectId;
	}

}
