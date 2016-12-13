package com.main.carassistant.activity;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import com.main.carassistant.App;
import com.main.carassistant.Constants.SqlConst;
import com.main.carassistant.R;
import com.main.carassistant.db.DbHelper;
import com.main.carassistant.model.Costs;

public class AllCostsActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{

    private DbHelper dbHelper;
    private SimpleCursorAdapter simpleCursorAdapter;
    private Spinner spinnerCategory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.all_costs_layout);

        spinnerCategory = (Spinner) findViewById(R.id.spinnerCategory);
        dbHelper = ((App) getApplication()).getDbHelper();
        String[] data = {"one", "two", "three"};
        String[] from = new String[] {Costs.DATE, Costs.COST, Costs.COMMENT};
        int[] to = new int[] {R.id.txtDateItem, R.id.txtCostItem, R.id.txtCommentItem};

        simpleCursorAdapter = new SimpleCursorAdapter(this, R.layout.all_costs_item_layout, null, from, to, 0);
        ListView listView = (ListView) findViewById(R.id.costsList);
        listView.setAdapter(simpleCursorAdapter);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        getSupportLoaderManager().initLoader(0, null, this);
        getSupportLoaderManager().getLoader(0).forceLoad();

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, data);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategory.setAdapter(adapter);
        spinnerCategory.setSelection(0);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new MyCursorLoader(this, dbHelper);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        simpleCursorAdapter.changeCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        simpleCursorAdapter.changeCursor(null);
    }

    static class MyCursorLoader extends CursorLoader {

        DbHelper dbHelper;

        private MyCursorLoader(Context context, DbHelper dbHelper) {
            super(context);
            this.dbHelper = dbHelper;
        }

        @Override
        public Cursor loadInBackground() {
            return dbHelper.query(SqlConst.QUERY_FOR_COSTS_LIST + DbHelper.getTableName(Costs.class));
        }
    }
}
