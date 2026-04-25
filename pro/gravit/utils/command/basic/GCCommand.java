package pro.gravit.utils.command.basic;

import pro.gravit.utils.command.Command;
import pro.gravit.utils.helper.JVMHelper;
import pro.gravit.utils.helper.LogHelper;

public class GCCommand extends Command {
  public String getArgsDescription() {
    return null;
  }
  
  public String getUsageDescription() {
    return null;
  }
  
  public void invoke(String... paramVarArgs) {
    LogHelper.subInfo("Performing full GC");
    JVMHelper.fullGC();
    long l1 = JVMHelper.RUNTIME.maxMemory() >> 20L;
    long l2 = JVMHelper.RUNTIME.freeMemory() >> 20L;
    long l3 = JVMHelper.RUNTIME.totalMemory() >> 20L;
    long l4 = l3 - l2;
    LogHelper.subInfo("Heap usage: %d / %d / %d MiB", new Object[] { Long.valueOf(l4), Long.valueOf(l3), Long.valueOf(l1) });
  }
  
  static {
  
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\pro\gravi\\utils\command\basic\GCCommand.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */