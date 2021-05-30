package com.bd.deliverytiger.app.ui.live.stream

data class StreamSettingData(
        var resolutionId: Int = -1, // 2->Low 240p    1-> Medium 360p    0-> High 480p
        var videoBitRate: Int = 500, //640X480 1280X720 1200
        var resolutionWidth: Int = 720, // 480p -> 854x480    360p -> 640x360    240p -> 360x240
        var resolutionHeight: Int = 480,// 480p -> 854x480    360p -> 640x360    240p -> 360x240
        var fps: Int = 25,
        var isHardwareRotation: Boolean = false,
        var isFaceDetection: Boolean = false,
        var audioBitRate: Int = 128, // 64, 128
        var audioSampleRate: Int = 44100, // Can be 8000, 16000, 22500, 32000, 44100.
        var isStereoChannel: Boolean = false,
        var isEchoCanceler: Boolean = true,
        var isNoiseSuppressor: Boolean = true,
        var isFrontCameraDefault: Boolean = false
)