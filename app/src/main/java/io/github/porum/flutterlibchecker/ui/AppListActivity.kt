package io.github.porum.flutterlibchecker.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.activity.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import dagger.hilt.android.AndroidEntryPoint
import io.github.porum.flutterlibchecker.databinding.ActivityAppListBinding
import io.github.porum.flutterlibchecker.databinding.ItemAppInfoBinding
import io.github.porum.flutterlibchecker.db.model.AppInfo
import io.github.porum.flutterlibchecker.ui.base.BaseBindingActivity
import io.github.porum.flutterlibchecker.ui.decoration.SimpleItemDecoration
import io.github.porum.flutterlibchecker.ui.extension.dp
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

@AndroidEntryPoint
class AppListActivity : BaseBindingActivity<ActivityAppListBinding>() {

  private val viewModel: AppListViewModel by viewModels()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    binding.recycleView.addItemDecoration(SimpleItemDecoration(top = 4.dp))

    lifecycleScope.launch {
      lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
        viewModel.uiState
          .onEach {
            when (it) {
              is AppListActivityState.Loading -> {}
              is AppListActivityState.Success -> {
                binding.recycleView.adapter = AppInfoListAdapter(it.appInfoList)
              }
            }
          }
          .collect()
      }
    }
  }

  class AppInfoListAdapter(
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

  class AppInfoViewHolder(
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
          itemView.context.packageManager.getPackageInfo(applicationId, 0).applicationInfo.loadIcon(
            itemView.context.packageManager
          )
        )

        binding.root.setOnClickListener {
          PackageListActivity.start(itemView.context, appName, applicationId)
        }
      }
    }
  }
}