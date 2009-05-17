package ua.org.jplayer.javformat.flv.model;

import java.util.Collection;

/**
 * Java convenience bean for working with FLV metadata
 *
 * @author Stanislav Vitvitskiy
 */
public class Metadata {

    public static class KeyFrame {
        private Double offset;
        private Double time;

        public Double getOffset() {
            return offset;
        }

        public void setOffset(Double offset) {
            this.offset = offset;
        }

        public Double getTime() {
            return time;
        }

        public void setTime(Double time) {
            this.time = time;
        }
    }

    private String creator;
    private String metadatacreator;
    private Boolean hasKeyframes;
    private Boolean hasVideo;
    private Boolean hasAudio;
    private Boolean hasMetadata;
    private Boolean canSeekToEnd;
    private Double duration;
    private Double datasize;
    private Double filesize;
    private Double videosize;
    private Double audiosize;
    private Double videocodecid;
    private Double width;
    private Double height;
    private Double framerate;
    private Double audiocodecid;

    private Double videodatarate;
    private Double audiodatarate;

    private Double audiosamplerate;
    private Double audiosamplesize;
    private Boolean stereo;
    private Double lasttimestamp;
    private Double lastkeyframetimestamp;
    private Double lastkeyframelocation;
    private Collection keyFrames;

    public Collection getKeyFrames() {
        return keyFrames;
    }

    public void setKeyFrames(Collection keyFrames) {
        this.keyFrames = keyFrames;
    }

    public String getCreator() {
        return creator;
    }

    public String getMetadatacreator() {
        return metadatacreator;
    }

    public Boolean getHasKeyframes() {
        return hasKeyframes;
    }

    public Boolean getHasVideo() {
        return hasVideo;
    }

    public Boolean getHasAudio() {
        return hasAudio;
    }

    public Boolean getHasMetadata() {
        return hasMetadata;
    }

    public Boolean getCanSeekToEnd() {
        return canSeekToEnd;
    }

    public Double getDuration() {
        return duration;
    }

    public Double getDatasize() {
        return datasize;
    }

    public Double getFilesize() {
        return filesize;
    }

    public Double getVideosize() {
        return videosize;
    }

    public Double getAudiosize() {
        return audiosize;
    }

    public Double getVideocodecid() {
        return videocodecid;
    }

    public Double getWidth() {
        return width;
    }

    public Double getHeight() {
        return height;
    }

    public Double getFramerate() {
        return framerate;
    }

    public Double getAudiocodecid() {
        return audiocodecid;
    }

    public Double getVideodatarate() {
        return videodatarate;
    }

    public Double getAudiodatarate() {
        return audiodatarate;
    }

    public Double getAudiosamplerate() {
        return audiosamplerate;
    }

    public Double getAudiosamplesize() {
        return audiosamplesize;
    }

    public Boolean getStereo() {
        return stereo;
    }

    public Double getLasttimestamp() {
        return lasttimestamp;
    }

    public Double getLastkeyframetimestamp() {
        return lastkeyframetimestamp;
    }

    public Double getLastkeyframelocation() {
        return lastkeyframelocation;
    }
}