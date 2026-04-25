package pro.gravit.launcher.gui.service;

public class PingServerReport {
  public final String name;
  
  public final int maxPlayers;
  
  public final int playersOnline;
  
  public PingServerReport(String paramString, int paramInt1, int paramInt2) {
    this.name = paramString;
    this.maxPlayers = paramInt1;
    this.playersOnline = paramInt2;
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\pro\gravit\launcher\gui\service\PingService$PingServerReport.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */