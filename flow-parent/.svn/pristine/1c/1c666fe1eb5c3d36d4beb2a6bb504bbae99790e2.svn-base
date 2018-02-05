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
 * 功能:操作windows风格的带有section的ini文件. 如：[database] username=luoqi password＝luoqi
 * 
 * @version 1.0 2004-8-21
 * @author luoqi
 */
public class INIHandler {
    /** ini文件名 */
    private String filename;

    /** key: section名, value: map, map保存了section下面的property键值对 */
    private HashMap sectionMap;

    /** key: sectoin下的property名 , value: sectoin下的property值 */
    private HashMap newPairMap;

    /** ini文件中注释，注意注释只能放在ini文件头部 */
    private String comment = "";

    /**
     * @param f
     *            ini文件名
     */
    public INIHandler(String f) {
        this.filename = f;
        this.sectionMap = new HashMap();
    }

    /**
     * 修改指定内容，如果指定的内容不存在则增加。
     * 
     * @param _section
     *            section名
     * @param _name
     *            property名
     * @param _value
     *            需要修改的值
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
     * 按section名和property名取property值
     * 
     * @param _section
     *            section名
     * @param _name
     *            property名
     * @return property值
     */
    public synchronized String getProperty(String _section, String _name) {
        HashMap hMap = (HashMap) this.sectionMap.get(_section);
        if (hMap == null) {
            return null;
        }
        return (String) hMap.get(_name);

    }

    /**
     * 按section名和property名删除指定property
     * 
     * @param _section
     *            section名
     * @param _name
     *            property名
     */
    public synchronized void removeProperty(String _section, String _name) {
        HashMap hMap = (HashMap) this.sectionMap.get(_section);
        hMap.remove(_name);
    }

    /**
     * 保存对ini文件的修改。 根据当前hashtable中的值重新写ini文件，覆盖掉原来的ini文件。
     */
    public synchronized void saveSection() {
        String outfile = this.filename;
        BufferedWriter fw = null;
        FileWriter out = null;
        try {
            out = new FileWriter(outfile);
            fw = new BufferedWriter(out);

            try {
                //先写回注释的内容
                fw.write(this.comment);
                fw.newLine();

                Iterator it = this.sectionMap.keySet().iterator();
                while (it.hasNext()) {
                    String section = (String) it.next();

                    // 写section
                    fw.write("[" + section + "]");
                    fw.newLine();
                    HashMap map = (HashMap) this.sectionMap.get(section);
                    Iterator iter = map.keySet().iterator();
                    while (iter.hasNext()) {
                        String name = (String) iter.next();
                        String value = (String) map.get(name);

                        // 写该section下的property键值对
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
     * 装载ini文件的内容到hashtable
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
                //保存以“#”和“;“号开头的注释行
                if (line.indexOf("#") != -1 || line.indexOf(";") != -1) {
                    this.comment += line + "\r\n";
                    continue;
                }
                // 判断此行是否是section
                if (line.indexOf("[") == -1) {
                    //这不是一个section行，分析出property键值对，并保存到map中。
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
                    // 这是一个section行，则将以此行中section名为key，map为value加到sectionMap
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
     * 打印ini文件的全部内容
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