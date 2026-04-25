package io.sentry.transport;

import io.sentry.RequestDetails;
import io.sentry.SentryEnvelope;
import io.sentry.SentryLevel;
import io.sentry.SentryOptions;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.nio.charset.Charset;
import java.util.Map;
import java.util.zip.GZIPOutputStream;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSocketFactory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.TestOnly;

final class HttpConnection {
  private static final Charset UTF_8 = Charset.forName("UTF-8");
  
  @Nullable
  private final Proxy proxy;
  
  @NotNull
  private final RequestDetails requestDetails;
  
  @NotNull
  private final SentryOptions options;
  
  @NotNull
  private final RateLimiter rateLimiter;
  
  public HttpConnection(@NotNull SentryOptions paramSentryOptions, @NotNull RequestDetails paramRequestDetails, @NotNull RateLimiter paramRateLimiter) {
    this(paramSentryOptions, paramRequestDetails, AuthenticatorWrapper.getInstance(), paramRateLimiter);
  }
  
  HttpConnection(@NotNull SentryOptions paramSentryOptions, @NotNull RequestDetails paramRequestDetails, @NotNull AuthenticatorWrapper paramAuthenticatorWrapper, @NotNull RateLimiter paramRateLimiter) {
    this.requestDetails = paramRequestDetails;
    this.options = paramSentryOptions;
    this.rateLimiter = paramRateLimiter;
    this.proxy = resolveProxy(paramSentryOptions.getProxy());
    if (this.proxy != null && paramSentryOptions.getProxy() != null) {
      String str1 = paramSentryOptions.getProxy().getUser();
      String str2 = paramSentryOptions.getProxy().getPass();
      if (str1 != null && str2 != null)
        paramAuthenticatorWrapper.setDefault(new ProxyAuthenticator(str1, str2)); 
    } 
  }
  
  @Nullable
  private Proxy resolveProxy(@Nullable SentryOptions.Proxy paramProxy) {
    Proxy proxy = null;
    if (paramProxy != null) {
      String str1 = paramProxy.getPort();
      String str2 = paramProxy.getHost();
      if (str1 != null && str2 != null)
        try {
          Proxy.Type type;
          if (paramProxy.getType() != null) {
            type = paramProxy.getType();
          } else {
            type = Proxy.Type.HTTP;
          } 
          InetSocketAddress inetSocketAddress = new InetSocketAddress(str2, Integer.parseInt(str1));
          proxy = new Proxy(type, inetSocketAddress);
        } catch (NumberFormatException numberFormatException) {
          this.options.getLogger().log(SentryLevel.ERROR, numberFormatException, "Failed to parse Sentry Proxy port: " + paramProxy.getPort() + ". Proxy is ignored", new Object[0]);
        }  
    } 
    return proxy;
  }
  
  @NotNull
  HttpURLConnection open() throws IOException {
    return (this.proxy == null) ? (HttpURLConnection)this.requestDetails.getUrl().openConnection() : (HttpURLConnection)this.requestDetails.getUrl().openConnection(this.proxy);
  }
  
  @NotNull
  private HttpURLConnection createConnection() throws IOException {
    HttpURLConnection httpURLConnection = open();
    for (Map.Entry entry : this.requestDetails.getHeaders().entrySet())
      httpURLConnection.setRequestProperty((String)entry.getKey(), (String)entry.getValue()); 
    httpURLConnection.setRequestMethod("POST");
    httpURLConnection.setDoOutput(true);
    httpURLConnection.setRequestProperty("Content-Encoding", "gzip");
    httpURLConnection.setRequestProperty("Content-Type", "application/x-sentry-envelope");
    httpURLConnection.setRequestProperty("Accept", "application/json");
    httpURLConnection.setRequestProperty("Connection", "close");
    httpURLConnection.setConnectTimeout(this.options.getConnectionTimeoutMillis());
    httpURLConnection.setReadTimeout(this.options.getReadTimeoutMillis());
    SSLSocketFactory sSLSocketFactory = this.options.getSslSocketFactory();
    if (httpURLConnection instanceof HttpsURLConnection && sSLSocketFactory != null)
      ((HttpsURLConnection)httpURLConnection).setSSLSocketFactory(sSLSocketFactory); 
    httpURLConnection.connect();
    return httpURLConnection;
  }
  
  @NotNull
  public TransportResult send(@NotNull SentryEnvelope paramSentryEnvelope) throws IOException {
    TransportResult transportResult;
    HttpURLConnection httpURLConnection = createConnection();
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
      this.options.getLogger().log(SentryLevel.ERROR, throwable, "An exception occurred while submitting the envelope to the Sentry server.", new Object[0]);
    } finally {
      transportResult = readAndLog(httpURLConnection);
    } 
    return transportResult;
  }
  
  @NotNull
  private TransportResult readAndLog(@NotNull HttpURLConnection paramHttpURLConnection) {
    try {
      int i = paramHttpURLConnection.getResponseCode();
      updateRetryAfterLimits(paramHttpURLConnection, i);
      if (!isSuccessfulResponseCode(i)) {
        this.options.getLogger().log(SentryLevel.ERROR, "Request failed, API returned %s", new Object[] { Integer.valueOf(i) });
        if (this.options.isDebug()) {
          String str = getErrorMessageFromStream(paramHttpURLConnection);
          this.options.getLogger().log(SentryLevel.ERROR, "%s", new Object[] { str });
        } 
        return TransportResult.error(i);
      } 
      this.options.getLogger().log(SentryLevel.DEBUG, "Envelope sent successfully.", new Object[0]);
      return TransportResult.success();
    } catch (IOException iOException) {
      this.options.getLogger().log(SentryLevel.ERROR, iOException, "Error reading and logging the response stream", new Object[0]);
    } finally {
      closeAndDisconnect(paramHttpURLConnection);
    } 
    return TransportResult.error();
  }
  
  public void updateRetryAfterLimits(@NotNull HttpURLConnection paramHttpURLConnection, int paramInt) {
    String str1 = paramHttpURLConnection.getHeaderField("Retry-After");
    String str2 = paramHttpURLConnection.getHeaderField("X-Sentry-Rate-Limits");
    this.rateLimiter.updateRetryAfterLimits(str2, str1, paramInt);
  }
  
  private void closeAndDisconnect(@NotNull HttpURLConnection paramHttpURLConnection) {
    try {
      paramHttpURLConnection.getInputStream().close();
    } catch (IOException iOException) {
    
    } finally {
      paramHttpURLConnection.disconnect();
    } 
  }
  
  @NotNull
  private String getErrorMessageFromStream(@NotNull HttpURLConnection paramHttpURLConnection) {
    try {
      InputStream inputStream = paramHttpURLConnection.getErrorStream();
      try {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, UTF_8));
        try {
          StringBuilder stringBuilder = new StringBuilder();
          String str1;
          boolean bool;
          for (bool = true; (str1 = bufferedReader.readLine()) != null; bool = false) {
            if (!bool)
              stringBuilder.append("\n"); 
            stringBuilder.append(str1);
          } 
          String str2 = stringBuilder.toString();
          bufferedReader.close();
          if (inputStream != null)
            inputStream.close(); 
          return str2;
        } catch (Throwable throwable) {
          try {
            bufferedReader.close();
          } catch (Throwable throwable1) {
            throwable.addSuppressed(throwable1);
          } 
          throw throwable;
        } 
      } catch (Throwable throwable) {
        if (inputStream != null)
          try {
            inputStream.close();
          } catch (Throwable throwable1) {
            throwable.addSuppressed(throwable1);
          }  
        throw throwable;
      } 
    } catch (IOException iOException) {
      return "Failed to obtain error message while analyzing send failure.";
    } 
  }
  
  private boolean isSuccessfulResponseCode(int paramInt) {
    return (paramInt == 200);
  }
  
  @TestOnly
  @Nullable
  Proxy getProxy() {
    return this.proxy;
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\io\sentry\transport\HttpConnection.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */