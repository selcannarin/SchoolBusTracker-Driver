package com.selcannarin.schoolbustrackerdriver.ui.location

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.selcannarin.schoolbustrackerdriver.R
import com.selcannarin.schoolbustrackerdriver.data.model.Driver
import com.selcannarin.schoolbustrackerdriver.data.model.Location
import com.selcannarin.schoolbustrackerdriver.data.model.Student
import com.selcannarin.schoolbustrackerdriver.databinding.FragmentLocationBinding
import com.selcannarin.schoolbustrackerdriver.ui.MainActivity
import com.selcannarin.schoolbustrackerdriver.ui.attendance.AttendanceViewModel
import com.selcannarin.schoolbustrackerdriver.ui.auth.AuthViewModel
import com.selcannarin.schoolbustrackerdriver.ui.profile.ProfileViewModel
import com.selcannarin.schoolbustrackerdriver.util.UiState
import dagger.hilt.android.AndroidEntryPoint
import java.util.Date
import java.util.Timer
import java.util.TimerTask

@AndroidEntryPoint
class LocationFragment : Fragment(), OnMapReadyCallback {

    private var _binding: FragmentLocationBinding? = null
    private val binding get() = _binding
    private val authViewModel: AuthViewModel by viewModels()
    private val profileViewModel: ProfileViewModel by viewModels()
    private val attendanceViewModel: AttendanceViewModel by viewModels()
    private val locationViewModel: LocationViewModel by viewModels()
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var googleMap: GoogleMap
    private val LOCATION_PERMISSION_REQUEST_CODE = 1001
    private val locationUpdateInterval = 30000L
    private lateinit var locationTimer: Timer
    private var currentUserMarker: Marker? = null
    private val driverLiveData: MutableLiveData<Driver> = MutableLiveData()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        _binding = FragmentLocationBinding.bind(view)
        (activity as MainActivity).setBottomNavVisibilityVisible()

        (requireActivity() as MainActivity).setToolbarTitle("Location")
        (activity as MainActivity).showNavigationDrawer()

        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as? SupportMapFragment
        mapFragment?.getMapAsync(this)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())

        locationTimer = Timer()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentLocationBinding.inflate(inflater, container, false)
        (activity as MainActivity).showNavigationDrawer()
        return binding?.root
    }

    override fun onMapReady(googleMap: GoogleMap) {
        this.googleMap = googleMap
        enableMyLocation()
        checkUser()
        driverLiveData.observe(viewLifecycleOwner) { driver ->
            startLocationUpdates(driver)
        }
    }

    private fun enableMyLocation() {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            googleMap.isMyLocationEnabled = true
        } else {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
        }
    }

    private fun checkUser(): LiveData<UiState<Driver>> {
        authViewModel.getCurrentUser()
        authViewModel.currentUser.observe(viewLifecycleOwner) { user ->
            if (user != null) {
                val email = user.email ?: ""
                val fullName = ""
                val licensePlate = ""
                val students: List<Int> = emptyList()
                profileViewModel.getDriver(Driver(email, fullName, licensePlate, students))
                profileViewModel.driver.observe(viewLifecycleOwner) { driverState ->
                    when (driverState) {
                        is UiState.Success -> {
                            val driver = driverState.data
                            driverLiveData.value = driver
                            getStudentNumberList(driver)
                        }

                        else -> {}
                    }
                }
            }
        }
        return profileViewModel.driver
    }


    private fun startLocationUpdates(user: Driver) {
        val timerTask = object : TimerTask() {
            override fun run() {
                Handler(Looper.getMainLooper()).post {
                    try {
                        getDriverLocation { driverLoc ->
                            if (driverLoc != null) {
                                locationViewModel.addLocationToFirebase(user, driverLoc)
                                updateCurrentUserLocationOnMap(driverLoc)

                            } else {
                            }
                        }
                    } catch (e: SecurityException) {
                        e.printStackTrace()
                    }
                }
            }
        }

        locationTimer.schedule(timerTask, 0, locationUpdateInterval)
    }

    private fun updateCurrentUserLocationOnMap(location: Location) {
        val userLocation = LatLng(location.latitude, location.longitude)
        currentUserMarker?.remove()
        currentUserMarker = googleMap.addMarker(
            MarkerOptions()
                .position(userLocation)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.custom_marker_driver_icon))
                .title("Driver Location")
        )
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 15f))
    }

    private fun getDriverLocation(callback: (Location?) -> Unit) {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            fusedLocationClient.lastLocation.addOnSuccessListener { lastLocation ->
                if (lastLocation != null) {
                    val latitude = lastLocation.latitude
                    val longitude = lastLocation.longitude
                    val currentDate = Date()
                    val location = Location(latitude, longitude, currentDate)
                    callback(location)
                } else {
                    callback(null)
                }
            }
        } else {
            callback(null)
        }
    }

    private fun getStudentNumberList(driver: Driver) {
        profileViewModel.getStudentNumberList(driver)
        profileViewModel.studentNumberList.observe(viewLifecycleOwner) { studentNumberList ->
            when (studentNumberList) {
                is UiState.Success -> {
                    val studentNumberList = studentNumberList.data
                    getStudentInfoList(studentNumberList)
                }

                else -> {}
            }
        }
    }

    private fun getStudentInfoList(studentNumberList: List<Int>) {
        attendanceViewModel.getStudentDetailsByNumbers(studentNumberList)
        attendanceViewModel.studentList.observe(viewLifecycleOwner) { studentList ->
            when (studentList) {
                is UiState.Success -> {
                    val studentInfoList = studentList.data
                    showStudentLocations(studentInfoList)
                }

                else -> {}
            }
        }
    }

    private fun showStudentLocations(studentList: List<Student>) {

        for (studentInfo in studentList) {
            val location = locationViewModel.getLocationFromAddress(
                requireContext(),
                studentInfo.student_address
            )
            location?.let {
                googleMap.addMarker(
                    MarkerOptions().position(location).title(studentInfo.student_name).icon(
                        BitmapDescriptorFactory.fromResource(R.drawable.custom_marker_student_icon)
                    )
                )
            }
        }

        val firstLocation = locationViewModel.getLocationFromAddress(
            requireContext(),
            studentList.firstOrNull()?.student_address ?: ""
        )
        firstLocation?.let {
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(it, 15f))
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
