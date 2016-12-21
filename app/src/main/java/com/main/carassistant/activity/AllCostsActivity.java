package com.main.carassistant.activity;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.main.carassistant.App;
import com.main.carassistant.Constants.SqlConst;
import com.main.carassistant.R;
import com.main.carassistant.db.DbHelper;
import com.main.carassistant.model.Costs;
import com.main.carassistant.threads.ResultCallback;

public class AllCostsActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private DbHelper dbHelper;
    private SimpleCursorAdapter simpleCursorAdapter;
    private Spinner spinnerCategory;
    private Handler handler;
    TextView txtTotalCost;
    private String filt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.all_costs_layout);

        spinnerCategory = (Spinner) findViewById(R.id.spinnerCategory);
        txtTotalCost = (TextView) findViewById(R.id.txtTotalCost);
        dbHelper = ((App) getApplication()).getDbHelper();
        handler = new Handler();
        String[] data = {"one", "two", "three"};
        String[] from = new String[]{Costs.DATE, Costs.COST, Costs.COMMENT};
        int[] to = new int[]{R.id.txtDateItem, R.id.txtCostItem, R.id.txtCommentItem};

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

        setTotalCost(dbHelper, handler, new ResultCallback<Cursor>() {
            @Override
            public void onSuccess(Cursor result) {
                txtTotalCost.setText(result.getString(0));
            }
        });


        spinnerCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String query;
                if (spinnerCategory.getSelectedItem().toString().equals("All")) {
                    query = SqlConst.QUERY_FOR_COSTS_LIST + DbHelper.getTableName(Costs.class);
                } else {
                    query = SqlConst.QUERY_FOR_COSTS_LIST + DbHelper.getTableName(Costs.class) +
                            " WHERE " + Costs.CATEGORY + " = '" + spinnerCategory.getSelectedItem().toString() + "'";
                }
                selectFiltered(dbHelper, query, handler, new ResultCallback<Cursor>() {
                    @Override
                    public void onSuccess(Cursor result) {
                        simpleCursorAdapter.changeCursor(result);
                        simpleCursorAdapter.notifyDataSetChanged();
                    }
                });
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
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
        return new MyCursorLoader(this, dbHelper, filt);
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
        String filter;

        private MyCursorLoader(Context context, DbHelper dbHelper, String pos) {
            super(context);
            this.dbHelper = dbHelper;
            this.filter = pos;
        }

        @Override
        public Cursor loadInBackground() {
//            if (filter == "one") {
//                return dbHelper.query(SqlConst.QUERY_FOR_COSTS_LIST + DbHelper.getTableName(Costs.class));
//            } else {
//                return dbHelper.query("SELECT * FROM " + DbHelper.getTableName(Costs.class) + " WHERE " + Costs.CATEGORY + " = " + filter);
//            }
            return dbHelper.query(SqlConst.QUERY_FOR_COSTS_LIST + DbHelper.getTableName(Costs.class));
        }
    }

    private void setTotalCost(final DbHelper dbHelper, final Handler handler, final ResultCallback<Cursor> callback) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                final Cursor cursor = dbHelper.query("SELECT SUM(" + Costs.COST + ") FROM " + DbHelper.getTableName(Costs.class));
                if (cursor.getCount() > 0) {
                    cursor.moveToFirst();
                }

                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        callback.onSuccess(cursor);
                    }
                });
            }
        }).start();
    }


    public void onSpinner(View view) {
        String quer = SqlConst.QUERY_FOR_COSTS_LIST + DbHelper.getTableName(Costs.class) + " WHERE " + Costs.CATEGORY + " = 'two'";

        selectFiltered(dbHelper, quer, handler, new ResultCallback<Cursor>() {
            @Override
            public void onSuccess(Cursor result) {
                simpleCursorAdapter.changeCursor(result);
                simpleCursorAdapter.notifyDataSetChanged();
            }
        });
    }

    private void selectFiltered(final DbHelper dbHelper, final String query, final Handler handler, final ResultCallback<Cursor> callback) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                final Cursor cursor = dbHelper.query(query);
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        callback.onSuccess(cursor);
                    }
                });
            }
        }).start();
    }
}
