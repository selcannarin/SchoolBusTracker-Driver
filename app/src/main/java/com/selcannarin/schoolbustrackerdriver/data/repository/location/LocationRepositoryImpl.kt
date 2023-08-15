package com.selcannarin.schoolbustrackerdriver.data.repository.location

import com.selcannarin.schoolbustrackerdriver.data.datasource.location.LocationDataSource
import com.selcannarin.schoolbustrackerdriver.data.model.Driver
import com.selcannarin.schoolbustrackerdriver.data.model.Location
import com.selcannarin.schoolbustrackerdriver.util.UiState
import javax.inject.Inject

class LocationRepositoryImpl @Inject constructor(
    private val locationDataSource: LocationDataSource
) : LocationRepository {
    override suspend fun addLocationToFirebase(
        user: Driver,
        location: Location,
        result: (UiState<String>) -> Unit
    ) {
        return locationDataSource.addLocationToFirebase(user, location, result)
    }
}