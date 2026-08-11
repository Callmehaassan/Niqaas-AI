package com.nikaas.app.ui.citizen

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.nikaas.app.databinding.FragmentCitizenReportBinding
import com.nikaas.app.ui.citizen.adapters.CitizenReportsAdapter

class CitizenReportFragment : Fragment() {

    private var _binding: FragmentCitizenReportBinding? = null
    private val binding get() = _binding!!

    private val viewModel: CitizenReportViewModel by viewModels()
    private lateinit var adapter: CitizenReportsAdapter

    // Camera launcher to capture thumbnail image
    private val cameraLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val bitmap = result.data?.extras?.get("data") as? Bitmap
            if (bitmap != null) {
                binding.imgThumbnail.setImageBitmap(bitmap)
                binding.cardThumbnail.visibility = View.VISIBLE
                Toast.makeText(requireContext(), "Photo attached successfully!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // GPS location permissions contract
    private val locationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val fineGranted = permissions[android.Manifest.permission.ACCESS_FINE_LOCATION] ?: false
        val coarseGranted = permissions[android.Manifest.permission.ACCESS_COARSE_LOCATION] ?: false
        if (fineGranted || coarseGranted) {
            fetchDeviceLocation()
        } else {
            Toast.makeText(requireContext(), "Location permission denied", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCitizenReportBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupLocationFields()
        setupChips()
        setupRecyclerView()
        setupSubmitButton()

        // Observe LiveData reports list
        viewModel.reports.observe(viewLifecycleOwner) { reports ->
            adapter.updateData(reports)
        }
    }

    private fun setupLocationFields() {
        binding.btnCurrentLocation.setOnClickListener {
            locationPermissionLauncher.launch(
                arrayOf(
                    android.Manifest.permission.ACCESS_FINE_LOCATION,
                    android.Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
        binding.btnAddPhoto.setOnClickListener {
            val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            try {
                cameraLauncher.launch(cameraIntent)
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Camera not available: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun fetchDeviceLocation() {
        try {
            val locationManager = requireContext().getSystemService(android.content.Context.LOCATION_SERVICE) as android.location.LocationManager
            val isGpsEnabled = locationManager.isProviderEnabled(android.location.LocationManager.GPS_PROVIDER)
            val isNetworkEnabled = locationManager.isProviderEnabled(android.location.LocationManager.NETWORK_PROVIDER)

            if (!isGpsEnabled && !isNetworkEnabled) {
                binding.inputLocation.setText("G-10 Sector (GPS Off: 33.6784, 72.9972)")
                Toast.makeText(requireContext(), "Location services are disabled. Using mock G-10 location.", Toast.LENGTH_SHORT).show()
                return
            }

            var location: android.location.Location? = null
            if (isNetworkEnabled) {
                location = locationManager.getLastKnownLocation(android.location.LocationManager.NETWORK_PROVIDER)
            }
            if (location == null && isGpsEnabled) {
                location = locationManager.getLastKnownLocation(android.location.LocationManager.GPS_PROVIDER)
            }

            if (location != null) {
                val latStr = String.format("%.4f", location.latitude)
                val lonStr = String.format("%.4f", location.longitude)
                binding.inputLocation.setText("G-10 Sector (GPS: $latStr, $lonStr)")
                Toast.makeText(requireContext(), "Device GPS location loaded!", Toast.LENGTH_SHORT).show()
            } else {
                binding.inputLocation.setText("G-10 Sector (GPS Mock: 33.6784, 72.9972)")
                Toast.makeText(requireContext(), "GPS signal lost. Using simulated G-10 location.", Toast.LENGTH_SHORT).show()
            }
        } catch (e: SecurityException) {
            Toast.makeText(requireContext(), "Location access error: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupChips() {
        binding.chipPhrase1.setOnClickListener {
            binding.inputDescription.setText(binding.chipPhrase1.text)
        }
        binding.chipPhrase2.setOnClickListener {
            binding.inputDescription.setText(binding.chipPhrase2.text)
        }
        binding.chipPhrase3.setOnClickListener {
            binding.inputDescription.setText(binding.chipPhrase3.text)
        }
    }

    private fun setupRecyclerView() {
        adapter = CitizenReportsAdapter()
        binding.rvRecentReports.layoutManager = LinearLayoutManager(requireContext())
        binding.rvRecentReports.adapter = adapter
    }

    private fun setupSubmitButton() {
        binding.btnSubmit.setOnClickListener {
            val location = binding.inputLocation.text?.toString()?.trim() ?: ""
            val description = binding.inputDescription.text?.toString()?.trim() ?: ""

            if (location.isEmpty()) {
                Toast.makeText(requireContext(), "Please enter location first", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (description.isEmpty()) {
                Toast.makeText(requireContext(), "Please enter a flood description first", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Extract the attached photo bitmap if any
            val bitmap = (binding.imgThumbnail.drawable as? android.graphics.drawable.BitmapDrawable)?.bitmap

            // Show temporary confirmation state: "Report submitted, analyzing..."
            binding.cardConfirmation.visibility = View.VISIBLE
            binding.btnSubmit.isEnabled = false

            viewModel.submitReport(location, description, bitmap) {
                if (_binding != null) {
                    binding.inputLocation.setText("")
                    binding.inputDescription.setText("")
                    binding.cardThumbnail.visibility = View.GONE
                    binding.imgThumbnail.setImageDrawable(null)
                    binding.cardConfirmation.visibility = View.GONE
                    binding.btnSubmit.isEnabled = true
                    Toast.makeText(requireContext(), "Flood report submitted successfully!", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        // Ensure data is refreshed when returning to this fragment
        viewModel.loadReports()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
