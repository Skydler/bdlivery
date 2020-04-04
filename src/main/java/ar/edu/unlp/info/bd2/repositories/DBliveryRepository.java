package ar.edu.unlp.info.bd2.repositories;

import java.util.List;

import javax.persistence.TypedQuery;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;

import ar.edu.unlp.info.bd2.model.Product;
import ar.edu.unlp.info.bd2.model.User;

public class DBliveryRepository {

	@Autowired
	private SessionFactory sessionFactory;

	public DBliveryRepository() {

	}

	public <T> Object findById(Class<T> entityClass, Long id) {
		Session session = sessionFactory.getCurrentSession();
		Object obj = session.find(entityClass, id);
		return obj;
	}

	public Object saveObject(Object obj) {
		Session session = sessionFactory.getCurrentSession();
		session.saveOrUpdate(obj);
		return obj;
	}

	public Object deleteObject(Object obj) {
		Session session = sessionFactory.getCurrentSession();
		session.delete(obj);
		return obj;
	}

	public User findByEmail(String email) {
		String hql = "SELECT u FROM User u WHERE u.email = :email";

		Session session = sessionFactory.getCurrentSession();

		TypedQuery<User> query = session.createQuery(hql, User.class);
		query.setParameter("email", email);
		User user = ((Query<User>) query).uniqueResult();

		return user;
	}

	public User findByUsername(String username) {
		String hql = "SELECT u FROM User u WHERE u.username = :username";

		Session session = sessionFactory.getCurrentSession();

		TypedQuery<User> query = session.createQuery(hql, User.class);
		query.setParameter("username", username);
		User user = ((Query<User>) query).uniqueResult();

		return user;
	}

	public List<Product> findProductBySimilarName(String name) {
		String hql = "FROM Product p WHERE p.name like :name";

		Session session = sessionFactory.getCurrentSession();

		Query query = session.createQuery(hql);
		query.setParameter("name", "%" + name + "%");
		List<Product> prods = query.list();
		return prods;
	}
}
