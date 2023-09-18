package io.github.porum.flutterlibchecker.ui

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.ViewGroup
import androidx.activity.viewModels
import androidx.browser.customtabs.CustomTabsIntent
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import dagger.hilt.android.AndroidEntryPoint
import io.github.porum.flutterlibchecker.databinding.ActivityPackageListBinding
import io.github.porum.flutterlibchecker.databinding.ItemPackageInfoBinding
import io.github.porum.flutterlibchecker.ui.base.BaseBindingActivity
import io.github.porum.flutterlibchecker.ui.decoration.SimpleItemDecoration
import io.github.porum.flutterlibchecker.ui.extension.dp
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

@AndroidEntryPoint
class PackageListActivity : BaseBindingActivity<ActivityPackageListBinding>() {

  private val vm by viewModels<PackageListViewModel>()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setSupportActionBar(binding.toolbar)
    supportActionBar?.setDisplayHomeAsUpEnabled(true)

    binding.toolbar.title = intent.getStringExtra(EXTRA_APP_NAME)
    binding.recycleView.addItemDecoration(SimpleItemDecoration(top = 4.dp))

    lifecycleScope.launch {
      vm.getPackageList(intent.getStringExtra(EXTRA_PACKAGE_NAME)!!).onEach {
        when (it) {
          is PackageListActivityState.Loading -> {
            binding.loading.show()
          }

          is PackageListActivityState.Success -> {
            binding.loading.hide()
            binding.recycleView.adapter =
              object : RecyclerView.Adapter<PackageItemViewHolder>() {
                override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
                  PackageItemViewHolder(
                    ItemPackageInfoBinding.inflate(
                      LayoutInflater.from(parent.context),
                      parent,
                      false
                    )
                  )

                override fun getItemCount(): Int = it.packageList.size

                override fun onBindViewHolder(holder: PackageItemViewHolder, position: Int) {
                  holder.bind(it.packageList.elementAt(position))
                }
              }
          }
        }
      }.collect()
    }
  }

  override fun onOptionsItemSelected(item: MenuItem): Boolean {
    if (item.itemId == android.R.id.home) {
      finish()
    }
    return true
  }

  companion object {

    private const val EXTRA_APP_NAME = "extra_app_name"
    private const val EXTRA_PACKAGE_NAME = "extra_application_id"

    fun start(context: Context, appName: String, applicationId: String) {
      context.startActivity(Intent(context, PackageListActivity::class.java).apply {
        putExtra(EXTRA_APP_NAME, appName)
        putExtra(EXTRA_PACKAGE_NAME, applicationId)
      })
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
        Uri.parse("https://pub.dev/packages/$packageName")
      )
    }
  }
}