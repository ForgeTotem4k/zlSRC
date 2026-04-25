package io.sentry;

import io.sentry.clientreport.ClientReport;
import io.sentry.exception.SentryEnvelopeException;
import io.sentry.metrics.EncodedMetrics;
import io.sentry.protocol.SentryTransaction;
import io.sentry.util.FileUtils;
import io.sentry.util.JsonSerializationUtils;
import io.sentry.util.Objects;
import io.sentry.vendor.Base64;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.util.concurrent.Callable;
import org.jetbrains.annotations.ApiStatus.Internal;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Internal
public final class SentryEnvelopeItem {
  private static final Charset UTF_8 = Charset.forName("UTF-8");
  
  private final SentryEnvelopeItemHeader header;
  
  @Nullable
  private final Callable<byte[]> dataFactory;
  
  @Nullable
  private byte[] data;
  
  SentryEnvelopeItem(@NotNull SentryEnvelopeItemHeader paramSentryEnvelopeItemHeader, byte[] paramArrayOfbyte) {
    this.header = (SentryEnvelopeItemHeader)Objects.requireNonNull(paramSentryEnvelopeItemHeader, "SentryEnvelopeItemHeader is required.");
    this.data = paramArrayOfbyte;
    this.dataFactory = null;
  }
  
  SentryEnvelopeItem(@NotNull SentryEnvelopeItemHeader paramSentryEnvelopeItemHeader, @Nullable Callable<byte[]> paramCallable) {
    this.header = (SentryEnvelopeItemHeader)Objects.requireNonNull(paramSentryEnvelopeItemHeader, "SentryEnvelopeItemHeader is required.");
    this.dataFactory = (Callable<byte[]>)Objects.requireNonNull(paramCallable, "DataFactory is required.");
    this.data = null;
  }
  
  @NotNull
  public byte[] getData() throws Exception {
    if (this.data == null && this.dataFactory != null)
      this.data = this.dataFactory.call(); 
    return this.data;
  }
  
  @NotNull
  public SentryEnvelopeItemHeader getHeader() {
    return this.header;
  }
  
  @NotNull
  public static SentryEnvelopeItem fromSession(@NotNull ISerializer paramISerializer, @NotNull Session paramSession) throws IOException {
    Objects.requireNonNull(paramISerializer, "ISerializer is required.");
    Objects.requireNonNull(paramSession, "Session is required.");
    CachedItem cachedItem = new CachedItem(() -> {
          ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
          try {
            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(byteArrayOutputStream, UTF_8));
            try {
              paramISerializer.serialize(paramSession, bufferedWriter);
              byte[] arrayOfByte = byteArrayOutputStream.toByteArray();
              bufferedWriter.close();
              byteArrayOutputStream.close();
              return arrayOfByte;
            } catch (Throwable throwable) {
              try {
                bufferedWriter.close();
              } catch (Throwable throwable1) {
                throwable.addSuppressed(throwable1);
              } 
              throw throwable;
            } 
          } catch (Throwable throwable) {
            try {
              byteArrayOutputStream.close();
            } catch (Throwable throwable1) {
              throwable.addSuppressed(throwable1);
            } 
            throw throwable;
          } 
        });
    SentryEnvelopeItemHeader sentryEnvelopeItemHeader = new SentryEnvelopeItemHeader(SentryItemType.Session, () -> Integer.valueOf((paramCachedItem.getBytes()).length), "application/json", null);
    return new SentryEnvelopeItem(sentryEnvelopeItemHeader, () -> paramCachedItem.getBytes());
  }
  
  @Nullable
  public SentryEvent getEvent(@NotNull ISerializer paramISerializer) throws Exception {
    if (this.header == null || this.header.getType() != SentryItemType.Event)
      return null; 
    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(getData()), UTF_8));
    try {
      SentryEvent sentryEvent = paramISerializer.<SentryEvent>deserialize(bufferedReader, SentryEvent.class);
      bufferedReader.close();
      return sentryEvent;
    } catch (Throwable throwable) {
      try {
        bufferedReader.close();
      } catch (Throwable throwable1) {
        throwable.addSuppressed(throwable1);
      } 
      throw throwable;
    } 
  }
  
  @NotNull
  public static SentryEnvelopeItem fromEvent(@NotNull ISerializer paramISerializer, @NotNull SentryBaseEvent paramSentryBaseEvent) throws IOException {
    Objects.requireNonNull(paramISerializer, "ISerializer is required.");
    Objects.requireNonNull(paramSentryBaseEvent, "SentryEvent is required.");
    CachedItem cachedItem = new CachedItem(() -> {
          ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
          try {
            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(byteArrayOutputStream, UTF_8));
            try {
              paramISerializer.serialize(paramSentryBaseEvent, bufferedWriter);
              byte[] arrayOfByte = byteArrayOutputStream.toByteArray();
              bufferedWriter.close();
              byteArrayOutputStream.close();
              return arrayOfByte;
            } catch (Throwable throwable) {
              try {
                bufferedWriter.close();
              } catch (Throwable throwable1) {
                throwable.addSuppressed(throwable1);
              } 
              throw throwable;
            } 
          } catch (Throwable throwable) {
            try {
              byteArrayOutputStream.close();
            } catch (Throwable throwable1) {
              throwable.addSuppressed(throwable1);
            } 
            throw throwable;
          } 
        });
    SentryEnvelopeItemHeader sentryEnvelopeItemHeader = new SentryEnvelopeItemHeader(SentryItemType.resolve(paramSentryBaseEvent), () -> Integer.valueOf((paramCachedItem.getBytes()).length), "application/json", null);
    return new SentryEnvelopeItem(sentryEnvelopeItemHeader, () -> paramCachedItem.getBytes());
  }
  
  @Nullable
  public SentryTransaction getTransaction(@NotNull ISerializer paramISerializer) throws Exception {
    if (this.header == null || this.header.getType() != SentryItemType.Transaction)
      return null; 
    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(getData()), UTF_8));
    try {
      SentryTransaction sentryTransaction = paramISerializer.<SentryTransaction>deserialize(bufferedReader, SentryTransaction.class);
      bufferedReader.close();
      return sentryTransaction;
    } catch (Throwable throwable) {
      try {
        bufferedReader.close();
      } catch (Throwable throwable1) {
        throwable.addSuppressed(throwable1);
      } 
      throw throwable;
    } 
  }
  
  public static SentryEnvelopeItem fromUserFeedback(@NotNull ISerializer paramISerializer, @NotNull UserFeedback paramUserFeedback) {
    Objects.requireNonNull(paramISerializer, "ISerializer is required.");
    Objects.requireNonNull(paramUserFeedback, "UserFeedback is required.");
    CachedItem cachedItem = new CachedItem(() -> {
          ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
          try {
            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(byteArrayOutputStream, UTF_8));
            try {
              paramISerializer.serialize(paramUserFeedback, bufferedWriter);
              byte[] arrayOfByte = byteArrayOutputStream.toByteArray();
              bufferedWriter.close();
              byteArrayOutputStream.close();
              return arrayOfByte;
            } catch (Throwable throwable) {
              try {
                bufferedWriter.close();
              } catch (Throwable throwable1) {
                throwable.addSuppressed(throwable1);
              } 
              throw throwable;
            } 
          } catch (Throwable throwable) {
            try {
              byteArrayOutputStream.close();
            } catch (Throwable throwable1) {
              throwable.addSuppressed(throwable1);
            } 
            throw throwable;
          } 
        });
    SentryEnvelopeItemHeader sentryEnvelopeItemHeader = new SentryEnvelopeItemHeader(SentryItemType.UserFeedback, () -> Integer.valueOf((paramCachedItem.getBytes()).length), "application/json", null);
    return new SentryEnvelopeItem(sentryEnvelopeItemHeader, () -> paramCachedItem.getBytes());
  }
  
  public static SentryEnvelopeItem fromCheckIn(@NotNull ISerializer paramISerializer, @NotNull CheckIn paramCheckIn) {
    Objects.requireNonNull(paramISerializer, "ISerializer is required.");
    Objects.requireNonNull(paramCheckIn, "CheckIn is required.");
    CachedItem cachedItem = new CachedItem(() -> {
          ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
          try {
            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(byteArrayOutputStream, UTF_8));
            try {
              paramISerializer.serialize(paramCheckIn, bufferedWriter);
              byte[] arrayOfByte = byteArrayOutputStream.toByteArray();
              bufferedWriter.close();
              byteArrayOutputStream.close();
              return arrayOfByte;
            } catch (Throwable throwable) {
              try {
                bufferedWriter.close();
              } catch (Throwable throwable1) {
                throwable.addSuppressed(throwable1);
              } 
              throw throwable;
            } 
          } catch (Throwable throwable) {
            try {
              byteArrayOutputStream.close();
            } catch (Throwable throwable1) {
              throwable.addSuppressed(throwable1);
            } 
            throw throwable;
          } 
        });
    SentryEnvelopeItemHeader sentryEnvelopeItemHeader = new SentryEnvelopeItemHeader(SentryItemType.CheckIn, () -> Integer.valueOf((paramCachedItem.getBytes()).length), "application/json", null);
    return new SentryEnvelopeItem(sentryEnvelopeItemHeader, () -> paramCachedItem.getBytes());
  }
  
  public static SentryEnvelopeItem fromMetrics(@NotNull EncodedMetrics paramEncodedMetrics) {
    CachedItem cachedItem = new CachedItem(() -> paramEncodedMetrics.encodeToStatsd());
    SentryEnvelopeItemHeader sentryEnvelopeItemHeader = new SentryEnvelopeItemHeader(SentryItemType.Statsd, () -> Integer.valueOf((paramCachedItem.getBytes()).length), "application/octet-stream", null);
    return new SentryEnvelopeItem(sentryEnvelopeItemHeader, () -> paramCachedItem.getBytes());
  }
  
  public static SentryEnvelopeItem fromAttachment(@NotNull ISerializer paramISerializer, @NotNull ILogger paramILogger, @NotNull Attachment paramAttachment, long paramLong) {
    CachedItem cachedItem = new CachedItem(() -> {
          if (paramAttachment.getBytes() != null) {
            byte[] arrayOfByte = paramAttachment.getBytes();
            ensureAttachmentSizeLimit(arrayOfByte.length, paramLong, paramAttachment.getFilename());
            return arrayOfByte;
          } 
          if (paramAttachment.getSerializable() != null) {
            JsonSerializable jsonSerializable = paramAttachment.getSerializable();
            byte[] arrayOfByte = JsonSerializationUtils.bytesFrom(paramISerializer, paramILogger, jsonSerializable);
            if (arrayOfByte != null) {
              ensureAttachmentSizeLimit(arrayOfByte.length, paramLong, paramAttachment.getFilename());
              return arrayOfByte;
            } 
          } else if (paramAttachment.getPathname() != null) {
            return FileUtils.readBytesFromFile(paramAttachment.getPathname(), paramLong);
          } 
          throw new SentryEnvelopeException(String.format("Couldn't attach the attachment %s.\nPlease check that either bytes, serializable or a path is set.", new Object[] { paramAttachment.getFilename() }));
        });
    SentryEnvelopeItemHeader sentryEnvelopeItemHeader = new SentryEnvelopeItemHeader(SentryItemType.Attachment, () -> Integer.valueOf((paramCachedItem.getBytes()).length), paramAttachment.getContentType(), paramAttachment.getFilename(), paramAttachment.getAttachmentType());
    return new SentryEnvelopeItem(sentryEnvelopeItemHeader, () -> paramCachedItem.getBytes());
  }
  
  private static void ensureAttachmentSizeLimit(long paramLong1, long paramLong2, @NotNull String paramString) throws SentryEnvelopeException {
    if (paramLong1 > paramLong2)
      throw new SentryEnvelopeException(String.format("Dropping attachment with filename '%s', because the size of the passed bytes with %d bytes is bigger than the maximum allowed attachment size of %d bytes.", new Object[] { paramString, Long.valueOf(paramLong1), Long.valueOf(paramLong2) })); 
  }
  
  @NotNull
  public static SentryEnvelopeItem fromProfilingTrace(@NotNull ProfilingTraceData paramProfilingTraceData, long paramLong, @NotNull ISerializer paramISerializer) throws SentryEnvelopeException {
    File file = paramProfilingTraceData.getTraceFile();
    CachedItem cachedItem = new CachedItem(() -> {
          if (!paramFile.exists())
            throw new SentryEnvelopeException(String.format("Dropping profiling trace data, because the file '%s' doesn't exists", new Object[] { paramFile.getName() })); 
          byte[] arrayOfByte = FileUtils.readBytesFromFile(paramFile.getPath(), paramLong);
          String str = Base64.encodeToString(arrayOfByte, 3);
          if (str.isEmpty())
            throw new SentryEnvelopeException("Profiling trace file is empty"); 
          paramProfilingTraceData.setSampledProfile(str);
          paramProfilingTraceData.readDeviceCpuFrequencies();
          try {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
          } catch (IOException iOException) {
            throw new SentryEnvelopeException(String.format("Failed to serialize profiling trace data\n%s", new Object[] { iOException.getMessage() }));
          } finally {
            paramFile.delete();
          } 
        });
    SentryEnvelopeItemHeader sentryEnvelopeItemHeader = new SentryEnvelopeItemHeader(SentryItemType.Profile, () -> Integer.valueOf((paramCachedItem.getBytes()).length), "application-json", file.getName());
    return new SentryEnvelopeItem(sentryEnvelopeItemHeader, () -> paramCachedItem.getBytes());
  }
  
  @NotNull
  public static SentryEnvelopeItem fromClientReport(@NotNull ISerializer paramISerializer, @NotNull ClientReport paramClientReport) throws IOException {
    Objects.requireNonNull(paramISerializer, "ISerializer is required.");
    Objects.requireNonNull(paramClientReport, "ClientReport is required.");
    CachedItem cachedItem = new CachedItem(() -> {
          ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
          try {
            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(byteArrayOutputStream, UTF_8));
            try {
              paramISerializer.serialize(paramClientReport, bufferedWriter);
              byte[] arrayOfByte = byteArrayOutputStream.toByteArray();
              bufferedWriter.close();
              byteArrayOutputStream.close();
              return arrayOfByte;
            } catch (Throwable throwable) {
              try {
                bufferedWriter.close();
              } catch (Throwable throwable1) {
                throwable.addSuppressed(throwable1);
              } 
              throw throwable;
            } 
          } catch (Throwable throwable) {
            try {
              byteArrayOutputStream.close();
            } catch (Throwable throwable1) {
              throwable.addSuppressed(throwable1);
            } 
            throw throwable;
          } 
        });
    SentryEnvelopeItemHeader sentryEnvelopeItemHeader = new SentryEnvelopeItemHeader(SentryItemType.resolve(paramClientReport), () -> Integer.valueOf((paramCachedItem.getBytes()).length), "application/json", null);
    return new SentryEnvelopeItem(sentryEnvelopeItemHeader, () -> paramCachedItem.getBytes());
  }
  
  @Nullable
  public ClientReport getClientReport(@NotNull ISerializer paramISerializer) throws Exception {
    if (this.header == null || this.header.getType() != SentryItemType.ClientReport)
      return null; 
    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(getData()), UTF_8));
    try {
      ClientReport clientReport = paramISerializer.<ClientReport>deserialize(bufferedReader, ClientReport.class);
      bufferedReader.close();
      return clientReport;
    } catch (Throwable throwable) {
      try {
        bufferedReader.close();
      } catch (Throwable throwable1) {
        throwable.addSuppressed(throwable1);
      } 
      throw throwable;
    } 
  }
  
  private static class CachedItem {
    @Nullable
    private byte[] bytes;
    
    @Nullable
    private final Callable<byte[]> dataFactory;
    
    public CachedItem(@Nullable Callable<byte[]> param1Callable) {
      this.dataFactory = param1Callable;
    }
    
    @NotNull
    public byte[] getBytes() throws Exception {
      if (this.bytes == null && this.dataFactory != null)
        this.bytes = this.dataFactory.call(); 
      return orEmptyArray(this.bytes);
    }
    
    @NotNull
    private static byte[] orEmptyArray(@Nullable byte[] param1ArrayOfbyte) {
      return (param1ArrayOfbyte != null) ? param1ArrayOfbyte : new byte[0];
    }
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\io\sentry\SentryEnvelopeItem.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */