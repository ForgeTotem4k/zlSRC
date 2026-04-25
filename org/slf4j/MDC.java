package org.slf4j;

import java.io.Closeable;
import java.util.Deque;
import java.util.Map;
import org.slf4j.helpers.NOPMDCAdapter;
import org.slf4j.helpers.Reporter;
import org.slf4j.spi.MDCAdapter;
import org.slf4j.spi.SLF4JServiceProvider;

public class MDC {
  static final String NULL_MDCA_URL = "http://www.slf4j.org/codes.html#null_MDCA";
  
  private static final String MDC_APAPTER_CANNOT_BE_NULL_MESSAGE = "MDCAdapter cannot be null. See also http://www.slf4j.org/codes.html#null_MDCA";
  
  static final String NO_STATIC_MDC_BINDER_URL = "http://www.slf4j.org/codes.html#no_static_mdc_binder";
  
  static MDCAdapter MDC_ADAPTER;
  
  private static MDCAdapter getMDCAdapterGivenByProvider() {
    SLF4JServiceProvider sLF4JServiceProvider = LoggerFactory.getProvider();
    if (sLF4JServiceProvider != null) {
      MDCAdapter mDCAdapter = sLF4JServiceProvider.getMDCAdapter();
      emitTemporaryMDCAdapterWarningIfNeeded(sLF4JServiceProvider);
      return mDCAdapter;
    } 
    Reporter.error("Failed to find provider.");
    Reporter.error("Defaulting to no-operation MDCAdapter implementation.");
    return (MDCAdapter)new NOPMDCAdapter();
  }
  
  private static void emitTemporaryMDCAdapterWarningIfNeeded(SLF4JServiceProvider paramSLF4JServiceProvider) {
    boolean bool = paramSLF4JServiceProvider instanceof org.slf4j.helpers.SubstituteServiceProvider;
    if (bool) {
      Reporter.info("Temporary mdcAdapter given by SubstituteServiceProvider.");
      Reporter.info("This mdcAdapter will be replaced after backend initialization has completed.");
    } 
  }
  
  public static void put(String paramString1, String paramString2) throws IllegalArgumentException {
    if (paramString1 == null)
      throw new IllegalArgumentException("key parameter cannot be null"); 
    if (getMDCAdapter() == null)
      throw new IllegalStateException("MDCAdapter cannot be null. See also http://www.slf4j.org/codes.html#null_MDCA"); 
    getMDCAdapter().put(paramString1, paramString2);
  }
  
  public static MDCCloseable putCloseable(String paramString1, String paramString2) throws IllegalArgumentException {
    put(paramString1, paramString2);
    return new MDCCloseable(paramString1);
  }
  
  public static String get(String paramString) throws IllegalArgumentException {
    if (paramString == null)
      throw new IllegalArgumentException("key parameter cannot be null"); 
    if (getMDCAdapter() == null)
      throw new IllegalStateException("MDCAdapter cannot be null. See also http://www.slf4j.org/codes.html#null_MDCA"); 
    return getMDCAdapter().get(paramString);
  }
  
  public static void remove(String paramString) throws IllegalArgumentException {
    if (paramString == null)
      throw new IllegalArgumentException("key parameter cannot be null"); 
    if (getMDCAdapter() == null)
      throw new IllegalStateException("MDCAdapter cannot be null. See also http://www.slf4j.org/codes.html#null_MDCA"); 
    getMDCAdapter().remove(paramString);
  }
  
  public static void clear() {
    if (getMDCAdapter() == null)
      throw new IllegalStateException("MDCAdapter cannot be null. See also http://www.slf4j.org/codes.html#null_MDCA"); 
    getMDCAdapter().clear();
  }
  
  public static Map<String, String> getCopyOfContextMap() {
    if (getMDCAdapter() == null)
      throw new IllegalStateException("MDCAdapter cannot be null. See also http://www.slf4j.org/codes.html#null_MDCA"); 
    return getMDCAdapter().getCopyOfContextMap();
  }
  
  public static void setContextMap(Map<String, String> paramMap) {
    if (getMDCAdapter() == null)
      throw new IllegalStateException("MDCAdapter cannot be null. See also http://www.slf4j.org/codes.html#null_MDCA"); 
    getMDCAdapter().setContextMap(paramMap);
  }
  
  public static MDCAdapter getMDCAdapter() {
    if (MDC_ADAPTER == null)
      MDC_ADAPTER = getMDCAdapterGivenByProvider(); 
    return MDC_ADAPTER;
  }
  
  static void setMDCAdapter(MDCAdapter paramMDCAdapter) {
    if (paramMDCAdapter == null)
      throw new IllegalStateException("MDCAdapter cannot be null. See also http://www.slf4j.org/codes.html#null_MDCA"); 
    MDC_ADAPTER = paramMDCAdapter;
  }
  
  public static void pushByKey(String paramString1, String paramString2) {
    if (getMDCAdapter() == null)
      throw new IllegalStateException("MDCAdapter cannot be null. See also http://www.slf4j.org/codes.html#null_MDCA"); 
    getMDCAdapter().pushByKey(paramString1, paramString2);
  }
  
  public static String popByKey(String paramString) {
    if (getMDCAdapter() == null)
      throw new IllegalStateException("MDCAdapter cannot be null. See also http://www.slf4j.org/codes.html#null_MDCA"); 
    return getMDCAdapter().popByKey(paramString);
  }
  
  public Deque<String> getCopyOfDequeByKey(String paramString) {
    if (getMDCAdapter() == null)
      throw new IllegalStateException("MDCAdapter cannot be null. See also http://www.slf4j.org/codes.html#null_MDCA"); 
    return getMDCAdapter().getCopyOfDequeByKey(paramString);
  }
  
  public static class MDCCloseable implements Closeable {
    private final String key;
    
    private MDCCloseable(String param1String) {
      this.key = param1String;
    }
    
    public void close() {
      MDC.remove(this.key);
    }
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\org\slf4j\MDC.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */