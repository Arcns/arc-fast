package com.arc.fast.core.file

enum class FileMimeType(val value: String) {
    Wildcard("*/*"),

    // application
    ApplicationApk("application/vnd.android.package-archive"),
    ApplicaionOctetStream("application/octet-stream"),
    ApplicaionDoc("application/msword"),
    ApplicaionGtar("application/x-gtar"),
    ApplicaionGz("application/x-gzip"),
    ApplicaionJar("application/java-archive"),
    ApplicaionJs("application/x-javascript"),
    ApplicaionMpc("application/vnd.mpohun.certificate"),
    ApplicaionMsg("application/vnd.ms-outlook"),
    ApplicaionPdf("application/pdf"),
    ApplicaionVndMsPowerpoint("application/vnd.ms-powerpoint"),
    ApplicaionRar("application/x-rar-compressed"),
    ApplicaionRtf("application/rtf"),
    ApplicaionTar("application/x-tar"),
    ApplicaionTgz("application/x-compressed"),
    ApplicaionWps("application/vnd.ms-works"),
    ApplicaionZ("application/x-compress"),
    ApplicaionZip("application/zip"),
    ApplicaionM3u8("application/x-mpegURL"),

    // text
    TextPlain("text/plain"),
    TextHtml("text/html"),
    TextXml("text/xml"),

    // image
    ImageWildcard("image/*"),
    ImageBmp("image/bmp"),
    ImageJpeg("image/jpeg"),
    ImageGif("image/gif"),
    ImagePng("image/png"),
    ImageIco("image/x-icon"),
    ImagePsd("image/vnd.adobe.photoshop"),
    ImageWebp("image/webp"),
    ImageTiff("image/tiff"),

    // video
    VideoWildcard("video/*"),
    Video3gp("video/3gpp"),
    VideoAsf("video/x-ms-asf"),
    VideoAvi("video/x-msvideo"),
    VideoM4u("video/vnd.mpegurl"),
    VideoM4v("video/x-m4v"),
    VideoQuicktime("video/quicktime"),
    VideoMp4("video/mp4"),
    VideoMpeg("video/mpeg"),
    VideoMng("video/x-mng"),
    VideoMovie("video/x-sgi-movie"),
    VideoPvx("video/x-pv-pvx"),
    VideoRv("video/vnd.rn-realvideo"),
    VideoWm("video/x-ms-wm"),
    VideoWmx("video/x-ms-wmx"),
    VideoWv("video/wavelet"),
    VideoWvx("video/x-ms-wvx"),
    VideoRmvb("video/vnd.rn-realvideo"),
    VideoWmv("video/x-ms-wmv"),
    VideoFlv("video/x-flv"),
    VideoFli("video/x-fli"),
    VideoF4v("video/x-f4v"),
    VideoTs("video/MP2T"),
    VideoOgg("video/ogg"),

    // audio
    AudioWildcard("audio/*"),
    AudioMp4aLatm("audio/mp4a-latm"),
    AudioM3u("audio/x-mpegurl"),
    AudioXMpeg("audio/x-mpeg"),
    AudioMpeg("audio/mpeg"),
    AudioWav("audio/x-wav"),
    AudioWma("audio/x-ms-wma"),
    AudioRmp("audio/x-pn-realaudio-plugin"),
    AudioWax("audio/x-ms-wax"),
    AudioAif("audio/x-aiff"),
    AudioAac("audio/x-aac"),
    AudioAu("audio/basic"),
    AudioAdp("audio/adp");

    /**
     * 是否为视频文件类型
     */
    val isVideo: Boolean get() = value.startsWith("video/", true)

    /**
     * 是否为音频文件类型
     */
    val isAudio: Boolean get() = value.startsWith("audio/", true)

    /**
     * 是否为图片文件类型
     */
    val isImage: Boolean get() = value.startsWith("image/", true)

    companion object {
        fun getMimeTypeBySuffix(suffix: String?): FileMimeType =
            when (suffix?.lowercase()) {
                // application
                ".gz" -> ApplicaionGz
                ".js" -> ApplicaionJs
                ".jar" -> ApplicaionJar
                ".gtar" -> ApplicaionGtar
                ".doc" -> ApplicaionDoc
                ".apk" -> ApplicationApk
                ".bin", ".class", ".exe" -> ApplicaionOctetStream
                ".mpc" -> ApplicaionMpc
                ".msg" -> ApplicaionMsg
                ".pdf" -> ApplicaionPdf
                ".pps", ".ppt" -> ApplicaionVndMsPowerpoint
                ".rar" -> ApplicaionRar
                ".rtf" -> ApplicaionRtf
                ".tar" -> ApplicaionTar
                ".tgz" -> ApplicaionTgz
                ".wps" -> ApplicaionWps
                ".z" -> ApplicaionZ
                ".zip" -> ApplicaionZip
                ".m3u8" -> ApplicaionM3u8
                // text
                ".java", ".c", ".conf", ".cpp", ".h", ".prop", ".rc", ".sh", ".txt", ".log" -> TextPlain
                ".htm", ".html" -> TextHtml
                ".xml" -> TextXml
                // image
                ".jpeg", ".jpg" -> ImageJpeg
                ".gif" -> ImageGif
                ".bmp" -> ImageBmp
                ".png" -> ImagePng
                ".ico" -> ImageIco
                ".psd" -> ImagePsd
                ".webp" -> ImageWebp
                ".tif", ".tiff" -> ImageTiff
                // video
                ".3gp" -> Video3gp
                ".asf" -> VideoAsf
                ".avi" -> VideoAvi
                ".m4v" -> VideoM4v
                ".mov", ".qt" -> VideoQuicktime
                ".mp4", ".mpg4" -> VideoMp4
                ".mpe", ".mpeg", ".mpg" -> VideoMpeg
                ".mng" -> VideoMng
                ".movie" -> VideoMovie
                ".pvx" -> VideoPvx
                ".rv" -> VideoRv
                ".wm" -> VideoWm
                ".wmx" -> VideoWmx
                ".wv" -> VideoWv
                ".wvx" -> VideoWvx
                ".rmvb" -> VideoRmvb
                ".wmv" -> VideoWmv
                ".flv" -> VideoFlv
                ".fli" -> VideoFli
                ".f4v" -> VideoF4v
                ".ts" -> VideoTs
                ".ogg", ".ogv" -> VideoOgg
                ".m4u" -> VideoM4u
                ".rm", ".swf", ".ram", ".webm", ".viv", ".uvu", ".pyv", ".mxu", "fvt", ".uvv", ".uvs",
                ".uvp", ".uvm", ".uvh", ".mj2", ".jpm", ".jpgv", ".h264", ".h263", ".h261", ".3g2" -> VideoWildcard
                // audio
                ".m3u" -> AudioM3u
                ".m4a", ".m4b", ".m4p" -> AudioMp4aLatm
                ".mp2", ".mp3" -> AudioXMpeg
                ".wav" -> AudioWma
                ".wma" -> AudioWma
                ".mpga" -> AudioMpeg
                ".rmp" -> AudioRmp
                ".wax" -> AudioWax
                ".aif" -> AudioAif
                ".aac" -> AudioAac
                ".au" -> AudioAu
                ".adp" -> AudioAdp
                ".flac", ".amr", ".mmf", ".cda", ".weba", ".rip", ".ecelp9600", ".ecelp7470", ".ecelp4800",
                ".pya", ".lvp", ".dtshd", ".dts", ".dra", ".eol", ".uva", ".mid" -> AudioWildcard
                //
                else -> Wildcard
            }

    }
}