package ar.edu.unlp.info.bd2.repositories;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import ar.edu.unlp.info.bd2.model.Order;

public interface OrderRepository extends CrudRepository<Order, Long> {

	public List<Order> findByCurrentStatus(String status);

	@Query("SELECT o FROM User u JOIN u.orders o WHERE u.username=:username")
	public List<Order> getAllOrdersMadeByUser(@Param("username") String username);

	public List<Order> findByCurrentStatusAndOrderDateBetween(String status, Date startDate, Date endDate);

	@Query("SELECT o FROM Order o JOIN o.client c WHERE c.username = :username AND o.currentStatus='Delivered'")
	public List<Order> getDeliveredOrdersForUser(@Param("username") String username);
}
