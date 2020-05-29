package com.robelseyoum3.foodrecipes.requests;

import com.robelseyoum3.foodrecipes.util.Constants;
import com.robelseyoum3.foodrecipes.util.LiveDataCallAdapterFactory;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.robelseyoum3.foodrecipes.util.Constants.CONNECTION_TIMEOUT;
import static com.robelseyoum3.foodrecipes.util.Constants.READ_TIMEOUT;
import static com.robelseyoum3.foodrecipes.util.Constants.WRITE_TIMEOUT;

public class ServiceGenerator {

    private static OkHttpClient client = new OkHttpClient.Builder()
            //establish connection to server
            .connectTimeout(CONNECTION_TIMEOUT, TimeUnit.SECONDS)

            //time between each byte read from the server
            .readTimeout(READ_TIMEOUT, TimeUnit.SECONDS)
            //time between each byte sent to server
            .writeTimeout(WRITE_TIMEOUT, TimeUnit.SECONDS)

            .retryOnConnectionFailure(false)
            .build();

    private static Retrofit.Builder retrofitBuilder =
            new Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
                    .client(client)
                    //LiveDataCallAdapterFactory is used to produce LiveDataCallAdapter
                    .addCallAdapterFactory(new LiveDataCallAdapterFactory())
            .addConverterFactory(GsonConverterFactory.create());

    private static Retrofit retrofit = retrofitBuilder.build();

    private static RecipeApi recipeApi = retrofit.create(RecipeApi.class);

    public static RecipeApi getRecipeApi(){
        return recipeApi;
    }
}
