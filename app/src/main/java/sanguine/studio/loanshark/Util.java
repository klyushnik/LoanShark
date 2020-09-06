package sanguine.studio.loanshark;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import androidx.core.app.ActivityCompat;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import static android.content.Context.MODE_PRIVATE;

public final class Util {
    private static final String PREFS_FILE_NAME = "LOAN_SHARK_SETTINGS";
    /*public interface FragmentInteractionListener{ //this is for manipulating the recycler views inside fragments
        void AddRecord();
        void RemoveRecord();
        void ReloadListView();
        void SortListView();
        void FilterListView(String searchString);
    }*/


    public static int GetStatusBarHeight(Context context) {
        int result = 0;
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = context.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    public static int GetNavBarHeight(Context context) {
        int result = 0;
        int resourceId = context.getResources().getIdentifier("navigation_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = context.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    public static String GetTodaysDate(){
        String todaysTimeCode;
        Date now = new Date();
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        todaysTimeCode = dateFormat.format(now);
        return todaysTimeCode;
    }

    public static float GetTotalDebt(float initialDebt, float interestRate, float paidAmt, String interestType, String initialDate, boolean returnWithPayments){
        float result = 0, interest = 0;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date initDate, todaysDate;
        int unitsPassed = 0;
        try {
           initDate = simpleDateFormat.parse(initialDate);
           todaysDate = new Date();
        }catch(Exception e){
            e.printStackTrace();
            return 0;
        }
        unitsPassed = (int)(Math.round((todaysDate.getTime() - initDate.getTime()) / (double)86400000));
        switch (interestType){
            case "d": //daily
                break;
            case "w": //weekly
                unitsPassed /= 7;
                break;
            case "bw": //bi-weekly
                unitsPassed /=14;
                break;
            case "m": //legalese monthly
                unitsPassed /=30;
                break;
            case "y": //annual
                unitsPassed /=365;
                break;
            case "n": //no interest
            default:
                unitsPassed = 0;
        }

        if(unitsPassed > 0){
            for (int i = 0; i < unitsPassed; i++){
                interest += initialDebt * interestRate / 100;
            }
        }
        if(returnWithPayments)
            result = initialDebt + interest - paidAmt;
        else
            result = initialDebt + interest;

        return result;
    }



    //PERMISIONS
    /*
     * Check if version is marshmallow and above.
     * Used in deciding to ask runtime permission
     * */
    public static boolean shouldAskPermission() {
        return (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M);
    }
    private static boolean shouldAskPermission(Context context, String permission){
        if (shouldAskPermission()) {
            int permissionResult = ActivityCompat.checkSelfPermission(context, permission);
            return permissionResult != PackageManager.PERMISSION_GRANTED;
        }
        return false;
    }
    public static void checkPermission(Context context, String permission, PermissionAskListener listener){
        /*
         * If permission is not granted
         * */
        if (shouldAskPermission(context, permission)){
            /*
             * If permission denied previously
             * */
            if (((Activity) context).shouldShowRequestPermissionRationale(permission)) {
                listener.onPermissionPreviouslyDenied();
            } else {
                /*
                 * Permission denied or first time requested
                 * */
                if (isFirstTimeAskingPermission(context, permission)) {
                    firstTimeAskingPermission(context, permission, false);
                    listener.onPermissionAsk();
                } else {
                    /*
                     * Handle the feature without permission or ask user to manually allow permission
                     * */
                    listener.onPermissionDisabled();
                }
            }
        } else {
            listener.onPermissionGranted();
        }
    }
    /*
     * Callback on various cases on checking permission
     *
     * 1.  Below M, runtime permission not needed. In that case onPermissionGranted() would be called.
     *     If permission is already granted, onPermissionGranted() would be called.
     *
     * 2.  Above M, if the permission is being asked first time onPermissionAsk() would be called.
     *
     * 3.  Above M, if the permission is previously asked but not granted, onPermissionPreviouslyDenied()
     *     would be called.
     *
     * 4.  Above M, if the permission is disabled by device policy or the user checked "Never ask again"
     *     check box on previous request permission, onPermissionDisabled() would be called.
     * */
    public interface PermissionAskListener {
        /*
         * Callback to ask permission
         * */
        void onPermissionAsk();
        /*
         * Callback on permission denied
         * */
        void onPermissionPreviouslyDenied();
        /*
         * Callback on permission "Never show again" checked and denied
         * */
        void onPermissionDisabled();
        /*
         * Callback on permission granted
         * */
        void onPermissionGranted();
    }


    public static void firstTimeAskingPermission(Context context, String permission, boolean isFirstTime){
        SharedPreferences sharedPreference = context.getSharedPreferences(PREFS_FILE_NAME, MODE_PRIVATE);
        sharedPreference.edit().putBoolean(permission, isFirstTime).apply();
    }
    public static boolean isFirstTimeAskingPermission(Context context, String permission){
        return context.getSharedPreferences(PREFS_FILE_NAME, MODE_PRIVATE).getBoolean(permission, true);
    }

}
