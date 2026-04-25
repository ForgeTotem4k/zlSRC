package pro.gravit.launcher.gui.scenes.internal;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBase;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import pro.gravit.launcher.gui.JavaFXApplication;
import pro.gravit.launcher.gui.helper.LookupHelper;
import pro.gravit.launcher.gui.scenes.AbstractScene;
import pro.gravit.utils.helper.LogHelper;

public class BrowserScene extends AbstractScene {
  private PasswordField passwordField;
  
  private PasswordField confirmField;
  
  private Button regButton;
  
  private TextField loginField;
  
  private StackPane stackPane;
  
  private String username;
  
  private String password;
  
  private static final HttpClient httpClient = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(5L)).followRedirects(HttpClient.Redirect.ALWAYS).build();
  
  private static final String REGISTER_URL = "https://api.endcraft.ru/launcher-v2/register/index.php";
  
  public BrowserScene(JavaFXApplication paramJavaFXApplication) {
    super("scenes/internal/browser/browser.fxml", paramJavaFXApplication);
  }
  
  public String getName() {
    return "browser";
  }
  
  protected void doInit() {
    this.loginField = (TextField)LookupHelper.lookup((Node)this.layout, new String[] { "#loginField" });
    this.passwordField = (PasswordField)LookupHelper.lookup((Node)this.layout, new String[] { "#passwordField" });
    this.confirmField = (PasswordField)LookupHelper.lookup((Node)this.layout, new String[] { "#confirmField" });
    this.stackPane = (StackPane)LookupHelper.lookup((Node)this.layout, new String[] { "#content" });
    this.regButton = (Button)LookupHelper.lookup((Node)this.layout, new String[] { "#registerButton" });
    this.passwordField.setOnMouseClicked(paramMouseEvent -> this.passwordField.setPromptText(""));
    this.confirmField.setOnMouseClicked(paramMouseEvent -> this.confirmField.setPromptText(""));
    ((ButtonBase)LookupHelper.lookup((Node)this.layout, new String[] { "#back" })).setOnMouseClicked(paramMouseEvent -> {
          try {
            switchToBackScene();
          } catch (Exception exception) {
            errorHandle(exception);
          } 
        });
    genUsername();
    ((ImageView)LookupHelper.lookup((Node)this.layout, new String[] { "#vk" })).setOnMouseClicked(paramMouseEvent -> this.application.openURL("https://vk.com/end_craft_ru"));
    ((ImageView)LookupHelper.lookup((Node)this.layout, new String[] { "#ds" })).setOnMouseClicked(paramMouseEvent -> this.application.openURL("https://discord.gg/wuczdENP"));
    this.regButton.setOnAction(paramActionEvent -> register());
  }
  
  public void reset() {
    genUsername();
    this.regButton.setDisable(false);
    this.regButton.setText("Создать персонажа!");
    this.passwordField.clear();
    if (this.confirmField != null)
      this.confirmField.clear(); 
  }
  
  private void genUsername() {
    String str1 = String.valueOf(System.currentTimeMillis());
    String str2 = "PL" + str1;
    this.loginField.setText(str2);
    this.loginField.setFocusTraversable(false);
  }
  
  private void register() {
    this.username = this.loginField.getText();
    this.password = this.passwordField.getText();
    if (!this.password.equals(this.confirmField.getText())) {
      showError("Пароли не совпадают");
      return;
    } 
    if (this.password.length() < 4) {
      showError("Пароль должен содержать минимум 4 символов!");
      return;
    } 
    if (!this.username.matches("^[a-zA-Z0-9_]{3,16}$")) {
      showError("Логин должен содержать от 3 до 16 символов \n (латинские буквы, цифры и подчеркивание)!");
      return;
    } 
    this.regButton.setDisable(true);
    this.regButton.setText("Регистрирую!");
    sendRegisterRequest();
  }
  
  private void sendRegisterRequest() {
    String str1 = escapeJson(this.username);
    String str2 = escapeJson(this.password);
    String str3 = String.format("{\"username\":\"%s\",\"password\":\"%s\"}", new Object[] { str1, str2 });
    HttpRequest httpRequest = HttpRequest.newBuilder().uri(URI.create("https://api.endcraft.ru/launcher-v2/register/index.php")).header("Content-Type", "application/json; charset=utf-8").header("Accept", "application/json").POST(HttpRequest.BodyPublishers.ofString(str3)).timeout(Duration.ofSeconds(15L)).build();
    httpClient.<String>sendAsync(httpRequest, HttpResponse.BodyHandlers.ofString()).thenAccept(this::handleHttpResponse).exceptionally(paramThrowable -> {
          Platform.runLater(());
          return null;
        });
  }
  
  private void handleHttpResponse(HttpResponse<String> paramHttpResponse) {
    Platform.runLater(() -> {
          try {
            LogHelper.info("HTTP Status: " + paramHttpResponse.statusCode());
            LogHelper.info("Response Body: " + (String)paramHttpResponse.body());
            if (paramHttpResponse.statusCode() >= 200 && paramHttpResponse.statusCode() < 300) {
              String str = paramHttpResponse.body();
              if (str.contains("\"success\":true")) {
                showSuccess();
                (new Thread(())).start();
              } else if (str.contains("\"success\":false")) {
                String str1 = extractErrorMessage(str);
                showError("Ошибка регистрации: " + str1);
                reset();
              } else {
                showError("Неверный формат ответа от сервера");
                reset();
              } 
            } else {
              showError("Ошибка сервера: " + paramHttpResponse.statusCode());
              reset();
            } 
          } catch (Exception exception) {
            LogHelper.error("Error processing response: " + exception.getMessage());
            showError("Ошибка обработки ответа");
            reset();
          } 
        });
  }
  
  private String escapeJson(String paramString) {
    return (paramString == null) ? "" : paramString.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n").replace("\r", "\\r").replace("\t", "\\t");
  }
  
  private String extractErrorMessage(String paramString) {
    String str = "\"error\"";
    int i = paramString.indexOf(str);
    if (i != -1) {
      int j = paramString.indexOf(":", i + str.length());
      int k = paramString.indexOf("\"", j);
      int m = paramString.indexOf("\"", k + 1);
      if (k != -1 && m > k)
        return paramString.substring(k + 1, m).replace("\\\"", "\""); 
    } 
    LogHelper.error("Error extracting error message from {}", new Object[] { paramString });
    return "Неизвестная ошибка";
  }
  
  private void showError(String paramString) {
    LogHelper.error(paramString);
    Platform.runLater(() -> this.application.messageManager.createNotification("Ошибка", paramString));
  }
  
  private void showSuccess() {
    LogHelper.info("Успешная регистрация! Входим...");
    Platform.runLater(() -> this.application.messageManager.createNotification("Успех", "Успешная регистрация! Входим..."));
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\pro\gravit\launcher\gui\scenes\internal\BrowserScene.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */