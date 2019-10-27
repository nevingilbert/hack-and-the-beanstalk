package com.example.soundtouchproject;

public class MediaRecorder {

    private android.media.MediaRecorder mic;

    public MediaRecorder(android.media.MediaRecorder mic) {
        this.mic = mic;
    }

    public android.media.MediaRecorder getMic() {
        return mic;
    }

    public double getIntensity() {
        return (Math.sqrt(mic.getMaxAmplitude()) - Math.sqrt(0)) / Math.sqrt(30000) * 100;
    }
}
