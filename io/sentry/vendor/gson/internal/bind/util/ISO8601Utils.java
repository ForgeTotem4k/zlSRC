package io.sentry.vendor.gson.internal.bind.util;

import java.text.ParseException;
import java.text.ParsePosition;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;
import org.jetbrains.annotations.ApiStatus.Internal;

@Internal
public class ISO8601Utils {
  private static final String UTC_ID = "UTC";
  
  public static final TimeZone TIMEZONE_UTC = TimeZone.getTimeZone("UTC");
  
  public static String format(Date paramDate) {
    return format(paramDate, false, TIMEZONE_UTC);
  }
  
  public static String format(Date paramDate, boolean paramBoolean) {
    return format(paramDate, paramBoolean, TIMEZONE_UTC);
  }
  
  public static String format(Date paramDate, boolean paramBoolean, TimeZone paramTimeZone) {
    GregorianCalendar gregorianCalendar = new GregorianCalendar(paramTimeZone, Locale.US);
    gregorianCalendar.setTime(paramDate);
    int i = "yyyy-MM-ddThh:mm:ss".length();
    i += paramBoolean ? ".sss".length() : 0;
    i += (paramTimeZone.getRawOffset() == 0) ? "Z".length() : "+hh:mm".length();
    StringBuilder stringBuilder = new StringBuilder(i);
    padInt(stringBuilder, gregorianCalendar.get(1), "yyyy".length());
    stringBuilder.append('-');
    padInt(stringBuilder, gregorianCalendar.get(2) + 1, "MM".length());
    stringBuilder.append('-');
    padInt(stringBuilder, gregorianCalendar.get(5), "dd".length());
    stringBuilder.append('T');
    padInt(stringBuilder, gregorianCalendar.get(11), "hh".length());
    stringBuilder.append(':');
    padInt(stringBuilder, gregorianCalendar.get(12), "mm".length());
    stringBuilder.append(':');
    padInt(stringBuilder, gregorianCalendar.get(13), "ss".length());
    if (paramBoolean) {
      stringBuilder.append('.');
      padInt(stringBuilder, gregorianCalendar.get(14), "sss".length());
    } 
    int j = paramTimeZone.getOffset(gregorianCalendar.getTimeInMillis());
    if (j != 0) {
      int k = Math.abs(j / 60000 / 60);
      int m = Math.abs(j / 60000 % 60);
      stringBuilder.append((j < 0) ? 45 : 43);
      padInt(stringBuilder, k, "hh".length());
      stringBuilder.append(':');
      padInt(stringBuilder, m, "mm".length());
    } else {
      stringBuilder.append('Z');
    } 
    return stringBuilder.toString();
  }
  
  public static Date parse(String paramString, ParsePosition paramParsePosition) throws ParseException {
    // Byte code:
    //   0: aconst_null
    //   1: astore_2
    //   2: aload_1
    //   3: invokevirtual getIndex : ()I
    //   6: istore_3
    //   7: aload_0
    //   8: iload_3
    //   9: iinc #3, 4
    //   12: iload_3
    //   13: invokestatic parseInt : (Ljava/lang/String;II)I
    //   16: istore #4
    //   18: aload_0
    //   19: iload_3
    //   20: bipush #45
    //   22: invokestatic checkOffset : (Ljava/lang/String;IC)Z
    //   25: ifeq -> 31
    //   28: iinc #3, 1
    //   31: aload_0
    //   32: iload_3
    //   33: iinc #3, 2
    //   36: iload_3
    //   37: invokestatic parseInt : (Ljava/lang/String;II)I
    //   40: istore #5
    //   42: aload_0
    //   43: iload_3
    //   44: bipush #45
    //   46: invokestatic checkOffset : (Ljava/lang/String;IC)Z
    //   49: ifeq -> 55
    //   52: iinc #3, 1
    //   55: aload_0
    //   56: iload_3
    //   57: iinc #3, 2
    //   60: iload_3
    //   61: invokestatic parseInt : (Ljava/lang/String;II)I
    //   64: istore #6
    //   66: iconst_0
    //   67: istore #7
    //   69: iconst_0
    //   70: istore #8
    //   72: iconst_0
    //   73: istore #9
    //   75: iconst_0
    //   76: istore #10
    //   78: aload_0
    //   79: iload_3
    //   80: bipush #84
    //   82: invokestatic checkOffset : (Ljava/lang/String;IC)Z
    //   85: istore #11
    //   87: iload #11
    //   89: ifne -> 128
    //   92: aload_0
    //   93: invokevirtual length : ()I
    //   96: iload_3
    //   97: if_icmpgt -> 128
    //   100: new java/util/GregorianCalendar
    //   103: dup
    //   104: iload #4
    //   106: iload #5
    //   108: iconst_1
    //   109: isub
    //   110: iload #6
    //   112: invokespecial <init> : (III)V
    //   115: astore #12
    //   117: aload_1
    //   118: iload_3
    //   119: invokevirtual setIndex : (I)V
    //   122: aload #12
    //   124: invokevirtual getTime : ()Ljava/util/Date;
    //   127: areturn
    //   128: iload #11
    //   130: ifeq -> 347
    //   133: aload_0
    //   134: iinc #3, 1
    //   137: iload_3
    //   138: iinc #3, 2
    //   141: iload_3
    //   142: invokestatic parseInt : (Ljava/lang/String;II)I
    //   145: istore #7
    //   147: aload_0
    //   148: iload_3
    //   149: bipush #58
    //   151: invokestatic checkOffset : (Ljava/lang/String;IC)Z
    //   154: ifeq -> 160
    //   157: iinc #3, 1
    //   160: aload_0
    //   161: iload_3
    //   162: iinc #3, 2
    //   165: iload_3
    //   166: invokestatic parseInt : (Ljava/lang/String;II)I
    //   169: istore #8
    //   171: aload_0
    //   172: iload_3
    //   173: bipush #58
    //   175: invokestatic checkOffset : (Ljava/lang/String;IC)Z
    //   178: ifeq -> 184
    //   181: iinc #3, 1
    //   184: aload_0
    //   185: invokevirtual length : ()I
    //   188: iload_3
    //   189: if_icmple -> 347
    //   192: aload_0
    //   193: iload_3
    //   194: invokevirtual charAt : (I)C
    //   197: istore #12
    //   199: iload #12
    //   201: bipush #90
    //   203: if_icmpeq -> 347
    //   206: iload #12
    //   208: bipush #43
    //   210: if_icmpeq -> 347
    //   213: iload #12
    //   215: bipush #45
    //   217: if_icmpeq -> 347
    //   220: aload_0
    //   221: iload_3
    //   222: iinc #3, 2
    //   225: iload_3
    //   226: invokestatic parseInt : (Ljava/lang/String;II)I
    //   229: istore #9
    //   231: iload #9
    //   233: bipush #59
    //   235: if_icmple -> 249
    //   238: iload #9
    //   240: bipush #63
    //   242: if_icmpge -> 249
    //   245: bipush #59
    //   247: istore #9
    //   249: aload_0
    //   250: iload_3
    //   251: bipush #46
    //   253: invokestatic checkOffset : (Ljava/lang/String;IC)Z
    //   256: ifeq -> 347
    //   259: iinc #3, 1
    //   262: aload_0
    //   263: iload_3
    //   264: iconst_1
    //   265: iadd
    //   266: invokestatic indexOfNonDigit : (Ljava/lang/String;I)I
    //   269: istore #13
    //   271: iload #13
    //   273: iload_3
    //   274: iconst_3
    //   275: iadd
    //   276: invokestatic min : (II)I
    //   279: istore #14
    //   281: aload_0
    //   282: iload_3
    //   283: iload #14
    //   285: invokestatic parseInt : (Ljava/lang/String;II)I
    //   288: istore #15
    //   290: iload #14
    //   292: iload_3
    //   293: isub
    //   294: lookupswitch default -> 340, 1 -> 330, 2 -> 320
    //   320: iload #15
    //   322: bipush #10
    //   324: imul
    //   325: istore #10
    //   327: goto -> 344
    //   330: iload #15
    //   332: bipush #100
    //   334: imul
    //   335: istore #10
    //   337: goto -> 344
    //   340: iload #15
    //   342: istore #10
    //   344: iload #13
    //   346: istore_3
    //   347: aload_0
    //   348: invokevirtual length : ()I
    //   351: iload_3
    //   352: if_icmpgt -> 365
    //   355: new java/lang/IllegalArgumentException
    //   358: dup
    //   359: ldc 'No time zone indicator'
    //   361: invokespecial <init> : (Ljava/lang/String;)V
    //   364: athrow
    //   365: aconst_null
    //   366: astore #12
    //   368: aload_0
    //   369: iload_3
    //   370: invokevirtual charAt : (I)C
    //   373: istore #13
    //   375: iload #13
    //   377: bipush #90
    //   379: if_icmpne -> 393
    //   382: getstatic io/sentry/vendor/gson/internal/bind/util/ISO8601Utils.TIMEZONE_UTC : Ljava/util/TimeZone;
    //   385: astore #12
    //   387: iinc #3, 1
    //   390: goto -> 630
    //   393: iload #13
    //   395: bipush #43
    //   397: if_icmpeq -> 407
    //   400: iload #13
    //   402: bipush #45
    //   404: if_icmpne -> 597
    //   407: aload_0
    //   408: iload_3
    //   409: invokevirtual substring : (I)Ljava/lang/String;
    //   412: astore #14
    //   414: aload #14
    //   416: invokevirtual length : ()I
    //   419: iconst_5
    //   420: if_icmplt -> 428
    //   423: aload #14
    //   425: goto -> 448
    //   428: new java/lang/StringBuilder
    //   431: dup
    //   432: invokespecial <init> : ()V
    //   435: aload #14
    //   437: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   440: ldc '00'
    //   442: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   445: invokevirtual toString : ()Ljava/lang/String;
    //   448: astore #14
    //   450: iload_3
    //   451: aload #14
    //   453: invokevirtual length : ()I
    //   456: iadd
    //   457: istore_3
    //   458: ldc '+0000'
    //   460: aload #14
    //   462: invokevirtual equals : (Ljava/lang/Object;)Z
    //   465: ifne -> 478
    //   468: ldc '+00:00'
    //   470: aload #14
    //   472: invokevirtual equals : (Ljava/lang/Object;)Z
    //   475: ifeq -> 486
    //   478: getstatic io/sentry/vendor/gson/internal/bind/util/ISO8601Utils.TIMEZONE_UTC : Ljava/util/TimeZone;
    //   481: astore #12
    //   483: goto -> 594
    //   486: new java/lang/StringBuilder
    //   489: dup
    //   490: invokespecial <init> : ()V
    //   493: ldc 'GMT'
    //   495: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   498: aload #14
    //   500: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   503: invokevirtual toString : ()Ljava/lang/String;
    //   506: astore #15
    //   508: aload #15
    //   510: invokestatic getTimeZone : (Ljava/lang/String;)Ljava/util/TimeZone;
    //   513: astore #12
    //   515: aload #12
    //   517: invokevirtual getID : ()Ljava/lang/String;
    //   520: astore #16
    //   522: aload #16
    //   524: aload #15
    //   526: invokevirtual equals : (Ljava/lang/Object;)Z
    //   529: ifne -> 594
    //   532: aload #16
    //   534: ldc ':'
    //   536: ldc ''
    //   538: invokevirtual replace : (Ljava/lang/CharSequence;Ljava/lang/CharSequence;)Ljava/lang/String;
    //   541: astore #17
    //   543: aload #17
    //   545: aload #15
    //   547: invokevirtual equals : (Ljava/lang/Object;)Z
    //   550: ifne -> 594
    //   553: new java/lang/IndexOutOfBoundsException
    //   556: dup
    //   557: new java/lang/StringBuilder
    //   560: dup
    //   561: invokespecial <init> : ()V
    //   564: ldc 'Mismatching time zone indicator: '
    //   566: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   569: aload #15
    //   571: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   574: ldc ' given, resolves to '
    //   576: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   579: aload #12
    //   581: invokevirtual getID : ()Ljava/lang/String;
    //   584: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   587: invokevirtual toString : ()Ljava/lang/String;
    //   590: invokespecial <init> : (Ljava/lang/String;)V
    //   593: athrow
    //   594: goto -> 630
    //   597: new java/lang/IndexOutOfBoundsException
    //   600: dup
    //   601: new java/lang/StringBuilder
    //   604: dup
    //   605: invokespecial <init> : ()V
    //   608: ldc 'Invalid time zone indicator ''
    //   610: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   613: iload #13
    //   615: invokevirtual append : (C)Ljava/lang/StringBuilder;
    //   618: ldc '''
    //   620: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   623: invokevirtual toString : ()Ljava/lang/String;
    //   626: invokespecial <init> : (Ljava/lang/String;)V
    //   629: athrow
    //   630: new java/util/GregorianCalendar
    //   633: dup
    //   634: aload #12
    //   636: invokespecial <init> : (Ljava/util/TimeZone;)V
    //   639: astore #14
    //   641: aload #14
    //   643: iconst_0
    //   644: invokevirtual setLenient : (Z)V
    //   647: aload #14
    //   649: iconst_1
    //   650: iload #4
    //   652: invokevirtual set : (II)V
    //   655: aload #14
    //   657: iconst_2
    //   658: iload #5
    //   660: iconst_1
    //   661: isub
    //   662: invokevirtual set : (II)V
    //   665: aload #14
    //   667: iconst_5
    //   668: iload #6
    //   670: invokevirtual set : (II)V
    //   673: aload #14
    //   675: bipush #11
    //   677: iload #7
    //   679: invokevirtual set : (II)V
    //   682: aload #14
    //   684: bipush #12
    //   686: iload #8
    //   688: invokevirtual set : (II)V
    //   691: aload #14
    //   693: bipush #13
    //   695: iload #9
    //   697: invokevirtual set : (II)V
    //   700: aload #14
    //   702: bipush #14
    //   704: iload #10
    //   706: invokevirtual set : (II)V
    //   709: aload_1
    //   710: iload_3
    //   711: invokevirtual setIndex : (I)V
    //   714: aload #14
    //   716: invokevirtual getTime : ()Ljava/util/Date;
    //   719: areturn
    //   720: astore_3
    //   721: aload_3
    //   722: astore_2
    //   723: goto -> 735
    //   726: astore_3
    //   727: aload_3
    //   728: astore_2
    //   729: goto -> 735
    //   732: astore_3
    //   733: aload_3
    //   734: astore_2
    //   735: aload_0
    //   736: ifnonnull -> 743
    //   739: aconst_null
    //   740: goto -> 767
    //   743: new java/lang/StringBuilder
    //   746: dup
    //   747: invokespecial <init> : ()V
    //   750: bipush #34
    //   752: invokevirtual append : (C)Ljava/lang/StringBuilder;
    //   755: aload_0
    //   756: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   759: bipush #34
    //   761: invokevirtual append : (C)Ljava/lang/StringBuilder;
    //   764: invokevirtual toString : ()Ljava/lang/String;
    //   767: astore_3
    //   768: aload_2
    //   769: invokevirtual getMessage : ()Ljava/lang/String;
    //   772: astore #4
    //   774: aload #4
    //   776: ifnull -> 787
    //   779: aload #4
    //   781: invokevirtual isEmpty : ()Z
    //   784: ifeq -> 819
    //   787: new java/lang/StringBuilder
    //   790: dup
    //   791: invokespecial <init> : ()V
    //   794: ldc '('
    //   796: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   799: aload_2
    //   800: invokevirtual getClass : ()Ljava/lang/Class;
    //   803: invokevirtual getName : ()Ljava/lang/String;
    //   806: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   809: ldc ')'
    //   811: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   814: invokevirtual toString : ()Ljava/lang/String;
    //   817: astore #4
    //   819: new java/text/ParseException
    //   822: dup
    //   823: new java/lang/StringBuilder
    //   826: dup
    //   827: invokespecial <init> : ()V
    //   830: ldc 'Failed to parse date ['
    //   832: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   835: aload_3
    //   836: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   839: ldc ']: '
    //   841: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   844: aload #4
    //   846: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   849: invokevirtual toString : ()Ljava/lang/String;
    //   852: aload_1
    //   853: invokevirtual getIndex : ()I
    //   856: invokespecial <init> : (Ljava/lang/String;I)V
    //   859: astore #5
    //   861: aload #5
    //   863: aload_2
    //   864: invokevirtual initCause : (Ljava/lang/Throwable;)Ljava/lang/Throwable;
    //   867: pop
    //   868: aload #5
    //   870: athrow
    // Exception table:
    //   from	to	target	type
    //   2	127	720	java/lang/IndexOutOfBoundsException
    //   2	127	726	java/lang/NumberFormatException
    //   2	127	732	java/lang/IllegalArgumentException
    //   128	719	720	java/lang/IndexOutOfBoundsException
    //   128	719	726	java/lang/NumberFormatException
    //   128	719	732	java/lang/IllegalArgumentException
  }
  
  private static boolean checkOffset(String paramString, int paramInt, char paramChar) {
    return (paramInt < paramString.length() && paramString.charAt(paramInt) == paramChar);
  }
  
  private static int parseInt(String paramString, int paramInt1, int paramInt2) throws NumberFormatException {
    if (paramInt1 < 0 || paramInt2 > paramString.length() || paramInt1 > paramInt2)
      throw new NumberFormatException(paramString); 
    int i = paramInt1;
    int j = 0;
    if (i < paramInt2) {
      int k = Character.digit(paramString.charAt(i++), 10);
      if (k < 0)
        throw new NumberFormatException("Invalid number: " + paramString.substring(paramInt1, paramInt2)); 
      j = -k;
    } 
    while (i < paramInt2) {
      int k = Character.digit(paramString.charAt(i++), 10);
      if (k < 0)
        throw new NumberFormatException("Invalid number: " + paramString.substring(paramInt1, paramInt2)); 
      j *= 10;
      j -= k;
    } 
    return -j;
  }
  
  private static void padInt(StringBuilder paramStringBuilder, int paramInt1, int paramInt2) {
    String str = Integer.toString(paramInt1);
    for (int i = paramInt2 - str.length(); i > 0; i--)
      paramStringBuilder.append('0'); 
    paramStringBuilder.append(str);
  }
  
  private static int indexOfNonDigit(String paramString, int paramInt) {
    for (int i = paramInt; i < paramString.length(); i++) {
      char c = paramString.charAt(i);
      if (c < '0' || c > '9')
        return i; 
    } 
    return paramString.length();
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\io\sentry\vendor\gson\internal\bin\\util\ISO8601Utils.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */