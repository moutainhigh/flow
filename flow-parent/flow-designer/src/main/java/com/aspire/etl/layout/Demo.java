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
		//���xy��������Ϣ��
		for(TaskNode node : nodeList){
			System.out.println( node.getName() + ": x=" + node.getX() + ",y=" + node.getY() );
		}
		
		//��������ͼ��
		test.createFlowMap(nodeList, linkList);
		
	}
	
	public void createFlowMap(List<TaskNode> nodeList,List<NodeLink> linkList){
		System.setProperty("sun.java2d.d3d", "false");
		GraphModel model = new DefaultGraphModel();
		JGraph graph = new JGraph(model);
//		 ��ֹ�����϶����ߵ�port
		graph.setDisconnectable(false);
		// ��ʾ������
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
//		������TaskNode���뵽grphLayout��
		graph.getGraphLayoutCache().insert(cells);
		
		//���ߣ�
		for(NodeLink link : linkList){
			
			//from �� to ����cells�е����꣺
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
		frame.setTitle("���̽ڵ��Զ�����");
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
	 * ��ȡ�ڵ�ǰ����node�е�Ԫ���±�λ,���ڻ���ʱ��ȡfrom��to������nodeOrderList�е�Ԫ���±�λ
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

		// �ýڵ�ͼ���Զ���С���ѱ�����ʾ����label��
//		 GraphConstants.setAutoSize(cell.getAttributes(), true);
		 GraphConstants.setResize(cell.getAttributes(), true);

		// ��ֹgraph��ȱʡ��cell�༭��
		GraphConstants.setEditable(cell.getAttributes(), false);
		// Add a Floating Port

		
		// Add a Floating Port
		cell.addPort();

		return cell;
	}
	
	/**
	 * ��ȡ���нڵ��б�
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
	 * ��ȡ�ڵ�Link����
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
