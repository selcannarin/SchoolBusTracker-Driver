package com.selcannarin.schoolbustrackerdriver.ui.location

import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.selcannarin.schoolbustrackerdriver.R
import com.selcannarin.schoolbustrackerdriver.databinding.FragmentLocationBinding
import com.selcannarin.schoolbustrackerdriver.ui.MainActivity
import dagger.hilt.android.AndroidEntryPoint
import java.io.IOException
import java.util.Locale

@AndroidEntryPoint
class LocationFragment : Fragment(), OnMapReadyCallback {

    private lateinit var googleMap: GoogleMap
    private var _binding: FragmentLocationBinding? = null
    private val binding get() = _binding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentLocationBinding.bind(view)
        (activity as MainActivity).setBottomNavVisibilityVisible()
        val toolbar = (activity as AppCompatActivity).supportActionBar
        toolbar?.title = "Location"
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentLocationBinding.inflate(inflater, container, false)

        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as? SupportMapFragment
        mapFragment?.getMapAsync(this)

        binding?.showLocationButton?.setOnClickListener {
            val address = binding?.addressEditText?.text.toString()
            if (address.isNotEmpty()) {
                val location = getLocationFromAddress(address)
                Toast.makeText(
                    requireContext(),
                    "Longitude: ${location?.longitude}, Latitude: ${location?.latitude}",
                    Toast.LENGTH_SHORT
                ).show()
                location?.let {
                    googleMap.clear()
                    googleMap.addMarker(MarkerOptions().position(location).title("Location"))
                    googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(location, 15f))
                }
            }
        }

        return binding?.root
    }

    override fun onMapReady(map: GoogleMap) {
        googleMap = map
    }

    fun getLocationFromAddress(address: String): LatLng? {
        val geocoder = Geocoder(requireContext())
        val addressList: List<Address>?
        var latLng: LatLng? = null

        try {
            addressList = geocoder.getFromLocationName(address, 1)
            if (addressList != null && addressList.isNotEmpty()) {
                val location = addressList[0]
                val latitude = location.latitude
                val longitude = location.longitude
                latLng = LatLng(latitude, longitude)
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return latLng
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}







