package oshi.hardware.platform.unix.freebsd;

import com.sun.jna.Memory;
import com.sun.jna.Pointer;
import com.sun.jna.platform.unix.LibCAPI;
import java.util.Locale;
import oshi.annotation.concurrent.ThreadSafe;
import oshi.hardware.common.AbstractSensors;
import oshi.jna.ByRef;
import oshi.jna.platform.unix.FreeBsdLibc;

@ThreadSafe
final class FreeBsdSensors extends AbstractSensors {
  public double queryCpuTemperature() {
    return queryKldloadCoretemp();
  }
  
  private static double queryKldloadCoretemp() {
    String str = "dev.cpu.%d.temperature";
    ByRef.CloseableSizeTByReference closeableSizeTByReference = new ByRef.CloseableSizeTByReference(FreeBsdLibc.INT_SIZE);
    try {
      byte b = 0;
      double d1 = 0.0D;
      Memory memory = new Memory(closeableSizeTByReference.longValue());
      try {
        while (0 == FreeBsdLibc.INSTANCE.sysctlbyname(String.format(Locale.ROOT, str, new Object[] { Integer.valueOf(b) }), (Pointer)memory, (LibCAPI.size_t.ByReference)closeableSizeTByReference, null, LibCAPI.size_t.ZERO)) {
          d1 += memory.getInt(0L) / 10.0D - 273.15D;
          b++;
        } 
        memory.close();
      } catch (Throwable throwable) {
        try {
          memory.close();
        } catch (Throwable throwable1) {
          throwable.addSuppressed(throwable1);
        } 
        throw throwable;
      } 
      double d2 = (b > 0) ? (d1 / b) : Double.NaN;
      closeableSizeTByReference.close();
      return d2;
    } catch (Throwable throwable) {
      try {
        closeableSizeTByReference.close();
      } catch (Throwable throwable1) {
        throwable.addSuppressed(throwable1);
      } 
      throw throwable;
    } 
  }
  
  public int[] queryFanSpeeds() {
    return new int[0];
  }
  
  public double queryCpuVoltage() {
    return 0.0D;
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\oshi\hardware\platfor\\unix\freebsd\FreeBsdSensors.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */