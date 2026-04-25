package com.sun.jna.platform.win32;

import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import java.lang.reflect.Array;

public abstract class OaIdlUtil {
  public static Object toPrimitiveArray(OaIdl.SAFEARRAY paramSAFEARRAY, boolean paramBoolean) {
    Pointer pointer = paramSAFEARRAY.accessData();
    try {
      byte[] arrayOfByte;
      short[] arrayOfShort;
      int[] arrayOfInt3;
      float[] arrayOfFloat;
      double[] arrayOfDouble;
      Pointer[] arrayOfPointer;
      Structure[] arrayOfStructure;
      Variant.VARIANT vARIANT;
      int i = paramSAFEARRAY.getDimensionCount();
      int[] arrayOfInt1 = new int[i];
      int[] arrayOfInt2 = new int[i];
      int j = paramSAFEARRAY.getVarType().intValue();
      int k;
      for (k = 0; k < i; k++)
        arrayOfInt1[k] = paramSAFEARRAY.getUBound(k) - paramSAFEARRAY.getLBound(k) + 1; 
      for (k = i - 1; k >= 0; k--) {
        if (k == i - 1) {
          arrayOfInt2[k] = 1;
        } else {
          arrayOfInt2[k] = arrayOfInt2[k + 1] * arrayOfInt1[k + 1];
        } 
      } 
      if (i == 0)
        throw new IllegalArgumentException("Supplied Array has no dimensions."); 
      k = arrayOfInt2[0] * arrayOfInt1[0];
      switch (j) {
        case 16:
        case 17:
          arrayOfByte = pointer.getByteArray(0L, k);
          break;
        case 2:
        case 11:
        case 18:
          arrayOfShort = pointer.getShortArray(0L, k);
          break;
        case 3:
        case 10:
        case 19:
        case 22:
        case 23:
          arrayOfInt3 = pointer.getIntArray(0L, k);
          break;
        case 4:
          arrayOfFloat = pointer.getFloatArray(0L, k);
          break;
        case 5:
        case 7:
          arrayOfDouble = pointer.getDoubleArray(0L, k);
          break;
        case 8:
          arrayOfPointer = pointer.getPointerArray(0L, k);
          break;
        case 12:
          vARIANT = new Variant.VARIANT(pointer);
          arrayOfStructure = vARIANT.toArray(k);
          break;
        default:
          throw new IllegalStateException("Type not supported: " + j);
      } 
      Object object = Array.newInstance(Object.class, arrayOfInt1);
      toPrimitiveArray(arrayOfStructure, object, arrayOfInt1, arrayOfInt2, j, new int[0]);
      return object;
    } finally {
      paramSAFEARRAY.unaccessData();
      if (paramBoolean)
        paramSAFEARRAY.destroy(); 
    } 
  }
  
  private static void toPrimitiveArray(Object paramObject1, Object paramObject2, int[] paramArrayOfint1, int[] paramArrayOfint2, int paramInt, int[] paramArrayOfint3) {
    int i = paramArrayOfint3.length;
    int[] arrayOfInt = new int[paramArrayOfint3.length + 1];
    System.arraycopy(paramArrayOfint3, 0, arrayOfInt, 0, i);
    for (byte b = 0; b < paramArrayOfint1[i]; b++) {
      arrayOfInt[i] = b;
      if (i == paramArrayOfint1.length - 1) {
        Variant.VARIANT vARIANT;
        int j = 0;
        int k;
        for (k = 0; k < i; k++)
          j += paramArrayOfint2[k] * paramArrayOfint3[k]; 
        j += arrayOfInt[i];
        k = arrayOfInt[i];
        switch (paramInt) {
          case 11:
            Array.set(paramObject2, k, Boolean.valueOf((Array.getShort(paramObject1, j) != 0)));
            break;
          case 16:
          case 17:
            Array.set(paramObject2, k, Byte.valueOf(Array.getByte(paramObject1, j)));
            break;
          case 2:
          case 18:
            Array.set(paramObject2, k, Short.valueOf(Array.getShort(paramObject1, j)));
            break;
          case 3:
          case 19:
          case 22:
          case 23:
            Array.set(paramObject2, k, Integer.valueOf(Array.getInt(paramObject1, j)));
            break;
          case 10:
            Array.set(paramObject2, k, new WinDef.SCODE(Array.getInt(paramObject1, j)));
            break;
          case 4:
            Array.set(paramObject2, k, Float.valueOf(Array.getFloat(paramObject1, j)));
            break;
          case 5:
            Array.set(paramObject2, k, Double.valueOf(Array.getDouble(paramObject1, j)));
            break;
          case 7:
            Array.set(paramObject2, k, (new OaIdl.DATE(Array.getDouble(paramObject1, j))).getAsJavaDate());
            break;
          case 8:
            Array.set(paramObject2, k, (new WTypes.BSTR((Pointer)Array.get(paramObject1, j))).getValue());
            break;
          case 12:
            vARIANT = (Variant.VARIANT)Array.get(paramObject1, j);
            switch (vARIANT.getVarType().intValue()) {
              case 0:
              case 1:
                Array.set(paramObject2, k, null);
                break;
              case 11:
                Array.set(paramObject2, k, Boolean.valueOf(vARIANT.booleanValue()));
                break;
              case 16:
              case 17:
                Array.set(paramObject2, k, Byte.valueOf(vARIANT.byteValue()));
                break;
              case 2:
              case 18:
                Array.set(paramObject2, k, Short.valueOf(vARIANT.shortValue()));
                break;
              case 3:
              case 19:
              case 22:
              case 23:
                Array.set(paramObject2, k, Integer.valueOf(vARIANT.intValue()));
                break;
              case 10:
                Array.set(paramObject2, k, new WinDef.SCODE(vARIANT.intValue()));
                break;
              case 4:
                Array.set(paramObject2, k, Float.valueOf(vARIANT.floatValue()));
                break;
              case 5:
                Array.set(paramObject2, k, Double.valueOf(vARIANT.doubleValue()));
                break;
              case 7:
                Array.set(paramObject2, k, vARIANT.dateValue());
                break;
              case 8:
                Array.set(paramObject2, k, vARIANT.stringValue());
                break;
            } 
            throw new IllegalStateException("Type not supported: " + vARIANT.getVarType().intValue());
          default:
            throw new IllegalStateException("Type not supported: " + paramInt);
        } 
      } else {
        toPrimitiveArray(paramObject1, Array.get(paramObject2, b), paramArrayOfint1, paramArrayOfint2, paramInt, arrayOfInt);
      } 
    } 
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\com\sun\jna\platform\win32\OaIdlUtil.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */