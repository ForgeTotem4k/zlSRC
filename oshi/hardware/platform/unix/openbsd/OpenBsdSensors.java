package oshi.hardware.platform.unix.openbsd;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import oshi.annotation.concurrent.ThreadSafe;
import oshi.hardware.common.AbstractSensors;
import oshi.util.ExecutingCommand;
import oshi.util.Memoizer;
import oshi.util.ParseUtil;
import oshi.util.tuples.Triplet;

@ThreadSafe
final class OpenBsdSensors extends AbstractSensors {
  private final Supplier<Triplet<Double, int[], Double>> tempFanVolts = Memoizer.memoize(OpenBsdSensors::querySensors, Memoizer.defaultExpiration());
  
  public double queryCpuTemperature() {
    return ((Double)((Triplet)this.tempFanVolts.get()).getA()).doubleValue();
  }
  
  public int[] queryFanSpeeds() {
    return (int[])((Triplet)this.tempFanVolts.get()).getB();
  }
  
  public double queryCpuVoltage() {
    return ((Double)((Triplet)this.tempFanVolts.get()).getC()).doubleValue();
  }
  
  private static Triplet<Double, int[], Double> querySensors() {
    double d1 = 0.0D;
    ArrayList<Double> arrayList1 = new ArrayList();
    ArrayList<Double> arrayList2 = new ArrayList();
    ArrayList<Integer> arrayList = new ArrayList();
    for (String str : ExecutingCommand.runNative("systat -ab sensors")) {
      String[] arrayOfString = ParseUtil.whitespaces.split(str);
      if (arrayOfString.length > 1) {
        if (arrayOfString[0].contains("cpu")) {
          if (arrayOfString[0].contains("temp0")) {
            arrayList1.add(Double.valueOf(ParseUtil.parseDoubleOrDefault(arrayOfString[1], Double.NaN)));
            continue;
          } 
          if (arrayOfString[0].contains("volt0"))
            d1 = ParseUtil.parseDoubleOrDefault(arrayOfString[1], 0.0D); 
          continue;
        } 
        if (arrayOfString[0].contains("temp0")) {
          arrayList2.add(Double.valueOf(ParseUtil.parseDoubleOrDefault(arrayOfString[1], Double.NaN)));
          continue;
        } 
        if (arrayOfString[0].contains("fan"))
          arrayList.add(Integer.valueOf(ParseUtil.parseIntOrDefault(arrayOfString[1], 0))); 
      } 
    } 
    double d2 = arrayList1.isEmpty() ? listAverage(arrayList2) : listAverage(arrayList1);
    int[] arrayOfInt = new int[arrayList.size()];
    for (byte b = 0; b < arrayOfInt.length; b++)
      arrayOfInt[b] = ((Integer)arrayList.get(b)).intValue(); 
    return new Triplet(Double.valueOf(d2), arrayOfInt, Double.valueOf(d1));
  }
  
  private static double listAverage(List<Double> paramList) {
    double d = 0.0D;
    byte b = 0;
    for (Double double_ : paramList) {
      if (!double_.isNaN()) {
        d += double_.doubleValue();
        b++;
      } 
    } 
    return (b > 0) ? (d / b) : 0.0D;
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\oshi\hardware\platfor\\unix\openbsd\OpenBsdSensors.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */