package pro.gravit.launcher.client.events;

import pro.gravit.launcher.base.modules.LauncherModule;
import pro.gravit.utils.command.CommandHandler;

public class ClientUnlockConsoleEvent extends LauncherModule.Event {
  public final CommandHandler handler;
  
  public ClientUnlockConsoleEvent(CommandHandler paramCommandHandler) {
    this.handler = paramCommandHandler;
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\pro\gravit\launcher\client\events\ClientUnlockConsoleEvent.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */