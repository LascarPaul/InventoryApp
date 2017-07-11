package miwok.android.example.com.inventoryapp;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import miwok.android.example.com.inventoryapp.data.ProdContract;

/**
 * Created by Paul on 09-Jul-17.
 */

public class DetailViewActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {


    private Uri currentProductUri;
    private String currentPhotoUri = "N/A";
    private TextView prodName;
    private TextView prodPrice;
    private ImageView stockRequest;
    private TextView prodQuantity;
    private ImageView prodImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_view);

        stockRequest = (ImageView) findViewById(R.id.stock_request);
        prodImage = (ImageView) findViewById(R.id.image_view_catalog);
        prodQuantity = (TextView) findViewById(R.id.product_quantity_catalog1);
        prodName = (TextView) findViewById(R.id.product_name_catalog1);
        prodPrice = (TextView) findViewById(R.id.product_price_catalog1);

        final Intent intent = getIntent();
        currentProductUri = intent.getData();

        getLoaderManager().initLoader(0, null, this);

        stockRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialogRequestMerchandise();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.catalog_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.edit_product_atributes:
                modifyProduct();
                finish();
                return true;
            case R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void modifyProduct() {

        Intent intent = new Intent(DetailViewActivity.this, EditorActivity.class);
        Uri currentProductUri = ContentUris.withAppendedId(ProdContract.ProductEntry.CONTENT_URI,
                ContentUris.parseId(this.currentProductUri));
        intent.setData(currentProductUri);
        startActivity(intent);
        finish();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        String[] projection = new String[]{
                ProdContract.ProductEntry._ID,
                ProdContract.ProductEntry.COLUMN_IMAGE_PRODUCT,
                ProdContract.ProductEntry.COLUMN_NAME_PRODUCT,
                ProdContract.ProductEntry.COLUMN_PRICE_PRODUCT,
                ProdContract.ProductEntry.COLUMN_QUANTITY_PRODUCT,
                ProdContract.ProductEntry.COLUMN_PRODUCT_SALES};

        return new CursorLoader(this, currentProductUri, projection, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {

        if (cursor == null || cursor.getCount() < 1) {
            return;
        }

        if (cursor.moveToFirst()) {

            int imageColumnIndex = cursor.getColumnIndex(ProdContract.ProductEntry.COLUMN_IMAGE_PRODUCT);
            int nameColumnIndex = cursor.getColumnIndex(ProdContract.ProductEntry.COLUMN_NAME_PRODUCT);
            int priceColumnIndex = cursor.getColumnIndex(ProdContract.ProductEntry.COLUMN_PRICE_PRODUCT);
            int quantityColumnIndex = cursor.getColumnIndex(ProdContract.ProductEntry.COLUMN_QUANTITY_PRODUCT);


            String name = cursor.getString(nameColumnIndex);
            float price = cursor.getFloat(priceColumnIndex);
            currentPhotoUri = cursor.getString(imageColumnIndex);
            int quantity = cursor.getInt(quantityColumnIndex);


            Picasso.with(this).load(currentPhotoUri)
                    .placeholder(R.drawable.add_image)
                    .fit()
                    .into(prodImage);

            prodName.setText(name);
            prodPrice.setText(String.valueOf(price) + " $");
            prodQuantity.setText(String.valueOf(quantity));


        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        prodName.setText("");
        prodPrice.setText(String.valueOf(""));
        prodQuantity.setText("");
    }

    private void showDialogRequestMerchandise() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setPositiveButton(R.string.requesty, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                requestStock();
            }
        });
        builder.setNegativeButton(R.string.requestd, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.setTitle(R.string.requestProd);
        alertDialog.show();
    }

    private void requestStock() {
        String name = " of type " + prodName.getText().toString();
        String stock = prodQuantity.getText().toString() + " pieces";

        StringBuilder builder = new StringBuilder();
        builder.append("Request for " + stock + name);
        String request = builder.toString();

        Intent requestIntent = new Intent(Intent.ACTION_SEND);
        requestIntent.setData(Uri.parse("mailto:"));
        requestIntent.setType("text/plain");
        requestIntent.putExtra(Intent.EXTRA_EMAIL, "supplier@yahoo.com");
        requestIntent.putExtra(Intent.EXTRA_SUBJECT, "Stock resupply ");
        requestIntent.putExtra(Intent.EXTRA_TEXT, request);
        startActivity(Intent.createChooser(requestIntent, "Requesting products"));
    }


}

