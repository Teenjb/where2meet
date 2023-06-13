package com.where2meet.ui.screen.group.create.location

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.LatLng
import com.where2meet.core.domain.model.form.UpdateLocation
import com.where2meet.core.domain.repository.GroupRepository
import com.where2meet.ui.base.BaseViewModel
import com.where2meet.ui.base.Event
import com.where2meet.ui.screen.group.create.CreateGroupEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PickLocationViewModel @Inject constructor(
    private val group: GroupRepository,
    handle: SavedStateHandle,
) : BaseViewModel() {
    private val groupId = handle.get<Int>("groupId") ?: -1
    fun submitLocation(location: LatLng) {
        val form = UpdateLocation(lat = location.latitude, lng = location.longitude)
        apiJob?.cancel()
        apiJob = viewModelScope.launch {
            group.updateLocation(groupId, form)
                .catch { Event.Error(it).send() }
                .collect { result ->
                    if (result.isSuccess) {
                        CreateGroupEvent.LocationSubmitted.send()
                    } else if (result.isFailure) {
                        Event.Error(result.exceptionOrNull()).send()
                    }
                }
        }
    }
}
