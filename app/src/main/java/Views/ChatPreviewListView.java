package Views;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import Adapter.ChatAdapter;
import JavaClass.Chat;

/**
 * Created by ivanmorandi on 14/02/2017.
 */

public class ChatPreviewListView extends ListView {
    ArrayList<Chat> chats;
    ChatAdapter chatAdapter;

    public ChatPreviewListView(Context context) {
        super(context);

        InitializeComponents();
    }

    public ChatPreviewListView(Context context, AttributeSet attrs) {
        super(context, attrs);

        InitializeComponents();
    }

    public ChatPreviewListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        InitializeComponents();
    }

    public ChatPreviewListView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);

        InitializeComponents();
    }

    public void addChat(Chat chat){
        chatAdapter.insert(chat,0);
    }

    public void addChats(List<Chat> chat_list){
        int min;

        for(int i=0;i<chat_list.size()-1;i++){
            min = i;

            for(int j=1;j<chat_list.size();j++){
                if(chat_list.get(j).getLastMessage().getDate().compareTo(chat_list.get(min).getLastMessage().getDate()) < 0){
                    min = j;
                }
            }

            if(i != min){
                Chat tmp = chat_list.get(min);
                chat_list.set(min, chat_list.get(i));
                chat_list.set(i, tmp);
            }
        }

        for (Chat c : chat_list)
            addChat(c);
    }

    public void clear(){
        chatAdapter.clear();
        chatAdapter.notifyDataSetChanged();
    }

    public void moveFirstPosition(Chat chat){
        for(int i=0;i<chats.size();i++){
            if(chats.get(i).getRecipient().equals(chat.getRecipient())){
                chats.remove(i);
                addChat(chat);
            }
        }
    }

    private void InitializeComponents(){
        this.chats = new ArrayList<>();
        chatAdapter = new ChatAdapter(getContext(), 0, chats);
        setAdapter(chatAdapter);
    }




}
