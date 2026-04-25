package pro.gravit.utils.logging;

import java.util.function.Supplier;
import org.fusesource.jansi.AnsiConsole;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pro.gravit.utils.helper.FormatHelper;
import pro.gravit.utils.helper.LogHelper;

public class Slf4jLogHelperImpl implements LogHelperAppender {
  private final Logger logger;
  
  private final boolean JANSI;
  
  public Slf4jLogHelperImpl() {
    boolean bool;
    this.logger = LoggerFactory.getLogger("LogHelper");
    try {
      if (Boolean.getBoolean("launcher.noJAnsi")) {
        bool = false;
      } else {
        Class.forName("org.fusesource.jansi.Ansi");
        AnsiConsole.systemInstall();
        bool = true;
      } 
    } catch (ClassNotFoundException classNotFoundException) {
      bool = false;
    } 
    this.JANSI = bool;
  }
  
  public void log(LogHelper.Level paramLevel, String paramString, boolean paramBoolean) {
    switch (paramLevel) {
      case DEV:
        this.logger.trace(paramString);
        break;
      case DEBUG:
        this.logger.debug(paramString);
        break;
      case INFO:
        this.logger.info(paramString);
        break;
      case WARNING:
        this.logger.warn(paramString);
        break;
      case ERROR:
        this.logger.error(paramString);
        break;
    } 
  }
  
  public void logJAnsi(LogHelper.Level paramLevel, Supplier<String> paramSupplier1, Supplier<String> paramSupplier2, boolean paramBoolean) {
    if (this.JANSI) {
      log(paramLevel, paramSupplier2.get(), paramBoolean);
    } else {
      log(paramLevel, paramSupplier1.get(), paramBoolean);
    } 
  }
  
  public boolean isDebugEnabled() {
    return true;
  }
  
  public void setDebugEnabled(boolean paramBoolean) {}
  
  public boolean isStacktraceEnabled() {
    return true;
  }
  
  public void setStacktraceEnabled(boolean paramBoolean) {}
  
  public boolean isDevEnabled() {
    return true;
  }
  
  public void setDevEnabled(boolean paramBoolean) {}
  
  public void addOutput(LogHelper.OutputEnity paramOutputEnity) {}
  
  public boolean removeOutput(LogHelper.OutputEnity paramOutputEnity) {
    return false;
  }
  
  public void printVersion(String paramString) {
    if (this.JANSI) {
      this.logger.info(FormatHelper.ansiFormatVersion(paramString));
    } else {
      this.logger.info(FormatHelper.formatVersion(paramString));
    } 
  }
  
  public void printLicense(String paramString) {
    if (this.JANSI) {
      this.logger.info(FormatHelper.ansiFormatLicense(paramString));
    } else {
      this.logger.info(FormatHelper.formatLicense(paramString));
    } 
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\pro\gravi\\utils\logging\Slf4jLogHelperImpl.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */