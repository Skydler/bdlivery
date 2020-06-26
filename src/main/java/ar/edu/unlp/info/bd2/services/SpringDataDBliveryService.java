package ar.edu.unlp.info.bd2.services;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import ar.edu.unlp.info.bd2.model.Item;
import ar.edu.unlp.info.bd2.model.Order;
import ar.edu.unlp.info.bd2.model.OrderStatus;
import ar.edu.unlp.info.bd2.model.Product;
import ar.edu.unlp.info.bd2.model.Supplier;
import ar.edu.unlp.info.bd2.model.User;
import ar.edu.unlp.info.bd2.repositories.DBliveryException;
import ar.edu.unlp.info.bd2.repositories.UserRepository;
import ar.edu.unlp.info.bd2.repositories.ProductRepository;
import ar.edu.unlp.info.bd2.repositories.SupplierRepository;
import ar.edu.unlp.info.bd2.repositories.OrderRepository;
import ar.edu.unlp.info.bd2.repositories.ItemRepository;


public class SpringDataDBliveryService implements DBliveryService, DBliveryStatisticsService {

	@Autowired
	private UserRepository userRepository;
	@Autowired
	private ProductRepository productRepository;
	@Autowired
	private SupplierRepository supplierRepository;
	@Autowired
	private OrderRepository orderRepository;
	@Autowired
	private ItemRepository itemRepository;

	@Override
	@Transactional
	public Product getMaxWeigth() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	@Transactional
	public List<Order> getAllOrdersMadeByUser(String username) {
		return orderRepository.getAllOrdersMadeByUser(username);
	}

	@Override
	@Transactional
	public List<Order> getPendingOrders() {
		return orderRepository.getOrdersInStatus("Pending");
	}

	@Override
	@Transactional
	public List<Order> getSentOrders() {
		return orderRepository.getOrdersInStatus("Sending");
	}

	@Override
	@Transactional
	public List<Order> getDeliveredOrdersInPeriod(Date startDate, Date endDate) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	@Transactional
	public List<Order> getDeliveredOrdersForUser(String username) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	@Transactional
	public List<Product> getProductsOnePrice() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	@Transactional
	public List<Product> getSoldProductsOn(Date day) {
		return productRepository.getSoldProductsOn(day);
	}

	@Override
	@Transactional
	public Product createProduct(String name, Float price, Float weight, Supplier supplier) {
		Product prod = new Product(name, price, weight, supplier);
		productRepository.save(prod);
		return prod;
	}

	@Override
	@Transactional
	public Product createProduct(String name, Float price, Float weight, Supplier supplier, Date date) {
		Product prod = new Product(name, price, weight, supplier, date);
		productRepository.save(prod);
		return prod;
	}

	@Override
	@Transactional
	public Supplier createSupplier(String name, String cuil, String address, Float coordX, Float coordY) {
		Supplier sup = new Supplier(name, cuil, address, coordX, coordY);
		supplierRepository.save(sup);
		return sup;
	}

	@Override
	@Transactional
	public User createUser(String email, String password, String username, String name, Date dateOfBirth) {
		User usr = new User(email, password, username, name, dateOfBirth);
		userRepository.save(usr);
		return usr;
	}

	@Override
	@Transactional
	public Product updateProductPrice(Long id, Float price, Date startDate) throws DBliveryException {
		Product prod = productRepository.findById(id)
				.orElseThrow(() -> new DBliveryException("No se encontró el producto"));
		prod.addPrice(price, startDate);
		productRepository.save(prod);
		return prod;
	}

	@Override
	@Transactional
	public Optional<User> getUserById(Long id) {
		return userRepository.findById(id);
	}

	@Override
	@Transactional
	public Optional<User> getUserByEmail(String email) {
		return userRepository.findByEmail(email);
	}

	@Override
	@Transactional
	public Optional<User> getUserByUsername(String username) {
		return userRepository.findByUsername(username);
	}

	@Override
	@Transactional
	public Optional<Order> getOrderById(Long id) {
		return orderRepository.findById(id);
	}

	@Override
	@Transactional
	public Order createOrder(Date dateOfOrder, String address, Float coordX, Float coordY, User client) {
		Order ord = new Order(dateOfOrder, address, coordX, coordY, client);
		orderRepository.save(ord);
		return ord;
	}

	@Override
	@Transactional
	public Order addProduct(Long order, Long quantity, Product product) throws DBliveryException {
		Product prod = productRepository.findById(product.getId())
				.orElseThrow(() -> new DBliveryException("Producto no encontrado"));
		Order ord = orderRepository.findById(order).orElseThrow(() -> new DBliveryException("Orden no encontrada"));
		Item item = new Item(quantity, prod);
		itemRepository.save(item);
		ord.addProduct(item);
		orderRepository.save(ord);
		return ord;
	}

	@Override
	@Transactional
	public Order deliverOrder(Long order, User deliveryUser) throws DBliveryException {
		Order ord = orderRepository.findById(order).orElseThrow(() -> new DBliveryException("Orden no encontrada"));
		if (!this.canDeliver(order)) {
			throw new DBliveryException("La orden no se puede entregar");
		} else {
			ord.setDeliveryUser(deliveryUser);
			ord.getStatus().add(new OrderStatus(Order.SENDING));
			ord.setCurrentStatus(Order.SENDING);
		}
		orderRepository.save(ord);
		return ord;
	}

	@Override
	@Transactional
	public Order deliverOrder(Long order, User deliveryUser, Date date) throws DBliveryException {
		Order ord = orderRepository.findById(order).orElseThrow(() -> new DBliveryException("Orden no encontrada"));
		if (!this.canDeliver(order)) {
			throw new DBliveryException("La orden no se puede entregar");
		} else {
			ord.setDeliveryUser(deliveryUser);
			ord.getStatus().add(new OrderStatus(Order.SENDING, date));
			ord.setCurrentStatus(Order.SENDING);
		}
		orderRepository.save(ord);
		return ord;
	}

	@Override
	@Transactional
	public Order cancelOrder(Long order) throws DBliveryException {
		Order ord = orderRepository.findById(order).orElseThrow(() -> new DBliveryException("Orden no encontrada"));
		if (!this.canCancel(order)) {
			throw new DBliveryException("La orden no se puede cancelar");
		} else {
			ord.getStatus().add(new OrderStatus(Order.CANCELLED));
			ord.setCurrentStatus(Order.CANCELLED);
		}
		orderRepository.save(ord);
		return ord;
	}

	@Override
	@Transactional
	public Order cancelOrder(Long order, Date date) throws DBliveryException {
		Order ord = orderRepository.findById(order).orElseThrow(() -> new DBliveryException("Orden no encontrada"));
		if (!this.canCancel(order)) {
			throw new DBliveryException("La orden no se puede cancelar");
		} else {
			ord.getStatus().add(new OrderStatus(Order.CANCELLED, date));
			ord.setCurrentStatus(Order.CANCELLED);
		}
		orderRepository.save(ord);
		return ord;
	}

	@Override
	@Transactional
	public Order finishOrder(Long order) throws DBliveryException {
		Order ord = orderRepository.findById(order).orElseThrow(() -> new DBliveryException("Orden no encontrada"));
		if (!this.canFinish(order)) {
			throw new DBliveryException("La orden no se puede finalizar");
		} else {
			ord.getStatus().add(new OrderStatus(Order.DELIVERED));
			ord.setCurrentStatus(Order.DELIVERED);
		}
		orderRepository.save(ord);
		return ord;
	}

	@Override
	@Transactional
	public Order finishOrder(Long order, Date date) throws DBliveryException {
		Order ord = orderRepository.findById(order).orElseThrow(() -> new DBliveryException("Orden no encontrada"));
		if (!this.canFinish(order)) {
			throw new DBliveryException("La orden no se puede finalizar");
		} else {
			ord.getStatus().add(new OrderStatus(Order.CANCELLED, date));
			ord.setCurrentStatus(Order.CANCELLED);
		}
		orderRepository.save(ord);
		return ord;
	}

	@Override
	@Transactional
	public boolean canCancel(Long order) throws DBliveryException {
		Order ord = orderRepository.findById(order).orElseThrow(() -> new DBliveryException("Orden no encontrada"));
		return ord.getActualStatus().getStatus().equals(Order.PENDING);
	}

	@Override
	@Transactional
	public boolean canFinish(Long id) throws DBliveryException {
		Order ord = orderRepository.findById(id).orElseThrow(() -> new DBliveryException("Orden no encontrada"));
		return !ord.isItemsEmpty() && ord.getActualStatus().getStatus().equals(Order.SENDING);
	}

	@Override
	@Transactional
	public boolean canDeliver(Long order) throws DBliveryException {
		Order ord = orderRepository.findById(order).orElseThrow(() -> new DBliveryException("Orden no encontrada"));
		return !ord.isItemsEmpty() && ord.getActualStatus().getStatus().equals(Order.PENDING);
	}

	@Override
	@Transactional
	public OrderStatus getActualStatus(Long order) {
		Order ord = orderRepository.findById(order).get();
		return ord.getActualStatus();
	}

	@Override
	@Transactional
	public List<Product> getProductsByName(String name) {
		return productRepository.findBySimilarName(name);
	}

}
