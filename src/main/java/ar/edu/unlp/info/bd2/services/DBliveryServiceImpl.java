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


public class DBliveryServiceImpl implements DBliveryService {

	private DBliveryRepository repository;

	private static final String ORDER_NOT_FOUND = "No existe la orden para el id dado";
	private static final String PRODUCT_NOT_FOUND = "No existe el producto para el id dado";

	public DBliveryServiceImpl(DBliveryRepository repository) {
		this.repository = repository;
	}

	@Override
	@Transactional
	public Order addProduct(Long order, Long quantity, Product product) throws DBliveryException {
		Product prod = this.getProductById(product.getId())
				.orElseThrow(() -> new DBliveryException(DBliveryServiceImpl.PRODUCT_NOT_FOUND));
		Order actualOrder = this.getOrderById(order)
				.orElseThrow(() -> new DBliveryException(DBliveryServiceImpl.ORDER_NOT_FOUND));
		Item item = new Item(quantity, prod);
		repository.saveObject(item);
		actualOrder.addProduct(item);
		repository.saveObject(actualOrder);
		return actualOrder;
	}

	@Override
	@Transactional
	public boolean canCancel(Long order) throws DBliveryException {
		Order actualOrder = this.getOrderById(order)
				.orElseThrow(() -> new DBliveryException(DBliveryServiceImpl.ORDER_NOT_FOUND));
		return actualOrder.getActualStatus().getStatus().equals(Order.PENDING);
	}

	@Override
	@Transactional
	public Order cancelOrder(Long order) throws DBliveryException {
		Order actualOrder = this.getOrderById(order)
				.orElseThrow(() -> new DBliveryException(DBliveryServiceImpl.ORDER_NOT_FOUND));

		if (!this.canCancel(order)) {
			throw new DBliveryException("The order can't be cancelled");
		} else {
			actualOrder.getStatus().add(new OrderStatus(Order.CANCELLED));
			actualOrder.setCurrentStatus(Order.CANCELLED);
		}
		repository.saveObject(actualOrder);
		return actualOrder;
	}

	@Override
	@Transactional
	public Order cancelOrder(Long order, Date date) throws DBliveryException {
		Order actualOrder = this.getOrderById(order)
				.orElseThrow(() -> new DBliveryException(DBliveryServiceImpl.ORDER_NOT_FOUND));

		if (!this.canCancel(order)) {
			throw new DBliveryException("The order can't be cancelled");
		} else {
			actualOrder.getStatus().add(new OrderStatus(Order.CANCELLED, date));
			actualOrder.setCurrentStatus(Order.CANCELLED);
		}
		repository.saveObject(actualOrder);
		return actualOrder;
	}

	@Override
	@Transactional
	public boolean canDeliver(Long order) throws DBliveryException {
		Order actualOrder = this.getOrderById(order)
				.orElseThrow(() -> new DBliveryException(DBliveryServiceImpl.ORDER_NOT_FOUND));
		return !actualOrder.isItemsEmpty() && actualOrder.getActualStatus().getStatus().equals(Order.PENDING);
	}

	@Override
	@Transactional
	public boolean canFinish(Long id) throws DBliveryException {
		Order actualOrder = this.getOrderById(id)
				.orElseThrow(() -> new DBliveryException(DBliveryServiceImpl.ORDER_NOT_FOUND));
		return !actualOrder.isItemsEmpty() && actualOrder.getActualStatus().getStatus().equals(Order.SENDING);
	}

	@Override
	@Transactional
	public Order createOrder(Date dateOfOrder, String address, Float coordX, Float coordY, User client) {
		Order order = new Order(dateOfOrder, address, coordX, coordY, client);
		repository.saveObject(order);
		return order;
	}

	@Override
	@Transactional
	public Product createProduct(String name, Float price, Float weight, Supplier supplier) {
		Product prod = new Product(name, price, weight, supplier);
		repository.saveObject(prod);
		return prod;
	}

	@Override
	@Transactional
	public Product createProduct(String name, Float price, Float weight, Supplier supplier, Date date) {
		Product prod = new Product(name, price, weight, supplier, date);
		repository.saveObject(prod);
		return prod;
	}

	@Override
	@Transactional
	public Supplier createSupplier(String name, String cuil, String address, Float coordX, Float coordY) {
		Supplier supplier = new Supplier(name, cuil, address, coordX, coordY);
		repository.saveObject(supplier);
		return supplier;
	}

	@Override
	@Transactional
	public User createUser(String email, String password, String username, String name, Date dateOfBirth) {
		User user = new User(email, password, username, name, dateOfBirth);
		repository.saveObject(user);
		return user;
	}

	@Override
	@Transactional
	public Order deliverOrder(Long order, User deliveryUser) throws DBliveryException {
		Order actualOrder = this.getOrderById(order)
				.orElseThrow(() -> new DBliveryException(DBliveryServiceImpl.ORDER_NOT_FOUND));
		if (!this.canDeliver(order)) {
			throw new DBliveryException("The order can't be delivered");
		} else {
			actualOrder.setDeliveryUser(deliveryUser);
			actualOrder.getStatus().add(new OrderStatus(Order.SENDING));
			actualOrder.setCurrentStatus(Order.SENDING);
		}
		repository.saveObject(actualOrder);
		return actualOrder;
	}

	@Override
	@Transactional
	public Order deliverOrder(Long order, User deliveryUser, Date date) throws DBliveryException {
		Order actualOrder = this.getOrderById(order)
				.orElseThrow(() -> new DBliveryException(DBliveryServiceImpl.ORDER_NOT_FOUND));
		if (!this.canDeliver(order)) {
			throw new DBliveryException("The order can't be delivered");
		} else {
			actualOrder.setDeliveryUser(deliveryUser);
			actualOrder.getStatus().add(new OrderStatus(Order.SENDING, date));
			actualOrder.setCurrentStatus(Order.SENDING);
		}
		repository.saveObject(actualOrder);
		return actualOrder;
	}

	@Override
	@Transactional
	public Order finishOrder(Long order) throws DBliveryException {
		Order actualOrder = this.getOrderById(order)
				.orElseThrow(() -> new DBliveryException(DBliveryServiceImpl.ORDER_NOT_FOUND));
		if (!this.canFinish(order)) {
			throw new DBliveryException("The order can't be finished");
		} else {
			actualOrder.getStatus().add(new OrderStatus(Order.DELIVERED));
			actualOrder.setCurrentStatus(Order.DELIVERED);
		}
		repository.saveObject(actualOrder);
		return actualOrder;
	}

	@Override
	@Transactional
	public Order finishOrder(Long order, Date date) throws DBliveryException {
		Order actualOrder = this.getOrderById(order)
				.orElseThrow(() -> new DBliveryException(DBliveryServiceImpl.ORDER_NOT_FOUND));
		if (!this.canFinish(order)) {
			throw new DBliveryException("The order can't be finished");
		} else {
			actualOrder.getStatus().add(new OrderStatus(Order.DELIVERED, date));
			actualOrder.setCurrentStatus(Order.DELIVERED);
		}
		repository.saveObject(actualOrder);
		return actualOrder;
	}

	@Override
	@Transactional
	public List<User> get5LessDeliveryUsers() {
		return repository.get5LessDeliveryUsers();
	}

	@Override
	@Transactional
	public OrderStatus getActualStatus(Long order) {
		Order actualOrder = this.getOrderById(order).get();
		return actualOrder.getActualStatus();
	}

	@Override
	@Transactional
	public List<Order> getAllOrdersMadeByUser(String username) {
		return repository.getAllOrdersMadeByUser(username);
	}

	@Override
	@Transactional
	public Product getBestSellingProduct() {
		return repository.getBestSellingProduct();
	}

	@Override
	@Transactional
	public List<Order> getCancelledOrdersInPeriod(Date startDate, Date endDate) {
		return repository.getCancelledOrdersInPeriod(startDate, endDate);
	}

	@Override
	@Transactional
	public List<Order> getDeliveredOrdersForUser(String username) {
		return repository.getDeliveredOrdersForUser(username);
	}

	@Override
	@Transactional
	public List<Order> getDeliveredOrdersInPeriod(Date startDate, Date endDate) {
		return repository.getDeliveredOrdersInPeriod(startDate, endDate);
	}

	@Override
	@Transactional
	public List<Order> getDeliveredOrdersSameDay() {
		return repository.getDeliveredOrdersSameDay();
	}

	@Override
	@Transactional
	public Optional<Order> getOrderById(Long id) {
		Order order = (Order) repository.findById(Order.class, id);
		return Optional.of(order);
	}

	@Override
	@Transactional
	public List<Order> getOrdersCompleteMorethanOneDay() {
		return repository.getOrdersCompleteMorethanOneDay();
	}

	@Override
	@Transactional
	public List<Order> getOrderWithMoreQuantityOfProducts(Date day) {
		return repository.getOrderWithMoreQuantityOfProducts(day);
	}

	@Override
	@Transactional
	public List<Order> getPendingOrders() {
		return repository.getPendingOrders();
	}

	@Override
	@Transactional
	public Optional<Product> getProductById(Long id) {
		Product prod = (Product) repository.findById(Product.class, id);
		return Optional.of(prod);
	}

	@Override
	@Transactional
	public List<Product> getProductByName(String name) {
		ArrayList<Product> prods = (ArrayList<Product>) repository.findProductBySimilarName(name);
		return prods;
	}

	@Override
	@Transactional
	public List<Product> getProductIncreaseMoreThan100() {
		return repository.getProductIncreaseMoreThan100();
	}

	@Override
	@Transactional
	public List<Product> getProductsNotSold() {
		return repository.getProductsNotSold();
	}

	@Override
	@Transactional
	public List<Product> getProductsOnePrice() {
		return repository.getProductsOnePrice();
	}

	@Override
	@Transactional
	public List<Object[]> getProductsWithPriceAt(Date day) {
		return repository.getProductsWithPriceAt(day);
	}

	@Override
	@Transactional
	public List<Order> getSentMoreOneHour() {
		return repository.getSentMoreOneHour();
	}

	@Override
	@Transactional
	public List<Order> getSentOrders() {
		return repository.getSentOrders();
	}

	@Override
	@Transactional
	public List<Product> getSoldProductsOn(Date day) {
		return repository.getSoldProductsOn(day);
	}

	@Override
	@Transactional
	public Supplier getSupplierLessExpensiveProduct() {
		return repository.getSupplierLessExpensiveProduct();
	}

	@Override
	@Transactional
	public List<Supplier> getSuppliersDoNotSellOn(Date day) {
		return repository.getSuppliersDoNotSellOn(day);
	}

	@Override
	@Transactional
	public List<Product> getTop10MoreExpensiveProducts() {
		return repository.getTop10MoreExpensiveProducts();
	}

	@Override
	@Transactional
	public List<User> getTop6UsersMoreOrders() {
		return repository.getTop6UsersMoreOrders();
	}

	@Override
	@Transactional
	public List<Supplier> getTopNSuppliersInSentOrders(int n) {
		return repository.getTopNSuppliersInSentOrders(n);
	}

	@Override
	@Transactional
	public Optional<User> getUserByEmail(String email) {
		User user = repository.findByEmail(email);
		return Optional.of(user);
	}

	@Override
	@Transactional
	public Optional<User> getUserById(Long id) {
		User user = (User) repository.findById(User.class, id);
		return Optional.of(user);
	}

	@Override
	@Transactional
	public Optional<User> getUserByUsername(String username) {
		User user = repository.findByUsername(username);
		return Optional.of(user);
	}

	@Override
	@Transactional
	public List<User> getUsersSpendingMoreThan(Float amount) {
		return repository.getUsersSpendingMoreThan(amount);
	}

	@Override
	@Transactional
	public Product updateProductPrice(Long id, Float price, Date startDate) throws DBliveryException {
		Product prod = this.getProductById(id)
				.orElseThrow(() -> new DBliveryException(DBliveryServiceImpl.PRODUCT_NOT_FOUND));
		prod.addPrice(price, startDate);
		repository.saveObject(prod);
		return prod;
	}

}
