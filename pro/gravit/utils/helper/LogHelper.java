package pro.gravit.utils.helper;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.file.Path;
import java.util.Collections;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.function.Supplier;
import pro.gravit.launcher.core.LauncherNetworkAPI;
import pro.gravit.utils.logging.LogHelperAppender;
import pro.gravit.utils.logging.SimpleLogHelperImpl;
import pro.gravit.utils.logging.Slf4jLogHelperImpl;

public final class LogHelper {
  public static final String DEBUG_PROPERTY = "launcher.debug";
  
  public static final String DEV_PROPERTY = "launcher.dev";
  
  public static final String STACKTRACE_PROPERTY = "launcher.stacktrace";
  
  public static final String NO_JANSI_PROPERTY = "launcher.noJAnsi";
  
  public static final String SLF4J_PROPERTY = "launcher.useSlf4j";
  
  private static final Set<Consumer<Throwable>> EXCEPTIONS_CALLBACKS = Collections.newSetFromMap(new ConcurrentHashMap<>(2));
  
  private static final LogHelperAppender impl;
  
  public static void addOutput(OutputEnity paramOutputEnity) {
    impl.addOutput(paramOutputEnity);
  }
  
  public static void addExcCallback(Consumer<Throwable> paramConsumer) {
    EXCEPTIONS_CALLBACKS.add(Objects.<Consumer<Throwable>>requireNonNull(paramConsumer, "output"));
  }
  
  public static void addOutput(Output paramOutput, OutputTypes paramOutputTypes) {
    addOutput(new OutputEnity(Objects.<Output>requireNonNull(paramOutput, "output"), paramOutputTypes));
  }
  
  public static void addOutput(Path paramPath) throws IOException {
    addOutput(IOHelper.newWriter(paramPath, true));
  }
  
  public static void addOutput(Writer paramWriter) {
    addOutput((Output)new SimpleLogHelperImpl.WriterOutput(paramWriter), OutputTypes.PLAIN);
  }
  
  public static void debug(String paramString) {
    if (isDebugEnabled())
      log(Level.DEBUG, paramString, false); 
  }
  
  public static void dev(String paramString) {
    if (isDevEnabled())
      log(Level.DEV, paramString, false); 
  }
  
  public static void debug(String paramString, Object... paramVarArgs) {
    debug(String.format(paramString, paramVarArgs));
  }
  
  public static void dev(String paramString, Object... paramVarArgs) {
    if (isDevEnabled())
      dev(String.format(paramString, paramVarArgs)); 
  }
  
  public static void error(Throwable paramThrowable) {
    EXCEPTIONS_CALLBACKS.forEach(paramConsumer -> paramConsumer.accept(paramThrowable));
    error(isStacktraceEnabled() ? toString(paramThrowable) : paramThrowable.toString());
  }
  
  public static void error(String paramString) {
    log(Level.ERROR, paramString, false);
  }
  
  public static void error(String paramString, Object... paramVarArgs) {
    error(String.format(paramString, paramVarArgs));
  }
  
  public static void info(String paramString) {
    log(Level.INFO, paramString, false);
  }
  
  public static void info(String paramString, Object... paramVarArgs) {
    info(String.format(paramString, paramVarArgs));
  }
  
  public static boolean isDebugEnabled() {
    return impl.isDebugEnabled();
  }
  
  public static void setDebugEnabled(boolean paramBoolean) {
    impl.setDebugEnabled(paramBoolean);
  }
  
  public static boolean isStacktraceEnabled() {
    return impl.isStacktraceEnabled();
  }
  
  public static void setStacktraceEnabled(boolean paramBoolean) {
    impl.setStacktraceEnabled(paramBoolean);
  }
  
  public static boolean isDevEnabled() {
    return impl.isDevEnabled();
  }
  
  public static void setDevEnabled(boolean paramBoolean) {
    impl.setDevEnabled(paramBoolean);
  }
  
  public static void log(Level paramLevel, String paramString, boolean paramBoolean) {
    impl.log(paramLevel, paramString, paramBoolean);
  }
  
  public static void logJAnsi(Level paramLevel, Supplier<String> paramSupplier1, Supplier<String> paramSupplier2, boolean paramBoolean) {
    impl.logJAnsi(paramLevel, paramSupplier1, paramSupplier2, paramBoolean);
  }
  
  public static void printVersion(String paramString) {
    impl.printVersion(paramString);
  }
  
  public static void printLicense(String paramString) {
    impl.printLicense(paramString);
  }
  
  public static boolean removeOutput(OutputEnity paramOutputEnity) {
    return impl.removeOutput(paramOutputEnity);
  }
  
  public static void subDebug(String paramString) {
    if (isDebugEnabled())
      log(Level.DEBUG, paramString, true); 
  }
  
  public static void subDebug(String paramString, Object... paramVarArgs) {
    subDebug(String.format(paramString, paramVarArgs));
  }
  
  public static void subInfo(String paramString) {
    log(Level.INFO, paramString, true);
  }
  
  public static void subInfo(String paramString, Object... paramVarArgs) {
    subInfo(String.format(paramString, paramVarArgs));
  }
  
  public static void subWarning(String paramString) {
    log(Level.WARNING, paramString, true);
  }
  
  public static void subWarning(String paramString, Object... paramVarArgs) {
    subWarning(String.format(paramString, paramVarArgs));
  }
  
  public static String toString(Throwable paramThrowable) {
    StringWriter stringWriter = new StringWriter();
    paramThrowable.printStackTrace(new PrintWriter(stringWriter));
    return stringWriter.toString();
  }
  
  public static void warning(String paramString) {
    log(Level.WARNING, paramString, false);
  }
  
  public static void warning(String paramString, Object... paramVarArgs) {
    warning(String.format(paramString, paramVarArgs));
  }
  
  static {
    boolean bool = false;
    try {
      Class.forName("org.slf4j.Logger", false, LogHelper.class.getClassLoader());
      bool = Boolean.getBoolean("launcher.useSlf4j");
    } catch (ClassNotFoundException classNotFoundException) {}
    if (bool) {
      impl = (LogHelperAppender)new Slf4jLogHelperImpl();
    } else {
      impl = (LogHelperAppender)new SimpleLogHelperImpl();
    } 
  }
  
  public static class OutputEnity {
    public final LogHelper.Output output;
    
    public final LogHelper.OutputTypes type;
    
    public OutputEnity(LogHelper.Output param1Output, LogHelper.OutputTypes param1OutputTypes) {
      this.output = param1Output;
      this.type = param1OutputTypes;
    }
  }
  
  @FunctionalInterface
  public static interface Output {
    void println(String param1String);
    
    static {
    
    }
  }
  
  public enum OutputTypes {
    PLAIN, JANSI;
  }
  
  public enum Level {
    DEV("DEV"),
    DEBUG("DEBUG"),
    INFO("INFO"),
    WARNING("WARN"),
    ERROR("ERROR");
    
    public final String name;
    
    Level(String param1String1) {
      this.name = param1String1;
    }
    
    public String toString() {
      return this.name;
    }
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\pro\gravi\\utils\helper\LogHelper.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */