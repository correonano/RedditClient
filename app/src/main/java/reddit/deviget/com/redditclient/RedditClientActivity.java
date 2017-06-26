package reddit.deviget.com.redditclient;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import reddit.deviget.com.redditclient.api.RedditApiClient;
import reddit.deviget.com.redditclient.model.RedditPost;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class RedditClientActivity extends AppCompatActivity {

    public static final String TAG = "RedditClientActivity";
    public static final String SAVED = "SAVED";
    public static final int MAX_POSTS = 50;

    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;

    private ArrayList<RedditPost> mRedditPosts;
    private RedditAdapter mAdapter;
    private Subscription mCurrentRequest;
    private ProgressDialog mProgressDialog;
    private Boolean canShow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reddit_client);
        ButterKnife.bind(this);

        canShow = true;
        mRedditPosts = new ArrayList<>();
        mAdapter = new RedditAdapter();
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(mAdapter);

        if (savedInstanceState != null) {
            restorePosts(savedInstanceState);
        } else {
            fetch();
        }

    }

    @Override
    protected void onStop() {
        super.onStop();
        canShow = false;
    }

    private void fetch() {
        if (mRedditPosts.size() < MAX_POSTS && mCurrentRequest == null) {

            String after = null;
            if (!mRedditPosts.isEmpty()) {
                RedditPost lastPost = mRedditPosts.get(mRedditPosts.size() - 1);
                after = lastPost.name;
            }
            showLoading();
            mCurrentRequest = RedditApiClient.getInstance().top(after, 10)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(response -> {
                        hideLoading();
                        mCurrentRequest = null;
                        mRedditPosts.addAll(Stream.of(response.data.children).map(d -> {return  d.data;}).collect(Collectors.toList()));
                        mAdapter.notifyDataSetChanged();
                    }, error -> {
                        hideLoading();
                        mCurrentRequest = null;
                        Log.e(TAG, "Error loading posts", error);
                        Toast.makeText(this, R.string.error, Toast.LENGTH_SHORT).show();
                    });
        }
    }

    private void showLoading() {
        if (mProgressDialog == null && canShow) {
            runOnUiThread(() -> {
                mProgressDialog = ProgressDialog.show(this, null, getString(R.string.loading), true, false);
                mProgressDialog.setOnCancelListener(dialog -> mProgressDialog = null);
                mProgressDialog.setOnDismissListener(dialog -> mProgressDialog = null);
            });
        }
    }

    private void hideLoading() {
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
        }
    }


    public class RedditAdapter extends RecyclerView.Adapter<RedditAdapter.ViewHolder> {

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(RedditClientActivity.this).inflate(R.layout.reddit_line, parent, false);
            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            RedditPost redditPost = mRedditPosts.get(position);
            holder.title.setText(redditPost.title);
            holder.comments.setText(String.format(getResources().getString(R.string.comments), redditPost.getNumComments()));
            holder.info.setText(String.format(getResources().getString(R.string.submitted), redditPost.getTime(), redditPost.author));

            if (!redditPost.thumbnail.isEmpty()) {
                if (!redditPost.url.isEmpty()) {
                    holder.thumb.setOnClickListener(v -> {
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(redditPost.url));
                        startActivity(browserIntent);
                    });
                } else {
                    holder.thumb.setOnClickListener(null);
                }
                holder.thumb.setVisibility(View.VISIBLE);
                Picasso.with(RedditClientActivity.this)
                        .load(redditPost.thumbnail)
                        .placeholder(R.mipmap.ic_launcher_round)
                        .fit().centerCrop().into(holder.thumb);
            } else {
                holder.thumb.setVisibility(View.GONE);
            }

            if (position >= mRedditPosts.size() - 1) {
                fetch();
            }
        }

        @Override
        public int getItemCount() {
            return mRedditPosts.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            @BindView(R.id.title)
            public TextView title;
            @BindView(R.id.comments)
            public TextView comments;
            @BindView(R.id.post_info)
            public TextView info;
            @BindView(R.id.thumb)
            public ImageView thumb;

            public ViewHolder(View itemView) {
                super(itemView);
                ButterKnife.bind(this, itemView);
            }
        }
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(SAVED, mRedditPosts);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        restorePosts(savedInstanceState);
    }

    private void restorePosts(Bundle savedInstanceState) {
        mRedditPosts = (ArrayList<RedditPost>) savedInstanceState.getSerializable(SAVED);
    }
}
