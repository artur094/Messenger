package Views;


import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.ivanmorandi.messenger.ActivityChat;
import com.example.ivanmorandi.messenger.R;

import JavaClass.Chat;
import JavaClass.Message;

/**
 * Created by ivanmorandi on 13/02/2017.
 */

public class ChatPreviewView extends LinearLayout {
    private Chat chat;
    private LinearLayout infoChat;
    private ImageView imageIcon;
    private TextView textName;
    private TextView textMessage;
    private TextView textDay;
    private TextView textInitialChar;


    public ChatPreviewView(Context context) {
        super(context);
        InitializeComponents();
    }

    public ChatPreviewView(Context context, AttributeSet attrs) {
        super(context, attrs);
        InitializeComponents();
    }

    public ChatPreviewView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        InitializeComponents();
    }

    public ChatPreviewView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        InitializeComponents();
    }

    public void update() {
        Message message = chat.getLastMessage();

        if (message == null) {
            textName.setText("Error");
            textMessage.setText("No message found!");
            textDay.setText("");
            return;
        }

        int icon_color = chat.getColor_app_bar();

        if (chat.getUnreadedSMS().equals("")) {
            textName.setTypeface(null, Typeface.NORMAL);
            textMessage.setTypeface(null, Typeface.NORMAL);
        } else {
            textName.setTypeface(null, Typeface.BOLD);
            textMessage.setTypeface(null, Typeface.BOLD);
        }

        Drawable d = getResources().getDrawable(R.drawable.circle);
        d.setColorFilter(icon_color, PorterDuff.Mode.MULTIPLY);
        imageIcon.setImageDrawable(d);


        textDay.setText(message.getTimeDifferenceFromNow());
        if (message.isSentByMe())
            textMessage.setText("Me: " + message.getMessage());
        else
            textMessage.setText(message.getRecipient() + ": " + message.getMessage());
        textName.setText(chat.getRecipient().getRecipientName());
        textInitialChar.setText(String.valueOf((chat.getRecipient().getRecipientName().toUpperCase().charAt(0))));


    }

    public void setLastMessge(Message m) {
        chat.addMessage(m);
    }

    public String getLastMessageRecipient() {
        return chat.getLastMessage().getRecipient();
    }

    public void update(Chat chat) {
        this.chat = chat;
        update();
    }

    public Chat getChat() {
        return chat;
    }

    protected void InitializeComponents() {
        this.chat = new Chat(getContext());

        LayoutInflater layoutInflater = (LayoutInflater) getContext().getSystemService(getContext().LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.chatinfo, this);

        infoChat = (LinearLayout) view.findViewById(R.id.chat_info);
        textName = (TextView) view.findViewById(R.id.chat_name);
        textMessage = (TextView) view.findViewById(R.id.chat_last_message);
        textDay = (TextView) view.findViewById(R.id.chat_date_last_message);
        textInitialChar = (TextView) view.findViewById(R.id.chat_info_first_char);
        imageIcon = (ImageView) view.findViewById(R.id.chat_info_circle);

        //this.addView(textName);
        //this.addView(infoChat);
        //infoChat.addView(textName);
        //infoChat.addView(textMessage);
        //infoChat.addView(textDay);

        update();

        this.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), ActivityChat.class);
                intent.putExtra("chat", chat);
                getContext().startActivity(intent);


            }
        });

    }


}
