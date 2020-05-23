package ar.edu.unlp.info.bd2.model;

import java.util.Date;

import org.bson.types.ObjectId;

public class Price implements Comparable<Price>{

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

	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
		this.id = id;
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
}
