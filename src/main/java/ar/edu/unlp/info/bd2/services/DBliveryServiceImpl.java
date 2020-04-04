package ar.edu.unlp.info.bd2.services;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import ar.edu.unlp.info.bd2.model.Item;
import ar.edu.unlp.info.bd2.model.Order;
import ar.edu.unlp.info.bd2.model.OrderStatus;
import ar.edu.unlp.info.bd2.model.Product;
import ar.edu.unlp.info.bd2.model.Supplier;
import ar.edu.unlp.info.bd2.model.User;
import ar.edu.unlp.info.bd2.repositories.DBliveryException;
import ar.edu.unlp.info.bd2.repositories.DBliveryRepository;

public class DBliveryServiceImpl implements DBliveryService {

	private DBliveryRepository repository;

	public DBliveryServiceImpl(DBliveryRepository repository) {
		this.repository = repository;
	}

	@Override
	public Product createProduct(String name, Float price, Float weight, Supplier supplier) {
		Product prod = new Product(name, price, weight, supplier);
		repository.saveObject(prod);
		return prod;
	}

	@Override
	public Supplier createSupplier(String name, String cuil, String address, Float coordX, Float coordY) {
		Supplier supplier = new Supplier(name, cuil, address, coordX, coordY);
		repository.saveObject(supplier);
		return supplier;
	}

	@Override
	public User createUser(String email, String password, String username, String name, Date dateOfBirth) {
		User user = new User(email, password, username, name, dateOfBirth);
		repository.saveObject(user);
		return user;
	}

	@Override
	public Product updateProductPrice(Long id, Float price, Date startDate) throws DBliveryException {
		Product prod = (Product) repository.findById(Product.class, id);
		if (prod == null) {
			throw new DBliveryException("No existe el producto para el id dado");
		}
		prod.addPrice(price, startDate);
		repository.saveObject(prod);
		return prod;
	}

	@Override
	public Optional<User> getUserById(Long id) {
		User user = (User) repository.findById(User.class, id);
		return Optional.of(user);
	}

	@Override
	public Optional<User> getUserByEmail(String email) {
		User user = repository.findByEmail(email);
		return Optional.of(user);
	}

	@Override
	public Optional<User> getUserByUsername(String username) {
		User user = repository.findByUsername(username);
		return Optional.of(user);
	}

	@Override
	public Optional<Product> getProductById(Long id) {
		Product prod = (Product) repository.findById(Product.class, id);
		return Optional.of(prod);
	}

	@Override
	public Optional<Order> getOrderById(Long id) {
		Order order = (Order) repository.findById(Order.class, id);
		return Optional.of(order);
	}

	@Override
	public Order createOrder(Date dateOfOrder, String address, Float coordX, Float coordY, User client) {
		Order order = new Order(dateOfOrder, address, coordX, coordY, client);
		repository.saveObject(order);
		return order;
	}

	@Override
	public Order addProduct(Long order, Long quantity, Product product) throws DBliveryException {
		Item item = new Item(quantity, product);
		Order actualOrder = (Order) repository.findById(Order.class, order);
		List<Item> orderItems = actualOrder.getProducts();
		orderItems.add(item);
		return actualOrder;
	}

	@Override
	public Order deliverOrder(Long order, User deliveryUser) throws DBliveryException {
		Order actualOrder = (Order) repository.findById(Order.class, order);
		actualOrder.deliverOrder(deliveryUser);
		return actualOrder;
	}

	@Override
	public Order cancelOrder(Long order) throws DBliveryException {
		Order actualOrder = (Order) repository.findById(Order.class, order);
		actualOrder.cancelOrder();
		return actualOrder;
	}

	@Override
	public Order finishOrder(Long order) throws DBliveryException {
		Order actualOrder = (Order) repository.findById(Order.class, order);
		actualOrder.finishOrder();
		return actualOrder;
	}

	@Override
	public boolean canCancel(Long order) throws DBliveryException {
		Order actualOrder = (Order) repository.findById(Order.class, order);
		return actualOrder.canCancel();
	}

	@Override
	public boolean canFinish(Long id) throws DBliveryException {
		Order actualOrder = (Order) repository.findById(Order.class, id);
		return actualOrder.canFinish();
	}

	@Override
	public boolean canDeliver(Long order) throws DBliveryException {
		Order actualOrder = (Order) repository.findById(Order.class, order);
		return actualOrder.canDeliver();
	}

	@Override
	public OrderStatus getActualStatus(Long order) {
		Order actualOrder = (Order) repository.findById(Order.class, order);
		return actualOrder.getActualStatus();
	}

	@Override
	public List<Product> getProductByName(String name) {
		// TODO Auto-generated method stub
		return null;
	}

}
