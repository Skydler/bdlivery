package ar.edu.unlp.info.bd2.repositories;

import static com.mongodb.client.model.Aggregates.*;
import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Updates.*;

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

	public void saveAssociation(PersistentObject source, PersistentObject destination, String associationName) {
		Association association = new Association(source.getObjectId(), destination.getObjectId());
		this.getDb().getCollection(associationName, Association.class).insertOne(association);
	}

	public MongoDatabase getDb() {
		return this.client.getDatabase("bd_grupo8");
	}

	public <T extends PersistentObject> List<T> getAssociatedObjects(PersistentObject source, Class<T> objectClass,
			String association, String destCollection) {
		AggregateIterable<T> iterable = this.getDb().getCollection(association, objectClass)
				.aggregate(Arrays.asList(match(eq("source", source.getObjectId())),
						lookup(destCollection, "destination", "_id", "_matches"), unwind("$_matches"),
						replaceRoot("$_matches")));
		Stream<T> stream = StreamSupport.stream(Spliterators.spliteratorUnknownSize(iterable.iterator(), 0), false);
		return stream.collect(Collectors.toList());
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void saveObject(String collectionName, Class cls, Object obj) {
		this.getDb().getCollection(collectionName, cls).insertOne(obj);
	}

	public Product updateProduct(ObjectId id, Price pri) {
		MongoCollection<Product> collection = this.getDb().getCollection("products", Product.class);
		FindOneAndUpdateOptions options = new FindOneAndUpdateOptions().returnDocument(ReturnDocument.AFTER);
		Product prod = collection.findOneAndUpdate(eq("_id", id), addToSet("prices", pri), options);
		return prod;
	}
	
	public User getUserByUsername(String username) {
		return this.getDb().getCollection("users", User.class).find(eq("username", username)).first();
	}
	
	/*public User getUserById(ObjectId id) {
		return this.getDb().getCollection("users", User.class).find(eq("id", id)).first();
	}*/
	
	public User getUserByEmail(String email) {
		return this.getDb().getCollection("users", User.class).find(eq("email", email)).first();
	}

}