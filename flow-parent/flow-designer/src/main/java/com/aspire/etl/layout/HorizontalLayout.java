package com.aspire.etl.layout;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;


/**
 * ˮƽ�����β���
 *
 */
public class HorizontalLayout {
	
	private static Logger logger = Logger.getLogger(HorizontalLayout.class);
	
	private boolean isListEnd ;
	private List nodeOrderList ; //�������
	private List<TaskNode> subNodeList;
	private List<TaskNode> currentList;
	private List<TaskNode> currentSubList;
	private HashMap tempNodeMap; 
	private HashMap tempNodeMap2; 
	
	private  List<TaskNode> taskNodeList;
	
	private  List<NodeLink> nodeLinkList;
	
	//ͼԪ���X����
	private int taskNodeXInterval = 100;
	
	//ͼԪ���Y����
	private int taskNodeYInterval = 30;
	
	//ÿ��ͼԪ�ĸ߶�
	private int taskNodeHeight = 80;
	
	public HorizontalLayout(){
		
	}
	
	/**
	 * ��ʼ��
	 *
	 */
	private void init(){
		isListEnd = false;
		nodeOrderList = new ArrayList(); //�������
		subNodeList = null;
		currentList = null;
		currentSubList = null;
		tempNodeMap = null; 
		tempNodeMap2 = null; 
		
		taskNodeList = null;
		nodeLinkList = null;
	}
	/**
	 * ����taskNodeList��ÿ���ڵ���ڲ����е�xy����ֵ,<BR>
	 * ����˵�� taskNodeList:����ڵ��б�nodeLinkList:�ڵ��Link��Ϣ�б�
	 * @param taskNodeList  --����ڵ��б�
	 * @param nodeLinkList  --����ڵ��Link��Ϣ�б�
	 */
	public void setLayout(List<TaskNode> taskNodeList, List<NodeLink> nodeLinkList){
		try {
			
			init();

			//����ظ��Ľڵ�
			List<TaskNode> list = deRepeateNode(taskNodeList);
			taskNodeList.clear();
			taskNodeList.addAll(list);

			this.taskNodeList = taskNodeList; 
			this.nodeLinkList = nodeLinkList;
			

			/**======0.�õ����ڵ�=====*/
			List<TaskNode> rootlist  = getRootTaskNodeList();

			//����������м���Ԫ��
			nodeOrderList.add(rootlist.toArray());

			logger.debug("�õ����ڵ㣺" + this.getString(rootlist));

			currentList = rootlist;
			int index = 1;

			//�ݹ���ڵ�������¼��ڵ㣺
			while(!isListEnd){

				boolean noSubNode = true;
				tempNodeMap = new HashMap();
				tempNodeMap2 = new HashMap();
				subNodeList = new ArrayList<TaskNode>();
				currentSubList = new ArrayList<TaskNode>();

				/**======1.����õ���ǰ�������ӽڵ�=====*/
				for(TaskNode node : currentList){

					logger.debug(index + ": 1xxxxxxxx" + node); 
					subNodeList = getAllSubNode(node);
					if (subNodeList.size() > 0){//��������ӽڵ㣬��isEndNode = false;
						noSubNode = false;
					}

					//�������ӽڵ�put��HashMap�У�ֻ������ȥ�أ���A���ӽ�ΪCD��B���ӽڵ�ҲΪCD�����и�E�������������ӽڵ�Ӧ��ΪCDE��������
					for(TaskNode subNode : subNodeList){
						logger.debug(index + ": 2xxxxxxxx" + subNode); 
						if (subNode != null){
							tempNodeMap.put(subNode.getName(), subNode);
						}
					}

					logger.debug(index + ": 2xxxxxxxx size=" + tempNodeMap.size()); 
				}

				subNodeList = new ArrayList<TaskNode>();
				Iterator it = tempNodeMap.entrySet().iterator();

				//������õ����ӽڵ�add����ǰ�ӽڵ㼯���У��統ǰA�ڵ���ӽڵ�ΪB��C�ڵ㣬�ǲ������ɵ�,����һ��Ϊ��ǰ�����еĽڵ㣬clearNotLevelNode()���Ƕ������д���
				while(it.hasNext()){
					Map.Entry entry = (Map.Entry) it.next();
					subNodeList.add((TaskNode) entry.getValue());

					logger.debug(index + ": 3xxxxxxxx currentSubList.add " + (TaskNode) entry.getValue()); 
					currentSubList.add((TaskNode) entry.getValue());
				}

				logger.debug("�����ȡ���ӽڵ㣺" + getString(currentSubList));
				//����ǵ�ǰ�����еĽڵ�
				clearNotLevelNode(nodeOrderList,currentSubList);

				logger.debug(index + ": 3xxxxxxxx ��ǰ�ӽڵ��б� = " + getString(subNodeList));


				/**======2.�ҳ����¼������и��ڵ㣬������ڵ�ǰ�ӽڵ��б��У������ͬ�нڵ�Ķ�����======*/

				//1.�ҳ���ǰ�ӽڵ���¼��ڵ�
				tempNodeMap2 = new HashMap();
				ArrayList<TaskNode> sub2NodeList = new ArrayList<TaskNode>();

				for(TaskNode node : currentSubList){
					List<TaskNode> nodeList = getAllSubNode(node);
					for(TaskNode subNode : nodeList){
						logger.debug(index + ": 5 node = "  + node + " ,tempNodeMap2.add " + subNode);
						tempNodeMap2.put(subNode.getName(), subNode);
					}
				}

				Iterator it2 = tempNodeMap2.entrySet().iterator();
				while(it2.hasNext()){
					Map.Entry entry = (Map.Entry) it2.next();
					sub2NodeList.add((TaskNode) entry.getValue());
				}

				logger.debug(index + ": 5xxxxxxxx ���¼��ڵ� = " + getString(sub2NodeList));
				for(TaskNode node : sub2NodeList){//�������¼��ڵ�
					List<TaskNode> nextSub2ParentList = getAllParentNode(node);//�ҳ����¼��ڵ�ĸ��ڵ�
					for(TaskNode nextSub2Parent : nextSub2ParentList){

						logger.debug(index + ": 5xxxxxxxx subNode= "+ node
								+ ",parent= " + nextSub2Parent  + " isRootNode(nextSub2Parent) =" +
								isNoParentNode(nextSub2Parent)
								+  " ,add parent= " + nextSub2Parent 
								+ "  -->" + isAllParentInNodeOrderList(nodeOrderList,nextSub2Parent));

						if ((isNoParentNode(nextSub2Parent) || isAllParentInNodeOrderList(nodeOrderList,nextSub2Parent))
								&& !isNodeExists(currentSubList, nextSub2Parent) 
								&& !isNodeInAllExists(nodeOrderList, nextSub2Parent)){ //���û�г����ڵ�ǰ�ӽڵ��б�,���Ҳ�����֪�ķ����г��ֵĽڵ㣬����Ҫ����
							currentSubList.add(nextSub2Parent);
						}
					}
				}

				//����ǵ�ǰ�����еĽڵ�
				clearNotLevelNode(nodeOrderList,currentSubList);

				logger.debug(index + ": 999xxxxxxxx ��󣬵�ǰ�ӽڵ��б� = " + getString(currentSubList));

				currentList = currentSubList;

				//����������м���Ԫ��
				if(currentList.size() > 0){
					nodeOrderList.add(currentList.toArray());
				}

				isListEnd = noSubNode;
				index ++;
			}

			logger.debug("������е�����Ϊ��" + nodeOrderList.size());

//			�ı���Ԫ���е�����
			changeSort();

			//��������ڵ�
			clearAloneNode();

			//���ø��ڵ������
			setTaskNodeCoord();

		}catch(Exception e){
			e.printStackTrace();
		}
		
	}
	
	/**
	 * ��������ڵ�
	 *
	 */
	private void clearAloneNode() {
		
		//1.ͨ��Link��Ϣɾ�������ڵ�
		ArrayList list = new ArrayList();
		for(int i = 0 ; i < nodeOrderList.size(); i++){
			Object[] taskNodes = (Object[])nodeOrderList.get(i);
			list = new ArrayList();
			for(int j =0 ; j < taskNodes.length; j++){
				TaskNode node = (TaskNode)taskNodes[j];
				
				//����ڵ��Ƿ����Link��Ϣ
				if (isInLinkList(node)){
					list.add(node);
				}else{
					removeTaskNode(node);
				}
			}
			nodeOrderList.remove(i);
			nodeOrderList.add(i,list.toArray());
		}
		
		
	}
	
	/**
	 * ɾ���ظ���TaskNode
	 * @param taskNodeList
	 */
	private List<TaskNode> deRepeateNode(List<TaskNode> taskNodeList){
		ArrayList list = new ArrayList();
		HashMap map = new HashMap();
		//System.out.println("ȥ��ǰ" + taskNodeList.size() + " " + StringUtil.getString(taskNodeList) );
		for(TaskNode node :taskNodeList){
			map.put(node.getName(), node);
		}
		Iterator it = map.entrySet().iterator();
		while(it.hasNext()){
			list.add(((Map.Entry)it.next()).getValue());
		}
		
		//System.out.println("ȥ�غ�" + list.size()+ " " + StringUtil.getString(list) );
		return list;
	}
	
	/**
	 * ɾ������ڵ�
	 * @param node
	 * @return
	 */
	private void removeTaskNode(TaskNode node){
		for(int i =0 ; i < taskNodeList.size(); i++){
			TaskNode taskNode = (TaskNode)taskNodeList.get(i);
			if (taskNode.getName().equals(node.getName())){
				taskNodeList.remove(i);
				i--;
			}
		}
	}
	
	/**
	 * ����ڵ��Ƿ����Link��Ϣ
	 * @param node
	 * @return
	 */
	private boolean isInLinkList(TaskNode node){
		
		boolean isInLink = false;
		
		for(NodeLink link : nodeLinkList){
			if (link.getFrom().equals(node.getName()) || link.getTo().equals(node.getName())){
				isInLink = true;
				break;
			}
		}
		
		return isInLink;
	}
	

	/**
	 * ���ýڵ�����
	 *
	 */
	private void setTaskNodeCoord (){
		
		//�������нڵ������
		int idx = 0;
		int xStartPoint = 20;
		
		for(int i = 0 ; i < nodeOrderList.size(); i++){
			Object[] taskNodes = (Object[])nodeOrderList.get(i);
			int maxVertCount = getVertMaxCount(nodeOrderList);
			int totalHeigth = (taskNodeHeight * maxVertCount + taskNodeYInterval * maxVertCount); //�ܸ߶� = ����߶� * ����������� + ����ֱ��� * ����������� 
			int nodesHeigth = taskNodeHeight * taskNodes.length + taskNodeYInterval * (taskNodes.length -1);//ÿ�ж��󼯵��ܸ߶� = ����߶� * �ж������ + ����ֱ��� * ����������� 
			int yStartPoint = totalHeigth/2 - nodesHeigth/2; //�ܸ߶� = �ܸ߶� / 2 + ÿ�ж��󼯵��ܸ߶� / 2
			
			logger.debug("yStartPoint = " + yStartPoint);

			int xPoint = xStartPoint + (i == 0 ? 0 : (getMaxWidth((Object[])nodeOrderList.get(i - 1)) + taskNodeXInterval)) ;//x��Ϊ x�Ὺʼ�� + ����һ���ڵ�����е������ + ���ࣩ
			for(int j =0 ; j < taskNodes.length; j++){
				
				TaskNode node = (TaskNode)taskNodes[j];
				int xPoint2 = xPoint + (getMaxWidth(taskNodes)/2 - node.getWidth()/2); //x��ʼ�� + ������ȵ�һ�� - ��ǰ�ڵ��ȵ�һ�룩 
				int yPoint = yStartPoint + (node.getHeigth() + taskNodeYInterval) * j; //�ڶ����ڵ��y�� = y�Ὺʼ���� + ������߶� + ����ࣩ* ��ǰ�ڵ��������Ԫ���±꣨��0��ʼ��
				
				node.setX(xPoint2);
				node.setY(yPoint);
				
				idx ++;
				
			}
			
			xStartPoint = xStartPoint + (i == 0 ? 0: (getMaxWidth((Object[])nodeOrderList.get(i - 1)) +taskNodeXInterval));
		}


	}

	private static int getMaxWidth(Object[] taskNodes) {
		int maxWidth = 0;
		
		for(Object obj: taskNodes){
			TaskNode node = (TaskNode)obj;
			if (node.getWidth() > maxWidth){
				maxWidth = node.getWidth();
			}
		}
		
		return maxWidth;
	}

	private boolean isAllParentInNodeOrderList(List nodeOrderList, TaskNode nextSub2Parent) {

		boolean isExists = true;
		List<TaskNode> list = getAllParentNode(nextSub2Parent);
		
		
		logger.debug(nextSub2Parent + "====>" + getString(list));
		for(int j = 0 ; j < nodeOrderList.size(); j++){
			Object[] objects = (Object[]) nodeOrderList.get(j);
			for(Object obj:objects){
				logger.debug(obj);
			}
		}
		
		 for(TaskNode node : list){
			  if (!this.isNodeInAllExists(nodeOrderList,node)){ //ֻҪ��һ���ڵ㲻�ڵ�ǰ���ӽڵ��б��У��򷵻�false;
				  isExists = false;
			  }
		  }
		 
		 
		 return isExists;
	}


	/**
	 * ��ѯ�ڵ��Ƿ������ָ���Ľڵ��б�
	 * @param nodeList
	 * @param node
	 * @return
	 */
	private boolean isNodeExists(List<TaskNode> nodeList,TaskNode node){
		boolean isExists = false;
		for(TaskNode taskNode : nodeList){
			if (taskNode.getName().equals(node.getName())){
				return true;
			}
		}
			
		return isExists;
	}
	
	/**
	 * ��ѯһ��ڵ��б��еĽڵ�Ԫ�أ��Ƿ��������һ��ڵ��б��У�
	 * ��ڵ��б�1��A,B,C,�ڵ��б�2��CDE,�ڵ��б�2��C�����ڽڵ��б�1��
	 * @param nodeList
	 * @param currentList
	 * @return
	 */
	private boolean isNodeExists(List<TaskNode> nodeList,List<TaskNode> currentList){
		boolean isExists = false;
		
		 for(TaskNode node : nodeList){
			  if (isNodeExists(currentList, node)){
				  isExists = true;
			  }
		  }
		 return isExists;
	}
	
	/**
	 * �Ƿ������nodeOrderList�����нڵ㣩��
	 * @param nodeOrderList
	 * @param node
	 * @return
	 */
	private boolean isNodeInAllExists(List nodeOrderList,TaskNode node){
		boolean isExists = false;
		
		for(int j = 0 ; j < nodeOrderList.size(); j++){
			Object[] objects = (Object[]) nodeOrderList.get(j);
			for(Object obj : objects){
				if (((TaskNode)obj).getName().equals(node.getName())){
					return true;
				}
			}
		}
			
		return isExists;
	}
	
	/**
	 * ��ȡ��ֱ���Ķ������
	 * @return
	 */
	private int getVertMaxCount(List nodeOrderList){
		int max = 0 ;
		for(int j = 0 ; j < nodeOrderList.size(); j++){
			Object[] objects = (Object[]) nodeOrderList.get(j);
			if (objects.length > max){
				max = objects.length;	
			}
		}
		return max;
	}
	
	/**
	 * ��ȡ���и��ڵ�
	 * @return
	 */
	private List<TaskNode> getRootTaskNodeList()
	{
		List<TaskNode> rootList = new ArrayList<TaskNode>();
		List<TaskNode> taskNodeList = getTaskNodeList();
		for(TaskNode node :taskNodeList){
			if (isRootNode(node))
				rootList.add(node);
		}
		
		if(rootList.size() == 0){
			for(TaskNode node :taskNodeList){
				if (isNoParentNode(node))
					rootList.add(node);
			}
		}
		
		return rootList;
	}
	
	
	/**
	 ��ȡû�и��ڵ�Ľڵ�List
	*/
	private List<TaskNode> getNoParentNodeList(List<TaskNode> taskNodeList){
		List<TaskNode> nodeList = new ArrayList<TaskNode>();
		for(TaskNode node :taskNodeList){
			if (isNoParentNode(node))
				nodeList.add(node);
		}
		return nodeList;
	}
	
	
	private Object[] getSortObjects(List<TaskNode> taskNodeList){
		
		List<SortPO> nodeList = new ArrayList<SortPO>();
		for(TaskNode node :taskNodeList){

				nodeList.add(new SortPO(node,getAllSubNodeCount(node)));
		}
		return nodeList.toArray();
	}
	
	/**
	 ��ȡ�����Ľڵ�List
	*/
	private  List<TaskNode> getOtherNodeList(List<TaskNode> currNodeList,List<TaskNode> noParentNodeList){
		List<TaskNode> nodeList = new ArrayList<TaskNode>();
		for(TaskNode node :currNodeList){
			if (!isNodeExists(noParentNodeList,node))
				nodeList.add(node);
		}
		return nodeList;
		
	}
	
	/**
	 * �Ƿ�Ϊ���ڵ�
	 * @param node
	 * @return
	 */
	private  boolean isRootNode(TaskNode node)
	{
		boolean isRoot = false;
		
		if (this.isNoParentNode(node)){
			isRoot = true;
			
			//if (node.getName().equals("A")){
			//����¼����нڵ�ĸ��ڵ�û�и��ڵ㣬����������ĸ��ڵ�
			//�õ��¼��ڵ�ĸ��ڵ�List,��ͨ���ҳ��Ľڵ㣬�ж��Ƿ���ڸ��㣬������Ϊ���ڵ�
			List<TaskNode> subList = getAllSubNode(node);
			for(TaskNode taskNode:subList){
				List<TaskNode> parentList = getAllParentNode(taskNode);
				for(TaskNode taskNode2:parentList){
					
					List<TaskNode> parent2List = getAllParentNode(taskNode2);
					if ( parent2List.size() > 0){
						logger.debug( "node:" + node + ", subNode:"  + taskNode + ", �ϼ��ӽڵ�taskNode2 " +taskNode2 + " ,getAllNoParentNodeList() :" + getString(getAllNoParentNodeList())
								+ "parentList :" + getString(parent2List));
						for(TaskNode parentNode:parent2List){
							
							if (isNoParentNode(parentNode)){//����ӽڵ�ĸ��ڵ�ĸ��ڵ���û�и��ڵ�Ľڵ�
								
								/**���µ��ж��������µĹ�ϵ�����������ģ���E�ڵ�Ӹ��ڵ����ų�����������ų�ĳЩ�ڵ㣬��ͨ����һ���жϽ����޸���
								 * 		list.add(new NodeLink("A","C"));
										list.add(new NodeLink("A","D"));
										list.add(new NodeLink("B","D"));
										list.add(new NodeLink("C","G"));
										list.add(new NodeLink("D","F"));
										list.add(new NodeLink("E","F"));
										list.add(new NodeLink("F","G"));
										list.add(new NodeLink("F","H"));
										list.add(new NodeLink("F","I"));
										list.add(new NodeLink("H","J"));
										list.add(new NodeLink("H","K"));
								 */
								if (isNodeExists(getAllParentNode(taskNode), getAllSubNode(parentNode))){
									isRoot = false;
									logger.debug("isRoot = false 1 ");
								}
								
								/**�����ǰ�����false�������������µ�������������Ϊtrue; 
								 * //��� taskNode2��node���ӽڵ㣬�磺
								    list.add(new NodeLink("A","C"));
									list.add(new NodeLink("B","C"));
									list.add(new NodeLink("C","E"));
									list.add(new NodeLink("C","D"));
									list.add(new NodeLink("B","D"));
									��ǰnodeΪB��subNodeΪD���ϼ��ڵ�taskNode2��C��ֻ��һ��û���㸸�ڵ�Ľڵ㣩�����C�ڵ�ǰnode�У�����Ϊ�Ǹ��ڵ�
									 */
								if (isNodeExists(getAllSubNode(node),taskNode2)){
									isRoot = true;
									logger.debug("isRoot = true 2 ");
								}
								
							} else{//����ӽڵ�ĸ��ڵ�ĸ��ڵ㲻��û�и��ڵ�Ľڵ㣬ֱ����Ϊ���Ǹ��ڵ㣬��D�ĸ��ڵ�C�ĸ��ڵ�ΪA�����A����û�и��ڵ�Ľڵ㣬����Ϊ���Ǹ��ڵ�
								logger.debug("isRoot = false 3 ");
								isRoot = false;
							}
						}
					}
				}
			}
			
		}
		
		return isRoot;
	}
	
	/**
	 * �Ƿ񲻴��ڸ��ڵ�
	 * @param node
	 * @return
	 */
	private  boolean isNoParentNode(TaskNode node)
	{
		boolean isRoot = false;
		List parentTaskList = getAllParentNode(node);
		if (parentTaskList.size() == 0)
			isRoot = true;
		
		return isRoot;
	}
	
	private List<TaskNode> getAllNoParentNodeList(){
		
		List<TaskNode> rootList = new ArrayList<TaskNode>();
		List<TaskNode> taskNodeList = getTaskNodeList();
		for(TaskNode node :taskNodeList){
			if ( getAllParentNode(node).size()== 0)
				rootList.add(node);
		}
		return rootList;
		
	}
	
	/**
	 * ��ȡ��ǰ�ڵ���ϼ��ڵ�
	 * @param node
	 * @return
	 */
	private List<TaskNode> getAllParentNode(TaskNode node)
	{
		List<TaskNode> parentTaskList = new ArrayList<TaskNode>();
		List<NodeLink> linkList = getNodeLinkList();
		//logger.info(node);
		
		for(NodeLink link:linkList){
			if (node != null && link.getTo().equals(node.getName())){
				parentTaskList.add(getTaskNode(link.getFrom()));
			}
		}
		return parentTaskList;
	}
	
	/**
	 * ��ȡ��ǰ�ڵ���¼��ڵ�
	 * @param node
	 * @return
	 */
	private List<TaskNode> getAllSubNode(TaskNode node)
	{
		List<TaskNode> subTaskList = new ArrayList<TaskNode>();
		List<NodeLink> linkList = getNodeLinkList();
		
		for(NodeLink link:linkList){
			if (link.getFrom().equals(node.getName())){
				subTaskList.add(getTaskNode(link.getTo()));
			}
		}
		return subTaskList;
	}
	
	/**
	 * ��ȡ��ǰ�ڵ���¼����нڵ���
	 * @param node
	 * @return
	 */
	private int getAllSubNodeCount(TaskNode node)
	{
		int count = 0;
		boolean isListEnd = false;
		List<TaskNode> subNodeList = null;
		List<TaskNode> currentList = new ArrayList<TaskNode>();
		currentList.add(node);
		while (!isListEnd) {
			boolean noSubNode = true;

			for (TaskNode currNode : currentList) {
				subNodeList = getAllSubNode(currNode);

				if (subNodeList.size() > 0) {// ��������ӽڵ㣬��isEndNode = false;
					noSubNode = false;
				}
				
				count += subNodeList.size();
			}
			currentList = subNodeList;
			isListEnd = noSubNode;
		}
		
		return count;
	}	
	
	/**
	 * ��listת���ɳ��ַ����������node��Ϣ�����ڵ���
	 * @param list
	 * @return
	 */
	private String getString(List<TaskNode> list){
		StringBuffer sb = new StringBuffer();
		for(TaskNode taskNode:list){
			sb.append(taskNode + " ");
		}
		return sb.toString();
	}
	
	/**
	 * ����ǵ�ǰ�����еĽڵ㣺�ӽڵ��ĳ�����ڵ�δ��������֪�ķ����У�����Ϊ���ǵ�ǰ�����еĽڵ㣬û�и��ڵ���ӽڵ㣬��Ϊû�и��ڵ㣬���Բ��μ��ж�
	 * �磺  C-->G
			D-->F
			F-->H
			H-->G
			��ǰ�ڵ�ΪC��D�����ǵ��ӽڵ�ΪG��F��
			F�����и��ڵ��Ѿ���������֪��������У�
			��G�ĸ��ڵ�H�أ�������������֪�����У�����G�ڵ㲻������F�ڵ����ڵĶ�����
	 * @param subNodeList
	 */
	private void clearNotLevelNode(List nodeOrderList,List<TaskNode> subNodeList){
		
		/*if (nodeOrderList.size() == 1){
			return ;
		}*/
		
		StringBuffer sb = new StringBuffer();
		logger.debug("clearNotLevelNode.subNodeList = " + getString(subNodeList));
		for(TaskNode subNode : subNodeList){
			
			if(!isAllParentInNodeOrderList(nodeOrderList,subNode)){
					sb.append(subNode + ",");
			}
		}
		
		
		String nodes = sb.toString();
		
		String[] notLevelNodes = nodes.substring(0,nodes.length() > 0 ? nodes.length() - 1 : 0).split(",");
		
		for(String nodeName : notLevelNodes){
			for(int i = 0; i < subNodeList.size(); i++){
				TaskNode node = (TaskNode)subNodeList.get(i);
				if (node.getName().equals(nodeName)){
					subNodeList.remove(i);
				}
			}
		}
		
	}
	
	

	/**
	 * ���ո��ڵ��˳�����ı�����
	 * @return
	 */
	private void changeSort(){
		ArrayList nodeList = new ArrayList();
		for(int i = 1; i < nodeOrderList.size(); i++){
			nodeList.clear();
			Object[] parentNodes = (Object[])nodeOrderList.get(i - 1);
			Object[] subNodes = (Object[])nodeOrderList.get(i);
			
			for(int ii = 0; ii < parentNodes.length; ii++){
				TaskNode parentNode = (TaskNode)parentNodes[ii];
				for(int j = 0; j <  subNodes.length; j++){
					if (subNodes[j] != null){
						TaskNode node = (TaskNode)subNodes[j];
						if (isNodeExists(getAllParentNode(node), parentNode) && !isNodeExists(nodeList,node)){
							nodeList.add(node);
							subNodes[j] = null;
						}
					}
				}
			}
			
			for(int ii = 0; ii < subNodes.length; ii++){
				if (subNodes[ii] != null){
					nodeList.add(subNodes[ii]);
				}
			}
			
			//�滻ԭ���Ķ���
			nodeOrderList.remove(i);
			nodeOrderList.add(i,nodeList.toArray());
		}

	}
	
	
	/**
	 * 
	 * @param list
	 * @return
	 */
	private void changeSortList(){
		
		for(int i = 0; i < nodeOrderList.size(); i++){
			
			Object[] objects = (Object[])nodeOrderList.get(i);
			int length = objects.length;
			List<TaskNode> currNodeList = new ArrayList<TaskNode>();
			List<TaskNode> noParentNodeList = new ArrayList<TaskNode>();
			List<TaskNode> otherNodeList = new ArrayList<TaskNode>();
			Object[] sortObjects = null;
			List<Integer> indexList = new ArrayList<Integer>();
			TaskNode[] nodes = new TaskNode[length];
			int idx = 0;		
			
			for(int ii = 0; ii < length; ii++){
				currNodeList.add((TaskNode)objects[ii]);
				indexList.add(ii);
			}
			noParentNodeList = getNoParentNodeList(currNodeList);
			otherNodeList = getOtherNodeList(currNodeList,noParentNodeList);
			sortObjects = getSortObjects(noParentNodeList);
			
			//1.û�и��ڵ�����нڵ���������棬
			//noParentNodeList���ڵ������ٵ��������
			QuickSort(sortObjects,true);//���ڵ������ٵ��������
			noParentNodeList = arrayToList(sortObjects);
			for(int j=0; j < noParentNodeList.size(); j++){
				String value = String.valueOf(j / 2.00); //��ΪjΪ0ʱҪ��Ҫ��������
				value = value.substring(value.indexOf(".") + 1);
			  if (Integer.parseInt(value) == 0){
			    idx = indexList.size() - 1;
			    nodes[Integer.parseInt(String.valueOf(indexList.get(idx)))] = noParentNodeList.get(j);
			    indexList.remove(idx);
			  } else{
			    idx = 0;
			    nodes[(int)indexList.get(idx)] = noParentNodeList.get(j);
			    indexList.remove(idx);
			  }
			}
			
			if (i == 0){
				continue;
			}
			//2.ʣ�µİ��ڵ������ҵ�С�����м���������
			//otherNodeList���ڵ����Ӷൽ�ٵ�����
			sortObjects = getSortObjects(otherNodeList);
			QuickSort(sortObjects,false);//���ڵ����Ӷൽ�ٵ�����
			//����ǰ�ýڵ�Ķ���˳��������
			//��ȡǰ�ýڵ�Ķ���Ԫ��
			otherNodeList = arrayToList(sortObjects);
			
			for (Object obj : (Object[])nodeOrderList.get(i-1)){
				TaskNode parent = (TaskNode)obj;
			  if (otherNodeList.size() > 0) {
			    for(int j=0; j < otherNodeList.size(); j++){
			        
			        if (isNodeExists(getAllParentNode(otherNodeList.get(j)),parent)){
			        	idx = (int) (Math.round(Math.ceil(indexList.size() / 2.00)) - 1);
			        	if (nodes.length > 2 && idx == 0){//��ֻ�������ڵ�ʱ����������ȡ�±꣬
			        		idx = indexList.size() -1;
			        	}
			        	
				        nodes[(int)indexList.get(idx)] = otherNodeList.get(j);
				        indexList.remove(idx);
				        otherNodeList.remove(j);
				        j--;
			        }
			    }
			  }
			}
			
			
			//���µ��㷨������
			//������ڵ�������ӽڵ�������ֻ��һ������������븸�ڵ�y��������±�λ ��
			//�����ǰ�ڵ���±�λ�븸�ڵ�y��������±�λ���ȣ���Ե�λ��	
			
			/*for(int j = 0; j < nodes.length; j++){
				TaskNode node = nodes[j];
				if (getAllParentNode(node).size() == 1){//������ڵ�������ӽڵ�������ֻ��һ������������븸�ڵ�y��������±�λ ��
					idx = getIndex(nodes,node);
					if (idx != j){//�����ǰ�ڵ���±�λ�븸�ڵ�y��������±�λ���ȣ���Ե�λ��
					//System.out.println("idx = " + idx + ", j=" + j );
						swap(nodes,j,idx);
					}
				}
			}*/
			
			
			//�滻ԭ���Ķ���
			nodeOrderList.remove(i);
			nodeOrderList.add(i,nodes);
		}

	}
	
	/**
	 * �ҳ�node��yֵ��nodes�е��±�λ
	 * @param nodes
	 * @param node
	 * @return
	 */
	private int getIndex(TaskNode[] nodes,TaskNode node){
		int idx = -1;
		int difValue = 100;
		
		for(int i = 0; i < nodes.length; i++){
			if (node.getY() - nodes[i].getY() < difValue){
				idx = i;
				difValue = node.getY() - nodes[i].getY();
			}
		}
		
		return idx;
		
	}
	
	/**
	 * ����ת����List
	 * @param array
	 * @return
	 */
	private List<TaskNode> arrayToList(Object[] array){
		List<TaskNode> list = new ArrayList<TaskNode>();
		for(Object obj: array){
			list.add(((SortPO)obj).getKey());
		}
		
		return list;
	}
	
	private void QuickSort(Object a[],boolean isSortAsc)  {//��Ϊð�ݷ�.����.
       for (int i = 0; i < a.length - 1; i++)
       {
           for (int j = i + 1; j < a.length; j++)
           {
               Object leftValue = ((SortPO)a[i]).getValue();
               Object nextValue = ((SortPO)a[j]).getValue();
               if (isSortAsc)
               {
            	   if ( compareTo(leftValue, nextValue) > 0){
	                   swap(a, i, j);
            	   }
               } else if (compareTo(leftValue, nextValue) < 0) {
                   swap(a, i, j);
               }
           }
       }
   }
	   
	/**
     * λ���滻����
     * @param a
     * @param i
     * @param j
     */
    private void swap(Object a[], int i, int j)
    {
        //log.info("swap!");
        Object T;
        T = a[i];

        a[i] = a[j];
        a[j] = T;
//        log.debug("swap....a["+i+"]="+a[i]+"a["+j+"]=="+a[j]);
    }

	
	 /**
     * �ж϶������ͣ������ͱȽϡ�
     * @param obj1
     * @param obj2
     * @param property
     * @return �ȽϽ����>0:obj1>obj2.
     */
    private int compareTo(Object obj1, Object obj2)
    {
        int compareValue = 0;
        boolean isNumber = false;
        try
        {
            new Long(obj1.toString()).longValue();
            isNumber = true;
        }
        catch (NumberFormatException ne)
        {
            isNumber = false;
        }
        if (isNumber)
        {
            //��������֣�����������
            compareValue = new Long(obj1.toString())
                .compareTo(new Long(obj2.toString()));
        }
        else
        {
            if (obj1 instanceof java.util.Date ||
                obj1 instanceof java.sql.Timestamp)
            {
                //��������ڣ�����������
                compareValue = ( (Date) obj1).compareTo( (Date) obj2);
            }
            else
            {
                //��������ת�����ַ�������
                compareValue = obj1.toString().compareTo(obj2.toString());
            }
        }
        return compareValue;
    }
	
	private TaskNode getTaskNode(String nodeName){
		for(TaskNode node : getTaskNodeList()){
			if (node.getName().equals(nodeName)){
				return node;
			}
		}
		
		return null;
	}
	
	public static int countWidthByString(String taskName){
		int iWidth=100;
		int taskNameLength = taskName.getBytes().length;
		iWidth = taskNameLength*8 <100?iWidth:taskNameLength*8 ;		
		return iWidth;
	}
	

	/**
	 * ��ȡ���нڵ��б�
	 * @return
	 */
	private List<TaskNode> getTaskNodeList(){
		return taskNodeList; 
	}
	
	/**
	 * ��ȡ�ڵ�Link����
	 * @return
	 */
	private List<NodeLink> getNodeLinkList(){
		return nodeLinkList;
	}


	public int getTaskNodeXInterval() {
		return taskNodeXInterval;
	}

	public void setTaskNodeXInterval(int taskNodeXInterval) {
		this.taskNodeXInterval = taskNodeXInterval;
	}

	public int getTaskNodeYInterval() {
		return taskNodeYInterval;
	}

	public void setTaskNodeYInterval(int taskNodeYInterval) {
		this.taskNodeYInterval = taskNodeYInterval;
	}

	public List getNodeOrderList() {
		return nodeOrderList;
	}
	
}






