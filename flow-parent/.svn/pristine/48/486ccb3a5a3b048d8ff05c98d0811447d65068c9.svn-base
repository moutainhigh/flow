package com.aspire.etl.plugins;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.joda.time.DateTime;

import com.aspire.etl.flowdefine.TaskHistory;
import com.aspire.etl.flowmetadata.dao.FlowMetaData;

import ch.ethz.ssh2.ChannelCondition;
import ch.ethz.ssh2.Connection;
import ch.ethz.ssh2.SCPClient;
import ch.ethz.ssh2.SFTPv3Client;
import ch.ethz.ssh2.Session;
import ch.ethz.ssh2.StreamGobbler;

public class Scp {
	private static String host;
	private static int port;
	private static String username;// 登录用户名
	private static String password;// 生成私钥的密码和登录密码，这两个共用这个密码
	private static Connection connection;
	private static String PRIVATEKEY;// 本机的私钥文件
	private static boolean usePassword = false;// 使用用户名和密码来进行登录验证。如果为true则通过用户名和密码登录，false则使用rsa免密码登录
	private FlowMetaData flowMetaData;
	private String stra;
	/** 定义Logger类对象 */
	Logger log = null;

	private final static int TASK_FAILED = -1;
	private final static int TASK_SUCCESSED = 2;
	private final int TIME_OUT = 1000 * 5 * 60;
	public int execute(Map<String, Object> map) {

		/** 定义使用ftp传送文件状态标志位为-1(发送失败标志) */
		int isSuccess = TASK_FAILED;

		/** 定义taskFlow局部变量，获取当前运行插件对应的流程名 */
		String taskFlow = (String) map.get("TASKFLOW");

		/** 如果 taskFlow 变量不为空 TODO */
		if (taskFlow != null) {
			/** 根据流程名，初始化log变量 */
			log = Logger.getLogger(taskFlow);
		}

		log.debug("Ftp.execute()... map =" + map);

		try {
			flowMetaData = FlowMetaData.getInstance();
			log.info("开始从数据库中获取filename");
			List<TaskHistory> historyList = flowMetaData.getTaskHistoryByFlowIdAndStartTime(map);
			//根据history成功任务条数,判断需不需要再次创建目录
			//int num = flowMetaData.getTaskHistoryNumByFlowId(map);
			stra = historyList.get(0).getFile();
			/*
			 * String property = System.getProperty("user.dir"); String confPath
			 * = property + "/cfg"; FlowCfg cfg =
			 * FlowEngineCfgLoader.load(confPath); Map<String, String> configs =
			 * cfg.getConfigs(); host = configs.get("host"); port =
			 * Integer.valueOf(configs.get("port")); username =
			 * configs.get("username"); password = configs.get("password");
			 */
			host = (String) map.get("host");
			String str = (String) map.get("port");
			port = Integer.valueOf(str);
			username = (String) map.get("username");
			password = (String) map.get("password");
			connection = new Connection(host, port);
			String basePath = (String) map.get("basePath");
			String filePath = (String) map.get("filePath");
			String flowId = (String) map.get("FLOW_ID");
			String flag = (String) map.get("AD_HOC");

			List<String> files = new ArrayList<>();
			if (historyList != null && historyList.size() > 0) {
				for (TaskHistory taskHistory : historyList) {
					log.info("taskflowID: " + taskHistory.getTaskflowID());
					log.info("file: " + taskHistory.getFile());
					log.info("id " + taskHistory.getId());
				}
			}
			for (TaskHistory taskHistory : historyList) {
				String file = taskHistory.getFile();
				if (StringUtils.isNotBlank(file)) {
					log.info("file: " + file);
					// String fileNew = file.substring(0, file.length() - 2);
					String newFile = flowId + "_" + file + "_0000.txt.gz";
					files.add(filePath + "/" + flowId + "/data/" + newFile);
				}
			}
			int putFile = putFile(files, basePath, 0);
			// System.out.println("user.dir path: " + property);
			if (putFile == 0) {
				/** scp上传文件状态标志位置为2(上传成功) */
				isSuccess = TASK_SUCCESSED;
			}
		} catch (Exception e) {
			if (log != null) {
				// e.printStackTrace();
				log.debug("catch (Exception e): ", e);
				log.info(" Scp.execute() Scp上传文件错误,请检查配置是否正确！  ");
				log.info(e);
			}
		}

		/** 返回发送邮件状态标志位 */
		return isSuccess;
	}

	private int putFile(List<String> files, String remoteTargetDirectory, int num) {
		Session openSession = null;
		Integer exitStatus = null;
		try {
			connection.connect();
			boolean isAuthed = isAuth();
			if (isAuthed) {
				
				SCPClient scpClient = connection.createSCPClient();
				//SFTPv3Client sftpClient = new SFTPv3Client(connection); 
				openSession = connection.openSession();
				DateTime dateTime = new DateTime();
				String filePath = dateTime.toString("yyyyMMdd");
				String targetPath = remoteTargetDirectory + filePath;
				//String tempPath = StringUtils.substringAfter(targetPath,"/");
				/*if(!targetPath.subSequence(0, 1).equals("/")){
					log.info("目标目录路径错误");
					return 1;
				}*/
				/*String temp = "";
				int i = 2;
				String[] split = targetPath.split("/");
				for (String string : split) {
					System.out.println(string);
					if (StringUtils.isNotBlank(string)) {
						if (i <= split.length) {
							if (i == 3) {
								temp += "mkdir /" + string + ";" + "cd /" + string + ";";
							} else {
								temp += "mkdir " + string + ";" + "cd " + string + ";";
							}
						} else {
							temp += "mkdir " + string;
						}
					}
					i++;
				}*/
				openSession.execCommand("mkdir -p " + targetPath);
				/*log.info("开始创建文件夹.....");
				String[] dirs = targetPath.split("/");
				boolean flag = false;//判断文件夹是否成功flag
				String tempPath = "";
				for (String dir : dirs) {
					if (null == dir || "".equals(dir)) continue;
					tempPath += "/" + dir;
					flag = createDir(sftpClient,tempPath);
				}
				log.info("flag:" + flag);*/
				// 显示执行命令后的信息
				/*InputStream stdout = new StreamGobbler(openSession.getStdout());
				BufferedReader br = new BufferedReader(new InputStreamReader(stdout));
				while (true) {
					String line = br.readLine();
					if (line == null) {
						log.info("session，远程服务器返回信息:空");
						break;
					}
					log.info("session，远程服务器返回信息:" + line);
				}*/

				openSession.waitForCondition(ChannelCondition.EXIT_STATUS, TIME_OUT);
				// 获得退出状态
				exitStatus = openSession.getExitStatus();
				log.info("session，ExitCode: " + exitStatus);// 返回0 表示目录成功；1表示失败
				openSession.close();
				//log.info("history num 数为:" + num);
				/*if(num  == 1){
					if (exitStatus == 0) {// 创建目录成功
						for (String localFile : files) {
							scpClient.put(localFile, targetPath);
						}
					} else {// 目录创建失败
						log.info("目录创建失败,请确认目录路径是否有效");
					}
					if(flag){//目录创建成功
						for (String localFile : files) {
							scpClient.put(localFile, targetPath);
						}
						exitStatus = 0;
					}else{//目录创建失败
						log.info("目录创建失败,请确认目录路径是否有效");
					}
				}else if (num > 1){//周期性任务,且运行次数大于1,此时不需要判断创建文件夹是否成功
					for (String localFile : files) {
						scpClient.put(localFile, targetPath);
					}
					exitStatus = 0;
				}*/
				if (exitStatus == 0) {// 创建目录成功
					for (String localFile : files) {
						scpClient.put(localFile, targetPath);
					}
				}else{//目录创建失败
					log.info("目录创建失败,请确认目录路径是否有效");
				}
			} else {
				log.info("认证失败!");
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			log.info(ex.getMessage());
			/*如果发生异常,将exitStatus置为1*/
			exitStatus = 1;
		} finally {
			connection.close();
			openSession.close();
		}
		return exitStatus;
	}

	/**
	 * ssh用户登录验证，使用用户名和密码来认证
	 * 
	 * @param user
	 * @param password
	 * @return
	 */
	public boolean isAuthedWithPassword(String user, String password) {
		try {
			return connection.authenticateWithPassword(user, password);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * ssh用户登录验证，使用用户名、私钥、密码来认证 其中密码如果没有可以为null，生成私钥的时候如果没有输入密码，则密码参数为null
	 * 
	 * @param user
	 * @param privateKey
	 * @param password
	 * @return
	 */
	public boolean isAuthedWithPublicKey(String user, File privateKey, String password) {
		try {
			return connection.authenticateWithPublicKey(user, privateKey, password);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}

	public boolean isAuth() {
		if (!usePassword) {
			return isAuthedWithPassword(username, password);
		} else {
			return isAuthedWithPublicKey(username, new File(PRIVATEKEY), password);
		}
	}

	public void getFile(String remoteFile, String path) {
		try {
			connection.connect();
			boolean isAuthed = isAuth();
			if (isAuthed) {
				System.out.println("认证成功!");
				SCPClient scpClient = connection.createSCPClient();
				scpClient.get(remoteFile, path);
			} else {
				System.out.println("认证失败!");
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			connection.close();
		}
	}

	public void putFile(String localFile, String remoteTargetDirectory) {
		Session openSession = null;
		try {
			connection.connect();
			boolean isAuthed = isAuth();
			if (isAuthed) {
				SCPClient scpClient = connection.createSCPClient();
				openSession = connection.openSession();
				DateTime dateTime = new DateTime();
				String filePath = dateTime.toString("yyyyMMdd");
				openSession.execCommand("mkdir " + remoteTargetDirectory + filePath);
				scpClient.put(localFile, remoteTargetDirectory + filePath);
			} else {
				System.out.println("认证失败!");
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			connection.close();
			openSession.close();
		}
	}
	/**
	 * 创建文件夹
	 * @author chenhaitao
	 * <p>Title: createDir</p>
	 * <p>Description: </p>
	 * @param sftpClient
	 * @param path
	 * @return
	 * @throws IOException: boolean
	 */
	public boolean createDir(SFTPv3Client sftpClient,String path) throws IOException{
		try {
			sftpClient.mkdir(path, 0777);
			return true;
		} catch (IOException e) {
			return false;
		}
	}
}
