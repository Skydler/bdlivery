package ar.edu.unlp.info.bd2.model;

import java.util.Date;

import org.bson.codecs.pojo.annotations.BsonId;
import org.bson.types.ObjectId;

import ar.edu.unlp.info.bd2.mongo.PersistentObject;

public class Price implements Comparable<Price>, PersistentObject {

	@BsonId
	private ObjectId id;

	private Float value;

	private Date startDate;

	private Date endDate;

	public Price(Float value, Date startDate) {
		this.value = value;
		this.startDate = startDate;
	}

	public Price() {

	}

	public Float getValue() {
		return value;
	}

	public void setValue(Float value) {
		this.value = value;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public boolean isInsidePriceRange(Date date) {
		if (this.endDate == null) {
			return this.startDate.before(date);
		} else {
			return this.startDate.before(date) && this.endDate.after(date);
		}
	}

	@Override
	public int compareTo(Price price) {
		return this.value.compareTo(price.getValue());
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
