package io.github.porum.flutterlibchecker.ui

import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import io.github.porum.flutterlibchecker.databinding.ActivityMainBinding
import io.github.porum.flutterlibchecker.ui.base.BaseBindingActivity
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : BaseBindingActivity<ActivityMainBinding>() {
  private val viewModel by viewModels<MainViewModel>()

  private val appListFragment = AppListFragment()
  private val packageListFragment = PackageListFragment()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    supportFragmentManager.beginTransaction()
      .add(binding.fragmentContainer.id, appListFragment)
      .add(binding.fragmentContainer.id, packageListFragment)
      .commit()
    supportFragmentManager.beginTransaction()
      .hide(appListFragment)
      .hide(packageListFragment)
      .commit()

    lifecycleScope.launch {
      viewModel.navigatorEvent.flowWithLifecycle(
        lifecycle,
        Lifecycle.State.STARTED
      ).collectLatest { event ->
        event.getContentIfNotConsumed()?.let {
          when (it) {
            NavigatorEvent.ToAppList -> {
              supportFragmentManager.beginTransaction()
                .hide(packageListFragment)
                .show(appListFragment)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .commit()
            }

            NavigatorEvent.ToPackageList -> {
              supportFragmentManager.beginTransaction()
                .hide(appListFragment)
                .show(packageListFragment)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .commit()
            }

            NavigatorEvent.ToBackground -> {
              finish()
            }
          }
        }
      }
    }

    onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
      override fun handleOnBackPressed() {
        if (viewModel.navigatorEvent.value.peekContent() == NavigatorEvent.ToAppList) {
          viewModel.toBackground()
        } else if (viewModel.navigatorEvent.value.peekContent() == NavigatorEvent.ToPackageList) {
          viewModel.toAppList()
        }
      }
    }
    )
  }
}