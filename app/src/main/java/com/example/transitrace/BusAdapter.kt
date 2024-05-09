package com.example.transitrace


import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot

class BusAdapter(private val busList: List<BusData>, private val onItemClick: (String) -> Unit) : RecyclerView.Adapter<BusAdapter.BusViewHolder>() {
    // Adapter implementation


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BusViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.card_bus_item, parent, false)
        return BusViewHolder(view)
    }

    override fun onBindViewHolder(holder: BusViewHolder, position: Int) {
        val bus = busList[position]
        holder.bind(bus)
        holder.cardviewbus.setOnClickListener {
            val busKey = busList[holder.adapterPosition].id // Use holder.adapterPosition to get the correct position
            onItemClick(busKey)
        }
    }


    fun onDataChange(dataSnapshot: DataSnapshot) {
        if (dataSnapshot.exists() && dataSnapshot.childrenCount > 0) {
            Log.d("Blankfragment", "DataSnapshot: $dataSnapshot")
        } else {
            Log.d("Blankfragment", "No data available")
        }

        // Your existing code...
    }

    override fun getItemCount(): Int {
        return busList.size
    }

    inner class BusViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val busNumberTextView: TextView = itemView.findViewById(R.id.busNumberTextView)
        private val startingPointTextView: TextView = itemView.findViewById(R.id.startingPointTextView)
        private val startingTimeTextView: TextView = itemView.findViewById(R.id.startingTimeTextView)
        private val endingPointTextView: TextView = itemView.findViewById(R.id.endingPointTextView)
        private val endingTimeTextView: TextView = itemView.findViewById(R.id.endingTimeTextView)
        private val routesTextView: TextView = itemView.findViewById(R.id.routesTextView)
        val cardviewbus: View = itemView.findViewById(R.id.cardviewbus)

        fun bind(bus: BusData) {
            busNumberTextView.text = "Bus Number: ${bus.busNumber}"
            startingPointTextView.text = "Starting Point: ${bus.from}"
            startingTimeTextView.text = "Starting Time: ${bus.startingTime}"
            endingPointTextView.text = "Ending Point: ${bus.to}"
            endingTimeTextView.text = "Ending Time: ${bus.endingTime}"
            routesTextView.text = "Routes: ${bus.busRoute}"

        }
    }
}