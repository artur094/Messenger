package Views;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;

import java.util.Calendar;

import Adapter.MessageAdapter;
import JavaClass.Chat;
import JavaClass.Message;

/**
 * Created by ivanmorandi on 15/02/2017.
 */

public class ChatListView extends ListView {
    private Chat chat;
    private MessageAdapter messageAdapter;

    public ChatListView(Context context) {
        super(context);
        initializeComponents();
    }

    public ChatListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initializeComponents();
    }

    public ChatListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initializeComponents();
    }

    public Chat getChat() {
        return chat;
    }

    public void addMessage(Message m){
        if(chat.getMessages().size() == 0) {
            Message divider = new Message();
            divider.setMessage(false);
            chat.addMessage(divider);
        }else{
            if(m.getDate().get(Calendar.DAY_OF_YEAR) != chat.getLastMessage().getDate().get(Calendar.DAY_OF_YEAR)) {
                Message divider = new Message();
                divider.setMessage(false);
                chat.addMessage(divider);
            }
        }

        chat.addMessage(m);
        messageAdapter.notifyDataSetChanged();
    }

    private void initializeComponents()
    {
        setDivider(null);
        setDividerHeight(0);

        chat = new Chat(getContext());
        messageAdapter = new MessageAdapter(getContext(), 0, chat.getMessages());
        setAdapter(messageAdapter);
    }


}
