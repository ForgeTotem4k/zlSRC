package pro.gravit.launcher.runtime.managers;

import java.io.IOException;
import pro.gravit.launcher.base.Launcher;
import pro.gravit.launcher.base.modules.LauncherModule;
import pro.gravit.launcher.client.events.ClientUnlockConsoleEvent;
import pro.gravit.launcher.runtime.LauncherEngine;
import pro.gravit.launcher.runtime.console.GetConnectUUIDCommand;
import pro.gravit.launcher.runtime.console.UnlockCommand;
import pro.gravit.launcher.runtime.console.test.PrintHardwareInfoCommand;
import pro.gravit.utils.command.Command;
import pro.gravit.utils.command.CommandHandler;
import pro.gravit.utils.command.JLineCommandHandler;
import pro.gravit.utils.command.StdCommandHandler;
import pro.gravit.utils.command.basic.ClearCommand;
import pro.gravit.utils.command.basic.DebugCommand;
import pro.gravit.utils.command.basic.GCCommand;
import pro.gravit.utils.command.basic.HelpCommand;
import pro.gravit.utils.helper.CommonHelper;
import pro.gravit.utils.helper.LogHelper;

public class ConsoleManager {
  public static CommandHandler handler;
  
  public static Thread thread;
  
  public static boolean isConsoleUnlock = false;
  
  public static void initConsole() throws IOException {
    StdCommandHandler stdCommandHandler;
    try {
      Class.forName("org.jline.terminal.Terminal");
      JLineCommandHandler jLineCommandHandler = new JLineCommandHandler();
      LogHelper.info("JLine2 terminal enabled");
    } catch (ClassNotFoundException classNotFoundException) {
      stdCommandHandler = new StdCommandHandler(true);
      LogHelper.warning("JLine2 isn't in classpath, using std");
    } 
    handler = (CommandHandler)stdCommandHandler;
    registerCommands();
    thread = CommonHelper.newThread("Launcher Console", true, (Runnable)handler);
    thread.start();
  }
  
  public static void registerCommands() {
    handler.registerCommand("help", (Command)new HelpCommand(handler));
    handler.registerCommand("gc", (Command)new GCCommand());
    handler.registerCommand("clear", (Command)new ClearCommand(handler));
    handler.registerCommand("unlock", (Command)new UnlockCommand());
    handler.registerCommand("printhardware", (Command)new PrintHardwareInfoCommand());
    handler.registerCommand("getconnectuuid", (Command)new GetConnectUUIDCommand());
  }
  
  public static boolean checkUnlockKey(String paramString) {
    return paramString.equals((Launcher.getConfig()).unlockSecret);
  }
  
  public static boolean unlock() {
    if (isConsoleUnlock)
      return true; 
    ClientUnlockConsoleEvent clientUnlockConsoleEvent = new ClientUnlockConsoleEvent(handler);
    LauncherEngine.modulesManager.invokeEvent((LauncherModule.Event)clientUnlockConsoleEvent);
    if (clientUnlockConsoleEvent.isCancel())
      return false; 
    handler.registerCommand("debug", (Command)new DebugCommand());
    handler.unregisterCommand("unlock");
    isConsoleUnlock = true;
    return true;
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\pro\gravit\launcher\runtime\managers\ConsoleManager.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */