package com.aspire.etl.tool;

/**
 * <p>Title: ���ݿ�������ϢVO����</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author liangel
 * @version 1.0
 */

public class ConnInfo {

    /** ���ݿ�����,�磺"oracle.jdbc.driver.OracleDriver". */
    private String dbDriver = "oracle.jdbc.driver.OracleDriver";
    /** ���ݿ������û���,�磺"scott". */
    private String dbUser;
    /** ���ݿ������û���,�磺"scott". */
    private String dbPassword;//���ݿ���������,�磺"tiger";
    /** ���ݿ�����URL,�磺"jdbc:oracle:oci8:@TNSName"��"jdbc:oracle:thin:@10.1.3.191:1521:hp2227". */
    private String dbURL;
    /** SQL*Load DNSֵ */
    private String SQLLoadDNS;
    //ʡID
    private String proId = "";
    /** ����������ݿ��������Ϣ�Ƿ���ȷ */
    private boolean status = true;
    //�������
    private String errCode = "";
    //���������Ϣ
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
