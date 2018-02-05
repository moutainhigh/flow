package com.aspire.etl.analyse;

import com.ibatis.common.resources.Resources;
import com.ibatis.sqlmap.client.*;
import java.io.Reader;
import java.sql.Connection;
import java.sql.SQLException;

public class ReportSqlMapManager {

  private static final SqlMapClient sqlMap;

  static {
    try {
      String resource = "conf/sql_map_config_report.xml";
      Reader reader = Resources.getResourceAsReader(resource);
      sqlMap = SqlMapClientBuilder.buildSqlMapClient(reader);
    } catch (Exception e) {
      throw new RuntimeException("Could not initialize SqlMapClient.  Cause: " + e);
    }
  }
  
  public static void setConnection(Connection conn){
	  try {
		sqlMap.setUserConnection(conn);
	} catch (SQLException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
  }

  public static SqlMapClient getSqlMap() {
    return sqlMap;
  }

}