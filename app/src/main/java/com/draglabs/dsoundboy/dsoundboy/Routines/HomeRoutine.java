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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.draglabs.dsoundboy.dsoundboy.Activities.AboutActivity;
import com.draglabs.dsoundboy.dsoundboy.Activities.ContactActivity;
import com.draglabs.dsoundboy.dsoundboy.Activities.EnterInfoActivity;
import com.draglabs.dsoundboy.dsoundboy.Activities.ListOfRecordingsActivity;
import com.draglabs.dsoundboy.dsoundboy.R;
import com.draglabs.dsoundboy.dsoundboy.Utils.APIutils;
import com.draglabs.dsoundboy.dsoundboy.Utils.PrefUtils;
import com.draglabs.dsoundboy.dsoundboy.Utils.RecorderUtils;

import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import omrecorder.Recorder;

/**
 * <p>The home routine which shows all the recording screen stuff</p>
 * <p>Created by davrukin on 11/3/17.</p>
 */
@Deprecated
public class HomeRoutine {

    private HashMap<String, Object> buttons;
    private Activity activity;
    private Context context;
    private PrefUtils prefUtils;
    private RecorderUtils recorderUtils;
    private Recorder recorder;

    /**
     * Constructor for the Home Routine
     * @param buttons the buttons seen on the UI
     * @param activity the activity calling the routine
     * @param context the context calling the routine
     */
    public HomeRoutine(HashMap<String, Object> buttons, Activity activity, Context context, String path, Button recordButton) {
        this.buttons = buttons;
        this.activity = activity;
        this.context = context;
        this.prefUtils = new PrefUtils(activity);
        this.recorderUtils = new RecorderUtils(context, null, activity);
        this.recorder = recorderUtils.setupRecorder(path, recordButton);
    }

    /**
     * What happens when selecting an item in the menu list
     * @param position the item's position in the list
     * @param settingsMenuItemTitles the titles of the menu items
     * @param settingsMenuDrawerLayout the layout of the drawer
     * @param settingsMenuItemList the list of the items in the drawer
     */
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

    /**
     * Contains a single fragment that stores the nav drawer
     */
    public static class ItemFragment extends Fragment {
        // from https://developer.android.com/training/implementing-navigation/nav-drawer.html
        public static final String ARG_ITEM_NAME = "item_name";

        public ItemFragment() {}

        /**
         * Creates the view for the fragment
         * @param layoutInflater inflates the layout
         * @param container contains the view group
         * @param savedInstanceState the saved instance state
         * @return the view created by the fragment
         */
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

    /**
     * Opens the About Activity
     */
    public void clickAbout() {
        Intent intent = new Intent(context, AboutActivity.class);
        activity.startActivity(intent);
    }

    /**
     * Opens the Contact Activity
     */
    public void clickContact() {
        Intent intent = new Intent(context, ContactActivity.class);
        activity.startActivity(intent);
    }

    /**
     * Opens the Enter Info Activity
     */
    public void clickEnterInfo() {
        Intent intent = new Intent(context, EnterInfoActivity.class);
        activity.startActivity(intent);
    }

    /**
     * Opens the View Recordings Activity
     */
    public void clickViewRecordings() {
        // APIutils.getUserActivity(this, uniqueUserID, this);
        Intent intent = new Intent(activity, ListOfRecordingsActivity.class);
        activity.startActivity(intent);
    }

    /**
     * Goes to the company website when the logo is clicked
     */
    public void clickLogoLink() {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://draglabs.com"));
        activity.startActivity(browserIntent);
    }

    /**
     * Starts recording audio
     * @param chronometer the chronometer
     */
    @Deprecated
    public void clickRec(Chronometer chronometer) {
        createJam();

        chronometer.setBase(SystemClock.elapsedRealtime());
        chronometer.start();
        //recorderUtils.startRecording(activity);
        //recorderUtils.startRecorderNew(activity);
        recorderUtils.startRecording(recorder);

        Toast.makeText(context, "Started recording.", Toast.LENGTH_LONG).show();
    }

    /**
     * Stops recording audio
     * @param view the view calling the method
     * @param chronometer stops the chronometer
     * @param recordingStartTime submits the recording's start time to the server
     * @param recordingEndTime submits the recording's end time to the server
     */
    @Deprecated
    public void clickStop(View view, Chronometer chronometer, Date recordingStartTime, Date recordingEndTime) {
        //recorderUtils.stopRecording();
        recorderUtils.stopRecording(recorder);
        chronometer.stop();

        Toast.makeText(context, "Stopped recording.", Toast.LENGTH_LONG).show();

        submitToServer(view, recorderUtils, recordingStartTime, recordingEndTime);
    }

    /**
     * Submits the file to the server
     * @param view the view calling the method
     * @param recorderUtils the recorder settings
     * @param recordingStartTime the recording's start time
     * @param recordingEndTime the recording's end time
     */
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
                context,
                prefUtils.getUniqueUserID(),
                prefUtils.getJamID(),
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

    /**
     * Creates a jam, shows a new jam PIN if there isn't one already, gets the device's location
     */
    public void createJam() {
        //LocationManager locationManager = (LocationManager)getSystemService(LOCATION_SERVICE);
        String provider = LocationManager.GPS_PROVIDER;
        //locationManager.requestLocationUpdates(provider, 5000, 10, this);
        Location location = new Location(provider);
        prefUtils = new PrefUtils(activity);

        if (!prefUtils.hasJamPIN()) {
            APIutils.newJam(activity, context, prefUtils.getUniqueUserID(), PrefUtils.getRecordingVenue(activity), PrefUtils.getRecordingDescription(activity), location);
            // may have to implement without a while loop
            prefUtils = new PrefUtils(activity);
            showNewJamPinDialog(context, "Jam PIN", prefUtils.getJamPIN());
        } else {
            Log.v("Current Jam PIN: ", prefUtils.getJamPIN());
        }

        /*String jamPIN = prefUtils.getJamPIN();
        Log.v("Jam PIN: ", jamPIN); // TODO: SHOW JSON RESPONSE IF ERROR AS INDEFINITE SNACKBAR OR TOAST
        String newJamPIN = jamPIN.substring(0, 3) + "-" + jamPIN.substring(3, 6) + "-" + jamPIN.substring(6, 9);
        showNewJamPinDialog(context, "Jam PIN", newJamPIN);*/
    }

    /**
     * Exits a jam
     */
    public void exitJam() {
        prefUtils = new PrefUtils(activity);
        APIutils.exitJam(prefUtils.getUniqueUserID(), prefUtils.getJamID());
    }

    /**
     * Joins a jam, shows dialog to enter a jam PIN
     */
    public void joinJam() {
        showEnterJamPinDialog(context); // doesn't show keyboard automatically right away, it should
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

    /**
     * Creates the dialog to show the new jam PIN
     * @param context the app context
     * @param title the title of the dialog
     * @param jamPIN the new jam PIN
     */
    private void showNewJamPinDialog(Context context, String title, String jamPIN) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(jamPIN).setTitle(title);
        builder.setNeutralButton("Okay", (dialog, which) -> dialog.dismiss());
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    /**
     * Creates the dialog to enter the new jam PIN
     * @param context the app context
     */
    private void showEnterJamPinDialog(Context context) {
        LayoutInflater inflater = activity.getLayoutInflater();
        View alertLayout = inflater.inflate(R.layout.layout_join_jam_dialog, null);
        final EditText joinJamPin = alertLayout.findViewById(R.id.join_jam_enter_pin_text);

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Enter a Jam PIN");
        builder.setView(alertLayout);
        builder.setCancelable(false);
        builder.setNegativeButton("Cancel", (dialogInterface, i) ->
            Toast.makeText(context, "Cancel clicked", Toast.LENGTH_LONG).show());
        builder.setPositiveButton("Done", (dialogInterface, i) -> {
            String jamPinEntered = joinJamPin.getText().toString();
            prefUtils = new PrefUtils(activity);
            prefUtils.saveJamPIN(jamPinEntered);
            Log.d("Jam PIN Entered: ", prefUtils.getJamPIN());
            String UUID = checkUUID(prefUtils);
            Log.d("UUID into Join Jam:", UUID);
            APIutils.joinJam(activity, context, jamPinEntered, UUID); // TODO: show error if incorrect
            //useDisposable(disposable, jamPinEntered, UUID);
        });

        AlertDialog dialog = builder.create();
        dialog.show();
        // TODO: enter jam pin here and return it
    }

    /*private void useDisposable(Disposable disposable, String jamPIN, String UUID) {
        io.reactivex.Observable asd = new ApiInterface().joinJam()
        disposable = new ApiInterface.joinJam(jamPIN, UUID)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe();
    }*/

    private String checkUUID() {
        String readUUID = PrefUtils.readUUID();

        if (readUUID != null || !readUUID.equals("")) {
            Log.v("Checked UUID:", readUUID);
            return readUUID;
        } else {
            APIutils.registerUser(activity, context);
            Log.v("Refreshed UUID:", readUUID);
            return readUUID;
        }
    }

    private String checkUUID(PrefUtils prefUtils) {
        if (prefUtils.getUniqueUserID() != null || !prefUtils.getUniqueUserID().equals("")) {
            Log.v("Checked UUID:", prefUtils.getUniqueUserID());
            return prefUtils.getUniqueUserID();
        } else {
            APIutils.registerUser(activity, context);
            Log.v("Refreshed UUID:", prefUtils.getUniqueUserID());
            return prefUtils.getUniqueUserID();
        }
    }

    /**
     * Returns the UI elements
     * @return buttons
     */
    public HashMap<String, Object> getButtons() {
        return buttons;
    }

    public RecorderUtils getRecorderUtils() {
        return recorderUtils;
    }

    public void setRecorderUtils(RecorderUtils recorderUtils) {
        this.recorderUtils = recorderUtils;
    }

    public Recorder getRecorder() {
        return recorder;
    }
}
