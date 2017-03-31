package Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;

import JavaClass.Chat;
import JavaClass.Message;
import JavaClass.Profile;

/**
 * Created by ivanmorandi on 22/02/2017.
 */

public class DBManager extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "Messaging.db";
    private Context context;


    private static final String SQL_CREATE_CONTACTS =
            "CREATE TABLE "+ FeedReader.FeedContact.TABLE_NAME + " (" +
                    FeedReader.FeedContact.COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "+
                    FeedReader.FeedContact.COLUMN_RECIPIENT + " TEXT NOT NULL, "+
                    FeedReader.FeedContact.COLUMN_COLOR + "INTEGER,"+
                    FeedReader.FeedContact.COLUMN_PROFILE_IMAGE + " TEXT) ";

    private static final String SQL_CREATE_CHATS =
            "CREATE TABLE "+ FeedReader.FeedChat.TABLE_NAME + " (" +
                    FeedReader.FeedChat.COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "+
                    FeedReader.FeedChat.COLUMN_CONTACT_ID + " INTEGER NOT NULL, " +
                    FeedReader.FeedChat.COLUMN_THEME_APP_BAR + " INTEGER NOT NULL, "+
                    FeedReader.FeedChat.COLUMN_UNREADED_MESSAGES + " TEXT, "+
                    FeedReader.FeedChat.COLUMN_THEME_CHAT + " INTEGER NOT NULL, "+
                    FeedReader.FeedChat.COLUMN_THEME_EDITTEXT + " INTEGER NOT NULL, "+
                    FeedReader.FeedChat.COLUMN_THEME_MY_MESS + " INTEGER NOT NULL, "+
                    FeedReader.FeedChat.COLUMN_THEME_NOT_BAR + " INTEGER NOT NULL, "+
                    FeedReader.FeedChat.COLUMN_THEME_RECIPIENT_MESS + " INTEGER NOT NULL, "+
                    "FOREIGN KEY ("+ FeedReader.FeedChat.COLUMN_CONTACT_ID+") REFERENCES "+ FeedReader.FeedContact.TABLE_NAME + " ("+ FeedReader.FeedContact.COLUMN_ID+") );";

    private static final String SQL_CREATE_MESSAGES =
            "CREATE TABLE " + FeedReader.FeedMessage.TABLE_NAME + " ("+
                    FeedReader.FeedMessage.COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "+
                    FeedReader.FeedMessage.COLUMN_DATE + " LONG, "+
                    FeedReader.FeedMessage.COLUMN_IS_ME + " BOOLEAN NOT NULL, "+
                    FeedReader.FeedMessage.COLUMN_MESSAGE + " TEXT NOT NULL, "+
                    FeedReader.FeedMessage.COLUMN_CHAT_ID + " INTEGER NOT NULL, "+
                    "FOREIGN KEY (" + FeedReader.FeedMessage.COLUMN_CHAT_ID + ") REFERENCES "+ FeedReader.FeedChat.TABLE_NAME + " ("+ FeedReader.FeedChat.COLUMN_ID + ") );";

    private static final String SQL_CREATE_PHONE_NUMBERS =
            "CREATE TABLE " + FeedReader.FeedPhone.TABLE_NAME + " ("+
                    FeedReader.FeedPhone.COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    FeedReader.FeedPhone.COLUMN_CONTACT + " INTEGER NOT NULL, "+
                    FeedReader.FeedPhone.COLUMN_NUMBER + " TEXT NOT NULL, "+
                    "FOREIGN KEY (" + FeedReader.FeedPhone.COLUMN_CONTACT + ") REFERENCES "+ FeedReader.FeedContact.TABLE_NAME + " ("+ FeedReader.FeedContact.COLUMN_ID+") );";

    private static final String SQL_DELETE_CONTACTS =
            "DROP TABLE IF EXISTS " + FeedReader.FeedContact.TABLE_NAME;
    private static final String SQL_DELETE_CHATS =
            "DROP TABLE IF EXISTS " + FeedReader.FeedChat.TABLE_NAME;
    private static final String SQL_DELETE_MESSAGES =
            "DROP TABLE IF EXISTS " + FeedReader.FeedMessage.TABLE_NAME;
    private static final String SQL_DELETE_PHONE_NUMBERS =
            "DROP TABLE IF EXISTS " + FeedReader.FeedPhone.TABLE_NAME;

    public DBManager(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        this.context = context;
    }

    public DBManager(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }


    @Override
    public synchronized void close() {
        super.close();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_CONTACTS);
        db.execSQL(SQL_CREATE_CHATS);
        db.execSQL(SQL_CREATE_MESSAGES);
        db.execSQL(SQL_CREATE_PHONE_NUMBERS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_MESSAGES);
        db.execSQL(SQL_DELETE_CHATS);
        db.execSQL(SQL_DELETE_PHONE_NUMBERS);
        db.execSQL(SQL_DELETE_CONTACTS);

        onCreate(db);
    }

    public void delete(){
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL(SQL_DELETE_MESSAGES);
        db.execSQL(SQL_DELETE_CHATS);
        db.execSQL(SQL_DELETE_PHONE_NUMBERS);
        db.execSQL(SQL_DELETE_CONTACTS);
    }

    public void create(){
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL(SQL_CREATE_CONTACTS);
        db.execSQL(SQL_CREATE_CHATS);
        db.execSQL(SQL_CREATE_MESSAGES);
        db.execSQL(SQL_CREATE_PHONE_NUMBERS);
    }

    public void insertContact(Profile profile){
        SQLiteDatabase db = getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(FeedReader.FeedContact.COLUMN_RECIPIENT, profile.getRecipientName());
        contentValues.put(FeedReader.FeedContact.COLUMN_PROFILE_IMAGE, profile.getProfileImage());

        long contact_id = db.insert(FeedReader.FeedContact.TABLE_NAME, null, contentValues);

        for(String number : profile.getPhoneNumbers())
        {
            ContentValues phoneValues = new ContentValues();
            phoneValues.put(FeedReader.FeedPhone.COLUMN_NUMBER, number);
            phoneValues.put(FeedReader.FeedPhone.COLUMN_CONTACT, contact_id);

            db.insert(FeedReader.FeedPhone.TABLE_NAME, null, phoneValues);
        }
    }

    public void insertMessage(Message message){
        if(!message.isMessage())
            return;

        long chat_id = getChatID(message.getRecipient());

        SQLiteDatabase db = getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(FeedReader.FeedMessage.COLUMN_CHAT_ID, String.valueOf(chat_id));
        contentValues.put(FeedReader.FeedMessage.COLUMN_IS_ME, String.valueOf(message.isSentByMe()));
        contentValues.put(FeedReader.FeedMessage.COLUMN_MESSAGE, message.getMessage());
        contentValues.put(FeedReader.FeedMessage.COLUMN_DATE, message.getDate().getTimeInMillis());

        db.insert(FeedReader.FeedMessage.TABLE_NAME, null, contentValues);
    }

    public void insertChat(Chat chat){
        long contact_id = getContactID(chat.getRecipient().getRecipientName());

        SQLiteDatabase db = getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(FeedReader.FeedChat.COLUMN_CONTACT_ID, contact_id);
        contentValues.put(FeedReader.FeedChat.COLUMN_UNREADED_MESSAGES, chat.getUnreadedSMS());
        contentValues.put(FeedReader.FeedChat.COLUMN_THEME_APP_BAR, chat.getColor_app_bar());
        contentValues.put(FeedReader.FeedChat.COLUMN_THEME_CHAT, chat.getColor_background());
        contentValues.put(FeedReader.FeedChat.COLUMN_THEME_EDITTEXT, chat.getColor_edittext());
        contentValues.put(FeedReader.FeedChat.COLUMN_THEME_MY_MESS, chat.getColor_my_messages());
        contentValues.put(FeedReader.FeedChat.COLUMN_THEME_NOT_BAR, chat.getColor_notification_bar());
        contentValues.put(FeedReader.FeedChat.COLUMN_THEME_RECIPIENT_MESS, chat.getColor_recipient_messages());

        db.insert(FeedReader.FeedChat.TABLE_NAME, null, contentValues);

        for(Message m : chat.getMessages())
            insertMessage(m);
     }

    public ArrayList<Profile> getContacts(){
        ArrayList<Profile> profiles = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();

        String[] projection = {FeedReader.FeedContact.COLUMN_ID, FeedReader.FeedContact.COLUMN_RECIPIENT, FeedReader.FeedContact.COLUMN_PROFILE_IMAGE};
        String selection = null;
        String[] selectionArgs = null;

        Cursor cursor = db.query(
                FeedReader.FeedContact.TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                null
        );

        while (cursor.moveToNext()){
            Profile p = new Profile(context);
            long contact_id = cursor.getLong(cursor.getColumnIndex(FeedReader.FeedContact.COLUMN_ID));
            p.setRecipientName(cursor.getString(cursor.getColumnIndex(FeedReader.FeedContact.COLUMN_RECIPIENT)));
            p.setProfileImage(cursor.getString(cursor.getColumnIndex(FeedReader.FeedContact.COLUMN_PROFILE_IMAGE)));
            p.setPhoneNumber(getPhoneNumbers(contact_id));
            p.setId(contact_id);
            profiles.add(p);
        }

        return profiles;
    }

    public Profile getContact(String recipient){
        SQLiteDatabase db = getReadableDatabase();

        String[] projection = {FeedReader.FeedContact.COLUMN_ID, FeedReader.FeedContact.COLUMN_RECIPIENT, FeedReader.FeedContact.COLUMN_PROFILE_IMAGE};
        String selection = FeedReader.FeedContact.COLUMN_RECIPIENT + " = ?";
        String[] selectionArgs = {recipient};

        Cursor cursor = db.query(
                FeedReader.FeedContact.TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                null
        );

        if (cursor.moveToNext()){
            Profile p = new Profile(context);
            long contact_id = cursor.getLong(cursor.getColumnIndex(FeedReader.FeedContact.COLUMN_ID));
            p.setRecipientName(cursor.getString(cursor.getColumnIndex(FeedReader.FeedContact.COLUMN_RECIPIENT)));
            p.setProfileImage(cursor.getString(cursor.getColumnIndex(FeedReader.FeedContact.COLUMN_PROFILE_IMAGE)));
            p.setPhoneNumber(getPhoneNumbers(contact_id));
            p.setId(contact_id);
            return p;
        }

        return null;
    }

    public Profile getContactFromPhoneNumber(String phoneNumber){
        SQLiteDatabase db = getReadableDatabase();

        String[] projection = {FeedReader.FeedPhone.COLUMN_CONTACT};
        String selection = FeedReader.FeedPhone.COLUMN_NUMBER + " = ?";
        String[] selectionArgs = {phoneNumber};

        Cursor cursor = db.query(
                FeedReader.FeedPhone.TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                null
        );

        if (cursor.moveToNext()){
            long contact_id = cursor.getLong(cursor.getColumnIndex(FeedReader.FeedPhone.COLUMN_CONTACT));
            return getContact(contact_id);
        }

        return null;
    }

    public Profile getContact(long contact_id){
        SQLiteDatabase db = getReadableDatabase();

        String[] projection = {FeedReader.FeedContact.COLUMN_ID, FeedReader.FeedContact.COLUMN_RECIPIENT, FeedReader.FeedContact.COLUMN_PROFILE_IMAGE};
        String selection = FeedReader.FeedContact.COLUMN_ID + " = ?";
        String[] selectionArgs = {String.valueOf(contact_id)};

        Cursor cursor = db.query(
                FeedReader.FeedContact.TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                null
        );

        if (cursor.moveToNext()){
            Profile p = new Profile(context);
            p.setRecipientName(cursor.getString(cursor.getColumnIndex(FeedReader.FeedContact.COLUMN_RECIPIENT)));
            p.setProfileImage(cursor.getString(cursor.getColumnIndex(FeedReader.FeedContact.COLUMN_PROFILE_IMAGE)));
            p.setPhoneNumber(getPhoneNumbers(contact_id));
            p.setId(contact_id);
            return p;
        }

        return null;
    }

    public HashSet<String> getPhoneNumbers(long contact_id){
        HashSet<String> phoneNumbers = new HashSet<>();
        SQLiteDatabase db = getReadableDatabase();

        String[] projection = {FeedReader.FeedPhone.COLUMN_NUMBER};
        String selection = FeedReader.FeedPhone.COLUMN_CONTACT + " = ?";
        String[] selectionArgs = {String.valueOf(contact_id)};

        Cursor cursor = db.query(
                FeedReader.FeedPhone.TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                null
        );

        while (cursor.moveToNext()){
            String number = cursor.getString(cursor.getColumnIndex(FeedReader.FeedPhone.COLUMN_NUMBER));
            phoneNumbers.add(number);
        }

        return phoneNumbers;
    }

    public ArrayList<Chat> getChats(){
        ArrayList<Chat> chats = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();

        String[] projection = {FeedReader.FeedChat.COLUMN_ID, FeedReader.FeedChat.COLUMN_CONTACT_ID,
                FeedReader.FeedChat.COLUMN_THEME_RECIPIENT_MESS,
                FeedReader.FeedChat.COLUMN_THEME_NOT_BAR,
                FeedReader.FeedChat.COLUMN_THEME_MY_MESS,
                FeedReader.FeedChat.COLUMN_THEME_EDITTEXT,
                FeedReader.FeedChat.COLUMN_THEME_APP_BAR,
                FeedReader.FeedChat.COLUMN_THEME_CHAT,
                FeedReader.FeedChat.COLUMN_UNREADED_MESSAGES
        };
        String selection = null;
        String[] selectionArgs = null;

        Cursor cursor = db.query(
                FeedReader.FeedChat.TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                null
        );

        while (cursor.moveToNext()){
            Chat chat = new Chat(context);
            long contact_id = cursor.getLong(cursor.getColumnIndex(FeedReader.FeedChat.COLUMN_CONTACT_ID));
            long chat_id = cursor.getLong(cursor.getColumnIndex(FeedReader.FeedChat.COLUMN_ID));
            chat.setUnreadedSMS(cursor.getString(cursor.getColumnIndex(FeedReader.FeedChat.COLUMN_UNREADED_MESSAGES)));
            chat.setRecipient(getContact(contact_id));
            chat.setMessages(getMessages(chat_id));
            chat.setColor_app_bar(cursor.getInt(cursor.getColumnIndex(FeedReader.FeedChat.COLUMN_THEME_APP_BAR)));
            chat.setColor_background(cursor.getInt(cursor.getColumnIndex(FeedReader.FeedChat.COLUMN_THEME_CHAT)));
            chat.setColor_edittext(cursor.getInt(cursor.getColumnIndex(FeedReader.FeedChat.COLUMN_THEME_EDITTEXT)));
            chat.setColor_my_messages(cursor.getInt(cursor.getColumnIndex(FeedReader.FeedChat.COLUMN_THEME_MY_MESS)));
            chat.setColor_notification_bar(cursor.getInt(cursor.getColumnIndex(FeedReader.FeedChat.COLUMN_THEME_NOT_BAR)));
            chat.setColor_recipient_messages(cursor.getInt(cursor.getColumnIndex(FeedReader.FeedChat.COLUMN_THEME_RECIPIENT_MESS)));
            chats.add(chat);
        }

        return chats;
    }

    public Chat getChat(String recipient){
        SQLiteDatabase db = getReadableDatabase();
        Profile profile = getContact(recipient);

        String[] projection = {
                FeedReader.FeedChat.COLUMN_ID,
                FeedReader.FeedChat.COLUMN_THEME_RECIPIENT_MESS,
                FeedReader.FeedChat.COLUMN_THEME_NOT_BAR,
                FeedReader.FeedChat.COLUMN_THEME_MY_MESS,
                FeedReader.FeedChat.COLUMN_THEME_EDITTEXT,
                FeedReader.FeedChat.COLUMN_THEME_APP_BAR,
                FeedReader.FeedChat.COLUMN_THEME_CHAT,
                FeedReader.FeedChat.COLUMN_UNREADED_MESSAGES
        };
        String selection = FeedReader.FeedChat.COLUMN_CONTACT_ID + " = ?";
        String[] selectionArgs = {String.valueOf(profile.getId())};

        Cursor cursor = db.query(
                FeedReader.FeedChat.TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                null
        );

        if (cursor.moveToNext()){
            Chat chat = new Chat(context);
            long chat_id = cursor.getLong(cursor.getColumnIndex(FeedReader.FeedChat.COLUMN_ID));
            chat.setRecipient(profile);
            chat.setMessages(getMessages(chat_id));
            chat.setId(chat_id);

            chat.setUnreadedSMS(cursor.getString(cursor.getColumnIndex(FeedReader.FeedChat.COLUMN_UNREADED_MESSAGES)));
            chat.setColor_app_bar(cursor.getInt(cursor.getColumnIndex(FeedReader.FeedChat.COLUMN_THEME_APP_BAR)));
            chat.setColor_background(cursor.getInt(cursor.getColumnIndex(FeedReader.FeedChat.COLUMN_THEME_CHAT)));
            chat.setColor_edittext(cursor.getInt(cursor.getColumnIndex(FeedReader.FeedChat.COLUMN_THEME_EDITTEXT)));
            chat.setColor_my_messages(cursor.getInt(cursor.getColumnIndex(FeedReader.FeedChat.COLUMN_THEME_MY_MESS)));
            chat.setColor_notification_bar(cursor.getInt(cursor.getColumnIndex(FeedReader.FeedChat.COLUMN_THEME_NOT_BAR)));
            chat.setColor_recipient_messages(cursor.getInt(cursor.getColumnIndex(FeedReader.FeedChat.COLUMN_THEME_RECIPIENT_MESS)));

            return chat;
        }
        return null;
    }

    public String getUnreadedMessages(String recipient){
        String messages = "";

        long contact_id = getContactID(recipient);

        String[] projection = {
                FeedReader.FeedChat.COLUMN_UNREADED_MESSAGES
        };
        String selection = FeedReader.FeedChat.COLUMN_CONTACT_ID + " = ?";
        String[] selectionArgs = {String.valueOf(contact_id)};

        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(
                FeedReader.FeedChat.TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                null
        );

        if(cursor.moveToNext()){
            messages = cursor.getString(cursor.getColumnIndex(FeedReader.FeedChat.COLUMN_UNREADED_MESSAGES));
        }

        return messages;
    }

    public void updateUnreadedMessages(String recipient, String messages){
        long contact_id = getContactID(recipient);

        SQLiteDatabase db = getWritableDatabase();

        ContentValues cv = new ContentValues();
        cv.put(FeedReader.FeedChat.COLUMN_UNREADED_MESSAGES, messages);

        String selection = FeedReader.FeedChat.COLUMN_CONTACT_ID + "= ?";
        String[] selectionArgs = {String.valueOf(contact_id)};

        db.update(FeedReader.FeedChat.TABLE_NAME, cv, selection, selectionArgs);
    }

    public Profile getRecipientFromChat(long chat_id){
        SQLiteDatabase db = getReadableDatabase();

        String[] projection = {FeedReader.FeedChat.COLUMN_ID, FeedReader.FeedChat.COLUMN_CONTACT_ID};
        String selection = FeedReader.FeedChat.COLUMN_ID + " = ?";
        String[] selectionArgs = {String.valueOf(chat_id)};

        Cursor cursor = db.query(
                FeedReader.FeedChat.TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                null
        );

        while (cursor.moveToNext()){
            long contact_id = cursor.getLong(cursor.getColumnIndex(FeedReader.FeedChat.COLUMN_CONTACT_ID));
            return getContact(contact_id);
        }

        return null;
    }

    public ArrayList<Message> getMessages(long chat_id){
        ArrayList<Message> messages = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();

        String[] projection = {FeedReader.FeedMessage.COLUMN_MESSAGE, FeedReader.FeedMessage.COLUMN_IS_ME, FeedReader.FeedMessage.COLUMN_DATE};
        String selection = FeedReader.FeedMessage.COLUMN_CHAT_ID + " = ?";
        String[] selectionArgs = {String.valueOf(chat_id)};

        Cursor cursor = db.query(
                FeedReader.FeedMessage.TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                null
        );

        while (cursor.moveToNext()){
            Message m = new Message();

            String mess = cursor.getString(cursor.getColumnIndex(FeedReader.FeedMessage.COLUMN_MESSAGE));
            boolean isSentByMe = Boolean.valueOf(cursor.getString(cursor.getColumnIndex(FeedReader.FeedMessage.COLUMN_IS_ME)));
            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(cursor.getLong(cursor.getColumnIndex(FeedReader.FeedMessage.COLUMN_DATE)));
            Profile recipient = getRecipientFromChat(chat_id);

            if(recipient == null)
                continue;

            m.setMessage(mess);
            m.setSentByMe(isSentByMe);
            m.setDate(cal);
            m.setRecipient(recipient.getRecipientName());

            /*if(messages.size()==0){
                Message divider = new Message();
                divider.setMessage(false);
                messages.add(divider);
            }
            else {
                Message previous = messages.get(messages.size() - 1);
                if (previous.getDate().get(Calendar.DAY_OF_YEAR) != m.getDate().get(Calendar.DAY_OF_YEAR)) {
                    Message divider = new Message();
                    divider.setMessage(false);
                    messages.add(divider);
                }
            }*/

            messages.add(m);


        }

        return messages;
    }

    public boolean setChatColorValue(String recipient, String columnName, int columnValue){
        long contact_id = getContactID(recipient);

        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();

        String selection = FeedReader.FeedChat.COLUMN_CONTACT_ID + "= ?";
        String[] selectionArgs = {String.valueOf(contact_id)};

        //If I try to modify primary or foreign key
        if(columnName.equals(FeedReader.FeedChat.COLUMN_ID) || columnName.equals(FeedReader.FeedChat.COLUMN_CONTACT_ID))
            return false;

        cv.put(columnName, columnValue);
        db.update(FeedReader.FeedChat.TABLE_NAME, cv, selection, selectionArgs);

        return true;
    }

    private long getContactID(String recipient){
        SQLiteDatabase db = getReadableDatabase();

        // First: retrieve right ID for the recipient
        String[] projection = {FeedReader.FeedContact.COLUMN_ID};
        String selection = FeedReader.FeedContact.COLUMN_RECIPIENT + " = ?";
        String[] selectionArgs = {recipient};
        long contact_id = Long.MIN_VALUE;

        Cursor cursor = db.query(
                FeedReader.FeedContact.TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                null
        );

        if(cursor.moveToNext()){
            contact_id = cursor.getLong(cursor.getColumnIndex(FeedReader.FeedContact.COLUMN_ID));
        }

        return contact_id;
    }

    private long getChatID(String recipient){
        long contact_id = getContactID(recipient);
        long chat_id = Long.MIN_VALUE;

        SQLiteDatabase db = getReadableDatabase();

        // First: retrieve right ID for the recipient
        String[] projection = {FeedReader.FeedChat.COLUMN_ID};
        String selection = FeedReader.FeedChat.COLUMN_CONTACT_ID + " = ?";
        String[] selectionArgs = {String.valueOf(contact_id)};

        Cursor cursor = db.query(
                FeedReader.FeedChat.TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                null
        );

        if(cursor.moveToNext()){
            chat_id = cursor.getLong(cursor.getColumnIndex(FeedReader.FeedChat.COLUMN_ID));
        }

        return chat_id;
    }


}
