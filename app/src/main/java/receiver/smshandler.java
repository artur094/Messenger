package receiver;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.telephony.SmsMessage;

import com.example.ivanmorandi.messenger.ActivityChat;
import com.example.ivanmorandi.messenger.MainActivity;
import com.example.ivanmorandi.messenger.R;

import java.util.Calendar;
import java.util.HashSet;

import Database.DBManager;
import JavaClass.Chat;
import Database.Contacts;
import JavaClass.Message;
import JavaClass.Profile;
import ObserverClass.SMSObserver;

public class smshandler extends BroadcastReceiver {
    public smshandler() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle bundle = intent.getExtras();
        SmsMessage[] smsMessages = null;
        DBManager dbManager = new DBManager(context);

        if(bundle != null){
            Object[] pdus = (Object[]) bundle.get("pdus");
            smsMessages = new SmsMessage[pdus.length];

            for (int i=0;i<pdus.length;i++){
                Message m;
                smsMessages[i] = getIncomingSMS(pdus[i], bundle);

                //Save most useful data
                String from = smsMessages[i].getDisplayOriginatingAddress();
                String sms_body = smsMessages[i].getMessageBody().toString();

                //Insert data into the DB
                //Before I've to search if the sender has already a chat
                Profile p = dbManager.getContactFromPhoneNumber(from);
                if(p != null)
                {
                    //If there is the contact, then there is also the chat and I can add the message
                    m = new Message();
                    m.setDate(Calendar.getInstance());
                    m.setMessage(sms_body);
                    m.setSentByMe(false);
                    m.setRecipient(p.getRecipientName());

                    dbManager.insertMessage(m);

                    if(dbManager.getChat(p.getRecipientName()) == null){
                        Chat c = new Chat(context);
                        c.setRecipient(p);
                        dbManager.insertChat(c);
                    }
                }else{
                    //In this case there are not chat stored in the DB
                    //So let's search if there is the contact in the phone
                    Contacts contacts = MainActivity.contacts;
                    p = contacts.getContactFromPhoneNumber(from);

                    if(p!=null){
                        //There is the contact in the phone
                        dbManager.insertContact(p);
                    }
                    else{
                        //There is no info about this phone number, so let's add it using the phonenumber as recipient
                        p = new Profile(context);
                        p.setRecipientName(from);
                        HashSet<String> phoneNumbers = new HashSet<>();
                        phoneNumbers.add(from);
                        p.setPhoneNumber(phoneNumbers);
                        p.setProfileImage("");
                        dbManager.insertContact(p);
                    }

                    m = new Message();
                    m.setMessage(sms_body);
                    m.setRecipient(p.getRecipientName());
                    m.setDate(Calendar.getInstance());
                    m.setSentByMe(false);

                    Chat c = new Chat(context);
                    c.setRecipient(p);
                    c.addMessage(m);

                    dbManager.insertChat(c);

                }
                Chat c = dbManager.getChat(p.getRecipientName());

                if(c.getUnreadedSMS().equals(""))
                    c.setUnreadedSMS(sms_body);
                else
                    c.setUnreadedSMS(c.getUnreadedSMS() + "\n" + sms_body);
                dbManager.updateUnreadedMessages(p.getRecipientName(), c.getUnreadedSMS());

                //Notify the user that he/she has received an sms
                sendNotification(context, p.getRecipientName(),sms_body, c.getUnreadedSMS(), c);


                SMSObserver.getInstance().updateValue(m);
                ActivityChat.addNotificationToRemove(m);
            }
        }
    }


    protected SmsMessage getIncomingSMS(Object obj, Bundle bundle){
        SmsMessage currentSMS;
        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.M){
            String format = bundle.getString("format");
            currentSMS = SmsMessage.createFromPdu((byte[])obj, format);
        }
        else{
            currentSMS = SmsMessage.createFromPdu((byte[])obj);
        }
        return currentSMS;
    }

    protected void sendNotification(Context context,String title, String last_sms, String body, Chat chat){
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(context)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle(title)
                        .setContentText(last_sms)
                        .setAutoCancel(true)
                        .setOngoing(false)
                        .setStyle(new NotificationCompat.BigTextStyle().bigText(body));

        Intent resultIntent = new Intent(context, ActivityChat.class);
        resultIntent.putExtra("chat",chat);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addParentStack(MainActivity.class);
        stackBuilder.addNextIntent(resultIntent);

        PendingIntent pendingIntent = stackBuilder.getPendingIntent(
                0,
                PendingIntent.FLAG_UPDATE_CURRENT
        );

        mBuilder.setContentIntent(pendingIntent);

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify((int)chat.getRecipient().getId(), mBuilder.build());

    }
}
