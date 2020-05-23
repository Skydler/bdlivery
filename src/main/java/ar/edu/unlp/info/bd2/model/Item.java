package ar.edu.unlp.info.bd2.model;


import org.bson.types.ObjectId;

public class Item {

	private ObjectId id;

	private Long quantity;

	private Product product;

	public Item(Long quantity, Product product) {
		this.quantity = quantity;
		this.product = product;
	}

	public Item() {

	}

	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
		this.id = id;
	}

	public Product getProduct() {
		return product;
	}

	public void setProduct(Product product) {
		this.product = product;
	}

	public Item(Long quantity) {
		this.quantity = quantity;
	}

	public Long getQuantity() {
		return quantity;
	}

	public void setQuantity(Long quantity) {
		this.quantity = quantity;
	}

}
