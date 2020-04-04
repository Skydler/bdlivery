package ar.edu.unlp.info.bd2.model;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name = "Products")
public class Product {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private String name;

	@Column(nullable = false)
	private Float weight;

	@OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	private Supplier supplier;

	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	private List<Price> prices;

	public Product(String name, Float currentPrice, Float weight, Supplier supplier) {
		this.name = name;
		this.weight = weight;
		this.supplier = supplier;
		this.prices = new ArrayList<Price>();

		Date currentDate = Calendar.getInstance().getTime();
		this.prices.add(new Price(currentPrice, currentDate));
	}

	public Product() {

	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
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

	public Float getPrice() {
		return this.currentPrice().getValue();
	}

	private Price currentPrice() {
		return this.prices.get(this.prices.size() - 1);
	}

	public void addPrice(Float value, Date startDate) {
		this.currentPrice().setEndDate(startDate);
		Price newPrice = new Price(value, startDate);
		this.prices.add(newPrice);
	}

}
