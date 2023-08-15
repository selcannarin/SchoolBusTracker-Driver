package com.selcannarin.schoolbustrackerdriver.ui.location

import android.content.Context
import android.location.Address
import android.location.Geocoder
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.LatLng
import com.selcannarin.schoolbustrackerdriver.data.model.Driver
import com.selcannarin.schoolbustrackerdriver.data.model.Location
import com.selcannarin.schoolbustrackerdriver.data.repository.location.LocationRepository
import com.selcannarin.schoolbustrackerdriver.util.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class LocationViewModel @Inject constructor(
    private val locationRepository: LocationRepository
) : ViewModel() {

    private val _addLocation = MutableLiveData<UiState<String>>()
    val addLocation: LiveData<UiState<String>>
        get() = _addLocation

    fun addLocationToFirebase(user: Driver, location: Location) = viewModelScope.launch {
        _addLocation.value = UiState.Loading
        locationRepository.addLocationToFirebase(user, location) { _addLocation.value = it }
    }

    fun getLocationFromAddress(context: Context, address: String): LatLng? {
        val geocoder = Geocoder(context)
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


}