package io.sentry.cache;

import io.sentry.DateUtils;
import io.sentry.Hint;
import io.sentry.SentryCrashLastRunState;
import io.sentry.SentryEnvelope;
import io.sentry.SentryEnvelopeItem;
import io.sentry.SentryItemType;
import io.sentry.SentryLevel;
import io.sentry.SentryOptions;
import io.sentry.Session;
import io.sentry.UncaughtExceptionHandlerIntegration;
import io.sentry.hints.AbnormalExit;
import io.sentry.hints.SessionEnd;
import io.sentry.hints.SessionStart;
import io.sentry.transport.NoOpEnvelopeCache;
import io.sentry.util.HintUtils;
import io.sentry.util.Objects;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;
import java.util.WeakHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import org.jetbrains.annotations.ApiStatus.Internal;
import org.jetbrains.annotations.NotNull;

@Internal
public class EnvelopeCache extends CacheStrategy implements IEnvelopeCache {
  public static final String SUFFIX_ENVELOPE_FILE = ".envelope";
  
  public static final String PREFIX_CURRENT_SESSION_FILE = "session";
  
  public static final String PREFIX_PREVIOUS_SESSION_FILE = "previous_session";
  
  static final String SUFFIX_SESSION_FILE = ".json";
  
  public static final String CRASH_MARKER_FILE = "last_crash";
  
  public static final String NATIVE_CRASH_MARKER_FILE = ".sentry-native/last_crash";
  
  public static final String STARTUP_CRASH_MARKER_FILE = "startup_crash";
  
  private final CountDownLatch previousSessionLatch = new CountDownLatch(1);
  
  @NotNull
  private final Map<SentryEnvelope, String> fileNameMap = new WeakHashMap<>();
  
  @NotNull
  public static IEnvelopeCache create(@NotNull SentryOptions paramSentryOptions) {
    String str = paramSentryOptions.getCacheDirPath();
    int i = paramSentryOptions.getMaxCacheItems();
    if (str == null) {
      paramSentryOptions.getLogger().log(SentryLevel.WARNING, "cacheDirPath is null, returning NoOpEnvelopeCache", new Object[0]);
      return (IEnvelopeCache)NoOpEnvelopeCache.getInstance();
    } 
    return new EnvelopeCache(paramSentryOptions, str, i);
  }
  
  public EnvelopeCache(@NotNull SentryOptions paramSentryOptions, @NotNull String paramString, int paramInt) {
    super(paramSentryOptions, paramString, paramInt);
  }
  
  public void store(@NotNull SentryEnvelope paramSentryEnvelope, @NotNull Hint paramHint) {
    Objects.requireNonNull(paramSentryEnvelope, "Envelope is required.");
    rotateCacheIfNeeded(allEnvelopeFiles());
    File file1 = getCurrentSessionFile(this.directory.getAbsolutePath());
    File file2 = getPreviousSessionFile(this.directory.getAbsolutePath());
    if (HintUtils.hasType(paramHint, SessionEnd.class) && !file1.delete())
      this.options.getLogger().log(SentryLevel.WARNING, "Current envelope doesn't exist.", new Object[0]); 
    if (HintUtils.hasType(paramHint, AbnormalExit.class))
      tryEndPreviousSession(paramHint); 
    if (HintUtils.hasType(paramHint, SessionStart.class)) {
      if (file1.exists()) {
        this.options.getLogger().log(SentryLevel.WARNING, "Current session is not ended, we'd need to end it.", new Object[0]);
        try {
          BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(file1), UTF_8));
          try {
            Session session = (Session)this.serializer.deserialize(bufferedReader, Session.class);
            if (session != null)
              writeSessionToDisk(file2, session); 
            bufferedReader.close();
          } catch (Throwable throwable) {
            try {
              bufferedReader.close();
            } catch (Throwable throwable1) {
              throwable.addSuppressed(throwable1);
            } 
            throw throwable;
          } 
        } catch (Throwable throwable) {
          this.options.getLogger().log(SentryLevel.ERROR, "Error processing session.", throwable);
        } 
      } 
      updateCurrentSession(file1, paramSentryEnvelope);
      boolean bool = false;
      File file = new File(this.options.getCacheDirPath(), ".sentry-native/last_crash");
      if (file.exists())
        bool = true; 
      if (!bool) {
        File file4 = new File(this.options.getCacheDirPath(), "last_crash");
        if (file4.exists()) {
          this.options.getLogger().log(SentryLevel.INFO, "Crash marker file exists, crashedLastRun will return true.", new Object[0]);
          bool = true;
          if (!file4.delete())
            this.options.getLogger().log(SentryLevel.ERROR, "Failed to delete the crash marker file. %s.", new Object[] { file4.getAbsolutePath() }); 
        } 
      } 
      SentryCrashLastRunState.getInstance().setCrashedLastRun(bool);
      flushPreviousSession();
    } 
    File file3 = getEnvelopeFile(paramSentryEnvelope);
    if (file3.exists()) {
      this.options.getLogger().log(SentryLevel.WARNING, "Not adding Envelope to offline storage because it already exists: %s", new Object[] { file3.getAbsolutePath() });
      return;
    } 
    this.options.getLogger().log(SentryLevel.DEBUG, "Adding Envelope to offline storage: %s", new Object[] { file3.getAbsolutePath() });
    writeEnvelopeToDisk(file3, paramSentryEnvelope);
    if (HintUtils.hasType(paramHint, UncaughtExceptionHandlerIntegration.UncaughtExceptionHint.class))
      writeCrashMarkerFile(); 
  }
  
  private void tryEndPreviousSession(@NotNull Hint paramHint) {
    Object object = HintUtils.getSentrySdkHint(paramHint);
    if (object instanceof AbnormalExit) {
      File file = getPreviousSessionFile(this.directory.getAbsolutePath());
      if (file.exists()) {
        this.options.getLogger().log(SentryLevel.WARNING, "Previous session is not ended, we'd need to end it.", new Object[0]);
        try {
          BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(file), UTF_8));
          try {
            Session session = (Session)this.serializer.deserialize(bufferedReader, Session.class);
            if (session != null) {
              AbnormalExit abnormalExit = (AbnormalExit)object;
              Long long_ = abnormalExit.timestamp();
              Date date = null;
              if (long_ != null) {
                date = DateUtils.getDateTime(long_.longValue());
                Date date1 = session.getStarted();
                if (date1 == null || date.before(date1)) {
                  this.options.getLogger().log(SentryLevel.WARNING, "Abnormal exit happened before previous session start, not ending the session.", new Object[0]);
                  bufferedReader.close();
                  return;
                } 
              } 
              String str = abnormalExit.mechanism();
              session.update(Session.State.Abnormal, null, true, str);
              session.end(date);
              writeSessionToDisk(file, session);
            } 
            bufferedReader.close();
          } catch (Throwable throwable) {
            try {
              bufferedReader.close();
            } catch (Throwable throwable1) {
              throwable.addSuppressed(throwable1);
            } 
            throw throwable;
          } 
        } catch (Throwable throwable) {
          this.options.getLogger().log(SentryLevel.ERROR, "Error processing previous session.", throwable);
        } 
      } else {
        this.options.getLogger().log(SentryLevel.DEBUG, "No previous session file to end.", new Object[0]);
      } 
    } 
  }
  
  private void writeCrashMarkerFile() {
    File file = new File(this.options.getCacheDirPath(), "last_crash");
    try {
      FileOutputStream fileOutputStream = new FileOutputStream(file);
      try {
        String str = DateUtils.getTimestamp(DateUtils.getCurrentDateTime());
        fileOutputStream.write(str.getBytes(UTF_8));
        fileOutputStream.flush();
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
      this.options.getLogger().log(SentryLevel.ERROR, "Error writing the crash marker file to the disk", throwable);
    } 
  }
  
  private void updateCurrentSession(@NotNull File paramFile, @NotNull SentryEnvelope paramSentryEnvelope) {
    Iterable<SentryEnvelopeItem> iterable = paramSentryEnvelope.getItems();
    if (iterable.iterator().hasNext()) {
      SentryEnvelopeItem sentryEnvelopeItem = iterable.iterator().next();
      if (SentryItemType.Session.equals(sentryEnvelopeItem.getHeader().getType())) {
        try {
          BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(sentryEnvelopeItem.getData()), UTF_8));
          try {
            Session session = (Session)this.serializer.deserialize(bufferedReader, Session.class);
            if (session == null) {
              this.options.getLogger().log(SentryLevel.ERROR, "Item of type %s returned null by the parser.", new Object[] { sentryEnvelopeItem.getHeader().getType() });
            } else {
              writeSessionToDisk(paramFile, session);
            } 
            bufferedReader.close();
          } catch (Throwable throwable) {
            try {
              bufferedReader.close();
            } catch (Throwable throwable1) {
              throwable.addSuppressed(throwable1);
            } 
            throw throwable;
          } 
        } catch (Throwable throwable) {
          this.options.getLogger().log(SentryLevel.ERROR, "Item failed to process.", throwable);
        } 
      } else {
        this.options.getLogger().log(SentryLevel.INFO, "Current envelope has a different envelope type %s", new Object[] { sentryEnvelopeItem.getHeader().getType() });
      } 
    } else {
      this.options.getLogger().log(SentryLevel.INFO, "Current envelope %s is empty", new Object[] { paramFile.getAbsolutePath() });
    } 
  }
  
  private void writeEnvelopeToDisk(@NotNull File paramFile, @NotNull SentryEnvelope paramSentryEnvelope) {
    if (paramFile.exists()) {
      this.options.getLogger().log(SentryLevel.DEBUG, "Overwriting envelope to offline storage: %s", new Object[] { paramFile.getAbsolutePath() });
      if (!paramFile.delete())
        this.options.getLogger().log(SentryLevel.ERROR, "Failed to delete: %s", new Object[] { paramFile.getAbsolutePath() }); 
    } 
    try {
      FileOutputStream fileOutputStream = new FileOutputStream(paramFile);
      try {
        this.serializer.serialize(paramSentryEnvelope, fileOutputStream);
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
      this.options.getLogger().log(SentryLevel.ERROR, throwable, "Error writing Envelope %s to offline storage", new Object[] { paramFile.getAbsolutePath() });
    } 
  }
  
  private void writeSessionToDisk(@NotNull File paramFile, @NotNull Session paramSession) {
    if (paramFile.exists()) {
      this.options.getLogger().log(SentryLevel.DEBUG, "Overwriting session to offline storage: %s", new Object[] { paramSession.getSessionId() });
      if (!paramFile.delete())
        this.options.getLogger().log(SentryLevel.ERROR, "Failed to delete: %s", new Object[] { paramFile.getAbsolutePath() }); 
    } 
    try {
      FileOutputStream fileOutputStream = new FileOutputStream(paramFile);
      try {
        BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(fileOutputStream, UTF_8));
        try {
          this.serializer.serialize(paramSession, bufferedWriter);
          bufferedWriter.close();
        } catch (Throwable throwable) {
          try {
            bufferedWriter.close();
          } catch (Throwable throwable1) {
            throwable.addSuppressed(throwable1);
          } 
          throw throwable;
        } 
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
      this.options.getLogger().log(SentryLevel.ERROR, throwable, "Error writing Session to offline storage: %s", new Object[] { paramSession.getSessionId() });
    } 
  }
  
  public void discard(@NotNull SentryEnvelope paramSentryEnvelope) {
    Objects.requireNonNull(paramSentryEnvelope, "Envelope is required.");
    File file = getEnvelopeFile(paramSentryEnvelope);
    if (file.exists()) {
      this.options.getLogger().log(SentryLevel.DEBUG, "Discarding envelope from cache: %s", new Object[] { file.getAbsolutePath() });
      if (!file.delete())
        this.options.getLogger().log(SentryLevel.ERROR, "Failed to delete envelope: %s", new Object[] { file.getAbsolutePath() }); 
    } else {
      this.options.getLogger().log(SentryLevel.DEBUG, "Envelope was not cached: %s", new Object[] { file.getAbsolutePath() });
    } 
  }
  
  @NotNull
  private synchronized File getEnvelopeFile(@NotNull SentryEnvelope paramSentryEnvelope) {
    String str;
    if (this.fileNameMap.containsKey(paramSentryEnvelope)) {
      str = this.fileNameMap.get(paramSentryEnvelope);
    } else {
      str = UUID.randomUUID() + ".envelope";
      this.fileNameMap.put(paramSentryEnvelope, str);
    } 
    return new File(this.directory.getAbsolutePath(), str);
  }
  
  @NotNull
  public static File getCurrentSessionFile(@NotNull String paramString) {
    return new File(paramString, "session.json");
  }
  
  @NotNull
  public static File getPreviousSessionFile(@NotNull String paramString) {
    return new File(paramString, "previous_session.json");
  }
  
  @NotNull
  public Iterator<SentryEnvelope> iterator() {
    File[] arrayOfFile = allEnvelopeFiles();
    ArrayList<SentryEnvelope> arrayList = new ArrayList(arrayOfFile.length);
    for (File file : arrayOfFile) {
      try {
        BufferedInputStream bufferedInputStream = new BufferedInputStream(new FileInputStream(file));
        try {
          arrayList.add(this.serializer.deserializeEnvelope(bufferedInputStream));
          bufferedInputStream.close();
        } catch (Throwable throwable) {
          try {
            bufferedInputStream.close();
          } catch (Throwable throwable1) {
            throwable.addSuppressed(throwable1);
          } 
          throw throwable;
        } 
      } catch (FileNotFoundException fileNotFoundException) {
        this.options.getLogger().log(SentryLevel.DEBUG, "Envelope file '%s' disappeared while converting all cached files to envelopes.", new Object[] { file.getAbsolutePath() });
      } catch (IOException iOException) {
        this.options.getLogger().log(SentryLevel.ERROR, String.format("Error while reading cached envelope from file %s", new Object[] { file.getAbsolutePath() }), iOException);
      } 
    } 
    return arrayList.iterator();
  }
  
  @NotNull
  private File[] allEnvelopeFiles() {
    if (isDirectoryValid()) {
      File[] arrayOfFile = this.directory.listFiles((paramFile, paramString) -> paramString.endsWith(".envelope"));
      if (arrayOfFile != null)
        return arrayOfFile; 
    } 
    return new File[0];
  }
  
  public boolean waitPreviousSessionFlush() {
    try {
      return this.previousSessionLatch.await(this.options.getSessionFlushTimeoutMillis(), TimeUnit.MILLISECONDS);
    } catch (InterruptedException interruptedException) {
      Thread.currentThread().interrupt();
      this.options.getLogger().log(SentryLevel.DEBUG, "Timed out waiting for previous session to flush.", new Object[0]);
      return false;
    } 
  }
  
  public void flushPreviousSession() {
    this.previousSessionLatch.countDown();
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\io\sentry\cache\EnvelopeCache.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */