package ar.edu.unlp.info.bd2.repositories;

import java.util.Date;
import java.util.List;

import javax.persistence.TypedQuery;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;

import ar.edu.unlp.info.bd2.model.Order;
import ar.edu.unlp.info.bd2.model.Product;
import ar.edu.unlp.info.bd2.model.Supplier;
import ar.edu.unlp.info.bd2.model.User;

public class DBliveryRepository {

	@Autowired
	private SessionFactory sessionFactory;

	public DBliveryRepository() {

	}

	public <T> Object findById(Class<T> entityClass, Long id) {
		Session session = null;
		session = sessionFactory.getCurrentSession();
		Object obj = session.find(entityClass, id);
		return obj;
	}

	public void saveObject(Object obj) {
		Session session = null;
		try {
			session = sessionFactory.getCurrentSession();
			session.saveOrUpdate(obj);
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}

	public void deleteObject(Object obj) {
		Session session = null;
		session = sessionFactory.getCurrentSession();
		session.delete(obj);
	}

	public User findByEmail(String email) {
		String hql = "SELECT u FROM User u WHERE u.email = :email";

		Session session = null;
		session = sessionFactory.getCurrentSession();

		TypedQuery<User> query = session.createQuery(hql, User.class);
		query.setParameter("email", email);
		User user = ((Query<User>) query).uniqueResult();

		return user;
	}

	public User findByUsername(String username) {
		String hql = "SELECT u FROM User u WHERE u.username = :username";

		Session session = null;
		session = sessionFactory.getCurrentSession();

		TypedQuery<User> query = session.createQuery(hql, User.class);
		query.setParameter("username", username);
		User user = ((Query<User>) query).uniqueResult();

		return user;
	}

	@SuppressWarnings("unchecked")
	public List<Product> findProductBySimilarName(String name) {
		String hql = "FROM Product p WHERE p.name like :name";

		Session session = null;
		session = sessionFactory.getCurrentSession();

		Query<?> query = session.createQuery(hql);
		query.setParameter("name", "%" + name + "%");
		List<Product> prods = (List<Product>) query.list();
		return prods;
	}

	// ------------------ Segundo Enunciado ------------------

	@SuppressWarnings("unchecked")
	public List<Order> getAllOrdersMadeByUser(String username) {
		String hql = "SELECT o FROM User u JOIN u.orders o WHERE u.username = :username";
		Session session = sessionFactory.getCurrentSession();
		Query<?> query = session.createQuery(hql);
		query.setParameter("username", username);
		List<Order> orders = (List<Order>) query.list();
		return orders;
	}

	@SuppressWarnings("unchecked")
		public List<User> getUsersSpendingMoreThan(Float amount){
			String hql = "SELECT o.client FROM Order o JOIN o.client WHERE o.amount > :amount";

			Session session = null;
			session = sessionFactory.getCurrentSession();

			Query<?> query = session.createQuery(hql);
			query.setParameter("amount", amount);
			List<User> users = (List<User>) query.list();
			return users;
		}

	@SuppressWarnings("unchecked")
	public List<Supplier> getTopNSuppliersInSentOrders(int n){
		String hql = "SELECT s FROM Order o JOIN o.items i JOIN i.product p JOIN p.supplier s WHERE o.currentStatus='Sending' GROUP BY s ORDER BY SUM(i.quantity) DESC";
		Session session = sessionFactory.getCurrentSession();
		Query<?> query = session.createQuery(hql);
		query.setFirstResult(0);
		query.setMaxResults(n);
		List<Supplier> suppliers = (List<Supplier>) query.list();
		return suppliers;
	}

	@SuppressWarnings("unchecked")
	public List<Product> getTop10MoreExpensiveProducts(){
		String hql ="SELECT p FROM Product p JOIN p.prices price WHERE price.value > (SELECT MIN(price.value) FROM Price price) ORDER BY price.value DESC";
		Session session = sessionFactory.getCurrentSession();
		Query<?> query = session.createQuery(hql);
		query.setFirstResult(0);
		query.setMaxResults(9);
		List<Product> products = (List<Product>) query.list();
		return products;
	}

	@SuppressWarnings("unchecked")
	public List<User> getTop6UsersMoreOrders(){
		String hql = "SELECT u FROM User u JOIN u.orders o GROUP BY u.id ORDER BY COUNT(o) DESC";

		Session session = sessionFactory.getCurrentSession();
		Query<?> query = session.createQuery(hql);
		query.setFirstResult(0);
		query.setMaxResults(6);
		List<User> users = (List<User>) query.list();
		return users;
	}

	@SuppressWarnings("unchecked")
	public List <Order>  getCancelledOrdersInPeriod(Date startDate, Date endDate){
		String hql = "SELECT o FROM Order o JOIN o.statesRecord os WHERE os.status = 'Cancelled' AND os.statusDate BETWEEN :startDate AND :endDate";

		Session session = sessionFactory.getCurrentSession();
		Query<?> query = session.createQuery(hql);
		query.setParameter("startDate", startDate);
		query.setParameter("endDate", endDate);
		List<Order> orders = (List<Order>) query.list();
		return orders;
	}

	@SuppressWarnings("unchecked")
	public List <Order>  getSentOrders(){
		String hql = "SELECT o FROM Order o WHERE o.currentStatus = 'Sending'";

		Session session = sessionFactory.getCurrentSession();
		Query<?> query = session.createQuery(hql);
		List<Order> orders = (List<Order>) query.list();
		return orders;
	}

	@SuppressWarnings("unchecked")
	public List <Order>  getDeliveredOrdersInPeriod(Date startDate, Date endDate){
		String hql = "SELECT o FROM Order o JOIN o.statesRecord os WHERE os.status = 'Delivered' AND os.statusDate BETWEEN :startDate AND :endDate";

		Session session = sessionFactory.getCurrentSession();
		Query<?> query = session.createQuery(hql);
		query.setParameter("startDate", startDate);
		query.setParameter("endDate", endDate);
		List<Order> orders = (List<Order>) query.list();
		return orders;
	}

	@SuppressWarnings("unchecked")
	public List <Order>  getSentMoreOneHour(){
		String hql = "SELECT o FROM Order o JOIN o.statesRecord s1 JOIN o.statesRecord s2 WHERE s1.status='Pending' AND s2.status='Sending' AND (s2.statusDate-s1.statusDate) >= 1";
		Session session = sessionFactory.getCurrentSession();
		Query<?> query = session.createQuery(hql);
		List<Order> orders = (List<Order>) query.list();
		return orders;
	}

	@SuppressWarnings("unchecked")
	public List <Order>  getDeliveredOrdersSameDay(){
		String hql = "SELECT o FROM Order o JOIN o.statesRecord s1 JOIN o.statesRecord s2 WHERE s1.status='Pending' AND s2.status='Delivered' AND DATEDIFF(s2.statusDate,s1.statusDate) < 1";

		Session session = sessionFactory.getCurrentSession();
		Query<?> query = session.createQuery(hql);
		List<Order> orders = (List<Order>) query.list();
		return orders;
	}

	@SuppressWarnings("unchecked")
	public List<Order> getOrderWithMoreQuantityOfProducts(Date day) {
		String hql = "SELECT ord FROM Order ord JOIN ord.items it WHERE ord.orderDate = :day GROUP BY ord HAVING SUM(it.quantity) >= ALL (SELECT SUM(i.quantity) FROM Order o JOIN o.items i WHERE o.orderDate = :day GROUP BY o)";

		Session session = sessionFactory.getCurrentSession();
		Query<?> query = session.createQuery(hql);
		query.setParameter("day", day);
		List<Order> orders = (List<Order>) query.list();
		return orders;
	}

	@SuppressWarnings("unchecked")
	public List<Product> getProductsNotSold() {
		String hql = "FROM Product p WHERE NOT EXISTS (FROM Item i WHERE i.product = p)";

		Session session = sessionFactory.getCurrentSession();
		Query<?> query = session.createQuery(hql);
		List<Product> products = (List<Product>) query.list();
		return products;
	}

	@SuppressWarnings("unchecked")
	public List<Object[]> getProductsWithPriceAt(Date day) {
		String hql = "SELECT p, price.value FROM Product p JOIN p.prices price WHERE (:day >= price.startDate AND price.endDate IS NULL) OR (:day BETWEEN price.startDate AND price.endDate)";

		Session session = sessionFactory.getCurrentSession();
		Query<?> query = session.createQuery(hql);
		query.setParameter("day", day);
		List<Object[]> products = (List<Object[]>) query.list();
		return products;
	}

	@SuppressWarnings("unchecked")
	public List<Order> getOrdersCompleteMorethanOneDay() {
		String hql = "SELECT o FROM Order o JOIN o.statesRecord s1 JOIN o.statesRecord s2 WHERE s1.status='Pending' AND s2.status='Delivered' AND DATEDIFF(s2.statusDate,s1.statusDate) >= 1";
		Session session = sessionFactory.getCurrentSession();
		Query<?> query = session.createQuery(hql);
		List<Order> orders = (List<Order>) query.list();
		return orders;
	}

	@SuppressWarnings("unchecked")
	public List<Product> getSoldProductsOn(Date day) {
		String hql = "SELECT p FROM Order o JOIN o.items i JOIN i.product p WHERE o.orderDate = :day";

		Session session = sessionFactory.getCurrentSession();
		Query<?> query = session.createQuery(hql);
		query.setParameter("day", day);
		List<Product> products = (List<Product>) query.list();
		return products;
	}

	@SuppressWarnings("unchecked")
	public List<Supplier> getSuppliersDoNotSellOn(Date day) {
		String hql = "SELECT s FROM Supplier s WHERE s NOT IN (SELECT s FROM Order o JOIN o.items i JOIN i.product p JOIN p.supplier s WHERE o.orderDate = :day)";

		Session session = sessionFactory.getCurrentSession();
		Query<?> query = session.createQuery(hql);
		query.setParameter("day", day);
		List<Supplier> suppliers = (List<Supplier>) query.list();
		return suppliers;
	}

	public Supplier getSupplierLessExpensiveProduct() {
		String hql = "SELECT s FROM Product p JOIN p.supplier s JOIN p.prices pri WHERE pri.value = (SELECT MIN(price.value) FROM Price price)";

		Session session = sessionFactory.getCurrentSession();
		TypedQuery<Supplier> query = session.createQuery(hql, Supplier.class);
		Supplier supplier = ((Query<Supplier>) query).uniqueResult();
		return supplier;
	}

	@SuppressWarnings("unchecked")
	public List<Product> getProductIncreaseMoreThan100() {
		String hql = "SELECT DISTINCT(p) FROM Product p JOIN p.prices pri WHERE (SELECT MAX(pric.value) FROM Product pr JOIN pr.prices pric WHERE p=pr) >= pri.value * 2";

		Session session = sessionFactory.getCurrentSession();
		Query<?> query = session.createQuery(hql);
		List<Product> products = (List<Product>) query.list();
		return products;
	}

	@SuppressWarnings("unchecked")
	public List<Product> getProductsOnePrice() {
		String hql = "SELECT p FROM Product p JOIN p.prices pri GROUP BY p HAVING COUNT(*) = 1";

		Session session = sessionFactory.getCurrentSession();
		Query<?> query = session.createQuery(hql);
		List<Product> products = (List<Product>) query.list();
		return products;
	}

	public Product getBestSellingProduct() {
		String hql = "SELECT p FROM Item i JOIN i.product p GROUP BY p ORDER BY SUM(i.quantity) DESC";

		Session session = sessionFactory.getCurrentSession();
		TypedQuery<Product> query = session.createQuery(hql, Product.class);
		query.setFirstResult(0);
		query.setMaxResults(1);
		Product product = ((Query<Product>) query).uniqueResult();
		return product;
	}

	@SuppressWarnings("unchecked")
	public List<User> get5LessDeliveryUsers() {
		String hql = "SELECT u FROM User u JOIN u.deliveredOrders o GROUP BY u ORDER BY COUNT(o)";

		Session session = sessionFactory.getCurrentSession();
		Query<?> query = session.createQuery(hql);
		query.setFirstResult(0);
		query.setMaxResults(5);
		List<User> users = (List<User>) query.list();
		return users;
	}

	@SuppressWarnings("unchecked")
	public List<Order> getPendingOrders() {
		String hql = "SELECT o FROM Order o WHERE o.currentStatus='Pending'";
		Session session = sessionFactory.getCurrentSession();
		Query<?> query = session.createQuery(hql);
		List<Order> orders = (List<Order>) query.list();
		return orders;
	}

	@SuppressWarnings("unchecked")
	public List<Order> getDeliveredOrdersForUser(String username) {
		String hql = "SELECT o FROM Order o JOIN o.client c WHERE c.username = :username AND o.currentStatus='Delivered'";
		Session session = sessionFactory.getCurrentSession();
		Query<?> query = session.createQuery(hql);
		query.setParameter("username",  username);
		List<Order> orders = (List<Order>) query.list();
		return orders;
	}
}
