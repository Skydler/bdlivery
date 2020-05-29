package ar.edu.unlp.info.bd2.repositories;

import static com.mongodb.client.model.Aggregates.*;
import static com.mongodb.client.model.Filters.*;
import static com.mongodb.client.model.Updates.*;
import static com.mongodb.client.model.Sorts.*;
import static com.mongodb.client.model.Accumulators.sum;

import ar.edu.unlp.info.bd2.model.*;
import ar.edu.unlp.info.bd2.mongo.*;

import com.mongodb.client.*;
import com.mongodb.client.model.FindOneAndUpdateOptions;
import com.mongodb.client.model.ReturnDocument;
import com.mongodb.client.model.geojson.Point;
import com.mongodb.client.model.geojson.Position;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;

public class DBliveryMongoRepository {

	@Autowired
	private MongoClient client;

	public MongoDatabase getDb() {
		return this.client.getDatabase("bd2_grupo8");
	}

	// Generic Methods

	public void saveAssociation(PersistentObject source, PersistentObject destination, String associationName) {
		Association association = new Association(source.getObjectId(), destination.getObjectId());
		this.getDb().getCollection(associationName, Association.class).insertOne(association);
	}

	public <T extends PersistentObject> List<T> getAssociatedObjects(PersistentObject source, Class<T> objectClass,
			String association, String destCollection) {
		// Realiza un left join con "destination" bajo una "association"
		AggregateIterable<T> iterable = this.getDb().getCollection(association, objectClass)
				.aggregate(Arrays.asList(match(eq("source", source.getObjectId())),
						lookup(destCollection, "destination", "_id", "_matches"), unwind("$_matches"),
						replaceRoot("$_matches")));
		Stream<T> stream = StreamSupport.stream(Spliterators.spliteratorUnknownSize(iterable.iterator(), 0), false);
		return stream.collect(Collectors.toList());
	}

	public <T extends PersistentObject, S extends PersistentObject> List<T> getAssociatedObjects(Iterable<S> sources,
			Class<T> objectClass, String association, String destCollection) {

		List<T> associated_objects = new ArrayList<T>();
		for (PersistentObject src : sources) {
			List<T> obj = this.getAssociatedObjects(src, objectClass, association, destCollection);
			associated_objects.addAll(obj);
		}
		return associated_objects;
	}

	public <T extends PersistentObject> List<T> getObjectsAssociatedWith(ObjectId objectId, Class<T> objectClass,
			String association, String destCollection) {
		// Realiza un right join con "destination" bajo una "association"
		AggregateIterable<T> iterable = this.getDb().getCollection(association, objectClass)
				.aggregate(Arrays.asList(match(eq("destination", objectId)),
						lookup(destCollection, "source", "_id", "_matches"), unwind("$_matches"),
						replaceRoot("$_matches")));
		Stream<T> stream = StreamSupport.stream(Spliterators.spliteratorUnknownSize(iterable.iterator(), 0), false);
		return stream.collect(Collectors.toList());
	}

	public <T> void saveObject(String collectionName, Class<T> cls, T obj) {
		this.getDb().getCollection(collectionName, cls).insertOne(obj);
	}

	public <T> T findById(String collectionName, Class<T> cls, ObjectId id) {
		return this.getDb().getCollection(collectionName, cls).find(eq("_id", id)).first();
	}

	// =============== Primera Parte ==================

	public User getUserByAttribute(String attribute, String value) {
		return this.getDb().getCollection("users", User.class).find(eq(attribute, value)).first();
	}

	public List<Product> findProductsLikeName(String name) {
		FindIterable<Product> collection = this.getDb().getCollection("products", Product.class)
				.find(regex("name", name));
		return collection.into(new ArrayList<Product>());
	}

	public Product updateProductPrice(ObjectId id, Price pri) {
		MongoCollection<Product> collection = this.getDb().getCollection("products", Product.class);
		FindOneAndUpdateOptions options = new FindOneAndUpdateOptions().returnDocument(ReturnDocument.AFTER);
		Product prod = collection.findOneAndUpdate(eq("_id", id), addToSet("prices", pri), options);
		return prod;
	}

	public Order addItemToOrder(ObjectId id, Item item) {
		MongoCollection<Order> collection = this.getDb().getCollection("orders", Order.class);
		FindOneAndUpdateOptions options = new FindOneAndUpdateOptions().returnDocument(ReturnDocument.AFTER);
		Order ord = collection.findOneAndUpdate(eq("_id", id), addToSet("items", item), options);
		return ord;
	}

	public void updateStatusOrder(ObjectId id, Order ord) {
		MongoCollection<Order> collection = this.getDb().getCollection("orders", Order.class);
		collection.findOneAndUpdate(eq("_id", id), combine(set("delivery", ord.getDeliveryUser()),
				addToSet("status", ord.getActualStatus()), set("currentStatus", ord.getCurrentStatus())));
	}

	// =============== Segunda Parte ==================

	public List<Supplier> getTopNSuppliersInSentOrders(int n) {
		AggregateIterable<Supplier> iterable = this.getDb().getCollection("orders", Supplier.class)
				.aggregate(Arrays.asList(match(eq("currentStatus", "Sending")), unwind("$items"), replaceRoot("$items"),
						lookup("item_product", "_id", "source", "_matchAssociation"), unwind("$_matchAssociation"),
						group("$_matchAssociation.destination", sum("total_product_quantity", "$quantity")),
						lookup("products", "_id", "_id", "_product"), unwind("$_product"),
						lookup("product_supplier", "_product._id", "source", "_supplier"), unwind("$_supplier"),
						group("$_supplier.destination", sum("total_supplier_quantity", "$total_product_quantity")),
						sort(descending("total_supplier_quantity")), limit(n),
						lookup("suppliers", "_id", "_id", "supplier"), unwind("$supplier"), replaceRoot("$supplier")));

		Stream<Supplier> stream = StreamSupport.stream(Spliterators.spliteratorUnknownSize(iterable.iterator(), 0),
				false);
		return stream.collect(Collectors.toList());
	}

	public List<Order> getOrdersWithCurrentStatus(String status) {
		FindIterable<Order> collection = this.getDb().getCollection("orders", Order.class)
				.find(eq("currentStatus", status));
		return collection.into(new ArrayList<Order>());
	}

	public List<Order> getDeliveredOrdersInPeriod(Date startDate, Date endDate) {
		FindIterable<Order> collection = this.getDb().getCollection("orders", Order.class)
				.find(and(eq("currentStatus", "Delivered"), gte("orderDate", startDate), lte("orderDate", endDate)));
		return collection.into(new ArrayList<Order>());
	}

	public List<Order> getOrdersOfTypeAssociatedWith(String string, ObjectId objectId) {
		AggregateIterable<Order> iterable = this.getDb().getCollection("order_usrClient", Order.class)
				.aggregate(Arrays.asList(match(eq("destination", objectId)),
						lookup("orders", "source", "_id", "_matches"), unwind("$_matches"), replaceRoot("$_matches"),
						match(eq("currentStatus", "Delivered"))));
		Stream<Order> stream = StreamSupport.stream(Spliterators.spliteratorUnknownSize(iterable.iterator(), 0), false);
		return stream.collect(Collectors.toList());
	}

	public Product getBestSellingProduct() {
		AggregateIterable<Product> iterable = this.getDb().getCollection("orders", Product.class)
				.aggregate(Arrays.asList(unwind("$items"), replaceRoot("$items"),
						lookup("item_product", "_id", "source", "_matchAssociation"), unwind("$_matchAssociation"),
						group("$_matchAssociation.destination", sum("total_quantity", "$quantity")),
						sort(descending("total_quantity")), limit(1), lookup("products", "_id", "_id", "_product"),
						unwind("$_product"), replaceRoot("$_product")));
		Product prod = iterable.first();
		return prod;
	}

	public List<Product> getProductsOnePrice() {
		FindIterable<Product> iterable = this.getDb().getCollection("products", Product.class).find(size("prices", 1));
		return iterable.into(new ArrayList<Product>());
	}

	public List<Product> getSoldProductsOn(Date day) {
		AggregateIterable<Item> iterable = this.getDb().getCollection("orders", Item.class)
				.aggregate(Arrays.asList(match(eq("orderDate", day)), unwind("$items"), replaceRoot("$items")));
		ArrayList<Item> item_list = iterable.into(new ArrayList<Item>());

		return this.getAssociatedObjects(item_list, Product.class, "item_product", "products");
	}

	public Product getMaxWeigth() {
		AggregateIterable<Product> iterable = this.getDb().getCollection("products", Product.class)
				.aggregate(Arrays.asList(sort(descending("weight")), limit(1)));
		return iterable.first();
	}

	public List<Order> getOrderNearPlazaMoreno() {
		Point pos = new Point(new Position(-34.921236, -57.954571));
		FindIterable<Order> iterable = this.getDb().getCollection("orders", Order.class)
				.find(near("position", pos, 400D, 0D));
		return iterable.into(new ArrayList<Order>());
	}

	public List<Order> setClientsOfOrders(List<Order> orders) {
		for (int i = 0; i < orders.size(); i++) {
			List<User> clients = this.getAssociatedObjects(orders.get(i), User.class, "order_usrClient", "users");
			orders.get(i).setClient(clients.get(0));
		}
		return orders;
	}

	public List<Product> setSuppliersOfProducts(List<Product> products) {
		for (int i = 0; i < products.size(); i++) {
			List<Supplier> sups = this.getAssociatedObjects(products.get(i), Supplier.class, "product_supplier",
					"suppliers");
			products.get(i).setSupplier(sups.get(0));
		}
		return products;
	}

}
