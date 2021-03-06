package com.robelseyoum3.foodrecipes.repositories;

import androidx.lifecycle.LiveData;

import com.robelseyoum3.foodrecipes.models.Recipe;
import com.robelseyoum3.foodrecipes.requests.RecipeApiClient;

import java.util.List;

public class RecipeRepository {

    private static RecipeRepository instance;
    private RecipeApiClient mRecipeApiClient;
    private String mQuery;
    private int mPageNumber;

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

    public LiveData<Boolean> isRecipeRequestTimeOut() {
        return mRecipeApiClient.isRecipeRequestTimeOut();
    }

    public LiveData<Recipe> getRecipe() {
        return mRecipeApiClient.getRecipe();
    }

    public void searchRecipesApi(String query, int pageNumber) {
        if (pageNumber == 0) {
            pageNumber = 1;
        }
        mQuery = query;
        mPageNumber = pageNumber;
        mRecipeApiClient.searchRecipesApi(query, pageNumber);
    }

    public void searchRecipeApiById(String recipeID) {
        mRecipeApiClient.searchRecipeApiById(recipeID);
    }

    public void searchNextPage() {
        searchRecipesApi(mQuery, mPageNumber + 1);
    }

    public void cancelRequest() {
        mRecipeApiClient.cancelRequest();
    }

}
