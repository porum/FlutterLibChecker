package io.github.porum.flutterlibchecker.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.porum.flutterlibchecker.repository.AppInfoRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class PackageListViewModel @Inject constructor(
  private val appInfoRepository: AppInfoRepository,
) : ViewModel() {

  fun getPackageList(applicationId: String): StateFlow<PackageListActivityState> =
    appInfoRepository.getPackageList(applicationId)
      .map { PackageListActivityState.Success(it) }
      .flowOn(Dispatchers.Main)
      .stateIn(
        scope = viewModelScope,
        initialValue = PackageListActivityState.Loading,
        started = SharingStarted.WhileSubscribed(5000)
      )
}

sealed interface PackageListActivityState {
  data object Loading : PackageListActivityState
  data class Success(val packageList: Collection<String>) : PackageListActivityState
}