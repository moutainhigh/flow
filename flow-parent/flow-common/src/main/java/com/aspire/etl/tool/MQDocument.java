/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.aspire.etl.tool;

/**
 *
 * @author x_lixin_a
 */
 import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;
import java.awt.Toolkit;
import java.util.regex.Pattern;

/**
* Java ���ı����MQDocument�ĵ�������������ʽ���ж������Ƿ�Ϸ�
* 
* @author �嶷�� <��ת���뱣�����ߺͳ���>
* @blog http://blog.csdn.net/mq612
*/
public class MQDocument extends PlainDocument
{
    private static final long serialVersionUID = 1L;
    
    private String limit = null; // �����ַ����Ƶ�������ʽ

    private int maxLength = -1; // �����ַ���󳤶ȵ����ƣ�-1Ϊ������

    private double maxValue = 0; // �������������֣������ֵ����

    private boolean isMaxValue = false; // �Ƿ���������ֵ����

    private Toolkit toolkit = null; // �����ڴ����ʱ�򷢳�ϵͳ����

    private boolean beep = false; // �Ƿ�����trueΪ��������
    
    public static MQDocument getOnlyInputNumDocInstance() {
        MQDocument onlyInputNumDocument = new MQDocument();
        onlyInputNumDocument.setCharLimit("[0-9]");
        return onlyInputNumDocument;
    }

    // ���췽��
    
    public MQDocument() {
     super();
     this.init();
    }

    public MQDocument(Content c) {
     super(c);
     this.init();
    }

    /**
     * ���й��춼��Ҫ�Ĺ�������
     */
    private void init() {
     toolkit = Toolkit.getDefaultToolkit();
    }

    // ���췽������
    
    /**
     * �����ַ���������
     * 
     * @param limit
     *            �������� �ο�������ʽ java.util.regex.Pattern
     */
    public void setCharLimit(String limit) {
     this.limit = limit;
    }

    /**
     * �����ַ����Ƶ�����
     * 
     * @return ����
     */
    public String getCharLimit() {
     return this.limit;
    }

    /**
     * ������������ַ�
     */
    public void clearLimit() {
     this.limit = null;
    }

    /**
     * �ַ����������Ƿ�������ַ�
     * 
     * @param input
     *            �ַ�
     * @return trueΪ������falseΪ������
     */
    public boolean isOfLimit(CharSequence input) {
     if (limit == null) {
      return true;
     } else {
      return Pattern.compile(limit).matcher(input).find();
     }
    }

    /**
     * �ַ�������������Ƿ�Ϊ��
     * 
     * @return trueΪ�գ�falseΪ��
     */
    public boolean isEmptyLimit() {
     if (limit == null) {
      return true;
     } else {
      return false;
     }
    }

    /**
     * �����ı������������������ַ�����
     * 
     * @param maxLength
     *            ����ַ�����
     */
    public void setMaxLength(int maxLength) {
     this.maxLength = maxLength;
    }

    /**
     * ȡ���ı�������ַ����ȵ�����
     */
    public void cancelMaxLength() {
     this.maxLength = -1;
    }

    /**
     * ��������Ϊ�����֣�����ô˷������������ֵ����ֵ
     * 
     * @param maxValue
     *            ���ֵ
     */
    public void setMaxValue(double maxValue) {
     this.isMaxValue = true;
     this.maxValue = maxValue;
    }

    /**
     * �ı����Ƿ��������������ݵ������ֵ
     * 
     * @return trueΪ������
     */
    public boolean isMaxValue() {
     return this.isMaxValue;
    }

    /**
     * �������������������ֵ
     * 
     * @return double�����ֵ�����û�����ƻ᷵��0
     */
    public double getMaxValue() {
     return this.maxValue;
    }

    /**
     * ȡ���������ݵ����ֵ����
     */
    public void cancelMaxValue() {
     this.isMaxValue = false;
     this.maxValue = 0;
    }

    /**
     * ʹ�����������ûָ�Ĭ��
     */
    public void reset() {
     clearLimit();
     cancelMaxLength();
     cancelMaxValue();
    }

    /**
     * ����ʱ��������
     * 
     * @param beep
     *            trueΪ������
     */
    public void errorBeep(boolean beep) {
     this.beep = beep;
    }

    /**
     * �������ʱ�Ƿ���
     * 
     * @return boolean trueΪ����
     */
    public boolean isErrorBeep() {
     return beep;
    }

    public void insertString(int offs, String str, AttributeSet a) throws BadLocationException, NumberFormatException {
     // ���ַ���Ϊ�գ�ֱ�ӷ��ء�
     if (str == null) {
      return;
     }
     boolean b = true;
     char[] ch = str.toCharArray();
     for (int i = 0; i < ch.length; i++) {
      String temp = String.valueOf(ch[i]);
      // ���Ҫ������ַ���������Χ��
      if (!isOfLimit(temp)) {
       b = false;
      }
      // ������ַ��������ƣ��������ڵ��ַ������Ѿ����ڻ��������
      if (maxLength > -1 && this.getLength() >= maxLength) {
       b = false;
      }

     }
     // ��������������������
     if (isMaxValue) {
      String s = this.getText(0, this.getLength()); // �ĵ������е��ַ�
      s = s.substring(0, offs) + str + s.substring(offs, s.length());
      if (Double.parseDouble(s) > maxValue) {
       if (beep) {
        toolkit.beep(); // ��������
       }
       return;
      }
     }
     
     // ������벻�Ϸ�
     if(!b){
      if (beep) {
       toolkit.beep(); // ��������
      }
      return;
     }

     super.insertString(offs, new String(ch), a);
    }

}
 
