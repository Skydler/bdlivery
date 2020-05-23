package ar.edu.unlp.info.bd2.model;

import org.bson.types.ObjectId;

import ar.edu.unlp.info.bd2.mongo.PersistentObject;

public class Item implements PersistentObject {

	private ObjectId id;

	private Long quantity;

	private Product product;

	public Item(Long quantity, Product product) {
		this.quantity = quantity;
		this.product = product;
	}

	public Item() {

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

	@Override
	public ObjectId getObjectId() {
		return id;
	}

	@Override
	public void setObjectId(ObjectId objectId) {
		this.id = objectId;
	}

}
