package deng.jitian.raeder.sourcelist

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import deng.jitian.raeder.R
import deng.jitian.raeder.database.Source

import deng.jitian.raeder.sourcelist.SourceFragment.OnListFragmentInteractionListener
import kotlinx.android.synthetic.main.fragment_source.view.*

class MySourceRecyclerViewAdapter(private val mValues: List<Source>, private val mListener: OnListFragmentInteractionListener?) : RecyclerView.Adapter<MySourceRecyclerViewAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.fragment_source, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.mItem = mValues[position]
        holder.mIdView.text = mValues[position].tag
        holder.mContentView.text = mValues[position].name

        holder.mDeleteButton.setOnClickListener {
            mListener?.onListFragmentInteraction(holder.mItem!!)
        }
    }

    override fun getItemCount(): Int {
        return mValues.size
    }

    inner class ViewHolder(mView: View) : RecyclerView.ViewHolder(mView) {
        val mIdView: TextView = mView.idView
        val mContentView: TextView = mView.content
        val mDeleteButton: Button = mView.deleteButton
        var mItem: Source? = null

        override fun toString(): String {
            return super.toString() + " '" + mContentView.text + "'"
        }
    }
}
