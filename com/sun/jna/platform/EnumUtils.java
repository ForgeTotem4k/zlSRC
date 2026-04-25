package com.sun.jna.platform;

import com.sun.jna.platform.win32.FlagEnum;
import java.util.HashSet;
import java.util.Set;

public class EnumUtils {
  public static final int UNINITIALIZED = -1;
  
  public static <E extends Enum<E>> int toInteger(E paramE) {
    Enum[] arrayOfEnum = (Enum[])paramE.getClass().getEnumConstants();
    for (byte b = 0; b < arrayOfEnum.length; b++) {
      if (arrayOfEnum[b] == paramE)
        return b; 
    } 
    throw new IllegalArgumentException();
  }
  
  public static <E extends Enum<E>> E fromInteger(int paramInt, Class<E> paramClass) {
    if (paramInt == -1)
      return null; 
    Enum[] arrayOfEnum = (Enum[])paramClass.getEnumConstants();
    return (E)arrayOfEnum[paramInt];
  }
  
  public static <T extends FlagEnum> Set<T> setFromInteger(int paramInt, Class<T> paramClass) {
    FlagEnum[] arrayOfFlagEnum = (FlagEnum[])paramClass.getEnumConstants();
    HashSet<FlagEnum> hashSet = new HashSet();
    for (FlagEnum flagEnum : arrayOfFlagEnum) {
      if ((paramInt & flagEnum.getFlag()) != 0)
        hashSet.add(flagEnum); 
    } 
    return (Set)hashSet;
  }
  
  public static <T extends FlagEnum> int setToInteger(Set<T> paramSet) {
    int i = 0;
    for (FlagEnum flagEnum : paramSet)
      i |= flagEnum.getFlag(); 
    return i;
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\com\sun\jna\platform\EnumUtils.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */