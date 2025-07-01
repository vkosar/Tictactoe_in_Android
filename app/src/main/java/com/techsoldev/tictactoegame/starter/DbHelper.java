package com.techsoldev.tictactoegame.starter;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;

public class DbHelper extends SQLiteOpenHelper {
    private static final String TAG = "TTT-DbHelper";
    public static final int DB_VERSION = 1;
    public static final String DB_NAME = "main.db";
    public static final String T_PARAMS = "params";
    public static final String T_PARAMS_C_NAME = "name";
    public static final String T_PARAMS_C_VALUE = "value";
    private static final String[] SQL_CREATE = {
        "CREATE TABLE " + T_PARAMS + " (" +
        T_PARAMS_C_NAME + " TEXT PRIMARY KEY," +
        T_PARAMS_C_VALUE + " TEXT" +
        ")"
    };
    private static final String[] SQL_DROP = {
        "DROP TABLE IF EXISTS " + T_PARAMS
    };

    public DbHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        for (String sql: SQL_CREATE) {
            db.execSQL(sql);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This database is only a cache for online data,
        // so its upgrade policy is drop-and-create DB
        for (String sql: SQL_DROP) {
            db.execSQL(sql);
        }
        onCreate(db);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This database is only a cache for online data,
        // so its downgrade policy is drop-and-create DB
        onUpgrade(db, oldVersion, newVersion);
    }

    public static void setParams(Context context, JSONArray params) throws JSONException {
        try (DbHelper dbHelper = new DbHelper(context)) {
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            for (int i = 0; i < params.length(); i++) {
                try {
                    JSONObject param = params.getJSONObject(i);
                    String name = param.getString("name");
                    // Null or missing "value" requires parameter deletion
                    String value = null;
                    try {
                        value = param.getString("value");
                    } catch (JSONException error) { /* empty */ }
                    if (value == null) {
                        int count = db.delete(
                            T_PARAMS,
                            T_PARAMS_C_NAME + " = ?",
                            new String[]{name}
                        );
                        //Log.i(TAG, String.format("Deleted '%s' with count=%d", name, count));
                    } else {
                        ContentValues insertValues = new ContentValues();
                        insertValues.put(T_PARAMS_C_NAME, name);
                        insertValues.put(T_PARAMS_C_VALUE, value);
                        long id = db.insertWithOnConflict(
                            T_PARAMS,
                            null,
                            insertValues,
                            SQLiteDatabase.CONFLICT_REPLACE
                        );
                        if (id < 0) {
                            Log.e(TAG, String.format("Error inserting '%s'='%s' with result=%d", name, value, id));
                        //} else {
                        //    Log.i(TAG, String.format("Inserted '%s'='%s' with id=%d", name, value, id));
                        }
                    }
                } catch (JSONException error) {
                    Log.e(TAG, String.format("Error parsing parameter at index=%d:", i), error);
                }
            }
        }
    }

    public static void setParams(Context context, JSONObject paramsObj) throws JSONException {
        JSONArray params = new JSONArray();
        for (Iterator<String> names = paramsObj.keys(); names.hasNext(); ) {
            String name = names.next();
            String value = paramsObj.getString(name);
            JSONObject obj = new JSONObject();
            obj.put("name", name);
            obj.put("value", value);
            params.put(obj);
        }
        setParams(context, params);
    }

    public static void setParam(Context context, String name, String value) throws JSONException {
        JSONArray params = new JSONArray();
        JSONObject obj = new JSONObject();
        obj.put("name", name);
        obj.put("value", value);
        params.put(obj);
        setParams(context, params);
    }

    public static String getParamStr(Context context, String name, String defValue) {
        String value = defValue;
        try (DbHelper dbHelper = new DbHelper(context)) {
            SQLiteDatabase db = dbHelper.getReadableDatabase();
            try (Cursor cursor = db.query(
                T_PARAMS,
                new String[] { T_PARAMS_C_VALUE },
                T_PARAMS_C_NAME + " = ?",
                new String[] { name },
                null, null, null
            )) {
                if (cursor.moveToNext()) {
                    value = cursor.getString(0);
                }
            }
        }
        return value;
    }

    public static int getParamInt(Context context, String name, int defValue) {
        int value = defValue;
        try {
            value = Integer.parseInt(getParamStr(context, name, null));
        } catch (NumberFormatException error) { /* empty */ }
        return value;
    }
}
