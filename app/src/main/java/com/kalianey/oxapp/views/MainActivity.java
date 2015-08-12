package com.kalianey.oxapp.views;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.kalianey.oxapp.R;
import com.kalianey.oxapp.utils.SessionManager;
import com.kalianey.oxapp.utils.QueryAPI;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    private SessionManager session;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private ListView navList;
    private FragmentTransaction fragmentTransaction;
    private FragmentManager fragmentManager;
    private QueryAPI query= new QueryAPI();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        navList = (ListView) findViewById(R.id.nav_list);
        ArrayList<String> navArray = new ArrayList<String>();
        navArray.add("People");
        navArray.add("Conversations");
        navArray.add("Friends");
        navArray.add("Favorites");
        navArray.add("Profile");
        navArray.add("Account");
        navList.setChoiceMode(ListView.CHOICE_MODE_SINGLE); //retain the choice of menu item
        //simple_list_item_activated_1 show colour on selected item
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_activated_1, navArray);
        navList.setAdapter(adapter);
        //Item listener for when the user click on an item we load a different fragment
        navList.setOnItemClickListener(this);

        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.openDrawer, R.string.closeDrawer);
        drawerLayout.setDrawerListener(actionBarDrawerToggle);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        fragmentManager = getFragmentManager();

        session = new SessionManager(getApplicationContext().getApplicationContext());

        // Check if user is already logged in or not
        if (!session.isLoggedIn()) {
            // User is not already logged in. Take him to signin
            Log.d("PeopleUserLoggedIn", "false");
            Intent intent = new Intent(getApplicationContext(), SignIn.class);
            startActivity(intent);
        }

        query.login("kalianey", "Fxvcoar123@Sal", new QueryAPI.ApiResponse<QueryAPI.ApiResult>() {
            @Override
            public void onCompletion(QueryAPI.ApiResult res) {

                if (res.success) {
                    Log.v("Login Successful", res.success.toString());

                    //Load first menu item by default
                    loadSelection(0);
                }
            }

        });

    }

    private void loadSelection(int i){
        navList.setItemChecked(i, true);
        switch (i){
            case 0:
                PeopleFragment peopleFragment = new PeopleFragment();
                fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.fragment_holder, peopleFragment);
                fragmentTransaction.commit();
                break;
            case 1:
                ConversationListFragment conversationListFragment = new ConversationListFragment();
                fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.fragment_holder, conversationListFragment);
                fragmentTransaction.commit();
                break;

            case 2:

                break;
            case 3:

                break;
            case 4:

                break;

        }
    }

    //Called when my objects are created
    @Override
    public void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        actionBarDrawerToggle.syncState();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        } else if (id == android.R.id.home){
            if (drawerLayout.isDrawerOpen(navList)) {
                drawerLayout.closeDrawer(navList);
            } else {
                drawerLayout.openDrawer(navList);
            }
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        loadSelection(position);

        //once click, dismiss drawer
        drawerLayout.closeDrawer(navList);

    }
}
