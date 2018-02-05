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

    /**  �û��� */
    public static final String USER = "USER";
   
    /**  ���� */
    public static final String PASSWORD = "PASSWORD";
    
    /**  smtp �ʼ������� */
    public static final String SMTP_SERVER = "SMTP_SERVER";
    
     /**  smtp �ʼ��������˿� */
    public static final String SMTP_SERVER_PORT = "SMTP_SERVER_PORT";
    
    /**  ������ */
    public static final String FROM = "FROM";
    
    /**  �ռ��� */
    public static final String TO = "TO";
    
    /**  �ʼ���ʽ */
    public static final String MAIL_FORMAT = "MAIL_FORMAT";
   
    /**  ���� */
    public static final String CC = "CC";
    
    /**  ���� */
    public static final String BCC = "BCC";
    
    /**  ���� */
    public static final String SUBJECT = "SUBJECT";
    
    /**  ���� */
    public static final String CONTENT = "CONTENT";
    
    /**  ���� */
    public static final String FILE_ATTACHMENT = "FILE_ATTACHMENT";
    
    /** �����ļ�ģʽ*/
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
    		String password = "migu@20177"; //QQ�ʼ�ʹ��STMP/POP3��Ȩ�룬�����ǵ�¼����
    		
    		String from = "BigData_mgv@migu.cn";
    		String to = _to;
    		
    		String subject = "���ԡ�"+encoding+"��";
        String text = "�������ݡ�"+encoding+"��";
        
        sendMail(host, null,accountName, password, from, to, subject, text, debugOn);
    }
    


	public static void sendMail(Map<String, String> map) throws AddressException
		, NoSuchProviderException, GeneralSecurityException , MessagingException,UnsupportedEncodingException {
		
		//TODO DY mail debug����
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

        //����Debug����
        if (debugOn) props.setProperty("mail.debug", "true");

        //���ͷ�������Ҫ�����֤
        props.setProperty("mail.smtp.auth", "true");

        //�����ʼ���������������
        props.setProperty("mail.host", host);
        if(null != port && !"".equals(port)){
        	props.setProperty("mail.smtp.port", port); 
        }
 
        //�����ʼ�Э��
        props.setProperty("mail.transport.protocol", "smtp");

        //����ssl���ܣ����������е��������������Ҫ������qq����������Ǳ���ģ�
/*        MailSSLSocketFactory msf= new MailSSLSocketFactory();
        msf.setTrustAllHosts(true);
        props.put("mail.smtp.ssl.enable", "true");
        props.put("mail.smtp.ssl.socketFactory",msf);*/

        //��ȡSession�Ựʵ����javamail Session��HttpSession��������Javamail��Sessionֻ��������Ϣ�ļ��ϣ�
        Session session=Session.getInstance(props,new javax.mail.Authenticator(){
                protected PasswordAuthentication getPasswordAuthentication(){
                        //�û���������֤��ȡ�õ���Ȩ��
                        return new PasswordAuthentication (accountName, password);
                }
        });

        //������MimeMessageΪʵ����  ��Ϣ�����װ���ʼ���������Ϣ
        Message message=new MimeMessage(session);

        System.out.println("["+encoding+"]");
        logger.debug("======>:"+encoding);
        //�����ʼ�����
        if("".equals(encoding)){
        	message.setSubject(subject);
        } else {
        	message.setSubject(MimeUtility.encodeText(subject, encoding, "B"));
        }
        

        //��װ��Ҫ���͵����ʼ�����Ϣ
//        message.setText(text);
        if("".equals(encoding)){
        	message.setText(text);
        } else {
        	message.setContent(text, "text/plain;charset="+encoding);
        }
        
        //���÷����˵�ַ
        message.setFrom(new InternetAddress(from));
        

        //����Ĺ����Ƿ����ʼ� �ֻỰ���ʵ��
        Transport transport=session.getTransport();

        //��������
        transport.connect();

        //�����ռ��˵�ַ�ʼ���Ϣ
        transport.sendMessage(message, new Address[]{new InternetAddress(to)});

        //�ʼ����ͺ�ر���Ϣ
        transport.close();
	}


}

