package Adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.ivanmorandi.messenger.ActivityChat;
import com.example.ivanmorandi.messenger.ActivityContacts;
import com.example.ivanmorandi.messenger.MainActivity;
import com.example.ivanmorandi.messenger.R;

import java.util.ArrayList;
import java.util.List;

import Database.DBManager;
import JavaClass.Chat;
import JavaClass.Profile;

/**
 * Created by ivanmorandi on 15/02/2017.
 */

public class ProfileAdapter extends ArrayAdapter<Profile> {
    private ArrayList<Profile> profiles;
    private DBManager dbManager;
    private int activity_caller = MainActivity.ACTIVITY_ID;

    public ProfileAdapter(Context context, int resource, List<Profile> objects) {
        super(context, resource, objects);
        this.profiles = (ArrayList<Profile>)objects;
        dbManager = new DBManager(context);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final Profile profile = profiles.get(position);

        if(convertView == null)
        {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.profile, parent, false);
        }

        TextView textImage = (TextView)convertView.findViewById(R.id.contact_profile_image_char);
        TextView textName = (TextView)convertView.findViewById(R.id.contact_profile_name);
        TextView textNumber = (TextView) convertView.findViewById(R.id.contact_profile_number);
        String firstChar = String.valueOf(profile.getRecipientName().toUpperCase().charAt(0));
        textImage.setText(firstChar);
        textName.setText(profile.getRecipientName());
        if(profile.getPhoneNumbers().size()>0)
            textNumber.setText((String)profile.getPhoneNumbers().toArray()[0]);

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(dbManager.getContact(profile.getRecipientName()) == null)
                    dbManager.insertContact(profile);

                Chat chat =dbManager.getChat(profile.getRecipientName());

                if(chat == null){
                    chat = new Chat(getContext());
                    chat.setRecipient(profile);
                }


                Intent intent = new Intent(getContext(), ActivityChat.class);
                intent.putExtra("chat", chat);
                intent.putExtra("activity_caller", ActivityContacts.ACTIVITY_ID);
                getContext().startActivity(intent);
            }
        });

        return  convertView;
    }

    public void setActivity_caller(int activity_caller) {
        this.activity_caller = activity_caller;
    }
}
