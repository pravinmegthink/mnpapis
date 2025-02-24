package com.megthink.gateway.model;

import java.sql.Timestamp;
import jakarta.persistence.*;

@Entity
@Table(name = "role_privileges")
public class RolePrivileges {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "role_privilege_id")
	private int rolePrivilegeId;

//	@ManyToOne
//	@JoinColumn(name = "role_id")
//	private Role role;

//	@ManyToOne
//	@JoinColumn(name = "privilege_id")
//	private Privileges privilege;

	// @CreationTimestamp
	@Column(name = "created_date_time")
	private Timestamp createdDateTime;

	// @UpdateTimestamp
	@Column(name = "updated_date_time")
	private Timestamp updatedDateTime;

	public int getRolePrivilegeId() {
		return rolePrivilegeId;
	}

	public void setRolePrivilegeId(int rolePrivilegeId) {
		this.rolePrivilegeId = rolePrivilegeId;
	}

//	public Role getRole() {
//		return role;
//	}
//
//	public void setRole(Role role) {
//		this.role = role;
//	}

//	public Privileges getPrivilege() {
//		return privilege;
//	}
//
//	public void setPrivilege(Privileges privilege) {
//		this.privilege = privilege;
//	}

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
}