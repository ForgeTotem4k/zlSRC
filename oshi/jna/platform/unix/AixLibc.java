package oshi.jna.platform.unix;

import com.sun.jna.Native;
import java.nio.ByteBuffer;
import oshi.util.FileUtil;

public interface AixLibc extends CLibrary {
  public static final AixLibc INSTANCE = (AixLibc)Native.load("c", AixLibc.class);
  
  public static final int PRCLSZ = 8;
  
  public static final int PRFNSZ = 16;
  
  public static final int PRARGSZ = 80;
  
  int thread_self();
  
  public static class Timestruc {
    public long tv_sec;
    
    public int tv_nsec;
    
    public int pad;
    
    public Timestruc(ByteBuffer param1ByteBuffer) {
      this.tv_sec = FileUtil.readLongFromBuffer(param1ByteBuffer);
      this.tv_nsec = FileUtil.readIntFromBuffer(param1ByteBuffer);
      this.pad = FileUtil.readIntFromBuffer(param1ByteBuffer);
    }
  }
  
  public static class AixLwpsInfo {
    public long pr_lwpid;
    
    public long pr_addr;
    
    public long pr_wchan;
    
    public int pr_flag;
    
    public byte pr_wtype;
    
    public byte pr_state;
    
    public byte pr_sname;
    
    public byte pr_nice;
    
    public int pr_pri;
    
    public int pr_policy;
    
    public byte[] pr_clname = new byte[8];
    
    public int pr_onpro;
    
    public int pr_bindpro;
    
    public AixLwpsInfo(ByteBuffer param1ByteBuffer) {
      this.pr_lwpid = FileUtil.readLongFromBuffer(param1ByteBuffer);
      this.pr_addr = FileUtil.readLongFromBuffer(param1ByteBuffer);
      this.pr_wchan = FileUtil.readLongFromBuffer(param1ByteBuffer);
      this.pr_flag = FileUtil.readIntFromBuffer(param1ByteBuffer);
      this.pr_wtype = FileUtil.readByteFromBuffer(param1ByteBuffer);
      this.pr_state = FileUtil.readByteFromBuffer(param1ByteBuffer);
      this.pr_sname = FileUtil.readByteFromBuffer(param1ByteBuffer);
      this.pr_nice = FileUtil.readByteFromBuffer(param1ByteBuffer);
      this.pr_pri = FileUtil.readIntFromBuffer(param1ByteBuffer);
      this.pr_policy = FileUtil.readIntFromBuffer(param1ByteBuffer);
      FileUtil.readByteArrayFromBuffer(param1ByteBuffer, this.pr_clname);
      this.pr_onpro = FileUtil.readIntFromBuffer(param1ByteBuffer);
      this.pr_bindpro = FileUtil.readIntFromBuffer(param1ByteBuffer);
    }
  }
  
  public static class AixPsInfo {
    public int pr_flag;
    
    public int pr_flag2;
    
    public int pr_nlwp;
    
    public int pr__pad1;
    
    public long pr_uid;
    
    public long pr_euid;
    
    public long pr_gid;
    
    public long pr_egid;
    
    public long pr_pid;
    
    public long pr_ppid;
    
    public long pr_pgid;
    
    public long pr_sid;
    
    public long pr_ttydev;
    
    public long pr_addr;
    
    public long pr_size;
    
    public long pr_rssize;
    
    public AixLibc.Timestruc pr_start;
    
    public AixLibc.Timestruc pr_time;
    
    public short pr_cid;
    
    public short pr__pad2;
    
    public int pr_argc;
    
    public long pr_argv;
    
    public long pr_envp;
    
    public byte[] pr_fname = new byte[16];
    
    public byte[] pr_psargs = new byte[80];
    
    public long[] pr__pad = new long[8];
    
    public AixLibc.AixLwpsInfo pr_lwp;
    
    public AixPsInfo(ByteBuffer param1ByteBuffer) {
      this.pr_flag = FileUtil.readIntFromBuffer(param1ByteBuffer);
      this.pr_flag2 = FileUtil.readIntFromBuffer(param1ByteBuffer);
      this.pr_nlwp = FileUtil.readIntFromBuffer(param1ByteBuffer);
      this.pr__pad1 = FileUtil.readIntFromBuffer(param1ByteBuffer);
      this.pr_uid = FileUtil.readLongFromBuffer(param1ByteBuffer);
      this.pr_euid = FileUtil.readLongFromBuffer(param1ByteBuffer);
      this.pr_gid = FileUtil.readLongFromBuffer(param1ByteBuffer);
      this.pr_egid = FileUtil.readLongFromBuffer(param1ByteBuffer);
      this.pr_pid = FileUtil.readLongFromBuffer(param1ByteBuffer);
      this.pr_ppid = FileUtil.readLongFromBuffer(param1ByteBuffer);
      this.pr_pgid = FileUtil.readLongFromBuffer(param1ByteBuffer);
      this.pr_sid = FileUtil.readLongFromBuffer(param1ByteBuffer);
      this.pr_ttydev = FileUtil.readLongFromBuffer(param1ByteBuffer);
      this.pr_addr = FileUtil.readLongFromBuffer(param1ByteBuffer);
      this.pr_size = FileUtil.readLongFromBuffer(param1ByteBuffer);
      this.pr_rssize = FileUtil.readLongFromBuffer(param1ByteBuffer);
      this.pr_start = new AixLibc.Timestruc(param1ByteBuffer);
      this.pr_time = new AixLibc.Timestruc(param1ByteBuffer);
      this.pr_cid = FileUtil.readShortFromBuffer(param1ByteBuffer);
      this.pr__pad2 = FileUtil.readShortFromBuffer(param1ByteBuffer);
      this.pr_argc = FileUtil.readIntFromBuffer(param1ByteBuffer);
      this.pr_argv = FileUtil.readLongFromBuffer(param1ByteBuffer);
      this.pr_envp = FileUtil.readLongFromBuffer(param1ByteBuffer);
      FileUtil.readByteArrayFromBuffer(param1ByteBuffer, this.pr_fname);
      FileUtil.readByteArrayFromBuffer(param1ByteBuffer, this.pr_psargs);
      for (byte b = 0; b < this.pr__pad.length; b++)
        this.pr__pad[b] = FileUtil.readLongFromBuffer(param1ByteBuffer); 
      this.pr_lwp = new AixLibc.AixLwpsInfo(param1ByteBuffer);
    }
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\oshi\jna\platfor\\unix\AixLibc.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */