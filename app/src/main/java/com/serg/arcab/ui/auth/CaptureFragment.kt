package com.serg.arcab.ui.auth


import android.Manifest
import android.content.pm.PackageManager
import android.graphics.*
import android.os.Bundle
import android.os.Handler
import android.support.v4.app.ActivityCompat
import android.support.v4.app.Fragment
import android.view.*
import com.google.android.gms.vision.CameraSource
import com.google.android.gms.vision.Detector
import com.google.android.gms.vision.Tracker
import com.google.android.gms.vision.face.Face
import com.google.android.gms.vision.face.FaceDetector
import com.google.android.gms.vision.face.LargestFaceFocusingProcessor
import com.google.android.gms.vision.text.TextBlock
import com.google.android.gms.vision.text.TextRecognizer
import com.serg.arcab.R
import kotlinx.android.synthetic.main.fragment_capture.*
import org.koin.android.architecture.ext.sharedViewModel
import timber.log.Timber
import java.io.ByteArrayOutputStream

class CaptureFragment : Fragment() {

    private val viewModel by sharedViewModel<AuthViewModel>()
    private val requestPermissionCode = 1001

    private var cameraSource: CameraSource? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_capture, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        when (viewModel.captureMode) {
            AuthViewModel.CaptureMode.FRONT -> recognizeFace()
            AuthViewModel.CaptureMode.BACK -> recognizeId()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == requestPermissionCode && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            if (ActivityCompat.checkSelfPermission(context!!, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
                return
            }
            cameraSource?.start(camera.holder)
        }
    }

    private fun recognizeId(){
        val textRecognizer = TextRecognizer.Builder(context).build()
        if (textRecognizer.isOperational){
            cameraSource = CameraSource.Builder(context, textRecognizer)
                    .setFacing(CameraSource.CAMERA_FACING_BACK)
                    .setRequestedPreviewSize(1280, 1024)
                    .setRequestedFps(2.0f)
                    .setAutoFocusEnabled(true)
                    .build()
            camera.holder.addCallback(object : SurfaceHolder.Callback{
                override fun surfaceChanged(holder: SurfaceHolder?, format: Int, width: Int, height: Int) {

                }

                override fun surfaceDestroyed(holder: SurfaceHolder?) {
                    cameraSource?.stop()
                }

                override fun surfaceCreated(holder: SurfaceHolder?) {
                    if (ActivityCompat.checkSelfPermission(context!!, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
                        ActivityCompat.requestPermissions(activity!!, arrayOf(Manifest.permission.CAMERA), requestPermissionCode)
                        return
                    }
                    cameraSource?.start(camera.holder)
                }
            })

            textRecognizer.setProcessor(object : Detector.Processor<TextBlock>{
                override fun release() {

                }

                override fun receiveDetections(p0: Detector.Detections<TextBlock>?) {
                    val items = p0?.detectedItems
                    if (items?.size() != 0){
                        var result = ""
                        for (i in 0 until items!!.size()){
                            result += items.valueAt(i).value
                            Timber.d("TEXTRECOGNITION temp result: $result")
                        }
                        if (result.contains("ID") && result.contains("ARE")){
                            textRecognizer.release()
                            Timber.d("TEXTRECOGNITION result: $result")
                            val id = result.substring(result.indexOf("IDARE"), result.indexOf("<")).replace(" ", "")
                            Timber.d("TEXTRECOGNITION id: $id")

                            Handler(activity!!.mainLooper).post {
                                viewModel.emiratesId.value = id
                                cameraSource?.takePicture({ }, {
                                    var bitmap = BitmapFactory.decodeByteArray(it, 0, it.size)
                                    val matrix = Matrix()
                                    matrix.postRotate(90f)
                                    bitmap = Bitmap.createBitmap(bitmap, (bitmap.width/9) * 2, 0, (bitmap.width/9)*5, bitmap.height)
                                    bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
                                    val stream = ByteArrayOutputStream()
                                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
                                    viewModel.backCapture = stream.toByteArray()
                                    bitmap.recycle()

                                    cameraSource?.stop()
                                    viewModel.onBackClicked()
                                })
                            }
                        }
                    }
                }
            })
        }
    }

    private fun recognizeFace(){
        val detector = FaceDetector.Builder(context)
                .setProminentFaceOnly(true)
                .build()
        cameraSource = CameraSource.Builder(context, detector)
                .setFacing(CameraSource.CAMERA_FACING_BACK)
                .setRequestedPreviewSize(320, 240)
                .setRequestedFps(2.0f)
                .setAutoFocusEnabled(true)
                .build()
        camera.holder.addCallback(object : SurfaceHolder.Callback{
            override fun surfaceChanged(holder: SurfaceHolder?, format: Int, width: Int, height: Int) {

            }

            override fun surfaceDestroyed(holder: SurfaceHolder?) {
                cameraSource?.stop()
            }

            override fun surfaceCreated(holder: SurfaceHolder?) {
                if (ActivityCompat.checkSelfPermission(context!!, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(activity!!, arrayOf(Manifest.permission.CAMERA), requestPermissionCode)
                    return
                }
                cameraSource?.start(camera.holder)
            }
        })
        if (detector.isOperational){
            detector.setProcessor(LargestFaceFocusingProcessor(detector, object : Tracker<Face>() {
                override fun onNewItem(p0: Int, p1: Face?) {
                    super.onNewItem(p0, p1)
                    Handler(activity!!.mainLooper).post {
                        cameraSource?.takePicture({ }, {
                            var bitmap = BitmapFactory.decodeByteArray(it, 0, it.size)
                            val matrix = Matrix()
                            matrix.postRotate(90f)
                            bitmap = Bitmap.createBitmap(bitmap, (bitmap.width/9) * 2, 0, (bitmap.width/9)*5, bitmap.height)
                            bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
                            val stream = ByteArrayOutputStream()
                            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
                            viewModel.frontCapture = stream.toByteArray()
                            bitmap.recycle()

                            cameraSource?.stop()
                            viewModel.onBackClicked()
                        })
                    }
                }
            }))
        }
    }


    companion object {
        const val TAG = "capture"

        fun newInstance() = CaptureFragment()
    }

}
