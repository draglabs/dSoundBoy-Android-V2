package com.draglabs.dsoundboy.dsoundboy.Interfaces

/**
 * Created by davrukin on 9/18/17.
 * Tutorial link: https://stackoverflow.com/a/18279548
 * @author Daniel Avrukin
 */

@Deprecated("No longer needed")
interface CallbackListener {

    // AuthenticateUser
    fun uniqueUserIDset()

    // StartJam and ExitJam
    fun jamPINset()

    fun jamIDset()
    fun jamStartTimeSet()
    fun jamEndTimeSet()

    // GetCollaborators
    fun getCollaboratorsSet()

    // GetUserActivity
    fun getUserActivitySet()

    // GetJamDetails
    fun getJamDetailsSet()

    // GetAccessToken
    fun getAccessTokenSet()
}
