package com.aspire.etl.plugins;

import java.io.File;
import java.io.FileInputStream;
import java.util.Map;

import org.apache.log4j.Logger;

import com.aspire.elt.utils.FtpUtil;

public class Ftp {
	/** 定义Logger类对象 */
	Logger log = null;

	private final static int TASK_FAILED = -1;
	private final static int TASK_SUCCESSED = 2;

	public int execute(Map<String, Object> map) {

		/** 定义发送邮件状态标志位为-1(发送失败标志) */
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
			String host = (String) map.get("host");
			int port = (int) map.get("port");
			String username = (String) map.get("username");
			String password = (String) map.get("password");
			String basePath = (String) map.get("basePath");
			String filePath = (String) map.get("filePath");
			String filename = (String) map.get("filename");
			FileInputStream input = new FileInputStream(new File((String) map.get("originFile")));
			FtpUtil.uploadFile(host, port, username, password, basePath, filePath, filename, input);
			/** 将发送邮件状态标志位置为2(发送成功) */
			isSuccess = TASK_SUCCESSED;
		} catch (Exception e) {
			if (log != null) {
				// e.printStackTrace();
				log.debug("catch (Exception e): ", e);
				log.info(" Email.execute() 邮件未发送，请检查邮件服务器地址、端口、用户名和密码是否正确！  ");
				log.info(e);
			}
		}

		/** 返回发送邮件状态标志位 */
		return isSuccess;
	}
}
