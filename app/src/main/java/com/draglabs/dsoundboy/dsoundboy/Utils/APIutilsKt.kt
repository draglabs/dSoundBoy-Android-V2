/*
 * Daniel Avrukin of Drag Labs. Copyright (c) 2017. All Rights Reserved.
 */

package com.draglabs.dsoundboy.dsoundboy.Utils

import android.content.Context
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.draglabs.dsoundboy.dsoundboy.Interfaces.ApiInterface
import com.draglabs.dsoundboy.dsoundboy.Models.StringsModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

/**
 * Created by davrukin on 12/30/2017.
 */
class APIutilsKt {

    private val NEW_JAM = StringsModel.apiPaths.NEW_JAM.path()
    private val UPDATE_JAM = StringsModel.apiPaths.UPDATE_JAM.path()
    private val JOIN_JAM = StringsModel.apiPaths.JOIN_JAM.path()
    private val JAM_RECORDING_UPLOAD = StringsModel.apiPaths.JAM_RECORDING_UPLOAD.path()
    private val GET_JAM_DETAILS = StringsModel.apiPaths.GET_JAM_DETAILS.path()
    private val GET_RECORDINGS = StringsModel.apiPaths.GET_RECORDINGS.path()
    private val REGISTER_USER = StringsModel.apiPaths.REGISTER_USER.path()
    private val UPDATE_USER = StringsModel.apiPaths.UPDATE_USER.path()
    private val GET_ACTIVE_JAM = StringsModel.apiPaths.GET_ACTIVE_JAM.path()
    private val GET_USER_ACTIVITY = StringsModel.apiPaths.GET_USER_ACTIVITY.path()

    private var disposable: Disposable? = null

    private val apiInterface by lazy {
        ApiInterface.create()
    }

    fun beginSearch(searchString: String) {
        disposable = apiInterface.joinJam("123", "456")
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe()
    }

    fun testRequest(context: Context, url: String) {
        val requestQueue = Volley.newRequestQueue(context)
        val stringRequest = StringRequest(Request.Method.GET, url, Response.Listener {  }, Response.ErrorListener {  })
        requestQueue.add(stringRequest)
    }

    fun pauseDisposable(disposable: Disposable) {
        disposable.dispose()
    }

}