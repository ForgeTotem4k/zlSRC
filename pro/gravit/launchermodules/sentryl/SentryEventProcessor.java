package pro.gravit.launchermodules.sentryl;

import io.sentry.EventProcessor;
import io.sentry.Hint;
import io.sentry.SentryEvent;
import java.util.Arrays;
import pro.gravit.launchermodules.sentryl.utils.OshiUtils;
import pro.gravit.utils.helper.JVMHelper;

public class SentryEventProcessor implements EventProcessor {
  public SentryEvent process(SentryEvent paramSentryEvent, Hint paramHint) {
    if (paramSentryEvent.getThrowable() != null && SentryModule.config.ignoreErrors != null && SentryModule.config.ignoreErrors.contains(paramSentryEvent.getThrowable().getMessage()))
      return null; 
    if (paramSentryEvent.getThrowable() instanceof pro.gravit.launcher.base.request.RequestException)
      paramSentryEvent.setFingerprints(Arrays.asList(new String[] { "RequestException", paramSentryEvent.getThrowable().getMessage() })); 
    if (SentryModule.config.collectMemoryInfo)
      paramSentryEvent.getContexts().put("Memory info", OshiUtils.makeMemoryProperties()); 
    long l = JVMHelper.RUNTIME_MXBEAN.getUptime();
    paramSentryEvent.getContexts().put("Uptime", String.format("%ds %dms", new Object[] { Long.valueOf(l / 1000L), Long.valueOf(l % 1000L) }));
    return paramSentryEvent;
  }
  
  static {
  
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\pro\gravit\launchermodules\sentryl\SentryEventProcessor.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */