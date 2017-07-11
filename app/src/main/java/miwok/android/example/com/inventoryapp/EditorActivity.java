package miwok.android.example.com.inventoryapp;

import android.Manifest;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.io.File;

import miwok.android.example.com.inventoryapp.data.ProdContract;

/**
 * Created by Paul on 09-Jul-17.
 */

public class EditorActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<Cursor> {


    private static final int EXISTING_PRODUCT_LOADER = 0;

    private Uri currentProductUri;

    private ImageView imageView;

    private EditText editTextName;

    private EditText editTextPrice;

    private EditText editTextQuantity;

    public static final int PHOTO_REQUEST_CODE = 20;

    public static final int EXTERNAL_STORAGE_REQUEST_PERMISSION_CODE = 21;

    private String currentPhotoUri = "N/A";

    private boolean isProductEdited = false;

    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        imageView = (ImageView) findViewById(R.id.product_image_editor);
        editTextName = (EditText) findViewById(R.id.product_name_editor);
        editTextPrice = (EditText) findViewById(R.id.product_price_editor);
        editTextQuantity = (EditText) findViewById(R.id.product_quantity_editor);

        editTextName.setOnTouchListener(mTouchListener);
        editTextPrice.setOnTouchListener(mTouchListener);
        editTextQuantity.setOnTouchListener(mTouchListener);

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updatePhoto(v);
            }
        });

        Intent intent = getIntent();
        currentProductUri = intent.getData();

        if (currentProductUri == null) {
            setTitle(getString(R.string.new_product));
            invalidateOptionsMenu();

        } else {
            setTitle(getString(R.string.edit_product));
            getSupportLoaderManager().initLoader(EXISTING_PRODUCT_LOADER, null, this);
        }
    }

    private void showDeleteConfirmationDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_product);
        builder.setPositiveButton(R.string.delete_product, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                deleteProduct();
            }
        });
        builder.setNegativeButton(R.string.discard, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void deleteProduct() {

        if (currentProductUri != null) {

            int rowsDeleted = getContentResolver().delete(currentProductUri, null, null);
            if (rowsDeleted == 0) {
                Toast.makeText(this, R.string.deleted, Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(EditorActivity.this, MainActivity.class);
                startActivity(intent);
            } else {
                Toast.makeText(this, R.string.error_deleting, Toast.LENGTH_SHORT).show();
            }
            finish();
        }
    }

    private void showDiscardChanges(
            DialogInterface.OnClickListener discardButtonClickListener) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.discard);
        builder.setPositiveButton(R.string.discard, discardButtonClickListener);
        builder.setNegativeButton(R.string.continue_editing, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    @Override
    public void onBackPressed() {

        if (!isProductEdited) {
            super.onBackPressed();
            return;
        }

        DialogInterface.OnClickListener discardButtonClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        finish();
                    }
                };

        showDiscardChanges(discardButtonClickListener);
    }

    public void updatePhoto(View view) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) ==
                    PackageManager.PERMISSION_GRANTED) {
                selectPhoto();
            } else {
                String[] permisionRequest = {Manifest.permission.READ_EXTERNAL_STORAGE};
                requestPermissions(permisionRequest, EXTERNAL_STORAGE_REQUEST_PERMISSION_CODE);
            }
        } else {
            selectPhoto();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == EXTERNAL_STORAGE_REQUEST_PERMISSION_CODE && grantResults[0] ==
                PackageManager.PERMISSION_GRANTED) {

            selectPhoto();
        } else {

            Toast.makeText(this, R.string.permission, Toast.LENGTH_LONG).show();
        }
    }

    private void selectPhoto() {
        Intent photoSelector = new Intent(Intent.ACTION_PICK);

        File photoDirectory = Environment.getExternalStoragePublicDirectory
                (Environment.DIRECTORY_PICTURES);
        String pictureDirectoryPath = photoDirectory.getPath();

        Uri data = Uri.parse(pictureDirectoryPath);

        photoSelector.setDataAndType(data, "image/*");

        startActivityForResult(photoSelector, PHOTO_REQUEST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PHOTO_REQUEST_CODE && resultCode == RESULT_OK) {
            if (data != null) {
            }

            Uri mProductPhotoUri = data.getData();
            currentPhotoUri = mProductPhotoUri.toString();

            Picasso.with(this).load(mProductPhotoUri)
                    .placeholder(R.drawable.add_image)
                    .fit()
                    .into(imageView);
        }
    }

    private void addNewProduct() {
        String name = editTextName.getText().toString();
        String price = editTextPrice.getText().toString();
        String quantity = editTextQuantity.getText().toString();
        String image = imageView.toString();

        if (name.isEmpty() || price.isEmpty() || image.isEmpty() || quantity.isEmpty()) {
            Toast.makeText(this, R.string.fill, Toast.LENGTH_SHORT).show();
            return;
        }

        ContentValues values = new ContentValues();
        values.put(ProdContract.ProductEntry.COLUMN_IMAGE_PRODUCT, currentPhotoUri);
        values.put(ProdContract.ProductEntry.COLUMN_NAME_PRODUCT, name);
        values.put(ProdContract.ProductEntry.COLUMN_PRICE_PRODUCT, price);
        values.put(ProdContract.ProductEntry.COLUMN_QUANTITY_PRODUCT, quantity);
        values.put(ProdContract.ProductEntry.COLUMN_PRODUCT_SALES, 0.0);

        if (currentProductUri == null) {

            Uri insertedRow = getContentResolver().insert(ProdContract.ProductEntry.CONTENT_URI, values);

            if (insertedRow == null) {
                Toast.makeText(this, R.string.failed, Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, R.string.added, Toast.LENGTH_LONG).show();
            }
        } else {

            int rowUpdated = getContentResolver().update(currentProductUri, values, null, null);

            if (rowUpdated == 0) {
                Toast.makeText(this, R.string.failed, Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, R.string.updated, Toast.LENGTH_LONG).show();
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.editor_menu, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);

        if (currentProductUri == null) {
            MenuItem menuItem = menu.findItem(R.id.delete_product);
            menuItem.setVisible(false);
        }

        if (currentProductUri != null) {
            MenuItem menuItem = menu.findItem(R.id.saved_product);
            menuItem.setIcon(R.drawable.attachment);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.saved_product:
                addNewProduct();
                finish();
                return true;
            case R.id.delete_product:
                showDeleteConfirmationDialog();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = {
                ProdContract.ProductEntry._ID,
                ProdContract.ProductEntry.COLUMN_IMAGE_PRODUCT,
                ProdContract.ProductEntry.COLUMN_NAME_PRODUCT,
                ProdContract.ProductEntry.COLUMN_PRICE_PRODUCT,
                ProdContract.ProductEntry.COLUMN_QUANTITY_PRODUCT,
                ProdContract.ProductEntry.COLUMN_PRODUCT_SALES};

        return new CursorLoader(this,
                currentProductUri,
                projection,
                null,
                null,
                null);
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
            final Button decrementStockButton = (Button) findViewById(R.id.stock_decrement);
            final Button incrementStockButton = (Button) findViewById(R.id.stock_increment);

            String name = cursor.getString(nameColumnIndex);
            float price = cursor.getFloat(priceColumnIndex);
            int qnt = cursor.getInt(quantityColumnIndex);
            currentPhotoUri = cursor.getString(imageColumnIndex);

            editTextName.setText(name);
            editTextPrice.setText(String.valueOf(price));
            editTextQuantity.setText(String.valueOf(qnt));

            decrementStockButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    incrementStock();
                }
            });

            incrementStockButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    decrementStock();
                }
            });
            Picasso.with(this).load(currentPhotoUri)
                    .placeholder(R.drawable.add_image)
                    .fit()
                    .into(imageView);
        }


    }


    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        editTextName.setText("");
        editTextQuantity.setText("");
        editTextPrice.setText("");
    }
    private void incrementStock() {
        int quantity = Integer.parseInt(editTextQuantity.getText().toString());
        if (quantity > 0) {
            quantity--;
            editTextQuantity.setText(String.valueOf(quantity));
        }
    }

    private void decrementStock() {
        int quantity = Integer.parseInt(editTextQuantity.getText().toString());
        if (quantity >= 0) {
            quantity++;
            editTextQuantity.setText(String.valueOf(quantity));
        }
    }

}