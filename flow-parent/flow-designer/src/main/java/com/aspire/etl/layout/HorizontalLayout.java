package com.aspire.etl.layout;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;


/**
 * 水平方向层次布局
 *
 */
public class HorizontalLayout {
	
	private static Logger logger = Logger.getLogger(HorizontalLayout.class);
	
	private boolean isListEnd ;
	private List nodeOrderList ; //方阵队列
	private List<TaskNode> subNodeList;
	private List<TaskNode> currentList;
	private List<TaskNode> currentSubList;
	private HashMap tempNodeMap; 
	private HashMap tempNodeMap2; 
	
	private  List<TaskNode> taskNodeList;
	
	private  List<NodeLink> nodeLinkList;
	
	//图元间的X轴间距
	private int taskNodeXInterval = 100;
	
	//图元间的Y轴间距
	private int taskNodeYInterval = 30;
	
	//每个图元的高度
	private int taskNodeHeight = 80;
	
	public HorizontalLayout(){
		
	}
	
	/**
	 * 初始化
	 *
	 */
	private void init(){
		isListEnd = false;
		nodeOrderList = new ArrayList(); //方阵队列
		subNodeList = null;
		currentList = null;
		currentSubList = null;
		tempNodeMap = null; 
		tempNodeMap2 = null; 
		
		taskNodeList = null;
		nodeLinkList = null;
	}
	/**
	 * 设置taskNodeList中每个节点的在布局中的xy坐标值,<BR>
	 * 参数说明 taskNodeList:任务节点列表，nodeLinkList:节点的Link信息列表
	 * @param taskNodeList  --任务节点列表
	 * @param nodeLinkList  --任务节点的Link信息列表
	 */
	public void setLayout(List<TaskNode> taskNodeList, List<NodeLink> nodeLinkList){
		try {
			
			init();

			//清除重复的节点
			List<TaskNode> list = deRepeateNode(taskNodeList);
			taskNodeList.clear();
			taskNodeList.addAll(list);

			this.taskNodeList = taskNodeList; 
			this.nodeLinkList = nodeLinkList;
			

			/**======0.得到根节点=====*/
			List<TaskNode> rootlist  = getRootTaskNodeList();

			//往方阵队列中加入元素
			nodeOrderList.add(rootlist.toArray());

			logger.debug("得到根节点：" + this.getString(rootlist));

			currentList = rootlist;
			int index = 1;

			//递归根节点的所有下级节点：
			while(!isListEnd){

				boolean noSubNode = true;
				tempNodeMap = new HashMap();
				tempNodeMap2 = new HashMap();
				subNodeList = new ArrayList<TaskNode>();
				currentSubList = new ArrayList<TaskNode>();

				/**======1.正向得到当前的所有子节点=====*/
				for(TaskNode node : currentList){

					logger.debug(index + ": 1xxxxxxxx" + node); 
					subNodeList = getAllSubNode(node);
					if (subNodeList.size() > 0){//如果存在子节点，则isEndNode = false;
						noSubNode = false;
					}

					//将所有子节点put入HashMap中，只是用于去重，如A的子节为CD，B的子节点也为CD，还有个E，这样，所有子节点应该为CDE三个而已
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

				//将正向得到的子节点add到当前子节点集合中，如当前A节点的子节点为B和C节点，是不用质疑的,但不一定为当前队列中的节点，clearNotLevelNode()就是对它进行处理
				while(it.hasNext()){
					Map.Entry entry = (Map.Entry) it.next();
					subNodeList.add((TaskNode) entry.getValue());

					logger.debug(index + ": 3xxxxxxxx currentSubList.add " + (TaskNode) entry.getValue()); 
					currentSubList.add((TaskNode) entry.getValue());
				}

				logger.debug("正向获取的子节点：" + getString(currentSubList));
				//清除非当前队列中的节点
				clearNotLevelNode(nodeOrderList,currentSubList);

				logger.debug(index + ": 3xxxxxxxx 当前子节点列表 = " + getString(subNodeList));


				/**======2.找出下下级的所有父节点，如果不在当前子节点列表中，则归入同列节点的对列中======*/

				//1.找出当前子节点的下级节点
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

				logger.debug(index + ": 5xxxxxxxx 下下级节点 = " + getString(sub2NodeList));
				for(TaskNode node : sub2NodeList){//迭代下下级节点
					List<TaskNode> nextSub2ParentList = getAllParentNode(node);//找出下下级节点的父节点
					for(TaskNode nextSub2Parent : nextSub2ParentList){

						logger.debug(index + ": 5xxxxxxxx subNode= "+ node
								+ ",parent= " + nextSub2Parent  + " isRootNode(nextSub2Parent) =" +
								isNoParentNode(nextSub2Parent)
								+  " ,add parent= " + nextSub2Parent 
								+ "  -->" + isAllParentInNodeOrderList(nodeOrderList,nextSub2Parent));

						if ((isNoParentNode(nextSub2Parent) || isAllParentInNodeOrderList(nodeOrderList,nextSub2Parent))
								&& !isNodeExists(currentSubList, nextSub2Parent) 
								&& !isNodeInAllExists(nodeOrderList, nextSub2Parent)){ //如果没有出现在当前子节点列表,并且不在已知的方阵中出现的节点，则需要增加
							currentSubList.add(nextSub2Parent);
						}
					}
				}

				//清除非当前队列中的节点
				clearNotLevelNode(nodeOrderList,currentSubList);

				logger.debug(index + ": 999xxxxxxxx 最后，当前子节点列表 = " + getString(currentSubList));

				currentList = currentSubList;

				//往方阵队列中加入元素
				if(currentList.size() > 0){
					nodeOrderList.add(currentList.toArray());
				}

				isListEnd = noSubNode;
				index ++;
			}

			logger.debug("方阵对列的总数为：" + nodeOrderList.size());

//			改变列元素中的排序
			changeSort();

			//清除孤立节点
			clearAloneNode();

			//设置各节点的坐标
			setTaskNodeCoord();

		}catch(Exception e){
			e.printStackTrace();
		}
		
	}
	
	/**
	 * 清除孤立节点
	 *
	 */
	private void clearAloneNode() {
		
		//1.通过Link信息删除孤立节点
		ArrayList list = new ArrayList();
		for(int i = 0 ; i < nodeOrderList.size(); i++){
			Object[] taskNodes = (Object[])nodeOrderList.get(i);
			list = new ArrayList();
			for(int j =0 ; j < taskNodes.length; j++){
				TaskNode node = (TaskNode)taskNodes[j];
				
				//任务节点是否存在Link信息
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
	 * 删除重复的TaskNode
	 * @param taskNodeList
	 */
	private List<TaskNode> deRepeateNode(List<TaskNode> taskNodeList){
		ArrayList list = new ArrayList();
		HashMap map = new HashMap();
		//System.out.println("去重前" + taskNodeList.size() + " " + StringUtil.getString(taskNodeList) );
		for(TaskNode node :taskNodeList){
			map.put(node.getName(), node);
		}
		Iterator it = map.entrySet().iterator();
		while(it.hasNext()){
			list.add(((Map.Entry)it.next()).getValue());
		}
		
		//System.out.println("去重后" + list.size()+ " " + StringUtil.getString(list) );
		return list;
	}
	
	/**
	 * 删除任务节点
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
	 * 任务节点是否存在Link信息
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
	 * 设置节点坐标
	 *
	 */
	private void setTaskNodeCoord (){
		
		//生成所有节点的坐标
		int idx = 0;
		int xStartPoint = 20;
		
		for(int i = 0 ; i < nodeOrderList.size(); i++){
			Object[] taskNodes = (Object[])nodeOrderList.get(i);
			int maxVertCount = getVertMaxCount(nodeOrderList);
			int totalHeigth = (taskNodeHeight * maxVertCount + taskNodeYInterval * maxVertCount); //总高度 = 对象高度 * 列最大对象个数 + 对象垂直间距 * 列最大对象个数 
			int nodesHeigth = taskNodeHeight * taskNodes.length + taskNodeYInterval * (taskNodes.length -1);//每列对象集的总高度 = 对象高度 * 列对象个数 + 对象垂直间距 * 列最大对象个数 
			int yStartPoint = totalHeigth/2 - nodesHeigth/2; //总高度 = 总高度 / 2 + 每列对象集的总高度 / 2
			
			logger.debug("yStartPoint = " + yStartPoint);

			int xPoint = xStartPoint + (i == 0 ? 0 : (getMaxWidth((Object[])nodeOrderList.get(i - 1)) + taskNodeXInterval)) ;//x点为 x轴开始点 + （上一级节点队列中的最大宽度 + 横间距）
			for(int j =0 ; j < taskNodes.length; j++){
				
				TaskNode node = (TaskNode)taskNodes[j];
				int xPoint2 = xPoint + (getMaxWidth(taskNodes)/2 - node.getWidth()/2); //x开始点 + （最大宽度的一半 - 当前节点宽度的一半） 
				int yPoint = yStartPoint + (node.getHeigth() + taskNodeYInterval) * j; //第二个节点的y轴 = y轴开始坐标 + （对象高度 + 竖间距）* 当前节点在数组的元素下标（从0开始）
				
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
			  if (!this.isNodeInAllExists(nodeOrderList,node)){ //只要有一个节点不在当前的子节点列表中，则返回false;
				  isExists = false;
			  }
		  }
		 
		 
		 return isExists;
	}


	/**
	 * 查询节点是否存在于指定的节点列表
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
	 * 查询一组节点列表中的节点元素，是否存在于另一组节点列表中，
	 * 如节点列表1有A,B,C,节点列表2有CDE,节点列表2的C存在于节点列表1中
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
	 * 是否存在于nodeOrderList（所有节点）中
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
	 * 获取垂直最大的对象个数
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
	 * 获取所有根节点
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
	 获取没有父节点的节点List
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
	 获取其他的节点List
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
	 * 是否为根节点
	 * @param node
	 * @return
	 */
	private  boolean isRootNode(TaskNode node)
	{
		boolean isRoot = false;
		
		if (this.isNoParentNode(node)){
			isRoot = true;
			
			//if (node.getName().equals("A")){
			//如果下级所有节点的父节点没有父节点，则才是真正的根节点
			//得到下级节点的父节点List,再通过找出的节点，判断是否存在父点，存在则不为根节点
			List<TaskNode> subList = getAllSubNode(node);
			for(TaskNode taskNode:subList){
				List<TaskNode> parentList = getAllParentNode(taskNode);
				for(TaskNode taskNode2:parentList){
					
					List<TaskNode> parent2List = getAllParentNode(taskNode2);
					if ( parent2List.size() > 0){
						logger.debug( "node:" + node + ", subNode:"  + taskNode + ", 上级子节点taskNode2 " +taskNode2 + " ,getAllNoParentNodeList() :" + getString(getAllNoParentNodeList())
								+ "parentList :" + getString(parent2List));
						for(TaskNode parentNode:parent2List){
							
							if (isNoParentNode(parentNode)){//如果子节点的父节点的父节点是没有父节点的节点
								
								/**以下的判断用于以下的关系场景而定做的，把E节点从根节点中排除掉，如果错排除某些节点，则通过下一个判断进行修复。
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
								
								/**以下是把上面false掉，并满足以下的条件，则设置为true; 
								 * //如果 taskNode2在node的子节点，如：
								    list.add(new NodeLink("A","C"));
									list.add(new NodeLink("B","C"));
									list.add(new NodeLink("C","E"));
									list.add(new NodeLink("C","D"));
									list.add(new NodeLink("B","D"));
									当前node为B，subNode为D，上级节点taskNode2（C，只有一个没有你父节点的节点），如果C在当前node中，则认为是根节点
									 */
								if (isNodeExists(getAllSubNode(node),taskNode2)){
									isRoot = true;
									logger.debug("isRoot = true 2 ");
								}
								
							} else{//如果子节点的父节点的父节点不是没有父节点的节点，直接认为不是根节点，如D的父节点C的父节点为A，如果A不是没有父节点的节点，则认为不是父节点
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
	 * 是否不存在父节点
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
	 * 获取当前节点的上级节点
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
	 * 获取当前节点的下级节点
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
	 * 获取当前节点的下级所有节点数
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

				if (subNodeList.size() > 0) {// 如果存在子节点，则isEndNode = false;
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
	 * 将list转换成出字符串，输出各node信息，用于调试
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
	 * 清除非当前队列中的节点：子节点的某个父节点未出现在已知的方阵中，则认为不是当前队列中的节点，没有父节点的子节点，因为没有父节点，所以不参加判断
	 * 如：  C-->G
			D-->F
			F-->H
			H-->G
			当前节点为C和D，它们的子节点为G和F，
			F的所有父节点已经存在于已知方阵队列中，
			而G的父节点H呢，并不存在于已知方阵中，所以G节点不能纳入F节点所在的队列中
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
	 * 参照父节点的顺序来改变排序
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
			
			//替换原来的对象
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
			
			//1.没有父节点的所有节点放在最外面，
			//noParentNodeList按节点数从少到多的排序
			QuickSort(sortObjects,true);//按节点数从少到多的排序
			noParentNodeList = arrayToList(sortObjects);
			for(int j=0; j < noParentNodeList.size(); j++){
				String value = String.valueOf(j / 2.00); //当为j为0时要不要处理？？？
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
			//2.剩下的按节点数从我到小，从中间向外排列
			//otherNodeList按节点数从多到少的排序
			sortObjects = getSortObjects(otherNodeList);
			QuickSort(sortObjects,false);//按节点数从多到少的排序
			//根据前置节点的队列顺序来排序：
			//获取前置节点的队列元素
			otherNodeList = arrayToList(sortObjects);
			
			for (Object obj : (Object[])nodeOrderList.get(i-1)){
				TaskNode parent = (TaskNode)obj;
			  if (otherNodeList.size() > 0) {
			    for(int j=0; j < otherNodeList.size(); j++){
			        
			        if (isNodeExists(getAllParentNode(otherNodeList.get(j)),parent)){
			        	idx = (int) (Math.round(Math.ceil(indexList.size() / 2.00)) - 1);
			        	if (nodes.length > 2 && idx == 0){//当只有两个节点时，从下往上取下标，
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
			
			
			//以下的算法不成立
			//如果父节点的所有子节点数有且只有一个，则调换到与父节点y轴相近的下标位 ，
			//如果当前节点的下标位与父节点y轴相近的下标位不等，则对调位置	
			
			/*for(int j = 0; j < nodes.length; j++){
				TaskNode node = nodes[j];
				if (getAllParentNode(node).size() == 1){//如果父节点的所有子节点数有且只有一个，则调换到与父节点y轴相近的下标位 ，
					idx = getIndex(nodes,node);
					if (idx != j){//如果当前节点的下标位与父节点y轴相近的下标位不等，则对调位置
					//System.out.println("idx = " + idx + ", j=" + j );
						swap(nodes,j,idx);
					}
				}
			}*/
			
			
			//替换原来的对象
			nodeOrderList.remove(i);
			nodeOrderList.add(i,nodes);
		}

	}
	
	/**
	 * 找出node的y值在nodes中的下标位
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
	 * 数组转换成List
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
	
	private void QuickSort(Object a[],boolean isSortAsc)  {//改为冒泡法.倒序.
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
     * 位置替换方法
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
     * 判断对象类型，按类型比较。
     * @param obj1
     * @param obj2
     * @param property
     * @return 比较结果，>0:obj1>obj2.
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
            //如果是数字，按数字排序
            compareValue = new Long(obj1.toString())
                .compareTo(new Long(obj2.toString()));
        }
        else
        {
            if (obj1 instanceof java.util.Date ||
                obj1 instanceof java.sql.Timestamp)
            {
                //如果是日期，按日期排序
                compareValue = ( (Date) obj1).compareTo( (Date) obj2);
            }
            else
            {
                //其他，都转换成字符串排序
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
	 * 获取所有节点列表
	 * @return
	 */
	private List<TaskNode> getTaskNodeList(){
		return taskNodeList; 
	}
	
	/**
	 * 获取节点Link属性
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






