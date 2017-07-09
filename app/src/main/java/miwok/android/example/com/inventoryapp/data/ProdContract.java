package miwok.android.example.com.inventoryapp.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by Paul on 09-Jul-17.
 */

public final class ProdContract {

    public static final String CONTENT_AUTHORITY = "miwok.android.example.com.inventoryapp";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_PRODUCT = "products";

    private ProdContract() {
    }

    public static class ProductEntry implements BaseColumns {

        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_PRODUCT);

        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_PRODUCT;

        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_PRODUCT;

        public static final String TABLE_NAME = "products";
        public static final String _ID = BaseColumns._ID;
        public static final String COLUMN_IMAGE_PRODUCT = "Product_Image";
        public static final String COLUMN_NAME_PRODUCT = "Product_Name";
        public static final String COLUMN_PRICE_PRODUCT = "Product_Price";
        public static final String COLUMN_QUANTITY_PRODUCT = "Product_Quantity";
        public static final String COLUMN_PRODUCT_SALES = "Product_Sales";
    }
}