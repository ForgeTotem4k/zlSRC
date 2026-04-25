package pro.gravit.launcher.runtime.client;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.Objects;
import java.util.regex.Pattern;
import pro.gravit.launcher.base.profiles.ClientProfile;
import pro.gravit.launcher.base.profiles.ClientProfileVersions;
import pro.gravit.launcher.core.serialize.HInput;
import pro.gravit.launcher.core.serialize.HOutput;
import pro.gravit.utils.helper.IOHelper;
import pro.gravit.utils.helper.LogHelper;
import pro.gravit.utils.helper.VerifyHelper;

public final class ServerPinger {
  private static final String LEGACY_PING_HOST_MAGIC = "§1";
  
  private static final String LEGACY_PING_HOST_CHANNEL = "MC|PingHost";
  
  private static final Pattern LEGACY_PING_HOST_DELIMETER = Pattern.compile("\000", 16);
  
  private static final int PACKET_LENGTH = 65535;
  
  private final InetSocketAddress address;
  
  private final int protocol;
  
  private final ClientProfile.Version version;
  
  private final Object cacheLock = new Object();
  
  private Result cache = null;
  
  private Exception cacheException = null;
  
  private Instant cacheTime = null;
  
  public ServerPinger(ClientProfile paramClientProfile) {
    this(paramClientProfile.getDefaultServerProfile(), paramClientProfile.getVersion());
  }
  
  public ServerPinger(ClientProfile.ServerProfile paramServerProfile, ClientProfile.Version paramVersion) {
    if (paramServerProfile == null)
      throw new NullPointerException("ServerProfile null"); 
    this.address = paramServerProfile.toSocketAddress();
    this.version = Objects.<ClientProfile.Version>requireNonNull(paramVersion, "version");
    this.protocol = paramServerProfile.protocol;
  }
  
  private static String readUTF16String(HInput paramHInput) throws IOException {
    int i = paramHInput.readUnsignedShort() << 1;
    byte[] arrayOfByte = paramHInput.readByteArray(-i);
    return new String(arrayOfByte, StandardCharsets.UTF_16BE);
  }
  
  private static void writeUTF16String(HOutput paramHOutput, String paramString) throws IOException {
    paramHOutput.writeShort((short)paramString.length());
    paramHOutput.stream.write(paramString.getBytes(StandardCharsets.UTF_16BE));
  }
  
  private Result doPing() throws IOException {
    Socket socket = IOHelper.newSocket();
    try {
      socket.connect(IOHelper.resolve(this.address), IOHelper.SOCKET_TIMEOUT);
      HInput hInput = new HInput(socket.getInputStream());
      try {
        HOutput hOutput = new HOutput(socket.getOutputStream());
        try {
          Result result = (this.version.compareTo(ClientProfileVersions.MINECRAFT_1_7_2) >= 0) ? modernPing(hInput, hOutput, this.protocol) : legacyPing(hInput, hOutput, (this.version.compareTo(ClientProfileVersions.MINECRAFT_1_6_4) >= 0));
          hOutput.close();
          hInput.close();
          if (socket != null)
            socket.close(); 
          return result;
        } catch (Throwable throwable) {
          try {
            hOutput.close();
          } catch (Throwable throwable1) {
            throwable.addSuppressed(throwable1);
          } 
          throw throwable;
        } 
      } catch (Throwable throwable) {
        try {
          hInput.close();
        } catch (Throwable throwable1) {
          throwable.addSuppressed(throwable1);
        } 
        throw throwable;
      } 
    } catch (Throwable throwable) {
      if (socket != null)
        try {
          socket.close();
        } catch (Throwable throwable1) {
          throwable.addSuppressed(throwable1);
        }  
      throw throwable;
    } 
  }
  
  private Result legacyPing(HInput paramHInput, HOutput paramHOutput, boolean paramBoolean) throws IOException {
    paramHOutput.writeUnsignedByte(254);
    paramHOutput.writeUnsignedByte(1);
    if (paramBoolean) {
      byte[] arrayOfByte;
      paramHOutput.writeUnsignedByte(250);
      writeUTF16String(paramHOutput, "MC|PingHost");
      ByteArrayOutputStream byteArrayOutputStream = IOHelper.newByteArrayOutput();
      try {
        HOutput hOutput = new HOutput(byteArrayOutputStream);
        try {
          hOutput.writeUnsignedByte(74);
          writeUTF16String(hOutput, this.address.getHostString());
          hOutput.writeInt(this.address.getPort());
          hOutput.close();
        } catch (Throwable throwable) {
          try {
            hOutput.close();
          } catch (Throwable throwable1) {
            throwable.addSuppressed(throwable1);
          } 
          throw throwable;
        } 
        arrayOfByte = byteArrayOutputStream.toByteArray();
        if (byteArrayOutputStream != null)
          byteArrayOutputStream.close(); 
      } catch (Throwable throwable) {
        if (byteArrayOutputStream != null)
          try {
            byteArrayOutputStream.close();
          } catch (Throwable throwable1) {
            throwable.addSuppressed(throwable1);
          }  
        throw throwable;
      } 
      paramHOutput.writeShort((short)arrayOfByte.length);
      paramHOutput.stream.write(arrayOfByte);
    } 
    paramHOutput.flush();
    int i = paramHInput.readUnsignedByte();
    if (i != 255)
      throw new IOException("Illegal kick packet ID: " + i); 
    String str1 = readUTF16String(paramHInput);
    LogHelper.debug("Ping response (legacy): '%s'", new Object[] { str1 });
    String[] arrayOfString = LEGACY_PING_HOST_DELIMETER.split(str1);
    if (arrayOfString.length != 6)
      throw new IOException("Tokens count mismatch"); 
    String str2 = arrayOfString[0];
    if (!str2.equals("§1"))
      throw new IOException("Magic file mismatch: " + str2); 
    int j = Integer.parseInt(arrayOfString[1]);
    String str3 = arrayOfString[2];
    int k = VerifyHelper.verifyInt(Integer.parseInt(arrayOfString[4]), VerifyHelper.NOT_NEGATIVE, "onlinePlayers can't be < 0");
    int m = VerifyHelper.verifyInt(Integer.parseInt(arrayOfString[5]), VerifyHelper.NOT_NEGATIVE, "maxPlayers can't be < 0");
    return new Result(k, m, str1);
  }
  
  private Result modernPing(HInput paramHInput, HOutput paramHOutput, int paramInt) throws IOException {
    byte[] arrayOfByte1;
    String str;
    ByteArrayOutputStream byteArrayOutputStream = IOHelper.newByteArrayOutput();
    try {
      HOutput hOutput = new HOutput(byteArrayOutputStream);
      try {
        hOutput.writeVarInt(0);
        hOutput.writeVarInt((paramInt > 0) ? paramInt : 4);
        hOutput.writeString(this.address.getHostString(), 0);
        hOutput.writeShort((short)this.address.getPort());
        hOutput.writeVarInt(1);
        hOutput.close();
      } catch (Throwable throwable) {
        try {
          hOutput.close();
        } catch (Throwable throwable1) {
          throwable.addSuppressed(throwable1);
        } 
        throw throwable;
      } 
      arrayOfByte1 = byteArrayOutputStream.toByteArray();
      if (byteArrayOutputStream != null)
        byteArrayOutputStream.close(); 
    } catch (Throwable throwable) {
      if (byteArrayOutputStream != null)
        try {
          byteArrayOutputStream.close();
        } catch (Throwable throwable1) {
          throwable.addSuppressed(throwable1);
        }  
      throw throwable;
    } 
    paramHOutput.writeByteArray(arrayOfByte1, 65535);
    paramHOutput.writeVarInt(1);
    paramHOutput.writeVarInt(0);
    paramHOutput.flush();
    int i;
    for (i = 0; i; i = IOHelper.verifyLength(paramHInput.readVarInt(), 65535));
    byte[] arrayOfByte2 = paramHInput.readByteArray(-i);
    HInput hInput = new HInput(arrayOfByte2);
    try {
      int m = hInput.readVarInt();
      if (m != 0)
        throw new IOException("Illegal status packet ID: " + m); 
      str = hInput.readString(65535);
      LogHelper.dev("Ping response (modern): '%s'", new Object[] { str });
      hInput.close();
    } catch (Throwable throwable) {
      try {
        hInput.close();
      } catch (Throwable throwable1) {
        throwable.addSuppressed(throwable1);
      } 
      throw throwable;
    } 
    JsonElement jsonElement = JsonParser.parseString(str);
    if (jsonElement.isJsonPrimitive())
      throw new IOException(jsonElement.getAsString()); 
    JsonObject jsonObject1 = jsonElement.getAsJsonObject();
    if (jsonObject1.has("error"))
      throw new IOException(jsonObject1.get("error").getAsString()); 
    JsonObject jsonObject2 = jsonObject1.get("players").getAsJsonObject();
    int j = jsonObject2.get("online").getAsInt();
    int k = jsonObject2.get("max").getAsInt();
    return new Result(j, k, str);
  }
  
  public Result ping() throws IOException {
    Instant instant = Instant.now();
    synchronized (this.cacheLock) {
      if (this.cacheTime == null || Duration.between(instant, this.cacheTime).toMillis() >= IOHelper.SOCKET_TIMEOUT) {
        this.cacheTime = instant;
        try {
          this.cache = doPing();
          this.cacheException = null;
        } catch (IOException|IllegalArgumentException iOException) {
          this.cache = null;
          this.cacheException = iOException;
        } 
      } 
      if (this.cache == null) {
        if (this.cacheException instanceof IOException)
          throw (IOException)this.cacheException; 
        if (this.cacheException instanceof IllegalArgumentException)
          throw (IllegalArgumentException)this.cacheException; 
        this.cacheException = new IOException("Unavailable");
        throw (IOException)this.cacheException;
      } 
      return this.cache;
    } 
  }
  
  public static final class Result {
    public final int onlinePlayers;
    
    public final int maxPlayers;
    
    public final String raw;
    
    public Result(int param1Int1, int param1Int2, String param1String) {
      this.onlinePlayers = VerifyHelper.verifyInt(param1Int1, VerifyHelper.NOT_NEGATIVE, "onlinePlayers can't be < 0");
      this.maxPlayers = VerifyHelper.verifyInt(param1Int2, VerifyHelper.NOT_NEGATIVE, "maxPlayers can't be < 0");
      this.raw = param1String;
    }
    
    public boolean isOverfilled() {
      return (this.onlinePlayers >= this.maxPlayers);
    }
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\pro\gravit\launcher\runtime\client\ServerPinger.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */