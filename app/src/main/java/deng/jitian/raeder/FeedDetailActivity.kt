package deng.jitian.raeder

import android.os.Bundle
import android.util.Log
import com.trello.rxlifecycle2.components.support.RxAppCompatActivity
import deng.jitian.backend.database.Feed
import deng.jitian.backend.database.updateFeed
import io.reactivex.Maybe
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_feed_detail.*

/**
 * An activity representing a single Feed detail screen. This
 * activity is only used on narrow width devices. On tablet-size devices,
 * item details are presented side-by-side with a list of items
 * in a [FeedListActivity].
 */
class FeedDetailActivity : RxAppCompatActivity() {

    lateinit var feed: Feed

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_feed_detail)

        // savedInstanceState is non-null when there is fragment state
        // saved from previous configurations of this activity
        // (e.g. when rotating the screen from portrait to landscape).
        // In this case, the fragment will automatically be re-added
        // to its container so we don't need to manually add it.
        // For more information, see the Fragments API guide at:
        //
        // http://developer.android.com/guide/components/fragments.html
        //
        if (savedInstanceState == null) {
            // Create the detail fragment and add it to the activity
            // using a fragment transaction.
            val fragment = FeedDetailFragment().apply {
                arguments = Bundle().apply {
                    feed = intent.getParcelableExtra<Feed>(FeedDetailFragment.ARG_ITEM_ID)
                    putParcelable(FeedDetailFragment.ARG_ITEM_ID, feed)
                }
            }

            // Set button as Starred
            if (feed.starred) {
                star_button.setImageResource(R.drawable.ic_star_black_24dp)
            }

            if (!feed.read) {
                feed.read = true
                Maybe.fromCallable { updateFeed(this, feed) }
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .compose(bindToLifecycle())
                        .subscribe {
                            Log.d("FeedDetail", "Marked ${feed.link} as read")
                        }
            }

            star_button.setOnClickListener {
                feed.starred = !feed.starred
                Maybe.fromCallable { updateFeed(this, feed) }
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .compose(bindToLifecycle())
                        .subscribe {
                            Log.d("FeedDetail", "(Un)starred ${feed.link} success")
                            if (feed.starred) {
                                star_button.setImageResource(R.drawable.ic_star_black_24dp)
                            } else {
                                star_button.setImageResource(R.drawable.ic_star_border_black_24dp)
                            }
                        }
            }

            supportFragmentManager.beginTransaction()
                    .add(R.id.feed_detail_container, fragment )
                    .commit()
        }
    }

}
