package oshi.software.os;

import oshi.annotation.concurrent.Immutable;

@Immutable
public class OSService {
  private final String name;
  
  private final int processID;
  
  private final State state;
  
  public OSService(String paramString, int paramInt, State paramState) {
    this.name = paramString;
    this.processID = paramInt;
    this.state = paramState;
  }
  
  public String getName() {
    return this.name;
  }
  
  public int getProcessID() {
    return this.processID;
  }
  
  public State getState() {
    return this.state;
  }
  
  public enum State {
    RUNNING, STOPPED, OTHER;
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\oshi\software\os\OSService.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */