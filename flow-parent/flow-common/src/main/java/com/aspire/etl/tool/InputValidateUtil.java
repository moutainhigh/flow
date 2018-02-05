/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.aspire.etl.tool;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author x_lixin_a
 */
public class InputValidateUtil {
       /**
        * �ж�������ַ����Ƿ�����ʱ���ʽ �� yyyy-MM-dd HH:mm:ss
        * @param patternString ��Ҫ��֤���ַ���
        * @return �Ϸ����� true ; ���Ϸ�����false
        */
       public static boolean isTimeLegal(String patternString) {
             
           Pattern a=Pattern.compile("^((((1[6-9]|[2-9]\\d)\\d{2})-(0?[13578]|1[02])-(0?[1-9]|[12]\\d|3[01]))|(((1[6-9]|[2-9]\\d)\\d{2})-(0?[13456789]|1[012])-(0?[1-9]|[12]\\d|30))|(((1[6-9]|[2-9]\\d)\\d{2})-0?2-(0?[1-9]|1\\d|2[0-8]))|(((1[6-9]|[2-9]\\d)(0[48]|[2468][048]|[13579][26])|((16|[2468][048]|[3579][26])00))-0?2-29-)) (20|21|22|23|[0-1]?\\d):[0-5]?\\d:[0-5]?\\d$"); 
      
           Matcher b=a.matcher(patternString); 
           if(b.matches()) {
                 return true;
            } else {
                  return false;
            }
       }
       
       /**
        * �ж�������ַ����Ƿ�������
        * @param patternString
        * @return �����ķ���true û�з���false
        */
       public static boolean haveChinese(String patternString) {
              
            Pattern a=Pattern.compile("/^(\\-?)([u4e00-u9fa5]+)$/"); 
            Matcher b=a.matcher(patternString); 
            if(b.matches()) {
                  return true;
            } else {
                  return false;
            }
       }

}
