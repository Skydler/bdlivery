package ar.edu.unlp.info.bd2.model;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "Sending")
public class Sending extends OrderStatus {

	public Sending(Order order) {
		super(order);
	}

}
