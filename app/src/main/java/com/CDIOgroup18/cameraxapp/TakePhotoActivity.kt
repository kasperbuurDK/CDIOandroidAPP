package com.CDIOgroup18.cameraxapp


import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.util.Size
import android.view.ViewGroup
import android.widget.Toast
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.doOnLayout
import kotlinx.android.synthetic.main.activity_take_photo.*
import java.io.File
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class TakePhotoActivity : AppCompatActivity() {

    private var status: String = "No statusIntent"
    private var imageCapture: ImageCapture? = null
    private var savedUri: String? = null
    private lateinit var outputDirectory: File
    private lateinit var cameraExecutor: ExecutorService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_take_photo)

        // Request camera permissions
        if (allPermissionsGranted()) {
            startCamera()
        } else {
            ActivityCompat.requestPermissions(
                this, MainActivity.REQUIRED_PERMISSIONS, MainActivity.REQUEST_CODE_PERMISSIONS)
        }

        // Set up the listener for take photo button

        take_photo_button.setOnClickListener { takePhotoGoToValidate() }
        to_menu_button.setOnClickListener { backToMenu() }

        //inform user of status
        var ourToastMessage = ""
        status = if (intent.getStringExtra("status") != null) ({
        }).toString() else "firstPhoto"

            when (status) {
                "resumed" -> {
                    ourToastMessage = "Just resumed with no statusIntent"
                }
                "nextMove" -> {
                    ourToastMessage = "Ready for next photo"
                }
                "no statusIntent" -> {
                    ourToastMessage = "no status"
                }
                "firstPhoto" -> {
                    ourToastMessage = "Please take first photo to start game"
                }
            }

        Toast.makeText(this, ourToastMessage , Toast.LENGTH_SHORT).show()

        outputDirectory = getOutputDirectory()
        cameraExecutor = Executors.newSingleThreadExecutor()

        val noOfVerticalLines = 6
        var width : Int = 0
        var height : Int = 0

        viewFinder.doOnLayout {
          width =  it.measuredWidth
          height = it.measuredHeight
        }

        var distanceBetweenLines = (width/noOfVerticalLines) + 2000

        println("DEBUG __--__--__--  distanceBetweenLines = $distanceBetweenLines \n" +
                "width = $width \n " +
                "height = $height")

        var param = fromLeft1.layoutParams as ViewGroup.MarginLayoutParams
        param.leftMargin = distanceBetweenLines

        fromLeft1.setBackgroundColor(Color.BLUE)

        fromLeft1.layoutParams = param



    }

    private fun backToMenu() {
        intent = Intent(this, MainActivity::class.java)
        intent.putExtra("status", "fromTakePhoto")
        startActivity(intent)
    }

    override fun onRestart() {
        super.onRestart()
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onPause() {
        super.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }


    private fun takePhotoGoToValidate() {
        // Get a stable reference of the modifiable image capture use case
        val imageCapture = imageCapture ?: return

        // Create time-stamped output file to hold the image
        //val photoFile = File(outputDirectory,SimpleDateFormat(FILENAME_FORMAT, Locale.US
        //    ).format(System.currentTimeMillis()) + ".jpg")
        //val photoFile = File(outputDirectory,"aPhoto.jpg")
        val photoFile = File(outputDirectory,"aPhoto.jpg")

        // Create output options object which contains file + metadata
        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

        // Set up image capture listener, which is triggered after photo has
        // been taken
        imageCapture.takePicture(
            outputOptions,                                  //where to store the image
            ContextCompat.getMainExecutor(this),     //what executor to use
            object : ImageCapture.OnImageSavedCallback {
                override fun onError(exc: ImageCaptureException) {
                    Log.e(MainActivity.TAG, "Photo capture failed: ${exc.message}", exc)
                }

                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                    savedUri = Uri.fromFile(photoFile).toString()
                    val msg = "Photo capture succeeded: $savedUri"
                    Toast.makeText(baseContext, msg, Toast.LENGTH_SHORT).show()
                    Log.d(MainActivity.TAG, msg)

                    goToValidate()

                }

            })

    }

    private fun goToValidate() {
        intent = Intent(this, ValidateActivity::class.java)
        intent.putExtra("imagePath", savedUri)
        intent.putExtra("outputD", outputDirectory)
        startActivity(intent)
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener(Runnable {
            // Used to bind the lifecycle of cameras to the lifecycle owner
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

            // Preview
            val preview = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(viewFinder.surfaceProvider)
                }

            // set properties of cameracapture

            imageCapture = ImageCapture.Builder().
            setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY).
            setTargetResolution(Size(1600,1200)).
            build()

            // Select back camera as a default
            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                // Unbind use cases before rebinding
                cameraProvider.unbindAll()

                // Bind use cases to camera
                cameraProvider.bindToLifecycle(
                    this, cameraSelector, preview, imageCapture)

            } catch(exc: Exception) {
                Log.e(MainActivity.TAG, "Use case binding failed", exc)
            }

        }, ContextCompat.getMainExecutor(this))
    }

    private fun allPermissionsGranted() = MainActivity.REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(
            baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    private fun getOutputDirectory(): File {
        val mediaDir = externalMediaDirs.firstOrNull()?.let {
            File(it, resources.getString(R.string.app_name)).apply { mkdirs() } }
        return if (mediaDir != null && mediaDir.exists())
            mediaDir else filesDir
    }


    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>, grantResults:
        IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == MainActivity.REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                startCamera()
            } else {
                Toast.makeText(this,
                    "Permissions not granted by the user.",
                    Toast.LENGTH_SHORT).show()
                finish()
            }
        }


    }

    private fun switchToResponseActivity() {

        if (savedUri.equals(null)) {
            Toast.makeText(this,"No photo taken", Toast.LENGTH_SHORT).show()
        } else
        {
            intent = Intent(this, ResponseActivity2::class.java)
            intent.putExtra("imagePath", savedUri)
            startActivity(intent)

        }
    }

}