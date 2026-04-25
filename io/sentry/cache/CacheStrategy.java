package io.sentry.cache;

import io.sentry.ISerializer;
import io.sentry.SentryEnvelope;
import io.sentry.SentryEnvelopeItem;
import io.sentry.SentryItemType;
import io.sentry.SentryLevel;
import io.sentry.SentryOptions;
import io.sentry.Session;
import io.sentry.util.Objects;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.UUID;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

abstract class CacheStrategy {
  protected static final Charset UTF_8 = Charset.forName("UTF-8");
  
  @NotNull
  protected final SentryOptions options;
  
  @NotNull
  protected final ISerializer serializer;
  
  @NotNull
  protected final File directory;
  
  private final int maxSize;
  
  CacheStrategy(@NotNull SentryOptions paramSentryOptions, @NotNull String paramString, int paramInt) {
    Objects.requireNonNull(paramString, "Directory is required.");
    this.options = (SentryOptions)Objects.requireNonNull(paramSentryOptions, "SentryOptions is required.");
    this.serializer = paramSentryOptions.getSerializer();
    this.directory = new File(paramString);
    this.maxSize = paramInt;
  }
  
  protected boolean isDirectoryValid() {
    if (!this.directory.isDirectory() || !this.directory.canWrite() || !this.directory.canRead()) {
      this.options.getLogger().log(SentryLevel.ERROR, "The directory for caching files is inaccessible.: %s", new Object[] { this.directory.getAbsolutePath() });
      return false;
    } 
    return true;
  }
  
  private void sortFilesOldestToNewest(@NotNull File[] paramArrayOfFile) {
    if (paramArrayOfFile.length > 1)
      Arrays.sort(paramArrayOfFile, (paramFile1, paramFile2) -> Long.compare(paramFile1.lastModified(), paramFile2.lastModified())); 
  }
  
  protected void rotateCacheIfNeeded(@NotNull File[] paramArrayOfFile) {
    int i = paramArrayOfFile.length;
    if (i >= this.maxSize) {
      this.options.getLogger().log(SentryLevel.WARNING, "Cache folder if full (respecting maxSize). Rotating files", new Object[0]);
      int j = i - this.maxSize + 1;
      sortFilesOldestToNewest(paramArrayOfFile);
      File[] arrayOfFile = Arrays.<File>copyOfRange(paramArrayOfFile, j, i);
      for (byte b = 0; b < j; b++) {
        File file = paramArrayOfFile[b];
        moveInitFlagIfNecessary(file, arrayOfFile);
        if (!file.delete())
          this.options.getLogger().log(SentryLevel.WARNING, "File can't be deleted: %s", new Object[] { file.getAbsolutePath() }); 
      } 
    } 
  }
  
  private void moveInitFlagIfNecessary(@NotNull File paramFile, @NotNull File[] paramArrayOfFile) {
    // Byte code:
    //   0: aload_0
    //   1: aload_1
    //   2: invokespecial readEnvelope : (Ljava/io/File;)Lio/sentry/SentryEnvelope;
    //   5: astore_3
    //   6: aload_3
    //   7: ifnull -> 18
    //   10: aload_0
    //   11: aload_3
    //   12: invokespecial isValidEnvelope : (Lio/sentry/SentryEnvelope;)Z
    //   15: ifne -> 19
    //   18: return
    //   19: aload_0
    //   20: getfield options : Lio/sentry/SentryOptions;
    //   23: invokevirtual getClientReportRecorder : ()Lio/sentry/clientreport/IClientReportRecorder;
    //   26: getstatic io/sentry/clientreport/DiscardReason.CACHE_OVERFLOW : Lio/sentry/clientreport/DiscardReason;
    //   29: aload_3
    //   30: invokeinterface recordLostEnvelope : (Lio/sentry/clientreport/DiscardReason;Lio/sentry/SentryEnvelope;)V
    //   35: aload_0
    //   36: aload_3
    //   37: invokespecial getFirstSession : (Lio/sentry/SentryEnvelope;)Lio/sentry/Session;
    //   40: astore #4
    //   42: aload #4
    //   44: ifnull -> 56
    //   47: aload_0
    //   48: aload #4
    //   50: invokespecial isValidSession : (Lio/sentry/Session;)Z
    //   53: ifne -> 57
    //   56: return
    //   57: aload #4
    //   59: invokevirtual getInit : ()Ljava/lang/Boolean;
    //   62: astore #5
    //   64: aload #5
    //   66: ifnull -> 77
    //   69: aload #5
    //   71: invokevirtual booleanValue : ()Z
    //   74: ifne -> 78
    //   77: return
    //   78: aload_2
    //   79: astore #6
    //   81: aload #6
    //   83: arraylength
    //   84: istore #7
    //   86: iconst_0
    //   87: istore #8
    //   89: iload #8
    //   91: iload #7
    //   93: if_icmpge -> 419
    //   96: aload #6
    //   98: iload #8
    //   100: aaload
    //   101: astore #9
    //   103: aload_0
    //   104: aload #9
    //   106: invokespecial readEnvelope : (Ljava/io/File;)Lio/sentry/SentryEnvelope;
    //   109: astore #10
    //   111: aload #10
    //   113: ifnull -> 413
    //   116: aload_0
    //   117: aload #10
    //   119: invokespecial isValidEnvelope : (Lio/sentry/SentryEnvelope;)Z
    //   122: ifne -> 128
    //   125: goto -> 413
    //   128: aconst_null
    //   129: astore #11
    //   131: aload #10
    //   133: invokevirtual getItems : ()Ljava/lang/Iterable;
    //   136: invokeinterface iterator : ()Ljava/util/Iterator;
    //   141: astore #12
    //   143: aload #12
    //   145: invokeinterface hasNext : ()Z
    //   150: ifeq -> 341
    //   153: aload #12
    //   155: invokeinterface next : ()Ljava/lang/Object;
    //   160: checkcast io/sentry/SentryEnvelopeItem
    //   163: astore #13
    //   165: aload_0
    //   166: aload #13
    //   168: invokespecial isSessionType : (Lio/sentry/SentryEnvelopeItem;)Z
    //   171: ifne -> 177
    //   174: goto -> 143
    //   177: aload_0
    //   178: aload #13
    //   180: invokespecial readSession : (Lio/sentry/SentryEnvelopeItem;)Lio/sentry/Session;
    //   183: astore #14
    //   185: aload #14
    //   187: ifnull -> 143
    //   190: aload_0
    //   191: aload #14
    //   193: invokespecial isValidSession : (Lio/sentry/Session;)Z
    //   196: ifne -> 202
    //   199: goto -> 143
    //   202: aload #14
    //   204: invokevirtual getInit : ()Ljava/lang/Boolean;
    //   207: astore #15
    //   209: aload #15
    //   211: ifnull -> 252
    //   214: aload #15
    //   216: invokevirtual booleanValue : ()Z
    //   219: ifeq -> 252
    //   222: aload_0
    //   223: getfield options : Lio/sentry/SentryOptions;
    //   226: invokevirtual getLogger : ()Lio/sentry/ILogger;
    //   229: getstatic io/sentry/SentryLevel.ERROR : Lio/sentry/SentryLevel;
    //   232: ldc 'Session %s has 2 times the init flag.'
    //   234: iconst_1
    //   235: anewarray java/lang/Object
    //   238: dup
    //   239: iconst_0
    //   240: aload #4
    //   242: invokevirtual getSessionId : ()Ljava/util/UUID;
    //   245: aastore
    //   246: invokeinterface log : (Lio/sentry/SentryLevel;Ljava/lang/String;[Ljava/lang/Object;)V
    //   251: return
    //   252: aload #4
    //   254: invokevirtual getSessionId : ()Ljava/util/UUID;
    //   257: ifnull -> 338
    //   260: aload #4
    //   262: invokevirtual getSessionId : ()Ljava/util/UUID;
    //   265: aload #14
    //   267: invokevirtual getSessionId : ()Ljava/util/UUID;
    //   270: invokevirtual equals : (Ljava/lang/Object;)Z
    //   273: ifeq -> 338
    //   276: aload #14
    //   278: invokevirtual setInitAsTrue : ()V
    //   281: aload_0
    //   282: getfield serializer : Lio/sentry/ISerializer;
    //   285: aload #14
    //   287: invokestatic fromSession : (Lio/sentry/ISerializer;Lio/sentry/Session;)Lio/sentry/SentryEnvelopeItem;
    //   290: astore #11
    //   292: aload #12
    //   294: invokeinterface remove : ()V
    //   299: goto -> 341
    //   302: astore #16
    //   304: aload_0
    //   305: getfield options : Lio/sentry/SentryOptions;
    //   308: invokevirtual getLogger : ()Lio/sentry/ILogger;
    //   311: getstatic io/sentry/SentryLevel.ERROR : Lio/sentry/SentryLevel;
    //   314: aload #16
    //   316: ldc 'Failed to create new envelope item for the session %s'
    //   318: iconst_1
    //   319: anewarray java/lang/Object
    //   322: dup
    //   323: iconst_0
    //   324: aload #4
    //   326: invokevirtual getSessionId : ()Ljava/util/UUID;
    //   329: aastore
    //   330: invokeinterface log : (Lio/sentry/SentryLevel;Ljava/lang/Throwable;Ljava/lang/String;[Ljava/lang/Object;)V
    //   335: goto -> 341
    //   338: goto -> 143
    //   341: aload #11
    //   343: ifnull -> 413
    //   346: aload_0
    //   347: aload #10
    //   349: aload #11
    //   351: invokespecial buildNewEnvelope : (Lio/sentry/SentryEnvelope;Lio/sentry/SentryEnvelopeItem;)Lio/sentry/SentryEnvelope;
    //   354: astore #13
    //   356: aload #9
    //   358: invokevirtual lastModified : ()J
    //   361: lstore #14
    //   363: aload #9
    //   365: invokevirtual delete : ()Z
    //   368: ifne -> 400
    //   371: aload_0
    //   372: getfield options : Lio/sentry/SentryOptions;
    //   375: invokevirtual getLogger : ()Lio/sentry/ILogger;
    //   378: getstatic io/sentry/SentryLevel.WARNING : Lio/sentry/SentryLevel;
    //   381: ldc 'File can't be deleted: %s'
    //   383: iconst_1
    //   384: anewarray java/lang/Object
    //   387: dup
    //   388: iconst_0
    //   389: aload #9
    //   391: invokevirtual getAbsolutePath : ()Ljava/lang/String;
    //   394: aastore
    //   395: invokeinterface log : (Lio/sentry/SentryLevel;Ljava/lang/String;[Ljava/lang/Object;)V
    //   400: aload_0
    //   401: aload #13
    //   403: aload #9
    //   405: lload #14
    //   407: invokespecial saveNewEnvelope : (Lio/sentry/SentryEnvelope;Ljava/io/File;J)V
    //   410: goto -> 419
    //   413: iinc #8, 1
    //   416: goto -> 89
    //   419: return
    // Exception table:
    //   from	to	target	type
    //   281	299	302	java/io/IOException
  }
  
  @Nullable
  private SentryEnvelope readEnvelope(@NotNull File paramFile) {
    try {
      BufferedInputStream bufferedInputStream = new BufferedInputStream(new FileInputStream(paramFile));
      try {
        SentryEnvelope sentryEnvelope = this.serializer.deserializeEnvelope(bufferedInputStream);
        bufferedInputStream.close();
        return sentryEnvelope;
      } catch (Throwable throwable) {
        try {
          bufferedInputStream.close();
        } catch (Throwable throwable1) {
          throwable.addSuppressed(throwable1);
        } 
        throw throwable;
      } 
    } catch (IOException iOException) {
      this.options.getLogger().log(SentryLevel.ERROR, "Failed to deserialize the envelope.", iOException);
      return null;
    } 
  }
  
  @Nullable
  private Session getFirstSession(@NotNull SentryEnvelope paramSentryEnvelope) {
    for (SentryEnvelopeItem sentryEnvelopeItem : paramSentryEnvelope.getItems()) {
      if (!isSessionType(sentryEnvelopeItem))
        continue; 
      return readSession(sentryEnvelopeItem);
    } 
    return null;
  }
  
  private boolean isValidSession(@NotNull Session paramSession) {
    if (!paramSession.getStatus().equals(Session.State.Ok))
      return false; 
    UUID uUID = paramSession.getSessionId();
    return (uUID != null);
  }
  
  private boolean isSessionType(@Nullable SentryEnvelopeItem paramSentryEnvelopeItem) {
    return (paramSentryEnvelopeItem == null) ? false : paramSentryEnvelopeItem.getHeader().getType().equals(SentryItemType.Session);
  }
  
  @Nullable
  private Session readSession(@NotNull SentryEnvelopeItem paramSentryEnvelopeItem) {
    try {
      BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(paramSentryEnvelopeItem.getData()), UTF_8));
      try {
        Session session = (Session)this.serializer.deserialize(bufferedReader, Session.class);
        bufferedReader.close();
        return session;
      } catch (Throwable throwable) {
        try {
          bufferedReader.close();
        } catch (Throwable throwable1) {
          throwable.addSuppressed(throwable1);
        } 
        throw throwable;
      } 
    } catch (Throwable throwable) {
      this.options.getLogger().log(SentryLevel.ERROR, "Failed to deserialize the session.", throwable);
      return null;
    } 
  }
  
  private void saveNewEnvelope(@NotNull SentryEnvelope paramSentryEnvelope, @NotNull File paramFile, long paramLong) {
    try {
      FileOutputStream fileOutputStream = new FileOutputStream(paramFile);
      try {
        this.serializer.serialize(paramSentryEnvelope, fileOutputStream);
        paramFile.setLastModified(paramLong);
        fileOutputStream.close();
      } catch (Throwable throwable) {
        try {
          fileOutputStream.close();
        } catch (Throwable throwable1) {
          throwable.addSuppressed(throwable1);
        } 
        throw throwable;
      } 
    } catch (Throwable throwable) {
      this.options.getLogger().log(SentryLevel.ERROR, "Failed to serialize the new envelope to the disk.", throwable);
    } 
  }
  
  @NotNull
  private SentryEnvelope buildNewEnvelope(@NotNull SentryEnvelope paramSentryEnvelope, @NotNull SentryEnvelopeItem paramSentryEnvelopeItem) {
    ArrayList<SentryEnvelopeItem> arrayList = new ArrayList();
    for (SentryEnvelopeItem sentryEnvelopeItem : paramSentryEnvelope.getItems())
      arrayList.add(sentryEnvelopeItem); 
    arrayList.add(paramSentryEnvelopeItem);
    return new SentryEnvelope(paramSentryEnvelope.getHeader(), arrayList);
  }
  
  private boolean isValidEnvelope(@NotNull SentryEnvelope paramSentryEnvelope) {
    return !!paramSentryEnvelope.getItems().iterator().hasNext();
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\io\sentry\cache\CacheStrategy.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */