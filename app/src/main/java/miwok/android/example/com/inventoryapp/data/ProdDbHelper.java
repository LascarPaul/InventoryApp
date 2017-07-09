package miwok.android.example.com.inventoryapp.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Paul on 09-Jul-17.
 */

public class ProdDbHelper extends SQLiteOpenHelper {

    public static final String LOG_TAG = ProdDbHelper.class.getSimpleName();

    private static final String DATABASE_NAME = "Inventory.db";

    private static final int DATABASE_VERSION = 1;

    public ProdDbHelper(Context c) {
        super(c, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String SQL_CREATE_PRODUCT_TABLE = "CREATE TABLE " + ProdContract.ProductEntry.TABLE_NAME + " ("
                + ProdContract.ProductEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + ProdContract.ProductEntry.COLUMN_IMAGE_PRODUCT + " TEXT NOT NULL DEFAULT 'no image', "
                + ProdContract.ProductEntry.COLUMN_NAME_PRODUCT + " TEXT NOT NULL, "
                + ProdContract.ProductEntry.COLUMN_PRICE_PRODUCT + " REAL NOT NULL, "
                + ProdContract.ProductEntry.COLUMN_QUANTITY_PRODUCT + " INTEGER DEFAULT 0, "
                + ProdContract.ProductEntry.COLUMN_PRODUCT_SALES + " INTEGER DEFAULT 0.0 );";

        db.execSQL(SQL_CREATE_PRODUCT_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + ProdContract.ProductEntry.TABLE_NAME);
        onCreate(db);
    }
}