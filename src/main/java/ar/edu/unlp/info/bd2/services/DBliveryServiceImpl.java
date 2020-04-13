package ar.edu.unlp.info.bd2.services;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.transaction.annotation.Transactional;

import ar.edu.unlp.info.bd2.model.Item;
import ar.edu.unlp.info.bd2.model.Order;
import ar.edu.unlp.info.bd2.model.OrderStatus;
import ar.edu.unlp.info.bd2.model.Product;
import ar.edu.unlp.info.bd2.model.Supplier;
import ar.edu.unlp.info.bd2.model.User;
import ar.edu.unlp.info.bd2.repositories.DBliveryException;
import ar.edu.unlp.info.bd2.repositories.DBliveryRepository;

@Transactional
public class DBliveryServiceImpl implements DBliveryService {

	private DBliveryRepository repository;

	private static final String ORDER_NOT_FOUND = "No existe la orden para el id dado";
	private static final String PRODUCT_NOT_FOUND = "No existe el producto para el id dado";

	public DBliveryServiceImpl(DBliveryRepository repository) {
		this.repository = repository;
	}

	@Override
	public Order addProduct(Long order, Long quantity, Product product) throws DBliveryException {
		Order actualOrder = this.getOrderById(order)
				.orElseThrow(() -> new DBliveryException(DBliveryServiceImpl.ORDER_NOT_FOUND));
		Item item = new Item(quantity, product);
		repository.saveObject(item);
		List<Item> orderItems = actualOrder.getProducts();
		orderItems.add(item);
		repository.saveObject(actualOrder);
		return actualOrder;
	}

	@Override
	public boolean canCancel(Long order) throws DBliveryException {
		Order actualOrder = this.getOrderById(order)
				.orElseThrow(() -> new DBliveryException(DBliveryServiceImpl.ORDER_NOT_FOUND));
		return actualOrder.getActualStatus().getStatus().equals(Order.PENDING);
	}

	@Override
	public Order cancelOrder(Long order) throws DBliveryException {
		Order actualOrder = this.getOrderById(order)
				.orElseThrow(() -> new DBliveryException(DBliveryServiceImpl.ORDER_NOT_FOUND));

		if (!this.canCancel(order)) {
			throw new DBliveryException("The order can't be cancelled");
		} else {
			actualOrder.getStatus().add(new OrderStatus(Order.CANCELLED));
		}
		repository.saveObject(actualOrder);
		return actualOrder;
	}

	@Override
	public Order cancelOrder(Long order, Date date) throws DBliveryException {
		Order actualOrder = this.getOrderById(order)
				.orElseThrow(() -> new DBliveryException(DBliveryServiceImpl.ORDER_NOT_FOUND));

		if (!this.canCancel(order)) {
			throw new DBliveryException("The order can't be cancelled");
		} else {
			actualOrder.getStatus().add(new OrderStatus(Order.CANCELLED, date));
		}
		repository.saveObject(actualOrder);
		return actualOrder;
	}

	@Override
	public boolean canDeliver(Long order) throws DBliveryException {
		Order actualOrder = this.getOrderById(order)
				.orElseThrow(() -> new DBliveryException(DBliveryServiceImpl.ORDER_NOT_FOUND));
		return !actualOrder.isItemsEmpty() && actualOrder.getActualStatus().getStatus().equals(Order.PENDING);
	}

	@Override
	public boolean canFinish(Long id) throws DBliveryException {
		Order actualOrder = this.getOrderById(id)
				.orElseThrow(() -> new DBliveryException(DBliveryServiceImpl.ORDER_NOT_FOUND));
		return !actualOrder.isItemsEmpty() && actualOrder.getActualStatus().getStatus().equals(Order.SENDING);
	}

	@Override
	public Order createOrder(Date dateOfOrder, String address, Float coordX, Float coordY, User client) {
		Order order = new Order(dateOfOrder, address, coordX, coordY, client);
		repository.saveObject(order);
		return order;
	}

	@Override
	public Product createProduct(String name, Float price, Float weight, Supplier supplier) {
		Product prod = new Product(name, price, weight, supplier);
		repository.saveObject(prod);
		return prod;
	}

	@Override
	public Product createProduct(String name, Float price, Float weight, Supplier supplier, Date date) {
		Product prod = new Product(name, price, weight, supplier, date);
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
	public Order deliverOrder(Long order, User deliveryUser) throws DBliveryException {
		Order actualOrder = this.getOrderById(order)
				.orElseThrow(() -> new DBliveryException(DBliveryServiceImpl.ORDER_NOT_FOUND));
		if (!this.canDeliver(order)) {
			throw new DBliveryException("The order can't be delivered");
		} else {
			actualOrder.setDeliveryUser(deliveryUser);
			actualOrder.getStatus().add(new OrderStatus(Order.SENDING));
		}
		repository.saveObject(actualOrder);
		return actualOrder;
	}

	@Override
	public Order deliverOrder(Long order, User deliveryUser, Date date) throws DBliveryException {
		Order actualOrder = this.getOrderById(order)
				.orElseThrow(() -> new DBliveryException(DBliveryServiceImpl.ORDER_NOT_FOUND));
		if (!this.canDeliver(order)) {
			throw new DBliveryException("The order can't be delivered");
		} else {
			actualOrder.setDeliveryUser(deliveryUser);
			actualOrder.getStatus().add(new OrderStatus(Order.SENDING, date));
		}
		repository.saveObject(actualOrder);
		return actualOrder;
	}

	@Override
	public Order finishOrder(Long order) throws DBliveryException {
		Order actualOrder = this.getOrderById(order)
				.orElseThrow(() -> new DBliveryException(DBliveryServiceImpl.ORDER_NOT_FOUND));
		if (!this.canFinish(order)) {
			throw new DBliveryException("The order can't be finished");
		} else {
			actualOrder.getStatus().add(new OrderStatus(Order.DELIVERED));
		}
		repository.saveObject(actualOrder);
		return actualOrder;
	}

	@Override
	public Order finishOrder(Long order, Date date) throws DBliveryException {
		Order actualOrder = this.getOrderById(order)
				.orElseThrow(() -> new DBliveryException(DBliveryServiceImpl.ORDER_NOT_FOUND));
		if (!this.canFinish(order)) {
			throw new DBliveryException("The order can't be finished");
		} else {
			actualOrder.getStatus().add(new OrderStatus(Order.DELIVERED, date));
		}
		repository.saveObject(actualOrder);
		return actualOrder;
	}

	@Override
	public List<User> get5LessDeliveryUsers() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public OrderStatus getActualStatus(Long order) {
		Order actualOrder = this.getOrderById(order).get();
		return actualOrder.getActualStatus();
	}

	@Override
	public List<Order> getAllOrdersMadeByUser(String username) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Product getBestSellingProduct() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Order> getCancelledOrdersInPeriod(Date startDate, Date endDate) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Order> getDeliveredOrdersForUser(String username) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Order> getDeliveredOrdersInPeriod(Date startDate, Date endDate) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Order> getDeliveredOrdersSameDay() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Optional<Order> getOrderById(Long id) {
		Order order = (Order) repository.findById(Order.class, id);
		return Optional.of(order);
	}

	@Override
	public List<Order> getOrdersCompleteMorethanOneDay() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Order> getOrderWithMoreQuantityOfProducts(Date day) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Order> getPendingOrders() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Optional<Product> getProductById(Long id) {
		Product prod = (Product) repository.findById(Product.class, id);
		return Optional.of(prod);
	}

	@Override
	public List<Product> getProductByName(String name) {
		ArrayList<Product> prods = (ArrayList<Product>) repository.findProductBySimilarName(name);
		return prods;
	}

	@Override
	public List<Product> getProductIncreaseMoreThan100() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Product> getProductsNotSold() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Product> getProductsOnePrice() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Object[]> getProductsWithPriceAt(Date day) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Order> getSentMoreOneHour() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Order> getSentOrders() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Product> getSoldProductsOn(Date day) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Supplier getSupplierLessExpensiveProduct() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Supplier> getSuppliersDoNotSellOn(Date day) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Product> getTop10MoreExpensiveProducts() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<User> getTop6UsersMoreOrders() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Supplier> getTopNSuppliersInSentOrders(int n) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Optional<User> getUserByEmail(String email) {
		User user = repository.findByEmail(email);
		return Optional.of(user);
	}

	@Override
	public Optional<User> getUserById(Long id) {
		User user = (User) repository.findById(User.class, id);
		return Optional.of(user);
	}

	@Override
	public Optional<User> getUserByUsername(String username) {
		User user = repository.findByUsername(username);
		return Optional.of(user);
	}

	@Override
	public List<User> getUsersSpendingMoreThan(Float amount) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Product updateProductPrice(Long id, Float price, Date startDate) throws DBliveryException {
		Product prod = this.getProductById(id)
				.orElseThrow(() -> new DBliveryException(DBliveryServiceImpl.PRODUCT_NOT_FOUND));
		prod.addPrice(price, startDate);
		repository.saveObject(prod);
		return prod;
	}

}
