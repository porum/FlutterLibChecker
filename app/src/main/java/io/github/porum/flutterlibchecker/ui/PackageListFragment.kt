package io.github.porum.flutterlibchecker.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.net.toUri
import androidx.core.view.MenuProvider
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import dagger.hilt.android.AndroidEntryPoint
import io.github.porum.flutterlibchecker.databinding.FragmentPackageListBinding
import io.github.porum.flutterlibchecker.databinding.ItemPackageInfoBinding
import io.github.porum.flutterlibchecker.ui.base.BaseBindingFragment
import io.github.porum.flutterlibchecker.ui.decoration.SimpleItemDecoration
import io.github.porum.flutterlibchecker.ui.extension.dp
import kotlinx.coroutines.launch

@AndroidEntryPoint
class PackageListFragment : BaseBindingFragment<FragmentPackageListBinding>() {

  private val viewModel by activityViewModels<MainViewModel>()

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)

    (activity as AppCompatActivity).setSupportActionBar(binding.toolbar)
    (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
    requireActivity().addMenuProvider(object : MenuProvider {
      override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
      }

      override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        if (menuItem.itemId == android.R.id.home) {
          viewModel.toAppList()
          return true
        }
        return false
      }
    }, viewLifecycleOwner, Lifecycle.State.RESUMED)

    binding.recycleView.addItemDecoration(SimpleItemDecoration(top = 4.dp))

    viewLifecycleOwner.lifecycleScope.launch {
      viewModel.targetAppInfo.flowWithLifecycle(
        viewLifecycleOwner.lifecycle,
        Lifecycle.State.STARTED
      ).collect { appInfo ->
        if (appInfo == null) {
          return@collect
        }

        binding.toolbar.title = appInfo.appName
        binding.recycleView.adapter = object : RecyclerView.Adapter<PackageItemViewHolder>() {
          override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
            PackageItemViewHolder(
              ItemPackageInfoBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
              )
            )

          override fun getItemCount(): Int = appInfo.packages.size

          override fun onBindViewHolder(holder: PackageItemViewHolder, position: Int) {
            holder.bind(appInfo.packages.elementAt(position))
          }
        }
      }
    }
  }
}

class PackageItemViewHolder(
  private val binding: ItemPackageInfoBinding
) : ViewHolder(binding.root) {
  fun bind(packageName: String) {
    binding.packageName.text = packageName
    binding.root.setOnClickListener {
      val customTabIntent = CustomTabsIntent.Builder().build()
      customTabIntent.launchUrl(
        itemView.context,
        "https://pub.dev/packages/$packageName".toUri()
      )
    }
  }
}