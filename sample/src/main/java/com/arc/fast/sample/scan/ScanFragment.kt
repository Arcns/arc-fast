package com.arc.fast.sample.scan

import android.Manifest
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.TranslateAnimation
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import com.arc.fast.core.util.PermissionUtil
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.common.Barcode
import com.arc.fast.sample.BaseFragment
import com.arc.fast.sample.databinding.FragmentScanBinding
import com.arc.fast.sample.extension.setLightSystemBar
import com.arc.fast.sample.utils.NavTransitionOptions
import java.util.concurrent.Executors

class ScanFragment : BaseFragment<FragmentScanBinding>() {
    private var isHandlingResult = false

    override fun onCreateBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): FragmentScanBinding = FragmentScanBinding.inflate(inflater, container, false)

    override fun onCreateTransition(): NavTransitionOptions? {
        return null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activity?.setLightSystemBar(false)
        binding.ivClose.setOnClickListener {
            findNavController().navigateUp()
        }
        PermissionUtil.fastRequest(
            this,
            Manifest.permission.CAMERA
        ) { allGranted, _ ->
            if (allGranted) {
                // 初始化相机
                startCamera()
            } else {
                findNavController().navigateUp()
            }
        }
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())
        cameraProviderFuture.addListener({
            // 摄像头选择
            val cameraSelector = CameraSelector.Builder().requireLensFacing(
                CameraSelector.LENS_FACING_BACK
            ).build()
            // 设置预览
            val preview = Preview.Builder().build().apply {
                setSurfaceProvider(binding.viewFinder.surfaceProvider)
            }
            // 设置二维码分析
            val analysis = ImageAnalysis.Builder().apply {
                // 背压只保留最新
                setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
            }.build().apply {
                setAnalyzer(
                    Executors.newSingleThreadExecutor(),
                    MLScannerAnalyzer(
                        barcodeScannerOptions = BarcodeScannerOptions.Builder()
                            .setBarcodeFormats(
                                Barcode.FORMAT_CODE_128,
                                Barcode.FORMAT_CODE_39,
                                Barcode.FORMAT_CODE_93,
                                Barcode.FORMAT_CODABAR,
                                Barcode.FORMAT_EAN_13,
                                Barcode.FORMAT_EAN_8,
                                Barcode.FORMAT_UPC_A,
                                Barcode.FORMAT_UPC_E,
                            ).build(),
                        onScannerResult = this@ScanFragment::onResult
                    )
                )
            }
            // 开始绑定
            val cameraProvider = cameraProviderFuture.get()
            cameraProvider.unbindAll() //取消之前的用例，避免重复绑定
            cameraProvider.bindToLifecycle(
                this,
                cameraSelector,
                preview,
                analysis
            )
        }, ContextCompat.getMainExecutor(requireContext()))

        binding.ivScanLine.post {
            binding.ivScanLine.startAnimation(
                TranslateAnimation(
                    0f,
                    0f,
                    -binding.ivScanLine.height.toFloat(),
                    binding.flScan.height.toFloat()
                ).apply {
                    duration = 2000
                    repeatCount = Animation.INFINITE
                }
            )
        }
    }

    /**
     * 处理扫描结果
     */
    private fun onResult(value: String) {
        if (isHandlingResult) return
        isHandlingResult = true
        appViewModel.setScanResult(value)
        findNavController().navigateUp()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        activity?.setLightSystemBar(true)
    }

}