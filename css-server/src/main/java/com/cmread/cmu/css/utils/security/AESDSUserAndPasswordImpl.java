package com.cmread.cmu.css.utils.security;



public class AESDSUserAndPasswordImpl
{
  private String algorithm = "AES";

  private EncryptTool tool = new EncryptTool();

  public String getEncrypt(String original)
  {
    String encoded = null;
    try
    {
      encoded = this.tool.encrypt(original, this.algorithm);
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }

    return encoded;
  }

  public String getDecrypt(String cryptograph)
  {
    String decode = null;
    try
    {
      decode = this.tool.parseEncrypt(cryptograph, this.algorithm);
    }
    catch (Exception ex)
    {
      ex.printStackTrace();
    }

    return decode;
  }
  public static void main(String[] args) {
	System.out.println(new AESDSUserAndPasswordImpl().getEncrypt("whYH_10"));
}
}