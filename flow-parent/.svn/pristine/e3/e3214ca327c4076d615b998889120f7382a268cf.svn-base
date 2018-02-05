package com.aspire.etl.flowdefine;

import com.aspire.etl.flowdefine.Note;
import com.aspire.etl.tool.Utils;

public class Note implements Cloneable {
	
	private Integer noteID;
	
	private Integer taskflowID;
	
	private String value;
	
	private int xPos;
	
	private int yPos;
	
	private int width;
	
	private int height;
	
	
	public Note(){}
	
	public Note(Integer noteID, Integer taskflowID, String value, int xPos, int yPos, int width, int height){
		this.noteID = noteID;
		this.taskflowID = taskflowID;
		this.value = value;
		this.xPos = xPos;
		this.yPos = yPos;
		this.width = width;
		this.height = height;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public Integer getNoteID() {
		return noteID;
	}

	public void setNoteID(Integer noteID) {
		this.noteID = noteID;
	}

	public Integer getTaskflowID() {
		return taskflowID;
	}

	public void setTaskflowID(Integer taskflowID) {
		this.taskflowID = taskflowID;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getXPos() {
		return xPos;
	}

	public void setXPos(int pos) {
		xPos = pos;
	}

	public int getYPos() {
		return yPos;
	}

	public void setYPos(int pos) {
		yPos = pos;
	}	
	
	public String toString(){
	    return value;
	}
	
	public Object clone() {   
        Note o = null;   
        try {   
            o = (Note) super.clone();
            o.setNoteID(Utils.getRandomIntValue());
        } catch (CloneNotSupportedException e) {   
            e.printStackTrace();   
        }   
        return o;   
    }
}
