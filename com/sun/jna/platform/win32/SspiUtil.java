package com.sun.jna.platform.win32;

public class SspiUtil {
  public static class ManagedSecBufferDesc extends Sspi.SecBufferDesc {
    private final Sspi.SecBuffer[] secBuffers;
    
    public ManagedSecBufferDesc(int param1Int, byte[] param1ArrayOfbyte) {
      this.secBuffers = new Sspi.SecBuffer[] { new Sspi.SecBuffer(param1Int, param1ArrayOfbyte) };
      this.pBuffers = this.secBuffers[0].getPointer();
      this.cBuffers = this.secBuffers.length;
    }
    
    public ManagedSecBufferDesc(int param1Int1, int param1Int2) {
      this.secBuffers = new Sspi.SecBuffer[] { new Sspi.SecBuffer(param1Int1, param1Int2) };
      this.pBuffers = this.secBuffers[0].getPointer();
      this.cBuffers = this.secBuffers.length;
    }
    
    public ManagedSecBufferDesc(int param1Int) {
      this.cBuffers = param1Int;
      this.secBuffers = (Sspi.SecBuffer[])(new Sspi.SecBuffer()).toArray(param1Int);
      this.pBuffers = this.secBuffers[0].getPointer();
      this.cBuffers = this.secBuffers.length;
    }
    
    public Sspi.SecBuffer getBuffer(int param1Int) {
      return this.secBuffers[param1Int];
    }
    
    public void write() {
      for (Sspi.SecBuffer secBuffer : this.secBuffers)
        secBuffer.write(); 
      writeField("ulVersion");
      writeField("pBuffers");
      writeField("cBuffers");
    }
    
    public void read() {
      for (Sspi.SecBuffer secBuffer : this.secBuffers)
        secBuffer.read(); 
    }
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\com\sun\jna\platform\win32\SspiUtil.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */