package com.nikaas.app.ui.citizen.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.nikaas.app.data.model.CitizenReport
import com.nikaas.app.databinding.ItemCitizenReportBinding
import com.nikaas.app.ui.common.formatTime

import coil.load

class CitizenReportsAdapter(
    private var reports: List<CitizenReport> = emptyList()
) : RecyclerView.Adapter<CitizenReportsAdapter.ReportViewHolder>() {

    fun updateData(newReports: List<CitizenReport>) {
        reports = newReports
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReportViewHolder {
        val binding = ItemCitizenReportBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ReportViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ReportViewHolder, position: Int) {
        holder.bind(reports[position])
    }

    override fun getItemCount(): Int = reports.size

    class ReportViewHolder(
        private val binding: ItemCitizenReportBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        
        fun bind(report: CitizenReport) {
            binding.txtLocation.text = report.location
            binding.txtDescription.text = report.description
            binding.txtTime.formatTime(report.timestamp)

            if (!report.photoUrl.isNullOrEmpty()) {
                binding.imgPhotoIndicator.visibility = android.view.View.VISIBLE
                binding.cardItemThumbnail.visibility = android.view.View.VISIBLE
                binding.imgItemPhoto.load(report.photoUrl) {
                    crossfade(true)
                    placeholder(android.R.drawable.ic_menu_gallery)
                    error(android.R.drawable.ic_menu_report_image)
                }
            } else {
                binding.imgPhotoIndicator.visibility = android.view.View.GONE
                binding.cardItemThumbnail.visibility = android.view.View.GONE
                binding.imgItemPhoto.setImageDrawable(null)
            }
        }
    }
}
