package com.aspire.etl.flowdefine;

public class UserRole {
	private String userID;
	
	private Integer roleID;

	public Integer getRoleID() {
		return roleID;
	}

	public void setRoleID(Integer roleID) {
		this.roleID = roleID;
	}

	public String getUserID() {
		return userID;
	}

	public void setUserID(String userID) {
		this.userID = userID;
	}

	public UserRole(String userID, Integer roleID) {
		super();
		this.userID = userID;
		this.roleID = roleID;
	}

	public UserRole() {
		super();
		// TODO Auto-generated constructor stub
	}

}
