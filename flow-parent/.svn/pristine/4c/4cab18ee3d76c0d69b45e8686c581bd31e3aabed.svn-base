package com.aspire.common.encryption;

import com.sun.crypto.provider.SunJCE;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Security;
import java.security.spec.InvalidKeySpecException;
import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;

public class DES
{
  private Cipher ecipher;
  private Cipher dcipher;
  private final String ALGORITHM = "DES";
  private final String TRANSFORMATION = "DES/ECB/NoPadding";
  private SecretKey secretKey = null;
  private DESLimit desLimit = null;
  
  public DES(String paramString)
  {
    try
    {
      byte[] arrayOfByte = StringEncode.hexStr2ByteArr(paramString);
      init(arrayOfByte);
    }
    catch (Exception localException)
    {
      localException.printStackTrace();
    }
  }
  
  public DES(byte[] paramArrayOfByte)
  {
    init(paramArrayOfByte);
  }
  
  public DES(String paramString, DESLimit paramDESLimit)
  {
    this.desLimit = paramDESLimit;
    try
    {
      byte[] arrayOfByte = StringEncode.hexStr2ByteArr(paramString);
      init(arrayOfByte);
    }
    catch (Exception localException)
    {
      localException.printStackTrace();
    }
  }
  
  public DES(byte[] paramArrayOfByte, DESLimit paramDESLimit)
  {
    this.desLimit = paramDESLimit;
    init(paramArrayOfByte);
  }
  
  private void init(byte[] paramArrayOfByte)
  {
    try
    {
      Security.addProvider(new SunJCE());
      DESKeySpec localDESKeySpec = new DESKeySpec(paramArrayOfByte);
      this.secretKey = SecretKeyFactory.getInstance("DES", "SunJCE").generateSecret(localDESKeySpec);
      this.ecipher = Cipher.getInstance("DES/ECB/NoPadding");
      this.dcipher = Cipher.getInstance("DES/ECB/NoPadding");
      this.ecipher.init(1, this.secretKey);
      this.dcipher.init(2, this.secretKey);
    }
    catch (NoSuchPaddingException localNoSuchPaddingException)
    {
      localNoSuchPaddingException.printStackTrace();
    }
    catch (InvalidKeySpecException localInvalidKeySpecException)
    {
      localInvalidKeySpecException.printStackTrace();
    }
    catch (NoSuchAlgorithmException localNoSuchAlgorithmException)
    {
      localNoSuchAlgorithmException.printStackTrace();
    }
    catch (NoSuchProviderException localNoSuchProviderException)
    {
      localNoSuchProviderException.printStackTrace();
    }
    catch (InvalidKeyException localInvalidKeyException)
    {
      localInvalidKeyException.printStackTrace();
    }
  }
  
  private byte[] encrypt(byte[] paramArrayOfByte)
    throws Exception
  {
    return this.ecipher.doFinal(paramArrayOfByte);
  }
  
  public String encrypt(String paramString)
    throws Exception
  {
    if ((this.desLimit == null) || (!this.desLimit.hasNoLimits())) {
      throw new Exception("Sorry, encrypt is not supported.");
    }
    byte[] arrayOfByte = crypt(paramString.getBytes(), 1);
    return StringEncode.byteArr2HexStr(arrayOfByte);
  }
  
  private byte[] crypt(byte[] paramArrayOfByte, int paramInt)
    throws Exception
  {
    int i = paramArrayOfByte.length;
    int j;
    int k;
    byte[] localObject = null;
    if (i % 8 != 0)
    {
      localObject = new byte[8 * (1 + i / 8)];
      for (j = 0; j < i; j++) {
        localObject[j] = paramArrayOfByte[j];
      }
      for (k = i; k < localObject.length; k++) {
        localObject[k] = 0;
      }
      paramArrayOfByte = (byte[])localObject;
    }
    if (paramInt == 1)
    {
      localObject = encrypt(paramArrayOfByte);
    }
    else if (paramInt == 2)
    {
      localObject = decrypt(paramArrayOfByte);
      j = 0;
      for (k = localObject.length - 1; k >= 0; k--)
      {
        if (localObject[k] != 0) {
          break;
        }
        j++;
      }
      if (j > 0)
      {
        byte[] arrayOfByte = new byte[localObject.length - j];
        for (int m = 0; m < arrayOfByte.length; m++) {
          arrayOfByte[m] = localObject[m];
        }
        localObject = arrayOfByte;
      }
    }
    return (byte[])localObject;
  }
  
  private byte[] decrypt(byte[] paramArrayOfByte)
    throws Exception
  {
    return this.dcipher.doFinal(paramArrayOfByte);
  }
  
  public String decrypt(String paramString)
    throws Exception
  {
    byte[] arrayOfByte = StringEncode.hexStr2ByteArr(paramString);
    return new String(crypt(arrayOfByte, 2));
  }
}
