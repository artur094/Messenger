package Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.example.ivanmorandi.messenger.R;

import java.util.ArrayList;
import java.util.List;

import JavaClass.Chat;
import Views.ChatPreviewView;

/**
 * Created by ivanmorandi on 15/02/2017.
 */

public class ChatAdapter extends ArrayAdapter<Chat> {
    ArrayList<Chat> chats;

    public ChatAdapter(Context context, int resource, List objects) {
        super(context, resource, objects);
        chats = (ArrayList<Chat>)objects;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //return super.getView(position, convertView, parent);

        Chat chat = chats.get(position);

        if(convertView == null)
        {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.chatpreview, parent, false);
        }

        ChatPreviewView chatPreviewView = (ChatPreviewView) convertView;
        chatPreviewView.update(chat);

        return  convertView;

    }
}
