package com.where2meet.ui.screen.group.detail

import com.where2meet.core.domain.repository.GroupRepository
import com.where2meet.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class DetailGroupViewModel @Inject constructor(
    private val group: GroupRepository,
) : BaseViewModel()
