package io.flashboard.dao;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;

import io.flashboard.beans.CompletedComprehensionTest;
import io.flashboard.beans.User;
import io.flashboard.util.HibernateUtil;

public class UserDaoImpl implements UserDao{
	

	@Override
	public boolean createNewUser(String firstName, String lastName, String username,
		String email, String password) {
		Boolean bool = false;
		Session session = HibernateUtil.getSession();
		Transaction tx = null;
		
		User newUser = new User(firstName, lastName, username, email, password);
		
		try {
			tx = session.beginTransaction();
			session.save(newUser);
			tx.commit();
			bool = true;
			System.out.println("User: '" + newUser.getUsername() + "' has been successfully created!");
		} catch (HibernateException he) {
			if (tx != null) {
				tx.rollback();
			}
			System.out.println("User creation failed!");
			he.printStackTrace();
			
		} finally {
			session.close();
		}
		return bool;
	}
	
	public User selectUserByUsername(String username) {
		Session session = HibernateUtil.getSession();
		String hql = "FROM USERS U WHERE U.USERNAME = " + username;
		User newUser = null;
		try {
			Query query = session.createQuery(hql);
			newUser = (User)query.uniqueResult();
		} catch (HibernateException he) {
			he.printStackTrace();
		} finally {
			session.close();
		}
		return newUser;
	}
	
	/**
	 * Gets a unique user from the database, if he/she exists
	 * Utilizes Criterias
	 * 
	 * @param username username to be searched for
	 * @return the user if he/she exists, otherwise null
	 */
	public User getUserByUsername(String username) {
		Session session = HibernateUtil.getSession();
		Criteria criteria;
		User user = null;
		
		try {
			criteria = session.createCriteria(User.class);
			//Adds like restriction to search for a particular username
			user = (User)criteria.add(Restrictions.like("username", username)).uniqueResult();
		}
		catch(HibernateException he) {
			he.printStackTrace();
		}finally {
			session.close();
		}
		return user;
	}

	@Override
	public User selectUserById(int userId) {
		Session session = HibernateUtil.getSession();
		String hql = "FROM USERS U WHERE U.USER_ID = " + userId;
		User newUser = null;
		try {
			Query query = session.createQuery(hql);
			newUser = (User)query.uniqueResult();
		} catch (HibernateException he) {
			he.printStackTrace();
		} finally {
			session.close();
		}
		return newUser;
	}
	
	@Override
	public boolean addTakenTest(String username, CompletedComprehensionTest test) {
		Session session = HibernateUtil.getSession();
		Transaction tx = session.beginTransaction();
		User currUser = selectUserByUsername(username);
		List<CompletedComprehensionTest> newList;
		newList = currUser.getTakenTests();
		newList.add(test);
		currUser.setTakenTests(newList);
		session.save(currUser);
		tx.commit();
		return true;
	}
}
