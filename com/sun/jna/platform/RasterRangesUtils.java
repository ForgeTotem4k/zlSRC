package com.sun.jna.platform;

import java.awt.Rectangle;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.DataBufferInt;
import java.awt.image.MultiPixelPackedSampleModel;
import java.awt.image.Raster;
import java.awt.image.SampleModel;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

public class RasterRangesUtils {
  private static final int[] subColMasks = new int[] { 128, 64, 32, 16, 8, 4, 2, 1 };
  
  private static final Comparator<Object> COMPARATOR = new Comparator() {
      public int compare(Object param1Object1, Object param1Object2) {
        return ((Rectangle)param1Object1).x - ((Rectangle)param1Object2).x;
      }
    };
  
  public static boolean outputOccupiedRanges(Raster paramRaster, RangesOutput paramRangesOutput) {
    Rectangle rectangle = paramRaster.getBounds();
    SampleModel sampleModel = paramRaster.getSampleModel();
    boolean bool = (sampleModel.getNumBands() == 4) ? true : false;
    if (paramRaster.getParent() == null && rectangle.x == 0 && rectangle.y == 0) {
      DataBuffer dataBuffer = paramRaster.getDataBuffer();
      if (dataBuffer.getNumBanks() == 1)
        if (sampleModel instanceof MultiPixelPackedSampleModel) {
          MultiPixelPackedSampleModel multiPixelPackedSampleModel = (MultiPixelPackedSampleModel)sampleModel;
          if (multiPixelPackedSampleModel.getPixelBitStride() == 1)
            return outputOccupiedRangesOfBinaryPixels(((DataBufferByte)dataBuffer).getData(), rectangle.width, rectangle.height, paramRangesOutput); 
        } else if (sampleModel instanceof java.awt.image.SinglePixelPackedSampleModel && sampleModel.getDataType() == 3) {
          return outputOccupiedRanges(((DataBufferInt)dataBuffer).getData(), rectangle.width, rectangle.height, bool ? -16777216 : 16777215, paramRangesOutput);
        }  
    } 
    int[] arrayOfInt = paramRaster.getPixels(0, 0, rectangle.width, rectangle.height, (int[])null);
    return outputOccupiedRanges(arrayOfInt, rectangle.width, rectangle.height, bool ? -16777216 : 16777215, paramRangesOutput);
  }
  
  public static boolean outputOccupiedRangesOfBinaryPixels(byte[] paramArrayOfbyte, int paramInt1, int paramInt2, RangesOutput paramRangesOutput) {
    HashSet<Rectangle> hashSet = new HashSet();
    Set<?> set = Collections.emptySet();
    int i = paramArrayOfbyte.length / paramInt2;
    for (byte b = 0; b < paramInt2; b++) {
      TreeSet<Rectangle> treeSet = new TreeSet(COMPARATOR);
      int j = b * i;
      int k = -1;
      for (byte b1 = 0; b1 < i; b1++) {
        int m = b1 << 3;
        byte b2 = paramArrayOfbyte[j + b1];
        if (b2 == 0) {
          if (k >= 0) {
            treeSet.add(new Rectangle(k, b, m - k, 1));
            k = -1;
          } 
        } else if (b2 == 255) {
          if (k < 0)
            k = m; 
        } else {
          for (byte b3 = 0; b3 < 8; b3++) {
            int n = m | b3;
            if ((b2 & subColMasks[b3]) != 0) {
              if (k < 0)
                k = n; 
            } else if (k >= 0) {
              treeSet.add(new Rectangle(k, b, n - k, 1));
              k = -1;
            } 
          } 
        } 
      } 
      if (k >= 0)
        treeSet.add(new Rectangle(k, b, paramInt1 - k, 1)); 
      Set<Rectangle> set1 = mergeRects((Set)set, treeSet);
      hashSet.addAll(set1);
      set = treeSet;
    } 
    hashSet.addAll(set);
    for (Rectangle rectangle : hashSet) {
      if (!paramRangesOutput.outputRange(rectangle.x, rectangle.y, rectangle.width, rectangle.height))
        return false; 
    } 
    return true;
  }
  
  public static boolean outputOccupiedRanges(int[] paramArrayOfint, int paramInt1, int paramInt2, int paramInt3, RangesOutput paramRangesOutput) {
    HashSet<Rectangle> hashSet = new HashSet();
    Set<?> set = Collections.emptySet();
    for (byte b = 0; b < paramInt2; b++) {
      TreeSet<Rectangle> treeSet = new TreeSet(COMPARATOR);
      int i = b * paramInt1;
      byte b1 = -1;
      for (byte b2 = 0; b2 < paramInt1; b2++) {
        if ((paramArrayOfint[i + b2] & paramInt3) != 0) {
          if (b1 < 0)
            b1 = b2; 
        } else if (b1 >= 0) {
          treeSet.add(new Rectangle(b1, b, b2 - b1, 1));
          b1 = -1;
        } 
      } 
      if (b1 >= 0)
        treeSet.add(new Rectangle(b1, b, paramInt1 - b1, 1)); 
      Set<Rectangle> set1 = mergeRects((Set)set, treeSet);
      hashSet.addAll(set1);
      set = treeSet;
    } 
    hashSet.addAll(set);
    for (Rectangle rectangle : hashSet) {
      if (!paramRangesOutput.outputRange(rectangle.x, rectangle.y, rectangle.width, rectangle.height))
        return false; 
    } 
    return true;
  }
  
  private static Set<Rectangle> mergeRects(Set<Rectangle> paramSet1, Set<Rectangle> paramSet2) {
    HashSet<Rectangle> hashSet = new HashSet<>(paramSet1);
    if (!paramSet1.isEmpty() && !paramSet2.isEmpty()) {
      Rectangle[] arrayOfRectangle1 = paramSet1.<Rectangle>toArray(new Rectangle[0]);
      Rectangle[] arrayOfRectangle2 = paramSet2.<Rectangle>toArray(new Rectangle[0]);
      byte b1 = 0;
      byte b2 = 0;
      while (b1 < arrayOfRectangle1.length && b2 < arrayOfRectangle2.length) {
        while ((arrayOfRectangle2[b2]).x < (arrayOfRectangle1[b1]).x) {
          if (++b2 == arrayOfRectangle2.length)
            return hashSet; 
        } 
        if ((arrayOfRectangle2[b2]).x == (arrayOfRectangle1[b1]).x && (arrayOfRectangle2[b2]).width == (arrayOfRectangle1[b1]).width) {
          hashSet.remove(arrayOfRectangle1[b1]);
          (arrayOfRectangle2[b2]).y = (arrayOfRectangle1[b1]).y;
          (arrayOfRectangle1[b1]).height++;
          b2++;
          continue;
        } 
        b1++;
      } 
    } 
    return hashSet;
  }
  
  public static interface RangesOutput {
    boolean outputRange(int param1Int1, int param1Int2, int param1Int3, int param1Int4);
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\com\sun\jna\platform\RasterRangesUtils.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */