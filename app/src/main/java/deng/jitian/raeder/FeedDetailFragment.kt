package deng.jitian.raeder

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.trello.rxlifecycle2.components.support.RxFragment
import deng.jitian.raeder.database.Feed
import deng.jitian.raeder.database.updateFeed
import io.reactivex.Maybe
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_feed_detail.*
import kotlinx.android.synthetic.main.feed_detail.view.*

/**
 * A fragment representing a single Feed detail screen.
 * This fragment is either contained in a [FeedListActivity]
 * in two-pane mode (on tablets) or a [FeedDetailActivity]
 * on handsets.
 */
class FeedDetailFragment : RxFragment() {

    /**
     * The feed that is passed
     */
    private var mItem: Feed? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            if (it.containsKey(ARG_ITEM_ID)) {
                // Load the passed feed here
                mItem = it.getParcelable(ARG_ITEM_ID)
                // If fail, break out
                if (mItem == null) {
                    Log.e("FeedDetail", "get mItem failed!")
                    activity!!.onBackPressed()
                }
                activity?.detail_toolbar?.text = mItem!!.title
                // Set the feed as Read
                if (!mItem!!.read) {
                    mItem!!.read = true
                    Maybe.fromCallable { updateFeed(activity!!, mItem!!) }
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .compose(bindToLifecycle())
                            .subscribe {
                                Log.d("Feed", "Update success!")
                            }
                }
                // Open the original page when click title
                activity?.detail_toolbar?.setOnClickListener {
                    startActivity(
                            Intent(Intent.ACTION_VIEW)
                                    .apply { data = Uri.parse(mItem!!.link) })
                }
            }
        }

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.feed_detail, container, false)

        // Load feed description into Webview
        mItem?.let {
            Log.d("FeedDetail", "Loading url:" + it.link)
            rootView.feedContent.apply {
                settings.domStorageEnabled = true
                // Found this on the internet, prevents bad encoding
                loadData(beautify(it.description), "text/html; charset=UTF-8", null);
                // Disable scrolling/zooming
                settings.loadWithOverviewMode = true
                settings.useWideViewPort = true
            }
        }

        return rootView
    }


    companion object {
        /**
         * The fragment argument representing the item ID that this fragment
         * represents.
         */
        const val ARG_ITEM_ID = "item_id"

        fun beautify(content: String): String {
            return "<head> <style type=\"text/css\"> body{font-size: 30px; margin: 10%; } </style> </head> <body> $content </body>"
        }
    }
}
