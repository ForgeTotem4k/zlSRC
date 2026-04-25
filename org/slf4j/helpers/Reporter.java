package org.slf4j.helpers;

import java.io.PrintStream;

public class Reporter {
  static final String SLF4J_DEBUG_PREFIX = "SLF4J(D): ";
  
  static final String SLF4J_INFO_PREFIX = "SLF4J(I): ";
  
  static final String SLF4J_WARN_PREFIX = "SLF4J(W): ";
  
  static final String SLF4J_ERROR_PREFIX = "SLF4J(E): ";
  
  public static final String SLF4J_INTERNAL_REPORT_STREAM_KEY = "slf4j.internal.report.stream";
  
  private static final String[] SYSOUT_KEYS = new String[] { "System.out", "stdout", "sysout" };
  
  public static final String SLF4J_INTERNAL_VERBOSITY_KEY = "slf4j.internal.verbosity";
  
  private static final TargetChoice TARGET_CHOICE = getTargetChoice();
  
  private static final Level INTERNAL_VERBOSITY = initVerbosity();
  
  private static TargetChoice getTargetChoice() {
    String str = System.getProperty("slf4j.internal.report.stream");
    if (str == null || str.isEmpty())
      return TargetChoice.Stderr; 
    for (String str1 : SYSOUT_KEYS) {
      if (str1.equalsIgnoreCase(str))
        return TargetChoice.Stdout; 
    } 
    return TargetChoice.Stderr;
  }
  
  private static Level initVerbosity() {
    String str = System.getProperty("slf4j.internal.verbosity");
    return (str == null || str.isEmpty()) ? Level.INFO : (str.equalsIgnoreCase("DEBUG") ? Level.DEBUG : (str.equalsIgnoreCase("ERROR") ? Level.ERROR : (str.equalsIgnoreCase("WARN") ? Level.WARN : Level.INFO)));
  }
  
  static boolean isEnabledFor(Level paramLevel) {
    return (paramLevel.levelInt >= INTERNAL_VERBOSITY.levelInt);
  }
  
  private static PrintStream getTarget() {
    switch (TARGET_CHOICE.ordinal()) {
      case 1:
        return System.out;
    } 
    return System.err;
  }
  
  public static void debug(String paramString) {
    if (isEnabledFor(Level.DEBUG))
      getTarget().println("SLF4J(D): " + paramString); 
  }
  
  public static void info(String paramString) {
    if (isEnabledFor(Level.INFO))
      getTarget().println("SLF4J(I): " + paramString); 
  }
  
  public static final void warn(String paramString) {
    if (isEnabledFor(Level.WARN))
      getTarget().println("SLF4J(W): " + paramString); 
  }
  
  public static final void error(String paramString, Throwable paramThrowable) {
    getTarget().println("SLF4J(E): " + paramString);
    getTarget().println("SLF4J(E): Reported exception:");
    paramThrowable.printStackTrace(getTarget());
  }
  
  public static final void error(String paramString) {
    getTarget().println("SLF4J(E): " + paramString);
  }
  
  private enum TargetChoice {
    Stderr, Stdout;
  }
  
  private enum Level {
    DEBUG(0),
    INFO(1),
    WARN(2),
    ERROR(3);
    
    int levelInt;
    
    Level(int param1Int1) {
      this.levelInt = param1Int1;
    }
    
    private int getLevelInt() {
      return this.levelInt;
    }
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\org\slf4j\helpers\Reporter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */