package com.where2meet.ui.screen.group.create.mood

import androidx.lifecycle.viewModelScope
import com.where2meet.core.domain.model.Mood
import com.where2meet.core.domain.repository.GroupRepository
import com.where2meet.ui.base.BaseViewModel
import com.where2meet.ui.base.Event
import com.where2meet.ui.parcelable.MoodChipData
import com.where2meet.ui.parcelable.toChipData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import logcat.logcat
import java.nio.file.Files.find
import javax.inject.Inject

@HiltViewModel
class PickMoodViewModel @Inject constructor(
    private val group: GroupRepository,
) : BaseViewModel() {
    private val mMoods = MutableStateFlow<List<MoodChipData>>(emptyList())
    val moods = mMoods.asStateFlow()

    suspend fun selectedMoods() = mMoods.map { list ->
        list.filter { it.isSelected }
    }.stateIn(viewModelScope)

    init {
        fetchMoods()
    }

    private fun fetchMoods() {
        apiJob?.cancel()
        apiJob = viewModelScope.launch {
            Event.Loading.send()
            group.fetchMoods()
                .catch { Event.Error(it).send() }
                .collect { result ->
                    if (result.isSuccess) {
                        mMoods.emit(result.getOrThrow().map(Mood::toChipData))
                        Event.NotLoading.send()
                    } else if (result.isFailure)
                        Event.Error(result.exceptionOrNull()).send()
                }
        }
    }

    fun updateSelectedChip(data: MoodChipData, checked: Boolean) {
       mMoods.value.find { it.mood.id == data.mood.id }?.isSelected = checked
    }

    fun submitMood() {
        apiJob?.cancel()
        apiJob = viewModelScope.launch {
            val selected = selectedMoods().value.map { it.mood }
            logcat { "submitMood() -> ${selected.map { it.name }}" }
            Event.Loading.send()
            // group.updateMoods(-1, selected)
            //     .catch { Event.Error(it).send() }
            //     .collect { result ->
            //         if (result.isSuccess) {
            //             CreateGroupEvent.MoodsSubmitted.send()
            //         } else if (result.isFailure)
            //             Event.Error(result.exceptionOrNull()).send()
            //     }
        }
    }
}
