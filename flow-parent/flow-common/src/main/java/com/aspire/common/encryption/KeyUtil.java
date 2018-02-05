package com.aspire.common.encryption;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;

public class KeyUtil
{
  private byte tag1 = -34;
  private byte tag2 = -118;
  
  public String getANewCipherKey()
  {
    DESLimit localDESLimit = new DESLimit(this.tag1, this.tag2);
    DESKey localDESKey = new DESKey(localDESLimit);
    try
    {
      return localDESKey.genCipherKey();
    }
    catch (Exception localException)
    {
      localException.printStackTrace();
    }
    return null;
  }
  
  public String encrpytPasswd(String paramString1, String paramString2)
  {
    DESKey localDESKey = new DESKey();
    try
    {
      String str1 = localDESKey.toPlainKey(paramString1);
      DESLimit localDESLimit = new DESLimit(this.tag1, this.tag2);
      DES localDES = new DES(str1, localDESLimit);
      String str2 = localDES.encrypt(paramString2);
      return str2;
    }
    catch (Exception localException)
    {
      localException.printStackTrace();
    }
    return null;
  }
  
  public static void main(String[] paramArrayOfString)
  {
    KeyUtil localKeyUtil = new KeyUtil();
    String str1 = "2ec71bbe2ccaee954a4762569ef7b045";
    BufferedReader localBufferedReader = new BufferedReader(new InputStreamReader(System.in));
    String str2 = null;
    try
    {
      for (;;)
      {
        str2 = selectKey(localBufferedReader, str1, localKeyUtil);
        if (str2 == null) {
          return;
        }
        if (str2.length() == 32) {
          break;
        }
        System.out.println("The cipher key you set is bad, try again please");
      }
      System.out.println("The cipher key you set is between two '<>' tags in \n\t<>" + str2 + "<>");
      System.out.println("Please input your plain password text and Enter:");
      String str3 = localBufferedReader.readLine();
      System.out.println("Your plain password text is between two '<>' tags in \n\t<>" + str3 + "<>");
      System.out.println("Now start encrypting, wait a moment please...");
      String str4 = localKeyUtil.encrpytPasswd(str2, str3);
      System.out.println("Over encrypting, the cipher password text is between two '<>' tags in \n\t<>" + str4 + "<>");
    }
    catch (Exception localException1)
    {
      localException1.printStackTrace();
    }
    finally
    {
      try
      {
        localBufferedReader.close();
      }
      catch (Exception localException2) {}
    }
  }
  
  private static String selectKey(BufferedReader paramBufferedReader, String paramString, KeyUtil paramKeyUtil)
    throws IOException
  {
    Object localObject = null;
    System.out.println("The initial cipher key is " + paramString);
    System.out.println("Options:");
    System.out.println("\t[1] Use this initial key, type i");
    System.out.println("\t[2] Create a new cipher key, type n");
    System.out.println("\t[3] Type your known key");
    System.out.println("\t[4] Type quit to exit");
    System.out.println("Please input and Enter:");
    String str = paramBufferedReader.readLine().trim();
    if (str.equalsIgnoreCase("quit")) {
      return (String)localObject;
    }
    if (str.equalsIgnoreCase("i")) {
      localObject = paramString;
    } else if (str.equalsIgnoreCase("n")) {
      localObject = paramKeyUtil.getANewCipherKey();
    } else {
      localObject = str;
    }
    return (String)localObject;
  }
}
