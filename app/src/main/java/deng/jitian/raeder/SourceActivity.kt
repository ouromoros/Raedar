package deng.jitian.raeder

import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import deng.jitian.raeder.backup.sourceToString
import deng.jitian.raeder.backup.stringToSource
import deng.jitian.raeder.database.Source
import deng.jitian.raeder.database.getSourceDao
import deng.jitian.raeder.network.getFeeds
import deng.jitian.raeder.sourcelist.SourceFragment
import io.reactivex.Maybe
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

import kotlinx.android.synthetic.main.activity_manage_source.*
import kotlinx.android.synthetic.main.content_manage_source.*

class SourceActivity : AppCompatActivity(), SourceFragment.OnListFragmentInteractionListener {

    public lateinit var sources: List<Source>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manage_source)
        setSupportActionBar(toolbar)
        fab.setOnClickListener{
            startActivity(Intent(this, AddSourceActivity::class.java))
        }
        val dao = getSourceDao(this)
        if (dao == null) {
            Log.e("Source", "getSourceDao returns null!")
            return
        }
        dao.getAll()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe{
                    sources = it
                    val f = SourceFragment.newInstance(1, sources)
                    supportFragmentManager.beginTransaction().apply {
                        replace(R.id.source_list, f)
                        commit()
                    }
                }
    }

    override fun onResume() {
        super.onResume()
        // Refresh Source List
        supportFragmentManager.beginTransaction().apply {
            detach(source_list)
            attach(source_list)
            commit()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_manage_source, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_import -> {
                try{
                    val c = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                    val s = c.text.toString()
                    val ss = stringToSource(s)
                    for(i in 0 until ss.size){
                        Maybe.fromCallable { getFeeds(ss[i].link) }
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe{
                                    getSourceDao(applicationContext)!!.insert(
                                            Source(it.name, it.link, ss[i].cat))
                                }
                    }
                }catch (e: Exception){
                    Toast.makeText(this, "Failed to import!", Toast.LENGTH_LONG).show()
                    Log.e("Source", e.toString())
                }
            }
            R.id.action_export -> {
                val c = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                val s = sourceToString(sources)
                c.text = s
            }
            else -> return super.onOptionsItemSelected(item)
        }
        return true;
    }

    override fun onListFragmentInteraction(item: Source) {
        val adBuilder = AlertDialog.Builder(this)
        val ad = adBuilder.setMessage("Are you sure you want to delete ${item.name}?")
                .setTitle("Delete")
                .setPositiveButton("Ok", {
                    _, _ ->
                    Maybe.fromCallable{getSourceDao(this)?.delete(item.name)}
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe{
                                Toast.makeText(this, "Delete successful!", Toast.LENGTH_SHORT).show()
                            }
                })
                .setNegativeButton("Cancel", {d,_-> d.cancel()})
                .create()
        ad.show()
    }

}
