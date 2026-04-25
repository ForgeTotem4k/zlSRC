package io.sentry.vendor;

import java.io.UnsupportedEncodingException;

public class Base64 {
  public static final int DEFAULT = 0;
  
  public static final int NO_PADDING = 1;
  
  public static final int NO_WRAP = 2;
  
  public static final int CRLF = 4;
  
  public static final int URL_SAFE = 8;
  
  public static final int NO_CLOSE = 16;
  
  public static byte[] decode(String paramString, int paramInt) {
    return decode(paramString.getBytes(), paramInt);
  }
  
  public static byte[] decode(byte[] paramArrayOfbyte, int paramInt) {
    return decode(paramArrayOfbyte, 0, paramArrayOfbyte.length, paramInt);
  }
  
  public static byte[] decode(byte[] paramArrayOfbyte, int paramInt1, int paramInt2, int paramInt3) {
    Decoder decoder = new Decoder(paramInt3, new byte[paramInt2 * 3 / 4]);
    if (!decoder.process(paramArrayOfbyte, paramInt1, paramInt2, true))
      throw new IllegalArgumentException("bad base-64"); 
    if (decoder.op == decoder.output.length)
      return decoder.output; 
    byte[] arrayOfByte = new byte[decoder.op];
    System.arraycopy(decoder.output, 0, arrayOfByte, 0, decoder.op);
    return arrayOfByte;
  }
  
  public static String encodeToString(byte[] paramArrayOfbyte, int paramInt) {
    try {
      return new String(encode(paramArrayOfbyte, paramInt), "US-ASCII");
    } catch (UnsupportedEncodingException unsupportedEncodingException) {
      throw new AssertionError(unsupportedEncodingException);
    } 
  }
  
  public static String encodeToString(byte[] paramArrayOfbyte, int paramInt1, int paramInt2, int paramInt3) {
    try {
      return new String(encode(paramArrayOfbyte, paramInt1, paramInt2, paramInt3), "US-ASCII");
    } catch (UnsupportedEncodingException unsupportedEncodingException) {
      throw new AssertionError(unsupportedEncodingException);
    } 
  }
  
  public static byte[] encode(byte[] paramArrayOfbyte, int paramInt) {
    return encode(paramArrayOfbyte, 0, paramArrayOfbyte.length, paramInt);
  }
  
  public static byte[] encode(byte[] paramArrayOfbyte, int paramInt1, int paramInt2, int paramInt3) {
    Encoder encoder = new Encoder(paramInt3, null);
    int i = paramInt2 / 3 * 4;
    if (encoder.do_padding) {
      if (paramInt2 % 3 > 0)
        i += 4; 
    } else {
      switch (paramInt2 % 3) {
        case 1:
          i += 2;
          break;
        case 2:
          i += 3;
          break;
      } 
    } 
    if (encoder.do_newline && paramInt2 > 0)
      i += ((paramInt2 - 1) / 57 + 1) * (encoder.do_cr ? 2 : 1); 
    encoder.output = new byte[i];
    encoder.process(paramArrayOfbyte, paramInt1, paramInt2, true);
    assert encoder.op == i;
    return encoder.output;
  }
  
  static class Decoder extends Coder {
    private static final int[] DECODE = new int[] { 
        -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 
        -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 
        -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 
        -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 
        -1, -1, -1, 62, -1, -1, -1, 63, 52, 53, 
        54, 55, 56, 57, 58, 59, 60, 61, -1, -1, 
        -1, -2, -1, -1, -1, 0, 1, 2, 3, 4, 
        5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 
        15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 
        25, -1, -1, -1, -1, -1, -1, 26, 27, 28, 
        29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 
        39, 40, 41, 42, 43, 44, 45, 46, 47, 48, 
        49, 50, 51, -1, -1, -1, -1, -1, -1, -1, 
        -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 
        -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 
        -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 
        -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 
        -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 
        -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 
        -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 
        -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 
        -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 
        -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 
        -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 
        -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 
        -1, -1, -1, -1, -1, -1 };
    
    private static final int[] DECODE_WEBSAFE = new int[] { 
        -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 
        -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 
        -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 
        -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 
        -1, -1, -1, -1, -1, 62, -1, -1, 52, 53, 
        54, 55, 56, 57, 58, 59, 60, 61, -1, -1, 
        -1, -2, -1, -1, -1, 0, 1, 2, 3, 4, 
        5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 
        15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 
        25, -1, -1, -1, -1, 63, -1, 26, 27, 28, 
        29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 
        39, 40, 41, 42, 43, 44, 45, 46, 47, 48, 
        49, 50, 51, -1, -1, -1, -1, -1, -1, -1, 
        -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 
        -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 
        -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 
        -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 
        -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 
        -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 
        -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 
        -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 
        -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 
        -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 
        -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 
        -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 
        -1, -1, -1, -1, -1, -1 };
    
    private static final int SKIP = -1;
    
    private static final int EQUALS = -2;
    
    private int state;
    
    private int value;
    
    private final int[] alphabet;
    
    public Decoder(int param1Int, byte[] param1ArrayOfbyte) {
      this.output = param1ArrayOfbyte;
      this.alphabet = ((param1Int & 0x8) == 0) ? DECODE : DECODE_WEBSAFE;
      this.state = 0;
      this.value = 0;
    }
    
    public int maxOutputSize(int param1Int) {
      return param1Int * 3 / 4 + 10;
    }
    
    public boolean process(byte[] param1ArrayOfbyte, int param1Int1, int param1Int2, boolean param1Boolean) {
      if (this.state == 6)
        return false; 
      int i = param1Int1;
      param1Int2 += param1Int1;
      int j = this.state;
      int k = this.value;
      byte b = 0;
      byte[] arrayOfByte = this.output;
      int[] arrayOfInt = this.alphabet;
      while (i < param1Int2) {
        if (j == 0) {
          while (i + 4 <= param1Int2 && (k = arrayOfInt[param1ArrayOfbyte[i] & 0xFF] << 18 | arrayOfInt[param1ArrayOfbyte[i + 1] & 0xFF] << 12 | arrayOfInt[param1ArrayOfbyte[i + 2] & 0xFF] << 6 | arrayOfInt[param1ArrayOfbyte[i + 3] & 0xFF]) >= 0) {
            arrayOfByte[b + 2] = (byte)k;
            arrayOfByte[b + 1] = (byte)(k >> 8);
            arrayOfByte[b] = (byte)(k >> 16);
            b += 3;
            i += 4;
          } 
          if (i >= param1Int2)
            break; 
        } 
        int m = arrayOfInt[param1ArrayOfbyte[i++] & 0xFF];
        switch (j) {
          case 0:
            if (m >= 0) {
              k = m;
              j++;
              continue;
            } 
            if (m != -1) {
              this.state = 6;
              return false;
            } 
          case 1:
            if (m >= 0) {
              k = k << 6 | m;
              j++;
              continue;
            } 
            if (m != -1) {
              this.state = 6;
              return false;
            } 
          case 2:
            if (m >= 0) {
              k = k << 6 | m;
              j++;
              continue;
            } 
            if (m == -2) {
              arrayOfByte[b++] = (byte)(k >> 4);
              j = 4;
              continue;
            } 
            if (m != -1) {
              this.state = 6;
              return false;
            } 
          case 3:
            if (m >= 0) {
              k = k << 6 | m;
              arrayOfByte[b + 2] = (byte)k;
              arrayOfByte[b + 1] = (byte)(k >> 8);
              arrayOfByte[b] = (byte)(k >> 16);
              b += 3;
              j = 0;
              continue;
            } 
            if (m == -2) {
              arrayOfByte[b + 1] = (byte)(k >> 2);
              arrayOfByte[b] = (byte)(k >> 10);
              b += 2;
              j = 5;
              continue;
            } 
            if (m != -1) {
              this.state = 6;
              return false;
            } 
          case 4:
            if (m == -2) {
              j++;
              continue;
            } 
            if (m != -1) {
              this.state = 6;
              return false;
            } 
          case 5:
            if (m != -1) {
              this.state = 6;
              return false;
            } 
        } 
      } 
      if (!param1Boolean) {
        this.state = j;
        this.value = k;
        this.op = b;
        return true;
      } 
      switch (j) {
        case 1:
          this.state = 6;
          return false;
        case 2:
          arrayOfByte[b++] = (byte)(k >> 4);
          break;
        case 3:
          arrayOfByte[b++] = (byte)(k >> 10);
          arrayOfByte[b++] = (byte)(k >> 2);
          break;
        case 4:
          this.state = 6;
          return false;
      } 
      this.state = j;
      this.op = b;
      return true;
    }
  }
  
  static class Encoder extends Coder {
    public static final int LINE_GROUPS = 19;
    
    private static final byte[] ENCODE = new byte[] { 
        65, 66, 67, 68, 69, 70, 71, 72, 73, 74, 
        75, 76, 77, 78, 79, 80, 81, 82, 83, 84, 
        85, 86, 87, 88, 89, 90, 97, 98, 99, 100, 
        101, 102, 103, 104, 105, 106, 107, 108, 109, 110, 
        111, 112, 113, 114, 115, 116, 117, 118, 119, 120, 
        121, 122, 48, 49, 50, 51, 52, 53, 54, 55, 
        56, 57, 43, 47 };
    
    private static final byte[] ENCODE_WEBSAFE = new byte[] { 
        65, 66, 67, 68, 69, 70, 71, 72, 73, 74, 
        75, 76, 77, 78, 79, 80, 81, 82, 83, 84, 
        85, 86, 87, 88, 89, 90, 97, 98, 99, 100, 
        101, 102, 103, 104, 105, 106, 107, 108, 109, 110, 
        111, 112, 113, 114, 115, 116, 117, 118, 119, 120, 
        121, 122, 48, 49, 50, 51, 52, 53, 54, 55, 
        56, 57, 45, 95 };
    
    private final byte[] tail;
    
    int tailLen;
    
    private int count;
    
    public final boolean do_padding;
    
    public final boolean do_newline;
    
    public final boolean do_cr;
    
    private final byte[] alphabet;
    
    public Encoder(int param1Int, byte[] param1ArrayOfbyte) {
      this.output = param1ArrayOfbyte;
      this.do_padding = ((param1Int & 0x1) == 0);
      this.do_newline = ((param1Int & 0x2) == 0);
      this.do_cr = ((param1Int & 0x4) != 0);
      this.alphabet = ((param1Int & 0x8) == 0) ? ENCODE : ENCODE_WEBSAFE;
      this.tail = new byte[2];
      this.tailLen = 0;
      this.count = this.do_newline ? 19 : -1;
    }
    
    public int maxOutputSize(int param1Int) {
      return param1Int * 8 / 5 + 10;
    }
    
    public boolean process(byte[] param1ArrayOfbyte, int param1Int1, int param1Int2, boolean param1Boolean) {
      byte[] arrayOfByte1 = this.alphabet;
      byte[] arrayOfByte2 = this.output;
      byte b = 0;
      int i = this.count;
      int j = param1Int1;
      param1Int2 += param1Int1;
      int k = -1;
      switch (this.tailLen) {
        case 1:
          if (j + 2 <= param1Int2) {
            k = (this.tail[0] & 0xFF) << 16 | (param1ArrayOfbyte[j++] & 0xFF) << 8 | param1ArrayOfbyte[j++] & 0xFF;
            this.tailLen = 0;
          } 
          break;
        case 2:
          if (j + 1 <= param1Int2) {
            k = (this.tail[0] & 0xFF) << 16 | (this.tail[1] & 0xFF) << 8 | param1ArrayOfbyte[j++] & 0xFF;
            this.tailLen = 0;
          } 
          break;
      } 
      if (k != -1) {
        arrayOfByte2[b++] = arrayOfByte1[k >> 18 & 0x3F];
        arrayOfByte2[b++] = arrayOfByte1[k >> 12 & 0x3F];
        arrayOfByte2[b++] = arrayOfByte1[k >> 6 & 0x3F];
        arrayOfByte2[b++] = arrayOfByte1[k & 0x3F];
        if (--i == 0) {
          if (this.do_cr)
            arrayOfByte2[b++] = 13; 
          arrayOfByte2[b++] = 10;
          i = 19;
        } 
      } 
      while (j + 3 <= param1Int2) {
        k = (param1ArrayOfbyte[j] & 0xFF) << 16 | (param1ArrayOfbyte[j + 1] & 0xFF) << 8 | param1ArrayOfbyte[j + 2] & 0xFF;
        arrayOfByte2[b] = arrayOfByte1[k >> 18 & 0x3F];
        arrayOfByte2[b + 1] = arrayOfByte1[k >> 12 & 0x3F];
        arrayOfByte2[b + 2] = arrayOfByte1[k >> 6 & 0x3F];
        arrayOfByte2[b + 3] = arrayOfByte1[k & 0x3F];
        j += 3;
        b += 4;
        if (--i == 0) {
          if (this.do_cr)
            arrayOfByte2[b++] = 13; 
          arrayOfByte2[b++] = 10;
          i = 19;
        } 
      } 
      if (param1Boolean) {
        if (j - this.tailLen == param1Int2 - 1) {
          byte b1 = 0;
          k = (((this.tailLen > 0) ? this.tail[b1++] : param1ArrayOfbyte[j++]) & 0xFF) << 4;
          this.tailLen -= b1;
          arrayOfByte2[b++] = arrayOfByte1[k >> 6 & 0x3F];
          arrayOfByte2[b++] = arrayOfByte1[k & 0x3F];
          if (this.do_padding) {
            arrayOfByte2[b++] = 61;
            arrayOfByte2[b++] = 61;
          } 
          if (this.do_newline) {
            if (this.do_cr)
              arrayOfByte2[b++] = 13; 
            arrayOfByte2[b++] = 10;
          } 
        } else if (j - this.tailLen == param1Int2 - 2) {
          byte b1 = 0;
          k = (((this.tailLen > 1) ? this.tail[b1++] : param1ArrayOfbyte[j++]) & 0xFF) << 10 | (((this.tailLen > 0) ? this.tail[b1++] : param1ArrayOfbyte[j++]) & 0xFF) << 2;
          this.tailLen -= b1;
          arrayOfByte2[b++] = arrayOfByte1[k >> 12 & 0x3F];
          arrayOfByte2[b++] = arrayOfByte1[k >> 6 & 0x3F];
          arrayOfByte2[b++] = arrayOfByte1[k & 0x3F];
          if (this.do_padding)
            arrayOfByte2[b++] = 61; 
          if (this.do_newline) {
            if (this.do_cr)
              arrayOfByte2[b++] = 13; 
            arrayOfByte2[b++] = 10;
          } 
        } else if (this.do_newline && b > 0 && i != 19) {
          if (this.do_cr)
            arrayOfByte2[b++] = 13; 
          arrayOfByte2[b++] = 10;
        } 
        assert this.tailLen == 0;
        assert j == param1Int2;
      } else if (j == param1Int2 - 1) {
        this.tail[this.tailLen++] = param1ArrayOfbyte[j];
      } else if (j == param1Int2 - 2) {
        this.tail[this.tailLen++] = param1ArrayOfbyte[j];
        this.tail[this.tailLen++] = param1ArrayOfbyte[j + 1];
      } 
      this.op = b;
      this.count = i;
      return true;
    }
  }
  
  static abstract class Coder {
    public byte[] output;
    
    public int op;
    
    public abstract boolean process(byte[] param1ArrayOfbyte, int param1Int1, int param1Int2, boolean param1Boolean);
    
    public abstract int maxOutputSize(int param1Int);
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\io\sentry\vendor\Base64.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */