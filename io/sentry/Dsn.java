package io.sentry;

import io.sentry.util.Objects;
import java.net.URI;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

final class Dsn {
  @NotNull
  private final String projectId;
  
  @Nullable
  private final String path;
  
  @Nullable
  private final String secretKey;
  
  @NotNull
  private final String publicKey;
  
  @NotNull
  private final URI sentryUri;
  
  @NotNull
  public String getProjectId() {
    return this.projectId;
  }
  
  @Nullable
  public String getPath() {
    return this.path;
  }
  
  @Nullable
  public String getSecretKey() {
    return this.secretKey;
  }
  
  @NotNull
  public String getPublicKey() {
    return this.publicKey;
  }
  
  @NotNull
  URI getSentryUri() {
    return this.sentryUri;
  }
  
  Dsn(@Nullable String paramString) throws IllegalArgumentException {
    try {
      Objects.requireNonNull(paramString, "The DSN is required.");
      URI uRI = (new URI(paramString)).normalize();
      String str1 = uRI.getScheme();
      if (!"http".equalsIgnoreCase(str1) && !"https".equalsIgnoreCase(str1))
        throw new IllegalArgumentException("Invalid DSN scheme: " + str1); 
      String str2 = uRI.getUserInfo();
      if (str2 == null || str2.isEmpty())
        throw new IllegalArgumentException("Invalid DSN: No public key provided."); 
      String[] arrayOfString = str2.split(":", -1);
      this.publicKey = arrayOfString[0];
      if (this.publicKey == null || this.publicKey.isEmpty())
        throw new IllegalArgumentException("Invalid DSN: No public key provided."); 
      this.secretKey = (arrayOfString.length > 1) ? arrayOfString[1] : null;
      String str3 = uRI.getPath();
      if (str3.endsWith("/"))
        str3 = str3.substring(0, str3.length() - 1); 
      int i = str3.lastIndexOf("/") + 1;
      String str4 = str3.substring(0, i);
      if (!str4.endsWith("/"))
        str4 = str4 + "/"; 
      this.path = str4;
      this.projectId = str3.substring(i);
      if (this.projectId.isEmpty())
        throw new IllegalArgumentException("Invalid DSN: A Project Id is required."); 
      this.sentryUri = new URI(str1, null, uRI.getHost(), uRI.getPort(), str4 + "api/" + this.projectId, null, null);
    } catch (Throwable throwable) {
      throw new IllegalArgumentException(throwable);
    } 
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\io\sentry\Dsn.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */