package pro.gravit.utils;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public final class Version implements Comparable<Version> {
  public static final int MAJOR = 5;
  
  public static final int MINOR = 6;
  
  public static final int PATCH = 16;
  
  public static final int BUILD = 1;
  
  public static final Type RELEASE = Type.STABLE;
  
  public final int major;
  
  public final int minor;
  
  public final int patch;
  
  public final int build;
  
  public final Type release;
  
  public Version(int paramInt1, int paramInt2, int paramInt3) {
    this.major = paramInt1;
    this.minor = paramInt2;
    this.patch = paramInt3;
    this.build = 0;
    this.release = Type.UNKNOWN;
  }
  
  public Version(int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
    this.major = paramInt1;
    this.minor = paramInt2;
    this.patch = paramInt3;
    this.build = paramInt4;
    this.release = Type.UNKNOWN;
  }
  
  public Version(int paramInt1, int paramInt2, int paramInt3, int paramInt4, Type paramType) {
    this.major = paramInt1;
    this.minor = paramInt2;
    this.patch = paramInt3;
    this.build = paramInt4;
    this.release = paramType;
  }
  
  public static Version of(int paramInt1, int paramInt2, int paramInt3) {
    return new Version(paramInt1, paramInt2, paramInt3);
  }
  
  public static Version of(int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
    return new Version(paramInt1, paramInt2, paramInt3, paramInt4);
  }
  
  public static Version of(int paramInt1, int paramInt2, int paramInt3, int paramInt4, Type paramType) {
    return new Version(paramInt1, paramInt2, paramInt3, paramInt4, paramType);
  }
  
  public static Version getVersion() {
    return new Version(5, 6, 16, 1, RELEASE);
  }
  
  public boolean equals(Object paramObject) {
    if (this == paramObject)
      return true; 
    if (paramObject == null || getClass() != paramObject.getClass())
      return false; 
    Version version = (Version)paramObject;
    return (this.major == version.major && this.minor == version.minor && this.patch == version.patch && this.build == version.build);
  }
  
  public String getVersionString() {
    return String.format("%d.%d.%d", new Object[] { Integer.valueOf(this.major), Integer.valueOf(this.minor), Integer.valueOf(this.patch) });
  }
  
  public int hashCode() {
    return Objects.hash(new Object[] { Integer.valueOf(this.major), Integer.valueOf(this.minor), Integer.valueOf(this.patch), Integer.valueOf(this.build) });
  }
  
  public String getReleaseStatus() {
    return this.release.equals(Type.UNKNOWN) ? "" : this.release.name().toLowerCase(Locale.ENGLISH);
  }
  
  public String toString() {
    return String.format("%d.%d.%d-%d %s", new Object[] { Integer.valueOf(this.major), Integer.valueOf(this.minor), Integer.valueOf(this.patch), Integer.valueOf(this.build), getReleaseStatus() });
  }
  
  public int compareTo(Version paramVersion) {
    return (paramVersion.major != this.major) ? Integer.compare(this.major, paramVersion.major) : ((paramVersion.minor != this.minor) ? Integer.compare(this.minor, paramVersion.minor) : ((paramVersion.patch != this.patch) ? Integer.compare(this.patch, paramVersion.patch) : 0));
  }
  
  public boolean isUpperThan(Version paramVersion) {
    return (compareTo(paramVersion) > 0);
  }
  
  public boolean isLowerThan(Version paramVersion) {
    return (compareTo(paramVersion) < 0);
  }
  
  public enum Type {
    LTS, STABLE, BETA, ALPHA, DEV, EXPERIMENTAL, UNKNOWN;
    
    private static final Map<String, Type> types = new HashMap<>();
    
    public static final Map<String, Type> unModTypes = Collections.unmodifiableMap(types);
    
    static {
      Arrays.<Type>asList(values()).forEach(param1Type -> types.put(param1Type.name().substring(0, Math.min(param1Type.name().length(), 3)).toLowerCase(Locale.ENGLISH), param1Type));
    }
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\pro\gravi\\utils\Version.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */