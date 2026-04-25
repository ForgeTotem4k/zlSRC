package pro.gravit.launcher.gui;

import java.lang.reflect.Method;
import java.util.Base64;
import javafx.stage.Stage;
import javax.swing.JOptionPane;
import pro.gravit.launcher.base.modules.LauncherInitContext;
import pro.gravit.launcher.base.modules.LauncherModule;
import pro.gravit.launcher.base.modules.LauncherModuleInfo;
import pro.gravit.launcher.base.modules.events.OfflineModeEvent;
import pro.gravit.launcher.base.request.websockets.OfflineRequestService;
import pro.gravit.launcher.client.events.ClientExitPhase;
import pro.gravit.launcher.client.events.ClientUnlockConsoleEvent;
import pro.gravit.launcher.gui.service.OfflineService;
import pro.gravit.launcher.runtime.LauncherEngine;
import pro.gravit.launcher.runtime.client.events.ClientEngineInitPhase;
import pro.gravit.launcher.runtime.client.events.ClientPreGuiPhase;
import pro.gravit.launcher.runtime.gui.RuntimeProvider;
import pro.gravit.utils.Version;
import pro.gravit.utils.helper.JVMHelper;
import pro.gravit.utils.helper.LogHelper;

public class JavaRuntimeModule extends LauncherModule {
  public static final String RUNTIME_NAME = "stdruntime";
  
  static LauncherEngine engine;
  
  private RuntimeProvider provider;
  
  public JavaRuntimeModule() {
    super(new LauncherModuleInfo("StdJavaRuntime", new Version(4, 0, 7, 1, Version.Type.STABLE), 0, new String[0], new String[] { "runtime" }));
  }
  
  private static void noJavaFxAlert() {
    String str = "Библиотеки JavaFX не найдены. У вас %s(x%d) ОС %s(x%d). Java %s. Установите Java с поддержкой JavaFX\nЕсли вы не можете решить проблему самостоятельно обратитесь к администрации своего проекта\n".formatted(new Object[] { JVMHelper.RUNTIME_MXBEAN.getVmName(), Integer.valueOf(JVMHelper.JVM_BITS), JVMHelper.OS_TYPE.name, Integer.valueOf(JVMHelper.OS_BITS), JVMHelper.RUNTIME_MXBEAN.getSpecVersion() });
    JOptionPane.showMessageDialog(null, str, "GravitLauncher", 0);
  }
  
  private static void noInitMethodAlert() {
    String str = "JavaFX приложение собрано некорректно. Обратитесь к администратору проекта с скриншотом этого окна\nОписание:\nПри сборке отсутствовали библиотеки JavaFX. Пожалуйста установите Java с поддержкой JavaFX на стороне лаунчсервера и повторите сборку лаунчера\n";
    JOptionPane.showMessageDialog(null, str, "GravitLauncher", 0);
  }
  
  public static void noLocaleAlert(String paramString) {
    String str = "Не найден файл языка '%s' при инициализации GUI. Дальнейшая работа невозможна.\nУбедитесь что все файлы дизайна лаунчера присутствуют в папке runtime при сборке лаунчера\n".formatted(new Object[] { paramString });
    JOptionPane.showMessageDialog(null, str, "GravitLauncher", 0);
  }
  
  public static void noEnFSAlert() {
    String str = "Запуск лаунчера невозможен из-за ошибки расшифровки рантайма.\nАдминистраторам: установите библиотеку EnFS для исправления этой проблемы\n";
    JOptionPane.showMessageDialog(null, str, "GravitLauncher", 0);
  }
  
  public static void errorHandleAlert(Throwable paramThrowable) {
    String str = "Произошла серьезная ошибка при инициализации интерфейса лаунчера.\nДля пользователей:\nОбратитесь к администрации своего проекта с скриншотом этого окна\nJava %d (x%d) Ошибка %s\nОписание: %s\nБолее подробную информацию можно получить из лога\n".formatted(new Object[] { Integer.valueOf(JVMHelper.JVM_VERSION), Integer.valueOf(JVMHelper.JVM_BITS), paramThrowable.getClass().getName(), (paramThrowable.getMessage() == null) ? "null" : paramThrowable.getMessage() });
    JOptionPane.showMessageDialog(null, str, "GravitLauncher", 0);
  }
  
  public static String getLauncherInfo() {
    return "Launcher %s | Java %d(%s %s) x%d | %s x%d".formatted(new Object[] { Version.getVersion().toString(), Integer.valueOf(JVMHelper.JVM_VERSION), JVMHelper.RUNTIME_MXBEAN.getVmName(), System.getProperty("java.version"), Integer.valueOf(JVMHelper.JVM_BITS), JVMHelper.OS_TYPE.name(), Integer.valueOf(JVMHelper.OS_BITS) });
  }
  
  public static String getMiniLauncherInfo() {
    return "Launcher %s | Java %d(%s) x%d | %s x%d".formatted(new Object[] { Version.getVersion().toString(), Integer.valueOf(JVMHelper.JVM_VERSION), System.getProperty("java.version"), Integer.valueOf(JVMHelper.JVM_BITS), JVMHelper.OS_TYPE.name(), Integer.valueOf(JVMHelper.OS_BITS) });
  }
  
  public void init(LauncherInitContext paramLauncherInitContext) {
    registerEvent(this::preGuiPhase, ClientPreGuiPhase.class);
    registerEvent(this::engineInitPhase, ClientEngineInitPhase.class);
    registerEvent(this::exitPhase, ClientExitPhase.class);
    registerEvent(this::consoleUnlock, ClientUnlockConsoleEvent.class);
    registerEvent(this::offlineMode, OfflineModeEvent.class);
  }
  
  private void preGuiPhase(ClientPreGuiPhase paramClientPreGuiPhase) {
    try {
      Class.forName("javafx.application.Application");
    } catch (ClassNotFoundException classNotFoundException) {
      noJavaFxAlert();
      LauncherEngine.exitLauncher(0);
    } 
    try {
      Method method = JavaFXApplication.class.getMethod(new String(Base64.getDecoder().decode("c3RhcnQ=")), new Class[] { Stage.class });
      if (method.getDeclaringClass() != JavaFXApplication.class)
        throw new RuntimeException("Method start not override"); 
    } catch (Throwable throwable) {
      LogHelper.error(throwable);
      noInitMethodAlert();
      LauncherEngine.exitLauncher(0);
    } 
    this.provider = new StdJavaRuntimeProvider();
    paramClientPreGuiPhase.runtimeProvider = this.provider;
  }
  
  private void consoleUnlock(ClientUnlockConsoleEvent paramClientUnlockConsoleEvent) {
    RuntimeProvider runtimeProvider = engine.runtimeProvider;
    if (runtimeProvider instanceof StdJavaRuntimeProvider) {
      StdJavaRuntimeProvider stdJavaRuntimeProvider = (StdJavaRuntimeProvider)runtimeProvider;
      stdJavaRuntimeProvider.registerPrivateCommands();
    } 
  }
  
  private void offlineMode(OfflineModeEvent paramOfflineModeEvent) {
    OfflineService.applyRuntimeProcessors((OfflineRequestService)paramOfflineModeEvent.service);
  }
  
  private void engineInitPhase(ClientEngineInitPhase paramClientEngineInitPhase) {
    engine = paramClientEngineInitPhase.engine;
  }
  
  private void exitPhase(ClientExitPhase paramClientExitPhase) {
    if (this.provider != null) {
      RuntimeProvider runtimeProvider = this.provider;
      if (runtimeProvider instanceof StdJavaRuntimeProvider) {
        StdJavaRuntimeProvider stdJavaRuntimeProvider = (StdJavaRuntimeProvider)runtimeProvider;
        try {
          stdJavaRuntimeProvider.getApplication().saveSettings();
        } catch (Throwable throwable) {
          LogHelper.error(throwable);
        } 
      } 
    } 
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\pro\gravit\launcher\gui\JavaRuntimeModule.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */