package com.CDIOgroup18.cameraxapp

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Resources
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.util.Size
import android.view.ViewGroup
import android.view.Window
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_take_photo.*
import java.io.File
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors


class TakePhotoActivity : AppCompatActivity() {

    companion object {
        const val FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"
        const val REQUEST_CODE_PERMISSIONS = 10
        val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
    }

    private lateinit var status: String
    private var imageCapture: ImageCapture? = null
    private var savedUri: String? = null
    private lateinit var outputDirectory: File
    private lateinit var cameraExecutor: ExecutorService


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        this.requestWindowFeature(Window.FEATURE_NO_TITLE)

        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }

        setContentView(R.layout.activity_take_photo)

        // Request camera permissions
        if (allPermissionsGranted()) {
            startCamera()
        } else {
            ActivityCompat.requestPermissions(
                this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS
            )
        }

        // Set up the listener for take photo button

       take_photo_button.setOnClickListener { takePhotoGoToValidate() }
       to_menu_button.setOnClickListener { backToMenu() }

        //inform user of status
        var ourToastMessage : String

        status = "noStatusIntent"
        if (intent.getStringExtra("status") != null) {
        status = intent.getStringExtra("status")!!
        }

        when (status) {
            "resumed" -> {
                ourToastMessage = "Please take photo to start game"
            }
            "nextMove" -> {
                ourToastMessage = "Ready for next photo"
            }
            "userDeclined" -> {
                ourToastMessage = "You declined the move suggested by server, please send new image"
            }
            "noStatusIntent" -> {
                ourToastMessage = "no status"
            }
            "bad_image" -> {
                ourToastMessage = "Please take new photo \n " +
                        "Make sure to align cards \n " +
                        "And keep a right angle over cards "
            }
            "fromValidate" -> {
                ourToastMessage = "Not happy with the photo? \n " +
                        "Please take a new one"
            }
            "internal_server_error" -> {
                ourToastMessage = "Error at the server \n " +
                        "If problems continue contact administrator"
            }
            else -> {
                ourToastMessage = "no status intent read"
            }

        }

        Toast.makeText(this, ourToastMessage, Toast.LENGTH_LONG).show()


        outputDirectory = getOutputDirectory()
        cameraExecutor = Executors.newSingleThreadExecutor()

        setupTheScreen()

    }


    private fun setupTheScreen() {
        val screenWidth = Resources.getSystem().displayMetrics.widthPixels
        val screenHeight = Resources.getSystem().displayMetrics.heightPixels

        val paramVF = viewFinder.layoutParams as ViewGroup.MarginLayoutParams

        val viewFinderHeight = screenHeight
        val viewFinderWidth = screenWidth
        val diffScreenVSviewFinder = screenWidth - viewFinderWidth

        val startX = diffScreenVSviewFinder/2
        val startY = 0
/*
        val startX = (screenWidth * 0.1).roundToInt()
        val startY = (screeHeight * 0.0025).roundToInt()
        val viewFinderWidth = (screenWidth * 0.8).roundToInt()
        val viewFinderHeight = (screeHeight * 0.8).roundToInt()
*/
        paramVF.leftMargin = startX
        paramVF.topMargin = startY
        paramVF.height = viewFinderHeight
        paramVF.width = viewFinderWidth

        viewFinder.layoutParams = paramVF

        val noOfColumns = 7
        val distanceBetweenVerticalLines = (viewFinderWidth / noOfColumns)

        val param1 = fromLeft1.layoutParams as ViewGroup.MarginLayoutParams
        param1.marginStart = startX + distanceBetweenVerticalLines * 1
        param1.topMargin = startY
        param1.height = viewFinderHeight

        val param2 = fromLeft2.layoutParams as ViewGroup.MarginLayoutParams
        param2.marginStart = startX + distanceBetweenVerticalLines * 2
        param2.topMargin = startY
        param2.height = viewFinderHeight

        val param3 = fromLeft3.layoutParams as ViewGroup.MarginLayoutParams
        param3.marginStart = startX + distanceBetweenVerticalLines * 3
        param3.topMargin = startY
        param3.height = viewFinderHeight

        val param4 = fromLeft4.layoutParams as ViewGroup.MarginLayoutParams
        param4.marginStart = startX + distanceBetweenVerticalLines * 4
        param4.topMargin = startY
        param4.height = viewFinderHeight

        val param5 = fromLeft5.layoutParams as ViewGroup.MarginLayoutParams
        param5.marginStart = startX + distanceBetweenVerticalLines * 5
        param5.topMargin = startY
        param5.height = viewFinderHeight

        val param6 = fromLeft6.layoutParams as ViewGroup.MarginLayoutParams
        param6.marginStart = startX + distanceBetweenVerticalLines * 6
        param6.topMargin = startY
        param6.height = viewFinderHeight

        fromLeft1.layoutParams = param1
        fromLeft2.layoutParams = param2
        fromLeft3.layoutParams = param3
        fromLeft4.layoutParams = param4
        fromLeft5.layoutParams = param5
        fromLeft6.layoutParams = param6

        val paramHorizontalLine = horizontalLine.layoutParams as ViewGroup.MarginLayoutParams
        paramHorizontalLine.topMargin = startY + viewFinderHeight/2
        paramHorizontalLine.leftMargin =startX
        paramHorizontalLine.width = viewFinderWidth
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
        val photoFile = File(outputDirectory, "aPhoto.jpg")

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
                    val msg =
                        "Photo capture succeeded. \n Press \"GO\" to send image to server or \"UNDO\" to take a new one"
                    Toast.makeText(baseContext, msg, Toast.LENGTH_LONG).show()
                    Log.d(MainActivity.TAG, msg)

                   // goToValidate()
                    goToAlternativeValidate()
                }

            })

    }

    private fun goToAlternativeValidate() {
        intent = Intent(this, ValidateActivity()::class.java)
        intent.putExtra("imagePath", savedUri)
        intent.putExtra("outputD", outputDirectory)
        startActivity(intent)
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

            imageCapture =
                ImageCapture.Builder().setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY).
                   // setTargetAspectRatio(aspectRatio).
                setTargetResolution(Size(Resources.getSystem().displayMetrics.widthPixels, Resources.getSystem().displayMetrics.heightPixels)).
                 build()

            // Select back camera as a default
            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                // Unbind use cases before rebinding
                cameraProvider.unbindAll()

                // Bind use cases to camera
                cameraProvider.bindToLifecycle(
                    this, cameraSelector, preview, imageCapture
                )

            } catch (exc: Exception) {
                Log.e(MainActivity.TAG, "Use case binding failed", exc)
            }

        }, ContextCompat.getMainExecutor(this))
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(
            baseContext, it
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun getOutputDirectory(): File {
        val mediaDir = externalMediaDirs.firstOrNull()?.let {
            File(it, resources.getString(R.string.app_name)).apply { mkdirs() }
        }
        return if (mediaDir != null && mediaDir.exists())
            mediaDir else filesDir
    }


    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>, grantResults:
        IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                startCamera()
            } else {
                Toast.makeText(
                    this,
                    "Permissions not granted by the user.",
                    Toast.LENGTH_SHORT
                ).show()
                finish()
            }
        }


    }

}