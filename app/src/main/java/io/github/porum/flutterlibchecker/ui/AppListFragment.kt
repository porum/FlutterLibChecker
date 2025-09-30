package io.github.porum.flutterlibchecker.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import dagger.hilt.android.AndroidEntryPoint
import io.github.porum.flutterlibchecker.databinding.FragmentAppListBinding
import io.github.porum.flutterlibchecker.databinding.ItemAppInfoBinding
import io.github.porum.flutterlibchecker.db.model.AppInfo
import io.github.porum.flutterlibchecker.ui.base.BaseBindingFragment
import io.github.porum.flutterlibchecker.ui.decoration.SimpleItemDecoration
import io.github.porum.flutterlibchecker.ui.extension.dp
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

@AndroidEntryPoint
class AppListFragment : BaseBindingFragment<FragmentAppListBinding>() {

  private val viewModel by activityViewModels<MainViewModel>()

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)

    binding.recycleView.addItemDecoration(SimpleItemDecoration(top = 4.dp))

    viewLifecycleOwner.lifecycleScope.launch {
      viewModel.uiState.flowWithLifecycle(lifecycle, Lifecycle.State.CREATED).onEach {
        when (it) {
          is AppListState.Start -> {
            binding.loadingLayout.isVisible = true
          }

          is AppListState.Progress -> {
            binding.progressBar.setProgress(100 * (it.index + 1) / it.total, true)
            binding.progressText.text = "${it.index + 1}/${it.total}"
            binding.recycleView.adapter = AppInfoListAdapter(it.appInfoList)
          }

          is AppListState.Success -> {
            binding.loadingLayout.isVisible = false
          }
        }
      }.collect()
    }
  }

  inner class AppInfoListAdapter(
    private val appInfoList: List<AppInfo>,
  ) : RecyclerView.Adapter<AppInfoViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = AppInfoViewHolder(
      ItemAppInfoBinding.inflate(
        LayoutInflater.from(parent.context),
        parent,
        false,
      )
    )

    override fun getItemCount() = appInfoList.size

    override fun onBindViewHolder(holder: AppInfoViewHolder, position: Int) {
      holder.bind(appInfoList[position])
    }

  }

  inner class AppInfoViewHolder(
    private val binding: ItemAppInfoBinding,
  ) : ViewHolder(binding.root) {
    fun bind(appInfo: AppInfo) {
      with(appInfo) {
        binding.appName.text = appName
        binding.appId.text = applicationId
        binding.version.text = "$versionName($versionCode)"
        binding.flutterVersion.text = "Flutter $flutterVersion"
        binding.dartVersion.text = "Dart $dartVersion"
        binding.channel.text = channel
        binding.appIcon.setImageDrawable(
          itemView.context.packageManager.getPackageInfo(
            applicationId,
            0
          ).applicationInfo?.loadIcon(
            itemView.context.packageManager
          )
        )

        binding.root.setOnClickListener {
          viewModel.toPackageList(appInfo)
        }
      }
    }
  }
}