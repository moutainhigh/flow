package com.aspire.common.encryption;

public class DESLimit
{
  private byte cert = 84;
  private byte tag1 = 0;
  private byte tag2 = 0;
  
  public DESLimit(byte paramByte1, byte paramByte2)
  {
    this.tag1 = paramByte1;
    this.tag2 = paramByte2;
  }
  
  public boolean hasNoLimits()
  {
    return (this.tag1 ^ this.cert) == this.tag2;
  }
}
