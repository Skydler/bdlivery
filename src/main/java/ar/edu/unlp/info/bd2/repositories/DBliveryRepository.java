package ar.edu.unlp.info.bd2.repositories;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;

import ar.edu.unlp.info.bd2.model.User;

public class DBliveryRepository {

	@Autowired
	private SessionFactory sessionFactory;

	public DBliveryRepository() {

	}

	public <T> Object findById(Class<T> entityClass, Long id) {
		Session session = sessionFactory.getCurrentSession();
		return session.find(entityClass, id);
	}

	public Object saveObject(Object obj) {
		Session session = sessionFactory.openSession();
		session.beginTransaction();

		session.saveOrUpdate(obj);

		session.getTransaction().commit();
		session.close();
		return obj;
	}

	public Object deleteObject(Object obj) {
		Session session = sessionFactory.getCurrentSession();
		session.beginTransaction();

		session.delete(obj);

		session.getTransaction().commit();
		session.close();
		return obj;
	}

	public User findByEmail(String email) {
		Session session = sessionFactory.getCurrentSession();
		String hql = "SELECT u FROM Users u WHERE u.email = :email";
		Query<User> query = session.createQuery(hql, User.class);
		query.setParameter("email", email);
		User user = query.uniqueResult();
		return user;
	}

	public User findByUsername(String username) {
		Session session = sessionFactory.getCurrentSession();
		String hql = "SELECT u FROM Users u WHERE u.username = :username";
		Query<User> query = session.createQuery(hql, User.class);
		query.setParameter("username", username);
		User user = query.uniqueResult();
		return user;
	}
}
