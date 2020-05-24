package ar.edu.unlp.info.bd2.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.bson.codecs.pojo.annotations.BsonId;
import org.bson.codecs.pojo.annotations.BsonIgnore;
import org.bson.types.ObjectId;

import org.joda.time.LocalDate;

import ar.edu.unlp.info.bd2.mongo.PersistentObject;

public class Product implements PersistentObject {

	@BsonId
	private ObjectId objectId;

	private String name;

	private Float weight;

	@BsonIgnore
	private Supplier supplier;

	private List<Price> prices;

	public Product(String name, Float currentPrice, Float weight, Supplier supplier) {
		this.name = name;
		this.weight = weight;
		this.supplier = supplier;
		this.prices = new ArrayList<Price>();

		Date currentDate = new Date(0);
		this.prices.add(new Price(currentPrice, currentDate));
	}

	public Product(String name, Float currentPrice, Float weight, Supplier supplier, Date date) {
		this.name = name;
		this.weight = weight;
		this.supplier = supplier;
		this.prices = new ArrayList<Price>();
		this.prices.add(new Price(currentPrice, date));
	}

	public Product() {

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

	public List<Price> getPrices() {
		return prices;
	}

	public void setPrices(List<Price> prices) {
		this.prices = prices;
	}

	public Supplier getSupplier() {
		return supplier;
	}

	public void setSupplier(Supplier supplier) {
		this.supplier = supplier;
	}

	@BsonIgnore
	public Float getPrice() {
		return this.currentPrice().getValue();
	}

	public Price currentPrice() {
		Collections.sort(this.prices);
		return this.prices.get(this.prices.size() - 1);
	}

	public Float getPriceAt(Date date) {
		Price price = this.prices.stream().filter(p -> p.isInsidePriceRange(date)).findFirst().get();
		return price.getValue();
	}

	public void addPrice(Float value, Date startDate) {
		Price currentPrice = this.currentPrice();
		Date cloned_date = (Date) startDate.clone();
		cloned_date = LocalDate.fromDateFields(cloned_date).minusDays(1).toDate();
		currentPrice.setEndDate(cloned_date);
		Price newPrice = new Price(value, startDate);
		this.prices.add(newPrice);
	}

	@Override
	public ObjectId getObjectId() {
		return objectId;
	}
	
	@BsonIgnore
	public ObjectId getId() {
		return objectId;
	}

	@Override
	public void setObjectId(ObjectId objectId) {
		this.objectId = objectId;
	}

}
