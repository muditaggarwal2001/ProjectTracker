package com.example.mudit.projecttracker;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.design.widget.FloatingActionButton;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * An activity representing a list of projects. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link projectDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
public class projectListActivity extends AppCompatActivity {

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private boolean mTwoPane;
    private static String incproj="";
    private boolean launch = true;
    static Date currentDate;
    static SimpleDateFormat sdf;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());
        sdf = new SimpleDateFormat("dd/MM/yyyy");
        currentDate = new Date();
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(projectListActivity.this,NewProject.class));
            }
        });

        if (findViewById(R.id.project_detail_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-w900dp).
            // If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        File[] files = getApplicationContext().getFilesDir().listFiles();
        if (files!=null) {
            Utils.ITEMS = Arrays.asList(files);
        }
        else {
            Utils.ITEMS = new ArrayList<File>();
        }
        View recyclerView = findViewById(R.id.project_list);
        assert recyclerView != null;
        setupRecyclerView((RecyclerView) recyclerView);
        if(launch==true&&!incproj.isEmpty())
        {
            launch=false;
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Projects due in 2 Days:");
            builder.setMessage(incproj);
            builder.setPositiveButton("OK",null);
            builder.create().show();
        }
    }

    private void setupRecyclerView(@NonNull RecyclerView recyclerView) {
        recyclerView.setAdapter(new SimpleItemRecyclerViewAdapter(this, Utils.ITEMS, mTwoPane));
    }

    public static class SimpleItemRecyclerViewAdapter
            extends RecyclerView.Adapter<SimpleItemRecyclerViewAdapter.ViewHolder> {

        private final projectListActivity mParentActivity;
        private final List<File> mValues;
        private final boolean mTwoPane;
        private final View.OnClickListener mOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int item = (int) view.getTag();
                if (mTwoPane) {
                    Bundle arguments = new Bundle();
                    arguments.putString(projectDetailFragment.ARG_ITEM_ID, String.valueOf(item));
                    projectDetailFragment fragment = new projectDetailFragment();
                    fragment.setArguments(arguments);
                    mParentActivity.getSupportFragmentManager().beginTransaction()
                            .replace(R.id.project_detail_container, fragment)
                            .commit();
                } else {
                    Context context = view.getContext();
                    Intent intent = new Intent(context, projectDetailActivity.class);
                    intent.putExtra(projectDetailFragment.ARG_ITEM_ID, String.valueOf(item));
                    context.startActivity(intent);
                }
            }
        };

        SimpleItemRecyclerViewAdapter(projectListActivity parent,
                                      List<File> items,
                                      boolean twoPane) {
            mValues = items;
            mParentActivity = parent;
            mTwoPane = twoPane;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.project_list_content, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            fileContentmanager contentmanager = new fileContentmanager(mValues.get(position));
            holder.mIdView.setText("Project: "+contentmanager.getPnumber());
            holder.mContentView.setText(contentmanager.getStatus());
            try {
                Long difference=currentDate.getTime()-sdf.parse(contentmanager.getDateview()).getTime();
                Long days=difference/86400000;
                if(days<2&&days>=0)
                {
                   incproj+="Project: "+contentmanager.getPnumber()+"\n";
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }

            holder.itemView.setTag(position);
            holder.itemView.setOnClickListener(mOnClickListener);
        }

        @Override
        public int getItemCount() {
            return mValues.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            final TextView mIdView;
            final TextView mContentView;

            ViewHolder(View view) {
                super(view);
                mIdView = (TextView) view.findViewById(R.id.id_text);
                mContentView = (TextView) view.findViewById(R.id.content);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_list_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.about:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("About");
                builder.setMessage("Mudit Aggarwal\nmuditaggarwal@cmail.carleton.ca");
                builder.setPositiveButton("OK",null);
                builder.create().show();
                break;
        }
        return true;
    }
}
