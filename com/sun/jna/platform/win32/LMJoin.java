package com.sun.jna.platform.win32;

public interface LMJoin {
  public static abstract class NETSETUP_JOIN_STATUS {
    public static final int NetSetupUnknownStatus = 0;
    
    public static final int NetSetupUnjoined = 1;
    
    public static final int NetSetupWorkgroupName = 2;
    
    public static final int NetSetupDomainName = 3;
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\com\sun\jna\platform\win32\LMJoin.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */