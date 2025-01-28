package com.dicoding.picodiploma.loginwithanimation.view.main

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Looper
import android.view.View
import android.widget.CheckBox
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresPermission
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.constraintlayout.motion.widget.Debug.getLocation
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.text.HtmlCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.dicoding.picodiploma.loginwithanimation.R
import com.dicoding.picodiploma.loginwithanimation.data.Result
import com.dicoding.picodiploma.loginwithanimation.data.response.PostStoryResponse
import com.dicoding.picodiploma.loginwithanimation.databinding.ActivityCreateBinding
import com.dicoding.picodiploma.loginwithanimation.utils.reduceFileImage
import com.dicoding.picodiploma.loginwithanimation.utils.uriToFile
import com.dicoding.picodiploma.loginwithanimation.view.ViewModelFactory
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.tasks.await
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

@Suppress("DEPRECATION")
class CreateActivity : AppCompatActivity() {

    companion object {
        private const val REQUEST_CAMERA_PERMISSION = 100
        private const val REQUEST_LOCATION_PERMISSION = 101
    }

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var binding: ActivityCreateBinding
    private lateinit var createViewModel: CreateActivityViewModel
    private lateinit var addLocationCheckBox: CheckBox
    private var selectedImageFile: File? = null
    private var currentPhotoUri: Uri? = null

    private val launcherGallery = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            val myFile = uriToFile(it, this)
            selectedImageFile = myFile
            binding.ivImagePreview.setImageURI(it)
        }
    }

    private val launcherCamera = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            currentPhotoUri?.let { uri ->
                contentResolver.openInputStream(uri)?.use { inputStream ->
                    val tempFile = File(cacheDir, "${System.currentTimeMillis()}.jpg")
                    tempFile.outputStream().use { outputStream ->
                        inputStream.copyTo(outputStream)
                    }
                    if (tempFile.exists()) {
                        selectedImageFile = tempFile
                        Glide.with(this)
                            .load(tempFile)
                            .into(binding.ivImagePreview)
                    } else {
                        showToast(getString(R.string.image_file_not_found))
                    }
                } ?: showToast(getString(R.string.failed_to_read_image_uri))
            }
        } else {
            showToast(getString(R.string.failed_to_capture_image))
        }
    }

    private fun checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                REQUEST_LOCATION_PERMISSION
            )
        } else {
            getLocation()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val toolbar: Toolbar = findViewById(R.id.toolbar_create)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = getString(R.string.create_story)
        supportActionBar?.title = HtmlCompat.fromHtml("<font color='#FFFFFF'>" + getString(R.string.create_story) + "</font>", HtmlCompat.FROM_HTML_MODE_LEGACY)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        addLocationCheckBox = findViewById(R.id.cb_add_location)
        addLocationCheckBox.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                checkLocationPermission()
            }
        }

        toolbar.navigationIcon?.setTint(ContextCompat.getColor(this, android.R.color.white))
        toolbar.setNavigationOnClickListener {
            onBackPressed()
        }

        val factory = ViewModelFactory.getInstance(applicationContext)
        createViewModel = ViewModelProvider(this, factory)[CreateActivityViewModel::class.java]
        binding.btOpenCamera.setOnClickListener { checkCameraPermission() }
        binding.btOpenGallery.setOnClickListener { openGallery() }
        binding.buttonAdd.setOnClickListener { uploadImage() }
    }

    private fun checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
            != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.CAMERA),
                REQUEST_CAMERA_PERMISSION)
        } else {
            openCamera()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            REQUEST_CAMERA_PERMISSION -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openCamera()
                } else {
                    showToast(getString(R.string.camera_permission_required))
                }
            }
            REQUEST_LOCATION_PERMISSION -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getLocation()
                } else {
                    showToast(getString(R.string.location_permission_required))
                }
            }
        }
    }

    private fun openGallery() {
        launcherGallery.launch("image/*")
    }

    private fun openCamera() {
        val photoFile = File(cacheDir, "${System.currentTimeMillis()}.jpg")
        photoFile.createNewFile()
        photoFile.deleteOnExit()
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION)

        currentPhotoUri = FileProvider.getUriForFile(this, "${packageName}.fileprovider", photoFile)
        if (currentPhotoUri != null) {
            launcherCamera.launch(currentPhotoUri!!)
        } else {
            showToast(getString(R.string.failed_to_create_uri))
        }
    }

    @SuppressLint("MissingPermission")
    private fun uploadImage() {
        if (selectedImageFile != null) {
            showLoading(true)
            lifecycleScope.launch {
                try {
                    val file = reduceFileImage(selectedImageFile as File, this@CreateActivity)
                    val descriptionText = binding.edAddDescription.text
                    if (!descriptionText.isNullOrEmpty()) {
                        val description = descriptionText.toString()
                            .toRequestBody("text/plain".toMediaType())
                        val requestFile = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
                        val body = MultipartBody.Part.createFormData("photo", file.name, requestFile)

                        var lat: RequestBody? = null
                        var lon: RequestBody? = null

                        if (addLocationCheckBox.isChecked) {
                            if (isLocationPermissionGranted()) {
                                val location = getCurrentLocationAsync()
                                if (location != null) {
                                    lat = location.latitude.toString().toRequestBody("text/plain".toMediaType())
                                    lon = location.longitude.toString().toRequestBody("text/plain".toMediaType())
                                } else {
                                    showToast(getString(R.string.location_not_found))
                                }
                            } else {
                                requestLocationPermission()
                            }
                        }

                        createViewModel.postStory(body, description, lat, lon)
                            .observe(this@CreateActivity) { handlePostStoryResult(it) }
                    } else {
                        showLoading(false)
                        showToast(getString(R.string.enter_description))
                    }
                } catch (e: Exception) {
                    showLoading(false)
                    showToast("Error: ${e.message}")
                }
            }
        } else {
            showToast(getString(R.string.select_image_file))
        }
    }

    private fun isLocationPermissionGranted(): Boolean {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                REQUEST_LOCATION_PERMISSION
            )
        } else {
            getLocation()
        }
    }

    private fun handlePostStoryResult(result: Result<PostStoryResponse>) {
        when (result) {
            is Result.Success -> {
                showLoading(false)
                showToast(result.data.message)
                val intent = Intent(this, MainActivity::class.java).apply {
                    putExtra("isFromCreateActivity", true)
                }
                startActivity(intent)
                finish()
            }
            is Result.Loading -> showLoading(true)
            is Result.Error -> {
                showLoading(false)
                showToast(result.error)
            }
        }
    }

    @RequiresPermission(Manifest.permission.ACCESS_FINE_LOCATION)
    private suspend fun getCurrentLocationAsync(): android.location.Location? {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            return try {
                fusedLocationClient.lastLocation.await() ?: requestNewLocationData()
            } catch (e: Exception) {
                showToast(getString(R.string.failed_to_get_location, e.message))
                null
            }
        } else {
            showToast(getString(R.string.location_permission_required))
            return null
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @RequiresPermission(allOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
    private suspend fun requestNewLocationData(): android.location.Location? {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            showToast(getString(R.string.location_permission_not_granted))
            return null
        }

        val locationRequest = com.google.android.gms.location.LocationRequest.Builder(
            com.google.android.gms.location.Priority.PRIORITY_HIGH_ACCURACY, 10000
        ).setMinUpdateIntervalMillis(5000)
            .setMaxUpdates(1)
            .build()

        return try {
            suspendCancellableCoroutine { continuation ->
                val locationCallback = object : com.google.android.gms.location.LocationCallback() {
                    override fun onLocationResult(locationResult: com.google.android.gms.location.LocationResult) {
                        val location = locationResult.locations.firstOrNull()
                        continuation.resume(location) {}
                        fusedLocationClient.removeLocationUpdates(this)
                    }

                    override fun onLocationAvailability(locationAvailability: com.google.android.gms.location.LocationAvailability) {
                        if (!locationAvailability.isLocationAvailable) {
                            continuation.resume(null) {}
                        }
                    }
                }

                fusedLocationClient.requestLocationUpdates(
                    locationRequest,
                    locationCallback,
                    Looper.getMainLooper()
                ).addOnFailureListener { e ->
                    continuation.resume(null) {}
                    showToast(getString(R.string.failed_to_start_location_updates, e.message))
                }

                continuation.invokeOnCancellation {
                    fusedLocationClient.removeLocationUpdates(locationCallback)
                }
            }
        } catch (e: SecurityException) {
            showToast(getString(R.string.location_permission_not_available, e.message))
            null
        } catch (e: Exception) {
            showToast(getString(R.string.failed_to_get_new_location, e.message))
            null
        }
    }

    private fun showLoading(state: Boolean) {
        binding.pbCreateStory.visibility = if (state) View.VISIBLE else View.GONE
        binding.edAddDescription.visibility = if (state) View.INVISIBLE else View.VISIBLE
        binding.ivImagePreview.visibility = if (state) View.INVISIBLE else View.VISIBLE
        binding.btOpenCamera.visibility = if (state) View.INVISIBLE else View.VISIBLE
        binding.btOpenGallery.visibility = if (state) View.INVISIBLE else View.VISIBLE
        binding.buttonAdd.visibility = if (state) View.INVISIBLE else View.VISIBLE
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}