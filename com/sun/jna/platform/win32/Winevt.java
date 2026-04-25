package com.sun.jna.platform.win32;

import com.sun.jna.Memory;
import com.sun.jna.Pointer;
import com.sun.jna.StringArray;
import com.sun.jna.Structure;
import com.sun.jna.Structure.FieldOrder;
import com.sun.jna.Union;
import com.sun.jna.win32.W32APITypeMapper;

public interface Winevt {
  public static final int EVT_VARIANT_TYPE_ARRAY = 128;
  
  public static final int EVT_VARIANT_TYPE_MASK = 127;
  
  public static final int EVT_READ_ACCESS = 1;
  
  public static final int EVT_WRITE_ACCESS = 2;
  
  public static final int EVT_ALL_ACCESS = 7;
  
  public static final int EVT_CLEAR_ACCESS = 4;
  
  public static class EVT_HANDLE extends WinNT.HANDLE {
    public EVT_HANDLE() {}
    
    public EVT_HANDLE(Pointer param1Pointer) {
      super(param1Pointer);
    }
  }
  
  public static interface EVT_EVENT_PROPERTY_ID {
    public static final int EvtEventQueryIDs = 0;
    
    public static final int EvtEventPath = 1;
    
    public static final int EvtEventPropertyIdEND = 2;
  }
  
  public static interface EVT_QUERY_PROPERTY_ID {
    public static final int EvtQueryNames = 0;
    
    public static final int EvtQueryStatuses = 1;
    
    public static final int EvtQueryPropertyIdEND = 2;
  }
  
  public static interface EVT_EVENT_METADATA_PROPERTY_ID {
    public static final int EventMetadataEventID = 0;
    
    public static final int EventMetadataEventVersion = 1;
    
    public static final int EventMetadataEventChannel = 2;
    
    public static final int EventMetadataEventLevel = 3;
    
    public static final int EventMetadataEventOpcode = 4;
    
    public static final int EventMetadataEventTask = 5;
    
    public static final int EventMetadataEventKeyword = 6;
    
    public static final int EventMetadataEventMessageID = 7;
    
    public static final int EventMetadataEventTemplate = 8;
    
    public static final int EvtEventMetadataPropertyIdEND = 9;
  }
  
  public static interface EVT_PUBLISHER_METADATA_PROPERTY_ID {
    public static final int EvtPublisherMetadataPublisherGuid = 0;
    
    public static final int EvtPublisherMetadataResourceFilePath = 1;
    
    public static final int EvtPublisherMetadataParameterFilePath = 2;
    
    public static final int EvtPublisherMetadataMessageFilePath = 3;
    
    public static final int EvtPublisherMetadataHelpLink = 4;
    
    public static final int EvtPublisherMetadataPublisherMessageID = 5;
    
    public static final int EvtPublisherMetadataChannelReferences = 6;
    
    public static final int EvtPublisherMetadataChannelReferencePath = 7;
    
    public static final int EvtPublisherMetadataChannelReferenceIndex = 8;
    
    public static final int EvtPublisherMetadataChannelReferenceID = 9;
    
    public static final int EvtPublisherMetadataChannelReferenceFlags = 10;
    
    public static final int EvtPublisherMetadataChannelReferenceMessageID = 11;
    
    public static final int EvtPublisherMetadataLevels = 12;
    
    public static final int EvtPublisherMetadataLevelName = 13;
    
    public static final int EvtPublisherMetadataLevelValue = 14;
    
    public static final int EvtPublisherMetadataLevelMessageID = 15;
    
    public static final int EvtPublisherMetadataTasks = 16;
    
    public static final int EvtPublisherMetadataTaskName = 17;
    
    public static final int EvtPublisherMetadataTaskEventGuid = 18;
    
    public static final int EvtPublisherMetadataTaskValue = 19;
    
    public static final int EvtPublisherMetadataTaskMessageID = 20;
    
    public static final int EvtPublisherMetadataOpcodes = 21;
    
    public static final int EvtPublisherMetadataOpcodeName = 22;
    
    public static final int EvtPublisherMetadataOpcodeValue = 23;
    
    public static final int EvtPublisherMetadataOpcodeMessageID = 24;
    
    public static final int EvtPublisherMetadataKeywords = 25;
    
    public static final int EvtPublisherMetadataKeywordName = 26;
    
    public static final int EvtPublisherMetadataKeywordValue = 27;
    
    public static final int EvtPublisherMetadataKeywordMessageID = 28;
    
    public static final int EvtPublisherMetadataPropertyIdEND = 29;
  }
  
  public static interface EVT_CHANNEL_REFERENCE_FLAGS {
    public static final int EvtChannelReferenceImported = 1;
  }
  
  public static interface EVT_CHANNEL_SID_TYPE {
    public static final int EvtChannelSidTypeNone = 0;
    
    public static final int EvtChannelSidTypePublishing = 1;
  }
  
  public static interface EVT_CHANNEL_CLOCK_TYPE {
    public static final int EvtChannelClockTypeSystemTime = 0;
    
    public static final int EvtChannelClockTypeQPC = 1;
  }
  
  public static interface EVT_CHANNEL_ISOLATION_TYPE {
    public static final int EvtChannelIsolationTypeApplication = 0;
    
    public static final int EvtChannelIsolationTypeSystem = 1;
    
    public static final int EvtChannelIsolationTypeCustom = 2;
  }
  
  public static interface EVT_CHANNEL_TYPE {
    public static final int EvtChannelTypeAdmin = 0;
    
    public static final int EvtChannelTypeOperational = 1;
    
    public static final int EvtChannelTypeAnalytic = 2;
    
    public static final int EvtChannelTypeDebug = 3;
  }
  
  public static interface EVT_CHANNEL_CONFIG_PROPERTY_ID {
    public static final int EvtChannelConfigEnabled = 0;
    
    public static final int EvtChannelConfigIsolation = 1;
    
    public static final int EvtChannelConfigType = 2;
    
    public static final int EvtChannelConfigOwningPublisher = 3;
    
    public static final int EvtChannelConfigClassicEventlog = 4;
    
    public static final int EvtChannelConfigAccess = 5;
    
    public static final int EvtChannelLoggingConfigRetention = 6;
    
    public static final int EvtChannelLoggingConfigAutoBackup = 7;
    
    public static final int EvtChannelLoggingConfigMaxSize = 8;
    
    public static final int EvtChannelLoggingConfigLogFilePath = 9;
    
    public static final int EvtChannelPublishingConfigLevel = 10;
    
    public static final int EvtChannelPublishingConfigKeywords = 11;
    
    public static final int EvtChannelPublishingConfigControlGuid = 12;
    
    public static final int EvtChannelPublishingConfigBufferSize = 13;
    
    public static final int EvtChannelPublishingConfigMinBuffers = 14;
    
    public static final int EvtChannelPublishingConfigMaxBuffers = 15;
    
    public static final int EvtChannelPublishingConfigLatency = 16;
    
    public static final int EvtChannelPublishingConfigClockType = 17;
    
    public static final int EvtChannelPublishingConfigSidType = 18;
    
    public static final int EvtChannelPublisherList = 19;
    
    public static final int EvtChannelPublishingConfigFileMax = 20;
    
    public static final int EvtChannelConfigPropertyIdEND = 21;
  }
  
  public static interface EVT_EXPORTLOG_FLAGS {
    public static final int EvtExportLogChannelPath = 1;
    
    public static final int EvtExportLogFilePath = 2;
    
    public static final int EvtExportLogTolerateQueryErrors = 4096;
    
    public static final int EvtExportLogOverwrite = 8192;
  }
  
  public static interface EVT_LOG_PROPERTY_ID {
    public static final int EvtLogCreationTime = 0;
    
    public static final int EvtLogLastAccessTime = 1;
    
    public static final int EvtLogLastWriteTime = 2;
    
    public static final int EvtLogFileSize = 3;
    
    public static final int EvtLogAttributes = 4;
    
    public static final int EvtLogNumberOfLogRecords = 5;
    
    public static final int EvtLogOldestRecordNumber = 6;
    
    public static final int EvtLogFull = 7;
  }
  
  public static interface EVT_OPEN_LOG_FLAGS {
    public static final int EvtOpenChannelPath = 1;
    
    public static final int EvtOpenFilePath = 2;
  }
  
  public static interface EVT_FORMAT_MESSAGE_FLAGS {
    public static final int EvtFormatMessageEvent = 1;
    
    public static final int EvtFormatMessageLevel = 2;
    
    public static final int EvtFormatMessageTask = 3;
    
    public static final int EvtFormatMessageOpcode = 4;
    
    public static final int EvtFormatMessageKeyword = 5;
    
    public static final int EvtFormatMessageChannel = 6;
    
    public static final int EvtFormatMessageProvider = 7;
    
    public static final int EvtFormatMessageId = 8;
    
    public static final int EvtFormatMessageXml = 9;
  }
  
  public static interface EVT_RENDER_FLAGS {
    public static final int EvtRenderEventValues = 0;
    
    public static final int EvtRenderEventXml = 1;
    
    public static final int EvtRenderBookmark = 2;
  }
  
  public static interface EVT_RENDER_CONTEXT_FLAGS {
    public static final int EvtRenderContextValues = 0;
    
    public static final int EvtRenderContextSystem = 1;
    
    public static final int EvtRenderContextUser = 2;
  }
  
  public static interface EVT_SYSTEM_PROPERTY_ID {
    public static final int EvtSystemProviderName = 0;
    
    public static final int EvtSystemProviderGuid = 1;
    
    public static final int EvtSystemEventID = 2;
    
    public static final int EvtSystemQualifiers = 3;
    
    public static final int EvtSystemLevel = 4;
    
    public static final int EvtSystemTask = 5;
    
    public static final int EvtSystemOpcode = 6;
    
    public static final int EvtSystemKeywords = 7;
    
    public static final int EvtSystemTimeCreated = 8;
    
    public static final int EvtSystemEventRecordId = 9;
    
    public static final int EvtSystemActivityID = 10;
    
    public static final int EvtSystemRelatedActivityID = 11;
    
    public static final int EvtSystemProcessID = 12;
    
    public static final int EvtSystemThreadID = 13;
    
    public static final int EvtSystemChannel = 14;
    
    public static final int EvtSystemComputer = 15;
    
    public static final int EvtSystemUserID = 16;
    
    public static final int EvtSystemVersion = 17;
    
    public static final int EvtSystemPropertyIdEND = 18;
  }
  
  public static interface EVT_SUBSCRIBE_NOTIFY_ACTION {
    public static final int EvtSubscribeActionError = 0;
    
    public static final int EvtSubscribeActionDeliver = 1;
  }
  
  public static interface EVT_SUBSCRIBE_FLAGS {
    public static final int EvtSubscribeToFutureEvents = 1;
    
    public static final int EvtSubscribeStartAtOldestRecord = 2;
    
    public static final int EvtSubscribeStartAfterBookmark = 3;
    
    public static final int EvtSubscribeOriginMask = 3;
    
    public static final int EvtSubscribeTolerateQueryErrors = 4096;
    
    public static final int EvtSubscribeStrict = 65536;
  }
  
  public static interface EVT_SEEK_FLAGS {
    public static final int EvtSeekRelativeToFirst = 1;
    
    public static final int EvtSeekRelativeToLast = 2;
    
    public static final int EvtSeekRelativeToCurrent = 3;
    
    public static final int EvtSeekRelativeToBookmark = 4;
    
    public static final int EvtSeekOriginMask = 7;
    
    public static final int EvtSeekStrict = 65536;
  }
  
  public static interface EVT_QUERY_FLAGS {
    public static final int EvtQueryChannelPath = 1;
    
    public static final int EvtQueryFilePath = 2;
    
    public static final int EvtQueryForwardDirection = 256;
    
    public static final int EvtQueryReverseDirection = 512;
    
    public static final int EvtQueryTolerateQueryErrors = 4096;
  }
  
  @FieldOrder({"Server", "User", "Domain", "Password", "Flags"})
  public static class EVT_RPC_LOGIN extends Structure {
    public String Server;
    
    public String User;
    
    public String Domain;
    
    public String Password;
    
    public int Flags;
    
    public EVT_RPC_LOGIN() {
      super(W32APITypeMapper.UNICODE);
    }
    
    public EVT_RPC_LOGIN(String param1String1, String param1String2, String param1String3, String param1String4, int param1Int) {
      super(W32APITypeMapper.UNICODE);
      this.Server = param1String1;
      this.User = param1String2;
      this.Domain = param1String3;
      this.Password = param1String4;
      this.Flags = param1Int;
    }
    
    public EVT_RPC_LOGIN(Pointer param1Pointer) {
      super(param1Pointer, 0, W32APITypeMapper.UNICODE);
    }
    
    public static class ByValue extends EVT_RPC_LOGIN implements Structure.ByValue {}
    
    public static class ByReference extends EVT_RPC_LOGIN implements Structure.ByReference {}
  }
  
  public static interface EVT_RPC_LOGIN_FLAGS {
    public static final int EvtRpcLoginAuthDefault = 0;
    
    public static final int EvtRpcLoginAuthNegotiate = 1;
    
    public static final int EvtRpcLoginAuthKerberos = 2;
    
    public static final int EvtRpcLoginAuthNTLM = 3;
  }
  
  @FieldOrder({"field1", "Count", "Type"})
  public static class EVT_VARIANT extends Structure {
    public field1_union field1;
    
    public int Count;
    
    public int Type;
    
    private Object holder;
    
    public EVT_VARIANT() {
      super(W32APITypeMapper.DEFAULT);
    }
    
    public EVT_VARIANT(Pointer param1Pointer) {
      super(param1Pointer, 0, W32APITypeMapper.DEFAULT);
    }
    
    public void use(Pointer param1Pointer) {
      useMemory(param1Pointer, 0);
    }
    
    private int getBaseType() {
      return this.Type & 0x7F;
    }
    
    public boolean isArray() {
      return ((this.Type & 0x80) == 128);
    }
    
    public Winevt.EVT_VARIANT_TYPE getVariantType() {
      return Winevt.EVT_VARIANT_TYPE.values()[getBaseType()];
    }
    
    public void setValue(Winevt.EVT_VARIANT_TYPE param1EVT_VARIANT_TYPE, Object param1Object) {
      allocateMemory();
      if (param1EVT_VARIANT_TYPE == null)
        throw new IllegalArgumentException("setValue must not be called with type set to NULL"); 
      this.holder = null;
      if (param1Object == null || param1EVT_VARIANT_TYPE == Winevt.EVT_VARIANT_TYPE.EvtVarTypeNull) {
        this.Type = Winevt.EVT_VARIANT_TYPE.EvtVarTypeNull.ordinal();
        this.Count = 0;
        this.field1.writeField("pointerValue", Pointer.NULL);
      } else {
        switch (param1EVT_VARIANT_TYPE.ordinal()) {
          case 2:
            if (param1Object.getClass().isArray() && param1Object.getClass().getComponentType() == String.class) {
              this.Type = param1EVT_VARIANT_TYPE.ordinal() | 0x80;
              StringArray stringArray = new StringArray((String[])param1Object, false);
              this.holder = stringArray;
              this.Count = ((String[])param1Object).length;
              this.field1.writeField("pointerValue", stringArray);
              break;
            } 
            if (param1Object.getClass() == String.class) {
              this.Type = param1EVT_VARIANT_TYPE.ordinal();
              Memory memory = new Memory((((String)param1Object).length() + 1));
              memory.setString(0L, (String)param1Object);
              this.holder = memory;
              this.Count = 0;
              this.field1.writeField("pointerValue", memory);
              break;
            } 
            throw new IllegalArgumentException(param1EVT_VARIANT_TYPE.name() + " must be set from String/String[]");
          case 13:
            if (param1Object.getClass().isArray() && param1Object.getClass().getComponentType() == WinDef.BOOL.class) {
              this.Type = param1EVT_VARIANT_TYPE.ordinal() | 0x80;
              Memory memory = new Memory((((WinDef.BOOL[])param1Object).length * 4));
              for (byte b = 0; b < ((WinDef.BOOL[])param1Object).length; b++)
                memory.setInt((b * 4), ((WinDef.BOOL[])param1Object)[b].intValue()); 
              this.holder = memory;
              this.Count = 0;
              this.field1.writeField("pointerValue", memory);
              break;
            } 
            if (param1Object.getClass() == WinDef.BOOL.class) {
              this.Type = param1EVT_VARIANT_TYPE.ordinal();
              this.Count = 0;
              this.field1.writeField("intValue", Integer.valueOf(((WinDef.BOOL)param1Object).intValue()));
              break;
            } 
            throw new IllegalArgumentException(param1EVT_VARIANT_TYPE.name() + " must be set from BOOL/BOOL[]");
          case 1:
          case 23:
            if (param1Object.getClass().isArray() && param1Object.getClass().getComponentType() == String.class) {
              this.Type = param1EVT_VARIANT_TYPE.ordinal() | 0x80;
              StringArray stringArray = new StringArray((String[])param1Object, true);
              this.holder = stringArray;
              this.Count = ((String[])param1Object).length;
              this.field1.writeField("pointerValue", stringArray);
              break;
            } 
            if (param1Object.getClass() == String.class) {
              this.Type = param1EVT_VARIANT_TYPE.ordinal();
              Memory memory = new Memory(((((String)param1Object).length() + 1) * 2));
              memory.setWideString(0L, (String)param1Object);
              this.holder = memory;
              this.Count = 0;
              this.field1.writeField("pointerValue", memory);
              break;
            } 
            throw new IllegalArgumentException(param1EVT_VARIANT_TYPE.name() + " must be set from String/String[]");
          case 3:
          case 4:
            if (param1Object.getClass().isArray() && param1Object.getClass().getComponentType() == byte.class) {
              this.Type = param1EVT_VARIANT_TYPE.ordinal() | 0x80;
              Memory memory = new Memory((((byte[])param1Object).length * 1));
              memory.write(0L, (byte[])param1Object, 0, ((byte[])param1Object).length);
              this.holder = memory;
              this.Count = 0;
              this.field1.writeField("pointerValue", memory);
              break;
            } 
            if (param1Object.getClass() == byte.class) {
              this.Type = param1EVT_VARIANT_TYPE.ordinal();
              this.Count = 0;
              this.field1.writeField("byteValue", param1Object);
              break;
            } 
            throw new IllegalArgumentException(param1EVT_VARIANT_TYPE.name() + " must be set from byte/byte[]");
          case 5:
          case 6:
            if (param1Object.getClass().isArray() && param1Object.getClass().getComponentType() == short.class) {
              this.Type = param1EVT_VARIANT_TYPE.ordinal() | 0x80;
              Memory memory = new Memory((((short[])param1Object).length * 2));
              memory.write(0L, (short[])param1Object, 0, ((short[])param1Object).length);
              this.holder = memory;
              this.Count = 0;
              this.field1.writeField("pointerValue", memory);
              break;
            } 
            if (param1Object.getClass() == short.class) {
              this.Type = param1EVT_VARIANT_TYPE.ordinal();
              this.Count = 0;
              this.field1.writeField("shortValue", param1Object);
              break;
            } 
            throw new IllegalArgumentException(param1EVT_VARIANT_TYPE.name() + " must be set from short/short[]");
          case 7:
          case 8:
          case 20:
            if (param1Object.getClass().isArray() && param1Object.getClass().getComponentType() == int.class) {
              this.Type = param1EVT_VARIANT_TYPE.ordinal() | 0x80;
              Memory memory = new Memory((((int[])param1Object).length * 4));
              memory.write(0L, (int[])param1Object, 0, ((int[])param1Object).length);
              this.holder = memory;
              this.Count = 0;
              this.field1.writeField("pointerValue", memory);
              break;
            } 
            if (param1Object.getClass() == int.class) {
              this.Type = param1EVT_VARIANT_TYPE.ordinal();
              this.Count = 0;
              this.field1.writeField("intValue", param1Object);
              break;
            } 
            throw new IllegalArgumentException(param1EVT_VARIANT_TYPE.name() + " must be set from int/int[]");
          case 9:
          case 10:
          case 21:
            if (param1Object.getClass().isArray() && param1Object.getClass().getComponentType() == long.class) {
              this.Type = param1EVT_VARIANT_TYPE.ordinal() | 0x80;
              Memory memory = new Memory((((long[])param1Object).length * 4));
              memory.write(0L, (long[])param1Object, 0, ((long[])param1Object).length);
              this.holder = memory;
              this.Count = 0;
              this.field1.writeField("pointerValue", memory);
              break;
            } 
            if (param1Object.getClass() == long.class) {
              this.Type = param1EVT_VARIANT_TYPE.ordinal();
              this.Count = 0;
              this.field1.writeField("longValue", param1Object);
              break;
            } 
            throw new IllegalArgumentException(param1EVT_VARIANT_TYPE.name() + " must be set from long/long[]");
          case 11:
            if (param1Object.getClass().isArray() && param1Object.getClass().getComponentType() == float.class) {
              this.Type = param1EVT_VARIANT_TYPE.ordinal() | 0x80;
              Memory memory = new Memory((((float[])param1Object).length * 4));
              memory.write(0L, (float[])param1Object, 0, ((float[])param1Object).length);
              this.holder = memory;
              this.Count = 0;
              this.field1.writeField("pointerValue", memory);
              break;
            } 
            if (param1Object.getClass() == float.class) {
              this.Type = param1EVT_VARIANT_TYPE.ordinal();
              this.Count = 0;
              this.field1.writeField("floatValue", param1Object);
              break;
            } 
            throw new IllegalArgumentException(param1EVT_VARIANT_TYPE.name() + " must be set from float/float[]");
          case 12:
            if (param1Object.getClass().isArray() && param1Object.getClass().getComponentType() == double.class) {
              this.Type = param1EVT_VARIANT_TYPE.ordinal() | 0x80;
              Memory memory = new Memory((((double[])param1Object).length * 4));
              memory.write(0L, (double[])param1Object, 0, ((double[])param1Object).length);
              this.holder = memory;
              this.Count = 0;
              this.field1.writeField("pointerValue", memory);
              break;
            } 
            if (param1Object.getClass() == double.class) {
              this.Type = param1EVT_VARIANT_TYPE.ordinal();
              this.Count = 0;
              this.field1.writeField("doubleVal", param1Object);
              break;
            } 
            throw new IllegalArgumentException(param1EVT_VARIANT_TYPE.name() + " must be set from double/double[]");
          case 14:
            if (param1Object.getClass().isArray() && param1Object.getClass().getComponentType() == byte.class) {
              this.Type = param1EVT_VARIANT_TYPE.ordinal();
              Memory memory = new Memory((((byte[])param1Object).length * 1));
              memory.write(0L, (byte[])param1Object, 0, ((byte[])param1Object).length);
              this.holder = memory;
              this.Count = 0;
              this.field1.writeField("pointerValue", memory);
              break;
            } 
            throw new IllegalArgumentException(param1EVT_VARIANT_TYPE.name() + " must be set from byte[]");
          default:
            throw new IllegalStateException(String.format("NOT IMPLEMENTED: getValue(%s) (Array: %b, Count: %d)", new Object[] { param1EVT_VARIANT_TYPE, Boolean.valueOf(isArray()), Integer.valueOf(this.Count) }));
        } 
      } 
      write();
    }
    
    public Object getValue() {
      WinBase.FILETIME fILETIME;
      WinBase.SYSTEMTIME sYSTEMTIME;
      Guid.GUID gUID;
      WinNT.PSID pSID;
      Winevt.EVT_VARIANT_TYPE eVT_VARIANT_TYPE = getVariantType();
      switch (eVT_VARIANT_TYPE.ordinal()) {
        case 2:
          return isArray() ? this.field1.getPointer().getPointer(0L).getStringArray(0L, this.Count) : this.field1.getPointer().getPointer(0L).getString(0L);
        case 13:
          if (isArray()) {
            int[] arrayOfInt = this.field1.getPointer().getPointer(0L).getIntArray(0L, this.Count);
            WinDef.BOOL[] arrayOfBOOL = new WinDef.BOOL[arrayOfInt.length];
            for (byte b = 0; b < arrayOfBOOL.length; b++)
              arrayOfBOOL[b] = new WinDef.BOOL(arrayOfInt[b]); 
            return arrayOfBOOL;
          } 
          return new WinDef.BOOL(this.field1.getPointer().getInt(0L));
        case 1:
        case 23:
          return isArray() ? this.field1.getPointer().getPointer(0L).getWideStringArray(0L, this.Count) : this.field1.getPointer().getPointer(0L).getWideString(0L);
        case 17:
          if (isArray()) {
            WinBase.FILETIME fILETIME1 = (WinBase.FILETIME)Structure.newInstance(WinBase.FILETIME.class, this.field1.getPointer().getPointer(0L));
            fILETIME1.read();
            return fILETIME1.toArray(this.Count);
          } 
          fILETIME = new WinBase.FILETIME(this.field1.getPointer());
          fILETIME.read();
          return fILETIME;
        case 18:
          if (isArray()) {
            WinBase.SYSTEMTIME sYSTEMTIME1 = (WinBase.SYSTEMTIME)Structure.newInstance(WinBase.SYSTEMTIME.class, this.field1.getPointer().getPointer(0L));
            sYSTEMTIME1.read();
            return sYSTEMTIME1.toArray(this.Count);
          } 
          sYSTEMTIME = (WinBase.SYSTEMTIME)Structure.newInstance(WinBase.SYSTEMTIME.class, this.field1.getPointer().getPointer(0L));
          sYSTEMTIME.read();
          return sYSTEMTIME;
        case 3:
        case 4:
          return isArray() ? this.field1.getPointer().getPointer(0L).getByteArray(0L, this.Count) : Byte.valueOf(this.field1.getPointer().getByte(0L));
        case 5:
        case 6:
          return isArray() ? this.field1.getPointer().getPointer(0L).getShortArray(0L, this.Count) : Short.valueOf(this.field1.getPointer().getShort(0L));
        case 7:
        case 8:
        case 20:
          return isArray() ? this.field1.getPointer().getPointer(0L).getIntArray(0L, this.Count) : Integer.valueOf(this.field1.getPointer().getInt(0L));
        case 9:
        case 10:
        case 21:
          return isArray() ? this.field1.getPointer().getPointer(0L).getLongArray(0L, this.Count) : Long.valueOf(this.field1.getPointer().getLong(0L));
        case 11:
          return isArray() ? this.field1.getPointer().getPointer(0L).getFloatArray(0L, this.Count) : Float.valueOf(this.field1.getPointer().getFloat(0L));
        case 12:
          return isArray() ? this.field1.getPointer().getPointer(0L).getDoubleArray(0L, this.Count) : Double.valueOf(this.field1.getPointer().getDouble(0L));
        case 14:
          assert !isArray();
          return this.field1.getPointer().getPointer(0L).getByteArray(0L, this.Count);
        case 0:
          return null;
        case 15:
          if (isArray()) {
            Guid.GUID gUID1 = (Guid.GUID)Structure.newInstance(Guid.GUID.class, this.field1.getPointer().getPointer(0L));
            gUID1.read();
            return gUID1.toArray(this.Count);
          } 
          gUID = (Guid.GUID)Structure.newInstance(Guid.GUID.class, this.field1.getPointer().getPointer(0L));
          gUID.read();
          return gUID;
        case 19:
          if (isArray()) {
            WinNT.PSID pSID1 = (WinNT.PSID)Structure.newInstance(WinNT.PSID.class, this.field1.getPointer().getPointer(0L));
            pSID1.read();
            return pSID1.toArray(this.Count);
          } 
          pSID = (WinNT.PSID)Structure.newInstance(WinNT.PSID.class, this.field1.getPointer().getPointer(0L));
          pSID.read();
          return pSID;
        case 16:
          if (isArray()) {
            long[] arrayOfLong = this.field1.getPointer().getPointer(0L).getLongArray(0L, this.Count);
            BaseTSD.SIZE_T[] arrayOfSIZE_T = new BaseTSD.SIZE_T[arrayOfLong.length];
            for (byte b = 0; b < arrayOfSIZE_T.length; b++)
              arrayOfSIZE_T[b] = new BaseTSD.SIZE_T(arrayOfLong[b]); 
            return arrayOfSIZE_T;
          } 
          return new BaseTSD.SIZE_T(this.field1.getPointer().getLong(0L));
        case 22:
          if (isArray()) {
            Pointer[] arrayOfPointer = this.field1.getPointer().getPointer(0L).getPointerArray(0L, this.Count);
            WinNT.HANDLE[] arrayOfHANDLE = new WinNT.HANDLE[arrayOfPointer.length];
            for (byte b = 0; b < arrayOfHANDLE.length; b++)
              arrayOfHANDLE[b] = new WinNT.HANDLE(arrayOfPointer[b]); 
            return arrayOfHANDLE;
          } 
          return new WinNT.HANDLE(this.field1.getPointer().getPointer(0L));
      } 
      throw new IllegalStateException(String.format("NOT IMPLEMENTED: getValue(%s) (Array: %b, Count: %d)", new Object[] { eVT_VARIANT_TYPE, Boolean.valueOf(isArray()), Integer.valueOf(this.Count) }));
    }
    
    public static class field1_union extends Union {
      public byte byteValue;
      
      public short shortValue;
      
      public int intValue;
      
      public long longValue;
      
      public float floatValue;
      
      public double doubleVal;
      
      public Pointer pointerValue;
    }
    
    public static class ByValue extends EVT_VARIANT implements Structure.ByValue {
      public ByValue(Pointer param2Pointer) {
        super(param2Pointer);
      }
      
      public ByValue() {}
    }
    
    public static class ByReference extends EVT_VARIANT implements Structure.ByReference {
      public ByReference(Pointer param2Pointer) {
        super(param2Pointer);
      }
      
      public ByReference() {}
    }
  }
  
  public static interface EVT_LOGIN_CLASS {
    public static final int EvtRpcLogin = 1;
  }
  
  public enum EVT_VARIANT_TYPE {
    EvtVarTypeNull(""),
    EvtVarTypeString("String"),
    EvtVarTypeAnsiString("AnsiString"),
    EvtVarTypeSByte("SByte"),
    EvtVarTypeByte("Byte"),
    EvtVarTypeInt16("Int16"),
    EvtVarTypeUInt16("UInt16"),
    EvtVarTypeInt32("Int32"),
    EvtVarTypeUInt32("UInt32"),
    EvtVarTypeInt64("Int64"),
    EvtVarTypeUInt64("UInt64"),
    EvtVarTypeSingle("Single"),
    EvtVarTypeDouble("Double"),
    EvtVarTypeBoolean("Boolean"),
    EvtVarTypeBinary("Binary"),
    EvtVarTypeGuid("Guid"),
    EvtVarTypeSizeT("SizeT"),
    EvtVarTypeFileTime("FileTime"),
    EvtVarTypeSysTime("SysTime"),
    EvtVarTypeSid("Sid"),
    EvtVarTypeHexInt32("Int32"),
    EvtVarTypeHexInt64("Int64"),
    EvtVarTypeEvtHandle("EvtHandle"),
    EvtVarTypeEvtXml("Xml");
    
    private final String field;
    
    EVT_VARIANT_TYPE(String param1String1) {
      this.field = param1String1;
    }
    
    public String getField() {
      return this.field.isEmpty() ? "" : (this.field + "Val");
    }
    
    public String getArrField() {
      return this.field.isEmpty() ? "" : (this.field + "Arr");
    }
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\com\sun\jna\platform\win32\Winevt.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */