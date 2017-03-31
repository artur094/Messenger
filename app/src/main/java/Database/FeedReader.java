package Database;

import android.os.StrictMode;
import android.provider.BaseColumns;

/**
 * Created by ivanmorandi on 22/02/2017.
 */

public class FeedReader {
    private FeedReader(){

    }

    public static class FeedContact implements BaseColumns {
        public static final String TABLE_NAME = "contacts";
        public static final String COLUMN_ID = "id";
        public static final String COLUMN_PROFILE_IMAGE = "image";
        public static final String COLUMN_RECIPIENT = "recipient";
        public static final String COLUMN_COLOR = "contact_color";
    }

    public static class FeedChat implements BaseColumns {
        public static final String TABLE_NAME = "chats";
        public static final String COLUMN_ID = "id";
        public static final String COLUMN_UNREADED_MESSAGES = "unreaded_messages";
        public static final String COLUMN_CONTACT_ID = "recipient_id";
        public static final String COLUMN_THEME_APP_BAR = "color_app_bar";
        public static final String COLUMN_THEME_NOT_BAR = "color_notification";
        public static final String COLUMN_THEME_MY_MESS = "color_my_messages";
        public static final String COLUMN_THEME_RECIPIENT_MESS = "color_recipient_messages";
        public static final String COLUMN_THEME_EDITTEXT = "color_edittext";
        public static final String COLUMN_THEME_CHAT = "background";
    }

    public static class FeedMessage implements BaseColumns{
        public static final String TABLE_NAME = "messages";
        public static final String COLUMN_ID = "id";
        public static final String COLUMN_CHAT_ID = "chat_id";
        public static final String COLUMN_IS_ME = "is_me";
        public static final String COLUMN_DATE = "date";
        public static final String COLUMN_MESSAGE = "message";
    }

    public static class FeedPhone implements BaseColumns{
        public static final String TABLE_NAME = "phone_numer";
        public static final String COLUMN_ID = "id";
        public static final String COLUMN_CONTACT = "contact_id";
        public static final String COLUMN_NUMBER = "number";
    }

    public static class FeedMail implements BaseColumns{
        public static final String TABLE_NAME = "email";
        public static final String COLUMN_ID = "id";
        public static final String COLUMN_CONTACT = "contact_id";
        public static final String COLUMN_EMAIL = "email";
    }
}
