package com.megthink.gateway.model;

import java.sql.Timestamp;


public class UserDTO {
	private int userId;
    private String firstName;
    private String lastName;
    private String username;
    private String password;
    private String emailId;
    private String contactNumber;
    private String companyName;
    private int status;
    private int createdByUserId;
    private String opId;
    private Timestamp createdDateTime;
    private Timestamp updatedDateTime;
    private int roleId;
    private String roleName;


   
	public int getUserId() {
		return userId;
	}



	public void setUserId(int userId) {
		this.userId = userId;
	}



	public String getFirstName() {
		return firstName;
	}



	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}



	public String getLastName() {
		return lastName;
	}



	public void setLastName(String lastName) {
		this.lastName = lastName;
	}



	public String getUsername() {
		return username;
	}



	public void setUsername(String username) {
		this.username = username;
	}



	public String getPassword() {
		return password;
	}



	public void setPassword(String password) {
		this.password = password;
	}



	public String getEmailId() {
		return emailId;
	}



	public void setEmailId(String emailId) {
		this.emailId = emailId;
	}



	public String getContactNumber() {
		return contactNumber;
	}



	public void setContactNumber(String contactNumber) {
		this.contactNumber = contactNumber;
	}



	public String getCompanyName() {
		return companyName;
	}



	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}



	public int getStatus() {
		return status;
	}



	public void setStatus(int status) {
		this.status = status;
	}



	public int getCreatedByUserId() {
		return createdByUserId;
	}



	public void setCreatedByUserId(int createdByUserId) {
		this.createdByUserId = createdByUserId;
	}



	public String getOpId() {
		return opId;
	}



	public void setOpId(String opId) {
		this.opId = opId;
	}



	public Timestamp getCreatedDateTime() {
		return createdDateTime;
	}



	public void setCreatedDateTime(Timestamp createdDateTime) {
		this.createdDateTime = createdDateTime;
	}



	public Timestamp getUpdatedDateTime() {
		return updatedDateTime;
	}



	public void setUpdatedDateTime(Timestamp updatedDateTime) {
		this.updatedDateTime = updatedDateTime;
	}



	public int getRoleId() {
		return roleId;
	}



	public void setRoleId(int roleId) {
		this.roleId = roleId;
	}



	public String getRoleName() {
		return roleName;
	}



	public void setRoleName(String roleName) {
		this.roleName = roleName;
	}



	// Constructor
	public UserDTO(int userId, String firstName, String lastName, String username, String password, 
            String emailId, String contactNumber, String companyName, int status, 
            int createdByUserId, String opId, Timestamp createdDateTime, Timestamp updatedDateTime, 
            int roleId, String roleName) {
 this.userId = userId;
 this.firstName = firstName;
 this.lastName = lastName;
 this.username = username;
 this.password = password;
 this.emailId = emailId;
 this.contactNumber = contactNumber;
 this.companyName = companyName;
 this.status = status;
 this.createdByUserId = createdByUserId;
 this.opId = opId;
 this.createdDateTime = createdDateTime;
 this.updatedDateTime = updatedDateTime;
 this.roleId = roleId;
 this.roleName = roleName;
}

    
}