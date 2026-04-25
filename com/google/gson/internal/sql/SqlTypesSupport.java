package com.google.gson.internal.sql;

import com.google.gson.TypeAdapterFactory;
import com.google.gson.internal.bind.DefaultDateTypeAdapter;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.Date;

public final class SqlTypesSupport {
  public static final boolean SUPPORTS_SQL_TYPES;
  
  public static final DefaultDateTypeAdapter.DateType<? extends Date> DATE_DATE_TYPE;
  
  public static final DefaultDateTypeAdapter.DateType<? extends Date> TIMESTAMP_DATE_TYPE;
  
  public static final TypeAdapterFactory DATE_FACTORY;
  
  public static final TypeAdapterFactory TIME_FACTORY;
  
  public static final TypeAdapterFactory TIMESTAMP_FACTORY;
  
  static {
    boolean bool;
    try {
      Class.forName("java.sql.Date");
      bool = true;
    } catch (ClassNotFoundException classNotFoundException) {
      bool = false;
    } 
    SUPPORTS_SQL_TYPES = bool;
    if (SUPPORTS_SQL_TYPES) {
      DATE_DATE_TYPE = (DefaultDateTypeAdapter.DateType)new DefaultDateTypeAdapter.DateType<Date>(Date.class) {
          protected Date deserialize(Date param1Date) {
            return new Date(param1Date.getTime());
          }
          
          static {
          
          }
        };
      TIMESTAMP_DATE_TYPE = (DefaultDateTypeAdapter.DateType)new DefaultDateTypeAdapter.DateType<Timestamp>(Timestamp.class) {
          protected Timestamp deserialize(Date param1Date) {
            return new Timestamp(param1Date.getTime());
          }
          
          static {
          
          }
        };
      DATE_FACTORY = SqlDateTypeAdapter.FACTORY;
      TIME_FACTORY = SqlTimeTypeAdapter.FACTORY;
      TIMESTAMP_FACTORY = SqlTimestampTypeAdapter.FACTORY;
    } else {
      DATE_DATE_TYPE = null;
      TIMESTAMP_DATE_TYPE = null;
      DATE_FACTORY = null;
      TIME_FACTORY = null;
      TIMESTAMP_FACTORY = null;
    } 
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\com\google\gson\internal\sql\SqlTypesSupport.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */