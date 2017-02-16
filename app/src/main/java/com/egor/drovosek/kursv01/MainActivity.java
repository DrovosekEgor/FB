package com.egor.drovosek.kursv01;

import android.app.ProgressDialog;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ExpandableListView;
import android.widget.Toast;
import android.os.Handler;
import android.os.Message;

import com.egor.drovosek.kursv01.DB.FootballDBHelper;
import com.egor.drovosek.kursv01.DB.Schema;
import com.egor.drovosek.kursv01.MainWindowTabFragments.BestPlayersTab.BestPlayersTabFragment;
import com.egor.drovosek.kursv01.MainWindowTabFragments.NewsTab.NewsTabFragment;
import com.egor.drovosek.kursv01.MainWindowTabFragments.ScheduleTab.ScheduleTabFragment;
import com.egor.drovosek.kursv01.MainWindowTabFragments.TableStats.TableTabFragment;
import com.egor.drovosek.kursv01.MainWindowTabFragments.ViewPagerAdapter;
import com.egor.drovosek.kursv01.Misc.DataMinerWorkerThread;
import com.egor.drovosek.kursv01.Misc.GrabTeamsRunnable;
import com.egor.drovosek.kursv01.Misc.Team;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    //todo: create global class
    public static int gdSeason = 2016;
    public static int gdNumberOfRounds = 6; //todo определить количество сезонов

    //private SectionsPagerAdapter mSectionsPagerAdapter;
    private ProgressDialog progressState;
    private ViewPager mViewPager;

    View view_Group;
    private DrawerLayout mDrawerLayout;
    ExpandableListAdapter mMenuAdapter;
    ExpandableListView expandableList;
    List<String> listDataHeader;
    HashMap<String, List<Team>> listDataChild;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //getSupportActionBar().setDisplayShowTitleEnabled(false);
        //getSupportActionBar().setTitle(title);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.actionbar_title);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this,
                drawer,
                toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close);

        drawer.setDrawerListener(toggle);
        toggle.syncState();

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        expandableList = (ExpandableListView) findViewById(R.id.navigationmenu);
        prepareLeftMenuItems();

        mMenuAdapter = new ExpandableListAdapter(this, listDataHeader, listDataChild);

        // setting list adapter
        expandableList.setAdapter(mMenuAdapter);
        /*expandableList.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView expandableListView,
                                        View view,
                                        int groupPosition,
                                        int childPosition, long id) {
                Log.d("DEBUG", "submenu item clicked");
                Toast.makeText(MainActivity.this,
                        "Header: "+String.valueOf(groupPosition) +
                                "\nItem: "+ String.valueOf(childPosition), Toast.LENGTH_SHORT)
                        .show();
                view.setSelected(true);
                if (view_Group != null) {
                    view_Group.setBackgroundColor(Color.parseColor("#ffffff"));
                }
                view_Group = view;
                view_Group.setBackgroundColor(Color.parseColor("#DDDDDD"));
                mDrawerLayout.closeDrawers();
                return false;
            }
        });
        expandableList.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView expandableListView, View view, int i, long l) {
                Log.d("DEBUG", "heading clicked");
                return false;
            }
        });*/
        // добавление списка комманд
        /*Menu navMenu = navigationView.getMenu();
        SubMenu subMenu = navMenu.getItem(0).getSubMenu();
        subMenu.add("Test1");*/

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        //mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        //mViewPager.setAdapter(mSectionsPagerAdapter);
        setupViewPager(mViewPager);
        mViewPager.setOffscreenPageLimit(4);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        DataMinerWorkerThread dataMinerThread;

        dataMinerThread = new DataMinerWorkerThread("DataMinerThread", mUIHandler);

        dataMinerThread.sendHello();

        dataMinerThread.postTask(new GrabTeamsRunnable(mUIHandler));
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new TableTabFragment(), "ТАБЛИЦА");
        adapter.addFragment(new ScheduleTabFragment(), "РАСПИСАНИЕ");
        adapter.addFragment(new NewsTabFragment(), "НОВОСТИ");
        adapter.addFragment(new BestPlayersTabFragment(), "БОМБАРДИРЫ");
        viewPager.setAdapter(adapter);

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //нажали кнопку обновить данные (на первом шаге получаем данные)
        if (id == R.id.action_refresh) {
            Toast.makeText(MainActivity.this,"Refresh started!", Toast.LENGTH_SHORT).show();

            //progressState = ProgressDialog.show(MainActivity.this, "", "Воруем данные с football.by...");
            /*new Thread() {
                public void run() {
                    //grab data from football.by
                    try {
                        TimeUnit.SECONDS.sleep(15);
                    }catch (InterruptedException e){
                        e.printStackTrace();
                    }
                    mUIHandler.sendEmptyMessage(0);
                }
            }.start();*/

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private Handler mUIHandler = new Handler() {
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            Bundle bundle = msg.getData();
            String dataFromMSG = bundle.getString("mydata");

            Log.i("MAIN", "mUIHandler got message " + dataFromMSG);
            Toast.makeText(MainActivity.this, dataFromMSG, Toast.LENGTH_SHORT).show();
            //progressState.dismiss();
        }
    };

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_teams_) {
            // Handle the camera action
        } else if (id == R.id.nav_settings_) {

        } else if (id == R.id.nav_exit) {
            finish();
        } else if (id == R.id.nav_rate_) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void prepareLeftMenuItems() {
        listDataHeader = new ArrayList<String>();
        listDataChild = new HashMap<String, List<Team>>();

        // Adding data header
        listDataHeader.add(getString(R.string.nav_teams));
        listDataHeader.add(getString(R.string.nav_settings));
        listDataHeader.add(getString(R.string.nav_rate));
        listDataHeader.add(getString(R.string.nav_exit));

        FootballDBHelper mDB = new FootballDBHelper(getApplicationContext());

        List<Team> teamsList = mDB.getListTeams(gdSeason);

        mDB.close();

        listDataChild.put(listDataHeader.get(0), teamsList);// Header, Child data
    }

}
