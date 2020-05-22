package com.robelseyoum3.foodrecipes.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.robelseyoum3.foodrecipes.models.Recipe;
import com.robelseyoum3.foodrecipes.repositories.RecipeRepository;

public class RecipeViewModel extends ViewModel {

    private RecipeRepository mRecipeRepository;
    private String mRecipeId;
    private boolean mDidRetrieved;

    public RecipeViewModel() {
        this.mRecipeRepository = RecipeRepository.getInstance();
        mDidRetrieved = false;
    }

    public LiveData<Boolean> isRecipeRequestTimeOut() {
        return mRecipeRepository.isRecipeRequestTimeOut();
    }

    public LiveData<Recipe> getRecipe() {
        return mRecipeRepository.getRecipe();
    }

    public void searchRecipeById(String recipeID) {
        mRecipeId = recipeID;
        mRecipeRepository.searchRecipeApiById(recipeID);
    }

    public String getRecipeId() {
        return mRecipeId;
    }

    public boolean isDidRetrieved() {
        return mDidRetrieved;
    }

    public void setDidRetrieved(boolean mDidRetrieved) {
        this.mDidRetrieved = mDidRetrieved;
    }
}
