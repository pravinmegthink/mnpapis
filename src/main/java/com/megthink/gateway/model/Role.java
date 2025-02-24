package com.megthink.gateway.model;

import jakarta.persistence.*;

@Entity
@Table(name = "role")
public class Role {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "role_id")
	private int roleId;

	@Column(name = "role_name")
	private String roleName;

	@Column(name = "role_description")
	private String roleDescription;

	@Column(name = "user_id")
	private int userId;

//	@OneToMany(mappedBy = "user")
//	private Set<UserRole> userRole;

//	@OneToMany(mappedBy = "privilege")
//	private Set<RolePrivileges> rolePriviliges;

	@Column(name = "created_date_time")
	private String createdDateTime;

	@Column(name = "updated_date_time")
	private String updatedDateTime;

	@Transient
	private String privilegesList;

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

	public String getRoleDescription() {
		return roleDescription;
	}

	public void setRoleDescription(String roleDescription) {
		this.roleDescription = roleDescription;
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

//	 public Set<UserRole> getUserRole() {
//	 return userRole;
//	 }
//	
//	 public void setUserRole(Set<UserRole> userRole) {
//	 this.userRole = userRole;
//	 }

//	public Set<RolePrivileges> getRolePriviliges() {
//		return rolePriviliges;
//	}
//
//	public void setRolePriviliges(Set<RolePrivileges> rolePriviliges) {
//		this.rolePriviliges = rolePriviliges;
//	}

	public String getCreatedDateTime() {
		return createdDateTime;
	}

	public void setCreatedDateTime(String createdDateTime) {
		this.createdDateTime = createdDateTime;
	}

	public String getUpdatedDateTime() {
		return updatedDateTime;
	}

	public void setUpdatedDateTime(String updatedDateTime) {
		this.updatedDateTime = updatedDateTime;
	}

	public String getPrivilegesList() {
		return privilegesList;
	}

	public void setPrivilegesList(String privilegesList) {
		this.privilegesList = privilegesList;
	}
}