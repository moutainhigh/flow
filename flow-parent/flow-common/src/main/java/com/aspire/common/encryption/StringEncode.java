package com.aspire.common.encryption;

public class StringEncode
{
  public static String byteArr2HexStr(byte[] paramArrayOfByte)
    throws Exception
  {
    int i = paramArrayOfByte.length;
    StringBuffer localStringBuffer = new StringBuffer(i * 2);
    for (int j = 0; j < i; j++)
    {
      int k = paramArrayOfByte[j];
      while (k < 0) {
        k += 256;
      }
      if (k < 16) {
        localStringBuffer.append("0");
      }
      localStringBuffer.append(Integer.toString(k, 16));
    }
    return localStringBuffer.toString();
  }
  
  public static byte[] hexStr2ByteArr(String paramString)
    throws Exception
  {
    byte[] arrayOfByte1 = paramString.getBytes();
    int i = arrayOfByte1.length;
    byte[] arrayOfByte2 = new byte[i / 2];
    int j = 0;
    while (j < i)
    {
      String str = new String(arrayOfByte1, j, 2);
      arrayOfByte2[(j / 2)] = ((byte)Integer.parseInt(str, 16));
      j += 2;
    }
    return arrayOfByte2;
  }
}
