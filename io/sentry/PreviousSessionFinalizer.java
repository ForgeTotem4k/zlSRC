package io.sentry;

import io.sentry.cache.EnvelopeCache;
import io.sentry.cache.IEnvelopeCache;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.Date;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

final class PreviousSessionFinalizer implements Runnable {
  private static final Charset UTF_8 = Charset.forName("UTF-8");
  
  @NotNull
  private final SentryOptions options;
  
  @NotNull
  private final IScopes scopes;
  
  PreviousSessionFinalizer(@NotNull SentryOptions paramSentryOptions, @NotNull IScopes paramIScopes) {
    this.options = paramSentryOptions;
    this.scopes = paramIScopes;
  }
  
  public void run() {
    String str = this.options.getCacheDirPath();
    if (str == null) {
      this.options.getLogger().log(SentryLevel.INFO, "Cache dir is not set, not finalizing the previous session.", new Object[0]);
      return;
    } 
    if (!this.options.isEnableAutoSessionTracking()) {
      this.options.getLogger().log(SentryLevel.DEBUG, "Session tracking is disabled, bailing from previous session finalizer.", new Object[0]);
      return;
    } 
    IEnvelopeCache iEnvelopeCache = this.options.getEnvelopeDiskCache();
    if (iEnvelopeCache instanceof EnvelopeCache && !((EnvelopeCache)iEnvelopeCache).waitPreviousSessionFlush()) {
      this.options.getLogger().log(SentryLevel.WARNING, "Timed out waiting to flush previous session to its own file in session finalizer.", new Object[0]);
      return;
    } 
    File file = EnvelopeCache.getPreviousSessionFile(str);
    ISerializer iSerializer = this.options.getSerializer();
    if (file.exists()) {
      this.options.getLogger().log(SentryLevel.WARNING, "Current session is not ended, we'd need to end it.", new Object[0]);
      try {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(file), UTF_8));
        try {
          Session session = iSerializer.<Session>deserialize(bufferedReader, Session.class);
          if (session == null) {
            this.options.getLogger().log(SentryLevel.ERROR, "Stream from path %s resulted in a null envelope.", new Object[] { file.getAbsolutePath() });
          } else {
            Date date = null;
            File file1 = new File(this.options.getCacheDirPath(), ".sentry-native/last_crash");
            if (file1.exists()) {
              this.options.getLogger().log(SentryLevel.INFO, "Crash marker file exists, last Session is gonna be Crashed.", new Object[0]);
              date = getTimestampFromCrashMarkerFile(file1);
              if (!file1.delete())
                this.options.getLogger().log(SentryLevel.ERROR, "Failed to delete the crash marker file. %s.", new Object[] { file1.getAbsolutePath() }); 
              session.update(Session.State.Crashed, null, true);
            } 
            if (session.getAbnormalMechanism() == null)
              session.end(date); 
            SentryEnvelope sentryEnvelope = SentryEnvelope.from(iSerializer, session, this.options.getSdkVersion());
            this.scopes.captureEnvelope(sentryEnvelope);
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
      if (!file.delete())
        this.options.getLogger().log(SentryLevel.WARNING, "Failed to delete the previous session file.", new Object[0]); 
    } 
  }
  
  @Nullable
  private Date getTimestampFromCrashMarkerFile(@NotNull File paramFile) {
    try {
      BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(paramFile), UTF_8));
      try {
        String str = bufferedReader.readLine();
        this.options.getLogger().log(SentryLevel.DEBUG, "Crash marker file has %s timestamp.", new Object[] { str });
        Date date = DateUtils.getDateTime(str);
        bufferedReader.close();
        return date;
      } catch (Throwable throwable) {
        try {
          bufferedReader.close();
        } catch (Throwable throwable1) {
          throwable.addSuppressed(throwable1);
        } 
        throw throwable;
      } 
    } catch (IOException iOException) {
      this.options.getLogger().log(SentryLevel.ERROR, "Error reading the crash marker file.", iOException);
    } catch (IllegalArgumentException illegalArgumentException) {
      this.options.getLogger().log(SentryLevel.ERROR, illegalArgumentException, "Error converting the crash timestamp.", new Object[0]);
    } 
    return null;
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\io\sentry\PreviousSessionFinalizer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */