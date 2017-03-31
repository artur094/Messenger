package com.example.ivanmorandi.messenger;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Telephony;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.Semaphore;

import Database.Contacts;
import Database.DBManager;
import FragmentClass.ContactsFragment;
import JavaClass.Chat;
import JavaClass.Message;
import JavaClass.Profile;
import ObserverClass.SMSObserver;
import Views.ChatPreviewListView;
import Views.ChatPreviewView;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, ContactsFragment.OnFragmentInteractionListener, Observer {
    public static final int ACTIVITY_ID = 0;

    public static Contacts contacts;

    private static final int PERMISSION_SMS_RECEIVED = 111;
    private static final int PERMISSION_BROADCAST_WAP_PUSH = 222;

    DBManager dbManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        contacts = new Contacts(this);

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    contacts.loadContacts();
                }catch (InterruptedException ex){

                }
            }
        }).start();

        SMSObserver smsObserver = SMSObserver.getInstance();
        smsObserver.addObserver(this);

        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                removeDialog();

                android.app.FragmentTransaction ft = getFragmentManager().beginTransaction();

                ContactsFragment contactsFragment = ContactsFragment.newInstance();
                contactsFragment.show(ft, "dialog");


            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setCheckedItem(R.id.nav_chat);


        dbManager = new DBManager(this);

        ArrayList<Chat> chats = dbManager.getChats();

        ChatPreviewListView chatPreviewListView = (ChatPreviewListView)findViewById(R.id.list_chats);

        chatPreviewListView.addChats(chats);

        final String myPackageName = getPackageName();

        if (!Telephony.Sms.getDefaultSmsPackage(this).equals(myPackageName)) {
            Intent intent =
                    new Intent(Telephony.Sms.Intents.ACTION_CHANGE_DEFAULT);
            intent.putExtra(Telephony.Sms.Intents.EXTRA_PACKAGE_NAME,
                    myPackageName);
            startActivity(intent);
        }

        int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.RECEIVE_SMS);

        if(permissionCheck == PackageManager.PERMISSION_GRANTED){

        }else{
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECEIVE_SMS}, PERMISSION_SMS_RECEIVED);
        }

    }

    protected void askPermissions(){
        int permission_receive_sms = ContextCompat.checkSelfPermission(this, Manifest.permission.RECEIVE_SMS);
        int permission_broadcast_wap_push = ContextCompat.checkSelfPermission(this, Manifest.permission.BROADCAST_WAP_PUSH);

        if(permission_receive_sms == PackageManager.PERMISSION_GRANTED){

        }else{
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECEIVE_SMS}, PERMISSION_SMS_RECEIVED);
        }

        if(permission_broadcast_wap_push == PackageManager.PERMISSION_GRANTED){

        }else{
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.BROADCAST_WAP_PUSH}, PERMISSION_BROADCAST_WAP_PUSH);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onResume() {

        ArrayList<Chat> chats = dbManager.getChats();

        ChatPreviewListView chatPreviewListView = (ChatPreviewListView)findViewById(R.id.list_chats);

        chatPreviewListView.clear();

        chatPreviewListView.addChats(chats);

        removeDialog();

        super.onResume();
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
        if (id == R.id.action_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
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
            Intent intent = new Intent(this, ActivityContacts.class);
            startActivity(intent);
            // Handle the camera action
        } else if (id == R.id.nav_chat) {
            //This activity
        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_settings_sms) {

        } else if (id == R.id.nav_settings_telegram) {

        } else if (id == R.id.nav_settings_app) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    private void removeDialog(){
        android.app.FragmentTransaction ft = getFragmentManager().beginTransaction();
        android.app.Fragment prev = getFragmentManager().findFragmentByTag("dialog");
        if (prev != null){
            ft.remove(prev);
        }
        ft.addToBackStack(null);
    }

    @Override
    public void update(Observable observable, Object data) {
        Boolean found = false;
        Message m = (Message)data;

        ChatPreviewListView chatPreviewListView = (ChatPreviewListView)findViewById(R.id.list_chats);

        for(int i = 0;i<chatPreviewListView.getChildCount();i++){
            ChatPreviewView chat = (ChatPreviewView)chatPreviewListView.getChildAt(i);
            if(chat.getLastMessageRecipient().equals(m.getRecipient())){
                chat.getChat().addMessage(m);
                chat.getChat().setUnreadedSMS(m.getMessage());
                chat.update();
                chatPreviewListView.moveFirstPosition(chat.getChat());
                found = true;
            }
        }

        if(!found){
            Chat chat = new Chat(this);
            Profile p = dbManager.getContact(m.getRecipient());
            chat.setRecipient(p);
            chat.addMessage(m);
            chat.setUnreadedSMS(m.getMessage());

            chatPreviewListView.addChat(chat);
        }
    }
}
