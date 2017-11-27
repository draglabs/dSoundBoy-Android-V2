package com.draglabs.dsoundboy.dsoundboy.Routines;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.DrawerLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Chronometer;
import android.widget.ListView;
import android.widget.Toast;

import com.draglabs.dsoundboy.dsoundboy.Utils.RecorderUtils;
import com.draglabs.dsoundboy.dsoundboy.Activities.AboutActivity;
import com.draglabs.dsoundboy.dsoundboy.Activities.ContactActivity;
import com.draglabs.dsoundboy.dsoundboy.Activities.EnterInfoActivity;
import com.draglabs.dsoundboy.dsoundboy.Activities.ListOfRecordingsActivity;
import com.draglabs.dsoundboy.dsoundboy.R;
import com.draglabs.dsoundboy.dsoundboy.Utils.APIutils;
import com.draglabs.dsoundboy.dsoundboy.Utils.PrefUtils;

import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

/**
 * Created by davrukin on 11/3/17.
 */
public class HomeRoutine {

    private HashMap<String, Object> buttons;
    private Activity activity;
    private Context context;
    private PrefUtils prefUtils;

    public HomeRoutine(HashMap<String, Object> buttons, Activity activity, Context context) {
        this.buttons = buttons;
        this.activity = activity;
        this.context = context;
        this.prefUtils = new PrefUtils(activity);
    }

    public void selectItem(int position, String[] settingsMenuItemTitles, DrawerLayout settingsMenuDrawerLayout, ListView settingsMenuItemList) {
        // from sample: https://developer.android.com/training/implementing-navigation/nav-drawer.html
        // create a few fragment and specify the activity to launch based on position
        Fragment fragment = new Fragment();
        Bundle args = new Bundle();
        args.putInt(ItemFragment.ARG_ITEM_NAME, position);
        fragment.setArguments(args);

        // insert fragment by replacing existing fragment
        FragmentManager fragmentManager = activity.getFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.content, fragment).commit();

        // highlight the selected item, update the title, and close the drawer
        settingsMenuItemList.setItemChecked(position, true);
        activity.setTitle(settingsMenuItemTitles[position]);
        settingsMenuDrawerLayout.closeDrawer(settingsMenuItemList);
    }

    public static class ItemFragment extends Fragment {
        // from https://developer.android.com/training/implementing-navigation/nav-drawer.html
        public static final String ARG_ITEM_NAME = "item_name";

        public ItemFragment() {}

        @Override
        public View onCreateView(LayoutInflater layoutInflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = layoutInflater.inflate(R.layout.navigation_view_1, container, false);
            int i = getArguments().getInt(ARG_ITEM_NAME);
            String itemName = getResources().getStringArray(R.array.menu_item_names)[i];
            int itemID = getResources().getIdentifier(itemName.toLowerCase(Locale.getDefault()), "drawable",
                    getActivity().getPackageName());
            // imageview line ignored from sample
            // ((ImageView) rootView.findViewById(R.id.image)).setImageResource(imageId);
            // where to show next activity?
            getActivity().setTitle(itemName);
            return rootView;
        }
    }

    public void clickAbout() {
        Intent intent = new Intent(context, AboutActivity.class);
        activity.startActivity(intent);
    }

    public void clickContact() {
        Intent intent = new Intent(context, ContactActivity.class);
        activity.startActivity(intent);
    }

    public void clickEnterInfo() {
        Intent intent = new Intent(context, EnterInfoActivity.class);
        activity.startActivity(intent);
    }

    public void clickViewRecordings() {
        // APIutils.getUserActivity(this, uniqueUserID, this);
        Intent intent = new Intent(activity, ListOfRecordingsActivity.class);
        activity.startActivity(intent);
    }

    public void clickLogoLink() {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://draglabs.com"));
        activity.startActivity(browserIntent);
    }

    public void clickRec(Chronometer chronometer, RecorderUtils recorderUtils) {
        createJam();

        chronometer.setBase(SystemClock.elapsedRealtime());
        chronometer.start();
        recorderUtils.startRecording(activity);

        Toast.makeText(context, "Started recording.", Toast.LENGTH_LONG).show();
    }

    public void clickStop(View view, RecorderUtils recorderUtils, Chronometer chronometer, Date recordingStartTime, Date recordingEndTime) {
        recorderUtils.stopRecording();
        chronometer.stop();

        Toast.makeText(context, "Stopped recording.", Toast.LENGTH_LONG).show();

        submitToServer(view, recorderUtils, recordingStartTime, recordingEndTime);
    }

    private void submitToServer(View view, RecorderUtils recorderUtils, Date recordingStartTime, Date recordingEndTime) {

        /*Thread thread = new Thread(() -> APIutils.jamRecordingUpload(uniqueUserID, jamID, "davrukin-test", recordingPath, "notes", recordingStartTime, recordingEndTime, view));
        thread.run();*/

        Snackbar.make(view, "Submitting Recording to Jam.", Snackbar.LENGTH_LONG).show();
        // TODO: more
        // TODO: finalize recording, close all buffers
        // TODO: go through folder to see which recording were from the current session, and upload all of those

        if (recordingStartTime == null) {
            recordingStartTime = new Date();
        }
        if (recordingEndTime == null) {
            recordingEndTime = new Date();
        }

        prefUtils = new PrefUtils(activity);
        String recordingPath = recorderUtils.getAudioSavePathInDevice();
        APIutils.jamRecordingUpload(
                prefUtils.getUniqueUserID(),
                prefUtils.getJamID(),
                PrefUtils.getRecordingVenue(activity),
                recordingPath, PrefUtils.getRecordingDescription(activity),
                recordingStartTime, recordingEndTime,
                view);

        /*int id = 1;
        NotificationManager notificationManager = (NotificationManager)activity.getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder notificationCompatBuilder = new NotificationCompat.Builder(context);
        notificationCompatBuilder.setContentTitle("Recording Upload").setContentText("Upload in progress").setSmallIcon(R.drawable.drag_labs_logo);
        Handler handler = new Handler(Looper.getMainLooper());
        Runnable runnable = new Thread(() -> {
            for (int i = 0; i < 100; i += 5) {
                notificationCompatBuilder.setProgress(100, i, false);
                notificationManager.notify(id, notificationCompatBuilder.build());

                String recordingPath = recorderUtils.getAudioSavePathInDevice();
                APIutils.jamRecordingUpload(
                        prefUtils.getUniqueUserID(),
                        prefUtils.getJamID(),
                        PrefUtils.getRecordingVenue(activity),
                        recordingPath, PrefUtils.getRecordingDescription(activity),
                        recordingStartTimeServer, recordingEndTimeServer,
                        view);

                notificationCompatBuilder.setContentText("Upload complete").setProgress(0, 0, false);
                notificationManager.notify(id, notificationCompatBuilder.build());
            }

        });
        handler.post(runnable);*/

        // TODO: SET CORRECT RECORDING PATH, START, AND END TIME
    }

    public void createJam() {
        //LocationManager locationManager = (LocationManager)getSystemService(LOCATION_SERVICE);
        String provider = LocationManager.GPS_PROVIDER;
        //locationManager.requestLocationUpdates(provider, 5000, 10, this);
        Location location = new Location(provider);
        prefUtils = new PrefUtils(activity);

        if (!prefUtils.hasJamPIN()) {
            APIutils.startJam(activity, prefUtils.getUniqueUserID(), PrefUtils.getRecordingVenue(activity), PrefUtils.getRecordingDescription(activity), location);
            // may have to implement without a while loop
            prefUtils = new PrefUtils(activity);
            showNewJamPinDialog(context, "Jam PIN", prefUtils.getJamPIN());
        }

        /*String jamPIN = prefUtils.getJamPIN();
        Log.v("Jam PIN: ", jamPIN); // TODO: SHOW JSON RESPONSE IF ERROR AS INDEFINITE SNACKBAR OR TOAST
        String newJamPIN = jamPIN.substring(0, 3) + "-" + jamPIN.substring(3, 6) + "-" + jamPIN.substring(6, 9);
        showNewJamPinDialog(context, "Jam PIN", newJamPIN);*/
    }

    public void exitJam() {
        prefUtils = new PrefUtils(activity);
        APIutils.exitJam(prefUtils.getUniqueUserID(), prefUtils.getJamID());
    }

    public void joinJam() {
        prefUtils = new PrefUtils(activity);
        int jamPIN = showEnterJamPinDialog(context, "Enter a Jam PIN", "yada yada yada");
        APIutils.joinJam(activity, prefUtils.getUniqueUserID(), jamPIN); // TODO: show error if incorrect
    }

    /*public void clickJoinJam() {

        APIutils.joinJam(this, prefUtils.getUniqueUserID(), Integer.parseInt(jamPINtext.getText().toString()));
    }*/

    /*private void setBandInfo() {
        this.emailText = PrefUtils.getArtistEmail(activity);
        this.descriptionText = PrefUtils.getRecordingDescription(activity);
        this.artistNameText = PrefUtils.getArtistName(activity);
        this.venueText = PrefUtils.getArtistEmail(activity);

        Toast.makeText(this, emailText + "\n" + descriptionText + "\n" + artistNameText + "\n" + venueText, Toast.LENGTH_LONG).show();
    }*/

    private void showNewJamPinDialog(Context context, String title, String jamPIN) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(jamPIN).setTitle(title);
        builder.setNeutralButton("Okay", (dialog, which) -> dialog.dismiss());
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private int showEnterJamPinDialog(Context context, String title, String jamPIN) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(jamPIN).setTitle(title);
        builder.setNeutralButton("Okay", (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
        dialog.show();
        // TODO: enter jam pin here and return it
        return 0;
    }

    public HashMap<String, Object> getButtons() {
        return buttons;
    }
}
