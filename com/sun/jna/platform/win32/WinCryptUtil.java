package com.sun.jna.platform.win32;

import com.sun.jna.Memory;
import com.sun.jna.Native;
import com.sun.jna.Pointer;

public abstract class WinCryptUtil {
  public static class MANAGED_CRYPT_SIGN_MESSAGE_PARA extends WinCrypt.CRYPT_SIGN_MESSAGE_PARA {
    private WinCrypt.CERT_CONTEXT[] rgpMsgCerts;
    
    private WinCrypt.CRL_CONTEXT[] rgpMsgCrls;
    
    private WinCrypt.CRYPT_ATTRIBUTE[] rgAuthAttrs;
    
    private WinCrypt.CRYPT_ATTRIBUTE[] rgUnauthAttrs;
    
    public void setRgpMsgCert(WinCrypt.CERT_CONTEXT[] param1ArrayOfCERT_CONTEXT) {
      this.rgpMsgCerts = param1ArrayOfCERT_CONTEXT;
      if (param1ArrayOfCERT_CONTEXT == null || param1ArrayOfCERT_CONTEXT.length == 0) {
        this.rgpMsgCert = null;
        this.cMsgCert = 0;
      } else {
        this.cMsgCert = param1ArrayOfCERT_CONTEXT.length;
        Memory memory = new Memory((Native.POINTER_SIZE * param1ArrayOfCERT_CONTEXT.length));
        for (byte b = 0; b < param1ArrayOfCERT_CONTEXT.length; b++)
          memory.setPointer((b * Native.POINTER_SIZE), param1ArrayOfCERT_CONTEXT[b].getPointer()); 
        this.rgpMsgCert = (Pointer)memory;
      } 
    }
    
    public WinCrypt.CERT_CONTEXT[] getRgpMsgCert() {
      return this.rgpMsgCerts;
    }
    
    public void setRgpMsgCrl(WinCrypt.CRL_CONTEXT[] param1ArrayOfCRL_CONTEXT) {
      this.rgpMsgCrls = param1ArrayOfCRL_CONTEXT;
      if (param1ArrayOfCRL_CONTEXT == null || param1ArrayOfCRL_CONTEXT.length == 0) {
        this.rgpMsgCert = null;
        this.cMsgCert = 0;
      } else {
        this.cMsgCert = param1ArrayOfCRL_CONTEXT.length;
        Memory memory = new Memory((Native.POINTER_SIZE * param1ArrayOfCRL_CONTEXT.length));
        for (byte b = 0; b < param1ArrayOfCRL_CONTEXT.length; b++)
          memory.setPointer((b * Native.POINTER_SIZE), param1ArrayOfCRL_CONTEXT[b].getPointer()); 
        this.rgpMsgCert = (Pointer)memory;
      } 
    }
    
    public WinCrypt.CRL_CONTEXT[] getRgpMsgCrl() {
      return this.rgpMsgCrls;
    }
    
    public void setRgAuthAttr(WinCrypt.CRYPT_ATTRIBUTE[] param1ArrayOfCRYPT_ATTRIBUTE) {
      this.rgAuthAttrs = param1ArrayOfCRYPT_ATTRIBUTE;
      if (param1ArrayOfCRYPT_ATTRIBUTE == null || param1ArrayOfCRYPT_ATTRIBUTE.length == 0) {
        this.rgAuthAttr = null;
        this.cMsgCert = 0;
      } else {
        this.cMsgCert = this.rgpMsgCerts.length;
        this.rgAuthAttr = param1ArrayOfCRYPT_ATTRIBUTE[0].getPointer();
      } 
    }
    
    public WinCrypt.CRYPT_ATTRIBUTE[] getRgAuthAttr() {
      return this.rgAuthAttrs;
    }
    
    public void setRgUnauthAttr(WinCrypt.CRYPT_ATTRIBUTE[] param1ArrayOfCRYPT_ATTRIBUTE) {
      this.rgUnauthAttrs = param1ArrayOfCRYPT_ATTRIBUTE;
      if (param1ArrayOfCRYPT_ATTRIBUTE == null || param1ArrayOfCRYPT_ATTRIBUTE.length == 0) {
        this.rgUnauthAttr = null;
        this.cMsgCert = 0;
      } else {
        this.cMsgCert = this.rgpMsgCerts.length;
        this.rgUnauthAttr = param1ArrayOfCRYPT_ATTRIBUTE[0].getPointer();
      } 
    }
    
    public WinCrypt.CRYPT_ATTRIBUTE[] getRgUnauthAttr() {
      return this.rgUnauthAttrs;
    }
    
    public void write() {
      if (this.rgpMsgCerts != null)
        for (WinCrypt.CERT_CONTEXT cERT_CONTEXT : this.rgpMsgCerts)
          cERT_CONTEXT.write();  
      if (this.rgpMsgCrls != null)
        for (WinCrypt.CRL_CONTEXT cRL_CONTEXT : this.rgpMsgCrls)
          cRL_CONTEXT.write();  
      if (this.rgAuthAttrs != null)
        for (WinCrypt.CRYPT_ATTRIBUTE cRYPT_ATTRIBUTE : this.rgAuthAttrs)
          cRYPT_ATTRIBUTE.write();  
      if (this.rgUnauthAttrs != null)
        for (WinCrypt.CRYPT_ATTRIBUTE cRYPT_ATTRIBUTE : this.rgUnauthAttrs)
          cRYPT_ATTRIBUTE.write();  
      this.cbSize = size();
      super.write();
    }
    
    public void read() {
      if (this.rgpMsgCerts != null)
        for (WinCrypt.CERT_CONTEXT cERT_CONTEXT : this.rgpMsgCerts)
          cERT_CONTEXT.read();  
      if (this.rgpMsgCrls != null)
        for (WinCrypt.CRL_CONTEXT cRL_CONTEXT : this.rgpMsgCrls)
          cRL_CONTEXT.read();  
      if (this.rgAuthAttrs != null)
        for (WinCrypt.CRYPT_ATTRIBUTE cRYPT_ATTRIBUTE : this.rgAuthAttrs)
          cRYPT_ATTRIBUTE.read();  
      if (this.rgUnauthAttrs != null)
        for (WinCrypt.CRYPT_ATTRIBUTE cRYPT_ATTRIBUTE : this.rgUnauthAttrs)
          cRYPT_ATTRIBUTE.read();  
      super.read();
    }
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\com\sun\jna\platform\win32\WinCryptUtil.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */