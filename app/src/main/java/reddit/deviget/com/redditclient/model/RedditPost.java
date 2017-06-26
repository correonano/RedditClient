package reddit.deviget.com.redditclient.model;

import java.io.Serializable;
import java.util.concurrent.TimeUnit;

/**
 * Created by Ignacio Saslavsky on 25/6/17.
 * correonano@gmail.com
 */

public class RedditPost implements Serializable {

    public String title;
    public String author;
    public int num_comments;
    public String name;
    public long created_utc;
    public String thumbnail;
    public String url;


    public int getNumComments() {
        return num_comments;
    }

    public Long getTime() {
        return TimeUnit.SECONDS.toHours(TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis()) - created_utc);
    }
}
