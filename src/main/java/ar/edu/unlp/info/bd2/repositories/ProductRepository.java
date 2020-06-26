package ar.edu.unlp.info.bd2.repositories;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import ar.edu.unlp.info.bd2.model.Product;

public interface ProductRepository extends CrudRepository<Product, Long> {

	List<Product> findByNameContaining(String name);

	@Query("SELECT p FROM Order o JOIN o.items i JOIN i.product p WHERE o.orderDate=:day")
	List<Product> getSoldProductsOn(@Param("day") Date day);

	@Query("SELECT p FROM Product p WHERE p.weight = (SELECT MAX(prod.weight) FROM Product prod)")
	Product getMaxWeight();

	@Query("SELECT p FROM Product p JOIN p.prices price GROUP BY p HAVING COUNT(*) = 1")
	List<Product> getProductsOnePrice();
}
