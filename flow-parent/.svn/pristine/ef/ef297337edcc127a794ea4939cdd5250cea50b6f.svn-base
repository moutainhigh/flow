package com.aspire.etl.flowdefine;

import com.aspire.etl.flowdefine.TaskflowGroup;

public class TaskflowGroup implements Comparable{
	private Integer groupID;
	
	private String groupName;
	
	private int groupOrder;
	
	private String groupDesc;


	public TaskflowGroup() {
		super();
	}
	
	public TaskflowGroup(Integer groupID, String groupName, int groupOrder) {
		super();
		this.groupID = groupID;
		this.groupName = groupName;
		this.groupOrder = groupOrder;
	}
	
	public int compareTo(Object o) {
		TaskflowGroup other = (TaskflowGroup)o;
		if(groupOrder < other.groupOrder)
			return -1;
		if(groupOrder > other.groupOrder)
			return 1;
		return 0;
	}

	public Integer getGroupID() {
		return groupID;
	}

	public void setGroupID(Integer groupID) {
		this.groupID = groupID;
	}

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public int getGroupOrder() {
		return groupOrder;
	}

	public void setGroupOrder(int groupOrder) {
		this.groupOrder = groupOrder;
	}
	

	public String getGroupDesc() {
		return groupDesc;
	}

	public void setGroupDesc(String groupDesc) {
		this.groupDesc = groupDesc;
	}
	
	public String toString() {
		return groupName;
	}
}
