package com.draglabs.dsoundboy.dsoundboy.Interfaces;

/**
 * Created by davrukin on 9/18/17.
 * Tutorial link: https://stackoverflow.com/a/18279548
 */

public interface CallbackListener {

    // AuthenticateUser
    void uniqueUserIDset();

    // StartJam and ExitJam
    void jamPINset();
    void jamIDset();
    void jamStartTimeSet();
    void jamEndTimeSet();

    // GetCollaborators
    void getCollaboratorsSet();

    // GetUserActivity
    void getUserActivitySet();

    // GetJamDetails
    void getJamDetailsSet();

    // GetAccessToken
    void getAccessTokenSet();
}
