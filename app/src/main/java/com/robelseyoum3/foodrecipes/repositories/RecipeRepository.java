package com.robelseyoum3.foodrecipes.repositories;

import androidx.lifecycle.LiveData;

import com.robelseyoum3.foodrecipes.models.Recipe;
import com.robelseyoum3.foodrecipes.requests.RecipeApiClient;

import java.util.List;

public class RecipeRepository {

    private static RecipeRepository instance;
    private RecipeApiClient mRecipeApiClient;

    public static RecipeRepository getInstance() {
        if(instance == null){
            instance = new RecipeRepository();
        }
        return instance;
    }

    private RecipeRepository(){
        mRecipeApiClient = RecipeApiClient.getInstance();
    }

    public LiveData<List<Recipe>> getRecipes(){
        return mRecipeApiClient.getRecipes();
    }

    public void searchRecipesApi(String query, int pageNumber) {
        if (pageNumber == 0) {
            pageNumber = 1;
        }
        mRecipeApiClient.searchRecipesApi(query, pageNumber);
    }

}
