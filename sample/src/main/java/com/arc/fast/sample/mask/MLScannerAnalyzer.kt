package com.arc.fast.sample.mask

import android.graphics.*
import android.os.Handler
import android.os.Looper
import android.util.Size
import android.view.View
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.view.PreviewView
import com.google.android.gms.tasks.Task
import com.google.mlkit.vision.barcode.BarcodeScanner
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import java.io.ByteArrayOutputStream
import kotlin.math.roundToInt

class MLScannerAnalyzer(
    private val barcodeScannerOptions: BarcodeScannerOptions = BarcodeScannerOptions.Builder()
        .setBarcodeFormats(
            Barcode.FORMAT_QR_CODE
        ).build(),
    private val onScannerResult: (String) -> Unit
) : ImageAnalysis.Analyzer {
    private val mainHandler = Handler(Looper.getMainLooper())

    // 扫描器
    private val scanner: BarcodeScanner by lazy {
        BarcodeScanning.getClient(barcodeScannerOptions)
    }

    // 识别任务
    private var pendingTask: Task<out Any>? = null

    // 扫描裁剪区域
    private var previewSize: Size? = null
    private var scannerCropRect: Rect? = null
    private var onScannerCropResult: ((bitmap: Bitmap) -> Unit)? = null

    /**
     * 设置扫描裁剪区域
     */
    fun setScannerCropRect(
        previewSize: Size,
        scannerCropRect: Rect,
        onCropResult: ((bitmap: Bitmap) -> Unit)? = null
    ) {
        this.previewSize = previewSize
        this.scannerCropRect = scannerCropRect
        this.onScannerCropResult = onCropResult
    }

    /**
     * 设置扫描裁剪区域
     */
    fun setScannerCropRect(
        previewView: PreviewView,
        scannerRectView: View,
        onCropResult: ((bitmap: Bitmap) -> Unit)? = null
    ) {
        setScannerCropRect(
            Size(
                previewView.measuredWidth,
                previewView.measuredHeight
            ), Rect().let {
                scannerRectView.getGlobalVisibleRect(it)
                it
            }, onCropResult
        )
    }

    @androidx.camera.core.ExperimentalGetImage
    override fun analyze(imageProxy: ImageProxy) {
        if (previewSize == null || scannerCropRect == null) {
            // 按整个预览界面做扫描识别
            val mediaImage = imageProxy.image ?: return
            val image =
                InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
            pendingTask =
                scanner.process(image)
                    .addOnSuccessListener {
                        val result = it.joinToString { barcode -> barcode?.rawValue.toString() }
                        if (result.isNotBlank()) onScannerResult.invoke(result)
                    }
                    .addOnFailureListener {
                        // 扫描错误
                    }
                    .addOnCompleteListener {
                        imageProxy.close()
                    }
        } else {
            // 先对预览界面进行裁剪，再做扫描识别
            val rotation = imageProxy.imageInfo.rotationDegrees
            val bitmap = imageProxy.toBitmap()?.rotate(rotation.toFloat()) ?: return
            // 因为预览界面的大小与扫描帧bitmap的大小并不一致，所以需要计算出比例
            val bitmapRatio = bitmap.width.toDouble() / bitmap.height
            val previewRatio = previewSize!!.width.toDouble() / previewSize!!.height
            var cropLeft = 0.0
            var cropTop = 0.0
            val ratio: Double
            if (bitmapRatio > previewRatio) {
                // 实际预览宽度会被裁剪掉一部分，所以需要计算出被裁剪的大小
                ratio = bitmap.height.toDouble() / previewSize!!.height
                cropLeft = (previewSize!!.height * bitmapRatio - previewSize!!.width) / 2

            } else if (bitmapRatio < previewRatio) {
                // 实际预览高度会被裁剪掉一部分，所以需要计算出被裁剪的大小
                ratio = bitmap.width.toDouble() / previewSize!!.width
                cropTop = (previewSize!!.width / bitmapRatio - previewSize!!.height) / 2
            } else {
                ratio = bitmap.width.toDouble() / previewSize!!.width
            }
            // 因为预览界面的大小与扫描帧bitmap的大小并不一致，所以需要按比例重新计算裁剪的大小
            val left = (cropLeft + scannerCropRect!!.left) * ratio
            val top = (cropTop + scannerCropRect!!.top) * ratio
            val width = scannerCropRect!!.width() * ratio
            val height = scannerCropRect!!.height() * ratio
            val cropBitmap = Bitmap.createBitmap(
                bitmap,
                left.roundToInt(),
                top.roundToInt(),
                width.roundToInt(),
                height.roundToInt()
            )
            if (onScannerCropResult != null) mainHandler.post {
                onScannerCropResult?.invoke(cropBitmap)
            }
            pendingTask =
                scanner.process(cropBitmap.rotate(-rotation.toFloat()), rotation)
                    .addOnSuccessListener {
                        val result = it.joinToString { barcode -> barcode?.rawValue.toString() }
                        if (result.isNotBlank()) onScannerResult.invoke(result)
                    }
                    .addOnFailureListener {
                        // 扫描错误
                    }
                    .addOnCompleteListener {
                        imageProxy.close()
                    }
        }
    }
}

/**
 * 旋转bitmap
 */
fun Bitmap.rotate(degrees: Float): Bitmap = Bitmap.createBitmap(
    this,
    0,
    0,
    width,
    height,
    Matrix().apply { postRotate(degrees) },
    true
)

fun ImageProxy.toBitmap(): Bitmap? {
    val nv21 = yuv420888ToNv21(this)
    val yuvImage = YuvImage(nv21, ImageFormat.NV21, width, height, null)
    return yuvImage.toBitmap()
}

private fun YuvImage.toBitmap(): Bitmap? {
    val out = ByteArrayOutputStream()
    if (!compressToJpeg(Rect(0, 0, width, height), 100, out))
        return null
    val imageBytes: ByteArray = out.toByteArray()
    return BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
}

private fun yuv420888ToNv21(image: ImageProxy): ByteArray {
    val pixelCount = image.cropRect.width() * image.cropRect.height()
    val pixelSizeBits = ImageFormat.getBitsPerPixel(ImageFormat.YUV_420_888)
    val outputBuffer = ByteArray(pixelCount * pixelSizeBits / 8)
    imageToByteBuffer(image, outputBuffer, pixelCount)
    return outputBuffer
}

private fun imageToByteBuffer(image: ImageProxy, outputBuffer: ByteArray, pixelCount: Int) {
    assert(image.format == ImageFormat.YUV_420_888)

    val imageCrop = image.cropRect
    val imagePlanes = image.planes

    imagePlanes.forEachIndexed { planeIndex, plane ->
        // How many values are read in input for each output value written
        // Only the Y plane has a value for every pixel, U and V have half the resolution i.e.
        //
        // Y Plane            U Plane    V Plane
        // ===============    =======    =======
        // Y Y Y Y Y Y Y Y    U U U U    V V V V
        // Y Y Y Y Y Y Y Y    U U U U    V V V V
        // Y Y Y Y Y Y Y Y    U U U U    V V V V
        // Y Y Y Y Y Y Y Y    U U U U    V V V V
        // Y Y Y Y Y Y Y Y
        // Y Y Y Y Y Y Y Y
        // Y Y Y Y Y Y Y Y
        val outputStride: Int

        // The index in the output buffer the next value will be written at
        // For Y it's zero, for U and V we start at the end of Y and interleave them i.e.
        //
        // First chunk        Second chunk
        // ===============    ===============
        // Y Y Y Y Y Y Y Y    V U V U V U V U
        // Y Y Y Y Y Y Y Y    V U V U V U V U
        // Y Y Y Y Y Y Y Y    V U V U V U V U
        // Y Y Y Y Y Y Y Y    V U V U V U V U
        // Y Y Y Y Y Y Y Y
        // Y Y Y Y Y Y Y Y
        // Y Y Y Y Y Y Y Y
        var outputOffset: Int

        when (planeIndex) {
            0 -> {
                outputStride = 1
                outputOffset = 0
            }
            1 -> {
                outputStride = 2
                // For NV21 format, U is in odd-numbered indices
                outputOffset = pixelCount + 1
            }
            2 -> {
                outputStride = 2
                // For NV21 format, V is in even-numbered indices
                outputOffset = pixelCount
            }
            else -> {
                // Image contains more than 3 planes, something strange is going on
                return@forEachIndexed
            }
        }

        val planeBuffer = plane.buffer
        val rowStride = plane.rowStride
        val pixelStride = plane.pixelStride

        // We have to divide the width and height by two if it's not the Y plane
        val planeCrop = if (planeIndex == 0) {
            imageCrop
        } else {
            Rect(
                imageCrop.left / 2,
                imageCrop.top / 2,
                imageCrop.right / 2,
                imageCrop.bottom / 2
            )
        }

        val planeWidth = planeCrop.width()
        val planeHeight = planeCrop.height()

        // Intermediate buffer used to store the bytes of each row
        val rowBuffer = ByteArray(plane.rowStride)

        // Size of each row in bytes
        val rowLength = if (pixelStride == 1 && outputStride == 1) {
            planeWidth
        } else {
            // Take into account that the stride may include data from pixels other than this
            // particular plane and row, and that could be between pixels and not after every
            // pixel:
            //
            // |---- Pixel stride ----|                    Row ends here --> |
            // | Pixel 1 | Other Data | Pixel 2 | Other Data | ... | Pixel N |
            //
            // We need to get (N-1) * (pixel stride bytes) per row + 1 byte for the last pixel
            (planeWidth - 1) * pixelStride + 1
        }

        for (row in 0 until planeHeight) {
            // Move buffer position to the beginning of this row
            planeBuffer.position(
                (row + planeCrop.top) * rowStride + planeCrop.left * pixelStride
            )

            if (pixelStride == 1 && outputStride == 1) {
                // When there is a single stride value for pixel and output, we can just copy
                // the entire row in a single step
                planeBuffer.get(outputBuffer, outputOffset, rowLength)
                outputOffset += rowLength
            } else {
                // When either pixel or output have a stride > 1 we must copy pixel by pixel
                planeBuffer.get(rowBuffer, 0, rowLength)
                for (col in 0 until planeWidth) {
                    outputBuffer[outputOffset] = rowBuffer[col * pixelStride]
                    outputOffset += outputStride
                }
            }
        }
    }
}
