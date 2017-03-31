package Adapter;

import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.DrawableContainer;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.StateListDrawable;
import android.support.annotation.NonNull;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ivanmorandi.messenger.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import JavaClass.Message;

/**
 * Created by ivanmorandi on 15/02/2017.
 */

public class MessageAdapter extends ArrayAdapter<Message>{
    ArrayList<Message> messages;

    public MessageAdapter(Context context, int resource, List<Message> objects) {
        super(context, resource, objects);

        messages = (ArrayList<Message>)objects;

        InitializeComponents();
    }

    private void InitializeComponents()
    {

    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        Message message = messages.get(position);

        if (message.isMessage()){
            if(convertView == null)
            {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.message, parent, false);
            }
            LinearLayout messageLayout = (LinearLayout)convertView.findViewById(R.id.messageLayout);
            TextView text = (TextView)convertView.findViewById(R.id.messageText);
            TextView time = (TextView)convertView.findViewById(R.id.messageTime);
            if(text == null)
            {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.message, parent, false);
                messageLayout = (LinearLayout)convertView.findViewById(R.id.messageLayout);
                text = (TextView)convertView.findViewById(R.id.messageText);
                time = (TextView)convertView.findViewById(R.id.messageTime);
            }
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams)text.getLayoutParams();
            text.setText(message.getMessage());
            time.setText(message.getTimeString());

            if (message.isSentByMe()){
                //params.addRule(RelativeLayout.ALIGN_PARENT_END);
                messageLayout.setGravity(Gravity.RIGHT);
                time.setGravity(Gravity.RIGHT);
                text.setBackground(getContext().getResources().getDrawable(R.drawable.message_by_me));
                text.setTextColor(getContext().getResources().getColor(R.color.my_message_text));

            /*
            StateListDrawable gradientDrawable = (StateListDrawable) text.getBackground();
            DrawableContainer.DrawableContainerState drawableContainerState = (DrawableContainer.DrawableContainerState) gradientDrawable.getConstantState();
            Drawable[] children = drawableContainerState.getChildren();
            GradientDrawable selectedItem = (GradientDrawable) children[0];
            selectedItem.setColor(getContext().getResources().getColor(R.color.md_grey_50));
            */

            }
            else{
                //params.addRule(RelativeLayout.ALIGN_PARENT_START);
                messageLayout.setGravity(Gravity.LEFT);
                time.setGravity(Gravity.LEFT);
                text.setBackground(getContext().getResources().getDrawable(R.drawable.message));
                text.setTextColor(getContext().getResources().getColor(R.color.other_message_text));

            }
            text.setLayoutParams(params);

            convertView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    Context context = getContext();
                    CharSequence text = "Long Click";
                    int duration = Toast.LENGTH_SHORT;

                    Toast toast = Toast.makeText(context, text, duration);
                    toast.show();
                    return true;
                }
            });

            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    TextView time = (TextView)v.findViewById(R.id.messageTime);
                    if (time.getVisibility() == View.VISIBLE)
                        time.setVisibility(View.GONE);
                    else
                        time.setVisibility(View.VISIBLE);
                }
            });

        }else{
            if(convertView == null){
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.chat_divider, parent, false);
            }
            Message mess = messages.get(position+1);
            TextView chat_divider_date = (TextView)convertView.findViewById(R.id.divider_text);
            if(chat_divider_date == null){
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.chat_divider, parent, false);
                chat_divider_date = (TextView)convertView.findViewById(R.id.divider_text);
            }
            chat_divider_date.setText(mess.getDateString());

            convertView.setClickable(false);
            convertView.setFocusable(false);
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
        }



        return convertView;
    }
}
