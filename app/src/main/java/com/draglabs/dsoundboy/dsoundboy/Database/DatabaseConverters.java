package com.draglabs.dsoundboy.dsoundboy.Database;

import android.arch.persistence.room.TypeConverter;

import java.util.Date;

/**
 * Created by davru on 12/28/2017.
 */

public class DatabaseConverters {

    @TypeConverter
    public static Date fromTimestamp(Long value) {
        return value == null ? null : new Date(value);
    }

    @TypeConverter
    public static Long dateToTimestamp(Date date) {
        return date == null ? null : date.getTime();
    }

}
