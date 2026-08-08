package com.nikaas.app.ui.authority

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.*
import com.nikaas.app.R
import com.nikaas.app.databinding.FragmentIncidentDetailBinding

class IncidentDetailFragment : Fragment(), OnMapReadyCallback {

    private var _binding: FragmentIncidentDetailBinding? = null
    private val binding get() = _binding!!

    private var googleMap: GoogleMap? = null
    private var isBeforeState = true

    // G-10 Coordinates
    private val floodPoint = LatLng(33.6784, 72.9972)
    private val truckPoint = LatLng(33.6740, 72.9940)

    private val g10Boundary = listOf(
        LatLng(33.690, 72.985),
        LatLng(33.690, 73.010),
        LatLng(33.665, 73.010),
        LatLng(33.665, 72.985)
    )

    private val reroutePath = listOf(
        LatLng(33.670, 72.990),
        LatLng(33.670, 73.005),
        LatLng(33.685, 73.005),
        LatLng(33.685, 72.990)
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentIncidentDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize Map
        binding.mapView.onCreate(savedInstanceState)
        binding.mapView.getMapAsync(this)

        setupToggleListeners()
        setupBackNavigation()
        updateUiState()
    }

    override fun onMapReady(map: GoogleMap) {
        googleMap = map
        
        // Apply Dark Mode Map Styling if possible
        try {
            val success = map.setMapStyle(
                MapStyleOptions.loadRawResourceStyle(requireContext(), R.raw.map_dark_style)
            )
            if (!success) {
                // If custom resource not found, use standard dark
            }
        } catch (e: Exception) {
            // Fallback safely if resource loading fails
        }

        // Focus camera on Islamabad G-10 Underpass
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(floodPoint, 14.5f))
        drawMapOverlays()
    }

    private fun setupToggleListeners() {
        binding.btnToggleBefore.setOnClickListener {
            if (!isBeforeState) {
                isBeforeState = true
                binding.btnToggleBefore.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.primary))
                binding.btnToggleBefore.setTextColor(Color.WHITE)
                binding.btnToggleAfter.setBackgroundColor(Color.TRANSPARENT)
                binding.btnToggleAfter.setTextColor(ContextCompat.getColor(requireContext(), R.color.gray_text))
                updateUiState()
                drawMapOverlays()
            }
        }

        binding.btnToggleAfter.setOnClickListener {
            if (isBeforeState) {
                isBeforeState = false
                binding.btnToggleAfter.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.primary))
                binding.btnToggleAfter.setTextColor(Color.WHITE)
                binding.btnToggleBefore.setBackgroundColor(Color.TRANSPARENT)
                binding.btnToggleBefore.setTextColor(ContextCompat.getColor(requireContext(), R.color.gray_text))
                updateUiState()
                drawMapOverlays()
            }
        }
    }

    private fun setupBackNavigation() {
        binding.btnBack.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun updateUiState() {
        if (isBeforeState) {
            // Stats Row
            binding.txtStatCongestion.text = "HIGH"
            binding.txtStatCongestion.setTextColor(ContextCompat.getColor(requireContext(), R.color.severity_high))
            binding.txtStatSpeed.text = "8 km/h"
            binding.txtStatAlerts.text = "0"
            binding.txtStatTicket.text = "—"
            binding.txtStatTicket.setTextColor(ContextCompat.getColor(requireContext(), R.color.gray_text))

            // Ticket Timeline
            binding.lineLifecycle2.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.gray_border))
            binding.dotLifecycle3.setBackgroundResource(R.drawable.circle_indicator_empty)
            binding.txtTicketDetails.text = "Unit 3 · 3:01 PM · G-10 Drain Team (Idle)"
        } else {
            // Stats Row
            binding.txtStatCongestion.text = "MOD"
            binding.txtStatCongestion.setTextColor(ContextCompat.getColor(requireContext(), R.color.severity_medium))
            binding.txtStatSpeed.text = "35 km/h"
            binding.txtStatAlerts.text = "240"
            binding.txtStatTicket.text = "Open"
            binding.txtStatTicket.setTextColor(ContextCompat.getColor(requireContext(), R.color.primary))

            // Ticket Timeline
            binding.lineLifecycle2.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.primary))
            binding.dotLifecycle3.setBackgroundResource(R.drawable.circle_indicator_filled)
            binding.txtTicketDetails.text = "Unit 3 · 3:01 PM · G-10 Drain Team (Active)"
        }
    }

    private fun drawMapOverlays() {
        val map = googleMap ?: return
        map.clear()

        if (isBeforeState) {
            // Draw Flood Risk Polygon (Red, semi-transparent)
            map.addPolygon(
                PolygonOptions()
                    .addAll(g10Boundary)
                    .fillColor(Color.parseColor("#40E84040")) // 25% opacity red
                    .strokeColor(Color.parseColor("#E84040"))
                    .strokeWidth(2f)
            )

            // Pulsing Flood Center Marker
            map.addMarker(
                MarkerOptions()
                    .position(floodPoint)
                    .title("G-10 Underpass Flooded")
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
            )
        } else {
            // Draw Flood Risk Polygon (Lighter opacity)
            map.addPolygon(
                PolygonOptions()
                    .addAll(g10Boundary)
                    .fillColor(Color.parseColor("#15E84040")) // 8% opacity red
                    .strokeColor(Color.parseColor("#80E84040"))
                    .strokeWidth(1.5f)
            )

            // Alert Broadcast Radius (Circle, cyan)
            map.addCircle(
                CircleOptions()
                    .center(floodPoint)
                    .radius(600.0) // 600 meters
                    .fillColor(Color.parseColor("#2000C2B2")) // 12% opacity teal
                    .strokeColor(Color.parseColor("#00C2B2"))
                    .strokeWidth(2f)
            )

            // Reroute Path (Polyline, cyan/teal)
            map.addPolyline(
                PolylineOptions()
                    .addAll(reroutePath)
                    .color(Color.parseColor("#00C2B2"))
                    .width(6f)
            )

            // Dispatch WASA Truck Marker (Yellow/Orange)
            map.addMarker(
                MarkerOptions()
                    .position(truckPoint)
                    .title("WASA Drain Team Truck")
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE))
            )
        }
    }

    // Lifecycles for Google MapView
    override fun onResume() {
        super.onResume()
        binding.mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        binding.mapView.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (_binding != null) {
            binding.mapView.onDestroy()
        }
        _binding = null
    }

    override fun onLowMemory() {
        super.onLowMemory()
        binding.mapView.onLowMemory()
    }
}
