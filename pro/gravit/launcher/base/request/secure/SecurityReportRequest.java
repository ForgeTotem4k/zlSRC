package pro.gravit.launcher.base.request.secure;

import pro.gravit.launcher.base.events.request.SecurityReportRequestEvent;
import pro.gravit.launcher.base.request.Request;

public final class SecurityReportRequest extends Request<SecurityReportRequestEvent> {
  public final String reportType;
  
  public final String smallData;
  
  public final String largeData;
  
  public final byte[] smallBytes;
  
  public final byte[] largeBytes;
  
  public SecurityReportRequest(String paramString1, String paramString2, String paramString3, byte[] paramArrayOfbyte1, byte[] paramArrayOfbyte2) {
    this.reportType = paramString1;
    this.smallData = paramString2;
    this.largeData = paramString3;
    this.smallBytes = paramArrayOfbyte1;
    this.largeBytes = paramArrayOfbyte2;
  }
  
  public SecurityReportRequest(String paramString1, String paramString2, String paramString3, byte[] paramArrayOfbyte) {
    this.reportType = paramString1;
    this.smallData = paramString2;
    this.largeData = paramString3;
    this.smallBytes = paramArrayOfbyte;
    this.largeBytes = null;
  }
  
  public SecurityReportRequest(String paramString1, String paramString2, String paramString3) {
    this.reportType = paramString1;
    this.smallData = paramString2;
    this.largeData = paramString3;
    this.smallBytes = null;
    this.largeBytes = null;
  }
  
  public SecurityReportRequest(String paramString1, String paramString2, byte[] paramArrayOfbyte) {
    this.reportType = paramString1;
    this.smallData = paramString2;
    this.largeData = null;
    this.smallBytes = paramArrayOfbyte;
    this.largeBytes = null;
  }
  
  public SecurityReportRequest(String paramString, byte[] paramArrayOfbyte1, byte[] paramArrayOfbyte2) {
    this.reportType = paramString;
    this.smallData = null;
    this.largeData = null;
    this.smallBytes = paramArrayOfbyte1;
    this.largeBytes = paramArrayOfbyte2;
  }
  
  public SecurityReportRequest(String paramString, byte[] paramArrayOfbyte) {
    this.reportType = paramString;
    this.smallData = null;
    this.largeData = null;
    this.smallBytes = paramArrayOfbyte;
    this.largeBytes = null;
  }
  
  public SecurityReportRequest(String paramString1, String paramString2) {
    this.reportType = paramString1;
    this.smallData = paramString2;
    this.largeData = null;
    this.smallBytes = null;
    this.largeBytes = null;
  }
  
  public SecurityReportRequest(String paramString) {
    this.reportType = paramString;
    this.smallData = null;
    this.largeData = null;
    this.smallBytes = null;
    this.largeBytes = null;
  }
  
  public String getType() {
    return "securityReport";
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\pro\gravit\launcher\base\request\secure\SecurityReportRequest.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */