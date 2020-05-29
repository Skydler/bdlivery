package ar.edu.unlp.info.bd2.services;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.bson.types.ObjectId;

import ar.edu.unlp.info.bd2.model.Item;
import ar.edu.unlp.info.bd2.model.Order;
import ar.edu.unlp.info.bd2.model.OrderStatus;
import ar.edu.unlp.info.bd2.model.Price;
import ar.edu.unlp.info.bd2.model.Product;
import ar.edu.unlp.info.bd2.model.Supplier;
import ar.edu.unlp.info.bd2.model.User;
import ar.edu.unlp.info.bd2.repositories.DBliveryException;
import ar.edu.unlp.info.bd2.repositories.DBliveryMongoRepository;

public class DBliveryServiceImpl implements DBliveryService, DBliveryStatisticsService {

	private DBliveryMongoRepository repository;

	private static final String ORDER_NOT_FOUND = "No existe la orden para el id dado";

	public DBliveryServiceImpl(DBliveryMongoRepository repository) {
		this.repository = repository;
	}

	@Override
	public Product createProduct(String name, Float price, Float weight, Supplier supplier) {
		Product prod = new Product(name, price, weight, supplier);
		repository.saveObject("products", Product.class, prod);
		repository.saveAssociation(prod, supplier, "product_supplier");
		return prod;
	}

	@Override
	public Product createProduct(String name, Float price, Float weight, Supplier supplier, Date date) {
		Product prod = new Product(name, price, weight, supplier, date);
		repository.saveObject("products", Product.class, prod);
		repository.saveAssociation(prod, supplier, "product_supplier");
		return prod;
	}

	@Override
	public Supplier createSupplier(String name, String cuil, String address, Float coordX, Float coordY) {
		Supplier sup = new Supplier(name, cuil, address, coordX, coordY);
		repository.saveObject("suppliers", Supplier.class, sup);
		return sup;
	}

	@Override
	public User createUser(String email, String password, String username, String name, Date dateOfBirth) {
		User user = new User(email, password, username, name, dateOfBirth);
		repository.saveObject("users", User.class, user);
		return user;
	}

	@Override
	public Product updateProductPrice(ObjectId id, Float price, Date startDate) throws DBliveryException {
		Price pri = new Price(price, startDate);
		Product prod = repository.updateProductPrice(id, pri);
		return prod;
	}

	@Override
	public Optional<User> getUserById(ObjectId id) {
		User user = repository.findById("users", User.class, id);
		return Optional.of(user);
	}

	@Override
	public Optional<User> getUserByEmail(String email) {
		User user = repository.getUserByAttribute("email", email);
		return Optional.of(user);
	}

	@Override
	public Optional<User> getUserByUsername(String username) {
		User user = repository.getUserByAttribute("username", username);
		return Optional.of(user);
	}

	@Override
	public Optional<Order> getOrderById(ObjectId id) {
		Order ord = repository.findById("orders", Order.class, id);
		List<User> users = repository.getAssociatedObjects(ord, User.class, "order_usrClient", "users");
		ord.setClient(users.get(0));
		return Optional.of(ord);
	}

	@Override
	public Order createOrder(Date dateOfOrder, String address, Float coordX, Float coordY, User client) {
		Order ord = new Order(dateOfOrder, address, coordX, coordY, client);
		repository.saveObject("orders", Order.class, ord);
		repository.saveAssociation(ord, client, "order_usrClient");
		return ord;
	}

	@Override
	public Order addProduct(ObjectId order, Long quantity, Product product) throws DBliveryException {
		Item item = new Item(quantity);
		item.setObjectId(new ObjectId());
		repository.saveAssociation(item, product, "item_product");
		Order ord = repository.addItemToOrder(order, item);
		return ord;
	}

	@Override
	public Order deliverOrder(ObjectId order, User deliveryUser) throws DBliveryException {
		Order actualOrder = this.getOrderById(order)
				.orElseThrow(() -> new DBliveryException(DBliveryServiceImpl.ORDER_NOT_FOUND));
		if (!this.canDeliver(order)) {
			throw new DBliveryException("The order can't be delivered");
		} else {
			actualOrder.setDeliveryUser(deliveryUser);
			actualOrder.getStatus().add(new OrderStatus(Order.SENDING));
			actualOrder.setCurrentStatus(Order.SENDING);
		}
		repository.updateStatusOrder(order, actualOrder);
		return actualOrder;
	}

	@Override
	public Order deliverOrder(ObjectId order, User deliveryUser, Date date) throws DBliveryException {
		Order actualOrder = this.getOrderById(order)
				.orElseThrow(() -> new DBliveryException(DBliveryServiceImpl.ORDER_NOT_FOUND));
		if (!this.canDeliver(order)) {
			throw new DBliveryException("The order can't be delivered");
		} else {
			actualOrder.setDeliveryUser(deliveryUser);
			actualOrder.getStatus().add(new OrderStatus(Order.SENDING, date));
			actualOrder.setCurrentStatus(Order.SENDING);
		}
		repository.updateStatusOrder(order, actualOrder);
		return actualOrder;
	}

	@Override
	public Order cancelOrder(ObjectId order) throws DBliveryException {
		Order actOrder = this.getOrderById(order)
				.orElseThrow(() -> new DBliveryException(DBliveryServiceImpl.ORDER_NOT_FOUND));
		if (!this.canCancel(order)) {
			throw new DBliveryException("The order can't be cancelled");
		} else {
			actOrder.getStatus().add(new OrderStatus(Order.CANCELLED));
			actOrder.setCurrentStatus(Order.CANCELLED);
		}
		repository.updateStatusOrder(order, actOrder);
		return actOrder;
	}

	@Override
	public Order cancelOrder(ObjectId order, Date date) throws DBliveryException {
		Order actOrder = this.getOrderById(order)
				.orElseThrow(() -> new DBliveryException(DBliveryServiceImpl.ORDER_NOT_FOUND));
		if (!this.canCancel(order)) {
			throw new DBliveryException("The order can't be cancelled");
		} else {
			actOrder.getStatus().add(new OrderStatus(Order.CANCELLED, date));
			actOrder.setCurrentStatus(Order.CANCELLED);
		}
		repository.updateStatusOrder(order, actOrder);
		return actOrder;
	}

	@Override
	public Order finishOrder(ObjectId order) throws DBliveryException {
		Order actOrder = this.getOrderById(order)
				.orElseThrow(() -> new DBliveryException(DBliveryServiceImpl.ORDER_NOT_FOUND));
		if (!this.canFinish(order)) {
			throw new DBliveryException("The order can't be finished");
		} else {
			actOrder.getStatus().add(new OrderStatus(Order.DELIVERED));
			actOrder.setCurrentStatus(Order.DELIVERED);
		}
		repository.updateStatusOrder(order, actOrder);
		return actOrder;
	}

	@Override
	public Order finishOrder(ObjectId order, Date date) throws DBliveryException {
		Order actOrder = this.getOrderById(order)
				.orElseThrow(() -> new DBliveryException(DBliveryServiceImpl.ORDER_NOT_FOUND));
		if (!this.canFinish(order)) {
			throw new DBliveryException("The order can't be finished");
		} else {
			actOrder.getStatus().add(new OrderStatus(Order.DELIVERED, date));
			actOrder.setCurrentStatus(Order.DELIVERED);
		}
		repository.updateStatusOrder(order, actOrder);
		return actOrder;
	}

	@Override
	public boolean canCancel(ObjectId order) throws DBliveryException {
		Order actOrder = this.getOrderById(order)
				.orElseThrow(() -> new DBliveryException(DBliveryServiceImpl.ORDER_NOT_FOUND));
		return actOrder.getActualStatus().getStatus().equals(Order.PENDING);
	}

	@Override
	public boolean canFinish(ObjectId id) throws DBliveryException {
		Order actOrder = this.getOrderById(id)
				.orElseThrow(() -> new DBliveryException(DBliveryServiceImpl.ORDER_NOT_FOUND));
		return !actOrder.isItemsEmpty() && actOrder.getActualStatus().getStatus().equals(Order.SENDING);
	}

	@Override
	public boolean canDeliver(ObjectId order) throws DBliveryException {
		Order actualOrder = this.getOrderById(order)
				.orElseThrow(() -> new DBliveryException(DBliveryServiceImpl.ORDER_NOT_FOUND));
		return !actualOrder.isItemsEmpty() && actualOrder.getActualStatus().getStatus().equals(Order.PENDING);
	}

	@Override
	public OrderStatus getActualStatus(ObjectId order) {
		Order actOrder = this.getOrderById(order).get();
		return actOrder.getActualStatus();
	}

	@Override
	public List<Product> getProductsByName(String name) {
		List<Product> prods = repository.findProductsLikeName(name);
		return repository.setSuppliersOfProducts(prods);
		
	}

	@Override
	public List<Order> getAllOrdersMadeByUser(String username) throws DBliveryException {
		User usr = this.getUserByUsername(username).orElseThrow(() -> new DBliveryException("No se ha encontrado un usuario con ese nombre"));
		List<Order> orders = repository.getObjectsAssociatedWith(usr.getObjectId(), Order.class, "order_usrClient", "orders");
		return repository.setClientsOfOrders(orders);
	}

	@Override
	public List<Supplier> getTopNSuppliersInSentOrders(int n) {
		return repository.getTopNSuppliersInSentOrders(n);
	}

	@Override
	public List<Order> getPendingOrders() {
		List<Order> ords = repository.getOrdersWithCurrentStatus("Pending");
		return repository.setClientsOfOrders(ords);
	}

	@Override
	public List<Order> getSentOrders() {
		List<Order> ords = repository.getOrdersWithCurrentStatus("Sending");
		return repository.setClientsOfOrders(ords);
	}

	@Override
	public List<Order> getDeliveredOrdersInPeriod(Date startDate, Date endDate) {
		List<Order> ords = repository.getDeliveredOrdersInPeriod(startDate, endDate);
		return repository.setClientsOfOrders(ords);
	}

	@Override
	public List<Order> getDeliveredOrdersForUser(String username) {
		User usr = repository.getUserByAttribute("username", username);
		List<Order> ords = repository.getOrdersOfTypeAssociatedWith("Delivered", usr.getObjectId());
		return repository.setClientsOfOrders(ords);
	}

	@Override
	public Product getBestSellingProduct() {
		Product prod = repository.getBestSellingProduct();
		List<Supplier> sups = repository.getAssociatedObjects(prod, Supplier.class, "product_supplier", "suppliers");
		prod.setSupplier(sups.get(0));
		return prod;
	}

	@Override
	public List<Product> getProductsOnePrice() {
		List<Product> prods = repository.getProductsOnePrice();
		return repository.setSuppliersOfProducts(prods);
	}

	@Override
	public List<Product> getSoldProductsOn(Date day) {
		List<Product> prods = repository.getSoldProductsOn(day);
		return repository.setSuppliersOfProducts(prods);
	}

	@Override
	public Product getMaxWeigth() {
		Product prod = repository.getMaxWeigth();
		List<Supplier> sups = repository.getAssociatedObjects(prod, Supplier.class, "product_supplier", "suppliers");
		prod.setSupplier(sups.get(0));
		return prod;
	}

	@Override
	public List<Order> getOrderNearPlazaMoreno() {
		List<Order> ords = repository.getOrderNearPlazaMoreno();
		return repository.setClientsOfOrders(ords);
	}

}
