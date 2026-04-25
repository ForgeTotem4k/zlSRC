package pro.gravit.launcher.base;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import pro.gravit.launcher.core.LauncherNetworkAPI;

public class ClientPermissions {
  public static final ClientPermissions DEFAULT = new ClientPermissions();
  
  @LauncherNetworkAPI
  private List<String> roles;
  
  @LauncherNetworkAPI
  private List<String> perms;
  
  private transient List<PermissionPattern> available;
  
  public ClientPermissions() {}
  
  public ClientPermissions(List<String> paramList1, List<String> paramList2) {
    this.roles = new ArrayList<>(paramList1);
    this.perms = new ArrayList<>(paramList2);
  }
  
  public static ClientPermissions getSuperuserAccount() {
    ClientPermissions clientPermissions = new ClientPermissions();
    clientPermissions.addPerm("*");
    return clientPermissions;
  }
  
  public boolean hasRole(String paramString) {
    return (this.roles != null && this.roles.contains(paramString));
  }
  
  public synchronized void compile() {
    if (this.available != null)
      return; 
    if (this.perms == null)
      this.perms = new ArrayList<>(0); 
    this.available = new ArrayList<>(this.perms.size());
    for (String str : this.perms)
      this.available.add(new PermissionPattern(str)); 
  }
  
  public boolean hasPerm(String paramString) {
    if (this.available == null)
      compile(); 
    for (PermissionPattern permissionPattern : this.available) {
      if (permissionPattern.match(paramString))
        return true; 
    } 
    return false;
  }
  
  public void addRole(String paramString) {
    if (this.roles == null)
      this.roles = new ArrayList<>(1); 
    this.roles.add(paramString);
  }
  
  public void addPerm(String paramString) {
    if (this.perms == null)
      this.perms = new ArrayList<>(1); 
    this.perms.add(paramString);
    if (this.available == null)
      this.available = new ArrayList<>(1); 
    this.available.add(new PermissionPattern(paramString));
  }
  
  public void removePerm(String paramString) {
    if (this.perms == null)
      return; 
    if (this.available == null)
      return; 
    this.perms.remove(paramString);
    this.available.remove(new PermissionPattern(paramString));
  }
  
  public List<String> getRoles() {
    return this.roles;
  }
  
  public List<String> getPerms() {
    return this.perms;
  }
  
  public String toString() {
    return "ClientPermissions{roles=" + String.join(", ", (this.roles == null) ? Collections.<CharSequence>emptyList() : (Iterable)this.roles) + ", actions=" + String.join(", ", (this.perms == null) ? Collections.<CharSequence>emptyList() : (Iterable)this.perms) + "}";
  }
  
  public static class PermissionPattern {
    private final String[] parts;
    
    private final int priority;
    
    public PermissionPattern(String param1String) {
      ArrayList<String> arrayList = new ArrayList();
      int i = 0;
      while (true) {
        int j = param1String.indexOf("*", i);
        if (j >= 0) {
          arrayList.add(param1String.substring(i, j));
          i = j + 1;
          continue;
        } 
        arrayList.add(param1String.substring(i));
        this.priority = arrayList.size() - 1;
        this.parts = arrayList.<String>toArray(new String[0]);
        return;
      } 
    }
    
    public int getPriority() {
      return this.priority;
    }
    
    public boolean match(String param1String) {
      if (this.parts.length == 0)
        return true; 
      if (this.parts.length == 1)
        return this.parts[0].equals(param1String); 
      int i = 0;
      if (!param1String.startsWith(this.parts[0]))
        return false; 
      if (!param1String.endsWith(this.parts[this.parts.length - 1]))
        return false; 
      for (byte b = 1; b < this.parts.length - 1; b++) {
        int j = param1String.indexOf(this.parts[b], i);
        if (j >= 0) {
          i = j + 1;
        } else {
          return false;
        } 
      } 
      return true;
    }
    
    public boolean equals(Object param1Object) {
      if (this == param1Object)
        return true; 
      if (param1Object == null || getClass() != param1Object.getClass())
        return false; 
      PermissionPattern permissionPattern = (PermissionPattern)param1Object;
      return (this.priority == permissionPattern.priority && Arrays.equals((Object[])this.parts, (Object[])permissionPattern.parts));
    }
    
    public int hashCode() {
      null = Objects.hash(new Object[] { Integer.valueOf(this.priority) });
      return 31 * null + Arrays.hashCode((Object[])this.parts);
    }
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\pro\gravit\launcher\base\ClientPermissions.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */