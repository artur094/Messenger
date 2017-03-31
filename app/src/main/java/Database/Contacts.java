package Database;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import com.example.ivanmorandi.messenger.R;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.concurrent.Semaphore;

import Database.DBManager;
import JavaClass.Profile;

/**
 * Created by ivanmorandi on 24/02/2017.
 */

public class Contacts {
    private static boolean contacts_changed = false;
    private static ArrayList<Profile> contacts = null;
    private final Semaphore contacts_semaphore = new Semaphore(1, true);
    private Context context;


    public Contacts(Context context){
        this.context = context;
        this.contacts = new ArrayList<>();

    }

    public boolean loadContacts() throws InterruptedException{
        contacts_semaphore.acquire();

        int permissionCheck = ContextCompat.checkSelfPermission(context, Manifest.permission.READ_CONTACTS);

        if(permissionCheck != PackageManager.PERMISSION_GRANTED){
            return false;
        }

        String[] projection = {ContactsContract.Contacts.DISPLAY_NAME, ContactsContract.Contacts.PHOTO_THUMBNAIL_URI};
        String selection = null;
        String[] selectionArgs = null;

        HashSet<Profile> profiles = new HashSet<>();

        Cursor c = context.getContentResolver().query(
                ContactsContract.Contacts.CONTENT_URI,
                projection,
                selection,
                selectionArgs,
                null,
                null
        );

        while (c.moveToNext()){
            Profile p = new Profile(context);
            p.setRecipientName(c.getString(c.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME)));
            p.setProfileImage(c.getString(c.getColumnIndex(ContactsContract.Contacts.PHOTO_THUMBNAIL_URI)));
            p.setPhoneNumber(loadPhoneNumbers(p.getRecipientName()));
            profiles.add(p);
        }

        for (Profile p : profiles)
            contacts.add(p);

        sort(contacts);

        contacts_semaphore.release();

        return true;
    }

    public void sort(ArrayList<Profile> profiles){
        if(profiles.size() < 1)
            return;

        int min;

        for (int i = 0; i < profiles.size(); i++){
            min = i;
            for(int j=i+1; j<profiles.size(); j++)
            {
                if(profiles.get(j).getRecipientName().compareTo(profiles.get(min).getRecipientName()) < 0)
                    min = j;
            }
            if (min!=i){
                Profile tmp = profiles.get(min);
                profiles.set(min, profiles.get(i));
                profiles.set(i, tmp);
            }
        }
    }

    public void saveContactsOnDB(){
        DBManager dbManager = new DBManager(context);

        for (Profile p : contacts){
            if(dbManager.getContact(p.getRecipientName()) == null){
                dbManager.insertContact(p);
            }
        }
    }

    public ArrayList<Profile> getContacts() {
        try {
            contacts_semaphore.acquire();
            ArrayList<Profile> copy = (ArrayList<Profile>) contacts.clone();
            contacts_semaphore.release();
            return copy;
        }catch (InterruptedException ex){
            return null;
        }
    }

    //TODO FIX THIS FUNCTION (CursorWindowAllocationException)
    private HashSet<String> loadPhoneNumbers(String recipient){
        HashSet<String> phoneNumbers = new HashSet<>();
        int permissionCheck = ContextCompat.checkSelfPermission(context, Manifest.permission.READ_CONTACTS);

        if(permissionCheck != PackageManager.PERMISSION_GRANTED){
            return phoneNumbers;
        }

        String[] projection = {ContactsContract.CommonDataKinds.Phone.NUMBER};
        String selection = ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " = ?";
        String[] selectionArgs = {recipient};

        Cursor c = context.getContentResolver().query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                projection,
                selection,
                selectionArgs,
                null,
                null
        );

        while (c.moveToNext()){
            String number = c.getString(c.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
            phoneNumbers.add(number);
        }

        return phoneNumbers;
    }

    public Profile getContactFromPhoneNumber(String phoneNumber){
        int permissionCheck = ContextCompat.checkSelfPermission(context, Manifest.permission.READ_CONTACTS);

        if(permissionCheck != PackageManager.PERMISSION_GRANTED){
            return null;
        }

        String[] projection = {ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME, ContactsContract.CommonDataKinds.Phone.PHOTO_THUMBNAIL_URI};
        String selection = ContactsContract.CommonDataKinds.Phone.NUMBER + "= ?";
        String[] selectionArgs = {phoneNumber};

        Cursor c = context.getContentResolver().query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                projection,
                selection,
                selectionArgs,
                null,
                null
        );

        if(c.moveToNext()){
            Profile p = new Profile(context);
            p.setRecipientName(c.getString(c.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)));
            p.setPhoneNumber(loadPhoneNumbers(p.getRecipientName()));
            p.setProfileImage(c.getString(c.getColumnIndex(ContactsContract.CommonDataKinds.Phone.PHOTO_THUMBNAIL_URI)));
            return  p;
        }
        return null;
    }
}
