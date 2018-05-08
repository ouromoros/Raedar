package deng.jitian.raeder.feedlist

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import deng.jitian.raeder.FeedListActivity
import deng.jitian.raeder.R
import deng.jitian.raeder.database.Feed
import deng.jitian.raeder.database.FeedCount
import deng.jitian.raeder.database.RSSDatabase
import deng.jitian.raeder.database.getFeedsDao
import io.reactivex.Flowable
import io.reactivex.Maybe
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.list_item.view.*
import java.util.*
import kotlin.collections.ArrayList
import kotlin.concurrent.thread

abstract class ListFragment : Fragment() {
    private lateinit var feeds: List<List<Pair<String, Int>>>
    private lateinit var tags: List<String>
    private lateinit var rootView: View
    val mdb: RSSDatabase? by lazy {
        RSSDatabase.getInstance(context!!)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        rootView = inflater.inflate(R.layout.fragment_list, null)
        getList().map{extractList(it)}
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    feeds = it.first
                    tags = it.second
                    val view = rootView.findViewById<ExpandableListView>(R.id.rootList)
                    view.setAdapter(MyAdapter())
                    view.setOnChildClickListener(MyChildClickListener())
                }
        return rootView
    }

    inner class MyChildClickListener : ExpandableListView.OnChildClickListener {
        override fun onChildClick(parent: ExpandableListView?, v: View?, groupPosition: Int, childPosition: Int, id: Long): Boolean {
            getFeedIn(feeds[groupPosition][childPosition].first)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe {
                        val intent = Intent(context, FeedListActivity::class.java)
                        intent.putExtra("data", it.toTypedArray())
                        startActivity(intent)
                    }
            return true
        }
    }

    inner class MyAdapter : BaseExpandableListAdapter() {
        override fun getChild(groupPosition: Int, childPosition: Int): Any {
            return feeds[groupPosition][childPosition]
        }

        override fun getGroup(groupPosition: Int): Any {
            return tags[groupPosition]
        }

        override fun getGroupCount(): Int {
            return tags.size
        }

        override fun getChildId(groupPosition: Int, childPosition: Int): Long {
            return childPosition.toLong()
        }

        override fun getChildView(groupPosition: Int, childPosition: Int, isLastChild: Boolean, convertView: View?, parent: ViewGroup?): View {
            val cname = feeds[groupPosition][childPosition].first
            val count = feeds[groupPosition][childPosition].second
            var cview = convertView
            if (cview == null) {
                cview = activity!!.layoutInflater.inflate(R.layout.list_item, null)
            }
            cview!!.name.text = cname
            cview.count.text = count.toString()
            return cview
        }

        override fun getGroupId(groupPosition: Int): Long {
            return groupPosition.toLong()
        }

        override fun getChildrenCount(groupPosition: Int): Int {
            return feeds[groupPosition].size
        }

        override fun getGroupView(groupPosition: Int, isExpanded: Boolean, convertView: View?, parent: ViewGroup?): View {
            val tag = tags[groupPosition]
            var cview = convertView
            if (cview == null) {
                cview = activity!!.layoutInflater.inflate(R.layout.list_tag, null)
            }
            cview!!.findViewById<TextView>(R.id.tagName).text = tag
            val tb = cview.findViewById<ImageButton>(R.id.tagButton)
            tb.isFocusable = false
            tb.setOnClickListener {
                // Use the abstract function here
                getFeedInTag(tag)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe {
                            val intent = Intent(context, FeedListActivity::class.java)
                            intent.putExtra("data", it.toTypedArray())
                            startActivity(intent)
                        }
            }
            return cview
        }

        override fun hasStableIds(): Boolean {
            return false
        }

        override fun isChildSelectable(groupPosition: Int, childPosition: Int): Boolean {
            return true
        }
    }

    override fun onPause() {
        super.onPause()
    }

    abstract fun getList(): Flowable<List<FeedCount>>

    abstract fun getFeedInTag(tag: String): Maybe<List<Feed>>

    abstract fun getFeedIn(s: String): Maybe<List<Feed>>

    private fun extractList(data: List<FeedCount>):
            Pair<List<List<Pair<String, Int>>>, List<String>> {
        val tags = HashMap<String, MutableList<Pair<String, Int>>>()
        val tagCount = HashMap<String, Int>()
        for (feedCount in data) {
            val tag = feedCount.tag
            if (tagCount[tag] == null) tagCount[tag] = 0
            tagCount[tag] = feedCount.count + tagCount[tag]!!
            if (tags[tag] == null) tags[tag] = ArrayList()
            tags[tag]!!.add(Pair(feedCount.source, feedCount.count))
        }
        return Pair(tags.values.toList(), tags.keys.toList())
    }
}

