package reddit.deviget.com.redditclient.api;

import reddit.deviget.com.redditclient.model.RedditResponse;
import retrofit2.http.GET;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by Ignacio Saslavsky on 25/6/17.
 * correonano@gmail.com
 */

public interface Api {

    @GET("top.json")
    Observable<RedditResponse> top(@Query("after") String after, @Query("limit") int limit);

}
