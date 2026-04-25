package pro.gravit.launchermodules.sentryl;

import io.sentry.IScope;
import io.sentry.IScopes;
import io.sentry.Scope;
import io.sentry.Sentry;
import io.sentry.SentryOptions;
import io.sentry.protocol.User;
import java.util.HashMap;
import pro.gravit.launcher.base.Launcher;
import pro.gravit.launcher.base.api.AuthService;
import pro.gravit.launcher.base.modules.LauncherInitContext;
import pro.gravit.launcher.base.modules.LauncherModule;
import pro.gravit.launcher.base.modules.LauncherModuleInfo;
import pro.gravit.launcher.base.profiles.ClientProfile;
import pro.gravit.launcher.base.profiles.PlayerProfile;
import pro.gravit.launcher.base.request.Request;
import pro.gravit.launcher.client.events.ClientProcessInitPhase;
import pro.gravit.launcher.client.events.ClientProcessPreInvokeMainClassEvent;
import pro.gravit.launcher.runtime.LauncherEngine;
import pro.gravit.launcher.runtime.client.events.ClientEngineInitPhase;
import pro.gravit.launchermodules.sentryl.utils.BasicProperties;
import pro.gravit.launchermodules.sentryl.utils.OshiUtils;
import pro.gravit.utils.Version;
import pro.gravit.utils.helper.LogHelper;

public class SentryModule extends LauncherModule {
  public static Config config = new Config();
  
  public static IScopes currentScopes;
  
  public static String proguardUuid;
  
  public SentryModule() {
    super(new LauncherModuleInfo("Sentry", Version.of(2, 0, 0), new String[] { "ClientLauncherCore" }));
  }
  
  public void init(LauncherInitContext paramLauncherInitContext) {
    if (config.dsn == null || "YOUR_DSN".equals(config.dsn)) {
      LogHelper.warning("Sentry module disabled. Please configure dsn");
      return;
    } 
    registerEvent(this::onInit, ClientEngineInitPhase.class);
    registerEvent(this::onClientInit, ClientProcessInitPhase.class);
    registerEvent(this::beforeStartClient, ClientProcessPreInvokeMainClassEvent.class);
  }
  
  public void onInit(ClientEngineInitPhase paramClientEngineInitPhase) {
    initSentry(paramClientEngineInitPhase.engine);
  }
  
  public void onClientInit(ClientProcessInitPhase paramClientProcessInitPhase) {
    initSentry((LauncherEngine)null);
  }
  
  public void initSentry(LauncherEngine paramLauncherEngine) {
    if (Sentry.isEnabled())
      return; 
    LogHelper.debug("Initialize Sentry");
    Sentry.init(paramSentryOptions -> {
          paramSentryOptions.addEventProcessor(new SentryEventProcessor());
          paramSentryOptions.setDsn(config.dsn);
          paramSentryOptions.setEnvironment((paramLauncherEngine == null) ? "CLIENT" : "LAUNCHER");
          paramSentryOptions.setRelease(Version.getVersion().getVersionString().concat(".").concat(String.valueOf((Launcher.getConfig()).buildNumber)));
          if (proguardUuid != null)
            paramSentryOptions.setProguardUuid(proguardUuid); 
        }true);
    currentScopes = Sentry.getCurrentScopes();
    Sentry.configureScope(paramIScope -> {
          BasicProperties.setupBasicProperties((Scope)paramIScope);
          OshiUtils.systemProperties((Scope)paramIScope);
        });
    LogHelper.addExcCallback(Sentry::captureException);
    if (Request.isAvailable())
      Request.getRequestService().registerEventHandler(new SentryEventHandler()); 
    LogHelper.debug("Sentry initialized");
  }
  
  public void beforeStartClient(ClientProcessPreInvokeMainClassEvent paramClientProcessPreInvokeMainClassEvent) {
    Sentry.configureScope(paramIScope -> {
          paramIScope.setUser(makeSentryUser(LauncherEngine.clientParams.playerProfile));
          ClientProfile clientProfile = AuthService.profile;
          paramIScope.setTag("minecraftVersion", clientProfile.getVersion().toString());
          HashMap<Object, Object> hashMap = new HashMap<>();
          hashMap.put("MinecraftVersion", clientProfile.getVersion().toString());
          hashMap.put("Name", clientProfile.getTitle());
          hashMap.put("UUID", clientProfile.getUUID().toString());
          paramIScope.setContexts("Profile", hashMap);
        });
  }
  
  protected static User makeSentryUser(PlayerProfile paramPlayerProfile) {
    User user = new User();
    user.setUsername(paramPlayerProfile.username);
    user.setId(paramPlayerProfile.uuid.toString());
    return user;
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\pro\gravit\launchermodules\sentryl\SentryModule.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */