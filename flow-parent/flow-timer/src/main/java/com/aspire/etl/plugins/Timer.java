package com.aspire.etl.plugins;


import java.util.HashMap;
import java.util.Map;
import org.apache.log4j.Logger;
import com.aspire.etl.flowdefine.Task;
import com.aspire.etl.flowdefine.Taskflow;
import com.aspire.etl.flowmetadata.dao.FlowMetaData;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Timer {

	Logger log = Logger.getLogger(Timer.class);

        String taskFlow = "";
         
    public int execute(Map map) {
        int isSuccess = Task.SUCCESSED;

        /** 定义taskFlow局部变量，获取当前运行插件对应的流程名 */
        taskFlow = (String) map.get("TASKFLOW");
        String currentTaskID = (String) map.get("TASKID");
        /** 如果 taskFlow 变量不为空 */
        if (taskFlow != null) {
            /** 根据流程名，初始化log变量 */
            log = Logger.getLogger(taskFlow);
        }
        int time = 0;
        /**获取定时方式 */
        String timingMode = String.valueOf(map.get(TimerConstants.TIMER_MODE));

        //是否为准点方式
        if (timingMode.equals(TimerConstants.TIME_TIMINGMODE)) {
            String dateTime = (String) map.get(TimerConstants.TIME_REGULAYVALUE);
            isSuccess = check(dateTime, currentTaskID);

            //是否为延时方式
        } else if (timingMode.equals(TimerConstants.DELAY_TIMINGMODE)) {
            String value = String.valueOf(map.get(TimerConstants.TIMER_VALUE));

            if (TimerConstants.TIMER_DATE.equals((String) map.get(TimerConstants.TIMER_TYPE))) {
                time = Integer.parseInt(value) * 24 * 60 * 60;
                isSuccess = check(time, currentTaskID, 2);
            } else if (TimerConstants.TIMER_HOUR.equals((String) map.get(TimerConstants.TIMER_TYPE))) {
                time = Integer.parseInt(value) * 60 * 60;
                isSuccess = check(time, currentTaskID, 2);
            } else if (TimerConstants.TIMER_MINUTE.equals((String) map.get(TimerConstants.TIMER_TYPE))) {
                time = Integer.parseInt(value) * 60;
                isSuccess = check(time, currentTaskID, 2);
            } else if (TimerConstants.TIMER_SECOND.equals((String) map.get(TimerConstants.TIMER_TYPE))) {
                time = Integer.parseInt(value);
                isSuccess = check(time, currentTaskID, 2);
            } else {
                log.error("错误的周期类型。");
                isSuccess = Task.FAILED;
            }

        }
        return isSuccess;
    }
          
	private int check( int time, String currentTaskID, int num) {
		int isSuccess = Task.SUCCESSED;
		try {		
			int count = time/num;
			FlowMetaData flowMetaData = FlowMetaData.getInstance();
			Task currentTask = flowMetaData.queryTask(Integer.parseInt(currentTaskID));
			Taskflow taskflow = flowMetaData.queryTaskflow(taskFlow);
			
                        //time + 进度时间小于当前时间，则返回成功.
                        Date statTime = addTime(taskflow.getStatTime(),Calendar.SECOND,time);
                        
                        SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

                        String steptype = taskflow.getStepType();
                        int stepnum = taskflow.getStep();

                        //步长类型,S秒,MI分,H时,D日,W周,HM半月,M月,SE季,HY半年,Y年
                        if (steptype.equals("Y")){
                            statTime = addTime(taskflow.getStatTime(),Calendar.YEAR,stepnum);
                        }else if (steptype.equals("M")){
                            statTime = addTime(taskflow.getStatTime(),Calendar.MONTH,stepnum);
                        }else if (steptype.equals("W")){
                            statTime = addTime(taskflow.getStatTime(),Calendar.WEDNESDAY,stepnum);
                        }else if (steptype.equals("D")){
                            statTime = addTime(taskflow.getStatTime(),Calendar.DAY_OF_MONTH,stepnum);
                        }else if (steptype.equals("H")){
                            statTime = addTime(taskflow.getStatTime(),Calendar.HOUR,stepnum);
                        }else if (steptype.equals("MI")){
                            statTime = addTime(taskflow.getStatTime(),Calendar.MINUTE,stepnum);
                        }else if (steptype.equals("S")){
                            statTime = addTime(taskflow.getStatTime(),Calendar.SECOND,stepnum);
                        }

                        Date statTime2 = addTime(statTime,Calendar.SECOND,time);

			for (int i = 0; i < count; i++)
			{
				
				Date nowDate = new Date();
	
	                        if (statTime2.compareTo(nowDate) <= 0){
	                            log.info("当前进度时间[" + dateformat.format(taskflow.getStatTime()) + "]的下一个周期的进度时间[" +
	                                    dateformat.format(statTime) + "]加上延迟时间[" + time + "秒]小于当前时间["
	                                    + dateformat.format(nowDate) + "],返回成功,不做延迟操作.");
	                            return Task.SUCCESSED;
	                        }
				
				
				Thread.sleep(num * 1000);
                               
				if (currentTask.getStatus() == Task.STOPPED){
					isSuccess = Task.FAILED;
					log.info(" 流程被STOP，定时结束！");
					break;
				}
			}			
			
		} catch (InterruptedException e) {
			isSuccess = Task.FAILED;
			log.debug(" Timer.execute() 定时器定时失败,"+ e.getMessage());
		} catch (Exception e) {
			isSuccess = Task.FAILED;
                        e.printStackTrace();;
			log.debug(" Timer.execute() 定时器定时失败,"+ e.getMessage());
		}
		return isSuccess;
	}

         /**
         * 准点定时业务处理
         * @param time 准点时间
         * @param currentTaskID
         * @return isSuccess
         */
        private int check( String time, String currentTaskID) {
           int isSuccess = Task.SUCCESSED;
            SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
          
            try {
                FlowMetaData flowMetaData = FlowMetaData.getInstance();
                Task currentTask = flowMetaData.queryTask(Integer.parseInt(currentTaskID));
                
                 Date tmpDate = dateformat.parse(time);
                 int sysDt=(int)new Date().getTime();
                 int dt= (int)tmpDate.getTime();

                 //通过时间判断，是否执行过该任务
                boolean isState = currentTask.getRunEndTime().getTime() > dt;

                  //系统时间是否小于设置时间
                if( sysDt < dt)
                {                  
                    isSuccess = Task.READY;		     
                }else
                {
                     //如果该任务没有执行过，则执行一次。
                     if (isState){
                        flowMetaData.updateTaskflowAndAllTask(flowMetaData.queryTaskflow(currentTask.getTaskflowID()), Taskflow.STOPPED);
                        isSuccess = Task.STOPPED;
                     } else {
                         isSuccess = Task.SUCCESSED;
                         isState = true;
                     }
                }

        } catch (Exception ex) {
            isSuccess = Task.FAILED;
            log.debug(" Timer.execute() 定时器定时失败,"+ ex.getMessage());
        }                       
		return isSuccess;
	}


    public Date addTime(Date date, int TYPE,int num){
        Calendar cd = Calendar.getInstance();
        cd.setTime(date);
        cd.add(TYPE, num);
        return cd.getTime();
    }


    public static void main(String[] args) throws Exception {

        FlowMetaData flowMetaData = null;

        try {
            FlowMetaData.init("oracle.jdbc.driver.OracleDriver", "mqms_etl_it", "mqms_etl_it",
                    "jdbc:oracle:thin:@10.1.3.200:1521:dbtestsub3");
            flowMetaData = FlowMetaData.getInstance();
            flowMetaData.loadAllTaskflowInfo();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        HashMap<String, String> paramap = new HashMap<String, String>();

        paramap.put(TimerConstants.TIMER_TYPE, "Second");
        paramap.put(TimerConstants.TIMER_VALUE, "10");
        paramap.put("TASKID", "1670621942");
        paramap.put("TASKFLOW", "COMPRESS_MISC_OUT_H");
         paramap.put(TimerConstants.TIMER_MODE, TimerConstants.DELAY_TIMINGMODE);
      //  paramap.put(TimerConstants.TIME_REGULAYVALUE, "2010-3-10 12:12:30");
        TimerDialog dialog = new TimerDialog(
                new javax.swing.JFrame());

        dialog.loadValue(paramap);

        dialog.setVisible(true);

        if (new Timer().execute(paramap) == Task.SUCCESSED) {

            System.out.println("定时成功！");
        } else {
            System.out.println("定时fail！");
        }

    }
}
