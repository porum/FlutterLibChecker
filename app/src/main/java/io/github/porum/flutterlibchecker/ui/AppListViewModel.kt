package io.github.porum.flutterlibchecker.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.porum.flutterlibchecker.ui.AppListActivityState.Loading
import io.github.porum.flutterlibchecker.ui.AppListActivityState.Success
import io.github.porum.flutterlibchecker.db.model.AppInfo
import io.github.porum.flutterlibchecker.repository.AppInfoRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class AppListViewModel @Inject constructor(
  appInfoRepository: AppInfoRepository,
) : ViewModel() {

  val uiState: StateFlow<AppListActivityState> = appInfoRepository.appInfoList
    .map { Success(it) }
    .flowOn(Dispatchers.Main)
    .stateIn(
      scope = viewModelScope,
      initialValue = Loading,
      started = SharingStarted.WhileSubscribed(5000)
    )
}

sealed interface AppListActivityState {
  data object Loading : AppListActivityState
  data class Success(val appInfoList: List<AppInfo>) : AppListActivityState
}