package com.example.mudit.projecttracker;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
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
import android.view.ViewTreeObserver;
import android.widget.TextView;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

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
    private static String incproj;
    private boolean launch;
    static Date currentDate;
    static SimpleDateFormat sdf;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent musicService = new Intent(getApplicationContext(),MusicPlayer.class);
        startService(musicService);
        incproj="";
        launch=true;
        setContentView(R.layout.activity_project_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());
        sdf = new SimpleDateFormat("dd/MM/yyyy");
        sdf.setTimeZone(TimeZone.getDefault());
        currentDate = Calendar.getInstance().getTime();
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

    void fillList()
    {
        File[] files = getApplicationContext().getFilesDir().listFiles();
        if (files!=null) {
            Utils.ITEMS = Arrays.asList(files);
        }
        else {
            Utils.ITEMS = new ArrayList<File>();
        }
        final View recyclerView = findViewById(R.id.project_list);
        assert recyclerView != null;
        setupRecyclerView((RecyclerView) recyclerView);
        final View view=findViewById(R.id.colayout);
        recyclerView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (!incproj.isEmpty()&&launch==true){
                final Snackbar snackbar = Snackbar.make(view,"Projects due in 2 Days:\n"+incproj,Snackbar.LENGTH_INDEFINITE);
                    launch=false;
                    incproj="";
                    snackbar.setAction("OK", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        snackbar.dismiss();
                    }
                });
                snackbar.show();
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        fillList();
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
                    Context context = view.getContext();
                    Intent intent = new Intent(context, projectDetailActivity.class);
                    intent.putExtra(projectDetailFragment.ARG_ITEM_ID, String.valueOf(item));
                    context.startActivity(intent);

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
                Long difference;
                difference = sdf.parse(contentmanager.getDateview()).getTime()-sdf.parse(sdf.format(currentDate)).getTime();
                Long days=difference/86400000; //divided by no. of milliseconds in a day to get no. of days.
                if(days<2&&contentmanager.getStatus().equalsIgnoreCase("Incomplete"))
                {
                   incproj+="Project: "+contentmanager.getPnumber()+"\n";
                }
                System.out.println("Printing incomplete project:");
                System.out.println(incproj);
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
