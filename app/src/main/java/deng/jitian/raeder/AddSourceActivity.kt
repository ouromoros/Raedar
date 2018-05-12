package deng.jitian.raeder

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.ArrayAdapter
import android.widget.Toast
import com.trello.rxlifecycle2.components.support.RxAppCompatActivity
import deng.jitian.raeder.database.Source
import deng.jitian.raeder.database.getSourceDao
import deng.jitian.raeder.network.getFeeds
import io.reactivex.Maybe
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

import kotlinx.android.synthetic.main.activity_add_source.*
import kotlinx.android.synthetic.main.content_add_source.*

class AddSourceActivity : RxAppCompatActivity() {

    private lateinit var menu: Menu
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_source)
        setSupportActionBar(toolbar)
        // Allow auto complete
        getSourceDao(this)?.getTags()?.compose(bindToLifecycle())?.subscribe {
            category_text.setAdapter(ArrayAdapter<String>(
                    this, android.R.layout.simple_list_item_1, it))
        }
        // Add watcher to watch text change
        url_text.addTextChangedListener(CheckCompleteWatcher())
        category_text.addTextChangedListener(CheckCompleteWatcher())
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_add_source, menu)
        this.menu = menu
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.finish_add_source -> {
                val url = url_text.text.toString()
                val cat = category_text.text.toString()
                Maybe.fromCallable { getFeeds(url) }
                        .subscribeOn(Schedulers.io())
                        .map {
                            getSourceDao(applicationContext)!!.insert(
                                    Source(it.name, it.link, cat))
                        }
                        .observeOn(AndroidSchedulers.mainThread())
                        .compose(bindToLifecycle())
                        .subscribe({
                            Toast.makeText(this
                                    , "Add source succeed!"
                                    , Toast.LENGTH_SHORT)
                                    .show()
                            Log.d("Source", "Added success!")
                            onBackPressed()
                        }, {
                            // On error, display error message
                            Toast.makeText(this
                                    , "Failed to add source... Error message: $it"
                                    , Toast.LENGTH_SHORT)
                                    .show()
                            Log.e("Source", it.toString())
                            onBackPressed()
                        })
            }
            else -> super.onOptionsItemSelected(item)
        }
        return true
    }


    inner class CheckCompleteWatcher : TextWatcher {
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

        // Only checkable when both fields are not empty
        override fun afterTextChanged(s: Editable?) {
            menu.findItem(R.id.finish_add_source).isCheckable =
                    !(url_text.text.isEmpty() || category_text.text.isEmpty())
        }
    }

}
