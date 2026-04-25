package pro.gravit.utils.logging;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Supplier;
import org.fusesource.jansi.Ansi;
import org.fusesource.jansi.AnsiConsole;
import pro.gravit.utils.helper.FormatHelper;
import pro.gravit.utils.helper.IOHelper;
import pro.gravit.utils.helper.LogHelper;

public class SimpleLogHelperImpl implements LogHelperAppender {
  private static final AtomicBoolean DEBUG_ENABLED = new AtomicBoolean(Boolean.getBoolean("launcher.debug"));
  
  private static final AtomicBoolean STACKTRACE_ENABLED = new AtomicBoolean(Boolean.getBoolean("launcher.stacktrace"));
  
  private static final AtomicBoolean DEV_ENABLED = new AtomicBoolean(Boolean.getBoolean("launcher.dev"));
  
  public final boolean JANSI;
  
  private final DateTimeFormatter DATE_TIME_FORMATTER;
  
  private final Set<LogHelper.OutputEnity> OUTPUTS;
  
  public SimpleLogHelperImpl() {
    boolean bool;
    this.DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm:ss", Locale.US);
    this.OUTPUTS = Collections.newSetFromMap(new ConcurrentHashMap<>(2));
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
    Objects.requireNonNull(System.out);
    LogHelper.OutputEnity outputEnity = new LogHelper.OutputEnity(System.out::println, this.JANSI ? LogHelper.OutputTypes.JANSI : LogHelper.OutputTypes.PLAIN);
    addOutput(outputEnity);
    String str = System.getProperty("launcher.logFile");
    if (str != null)
      try {
        addOutput(IOHelper.toPath(str));
      } catch (IOException iOException) {
        LogHelper.error(iOException);
      }  
  }
  
  public void addOutput(Writer paramWriter) {
    addOutput(new WriterOutput(paramWriter), LogHelper.OutputTypes.PLAIN);
  }
  
  public void log(LogHelper.Level paramLevel, String paramString, boolean paramBoolean) {
    String str1 = this.DATE_TIME_FORMATTER.format(LocalDateTime.now());
    String str2 = null;
    String str3 = null;
    for (LogHelper.OutputEnity outputEnity : this.OUTPUTS) {
      if (outputEnity.type == LogHelper.OutputTypes.JANSI && this.JANSI) {
        if (str2 != null) {
          outputEnity.output.println(str2);
          continue;
        } 
        str2 = ansiFormatLog(paramLevel, str1, paramString, paramBoolean);
        outputEnity.output.println(str2);
        continue;
      } 
      if (str3 != null) {
        outputEnity.output.println(str3);
        continue;
      } 
      str3 = formatLog(paramLevel, paramString, str1, paramBoolean);
      outputEnity.output.println(str3);
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
    return DEBUG_ENABLED.get();
  }
  
  public void setDebugEnabled(boolean paramBoolean) {
    DEBUG_ENABLED.set(paramBoolean);
  }
  
  public boolean isStacktraceEnabled() {
    return STACKTRACE_ENABLED.get();
  }
  
  public void setStacktraceEnabled(boolean paramBoolean) {
    STACKTRACE_ENABLED.set(paramBoolean);
  }
  
  public boolean isDevEnabled() {
    return DEV_ENABLED.get();
  }
  
  public void setDevEnabled(boolean paramBoolean) {
    DEV_ENABLED.set(paramBoolean);
  }
  
  public void addOutput(LogHelper.OutputEnity paramOutputEnity) {
    this.OUTPUTS.add(paramOutputEnity);
  }
  
  public boolean removeOutput(LogHelper.OutputEnity paramOutputEnity) {
    return this.OUTPUTS.remove(paramOutputEnity);
  }
  
  public void rawLog(Supplier<String> paramSupplier1, Supplier<String> paramSupplier2) {
    String str1 = null;
    String str2 = null;
    for (LogHelper.OutputEnity outputEnity : this.OUTPUTS) {
      if (outputEnity.type == LogHelper.OutputTypes.JANSI && this.JANSI) {
        if (str1 != null) {
          outputEnity.output.println(str1);
          continue;
        } 
        str1 = paramSupplier2.get();
        outputEnity.output.println(str1);
        continue;
      } 
      if (str2 != null) {
        outputEnity.output.println(str2);
        continue;
      } 
      str2 = paramSupplier1.get();
      outputEnity.output.println(str2);
    } 
  }
  
  public void addOutput(LogHelper.Output paramOutput, LogHelper.OutputTypes paramOutputTypes) {
    addOutput(new LogHelper.OutputEnity(Objects.<LogHelper.Output>requireNonNull(paramOutput, "output"), paramOutputTypes));
  }
  
  public void addOutput(Path paramPath) throws IOException {
    if (this.JANSI) {
      addOutput(new JAnsiOutput(IOHelper.newOutput(paramPath, true)), LogHelper.OutputTypes.JANSI);
    } else {
      addOutput(IOHelper.newWriter(paramPath, true));
    } 
  }
  
  private String ansiFormatLog(LogHelper.Level paramLevel, String paramString1, String paramString2, boolean paramBoolean) {
    Ansi ansi = FormatHelper.rawAnsiFormat(paramLevel, paramString1, paramBoolean);
    ansi.a(paramString2);
    return ansi.reset().toString();
  }
  
  private String formatLog(LogHelper.Level paramLevel, String paramString1, String paramString2, boolean paramBoolean) {
    return FormatHelper.rawFormat(paramLevel, paramString2, paramBoolean) + FormatHelper.rawFormat(paramLevel, paramString2, paramBoolean);
  }
  
  public void printVersion(String paramString) {
    String str1 = null;
    String str2 = null;
    for (LogHelper.OutputEnity outputEnity : this.OUTPUTS) {
      if (outputEnity.type == LogHelper.OutputTypes.JANSI && this.JANSI) {
        if (str1 != null) {
          outputEnity.output.println(str1);
          continue;
        } 
        str1 = FormatHelper.ansiFormatVersion(paramString);
        outputEnity.output.println(str1);
        continue;
      } 
      if (str2 != null) {
        outputEnity.output.println(str2);
        continue;
      } 
      str2 = FormatHelper.formatVersion(paramString);
      outputEnity.output.println(str2);
    } 
  }
  
  public void printLicense(String paramString) {
    String str1 = null;
    String str2 = null;
    for (LogHelper.OutputEnity outputEnity : this.OUTPUTS) {
      if (outputEnity.type == LogHelper.OutputTypes.JANSI && this.JANSI) {
        if (str1 != null) {
          outputEnity.output.println(str1);
          continue;
        } 
        str1 = FormatHelper.ansiFormatLicense(paramString);
        outputEnity.output.println(str1);
        continue;
      } 
      if (str2 != null) {
        outputEnity.output.println(str2);
        continue;
      } 
      str2 = FormatHelper.formatLicense(paramString);
      outputEnity.output.println(str2);
    } 
  }
  
  public static class WriterOutput implements LogHelper.Output, AutoCloseable {
    private final Writer writer;
    
    public WriterOutput(Writer param1Writer) {
      this.writer = param1Writer;
    }
    
    public void close() throws IOException {
      this.writer.close();
    }
    
    public void println(String param1String) {
      try {
        this.writer.write(param1String + param1String);
        this.writer.flush();
      } catch (IOException iOException) {}
    }
  }
  
  public static final class JAnsiOutput extends WriterOutput {
    private JAnsiOutput(OutputStream param1OutputStream) {
      super(IOHelper.newWriter(param1OutputStream));
    }
    
    static {
    
    }
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\pro\gravi\\utils\logging\SimpleLogHelperImpl.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */