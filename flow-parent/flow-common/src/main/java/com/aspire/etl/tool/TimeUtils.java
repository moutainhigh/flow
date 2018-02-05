/*
 * @(#)TimeUtils.java  1.0 2004-11-2
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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * 提供一些日期计算函数.
 *
 * @version 	1.0  2004-11-2
 * @version 	2.0  2008-2-20
 * @author 	luoqi
 */
public class TimeUtils {
	public static final String SECOND = "S";
	public static final String MINUTE = "MI";
	public static final String HOUR = "H";
	public static final String DAY = "D";
	public static final String WEEK = "W";
	public static final String TENDAY = "TD";
	public static final String HALF_MONTH = "HM";
	public static final String MONTH = "M";
	public static final String SEASON = "SE";
	public static final String HALF_YEAR = "HY";
	public static final String YEAR = "Y";
	
    public static String sysdate(String format) {
        SimpleDateFormat formatter = new SimpleDateFormat(format);
        Date currentTime = new Date();
        return formatter.format(currentTime);
    }
    public static Date toDate(String dateStr) {
    	return toDate(dateStr,"yyyy-MM-dd HH:mm:ss");
    }
    public static Date toDate(String dateStr, String format) {
    	if(dateStr.equals("")||dateStr==null){
    		return null;
    	}
    	SimpleDateFormat formatter = new SimpleDateFormat(format);
    	Date date = null;
    	try {
			return date = formatter.parse(dateStr);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return date;
    }
    public static String toChar(Date date, String format) {
        SimpleDateFormat formatter = new SimpleDateFormat(format);
        return formatter.format(date);
    }
    public static String toChar(Date date) {
    	if(date==null){
    		return "";
    	}
        return toChar(date, "yyyy-MM-dd HH:mm:ss");
    }
    public static Date addSecond(Date date, int number) {
        GregorianCalendar c = new GregorianCalendar();
        c.setTime(date);
        c.add(Calendar.SECOND, number);
        return c.getTime();
    }

    public static Date addMinute(Date date, int number) {
        GregorianCalendar c = new GregorianCalendar();
        c.setTime(date);
        c.add(Calendar.MINUTE, number);
        return c.getTime();
    }

    public static Date addHour(Date date, int number) {
        GregorianCalendar c = new GregorianCalendar();
        c.setTime(date);
        c.add(Calendar.HOUR, number);
        return c.getTime();
    }

    public static Date addDay(Date date, int number) {
        GregorianCalendar c = new GregorianCalendar();
        c.setTime(date);
        c.add(Calendar.DAY_OF_YEAR, number);
        return c.getTime();
    }
   
    public static Date addWeek(Date date, int number) {
        GregorianCalendar c = new GregorianCalendar();
        c.setTime(date);
        c.add(Calendar.WEEK_OF_YEAR, number);
        return c.getTime();
    }
    public static Date addWeekOfMonth(Date date, int number) {
        GregorianCalendar c = new GregorianCalendar();
        c.setTime(date);
        c.add(Calendar.WEEK_OF_MONTH, number);
        return c.getTime();
    }
    public static Date addMonth(Date date, int number) {
        GregorianCalendar c = new GregorianCalendar();
        c.setTime(date);
        c.add(Calendar.MONTH, number);
        return c.getTime();
    }
    public static Date addYear(Date date, int number) {
        GregorianCalendar c = new GregorianCalendar();
        c.setTime(date);
        c.add(Calendar.YEAR, number);
        return c.getTime();
    }
	public static Date getNewTime(Date oldTime, String stepType, int step) {
		Date newTime = null;
		if (stepType.equalsIgnoreCase(SECOND)) {
			newTime=TimeUtils.addSecond(oldTime, step);
		} else if (stepType.equalsIgnoreCase(MINUTE)) {
			newTime=TimeUtils.addMinute(oldTime, step);
		} else if (stepType.equalsIgnoreCase(HOUR)) {
			newTime=TimeUtils.addHour(oldTime, step);
		} else if (stepType.equalsIgnoreCase(DAY)) {
			newTime=TimeUtils.addDay(oldTime, step);
		} else if (stepType.equalsIgnoreCase(WEEK)) {
			newTime=TimeUtils.addWeek(oldTime, step);
		} else if (stepType.equalsIgnoreCase(TENDAY)) {
			GregorianCalendar c = new GregorianCalendar();
	        c.setTime(oldTime);
	        if(c.get(Calendar.DAY_OF_MONTH)==1){
				c.set(Calendar.DAY_OF_MONTH,11);
				newTime=c.getTime();
			}else if(c.get(Calendar.DAY_OF_MONTH)==11){		
				c.set(Calendar.DAY_OF_MONTH,21);
				newTime=c.getTime();
			}else if(c.get(Calendar.DAY_OF_MONTH)==21){		
				c.add(Calendar.MONTH, 1);
				c.set(Calendar.DAY_OF_MONTH,1);
				newTime=c.getTime();
			}else{
				//不是1号11或21号的，原样返回
				newTime=oldTime;
			}
		} else if (stepType.equalsIgnoreCase(HALF_MONTH)) {
			//1号改成当月16号，16号改成下月1号
			//step对半月类型无效
			GregorianCalendar c = new GregorianCalendar();
	        c.setTime(oldTime);
			if(c.get(Calendar.DAY_OF_MONTH)==1){
				c.set(Calendar.DAY_OF_MONTH,16);
				newTime=c.getTime();
			}else if(c.get(Calendar.DAY_OF_MONTH)==16){
				c.add(Calendar.MONTH, 1);				
				c.set(Calendar.DAY_OF_MONTH,1);
				newTime=c.getTime();
			}else{
				//不是1号和16号的，原样返回
				newTime=oldTime;
			}
		}else if (stepType.equalsIgnoreCase(MONTH)) {
			newTime=TimeUtils.addMonth(oldTime, step);
		}else if (stepType.equalsIgnoreCase(SEASON)) {
			newTime=TimeUtils.addMonth(oldTime, step*3);
		}else if (stepType.equalsIgnoreCase(HALF_YEAR)) {
			newTime=TimeUtils.addMonth(oldTime, step*6);
		} else if (stepType.equalsIgnoreCase(YEAR)) {
			newTime=TimeUtils.addYear(oldTime, step);
		} else {
			System.out.println("流程的周期类型错误，’" + stepType+"‘不是预定义的类型！");		}
		return newTime;
	}
	
	public static String variable2String(Date curDate, String variable){
		String time = TimeUtils.toChar(curDate, "yyyyMMddHHmmss");
        String year = time.substring(0, 4);
        String month = time.substring(4, 6);
        String date = time.substring(6, 8);
        String hour = time.substring(8, 10);
        String min = time.substring(10, 12);
        String second = time.substring(12, time.length());
        if (variable.contains("%YYYY%")){
        	variable = variable.replace("%YYYY%", year);
        }
        if (variable.contains("%MM%")){
        	variable = variable.replace("%MM%", month);
        }
        if (variable.contains("%DD%")){
            variable = variable.replace("%DD%", date);
        }
        if (variable.contains("%HH%")){
            variable = variable.replace("%HH%", hour);
        }
        if (variable.contains("%MI%")){
            variable = variable.replace("%MI%", min);
        }
        if (variable.contains("%SS%")){
            variable = variable.replace("%SS%", second);
        }
        return variable;
	}
	
    public static void main(String[] args) {
        
        Date start=  TimeUtils.toDate("2008-02-20 00:00:00");
        Date end = TimeUtils.toDate("2008-02-21 00:00:00");
        System.out.println("原时间：["+TimeUtils.toChar(start)+"]->["+TimeUtils.toChar(end)+"]");        
        start=TimeUtils.addSecond(start,1);
        end=TimeUtils.addSecond(end,1);
        System.out.println("新时间 秒：["+TimeUtils.toChar(start)+"]->["+TimeUtils.toChar(end)+"]");
        System.out.println("");
        
        start=  TimeUtils.toDate("2008-02-20 00:00:00");
        end = TimeUtils.toDate("2008-02-20 23:59:59");
        System.out.println("原时间：["+TimeUtils.toChar(start)+"]->["+TimeUtils.toChar(end)+"]");       
        start=TimeUtils.addMinute(start,15);
        end=TimeUtils.addMinute(end,15);
        System.out.println("新时间 15分：["+TimeUtils.toChar(start)+"]->["+TimeUtils.toChar(end)+"]");
        System.out.println("");
       
        start=  TimeUtils.toDate("2008-02-20 00:00:00");
        end = TimeUtils.toDate("2008-02-20 00:00:00");
        System.out.println("原时间：["+TimeUtils.toChar(start)+"]->["+TimeUtils.toChar(end)+"]");        
        start=TimeUtils.addHour(start,1);
        end=TimeUtils.addHour(end,1);
        System.out.println("新时间 时：["+TimeUtils.toChar(start)+"]->["+TimeUtils.toChar(end)+"]");
        System.out.println("");
       

        start=  TimeUtils.toDate("2008-02-18 00:00:00");
        end = TimeUtils.toDate("2008-02-27 00:00:00");
        System.out.println("原时间：["+TimeUtils.toChar(start)+"]->["+TimeUtils.toChar(end)+"]");        
        start=TimeUtils.addDay(start,9);
        end=TimeUtils.addDay(end,9);
        System.out.println("新时间 日：["+TimeUtils.toChar(start)+"]->["+TimeUtils.toChar(end)+"]");
        System.out.println("");
       

        start=  TimeUtils.toDate("2008-02-20 00:00:00");
        end = TimeUtils.toDate("2008-02-20 00:00:00");
        System.out.println("原时间：["+TimeUtils.toChar(start)+"]->["+TimeUtils.toChar(end)+"]");        
        start=TimeUtils.addWeek(start,1);
        end=TimeUtils.addWeek(end,1);
        System.out.println("新时间 周：["+TimeUtils.toChar(start)+"]->["+TimeUtils.toChar(end)+"]");
        System.out.println("");

        start=  TimeUtils.toDate("2008-02-18 00:00:00");
        end = TimeUtils.toDate("2008-02-25 00:00:00");
        System.out.println("原时间：["+TimeUtils.toChar(start)+"]->["+TimeUtils.toChar(end)+"]");        
        start=TimeUtils.addWeekOfMonth(start,1);
        end=TimeUtils.addWeekOfMonth(end,1);
      
        System.out.println("新时间 WEEK_OF_MONTH周：["+TimeUtils.toChar(start)+"]->["+TimeUtils.toChar(end)+"]");
        System.out.println("");
       

        start=  TimeUtils.toDate("2008-02-01 00:00:00");
        end = TimeUtils.toDate("2008-02-16 00:00:00");
        System.out.println("原时间：["+TimeUtils.toChar(start)+"]->["+TimeUtils.toChar(end)+"]");        
        start=TimeUtils.getNewTime(start,"HM",1);
        end=TimeUtils.getNewTime(start,"HM",1);
        System.out.println("新时间 半月：["+TimeUtils.toChar(start)+"]->["+TimeUtils.toChar(end)+"]");
        System.out.println("");
        
        start=  TimeUtils.toDate("2008-02-20 00:00:00");
        end = TimeUtils.toDate("2008-02-20 23:59:59");
        System.out.println("原时间：["+TimeUtils.toChar(start)+"]->["+TimeUtils.toChar(end)+"]");        
        start=TimeUtils.addMonth(start,1);
        end=TimeUtils.addMonth(end,1);
        System.out.println("新时间 月：["+TimeUtils.toChar(start)+"]->["+TimeUtils.toChar(end)+"]");
        System.out.println("");
       
        start=  TimeUtils.toDate("2008-02-20 00:00:00");
        end = TimeUtils.toDate("2008-02-20 23:59:59");
        System.out.println("原时间：["+TimeUtils.toChar(start)+"]->["+TimeUtils.toChar(end)+"]");        
        start=TimeUtils.getNewTime(start,"SE",2);
        end=TimeUtils.getNewTime(start,"SE",2);
        System.out.println("新时间 季度：["+TimeUtils.toChar(start)+"]->["+TimeUtils.toChar(end)+"]");
        System.out.println("");
       
        start=  TimeUtils.toDate("2008-02-01 00:00:00");
        end = TimeUtils.toDate("2008-02-16 00:00:00");
        System.out.println("原时间：["+TimeUtils.toChar(start)+"]->["+TimeUtils.toChar(end)+"]");        
        start=TimeUtils.getNewTime(start,"HY",1);
        end=TimeUtils.getNewTime(start,"HY",1);
        System.out.println("新时间 半年：["+TimeUtils.toChar(start)+"]->["+TimeUtils.toChar(end)+"]");
        System.out.println("");
        

        start=  TimeUtils.toDate("2008-02-20 00:00:00");
        end = TimeUtils.toDate("2008-02-20 23:59:59");
        System.out.println("原时间：["+TimeUtils.toChar(start)+"]->["+TimeUtils.toChar(end)+"]");        
        start=TimeUtils.addYear(start,1);
        end=TimeUtils.addYear(end,1);
        System.out.println("新时间 年：["+TimeUtils.toChar(start)+"]->["+TimeUtils.toChar(end)+"]");
        System.out.println("");
      
    }
}
