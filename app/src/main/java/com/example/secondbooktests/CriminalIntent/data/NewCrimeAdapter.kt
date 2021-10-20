package com.example.secondbooktests.CriminalIntent.data

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.secondbooktests.CriminalIntent.CrimeListFragment
import com.example.secondbooktests.CriminalIntent.classes.Crime
import com.example.secondbooktests.R

class NewCrimeAdapter(var crimes: List<Crime>) :
    ListAdapter<Crime, NewCrimeAdapter.NewCrimeHolder>(NewFeedDiffCallback()) {
    class NewCrimeHolder(view: View) : RecyclerView.ViewHolder(view), View.OnClickListener {
        private lateinit var crime: Crime
        private val titleTextView: TextView = itemView.findViewById(R.id.crime_title)
        private val dateTextView: TextView = itemView.findViewById(R.id.crime_date)
        private val solvedImageView: ImageView = itemView.findViewById(R.id.is_solved)

        init {
            itemView.setOnClickListener(this)
        }

        fun bind(crime: Crime) {
            this.crime = crime
            titleTextView.text = this.crime.title
            dateTextView.text = this.crime.date.toString()
            solvedImageView.visibility = if (crime.isSolved) {
                View.VISIBLE
            } else View.GONE
        }

        override fun onClick(v: View?) {
            CrimeListFragment.callbacks?.onCrimeSelected(crime.id)

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewCrimeHolder {
        val inflater = LayoutInflater.from(parent.context)
        return NewCrimeHolder(inflater.inflate(R.layout.list_item_crime, parent, false))
    }

    override fun onBindViewHolder(holder: NewCrimeHolder, position: Int) {
        val crime = crimes[position]
        holder.bind(crime)
    }
}

private class NewFeedDiffCallback : DiffUtil.ItemCallback<Crime>() {
    override fun areItemsTheSame(oldItem: Crime, newItem: Crime): Boolean {
        return oldItem.title == newItem.title

    }

    override fun areContentsTheSame(oldItem: Crime, newItem: Crime): Boolean {
        return areItemsTheSame(oldItem, newItem)
    }

}