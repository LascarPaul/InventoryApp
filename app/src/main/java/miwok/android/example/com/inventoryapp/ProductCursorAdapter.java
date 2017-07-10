package miwok.android.example.com.inventoryapp;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import miwok.android.example.com.inventoryapp.data.ProdContract;

/**
 * Created by Paul on 09-Jul-17.
 */

public class ProductCursorAdapter extends CursorAdapter {

    public ProductCursorAdapter(Context context, Cursor c) {
        super(context, c, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {

        return LayoutInflater.from(context).inflate(R.layout.product_list, parent, false);
    }

    @Override
    public void bindView(View view, final Context context, final Cursor cursor) {

        ImageView productImageView = (ImageView) view.findViewById(R.id.product_image);
        TextView productNameTextView = (TextView) view.findViewById(R.id.product_name);
        TextView productPriceTextVIew = (TextView) view.findViewById(R.id.product_price);
        TextView productQuantityTextView = (TextView) view.findViewById(R.id.product_quantity);
        TextView productSalesTextView = (TextView) view.findViewById(R.id.product_sales);
        ImageView productBuyImageVIew = (ImageView) view.findViewById(R.id.product_buy_image);

        int imageColumnIndex = cursor.getColumnIndex(ProdContract.ProductEntry.COLUMN_IMAGE_PRODUCT);
        int nameColumnIndex = cursor.getColumnIndex(ProdContract.ProductEntry.COLUMN_NAME_PRODUCT);
        final int priceColumnIndex = cursor.getColumnIndex(ProdContract.ProductEntry.COLUMN_PRICE_PRODUCT);
        int quantityColumnIndex = cursor.getColumnIndex(ProdContract.ProductEntry.COLUMN_QUANTITY_PRODUCT);
        int salesColumnIndex = cursor.getColumnIndex(ProdContract.ProductEntry.COLUMN_PRODUCT_SALES);

        int id = cursor.getInt(cursor.getColumnIndex(ProdContract.ProductEntry._ID));
        Uri productImage = Uri.parse(cursor.getString(imageColumnIndex));
        final String productName = cursor.getString(nameColumnIndex);
        final double productPrice = priceColumnIndex;
        final double price = cursor.getDouble(priceColumnIndex);
        String productsPrice = "Price: " + cursor.getString(priceColumnIndex) + " $";
        final int quantity = cursor.getInt(quantityColumnIndex);
        String productQuantity = "Stock: " + cursor.getString(quantityColumnIndex);
        final double productSales = cursor.getDouble(salesColumnIndex);
        String productTotalSales = "Sales: " + cursor.getString(salesColumnIndex) +
                " $";

        final Uri currentProductUri = ContentUris.withAppendedId(ProdContract.ProductEntry.CONTENT_URI, id);

        productNameTextView.setText(productName);
        productPriceTextVIew.setText(productsPrice);
        productQuantityTextView.setText(productQuantity);
        productSalesTextView.setText(productTotalSales);

        Picasso.with(context).load(productImage)
                .placeholder(R.drawable.add_image)
                .fit()
                .into(productImageView);

        Picasso.with(context).load(productImage)
                .placeholder(R.drawable.shop)
                .fit()
                .into(productBuyImageVIew);


        productBuyImageVIew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ContentResolver resolver = v.getContext().getContentResolver();
                ContentValues values = new ContentValues();

                if (quantity > 0) {
                    int stock = quantity;
                    double prices = price;
                    double totalSales = productSales + prices;
                    values.put(ProdContract.ProductEntry.COLUMN_PRODUCT_SALES, totalSales);
                    values.put(ProdContract.ProductEntry.COLUMN_QUANTITY_PRODUCT, --stock);
                    resolver.update(
                            currentProductUri,
                            values,
                            null,
                            null
                    );
                    context.getContentResolver().notifyChange(currentProductUri, null);
                } else {

                    Toast.makeText(context, R.string.no_product, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}