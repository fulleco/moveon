package com.application.moveon.rest;


        import com.google.gson.Gson;
        import com.google.gson.GsonBuilder;

        import retrofit.RestAdapter;
        import retrofit.converter.GsonConverter;

/**
 * Created by Hugo on 02/12/2014.
 */
public class RestClient {
    private static final String BASE_URL = "http://martinezhugo.com/pfe";
    private MoveOnService apiService;

    public RestClient()
    {
        Gson gson = new GsonBuilder()
                .setDateFormat("yyyy'-'MM'-'dd'T'HH':'mm':'ss'.'SSS'Z'")
                .create();

        RestAdapter restAdapter = new RestAdapter.Builder()
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .setEndpoint(BASE_URL)
                .setConverter(new GsonConverter(gson))
                .build();

        apiService = restAdapter.create(MoveOnService.class);
    }

    public MoveOnService getApiService()
    {
        return apiService;
    }
}
