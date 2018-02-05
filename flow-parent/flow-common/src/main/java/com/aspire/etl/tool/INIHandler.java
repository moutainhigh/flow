/*
 * @(#)INIHandler.java  1.0 2004-8-21
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

import java.io.*;
import java.util.*;

/**
 * ����:����windows���Ĵ���section��ini�ļ�. �磺[database] username=luoqi password��luoqi
 * 
 * @version 1.0 2004-8-21
 * @author luoqi
 */
public class INIHandler {
    /** ini�ļ��� */
    private String filename;

    /** key: section��, value: map, map������section�����property��ֵ�� */
    private HashMap sectionMap;

    /** key: sectoin�µ�property�� , value: sectoin�µ�propertyֵ */
    private HashMap newPairMap;

    /** ini�ļ���ע�ͣ�ע��ע��ֻ�ܷ���ini�ļ�ͷ�� */
    private String comment = "";

    /**
     * @param f
     *            ini�ļ���
     */
    public INIHandler(String f) {
        this.filename = f;
        this.sectionMap = new HashMap();
    }

    /**
     * �޸�ָ�����ݣ����ָ�������ݲ����������ӡ�
     * 
     * @param _section
     *            section��
     * @param _name
     *            property��
     * @param _value
     *            ��Ҫ�޸ĵ�ֵ
     */
    @SuppressWarnings("unchecked")
	public synchronized void setProperty(String _section, String _name,
            String _value) {
        HashMap hMap = (HashMap) this.sectionMap.get(_section);
        if (hMap == null) {
            this.newPairMap = new HashMap();
            this.newPairMap.put(_name, _value);
            this.sectionMap.put(_section, this.newPairMap);
        } else {
            hMap.put(_name, _value);
        }
    }

    /**
     * ��section����property��ȡpropertyֵ
     * 
     * @param _section
     *            section��
     * @param _name
     *            property��
     * @return propertyֵ
     */
    public synchronized String getProperty(String _section, String _name) {
        HashMap hMap = (HashMap) this.sectionMap.get(_section);
        if (hMap == null) {
            return null;
        }
        return (String) hMap.get(_name);

    }

    /**
     * ��section����property��ɾ��ָ��property
     * 
     * @param _section
     *            section��
     * @param _name
     *            property��
     */
    public synchronized void removeProperty(String _section, String _name) {
        HashMap hMap = (HashMap) this.sectionMap.get(_section);
        hMap.remove(_name);
    }

    /**
     * �����ini�ļ����޸ġ� ���ݵ�ǰhashtable�е�ֵ����дini�ļ������ǵ�ԭ����ini�ļ���
     */
    public synchronized void saveSection() {
        String outfile = this.filename;
        BufferedWriter fw = null;
        FileWriter out = null;
        try {
            out = new FileWriter(outfile);
            fw = new BufferedWriter(out);

            try {
                //��д��ע�͵�����
                fw.write(this.comment);
                fw.newLine();

                Iterator it = this.sectionMap.keySet().iterator();
                while (it.hasNext()) {
                    String section = (String) it.next();

                    // дsection
                    fw.write("[" + section + "]");
                    fw.newLine();
                    HashMap map = (HashMap) this.sectionMap.get(section);
                    Iterator iter = map.keySet().iterator();
                    while (iter.hasNext()) {
                        String name = (String) iter.next();
                        String value = (String) map.get(name);

                        // д��section�µ�property��ֵ��
                        fw.write(name + "=" + value);
                        fw.newLine();
                    }
                }
                fw.flush();
            } catch (Exception e) {
                //System.out.println(getNowTime() + " [ERROR] : \r\n" + e);
            }
        } catch (Exception e) {
            //System.out.println(getNowTime() + " [ERROR] : \r\n" + e);
        } finally {
            try {
                
                if (fw != null) {
                    fw.close();
                }
                if (out != null) {
                    out.close();
                }

            } catch (Exception e) {
            }
        }
    }

    /**
     * װ��ini�ļ������ݵ�hashtable
     */
    @SuppressWarnings("unchecked")
	public synchronized void loadSection() {
        BufferedReader fs = null;
        FileReader in = null;
        try {
            String line;
            String section = null;
            HashMap map = null;
            this.comment = "";
            in = new FileReader(this.filename);
            fs = new BufferedReader(in);
            while ((line = fs.readLine()) != null) {
                //�����ԡ�#���͡�;���ſ�ͷ��ע����
                if (line.indexOf("#") != -1 || line.indexOf(";") != -1) {
                    this.comment += line + "\r\n";
                    continue;
                }
                // �жϴ����Ƿ���section
                if (line.indexOf("[") == -1) {
                    //�ⲻ��һ��section�У�������property��ֵ�ԣ������浽map�С�
                    String name;
                    String value;
                    if (line.trim().length() > 0) {
                        line = line.trim();
                        if (line.indexOf("=") > -1) {
                            name = line.substring(0, line.indexOf("=")).trim();
                            value = line.substring(line.indexOf("=") + 1,
                                    line.length()).trim();
                        } else {
                            name = line.trim();
                            value = "";
                        }
                        if (map != null) {
                            map.put(name, value);
                        }
                    }
                } else {
                    // ����һ��section�У����Դ�����section��Ϊkey��mapΪvalue�ӵ�sectionMap
                    if (map != null) {
                        if (map.size() != 0) {
                            this.sectionMap.put(section, map);
                        }
                    }
                    line = line.trim();
                    section = line.substring(1, line.length() - 1);
                    map = new HashMap();
                }
            }
            //
            if (map != null && map.size() != 0) {
                this.sectionMap.put(section, map);
            }
        } catch (FileNotFoundException f) {
            //System.out.println(getNowTime() + " [ERROR] : \r\n" + f);
        } catch (IOException i) {
            //System.out.println(getNowTime() + " [ERROR] : \r\n" + i);
        } catch (NullPointerException e) {
            //System.out.println(getNowTime() + " [ERROR] : \r\n" + e);
            //e.printStackTrace();
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
                if (fs != null) {
                    fs.close();
                }
            } catch (Exception e) {
                //System.out.println(getNowTime() + " [ERROR] : \r\n" + e);
            }
        }
    }

    /**
     * ��ӡini�ļ���ȫ������
     */
    public synchronized void print() {
        Iterator it = this.sectionMap.keySet().iterator();
        while (it.hasNext()) {
            String section = (String) it.next();
            HashMap map = (HashMap) this.sectionMap.get(section);
            Iterator iter = map.keySet().iterator();
            System.out.println("[" + section + "] ");
            while (iter.hasNext()) {
                String name = (String) iter.next();
                String value = (String) map.get(name);
                System.out.println(name + "=" + value);
            }
        }
    }
    
}