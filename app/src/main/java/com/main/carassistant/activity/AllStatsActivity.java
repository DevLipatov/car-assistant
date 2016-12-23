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
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import com.main.carassistant.App;
import com.main.carassistant.constants.SqlConst;
import com.main.carassistant.R;
import com.main.carassistant.db.DbHelper;
import com.main.carassistant.model.Stats;

public class AllStatsActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private DbHelper dbHelper;
    private SimpleCursorAdapter simpleCursorAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_stats);

        dbHelper = ((App) getApplication()).getDbHelper();
        String[] from = new String[]{Stats.FUELING, Stats.OIL_FILLED, Stats.DATE};
        int[] to = new int[]{R.id.txtFuelingItem, R.id.txtOilFilledItem, R.id.txtDateItem};

        simpleCursorAdapter = new SimpleCursorAdapter(this, R.layout.all_stats_item_layout, null, from, to, 0);
        ListView listView = (ListView) findViewById(R.id.statsList);
        listView.setAdapter(simpleCursorAdapter);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        getSupportLoaderManager().initLoader(0, null, this);
        getSupportLoaderManager().getLoader(0).forceLoad();
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
            return dbHelper.query(SqlConst.QUERY_FOR_STATS_LIST + DbHelper.getTableName(Stats.class));
        }
    }
}
