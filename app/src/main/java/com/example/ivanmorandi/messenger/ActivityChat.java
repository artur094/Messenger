package com.example.ivanmorandi.messenger;

import android.Manifest;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.telephony.SmsManager;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

import Database.DBManager;
import JavaClass.Chat;
import JavaClass.Message;
import JavaClass.Profile;
import Views.ChatListView;
import ObserverClass.SMSObserver;

public class ActivityChat extends AppCompatActivity implements Observer, NavigationView.OnNavigationItemSelectedListener {

    public static final int ACTIVITY_ID = 2;

    public static final int ACTION_RECEIVED_SMS = 0;
    public static final int ACTION_START_CHAT = 1;
    public static final int ACTION_READ_CHAT = 2;


    protected static final String ACTIVITY_MODE = "activity_mode";

    private static ArrayList<Message> notificationsToRemove;

    private boolean permission = false;
    private static final int PERMISSION_SEND_SMS_RESULT = 111;
    private Chat chat;
    private DBManager dbManager;
    private ChatListView chatListView;
    private int activity_caller = -1;


    public static void addNotificationToRemove(Message m){
        if(notificationsToRemove == null)
            notificationsToRemove = new ArrayList<>();

        notificationsToRemove.add(m);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        Toolbar toolbar = (Toolbar) findViewById(R.id.chat_toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        NavigationView navigationView = (NavigationView) findViewById(R.id.chat_nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        SMSObserver smsObserver = SMSObserver.getInstance();
        smsObserver.addObserver(this);

        dbManager = new DBManager(this);
        if(notificationsToRemove == null)
            notificationsToRemove = new ArrayList<>();

        chatListView = (ChatListView)findViewById(R.id.chatlistview);


        chat = (Chat)getIntent().getSerializableExtra("chat");

        if(getIntent().hasExtra("activity_caller")){
            activity_caller = (int)getIntent().getSerializableExtra("activity_caller");
        }

        chatListView.setSelection(chat.getMessages().size());

        this.setTitle(chat.getRecipient().getRecipientName());

        //getActionBar().setDisplayHomeAsUpEnabled(true);

        //getSupportActionBar().setTitle(chat.getRecipient().getRecipientName());
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //getActionBar().setTitle(chat.getRecipientName());

        final EditText editText = (EditText)findViewById(R.id.chatTextSend);

        for(Message m:chat.getMessages()){
            chatListView.addMessage(m);
        }

        int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS);

        if(permissionCheck == PackageManager.PERMISSION_GRANTED){
            permission = true;
        }else{
            requestPermission();
        }

        dbManager.updateUnreadedMessages(chat.getRecipient().getRecipientName(), "");


        ImageView sendButton = (ImageView)findViewById(R.id.chat_send);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!permission){
                    requestPermission();
                    return;
                }

                String text = editText.getText().toString();

                if(!text.equals("")) {
                    Message m = new Message();
                    m.setMessage(text);
                    m.setSentByMe(true);
                    m.setRecipient(chat.getRecipient().getRecipientName());

                    /*Intent sendIntent = new Intent(getApplicationContext(), SendSMS.class);
                    sendIntent.setAction(Intent.ACTION_SENDTO);
                    sendIntent.putExtra("address", (String)chat.getRecipient().getPhoneNumbers().toArray()[0]);
                    sendIntent.putExtra("sms_body", m.getMessage());
                    sendIntent.setType("vnd.android-dir/mms-sms");
                    startActivity(sendIntent);*/

                    SmsManager smsManager = SmsManager.getDefault();
                    smsManager.sendTextMessage((String)chat.getRecipient().getPhoneNumbers().toArray()[0], null, m.getMessage(), null, null);

                    chat.addMessage(m);
                    chatListView.addMessage(m);
                    editText.setText("");


                    if(dbManager.getChat(chat.getRecipient().getRecipientName()) == null) //Not saved yet
                    {
                        dbManager.insertChat(chat);
                        chat = dbManager.getChat(chat.getRecipient().getRecipientName());
                    }
                    else{
                        dbManager.insertMessage(m);
                    }




                }
            }
        });

        editText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    chatListView.setSelection(chat.getMessages().size());
                }
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case PERMISSION_SEND_SMS_RESULT:{
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    permission = true;
                }
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Store our shared preference
        /*SharedPreferences sp = getSharedPreferences("a", MODE_PRIVATE);
        SharedPreferences.Editor ed = sp.edit();
        ed.putBoolean(ACTIVITY_MODE, true);
        ed.commit();*/

        for(Message m : notificationsToRemove){
            removeNotification(m);
        }
        notificationsToRemove.clear();
    }

    @Override
    protected void onPause() {
        super.onPause();

        // Store our shared preference
        /*SharedPreferences sp = getSharedPreferences("a", MODE_PRIVATE);
        SharedPreferences.Editor ed = sp.edit();
        ed.putBoolean(ACTIVITY_MODE, false);
        ed.commit();*/
    }

    private void requestPermission(){
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS}, PERMISSION_SEND_SMS_RESULT);
    }

    protected Chat getChat(){
        return null;
    }

    @Override
    public void update(Observable observable, Object data) {
        Message m = (Message)data;

        // Store our shared preference
        //SharedPreferences sp = getSharedPreferences("a", MODE_PRIVATE);
        //boolean is_active = sp.getBoolean(ACTIVITY_MODE, false);

        if(chat.getRecipient().getRecipientName().equals(m.getRecipient())){
            chat.addMessage(m);
            chatListView.addMessage(m);

            removeNotification(m);
        }

    }

    @Override
    public boolean onSupportNavigateUp() {
        switch (activity_caller){
            case ActivityContacts.ACTIVITY_ID:
                Intent intent = new Intent(this, ActivityContacts.class);
                startActivity(intent);
                return true;
            default:return super.onSupportNavigateUp();
        }
    }



    protected void removeNotification(Message m){
        String ns = Context.NOTIFICATION_SERVICE;
        Profile p = dbManager.getContact(m.getRecipient());
        NotificationManager nMgr = (NotificationManager) this.getSystemService(ns);
        nMgr.cancel((int)p.getId());
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

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
