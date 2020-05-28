package ar.edu.unlp.info.bd2.repositories;

import static com.mongodb.client.model.Aggregates.*;
import static com.mongodb.client.model.Filters.*;
import static com.mongodb.client.model.Updates.*;
import static com.mongodb.client.model.Sorts.*;

import ar.edu.unlp.info.bd2.model.*;
import ar.edu.unlp.info.bd2.mongo.*;

import com.mongodb.client.*;
import com.mongodb.client.model.FindOneAndUpdateOptions;
import com.mongodb.client.model.ReturnDocument;
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
		AggregateIterable<Order> iterable = this.getDb().getCollection("orders", Order.class)
				.aggregate(Arrays.asList(match(eq("currentStatus", "Sending")), unwind("$items"), replaceRoot("$items"),
						sort(descending("quantity")), limit(n)));

		List<Product> filtered_prods = this.getAssociatedObjects(iterable, Product.class, "item_product", "products");
		List<Supplier> filtered_suppliers = this.getAssociatedObjects(filtered_prods, Supplier.class,
				"product_supplier", "suppliers");
		return filtered_suppliers;
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
		// TODO: Tengo algúna opción que no sea copiar el esquema de
		// getObjectsAssociatedWith?
		// TODO: Deberíamos filtrar las ordenes en la BD o en Java? De la segunda forma
		// se puede reutilizar el getObjectsAssociatedWith
		AggregateIterable<Order> iterable = this.getDb().getCollection("order_usrClient", Order.class)
				.aggregate(Arrays.asList(match(eq("destination", objectId)),
						lookup("orders", "source", "_id", "_matches"), unwind("$_matches"), replaceRoot("$_matches"),
						match(eq("currentStatus", "Delivered"))));
		Stream<Order> stream = StreamSupport.stream(Spliterators.spliteratorUnknownSize(iterable.iterator(), 0), false);
		return stream.collect(Collectors.toList());
	}

	public Product getBestSellingProduct() {
		AggregateIterable<Item> iterable = this.getDb().getCollection("orders", Item.class).aggregate(
				Arrays.asList(unwind("$items"), replaceRoot("$items"), sort(descending("quantity")), limit(1)));
		Item item = iterable.first();
		List<Product> products = this.getAssociatedObjects(item, Product.class, "item_product", "products");
		return products.get(0);
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

}
