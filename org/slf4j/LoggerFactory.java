package org.slf4j;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.net.URL;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.ServiceConfigurationError;
import java.util.ServiceLoader;
import java.util.Set;
import java.util.concurrent.LinkedBlockingQueue;
import org.slf4j.event.LoggingEvent;
import org.slf4j.event.SubstituteLoggingEvent;
import org.slf4j.helpers.NOP_FallbackServiceProvider;
import org.slf4j.helpers.Reporter;
import org.slf4j.helpers.SubstituteLogger;
import org.slf4j.helpers.SubstituteServiceProvider;
import org.slf4j.helpers.Util;
import org.slf4j.spi.MDCAdapter;
import org.slf4j.spi.SLF4JServiceProvider;

public final class LoggerFactory {
  static final String CODES_PREFIX = "https://www.slf4j.org/codes.html";
  
  static final String NO_PROVIDERS_URL = "https://www.slf4j.org/codes.html#noProviders";
  
  static final String IGNORED_BINDINGS_URL = "https://www.slf4j.org/codes.html#ignoredBindings";
  
  static final String MULTIPLE_BINDINGS_URL = "https://www.slf4j.org/codes.html#multiple_bindings";
  
  static final String VERSION_MISMATCH = "https://www.slf4j.org/codes.html#version_mismatch";
  
  static final String SUBSTITUTE_LOGGER_URL = "https://www.slf4j.org/codes.html#substituteLogger";
  
  static final String LOGGER_NAME_MISMATCH_URL = "https://www.slf4j.org/codes.html#loggerNameMismatch";
  
  static final String REPLAY_URL = "https://www.slf4j.org/codes.html#replay";
  
  static final String UNSUCCESSFUL_INIT_URL = "https://www.slf4j.org/codes.html#unsuccessfulInit";
  
  static final String UNSUCCESSFUL_INIT_MSG = "org.slf4j.LoggerFactory in failed state. Original exception was thrown EARLIER. See also https://www.slf4j.org/codes.html#unsuccessfulInit";
  
  static final String CONNECTED_WITH_MSG = "Connected with provider of type [";
  
  public static final String PROVIDER_PROPERTY_KEY = "slf4j.provider";
  
  static final int UNINITIALIZED = 0;
  
  static final int ONGOING_INITIALIZATION = 1;
  
  static final int FAILED_INITIALIZATION = 2;
  
  static final int SUCCESSFUL_INITIALIZATION = 3;
  
  static final int NOP_FALLBACK_INITIALIZATION = 4;
  
  static volatile int INITIALIZATION_STATE = 0;
  
  static final SubstituteServiceProvider SUBST_PROVIDER = new SubstituteServiceProvider();
  
  static final NOP_FallbackServiceProvider NOP_FALLBACK_SERVICE_PROVIDER = new NOP_FallbackServiceProvider();
  
  static final String DETECT_LOGGER_NAME_MISMATCH_PROPERTY = "slf4j.detectLoggerNameMismatch";
  
  static final String JAVA_VENDOR_PROPERTY = "java.vendor.url";
  
  static boolean DETECT_LOGGER_NAME_MISMATCH = Util.safeGetBooleanSystemProperty("slf4j.detectLoggerNameMismatch");
  
  static volatile SLF4JServiceProvider PROVIDER;
  
  private static final String[] API_COMPATIBILITY_LIST = new String[] { "2.0" };
  
  private static final String STATIC_LOGGER_BINDER_PATH = "org/slf4j/impl/StaticLoggerBinder.class";
  
  static List<SLF4JServiceProvider> findServiceProviders() {
    ArrayList<SLF4JServiceProvider> arrayList = new ArrayList();
    ClassLoader classLoader = LoggerFactory.class.getClassLoader();
    SLF4JServiceProvider sLF4JServiceProvider = loadExplicitlySpecified(classLoader);
    if (sLF4JServiceProvider != null) {
      arrayList.add(sLF4JServiceProvider);
      return arrayList;
    } 
    ServiceLoader<SLF4JServiceProvider> serviceLoader = getServiceLoader(classLoader);
    Iterator<SLF4JServiceProvider> iterator = serviceLoader.iterator();
    while (iterator.hasNext())
      safelyInstantiate(arrayList, iterator); 
    return arrayList;
  }
  
  private static ServiceLoader<SLF4JServiceProvider> getServiceLoader(ClassLoader paramClassLoader) {
    ServiceLoader<SLF4JServiceProvider> serviceLoader;
    SecurityManager securityManager = System.getSecurityManager();
    if (securityManager == null) {
      serviceLoader = ServiceLoader.load(SLF4JServiceProvider.class, paramClassLoader);
    } else {
      PrivilegedAction<ServiceLoader> privilegedAction = () -> ServiceLoader.load(SLF4JServiceProvider.class, paramClassLoader);
      serviceLoader = AccessController.<ServiceLoader>doPrivileged(privilegedAction);
    } 
    return serviceLoader;
  }
  
  private static void safelyInstantiate(List<SLF4JServiceProvider> paramList, Iterator<SLF4JServiceProvider> paramIterator) {
    try {
      SLF4JServiceProvider sLF4JServiceProvider = paramIterator.next();
      paramList.add(sLF4JServiceProvider);
    } catch (ServiceConfigurationError serviceConfigurationError) {
      Reporter.error("A service provider failed to instantiate:\n" + serviceConfigurationError.getMessage());
    } 
  }
  
  static void reset() {
    INITIALIZATION_STATE = 0;
  }
  
  private static final void performInitialization() {
    bind();
    if (INITIALIZATION_STATE == 3)
      versionSanityCheck(); 
  }
  
  private static final void bind() {
    try {
      List<SLF4JServiceProvider> list = findServiceProviders();
      reportMultipleBindingAmbiguity(list);
      if (list != null && !list.isEmpty()) {
        PROVIDER = list.get(0);
        earlyBindMDCAdapter();
        PROVIDER.initialize();
        INITIALIZATION_STATE = 3;
        reportActualBinding(list);
      } else {
        INITIALIZATION_STATE = 4;
        Reporter.warn("No SLF4J providers were found.");
        Reporter.warn("Defaulting to no-operation (NOP) logger implementation");
        Reporter.warn("See https://www.slf4j.org/codes.html#noProviders for further details.");
        Set<URL> set = findPossibleStaticLoggerBinderPathSet();
        reportIgnoredStaticLoggerBinders(set);
      } 
      postBindCleanUp();
    } catch (Exception exception) {
      failedBinding(exception);
      throw new IllegalStateException("Unexpected initialization failure", exception);
    } 
  }
  
  private static void earlyBindMDCAdapter() {
    MDCAdapter mDCAdapter = PROVIDER.getMDCAdapter();
    if (mDCAdapter != null)
      MDC.setMDCAdapter(mDCAdapter); 
  }
  
  static SLF4JServiceProvider loadExplicitlySpecified(ClassLoader paramClassLoader) {
    String str = System.getProperty("slf4j.provider");
    if (null == str || str.isEmpty())
      return null; 
    try {
      String str1 = String.format("Attempting to load provider \"%s\" specified via \"%s\" system property", new Object[] { str, "slf4j.provider" });
      Reporter.info(str1);
      Class<?> clazz = paramClassLoader.loadClass(str);
      Constructor<?> constructor = clazz.getConstructor(new Class[0]);
      Object object = constructor.newInstance(new Object[0]);
      return (SLF4JServiceProvider)object;
    } catch (ClassNotFoundException|NoSuchMethodException|InstantiationException|IllegalAccessException|java.lang.reflect.InvocationTargetException classNotFoundException) {
      String str1 = String.format("Failed to instantiate the specified SLF4JServiceProvider (%s)", new Object[] { str });
      Reporter.error(str1, classNotFoundException);
      return null;
    } catch (ClassCastException classCastException) {
      String str1 = String.format("Specified SLF4JServiceProvider (%s) does not implement SLF4JServiceProvider interface", new Object[] { str });
      Reporter.error(str1, classCastException);
      return null;
    } 
  }
  
  private static void reportIgnoredStaticLoggerBinders(Set<URL> paramSet) {
    if (paramSet.isEmpty())
      return; 
    Reporter.warn("Class path contains SLF4J bindings targeting slf4j-api versions 1.7.x or earlier.");
    for (URL uRL : paramSet)
      Reporter.warn("Ignoring binding found at [" + uRL + "]"); 
    Reporter.warn("See https://www.slf4j.org/codes.html#ignoredBindings for an explanation.");
  }
  
  static Set<URL> findPossibleStaticLoggerBinderPathSet() {
    LinkedHashSet<URL> linkedHashSet = new LinkedHashSet();
    try {
      Enumeration<URL> enumeration;
      ClassLoader classLoader = LoggerFactory.class.getClassLoader();
      if (classLoader == null) {
        enumeration = ClassLoader.getSystemResources("org/slf4j/impl/StaticLoggerBinder.class");
      } else {
        enumeration = classLoader.getResources("org/slf4j/impl/StaticLoggerBinder.class");
      } 
      while (enumeration.hasMoreElements()) {
        URL uRL = enumeration.nextElement();
        linkedHashSet.add(uRL);
      } 
    } catch (IOException iOException) {
      Reporter.error("Error getting resources from path", iOException);
    } 
    return linkedHashSet;
  }
  
  private static void postBindCleanUp() {
    fixSubstituteLoggers();
    replayEvents();
    SUBST_PROVIDER.getSubstituteLoggerFactory().clear();
  }
  
  private static void fixSubstituteLoggers() {
    synchronized (SUBST_PROVIDER) {
      SUBST_PROVIDER.getSubstituteLoggerFactory().postInitialization();
      for (SubstituteLogger substituteLogger : SUBST_PROVIDER.getSubstituteLoggerFactory().getLoggers()) {
        Logger logger = getLogger(substituteLogger.getName());
        substituteLogger.setDelegate(logger);
      } 
    } 
  }
  
  static void failedBinding(Throwable paramThrowable) {
    INITIALIZATION_STATE = 2;
    Reporter.error("Failed to instantiate SLF4J LoggerFactory", paramThrowable);
  }
  
  private static void replayEvents() {
    LinkedBlockingQueue linkedBlockingQueue = SUBST_PROVIDER.getSubstituteLoggerFactory().getEventQueue();
    int i = linkedBlockingQueue.size();
    byte b = 0;
    char c = '';
    ArrayList arrayList = new ArrayList(128);
    while (true) {
      int j = linkedBlockingQueue.drainTo(arrayList, 128);
      if (j == 0)
        break; 
      for (SubstituteLoggingEvent substituteLoggingEvent : arrayList) {
        replaySingleEvent(substituteLoggingEvent);
        if (b++ == 0)
          emitReplayOrSubstituionWarning(substituteLoggingEvent, i); 
      } 
      arrayList.clear();
    } 
  }
  
  private static void emitReplayOrSubstituionWarning(SubstituteLoggingEvent paramSubstituteLoggingEvent, int paramInt) {
    if (paramSubstituteLoggingEvent.getLogger().isDelegateEventAware()) {
      emitReplayWarning(paramInt);
    } else if (!paramSubstituteLoggingEvent.getLogger().isDelegateNOP()) {
      emitSubstitutionWarning();
    } 
  }
  
  private static void replaySingleEvent(SubstituteLoggingEvent paramSubstituteLoggingEvent) {
    if (paramSubstituteLoggingEvent == null)
      return; 
    SubstituteLogger substituteLogger = paramSubstituteLoggingEvent.getLogger();
    String str = substituteLogger.getName();
    if (substituteLogger.isDelegateNull())
      throw new IllegalStateException("Delegate logger cannot be null at this state."); 
    if (!substituteLogger.isDelegateNOP())
      if (substituteLogger.isDelegateEventAware()) {
        if (substituteLogger.isEnabledForLevel(paramSubstituteLoggingEvent.getLevel()))
          substituteLogger.log((LoggingEvent)paramSubstituteLoggingEvent); 
      } else {
        Reporter.warn(str);
      }  
  }
  
  private static void emitSubstitutionWarning() {
    Reporter.warn("The following set of substitute loggers may have been accessed");
    Reporter.warn("during the initialization phase. Logging calls during this");
    Reporter.warn("phase were not honored. However, subsequent logging calls to these");
    Reporter.warn("loggers will work as normally expected.");
    Reporter.warn("See also https://www.slf4j.org/codes.html#substituteLogger");
  }
  
  private static void emitReplayWarning(int paramInt) {
    Reporter.warn("A number (" + paramInt + ") of logging calls during the initialization phase have been intercepted and are");
    Reporter.warn("now being replayed. These are subject to the filtering rules of the underlying logging system.");
    Reporter.warn("See also https://www.slf4j.org/codes.html#replay");
  }
  
  private static final void versionSanityCheck() {
    try {
      String str = PROVIDER.getRequestedApiVersion();
      boolean bool = false;
      for (String str1 : API_COMPATIBILITY_LIST) {
        if (str.startsWith(str1))
          bool = true; 
      } 
      if (!bool) {
        Reporter.warn("The requested version " + str + " by your slf4j provider is not compatible with " + Arrays.<String>asList(API_COMPATIBILITY_LIST).toString());
        Reporter.warn("See https://www.slf4j.org/codes.html#version_mismatch for further details.");
      } 
    } catch (Throwable throwable) {
      Reporter.error("Unexpected problem occurred during version sanity check", throwable);
    } 
  }
  
  private static boolean isAmbiguousProviderList(List<SLF4JServiceProvider> paramList) {
    return (paramList.size() > 1);
  }
  
  private static void reportMultipleBindingAmbiguity(List<SLF4JServiceProvider> paramList) {
    if (isAmbiguousProviderList(paramList)) {
      Reporter.warn("Class path contains multiple SLF4J providers.");
      for (SLF4JServiceProvider sLF4JServiceProvider : paramList)
        Reporter.warn("Found provider [" + sLF4JServiceProvider + "]"); 
      Reporter.warn("See https://www.slf4j.org/codes.html#multiple_bindings for an explanation.");
    } 
  }
  
  private static void reportActualBinding(List<SLF4JServiceProvider> paramList) {
    if (paramList.isEmpty())
      throw new IllegalStateException("No providers were found which is impossible after successful initialization."); 
    if (isAmbiguousProviderList(paramList)) {
      Reporter.info("Actual provider is of type [" + paramList.get(0) + "]");
    } else {
      SLF4JServiceProvider sLF4JServiceProvider = paramList.get(0);
      Reporter.debug("Connected with provider of type [" + sLF4JServiceProvider.getClass().getName() + "]");
    } 
  }
  
  public static Logger getLogger(String paramString) {
    ILoggerFactory iLoggerFactory = getILoggerFactory();
    return iLoggerFactory.getLogger(paramString);
  }
  
  public static Logger getLogger(Class<?> paramClass) {
    Logger logger = getLogger(paramClass.getName());
    if (DETECT_LOGGER_NAME_MISMATCH) {
      Class<?> clazz = Util.getCallingClass();
      if (clazz != null && nonMatchingClasses(paramClass, clazz)) {
        Reporter.warn(String.format("Detected logger name mismatch. Given name: \"%s\"; computed name: \"%s\".", new Object[] { logger.getName(), clazz.getName() }));
        Reporter.warn("See https://www.slf4j.org/codes.html#loggerNameMismatch for an explanation");
      } 
    } 
    return logger;
  }
  
  private static boolean nonMatchingClasses(Class<?> paramClass1, Class<?> paramClass2) {
    return !paramClass2.isAssignableFrom(paramClass1);
  }
  
  public static ILoggerFactory getILoggerFactory() {
    return getProvider().getLoggerFactory();
  }
  
  static SLF4JServiceProvider getProvider() {
    if (INITIALIZATION_STATE == 0)
      synchronized (LoggerFactory.class) {
        if (INITIALIZATION_STATE == 0) {
          INITIALIZATION_STATE = 1;
          performInitialization();
        } 
      }  
    switch (INITIALIZATION_STATE) {
      case 3:
        return PROVIDER;
      case 4:
        return (SLF4JServiceProvider)NOP_FALLBACK_SERVICE_PROVIDER;
      case 2:
        throw new IllegalStateException("org.slf4j.LoggerFactory in failed state. Original exception was thrown EARLIER. See also https://www.slf4j.org/codes.html#unsuccessfulInit");
      case 1:
        return (SLF4JServiceProvider)SUBST_PROVIDER;
    } 
    throw new IllegalStateException("Unreachable code");
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\org\slf4j\LoggerFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */