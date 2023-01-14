package com.arc.fast.sample.mask

import android.Manifest
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import com.arc.fast.permission.FastPermissionUtil
import com.arc.fast.sample.BaseFragment
import com.arc.fast.sample.common.extension.setLightSystemBar
import com.arc.fast.sample.databinding.FragmentMaskBinding
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.common.Barcode
import java.util.concurrent.Executors

class MaskFragment : BaseFragment<FragmentMaskBinding>() {

    override fun onCreateBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): FragmentMaskBinding = FragmentMaskBinding.inflate(inflater, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activity?.setLightSystemBar(false)
        FastPermissionUtil.request(
            this,
            Manifest.permission.CAMERA,
            overallRationale = "应用需要相机权限用于扫描"
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
                            .setBarcodeFormats(Barcode.FORMAT_ALL_FORMATS).build(),
                        onScannerResult = this@MaskFragment::onResult
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
    }

    /**
     * 处理扫描结果
     */
    private fun onResult(value: String) {
    }

    override fun onDestroyView() {
        super.onDestroyView()
        activity?.setLightSystemBar(true)
    }
}