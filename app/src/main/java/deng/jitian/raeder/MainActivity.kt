package deng.jitian.raeder

import android.content.Intent
import android.support.design.widget.TabLayout
import android.support.v7.app.AppCompatActivity

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import deng.jitian.raeder.database.Feed
import deng.jitian.raeder.database.getFeedsDao
import deng.jitian.raeder.database.getSourceDao
import deng.jitian.raeder.feedlist.NewListFragment
import deng.jitian.raeder.feedlist.OldListFragment
import deng.jitian.raeder.feedlist.StarredListFragment
import deng.jitian.raeder.network.getFeeds
import io.reactivex.Flowable
import io.reactivex.Maybe
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private var mSectionsPagerAdapter: SectionsPagerAdapter? = null
    private var autoRefreshed = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setSupportActionBar(toolbar)
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = SectionsPagerAdapter(supportFragmentManager)

        // Set up the ViewPager with the sections adapter.
        container.adapter = mSectionsPagerAdapter

        container.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(tabs))
        tabs.addOnTabSelectedListener(TabLayout.ViewPagerOnTabSelectedListener(container))
    }

    override fun onResume() {
        super.onResume()
        // If haven't refreshed this time
        // try to refresh database
        if (!autoRefreshed) refresh()
    }

    private fun refresh() {
        // Update database then reload
        val dao = getSourceDao(this)
        if (dao == null) {
            Toast.makeText(this, "Load database failed!", Toast.LENGTH_SHORT).show()
            Log.e("Main", "getSourceDao return null!")
            return
        }
        dao.getAll()
                .subscribeOn(Schedulers.io())
                .map {
                    it.map {
                        val feeds = getFeeds(it.link)
                        val items = feeds!!.articles
                        for (item in items) {
                            val newFeed = Feed()
                            newFeed.description = item.description
                            newFeed.title = item.title
                            newFeed.pubDate = item.pubDate
                            newFeed.sourceName = it.name
                            newFeed.link = item.link
                            getFeedsDao(applicationContext)?.insertFeed(newFeed)
                        }
                    }
                }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    reload()
                    autoRefreshed = true;
                }
    }

    private fun reload() {
        container.adapter = mSectionsPagerAdapter
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean =
            when (item.itemId) {
                R.id.action_settings -> {
                    startActivity(Intent(this, SourceActivity::class.java))
                    true
                }
                R.id.action_refresh -> {
                    refresh();true
                }
                else -> super.onOptionsItemSelected(item)
            }


    /**
     * A [FragmentPagerAdapter] that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    inner class SectionsPagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {

        override fun getItem(position: Int): Fragment =
                when (position) {
                    0 -> NewListFragment()
                    1 -> OldListFragment()
                    2 -> StarredListFragment()
                    else -> throw IllegalArgumentException("Unknown position!")
                }

        override fun getCount(): Int {
            // Show 3 total pages.
            return 3
        }
    }
}
