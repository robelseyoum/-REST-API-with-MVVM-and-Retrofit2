package com.robelseyoum3.foodrecipes.requests;

import com.robelseyoum3.foodrecipes.requests.response.RecipeResponse;
import com.robelseyoum3.foodrecipes.requests.response.RecipeSearchResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface RecipeApi {

// https://recipesapi.herokuapp.com/api/search?q=chicken&page=3
// https://recipesapi.herokuapp.com/api/get?rId=41470


    //SEARCH
    @GET("/api/search")
    Call<RecipeSearchResponse> searchRecipe(
            @Query("q") String key, //appends ?
            @Query("page") String page //appends &
    );

    //GET RECIPE REQUEST
    @GET("/api/get")
    Call<RecipeResponse> getRecipe(
      @Query("rId") String recipe_id
    );
}
