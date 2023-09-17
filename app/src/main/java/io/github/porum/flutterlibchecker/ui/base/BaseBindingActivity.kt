package io.github.porum.flutterlibchecker.ui.base

import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AppCompatActivity
import androidx.viewbinding.ViewBinding
import java.lang.reflect.ParameterizedType

open class BaseBindingActivity<VB : ViewBinding> : AppCompatActivity() {
  protected lateinit var binding: VB

  @Suppress("UNCHECKED_CAST")
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    val type = javaClass.genericSuperclass as ParameterizedType
    val clazz = type.actualTypeArguments[0] as Class<VB>
    val method = clazz.getMethod("inflate", LayoutInflater::class.java)
    binding = method.invoke(null, layoutInflater) as VB
    setContentView(binding.root)
  }
}