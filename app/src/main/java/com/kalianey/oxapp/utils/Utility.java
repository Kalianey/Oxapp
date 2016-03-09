package com.kalianey.oxapp.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.NinePatchDrawable;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.androidadvance.topsnackbar.TSnackbar;
import com.kalianey.oxapp.R;
import com.kalianey.oxapp.models.ModelConversation;
import com.kalianey.oxapp.views.activities.Message;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by kalianey on 19/08/2015.
 */
public class Utility {

    public static final String DATE_FORMAT = "yyyyMMdd";

    /**
     * Helper method to convert the database representation of the date into something to display
     * to users.  As classy and polished a user experience as "20140102" is, we can do better.
     *
     * @param context Context to use for resource localization
     * @param dateStr The db formatted date string, expected to be of the form specified
     *                in Utility.DATE_FORMAT
     * @return a user-friendly representation of the date.
     */
    public static String getFriendlyDayString(Context context, String dateStr) {
        // The day string for forecast uses the following logic:
        // For today: "Today, June 8"
        // For tomorrow:  "Tomorrow"
        // For the next 5 days: "Wednesday" (just the day name)
        // For all days after that: "Mon Jun 8"

        Date todayDate = new Date();
        String todayStr = getDbDateString(todayDate);
        Date inputDate = getDateFromDb(dateStr);

        // If the date we're building the String for is today's date, the format
        // is "Today, June 24"
        if (todayStr.equals(dateStr)) {
            String today = context.getString(R.string.today);
            return context.getString(
                    R.string.format_full_friendly_date,
                    today,
                    getFormattedMonthDay(context, dateStr));
        } else {
            Calendar cal = Calendar.getInstance();
            cal.setTime(todayDate);
            cal.add(Calendar.DATE, 7);
            String weekFutureString = getDbDateString(cal.getTime());

            if (dateStr.compareTo(weekFutureString) < 0) {
                // If the input date is less than a week in the future, just return the day name.
                return getDayName(context, dateStr);
            } else {
                // Otherwise, use the form "Mon Jun 3"
                SimpleDateFormat shortenedDateFormat = new SimpleDateFormat("EEE MMM dd");
                return shortenedDateFormat.format(inputDate);
            }
        }
    }

    /**
     * Given a day, returns just the name to use for that day.
     * E.g "today", "tomorrow", "wednesday".
     *
     * @param context Context to use for resource localization
     * @param dateStr The db formatted date string, expected to be of the form specified
     *                in Utility.DATE_FORMAT
     * @return returns the name to use for that day
     */
    public static String getDayName(Context context, String dateStr) {
        SimpleDateFormat dbDateFormat = new SimpleDateFormat(Utility.DATE_FORMAT);
        try {
            Date inputDate = dbDateFormat.parse(dateStr);
            Date todayDate = new Date();
            // If the date is today, return the localized version of "Today" instead of the actual
            // day name.
            if (getDbDateString(todayDate).equals(dateStr)) {
                return context.getString(R.string.today);
            } else {
                // If the date is set for tomorrow, the format is "Tomorrow".
                Calendar cal = Calendar.getInstance();
                cal.setTime(todayDate);
                cal.add(Calendar.DATE, 1);
                Date tomorrowDate = cal.getTime();
                if (getDbDateString(tomorrowDate).equals(
                        dateStr)) {
                    return context.getString(R.string.tomorrow);
                } else {
                    // Otherwise, the format is just the day of the week (e.g "Wednesday".
                    SimpleDateFormat dayFormat = new SimpleDateFormat("EEEE");
                    return dayFormat.format(inputDate);
                }
            }
        } catch (ParseException e) {
            e.printStackTrace();
            // It couldn't process the date correctly.
            return "";
        }
    }


    /**
     * Converts db date format to the format "Month day", e.g "June 24".
     * @param context Context to use for resource localization
     * @param dateStr The db formatted date string, expected to be of the form specified
     *                in Utility.DATE_FORMAT
     * @return The day in the form of a string formatted "December 6"
     */
    public static String getFormattedMonthDay(Context context, String dateStr) {
        SimpleDateFormat dbDateFormat = new SimpleDateFormat(Utility.DATE_FORMAT);
        try {
            Date inputDate = dbDateFormat.parse(dateStr);
            SimpleDateFormat monthDayFormat = new SimpleDateFormat("MMMM dd");
            String monthDayString = monthDayFormat.format(inputDate);
            return monthDayString;
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }


    /**
     * Converts Date class to a String representation, used for easy comparison and database location
     * @param date The input date
     * @return a DB-friendly representation of the date using the format defined in DATE_FORMAT
     */
    public static String getDbDateString(Date date) {
        // Because the API returns a unix timestamp (measured in seconds),
        // it must be converted to milliseconds in order to be converted to valid date
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
        return sdf.format(date);
    }

    /**
     * Converts a dateText to a long Unix time representation
     * @param dateText the input date string
     * @return the Date object
     */
    public static Date getDateFromDb(String dateText) {
        SimpleDateFormat dbDateFormat = new SimpleDateFormat(DATE_FORMAT);
        try {
            return dbDateFormat.parse(dateText);
        } catch ( ParseException e ) {
            e.printStackTrace();
            return null;
        }
    }


    /**
     * Display a TSnackbar at the top of the screen with a new message received and a button redirecting to the conversation
     * @param context the context
     * @param activity the Activity to send the intent from
     * @param targetActivityClass the class Activity to send the intent to
     * @param bundle A bundle to sent with the intent
     * @param view The view under which display the notification
     */
    public static void displayNewMsgNotification(final Context context, final FragmentActivity activity, Class targetActivityClass, final Bundle bundle, View view)
    {

        //Extract the conversation
        ModelConversation conversation = (ModelConversation) bundle.getSerializable("convObj");

        String msg = "";
        if (conversation != null) {
             msg = conversation.getName() + ": " + conversation.getPreviewText();
        }

        TSnackbar snackbar = TSnackbar
                .make(view, msg, TSnackbar.LENGTH_LONG)
                .setAction("See", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.d("SnackBar Button", " onClick triggered");
                        Intent i = new Intent(activity, Message.class);
                        i.putExtras(bundle);
                        activity.startActivity(i);
                    }
                });
        snackbar.setActionTextColor(Color.LTGRAY);
        snackbar.setDuration(TSnackbar.LENGTH_LONG);
        snackbar.addIcon(R.mipmap.ic_core, 100);
        View snackbarView = snackbar.getView();
        snackbarView.setBackgroundColor(Color.parseColor("#555555"));
        TextView textView = (TextView) snackbarView.findViewById(com.androidadvance.topsnackbar.R.id.snackbar_text);
        textView.setTextColor(Color.WHITE);
        snackbar.show();

    }


    /**
     * Set up Touch Listeners for the UI to be able to open and close the keyboard
     * @param view parentView id to start the recursion from
     * @param activity activity class where to attach the touchlistener
     */
    public static void setupUITouchListener(View view, final Activity activity) {

        //Set up touch listener for non-text box views to hide keyboard.
        if(!(view instanceof EditText)) {

            view.setOnTouchListener(new View.OnTouchListener() {

                public boolean onTouch(View v, MotionEvent event) {
                    hideSoftKeyboard(activity);
                    return false;
                }

            });
        }

        //If a layout container, iterate over children and seed recursion.
        if (view instanceof ViewGroup) {

            for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {

                View innerView = ((ViewGroup) view).getChildAt(i);

                setupUITouchListener(innerView, activity);
            }
        }
    }

    public static void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager = (InputMethodManager)  activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
    }


    /**
     *  Draw a mask around an image (used for bubble media messages)
     * @param context Context
     * @param image The original image
     * @param drawable The mask to set on the image (of type NinePatch)
     * @param w width
     * @param h height
     * @return a bitmap with the mask applied
     */
    public static Bitmap drawMediaWithMask(Context context, Bitmap image, int drawable, int w, int h)
    {

        Bitmap resize_image = Bitmap.createScaledBitmap(image, w, h, false);
        resize_image.setHasAlpha(true);

        Bitmap mask = BitmapFactory.decodeResource(context.getResources(), drawable);
        if (mask.getNinePatchChunk()!=null){
            byte[] chunk = mask.getNinePatchChunk();
            NinePatchDrawable ninepatch = new NinePatchDrawable(context.getResources(), mask, chunk, new Rect(), null);
            ninepatch.setBounds(0, 0, w, h);

            Bitmap resize_mask = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
            Canvas mask_canvas = new Canvas(resize_mask);
            ninepatch.draw(mask_canvas);


            Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
            paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));

            Bitmap final_image = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(final_image);
            canvas.drawBitmap(resize_image, 0, 0, null);
            canvas.drawBitmap(resize_mask, 0, 0, paint);
            paint.setXfermode(null);

            return final_image;
        }

        return resize_image;

    }

}
