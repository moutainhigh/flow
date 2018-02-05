package com.aspire.etl.tool;

import java.io.*;
import java.net.*;

public class Connect {
  //默认连接超时10秒
  private int nTimeOut = 10000;

  //数据库密码管理模块的配置
  private String host_ip = "127.0.0.1";//ip
  private int host_port = 3000;//port
  private String MISC_ALIAS = "";//kernel数据库名
  private String XPORTAL_ALIAS = "";//portal数据库名
  private String REPORT_ALIAS = "";//报表的数据库名
  private String COGNOS_ALIAS = "";//cognos
  /** 数据库驱动,如："oracle.jdbc.driver.OracleDriver". */
  private String dbDriver = null;

  public Connect() {
  }

  public Connect(String fileName) {
      INIHandler iniReader = new INIHandler(fileName);
      //载入ini文件
      iniReader.loadSection();
      //ip
      if (iniReader.getProperty("DBSecurityManager", "IP") != null) {
        this.host_ip = iniReader.getProperty("DBSecurityManager", "IP");
      }
      //port
      if (iniReader.getProperty("DBSecurityManager", "PORT") != null) {
        this.host_port = Integer.parseInt(iniReader.getProperty("DBSecurityManager", "PORT"));
      }
      //kernel数据库名
      if (iniReader.getProperty("DBSecurityManager", "MISC_ALIAS") != null) {
        this.MISC_ALIAS = iniReader.getProperty("DBSecurityManager", "MISC_ALIAS");
      }
      //portal数据库名
      if (iniReader.getProperty("DBSecurityManager", "XPORTAL_ALIAS") != null) {
        this.XPORTAL_ALIAS = iniReader.getProperty("DBSecurityManager", "XPORTAL_ALIAS");
      }
      //report数据库名
      if (iniReader.getProperty("DBSecurityManager", "REPORT_ALIAS") != null) {
        this.REPORT_ALIAS = iniReader.getProperty("DBSecurityManager", "REPORT_ALIAS");
      }
      //cognos数据库名
      if (iniReader.getProperty("DBSecurityManager", "COGNOS_ALIAS") != null) {
        this.COGNOS_ALIAS = iniReader.getProperty("DBSecurityManager", "COGNOS_ALIAS");
      }
      //数据库驱动
      if (iniReader.getProperty("DBSecurityManager", "dbDriver") != null) {
        this.dbDriver = iniReader.getProperty("DBSecurityManager", "dbDriver");
      }
  }

  public ConnInfo getConnInfo(String dbName) throws IOException{
    return getConnInfo(this.host_ip,this.host_port,dbName);
  }

  public ConnInfo getConnInfo(String hostIp , int nPort , String dbName) throws IOException{
    ConnInfo cnInfo = new ConnInfo();
    String strLine = "";
    //收到的总长度
    int nTotalLen = 0;
    //缓存收到的数据
    byte[] buffer = new byte[1024];
    //每次读的长度
    int nLen = 0;
    //记录当前位置
    int nPos = 0;
    Socket clientSocket1 = null;
    DataInputStream dis = null;
    DataOutputStream dos = null;
      //连接server
    clientSocket1 = new Socket(hostIp, nPort);
      //设置默认的超时时间
      clientSocket1.setSoTimeout(nTimeOut);
      //取得输入流
      dis = new DataInputStream(clientSocket1.getInputStream() );
      //取得输出流
       dos = new DataOutputStream(clientSocket1.getOutputStream() );
      //发送请求包
      dos.write(("REQ" + getLength(dbName) + dbName).getBytes());
      dos.flush();
      //先读包的头，固定3个长度
      while(true){
        nLen = dis.read(buffer,nPos,3);
        if(nLen == -1)
          break;
        else
          nTotalLen = nTotalLen + nLen;
        if (nTotalLen == 3) break;
      }
      //先检查收到的数据的长度对不对，长度不对，则直接返回失败
      if(nTotalLen<3){
        cnInfo.setErrCode("1000");
        cnInfo.setErrInfo("包格式错误！");//返回错误信息
        cnInfo.setStatus(false);
        return cnInfo;
      }
      nPos = nTotalLen;
      strLine = new String(buffer,0,nTotalLen);
      //解析包头
      if(strLine.equalsIgnoreCase("REP")){//返回成功包
        //如果成功包，再读下边固定的15个长度，分别表示：用户名、密码、Url、TNS和省Id的长度
        while(true){
          nLen = dis.read(buffer,nPos,15);
          if(nLen == -1)
            break;
          else
            nTotalLen = nTotalLen + nLen;
          if (nTotalLen == nPos + 15) break;
        }
        //先检查收到的数据的长度对不对，长度不对，则直接返回失败
        if(nTotalLen < nPos + 15){
          cnInfo.setErrCode("1000");
          cnInfo.setErrInfo("包格式错误！");//返回错误信息
          cnInfo.setStatus(false);
          return cnInfo;
        }
        nPos = nTotalLen;
        //先转成字符串
        strLine = new String(buffer,0,nTotalLen);
        int nUsrLen = 0;
        int nPwdLen = 0;
        int nUrlLen = 0;
        int nTnsLen = 0;
        int nProLen = 0;

        //然后按固定位置拆分。
        nUsrLen = Integer.parseInt(strLine.substring(3,6));
        nPwdLen = Integer.parseInt(strLine.substring(6,9));
        nUrlLen = Integer.parseInt(strLine.substring(9,12));
        nTnsLen = Integer.parseInt(strLine.substring(12,15));
        nProLen = Integer.parseInt(strLine.substring(15,18));

        //读后面的实际的内容
        while(true){
          nLen = dis.read(buffer, nPos, nUsrLen + nPwdLen + nUrlLen + nTnsLen + nProLen);
          if (nLen == -1)
            break;
          else
            nTotalLen = nTotalLen + nLen;
          if (nTotalLen == nPos + nUsrLen + nPwdLen + nUrlLen + nTnsLen + nProLen)break;
        }
        //先检查收到的数据的长度对不对，长度不对，则直接返回失败
        if(nTotalLen<nPos + nUsrLen + nPwdLen + nUrlLen + nTnsLen + nProLen){
          cnInfo.setErrCode("1000");
          cnInfo.setErrInfo("包格式错误！");//返回错误信息
          cnInfo.setStatus(false);
          return cnInfo;
        }
        //按长度，分别解析
        cnInfo.setDbUser(new String(buffer, nPos, nUsrLen)); //用户名
        nPos = nPos + nUsrLen;
        cnInfo.setDbPassword(new String(buffer, nPos, nPwdLen)); //密码
        nPos = nPos + nPwdLen;
        cnInfo.setDbURL(new String(buffer, nPos, nUrlLen)); //URL
        nPos = nPos + nUrlLen;
        cnInfo.setSQLLoadDNS(new String(buffer, nPos, nTnsLen)); //TNS
        nPos = nPos + nTnsLen;
//        cnInfo.setProId(new String(buffer, nPos, nProLen)); //省ID
        cnInfo.setDbDriver(new String(buffer, nPos, nProLen)); //driver
        cnInfo.setStatus(true);
      }else if(strLine.equalsIgnoreCase("ERR")){////返回失败包
        //再读下边固定的错误代码和长度
        while(true){
          nLen = dis.read(buffer,nPos,6);
          if(nLen == -1)
            break;
          else
            nTotalLen = nTotalLen + nLen;
          if (nTotalLen == nPos + 6) break;
        }
        //先检查收到的数据的长度对不对，长度不对，则直接返回失败
        if(nTotalLen<nPos + 6){
          cnInfo.setErrCode("1000");
          cnInfo.setErrInfo("包格式错误！");//返回错误信息
          cnInfo.setStatus(false);
          return cnInfo;
        }
        nPos = nTotalLen;
        //先转成字符串
        strLine = new String(buffer,0,nTotalLen);
        int nErrLen = 0;
        cnInfo.setStatus(false);
        cnInfo.setErrCode(strLine.substring(3,6));//错误代码
        nErrLen = Integer.parseInt(strLine.substring(6,9));//错误信息的长度
        //读错误详细信息
        while(true){
          nLen = dis.read(buffer,nPos,nErrLen);
          if(nLen == -1)
            break;
          else
            nTotalLen = nTotalLen + nLen;
          if (nTotalLen == nPos + nErrLen) break;
        }
        //先检查收到的数据的长度对不对，长度不对，则直接返回失败
        if(nTotalLen<nPos + nErrLen){
          cnInfo.setErrCode("1000");
          cnInfo.setErrInfo("包格式错误！");//返回错误信息
          cnInfo.setStatus(false);
          return cnInfo;
        }
        cnInfo.setErrInfo(new String(buffer, nPos, nErrLen));//错误信息
      }
      
      //关闭连接
      try{
    	  clientSocket1.close();
      }catch(Exception e)
      {  
      };
      
    //返回
    return cnInfo;
  }

  private String getLength(String str){
    byte[] b = str.getBytes();
    if (str==null || str.equals(""))
      return "000";
    else if(b.length<10){
      return "00" + (b.length);
    }
    else if(b.length<100){
      return "0" + b.length;
    }
    else
      return "" + b.length ;
  }
  //读超时时间
  public int getNTimeOut() {
    return nTimeOut;
  }
  //设置超时时间
  public void setNTimeOut(int nTimeOut) {
    this.nTimeOut = nTimeOut;
  }
  public String getMISC_ALIAS() {
    return MISC_ALIAS;
  }
  public void setMISC_ALIAS(String MISC_ALIAS) {
    this.MISC_ALIAS = MISC_ALIAS;
  }
  public String getXPORTAL_ALIAS() {
    return XPORTAL_ALIAS;
  }
  public void setXPORTAL_ALIAS(String XPORTAL_ALIAS) {
    this.XPORTAL_ALIAS = XPORTAL_ALIAS;
  }
  public String getCOGNOS_ALIAS() {
    return COGNOS_ALIAS;
  }
  public void setCOGNOS_ALIAS(String COGNOS_ALIAS) {
    this.COGNOS_ALIAS = COGNOS_ALIAS;
  }
  public String getREPORT_ALIAS() {
    return REPORT_ALIAS;
  }
  public void setREPORT_ALIAS(String REPORT_ALIAS) {
    this.REPORT_ALIAS = REPORT_ALIAS;
  }
  public String getDbDriver() {
    return dbDriver;
  }
  public void setDbDriver(String dbDriver) {
    this.dbDriver = dbDriver;
  }

public String getHost_ip() {
	return host_ip;
}

public void setHost_ip(String host_ip) {
	this.host_ip = host_ip;
}

public int getHost_port() {
	return host_port;
}

public void setHost_port(int host_port) {
	this.host_port = host_port;
}


}
