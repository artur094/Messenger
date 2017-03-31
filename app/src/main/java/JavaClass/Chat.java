package JavaClass;

import android.content.Context;

import com.example.ivanmorandi.messenger.R;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by ivanmorandi on 14/02/2017.
 */

public class Chat implements Serializable{
    private Profile recipient;
    private ArrayList<Message> messages;
    private long id;
    private String unreadedSMS;

    private int color_app_bar;
    private int color_notification_bar;
    private int color_my_messages;
    private int color_recipient_messages;
    private int color_background;
    private int color_edittext;

    public Chat(Context context){
        id=-1;
        messages = new ArrayList<>();
        unreadedSMS = "";

        color_app_bar = context.getResources().getColor(R.color.appbar);
        color_notification_bar = context.getResources().getColor(R.color.notificationbar);
        color_edittext = context.getResources().getColor(R.color.edittext_chat);
        color_my_messages = context.getResources().getColor(R.color.my_message);
        color_recipient_messages = context.getResources().getColor(R.color.other_message);
        color_background = context.getResources().getColor(R.color.chat_backgroud);
    }

    public Profile getRecipient() {
        return recipient;
    }

    public void setRecipient(Profile recipient) {
        this.recipient = recipient;
    }

    public void addMessage(Message message){
        messages.add(message);
    }

    public ArrayList<Message> getMessages(){
        return messages;
    }

    public void setMessages(ArrayList<Message> messages){
        this.messages = messages;
    }

    public Message getLastMessage(){
        if (messages.size()>0)
            return messages.get(messages.size()-1);
        return null;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getColor_app_bar() {
        return color_app_bar;
    }

    public void setColor_app_bar(int color_app_bar) {
        this.color_app_bar = color_app_bar;
    }

    public int getColor_notification_bar() {
        return color_notification_bar;
    }

    public void setColor_notification_bar(int color_notification_bar) {
        this.color_notification_bar = color_notification_bar;
    }

    public int getColor_my_messages() {
        return color_my_messages;
    }

    public void setColor_my_messages(int color_my_messages) {
        this.color_my_messages = color_my_messages;
    }

    public int getColor_recipient_messages() {
        return color_recipient_messages;
    }

    public void setColor_recipient_messages(int color_recipient_messages) {
        this.color_recipient_messages = color_recipient_messages;
    }

    public int getColor_background() {
        return color_background;
    }

    public void setColor_background(int color_background) {
        this.color_background = color_background;
    }

    public int getColor_edittext() {
        return color_edittext;
    }

    public void setColor_edittext(int color_edittext) {
        this.color_edittext = color_edittext;
    }

    public String getUnreadedSMS() {
        return unreadedSMS;
    }

    public void setUnreadedSMS(String unreadedSMS) {
        this.unreadedSMS = unreadedSMS;
    }
}
