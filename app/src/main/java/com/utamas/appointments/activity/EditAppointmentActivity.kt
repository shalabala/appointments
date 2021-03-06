package com.utamas.appointments.activity

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.google.android.material.chip.ChipGroup
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.timepicker.MaterialTimePicker
import com.utamas.appointments.APPOINTMENT_ITEM
import com.utamas.appointments.R
import com.utamas.appointments.activity.dialog.ImageDialog
import com.utamas.appointments.activity.dialog.NoteDialog
import com.utamas.appointments.activity.dialog.PersonDialog
import com.utamas.appointments.architecture.abstractions.BaseActivity
import com.utamas.appointments.architecture.abstractions.ImageUtils
import com.utamas.appointments.architecture.annotations.DeclareViewModel
import com.utamas.appointments.architecture.annotations.DeclareXmlLayout
import com.utamas.appointments.architecture.bining.ChipGroupListObserver
import com.utamas.appointments.viewmodel.EditAppointmentViewModel
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers
import java.io.File
import java.io.IOException
import java.time.Instant
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.util.*
import javax.inject.Inject


@DeclareXmlLayout(R.layout.activity_edit_appointment)
@DeclareViewModel(EditAppointmentViewModel::class)
class EditAppointmentActivity : BaseActivity<EditAppointmentViewModel>() {

    private val edit = false
    private val CAMERA_REQUEST_CODE = 3
    private val GALLERY_REQUEST_CODE = 5
    private val STORAGE_REQUEST_CODE = 2
    private val FILE_PROVIDER_AUTHORITY_STRING = "com.utamas.appointments.android.fileprovider"

    private val STORAGE_PERMISSIONS = arrayOf(
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.READ_EXTERNAL_STORAGE
    )

    @Inject
    lateinit var imageUtils: ImageUtils


    private lateinit var personChipGroup: ChipGroup
    private lateinit var notesChipGroup: ChipGroup
    private lateinit var dateTextField: TextInputEditText
    private lateinit var timeTextField: TextInputEditText
    private lateinit var imageTextView: TextView

    private var storagePermissionAsked = false
    private lateinit var imageCacheFile: File

    //region inherited
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setUpToolbar(findViewById(R.id.toolbar2))

        val id = intent.getStringExtra(APPOINTMENT_ITEM)
        if (id != null) {
            viewModel.setAppointment(id, {}, {
                showLongToast(R.string.data_loading_error)
            }, { showLongToast(R.string.image_conversion_error) })
        }
        appointmentApplication.appComponent.inject(this)
        personChipGroup = findViewById(R.id.contactsChipGroup)
        notesChipGroup = findViewById(R.id.chipGroupNotes)
        dateTextField = findViewById(R.id.dateTextInputEditText)
        timeTextField = findViewById(R.id.timeTextInputEditText)
        imageTextView = findViewById(R.id.imageTextView)

        performManualBindings()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {

            when (requestCode) {
                GALLERY_REQUEST_CODE -> handleGalleryRequestResponse(data)
                CAMERA_REQUEST_CODE -> handleCameraRequestResponse()
            }

        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == STORAGE_REQUEST_CODE) {
            if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getImage()
            } else {
                Toast.makeText(
                    this, resources.getString(R.string.storage_permission_gallery_error),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
    //endregion

    //region toolbaar
    private fun setUpToolbar(t: Toolbar) {

        t.inflateMenu(R.menu.menu_on_edit)
        t.menu.findItem(R.id.save).setOnMenuItemClickListener { save() }

        t.title =
            if (edit) resources.getString(R.string.edit) else resources.getString(R.string.new_appointment)
        t.setNavigationIcon(R.drawable.outline_arrow_back_24)
        t.setNavigationOnClickListener { finish() }
    }

    private fun save(): Boolean {
        if (viewModel.isValid()) {
            viewModel.save({
                finish()
            }, {
                Toast.makeText(this, resources.getString(R.string.save_error), Toast.LENGTH_LONG)
                    .show()
                it.printStackTrace()
            })
        } else {
            showLongToast(R.string.edit_activity_validation_message)
        }
        return true
    }

    //endregion

    //region data binding
    private fun performManualBindings() {
        viewModel.apply {
            contacts.addOnListChangedCallback(
                ChipGroupListObserver(
                    this@EditAppointmentActivity,
                    viewModel.contacts,
                    personChipGroup,
                    { it.name })
            )
            notes.addOnListChangedCallback(
                ChipGroupListObserver(
                    this@EditAppointmentActivity,
                    viewModel.notes,
                    notesChipGroup
                )
            )
        }
        dateTextField.apply {
            setOnClickListener {
                openDatePicker()
                true
            }
            setOnFocusChangeListener { v, hasFocus ->
                if (hasFocus) {
                    openDatePicker()
                }
            }

        }
        timeTextField.apply {
            setOnClickListener {
                openTimePicker()
                true
            }
            setOnFocusChangeListener { v, hasFocus ->
                if (hasFocus) {
                    openTimePicker()
                }
            }
        }
    }


//endregion

    //region chips
    fun addPerson(v: View) {
        PersonDialog(viewModel.contacts).show(supportFragmentManager, "personTag")
    }

    fun addNote(v: View) {
        NoteDialog(viewModel.notes).show(supportFragmentManager, "noteTag")
    }

//endregion


    //region image selection
    fun getImage(v: View? = null) {
        val permission = checkStoragePermission()
        if (permission || storagePermissionAsked) {
            ImageDialog(permission, this::imageDialogCallback).show(
                supportFragmentManager,
                "choosepic"
            )
        } else {
            tryGetStoragePermission()
        }
    }

    private fun checkStoragePermission(): Boolean {
        return STORAGE_PERMISSIONS.all {
            ContextCompat.checkSelfPermission(
                this,
                it
            ) == PackageManager.PERMISSION_GRANTED
        }
    }

    private fun tryGetStoragePermission() {
        ActivityCompat.requestPermissions(
            this,
            STORAGE_PERMISSIONS,
            STORAGE_REQUEST_CODE
        )
    }

    private fun imageDialogCallback(option: ImageDialog.Options) {
        when (option) {
            ImageDialog.Options.CAMERA -> startCameraIntent()
            ImageDialog.Options.GALLERY -> startGalleryIntent()
        }
    }

    private fun startGalleryIntent() {
        val galleryIntent = Intent(
            Intent.ACTION_PICK,
            android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        )
        if (!startActivityForResultIfIntentReceivable(galleryIntent, GALLERY_REQUEST_CODE)) {
            Toast.makeText(this, resources.getString(R.string.no_gallery_error), Toast.LENGTH_LONG)
                .show()
        }

    }

    private fun startCameraIntent() {
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        try {
            imageCacheFile = createImageFile()
            val photoURI =
                FileProvider.getUriForFile(this, FILE_PROVIDER_AUTHORITY_STRING, imageCacheFile)
            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
            if (!startActivityForResultIfIntentReceivable(cameraIntent, CAMERA_REQUEST_CODE)) {
                Toast.makeText(
                    this,
                    resources.getString(R.string.no_camera_error),
                    Toast.LENGTH_LONG
                ).show()
            }
        } catch (ex: IOException) {
            Toast.makeText(this, resources.getString(R.string.io_error), Toast.LENGTH_LONG).show()
        }
    }

    private fun handleCameraRequestResponse() {
        setImage(imageCacheFile)
    }

    private fun handleGalleryRequestResponse(data: Intent?) {
        if (data == null) return
        val image = getGalleryPath(data).map { File(it) }
        if (image.isPresent) {
            setImage(image.get())
        }
    }

    private fun setImage(imageFile: File) {
        imageUtils.processIntentImage(imageFile).subscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { viewModel.image.set(it) },
                {
                    Toast.makeText(
                        this,
                        resources.getString(R.string.image_fetch_failed),
                        Toast.LENGTH_LONG
                    ).show()
                    it.printStackTrace()
                })

    }


    private fun getGalleryPath(data: Intent): Optional<String> {
        val uri = data.data
        val filePathColumn = arrayOf(MediaStore.Images.Media.DATA)
        if (uri != null) {
            val cursor = contentResolver.query(uri, filePathColumn, null, null, null)
            if (cursor != null) {
                cursor.moveToFirst()
                val colIndex = cursor.getColumnIndex(filePathColumn[0])
                return Optional.of(cursor.getString(colIndex))
            }
        }
        return Optional.empty()
    }


    @Throws(IOException::class)
    private fun createImageFile(): File {
        val uuid = UUID.randomUUID().toString()
        val imageFileName = "JPEG_" + uuid + "_"
        val storageDir = externalCacheDir
        return File.createTempFile(
            imageFileName,
            ".jpg",
            storageDir
        )
    }
//endregion

    //region date time

    fun openDatePicker(v: View? = null) {

        val datePicker =
            MaterialDatePicker.Builder.datePicker()
                .setTitleText(resources.getString(R.string.pick_a_date))
                .setSelection(
                    viewModel.validFor.atZone(ZoneId.systemDefault()).toInstant()
                        .toEpochMilli()
                )
                .setCalendarConstraints(
                    CalendarConstraints.Builder()
                        .setStart(MaterialDatePicker.todayInUtcMilliseconds()).build()
                )
                .build()

        datePicker.addOnPositiveButtonClickListener {
            val date = Instant.ofEpochMilli(it).atZone(ZoneId.systemDefault()).toLocalDate()
            val time = viewModel.validFor.toLocalTime()
            viewModel.validFor = LocalDateTime.of(date, time)
        }
        datePicker.show(supportFragmentManager, "dateTag")
    }

    fun openTimePicker(v: View? = null) {
        val timePicker =
            MaterialTimePicker.Builder()
                .setTitleText(resources.getString(R.string.pick_a_time))
                .setHour(viewModel.validFor.hour)
                .setMinute(viewModel.validFor.minute)
                .build()

        timePicker.addOnPositiveButtonClickListener {
            val date = viewModel.validFor.toLocalDate()
            val time = LocalTime.of(timePicker.hour, timePicker.minute)
            viewModel.validFor = LocalDateTime.of(date, time)
        }
        timePicker.show(supportFragmentManager, "timeTag")
    }
//endregion

}

