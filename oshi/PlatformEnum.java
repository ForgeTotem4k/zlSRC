package oshi;

public enum PlatformEnum {
  MACOS("macOS"),
  LINUX("Linux"),
  WINDOWS("Windows"),
  SOLARIS("Solaris"),
  FREEBSD("FreeBSD"),
  OPENBSD("OpenBSD"),
  WINDOWSCE("Windows CE"),
  AIX("AIX"),
  ANDROID("Android"),
  GNU("GNU"),
  KFREEBSD("kFreeBSD"),
  NETBSD("NetBSD"),
  UNKNOWN("Unknown");
  
  private final String name;
  
  PlatformEnum(String paramString1) {
    this.name = paramString1;
  }
  
  public String getName() {
    return this.name;
  }
  
  public static String getName(int paramInt) {
    return getValue(paramInt).getName();
  }
  
  public static PlatformEnum getValue(int paramInt) {
    return (paramInt < 0 || paramInt >= UNKNOWN.ordinal()) ? UNKNOWN : values()[paramInt];
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\oshi\PlatformEnum.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */