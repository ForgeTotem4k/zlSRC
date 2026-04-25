package pro.gravit.launcher.gui.scenes.login;

import javafx.util.StringConverter;
import pro.gravit.launcher.base.events.request.GetAvailabilityAuthRequestEvent;

class AuthAvailabilityStringConverter extends StringConverter<GetAvailabilityAuthRequestEvent.AuthAvailability> {
  public String toString(GetAvailabilityAuthRequestEvent.AuthAvailability paramAuthAvailability) {
    return (paramAuthAvailability == null) ? "null" : paramAuthAvailability.displayName;
  }
  
  public GetAvailabilityAuthRequestEvent.AuthAvailability fromString(String paramString) {
    return null;
  }
  
  static {
  
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\pro\gravit\launcher\gui\scenes\login\LoginScene$AuthAvailabilityStringConverter.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */