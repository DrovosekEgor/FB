package com.egor.drovosek.kursv01;

import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.view.LayoutInflater;
import android.view.SubMenu;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.TextView;
import android.widget.Toast;

import com.egor.drovosek.kursv01.DB.DataMiner;
import com.egor.drovosek.kursv01.DB.FootballDBHelper;
import com.egor.drovosek.kursv01.DB.Schema;
import com.egor.drovosek.kursv01.MainWindowTabFragments.BestPlayersTabFragment;
import com.egor.drovosek.kursv01.MainWindowTabFragments.NewsTabFragment;
import com.egor.drovosek.kursv01.MainWindowTabFragments.ScheduleTabFragment;
import com.egor.drovosek.kursv01.MainWindowTabFragments.TableTabFragment;
import com.egor.drovosek.kursv01.MainWindowTabFragments.ViewPagerAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.egor.drovosek.kursv01.R.string.title;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    //todo: create global class
    public static int gdSeason = 2016;
    public static int gdNumberOfRounds = 3; //todo определить количество сезонов

    //private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;

    View view_Group;
    private DrawerLayout mDrawerLayout;
    ExpandableListAdapter mMenuAdapter;
    ExpandableListView expandableList;
    List<String> listDataHeader;
    HashMap<String, List<String>> listDataChild;

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
        prepareListData();

        mMenuAdapter = new ExpandableListAdapter(this, listDataHeader, listDataChild);

        // setting list adapter
        expandableList.setAdapter(mMenuAdapter);
        expandableList.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView expandableListView,
                                        View view,
                                        int groupPosition,
                                        int childPosition, long id) {
                //Log.d("DEBUG", "submenu item clicked");
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
                //Log.d("DEBUG", "heading clicked");
                return false;
            }
        });
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

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

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

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_refresh) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        /*if (id == R.id.nav_teams) {
            // Handle the camera action
        } else if (id == R.id.nav_settings) {

        } else if (id == R.id.nav_exit) {

        } else if (id == R.id.nav_rate) {

        }*/

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void prepareListData() {
        listDataHeader = new ArrayList<String>();
        listDataChild = new HashMap<String, List<String>>();

        // Adding data header
        listDataHeader.add(getString(R.string.nav_teams));
        listDataHeader.add(getString(R.string.nav_settings));
        listDataHeader.add(getString(R.string.nav_rate));
        listDataHeader.add(getString(R.string.nav_exit));

        // Adding child data
        // получаем список комманд из db table TEAMS
        FootballDBHelper mDB = new FootballDBHelper(getApplicationContext());
        Cursor curs = mDB.getAllTeams(2016);

        List<String> heading1 = new ArrayList<String>();

        if (curs != null && curs.getCount()>0) {

            curs.moveToFirst();
            while (!curs.isLast())
            {
                String tmpWName = curs.getString(curs.getColumnIndex(Schema.TEAMS_TITLE));
                heading1.add(tmpWName);
                curs.moveToNext();
            }
            String tmpWName = curs.getString(curs.getColumnIndex(Schema.TEAMS_TITLE));
            heading1.add(tmpWName);
        }
        else {
            heading1.add("Динамо Минск");
            heading1.add("БАТЭ");
            heading1.add("Шахтер Солигорск");
        }

        listDataChild.put(listDataHeader.get(0), heading1);// Header, Child data
    }
}
