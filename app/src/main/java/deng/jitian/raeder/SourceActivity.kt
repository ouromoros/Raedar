package deng.jitian.raeder

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.FragmentManager
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.text.InputType
import android.util.Log
import android.widget.EditText
import android.widget.Toast
import deng.jitian.raeder.database.Source
import deng.jitian.raeder.database.getSourceDao
import deng.jitian.raeder.network.getFeeds
import deng.jitian.raeder.sourcelist.SourceFragment
import io.reactivex.Maybe
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

import kotlinx.android.synthetic.main.activity_manage_source.*
import kotlinx.android.synthetic.main.content_manage_source.*

class SourceActivity : AppCompatActivity(), SourceFragment.OnListFragmentInteractionListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manage_source)
        setSupportActionBar(toolbar)
        fab.setOnClickListener{
            startActivity(Intent(this, AddSource::class.java))
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

    override fun onListFragmentInteraction(item: Source) {
        Maybe.fromCallable{getSourceDao(this)?.delete(item.name)}
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe{
                    Toast.makeText(this, "Delete successful!", Toast.LENGTH_SHORT).show()
                }
    }

}
