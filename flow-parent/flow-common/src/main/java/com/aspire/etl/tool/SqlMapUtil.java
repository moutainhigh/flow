package com.aspire.etl.tool;

import java.io.BufferedReader;
import java.io.Reader;
import java.io.StringReader;
import java.util.List;

import com.ibatis.sqlmap.client.SqlMapClient;
import com.ibatis.sqlmap.client.SqlMapClientBuilder;

/**
 * @author luoqi
 * 20080325 修改SqlMapUtil的初始化方式，从默认配置文件路径改为指定配置文件路径
 *
 */
public class SqlMapUtil {
	public static SqlMapClient sqlMapClient;
	//不能都用默认配置文件路径初始化
	/*	
	 * static {
		try {
			loadSqlMap();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}*/

	

	public static void init(ConnInfo connInfo,String ibatisConfigFile) throws Exception {						
		StringReader in = new StringReader(getXmlConfig(connInfo, ibatisConfigFile));
		Reader reader = new BufferedReader(in);
		sqlMapClient = SqlMapClientBuilder.buildSqlMapClient(reader);
	}
	

	/**
	 * 
	 * @param MappedStatementName
	 * @param cn
	 * @param vo
	 * @return
	 * @throws DOException
	 */
	public static int executeUpdate(String MappedStatementName, Object o)
			throws Exception {

		return sqlMapClient.update(MappedStatementName, o);

	}

	public static List executeQueryForList(String MappedStatementName,
			Object inputParam) throws Exception {

		return sqlMapClient.queryForList(MappedStatementName, inputParam);

	}

	public static Object executeQueryForObject(String MappedStatementName,
			Object inputParam) throws Exception {

		return sqlMapClient.queryForObject(MappedStatementName, inputParam);

	}

	/**
	 * 开始事务
	 * 
	 * @param
	 * @return
	 * @throws Exception
	 */
	public static void startTransaction() throws Exception {

		sqlMapClient.startTransaction();

	}

	/**
	 * 提交事务
	 * 
	 * @param
	 * @return
	 * @throws Exception
	 */
	public static void commitTransaction() throws Exception {

		sqlMapClient.commitTransaction();

	}

	/**
	 * 回滚事务
	 * 
	 * @param
	 * @return
	 * @throws Exception
	 */
	public static void endTransaction() throws Exception {

		sqlMapClient.endTransaction();

	}

	private static String getXmlConfig(ConnInfo connInfo,
			String ibatisConfigFile) {
		StringBuffer sb = new StringBuffer();
		sb
				.append("<?xml version=\"1.0\" encoding=\"GBK\" ?>                                                          ");
		sb
				.append("<!DOCTYPE sqlMapConfig PUBLIC \"-//iBATIS.com//DTD SQL Map Config 2.0//EN\"                        ");
		sb
				.append("\"http://www.ibatis.com/dtd/sql-map-config-2.dtd\">                                                ");
		sb
				.append("<!-- Always ensure to use the correct XML header as above! -->                                     ");
		sb
				.append("<sqlMapConfig>                                                                                     ");
		sb
				.append("    <transactionManager type=\"JDBC\">                                                             ");
		sb
				.append("        <dataSource type=\"SIMPLE\">                                                               ");
		sb.append("          <property name=\"JDBC.Driver\" value=\"");
		sb.append(connInfo.getDbDriver());
		sb.append("\"/>               ");
		sb.append("          <property name=\"JDBC.ConnectionURL\" value=\"");
		sb.append(connInfo.getDbURL());
		sb.append("\"/>");
		sb.append("          <property name=\"JDBC.Username\" value=\"");
		sb.append(connInfo.getDbUser());
		sb.append("\"/>                                    ");
		sb.append("          <property name=\"JDBC.Password\" value=\"");
		sb.append(connInfo.getDbPassword());
		sb.append("\"/>                                    ");
		sb
				.append("        </dataSource>                                                                              ");
		sb
				.append("    </transactionManager>                                                                          ");
		// todo--
		sb.append("    <sqlMap resource=\"").append(ibatisConfigFile).append(
				"\"/>  ");
		sb
				.append("</sqlMapConfig>                                                                                    ");
		return sb.toString();
	}

}
