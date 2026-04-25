package oshi.jna.platform.unix;

import com.sun.jna.Native;
import com.sun.jna.NativeLong;
import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.Structure.FieldOrder;
import com.sun.jna.platform.unix.LibCAPI;
import java.nio.ByteBuffer;
import oshi.util.FileUtil;

public interface SolarisLibc extends CLibrary {
  public static final SolarisLibc INSTANCE = (SolarisLibc)Native.load("c", SolarisLibc.class);
  
  public static final int UTX_USERSIZE = 32;
  
  public static final int UTX_LINESIZE = 32;
  
  public static final int UTX_IDSIZE = 4;
  
  public static final int UTX_HOSTSIZE = 257;
  
  public static final int PRCLSZ = 8;
  
  public static final int PRFNSZ = 16;
  
  public static final int PRLNSZ = 32;
  
  public static final int PRARGSZ = 80;
  
  SolarisUtmpx getutxent();
  
  int thr_self();
  
  public static class Timestruc {
    public NativeLong tv_sec;
    
    public NativeLong tv_nsec;
    
    public Timestruc(ByteBuffer param1ByteBuffer) {
      this.tv_sec = FileUtil.readNativeLongFromBuffer(param1ByteBuffer);
      this.tv_nsec = FileUtil.readNativeLongFromBuffer(param1ByteBuffer);
    }
  }
  
  public static class SolarisPrUsage {
    public int pr_lwpid;
    
    public int pr_count;
    
    public SolarisLibc.Timestruc pr_tstamp;
    
    public SolarisLibc.Timestruc pr_create;
    
    public SolarisLibc.Timestruc pr_term;
    
    public SolarisLibc.Timestruc pr_rtime;
    
    public SolarisLibc.Timestruc pr_utime;
    
    public SolarisLibc.Timestruc pr_stime;
    
    public SolarisLibc.Timestruc pr_ttime;
    
    public SolarisLibc.Timestruc pr_tftime;
    
    public SolarisLibc.Timestruc pr_dftime;
    
    public SolarisLibc.Timestruc pr_kftime;
    
    public SolarisLibc.Timestruc pr_ltime;
    
    public SolarisLibc.Timestruc pr_slptime;
    
    public SolarisLibc.Timestruc pr_wtime;
    
    public SolarisLibc.Timestruc pr_stoptime;
    
    public SolarisLibc.Timestruc[] filltime = new SolarisLibc.Timestruc[6];
    
    public NativeLong pr_minf;
    
    public NativeLong pr_majf;
    
    public NativeLong pr_nswap;
    
    public NativeLong pr_inblk;
    
    public NativeLong pr_oublk;
    
    public NativeLong pr_msnd;
    
    public NativeLong pr_mrcv;
    
    public NativeLong pr_sigs;
    
    public NativeLong pr_vctx;
    
    public NativeLong pr_ictx;
    
    public NativeLong pr_sysc;
    
    public NativeLong pr_ioch;
    
    public NativeLong[] filler = new NativeLong[10];
    
    public SolarisPrUsage(ByteBuffer param1ByteBuffer) {
      this.pr_lwpid = FileUtil.readIntFromBuffer(param1ByteBuffer);
      this.pr_count = FileUtil.readIntFromBuffer(param1ByteBuffer);
      this.pr_tstamp = new SolarisLibc.Timestruc(param1ByteBuffer);
      this.pr_create = new SolarisLibc.Timestruc(param1ByteBuffer);
      this.pr_term = new SolarisLibc.Timestruc(param1ByteBuffer);
      this.pr_rtime = new SolarisLibc.Timestruc(param1ByteBuffer);
      this.pr_utime = new SolarisLibc.Timestruc(param1ByteBuffer);
      this.pr_stime = new SolarisLibc.Timestruc(param1ByteBuffer);
      this.pr_ttime = new SolarisLibc.Timestruc(param1ByteBuffer);
      this.pr_tftime = new SolarisLibc.Timestruc(param1ByteBuffer);
      this.pr_dftime = new SolarisLibc.Timestruc(param1ByteBuffer);
      this.pr_kftime = new SolarisLibc.Timestruc(param1ByteBuffer);
      this.pr_ltime = new SolarisLibc.Timestruc(param1ByteBuffer);
      this.pr_slptime = new SolarisLibc.Timestruc(param1ByteBuffer);
      this.pr_wtime = new SolarisLibc.Timestruc(param1ByteBuffer);
      this.pr_stoptime = new SolarisLibc.Timestruc(param1ByteBuffer);
      byte b;
      for (b = 0; b < this.filltime.length; b++)
        this.filltime[b] = new SolarisLibc.Timestruc(param1ByteBuffer); 
      this.pr_minf = FileUtil.readNativeLongFromBuffer(param1ByteBuffer);
      this.pr_majf = FileUtil.readNativeLongFromBuffer(param1ByteBuffer);
      this.pr_nswap = FileUtil.readNativeLongFromBuffer(param1ByteBuffer);
      this.pr_inblk = FileUtil.readNativeLongFromBuffer(param1ByteBuffer);
      this.pr_oublk = FileUtil.readNativeLongFromBuffer(param1ByteBuffer);
      this.pr_msnd = FileUtil.readNativeLongFromBuffer(param1ByteBuffer);
      this.pr_mrcv = FileUtil.readNativeLongFromBuffer(param1ByteBuffer);
      this.pr_sigs = FileUtil.readNativeLongFromBuffer(param1ByteBuffer);
      this.pr_vctx = FileUtil.readNativeLongFromBuffer(param1ByteBuffer);
      this.pr_ictx = FileUtil.readNativeLongFromBuffer(param1ByteBuffer);
      this.pr_sysc = FileUtil.readNativeLongFromBuffer(param1ByteBuffer);
      this.pr_ioch = FileUtil.readNativeLongFromBuffer(param1ByteBuffer);
      for (b = 0; b < this.filler.length; b++)
        this.filler[b] = FileUtil.readNativeLongFromBuffer(param1ByteBuffer); 
    }
  }
  
  public static class SolarisLwpsInfo {
    public int pr_flag;
    
    public int pr_lwpid;
    
    public Pointer pr_addr;
    
    public Pointer pr_wchan;
    
    public byte pr_stype;
    
    public byte pr_state;
    
    public byte pr_sname;
    
    public byte pr_nice;
    
    public short pr_syscall;
    
    public byte pr_oldpri;
    
    public byte pr_cpu;
    
    public int pr_pri;
    
    public short pr_pctcpu;
    
    public short pr_pad;
    
    public SolarisLibc.Timestruc pr_start;
    
    public SolarisLibc.Timestruc pr_time;
    
    public byte[] pr_clname = new byte[8];
    
    public byte[] pr_oldname = new byte[16];
    
    public int pr_onpro;
    
    public int pr_bindpro;
    
    public int pr_bindpset;
    
    public int pr_lgrp;
    
    public long pr_last_onproc;
    
    public byte[] pr_name = new byte[32];
    
    public SolarisLwpsInfo(ByteBuffer param1ByteBuffer) {
      this.pr_flag = FileUtil.readIntFromBuffer(param1ByteBuffer);
      this.pr_lwpid = FileUtil.readIntFromBuffer(param1ByteBuffer);
      this.pr_addr = FileUtil.readPointerFromBuffer(param1ByteBuffer);
      this.pr_wchan = FileUtil.readPointerFromBuffer(param1ByteBuffer);
      this.pr_stype = FileUtil.readByteFromBuffer(param1ByteBuffer);
      this.pr_state = FileUtil.readByteFromBuffer(param1ByteBuffer);
      this.pr_sname = FileUtil.readByteFromBuffer(param1ByteBuffer);
      this.pr_nice = FileUtil.readByteFromBuffer(param1ByteBuffer);
      this.pr_syscall = FileUtil.readShortFromBuffer(param1ByteBuffer);
      this.pr_oldpri = FileUtil.readByteFromBuffer(param1ByteBuffer);
      this.pr_cpu = FileUtil.readByteFromBuffer(param1ByteBuffer);
      this.pr_pri = FileUtil.readIntFromBuffer(param1ByteBuffer);
      this.pr_pctcpu = FileUtil.readShortFromBuffer(param1ByteBuffer);
      this.pr_pad = FileUtil.readShortFromBuffer(param1ByteBuffer);
      this.pr_start = new SolarisLibc.Timestruc(param1ByteBuffer);
      this.pr_time = new SolarisLibc.Timestruc(param1ByteBuffer);
      FileUtil.readByteArrayFromBuffer(param1ByteBuffer, this.pr_clname);
      FileUtil.readByteArrayFromBuffer(param1ByteBuffer, this.pr_oldname);
      this.pr_onpro = FileUtil.readIntFromBuffer(param1ByteBuffer);
      this.pr_bindpro = FileUtil.readIntFromBuffer(param1ByteBuffer);
      this.pr_bindpset = FileUtil.readIntFromBuffer(param1ByteBuffer);
      this.pr_lgrp = FileUtil.readIntFromBuffer(param1ByteBuffer);
      this.pr_last_onproc = FileUtil.readLongFromBuffer(param1ByteBuffer);
      FileUtil.readByteArrayFromBuffer(param1ByteBuffer, this.pr_name);
    }
  }
  
  public static class SolarisPsInfo {
    public int pr_flag;
    
    public int pr_nlwp;
    
    public int pr_pid;
    
    public int pr_ppid;
    
    public int pr_pgid;
    
    public int pr_sid;
    
    public int pr_uid;
    
    public int pr_euid;
    
    public int pr_gid;
    
    public int pr_egid;
    
    public Pointer pr_addr;
    
    public LibCAPI.size_t pr_size;
    
    public LibCAPI.size_t pr_rssize;
    
    public LibCAPI.size_t pr_rssizepriv;
    
    public NativeLong pr_ttydev;
    
    public short pr_pctcpu;
    
    public short pr_pctmem;
    
    public SolarisLibc.Timestruc pr_start;
    
    public SolarisLibc.Timestruc pr_time;
    
    public SolarisLibc.Timestruc pr_ctime;
    
    public byte[] pr_fname = new byte[16];
    
    public byte[] pr_psargs = new byte[80];
    
    public int pr_wstat;
    
    public int pr_argc;
    
    public Pointer pr_argv;
    
    public Pointer pr_envp;
    
    public byte pr_dmodel;
    
    public byte[] pr_pad2 = new byte[3];
    
    public int pr_taskid;
    
    public int pr_projid;
    
    public int pr_nzomb;
    
    public int pr_poolid;
    
    public int pr_zoneid;
    
    public int pr_contract;
    
    public int pr_filler;
    
    public SolarisLibc.SolarisLwpsInfo pr_lwp;
    
    public SolarisPsInfo(ByteBuffer param1ByteBuffer) {
      this.pr_flag = FileUtil.readIntFromBuffer(param1ByteBuffer);
      this.pr_nlwp = FileUtil.readIntFromBuffer(param1ByteBuffer);
      this.pr_pid = FileUtil.readIntFromBuffer(param1ByteBuffer);
      this.pr_ppid = FileUtil.readIntFromBuffer(param1ByteBuffer);
      this.pr_pgid = FileUtil.readIntFromBuffer(param1ByteBuffer);
      this.pr_sid = FileUtil.readIntFromBuffer(param1ByteBuffer);
      this.pr_uid = FileUtil.readIntFromBuffer(param1ByteBuffer);
      this.pr_euid = FileUtil.readIntFromBuffer(param1ByteBuffer);
      this.pr_gid = FileUtil.readIntFromBuffer(param1ByteBuffer);
      this.pr_egid = FileUtil.readIntFromBuffer(param1ByteBuffer);
      this.pr_addr = FileUtil.readPointerFromBuffer(param1ByteBuffer);
      this.pr_size = FileUtil.readSizeTFromBuffer(param1ByteBuffer);
      this.pr_rssize = FileUtil.readSizeTFromBuffer(param1ByteBuffer);
      this.pr_rssizepriv = FileUtil.readSizeTFromBuffer(param1ByteBuffer);
      this.pr_ttydev = FileUtil.readNativeLongFromBuffer(param1ByteBuffer);
      this.pr_pctcpu = FileUtil.readShortFromBuffer(param1ByteBuffer);
      this.pr_pctmem = FileUtil.readShortFromBuffer(param1ByteBuffer);
      if (Native.LONG_SIZE > 4)
        FileUtil.readIntFromBuffer(param1ByteBuffer); 
      this.pr_start = new SolarisLibc.Timestruc(param1ByteBuffer);
      this.pr_time = new SolarisLibc.Timestruc(param1ByteBuffer);
      this.pr_ctime = new SolarisLibc.Timestruc(param1ByteBuffer);
      FileUtil.readByteArrayFromBuffer(param1ByteBuffer, this.pr_fname);
      FileUtil.readByteArrayFromBuffer(param1ByteBuffer, this.pr_psargs);
      this.pr_wstat = FileUtil.readIntFromBuffer(param1ByteBuffer);
      this.pr_argc = FileUtil.readIntFromBuffer(param1ByteBuffer);
      this.pr_argv = FileUtil.readPointerFromBuffer(param1ByteBuffer);
      this.pr_envp = FileUtil.readPointerFromBuffer(param1ByteBuffer);
      this.pr_dmodel = FileUtil.readByteFromBuffer(param1ByteBuffer);
      FileUtil.readByteArrayFromBuffer(param1ByteBuffer, this.pr_pad2);
      this.pr_taskid = FileUtil.readIntFromBuffer(param1ByteBuffer);
      this.pr_projid = FileUtil.readIntFromBuffer(param1ByteBuffer);
      this.pr_nzomb = FileUtil.readIntFromBuffer(param1ByteBuffer);
      this.pr_poolid = FileUtil.readIntFromBuffer(param1ByteBuffer);
      this.pr_zoneid = FileUtil.readIntFromBuffer(param1ByteBuffer);
      this.pr_contract = FileUtil.readIntFromBuffer(param1ByteBuffer);
      this.pr_filler = FileUtil.readIntFromBuffer(param1ByteBuffer);
      this.pr_lwp = new SolarisLibc.SolarisLwpsInfo(param1ByteBuffer);
    }
  }
  
  @FieldOrder({"tv_sec", "tv_usec"})
  public static class Timeval extends Structure {
    public NativeLong tv_sec;
    
    public NativeLong tv_usec;
  }
  
  @FieldOrder({"e_termination", "e_exit"})
  public static class Exit_status extends Structure {
    public short e_termination;
    
    public short e_exit;
  }
  
  @FieldOrder({"ut_user", "ut_id", "ut_line", "ut_pid", "ut_type", "ut_exit", "ut_tv", "ut_session", "pad", "ut_syslen", "ut_host"})
  public static class SolarisUtmpx extends Structure {
    public byte[] ut_user = new byte[32];
    
    public byte[] ut_id = new byte[4];
    
    public byte[] ut_line = new byte[32];
    
    public int ut_pid;
    
    public short ut_type;
    
    public SolarisLibc.Exit_status ut_exit;
    
    public SolarisLibc.Timeval ut_tv;
    
    public int ut_session;
    
    public int[] pad = new int[5];
    
    public short ut_syslen;
    
    public byte[] ut_host = new byte[257];
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\oshi\jna\platfor\\unix\SolarisLibc.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */