package oshi.hardware.platform.unix.solaris;

import com.sun.jna.platform.unix.solaris.LibKstat;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import oshi.annotation.concurrent.ThreadSafe;
import oshi.hardware.PowerSource;
import oshi.hardware.common.AbstractPowerSource;
import oshi.util.platform.unix.solaris.KstatUtil;

@ThreadSafe
public final class SolarisPowerSource extends AbstractPowerSource {
  private static final String[] KSTAT_BATT_MOD = new String[] { null, "battery", "acpi_drv" };
  
  private static final int KSTAT_BATT_IDX;
  
  public SolarisPowerSource(String paramString1, String paramString2, double paramDouble1, double paramDouble2, double paramDouble3, double paramDouble4, double paramDouble5, double paramDouble6, boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3, PowerSource.CapacityUnits paramCapacityUnits, int paramInt1, int paramInt2, int paramInt3, int paramInt4, String paramString3, LocalDate paramLocalDate, String paramString4, String paramString5, double paramDouble7) {
    super(paramString1, paramString2, paramDouble1, paramDouble2, paramDouble3, paramDouble4, paramDouble5, paramDouble6, paramBoolean1, paramBoolean2, paramBoolean3, paramCapacityUnits, paramInt1, paramInt2, paramInt3, paramInt4, paramString3, paramLocalDate, paramString4, paramString5, paramDouble7);
  }
  
  public static List<PowerSource> getPowerSources() {
    return Arrays.asList(new PowerSource[] { (PowerSource)getPowerSource("BAT0") });
  }
  
  private static SolarisPowerSource getPowerSource(String paramString) {
    String str1 = paramString;
    String str2 = "unknown";
    double d1 = 1.0D;
    double d2 = -1.0D;
    double d3 = 0.0D;
    double d4 = 0.0D;
    double d5 = -1.0D;
    double d6 = 0.0D;
    boolean bool1 = false;
    boolean bool2 = false;
    boolean bool3 = false;
    PowerSource.CapacityUnits capacityUnits = PowerSource.CapacityUnits.RELATIVE;
    int i = 0;
    int j = 1;
    boolean bool4 = true;
    byte b = -1;
    String str3 = "unknown";
    LocalDate localDate = null;
    String str4 = "unknown";
    String str5 = "unknown";
    double d7 = 0.0D;
    if (KSTAT_BATT_IDX > 0) {
      KstatUtil.KstatChain kstatChain = KstatUtil.openChain();
      try {
        LibKstat.Kstat kstat = kstatChain.lookup(KSTAT_BATT_MOD[KSTAT_BATT_IDX], 0, "battery BIF0");
        if (kstat != null && kstatChain.read(kstat)) {
          long l1 = KstatUtil.dataLookupLong(kstat, "bif_last_cap");
          if (l1 == -1L || l1 <= 0L)
            l1 = KstatUtil.dataLookupLong(kstat, "bif_design_cap"); 
          if (l1 != -1L && l1 > 0L)
            j = (int)l1; 
          long l2 = KstatUtil.dataLookupLong(kstat, "bif_unit");
          if (l2 == 0L) {
            capacityUnits = PowerSource.CapacityUnits.MWH;
          } else if (l2 == 1L) {
            capacityUnits = PowerSource.CapacityUnits.MAH;
          } 
          str2 = KstatUtil.dataLookupString(kstat, "bif_model");
          str5 = KstatUtil.dataLookupString(kstat, "bif_serial");
          str3 = KstatUtil.dataLookupString(kstat, "bif_type");
          str4 = KstatUtil.dataLookupString(kstat, "bif_oem_info");
        } 
        kstat = kstatChain.lookup(KSTAT_BATT_MOD[KSTAT_BATT_IDX], 0, "battery BST0");
        if (kstat != null && kstatChain.read(kstat)) {
          long l1 = KstatUtil.dataLookupLong(kstat, "bst_rem_cap");
          if (l1 >= 0L)
            i = (int)l1; 
          long l2 = KstatUtil.dataLookupLong(kstat, "bst_rate");
          if (l2 == -1L)
            l2 = 0L; 
          boolean bool = ((KstatUtil.dataLookupLong(kstat, "bst_state") & 0x10L) > 0L) ? true : false;
          if (!bool)
            d2 = (l2 > 0L) ? (3600.0D * l1 / l2) : -1.0D; 
          long l3 = KstatUtil.dataLookupLong(kstat, "bst_voltage");
          if (l3 > 0L) {
            d5 = l3 / 1000.0D;
            d6 = d4 * 1000.0D / l3;
          } 
        } 
        if (kstatChain != null)
          kstatChain.close(); 
      } catch (Throwable throwable) {
        if (kstatChain != null)
          try {
            kstatChain.close();
          } catch (Throwable throwable1) {
            throwable.addSuppressed(throwable1);
          }  
        throw throwable;
      } 
    } 
    return new SolarisPowerSource(str1, str2, d1, d2, d3, d4, d5, d6, bool1, bool2, bool3, capacityUnits, i, j, bool4, b, str3, localDate, str4, str5, d7);
  }
  
  static {
    KstatUtil.KstatChain kstatChain = KstatUtil.openChain();
    try {
      if (kstatChain.lookup(KSTAT_BATT_MOD[1], 0, null) != null) {
        KSTAT_BATT_IDX = 1;
      } else if (kstatChain.lookup(KSTAT_BATT_MOD[2], 0, null) != null) {
        KSTAT_BATT_IDX = 2;
      } else {
        KSTAT_BATT_IDX = 0;
      } 
      if (kstatChain != null)
        kstatChain.close(); 
    } catch (Throwable throwable) {
      if (kstatChain != null)
        try {
          kstatChain.close();
        } catch (Throwable throwable1) {
          throwable.addSuppressed(throwable1);
        }  
      throw throwable;
    } 
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\oshi\hardware\platfor\\unix\solaris\SolarisPowerSource.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */