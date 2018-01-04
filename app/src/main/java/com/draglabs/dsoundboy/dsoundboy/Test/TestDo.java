/*
 * Daniel Avrukin of Drag Labs. Copyright (c) 2016-2018. All Rights Reserved.
 */

package com.draglabs.dsoundboy.dsoundboy.Test;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by davrukin on 1/3/2018.
 */

public class TestDo {

    /*private void testDo(Context context, String facebookID, String accessToken) {
        Retrofit userService = getClient();
        ApiInterface api = userService.create(ApiInterface.class);
        Call<ResponseModelKt.UserFunctions.RegisterUser> call = api.registerUser(facebookID, accessToken);
        call.enqueue(new Callback<ResponseModelKt.UserFunctions.RegisterUser>() {
            @Override
            public void onResponse(Call<ResponseModelKt.UserFunctions.RegisterUser> call, Response<ResponseModelKt.UserFunctions.RegisterUser> response) {
                if (response.isSuccessful()) {
                    ResponseModelKt.UserFunctions.RegisterUser result = response.body();
                    String id = result.getId();
                    new PrefUtilsKt().storeUUID(context, id);
                } else {
                    Log.d("Failed Response", response.errorBody().toString());
                }
            }

            @Override
            public void onFailure(Call<ResponseModelKt.UserFunctions.RegisterUser> call, Throwable t) {
                Log.d("onFailure Failed", t.getMessage());
            }
        });
    }*/

    private Retrofit getClient() {
        return new Retrofit.Builder()
                .baseUrl("http://api.draglabs.com/api/v2.0/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }
}
