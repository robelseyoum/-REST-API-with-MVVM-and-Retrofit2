package com.robelseyoum3.foodrecipes.requests;

import androidx.lifecycle.LiveData;

import com.robelseyoum3.foodrecipes.requests.response.ApiResponse;
import com.robelseyoum3.foodrecipes.requests.response.RecipeResponse;
import com.robelseyoum3.foodrecipes.requests.response.RecipeSearchResponse;

import retrofit2.http.GET;
import retrofit2.http.Query;

public interface RecipeApi {

// https://recipesapi.herokuapp.com/api/search?q=chicken&page=3
// https://recipesapi.herokuapp.com/api/get?rId=41470


    //SEARCH
    @GET("/api/search")
    LiveData<ApiResponse<RecipeSearchResponse>> searchRecipe(
            @Query("q") String key, //appends ?
            @Query("page") String page //appends &
    );

    //GET RECIPE REQUEST
    @GET("/api/get")
    LiveData<ApiResponse<RecipeResponse>> getRecipe(
      @Query("rId") String recipe_id
    );
}
