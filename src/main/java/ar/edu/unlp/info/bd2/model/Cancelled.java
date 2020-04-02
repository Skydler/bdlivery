package ar.edu.unlp.info.bd2.model;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "Cancelled")
public class Cancelled extends OrderStatus {

	public Cancelled(Order order) {
		super(order);

	}

}
