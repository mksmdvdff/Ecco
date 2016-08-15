package mksm.ecco.model.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;

import java.util.ArrayList;
import java.util.List;

import mksm.ecco.model.EccoShop;

/**
 * Created by mskm on 11.08.2016.
 */
public class CacheTable extends DatabaseTable{

    private static CacheTable sInstance;

    private final String ID_COLUMN = "_id";
    private final String NAME_COLUMN = "name";
    private final String COUNTRY_COLUMN = "country";
    private final String REGION_COLUMN = "region";
    private final String TOWN_COLUMN = "town";
    private final String ADDRESS_COLUMN = "address";
    private final String METRO_COLUMN = "metro";
    private final String PHONE_COLUMN = "phone";
    private final String WORKTIME_COLUMN = "worktime";
    private final String TYPE_COLUMN = "type";
    private final String LONGTITUDE_COLUMN = "longtitude";
    private final String LATITUDE_COLUMN = "latitude";
    private final String IS_NEW_COLUMN = "is_new";

    private CacheTable(Context context) {
        super(context);
    }



    public static synchronized CacheTable getInstance(Context context) {

        if (sInstance == null) {
            sInstance = new CacheTable(context.getApplicationContext());
        }
        return sInstance;
    }

    @Override
    public void init() {
        TABLE_NAME = "cache";

        columns.put(ID_COLUMN," TEXT PRIMARY KEY ");
        columns.put(NAME_COLUMN," TEXT ");
        columns.put(COUNTRY_COLUMN," TEXT ");
        columns.put(REGION_COLUMN," TEXT ");
        columns.put(TOWN_COLUMN," TEXT ");
        columns.put(ADDRESS_COLUMN," TEXT ");
        columns.put(METRO_COLUMN," TEXT ");
        columns.put(PHONE_COLUMN," TEXT ");
        columns.put(WORKTIME_COLUMN," TEXT ");
        columns.put(TYPE_COLUMN," TEXT ");
        columns.put(LONGTITUDE_COLUMN," TEXT ");
        columns.put(LATITUDE_COLUMN," TEXT ");
        columns.put(IS_NEW_COLUMN," TEXT ");
    }

    public List<EccoShop> getAllShops() {
        Cursor cursor = mDb.rawQuery(GET_ALL_STRING, null);
        List<EccoShop> result = new ArrayList<>();
        if (cursor.moveToFirst()) {

            while (!cursor.isAfterLast()) {
                String _id = cursor.getString(cursor.getColumnIndex(ID_COLUMN));
                String name = cursor.getString(cursor.getColumnIndex(NAME_COLUMN));
                String country = cursor.getString(cursor.getColumnIndex(COUNTRY_COLUMN));
                String region = cursor.getString(cursor.getColumnIndex(REGION_COLUMN));
                String town = cursor.getString(cursor.getColumnIndex(TOWN_COLUMN));
                String address = cursor.getString(cursor.getColumnIndex(ADDRESS_COLUMN));
                String metro = cursor.getString(cursor.getColumnIndex(METRO_COLUMN));
                String phone = cursor.getString(cursor.getColumnIndex(PHONE_COLUMN));
                String workTime = cursor.getString(cursor.getColumnIndex(WORKTIME_COLUMN));
                String type = cursor.getString(cursor.getColumnIndex(TYPE_COLUMN));
                String longtitude = cursor.getString(cursor.getColumnIndex(LONGTITUDE_COLUMN));
                String latitude = cursor.getString(cursor.getColumnIndex(LATITUDE_COLUMN));
                String isNew = cursor.getString(cursor.getColumnIndex(IS_NEW_COLUMN));

                result.add(new EccoShop(_id, name, country, region, town, address, metro, phone, workTime, type, longtitude, latitude, isNew));

                cursor.moveToNext();
            }
        }

        return result;
    }

    public void replaceAllNotes(List<EccoShop> shops) {
        mDb.execSQL(DELETE_ALL_STRING);
        mDb.execSQL(VACUUM);
        mDb.beginTransaction();
        //уж простите, но c prepared statements я заморачиваться не сталыы
        for (EccoShop shop : shops) {
            ContentValues content = new ContentValues();
            content.put(ID_COLUMN, shop.getId());
            content.put(NAME_COLUMN, shop.getName());
            content.put(COUNTRY_COLUMN, shop.getCountry());
            content.put(REGION_COLUMN, shop.getRegion());
            content.put(TOWN_COLUMN, shop.getTown());
            content.put(ADDRESS_COLUMN, shop.getAddress());
            content.put(METRO_COLUMN, shop.getMetro());
            content.put(PHONE_COLUMN, shop.getPhone());
            content.put(WORKTIME_COLUMN, shop.getWorktime());
            content.put(TYPE_COLUMN, shop.getType());
            content.put(LONGTITUDE_COLUMN, shop.getLongtitude());
            content.put(LATITUDE_COLUMN, shop.getLatitude());
            content.put(IS_NEW_COLUMN, shop.getIsNew());
            mDb.insert(TABLE_NAME, null, content);
        }
        mDb.setTransactionSuccessful();
        mDb.endTransaction();
    }
}
