package com.aspire.etl.layout;

import java.awt.Color;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JScrollPane;

import org.jgraph.JGraph;
import org.jgraph.graph.DefaultEdge;
import org.jgraph.graph.DefaultGraphCell;
import org.jgraph.graph.DefaultGraphModel;
import org.jgraph.graph.GraphConstants;
import org.jgraph.graph.GraphModel;


public class Demo
{
	public static void main(String[] args) {
		 
		HorizontalLayout layout = new HorizontalLayout();
		Demo test = new Demo();
		
		List<TaskNode> nodeList = test.getTaskNodeList();
		List<NodeLink> linkList = test.getNodeLinkList();
		
		layout.setLayout(nodeList,linkList);
		
		System.err.println("nodeList " + nodeList.size());
		//输出xy的坐标信息：
		for(TaskNode node : nodeList){
			System.out.println( node.getName() + ": x=" + node.getX() + ",y=" + node.getY() );
		}
		
		//创建流程图：
		test.createFlowMap(nodeList, linkList);
		
	}
	
	public void createFlowMap(List<TaskNode> nodeList,List<NodeLink> linkList){
		System.setProperty("sun.java2d.d3d", "false");
		GraphModel model = new DefaultGraphModel();
		JGraph graph = new JGraph(model);
//		 阻止单独拖动连线的port
		graph.setDisconnectable(false);
		// 显示网格线
		graph.setGridEnabled(true);
		graph.setGridVisible(true);
		graph.setPortsVisible(true);
		graph.setHandleColor(Color.RED);
		int idx = 0;
		
		DefaultGraphCell[] cells = new DefaultGraphCell[nodeList.size()];
		
		for(int j =0 ; j < nodeList.size(); j++){
			
			TaskNode node = (TaskNode)nodeList.get(j);
			
			cells[idx] = createVertex(node.getName()
								, node.getX()
								, node.getY()
								, node.getWidth(), node.getHeigth(), null, false);
			idx ++;
			
		}
//		将所有TaskNode插入到grphLayout中
		graph.getGraphLayoutCache().insert(cells);
		
		//画线：
		for(NodeLink link : linkList){
			
			//from 和 to 所在cells中的坐标：
			int from = getCellIndex(nodeList, getTaskNode(nodeList,link.getFrom()));
			int to = getCellIndex(nodeList, getTaskNode(nodeList,link.getTo()));
			DefaultGraphCell[] cells2 = new DefaultGraphCell[1];
			
			// Create Edge
			DefaultEdge edge = new DefaultEdge();
			
			// Fetch the ports from the new vertices, and connect them with the edge
			edge.setSource(cells[from].getChildAt(0));
			edge.setTarget(cells[to].getChildAt(0));
			cells2[0] = edge;
			
			//Set Arrow Style for edge
			int arrow = GraphConstants.ARROW_CLASSIC;
			GraphConstants.setLineEnd(edge.getAttributes(), arrow);
			GraphConstants.setEndFill(edge.getAttributes(), true);
			graph.getGraphLayoutCache().insert(cells2);
		
		}
		
		
		// Show in Frame
		JFrame frame = new JFrame();
		frame.setTitle("流程节点自动布局");
		frame.getContentPane().add(new JScrollPane(graph));
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.pack();
		frame.setVisible(true);
		
	}
	
	private TaskNode getTaskNode(List<TaskNode> nodeList, String nodeName){
		for(TaskNode node : nodeList){
			if (node.getName().equals(nodeName)){
				return node;
			}
		}
		
		return null;
	}
	
	/**
	 * 获取在当前所有node中的元素下标位,用于画线时获取from和to对象在nodeOrderList中的元素下标位
	 * @param nodeOrderList
	 * @param node
	 * @return
	 */
	private int getCellIndex(List<TaskNode> nodeList,TaskNode node){
		int index = 0 ;
		for(int j = 0 ; j < nodeList.size(); j++){
			if (((TaskNode)nodeList.get(j)).getName().equals(node.getName())){
				return index;
			}
			index ++;
		}
			
		return index;
	}

	private static DefaultGraphCell createVertex(String name, double x,
			double y, double w, double h, Color bg, boolean raised) {

		// Create vertex with the given name
		DefaultGraphCell cell = new DefaultGraphCell(name);

		// Set bounds
		GraphConstants.setBounds(cell.getAttributes(), new Rectangle2D.Double(
				x, y, w, h));

		// Set fill color
		if (bg != null) {
			GraphConstants.setGradientColor(cell.getAttributes(), bg);
			GraphConstants.setOpaque(cell.getAttributes(), true);
		}

		// Set raised border
		if (raised)
			GraphConstants.setBorder(cell.getAttributes(), BorderFactory
					.createRaisedBevelBorder());
		else
			// Set black border
			GraphConstants.setBorderColor(cell.getAttributes(), Color.black);

		
		GraphConstants.setOpaque(cell.getAttributes(), false);

		GraphConstants.setSizeable(cell.getAttributes(), true);

		// 让节点图标自动大小，已便于显示长的label。
//		 GraphConstants.setAutoSize(cell.getAttributes(), true);
		 GraphConstants.setResize(cell.getAttributes(), true);

		// 阻止graph打开缺省的cell编辑框
		GraphConstants.setEditable(cell.getAttributes(), false);
		// Add a Floating Port

		
		// Add a Floating Port
		cell.addPort();

		return cell;
	}
	
	/**
	 * 获取所有节点列表
	 * @return
	 */
	private List<TaskNode> getTaskNodeList(){
		ArrayList<TaskNode> list = new ArrayList<TaskNode>();
		
	/*	list.add(new TaskNode("A",70));
		list.add(new TaskNode("B"));
		list.add(new TaskNode("C",100));
		list.add(new TaskNode("D",60));
		list.add(new TaskNode("E",80));
		list.add(new TaskNode("F"));
		list.add(new TaskNode("G"));
		list.add(new TaskNode("H"));
		list.add(new TaskNode("I"));
		list.add(new TaskNode("J"));
		list.add(new TaskNode("K"));
		*/
/*
		list.add(new TaskNode("A"));
		list.add(new TaskNode("B"));
		list.add(new TaskNode("C"));
		list.add(new TaskNode("D"));
		list.add(new TaskNode("E"));
		list.add(new TaskNode("F"));
		list.add(new TaskNode("G"));
		list.add(new TaskNode("C1"));
		list.add(new TaskNode("E1"));
		list.add(new TaskNode("G1"));
		list.add(new TaskNode("H"));
		list.add(new TaskNode("1E"));
		list.add(new TaskNode("1F"));
		list.add(new TaskNode("1G"));
		list.add(new TaskNode("1H"));*/
		

		list.add(new TaskNode("A"));
		list.add(new TaskNode("B"));
		list.add(new TaskNode("C"));
		list.add(new TaskNode("D"));
		list.add(new TaskNode("E"));
		list.add(new TaskNode("F"));
		list.add(new TaskNode("G"));

		return list;
	}
	
	/**
	 * 获取节点Link属性
	 * @return
	 */
	private List<NodeLink> getNodeLinkList(){
		ArrayList<NodeLink> list = new ArrayList<NodeLink>();
	    //list.add(new NodeLink("B","E"));
	    //list.add(new NodeLink("G","K"));
		/*list.add(new NodeLink("A","B"));
		list.add(new NodeLink("B","C"));
		list.add(new NodeLink("C","C1"));
		list.add(new NodeLink("A","D"));
		list.add(new NodeLink("D","E"));
		list.add(new NodeLink("E","E1"));
		list.add(new NodeLink("A","F"));
		list.add(new NodeLink("F","G"));
		list.add(new NodeLink("G","G1"));*/
		
		/*list.add(new NodeLink("A","C"));
		list.add(new NodeLink("B","C"));
		list.add(new NodeLink("C","E"));
		list.add(new NodeLink("C","D"));
		list.add(new NodeLink("B","D"));*/
		
		list.add(new NodeLink("A","E"));
		list.add(new NodeLink("A","B"));
		list.add(new NodeLink("B","C"));
		list.add(new NodeLink("B","D"));
		list.add(new NodeLink("C","E"));
		list.add(new NodeLink("C","F"));
		list.add(new NodeLink("E","G"));
		//list.add(new NodeLink("F","H"));
		
		return list;
	}
	
}
