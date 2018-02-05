package com.aspire.etl.layout;

import java.awt.Color;

public class TaskNode {
	
	
	public static final int TYPE_IS_FUNCTION = 0;  //函数 	
	public static final int TYPE_IS_LOADLOG  = 1;  //入库
	public static final int TYPE_IS_DATASYN  = 2;  //同步
	public static final int TYPE_IS_SYNONYM  = 3;  //同义词
	public static final int TYPE_IS_OTHER    = 4;  //其他（视图，或其他）
	public static final int TYPE_IS_TABLE    = 9;  //表格
	
	
	private String name;
	private int width = 40;
	private int heigth = 50;
	private int x;
	private int y;
	private Color color = null;
	
	//类型
	private int type; 
	
	public TaskNode(){
		
	}
	
	public TaskNode(String name,int width,int heigth){
		this.name = name;
		this.width = width;
		this.heigth = heigth;
	}
	
	public TaskNode(String name,int width,int heigth,Color color){
		this.name = name;
		this.width = width;
		this.heigth = heigth;
		this.color = color;
	}
	
	public TaskNode(String name,int width,int heigth,Color color,int type){
		this.name = name;
		this.width = width;
		this.heigth = heigth;
		this.color = color;
		this.type = type;
	}
	
	public TaskNode(String name){
		this.name = name;
	}
	
	public TaskNode(String name,int width){
		this.name = name;
		this.width = width;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}
	
	public String toString(){
		return this.name;
	}

	public int getHeigth()
	{
		return heigth;
	}

	public void setHeigth(int heigth)
	{
		this.heigth = heigth;
	}

	public int getX()
	{
		return x;
	}

	public int getY()
	{
		return y;
	}

	public void setX(int x)
	{
		this.x = x;
	}

	public void setY(int y)
	{
		this.y = y;
	}

	public Color getColor() {
		return color;
	}

	public void setColor(Color color) {
		this.color = color;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

}