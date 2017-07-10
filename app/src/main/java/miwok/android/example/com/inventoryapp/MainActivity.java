package miwok.android.example.com.inventoryapp;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import miwok.android.example.com.inventoryapp.data.ProdContract;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    ProductCursorAdapter mCursorAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);

        ListView productListView = (ListView) findViewById(R.id.product_list);

        productListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                fab.setSize(FloatingActionButton.SIZE_AUTO);
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount,
                                 int totalItemCount) {
                fab.setSize(FloatingActionButton.SIZE_MINI);
            }
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, EditorActivity.class);
                startActivity(intent);
            }
        });

        View emptyView = findViewById(R.id.empty_view);
        productListView.setEmptyView(emptyView);
        mCursorAdapter = new ProductCursorAdapter(this, null);
        productListView.setAdapter(mCursorAdapter);

        productListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Intent intent = new Intent(MainActivity.this, DetailViewActivity.class);

                Uri currentPetUri = ContentUris.withAppendedId(ProdContract.ProductEntry.CONTENT_URI, id);

                intent.setData(currentPetUri);

                startActivity(intent);
            }
        });

        getLoaderManager().initLoader(0, null, this);
    }

    private void addDummy() {

        ContentValues values = new ContentValues();
        values.put(ProdContract.ProductEntry.COLUMN_NAME_PRODUCT, "New Product");
        values.put(ProdContract.ProductEntry.COLUMN_PRICE_PRODUCT, "1.5");
        values.put(ProdContract.ProductEntry.COLUMN_QUANTITY_PRODUCT, 10);
        values.put(ProdContract.ProductEntry.COLUMN_PRODUCT_SALES, 0.0);

        Uri newUri = getContentResolver().insert(ProdContract.ProductEntry.CONTENT_URI, values);

        Toast.makeText(this, R.string.dummyData, Toast.LENGTH_SHORT).show();
    }

    private void showDeleteConfirmationDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.deleteAll);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                deleteAllProducts();
                Toast.makeText(MainActivity.this, R.string.deletedAll, Toast.LENGTH_SHORT).show();
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
        alertDialog.setTitle("DELETE COMPLETE LIST");
        alertDialog.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    private void deleteAllProducts() {
        int rowsDeleted = getContentResolver().delete(ProdContract.ProductEntry.CONTENT_URI, null, null);
        Log.v("DetailViewActivity", rowsDeleted + " rows deleted from pet database");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.settings:
                Toast.makeText(this, R.string.settingsButton, Toast.LENGTH_SHORT)
                        .show();
                return true;
            case R.id.dummy_data:
                addDummy();
                return true;
            case R.id.delete_all:
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


        return new CursorLoader(this, ProdContract.ProductEntry.CONTENT_URI, projection, null, null, null);
    }

    @Override
    public void onLoadFinished(android.content.Loader<Cursor> loader, Cursor data) {

        mCursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(android.content.Loader<Cursor> loader) {

        mCursorAdapter.swapCursor(null);
    }
}