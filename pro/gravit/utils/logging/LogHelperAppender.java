package pro.gravit.utils.logging;

import java.util.function.Supplier;
import pro.gravit.utils.helper.LogHelper;

public interface LogHelperAppender {
  void log(LogHelper.Level paramLevel, String paramString, boolean paramBoolean);
  
  void logJAnsi(LogHelper.Level paramLevel, Supplier<String> paramSupplier1, Supplier<String> paramSupplier2, boolean paramBoolean);
  
  boolean isDebugEnabled();
  
  void setDebugEnabled(boolean paramBoolean);
  
  boolean isStacktraceEnabled();
  
  void setStacktraceEnabled(boolean paramBoolean);
  
  boolean isDevEnabled();
  
  void setDevEnabled(boolean paramBoolean);
  
  void addOutput(LogHelper.OutputEnity paramOutputEnity);
  
  boolean removeOutput(LogHelper.OutputEnity paramOutputEnity);
  
  void printVersion(String paramString);
  
  void printLicense(String paramString);
  
  static {
  
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\pro\gravi\\utils\logging\LogHelperAppender.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */