package com.nikaas.app.ui.authority.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.nikaas.app.databinding.ItemSignalBadgeBinding

data class SignalItem(
    val title: String,
    val value: String,
    val iconResId: Int
)

class SignalsAdapter(
    private var signals: List<SignalItem> = emptyList()
) : RecyclerView.Adapter<SignalsAdapter.SignalViewHolder>() {

    fun updateData(newSignals: List<SignalItem>) {
        signals = newSignals
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SignalViewHolder {
        val binding = ItemSignalBadgeBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return SignalViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SignalViewHolder, position: Int) {
        holder.bind(signals[position])
    }

    override fun getItemCount(): Int = signals.size

    class SignalViewHolder(
        private val binding: ItemSignalBadgeBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        
        fun bind(item: SignalItem) {
            binding.txtSignalTitle.text = item.title
            binding.txtSignalValue.text = item.value
            binding.imgSignalIcon.setImageResource(item.iconResId)
        }
    }
}
