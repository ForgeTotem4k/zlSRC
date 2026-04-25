package pro.gravit.utils.helper;

import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.EOFException;
import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.FilterInputStream;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.CopyOption;
import java.nio.file.DirectoryStream;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitOption;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.LinkOption;
import java.nio.file.NoSuchFileException;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileAttribute;
import java.util.Collections;
import java.util.HexFormat;
import java.util.Set;
import java.util.jar.JarFile;
import java.util.jar.Manifest;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.Deflater;
import java.util.zip.Inflater;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;

public final class IOHelper {
  public static final long MB32 = 33554432L;
  
  public static final Charset UNICODE_CHARSET = StandardCharsets.UTF_8;
  
  public static final Charset ASCII_CHARSET = StandardCharsets.US_ASCII;
  
  public static final int MAX_BATCH_SIZE = 128;
  
  public static final int SOCKET_TIMEOUT = VerifyHelper.verifyInt(Integer.parseUnsignedInt(System.getProperty("launcher.socketTimeout", Integer.toString(30000))), VerifyHelper.POSITIVE, "launcher.socketTimeout can't be <= 0");
  
  public static final int HTTP_TIMEOUT = VerifyHelper.verifyInt(Integer.parseUnsignedInt(System.getProperty("launcher.httpTimeout", Integer.toString(5000))), VerifyHelper.POSITIVE, "launcher.httpTimeout can't be <= 0");
  
  public static final int BUFFER_SIZE = VerifyHelper.verifyInt(Integer.parseUnsignedInt(System.getProperty("launcher.bufferSize", Integer.toString(4096))), VerifyHelper.POSITIVE, "launcher.bufferSize can't be <= 0");
  
  public static final String CROSS_SEPARATOR = "/";
  
  public static final FileSystem FS = FileSystems.getDefault();
  
  public static final String PLATFORM_SEPARATOR = FS.getSeparator();
  
  private static final Pattern PLATFORM_SEPARATOR_PATTERN = Pattern.compile(PLATFORM_SEPARATOR, 16);
  
  public static final boolean POSIX = (FS.supportedFileAttributeViews().contains("posix") || FS.supportedFileAttributeViews().contains("Posix"));
  
  public static final Path JVM_DIR = Paths.get(System.getProperty("java.home"), new String[0]);
  
  public static final Path HOME_DIR = Paths.get(System.getProperty("user.home"), new String[0]);
  
  public static final Path WORKING_DIR = Paths.get(System.getProperty("user.dir"), new String[0]);
  
  public static final String USER_AGENT = System.getProperty("launcher.userAgentDefault", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.0)");
  
  private static final OpenOption[] READ_OPTIONS = new OpenOption[] { StandardOpenOption.READ };
  
  private static final OpenOption[] WRITE_OPTIONS = new OpenOption[] { StandardOpenOption.CREATE, StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING };
  
  private static final OpenOption[] APPEND_OPTIONS = new OpenOption[] { StandardOpenOption.CREATE, StandardOpenOption.WRITE, StandardOpenOption.APPEND };
  
  private static final LinkOption[] LINK_OPTIONS = new LinkOption[0];
  
  private static final CopyOption[] COPY_OPTIONS = new CopyOption[] { StandardCopyOption.REPLACE_EXISTING };
  
  private static final Set<FileVisitOption> WALK_OPTIONS = Collections.singleton(FileVisitOption.FOLLOW_LINKS);
  
  private static final Pattern CROSS_SEPARATOR_PATTERN = Pattern.compile("/", 16);
  
  public static void close(AutoCloseable paramAutoCloseable) {
    try {
      paramAutoCloseable.close();
    } catch (Exception exception) {
      LogHelper.error(exception);
    } 
  }
  
  public static void close(InputStream paramInputStream) {
    try {
      paramInputStream.close();
    } catch (Exception exception) {}
  }
  
  public static void close(OutputStream paramOutputStream) {
    try {
      paramOutputStream.flush();
      paramOutputStream.close();
    } catch (Exception exception) {}
  }
  
  public static Manifest getManifest(Class<?> paramClass) {
    Path path = getCodeSource(paramClass);
    try {
      JarFile jarFile = new JarFile(path.toFile());
      try {
        Manifest manifest = jarFile.getManifest();
        jarFile.close();
        return manifest;
      } catch (Throwable throwable) {
        try {
          jarFile.close();
        } catch (Throwable throwable1) {
          throwable.addSuppressed(throwable1);
        } 
        throw throwable;
      } 
    } catch (IOException iOException) {
      throw new RuntimeException(iOException);
    } 
  }
  
  public static URL convertToURL(String paramString) {
    try {
      return (new URI(paramString)).toURL();
    } catch (MalformedURLException|URISyntaxException malformedURLException) {
      throw new IllegalArgumentException("Invalid URL", malformedURLException);
    } 
  }
  
  public static void copy(Path paramPath1, Path paramPath2) throws IOException {
    createParentDirs(paramPath2);
    Files.copy(paramPath1, paramPath2, COPY_OPTIONS);
  }
  
  public static void createParentDirs(Path paramPath) throws IOException {
    Path path = paramPath.getParent();
    if (path != null && !isDir(path))
      Files.createDirectories(path, (FileAttribute<?>[])new FileAttribute[0]); 
  }
  
  public static String decode(byte[] paramArrayOfbyte) {
    return new String(paramArrayOfbyte, UNICODE_CHARSET);
  }
  
  public static String decodeASCII(byte[] paramArrayOfbyte) {
    return new String(paramArrayOfbyte, ASCII_CHARSET);
  }
  
  public static void deleteDir(Path paramPath, boolean paramBoolean) throws IOException {
    walk(paramPath, new DeleteDirVisitor(paramPath, paramBoolean), true);
  }
  
  public static byte[] encode(String paramString) {
    return paramString.getBytes(UNICODE_CHARSET);
  }
  
  public static byte[] encodeASCII(String paramString) {
    return paramString.getBytes(ASCII_CHARSET);
  }
  
  public static boolean exists(Path paramPath) {
    return Files.exists(paramPath, LINK_OPTIONS);
  }
  
  public static Path getCodeSource(Class<?> paramClass) {
    return Paths.get(toURI(paramClass.getProtectionDomain().getCodeSource().getLocation()));
  }
  
  public static String getFileName(Path paramPath) {
    return paramPath.getFileName().toString();
  }
  
  public static String getIP(SocketAddress paramSocketAddress) {
    return ((InetSocketAddress)paramSocketAddress).getAddress().getHostAddress();
  }
  
  public static Path getRoot() {
    String str;
    switch (JVMHelper.OS_TYPE) {
      default:
        throw new IncompatibleClassChangeError();
      case MUSTDIE:
        str = System.getenv("SystemDrive").concat("\\");
      case LINUX:
      case MACOSX:
        break;
    } 
    return Paths.get("/", new String[0]);
  }
  
  public static byte[] getResourceBytes(String paramString) throws IOException {
    return read(getResourceURL(paramString));
  }
  
  public static URL getResourceURL(String paramString) throws NoSuchFileException {
    URL uRL = IOHelper.class.getResource("/" + paramString);
    if (uRL == null)
      throw new NoSuchFileException(paramString); 
    return uRL;
  }
  
  public static boolean hasExtension(Path paramPath, String paramString) {
    return getFileName(paramPath).endsWith("." + paramString);
  }
  
  public static boolean isDir(Path paramPath) {
    return Files.isDirectory(paramPath, LINK_OPTIONS);
  }
  
  public static boolean isEmpty(Path paramPath) throws IOException {
    DirectoryStream<Path> directoryStream = Files.newDirectoryStream(paramPath);
    try {
      boolean bool = !directoryStream.iterator().hasNext() ? true : false;
      if (directoryStream != null)
        directoryStream.close(); 
      return bool;
    } catch (Throwable throwable) {
      if (directoryStream != null)
        try {
          directoryStream.close();
        } catch (Throwable throwable1) {
          throwable.addSuppressed(throwable1);
        }  
      throw throwable;
    } 
  }
  
  public static boolean isFile(Path paramPath) {
    return Files.isRegularFile(paramPath, LINK_OPTIONS);
  }
  
  public static boolean isValidFileName(String paramString) {
    return (!paramString.equals(".") && !paramString.equals("..") && paramString.chars().noneMatch(paramInt -> (paramInt == 47 || paramInt == 92)) && isValidPath(paramString));
  }
  
  public static boolean isValidPath(String paramString) {
    try {
      toPath(paramString);
      return true;
    } catch (InvalidPathException invalidPathException) {
      return false;
    } 
  }
  
  public static boolean isValidTextureBounds(int paramInt1, int paramInt2, boolean paramBoolean) {
    return ((paramInt1 % 64 == 0 && (paramInt2 << 1 == paramInt1 || (!paramBoolean && paramInt2 == paramInt1)) && paramInt1 <= 1024) || (paramBoolean && paramInt1 % 22 == 0 && paramInt2 % 17 == 0 && paramInt1 / 22 == paramInt2 / 17));
  }
  
  public static void move(Path paramPath1, Path paramPath2) throws IOException {
    walk(paramPath1, new MoveFileVisitor(paramPath1, paramPath2), true);
  }
  
  public static byte[] newBuffer() {
    return new byte[BUFFER_SIZE];
  }
  
  public static ByteArrayOutputStream newByteArrayOutput() {
    return new ByteArrayOutputStream();
  }
  
  public static char[] newCharBuffer() {
    return new char[BUFFER_SIZE];
  }
  
  public static URLConnection newConnection(URL paramURL) throws IOException {
    URLConnection uRLConnection = paramURL.openConnection();
    if (uRLConnection instanceof HttpURLConnection) {
      uRLConnection.setReadTimeout(HTTP_TIMEOUT);
      uRLConnection.setConnectTimeout(HTTP_TIMEOUT);
      uRLConnection.addRequestProperty("User-Agent", USER_AGENT);
    } else {
      uRLConnection.setUseCaches(false);
    } 
    uRLConnection.setDoInput(true);
    uRLConnection.setDoOutput(false);
    return uRLConnection;
  }
  
  public static HttpURLConnection newConnectionPost(URL paramURL) throws IOException {
    HttpURLConnection httpURLConnection = (HttpURLConnection)newConnection(paramURL);
    httpURLConnection.setDoOutput(true);
    httpURLConnection.setRequestMethod("POST");
    return httpURLConnection;
  }
  
  public static Deflater newDeflater() {
    Deflater deflater = new Deflater(-1, true);
    deflater.setStrategy(0);
    return deflater;
  }
  
  public static Inflater newInflater() {
    return new Inflater(true);
  }
  
  public static InputStream newInput(Path paramPath) throws IOException {
    return Files.newInputStream(paramPath, READ_OPTIONS);
  }
  
  public static InputStream newBufferedInput(Path paramPath) throws IOException {
    return new BufferedInputStream(Files.newInputStream(paramPath, READ_OPTIONS));
  }
  
  public static InputStream newInput(URL paramURL) throws IOException {
    return newConnection(paramURL).getInputStream();
  }
  
  public static BufferedInputStream newBufferedInput(URL paramURL) throws IOException {
    return new BufferedInputStream(newConnection(paramURL).getInputStream());
  }
  
  public static OutputStream newOutput(Path paramPath) throws IOException {
    return newOutput(paramPath, false);
  }
  
  public static OutputStream newBufferedOutput(Path paramPath) throws IOException {
    return newBufferedOutput(paramPath, false);
  }
  
  public static OutputStream newOutput(Path paramPath, boolean paramBoolean) throws IOException {
    createParentDirs(paramPath);
    return Files.newOutputStream(paramPath, paramBoolean ? APPEND_OPTIONS : WRITE_OPTIONS);
  }
  
  public static OutputStream newBufferedOutput(Path paramPath, boolean paramBoolean) throws IOException {
    createParentDirs(paramPath);
    return new BufferedOutputStream(Files.newOutputStream(paramPath, paramBoolean ? APPEND_OPTIONS : WRITE_OPTIONS));
  }
  
  public static BufferedReader newReader(InputStream paramInputStream) {
    return newReader(paramInputStream, UNICODE_CHARSET);
  }
  
  public static BufferedReader newReader(InputStream paramInputStream, Charset paramCharset) {
    return new BufferedReader(new InputStreamReader(paramInputStream, paramCharset));
  }
  
  public static BufferedReader newReader(Path paramPath) throws IOException {
    return Files.newBufferedReader(paramPath, UNICODE_CHARSET);
  }
  
  public static BufferedReader newReader(URL paramURL) throws IOException {
    URLConnection uRLConnection = newConnection(paramURL);
    String str = uRLConnection.getContentEncoding();
    return newReader(uRLConnection.getInputStream(), (str == null) ? UNICODE_CHARSET : Charset.forName(str));
  }
  
  public static Socket newSocket() throws SocketException {
    Socket socket = new Socket();
    setSocketFlags(socket);
    return socket;
  }
  
  public static BufferedWriter newWriter(FileDescriptor paramFileDescriptor) {
    return newWriter(new FileOutputStream(paramFileDescriptor));
  }
  
  public static BufferedWriter newWriter(OutputStream paramOutputStream) {
    return new BufferedWriter(new OutputStreamWriter(paramOutputStream, UNICODE_CHARSET));
  }
  
  public static BufferedWriter newWriter(Path paramPath) throws IOException {
    return newWriter(paramPath, false);
  }
  
  public static BufferedWriter newWriter(Path paramPath, boolean paramBoolean) throws IOException {
    createParentDirs(paramPath);
    return Files.newBufferedWriter(paramPath, UNICODE_CHARSET, paramBoolean ? APPEND_OPTIONS : WRITE_OPTIONS);
  }
  
  public static ZipEntry newZipEntry(String paramString) {
    ZipEntry zipEntry = new ZipEntry(paramString);
    zipEntry.setTime(0L);
    return zipEntry;
  }
  
  public static ZipEntry newZipEntry(ZipEntry paramZipEntry) {
    return newZipEntry(paramZipEntry.getName());
  }
  
  public static ZipInputStream newZipInput(InputStream paramInputStream) {
    return new ZipInputStream(paramInputStream, UNICODE_CHARSET);
  }
  
  public static ZipInputStream newZipInput(Path paramPath) throws IOException {
    return newZipInput(newInput(paramPath));
  }
  
  public static ZipInputStream newZipInput(URL paramURL) throws IOException {
    return newZipInput(newInput(paramURL));
  }
  
  public static byte[] read(InputStream paramInputStream) throws IOException {
    ByteArrayOutputStream byteArrayOutputStream = newByteArrayOutput();
    try {
      transfer(paramInputStream, byteArrayOutputStream);
      byte[] arrayOfByte = byteArrayOutputStream.toByteArray();
      if (byteArrayOutputStream != null)
        byteArrayOutputStream.close(); 
      return arrayOfByte;
    } catch (Throwable throwable) {
      if (byteArrayOutputStream != null)
        try {
          byteArrayOutputStream.close();
        } catch (Throwable throwable1) {
          throwable.addSuppressed(throwable1);
        }  
      throw throwable;
    } 
  }
  
  public static void read(InputStream paramInputStream, byte[] paramArrayOfbyte) throws IOException {
    for (int i = 0; i < paramArrayOfbyte.length; i += j) {
      int j = paramInputStream.read(paramArrayOfbyte, i, paramArrayOfbyte.length - i);
      if (j < 0)
        throw new EOFException(String.format("%d bytes remaining", new Object[] { Integer.valueOf(paramArrayOfbyte.length - i) })); 
    } 
  }
  
  public static byte[] read(Path paramPath) throws IOException {
    long l = readAttributes(paramPath).size();
    if (l > 2147483647L)
      throw new IOException("File too big"); 
    byte[] arrayOfByte = new byte[(int)l];
    InputStream inputStream = newInput(paramPath);
    try {
      read(inputStream, arrayOfByte);
      if (inputStream != null)
        inputStream.close(); 
    } catch (Throwable throwable) {
      if (inputStream != null)
        try {
          inputStream.close();
        } catch (Throwable throwable1) {
          throwable.addSuppressed(throwable1);
        }  
      throw throwable;
    } 
    return arrayOfByte;
  }
  
  public static byte[] read(URL paramURL) throws IOException {
    InputStream inputStream = newInput(paramURL);
    try {
      byte[] arrayOfByte = read(inputStream);
      if (inputStream != null)
        inputStream.close(); 
      return arrayOfByte;
    } catch (Throwable throwable) {
      if (inputStream != null)
        try {
          inputStream.close();
        } catch (Throwable throwable1) {
          throwable.addSuppressed(throwable1);
        }  
      throw throwable;
    } 
  }
  
  public static BasicFileAttributes readAttributes(Path paramPath) throws IOException {
    return Files.readAttributes(paramPath, BasicFileAttributes.class, LINK_OPTIONS);
  }
  
  public static void readTexture(Object paramObject, boolean paramBoolean) throws IOException {
    ImageReader imageReader = ImageIO.getImageReadersByMIMEType("image/png").next();
    try {
      imageReader.setInput(ImageIO.createImageInputStream(paramObject), false, false);
      int i = imageReader.getWidth(0);
      int j = imageReader.getHeight(0);
      if (!isValidTextureBounds(i, j, paramBoolean))
        throw new IOException(String.format("Invalid texture bounds: %dx%d", new Object[] { Integer.valueOf(i), Integer.valueOf(j) })); 
      imageReader.read(0);
    } finally {
      imageReader.dispose();
    } 
  }
  
  public static String request(URL paramURL) throws IOException {
    return decode(read(paramURL)).trim();
  }
  
  public static InetSocketAddress resolve(InetSocketAddress paramInetSocketAddress) {
    return paramInetSocketAddress.isUnresolved() ? new InetSocketAddress(paramInetSocketAddress.getHostString(), paramInetSocketAddress.getPort()) : paramInetSocketAddress;
  }
  
  public static Path resolveIncremental(Path paramPath, String paramString1, String paramString2) {
    Path path = paramPath.resolve(paramString1 + "." + paramString1);
    if (!exists(path))
      return path; 
    byte b = 1;
    while (true) {
      Path path1 = paramPath.resolve(String.format("%s (%d).%s", new Object[] { paramString1, Integer.valueOf(b), paramString2 }));
      if (exists(path1)) {
        b++;
        continue;
      } 
      return path1;
    } 
  }
  
  public static Path resolveJavaBin(Path paramPath) {
    return resolveJavaBin(paramPath, false);
  }
  
  public static Path resolveJavaBin(Path paramPath, boolean paramBoolean) {
    Path path1 = ((paramPath == null) ? JVM_DIR : paramPath).resolve("bin");
    if (!paramBoolean && !LogHelper.isDebugEnabled()) {
      Path path = path1.resolve("javaw.exe");
      if (isFile(path))
        return path; 
    } 
    Path path2 = path1.resolve("java.exe");
    if (isFile(path2))
      return path2; 
    Path path3 = path1.resolve("java");
    if (isFile(path3))
      return path3; 
    throw new RuntimeException("Java binary wasn't found");
  }
  
  public static void setSocketFlags(Socket paramSocket) throws SocketException {
    paramSocket.setKeepAlive(false);
    paramSocket.setTcpNoDelay(false);
    paramSocket.setReuseAddress(true);
    paramSocket.setSoTimeout(SOCKET_TIMEOUT);
    try {
      paramSocket.setTrafficClass(28);
    } catch (SocketException socketException) {}
    paramSocket.setPerformancePreferences(1, 0, 2);
  }
  
  public static String toAbsPathString(Path paramPath) {
    return toAbsPath(paramPath).toFile().getAbsolutePath();
  }
  
  public static Path toAbsPath(Path paramPath) {
    return paramPath.normalize().toAbsolutePath();
  }
  
  public static byte[] toByteArray(InputStream paramInputStream) throws IOException {
    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(paramInputStream.available());
    transfer(paramInputStream, byteArrayOutputStream);
    return byteArrayOutputStream.toByteArray();
  }
  
  public static Path toPath(String paramString) {
    return Paths.get(CROSS_SEPARATOR_PATTERN.matcher(paramString).replaceAll(Matcher.quoteReplacement(PLATFORM_SEPARATOR)), new String[0]);
  }
  
  public static String toString(Path paramPath) {
    return PLATFORM_SEPARATOR_PATTERN.matcher(paramPath.toString()).replaceAll(Matcher.quoteReplacement("/"));
  }
  
  public static URI toURI(URL paramURL) {
    try {
      return paramURL.toURI();
    } catch (URISyntaxException uRISyntaxException) {
      throw new IllegalArgumentException(uRISyntaxException);
    } 
  }
  
  public static URL toURL(Path paramPath) {
    try {
      return paramPath.toUri().toURL();
    } catch (MalformedURLException malformedURLException) {
      throw new InternalError(malformedURLException);
    } 
  }
  
  public static void transfer(byte[] paramArrayOfbyte, Path paramPath, boolean paramBoolean) throws IOException {
    OutputStream outputStream = newOutput(paramPath, paramBoolean);
    try {
      outputStream.write(paramArrayOfbyte);
      if (outputStream != null)
        outputStream.close(); 
    } catch (Throwable throwable) {
      if (outputStream != null)
        try {
          outputStream.close();
        } catch (Throwable throwable1) {
          throwable.addSuppressed(throwable1);
        }  
      throw throwable;
    } 
  }
  
  public static long transfer(InputStream paramInputStream, OutputStream paramOutputStream) throws IOException {
    long l = 0L;
    byte[] arrayOfByte = newBuffer();
    int i;
    for (i = paramInputStream.read(arrayOfByte); i >= 0; i = paramInputStream.read(arrayOfByte)) {
      paramOutputStream.write(arrayOfByte, 0, i);
      l += i;
    } 
    return l;
  }
  
  public static void transfer(InputStream paramInputStream, Path paramPath) throws IOException {
    transfer(paramInputStream, paramPath, false);
  }
  
  public static long transfer(InputStream paramInputStream, Path paramPath, boolean paramBoolean) throws IOException {
    OutputStream outputStream = newOutput(paramPath, paramBoolean);
    try {
      long l = transfer(paramInputStream, outputStream);
      if (outputStream != null)
        outputStream.close(); 
      return l;
    } catch (Throwable throwable) {
      if (outputStream != null)
        try {
          outputStream.close();
        } catch (Throwable throwable1) {
          throwable.addSuppressed(throwable1);
        }  
      throw throwable;
    } 
  }
  
  public static void transfer(Path paramPath, OutputStream paramOutputStream) throws IOException {
    InputStream inputStream = newInput(paramPath);
    try {
      transfer(inputStream, paramOutputStream);
      if (inputStream != null)
        inputStream.close(); 
    } catch (Throwable throwable) {
      if (inputStream != null)
        try {
          inputStream.close();
        } catch (Throwable throwable1) {
          throwable.addSuppressed(throwable1);
        }  
      throw throwable;
    } 
  }
  
  public static String urlDecode(String paramString) {
    return URLDecoder.decode(paramString, UNICODE_CHARSET);
  }
  
  public static String urlEncode(String paramString) {
    return URLEncoder.encode(paramString, UNICODE_CHARSET);
  }
  
  public static String urlDecodeStrict(String paramString) {
    StringBuilder stringBuilder = new StringBuilder();
    char[] arrayOfChar = paramString.toCharArray();
    for (byte b = 0; b < arrayOfChar.length; b++) {
      char c = arrayOfChar[b];
      if (c != '%') {
        stringBuilder.append(c);
      } else {
        if (b + 2 >= arrayOfChar.length)
          return null; 
        CharBuffer charBuffer = UNICODE_CHARSET.decode(ByteBuffer.wrap(HexFormat.of().parseHex(CharBuffer.wrap(arrayOfChar, b + 1, 2))));
        stringBuilder.append(charBuffer);
        b += 2;
      } 
    } 
    return stringBuilder.toString();
  }
  
  public static String getPathFromUrlFragment(String paramString) {
    return (paramString.indexOf('?') < 0) ? paramString : paramString.substring(0, paramString.indexOf('?'));
  }
  
  public static String verifyFileName(String paramString) {
    return VerifyHelper.<String>verify(paramString, IOHelper::isValidFileName, String.format("Invalid file name: '%s'", new Object[] { paramString }));
  }
  
  public static int verifyLength(int paramInt1, int paramInt2) throws IOException {
    if (paramInt1 < 0 || (paramInt2 < 0 && paramInt1 != -paramInt2) || (paramInt2 > 0 && paramInt1 > paramInt2))
      throw new IOException("Illegal length: " + paramInt1); 
    return paramInt1;
  }
  
  public static BufferedImage verifyTexture(BufferedImage paramBufferedImage, boolean paramBoolean) {
    return VerifyHelper.<BufferedImage>verify(paramBufferedImage, paramBufferedImage -> isValidTextureBounds(paramBufferedImage.getWidth(), paramBufferedImage.getHeight(), paramBoolean), String.format("Invalid texture bounds: %dx%d", new Object[] { Integer.valueOf(paramBufferedImage.getWidth()), Integer.valueOf(paramBufferedImage.getHeight()) }));
  }
  
  public static String verifyURL(String paramString) {
    try {
      new URI(paramString);
      return paramString;
    } catch (URISyntaxException uRISyntaxException) {
      throw new IllegalArgumentException("Invalid URL", uRISyntaxException);
    } 
  }
  
  public static void walk(Path paramPath, FileVisitor<Path> paramFileVisitor, boolean paramBoolean) throws IOException {
    Files.walkFileTree(paramPath, WALK_OPTIONS, 2147483647, paramBoolean ? paramFileVisitor : new SkipHiddenVisitor(paramFileVisitor));
  }
  
  public static void write(Path paramPath, byte[] paramArrayOfbyte) throws IOException {
    createParentDirs(paramPath);
    Files.write(paramPath, paramArrayOfbyte, WRITE_OPTIONS);
  }
  
  public static InputStream nonClosing(InputStream paramInputStream) {
    return new FilterInputStream(paramInputStream) {
        public void close() {}
        
        static {
        
        }
      };
  }
  
  public static OutputStream nonClosing(OutputStream paramOutputStream) {
    return new FilterOutputStream(paramOutputStream) {
        public void write(byte[] param1ArrayOfbyte, int param1Int1, int param1Int2) throws IOException {
          this.out.write(param1ArrayOfbyte, param1Int1, param1Int2);
        }
        
        public void close() {}
        
        static {
        
        }
      };
  }
  
  private static final class DeleteDirVisitor extends SimpleFileVisitor<Path> {
    private final Path dir;
    
    private final boolean self;
    
    private DeleteDirVisitor(Path param1Path, boolean param1Boolean) {
      this.dir = param1Path;
      this.self = param1Boolean;
    }
    
    public FileVisitResult postVisitDirectory(Path param1Path, IOException param1IOException) throws IOException {
      FileVisitResult fileVisitResult = super.postVisitDirectory(param1Path, param1IOException);
      if (this.self || !this.dir.equals(param1Path))
        Files.delete(param1Path); 
      return fileVisitResult;
    }
    
    public FileVisitResult visitFile(Path param1Path, BasicFileAttributes param1BasicFileAttributes) throws IOException {
      Files.delete(param1Path);
      return super.visitFile(param1Path, param1BasicFileAttributes);
    }
  }
  
  private static class MoveFileVisitor implements FileVisitor<Path> {
    private final Path from;
    
    private final Path to;
    
    private MoveFileVisitor(Path param1Path1, Path param1Path2) {
      this.from = param1Path1;
      this.to = param1Path2;
    }
    
    public FileVisitResult preVisitDirectory(Path param1Path, BasicFileAttributes param1BasicFileAttributes) throws IOException {
      Path path = this.to.resolve(this.from.relativize(param1Path));
      if (!IOHelper.isDir(path))
        Files.createDirectories(path, (FileAttribute<?>[])new FileAttribute[0]); 
      return FileVisitResult.CONTINUE;
    }
    
    public FileVisitResult visitFile(Path param1Path, BasicFileAttributes param1BasicFileAttributes) throws IOException {
      Files.move(param1Path, this.to.resolve(this.from.relativize(param1Path)), IOHelper.COPY_OPTIONS);
      return FileVisitResult.CONTINUE;
    }
    
    public FileVisitResult visitFileFailed(Path param1Path, IOException param1IOException) throws IOException {
      throw param1IOException;
    }
    
    public FileVisitResult postVisitDirectory(Path param1Path, IOException param1IOException) throws IOException {
      if (param1IOException != null)
        throw param1IOException; 
      if (!this.from.equals(param1Path))
        Files.delete(param1Path); 
      return FileVisitResult.CONTINUE;
    }
  }
  
  private static final class SkipHiddenVisitor implements FileVisitor<Path> {
    private final FileVisitor<Path> visitor;
    
    private SkipHiddenVisitor(FileVisitor<Path> param1FileVisitor) {
      this.visitor = param1FileVisitor;
    }
    
    public FileVisitResult postVisitDirectory(Path param1Path, IOException param1IOException) throws IOException {
      return Files.isHidden(param1Path) ? FileVisitResult.CONTINUE : this.visitor.postVisitDirectory(param1Path, param1IOException);
    }
    
    public FileVisitResult preVisitDirectory(Path param1Path, BasicFileAttributes param1BasicFileAttributes) throws IOException {
      return Files.isHidden(param1Path) ? FileVisitResult.SKIP_SUBTREE : this.visitor.preVisitDirectory(param1Path, param1BasicFileAttributes);
    }
    
    public FileVisitResult visitFile(Path param1Path, BasicFileAttributes param1BasicFileAttributes) throws IOException {
      return Files.isHidden(param1Path) ? FileVisitResult.CONTINUE : this.visitor.visitFile(param1Path, param1BasicFileAttributes);
    }
    
    public FileVisitResult visitFileFailed(Path param1Path, IOException param1IOException) throws IOException {
      return this.visitor.visitFileFailed(param1Path, param1IOException);
    }
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\pro\gravi\\utils\helper\IOHelper.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */