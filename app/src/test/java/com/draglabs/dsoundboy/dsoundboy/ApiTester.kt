/*
 * Daniel Avrukin of Drag Labs. Copyright (c) 2018. All Rights Reserved.
 */

package com.draglabs.dsoundboy.dsoundboy

import com.draglabs.dsoundboy.dsoundboy.Interfaces.ApiInterface
import com.draglabs.dsoundboy.dsoundboy.Utils.LogUtils
import com.draglabs.dsoundboy.dsoundboy.Utils.PrefUtils
import com.nhaarman.mockito_kotlin.mock
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import org.junit.Test

/**
 * Created by davrukin on 1/2/2018.
 */

class ApiTester {

    private val facebook_id = "10155794395553028"
    private val access_token = "EAACGUrlj4pUBAM39tWiDeDqYnoKYK7XU1gFB561jZBvT8eHsRBEIAnVeXEvY6trVou68CpoPZBfFWfWpbHJXppSvUZBJXQQRH3Qf3lyeNdYndFdNu2eNYSytNzeZCezAwQqMjheCEW7i1va15bBUDbXMrZAQlZBLNJ4e6SNhrLXASNh7KoBTR1ZBLXZBF2MA6prSTl4ZCjCZAAjd9SSAzYa9QZB42VfGv6FzR8ZD"
    private val postmanUUID = "5a4b230bbe307d06c7ad5c57"

    private var disposable: Disposable? = null
    private val apiInterface by lazy {
        ApiInterface.create()
    }

    private val environment: android.os.Environment = mock()

    @Test
    fun testWriteUUID() {
        PrefUtils.writeUUID(postmanUUID, environment)
        println("UUID: " + PrefUtils.readUUID(environment))
    }

    @Test
    fun testRegisterUser() {
        disposable = apiInterface.registerUser(facebook_id, access_token)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                {result ->
                    LogUtils.debug("UUID", result.id)
                    PrefUtils.writeUUID(result.id)
                },
                {error -> LogUtils.debug("Error Registering User", error.message + "")}
            )
    }

}
