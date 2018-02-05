/*
 * Email.java
 *
 * Created on 2008-3-28 17:44:29 by luoqi
 *
 * Copyright (c) 2001-2008 ASPire Technologies, Inc.
 * 6/F,IER BUILDING, SOUTH AREA,SHENZHEN HI-TECH INDUSTRIAL PARK Mail Box:11# 12#.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of
 * ASPire Technologies, Inc. ("Confidential Information").  You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Aspire.
 */
package com.aspire.etl.plugins;

import java.util.Map;

import org.apache.log4j.Logger;

/**
 * Email 插件
 * @author x_lixin_a
 *
 */
public class Email {
	
	/** 定义Logger类对象 */
	Logger log = null;
	
	private final static int TASK_FAILED = -1;
	private final static int TASK_SUCCESSED = 2;
	
	
	/**
	 * 通用插件接口方法，开发插件只需要实现此方法即可
	 * @param map 获取到从 流程设计器（FlowDesigner.jar） 定义的Map 对象
	 * @return 运行状态标志位 2 成功 -1 失败
	 */
	public int execute(Map<String,String> map){
		
		/** 定义发送邮件状态标志位为-1(发送失败标志) */
 		int isSuccess = TASK_FAILED;
		
		/** 定义taskFlow局部变量，获取当前运行插件对应的流程名 */
		String taskFlow = (String) map.get("TASKFLOW");
		
		/** 如果 taskFlow 变量不为空 TODO*/
		if(taskFlow != null) {
            /** 根据流程名，初始化log变量 */
			log = Logger.getLogger(taskFlow);
		}
		
		log.debug("Email.execute()... map =" + map);
		
		try {
			QQMailUtil.sendMail(map);

	        	/** 将发送邮件状态标志位置为2(发送成功) */
	        	isSuccess =  TASK_SUCCESSED;
		} catch (Exception e) {
            if(log != null) {
            		//e.printStackTrace();
            		log.debug("catch (Exception e): ", e);
                log.info(" Email.execute() 邮件未发送，请检查邮件服务器地址、端口、用户名和密码是否正确！  ");
                log.info(e);
            }
		} 
		
        /** 返回发送邮件状态标志位 */
  	    return isSuccess; 
	}
}