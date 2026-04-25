package pro.gravit.launcher.base.events.request;

import pro.gravit.launcher.base.events.RequestEvent;

public class SecurityReportRequestEvent extends RequestEvent {
  public final ReportAction action;
  
  public final String otherAction;
  
  public SecurityReportRequestEvent(ReportAction paramReportAction) {
    this.action = paramReportAction;
    this.otherAction = null;
  }
  
  public SecurityReportRequestEvent(String paramString) {
    this.action = ReportAction.OTHER;
    this.otherAction = paramString;
  }
  
  public SecurityReportRequestEvent() {
    this.action = ReportAction.NONE;
    this.otherAction = null;
  }
  
  public String getType() {
    return "securityReport";
  }
  
  public enum ReportAction {
    NONE, LOGOUT, TOKEN_EXPIRED, EXIT, CRASH, OTHER;
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\pro\gravit\launcher\base\events\request\SecurityReportRequestEvent.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */