package pro.gravit.utils.helper;

import org.fusesource.jansi.Ansi;
import pro.gravit.utils.Version;

public class FormatHelper {
  public static Ansi rawAnsiFormat(LogHelper.Level paramLevel, String paramString, boolean paramBoolean) {
    boolean bool = (paramLevel != LogHelper.Level.DEBUG) ? true : false;
    switch (paramLevel) {
      case WARNING:
      
      case ERROR:
      
      default:
        break;
    } 
    Ansi.Color color = Ansi.Color.WHITE;
    Ansi ansi = new Ansi();
    ansi.fg(Ansi.Color.WHITE).a(paramString);
    ansi.fgBright(Ansi.Color.WHITE).a(" [").bold();
    if (bool) {
      ansi.fgBright(color);
    } else {
      ansi.fg(color);
    } 
    ansi.a(paramLevel).boldOff().fgBright(Ansi.Color.WHITE).a("] ");
    if (bool) {
      ansi.fgBright(color);
    } else {
      ansi.fg(color);
    } 
    if (paramBoolean)
      ansi.a(' ').a(Ansi.Attribute.ITALIC); 
    return ansi;
  }
  
  public static String ansiFormatVersion(String paramString) {
    return (new Ansi()).bold().fgBright(Ansi.Color.MAGENTA).a("GravitLauncher ").fgBright(Ansi.Color.BLUE).a("(fork sashok724's Launcher) ").fgBright(Ansi.Color.CYAN).a(paramString).fgBright(Ansi.Color.WHITE).a(" v").fgBright(Ansi.Color.BLUE).a(Version.getVersion().toString()).fgBright(Ansi.Color.WHITE).a(" (build #").fgBright(Ansi.Color.RED).a((Version.getVersion()).build).fgBright(Ansi.Color.WHITE).a(')').reset().toString();
  }
  
  public static String ansiFormatLicense(String paramString) {
    return (new Ansi()).bold().fgBright(Ansi.Color.MAGENTA).a("License for ").fgBright(Ansi.Color.CYAN).a(paramString).fgBright(Ansi.Color.WHITE).a(" GPLv3").fgBright(Ansi.Color.WHITE).a(". SourceCode: ").fgBright(Ansi.Color.YELLOW).a("https://github.com/GravitLauncher/Launcher").reset().toString();
  }
  
  public static String rawFormat(LogHelper.Level paramLevel, String paramString, boolean paramBoolean) {
    return paramString + " [" + paramString + paramLevel.name;
  }
  
  public static String formatVersion(String paramString) {
    return String.format("GravitLauncher (fork sashok724's Launcher) %s v%s", new Object[] { paramString, Version.getVersion() });
  }
  
  public static String formatLicense(String paramString) {
    return String.format("License for %s GPLv3. SourceCode: https://github.com/GravitLauncher/Launcher", new Object[] { paramString });
  }
  
  static {
  
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\pro\gravi\\utils\helper\FormatHelper.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */