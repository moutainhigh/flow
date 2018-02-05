/*
 * @(#)SendMail.java  1.0 2005-7-27
 * 
 * Copyright (c) 2003-2005 ASPire Technologies, Inc.
 * 6/F,IER BUILDING, SOUTH AREA,SHENZHEN HI-TECH INDUSTRIAL PARK Mail Box:11# 12#.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of 
 * ASPire Technologies, Inc. ("Confidential Information").  You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Aspire.
 **/

package com.aspire.etl.tool;

/**
 * �����ʼ��������֧�����ģ�����ռ��ˣ�������֤�ͷ��Ͷ������.
 * 
 * @version 1.0 2005-7-27
 * @author luoqi
 */

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.StringTokenizer;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.MimeUtility;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

public class SendMail {
    private String SMTPServer = null;
    
    private String SMTPServerPort = "25";    

    private String from = null;

    private String subject = null;

    private String content = null;

    private String user = null;

    private String password = null;

    private String fileAttachment = null;

    private List fileAttachmentList = new ArrayList();

    private String to = null;

    private String cc = null;

    private String bcc = null;

    private Date sentDate = new Date();

    private Logger log = null;
    
    private boolean bHtmlMail = false;
    
    /**
     * @return
     */
    public String getSMTPServer() {
        return SMTPServer;
    }

    /**
     * @param SMTPServer
     */
    public void setSMTPServer(String SMTPServer) {
        this.SMTPServer = SMTPServer;
    }

    /**
     * @return
     */
    public String getFrom() {
        return from;
    }

    /**
     * @param from
     */
    public void setFrom(String from) {
        this.from = from;
    }

    /**
     * @return
     */
    public String getSubject() {
        return subject;
    }

    /**
     * @param subject
     */
    public void setSubject(String subject) {
        this.subject = subject;
    }

    /**
     * @return
     */
    public String getContent() {
        return content;
    }

    /**
     * @param content
     */
    public void setContent(String content) {
        this.content = content;
    }

    /**
     * @return
     */
    public String getUser() {
        return user;
    }

    /**
     * @param user
     */
    public void setUser(String user) {
        this.user = user;
    }

    /**
     * @return
     */
    public String getPassword() {
        return password;
    }

    /**
     * @param password
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * @return
     */
    public String getFileAttachment() {
        return fileAttachment;
    }

    /**
     * @param fileAttachment
     */
    public void setFileAttachment(String fileAttachment) {
        this.fileAttachmentList.add(fileAttachment);
    }

    /**
     * @return
     */
    public String getBcc() {
        return bcc;
    }

    /**
     * @param bcc
     */
    public void setBcc(String bcc) {
        this.bcc = bcc;
    }

    /**
     * @return
     */
    public String getCc() {
        return cc;
    }

    /**
     * @param cc
     */
    public void setCc(String cc) {
        this.cc = cc;
    }

    /**
     * @return
     */
    public String getTo() {
        return to;
    }

    /**
     * @param to
     */
    public void setTo(String to) {
        this.to = to;
    }

    /**
     * @return
     */
    public List getFileAttachmentList() {
        return fileAttachmentList;
    }

    /**
     * @param fileAttachmentList
     */
    public void setFileAttachmentList(List fileAttachmentList) {
        this.fileAttachmentList = fileAttachmentList;
    }

    /**
     * @return
     */
    public Date getSentDate() {
        return sentDate;
    }

    /**
     * @param sentDate
     */
    public void setSentDate(Date sentDate) {
        this.sentDate = sentDate;
    }

    /**
     * @return
     */
    public Logger getLog() {
        return log;
    }

    /**
     * @param log
     */
    public void setLog(Logger log) {
        this.log = log;
    }

    /**
     * @return
     */
    public boolean send() {
        try {
            Properties props = new Properties();
            Session sendMailSession;

            props.put("mail.smtp.host", SMTPServer);
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.port", SMTPServerPort);

            MyAuthenticator myAuthenticator = new MyAuthenticator(user,
                    password);
            sendMailSession = Session.getInstance(props, myAuthenticator);

            MimeMessage newMessage = new MimeMessage(sendMailSession);
            if (from != null) {
                newMessage.setFrom(new InternetAddress(from));
            }
            if (subject != null) {
                newMessage.setSubject(subject);
            }
            newMessage.setSentDate(sentDate);

            if (to != null||!to.equals("")) {
                if ((to.indexOf(",") != -1) || (to.indexOf(";") != -1)) {

                    StringTokenizer tokenTO = null;

                    tokenTO = new StringTokenizer(to, ", ;");

                    InternetAddress[] addrArrTO = new InternetAddress[tokenTO
                            .countTokens()];
                    int i = 0;
                    while (tokenTO.hasMoreTokens()) {
                        addrArrTO[i] = new InternetAddress(tokenTO.nextToken()
                                .toString());
                        i++;
                    }

                    newMessage.setRecipients(Message.RecipientType.TO,
                            addrArrTO);
                } else {
                    newMessage.setRecipients(Message.RecipientType.TO,
                            InternetAddress.parse(to));
                }

                if (cc != null) {
                    newMessage.setRecipients(Message.RecipientType.CC,
                            InternetAddress.parse(cc));
                }
                if (bcc != null) {
                    newMessage.setRecipients(Message.RecipientType.BCC,
                            InternetAddress.parse(bcc));
                }
                Multipart multipart = new MimeMultipart();

                MimeBodyPart messageBodyPart = new MimeBodyPart();
                
                if(bHtmlMail){
                	messageBodyPart.setContent(content,"text/html;charset=GBK");
                }else {
                	messageBodyPart.setText(content);
                }

                multipart.addBodyPart(messageBodyPart);

                if (fileAttachmentList != null) {
                    for (Iterator iter = fileAttachmentList.iterator(); iter
                            .hasNext();) {
                        String fileAttachment = (String) iter.next();
                        if (fileAttachment != null) {
                            messageBodyPart = new MimeBodyPart();
                            DataSource source = new FileDataSource(
                                    fileAttachment);
                            messageBodyPart.setDataHandler(new DataHandler(source));
                            /*
                             * @�޸ģ�libaoyu
                             */
                            // ȥ�������Ļس����У����ڳ��ַ���encode����ֻس����е����
                            String nextline = javax.mail.internet.MimeUtility.encodeWord("\n", "GBK", "B");
                            String enter = javax.mail.internet.MimeUtility.encodeWord("\r", "GBK", "B");
                            String tmp=MimeUtility.encodeText(source.getName(), "GBK", "B");
                            tmp = StringUtils.replace(tmp, nextline, "");
                            tmp = StringUtils.replace(tmp, enter, "");
                            messageBodyPart.setFileName(tmp);                       
                            //messageBodyPart.setFileName(MimeUtility.encodeText(source.getName(), "GBK", "B"));
                            multipart.addBodyPart(messageBodyPart);
                        }
                    }
                    fileAttachmentList.clear();
                }
           
                newMessage.setContent(multipart);

                Transport.send(newMessage);
            }else {
                log.info("û����д�ռ��ˣ�֪ͨ�ʼ����跢�ͣ�");
            }
            
        } catch (Exception e) {
            if (log == null) {
                System.out.println(e.getMessage());
            } else {
                log.error(e.getMessage());
            }
            return false;
        }
        return true;
    }

    public static void main(String[] args) {
    	
        SendMail mail = new SendMail();
        //����SMTP��������Ϣ
        mail.setSMTPServer("smtp.aspire-tech.com");
        mail.setUser("rptalert");
        mail.setPassword("rptalert");

        //����һ����
        mail.setFrom("rptalert@aspire-tech.com");
        mail.setTo("libaoyu@aspire-tech.com");

        mail.setSubject("test mail-����16");
        mail.setContent("this is a test mail ������ \r\n �ڶ���\r\n������");

        mail.setFileAttachment("D:\\aaaaaa1.zip");
        mail.setFileAttachment("D:\\����SPͳ�ƻ�������_����SPͳ�ƻ�������_����SPͳ�ƻ�������_����SPͳ�ƻ�������.txt");

        if (mail.send()) {
            System.out.println("���ͳɹ���");
        } else {
            System.out.println("����ʧ�ܣ�");
        }

        //���ڶ�����
        mail.setFrom("rptalert@aspire-tech.com");
        mail.setTo("libaoyu@aspire-tech.com");

        mail.setSubject("����SPͳ�ƻ�������_����SPͳ�ƻ�������_����SPͳ�ƻ�������_����SPͳ�ƻ�������.txt����SPͳ�ƻ�������_����SPͳ�ƻ�������_����SPͳ�ƻ�������_����SPͳ�ƻ�������.txt");
        mail.setContent("this is a test mail ������ \r\n �ڶ���\r\n������");

        mail.setFileAttachment("D:\\a.txt");
        mail.setFileAttachment("D:\\aa.txt");

        if (mail.send()) {
            System.out.println("���ͳɹ���");
        } else {
            System.out.println("����ʧ�ܣ�");
        }

    }

    class MyAuthenticator extends Authenticator {
        String _username = null;

        String _password = null;

        public MyAuthenticator(String user, String pass) {
            _username = user;
            _password = pass;
        }

        protected PasswordAuthentication getPasswordAuthentication() {
            return new PasswordAuthentication(_username, _password);
        }

    }

	public boolean isBHtmlMail() {
		return bHtmlMail;
	}

	public void setBHtmlMail(boolean htmlMail) {
		bHtmlMail = htmlMail;
	}    

	/**
	 * @return the sMTPServerPort
	 */
	public String getSMTPServerPort() {
		return SMTPServerPort;
	}

	/**
	 * @param serverPort the sMTPServerPort to set
	 */
	public void setSMTPServerPort(String serverPort) {
		SMTPServerPort = serverPort;
	}

}