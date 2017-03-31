package JavaClass;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by ivanmorandi on 14/02/2017.
 */

public class Message implements Serializable {
    private Calendar date;
    private String chat_recipient;
    private String message;
    private boolean me;
    private int id=-1;
    private boolean isMessage = true;

    public Message(){
        date = Calendar.getInstance();
        chat_recipient = "";
        message = "";
        me = true;
        id = -1;
    }

    public Message(String recipient){
        date = Calendar.getInstance();
        this.chat_recipient = recipient;
        message = "";
        me = true;
        id=-1;
    }

    public Message(String recipient, String message){
        date = Calendar.getInstance();
        this.chat_recipient = recipient;
        this.message = message;
    }

    public Calendar getDate() {
        return date;
    }

    public void setDate(Calendar date) {
        this.date = date;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getRecipient() {
        return chat_recipient;
    }

    public boolean isSentByMe() {
        return me;
    }

    public void setSentByMe(boolean me) {
        this.me = me;
    }

    public void setRecipient(String recipient) {
        this.chat_recipient = recipient;
    }

    public boolean isMessage() {
        return isMessage;
    }

    public void setMessage(boolean message) {
        isMessage = message;
    }

    public String getTimeString(){
        String day = new SimpleDateFormat("dd").format(date.getTime());
        String month = new SimpleDateFormat("MMM", Locale.ITALIAN).format(date.getTime());
        String time = new SimpleDateFormat("HH:mm").format(date.getTime());
        return day + " " + month + " - " + time;
    }

    public String getDateString(){
        int day = date.get(Calendar.DAY_OF_MONTH);

        Date date = getDate().getTime();

        // Then get the day of week from the Date based on specific locale.
        String dayOfWeek = new SimpleDateFormat("EEEE", Locale.ITALIAN).format(date);
        String month_name = new SimpleDateFormat("MMM", Locale.ITALIAN).format(date);
        String time = new SimpleDateFormat("HH:mm").format(date);

        return  capitalize(dayOfWeek) + " " + day + " " + capitalize(month_name) + " - " + time;
    }

    public String getTimeDifferenceFromNow(){
        Calendar now = Calendar.getInstance();

        int now_dd = now.get(Calendar.DAY_OF_MONTH);
        int now_MM = now.get(Calendar.MONTH);
        int now_yyyy = now.get(Calendar.YEAR);
        int now_hh = now.get(Calendar.HOUR_OF_DAY);
        int now_mm = now.get(Calendar.MINUTE);
        int now_ss = now.get(Calendar.SECOND);
        int c_dd = date.get(Calendar.DAY_OF_MONTH);
        int c_MM = date.get(Calendar.MONTH);
        int c_yyyy = date.get(Calendar.YEAR);
        int c_hh = date.get(Calendar.HOUR_OF_DAY);
        int c_mm = date.get(Calendar.MINUTE);
        int c_ss = date.get(Calendar.SECOND);

        if(now.get(Calendar.DAY_OF_YEAR) == date.get(Calendar.DAY_OF_YEAR) && now.get(Calendar.YEAR) == date.get(Calendar.YEAR))
        {
            int diff_hours = now.get(Calendar.HOUR_OF_DAY) - date.get(Calendar.HOUR_OF_DAY);
            int diff_minutes = now_hh*60+now_mm - (c_hh*60+c_mm);
            int diff_seconds = now_hh*3600+now_mm*60+now_ss -
                    (c_hh*3600+c_mm*60+c_ss);

            if(diff_minutes < 60){
                if(diff_seconds < 60){
                    if(now_ss == c_ss) {
                        return "Adesso";
                    }
                    else
                    {
                        return String.valueOf(diff_seconds) + " secondi fa";
                    }
                }else{
                    return String.valueOf(diff_minutes) + " minuti fa";
                }
            }
            else{
                return String.valueOf(diff_hours) + " ore fa";
            }
        }
        else{
            if(now_yyyy == c_yyyy && (now_dd-c_dd) == 1)
                return "Ieri";

            return (new SimpleDateFormat("dd/MM/yyyy").format(date.getTime()));
        }

    }
    private String capitalize(final String line) {
        return Character.toUpperCase(line.charAt(0)) + line.substring(1);
    }

}
