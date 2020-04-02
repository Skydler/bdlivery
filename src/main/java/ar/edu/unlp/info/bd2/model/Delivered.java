package ar.edu.unlp.info.bd2.model;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "Delivered")
public class Delivered extends OrderStatus {

	public Delivered(Order order) {
		super(order);
	}

}
