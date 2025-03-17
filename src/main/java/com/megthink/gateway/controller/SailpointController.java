package com.megthink.gateway.controller;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import org.springframework.http.MediaType;
import java.util.Map;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import java.io.IOException;
import java.sql.Timestamp;
import java.text.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import com.megthink.gateway.form.UserForm;
import com.megthink.gateway.form.UserFormMapper;
import com.megthink.gateway.model.Role;
import com.megthink.gateway.model.User;
import com.megthink.gateway.model.UserDTO;
import com.megthink.gateway.model.WebAccessTrace;
import com.megthink.gateway.service.UserService;
import com.megthink.gateway.service.WebAccessTraceService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/spapi")
public class SailpointController {
	 
	private static final Logger _logger = LoggerFactory.getLogger(UserController.class);

	 @Autowired
	    private UserService userService;
	
	 @Autowired
		private WebAccessTraceService webAccessTraceService;

	    @GetMapping("/users1")
	    public List<User> getUsersJson() {
    	
	        return userService.getUserList1();
	        
	    }
	    @GetMapping("/users2")
	    public List<UserDTO> getUsersJson1() {
    	
	        return userService.getUserList2();
	        
	    }
	    
//	    @GetMapping("/users1")
//	    public User getUsersJson() {
//	    	User item = userService.findUserById(1);
//	    	System.out.println("Username - "+item.getUsername());
//	        return item;
//	        
//	    }
	    
	    @GetMapping("/groups")
	    public List<Role> getgroups() {
	        return userService.getGroups();
	    }
	    
//	    @RequestMapping(value = { "/createuser" }, method = RequestMethod.POST, produces = {
//	            MediaType.APPLICATION_JSON_VALUE })
//	    @ResponseBody
//	    public ResponseEntity<?> createUser(@Valid @RequestBody UserForm userForm)
//	            throws ParseException, IllegalStateException, IOException {
//	    	System.out.println("Create user start");
//	        String sessionId = Long.toString(System.currentTimeMillis());
//	        _logger.info("[sessionId=" + sessionId + "]: UserController.createUser()-Start processing to save new user details");
//	        
//	        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
//	        User user = userService.findUserByUsername(auth.getName());
//	        WebAccessTrace logTrace = new WebAccessTrace();
//	        logTrace.setAction("createuser");
//	        logTrace.setDesc("submit data to create user");
//	        logTrace.setUser_id(user.getUserId());
//	        saveWebAccessTrace(logTrace);
//
//	        int isExist = userService.isUserExist(userForm.getUsername());
//	        Map<String, String> response = new HashMap<>();
//
//	        if (isExist == 0) {
//	            Date date = new Date();
//	            long time = date.getTime();
//	            Timestamp timestamp = new Timestamp(time);
//
//	            User userDetails = UserFormMapper.mapToForm(userForm, sessionId);
//	            userDetails.setCreatedByUserId(user.getUserId());
//	            userDetails.setCreatedDateTime(timestamp.toString());
//	            userDetails.setUpdatedDateTime(timestamp);
//
//	            userDetails = userService.saveUser(userDetails);
//
//	            if (userDetails.getUserId() != 0) {
//	                userService.createRoleMappingEntry(userDetails.getUserId(), userForm.getUserType());
//	                _logger.info("[sessionId=" + sessionId + "]: UserController.createUser()-save new user details successfully");
//	                response.put("status", "success");
//	                response.put("message", "User created successfully");
//	                return ResponseEntity.ok(response);
//	            } else {
//	                _logger.info("[sessionId=" + sessionId + "]: UserController.createUser()-save new user details failed");
//	                response.put("status", "error");
//	                response.put("message", "User creation failed");
//	                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
//	            }
//	        } else {
//	            _logger.info("[sessionId=" + sessionId + "]: UserController.createUser()-user details already exist");
//	            response.put("status", "error");
//	            response.put("message", "User details already exist");
//	            return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
//	        }
//	    }
	    
	    @RequestMapping(value = { "/createuser" }, method = RequestMethod.POST, produces = { MediaType.APPLICATION_JSON_VALUE })
	    @ResponseBody
	    public ResponseEntity<?> createUser(@Valid @RequestBody UserForm userForm)
	            throws ParseException, IllegalStateException, IOException {
	        System.out.println("Create user start"+userForm);
	        String sessionId = Long.toString(System.currentTimeMillis());
	        _logger.info("[sessionId=" + sessionId + "]: UserController.createUser()-Start processing to save new user details");
	        
	        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
	        User user = userService.findUserByUsername(auth.getName());
	        WebAccessTrace logTrace = new WebAccessTrace();
	        logTrace.setAction("createuser");
	        logTrace.setDesc("submit data to create user");
	        logTrace.setUser_id(user.getUserId());
	        saveWebAccessTrace(logTrace);

	        int isExist = userService.isUserExist(userForm.getUsername());
	        Map<String, String> response = new HashMap<>();

	        if (isExist == 0) {
	            Date date = new Date();
	            long time = date.getTime();
	            Timestamp timestamp = new Timestamp(time);

	            // ✅ Generate password dynamically using firstname from request body
	            String generatedPassword = userForm.getFirstName() + "#1";

	            User userDetails = UserFormMapper.mapToForm(userForm, sessionId);
	            userDetails.setEmailId(userForm.getEmailId());
	            userDetails.setContactNumber(userForm.getContactNumber());
	            userDetails.setCompanyName(userForm.getCompanyName());
	            userDetails.setCreatedByUserId(user.getUserId());
	            userDetails.setContactPerson(userForm.getContactPerson());
	            userDetails.setStatus(user.getStatus());
	            userDetails.setCreatedDateTime(timestamp.toString());
	            userDetails.setUpdatedDateTime(timestamp);
	            
	            // ✅ Set the generated password
	            userDetails.setPassword(generatedPassword);

	            userDetails = userService.saveUser(userDetails);

	            if (userDetails.getUserId() != 0) {
	                userService.createRoleMappingEntry(userDetails.getUserId(), userForm.getUserType());
	                _logger.info("[sessionId=" + sessionId + "]: UserController.createUser()-save new user details successfully");
	                response.put("status", "success");
	                response.put("message", "User created successfully");
	                return ResponseEntity.ok(response);
	            } else {
	                _logger.info("[sessionId=" + sessionId + "]: UserController.createUser()-save new user details failed");
	                response.put("status", "error");
	                response.put("message", "User creation failed");
	                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
	            }
	        } else {
	            _logger.info("[sessionId=" + sessionId + "]: UserController.createUser()-user details already exist");
	            response.put("status", "error");
	            response.put("message", "User details already exist");
	            return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
	        }
	    }

	    
	    public void saveWebAccessTrace(WebAccessTrace logTrace) {
			try {
				webAccessTraceService.saveWebAccessTrace(logTrace);
			} catch (Exception e) {
				_logger.error("Exception occurs while inserting WebAccessTrace" + e);
			}
		}
	    
	    @RequestMapping(value = { "/updateuser1" }, method = RequestMethod.PUT, produces = {
	            MediaType.APPLICATION_JSON_VALUE })
	    @ResponseBody
	    public ResponseEntity<?> updateUser(@Valid @RequestBody UserForm userForm)
	            throws ParseException, IllegalStateException, IOException {
	        String sessionId = Long.toString(System.currentTimeMillis());
	        _logger.info("[sessionId=" + sessionId + "]: UserController.updateUser()-Start processing to update user details");

	        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
	        User user = userService.findUserByUsername(auth.getName());
	        WebAccessTrace logTrace = new WebAccessTrace();
	        logTrace.setAction("updateuser");
	        logTrace.setDesc("submit data to update user");
	        logTrace.setUser_id(user.getUserId());
	        saveWebAccessTrace(logTrace);

	        User existingUser = userService.findUserByEmail(userForm.getEmailId());
	        Map<String, String> response = new HashMap<>();

	        if (existingUser != null) {
	            existingUser.setFirstName(userForm.getFirstName());
	            existingUser.setLastName(userForm.getLastName());
	            existingUser.setUsername(userForm.getUsername());
	            existingUser.setContactNumber(userForm.getContactNumber());
	            existingUser.setEmailId(userForm.getEmailId());
	            existingUser.setContactPerson(userForm.getContactPerson());
	            existingUser.setStatus(userForm.getStatus());
	            existingUser.setCompanyName(userForm.getCompanyName());
	            existingUser.setUpdatedDateTime(new Timestamp(System.currentTimeMillis()));

	            existingUser = userService.saveUser(existingUser);

	            if (existingUser.getUserId() != 0) {
	                _logger.info("[sessionId=" + sessionId + "]: UserController.updateUser()-User updated successfully");
	                response.put("status", "success");
	                response.put("message", "User updated successfully");
	                return ResponseEntity.ok(response);
	            } else {
	                _logger.info("[sessionId=" + sessionId + "]: UserController.updateUser()-User update failed");
	                response.put("status", "error");
	                response.put("message", "User update failed");
	                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
	            }
	        } else {
	            _logger.info("[sessionId=" + sessionId + "]: UserController.updateUser()-User not found");
	            response.put("status", "error");
	            response.put("message", "User not found");
	            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
	        }
	    }
	    
	    @RequestMapping(value = { "/deactivateuser" }, method = RequestMethod.PUT, produces = {
	            MediaType.APPLICATION_JSON_VALUE })
	    @ResponseBody
	    public ResponseEntity<?> deactivateUser(@RequestParam("userId") int userId) {
	        String sessionId = Long.toString(System.currentTimeMillis());
	        _logger.info("[sessionId=" + sessionId + "]: UserController.deactivateUser()-Start processing to deactivate user");

	        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
	        User user = userService.findUserByUsername(auth.getName());
	        WebAccessTrace logTrace = new WebAccessTrace();
	        logTrace.setAction("deactivateuser");
	        logTrace.setDesc("submit data to deactivate user");
	        logTrace.setUser_id(user.getUserId());
	        saveWebAccessTrace(logTrace);

	        boolean isDeactivated = userService.deactivateUser(userId);
	        Map<String, String> response = new HashMap<>();

	        if (isDeactivated) {
	            _logger.info("[sessionId=" + sessionId + "]: UserController.deactivateUser()-User deactivated successfully");
	            response.put("status", "success");
	            response.put("message", "User deactivated successfully");
	            return ResponseEntity.ok(response);
	        } else {
	            _logger.info("[sessionId=" + sessionId + "]: UserController.deactivateUser()-User not found");
	            response.put("status", "error");
	            response.put("message", "User not found");
	            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
	        }
	    }
	    @RequestMapping(value = { "/deactivateuser2" }, method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
	    @ResponseBody
	    public ResponseEntity<?> deactivateUser2(@RequestBody Map<String, Integer> requestBody) {
	        String sessionId = Long.toString(System.currentTimeMillis());
	        _logger.info("[sessionId=" + sessionId + "]: UserController.deactivateUser()-Start processing to deactivate user");

	        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
	        User user = userService.findUserByUsername(auth.getName());
	        WebAccessTrace logTrace = new WebAccessTrace();
	        logTrace.setAction("deactivateuser");
	        logTrace.setDesc("submit data to deactivate user");
	        logTrace.setUser_id(user.getUserId());
	        saveWebAccessTrace(logTrace);

	        Integer userId = requestBody.get("userId");  // Extract userId from request body
	        boolean isDeactivated = userService.deactivateUser(userId);
	        Map<String, String> response = new HashMap<>();

	        if (isDeactivated) {
	            _logger.info("[sessionId=" + sessionId + "]: UserController.deactivateUser()-User deactivated successfully");
	            response.put("status", "success");
	            response.put("message", "User deactivated successfully");
	            return ResponseEntity.ok(response);
	        } else {
	            _logger.info("[sessionId=" + sessionId + "]: UserController.deactivateUser()-User not found");
	            response.put("status", "error");
	            response.put("message", "User not found");
	            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
	        }
	    }

 @RequestMapping(value = { "/deactivateuser1" }, method = RequestMethod.PUT, 
                consumes = MediaType.APPLICATION_JSON_VALUE, 
                produces = MediaType.APPLICATION_JSON_VALUE)
 @ResponseBody
 public ResponseEntity<?> deactivateUser(@RequestBody Map<String, String> requestBody) {
    String sessionId = Long.toString(System.currentTimeMillis());
    _logger.info("[sessionId=" + sessionId + "]: UserController.deactivateUser1()-Start processing to deactivate user");

    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    User authenticatedUser = userService.findUserByUsername(auth.getName());

    WebAccessTrace logTrace = new WebAccessTrace();
    logTrace.setAction("deactivateuser");
    logTrace.setDesc("submit data to deactivate user");
    logTrace.setUser_id(authenticatedUser.getUserId());
    saveWebAccessTrace(logTrace);

    // Extract email from request body
    String email = requestBody.get("email");
    
    if (email == null || email.isEmpty()) {
        _logger.error("[sessionId=" + sessionId + "]: UserController.deactivateUser1()- Email is missing in request body");
        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put("status", "error");
        errorResponse.put("message", "Email is required in the request body");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

   // boolean isDeactivated = userService.deactivateUserByEmail(email);
    boolean isDeactivated = userService.deactivateUserByEmail1(email);

    
    Map<String, String> response = new HashMap<>();

    if (isDeactivated) {
        _logger.info("[sessionId=" + sessionId + "]: UserController.deactivateUser1()-User deactivated successfully  "+email);
        response.put("status", "success");
        response.put("message", "User deactivated successfully");
        return ResponseEntity.ok(response);
    } else {
        _logger.info("[sessionId=" + sessionId + "]: UserController.deactivateUser1()-User not found  "+email);
        response.put("status", "error");
        response.put("message", "User not found");
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }
 }
 
// @RequestMapping(value = "/reactivateuser", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
// @ResponseBody
// public ResponseEntity<?> reactivateUser(@RequestBody Map<String, Object> requestBody) {
//     String emailId = (String) requestBody.get("emailId");
//     Logger logger = LoggerFactory.getLogger(this.getClass());
//     Integer roleId = ((Number) requestBody.get("userType")).intValue(); 
//     
//     logger.info( emailId, roleId);
//     boolean isReactivated = userService.reactivateUserAndAssignRole(emailId, roleId);
//     Map<String, String> response = new HashMap<>();
//
//     if (isReactivated) {
//         response.put("status", "success");
//         response.put("message", "User reactivated and role assigned successfully");
//         return ResponseEntity.ok(response);
//     } else {
//         response.put("status", "error");
//         response.put("message", "User not found");
//         return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
//     }
// }
 
 @RequestMapping(value = "/reactivateuser", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
 @ResponseBody
 public ResponseEntity<?> reactivateUser(@RequestBody Map<String, Object> requestBody) {
     Logger logger = LoggerFactory.getLogger(this.getClass());

     try {
         // Extract emailId
         String emailId = (String) requestBody.get("emailId");

         // Extract and convert userType (supports Number or String)
         Object userTypeObj = requestBody.get("userType");
         int roleId;

         if (userTypeObj instanceof Number) {
             roleId = ((Number) userTypeObj).intValue();
         } else if (userTypeObj instanceof String) {
             try {
                 roleId = Integer.parseInt((String) userTypeObj);
             } catch (NumberFormatException e) {
                 logger.error("Invalid userType format: not a number string.");
                 return ResponseEntity.badRequest().body(Map.of(
                         "status", "error",
                         "message", "Invalid userType format: must be numeric string or number."
                 ));
             }
         } else {
             logger.error("Invalid userType type received: {}", userTypeObj.getClass().getName());
             return ResponseEntity.badRequest().body(Map.of(
                     "status", "error",
                     "message", "Invalid userType type. Must be number or numeric string."
             ));
         }

         // Log values
         logger.info("Received request - emailId: {}, roleId: {}", emailId, roleId);

         // Business logic
         boolean isReactivated = userService.reactivateUserAndAssignRole(emailId, roleId);

         Map<String, String> response = new HashMap<>();
         if (isReactivated) {
             response.put("status", "success");
             response.put("message", "User reactivated and role assigned successfully");
             return ResponseEntity.ok(response);
         } else {
             response.put("status", "error");
             response.put("message", "User not found");
             return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
         }

     } catch (Exception ex) {
         logger.error("Exception in reactivateUser: ", ex);
         return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                 "status", "error",
                 "message", "Unexpected error occurred"
         ));
     }
 }

 
 

// @RequestMapping(value = "/reactivateuser", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
// @ResponseBody
// public ResponseEntity<?> reactivateUser(@RequestBody Map<String, Object> requestBody) {
//     Logger logger = LoggerFactory.getLogger(this.getClass()); // Initialize logger
//
//     String sessionId = Long.toString(System.currentTimeMillis()); // Optional: session-based tracking
//
//     logger.info("[sessionId={}]: Start processing reactivateUser API", sessionId);
//
//     String emailId = (String) requestBody.get("emailId");
//    int roleId;
//
//     try {
//         roleId = ((Integer) requestBody.get("userType")).intValue();
//     } catch (Exception e) {
//         logger.error("[sessionId={}]: Invalid roleId format received in requestBody", sessionId, e);
//         Map<String, String> errorResponse = new HashMap<>();
//         errorResponse.put("status", "error");
//         errorResponse.put("message", "Invalid userType format");
//         return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
//     }
//
//     logger.info("[sessionId={}]: Reactivating user with emailId={} and assigning roleId={}", sessionId, emailId, roleId);
//
//     boolean isReactivated = userService.reactivateUserAndAssignRole(emailId, roleId);
//     Map<String, String> response = new HashMap<>();
//
//     if (isReactivated) {
//         logger.info("[sessionId={}]: User reactivated and role assigned successfully", sessionId);
//         response.put("status", "success");
//         response.put("message", "User reactivated and role assigned successfully");
//         return ResponseEntity.ok(response);
//     } else {
//         logger.warn("[sessionId={}]: User with emailId={} not found", sessionId, emailId);
//         response.put("status", "error");
//         response.put("message", "User not found");
//         return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
//     }
// }
 

}


