package io.github.porum.flutterlibchecker.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.porum.flutterlibchecker.data.Event
import io.github.porum.flutterlibchecker.db.model.AppInfo
import io.github.porum.flutterlibchecker.repository.AppInfoRepository
import io.github.porum.flutterlibchecker.ui.AppListState.Start
import io.github.porum.flutterlibchecker.ui.AppListState.Progress
import io.github.porum.flutterlibchecker.ui.AppListState.Success
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
  repository: AppInfoRepository,
) : ViewModel() {

  val uiState: StateFlow<AppListState> = repository.getFlutterApps()
    .map {
      if (it.index == it.total - 1) {
        Success
      } else {
        Progress(it.index, it.total, it.appList)
      }
    }
    .flowOn(Dispatchers.Main)
    .stateIn(
      scope = viewModelScope,
      initialValue = Start,
      started = SharingStarted.WhileSubscribed(5000)
    )

  private val _navigatorEvent = MutableStateFlow(Event(NavigatorEvent.ToAppList))
  val navigatorEvent: StateFlow<Event<NavigatorEvent>> = _navigatorEvent.asStateFlow()

  private val _targetAppInfo = MutableStateFlow<AppInfo?>(null)
  val targetAppInfo: StateFlow<AppInfo?> = _targetAppInfo.asStateFlow()

  fun toAppList() {
    if (_navigatorEvent.value.peekContent() == NavigatorEvent.ToPackageList) {
      _targetAppInfo.value = null
      _navigatorEvent.value = Event(NavigatorEvent.ToAppList)
    }
  }

  fun toPackageList(appInfo: AppInfo) {
    _targetAppInfo.value = appInfo
    _navigatorEvent.value = Event(NavigatorEvent.ToPackageList)
  }

  fun toBackground() {
    if (_navigatorEvent.value.peekContent() == NavigatorEvent.ToAppList) {
      _navigatorEvent.value = Event(NavigatorEvent.ToBackground)
    }
  }

}

sealed interface AppListState {
  data object Start : AppListState

  data class Progress(
    val index: Int,
    val total: Int,
    val appInfoList: List<AppInfo>
  ) : AppListState

  data object Success : AppListState
}

enum class NavigatorEvent {
  ToAppList,
  ToPackageList,
  ToBackground
}