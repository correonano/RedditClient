package reddit.deviget.com.redditclient.model;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Ignacio Saslavsky on 25/6/17.
 * correonano@gmail.com
 */

public class RedditResponse implements Serializable {

    public RedditData data;

    public class RedditData implements Serializable {
        public List<RedditChildren> children;
    }

    public class RedditChildren implements Serializable {
        public RedditPost data;
    }
}
