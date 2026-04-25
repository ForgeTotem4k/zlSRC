package com.sun.jna;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

class ELFAnalyser {
  private static final byte[] ELF_MAGIC = new byte[] { Byte.MAX_VALUE, 69, 76, 70 };
  
  private static final int EF_ARM_ABI_FLOAT_HARD = 1024;
  
  private static final int EF_ARM_ABI_FLOAT_SOFT = 512;
  
  private static final int EI_DATA_BIG_ENDIAN = 2;
  
  private static final int E_MACHINE_ARM = 40;
  
  private static final int EI_CLASS_64BIT = 2;
  
  private static final int SHN_UNDEF = 0;
  
  private static final int SHN_XINDEX = 65535;
  
  private final String filename;
  
  private boolean ELF = false;
  
  private boolean _64Bit = false;
  
  private boolean bigEndian = false;
  
  private boolean armHardFloatFlag = false;
  
  private boolean armSoftFloatFlag = false;
  
  private boolean armEabiAapcsVfp = false;
  
  private boolean arm = false;
  
  public static ELFAnalyser analyse(String paramString) throws IOException {
    ELFAnalyser eLFAnalyser = new ELFAnalyser(paramString);
    eLFAnalyser.runDetection();
    return eLFAnalyser;
  }
  
  public boolean isELF() {
    return this.ELF;
  }
  
  public boolean is64Bit() {
    return this._64Bit;
  }
  
  public boolean isBigEndian() {
    return this.bigEndian;
  }
  
  public String getFilename() {
    return this.filename;
  }
  
  public boolean isArmHardFloat() {
    return (isArmEabiAapcsVfp() || isArmHardFloatFlag());
  }
  
  public boolean isArmEabiAapcsVfp() {
    return this.armEabiAapcsVfp;
  }
  
  public boolean isArmHardFloatFlag() {
    return this.armHardFloatFlag;
  }
  
  public boolean isArmSoftFloatFlag() {
    return this.armSoftFloatFlag;
  }
  
  public boolean isArm() {
    return this.arm;
  }
  
  private ELFAnalyser(String paramString) {
    this.filename = paramString;
  }
  
  private void runDetection() throws IOException {
    RandomAccessFile randomAccessFile = new RandomAccessFile(this.filename, "r");
    try {
      if (randomAccessFile.length() > 4L) {
        byte[] arrayOfByte = new byte[4];
        randomAccessFile.seek(0L);
        randomAccessFile.read(arrayOfByte);
        if (Arrays.equals(arrayOfByte, ELF_MAGIC))
          this.ELF = true; 
      } 
      if (!this.ELF)
        return; 
      randomAccessFile.seek(4L);
      byte b1 = randomAccessFile.readByte();
      byte b2 = randomAccessFile.readByte();
      this._64Bit = (b1 == 2);
      this.bigEndian = (b2 == 2);
      randomAccessFile.seek(0L);
      ByteBuffer byteBuffer = ByteBuffer.allocate(this._64Bit ? 64 : 52);
      randomAccessFile.getChannel().read(byteBuffer, 0L);
      byteBuffer.order(this.bigEndian ? ByteOrder.BIG_ENDIAN : ByteOrder.LITTLE_ENDIAN);
      this.arm = (byteBuffer.get(18) == 40);
      if (this.arm) {
        int i = byteBuffer.getInt(this._64Bit ? 48 : 36);
        this.armHardFloatFlag = ((i & 0x400) == 1024);
        this.armSoftFloatFlag = ((i & 0x200) == 512);
        parseEabiAapcsVfp(byteBuffer, randomAccessFile);
      } 
    } finally {
      try {
        randomAccessFile.close();
      } catch (IOException iOException) {}
    } 
  }
  
  private void parseEabiAapcsVfp(ByteBuffer paramByteBuffer, RandomAccessFile paramRandomAccessFile) throws IOException {
    ELFSectionHeaders eLFSectionHeaders = new ELFSectionHeaders(this._64Bit, this.bigEndian, paramByteBuffer, paramRandomAccessFile);
    for (ELFSectionHeaderEntry eLFSectionHeaderEntry : eLFSectionHeaders.getEntries()) {
      if (".ARM.attributes".equals(eLFSectionHeaderEntry.getName())) {
        ByteBuffer byteBuffer = ByteBuffer.allocate((int)eLFSectionHeaderEntry.getSize());
        byteBuffer.order(this.bigEndian ? ByteOrder.BIG_ENDIAN : ByteOrder.LITTLE_ENDIAN);
        paramRandomAccessFile.getChannel().read(byteBuffer, eLFSectionHeaderEntry.getOffset());
        byteBuffer.rewind();
        Map<Integer, Map<ArmAeabiAttributesTag, Object>> map = parseArmAttributes(byteBuffer);
        Map map1 = map.get(Integer.valueOf(1));
        if (map1 == null)
          continue; 
        Object object = map1.get(ArmAeabiAttributesTag.ABI_VFP_args);
        if (object instanceof Integer && ((Integer)object).equals(Integer.valueOf(1))) {
          this.armEabiAapcsVfp = true;
          continue;
        } 
        if (object instanceof BigInteger && ((BigInteger)object).intValue() == 1)
          this.armEabiAapcsVfp = true; 
      } 
    } 
  }
  
  private static Map<Integer, Map<ArmAeabiAttributesTag, Object>> parseArmAttributes(ByteBuffer paramByteBuffer) {
    byte b = paramByteBuffer.get();
    if (b != 65)
      return Collections.EMPTY_MAP; 
    while (paramByteBuffer.position() < paramByteBuffer.limit()) {
      int i = paramByteBuffer.position();
      int j = paramByteBuffer.getInt();
      if (j <= 0)
        break; 
      String str = readNTBS(paramByteBuffer, null);
      if ("aeabi".equals(str))
        return parseAEABI(paramByteBuffer); 
      paramByteBuffer.position(i + j);
    } 
    return Collections.EMPTY_MAP;
  }
  
  private static Map<Integer, Map<ArmAeabiAttributesTag, Object>> parseAEABI(ByteBuffer paramByteBuffer) {
    HashMap<Object, Object> hashMap = new HashMap<>();
    while (paramByteBuffer.position() < paramByteBuffer.limit()) {
      int i = paramByteBuffer.position();
      int j = readULEB128(paramByteBuffer).intValue();
      int k = paramByteBuffer.getInt();
      if (j == 1)
        hashMap.put(Integer.valueOf(j), parseFileAttribute(paramByteBuffer)); 
      paramByteBuffer.position(i + k);
    } 
    return (Map)hashMap;
  }
  
  private static Map<ArmAeabiAttributesTag, Object> parseFileAttribute(ByteBuffer paramByteBuffer) {
    HashMap<Object, Object> hashMap = new HashMap<>();
    while (paramByteBuffer.position() < paramByteBuffer.limit()) {
      int i = readULEB128(paramByteBuffer).intValue();
      ArmAeabiAttributesTag armAeabiAttributesTag = ArmAeabiAttributesTag.getByValue(i);
      switch (armAeabiAttributesTag.getParameterType().ordinal()) {
        case 0:
          hashMap.put(armAeabiAttributesTag, Integer.valueOf(paramByteBuffer.getInt()));
        case 1:
          hashMap.put(armAeabiAttributesTag, readNTBS(paramByteBuffer, null));
        case 2:
          hashMap.put(armAeabiAttributesTag, readULEB128(paramByteBuffer));
      } 
    } 
    return (Map)hashMap;
  }
  
  private static String readNTBS(ByteBuffer paramByteBuffer, Integer paramInteger) {
    byte b;
    if (paramInteger != null)
      paramByteBuffer.position(paramInteger.intValue()); 
    int i = paramByteBuffer.position();
    do {
      b = paramByteBuffer.get();
    } while (b != 0 && paramByteBuffer.position() <= paramByteBuffer.limit());
    int j = paramByteBuffer.position();
    byte[] arrayOfByte = new byte[j - i - 1];
    paramByteBuffer.position(i);
    paramByteBuffer.get(arrayOfByte);
    paramByteBuffer.position(paramByteBuffer.position() + 1);
    try {
      return new String(arrayOfByte, "ASCII");
    } catch (UnsupportedEncodingException unsupportedEncodingException) {
      throw new RuntimeException(unsupportedEncodingException);
    } 
  }
  
  private static BigInteger readULEB128(ByteBuffer paramByteBuffer) {
    BigInteger bigInteger = BigInteger.ZERO;
    for (boolean bool = false;; bool += true) {
      byte b = paramByteBuffer.get();
      bigInteger = bigInteger.or(BigInteger.valueOf((b & Byte.MAX_VALUE)).shiftLeft(bool));
      if ((b & 0x80) == 0)
        return bigInteger; 
    } 
  }
  
  static class ELFSectionHeaders {
    private final List<ELFAnalyser.ELFSectionHeaderEntry> entries;
    
    public ELFSectionHeaders(boolean param1Boolean1, boolean param1Boolean2, ByteBuffer param1ByteBuffer, RandomAccessFile param1RandomAccessFile) throws IOException {
      long l;
      short s1;
      short s2;
      int i;
      short s;
      this.entries = new ArrayList<>();
      if (param1Boolean1) {
        l = param1ByteBuffer.getLong(40);
        s1 = param1ByteBuffer.getShort(58);
        s2 = param1ByteBuffer.getShort(60);
        i = param1ByteBuffer.getShort(62);
      } else {
        l = param1ByteBuffer.getInt(32);
        s1 = param1ByteBuffer.getShort(46);
        s2 = param1ByteBuffer.getShort(48);
        i = param1ByteBuffer.getShort(50);
      } 
      ByteBuffer byteBuffer1 = ByteBuffer.allocate(s1);
      param1RandomAccessFile.getChannel().read(byteBuffer1, l);
      ELFAnalyser.ELFSectionHeaderEntry eLFSectionHeaderEntry1 = new ELFAnalyser.ELFSectionHeaderEntry(param1Boolean1, byteBuffer1);
      if (s2 == 0 && l != 0L) {
        s = (int)eLFSectionHeaderEntry1.getSize();
      } else {
        s = s2;
      } 
      if (i == 65535)
        i = eLFSectionHeaderEntry1.getLink(); 
      int j = s * s1;
      if (j == 0 || i == 0)
        return; 
      ByteBuffer byteBuffer2 = ByteBuffer.allocate(j);
      byteBuffer2.order(param1Boolean2 ? ByteOrder.BIG_ENDIAN : ByteOrder.LITTLE_ENDIAN);
      param1RandomAccessFile.getChannel().read(byteBuffer2, l);
      for (byte b = 0; b < s2; b++) {
        byteBuffer2.position(b * s1);
        ByteBuffer byteBuffer = byteBuffer2.slice();
        byteBuffer.order(byteBuffer2.order());
        byteBuffer.limit(s1);
        this.entries.add(new ELFAnalyser.ELFSectionHeaderEntry(param1Boolean1, byteBuffer));
      } 
      ELFAnalyser.ELFSectionHeaderEntry eLFSectionHeaderEntry2 = this.entries.get(i);
      ByteBuffer byteBuffer3 = ByteBuffer.allocate((int)eLFSectionHeaderEntry2.getSize());
      byteBuffer3.order(param1Boolean2 ? ByteOrder.BIG_ENDIAN : ByteOrder.LITTLE_ENDIAN);
      param1RandomAccessFile.getChannel().read(byteBuffer3, eLFSectionHeaderEntry2.getOffset());
      byteBuffer3.rewind();
      ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(20);
      for (ELFAnalyser.ELFSectionHeaderEntry eLFSectionHeaderEntry : this.entries) {
        byteArrayOutputStream.reset();
        byteBuffer3.position(eLFSectionHeaderEntry.getNameOffset());
        while (byteBuffer3.position() < byteBuffer3.limit()) {
          byte b1 = byteBuffer3.get();
          if (b1 == 0)
            break; 
          byteArrayOutputStream.write(b1);
        } 
        eLFSectionHeaderEntry.setName(byteArrayOutputStream.toString("ASCII"));
      } 
    }
    
    public List<ELFAnalyser.ELFSectionHeaderEntry> getEntries() {
      return this.entries;
    }
  }
  
  static class ELFSectionHeaderEntry {
    private final int nameOffset;
    
    private String name;
    
    private final int type;
    
    private final long flags;
    
    private final long addr;
    
    private final long offset;
    
    private final long size;
    
    private final int link;
    
    public ELFSectionHeaderEntry(boolean param1Boolean, ByteBuffer param1ByteBuffer) {
      this.nameOffset = param1ByteBuffer.getInt(0);
      this.type = param1ByteBuffer.getInt(4);
      this.flags = param1Boolean ? param1ByteBuffer.getLong(8) : param1ByteBuffer.getInt(8);
      this.addr = param1Boolean ? param1ByteBuffer.getLong(16) : param1ByteBuffer.getInt(12);
      this.offset = param1Boolean ? param1ByteBuffer.getLong(24) : param1ByteBuffer.getInt(16);
      this.size = param1Boolean ? param1ByteBuffer.getLong(32) : param1ByteBuffer.getInt(20);
      this.link = param1ByteBuffer.getInt(param1Boolean ? 40 : 24);
    }
    
    public String getName() {
      return this.name;
    }
    
    public void setName(String param1String) {
      this.name = param1String;
    }
    
    public int getNameOffset() {
      return this.nameOffset;
    }
    
    public int getType() {
      return this.type;
    }
    
    public long getFlags() {
      return this.flags;
    }
    
    public long getOffset() {
      return this.offset;
    }
    
    public long getSize() {
      return this.size;
    }
    
    public long getAddr() {
      return this.addr;
    }
    
    public int getLink() {
      return this.link;
    }
    
    public String toString() {
      return String.format("ELFSectionHeaderEntry{nameOffset=%1$d (0x%1$x), name=%2$s, type=%3$d (0x%3$x), flags=%4$d (0x%4$x), addr=%5$d (0x%5$x), offset=%6$d (0x%6$x), size=%7$d (0x%7$x), link=%8$d (0x%8$x)}", new Object[] { Integer.valueOf(this.nameOffset), this.name, Integer.valueOf(this.type), Long.valueOf(this.flags), Long.valueOf(this.addr), Long.valueOf(this.offset), Long.valueOf(this.size), Integer.valueOf(this.link) });
    }
  }
  
  static class ArmAeabiAttributesTag {
    private final int value;
    
    private final String name;
    
    private final ParameterType parameterType;
    
    private static final List<ArmAeabiAttributesTag> tags = new LinkedList<>();
    
    private static final Map<Integer, ArmAeabiAttributesTag> valueMap = new HashMap<>();
    
    private static final Map<String, ArmAeabiAttributesTag> nameMap = new HashMap<>();
    
    public static final ArmAeabiAttributesTag File = addTag(1, "File", ParameterType.UINT32);
    
    public static final ArmAeabiAttributesTag Section = addTag(2, "Section", ParameterType.UINT32);
    
    public static final ArmAeabiAttributesTag Symbol = addTag(3, "Symbol", ParameterType.UINT32);
    
    public static final ArmAeabiAttributesTag CPU_raw_name = addTag(4, "CPU_raw_name", ParameterType.NTBS);
    
    public static final ArmAeabiAttributesTag CPU_name = addTag(5, "CPU_name", ParameterType.NTBS);
    
    public static final ArmAeabiAttributesTag CPU_arch = addTag(6, "CPU_arch", ParameterType.ULEB128);
    
    public static final ArmAeabiAttributesTag CPU_arch_profile = addTag(7, "CPU_arch_profile", ParameterType.ULEB128);
    
    public static final ArmAeabiAttributesTag ARM_ISA_use = addTag(8, "ARM_ISA_use", ParameterType.ULEB128);
    
    public static final ArmAeabiAttributesTag THUMB_ISA_use = addTag(9, "THUMB_ISA_use", ParameterType.ULEB128);
    
    public static final ArmAeabiAttributesTag FP_arch = addTag(10, "FP_arch", ParameterType.ULEB128);
    
    public static final ArmAeabiAttributesTag WMMX_arch = addTag(11, "WMMX_arch", ParameterType.ULEB128);
    
    public static final ArmAeabiAttributesTag Advanced_SIMD_arch = addTag(12, "Advanced_SIMD_arch", ParameterType.ULEB128);
    
    public static final ArmAeabiAttributesTag PCS_config = addTag(13, "PCS_config", ParameterType.ULEB128);
    
    public static final ArmAeabiAttributesTag ABI_PCS_R9_use = addTag(14, "ABI_PCS_R9_use", ParameterType.ULEB128);
    
    public static final ArmAeabiAttributesTag ABI_PCS_RW_data = addTag(15, "ABI_PCS_RW_data", ParameterType.ULEB128);
    
    public static final ArmAeabiAttributesTag ABI_PCS_RO_data = addTag(16, "ABI_PCS_RO_data", ParameterType.ULEB128);
    
    public static final ArmAeabiAttributesTag ABI_PCS_GOT_use = addTag(17, "ABI_PCS_GOT_use", ParameterType.ULEB128);
    
    public static final ArmAeabiAttributesTag ABI_PCS_wchar_t = addTag(18, "ABI_PCS_wchar_t", ParameterType.ULEB128);
    
    public static final ArmAeabiAttributesTag ABI_FP_rounding = addTag(19, "ABI_FP_rounding", ParameterType.ULEB128);
    
    public static final ArmAeabiAttributesTag ABI_FP_denormal = addTag(20, "ABI_FP_denormal", ParameterType.ULEB128);
    
    public static final ArmAeabiAttributesTag ABI_FP_exceptions = addTag(21, "ABI_FP_exceptions", ParameterType.ULEB128);
    
    public static final ArmAeabiAttributesTag ABI_FP_user_exceptions = addTag(22, "ABI_FP_user_exceptions", ParameterType.ULEB128);
    
    public static final ArmAeabiAttributesTag ABI_FP_number_model = addTag(23, "ABI_FP_number_model", ParameterType.ULEB128);
    
    public static final ArmAeabiAttributesTag ABI_align_needed = addTag(24, "ABI_align_needed", ParameterType.ULEB128);
    
    public static final ArmAeabiAttributesTag ABI_align8_preserved = addTag(25, "ABI_align8_preserved", ParameterType.ULEB128);
    
    public static final ArmAeabiAttributesTag ABI_enum_size = addTag(26, "ABI_enum_size", ParameterType.ULEB128);
    
    public static final ArmAeabiAttributesTag ABI_HardFP_use = addTag(27, "ABI_HardFP_use", ParameterType.ULEB128);
    
    public static final ArmAeabiAttributesTag ABI_VFP_args = addTag(28, "ABI_VFP_args", ParameterType.ULEB128);
    
    public static final ArmAeabiAttributesTag ABI_WMMX_args = addTag(29, "ABI_WMMX_args", ParameterType.ULEB128);
    
    public static final ArmAeabiAttributesTag ABI_optimization_goals = addTag(30, "ABI_optimization_goals", ParameterType.ULEB128);
    
    public static final ArmAeabiAttributesTag ABI_FP_optimization_goals = addTag(31, "ABI_FP_optimization_goals", ParameterType.ULEB128);
    
    public static final ArmAeabiAttributesTag compatibility = addTag(32, "compatibility", ParameterType.NTBS);
    
    public static final ArmAeabiAttributesTag CPU_unaligned_access = addTag(34, "CPU_unaligned_access", ParameterType.ULEB128);
    
    public static final ArmAeabiAttributesTag FP_HP_extension = addTag(36, "FP_HP_extension", ParameterType.ULEB128);
    
    public static final ArmAeabiAttributesTag ABI_FP_16bit_format = addTag(38, "ABI_FP_16bit_format", ParameterType.ULEB128);
    
    public static final ArmAeabiAttributesTag MPextension_use = addTag(42, "MPextension_use", ParameterType.ULEB128);
    
    public static final ArmAeabiAttributesTag DIV_use = addTag(44, "DIV_use", ParameterType.ULEB128);
    
    public static final ArmAeabiAttributesTag nodefaults = addTag(64, "nodefaults", ParameterType.ULEB128);
    
    public static final ArmAeabiAttributesTag also_compatible_with = addTag(65, "also_compatible_with", ParameterType.NTBS);
    
    public static final ArmAeabiAttributesTag conformance = addTag(67, "conformance", ParameterType.NTBS);
    
    public static final ArmAeabiAttributesTag T2EE_use = addTag(66, "T2EE_use", ParameterType.ULEB128);
    
    public static final ArmAeabiAttributesTag Virtualization_use = addTag(68, "Virtualization_use", ParameterType.ULEB128);
    
    public static final ArmAeabiAttributesTag MPextension_use2 = addTag(70, "MPextension_use", ParameterType.ULEB128);
    
    public ArmAeabiAttributesTag(int param1Int, String param1String, ParameterType param1ParameterType) {
      this.value = param1Int;
      this.name = param1String;
      this.parameterType = param1ParameterType;
    }
    
    public int getValue() {
      return this.value;
    }
    
    public String getName() {
      return this.name;
    }
    
    public ParameterType getParameterType() {
      return this.parameterType;
    }
    
    public String toString() {
      return this.name + " (" + this.value + ")";
    }
    
    public int hashCode() {
      null = 7;
      return 67 * null + this.value;
    }
    
    public boolean equals(Object param1Object) {
      if (this == param1Object)
        return true; 
      if (param1Object == null)
        return false; 
      if (getClass() != param1Object.getClass())
        return false; 
      ArmAeabiAttributesTag armAeabiAttributesTag = (ArmAeabiAttributesTag)param1Object;
      return !(this.value != armAeabiAttributesTag.value);
    }
    
    private static ArmAeabiAttributesTag addTag(int param1Int, String param1String, ParameterType param1ParameterType) {
      ArmAeabiAttributesTag armAeabiAttributesTag = new ArmAeabiAttributesTag(param1Int, param1String, param1ParameterType);
      if (!valueMap.containsKey(Integer.valueOf(armAeabiAttributesTag.getValue())))
        valueMap.put(Integer.valueOf(armAeabiAttributesTag.getValue()), armAeabiAttributesTag); 
      if (!nameMap.containsKey(armAeabiAttributesTag.getName()))
        nameMap.put(armAeabiAttributesTag.getName(), armAeabiAttributesTag); 
      tags.add(armAeabiAttributesTag);
      return armAeabiAttributesTag;
    }
    
    public static List<ArmAeabiAttributesTag> getTags() {
      return Collections.unmodifiableList(tags);
    }
    
    public static ArmAeabiAttributesTag getByName(String param1String) {
      return nameMap.get(param1String);
    }
    
    public static ArmAeabiAttributesTag getByValue(int param1Int) {
      return valueMap.containsKey(Integer.valueOf(param1Int)) ? valueMap.get(Integer.valueOf(param1Int)) : new ArmAeabiAttributesTag(param1Int, "Unknown " + param1Int, getParameterType(param1Int));
    }
    
    private static ParameterType getParameterType(int param1Int) {
      ArmAeabiAttributesTag armAeabiAttributesTag = getByValue(param1Int);
      return (armAeabiAttributesTag == null) ? ((param1Int % 2 == 0) ? ParameterType.ULEB128 : ParameterType.NTBS) : armAeabiAttributesTag.getParameterType();
    }
    
    public enum ParameterType {
      UINT32, NTBS, ULEB128;
    }
  }
  
  public enum ParameterType {
    UINT32, NTBS, ULEB128;
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\com\sun\jna\ELFAnalyser.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */