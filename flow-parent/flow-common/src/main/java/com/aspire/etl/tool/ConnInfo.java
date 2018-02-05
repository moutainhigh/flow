package com.aspire.etl.tool;

/**
 * <p>Title: 数据库连接信息VO对象</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author liangel
 * @version 1.0
 */

public class ConnInfo {

    /** 数据库驱动,如："oracle.jdbc.driver.OracleDriver". */
    private String dbDriver = "oracle.jdbc.driver.OracleDriver";
    /** 数据库连接用户名,如："scott". */
    private String dbUser;
    /** 数据库连接用户名,如："scott". */
    private String dbPassword;//数据库连接密码,如："tiger";
    /** 数据库连接URL,如："jdbc:oracle:oci8:@TNSName"或"jdbc:oracle:thin:@10.1.3.191:1521:hp2227". */
    private String dbURL;
    /** SQL*Load DNS值 */
    private String SQLLoadDNS;
    //省ID
    private String proId = "";
    /** 表明获得数据库的连接信息是否正确 */
    private boolean status = true;
    //错误代码
    private String errCode = "";
    //保存错误信息
    private String errInfo = "";

    public ConnInfo(){
    }

    public String getDbDriver() {
        return dbDriver;
    }
    public String getDbPassword() {
        return dbPassword;
    }
    public String getDbURL() {
        return dbURL;
    }
    public String getDbUser() {
        return dbUser;
    }
    public String getSQLLoadDNS() {
        return SQLLoadDNS;
    }
    public boolean getStatus(){
        return status;
    }
  public void setDbPassword(String dbPassword) {
    this.dbPassword = dbPassword;
  }
  public void setDbURL(String dbURL) {
    this.dbURL = dbURL;
  }
  public void setDbUser(String dbUser) {
    this.dbUser = dbUser;
  }
  public void setSQLLoadDNS(String SQLLoadDNS) {
    this.SQLLoadDNS = SQLLoadDNS;
  }
  public void setStatus(boolean status) {
    this.status = status;
  }
  public String getErrInfo() {
    return errInfo;
  }
  public void setErrInfo(String errInfo) {
    this.errInfo = errInfo;
  }
  public String getErrCode() {
    return errCode;
  }
  public void setErrCode(String errCode) {
    this.errCode = errCode;
  }
  public String getProId() {
    return proId;
  }
  public void setProId(String proId) {
    this.proId = proId;
  }
  public void setDbDriver(String dbDriver) {
    this.dbDriver = dbDriver;
  }
 
}
