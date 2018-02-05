package com.aspire.etl.analyse;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.aspire.etl.metadatamanager.bean.DBConnInfo;
import com.ibatis.sqlmap.client.SqlMapClient;

public class ReportDAO {

	private static SqlMapClient sqlMap = ReportSqlMapManager.getSqlMap();
	
	private DBConnInfo dbConnInfo;
	
	public ReportDAO(DBConnInfo dbConnInfo) {
		this.dbConnInfo = dbConnInfo;
		
	}
	
	public void setConnection(){
		//创建数据库连接：
		try {
			Class.forName(dbConnInfo.getDriver());
			Connection dbConn = DriverManager.getConnection(dbConnInfo.getUrl(), dbConnInfo.getUser(), dbConnInfo.getPasswd());
			sqlMap.setUserConnection(dbConn);

		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@SuppressWarnings("unchecked")
	public List<String> getUserObjectList()
	{
		List<String> list = new ArrayList<String>();
		
		try {
			setConnection();
			
		  List<HashMap> 	resultList = sqlMap.queryForList("getUserObjects",null);
		  for(Map map : resultList){
			  list.add((String) map.get("OBJECT_NAME"));
		  }
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return list;
	}
	
	@SuppressWarnings("unchecked")
	public List<HashMap> getPackageBody()
	{
		setConnection();
		List<HashMap> resultList = new ArrayList<HashMap>();
		
		try {
			
		 resultList = sqlMap.queryForList("getPackageBody",null);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return resultList;
	}
	
}
