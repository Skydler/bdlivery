package ar.edu.unlp.info.bd2.model;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "Pending")
public class Pending extends OrderStatus {

	public Pending(Order order) {
		super(order);
	}

}
