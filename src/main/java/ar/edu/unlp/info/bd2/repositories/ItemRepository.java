package ar.edu.unlp.info.bd2.repositories;

import org.springframework.data.repository.CrudRepository;

import ar.edu.unlp.info.bd2.model.Item;

public interface ItemRepository extends CrudRepository<Item, Long> {

	
	
}
