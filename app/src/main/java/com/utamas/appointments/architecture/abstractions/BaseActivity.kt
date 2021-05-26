package com.utamas.appointments.architecture.abstractions

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.annotation.StringRes
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.utamas.appointments.AppointmentApplication
import com.utamas.appointments.architecture.annotations.DeclareViewModel
import com.utamas.appointments.architecture.annotations.DeclareXmlLayout
import com.utamas.appointments.architecture.exception.NotAnnotatedException
import java.lang.reflect.Method
import kotlin.reflect.KClass

abstract class BaseActivity<T : BaseViewModel> : AppCompatActivity() {
    //region private constants
    private val errorTemplate =
        "%s annotation is required to subclass " + this.javaClass.name + "!"
    private val bindingMethodName = "setVM"
    private val setContentViewMethodName = "setContentView"
    private val dataBindingUtil = "androidx.databinding.DataBindingUtil"

    //endregion
    //region members

    protected lateinit var viewModel: T
    private set

    protected lateinit var appointmentApplication: AppointmentApplication
    private set

    private lateinit var mainThreadHandler: Handler
    private var layout: Int = 0

    //endregion
    //region methods
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        appointmentApplication=application as AppointmentApplication
        mainThreadHandler = Handler(applicationContext.mainLooper)
        setLayoutAndViewModel()
    }

    override fun onDestroy() {
        super.onDestroy()
        mainThreadHandler.removeCallbacksAndMessages(null)
    }

    protected fun setViewModelInstance(viewModelInstance: BaseViewModel): Unit {
        viewModel = viewModelInstance as T
        try {
            val binding = getMethodOnlyForName(
                Class.forName(dataBindingUtil),
                setContentViewMethodName
            ).invoke(null, this, layout)
            getMethodOnlyForName(binding.javaClass, bindingMethodName).invoke(
                binding,
                viewModel
            )
            lifecycle.addObserver(viewModel)
        } catch (e: Exception) {
            Log.e(
                "Warning",
                "Annotation for viewmodel is present, but binding failed. Stack trace:"
            )
            e.printStackTrace()
        }
    }

    protected fun setLayoutInt(layoutVal: Int) {
        layout = layoutVal
        setContentView(layout)
    }

    protected fun startActivityForResultIfIntentReceivable(
        i: Intent,
        requestCode: Int
    ): Boolean {
        return if (i.resolveActivity(packageManager) != null) {
            startActivityForResult(i, requestCode)
            true
        } else {
            false
        }
    }

    protected fun showMessageDialog(
        title: String?,
        message: String?,
        button: String? /*Drawable icon*/
    ) {
        val alertDialog1 =
            AlertDialog.Builder(
                this
            ).create()
        alertDialog1.setTitle(title)
        alertDialog1.setMessage(message)
        alertDialog1.setButton(
            DialogInterface.BUTTON_NEUTRAL, button
        ) { dialog: DialogInterface, which: Int -> dialog.dismiss() }
        alertDialog1.show()
    }

    protected fun showMessageDialog(
        @StringRes titleR: Int,
        @StringRes messageR: Int,
        @StringRes buttonR: Int /*,Drawable icon*/
    ) {
        val title: String
        val message: String
        val button: String
        title = resources.getString(titleR)
        message = resources.getString(messageR)
        button = resources.getString(buttonR)
        showMessageDialog(title, message, button)
    }

    @Deprecated("Use rx instead")
    protected fun runOnMainThread(runnable: ()->Unit) {
        mainThreadHandler.post(runnable)
    }

    //endregion
    //region private helpers
    private fun setLayoutAndViewModel() {
        val annotations = annotationsOfClass
        setLayoutInt(getLayoutInt(annotations))
        setViewModelInstance(ViewModelProvider(this)[getAnnotatedVmClass(annotations)!!.java])
    }

    @Throws(NoSuchMethodException::class)
    private fun getMethodOnlyForName(
        c: Class<*>,
        name: String
    ): Method {
        val methods = c.methods
        for (m in methods) {
            if (m.name == name) {
                return m
            }
        }
        throw NoSuchMethodException("Method " + name + " in class " + c.name + " was not found!")
    }

    private val annotationsOfClass: Array<Annotation>
        private get() = this.javaClass.annotations

    private fun getAnnotatedVmClass(annotations: Array<Annotation>): KClass<out BaseViewModel> {
        for (a in annotations) {
            if (a is DeclareViewModel) {
                return a.value
            }
        }
        throw NotAnnotatedException(
                String.format(
                    errorTemplate,
                    DeclareXmlLayout::class.java.getName()
                )
                )
    }

    private fun getLayoutInt(annotations: Array<Annotation>): Int {
        for (a in annotations) {
            if (a is DeclareXmlLayout) {
                return a.value
            }
        }
        throw NotAnnotatedException(
            String.format(
                errorTemplate,
                DeclareXmlLayout::class.java.getName()
            )
        )
    } //endregion


}