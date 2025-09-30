package io.github.porum.flutterlibchecker.ui.base

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.viewbinding.ViewBinding
import java.lang.reflect.ParameterizedType

open class BaseBindingFragment<VB : ViewBinding> : Fragment(), LifecycleEventObserver {

  private var _binding: VB? = null
  protected val binding get() = _binding!!

  override fun onAttach(context: Context) {
    super.onAttach(context)
    requireActivity().lifecycle.addObserver(this)
  }

  @Suppress("UNCHECKED_CAST")
  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View {
    val type = javaClass.genericSuperclass
    val clazz = (type as ParameterizedType).actualTypeArguments[0] as Class<VB>
    val method = clazz.getMethod(
      "inflate",
      LayoutInflater::class.java,
      ViewGroup::class.java,
      Boolean::class.java
    )
    _binding = method.invoke(null, layoutInflater, container, false) as VB
    this.viewLifecycleOwner.lifecycle.addObserver(object : DefaultLifecycleObserver {
      override fun onDestroy(owner: LifecycleOwner) {
        super.onDestroy(owner)
        _binding = null
      }
    })
    return binding.root
  }

  override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
    if (event.targetState == Lifecycle.State.CREATED) {
      onActivityCreated(source)
    }
  }

  open fun onActivityCreated(owner: LifecycleOwner) {

  }
}