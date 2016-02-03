package com.tcloud.util;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.telephony.TelephonyManager;
import android.provider.Settings.Secure;
import android.util.Log;

public class CoreDB extends SQLiteOpenHelper {

	public static final String DBNAME = "tclouddb";
	public static final int VERSION = 1;
	private Context context;

	private final static String TAG = "CoreDB";

	public CoreDB(Context context) {
		super(context, DBNAME, null, VERSION);
		this.context = context;
		Log.d(TAG, "CoreDB");
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		Log.d(TAG, "onCreate");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.d(TAG, "onUpgrade");
	}

	// Borro Base de datos
	public void onDelete() {
		Log.d(TAG, "onDelete");
		context.deleteDatabase(DBNAME);
	}

	// Borro Base de datos
	public boolean isExist() {
		Log.d(TAG, "isExist");

		File database = context.getDatabasePath(DBNAME);

		if (!database.exists())
			return false;

		return true;

	}

	// Retorno detalles de usuario de DB
	public String getDB(String col) {

		Log.d(TAG, "getDB [" + col + "]");

		try {

			Map<String, String> rec = null;
			SQLiteDatabase udb = this.getWritableDatabase();

			Cursor cursor = udb.rawQuery("SELECT * FROM OAuth2", null);

			// looping
			if (cursor.moveToFirst())
				rec = cur2Map(cursor);

			udb.close();

			Log.d(TAG, rec.get(col));

			return rec.get(col);

		} catch (android.database.sqlite.SQLiteConstraintException e) {
			Log.e(TAG, "SQLiteConstraintException:" + e.getMessage());
		} catch (android.database.sqlite.SQLiteException e) {
			Log.e(TAG, "SQLiteException:" + e.getMessage());
		} catch (Exception e) {
			Log.e(TAG, "Exception:" + e.getMessage());
		}

		return null;
	}

	// Function para pasar de list a Map
	public Map<String, String> cur2Map(Cursor cur) {

		// Defino el HashMaps
		Map<String, String> recordVal = new HashMap<String, String>();

		for (int n = 0; n < cur.getColumnCount(); n++)
			recordVal.put(cur.getColumnName(n), cur.getString(n));

		return recordVal;
	}

	// Function para pasar de list a Json
	public JSONArray cur2Json(Cursor cursor) {

		JSONArray resultSet = new JSONArray();
		cursor.moveToFirst();
		while (cursor.isAfterLast() == false) {

			int totalColumn = cursor.getColumnCount();
			JSONObject rowObject = new JSONObject();

			for (int i = 0; i < totalColumn; i++) {
				if (cursor.getColumnName(i) != null) {
					try {
						rowObject.put(cursor.getColumnName(i),
								cursor.getString(i));
					} catch (Exception e) {
						Log.d(TAG, e.getMessage());
					}
				}
			}
			resultSet.put(rowObject);
			cursor.moveToNext();
		}

		cursor.close();
		return resultSet;

	}
}