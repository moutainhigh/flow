package com.aspire.etl.flowengine;

import java.io.File;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.DailyRollingFileAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.MDC;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.jdbc.JDBCAppender;
import org.apache.log4j.varia.LevelRangeFilter;

import com.aspire.etl.flowdefine.Taskflow;
import com.aspire.etl.flowmetadata.dao.FlowMetaData;
import com.aspire.etl.tool.ConnInfo;
import com.aspire.etl.tool.TimeUtils;

public class MyConfigurator {

	static Logger log = Logger.getLogger("server");

	public static String fs = System.getProperty("file.separator");

	static String consoleLayoutStr = "[%-5p]%m%n";

	static String layoutStr = "[%-5p]%d{yyyy-MM-dd HH:mm:ss} [%c]-  %X{REDO_FLAG}, %X{STATTIME},%X{ENDTIME} %m%n";

	static String jdbcLayoutStr = "insert into RPT_LOG (TASKFLOW,TASK, REDO_FLAG, LOGLEVEL, DESCRIPTION, STATTIME, ENDTIME,OCCURTIME) values ('%X{TASKFLOW}', '%X{TASK}', %X{REDO_FLAG},'%.5p', '%m', to_date('%X{STATTIME}', 'dd-mm-yyyy hh24:mi:ss'), to_date('%X{ENDTIME}', 'dd-mm-yyyy hh24:mi:ss'),sysdate)";

	static String oamJdbcLayoutStr = "insert into OAM_ALERT_INFO (HOSTIP, SOURCE, APPENDID, DESCRIPTION, OCCURTIME, CURRENTLY) values ('%X{HOSTIP}', '%X{SOURCE}', '%X{APPENDID}', '%m', sysdate, %X{CURRENTLY})";

	/**
	 * 一次性初始化所有流程的logger,包括jdbc logger
	 * 
	 * @param taskflowList
	 * @param serverConfig
	 * @param logPath
	 * @param oamLevel
	 * @param connInfo
	 */
	public static void configure(List<Taskflow> taskflowList,
			FlowMetaData flowMetaData) {
		for (Taskflow taskflow : taskflowList) {
			configure(taskflow, flowMetaData);
		}
	}
	
	public static void configure(Taskflow taskflow,
			FlowMetaData flowMetaData) {
		String logPath = flowMetaData.querySysConfigValue("SYS_LOG_PATH");
		int oamLevel = Integer.parseInt(flowMetaData
				.querySysConfigValue("SYS_LOG_TO_OAM"));
		ConnInfo connInfo = null;
		connInfo = flowMetaData.getConnInfo();
		String fileLayoutStr = flowMetaData.querySysConfigValue("FILE_LAYOUT");
		String jdbcLayoutStr = flowMetaData.querySysConfigValue("JDBC_LAYOUT");
		String oamJdbcLayoutStr = flowMetaData
				.querySysConfigValue("OAM_JDBC_LAYOUT");
		String consoleLevel = flowMetaData.querySysConfigValue("CONSOLE_LEVEL");
		String consoleLayoutStr = flowMetaData
				.querySysConfigValue("CONSOLE_LAYOUT");
		
		// 建立日志目录
		makeDirs(logPath);
		
		MDC.put("TASKFLOW", taskflow.getTaskflow());
		MDC.put("REDO_FLAG", taskflow.getRedoFlag());
		MDC.put("STATTIME", TimeUtils.toChar(taskflow.getStatTime()));
		// 2008-05-19 wuzhuokun (为空时调用TimeUtils会出空指针错误)增加为空的判断
		MDC.put("ENDTIME", taskflow.getStatTime() == null ? TimeUtils
				.toChar(taskflow.getStatTime()) : TimeUtils.toChar(TimeUtils
				.getNewTime(taskflow.getStatTime(), taskflow.getStepType(),
						taskflow.getStep())));

		Logger logger = Logger.getLogger(taskflow.getTaskflow());

		logger.setLevel(Level.toLevel("ALL"));

		// 增加控制台输出 ConsoleAppender
		ConsoleAppender consoleAppender = newConsoleAppender(consoleLevel, consoleLayoutStr);
		logger.addAppender(consoleAppender);

		// 增加文件输出 DailyRollingFileAppender
		DailyRollingFileAppender fileAppender = newDailyRollingFileAppender(taskflow.getTaskflow(),taskflow
				.getFileLogLevel(), logPath + File.separator + taskflow.getTaskflow(), fileLayoutStr);

		logger.addAppender(fileAppender);

		if (connInfo != null) {
			// 增加数据库输出 JDBCAppender
			JDBCAppender jdbcAppender = newJDBCAppender(taskflow, connInfo, jdbcLayoutStr);
			logger.addAppender(jdbcAppender);

			// 增加OAM输出 OAMJDBCAppender
			JDBCAppender OAMJdbcAppender = newOAMJDBCAppender(taskflow, oamLevel, connInfo, oamJdbcLayoutStr);
			logger.addAppender(OAMJdbcAppender);

			try {
				MDC.put("HOSTIP", InetAddress.getLocalHost().getHostAddress());
			} catch (UnknownHostException e) {
				MDC.put("HOSTIP", "unknow ip");
				log.error(e);
			}
			MDC.put("SOURCE", "0000000002");
			MDC.put("APPENDID", "00000");
			MDC.put("CURRENTLY", "1");
			
		}

	}



	private static JDBCAppender newOAMJDBCAppender(Taskflow taskflow, int oamLevel, ConnInfo connInfo, String oamJdbcLayoutStr) {
		PatternLayout oamJdbclayout = new PatternLayout(oamJdbcLayoutStr);
		JDBCAppender OAMJdbcAppender = new JDBCAppender();
		OAMJdbcAppender.setLayout(oamJdbclayout);
		OAMJdbcAppender.setName(taskflow.getTaskflow() + "oam");
		OAMJdbcAppender.setURL(connInfo.getDbURL());
		OAMJdbcAppender.setUser(connInfo.getDbUser());
		OAMJdbcAppender.setPassword(connInfo.getDbPassword());
		OAMJdbcAppender.setDriver(connInfo.getDbDriver());

		String OAMLevel = "OFF";
		if (oamLevel == 0) {
			OAMLevel = "ERROR";
		} else if (oamLevel == 1) {
			OAMLevel = "OFF";
		}
		
		LevelRangeFilter OAMLevelRangeFilter = new LevelRangeFilter();
		OAMLevelRangeFilter.setLevelMin(Level.toLevel(OAMLevel));
		OAMLevelRangeFilter.setLevelMax(Level.toLevel(OAMLevel));
		OAMJdbcAppender.addFilter(OAMLevelRangeFilter);
		return OAMJdbcAppender;
	}



	private static JDBCAppender newJDBCAppender(Taskflow taskflow, ConnInfo connInfo, String jdbcLayoutStr) {
		PatternLayout jdbclayout = new PatternLayout(jdbcLayoutStr);

		JDBCAppender jdbcAppender = new JDBCAppender();
		jdbcAppender.setLayout(jdbclayout);
		jdbcAppender.setName(taskflow.getTaskflow() + "jdbc");
		jdbcAppender.setURL(connInfo.getDbURL());
		jdbcAppender.setUser(connInfo.getDbUser());
		jdbcAppender.setPassword(connInfo.getDbPassword());
		jdbcAppender.setDriver(connInfo.getDbDriver());

		LevelRangeFilter DBLevelRangeFilter = new LevelRangeFilter();
		DBLevelRangeFilter.setLevelMin(Level.toLevel(taskflow
				.getDbLogLevel()));
		DBLevelRangeFilter.setLevelMax(Level.toLevel("FATAL"));
		jdbcAppender.addFilter(DBLevelRangeFilter);
		return jdbcAppender;
	}



	private static DailyRollingFileAppender newDailyRollingFileAppender(String taskflowName,String level , String logPath, String fileLayoutStr) {
		PatternLayout fileLayout = new org.apache.log4j.PatternLayout(fileLayoutStr);
		
		DailyRollingFileAppender fileAppender = new DailyRollingFileAppender();
		fileAppender.setEncoding("GB2312");
		fileAppender.setDatePattern("'.'yyyyMMdd");
		fileAppender.setName(taskflowName);
		fileAppender.setLayout(fileLayout);
		fileAppender.setFile(logPath + fs + taskflowName + ".log");
		fileAppender.activateOptions();

		LevelRangeFilter fileLevelRangeFilter = new LevelRangeFilter();
		fileLevelRangeFilter.setLevelMin(Level.toLevel(level));
		fileLevelRangeFilter.setLevelMax(Level.toLevel("FATAL"));
		fileLevelRangeFilter.setAcceptOnMatch(true);

		fileAppender.addFilter(fileLevelRangeFilter);
		return fileAppender;
	}



	private static ConsoleAppender newConsoleAppender(String consoleLevel, String consoleLayoutStr) {
		PatternLayout consoleLayout = new org.apache.log4j.PatternLayout(
				consoleLayoutStr);
		ConsoleAppender consoleAppender = new ConsoleAppender(consoleLayout,
				"System.out");

		consoleAppender.setName("Console");
		LevelRangeFilter consoleLevelRangeFilter = new LevelRangeFilter();
		consoleLevelRangeFilter.setLevelMin(Level.toLevel(consoleLevel));
		consoleLevelRangeFilter.setLevelMax(Level.toLevel("FATAL"));
		consoleLevelRangeFilter.setAcceptOnMatch(true);

		consoleAppender.addFilter(consoleLevelRangeFilter);
		return consoleAppender;
	}

	public static void makeDirs(String path) {
		File f = new File(path);
		if (!f.exists())
			f.mkdirs();
	}

}
