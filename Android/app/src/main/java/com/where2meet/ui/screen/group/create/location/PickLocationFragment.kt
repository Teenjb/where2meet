package com.where2meet.ui.screen.group.create.location

import android.Manifest
import android.content.Intent
import android.location.Location
import android.net.Uri
import android.provider.Settings
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.afollestad.materialdialogs.MaterialDialog
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.where2meet.R
import com.where2meet.databinding.FragmentPickLocationBinding
import com.where2meet.ui.base.BaseFragment
import com.where2meet.ui.base.Event
import com.where2meet.ui.ext.checkPermission
import com.where2meet.ui.ext.snackbar
import com.where2meet.ui.ext.toast
import com.where2meet.ui.ext.viewBinding
import com.where2meet.ui.screen.group.create.CreateGroupEvent
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import logcat.logcat
import reactivecircus.flowbinding.android.view.clicks

class PickLocationFragment : BaseFragment(R.layout.fragment_pick_location) {
    private val binding by viewBinding<FragmentPickLocationBinding>()
    private val viewModel by viewModels<PickLocationViewModel>()
    private val args: PickLocationFragmentArgs by navArgs()

    private lateinit var mMap: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var centerMarker: Marker? = null

    private val callback = OnMapReadyCallback { googleMap ->
        mMap = googleMap

        mMap.uiSettings.apply {
            isZoomControlsEnabled = true
            isIndoorLevelPickerEnabled = true
            isCompassEnabled = true
            isRotateGesturesEnabled = true
        }

        mMap.setOnCameraIdleListener {
            centerMarker?.remove()

            centerMarker = mMap.addMarker(
                MarkerOptions()
                    .position(mMap.cameraPosition.target)
            )
        }

        getCurrentLocation()
    }

    override fun onStart() {
        super.onStart()
        eventJob = viewModel.events
            .onEach { event ->
                when (event) {
                    is CreateGroupEvent.LocationSubmitted -> {
                        toggleLoading(false)
                        toast(getString(R.string.msg_location_selected))
                        navigateTo(
                            PickLocationFragmentDirections.actionPickLocationToDetail(
                                args.groupId,
                                args.isAdmin
                            )
                        )
                    }

                    is Event.Loading -> {
                        toggleLoading(true)
                    }

                    is Event.NotLoading -> {
                        toggleLoading(false)
                    }

                    is Event.Error -> {
                        toggleLoading(false)
                        logcat { "Error : ${event.throwable?.message}" }
                        snackbar(
                            "Error : ${event.throwable?.message}",
                            binding.sectionSheet,
                        )
                    }
                }
            }.launchIn(lifecycleScope)
    }

    override fun bindView() {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())

        with(binding) {
            val mapView = childFragmentManager.findFragmentById(map.id) as SupportMapFragment
            mapView.getMapAsync(callback)

            btnDetectLocation.clicks().onEach {
                getCurrentLocation()
            }.launchIn(lifecycleScope)

            btnSelectLocation.clicks().onEach {
                viewModel.submitLocation(mMap.cameraPosition.target)
            }.launchIn(lifecycleScope)
        }
    }

    private fun toggleLoading(flag: Boolean) {
        binding.loadingBar.isVisible = flag
    }

    // location shenanigans
    private fun getCurrentLocation() {
        if (checkPermission(Manifest.permission.ACCESS_FINE_LOCATION) &&
            checkPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
        ) {
            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                if (location != null) {
                    mMap.moveCamera(
                        CameraUpdateFactory.newLatLngZoom(
                            LatLng(location.latitude, location.longitude),
                            15f // city-wide zoom
                        )
                    )
                } else {
                    toast(getString(R.string.err_location_not_found))
                }
            }
        } else {
            requestLocationPermission()
        }
    }

    private fun requestLocationPermission() {
        MaterialDialog(requireContext()).show {
            title(R.string.dialog_location_permission_needed_title)
            message(R.string.dialog_location_permission_needed_message)
            positiveButton(android.R.string.ok) {
                requestLocationPermissionLauncher.launch(
                    arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    )
                )
            }
            negativeButton(android.R.string.cancel) {
                findNavController().popBackStack()
            }
        }
    }

    private fun onPermissionDenied() {
        MaterialDialog(requireContext()).show {
            title(R.string.dialog_location_permission_rejected_title)
            message(R.string.dialog_location_permission_rejected_message)
            positiveButton(android.R.string.ok) {
                Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).also {
                    it.data = Uri.fromParts("package", requireActivity().packageName, null)
                    startActivity(it)
                }
            }
        }
    }

    private val requestLocationPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            when {
                permissions.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false) -> {
                    getCurrentLocation()
                }

                permissions.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false) -> {
                    getCurrentLocation()
                }

                else -> {
                    onPermissionDenied()
                }
            }
        }
}
