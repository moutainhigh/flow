/*
 * SwingUtil.java
 *
 * Created on 2008-3-22 18:03:07 by luoqi
 *
 * Copyright (c) 2001-2008 ASPire Technologies, Inc.
 * 6/F,IER BUILDING, SOUTH AREA,SHENZHEN HI-TECH INDUSTRIAL PARK Mail Box:11# 12#.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of
 * ASPire Technologies, Inc. ("Confidential Information").  You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Aspire.
 */
package com.aspire.etl.tool;
import java.awt.Component;

import java.awt.Cursor;

import java.awt.Dimension;

import java.awt.Font;

import java.awt.Frame;

import java.awt.Image;

import java.awt.Point;

import java.awt.Toolkit;

import java.awt.Window;

import java.util.Enumeration;

import javax.swing.JDialog;

import javax.swing.JFrame;

import javax.swing.JRootPane;

import javax.swing.LookAndFeel;

import javax.swing.SwingUtilities;

import javax.swing.UIManager;

import javax.swing.plaf.FontUIResource;

/**

 * �ṩ��AWT,Swing���õľ�̬����

 *

 * @author none

 */

public class SwingUtil {

    /**

     * �ɴ����з���ʹ�õ�Ĭ�� java.awt.Toolkit

     */

    public static final Toolkit toolkit = Toolkit.getDefaultToolkit();

    /**

     * ��windowȫ����,��Window,Frame,Dialog,JWindow,JFrame,JDialog��Ч

     * @param window ȫ������window

     * @see Window,Frame,Dialog,JWindow,JFrame,JDialog

     */

    public static void setSizeFullOfScreen(Window window) {

        Dimension screenSize = toolkit.getScreenSize();

        window.setBounds(0, 0, screenSize.width, screenSize.height);

    }

    /**

     * ��window��������Ļ�м�,��Window,Frame,Dialog,JWindow,JFrame,JDialog��Ч

     * @param window ��������Ļ�м��window

     * @see Window,Frame,Dialog,JWindow,JFrame,JDialog

     */

    public static void setToScreenCenter(Window window) {

        Dimension screenSize = toolkit.getScreenSize();

        Dimension windowSize = window.getSize();

        if (windowSize.height > screenSize.height)

            windowSize.height = screenSize.height;

        if (windowSize.width > screenSize.width)

            windowSize.width = screenSize.width;

        window.setLocation((screenSize.width - windowSize.width) / 2,

                (screenSize.height - windowSize.height) / 2);

    }

    /**

     * ����ȫ������

     * @param font ȫ������

     */

    public static void initGlobalFontSetting(Font font) {

        FontUIResource fontRes = new FontUIResource(font);

        for (Enumeration keys = UIManager.getDefaults().keys(); keys

                .hasMoreElements();) {

            Object key = keys.nextElement();

            Object value = UIManager.get(key);

            if (value instanceof FontUIResource)

                UIManager.put(key, fontRes);

        }

    }

    /**

     * ��Component�����ù��(Cursor)

     * @param c �ڴ�Component�����ù��(Cursor)

     * @param image ���õĹ��(Cursor)ͼ��

     */

    public static void setCursor(Component c, Image image) {

        Cursor cursor = toolkit

                .createCustomCursor(image, new Point(0, 0), null);

        c.setCursor(cursor);

    }

    /**

     * ����ȫ��LookAndFeel,�˺�����Swing����ʹ�ô�LookAndFeel

     * @param lafstr ���õ�LookAndFeel

     * @return 0:���óɹ�,1:����ʧ��

     */

    public static int setGlobalLookAndFeel(String lafstr) {

        try {

            UIManager.setLookAndFeel(lafstr);

            return 0;

        } catch (Exception e) {

            return 1;

        }

    }

 

    /**

     * �˺�����JFrame,JDialog�Ƿ�ʹ��LookAndFeel�Ĵ���װ��(������,�߿��)

     */

    private static boolean isUndecorated=false;

 

    /**

     * ���ش˺�����JFrame,JDialog�Ƿ�ʹ��LookAndFeel�Ĵ���װ��(������,�߿��)

     * @return �˺�����JFrame,JDialog�Ƿ�ʹ��LookAndFeel�Ĵ���װ��(������,�߿��)

     */

    public static boolean isUndecorated() {

        return isUndecorated;

    }

 

    /**

     * ���ô˺�����JFrame,JDialog�Ƿ�ʹ��LookAndFeel�Ĵ���װ��(������,�߿��)

     * @param �Ƿ�˺�����JFrame,JDialogʹ��LookAndFeel�Ĵ���װ��(������,�߿��)

     */

    public static void setUndecorated(boolean undecorated) {

        LookAndFeel laf = UIManager.getLookAndFeel();

        SwingUtil.isUndecorated = undecorated;

        JFrame.setDefaultLookAndFeelDecorated(undecorated

                && laf.getSupportsWindowDecorations());

        JDialog.setDefaultLookAndFeelDecorated(undecorated

                && laf.getSupportsWindowDecorations());

    }

 

    /**

     * ����JFrame�Ƿ�ʹ��LookAndFeel�Ĵ���װ��(������,�߿��)

     * @param �Ƿ�ʹ��LookAndFeel�Ĵ���װ��(������,�߿��)

     */

    public static void setUndecorated(JFrame frame,boolean undecorated){

        if(frame.isDisplayable()) frame.dispose();

        frame.setUndecorated(undecorated&&UIManager.getLookAndFeel().getSupportsWindowDecorations());

        JRootPane jRootPane=frame.getRootPane();

        jRootPane.setWindowDecorationStyle(jRootPane.getWindowDecorationStyle());

        if (frame.isDisplayable())
			frame.setVisible(true);

    }

 

    /**

     * ����JDialog�Ƿ�ʹ��LookAndFeel�Ĵ���װ��(������,�߿��)

     * @param �Ƿ�ʹ��LookAndFeel�Ĵ���װ��(������,�߿��)

     */

    public static void setUndecorated(JDialog dialog,boolean undecorated){

        if(dialog.isDisplayable()) dialog.dispose();

        dialog.setUndecorated(undecorated&&UIManager.getLookAndFeel().getSupportsWindowDecorations());

        JRootPane jRootPane=dialog.getRootPane();

        jRootPane.setWindowDecorationStyle(jRootPane.getWindowDecorationStyle());

        if (dialog.isDisplayable())
			//dialog.show();
        	dialog.setVisible(true);
    }

    /**

     * ���±�������������JFrame��LookAndFeel

     */

    public static void updateUI(){

        Frame[] frames=Frame.getFrames();

        for(int i=0;i<frames.length;i++)

            updateUI(frames[i]);

    }

    /**

     * ����Component��LookAndFeel

     * @param target ��������

     */

    public static void updateUI(Component target){

            if(target instanceof JFrame)

                updateUI((JFrame)target,isUndecorated);

            if(target instanceof JDialog)

                updateUI((JDialog)target,isUndecorated);

            SwingUtilities.updateComponentTreeUI(target);

    }

    /**

     * ����JFrame��LookAndFeel,�ı�JFrame�ı߿�,����

     * @param frame ��������

     * @param undecorated �Ƿ�ʹ��LookAndFeel�Ĵ���װ��(������,�߿��)

     */

    public static void updateUI(JFrame frame,boolean undecorated){

            setUndecorated(frame,undecorated);

            SwingUtilities.updateComponentTreeUI(frame);

    }

 

    /**

     * ����JDialog��LookAndFeel,�ı�JDialog�ı߿�,����

     * @param dialog ��������

     * @param undecorated �Ƿ�ʹ��LookAndFeel�Ĵ���װ��(������,�߿��)

     */

    public static void updateUI(JDialog dialog,boolean undecorated){

            setUndecorated(dialog,undecorated);

            SwingUtilities.updateComponentTreeUI(dialog);

    }

} 


