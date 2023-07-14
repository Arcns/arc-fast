package com.arc.fast.core.util

import android.util.Log
import kotlin.math.roundToInt

class VideoUtil {
    fun cropViaFFmpeg(){
        // 使应用支持ffmpeg命令 0.3.5
//        implementation group: 'org.bytedeco', name: 'javacv', version: '1.5.3'
////    implementation group: 'org.bytedeco', name: 'javacv-platform', version: '1.5.3'
//        implementation group: 'org.bytedeco', name: 'javacpp', version: '1.5.3'
//        implementation group: 'org.bytedeco', name: 'javacpp', version: '1.5.3', classifier: 'android-arm'
//        implementation group: 'org.bytedeco', name: 'javacpp', version: '1.5.3', classifier: 'android-arm64'
//        implementation group: 'org.bytedeco', name: 'ffmpeg', version: '4.2.2-1.5.3'
//        implementation group: 'org.bytedeco', name: 'ffmpeg', version: '4.2.2-1.5.3', classifier: 'android-arm'
//        implementation group: 'org.bytedeco', name: 'ffmpeg', version: '4.2.2-1.5.3', classifier: 'android-arm64'
////    implementation group: 'org.bytedeco', name: 'opencv', version: '4.3.0-1.5.3'
////    implementation group: 'org.bytedeco', name: 'opencv', version: '4.3.0-1.5.3', classifier: 'android-arm'
////    implementation group: 'org.bytedeco', name: 'opencv', version: '4.3.0-1.5.3', classifier: 'android-arm64'
//        implementation group: 'org.bytedeco', name: 'openblas', version: '0.3.9-1.5.3'
//        implementation group: 'org.bytedeco', name: 'openblas', version: '0.3.9-1.5.3', classifier: 'android-arm'
//        implementation group: 'org.bytedeco', name: 'openblas', version: '0.3.9-1.5.3', classifier: 'android-arm64'
        //testtest
//        lifecycleScope.launch {
//
//            // 设置大内存，防止小内存的设备无法合成视频音频
//            System.setProperty("org.bytedeco.javacpp.maxphysicalbytes", "0");
//            System.setProperty("org.bytedeco.javacpp.maxbytes", "0");
//
//            FFmpegLogCallback.set()
//            var grabber: FFmpegFrameGrabber? = null
//            var filter: FFmpegFrameFilter? = null
//            var recorder: FFmpegFrameRecorder? = null
//            try {
//                grabber = FFmpegFrameGrabber(File(outputFileResults.savedUri?.path))
//                grabber.start()
//                val actualWidth: Int
//                val actualHeight: Int
//                if (grabber.imageWidth < grabber.imageHeight) {
//                    actualWidth = grabber.imageWidth
//                    actualHeight = grabber.imageHeight
//                } else {
//                    actualWidth = grabber.imageHeight
//                    actualHeight = grabber.imageWidth
//                }
//                val cropSize = actualWidth
//                val cropY: Int = ((actualHeight - cropSize).toFloat() / 2).roundToInt()
//
//                filter =
//                    FFmpegFrameFilter(
//                        "transpose=clock,crop=$cropSize:$cropSize:0:$cropY",
//                        "anull",
//                        grabber.imageWidth,
//                        grabber.imageHeight,
//                        grabber.audioChannels
//                    ).apply {
//                        pixelFormat = grabber.pixelFormat
//                        sampleFormat = grabber.sampleFormat
//                        frameRate = grabber.frameRate
//                        sampleRate = grabber.sampleRate
//                    }
//                filter.start()
//
//
//                val outFile = cameraUtil.createRandomFile(".mp4")
//                outFile.createNewFile()
//                recorder = FFmpegFrameRecorder(
//                    outFile,
//                    cropSize,
//                    cropSize,
//                    grabber.audioChannels
//                ).apply {
//                    videoCodec = grabber.videoCodec
//                    audioCodec = grabber.audioCodec
//                    frameRate = grabber.frameRate
//                    sampleRate = grabber.sampleRate
//                    format = "mp4"
//                }
//                recorder.start()
//
//                var capturedFrame: Frame?
//                var pullFrame: Frame?
//                while (grabber.grab().also { capturedFrame = it } != null) {
//                    if (capturedFrame?.image != null || capturedFrame?.samples != null) {
//                        filter.push(capturedFrame)
//                    }
//                    while (filter.pull().also { pullFrame = it } != null) {
//                        if (pullFrame?.image != null || pullFrame?.samples != null) {
//                            recorder.record(pullFrame)
//                        }
//                    }
//                }
//
//                binding.jzPlayer.isVisible = true
//                binding.jzPlayer.setUp(
//                    outFile.absolutePath, JZVideoPlayerStandard.SCREEN_WINDOW_NORMAL
//                )
//                binding.jzPlayer.startVideo()
//                if (onVideoCaptureListener?.invoke(outputFileResults.savedUri?.path) == true) {
//                    finish()
//                }
//            } catch (e: Exception) {
//                e.printStackTrace()
//                Log.e("aaaaaaaaaaaaaaaaa", "aaaaaaaaaaaaaaaaaaa:err - " + e.message)
//            } finally {
//                filter?.stop();
//                filter?.release();
//                grabber?.stop();
//                grabber?.release();
//                recorder?.stop();
//                recorder?.release();
//            }
//        }
    }
}