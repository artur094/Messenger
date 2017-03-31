package Views;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;

import java.util.ArrayList;

import Adapter.ProfileAdapter;
import JavaClass.Profile;

/**
 * Created by ivanmorandi on 15/02/2017.
 */

public class ProfileListView extends ListView {
    private ProfileAdapter profileAdapter;
    private ArrayList<Profile> profiles;

    public ProfileListView(Context context) {
        super(context);
        initializeComponents();
    }

    public ProfileListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initializeComponents();
    }

    public ProfileListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initializeComponents();
    }

    public void addProfile(Profile profile){
        profileAdapter.add(profile);
        profileAdapter.notifyDataSetChanged();

    }

    public void setActivityCaller(int activityCaller){
        profileAdapter.setActivity_caller(activityCaller);
    }

    private void initializeComponents(){
        profiles = new ArrayList<>();
        profileAdapter = new ProfileAdapter(getContext(), 0, profiles);
        setAdapter(profileAdapter);
    }
}
