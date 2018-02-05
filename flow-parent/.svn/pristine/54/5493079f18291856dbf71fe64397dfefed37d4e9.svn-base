package com.aspire.common.encryption;

import java.io.PrintStream;
import java.util.Random;

public class DESKey
{
  private static final byte[] KEYBYTES = { -117, -59, 30, 49, 6, 39, -4, 110 };
  private DES des = null;
  private DESLimit desLimit = null;
  
  public DESKey()
  {
    this.des = new DES(KEYBYTES);
  }
  
  public DESKey(DESLimit paramDESLimit)
  {
    this.desLimit = paramDESLimit;
    this.des = new DES(KEYBYTES, paramDESLimit);
  }
  
  public String toCihperKey(String paramString)
    throws Exception
  {
    if ((this.desLimit == null) || (!this.desLimit.hasNoLimits())) {
      throw new Exception("Sorry, toCihperKey is not supported.");
    }
    return this.des.encrypt(paramString);
  }
  
  public String toPlainKey(String paramString)
    throws Exception
  {
    return this.des.decrypt(paramString);
  }
  
  public String genCipherKey()
    throws Exception
  {
    byte[] arrayOfByte = new byte[8];
    Random localRandom = new Random(System.currentTimeMillis());
    localRandom.nextBytes(arrayOfByte);
    return toCihperKey(StringEncode.byteArr2HexStr(arrayOfByte));
  }
  
  public static void main(String[] paramArrayOfString)
    throws Exception
  {
    DESKey localDESKey = new DESKey();
    System.out.println(localDESKey.genCipherKey());
  }
}
