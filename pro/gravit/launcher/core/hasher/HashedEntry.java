package pro.gravit.launcher.core.hasher;

import java.io.IOException;
import pro.gravit.launcher.core.LauncherNetworkAPI;
import pro.gravit.launcher.core.serialize.HInput;
import pro.gravit.launcher.core.serialize.stream.EnumSerializer;
import pro.gravit.launcher.core.serialize.stream.StreamObject;

public abstract class HashedEntry extends StreamObject {
  @LauncherNetworkAPI
  public boolean flag;
  
  public abstract Type getType();
  
  public abstract long size();
  
  public enum Type implements EnumSerializer.Itf {
    DIR(1),
    FILE(2);
    
    private static final EnumSerializer<Type> SERIALIZER = new EnumSerializer(Type.class);
    
    private final int n;
    
    Type(int param1Int1) {
      this.n = param1Int1;
    }
    
    public static Type read(HInput param1HInput) throws IOException {
      return (Type)SERIALIZER.read(param1HInput);
    }
    
    public int getNumber() {
      return this.n;
    }
    
    static {
    
    }
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\pro\gravit\launcher\core\hasher\HashedEntry.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */