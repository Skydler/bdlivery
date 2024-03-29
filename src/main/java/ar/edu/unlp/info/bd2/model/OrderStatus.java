package ar.edu.unlp.info.bd2.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table(name = "OrderStatus")
public class OrderStatus implements Comparable<OrderStatus> {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private String status;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(nullable = false)
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

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
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

}
