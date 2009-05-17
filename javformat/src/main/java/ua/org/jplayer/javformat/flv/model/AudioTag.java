package ua.org.jplayer.javformat.flv.model;

/**
 * A model object to store individual FLV audio tag
 *
 * @author Stanislav Vitvitskiy
 */
public class AudioTag extends Tag {
    private AudioCodec audioCodec;
    private int sampleRate;
    private int sampleSize;
    private boolean stereo;
    private byte[] data;

    public AudioTag(long streamId, long timestamp) {
        super(TagType.AUDIO, streamId, timestamp);
    }

    public AudioCodec getAudioCodec() {
        return audioCodec;
    }

    public int getSampleRate() {
        return sampleRate;
    }

    public int getSampleSize() {
        return sampleSize;
    }

    public boolean isStereo() {
        return stereo;
    }

    public byte[] getData() {
        return data;
    }

    public void setAudioCodec(AudioCodec soundFormat) {
        this.audioCodec = soundFormat;
    }

    public void setSampleRate(int soundRate) {
        this.sampleRate = soundRate;
    }

    public void setSampleSize(int soundSize) {
        this.sampleSize = soundSize;
    }

    public void setStereo(boolean stereo) {
        this.stereo = stereo;
    }

    public void setData(byte[] data) {
        this.data = data;
    }
}