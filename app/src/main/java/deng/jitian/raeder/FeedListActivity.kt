package deng.jitian.raeder

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.os.Parcelable
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

import deng.jitian.raeder.database.Feed
import kotlinx.android.synthetic.main.activity_feed_list.*
import kotlinx.android.synthetic.main.feed_list_content.view.*

import kotlinx.android.synthetic.main.feed_list.*

/**
 * An activity representing a list of Pings. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a [FeedDetailActivity] representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
class FeedListActivity : AppCompatActivity() {

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private var mTwoPane: Boolean = false
    private lateinit var feeds: List<Feed>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_feed_list)
        feeds = intent.getParcelableArrayExtra("data").asList().map{ it as Feed }

        setSupportActionBar(toolbar)
        toolbar.title = title

        if (feed_detail_container != null) {
            mTwoPane = true
        }

        setupRecyclerView(feed_list)
    }

    private fun setupRecyclerView(recyclerView: RecyclerView) {
        recyclerView.adapter = SimpleItemRecyclerViewAdapter(this, feeds, mTwoPane)
    }

    class SimpleItemRecyclerViewAdapter(private val mParentActivity: FeedListActivity,
                                        private val mValues: List<Feed>,
                                        private val mTwoPane: Boolean) :
            RecyclerView.Adapter<SimpleItemRecyclerViewAdapter.ViewHolder>() {

        private val mOnClickListener: View.OnClickListener

        init {
            mOnClickListener = View.OnClickListener { v ->
                val item = v.tag as Feed
                v.id_text.setTextColor(Color.GRAY)
                v.content.setTextColor(Color.GRAY)
                if (mTwoPane) {
                    val fragment = FeedDetailFragment().apply {
                        arguments = Bundle().apply {
                            putParcelable(FeedDetailFragment.ARG_ITEM_ID, item)
                        }
                    }
                    mParentActivity.supportFragmentManager
                            .beginTransaction()
                            .replace(R.id.feed_detail_container, fragment)
                            .commit()
                } else {
                    val intent = Intent(v.context, FeedDetailActivity::class.java).apply {
                        putExtra(FeedDetailFragment.ARG_ITEM_ID, item as Parcelable)
                    }
                    v.context.startActivity(intent)
                }
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.feed_list_content, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val item = mValues[position]
            holder.mIdView.text = (position + 1).toString()
            holder.mContentView.text = item.title

            with(holder.itemView) {
                tag = item
                setOnClickListener(mOnClickListener)
            }
        }

        override fun getItemCount(): Int {
            return mValues.size
        }

        inner class ViewHolder(mView: View) : RecyclerView.ViewHolder(mView) {
            val mIdView: TextView = mView.id_text
            val mContentView: TextView = mView.content
        }
    }
}
