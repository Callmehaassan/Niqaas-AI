package com.nikaas.app.ui.authority

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.nikaas.app.R
import com.nikaas.app.databinding.FragmentDashboardBinding
import com.nikaas.app.domain.model.FusedIncident
import com.nikaas.app.ui.authority.adapters.ActionListAdapter
import com.nikaas.app.ui.authority.adapters.SignalItem
import com.nikaas.app.ui.authority.adapters.SignalsAdapter
import com.nikaas.app.ui.common.UIState
import com.nikaas.app.ui.common.hide
import com.nikaas.app.ui.common.show

class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!

    private val viewModel: DashboardViewModel by viewModels()
    private lateinit var signalsAdapter: SignalsAdapter
    private lateinit var actionListAdapter: ActionListAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupSectorSpinner()
        setupRecyclerViews()
        setupListeners()

        // Observe incident state transitions
        viewModel.incidentState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is UIState.Loading -> {
                    binding.progressLoader.show()
                    binding.txtEmptyState.hide()
                    binding.layoutIncidentDetails.hide()
                }
                is UIState.Error -> {
                    binding.progressLoader.hide()
                    binding.txtEmptyState.show()
                    binding.txtEmptyState.text = state.message
                    binding.layoutIncidentDetails.hide()
                }
                is UIState.Success -> {
                    binding.progressLoader.hide()
                    val incident = state.data
                    if (incident == null) {
                        binding.txtEmptyState.show()
                        binding.txtEmptyState.text = "No active incident analysis. Select an area above and click 'Fuse Signals & Analyze' to trigger AI evaluation."
                        binding.layoutIncidentDetails.hide()
                    } else {
                        binding.txtEmptyState.hide()
                        binding.layoutIncidentDetails.show()
                        bindIncidentDetails(incident)
                    }
                }
            }
        }
    }

    private fun setupSectorSpinner() {
        val sectors = viewModel.getSectors()
        val spinnerAdapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            sectors
        )
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerSectors.adapter = spinnerAdapter
    }

    private fun setupRecyclerViews() {
        signalsAdapter = SignalsAdapter()
        binding.rvSignals.layoutManager = LinearLayoutManager(requireContext())
        binding.rvSignals.adapter = signalsAdapter

        actionListAdapter = ActionListAdapter()
        binding.rvActions.layoutManager = LinearLayoutManager(requireContext())
        binding.rvActions.adapter = actionListAdapter
    }

    private fun setupListeners() {
        binding.btnFuse.setOnClickListener {
            val area = binding.spinnerSectors.selectedItem.toString()
            viewModel.fuseAndAnalyze(area)
        }
    }

    private fun bindIncidentDetails(incident: FusedIncident) {
        // 1. Severity level badge
        binding.txtSeverityBadge.text = "${incident.severity.uppercase()} SEVERITY"
        val severityColor = when (incident.severity.lowercase()) {
            "high" -> ContextCompat.getColor(requireContext(), R.color.severity_high)
            "medium" -> ContextCompat.getColor(requireContext(), R.color.severity_medium)
            else -> ContextCompat.getColor(requireContext(), R.color.severity_low)
        }
        binding.txtSeverityBadge.setBackgroundColor(severityColor)

        // 2. Confidence rating
        binding.txtConfidenceScore.text = "Confidence: ${incident.confidenceScore}%"

        // 3. Reasoning explanation
        binding.txtReasoning.text = incident.confidenceReasoning

        // 4. Populate Live Input Signals
        val reportsCount = incident.citizenReports.size
        val signalsList = listOf(
            SignalItem(
                title = "Citizen Reports",
                value = "$reportsCount active reports submitted for this area",
                iconResId = android.R.drawable.ic_menu_myplaces
            ),
            SignalItem(
                title = "Weather Feed",
                value = "Rain Alert: ${if (incident.weatherSignal.hasRainfallAlert) "Active" else "None"} | Intensity: ${incident.weatherSignal.intensity}",
                iconResId = android.R.drawable.ic_dialog_alert
            ),
            SignalItem(
                title = "Traffic Congestion",
                value = "Level: ${incident.trafficSignal.congestionLevel} | Avg Speed: ${incident.trafficSignal.averageSpeedKmph} km/h",
                iconResId = android.R.drawable.ic_menu_recent_history
            )
        )
        signalsAdapter.updateData(signalsList)

        // 5. Populate Actions List
        actionListAdapter.updateData(incident.actions)

        // 6. Before vs After Outcomes
        binding.txtBeforeTraffic.text = incident.beforeOutcome.trafficStatus
        binding.txtBeforeDispatch.text = incident.beforeOutcome.dispatchStatus
        binding.txtBeforeAlerts.text = incident.beforeOutcome.alertsStatus

        binding.txtAfterTraffic.text = incident.afterOutcome.trafficStatus
        binding.txtAfterDispatch.text = incident.afterOutcome.dispatchStatus
        binding.txtAfterAlerts.text = incident.afterOutcome.alertsStatus

        binding.btnViewMap.setOnClickListener {
            val bundle = Bundle().apply {
                putString("incidentId", incident.id)
            }
            findNavController().navigate(R.id.action_dashboard_to_detail, bundle)
        }

        // 7. Simulation Status and Buttons
        when {
            incident.isResolved -> {
                binding.txtSimStatus.text = "Status: Incident Resolved / All Clear"
                binding.txtSimStatus.setTextColor(ContextCompat.getColor(requireContext(), R.color.severity_low))
                binding.btnApprove.hide()
                binding.btnOverride.hide()
                binding.btnResolve.hide()
            }
            incident.isApproved -> {
                binding.txtSimStatus.text = "Status: Coordinated Response Executing..."
                binding.txtSimStatus.setTextColor(Color.parseColor("#0097A7")) // Cyan
                binding.btnApprove.hide()
                binding.btnOverride.hide()
                binding.btnResolve.show()
                binding.btnResolve.setOnClickListener {
                    viewModel.resolveIncident(incident.id)
                    Toast.makeText(requireContext(), "Incident marked resolved & cleared!", Toast.LENGTH_SHORT).show()
                }
            }
            else -> {
                binding.txtSimStatus.text = "Status: Pending Coordinator Approval"
                binding.txtSimStatus.setTextColor(ContextCompat.getColor(requireContext(), R.color.error))
                binding.btnApprove.show()
                binding.btnOverride.show()
                binding.btnResolve.hide()
                binding.btnApprove.setOnClickListener {
                    viewModel.approveResponse(incident.id)
                    Toast.makeText(requireContext(), "Response approved! Simulation executing...", Toast.LENGTH_SHORT).show()
                }
                binding.btnOverride.setOnClickListener {
                    androidx.appcompat.app.AlertDialog.Builder(requireContext())
                        .setTitle("Manual Override")
                        .setMessage("Do you want to override the AI recommendation and execute the emergency response actions manually?")
                        .setPositiveButton("Override & Execute") { _, _ ->
                            viewModel.approveResponse(incident.id)
                            Toast.makeText(requireContext(), "Administrative Override Triggered: Actions Executing!", Toast.LENGTH_LONG).show()
                        }
                        .setNegativeButton("Cancel", null)
                        .show()
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
