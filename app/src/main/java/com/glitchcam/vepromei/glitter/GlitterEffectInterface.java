package com.glitchcam.vepromei.glitter;

public interface GlitterEffectInterface {

    void onCaptureRecordingStarted(int i);

    void onCaptureRecordingDuration(int i, long l);

    void onCaptureRecordingFinished(int i);

    void onCaptureRecordingError(int i);

    void onCaptureDevicePreviewStarted(int i);

    void onFragmentLoadFinished();

    void onStreamingEngineStateChanged(int i);
}
