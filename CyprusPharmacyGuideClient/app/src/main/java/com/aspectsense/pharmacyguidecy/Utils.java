package com.aspectsense.pharmacyguidecy;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.location.Location;

import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

/**
 * @author Nearchos
 * Created: 28-Mar-20
 */
public class Utils {

    /**
     * Checks if current date is May 1st to Sep 30th (summer time)
     *
     * @return true iff current date is May 1st to Sep 30th
     */
    static boolean isSummerTime() {
        final Date date = new Date();
        final LocalDate localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        final int month = localDate.getMonthValue(); // 1 = Jan, 12 = Dec
        return month >= 5 && month <= 9;
    }

    /**
     * Checks if current date is Sep 30th to May 1st (winter time)
     *
     * @return true iff current date is May 1st to Sep 30th
     */
    public static boolean isWinterTime() {
        return !isSummerTime();
    }

    private static final TimeZone TIME_ZONE_NICOSIA = TimeZone.getTimeZone("Europe/Nicosia");

    /**
     * Returns true if pharmacies are open, i.e.:
     *
     * Summer time
     *  - Mon, Tue, Thu, Fri: 8:00am to 13:30pm & 16:00pm to 19:30pm
     *  - Wed, Sat: 8:00 am to 13:30pm
     *  - Sun: closed
     *
     * Winter time
     *  - Mon, Tue, Thu, Fri: 8:00am to 13:30pm & 15:00pm to 18:30pm
     *  - Wed, Sat: 8:00 am to 13:30pm
     *  - Sun: closed
     *
     * @return true iff pharmacies are currently open
     */
    public static boolean arePharmaciesOpen() {
        final Date date = new Date();
        final ZonedDateTime zonedDateTime = date.toInstant().atZone(TIME_ZONE_NICOSIA.toZoneId());
        final LocalDate localDate = zonedDateTime.toLocalDate();
        final LocalTime localTime = zonedDateTime.toLocalTime();

        final int month = localDate.getMonthValue(); // 1 = Jan, 12 = Dec
        final boolean isSummerTime = month >= 5 && month <= 9;

        final DayOfWeek dayOfWeek = localDate.getDayOfWeek();
        final int hour = localTime.getHour(); // 0..23
        final int minute = localTime.getMinute(); // 0..59
        final boolean morningTime = hour >= 8 && (hour < 13 || (hour == 13 && minute <= 30)); // 8:00 to 13:30
        final boolean afternoonTime = isSummerTime ?
                hour >= 16 && (hour < 19 || (hour == 19 && minute <= 30)) : // summer time 16:00 to 19:30
                hour >= 15 && (hour < 18 || (hour == 18 && minute <= 30));  // winter 15:00 to 18:30

        switch (dayOfWeek) {
            case MONDAY:
            case TUESDAY:
            case THURSDAY:
            case FRIDAY:
                return morningTime || afternoonTime;
            case WEDNESDAY:
            case SATURDAY: // morning only on Wed and Sat
                return morningTime;
            case SUNDAY: // closed on Sundays
            default:
                return false;
        }
    }

    /**
     * Returns next closing time or empty string if already closed.
     *
     * E.g. it returns 13:30 if between 8:00 and 13:30 in the morning
     * and for Mon, Tue, Thu, Fri it returns 19:30 if between 16:00 and 19:30 (for summer time)
     *
     * Summer time
     *  - Mon, Tue, Thu, Fri: 8:00am to 13:30pm & 16:00pm to 19:30pm
     *  - Wed, Sat: 8:00 am to 13:30pm
     *  - Sun: closed
     *
     * Winter time
     *  - Mon, Tue, Thu, Fri: 8:00am to 13:30pm & 15:00pm to 18:30pm
     *  - Wed, Sat: 8:00 am to 13:30pm
     *  - Sun: closed
     *
     * @return next closing time (e.g. "13:30") or empty string (i.e. "") if already closed
     */
    public static String getNextClosingTime() {
        final Date date = new Date();
        final ZonedDateTime zonedDateTime = date.toInstant().atZone(TIME_ZONE_NICOSIA.toZoneId());
        final LocalDate localDate = zonedDateTime.toLocalDate();
        final LocalTime localTime = zonedDateTime.toLocalTime();

        final int month = localDate.getMonthValue(); // 1 = Jan, 12 = Dec
        final boolean isSummerTime = month >= 5 && month <= 9;

        final DayOfWeek dayOfWeek = localDate.getDayOfWeek();
        final int hour = localTime.getHour(); // 0..23
        final int minute = localTime.getMinute(); // 0..59
        final boolean morningTime = hour >= 8 && (hour < 13 || (hour == 13 && minute <= 30)); // 8:00 to 13:30
        final boolean afternoonTime = isSummerTime ?
                hour >= 16 && (hour < 19 || (hour == 19 && minute <= 30)) : // summer time 16:00 to 19:30
                hour >= 15 && (hour < 18 || (hour == 18 && minute <= 30));  // winter 15:00 to 18:30

        switch (dayOfWeek) {
            case MONDAY:
            case TUESDAY:
            case THURSDAY:
            case FRIDAY:
                if(morningTime)
                    return "13:30";
                else if(afternoonTime)
                    return isSummerTime ? "19:30" : "18:30";
            case WEDNESDAY:
            case SATURDAY: // morning only on Wed and Sat
                if(morningTime)
                    return "13:30";
            case SUNDAY: // closed on Sundays
            default:
                return "";
        }
    }

    public static boolean isEarlyHours() {
        final int hourOfDay = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        return hourOfDay < 8; // between midnight and 7:59
    }

    public static boolean isEvening() {
        final int hourOfDay = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        return hourOfDay >= 19; // between 19:00 and 23:59
    }

    static String getDayBeforeYesterdayAsString() {
        final Date dayBeforeYesterday = Date.from(LocalDate.now().minusDays(2).atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
        return formatAsDate(dayBeforeYesterday);
    }

    public static Date getYesterday() {
        return Date.from(LocalDate.now().minusDays(1).atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
    }

    public static Date getToday() {
        return Date.from(LocalDate.now().atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
    }

    public static Date getTomorrow() {
        return Date.from(LocalDate.now().plusDays(1).atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
    }

    private static String formatAsDate(final Date date) {
        return new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(date);
    }

    public static String formatAsDay(final Date date) {
        return new SimpleDateFormat("EEE, MMM d", Locale.getDefault()).format(date);
    }

    public static boolean isGreek(final Locale locale) {
        return "el".equals(locale.getLanguage());
    }

    public static float distanceBetween(final Location location, final float lat, final float lng) {
        return location == null ? 0f : distanceBetween(location.getLatitude(), location.getLongitude(), lat, lng);
    }

    public static float distanceBetween(final double lat1, final double lng1, final float lat2, final float lng2) {
        final float [] results = new float[2];
        Location.distanceBetween(lat1, lng1, lat2, lng2, results);
        return results[0];
    }

    static boolean isIntentAvailable(final Context context, final Intent intent) {
        if(context == null) return false;
        final PackageManager packageManager = context.getPackageManager();
        final List<ResolveInfo> list = packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
        return list.size() > 0;
    }

    /**
     * Creates an intent for enabling a 'share' action. If no app exists to handle the intent, the
     * method returns false.
     *
     * @param context the activity/application {@link Context}
     * @return true if the sharing intent succeeded, false otherwise
     */
    static boolean shareApp(final Context context) {

        final String title = context.getString(R.string.app_name);
        final String text = context.getString(R.string.Install);
        final Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, title);
        shareIntent.putExtra(Intent.EXTRA_TEXT, text);
        final String shareText = context.getString(R.string.Share);
        if(isIntentAvailable(context, shareIntent)) {
            context.startActivity(Intent.createChooser(shareIntent, shareText));
            return true;
        } else {
            return false;
        }
    }

}