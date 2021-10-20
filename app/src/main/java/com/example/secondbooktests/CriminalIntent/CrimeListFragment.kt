package com.example.secondbooktests.CriminalIntent

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Button
import androidx.fragment.app.Fragment
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.secondbooktests.CriminalIntent.classes.Crime
import com.example.secondbooktests.CriminalIntent.viewmodel.CrimeListViewModel
import com.example.secondbooktests.R
import java.util.*


private const val TAG = "CrimeListFragment"
class CrimeListFragment : Fragment() {

    interface Callbacks{
        fun onCrimeSelected(crimeId: UUID)
    }
    private var button_visibility = true
    private lateinit var crimeRecyclerView: RecyclerView
    private var adapter:CrimeAdapter? = CrimeAdapter(emptyList())
    private var emptyAdapter:EmptyAdapter = EmptyAdapter()
    //private var adapter:NewCrimeAdapter? = NewCrimeAdapter(emptyList())
    private val crimeListViewModel:CrimeListViewModel by lazy {
        ViewModelProvider(this).get(CrimeListViewModel::class.java)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        callbacks = context as Callbacks?
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_crime_list, container, false)
        crimeRecyclerView = view.findViewById(R.id.crime_recycler_view)
        crimeRecyclerView.layoutManager = LinearLayoutManager(context)
        crimeRecyclerView.adapter = adapter
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        crimeListViewModel.crimeListLiveData.observe(viewLifecycleOwner,
            Observer { crimes ->
                crimes.let {
                    Log.d(TAG, "Got crimes ${crimes.size}")
                    updateUI(crimes)} }) //LiveData.observer(LifecycleOwner, Observer) используется для регистрации
    // страниццы наблюдателя за экземпляром LiveData и связи наблюдения с жизненным циклом другого компонента
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onDetach() {
        super.onDetach()
        callbacks = null
    }

    private fun updateUI(crimes: List<Crime>){
        emptyAdapter = EmptyAdapter()
        adapter = CrimeAdapter(crimes)
        if (crimes.isEmpty()) crimeRecyclerView.adapter = emptyAdapter
        else crimeRecyclerView.adapter = adapter
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.fragment_crime_list,menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId){
            R.id.new_crime -> {
                val crime = Crime()
                crimeListViewModel.addCrime(crime)
                callbacks?.onCrimeSelected(crime.id)
                true
            }

            else -> return super.onOptionsItemSelected(item)
        }

    }

    private inner class EmptyHolder(view: View):RecyclerView.ViewHolder(view){
        private val emptyListButton:Button = itemView.findViewById(R.id.emptyListButton)
        init {
            emptyListButton.setOnClickListener {
                val crime = Crime()
                crimeListViewModel.addCrime(crime)
                callbacks?.onCrimeSelected(crime.id)

            }
        }
    }
    private inner class EmptyAdapter :RecyclerView.Adapter<EmptyHolder>(){
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EmptyHolder {
            val view = layoutInflater.inflate(R.layout.empty_list_view,parent,false)
            return EmptyHolder(view)
        }

        override fun onBindViewHolder(holder: EmptyHolder, position: Int) {

        }

        override fun getItemCount(): Int {
            return 1
        }

    }

    private inner class CrimeHolder(view:View): RecyclerView.ViewHolder(view),View.OnClickListener {
        private lateinit var crime:Crime
        private val titleTextView: TextView = itemView.findViewById(R.id.crime_title)
        private val dateTextView: TextView = itemView.findViewById(R.id.crime_date)
        private val solvedImageView: ImageView = itemView.findViewById(R.id.is_solved)
        init {
            itemView.setOnClickListener(this)
        }
        fun bind(crime:Crime){
            this.crime = crime
            titleTextView.text = this.crime.title
            dateTextView.text = this.crime.date.toString()
            solvedImageView.visibility = if (crime.isSolved){
                View.VISIBLE
            } else View.GONE
        }

        override fun onClick(view: View?) {
            callbacks?.onCrimeSelected(crime.id)
        }
    }

    private inner class CrimeAdapter(var crimes:List<Crime>):RecyclerView.Adapter<CrimeHolder>(){
        private val TYPE_ITEM1 = 0
        private val TYPE_ITEM2 = 1


        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CrimeHolder {
            val view = when (viewType){
                TYPE_ITEM1 -> layoutInflater.inflate(R.layout.list_item_crime,parent,false)
                else -> layoutInflater.inflate(R.layout.list_item_crime_two,parent,false)
            }
            return CrimeHolder(view)
        }

        override fun getItemViewType(position: Int): Int {
            return TYPE_ITEM1
        }

        override fun onBindViewHolder(holder: CrimeHolder, position: Int) {
            val crime = crimes[position]

            val type = getItemViewType(position)
            when (type) {
                TYPE_ITEM1 -> holder.bind(crime)
                TYPE_ITEM2 -> holder.bind(crime)
            }
        }

        override fun getItemCount(): Int {
            return crimes.size
        }

    }

    companion object{
        fun newInstance():CrimeListFragment{
            return CrimeListFragment()
        }
        var callbacks:Callbacks? = null
    }

}