package com.aspire.etl.flowdefine;

public class Role {
	private Integer ID;
	
	private String name;
	
	private String description;

	public Role(Integer id, String name, String description) {
		super();
		this.ID = id;
		this.name = name;
		this.description = description;
	}

	public Role() {
		super();
		// TODO Auto-generated constructor stub
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Integer getID() {
		return ID;
	}

	public void setID(Integer id) {
		ID = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
