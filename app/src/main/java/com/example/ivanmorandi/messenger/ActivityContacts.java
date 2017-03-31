package com.example.ivanmorandi.messenger;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import java.util.ArrayList;

import Database.Contacts;
import JavaClass.Profile;
import Views.ProfileListView;

public class ActivityContacts extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    public static final int ACTIVITY_ID = 1;

    private static final int PERMISSION_READ_CONTACTS_RESULT = 111;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);

        Toolbar toolbar = (Toolbar) findViewById(R.id.contact_toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.activity_contacts);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.contact_nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setCheckedItem(R.id.nav_contacts);

        int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS);

        if(permissionCheck == PackageManager.PERMISSION_GRANTED){
            readContacts();
        }else{
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_CONTACTS}, PERMISSION_READ_CONTACTS_RESULT);
        }
    }


    protected void readContacts(){
        ProfileListView profileListView = (ProfileListView)findViewById(R.id.profilelistview);
        profileListView.setActivityCaller(this.ACTIVITY_ID);

        Contacts contacts = MainActivity.contacts;
        ArrayList<Profile> profiles = contacts.getContacts();
        sort(profiles);

        for(Profile p : profiles){
            profileListView.addProfile(p);
        }
    }

    protected void sort(ArrayList<Profile> profiles){
        if(profiles.size() < 1)
            return;
        int min;

        for (int i=0;i<profiles.size();i++){
            min = i;
            for(int j=i+1;j<profiles.size();j++){
                if(profiles.get(j).compareTo(profiles.get(min))<0)
                {
                    min = j;
                }
            }
            if(min != i){
                Profile tmp = profiles.get(i);
                profiles.set(i, profiles.get(min));
                profiles.set(min, tmp);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case PERMISSION_READ_CONTACTS_RESULT:{
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    readContacts();
                }
            }
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
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_contacts) {

        } else if (id == R.id.nav_chat) {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_settings_telegram) {

        } else if (id == R.id.nav_settings_sms) {

        } else if (id == R.id.nav_settings_app) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.activity_contacts);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
