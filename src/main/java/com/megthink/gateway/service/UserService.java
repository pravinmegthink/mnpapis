package com.megthink.gateway.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.megthink.gateway.dao.UserDao;
import com.megthink.gateway.model.Role;
import com.megthink.gateway.model.User;
import com.megthink.gateway.repository.UserRepository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.transaction.Transactional;

@Service("userService")
public class UserService {

	private static final Logger _logger = LoggerFactory.getLogger(UserService.class);

	private UserRepository userRepository;

	@Autowired
	public UserService(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	public User findUserByUsername(String username) {
		return userRepository.findByUsername(username).get();
	}
	public User findUserById(int userId) {
		return userRepository.findById(userId).orElse(null);
		
	}

	public User saveUser(User user) {
		return userRepository.save(user);
	}

	public User findUserByUserId(int userId) {
		return userRepository.findByUserId(userId);
	}

	public List<User> findAllUser() {
		return userRepository.findAll();
	}

	@PersistenceContext
	private EntityManager entityManager;

	@Transactional
	public void createRoleMappingEntry(int userId, int roleId) {
		try {
			entityManager.createNativeQuery("INSERT INTO user_role(role_id, user_id) values (?,?)")
					.setParameter(1, roleId).setParameter(2, userId).executeUpdate();
		} catch (Exception e) {
			_logger.error("UserService.createRoleMappingEntry() - " + e.getMessage());
		}
	}

	@SuppressWarnings("unchecked")
	@Transactional
	public List<User> getUserList() {
		List<User> list = null;
		try {
			String sql = "select users.user_id, company_name, contact_number, contact_person, created_by_user_id, users.created_date_time, email_id, first_name, last_name, password,status, users.updated_date_time, username,role.role_name as op_id from users join user_role on user_role.user_id=users.user_id join role on role.role_id=user_role.role_id";
			Query query = entityManager.createNativeQuery(sql, User.class);
			list = (List<User>) query.getResultList();
		} catch (Exception e) {
			_logger.error("Exception occurs while getting UserService.getUserList()-" + e.getMessage());
		}
		return list;
	}
	@SuppressWarnings("unchecked")
	@Transactional
	public List<User> getUserList1() {
		List<User> list = null;
		try {
			String sql = "select users.user_id, company_name, contact_number, contact_person, created_by_user_id, users.created_date_time, email_id, first_name, last_name, null as password, status, users.updated_date_time, username,role.role_name as op_id from users join user_role on user_role.user_id=users.user_id join role on role.role_id=user_role.role_id";
			Query query = entityManager.createNativeQuery(sql, User.class);
			list = (List<User>) query.getResultList();
		} catch (Exception e) {
			_logger.error("Exception occurs while getting UserService.getUserList1()-" + e.getMessage());
		}
		System.out.println(list);
		return list;
	}
	
	@SuppressWarnings("unchecked")
	@Transactional
	public List<Role> getGroups() {
		List<Role> list = null;
		try {
			String sql = "select * from role";
						Query query = entityManager.createNativeQuery(sql, Role.class);
			list = (List<Role>) query.getResultList();
		} catch (Exception e) {
			_logger.error("Exception occurs while getting UserService.getGroups()-" + e.getMessage());
		}
		System.out.println(list);
		return list;
	}
	@Autowired
	private UserDao userDao;

	public int isUserExist(String userName) {
		return userDao.isUserExist(userName);
	}
	
	 public boolean deactivateUser(int userId) {
	        User user = findUserById(userId);
	        if (user != null) {
	            user.setStatus(0); 
	            saveUser(user); 
	            return true;
	        }
	        return false; 
	 }

	
}