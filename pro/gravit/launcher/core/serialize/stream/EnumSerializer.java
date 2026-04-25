package pro.gravit.launcher.core.serialize.stream;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import pro.gravit.launcher.core.serialize.HInput;
import pro.gravit.launcher.core.serialize.HOutput;
import pro.gravit.utils.helper.VerifyHelper;

public final class EnumSerializer<E extends Enum<?> & EnumSerializer.Itf> {
  private final Map<Integer, E> map = new HashMap<>(16);
  
  public EnumSerializer(Class<E> paramClass) {
    for (Enum enum_ : (Enum[])paramClass.getEnumConstants())
      VerifyHelper.putIfAbsent(this.map, Integer.valueOf(((Itf)enum_).getNumber()), enum_, "Duplicate number for enum constant " + enum_.name()); 
  }
  
  public static void write(HOutput paramHOutput, Itf paramItf) throws IOException {
    paramHOutput.writeVarInt(paramItf.getNumber());
  }
  
  public E read(HInput paramHInput) throws IOException {
    int i = paramHInput.readVarInt();
    return (E)VerifyHelper.getMapValue(this.map, Integer.valueOf(i), "Unknown enum number: " + i);
  }
  
  @FunctionalInterface
  public static interface Itf {
    int getNumber();
    
    static {
    
    }
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\pro\gravit\launcher\core\serialize\stream\EnumSerializer.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */