package JavaClass;

import android.content.Context;

import com.example.ivanmorandi.messenger.R;

import java.io.Serializable;
import java.util.HashSet;

/**
 * Created by ivanmorandi on 15/02/2017.
 */

public class Profile implements Serializable{
    private String recipientName;
    private HashSet<String> phoneNumbers;
    private String profileImage;
    private long id;
    private int color;

    public Profile(Context context){
        phoneNumbers = new HashSet<>();
        this.recipientName = "";
        this.profileImage="";
        id = -1;
        color = context.getResources().getColor(R.color.circle);
    }

    public Profile(String recipientName, String phoneNumber){
        phoneNumbers = new HashSet<>();
        phoneNumbers.add(phoneNumber);
        this.recipientName = recipientName;
        this.profileImage="";
        id=-1;
    }

    public HashSet<String> getPhoneNumbers() {
        return phoneNumbers;
    }

    public void setPhoneNumber(HashSet<String> phoneNumbers) {
        this.phoneNumbers = phoneNumbers;
    }

    public String getRecipientName() {
        return recipientName;
    }

    public void setRecipientName(String recipientName) {
        this.recipientName = recipientName;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    public void addPhoneNumber(String phoneNumber){
        phoneNumbers.add(phoneNumber);
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Override
    public int hashCode() {
        return 0;
    }

    @Override
    public boolean equals(Object o) {
        Profile p = (Profile)o;
        return p.getRecipientName().equals(recipientName);
    }

    public int compareTo(Profile p){
        return recipientName.compareTo(p.getRecipientName());
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }
}
