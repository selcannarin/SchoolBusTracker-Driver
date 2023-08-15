package com.selcannarin.schoolbustrackerdriver.data.datasource.location

import com.selcannarin.schoolbustrackerdriver.data.model.Driver
import com.selcannarin.schoolbustrackerdriver.data.model.Location
import com.selcannarin.schoolbustrackerdriver.util.UiState

interface LocationDataSource {

    suspend fun addLocationToFirebase(
        user: Driver,
        location: Location,
        result: (UiState<String>) -> Unit
    )

}