package pro.gravit.launcher.base.modules;

import java.util.ArrayList;
import java.util.List;
import pro.gravit.launcher.core.LauncherTrustManager;
import pro.gravit.utils.Version;

public abstract class LauncherModule {
  protected final LauncherModuleInfo moduleInfo = new LauncherModuleInfo("UnknownModule");
  
  private final List<EventEntity<? extends Event>> eventList = new ArrayList<>(4);
  
  protected LauncherModulesManager modulesManager;
  
  protected ModulesConfigManager modulesConfigManager;
  
  protected InitStatus initStatus = InitStatus.CREATED;
  
  private LauncherModulesContext context;
  
  private LauncherTrustManager.CheckClassResult checkResult;
  
  protected LauncherModule() {}
  
  protected LauncherModule(LauncherModuleInfo paramLauncherModuleInfo) {}
  
  public final LauncherModuleInfo getModuleInfo() {
    return this.moduleInfo;
  }
  
  public InitStatus getInitStatus() {
    return this.initStatus;
  }
  
  public void setInitStatus(InitStatus paramInitStatus) {
    this.initStatus = paramInitStatus;
  }
  
  public final void setContext(LauncherModulesContext paramLauncherModulesContext) {
    if (this.context != null)
      throw new IllegalStateException("Module already set context"); 
    this.context = paramLauncherModulesContext;
    this.modulesManager = paramLauncherModulesContext.getModulesManager();
    this.modulesConfigManager = paramLauncherModulesContext.getModulesConfigManager();
    setInitStatus(InitStatus.PRE_INIT_WAIT);
  }
  
  public final LauncherTrustManager.CheckClassResultType getCheckStatus() {
    return (this.checkResult == null) ? null : this.checkResult.type;
  }
  
  public final LauncherTrustManager.CheckClassResult getCheckResult() {
    return (this.checkResult == null) ? null : new LauncherTrustManager.CheckClassResult(this.checkResult);
  }
  
  public final void setCheckResult(LauncherTrustManager.CheckClassResult paramCheckClassResult) {
    if (this.checkResult != null)
      throw new IllegalStateException("Module already set check result"); 
    this.checkResult = paramCheckClassResult;
  }
  
  protected final void requireModule(String paramString, Version paramVersion) {
    if (this.context == null)
      throw new IllegalStateException("requireModule must be used in init() phase"); 
    LauncherModule launcherModule = this.context.getModulesManager().getModule(paramString);
    requireModule(launcherModule, paramVersion, paramString);
  }
  
  protected final <T extends LauncherModule> T requireModule(Class<? extends T> paramClass, Version paramVersion) {
    if (this.context == null)
      throw new IllegalStateException("requireModule must be used in init() phase"); 
    LauncherModule launcherModule = (LauncherModule)this.context.getModulesManager().getModule((Class)paramClass);
    requireModule(launcherModule, paramVersion, paramClass.getName());
    return (T)launcherModule;
  }
  
  protected LauncherModulesContext getContext() {
    return this.context;
  }
  
  private void requireModule(LauncherModule paramLauncherModule, Version paramVersion, String paramString) {
    if (paramLauncherModule == null)
      throw new RuntimeException(String.format("Module %s required %s v%s or higher", new Object[] { this.moduleInfo.name, paramString, paramVersion.getVersionString() })); 
    if (paramLauncherModule.moduleInfo.version.isLowerThan(paramVersion))
      throw new RuntimeException(String.format("Module %s required %s v%s or higher (current version %s)", new Object[] { this.moduleInfo.name, paramString, paramVersion.getVersionString(), paramLauncherModule.moduleInfo.version.getVersionString() })); 
  }
  
  public void preInitAction() {}
  
  public final void preInit() {
    if (!this.initStatus.equals(InitStatus.PRE_INIT_WAIT))
      throw new IllegalStateException("PreInit not allowed in current state"); 
    this.initStatus = InitStatus.PRE_INIT;
    preInitAction();
    this.initStatus = InitStatus.INIT_WAIT;
  }
  
  public abstract void init(LauncherInitContext paramLauncherInitContext);
  
  protected <T extends Event> boolean registerEvent(EventHandler<T> paramEventHandler, Class<T> paramClass) {
    EventEntity<T> eventEntity = new EventEntity<>(paramEventHandler, paramClass);
    this.eventList.add(eventEntity);
    return true;
  }
  
  public final <T extends Event> void callEvent(T paramT) {
    Class<?> clazz = paramT.getClass();
    for (EventEntity<? extends Event> eventEntity : this.eventList) {
      if (eventEntity.clazz.isAssignableFrom(clazz)) {
        eventEntity.handler.event(paramT);
        if (paramT.isCancel())
          return; 
      } 
    } 
  }
  
  public enum InitStatus {
    CREATED(false),
    PRE_INIT_WAIT(true),
    PRE_INIT(false),
    INIT_WAIT(true),
    INIT(false),
    FINISH(true);
    
    private final boolean isAvailable;
    
    InitStatus(boolean param1Boolean) {
      this.isAvailable = param1Boolean;
    }
    
    public boolean isAvailable() {
      return this.isAvailable;
    }
  }
  
  private static final class EventEntity<T extends Event> {
    final Class<T> clazz;
    
    final LauncherModule.EventHandler<T> handler;
    
    private EventEntity(LauncherModule.EventHandler<T> param1EventHandler, Class<T> param1Class) {
      this.clazz = param1Class;
      this.handler = param1EventHandler;
    }
  }
  
  @FunctionalInterface
  public static interface EventHandler<T extends Event> {
    void event(T param1T);
    
    static {
    
    }
  }
  
  public static class Event {
    protected boolean cancel = false;
    
    public boolean isCancel() {
      return this.cancel;
    }
    
    public void cancel() {
      this.cancel = true;
    }
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\pro\gravit\launcher\base\modules\LauncherModule.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */