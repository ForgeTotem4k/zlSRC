package io.sentry;

import io.sentry.clientreport.ClientReport;
import io.sentry.profilemeasurements.ProfileMeasurement;
import io.sentry.profilemeasurements.ProfileMeasurementValue;
import io.sentry.protocol.App;
import io.sentry.protocol.Browser;
import io.sentry.protocol.Contexts;
import io.sentry.protocol.DebugImage;
import io.sentry.protocol.DebugMeta;
import io.sentry.protocol.Device;
import io.sentry.protocol.Geo;
import io.sentry.protocol.Gpu;
import io.sentry.protocol.MeasurementValue;
import io.sentry.protocol.Mechanism;
import io.sentry.protocol.Message;
import io.sentry.protocol.MetricSummary;
import io.sentry.protocol.OperatingSystem;
import io.sentry.protocol.Request;
import io.sentry.protocol.SdkInfo;
import io.sentry.protocol.SdkVersion;
import io.sentry.protocol.SentryException;
import io.sentry.protocol.SentryPackage;
import io.sentry.protocol.SentryRuntime;
import io.sentry.protocol.SentrySpan;
import io.sentry.protocol.SentryStackFrame;
import io.sentry.protocol.SentryStackTrace;
import io.sentry.protocol.SentryThread;
import io.sentry.protocol.SentryTransaction;
import io.sentry.protocol.User;
import io.sentry.protocol.ViewHierarchy;
import io.sentry.protocol.ViewHierarchyNode;
import io.sentry.util.Objects;
import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.charset.Charset;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class JsonSerializer implements ISerializer {
  private static final Charset UTF_8 = Charset.forName("UTF-8");
  
  @NotNull
  private final SentryOptions options;
  
  @NotNull
  private final Map<Class<?>, JsonDeserializer<?>> deserializersByClass;
  
  public JsonSerializer(@NotNull SentryOptions paramSentryOptions) {
    this.options = paramSentryOptions;
    this.deserializersByClass = new HashMap<>();
    this.deserializersByClass.put(App.class, new App.Deserializer());
    this.deserializersByClass.put(Breadcrumb.class, new Breadcrumb.Deserializer());
    this.deserializersByClass.put(Browser.class, new Browser.Deserializer());
    this.deserializersByClass.put(Contexts.class, new Contexts.Deserializer());
    this.deserializersByClass.put(DebugImage.class, new DebugImage.Deserializer());
    this.deserializersByClass.put(DebugMeta.class, new DebugMeta.Deserializer());
    this.deserializersByClass.put(Device.class, new Device.Deserializer());
    this.deserializersByClass.put(Device.DeviceOrientation.class, new Device.DeviceOrientation.Deserializer());
    this.deserializersByClass.put(Gpu.class, new Gpu.Deserializer());
    this.deserializersByClass.put(MeasurementValue.class, new MeasurementValue.Deserializer());
    this.deserializersByClass.put(Mechanism.class, new Mechanism.Deserializer());
    this.deserializersByClass.put(Message.class, new Message.Deserializer());
    this.deserializersByClass.put(MetricSummary.class, new MetricSummary.Deserializer());
    this.deserializersByClass.put(OperatingSystem.class, new OperatingSystem.Deserializer());
    this.deserializersByClass.put(ProfilingTraceData.class, new ProfilingTraceData.Deserializer());
    this.deserializersByClass.put(ProfilingTransactionData.class, new ProfilingTransactionData.Deserializer());
    this.deserializersByClass.put(ProfileMeasurement.class, new ProfileMeasurement.Deserializer());
    this.deserializersByClass.put(ProfileMeasurementValue.class, new ProfileMeasurementValue.Deserializer());
    this.deserializersByClass.put(Request.class, new Request.Deserializer());
    this.deserializersByClass.put(SdkInfo.class, new SdkInfo.Deserializer());
    this.deserializersByClass.put(SdkVersion.class, new SdkVersion.Deserializer());
    this.deserializersByClass.put(SentryEnvelopeHeader.class, new SentryEnvelopeHeader.Deserializer());
    this.deserializersByClass.put(SentryEnvelopeItemHeader.class, new SentryEnvelopeItemHeader.Deserializer());
    this.deserializersByClass.put(SentryEvent.class, new SentryEvent.Deserializer());
    this.deserializersByClass.put(SentryException.class, new SentryException.Deserializer());
    this.deserializersByClass.put(SentryItemType.class, new SentryItemType.Deserializer());
    this.deserializersByClass.put(SentryLevel.class, new SentryLevel.Deserializer());
    this.deserializersByClass.put(SentryLockReason.class, new SentryLockReason.Deserializer());
    this.deserializersByClass.put(SentryPackage.class, new SentryPackage.Deserializer());
    this.deserializersByClass.put(SentryRuntime.class, new SentryRuntime.Deserializer());
    this.deserializersByClass.put(SentrySpan.class, new SentrySpan.Deserializer());
    this.deserializersByClass.put(SentryStackFrame.class, new SentryStackFrame.Deserializer());
    this.deserializersByClass.put(SentryStackTrace.class, new SentryStackTrace.Deserializer());
    this.deserializersByClass.put(SentryAppStartProfilingOptions.class, new SentryAppStartProfilingOptions.Deserializer());
    this.deserializersByClass.put(SentryThread.class, new SentryThread.Deserializer());
    this.deserializersByClass.put(SentryTransaction.class, new SentryTransaction.Deserializer());
    this.deserializersByClass.put(Session.class, new Session.Deserializer());
    this.deserializersByClass.put(SpanContext.class, new SpanContext.Deserializer());
    this.deserializersByClass.put(SpanId.class, new SpanId.Deserializer());
    this.deserializersByClass.put(SpanStatus.class, new SpanStatus.Deserializer());
    this.deserializersByClass.put(User.class, new User.Deserializer());
    this.deserializersByClass.put(Geo.class, new Geo.Deserializer());
    this.deserializersByClass.put(UserFeedback.class, new UserFeedback.Deserializer());
    this.deserializersByClass.put(ClientReport.class, new ClientReport.Deserializer());
    this.deserializersByClass.put(ViewHierarchyNode.class, new ViewHierarchyNode.Deserializer());
    this.deserializersByClass.put(ViewHierarchy.class, new ViewHierarchy.Deserializer());
  }
  
  @Nullable
  public <T, R> T deserializeCollection(@NotNull Reader paramReader, @NotNull Class<T> paramClass, @Nullable JsonDeserializer<R> paramJsonDeserializer) {
    try {
      JsonObjectReader jsonObjectReader = new JsonObjectReader(paramReader);
      try {
        if (Collection.class.isAssignableFrom(paramClass)) {
          if (paramJsonDeserializer == null) {
            Object object1 = jsonObjectReader.nextObjectOrNull();
            jsonObjectReader.close();
            return (T)object1;
          } 
          List<R> list = jsonObjectReader.nextListOrNull(this.options.getLogger(), paramJsonDeserializer);
          jsonObjectReader.close();
          return (T)list;
        } 
        Object object = jsonObjectReader.nextObjectOrNull();
        jsonObjectReader.close();
        return (T)object;
      } catch (Throwable throwable) {
        try {
          jsonObjectReader.close();
        } catch (Throwable throwable1) {
          throwable.addSuppressed(throwable1);
        } 
        throw throwable;
      } 
    } catch (Throwable throwable) {
      this.options.getLogger().log(SentryLevel.ERROR, "Error when deserializing", throwable);
      return null;
    } 
  }
  
  @Nullable
  public <T> T deserialize(@NotNull Reader paramReader, @NotNull Class<T> paramClass) {
    try {
      JsonObjectReader jsonObjectReader = new JsonObjectReader(paramReader);
      try {
        JsonDeserializer<Object> jsonDeserializer = (JsonDeserializer)this.deserializersByClass.get(paramClass);
        if (jsonDeserializer != null) {
          Object object = jsonDeserializer.deserialize(jsonObjectReader, this.options.getLogger());
          T t1 = paramClass.cast(object);
          jsonObjectReader.close();
          return t1;
        } 
        if (isKnownPrimitive(paramClass)) {
          Object object = jsonObjectReader.nextObjectOrNull();
          jsonObjectReader.close();
          return (T)object;
        } 
        T t = null;
        jsonObjectReader.close();
        return t;
      } catch (Throwable throwable) {
        try {
          jsonObjectReader.close();
        } catch (Throwable throwable1) {
          throwable.addSuppressed(throwable1);
        } 
        throw throwable;
      } 
    } catch (Exception exception) {
      this.options.getLogger().log(SentryLevel.ERROR, "Error when deserializing", exception);
      return null;
    } 
  }
  
  @Nullable
  public SentryEnvelope deserializeEnvelope(@NotNull InputStream paramInputStream) {
    Objects.requireNonNull(paramInputStream, "The InputStream object is required.");
    try {
      return this.options.getEnvelopeReader().read(paramInputStream);
    } catch (IOException iOException) {
      this.options.getLogger().log(SentryLevel.ERROR, "Error deserializing envelope.", iOException);
      return null;
    } 
  }
  
  public <T> void serialize(@NotNull T paramT, @NotNull Writer paramWriter) throws IOException {
    Objects.requireNonNull(paramT, "The entity is required.");
    Objects.requireNonNull(paramWriter, "The Writer object is required.");
    if (this.options.getLogger().isEnabled(SentryLevel.DEBUG)) {
      String str = serializeToString(paramT, this.options.isEnablePrettySerializationOutput());
      this.options.getLogger().log(SentryLevel.DEBUG, "Serializing object: %s", new Object[] { str });
    } 
    JsonObjectWriter jsonObjectWriter = new JsonObjectWriter(paramWriter, this.options.getMaxDepth());
    jsonObjectWriter.value(this.options.getLogger(), paramT);
    paramWriter.flush();
  }
  
  public void serialize(@NotNull SentryEnvelope paramSentryEnvelope, @NotNull OutputStream paramOutputStream) throws Exception {
    Objects.requireNonNull(paramSentryEnvelope, "The SentryEnvelope object is required.");
    Objects.requireNonNull(paramOutputStream, "The Stream object is required.");
    BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(paramOutputStream);
    BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(bufferedOutputStream, UTF_8));
    try {
      paramSentryEnvelope.getHeader().serialize(new JsonObjectWriter(bufferedWriter, this.options.getMaxDepth()), this.options.getLogger());
      bufferedWriter.write("\n");
      for (SentryEnvelopeItem sentryEnvelopeItem : paramSentryEnvelope.getItems()) {
        try {
          byte[] arrayOfByte = sentryEnvelopeItem.getData();
          sentryEnvelopeItem.getHeader().serialize(new JsonObjectWriter(bufferedWriter, this.options.getMaxDepth()), this.options.getLogger());
          bufferedWriter.write("\n");
          bufferedWriter.flush();
          paramOutputStream.write(arrayOfByte);
          bufferedWriter.write("\n");
        } catch (Exception exception) {
          this.options.getLogger().log(SentryLevel.ERROR, "Failed to create envelope item. Dropping it.", exception);
        } 
      } 
    } finally {
      bufferedWriter.flush();
    } 
  }
  
  @NotNull
  public String serialize(@NotNull Map<String, Object> paramMap) throws Exception {
    return serializeToString(paramMap, false);
  }
  
  @NotNull
  private String serializeToString(Object paramObject, boolean paramBoolean) throws IOException {
    StringWriter stringWriter = new StringWriter();
    JsonObjectWriter jsonObjectWriter = new JsonObjectWriter(stringWriter, this.options.getMaxDepth());
    if (paramBoolean)
      jsonObjectWriter.setIndent("\t"); 
    jsonObjectWriter.value(this.options.getLogger(), paramObject);
    return stringWriter.toString();
  }
  
  private <T> boolean isKnownPrimitive(@NotNull Class<T> paramClass) {
    return (paramClass.isArray() || Collection.class.isAssignableFrom(paramClass) || String.class.isAssignableFrom(paramClass) || Map.class.isAssignableFrom(paramClass));
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\io\sentry\JsonSerializer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */