package pro.gravit.launcher.base;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

public class PermissionPattern {
  private final String[] parts;
  
  private final int priority;
  
  public PermissionPattern(String paramString) {
    ArrayList<String> arrayList = new ArrayList();
    int i = 0;
    while (true) {
      int j = paramString.indexOf("*", i);
      if (j >= 0) {
        arrayList.add(paramString.substring(i, j));
        i = j + 1;
        continue;
      } 
      arrayList.add(paramString.substring(i));
      this.priority = arrayList.size() - 1;
      this.parts = arrayList.<String>toArray(new String[0]);
      return;
    } 
  }
  
  public int getPriority() {
    return this.priority;
  }
  
  public boolean match(String paramString) {
    if (this.parts.length == 0)
      return true; 
    if (this.parts.length == 1)
      return this.parts[0].equals(paramString); 
    int i = 0;
    if (!paramString.startsWith(this.parts[0]))
      return false; 
    if (!paramString.endsWith(this.parts[this.parts.length - 1]))
      return false; 
    for (byte b = 1; b < this.parts.length - 1; b++) {
      int j = paramString.indexOf(this.parts[b], i);
      if (j >= 0) {
        i = j + 1;
      } else {
        return false;
      } 
    } 
    return true;
  }
  
  public boolean equals(Object paramObject) {
    if (this == paramObject)
      return true; 
    if (paramObject == null || getClass() != paramObject.getClass())
      return false; 
    PermissionPattern permissionPattern = (PermissionPattern)paramObject;
    return (this.priority == permissionPattern.priority && Arrays.equals((Object[])this.parts, (Object[])permissionPattern.parts));
  }
  
  public int hashCode() {
    null = Objects.hash(new Object[] { Integer.valueOf(this.priority) });
    return 31 * null + Arrays.hashCode((Object[])this.parts);
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\pro\gravit\launcher\base\ClientPermissions$PermissionPattern.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */