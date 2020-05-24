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

public class DBliveryServiceImpl implements DBliveryService {

	private DBliveryMongoRepository repository;

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
		Product prod = repository.updateProduct(id, pri);
		return prod;
	}

	@Override
	//M
	public Optional<User> getUserById(ObjectId id) {
		/*User user = repository.getUserByEmail(email);
		return user;*/
		return null;
	}

	@Override
	//M
	public Optional<User> getUserByEmail(String email) {
		/*User user = repository.getUserByEmail(email);
		return user;*/
		return null;
	}

	@Override
	//M
	public Optional<User> getUserByUsername(String username) {
		/*User user = repository.getUserByUsername(username);
		return user;*/
		return null;
	}

	@Override
	public Optional<Order> getOrderById(ObjectId id) {
		// TODO Auto-generated method stub
		return null;
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
		Order ord = repository.updateOrder(order, item);
		return ord;
	}

	@Override
	public Order deliverOrder(ObjectId order, User deliveryUser) throws DBliveryException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Order deliverOrder(ObjectId order, User deliveryUser, Date date) throws DBliveryException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	//M
	public Order cancelOrder(ObjectId order) throws DBliveryException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	//M
	public Order cancelOrder(ObjectId order, Date date) throws DBliveryException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	//M
	public Order finishOrder(ObjectId order) throws DBliveryException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	//M
	public Order finishOrder(ObjectId order, Date date) throws DBliveryException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	//M
	public boolean canCancel(ObjectId order) throws DBliveryException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	//M
	public boolean canFinish(ObjectId id) throws DBliveryException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean canDeliver(ObjectId order) throws DBliveryException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	//M
	public OrderStatus getActualStatus(ObjectId order) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	//M
	public List<Product> getProductsByName(String name) {
		// TODO Auto-generated method stub
		return null;
	}

}
