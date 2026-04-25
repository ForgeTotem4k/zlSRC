package io.sentry;

import io.sentry.util.Platform;
import java.io.Closeable;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.util.concurrent.RejectedExecutionException;
import java.util.zip.GZIPOutputStream;
import org.jetbrains.annotations.ApiStatus.Internal;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.TestOnly;

@Internal
public final class SpotlightIntegration implements Integration, SentryOptions.BeforeEnvelopeCallback, Closeable {
  @Nullable
  private SentryOptions options;
  
  @NotNull
  private ILogger logger = NoOpLogger.getInstance();
  
  @NotNull
  private ISentryExecutorService executorService = NoOpSentryExecutorService.getInstance();
  
  public void register(@NotNull IScopes paramIScopes, @NotNull SentryOptions paramSentryOptions) {
    this.options = paramSentryOptions;
    this.logger = paramSentryOptions.getLogger();
    if (paramSentryOptions.getBeforeEnvelopeCallback() == null && paramSentryOptions.isEnableSpotlight()) {
      this.executorService = new SentryExecutorService();
      paramSentryOptions.setBeforeEnvelopeCallback(this);
      this.logger.log(SentryLevel.DEBUG, "SpotlightIntegration enabled.", new Object[0]);
    } else {
      this.logger.log(SentryLevel.DEBUG, "SpotlightIntegration is not enabled. BeforeEnvelopeCallback is already set or spotlight is not enabled.", new Object[0]);
    } 
  }
  
  public void execute(@NotNull SentryEnvelope paramSentryEnvelope, @Nullable Hint paramHint) {
    try {
      this.executorService.submit(() -> sendEnvelope(paramSentryEnvelope));
    } catch (RejectedExecutionException rejectedExecutionException) {
      this.logger.log(SentryLevel.WARNING, "Spotlight envelope submission rejected.", rejectedExecutionException);
    } 
  }
  
  private void sendEnvelope(@NotNull SentryEnvelope paramSentryEnvelope) {
    try {
      if (this.options == null)
        throw new IllegalArgumentException("SentryOptions are required to send envelopes."); 
      String str = getSpotlightConnectionUrl();
      HttpURLConnection httpURLConnection = createConnection(str);
      try {
        OutputStream outputStream = httpURLConnection.getOutputStream();
        try {
          GZIPOutputStream gZIPOutputStream = new GZIPOutputStream(outputStream);
          try {
            this.options.getSerializer().serialize(paramSentryEnvelope, gZIPOutputStream);
            gZIPOutputStream.close();
          } catch (Throwable throwable) {
            try {
              gZIPOutputStream.close();
            } catch (Throwable throwable1) {
              throwable.addSuppressed(throwable1);
            } 
            throw throwable;
          } 
          if (outputStream != null)
            outputStream.close(); 
        } catch (Throwable throwable) {
          if (outputStream != null)
            try {
              outputStream.close();
            } catch (Throwable throwable1) {
              throwable.addSuppressed(throwable1);
            }  
          throw throwable;
        } 
      } catch (Throwable throwable) {
        this.logger.log(SentryLevel.ERROR, "An exception occurred while submitting the envelope to the Sentry server.", throwable);
      } finally {
        int i = httpURLConnection.getResponseCode();
        this.logger.log(SentryLevel.DEBUG, "Envelope sent to spotlight: %d", new Object[] { Integer.valueOf(i) });
        closeAndDisconnect(httpURLConnection);
      } 
    } catch (Exception exception) {
      this.logger.log(SentryLevel.ERROR, "An exception occurred while creating the connection to spotlight.", exception);
    } 
  }
  
  @TestOnly
  public String getSpotlightConnectionUrl() {
    return (this.options != null && this.options.getSpotlightConnectionUrl() != null) ? this.options.getSpotlightConnectionUrl() : (Platform.isAndroid() ? "http://10.0.2.2:8969/stream" : "http://localhost:8969/stream");
  }
  
  @NotNull
  private HttpURLConnection createConnection(@NotNull String paramString) throws Exception {
    HttpURLConnection httpURLConnection = (HttpURLConnection)URI.create(paramString).toURL().openConnection();
    httpURLConnection.setReadTimeout(1000);
    httpURLConnection.setConnectTimeout(1000);
    httpURLConnection.setRequestMethod("POST");
    httpURLConnection.setDoOutput(true);
    httpURLConnection.setRequestProperty("Content-Encoding", "gzip");
    httpURLConnection.setRequestProperty("Content-Type", "application/x-sentry-envelope");
    httpURLConnection.setRequestProperty("Accept", "application/json");
    httpURLConnection.setRequestProperty("Connection", "close");
    httpURLConnection.connect();
    return httpURLConnection;
  }
  
  private void closeAndDisconnect(@NotNull HttpURLConnection paramHttpURLConnection) {
    try {
      paramHttpURLConnection.getInputStream().close();
    } catch (IOException iOException) {
    
    } finally {
      paramHttpURLConnection.disconnect();
    } 
  }
  
  public void close() throws IOException {
    this.executorService.close(0L);
    if (this.options != null && this.options.getBeforeEnvelopeCallback() == this)
      this.options.setBeforeEnvelopeCallback(null); 
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\io\sentry\SpotlightIntegration.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */