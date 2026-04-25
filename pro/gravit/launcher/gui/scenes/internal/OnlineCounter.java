package pro.gravit.launcher.gui.scenes.internal;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import javafx.application.Platform;
import pro.gravit.utils.helper.LogHelper;

public class OnlineCounter {
  private static final HttpClient httpClient = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(5L)).followRedirects(HttpClient.Redirect.ALWAYS).build();
  
  private static final String ONLINE_COUNTER = "https://api.endcraft.ru/api/v3/online.php";
  
  private static final String ONLINE_COUNTER2 = "https://api.endcraft.ru/api/v2/online.php";
  
  private static final Gson gson = new Gson();
  
  private OnlineUpdateCallback callback;
  
  public void setCallback(OnlineUpdateCallback paramOnlineUpdateCallback) {
    this.callback = paramOnlineUpdateCallback;
  }
  
  public CompletableFuture<Integer> fetchOnlineAsync() {
    CompletableFuture completableFuture1 = new CompletableFuture();
    CompletableFuture completableFuture2 = new CompletableFuture();
    HttpRequest httpRequest1 = HttpRequest.newBuilder().uri(URI.create("https://api.endcraft.ru/api/v3/online.php")).GET().build();
    HttpRequest httpRequest2 = HttpRequest.newBuilder().uri(URI.create("https://api.endcraft.ru/api/v2/online.php")).GET().build();
    httpClient.<String>sendAsync(httpRequest1, HttpResponse.BodyHandlers.ofString()).thenAccept(paramHttpResponse -> handleHttpResponse(paramHttpResponse, paramCompletableFuture)).exceptionally(paramThrowable -> {
          paramCompletableFuture.completeExceptionally(paramThrowable);
          return null;
        });
    httpClient.<String>sendAsync(httpRequest2, HttpResponse.BodyHandlers.ofString()).thenAccept(paramHttpResponse -> handleHttpResponse(paramHttpResponse, paramCompletableFuture)).exceptionally(paramThrowable -> {
          paramCompletableFuture.completeExceptionally(paramThrowable);
          return null;
        });
    return completableFuture1.thenCombine(completableFuture2, (paramInteger1, paramInteger2) -> {
          int i = paramInteger1.intValue() + paramInteger2.intValue();
          if (this.callback != null)
            Platform.runLater(()); 
          return Integer.valueOf(i);
        }).exceptionally(paramThrowable -> {
          LogHelper.error("Connection error: " + paramThrowable.getMessage());
          if (this.callback != null)
            Platform.runLater(()); 
          paramCompletableFuture.completeExceptionally(paramThrowable);
          return Integer.valueOf(0);
        });
  }
  
  private void handleHttpResponse(HttpResponse<String> paramHttpResponse, CompletableFuture<Integer> paramCompletableFuture) {
    Platform.runLater(() -> {
          try {
            LogHelper.info("HTTP Status: " + paramHttpResponse.statusCode());
            LogHelper.debug("Response Body: " + (String)paramHttpResponse.body());
            if (paramHttpResponse.statusCode() >= 200 && paramHttpResponse.statusCode() < 300) {
              String str = paramHttpResponse.body();
              JsonElement jsonElement = JsonParser.parseString(str);
              JsonObject jsonObject = jsonElement.getAsJsonObject();
              boolean bool = jsonObject.get("success").getAsBoolean();
              if (bool) {
                JsonObject jsonObject1 = jsonObject.getAsJsonObject("body");
                int i = jsonObject1.get("online").getAsInt();
                LogHelper.info("Server online: " + i + " players");
                paramCompletableFuture.complete(Integer.valueOf(i));
                if (this.callback != null)
                  this.callback.onOnlineReceived(i); 
              } else {
                String str1 = "API returned success: false";
                LogHelper.warning(str1);
                paramCompletableFuture.completeExceptionally(new RuntimeException(str1));
                if (this.callback != null)
                  this.callback.onError(str1); 
              } 
            } else {
              String str = "HTTP error: " + paramHttpResponse.statusCode();
              LogHelper.error(str);
              paramCompletableFuture.completeExceptionally(new RuntimeException(str));
              if (this.callback != null)
                this.callback.onError(str); 
            } 
          } catch (JsonSyntaxException jsonSyntaxException) {
            String str = "Invalid JSON response: " + jsonSyntaxException.getMessage();
            LogHelper.error(str);
            paramCompletableFuture.completeExceptionally((Throwable)jsonSyntaxException);
            if (this.callback != null)
              this.callback.onError(str); 
          } catch (Exception exception) {
            String str = "Unexpected error: " + exception.getMessage();
            LogHelper.error(str);
            paramCompletableFuture.completeExceptionally(exception);
            if (this.callback != null)
              this.callback.onError(str); 
          } 
        });
  }
  
  public int fetchOnlineSync() throws Exception {
    HttpRequest httpRequest = HttpRequest.newBuilder().uri(URI.create("https://api.endcraft.ru/api/v3/online.php")).GET().build();
    HttpResponse<String> httpResponse = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
    if (httpResponse.statusCode() >= 200 && httpResponse.statusCode() < 300) {
      JsonObject jsonObject = JsonParser.parseString(httpResponse.body()).getAsJsonObject();
      if (jsonObject.get("success").getAsBoolean())
        return jsonObject.getAsJsonObject("body").get("online").getAsInt(); 
      throw new RuntimeException("API returned success: false");
    } 
    throw new RuntimeException("HTTP error: " + httpResponse.statusCode());
  }
  
  public static interface OnlineUpdateCallback {
    void onOnlineReceived(int param1Int);
    
    void onError(String param1String);
    
    static {
    
    }
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\pro\gravit\launcher\gui\scenes\internal\OnlineCounter.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */