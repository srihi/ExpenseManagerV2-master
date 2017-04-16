package expmanager.idea.spark.in.expensemanager.utils;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;


/**
 * Created by Haresh.Veldurty on 2/21/2017.
 */

public class Utils {

    public static DisplayMetrics getDeviceMetrics(Context context) {
        DisplayMetrics metrics = new DisplayMetrics();
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        display.getMetrics(metrics);
        return metrics;
    }

    public static String getDeviceId(Context context) {

        // for Emulators, pass 123 as the deviceId
        if ((Build.DEVICE.equals("generic")) || (Build.DEVICE.equals("generic_x86")) || (Build.DEVICE.equals("vbox86p"))) {
            return "123";
        }
        return Settings.Secure.getString(context.getContentResolver(),
                Settings.Secure.ANDROID_ID);
    }

    public static String getDateTime() {
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        Date date = new Date();
        return dateFormat.format(date);
    }

    public static String getDateTimeforFormat(String format){
        DateFormat dateFormat = new SimpleDateFormat(format);
        Date date = new Date();
        return dateFormat.format(date);
    }

    public static int[] getWeeksOfMonth(int month, int year) {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.MONTH, month);
        cal.set(Calendar.DAY_OF_MONTH, 1);

        int ndays = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
        int weeks[] = new int[ndays];
        for (int i = 0; i < ndays; i++) {
            weeks[i] = cal.get(Calendar.WEEK_OF_YEAR);
            cal.add(Calendar.DATE, 1);
        }
        return weeks;
    }

    public static int dpConverter(int val, Context ctx) {
     float di = ctx.getResources().getDisplayMetrics().density;
     int margin = (int) (val * di);
      return margin;
    }

    /**
     * Method to hide the keyboard. Pass the activity context
     * @param context
     */
    public static void hideKeyboard(Activity context){
        InputMethodManager inputManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);

        if(context.getCurrentFocus() != null) {
            inputManager.hideSoftInputFromWindow(context.getCurrentFocus().getWindowToken(),
                    InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }


    public static int getCurrentWeekofYear() {
        return Calendar.getInstance().get(Calendar.WEEK_OF_YEAR);
    }

    public static String getStartDateofCurrentWeek(){
        Calendar c = Calendar.getInstance();

        System.out.println("Current week = " + Calendar.DAY_OF_WEEK);

        // Set the calendar to monday of the current week
        c.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
        c.setFirstDayOfWeek(Calendar.SUNDAY);
        System.out.println("Current week = " + Calendar.DAY_OF_WEEK);

        // Print dates of the current week starting on Monday
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String startDate = "", endDate = "";

        c.add(Calendar.DATE, -6);
        startDate = df.format(c.getTime());
        c.add(Calendar.DATE, 6);
        endDate = df.format(c.getTime());

        System.out.println("Start Date = " + startDate);
        System.out.println("End Date = " + endDate);

        return startDate;
    }

    public static String getEndDateofCurrentWeek(){
       Calendar c = GregorianCalendar.getInstance();

        System.out.println("Current week = " + Calendar.DAY_OF_WEEK);

        // Set the calendar to monday of the current week
        c.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
        c.setFirstDayOfWeek(Calendar.SUNDAY);
        System.out.println("Current week = " + Calendar.DAY_OF_WEEK);

        // Print dates of the current week starting on Monday
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        String startDate = "", endDate = "";
        c.add(Calendar.DATE, -6);
        startDate = df.format(c.getTime());
        c.add(Calendar.DATE, +6);
        endDate = df.format(c.getTime());

        System.out.println("Start Date = " + startDate);
        System.out.println("End Date = " + endDate);

        return endDate;
    }
}
