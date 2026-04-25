package com.sun.jna;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.nio.Buffer;
import java.util.AbstractCollection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class Structure {
  private static final Logger LOG = Logger.getLogger(Structure.class.getName());
  
  public static final int ALIGN_DEFAULT = 0;
  
  public static final int ALIGN_NONE = 1;
  
  public static final int ALIGN_GNUC = 2;
  
  public static final int ALIGN_MSVC = 3;
  
  protected static final int CALCULATE_SIZE = -1;
  
  static final ReentrantReadWriteLock layoutInfoLock = new ReentrantReadWriteLock();
  
  static final ReentrantReadWriteLock fieldOrderLock = new ReentrantReadWriteLock();
  
  static final ReentrantReadWriteLock fieldListLock = new ReentrantReadWriteLock();
  
  static final ReentrantReadWriteLock validationLock = new ReentrantReadWriteLock();
  
  static final Map<Class<?>, LayoutInfo> layoutInfo = new WeakHashMap<>();
  
  static final Map<Class<?>, List<String>> fieldOrder = new WeakHashMap<>();
  
  static final Map<Class<?>, List<Field>> fieldList = new WeakHashMap<>();
  
  static final Map<Class<?>, Boolean> validationMap = new WeakHashMap<>();
  
  private Pointer memory;
  
  private int size = -1;
  
  private int alignType;
  
  private String encoding;
  
  private int actualAlignType;
  
  private int structAlignment;
  
  private Map<String, StructField> structFields;
  
  private final Map<String, NativeStringTracking> nativeStrings = new HashMap<>(8);
  
  private TypeMapper typeMapper;
  
  private long typeInfo;
  
  private boolean autoRead = true;
  
  private boolean autoWrite = true;
  
  private Structure[] array;
  
  private boolean readCalled;
  
  private static final ThreadLocal<Map<Pointer, Structure>> reads = new ThreadLocal<Map<Pointer, Structure>>() {
      protected synchronized Map<Pointer, Structure> initialValue() {
        return new HashMap<>();
      }
    };
  
  private static final ThreadLocal<Set<Structure>> busy = new ThreadLocal<Set<Structure>>() {
      protected synchronized Set<Structure> initialValue() {
        return new Structure.StructureSet();
      }
    };
  
  private static final Pointer PLACEHOLDER_MEMORY = new Pointer(0L) {
      public Pointer share(long param1Long1, long param1Long2) {
        return this;
      }
    };
  
  protected Structure() {
    this(0);
  }
  
  protected Structure(TypeMapper paramTypeMapper) {
    this(null, 0, paramTypeMapper);
  }
  
  protected Structure(int paramInt) {
    this((Pointer)null, paramInt);
  }
  
  protected Structure(int paramInt, TypeMapper paramTypeMapper) {
    this(null, paramInt, paramTypeMapper);
  }
  
  protected Structure(Pointer paramPointer) {
    this(paramPointer, 0);
  }
  
  protected Structure(Pointer paramPointer, int paramInt) {
    this(paramPointer, paramInt, null);
  }
  
  protected Structure(Pointer paramPointer, int paramInt, TypeMapper paramTypeMapper) {
    setAlignType(paramInt);
    setStringEncoding(Native.getStringEncoding(getClass()));
    initializeTypeMapper(paramTypeMapper);
    validateFields();
    if (paramPointer != null) {
      useMemory(paramPointer, 0, true);
    } else {
      allocateMemory(-1);
    } 
    initializeFields();
  }
  
  Map<String, StructField> fields() {
    return this.structFields;
  }
  
  TypeMapper getTypeMapper() {
    return this.typeMapper;
  }
  
  private void initializeTypeMapper(TypeMapper paramTypeMapper) {
    if (paramTypeMapper == null)
      paramTypeMapper = Native.getTypeMapper(getClass()); 
    this.typeMapper = paramTypeMapper;
    layoutChanged();
  }
  
  private void layoutChanged() {
    if (this.size != -1) {
      this.size = -1;
      if (this.memory instanceof AutoAllocated)
        this.memory = null; 
      ensureAllocated();
    } 
  }
  
  protected void setStringEncoding(String paramString) {
    this.encoding = paramString;
  }
  
  protected String getStringEncoding() {
    return this.encoding;
  }
  
  protected void setAlignType(int paramInt) {
    this.alignType = paramInt;
    if (paramInt == 0) {
      paramInt = Native.getStructureAlignment(getClass());
      if (paramInt == 0)
        if (Platform.isWindows()) {
          paramInt = 3;
        } else {
          paramInt = 2;
        }  
    } 
    this.actualAlignType = paramInt;
    layoutChanged();
  }
  
  protected Memory autoAllocate(int paramInt) {
    return new AutoAllocated(paramInt);
  }
  
  protected void useMemory(Pointer paramPointer) {
    useMemory(paramPointer, 0);
  }
  
  protected void useMemory(Pointer paramPointer, int paramInt) {
    useMemory(paramPointer, paramInt, false);
  }
  
  void useMemory(Pointer paramPointer, int paramInt, boolean paramBoolean) {
    try {
      this.nativeStrings.clear();
      if (this instanceof ByValue && !paramBoolean) {
        byte[] arrayOfByte = new byte[size()];
        paramPointer.read(0L, arrayOfByte, 0, arrayOfByte.length);
        this.memory.write(0L, arrayOfByte, 0, arrayOfByte.length);
      } else {
        if (this.size == -1)
          this.size = calculateSize(false); 
        if (this.size != -1) {
          this.memory = paramPointer.share(paramInt, this.size);
        } else {
          this.memory = paramPointer.share(paramInt);
        } 
      } 
      this.array = null;
      this.readCalled = false;
    } catch (IndexOutOfBoundsException indexOutOfBoundsException) {
      throw new IllegalArgumentException("Structure exceeds provided memory bounds", indexOutOfBoundsException);
    } 
  }
  
  protected void ensureAllocated() {
    ensureAllocated(false);
  }
  
  private void ensureAllocated(boolean paramBoolean) {
    if (this.memory == null) {
      allocateMemory(paramBoolean);
    } else if (this.size == -1) {
      this.size = calculateSize(true, paramBoolean);
      if (!(this.memory instanceof AutoAllocated))
        try {
          this.memory = this.memory.share(0L, this.size);
        } catch (IndexOutOfBoundsException indexOutOfBoundsException) {
          throw new IllegalArgumentException("Structure exceeds provided memory bounds", indexOutOfBoundsException);
        }  
    } 
  }
  
  protected void allocateMemory() {
    allocateMemory(false);
  }
  
  private void allocateMemory(boolean paramBoolean) {
    allocateMemory(calculateSize(true, paramBoolean));
  }
  
  protected void allocateMemory(int paramInt) {
    if (paramInt == -1) {
      paramInt = calculateSize(false);
    } else if (paramInt <= 0) {
      throw new IllegalArgumentException("Structure size must be greater than zero: " + paramInt);
    } 
    if (paramInt != -1) {
      if (this.memory == null || this.memory instanceof AutoAllocated)
        this.memory = autoAllocate(paramInt); 
      this.size = paramInt;
    } 
  }
  
  public int size() {
    ensureAllocated();
    return this.size;
  }
  
  public void clear() {
    ensureAllocated();
    this.nativeStrings.clear();
    this.memory.clear(size());
  }
  
  public Pointer getPointer() {
    ensureAllocated();
    return this.memory;
  }
  
  static Set<Structure> busy() {
    return busy.get();
  }
  
  static Map<Pointer, Structure> reading() {
    return reads.get();
  }
  
  void conditionalAutoRead() {
    if (!this.readCalled)
      autoRead(); 
  }
  
  public void read() {
    if (this.memory == PLACEHOLDER_MEMORY)
      return; 
    this.readCalled = true;
    ensureAllocated();
    if (!busy().add(this))
      return; 
    if (this instanceof ByReference)
      reading().put(getPointer(), this); 
    try {
      for (StructField structField : fields().values())
        readField(structField); 
    } finally {
      busy().remove(this);
      if (this instanceof ByReference && reading().get(getPointer()) == this)
        reading().remove(getPointer()); 
    } 
  }
  
  protected int fieldOffset(String paramString) {
    ensureAllocated();
    StructField structField = fields().get(paramString);
    if (structField == null)
      throw new IllegalArgumentException("No such field: " + paramString); 
    return structField.offset;
  }
  
  public Object readField(String paramString) {
    ensureAllocated();
    StructField structField = fields().get(paramString);
    if (structField == null)
      throw new IllegalArgumentException("No such field: " + paramString); 
    return readField(structField);
  }
  
  Object getFieldValue(Field paramField) {
    try {
      return paramField.get(this);
    } catch (Exception exception) {
      throw new Error("Exception reading field '" + paramField.getName() + "' in " + getClass(), exception);
    } 
  }
  
  void setFieldValue(Field paramField, Object paramObject) {
    setFieldValue(paramField, paramObject, false);
  }
  
  private void setFieldValue(Field paramField, Object paramObject, boolean paramBoolean) {
    try {
      paramField.set(this, paramObject);
    } catch (IllegalAccessException illegalAccessException) {
      int i = paramField.getModifiers();
      if (Modifier.isFinal(i)) {
        if (paramBoolean)
          throw new UnsupportedOperationException("This VM does not support Structures with final fields (field '" + paramField.getName() + "' within " + getClass() + ")", illegalAccessException); 
        throw new UnsupportedOperationException("Attempt to write to read-only field '" + paramField.getName() + "' within " + getClass(), illegalAccessException);
      } 
      throw new Error("Unexpectedly unable to write to field '" + paramField.getName() + "' within " + getClass(), illegalAccessException);
    } 
  }
  
  static <T extends Structure> T updateStructureByReference(Class<T> paramClass, T paramT, Pointer paramPointer) {
    if (paramPointer == null) {
      paramT = null;
    } else if (paramT == null || !paramPointer.equals(paramT.getPointer())) {
      Structure structure = reading().get(paramPointer);
      if (structure != null && paramClass.equals(structure.getClass())) {
        Structure structure1 = structure;
        structure1.autoRead();
      } else {
        paramT = newInstance(paramClass, paramPointer);
        paramT.conditionalAutoRead();
      } 
    } else {
      paramT.autoRead();
    } 
    return paramT;
  }
  
  protected Object readField(StructField paramStructField) {
    Object object2;
    int i = paramStructField.offset;
    Class<?> clazz = paramStructField.type;
    FromNativeConverter fromNativeConverter = paramStructField.readConverter;
    if (fromNativeConverter != null)
      clazz = fromNativeConverter.nativeType(); 
    Object object1 = (Structure.class.isAssignableFrom(clazz) || Callback.class.isAssignableFrom(clazz) || (Platform.HAS_BUFFERS && Buffer.class.isAssignableFrom(clazz)) || Pointer.class.isAssignableFrom(clazz) || NativeMapped.class.isAssignableFrom(clazz) || clazz.isArray()) ? getFieldValue(paramStructField.field) : null;
    if (clazz == String.class) {
      Pointer pointer = this.memory.getPointer(i);
      object2 = (pointer == null) ? null : pointer.getString(0L, this.encoding);
    } else {
      object2 = this.memory.getValue(i, clazz, object1);
    } 
    if (fromNativeConverter != null) {
      object2 = fromNativeConverter.fromNative(object2, paramStructField.context);
      if (object1 != null && object1.equals(object2))
        object2 = object1; 
    } 
    if (clazz.equals(String.class) || clazz.equals(WString.class))
      if (object2 != null) {
        NativeStringTracking nativeStringTracking1 = new NativeStringTracking(object2);
        NativeStringTracking nativeStringTracking2 = this.nativeStrings.put(paramStructField.name, nativeStringTracking1);
        if (nativeStringTracking2 != null)
          nativeStringTracking1.peer = nativeStringTracking2.peer; 
      } else {
        this.nativeStrings.remove(paramStructField.name);
      }  
    setFieldValue(paramStructField.field, object2, true);
    return object2;
  }
  
  public void write() {
    if (this.memory == PLACEHOLDER_MEMORY)
      return; 
    ensureAllocated();
    if (this instanceof ByValue)
      getTypeInfo(); 
    if (!busy().add(this))
      return; 
    try {
      for (StructField structField : fields().values()) {
        if (!structField.isVolatile)
          writeField(structField); 
      } 
    } finally {
      busy().remove(this);
    } 
  }
  
  public void writeField(String paramString) {
    ensureAllocated();
    StructField structField = fields().get(paramString);
    if (structField == null)
      throw new IllegalArgumentException("No such field: " + paramString); 
    writeField(structField);
  }
  
  public void writeField(String paramString, Object paramObject) {
    ensureAllocated();
    StructField structField = fields().get(paramString);
    if (structField == null)
      throw new IllegalArgumentException("No such field: " + paramString); 
    setFieldValue(structField.field, paramObject);
    writeField(structField, paramObject);
  }
  
  protected void writeField(StructField paramStructField) {
    if (paramStructField.isReadOnly)
      return; 
    Object object = getFieldValue(paramStructField.field);
    writeField(paramStructField, object);
  }
  
  private void writeField(StructField paramStructField, Object paramObject) {
    int i = paramStructField.offset;
    Class<?> clazz = paramStructField.type;
    ToNativeConverter toNativeConverter = paramStructField.writeConverter;
    if (toNativeConverter != null) {
      paramObject = toNativeConverter.toNative(paramObject, new StructureWriteContext(this, paramStructField.field));
      clazz = toNativeConverter.nativeType();
    } 
    if (String.class == clazz || WString.class == clazz)
      if (paramObject != null) {
        NativeStringTracking nativeStringTracking1 = new NativeStringTracking(paramObject);
        NativeStringTracking nativeStringTracking2 = this.nativeStrings.put(paramStructField.name, nativeStringTracking1);
        if (nativeStringTracking2 != null && paramObject.equals(nativeStringTracking2.value)) {
          nativeStringTracking1.peer = nativeStringTracking2.peer;
          return;
        } 
        boolean bool = (clazz == WString.class) ? true : false;
        NativeString nativeString = bool ? new NativeString(paramObject.toString(), true) : new NativeString(paramObject.toString(), this.encoding);
        nativeStringTracking1.peer = nativeString;
        paramObject = nativeString.getPointer();
      } else {
        this.nativeStrings.remove(paramStructField.name);
      }  
    try {
      this.memory.setValue(i, paramObject, clazz);
    } catch (IllegalArgumentException illegalArgumentException) {
      String str = "Structure field \"" + paramStructField.name + "\" was declared as " + paramStructField.type + ((paramStructField.type == clazz) ? "" : (" (native type " + clazz + ")")) + ", which is not supported within a Structure";
      throw new IllegalArgumentException(str, illegalArgumentException);
    } 
  }
  
  protected List<String> getFieldOrder() {
    LinkedList<? extends String> linkedList = new LinkedList();
    for (Class<?> clazz = getClass(); clazz != Structure.class; clazz = clazz.getSuperclass()) {
      FieldOrder fieldOrder = clazz.<FieldOrder>getAnnotation(FieldOrder.class);
      if (fieldOrder != null)
        linkedList.addAll(0, Arrays.asList(fieldOrder.value())); 
    } 
    return Collections.unmodifiableList(linkedList);
  }
  
  protected void sortFields(List<Field> paramList, List<String> paramList1) {
    for (byte b = 0; b < paramList1.size(); b++) {
      String str = paramList1.get(b);
      for (byte b1 = 0; b1 < paramList.size(); b1++) {
        Field field = paramList.get(b1);
        if (str.equals(field.getName())) {
          Collections.swap(paramList, b, b1);
          break;
        } 
      } 
    } 
  }
  
  protected List<Field> getFieldList() {
    Class<?> clazz = getClass();
    fieldListLock.readLock().lock();
    try {
      List<Field> list = fieldList.get(clazz);
      if (list != null)
        return list; 
    } finally {
      fieldListLock.readLock().unlock();
    } 
    fieldListLock.writeLock().lock();
    try {
      return fieldList.computeIfAbsent(clazz, paramClass2 -> {
            ArrayList<Field> arrayList1 = new ArrayList();
            ArrayList<Field> arrayList2 = new ArrayList();
            for (Class clazz = paramClass1; !clazz.equals(Structure.class); clazz = clazz.getSuperclass()) {
              for (Field field : clazz.getDeclaredFields()) {
                int i = field.getModifiers();
                if (!Modifier.isStatic(i) && Modifier.isPublic(i))
                  arrayList2.add(field); 
              } 
              arrayList1.addAll(0, arrayList2);
              arrayList2.clear();
            } 
            return arrayList1;
          });
    } finally {
      fieldListLock.writeLock().unlock();
    } 
  }
  
  private List<String> fieldOrder() {
    Class<?> clazz = getClass();
    fieldOrderLock.readLock().lock();
    try {
      List<String> list = fieldOrder.get(clazz);
      if (list != null)
        return list; 
    } finally {
      fieldOrderLock.readLock().unlock();
    } 
    fieldOrderLock.writeLock().lock();
    try {
      return fieldOrder.computeIfAbsent(clazz, paramClass -> getFieldOrder());
    } finally {
      fieldOrderLock.writeLock().unlock();
    } 
  }
  
  public static List<String> createFieldsOrder(List<String> paramList, String... paramVarArgs) {
    return createFieldsOrder(paramList, Arrays.asList(paramVarArgs));
  }
  
  public static List<String> createFieldsOrder(List<String> paramList1, List<String> paramList2) {
    ArrayList<String> arrayList = new ArrayList(paramList1.size() + paramList2.size());
    arrayList.addAll(paramList1);
    arrayList.addAll(paramList2);
    return Collections.unmodifiableList(arrayList);
  }
  
  public static List<String> createFieldsOrder(String paramString) {
    return Collections.unmodifiableList(Collections.singletonList(paramString));
  }
  
  public static List<String> createFieldsOrder(String... paramVarArgs) {
    return Collections.unmodifiableList(Arrays.asList(paramVarArgs));
  }
  
  private static <T extends Comparable<T>> List<T> sort(Collection<? extends T> paramCollection) {
    ArrayList<T> arrayList = new ArrayList<>(paramCollection);
    Collections.sort(arrayList);
    return arrayList;
  }
  
  protected List<Field> getFields(boolean paramBoolean) {
    List<Field> list = getFieldList();
    HashSet<String> hashSet1 = new HashSet();
    for (Field field : list)
      hashSet1.add(field.getName()); 
    List<String> list1 = fieldOrder();
    if (list1.size() != list.size() && list.size() > 1) {
      if (paramBoolean)
        throw new Error("Structure.getFieldOrder() on " + getClass() + ((list1.size() < list.size()) ? " does not provide enough" : " provides too many") + " names [" + list1.size() + "] (" + sort(list1) + ") to match declared fields [" + list.size() + "] (" + sort(hashSet1) + ")"); 
      return null;
    } 
    HashSet<String> hashSet2 = new HashSet<>(list1);
    if (!hashSet2.equals(hashSet1))
      throw new Error("Structure.getFieldOrder() on " + getClass() + " returns names (" + sort(list1) + ") which do not match declared field names (" + sort(hashSet1) + ")"); 
    sortFields(list, list1);
    return list;
  }
  
  protected int calculateSize(boolean paramBoolean) {
    return calculateSize(paramBoolean, false);
  }
  
  static int size(Class<? extends Structure> paramClass) {
    return size(paramClass, null);
  }
  
  static <T extends Structure> int size(Class<T> paramClass, T paramT) {
    LayoutInfo layoutInfo;
    layoutInfoLock.readLock().lock();
    try {
      layoutInfo = layoutInfo.get(paramClass);
    } finally {
      layoutInfoLock.readLock().unlock();
    } 
    int i = (layoutInfo != null && !layoutInfo.variable) ? layoutInfo.size : -1;
    if (i == -1) {
      if (paramT == null)
        paramT = newInstance(paramClass, PLACEHOLDER_MEMORY); 
      i = paramT.size();
    } 
    return i;
  }
  
  int calculateSize(boolean paramBoolean1, boolean paramBoolean2) {
    LayoutInfo layoutInfo;
    int i = -1;
    Class<?> clazz = getClass();
    layoutInfoLock.readLock().lock();
    try {
      layoutInfo = layoutInfo.get(clazz);
    } finally {
      layoutInfoLock.readLock().unlock();
    } 
    if (layoutInfo == null || this.alignType != layoutInfo.alignType || this.typeMapper != layoutInfo.typeMapper)
      layoutInfo = deriveLayout(paramBoolean1, paramBoolean2); 
    if (layoutInfo != null) {
      this.structAlignment = layoutInfo.alignment;
      this.structFields = layoutInfo.fields;
      if (!layoutInfo.variable) {
        layoutInfoLock.readLock().lock();
        try {
          if (!layoutInfo.containsKey(clazz) || this.alignType != 0 || this.typeMapper != null) {
            layoutInfoLock.readLock().unlock();
            layoutInfoLock.writeLock().lock();
            layoutInfo.put(clazz, layoutInfo);
            layoutInfoLock.readLock().lock();
            layoutInfoLock.writeLock().unlock();
          } 
        } finally {
          layoutInfoLock.readLock().unlock();
        } 
      } 
      i = layoutInfo.size;
    } 
    return i;
  }
  
  private void validateField(String paramString, Class<?> paramClass) {
    if (this.typeMapper != null) {
      ToNativeConverter toNativeConverter = this.typeMapper.getToNativeConverter(paramClass);
      if (toNativeConverter != null) {
        validateField(paramString, toNativeConverter.nativeType());
        return;
      } 
    } 
    if (paramClass.isArray()) {
      validateField(paramString, paramClass.getComponentType());
    } else {
      try {
        getNativeSize(paramClass);
      } catch (IllegalArgumentException illegalArgumentException) {
        String str = "Invalid Structure field in " + getClass() + ", field name '" + paramString + "' (" + paramClass + "): " + illegalArgumentException.getMessage();
        throw new IllegalArgumentException(str, illegalArgumentException);
      } 
    } 
  }
  
  private void validateFields() {
    validationLock.readLock().lock();
    try {
      if (validationMap.containsKey(getClass()))
        return; 
    } finally {
      validationLock.readLock().unlock();
    } 
    validationLock.writeLock().lock();
    try {
      validationMap.computeIfAbsent(getClass(), paramClass -> {
            for (Field field : getFieldList())
              validateField(field.getName(), field.getType()); 
            return Boolean.valueOf(true);
          });
    } finally {
      validationLock.writeLock().unlock();
    } 
  }
  
  private LayoutInfo deriveLayout(boolean paramBoolean1, boolean paramBoolean2) {
    int i = 0;
    List<Field> list = getFields(paramBoolean1);
    if (list == null)
      return null; 
    LayoutInfo layoutInfo = new LayoutInfo();
    layoutInfo.alignType = this.alignType;
    layoutInfo.typeMapper = this.typeMapper;
    boolean bool = true;
    for (Field field : list) {
      int j = field.getModifiers();
      Class<?> clazz = field.getType();
      if (clazz.isArray())
        layoutInfo.variable = true; 
      StructField structField = new StructField();
      structField.isVolatile = Modifier.isVolatile(j);
      structField.isReadOnly = Modifier.isFinal(j);
      if (structField.isReadOnly) {
        if (!Platform.RO_FIELDS)
          throw new IllegalArgumentException("This VM does not support read-only fields (field '" + field.getName() + "' within " + getClass() + ")"); 
        field.setAccessible(true);
      } 
      structField.field = field;
      structField.name = field.getName();
      structField.type = clazz;
      if (Callback.class.isAssignableFrom(clazz) && !clazz.isInterface())
        throw new IllegalArgumentException("Structure Callback field '" + field.getName() + "' must be an interface"); 
      if (clazz.isArray() && Structure.class.equals(clazz.getComponentType())) {
        String str = "Nested Structure arrays must use a derived Structure type so that the size of the elements can be determined";
        throw new IllegalArgumentException(str);
      } 
      int k = 1;
      if (Modifier.isPublic(field.getModifiers())) {
        Object object = getFieldValue(structField.field);
        if (object == null && clazz.isArray()) {
          if (paramBoolean1)
            throw new IllegalStateException("Array fields must be initialized"); 
          return null;
        } 
        Class<?> clazz1 = clazz;
        if (NativeMapped.class.isAssignableFrom(clazz)) {
          NativeMappedConverter nativeMappedConverter = NativeMappedConverter.getInstance(clazz);
          clazz1 = nativeMappedConverter.nativeType();
          structField.writeConverter = nativeMappedConverter;
          structField.readConverter = nativeMappedConverter;
          structField.context = new StructureReadContext(this, field);
        } else if (this.typeMapper != null) {
          ToNativeConverter toNativeConverter = this.typeMapper.getToNativeConverter(clazz);
          FromNativeConverter fromNativeConverter = this.typeMapper.getFromNativeConverter(clazz);
          if (toNativeConverter != null && fromNativeConverter != null) {
            object = toNativeConverter.toNative(object, new StructureWriteContext(this, structField.field));
            clazz1 = (object != null) ? object.getClass() : Pointer.class;
            structField.writeConverter = toNativeConverter;
            structField.readConverter = fromNativeConverter;
            structField.context = new StructureReadContext(this, field);
          } else if (toNativeConverter != null || fromNativeConverter != null) {
            String str = "Structures require bidirectional type conversion for " + clazz;
            throw new IllegalArgumentException(str);
          } 
        } 
        if (object == null)
          object = initializeField(structField.field, clazz); 
        try {
          structField.size = getNativeSize(clazz1, object);
          k = getNativeAlignment(clazz1, object, bool);
        } catch (IllegalArgumentException illegalArgumentException) {
          if (!paramBoolean1 && this.typeMapper == null)
            return null; 
          String str = "Invalid Structure field in " + getClass() + ", field name '" + structField.name + "' (" + structField.type + "): " + illegalArgumentException.getMessage();
          throw new IllegalArgumentException(str, illegalArgumentException);
        } 
        if (k == 0)
          throw new Error("Field alignment is zero for field '" + structField.name + "' within " + getClass()); 
        layoutInfo.alignment = Math.max(layoutInfo.alignment, k);
        if (i % k != 0)
          i += k - i % k; 
        if (this instanceof Union) {
          structField.offset = 0;
          i = Math.max(i, structField.size);
        } else {
          structField.offset = i;
          i += structField.size;
        } 
        layoutInfo.fields.put(structField.name, structField);
      } 
      bool = false;
    } 
    if (i > 0) {
      int j = addPadding(i, layoutInfo.alignment);
      if (this instanceof ByValue && !paramBoolean2)
        getTypeInfo(); 
      layoutInfo.size = j;
      return layoutInfo;
    } 
    throw new IllegalArgumentException("Structure " + getClass() + " has unknown or zero size (ensure all fields are public)");
  }
  
  private void initializeFields() {
    List<Field> list = getFieldList();
    for (Field field : list) {
      try {
        Object object = field.get(this);
        if (object == null)
          initializeField(field, field.getType()); 
      } catch (Exception exception) {
        throw new Error("Exception reading field '" + field.getName() + "' in " + getClass(), exception);
      } 
    } 
  }
  
  private Object initializeField(Field paramField, Class<?> paramClass) {
    NativeMapped nativeMapped = null;
    if (Structure.class.isAssignableFrom(paramClass) && !ByReference.class.isAssignableFrom(paramClass)) {
      try {
        nativeMapped = newInstance((Class)paramClass, PLACEHOLDER_MEMORY);
        setFieldValue(paramField, nativeMapped);
      } catch (IllegalArgumentException illegalArgumentException) {
        String str = "Can't determine size of nested structure";
        throw new IllegalArgumentException(str, illegalArgumentException);
      } 
    } else if (NativeMapped.class.isAssignableFrom(paramClass)) {
      NativeMappedConverter nativeMappedConverter = NativeMappedConverter.getInstance(paramClass);
      nativeMapped = nativeMappedConverter.defaultValue();
      setFieldValue(paramField, nativeMapped);
    } 
    return nativeMapped;
  }
  
  private int addPadding(int paramInt) {
    return addPadding(paramInt, this.structAlignment);
  }
  
  private int addPadding(int paramInt1, int paramInt2) {
    if (this.actualAlignType != 1 && paramInt1 % paramInt2 != 0)
      paramInt1 += paramInt2 - paramInt1 % paramInt2; 
    return paramInt1;
  }
  
  protected int getStructAlignment() {
    if (this.size == -1)
      calculateSize(true); 
    return this.structAlignment;
  }
  
  protected int getNativeAlignment(Class<?> paramClass, Object paramObject, boolean paramBoolean) {
    int i = 1;
    if (NativeMapped.class.isAssignableFrom(paramClass)) {
      NativeMappedConverter nativeMappedConverter = NativeMappedConverter.getInstance(paramClass);
      paramClass = nativeMappedConverter.nativeType();
      paramObject = nativeMappedConverter.toNative(paramObject, new ToNativeContext());
    } 
    int j = Native.getNativeSize(paramClass, paramObject);
    if (paramClass.isPrimitive() || Long.class == paramClass || Integer.class == paramClass || Short.class == paramClass || Character.class == paramClass || Byte.class == paramClass || Boolean.class == paramClass || Float.class == paramClass || Double.class == paramClass) {
      i = j;
    } else if ((Pointer.class.isAssignableFrom(paramClass) && !Function.class.isAssignableFrom(paramClass)) || (Platform.HAS_BUFFERS && Buffer.class.isAssignableFrom(paramClass)) || Callback.class.isAssignableFrom(paramClass) || WString.class == paramClass || String.class == paramClass) {
      i = Native.POINTER_SIZE;
    } else if (Structure.class.isAssignableFrom(paramClass)) {
      if (ByReference.class.isAssignableFrom(paramClass)) {
        i = Native.POINTER_SIZE;
      } else {
        if (paramObject == null)
          paramObject = newInstance(paramClass, PLACEHOLDER_MEMORY); 
        i = ((Structure)paramObject).getStructAlignment();
      } 
    } else if (paramClass.isArray()) {
      i = getNativeAlignment(paramClass.getComponentType(), null, paramBoolean);
    } else {
      throw new IllegalArgumentException("Type " + paramClass + " has unknown native alignment");
    } 
    if (this.actualAlignType == 1) {
      i = 1;
    } else if (this.actualAlignType == 3) {
      i = Math.min(8, i);
    } else if (this.actualAlignType == 2) {
      if (!paramBoolean || !Platform.isMac() || !Platform.isPPC())
        i = Math.min(Native.MAX_ALIGNMENT, i); 
      if (!paramBoolean && Platform.isAIX() && (paramClass == double.class || paramClass == Double.class))
        i = 4; 
    } 
    return i;
  }
  
  public String toString() {
    return toString(Boolean.getBoolean("jna.dump_memory"));
  }
  
  public String toString(boolean paramBoolean) {
    return toString(0, true, paramBoolean);
  }
  
  private String format(Class<?> paramClass) {
    String str = paramClass.getName();
    int i = str.lastIndexOf(".");
    return str.substring(i + 1);
  }
  
  private String toString(int paramInt, boolean paramBoolean1, boolean paramBoolean2) {
    ensureAllocated();
    String str1 = System.lineSeparator();
    String str2 = format(getClass()) + "(" + getPointer() + ")";
    if (!(getPointer() instanceof Memory))
      str2 = str2 + " (" + size() + " bytes)"; 
    String str3 = "";
    for (byte b = 0; b < paramInt; b++)
      str3 = str3 + "  "; 
    String str4 = str1;
    if (!paramBoolean1) {
      str4 = "...}";
    } else {
      Iterator<StructField> iterator = fields().values().iterator();
      while (iterator.hasNext()) {
        StructField structField = iterator.next();
        Object object = getFieldValue(structField.field);
        String str5 = format(structField.type);
        String str6 = "";
        str4 = str4 + str3;
        if (structField.type.isArray() && object != null) {
          str5 = format(structField.type.getComponentType());
          str6 = "[" + Array.getLength(object) + "]";
        } 
        str4 = str4 + String.format("  %s %s%s@0x%X", new Object[] { str5, structField.name, str6, Integer.valueOf(structField.offset) });
        if (object instanceof Structure)
          object = ((Structure)object).toString(paramInt + 1, !(object instanceof ByReference), paramBoolean2); 
        str4 = str4 + "=";
        if (object instanceof Long) {
          str4 = str4 + String.format("0x%08X", new Object[] { object });
        } else if (object instanceof Integer) {
          str4 = str4 + String.format("0x%04X", new Object[] { object });
        } else if (object instanceof Short) {
          str4 = str4 + String.format("0x%02X", new Object[] { object });
        } else if (object instanceof Byte) {
          str4 = str4 + String.format("0x%01X", new Object[] { object });
        } else {
          str4 = str4 + String.valueOf(object).trim();
        } 
        str4 = str4 + str1;
        if (!iterator.hasNext())
          str4 = str4 + str3 + "}"; 
      } 
    } 
    if (paramInt == 0 && paramBoolean2) {
      byte b1 = 4;
      str4 = str4 + str1 + "memory dump" + str1;
      byte[] arrayOfByte = getPointer().getByteArray(0L, size());
      for (byte b2 = 0; b2 < arrayOfByte.length; b2++) {
        if (b2 % 4 == 0)
          str4 = str4 + "["; 
        if (arrayOfByte[b2] >= 0 && arrayOfByte[b2] < 16)
          str4 = str4 + "0"; 
        str4 = str4 + Integer.toHexString(arrayOfByte[b2] & 0xFF);
        if (b2 % 4 == 3 && b2 < arrayOfByte.length - 1)
          str4 = str4 + "]" + str1; 
      } 
      str4 = str4 + "]";
    } 
    return str2 + " {" + str4;
  }
  
  public Structure[] toArray(Structure[] paramArrayOfStructure) {
    ensureAllocated();
    if (this.memory instanceof AutoAllocated) {
      Memory memory = (Memory)this.memory;
      int j = paramArrayOfStructure.length * size();
      if (memory.size() < j)
        useMemory(autoAllocate(j)); 
    } 
    paramArrayOfStructure[0] = this;
    int i = size();
    for (byte b = 1; b < paramArrayOfStructure.length; b++) {
      paramArrayOfStructure[b] = newInstance(getClass(), this.memory.share((b * i), i));
      paramArrayOfStructure[b].conditionalAutoRead();
    } 
    if (!(this instanceof ByValue))
      this.array = paramArrayOfStructure; 
    return paramArrayOfStructure;
  }
  
  public Structure[] toArray(int paramInt) {
    return toArray((Structure[])Array.newInstance(getClass(), paramInt));
  }
  
  private Class<?> baseClass() {
    return ((this instanceof ByReference || this instanceof ByValue) && Structure.class.isAssignableFrom(getClass().getSuperclass())) ? getClass().getSuperclass() : getClass();
  }
  
  public boolean dataEquals(Structure paramStructure) {
    return dataEquals(paramStructure, false);
  }
  
  public boolean dataEquals(Structure paramStructure, boolean paramBoolean) {
    if (paramBoolean) {
      paramStructure.getPointer().clear(paramStructure.size());
      paramStructure.write();
      getPointer().clear(size());
      write();
    } 
    byte[] arrayOfByte1 = paramStructure.getPointer().getByteArray(0L, paramStructure.size());
    byte[] arrayOfByte2 = getPointer().getByteArray(0L, size());
    if (arrayOfByte1.length == arrayOfByte2.length) {
      for (byte b = 0; b < arrayOfByte1.length; b++) {
        if (arrayOfByte1[b] != arrayOfByte2[b])
          return false; 
      } 
      return true;
    } 
    return false;
  }
  
  public boolean equals(Object paramObject) {
    return (paramObject instanceof Structure && paramObject.getClass() == getClass() && ((Structure)paramObject).getPointer().equals(getPointer()));
  }
  
  public int hashCode() {
    Pointer pointer = getPointer();
    return (pointer != null) ? getPointer().hashCode() : getClass().hashCode();
  }
  
  protected void cacheTypeInfo(Pointer paramPointer) {
    this.typeInfo = paramPointer.peer;
  }
  
  FFIType getFieldTypeInfo(StructField paramStructField) {
    Class<?> clazz = paramStructField.type;
    Object object = getFieldValue(paramStructField.field);
    if (this.typeMapper != null) {
      ToNativeConverter toNativeConverter = this.typeMapper.getToNativeConverter(clazz);
      if (toNativeConverter != null) {
        clazz = toNativeConverter.nativeType();
        object = toNativeConverter.toNative(object, new ToNativeContext());
      } 
    } 
    return FFIType.get(object, clazz);
  }
  
  Pointer getTypeInfo() {
    Pointer pointer = getTypeInfo(this).getPointer();
    cacheTypeInfo(pointer);
    return pointer;
  }
  
  public void setAutoSynch(boolean paramBoolean) {
    setAutoRead(paramBoolean);
    setAutoWrite(paramBoolean);
  }
  
  public void setAutoRead(boolean paramBoolean) {
    this.autoRead = paramBoolean;
  }
  
  public boolean getAutoRead() {
    return this.autoRead;
  }
  
  public void setAutoWrite(boolean paramBoolean) {
    this.autoWrite = paramBoolean;
  }
  
  public boolean getAutoWrite() {
    return this.autoWrite;
  }
  
  static FFIType getTypeInfo(Object paramObject) {
    return FFIType.get(paramObject);
  }
  
  private static <T extends Structure> T newInstance(Class<T> paramClass, long paramLong) {
    try {
      Structure structure = (Structure)newInstance((Class)paramClass, (paramLong == 0L) ? PLACEHOLDER_MEMORY : new Pointer(paramLong));
      if (paramLong != 0L)
        structure.conditionalAutoRead(); 
      return (T)structure;
    } catch (Throwable throwable) {
      LOG.log(Level.WARNING, "JNA: Error creating structure", throwable);
      return null;
    } 
  }
  
  public static <T extends Structure> T newInstance(Class<T> paramClass, Pointer paramPointer) throws IllegalArgumentException {
    try {
      Constructor<T> constructor = getPointerConstructor(paramClass);
      if (constructor != null)
        return constructor.newInstance(new Object[] { paramPointer }); 
    } catch (SecurityException securityException) {
    
    } catch (InstantiationException instantiationException) {
      String str = "Can't instantiate " + paramClass;
      throw new IllegalArgumentException(str, instantiationException);
    } catch (IllegalAccessException illegalAccessException) {
      String str = "Instantiation of " + paramClass + " (Pointer) not allowed, is it public?";
      throw new IllegalArgumentException(str, illegalAccessException);
    } catch (InvocationTargetException invocationTargetException) {
      String str = "Exception thrown while instantiating an instance of " + paramClass;
      throw new IllegalArgumentException(str, invocationTargetException);
    } 
    Structure structure = (Structure)newInstance((Class)paramClass);
    if (paramPointer != PLACEHOLDER_MEMORY)
      structure.useMemory(paramPointer); 
    return (T)structure;
  }
  
  public static <T extends Structure> T newInstance(Class<T> paramClass) throws IllegalArgumentException {
    Structure structure = Klass.<Structure>newInstance(paramClass);
    if (structure instanceof ByValue)
      structure.allocateMemory(); 
    return (T)structure;
  }
  
  private static <T> Constructor<T> getPointerConstructor(Class<T> paramClass) {
    for (Constructor<T> constructor : paramClass.getConstructors()) {
      Class[] arrayOfClass = constructor.getParameterTypes();
      if (arrayOfClass.length == 1 && arrayOfClass[0].equals(Pointer.class))
        return constructor; 
    } 
    return null;
  }
  
  private static void structureArrayCheck(Structure[] paramArrayOfStructure) {
    if (ByReference[].class.isAssignableFrom(paramArrayOfStructure.getClass()))
      return; 
    Pointer pointer = paramArrayOfStructure[0].getPointer();
    int i = paramArrayOfStructure[0].size();
    for (byte b = 1; b < paramArrayOfStructure.length; b++) {
      if ((paramArrayOfStructure[b].getPointer()).peer != pointer.peer + (i * b)) {
        String str = "Structure array elements must use contiguous memory (bad backing address at Structure array index " + b + ")";
        throw new IllegalArgumentException(str);
      } 
    } 
  }
  
  public static void autoRead(Structure[] paramArrayOfStructure) {
    structureArrayCheck(paramArrayOfStructure);
    if ((paramArrayOfStructure[0]).array == paramArrayOfStructure) {
      paramArrayOfStructure[0].autoRead();
    } else {
      for (byte b = 0; b < paramArrayOfStructure.length; b++) {
        if (paramArrayOfStructure[b] != null)
          paramArrayOfStructure[b].autoRead(); 
      } 
    } 
  }
  
  public void autoRead() {
    if (getAutoRead()) {
      read();
      if (this.array != null)
        for (byte b = 1; b < this.array.length; b++)
          this.array[b].autoRead();  
    } 
  }
  
  public static void autoWrite(Structure[] paramArrayOfStructure) {
    structureArrayCheck(paramArrayOfStructure);
    if ((paramArrayOfStructure[0]).array == paramArrayOfStructure) {
      paramArrayOfStructure[0].autoWrite();
    } else {
      for (byte b = 0; b < paramArrayOfStructure.length; b++) {
        if (paramArrayOfStructure[b] != null)
          paramArrayOfStructure[b].autoWrite(); 
      } 
    } 
  }
  
  public void autoWrite() {
    if (getAutoWrite()) {
      write();
      if (this.array != null)
        for (byte b = 1; b < this.array.length; b++)
          this.array[b].autoWrite();  
    } 
  }
  
  protected int getNativeSize(Class<?> paramClass) {
    return getNativeSize(paramClass, null);
  }
  
  protected int getNativeSize(Class<?> paramClass, Object paramObject) {
    return Native.getNativeSize(paramClass, paramObject);
  }
  
  static void validate(Class<? extends Structure> paramClass) {
    try {
      paramClass.getConstructor(new Class[0]);
      return;
    } catch (NoSuchMethodException|SecurityException noSuchMethodException) {
      throw new IllegalArgumentException("No suitable constructor found for class: " + paramClass.getName());
    } 
  }
  
  private static class AutoAllocated extends Memory {
    public AutoAllocated(int param1Int) {
      super(param1Int);
      clear();
    }
    
    public String toString() {
      return "auto-" + super.toString();
    }
  }
  
  public static interface ByValue {}
  
  public static interface ByReference {}
  
  protected static class StructField {
    public String name;
    
    public Class<?> type;
    
    public Field field;
    
    public int size = -1;
    
    public int offset = -1;
    
    public boolean isVolatile;
    
    public boolean isReadOnly;
    
    public FromNativeConverter readConverter;
    
    public ToNativeConverter writeConverter;
    
    public FromNativeContext context;
    
    public String toString() {
      return this.name + "@" + this.offset + "[" + this.size + "] (" + this.type + ")";
    }
  }
  
  private static class NativeStringTracking {
    private final Object value;
    
    private NativeString peer;
    
    NativeStringTracking(Object param1Object) {
      this.value = param1Object;
    }
  }
  
  @Documented
  @Retention(RetentionPolicy.RUNTIME)
  @Target({ElementType.TYPE})
  public static @interface FieldOrder {
    String[] value();
  }
  
  private static class LayoutInfo {
    private int size = -1;
    
    private int alignment = 1;
    
    private final Map<String, Structure.StructField> fields = Collections.synchronizedMap(new LinkedHashMap<>());
    
    private int alignType = 0;
    
    private TypeMapper typeMapper;
    
    private boolean variable;
    
    private LayoutInfo() {}
  }
  
  @FieldOrder({"size", "alignment", "type", "elements"})
  static class FFIType extends Structure {
    private static final Map<Class, Map<Integer, FFIType>> typeInfoMap = (Map)new WeakHashMap<>();
    
    private static final Map<Class, FFIType> unionHelper = (Map)new WeakHashMap<>();
    
    private static final Map<Pointer, FFIType> ffiTypeInfo = new HashMap<>();
    
    private static final int FFI_TYPE_STRUCT = 13;
    
    public size_t size;
    
    public short alignment;
    
    public short type;
    
    public Pointer elements;
    
    private static boolean isIntegerType(FFIType param1FFIType) {
      Pointer pointer = param1FFIType.getPointer();
      return (pointer.equals(FFITypes.ffi_type_uint8) || pointer.equals(FFITypes.ffi_type_sint8) || pointer.equals(FFITypes.ffi_type_uint16) || pointer.equals(FFITypes.ffi_type_sint16) || pointer.equals(FFITypes.ffi_type_uint32) || pointer.equals(FFITypes.ffi_type_sint32) || pointer.equals(FFITypes.ffi_type_uint64) || pointer.equals(FFITypes.ffi_type_sint64) || pointer.equals(FFITypes.ffi_type_pointer));
    }
    
    private static boolean isFloatType(FFIType param1FFIType) {
      Pointer pointer = param1FFIType.getPointer();
      return (pointer.equals(FFITypes.ffi_type_float) || pointer.equals(FFITypes.ffi_type_double));
    }
    
    public FFIType(FFIType param1FFIType) {
      this.type = 13;
      this.size = param1FFIType.size;
      this.alignment = param1FFIType.alignment;
      this.type = param1FFIType.type;
      this.elements = param1FFIType.elements;
    }
    
    public FFIType() {
      this.type = 13;
    }
    
    public FFIType(Structure param1Structure) {
      Pointer[] arrayOfPointer;
      this.type = 13;
      param1Structure.ensureAllocated(true);
      if (param1Structure instanceof Union) {
        FFIType fFIType = null;
        int i = 0;
        boolean bool = false;
        for (Structure.StructField structField : param1Structure.fields().values()) {
          FFIType fFIType1 = param1Structure.getFieldTypeInfo(structField);
          if (isIntegerType(fFIType1))
            bool = true; 
          if (fFIType == null || i < structField.size || (i == structField.size && Structure.class.isAssignableFrom(structField.type))) {
            fFIType = fFIType1;
            i = structField.size;
          } 
        } 
        if (((Platform.isIntel() && Platform.is64Bit() && !Platform.isWindows()) || Platform.isARM() || Platform.isLoongArch()) && bool && isFloatType(fFIType)) {
          fFIType = new FFIType(fFIType);
          if (fFIType.size.intValue() == 4) {
            fFIType.type = ((FFIType)ffiTypeInfo.get(FFITypes.ffi_type_uint32)).type;
          } else if (fFIType.size.intValue() == 8) {
            fFIType.type = ((FFIType)ffiTypeInfo.get(FFITypes.ffi_type_uint64)).type;
          } 
          fFIType.write();
        } 
        arrayOfPointer = new Pointer[] { fFIType.getPointer(), null };
        unionHelper.put(param1Structure.getClass(), fFIType);
      } else {
        arrayOfPointer = new Pointer[param1Structure.fields().size() + 1];
        byte b = 0;
        for (Structure.StructField structField : param1Structure.fields().values())
          arrayOfPointer[b++] = param1Structure.getFieldTypeInfo(structField).getPointer(); 
      } 
      init(arrayOfPointer);
      write();
    }
    
    public FFIType(Object param1Object, Class<?> param1Class) {
      this.type = 13;
      int i = Array.getLength(param1Object);
      Pointer[] arrayOfPointer = new Pointer[i + 1];
      Pointer pointer = get((Object)null, param1Class.getComponentType()).getPointer();
      for (byte b = 0; b < i; b++)
        arrayOfPointer[b] = pointer; 
      init(arrayOfPointer);
      write();
    }
    
    private void init(Pointer[] param1ArrayOfPointer) {
      this.elements = new Memory((Native.POINTER_SIZE * param1ArrayOfPointer.length));
      this.elements.write(0L, param1ArrayOfPointer, 0, param1ArrayOfPointer.length);
      write();
    }
    
    static FFIType get(Object param1Object) {
      if (param1Object == null)
        synchronized (typeInfoMap) {
          return getTypeInfo(Pointer.class, 0);
        }  
      return (param1Object instanceof Class) ? get((Object)null, (Class)param1Object) : get(param1Object, param1Object.getClass());
    }
    
    private static FFIType get(Object param1Object, Class<?> param1Class) {
      TypeMapper typeMapper = Native.getTypeMapper(param1Class);
      if (typeMapper != null) {
        ToNativeConverter toNativeConverter = typeMapper.getToNativeConverter(param1Class);
        if (toNativeConverter != null)
          param1Class = toNativeConverter.nativeType(); 
      } 
      synchronized (typeInfoMap) {
        FFIType fFIType = getTypeInfo(param1Class, param1Class.isArray() ? Array.getLength(param1Object) : 0);
        if (fFIType != null)
          return fFIType; 
        if ((Platform.HAS_BUFFERS && Buffer.class.isAssignableFrom(param1Class)) || Callback.class.isAssignableFrom(param1Class)) {
          typeInfoMap.put(param1Class, typeInfoMap.get(Pointer.class));
          return (FFIType)((Map)typeInfoMap.get(Pointer.class)).get(Integer.valueOf(0));
        } 
        if (Structure.class.isAssignableFrom(param1Class)) {
          if (param1Object == null)
            param1Object = newInstance(param1Class, Structure.PLACEHOLDER_MEMORY); 
          if (Structure.ByReference.class.isAssignableFrom(param1Class)) {
            typeInfoMap.put(param1Class, typeInfoMap.get(Pointer.class));
            return (FFIType)((Map)typeInfoMap.get(Pointer.class)).get(Integer.valueOf(0));
          } 
          FFIType fFIType1 = new FFIType((Structure)param1Object);
          storeTypeInfo(param1Class, fFIType1);
          return fFIType1;
        } 
        if (NativeMapped.class.isAssignableFrom(param1Class)) {
          NativeMappedConverter nativeMappedConverter = NativeMappedConverter.getInstance(param1Class);
          return get(nativeMappedConverter.toNative(param1Object, new ToNativeContext()), nativeMappedConverter.nativeType());
        } 
        if (param1Class.isArray()) {
          FFIType fFIType1 = new FFIType(param1Object, param1Class);
          storeTypeInfo(param1Class, Array.getLength(param1Object), fFIType1);
          return fFIType1;
        } 
        throw new IllegalArgumentException("Unsupported type " + param1Class);
      } 
    }
    
    private static FFIType getTypeInfo(Class param1Class, int param1Int) {
      Map map = typeInfoMap.get(param1Class);
      return (map != null) ? (FFIType)map.get(Integer.valueOf(param1Int)) : null;
    }
    
    private static void storeTypeInfo(Class param1Class, FFIType param1FFIType) {
      storeTypeInfo(param1Class, 0, param1FFIType);
    }
    
    private static void storeTypeInfo(Class param1Class, int param1Int, FFIType param1FFIType) {
      synchronized (typeInfoMap) {
        Map<Object, Object> map = (Map)typeInfoMap.get(param1Class);
        if (map == null) {
          map = new HashMap<>();
          typeInfoMap.put(param1Class, map);
        } 
        map.put(Integer.valueOf(param1Int), param1FFIType);
      } 
    }
    
    static {
      if (Native.POINTER_SIZE == 0)
        throw new Error("Native library not initialized"); 
      if (FFITypes.ffi_type_void == null)
        throw new Error("FFI types not initialized"); 
      ffiTypeInfo.put(FFITypes.ffi_type_void, Structure.<FFIType>newInstance(FFIType.class, FFITypes.ffi_type_void));
      ffiTypeInfo.put(FFITypes.ffi_type_float, Structure.<FFIType>newInstance(FFIType.class, FFITypes.ffi_type_float));
      ffiTypeInfo.put(FFITypes.ffi_type_double, Structure.<FFIType>newInstance(FFIType.class, FFITypes.ffi_type_double));
      ffiTypeInfo.put(FFITypes.ffi_type_longdouble, Structure.<FFIType>newInstance(FFIType.class, FFITypes.ffi_type_longdouble));
      ffiTypeInfo.put(FFITypes.ffi_type_uint8, Structure.<FFIType>newInstance(FFIType.class, FFITypes.ffi_type_uint8));
      ffiTypeInfo.put(FFITypes.ffi_type_sint8, Structure.<FFIType>newInstance(FFIType.class, FFITypes.ffi_type_sint8));
      ffiTypeInfo.put(FFITypes.ffi_type_uint16, Structure.<FFIType>newInstance(FFIType.class, FFITypes.ffi_type_uint16));
      ffiTypeInfo.put(FFITypes.ffi_type_sint16, Structure.<FFIType>newInstance(FFIType.class, FFITypes.ffi_type_sint16));
      ffiTypeInfo.put(FFITypes.ffi_type_uint32, Structure.<FFIType>newInstance(FFIType.class, FFITypes.ffi_type_uint32));
      ffiTypeInfo.put(FFITypes.ffi_type_sint32, Structure.<FFIType>newInstance(FFIType.class, FFITypes.ffi_type_sint32));
      ffiTypeInfo.put(FFITypes.ffi_type_uint64, Structure.<FFIType>newInstance(FFIType.class, FFITypes.ffi_type_uint64));
      ffiTypeInfo.put(FFITypes.ffi_type_sint64, Structure.<FFIType>newInstance(FFIType.class, FFITypes.ffi_type_sint64));
      ffiTypeInfo.put(FFITypes.ffi_type_pointer, Structure.<FFIType>newInstance(FFIType.class, FFITypes.ffi_type_pointer));
      for (FFIType fFIType1 : ffiTypeInfo.values())
        fFIType1.read(); 
      storeTypeInfo(void.class, ffiTypeInfo.get(FFITypes.ffi_type_void));
      storeTypeInfo(Void.class, ffiTypeInfo.get(FFITypes.ffi_type_void));
      storeTypeInfo(float.class, ffiTypeInfo.get(FFITypes.ffi_type_float));
      storeTypeInfo(Float.class, ffiTypeInfo.get(FFITypes.ffi_type_float));
      storeTypeInfo(double.class, ffiTypeInfo.get(FFITypes.ffi_type_double));
      storeTypeInfo(Double.class, ffiTypeInfo.get(FFITypes.ffi_type_double));
      storeTypeInfo(long.class, ffiTypeInfo.get(FFITypes.ffi_type_sint64));
      storeTypeInfo(Long.class, ffiTypeInfo.get(FFITypes.ffi_type_sint64));
      storeTypeInfo(int.class, ffiTypeInfo.get(FFITypes.ffi_type_sint32));
      storeTypeInfo(Integer.class, ffiTypeInfo.get(FFITypes.ffi_type_sint32));
      storeTypeInfo(short.class, ffiTypeInfo.get(FFITypes.ffi_type_sint16));
      storeTypeInfo(Short.class, ffiTypeInfo.get(FFITypes.ffi_type_sint16));
      FFIType fFIType = (Native.WCHAR_SIZE == 2) ? ffiTypeInfo.get(FFITypes.ffi_type_uint16) : ffiTypeInfo.get(FFITypes.ffi_type_uint32);
      storeTypeInfo(char.class, fFIType);
      storeTypeInfo(Character.class, fFIType);
      storeTypeInfo(byte.class, ffiTypeInfo.get(FFITypes.ffi_type_sint8));
      storeTypeInfo(Byte.class, ffiTypeInfo.get(FFITypes.ffi_type_sint8));
      storeTypeInfo(Pointer.class, ffiTypeInfo.get(FFITypes.ffi_type_pointer));
      storeTypeInfo(String.class, ffiTypeInfo.get(FFITypes.ffi_type_pointer));
      storeTypeInfo(WString.class, ffiTypeInfo.get(FFITypes.ffi_type_pointer));
      storeTypeInfo(boolean.class, ffiTypeInfo.get(FFITypes.ffi_type_uint32));
      storeTypeInfo(Boolean.class, ffiTypeInfo.get(FFITypes.ffi_type_uint32));
    }
    
    private static class FFITypes {
      private static Pointer ffi_type_void;
      
      private static Pointer ffi_type_float;
      
      private static Pointer ffi_type_double;
      
      private static Pointer ffi_type_longdouble;
      
      private static Pointer ffi_type_uint8;
      
      private static Pointer ffi_type_sint8;
      
      private static Pointer ffi_type_uint16;
      
      private static Pointer ffi_type_sint16;
      
      private static Pointer ffi_type_uint32;
      
      private static Pointer ffi_type_sint32;
      
      private static Pointer ffi_type_uint64;
      
      private static Pointer ffi_type_sint64;
      
      private static Pointer ffi_type_pointer;
    }
    
    public static class size_t extends IntegerType {
      private static final long serialVersionUID = 1L;
      
      public size_t() {
        this(0L);
      }
      
      public size_t(long param2Long) {
        super(Native.SIZE_T_SIZE, param2Long);
      }
    }
  }
  
  static class StructureSet extends AbstractCollection<Structure> implements Set<Structure> {
    Structure[] elements;
    
    private int count;
    
    private void ensureCapacity(int param1Int) {
      if (this.elements == null) {
        this.elements = new Structure[param1Int * 3 / 2];
      } else if (this.elements.length < param1Int) {
        Structure[] arrayOfStructure = new Structure[param1Int * 3 / 2];
        System.arraycopy(this.elements, 0, arrayOfStructure, 0, this.elements.length);
        this.elements = arrayOfStructure;
      } 
    }
    
    public Structure[] getElements() {
      return this.elements;
    }
    
    public int size() {
      return this.count;
    }
    
    public boolean contains(Object param1Object) {
      return (indexOf((Structure)param1Object) != -1);
    }
    
    public boolean add(Structure param1Structure) {
      if (!contains(param1Structure)) {
        ensureCapacity(this.count + 1);
        this.elements[this.count++] = param1Structure;
        return true;
      } 
      return false;
    }
    
    private int indexOf(Structure param1Structure) {
      for (byte b = 0; b < this.count; b++) {
        Structure structure = this.elements[b];
        if (param1Structure == structure || (param1Structure.getClass() == structure.getClass() && param1Structure.size() == structure.size() && param1Structure.getPointer().equals(structure.getPointer())))
          return b; 
      } 
      return -1;
    }
    
    public boolean remove(Object param1Object) {
      int i = indexOf((Structure)param1Object);
      if (i != -1) {
        if (--this.count >= 0) {
          this.elements[i] = this.elements[this.count];
          this.elements[this.count] = null;
        } 
        return true;
      } 
      return false;
    }
    
    public Iterator<Structure> iterator() {
      Structure[] arrayOfStructure = new Structure[this.count];
      if (this.count > 0)
        System.arraycopy(this.elements, 0, arrayOfStructure, 0, this.count); 
      return Arrays.<Structure>asList(arrayOfStructure).iterator();
    }
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\com\sun\jna\Structure.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */