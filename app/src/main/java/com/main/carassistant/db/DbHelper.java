package com.main.carassistant.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.Nullable;
import com.main.carassistant.db.annotations.Table;
import com.main.carassistant.db.annotations.type.dbInteger;
import com.main.carassistant.db.annotations.type.dbLong;
import com.main.carassistant.db.annotations.type.dbString;
import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Locale;

public class DbHelper extends SQLiteOpenHelper implements IDbOperations {

    private static final String SQL_TABLE_CREATE_TEMPLATE = "CREATE TABLE IF NOT EXISTS %s (%s)";
    private static final String SQL_TABLE_CREATE_FIELD_TEMPLATE = "%s %s";
    private static DbHelper instance = null;

    //TODO move to App
    private DbHelper(final Context context, final String name, final int version) {
        super(context, name, null, version);
    }

    public static DbHelper getHelper(final Context cont, final String nm, final int ver){
        if (instance == null) {
            instance = new DbHelper(cont, nm, ver);
        }
//        cont.getSystemService("my_db");
        return instance;
    }

    @Nullable
    public static String getTableName(final AnnotatedElement clazz) {
        final Table table = clazz.getAnnotation(Table.class);

        if (table != null) {
            return table.name();
        } else {
            return null;
        }
    }

    @Nullable
    private static String getTableCreateQuery (final Class<?> clazz) {
        final Table table = clazz.getAnnotation(Table.class);
        String ptype = null;

        if (table != null) {
            try {
                final String name = table.name();

                final StringBuilder builder = new StringBuilder();
                final Field[] fields = clazz.getFields();

                for (int i = 0; i < fields.length; i++) {

                    final Field field = fields[i];
                    final Annotation[] annotations = field.getAnnotations();

                    String type = null;

                    for (final Annotation annotation : annotations) {
                        if (annotation instanceof dbInteger) {
                            type = ((dbInteger) annotation).value();
                        } else if (annotation instanceof dbLong) {
                            type = ((dbLong) annotation).value();
                        } else if (annotation instanceof dbString) {
                            type = ((dbString) annotation).value();
                        }
                    }

                    if (ptype != null && type != null) {
                        builder.append(", ");
                    }

                    if (i < fields.length && type!= null) {
                        final String value = (String) field.get(null);
                        builder.append(String.format(Locale.US ,SQL_TABLE_CREATE_FIELD_TEMPLATE, value, type));
                    }

                    ptype = type;
                }

                return String.format(Locale.US ,SQL_TABLE_CREATE_TEMPLATE, name, builder.toString());

            } catch (final Exception e) {
                return null;
            }

        } else {
            return null;
        }
    }



    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        for (final Class<?> clazz : Contract.MODELS) {
            final String sql = getTableCreateQuery(clazz);

            if (sql != null) {
                sqLiteDatabase.execSQL(sql);
            }
        }
    }

    @Override
    public void onUpgrade(final SQLiteDatabase sqLiteDatabase, final int i, final int i1) {
        if (i < 2) {
            sqLiteDatabase.execSQL("ALTER TABLE Stats ADD COLUMN total_fueling INTEGER DEFAULT 0");
        }
    }

    @Override
    public Cursor query(String sql, String... args) {
        final SQLiteDatabase database = getReadableDatabase();

        return database.rawQuery(sql, args);
    }

    @Override
    public long insert(final Class<?> table, final ContentValues values) {

        final String name = getTableName(table);

        if (name != null) {
            final SQLiteDatabase database = getWritableDatabase();
            long id;

            try {
                database.beginTransaction();

                id = database.insert(name, null, values);

                database.setTransactionSuccessful();

            } finally {
                database.endTransaction();
            }

            return id;
        } else {
            throw new RuntimeException();
        }
    }

    @Override
    public int bulkInsert(final Class<?> table, final List<ContentValues> values) {
        final String name = getTableName(table);

        if (name != null) {
            final SQLiteDatabase database = getWritableDatabase();
            int count = 0;

            try {
                database.beginTransaction();

                for (final ContentValues value : values) {
                    database.insert(name, null, value);
                    count++;
                }

                database.setTransactionSuccessful();

            } finally {
                database.endTransaction();
            }

            return count;
        } else {
            throw new RuntimeException();
        }
    }

    @Override
    public int delete(final Class<?> table, final String sql, final String... args) {
        final String name = getTableName(table);

        if (name != null) {
            final SQLiteDatabase database = getWritableDatabase();
            int count = 0;

            try {
                database.beginTransaction();

                count = database.delete(name, sql, args);

                database.setTransactionSuccessful();

            } finally {
                database.endTransaction();
            }

            return count;
        } else {
            throw new RuntimeException();
        }
    }
}
