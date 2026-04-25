package pro.gravit.launcher.base.request.auth;

import pro.gravit.launcher.base.events.request.GetAvailabilityAuthRequestEvent;
import pro.gravit.launcher.base.request.Request;
import pro.gravit.launcher.base.request.auth.details.AuthLoginOnlyDetails;
import pro.gravit.launcher.base.request.auth.details.AuthPasswordDetails;
import pro.gravit.launcher.base.request.auth.details.AuthTotpDetails;
import pro.gravit.launcher.base.request.auth.details.AuthWebViewDetails;
import pro.gravit.launcher.base.request.websockets.WebSocketRequest;
import pro.gravit.utils.ProviderMap;

public class GetAvailabilityAuthRequest extends Request<GetAvailabilityAuthRequestEvent> implements WebSocketRequest {
  public static final ProviderMap<GetAvailabilityAuthRequestEvent.AuthAvailabilityDetails> providers = new ProviderMap();
  
  private static boolean registeredProviders = false;
  
  public static void registerProviders() {
    if (!registeredProviders) {
      providers.register("password", AuthPasswordDetails.class);
      providers.register("webview", AuthWebViewDetails.class);
      providers.register("totp", AuthTotpDetails.class);
      providers.register("loginonly", AuthLoginOnlyDetails.class);
      registeredProviders = true;
    } 
  }
  
  public String getType() {
    return "getAvailabilityAuth";
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\pro\gravit\launcher\base\request\auth\GetAvailabilityAuthRequest.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */