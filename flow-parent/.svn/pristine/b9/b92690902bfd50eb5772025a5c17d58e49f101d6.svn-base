package com.aspire.etl.flowmonitor.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Vector;

import org.apache.log4j.Logger;

import com.aspire.etl.flowmonitor.FlowMonitor;
import com.aspire.etl.flowmonitor.Xmlrpc;
import com.aspire.etl.flowmonitor.beans.FlowBean;
import com.aspire.etl.flowmonitor.beans.WarnBean;

public class FlowDao 
{
//	private FlowMetaData flowMetaData;
	private String driverName;
	private String url;
	private String userName;
	private String pass;
	private Connection conn;
	private PreparedStatement ps;
	private Statement statement;
	private ResultSet rs;
	static Logger log = FlowMonitor.getLog();
	
	
	
	public FlowDao()
	{
	 	try {
	 		HashMap<String, String> map = Xmlrpc.getInstance().queryConnInfo();
	 		if(map != null){
	 			driverName = map.get("dbDriver");
	 			url = map.get("dbURL");
//	 			System.out.println(driverName);
	 			//System.out.println(url);
	 			userName = map.get("dbUser");
	 			pass = map.get("dbPassword");
	 		}
	 		this.init();
	 	} catch (Exception e) {
	 		log.error("初始DAO时出错：" ,e);
	 	}
	}
	
	public void init()
	{
		try {
			Class.forName(driverName);
			this.conn = DriverManager.getConnection(this.url, this.userName, this.pass);
			this.conn.setAutoCommit(false);
			//System.out.println("连接成功！！！");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public Vector<FlowBean> getFlowMessage() throws SQLException
	{
		if (this.conn == null){
			return null;
		}
		
		Vector <FlowBean> vector = new Vector<FlowBean>();
		FlowBean bean = null;
		String s = null;
		this.ps = this.conn.prepareStatement("select * from RPT_LOG");
		this.rs = this.ps.executeQuery();
		while(this.rs.next())
		{
			bean = new FlowBean();
			bean.setTaskFlow(this.rs.getObject(1));
			bean.setTask(this.rs.getObject(2));
			if (this.rs.getInt(3) == 0)
			{
				s = "正常";
			}
			else
			{
				s = "重做";
			}
			bean.setRedo_flag(s);
			bean.setLogLevel(this.rs.getString(4));
			bean.setDescpription(this.rs.getString(5));
			bean.setStartTime(this.rs.getTimestamp(6));
			bean.setEndTime(this.rs.getTimestamp(7));
			bean.setOccurTime(this.rs.getTimestamp(8));
			vector.add(bean);
		}
		return vector;
	}
	
	public Vector<FlowBean> getConfigMessage(String s) throws SQLException
	{
		if (this.conn == null){
			return null;
		}
		
		if (url.toLowerCase().indexOf("jdbc:mysql:") > -1){
			s = s.replace("sysdate", "sysdate()");
		}
		
		Vector <FlowBean> vector = new Vector<FlowBean>();
		FlowBean bean = null;
		this.statement = this.conn.createStatement();
		this.rs = this.statement.executeQuery(s);
		while(this.rs.next())
		{
			bean = new FlowBean();
			String str;
			bean.setTaskFlow(this.rs.getObject(1));
			bean.setTask(this.rs.getObject(2));
			if (this.rs.getInt(3) == 0)
			{
				str = "正常";
			}
			else
			{
				str = "重做";
			}
			bean.setRedo_flag(str);
			bean.setLogLevel(this.rs.getString(4));
			bean.setDescpription(this.rs.getString(5));
			bean.setStartTime(this.rs.getTimestamp(6));
			bean.setEndTime(this.rs.getTimestamp(7));
			bean.setOccurTime(this.rs.getTimestamp(8));
			vector.add(bean);
		}
		return vector;
	}
	
	public Vector<WarnBean> getWarnMessage(String sql) throws SQLException
	{
		if (this.conn == null){
			return null;
		}
		
		if (url.toLowerCase().indexOf("jdbc:mysql:") > -1){
			sql = sql.replace("sysdate", "sysdate()");
		}
		
		Vector<WarnBean> vector = new Vector<WarnBean>();
		WarnBean bean = null;
		String s = null;
		this.statement = this.conn.createStatement();
		this.rs = this.statement.executeQuery(sql);
		while(this.rs.next())
		{
			bean = new WarnBean();
			bean.setHostIp(this.rs.getString(1));
			bean.setSource(this.rs.getString(2));
			bean.setAppendId(this.rs.getString(3));
			bean.setDescription(this.rs.getString(4));
			bean.setOccurTime(this.rs.getTimestamp(5));
			if (this.rs.getInt(6) == 0)
			{
				s = "不是";
			}
			else if (this.rs.getInt(6) == 1)
			{
				s = "是";
			}
			else
			{
				s = "需要备份到历史表的数据";
			}
			bean.setCurrently(s);
			vector.add(bean);
		}	
		return vector;
	}
	
	public ResultSet getRs() {
		return rs;
	}

	public void setRs(ResultSet rs) {
		this.rs = rs;
	} 
	
	public ResultSet getFlowListRs() throws SQLException{
		String sql = "";
		StringBuffer sb = new StringBuffer();
		sb.append("           SELECT V1.GROUP_NAME AS 流程组,                                                                                                                                 ");
		sb.append("                  V1.TASKFLOW AS 流程,                                                                                                                                     ");
		sb.append("                  V1.SUSPENDED AS 挂起状态,                                                                                                                                ");
		sb.append("                  V1.STATUS AS 运行状态,                                                                                                                                   ");
		sb.append("                  V1.FILE_LOGLEVEL AS 文件日志,                                                                                                                            ");
		sb.append("                  V1.DB_LOGLEVEL AS 数据库日志,                                                                                                                            ");
		sb.append("                  V1.RUN_STARTTIME AS 统计时间,                                                                                                                            ");
		sb.append("                  V2.TASK AS 当前任务                                                                                                                                      ");
		sb.append("             FROM (SELECT T1.GROUP_NAME,                                                                                                                                   ");
		sb.append("                          T2.ID_TASKFLOW,                                                                                                                                  ");
		sb.append("                          T2.TASKFLOW,                                                                                                                                     ");
		sb.append("                          T2.SUSPENDED,                                                                                                                                    ");
		sb.append("                          T2.STATUS,                                                                                                                                       ");
		sb.append("                          T2.FILE_LOGLEVEL,                                                                                                                                ");
		sb.append("                          T2.DB_LOGLEVEL,                                                                                                                                  ");
		sb.append("                          T2.RUN_STARTTIME                                                                                                                                 ");
		sb.append("                     FROM RPT_TASKFLOW_GROUP T1, (SELECT FLOW1.ID_TASKFLOW, FLOW1.TASKFLOW, FLOW1.STEP_TYPE, FLOW1.STEP, FLOW2.STATTIME, FLOW2.SCENE_STATTIME,             ");
		sb.append("												FLOW2.STATUS, FLOW2.SUSPENDED, FLOW1.DESCRIPTION, FLOW2.REDO_FLAG, FLOW2.REDO_STARTTIME, FLOW2.REDO_ENDTIME, FLOW2.THREAD_NUM, FLOW2.FILE_LOGLEVEL, ");
		sb.append("												FLOW2.DB_LOGLEVEL, FLOW2.RUN_STARTTIME, FLOW2.RUN_ENDTIME, FLOW1.ID_GROUP, FLOW1.MEMO FROM RPT_TASKFLOW_TEMPLATE FLOW1, RPT_TASKFLOW FLOW2 WHERE    ");
		sb.append("												FLOW1.ID_TASKFLOW = FLOW2.ID_TASKFLOW ORDER BY FLOW1.TASKFLOW) T2  ");																					
		sb.append("                    WHERE T1.ID_GROUP = T2.ID_GROUP) V1,                                                                                                                   ");
		sb.append("                  (SELECT ID_TASKFLOW, TASK FROM (SELECT TASK1.ID_TASK, TASK1.ID_TASKFLOW, TASK1.TASK, TASK1.TASK_TYPE, TASK2.STATUS, TASK1.PLAN_TIME, TASK1.IS_ROOT, 			");
		sb.append("												TASK2.SUSPENDED, TASK1.DESCRIPTION, TASK1.ALERT_ID, TASK1.PERFORMANCE_ID, TASK1.X, TASK1.Y, TASK2.RUN_STARTTIME, TASK2.RUN_ENDTIME, TASK1.MEMO FROM ");
		sb.append("												RPT_TASK_TEMPLATE TASK1, RPT_TASK TASK2 WHERE TASK1.ID_TASK = TASK2.ID_TASK) T3 WHERE T3.STATUS = 1) V2                                             ");
		sb.append("            WHERE V1.ID_TASKFLOW = V2.ID_TASKFLOW(+)                                                                                                                       ");
		sql = sb.toString();
		Statement stmt = null;
		ResultSet flowListRS = null;
		stmt = conn.createStatement();
		flowListRS = stmt.executeQuery(sql);
		return flowListRS;
	}
}
