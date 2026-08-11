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
    private var useSimulatedMap = false

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

        // Check API Key to determine if we should fallback to offline simulated map
        val mapsKey = getString(R.string.google_maps_key)
        if (mapsKey == "YOUR_GOOGLE_MAPS_KEY_HERE" || mapsKey.startsWith("AQ.")) {
            useSimulatedMap = true
            binding.mapView.visibility = View.GONE
            binding.layoutSimulatedMap.visibility = View.VISIBLE
        } else {
            useSimulatedMap = false
            binding.mapView.visibility = View.VISIBLE
            binding.layoutSimulatedMap.visibility = View.GONE
            
            // Initialize Live Map
            try {
                binding.mapView.onCreate(savedInstanceState)
                binding.mapView.getMapAsync(this)
            } catch (e: Exception) {
                // Fallback to simulated map if Google Play Services is missing
                useSimulatedMap = true
                binding.mapView.visibility = View.GONE
                binding.layoutSimulatedMap.visibility = View.VISIBLE
            }
        }

        setupToggleListeners()
        setupBackNavigation()
        updateUiState()
        drawMapOverlays()
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
        if (useSimulatedMap) {
            if (isBeforeState) {
                binding.simFloodZone.visibility = View.VISIBLE
                binding.simFloodZone.alpha = 1.0f
                binding.simFloodMarker.visibility = View.VISIBLE
                binding.simTrafficMarker1.visibility = View.VISIBLE
                binding.simTrafficMarker2.visibility = View.VISIBLE
                binding.simSolvedMarker1.visibility = View.GONE
                binding.simSolvedMarker2.visibility = View.GONE
                binding.simAlertZone.visibility = View.GONE
                binding.simRerouteLine.visibility = View.GONE
                binding.simTruckMarker.visibility = View.GONE
            } else {
                binding.simFloodZone.visibility = View.VISIBLE
                binding.simFloodZone.alpha = 0.4f // faded opacity
                binding.simFloodMarker.visibility = View.GONE
                binding.simTrafficMarker1.visibility = View.GONE
                binding.simTrafficMarker2.visibility = View.GONE
                binding.simSolvedMarker1.visibility = View.VISIBLE
                binding.simSolvedMarker2.visibility = View.VISIBLE
                binding.simAlertZone.visibility = View.VISIBLE
                binding.simRerouteLine.visibility = View.VISIBLE
                binding.simTruckMarker.visibility = View.VISIBLE
            }
            return
        }

        val map = googleMap ?: return
        map.clear()

        // Surrounding intersection points for traffic congestion and solved routing indicators
        val trafficPoint1 = LatLng(33.6740, 72.9920)
        val trafficPoint2 = LatLng(33.6820, 73.0020)

        if (isBeforeState) {
            // Draw Flood Risk Polygon (Red, semi-transparent)
            map.addPolygon(
                PolygonOptions()
                    .addAll(g10Boundary)
                    .fillColor(Color.parseColor("#40E84040")) // 25% opacity red
                    .strokeColor(Color.parseColor("#E84040"))
                    .strokeWidth(2f)
            )

            // Pulsing Flood Center Marker (Red)
            map.addMarker(
                MarkerOptions()
                    .position(floodPoint)
                    .title("G-10 Underpass Flooded (CRITICAL)")
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
            )

            // Traffic Congested Intersections (Red Markers)
            map.addMarker(
                MarkerOptions()
                    .position(trafficPoint1)
                    .title("CONGESTED INTERSECTION (8 km/h)")
                    .snippet("Water backing up from underpass")
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
            )
            map.addMarker(
                MarkerOptions()
                    .position(trafficPoint2)
                    .title("TRAFFIC GRIDLOCK (5 km/h)")
                    .snippet("Severe commuter back up")
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
                    .snippet("Suction pump active - clearing drainage lines")
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE))
            )

            // Traffic Solved / Rerouted Intersections (Green Markers)
            map.addMarker(
                MarkerOptions()
                    .position(trafficPoint1)
                    .title("TRAFFIC REROUTED (35 km/h)")
                    .snippet("Service Road West bypass route clear")
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
            )
            map.addMarker(
                MarkerOptions()
                    .position(trafficPoint2)
                    .title("DETOUR FLOWING (38 km/h)")
                    .snippet("Gridlock cleared via diversion plan")
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
            )
        }
    }

    // Lifecycles for Google MapView
    override fun onStart() {
        super.onStart()
        if (_binding != null && !useSimulatedMap) {
            binding.mapView.onStart()
        }
    }

    override fun onResume() {
        super.onResume()
        if (_binding != null && !useSimulatedMap) {
            binding.mapView.onResume()
        }
    }

    override fun onPause() {
        super.onPause()
        if (_binding != null && !useSimulatedMap) {
            binding.mapView.onPause()
        }
    }

    override fun onStop() {
        super.onStop()
        if (_binding != null && !useSimulatedMap) {
            binding.mapView.onStop()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        if (_binding != null && !useSimulatedMap) {
            binding.mapView.onSaveInstanceState(outState)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (_binding != null && !useSimulatedMap) {
            binding.mapView.onDestroy()
        }
        _binding = null
    }

    override fun onLowMemory() {
        super.onLowMemory()
        if (_binding != null && !useSimulatedMap) {
            binding.mapView.onLowMemory()
        }
    }
}
