package com.where2meet.ui.screen.group.list

import com.where2meet.core.domain.repository.GroupRepository
import com.where2meet.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ListGroupViewModel @Inject constructor(
    private val group: GroupRepository,
) : BaseViewModel()
