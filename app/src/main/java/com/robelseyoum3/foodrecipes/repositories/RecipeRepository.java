package com.robelseyoum3.foodrecipes.repositories;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;

import com.robelseyoum3.foodrecipes.AppExecutors;
import com.robelseyoum3.foodrecipes.models.Recipe;
import com.robelseyoum3.foodrecipes.persistence.RecipeDao;
import com.robelseyoum3.foodrecipes.persistence.RecipeDatabase;
import com.robelseyoum3.foodrecipes.requests.ServiceGenerator;
import com.robelseyoum3.foodrecipes.requests.response.ApiResponse;
import com.robelseyoum3.foodrecipes.requests.response.RecipeSearchResponse;
import com.robelseyoum3.foodrecipes.util.NetworkBoundResource;
import com.robelseyoum3.foodrecipes.util.Resource;

import java.util.List;

public class RecipeRepository {
    private static final String TAG = "RecipeRepository";
    private static RecipeRepository instance;
    private RecipeDao recipeDao;

    public static RecipeRepository getInstance(Context context) {
        if(instance == null){
            instance = new RecipeRepository(context);
        }
        return instance;
    }

    public RecipeRepository(Context context) {
        recipeDao = RecipeDatabase.getInstance(context).getRecipeDao();
    }

    public LiveData<Resource<List<Recipe>>> searchRecipesApi(final String query, final int pageNumber) {
        return new NetworkBoundResource<List<Recipe>, RecipeSearchResponse>(AppExecutors.getInstance()) {

            @Override
            protected void saveCallResult(@NonNull RecipeSearchResponse item) {
                if (item.getRecipes() != null) { //recipe list will be null if the api key is expired
                    Recipe[] recipes = new Recipe[item.getRecipes().size()];
                    int index = 0;
                    for (long rowId : recipeDao.insertRecipes((Recipe[]) item.getRecipes().toArray(recipes))) {
                        if (rowId == -1) {
                            Log.d(TAG, "saveCallResult: CONFLICT.. This recipe is already in the cache");
                            //if the recipe already exists.. I don't want to set the ingredients or timestamp b/c they will be erased
                            recipeDao.updateRecipe(
                                    recipes[index].getRecipe_id(),
                                    recipes[index].getTitle(),
                                    recipes[index].getPublisher(),
                                    recipes[index].getImage_url(),
                                    recipes[index].getSocial_rank()
                            );
                        }
                        index++;
                    }
                }
            }

            @Override
            protected boolean shouldFetch(@Nullable List<Recipe> data) {
                return true;
            }

            @Nullable
            @Override
            protected LiveData<List<Recipe>> loadFromDb() {
                return recipeDao.searchRecipes(query, pageNumber);
            }

            @NonNull
            @Override
            protected LiveData<ApiResponse<RecipeSearchResponse>> createCall() {
                return ServiceGenerator.getRecipeApi().searchRecipe(
                        query,
                        String.valueOf(pageNumber)
                );
            }
        }.getAsLiveData();
    }

}
