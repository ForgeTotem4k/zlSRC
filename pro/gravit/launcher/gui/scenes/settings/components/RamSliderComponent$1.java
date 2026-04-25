package pro.gravit.launcher.gui.scenes.settings.components;

import javafx.util.StringConverter;

class null extends StringConverter<Double> {
  public String toString(Double paramDouble) {
    return "%.0fG".formatted(new Object[] { Double.valueOf(paramDouble.doubleValue() / 1024.0D) });
  }
  
  public Double fromString(String paramString) {
    return null;
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\pro\gravit\launcher\gui\scenes\settings\components\RamSliderComponent$1.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */