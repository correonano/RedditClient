package reddit.deviget.com.redditclient.api;

import reddit.deviget.com.redditclient.model.RedditResponse;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Observable;

/**
 * Created by Ignacio Saslavsky on 25/6/17.
 * correonano@gmail.com
 */

public class RedditApiClient {

    public static final String URI = "http://www.reddit.com/";

    private Api mApi;

    private static RedditApiClient instance;

    private RedditApiClient(){

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(URI)
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        mApi = retrofit.create(Api.class);

    }

    public static RedditApiClient getInstance() {
        if(instance == null) {
            instance = new RedditApiClient();
        }
        return instance;
    }

    public Observable<RedditResponse> top(String after, int limit) {
        return mApi.top(after, limit);
    }
}
