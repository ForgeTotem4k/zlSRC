package pro.gravit.launcher.base.profiles;

import java.net.InetSocketAddress;

public class ServerProfile {
  public String name;
  
  public String serverAddress;
  
  public int serverPort;
  
  public boolean isDefault = true;
  
  public int protocol = -1;
  
  public boolean socketPing = true;
  
  public ServerProfile() {}
  
  public ServerProfile(String paramString1, String paramString2, int paramInt) {
    this.name = paramString1;
    this.serverAddress = paramString2;
    this.serverPort = paramInt;
  }
  
  public ServerProfile(String paramString1, String paramString2, int paramInt, boolean paramBoolean) {
    this.name = paramString1;
    this.serverAddress = paramString2;
    this.serverPort = paramInt;
    this.isDefault = paramBoolean;
  }
  
  public InetSocketAddress toSocketAddress() {
    return InetSocketAddress.createUnresolved(this.serverAddress, this.serverPort);
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\pro\gravit\launcher\base\profiles\ClientProfile$ServerProfile.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */