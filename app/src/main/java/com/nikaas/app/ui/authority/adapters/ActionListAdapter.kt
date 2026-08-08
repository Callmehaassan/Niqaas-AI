package com.nikaas.app.ui.authority.adapters

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.nikaas.app.data.model.AiAction
import com.nikaas.app.databinding.ItemAiActionBinding

class ActionListAdapter(
    private var actions: List<AiAction> = emptyList()
) : RecyclerView.Adapter<ActionListAdapter.ActionViewHolder>() {

    fun updateData(newActions: List<AiAction>) {
        actions = newActions
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ActionViewHolder {
        val binding = ItemAiActionBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ActionViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ActionViewHolder, position: Int) {
        holder.bind(actions[position])
    }

    override fun getItemCount(): Int = actions.size

    class ActionViewHolder(
        private val binding: ItemAiActionBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(action: AiAction) {
            val title = when (action.type) {
                "REROUTE" -> "Reroute Traffic Flow"
                "DISPATCH" -> "Dispatch WASA Team"
                "ALERT" -> "Broadcast Resident Alert"
                else -> action.type
            }

            val iconResId = when (action.type) {
                "REROUTE" -> android.R.drawable.ic_menu_directions
                "DISPATCH" -> android.R.drawable.ic_menu_call
                "ALERT" -> android.R.drawable.ic_dialog_alert
                else -> android.R.drawable.ic_dialog_info
            }

            binding.txtActionTitle.text = title
            binding.txtActionDesc.text = action.description
            binding.imgActionIcon.setImageResource(iconResId)

            binding.txtActionStatus.text = action.status
            when (action.status) {
                "Pending Approval" -> {
                    binding.txtActionStatus.setBackgroundColor(Color.parseColor("#78909C")) // Blue Gray
                }
                "Executing" -> {
                    binding.txtActionStatus.setBackgroundColor(Color.parseColor("#F57C00")) // Orange
                }
                "Completed" -> {
                    binding.txtActionStatus.setBackgroundColor(Color.parseColor("#388E3C")) // Green
                }
                else -> {
                    binding.txtActionStatus.setBackgroundColor(Color.GRAY)
                }
            }
        }
    }
}
