package com.robelseyoum3.foodrecipes.util;

import android.util.Log;

import androidx.annotation.MainThread;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.WorkerThread;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.Observer;

import com.robelseyoum3.foodrecipes.AppExecutors;
import com.robelseyoum3.foodrecipes.requests.response.ApiResponse;

/**
 * A generic class that can provide a resource backed by both the sqlite database and the network.
 * <p>
 * <p>
 * You can read more about it in the [Architecture
 * Guide](https://developer.android.com/arch).
 *
 * @param //<ResultType>
 * @param //<RequestType>
 * @param <CacheObject>   - Type  for the resource data (database cache)
 * @param <RequestObject> - Type for the API response (network request)
 *                        </RequestType></ResultType>
 *                        abstract class NetworkBoundResource<ResultType, RequestType>{
 */

public abstract class NetworkBoundResource<CacheObject, RequestObject> {
    private static final String TAG = "NetworkBoundResource";
    private AppExecutors appExecutors;
    private MediatorLiveData<Resource<CacheObject>> results = new MediatorLiveData<>();

    public NetworkBoundResource(AppExecutors appExecutors) {
        this.appExecutors = appExecutors;
        init();
    }

    private void init() {
        //update LiveData for loading status and set the data null bc nothing has retrieved yet
        //we are telling UI that something is happening and prepare for the next staff
        results.setValue((Resource<CacheObject>) Resource.loading(null));

        //observe LiveData source from local db
        final LiveData<CacheObject> dbSource = loadFromDb();

        results.addSource(dbSource, new Observer<CacheObject>() {
                    @Override
                    public void onChanged(CacheObject cacheObject) {
                        results.removeSource(dbSource);
                        //refresh the cache
                        if (shouldFetch(cacheObject)) {
                            //get data from the network or query the network
                            fetchFromNetwork(dbSource);
                        } else {
                            results.addSource(dbSource, new Observer<CacheObject>() {
                                @Override
                                public void onChanged(CacheObject cacheObject) {
                                    setValue(Resource.success(cacheObject));
                                }
                            });
                        }
                    }
                }
        );
    }

    /**
     * 1) observe local db
     * 2) if (condition) query the network
     * 3) stop observing the local db
     * 4) insert new data into local db
     * 5) begin observing local db again to see the refreshed data from network
     *
     * @param dbSource
     */
    private void fetchFromNetwork(final LiveData<CacheObject> dbSource) {
        Log.d(TAG, "fetchFromNetwork: called");

        //update liveData from loading status
        results.addSource(dbSource, new Observer<CacheObject>() {
            @Override
            public void onChanged(CacheObject cacheObject) {
                setValue(Resource.loading(cacheObject)); //view the cache and update the status
            }
        });

        final LiveData<ApiResponse<RequestObject>> apiResponse = createCall();

        results.addSource(apiResponse, new Observer<ApiResponse<RequestObject>>() {
            @Override
            public void onChanged(ApiResponse<RequestObject> requestObjectApiResponse) {
                results.removeSource(dbSource);
                results.removeSource(apiResponse);
                /**
                 * 3 cases:
                 * 1) ApiSuccessResponse
                 * 2) ApiErrorResponse
                 * 3) ApiEmptyResponse
                 */
                if (requestObjectApiResponse instanceof ApiResponse.ApiSuccessResponse) {
                    Log.d(TAG, "onChanged: ApiSuccessResponse.");
                    appExecutors.getDiskIO().execute(new Runnable() {
                        @Override
                        public void run() {
                            //save the response to the local db
                            saveCallResult((RequestObject) processResponse((ApiResponse.ApiSuccessResponse) requestObjectApiResponse));

                            appExecutors.mainThread().execute(new Runnable() {
                                @Override
                                public void run() {
                                    results.addSource(loadFromDb(), new Observer<CacheObject>() {
                                        @Override
                                        public void onChanged(CacheObject cacheObject) {
                                            setValue(Resource.success(cacheObject));
                                        }
                                    });
                                }
                            });
                        }
                    });
                } else if (requestObjectApiResponse instanceof ApiResponse.ApiEmptyResponse) {
                    Log.d(TAG, "onChanged: ApiEmptyResponse");
                    appExecutors.mainThread().execute(new Runnable() {
                        @Override
                        public void run() {
                            results.addSource(loadFromDb(), new Observer<CacheObject>() {
                                @Override
                                public void onChanged(CacheObject cacheObject) {
                                    setValue(Resource.success(cacheObject));//technically success but didn't return nothing so, view db cache
                                }
                            });
                        }
                    });
                } else if (requestObjectApiResponse instanceof ApiResponse.ApiErrorResponse) {
                    Log.d(TAG, "onChanged: ApiErrorResponse");
                    results.addSource(loadFromDb(), new Observer<CacheObject>() {
                        @Override
                        public void onChanged(CacheObject cacheObject) {
                            setValue(
                                    Resource.error(((ApiResponse.ApiErrorResponse) requestObjectApiResponse).getErrorMessage(), cacheObject)
                            );
                        }
                    });
                }
            }
        });
    }

    private CacheObject processResponse(ApiResponse.ApiSuccessResponse response) {
        return (CacheObject) response.getBody();
    }

    private void setValue(Resource<CacheObject> newValue) {
        if (results.getValue() != newValue) {
            results.setValue(newValue);
        }
    }

    //called to save the result of the API response into the database
    @WorkerThread
    protected abstract void saveCallResult(@NonNull RequestObject item);

    //Called with the data in the database to decide whether to fetch potentially updated data from the network
    //refresh data
    @MainThread
    protected abstract boolean shouldFetch(@Nullable CacheObject data);

    //Called to get the cached data from the database
    @Nullable
    @MainThread
    protected abstract LiveData<CacheObject> loadFromDb();

    //Called to create the API call
    @NonNull
    @MainThread
    protected abstract LiveData<ApiResponse<RequestObject>> createCall();

    //Returns a LiveData object that represent the resource that's implemented in the class
    public final LiveData<Resource<CacheObject>> getAsLiveData() {
        return results;
    }
}
