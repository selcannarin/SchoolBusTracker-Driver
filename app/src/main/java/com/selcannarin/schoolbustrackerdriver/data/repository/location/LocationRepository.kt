package com.selcannarin.schoolbustrackerdriver.data.repository.location

import com.selcannarin.schoolbustrackerdriver.data.model.Driver
import com.selcannarin.schoolbustrackerdriver.data.model.Location
import com.selcannarin.schoolbustrackerdriver.util.UiState

interface LocationRepository {

    suspend fun addLocationToFirebase(
        user: Driver,
        location: Location,
        result: (UiState<String>) -> Unit
    )

}