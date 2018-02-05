package com.aspire.etl.plugins;


import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.util.Date;
import java.util.Map;
import java.util.Properties;

import javax.mail.Address;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.NoSuchProviderException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeUtility;

import org.apache.log4j.Logger;

import com.sun.mail.util.MailSSLSocketFactory;

/*  */
public class QQMailUtil {
	private static Logger logger  = Logger.getLogger(QQMailUtil.class);

    /**  用户名 */
    public static final String USER = "USER";
   
    /**  密码 */
    public static final String PASSWORD = "PASSWORD";
    
    /**  smtp 邮件服务器 */
    public static final String SMTP_SERVER = "SMTP_SERVER";
    
     /**  smtp 邮件服务器端口 */
    public static final String SMTP_SERVER_PORT = "SMTP_SERVER_PORT";
    
    /**  发件人 */
    public static final String FROM = "FROM";
    
    /**  收件人 */
    public static final String TO = "TO";
    
    /**  邮件格式 */
    public static final String MAIL_FORMAT = "MAIL_FORMAT";
   
    /**  抄送 */
    public static final String CC = "CC";
    
    /**  暗送 */
    public static final String BCC = "BCC";
    
    /**  主题 */
    public static final String SUBJECT = "SUBJECT";
    
    /**  正文 */
    public static final String CONTENT = "CONTENT";
    
    /**  附件 */
    public static final String FILE_ATTACHMENT = "FILE_ATTACHMENT";
    
    /** 附件文件模式*/
    public static final String FILE_PATTERN = "FILE_PATTERN";
    
    private static String encoding = "GBK";
    public static void main(String[] args) throws GeneralSecurityException, MessagingException,UnsupportedEncodingException {

    		
    		String _to = args[0];
    		if(args.length>1){
    			encoding = args[1];
    		}
    		
    		System.out.println("===["+encoding+"]");
    		boolean debugOn = true;
    		
    		String host = "117.185.122.146";
    		String accountName = "BigData_mgv@migu.cn";
    		String password = "migu@20177"; //QQ邮件使用STMP/POP3授权码，而不是登录密码
    		
    		String from = "BigData_mgv@migu.cn";
    		String to = _to;
    		
    		String subject = "测试【"+encoding+"】";
        String text = "测试内容【"+encoding+"】";
        
        sendMail(host, null,accountName, password, from, to, subject, text, debugOn);
    }
    


	public static void sendMail(Map<String, String> map) throws AddressException
		, NoSuchProviderException, GeneralSecurityException , MessagingException,UnsupportedEncodingException {
		
		//TODO DY mail debug开关
		boolean debugOn = true;
		
    		String host = map.get(SMTP_SERVER);
    		String port = map.get(SMTP_SERVER_PORT);
    		String accountName = map.get(USER);
    		String password = map.get(PASSWORD);
    		
    		String from = map.get(FROM);
    		String to = map.get(TO);
    		String subject = map.get(SUBJECT);
        String text = map.get(CONTENT);
        
        if (host == null || host.isEmpty()) {
        		throw new IllegalArgumentException(map.toString());
        }

        sendMail(host, port,accountName, password, from, to, subject, text, debugOn);
	}

	public static void sendMail(String host, String port,final String accountName, final String password, String from, String to,
			String subject, String text, boolean debugOn)
			throws GeneralSecurityException, MessagingException, AddressException, NoSuchProviderException,UnsupportedEncodingException {
		Properties props=new Properties();

        //开启Debug调试
        if (debugOn) props.setProperty("mail.debug", "true");

        //发送服务器需要身份验证
        props.setProperty("mail.smtp.auth", "true");

        //发送邮件服务器的主机名
        props.setProperty("mail.host", host);
        if(null != port && !"".equals(port)){
        	props.setProperty("mail.smtp.port", port); 
        }
 
        //发送邮件协议
        props.setProperty("mail.transport.protocol", "smtp");

        //开启ssl加密（并不是所有的邮箱服务器都需要，但是qq邮箱服务器是必须的）
/*        MailSSLSocketFactory msf= new MailSSLSocketFactory();
        msf.setTrustAllHosts(true);
        props.put("mail.smtp.ssl.enable", "true");
        props.put("mail.smtp.ssl.socketFactory",msf);*/

        //获取Session会话实例（javamail Session与HttpSession的区别是Javamail的Session只是配置信息的集合）
        Session session=Session.getInstance(props,new javax.mail.Authenticator(){
                protected PasswordAuthentication getPasswordAuthentication(){
                        //用户名密码验证（取得的授权吗）
                        return new PasswordAuthentication (accountName, password);
                }
        });

        //抽象类MimeMessage为实现类  消息载体封装了邮件的所有消息
        Message message=new MimeMessage(session);

        System.out.println("["+encoding+"]");
        logger.debug("======>:"+encoding);
        //设置邮件主题
        if("".equals(encoding)){
        	message.setSubject(subject);
        } else {
        	message.setSubject(MimeUtility.encodeText(subject, encoding, "B"));
        }
        

        //封装需要发送电子邮件的信息
//        message.setText(text);
        if("".equals(encoding)){
        	message.setText(text);
        } else {
        	message.setContent(text, "text/plain;charset="+encoding);
        }
        
        //设置发件人地址
        message.setFrom(new InternetAddress(from));
        

        //此类的功能是发送邮件 又会话获得实例
        Transport transport=session.getTransport();

        //开启连接
        transport.connect();

        //设置收件人地址邮件信息
        transport.sendMessage(message, new Address[]{new InternetAddress(to)});

        //邮件发送后关闭信息
        transport.close();
	}


}

