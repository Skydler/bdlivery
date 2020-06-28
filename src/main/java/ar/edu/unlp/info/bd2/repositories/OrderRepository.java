package ar.edu.unlp.info.bd2.repositories;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import ar.edu.unlp.info.bd2.model.Order;

public interface OrderRepository extends CrudRepository<Order, Long> {

	@Query("SELECT o FROM Order o WHERE o.currentStatus=:status")
	public List<Order> getOrdersInStatus(@Param("status")String status);
	
	@Query("SELECT o FROM User u JOIN u.orders o WHERE u.username=:username")
	public List<Order> getAllOrdersMadeByUser(@Param("username")String username);
	
	@Query("SELECT o FROM Order o JOIN o.statesRecord state WHERE state.status='Delivered' AND (state.statusDate BETWEEN :startDate AND :endDate)")
	public List<Order> getDeliveredOrdersInPeriodDate(@Param("startDate")Date startDate, @Param("endDate")Date endDate);

	@Query("SELECT o FROM Order o JOIN o.client c WHERE c.username = :username AND o.currentStatus='Delivered'")
	public List<Order> getDeliveredOrdersForUser(@Param("username")String username);
}
