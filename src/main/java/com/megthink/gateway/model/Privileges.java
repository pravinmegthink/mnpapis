package com.megthink.gateway.model;

import jakarta.persistence.*;

@Entity
@Table(name = "privileges")
public class Privileges {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "privilege_id")
	private int privilegeId;

	@Column(name = "privilege_name")
	private String privilegeName;

//	@OneToMany(mappedBy = "role")
//	private Set<RolePrivileges> rolePriviliges;

	public int getPrivilegeId() {
		return privilegeId;
	}

	public void setPrivilegeId(int privilegeId) {
		this.privilegeId = privilegeId;
	}

	public String getPrivilegeName() {
		return privilegeName;
	}

	public void setPrivilegeName(String privilegeName) {
		this.privilegeName = privilegeName;
	}

//	public Set<RolePrivileges> getRolePriviliges() {
//		return rolePriviliges;
//	}
//
//	public void setRolePriviliges(Set<RolePrivileges> rolePriviliges) {
//		this.rolePriviliges = rolePriviliges;
//	}

}
