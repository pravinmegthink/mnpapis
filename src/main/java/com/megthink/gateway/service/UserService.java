package com.megthink.gateway.service;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import com.megthink.gateway.dao.UserDao;
import com.megthink.gateway.model.Role;
import com.megthink.gateway.model.User;
import com.megthink.gateway.model.UserDTO;
import com.megthink.gateway.repository.UserRepository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.transaction.Transactional;

@Service("userService")
public class UserService {

	private static final Logger _logger = LoggerFactory.getLogger(UserService.class);

	private UserRepository userRepository;
	DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");


	 @Autowired
	    private JdbcTemplate jdbcTemplate;
	 
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
	public User findUserByEmail(String emailId) {
        return userRepository.findByEmailId(emailId);
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
	                     .setParameter(1, roleId)
	                     .setParameter(2, userId)
	                     .executeUpdate();
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
	    public List<UserDTO> getUserList2() {
	        List<UserDTO> list = new ArrayList<>();
	        try {
	            String sql = "SELECT u.user_id, u.first_name, u.last_name, u.username, u.password, u.email_id, " +
	                         "u.contact_number, u.company_name, u.status, u.created_by_user_id, u.op_id, " +
	                         "u.created_date_time, u.updated_date_time, r.role_id, r.role_name " +
	                         "FROM users u " +
	                         "JOIN user_role ur ON u.user_id = ur.user_id " +
	                         "JOIN role r ON ur.role_id = r.role_id";

	            Query query = entityManager.createNativeQuery(sql);
	            List<Object[]> results = query.getResultList();
	            
	            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	            for (Object[] row : results) {
	                try {
	                    Timestamp createdTimestamp = null;
	                    Timestamp updatedTimestamp = null;

	                    if (row[11] instanceof String) {
	                        createdTimestamp = new Timestamp(dateFormat.parse((String) row[11]).getTime());
	                    } else if (row[11] instanceof Timestamp) {
	                        createdTimestamp = (Timestamp) row[11];
	                    }

	                    if (row[12] instanceof String) {
	                        updatedTimestamp = new Timestamp(dateFormat.parse((String) row[12]).getTime());
	                    } else if (row[12] instanceof Timestamp) {
	                        updatedTimestamp = (Timestamp) row[12];
	                    }

	                    UserDTO userDto = new UserDTO(
	                        ((Number) row[0]).intValue(), // user_id
	                        (String) row[1], // first_name
	                        (String) row[2], // last_name
	                        (String) row[3], // username
	                        (String) row[4], // password
	                        (String) row[5], // email_id
	                        (String) row[6], // contact_number
	                        (String) row[7], // company_name
	                        ((Number) row[8]).intValue(), // status
	                        ((Number) row[9]).intValue(), // created_by_user_id
	                        (String) row[10], // op_id
	                        createdTimestamp, // created_date_time
	                        updatedTimestamp, // updated_date_time
	                        ((Number) row[13]).intValue(), // role_id
	                        (String) row[14] // role_name
	                    );

	                    list.add(userDto);
	                } catch (Exception e) {
	                    _logger.error("Error processing user data: " + e.getMessage(), e);
	                }
	            }
	        } catch (Exception e) {
	            _logger.error("Exception while getting UserService.getUserList2(): " + e.getMessage(), e);
	        }
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
	 
	 public boolean deactivateUserByEmail(String emailId) {
		    User user = findUserByEmail(emailId);
		    if (user != null) {
		        user.setStatus(0); 
		        saveUser(user); 
		        return true;
		    }
		    return false;
		}

	 public boolean deactivateUserByEmail1(String emailId) {
		    User user = findUserByEmail(emailId);
		    if (user != null) {
		        user.setStatus(0); 
		        saveUser(user); 
		        deleteUserRolesByUserId1(user.getUserId());

		        return true;
		    }
		    return false;
		}
	 public void deleteUserRolesByUserId1(int userId) {
		    String sql = "DELETE FROM user_role WHERE user_id = ?";
		    jdbcTemplate.update(sql, userId);
		}

	 @Transactional
	 public boolean reactivateUserAndAssignRole(String emailId, int roleId) {
	     User user = findUserByEmail(emailId);
	     if (user != null) {
	         user.setStatus(1);
	         saveUser(user);

	         createRoleMappingEntry(user.getUserId(), roleId);

	         return true;
	     }
	     return false;
	 }
	 
	
	 
}