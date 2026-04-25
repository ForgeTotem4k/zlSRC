package pro.gravit.launcher.core.hasher;

import java.io.IOException;
import pro.gravit.launcher.core.serialize.HInput;
import pro.gravit.launcher.core.serialize.stream.EnumSerializer;

public enum Type implements EnumSerializer.Itf {
  DIR(1),
  FILE(2);
  
  private static final EnumSerializer<Type> SERIALIZER;
  
  private final int n;
  
  Type(int paramInt1) {
    this.n = paramInt1;
  }
  
  public static Type read(HInput paramHInput) throws IOException {
    return (Type)SERIALIZER.read(paramHInput);
  }
  
  public int getNumber() {
    return this.n;
  }
  
  static {
    SERIALIZER = new EnumSerializer(Type.class);
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\pro\gravit\launcher\core\hasher\HashedEntry$Type.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */