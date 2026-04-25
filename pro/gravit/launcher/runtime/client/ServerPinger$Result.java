package pro.gravit.launcher.runtime.client;

import pro.gravit.utils.helper.VerifyHelper;

public final class Result {
  public final int onlinePlayers;
  
  public final int maxPlayers;
  
  public final String raw;
  
  public Result(int paramInt1, int paramInt2, String paramString) {
    this.onlinePlayers = VerifyHelper.verifyInt(paramInt1, VerifyHelper.NOT_NEGATIVE, "onlinePlayers can't be < 0");
    this.maxPlayers = VerifyHelper.verifyInt(paramInt2, VerifyHelper.NOT_NEGATIVE, "maxPlayers can't be < 0");
    this.raw = paramString;
  }
  
  public boolean isOverfilled() {
    return (this.onlinePlayers >= this.maxPlayers);
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\pro\gravit\launcher\runtime\client\ServerPinger$Result.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */