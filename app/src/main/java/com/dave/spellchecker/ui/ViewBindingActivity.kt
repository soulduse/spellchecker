package com.dave.spellchecker.ui

import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LiveData
import androidx.viewbinding.ViewBinding
import com.squareup.moshi.Moshi
import javax.inject.Inject

abstract class ViewBindingActivity<VB : ViewBinding> : AppCompatActivity() {

    @Inject lateinit var moshi: Moshi

    private var _binding: ViewBinding? = null
    abstract val bindingInflater: (LayoutInflater) -> VB

    @Suppress("UNCHECKED_CAST")
    protected val binding: VB
        get() = _binding as VB

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = bindingInflater.invoke(layoutInflater)
        setContentView(requireNotNull(_binding).root)
        setup()
    }

    abstract fun setup()

    inline fun <T> LiveData<T>.observe(crossinline function: (T) -> Unit) = this.observe(this@ViewBindingActivity, { it?.let(function) })

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}
