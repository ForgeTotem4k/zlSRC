package com.sun.jna.platform.linux;

import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.NativeLong;
import com.sun.jna.Structure;
import com.sun.jna.Structure.FieldOrder;
import com.sun.jna.platform.unix.LibCAPI;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public interface LibC extends LibCAPI, Library {
  public static final String NAME = "c";
  
  public static final LibC INSTANCE = (LibC)Native.load("c", LibC.class);
  
  int sysinfo(Sysinfo paramSysinfo);
  
  int statvfs(String paramString, Statvfs paramStatvfs);
  
  @FieldOrder({"f_bsize", "f_frsize", "f_blocks", "f_bfree", "f_bavail", "f_files", "f_ffree", "f_favail", "f_fsid", "_f_unused", "f_flag", "f_namemax", "_f_spare"})
  public static class Statvfs extends Structure {
    public NativeLong f_bsize;
    
    public NativeLong f_frsize;
    
    public NativeLong f_blocks;
    
    public NativeLong f_bfree;
    
    public NativeLong f_bavail;
    
    public NativeLong f_files;
    
    public NativeLong f_ffree;
    
    public NativeLong f_favail;
    
    public NativeLong f_fsid;
    
    public int _f_unused;
    
    public NativeLong f_flag;
    
    public NativeLong f_namemax;
    
    public int[] _f_spare = new int[6];
    
    protected List<Field> getFieldList() {
      ArrayList<Field> arrayList = new ArrayList(super.getFieldList());
      if (NativeLong.SIZE > 4) {
        Iterator<Field> iterator = arrayList.iterator();
        while (iterator.hasNext()) {
          Field field = iterator.next();
          if ("_f_unused".equals(field.getName()))
            iterator.remove(); 
        } 
      } 
      return arrayList;
    }
    
    protected List<String> getFieldOrder() {
      ArrayList<String> arrayList = new ArrayList(super.getFieldOrder());
      if (NativeLong.SIZE > 4)
        arrayList.remove("_f_unused"); 
      return arrayList;
    }
  }
  
  @FieldOrder({"uptime", "loads", "totalram", "freeram", "sharedram", "bufferram", "totalswap", "freeswap", "procs", "totalhigh", "freehigh", "mem_unit", "_f"})
  public static class Sysinfo extends Structure {
    private static final int PADDING_SIZE = 20 - 2 * NativeLong.SIZE - 4;
    
    public NativeLong uptime;
    
    public NativeLong[] loads = new NativeLong[3];
    
    public NativeLong totalram;
    
    public NativeLong freeram;
    
    public NativeLong sharedram;
    
    public NativeLong bufferram;
    
    public NativeLong totalswap;
    
    public NativeLong freeswap;
    
    public short procs;
    
    public NativeLong totalhigh;
    
    public NativeLong freehigh;
    
    public int mem_unit;
    
    public byte[] _f = new byte[PADDING_SIZE];
    
    protected List<Field> getFieldList() {
      ArrayList<Field> arrayList = new ArrayList(super.getFieldList());
      if (PADDING_SIZE == 0) {
        Iterator<Field> iterator = arrayList.iterator();
        while (iterator.hasNext()) {
          Field field = iterator.next();
          if ("_f".equals(field.getName()))
            iterator.remove(); 
        } 
      } 
      return arrayList;
    }
    
    protected List<String> getFieldOrder() {
      ArrayList<String> arrayList = new ArrayList(super.getFieldOrder());
      if (PADDING_SIZE == 0)
        arrayList.remove("_f"); 
      return arrayList;
    }
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\com\sun\jna\platform\linux\LibC.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */