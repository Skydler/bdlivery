package ar.edu.unlp.info.bd2.repositories;

import static com.mongodb.client.model.Aggregates.*;
import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Filters.regex;

import ar.edu.unlp.info.bd2.model.*;
import ar.edu.unlp.info.bd2.mongo.*;
import com.mongodb.client.*;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.springframework.beans.factory.annotation.Autowired;

public class DBliveryMongoRepository {

	@Autowired
	private MongoClient client;

	public void saveAssociation(PersistentObject source, PersistentObject destination, String associationName) {
		Association association = new Association(source.getObjectId(), destination.getObjectId());
		this.getDb().getCollection(associationName, Association.class).insertOne(association);
	}

	public MongoDatabase getDb() {
		return this.client.getDatabase("dblivery");
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

	public void saveUser(String collectionName, User obj) {
		this.getDb().getCollection(collectionName, User.class).insertOne(obj);
	}

}
