package deng.jitian.raeder

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import deng.jitian.raeder.database.Source
import deng.jitian.raeder.database.getSourceDao
import deng.jitian.raeder.sourcelist.SourceFragment
import io.reactivex.Maybe
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
            startActivity(Intent(this, AddSourceActivity::class.java))
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
