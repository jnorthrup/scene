package ua.org.jplayer.javformat.iso;

import ua.org.jplayer.javformat.iso.model.Movie;
import ua.org.jplayer.javformat.iso.model.SampleInfo;
import ua.org.jplayer.javformat.iso.model.TrackInfo;
import ua.org.jplayer.javformat.toolkit.YAIOUtil;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.*;

/**
 * Interleaves tracks in the way a chunk of one track follows a chunk of another
 * one
 *
 * @author Stanislav Vitvitskiy
 */
public class TrackInterleaver {

    private Movie sourceMovie;
    private Collection samplesInWriteOrder;
    private Movie resultMovie;

    public TrackInterleaver(Movie sourceMovie) {
        this.sourceMovie = sourceMovie;
        interleave();
    }

    public Movie getInerleavedMovie() {
        return resultMovie;
    }

    private static class Entry {
        TrackInfo oldTrack;

        Iterator sampleIterator;
        Collection newSamples;
        int curChunk;
        int curTime;
    }

    private static class SampleInfoEx {

        private SampleInfo sampleInfo;
        private long realTime;

        public SampleInfoEx(SampleInfo sampleInfo, long realTime) {
            this.sampleInfo = sampleInfo;
            this.realTime = realTime;
        }

        public long getRealTime() {
            return realTime;
        }

        public SampleInfo getSampleInfo() {
            return sampleInfo;
        }

    }

    private void interleave() {
        int chunkDuration = 1000;
        int curOffset = 0;
        Queue entries = new LinkedList();
        {
            Iterator it = sourceMovie.getTracks().iterator();

            while (it.hasNext()) {
                TrackInfo track = (TrackInfo) it.next();
                Entry e = new Entry();
                e.sampleIterator = track.getSampleInfos().iterator();
                e.newSamples = new ArrayList();
                e.oldTrack = track;
                entries.add(e);
            }
        }

        samplesInWriteOrder = new ArrayList();

        Collection newTracks = new ArrayList();
        Entry e;
        while ((e = (Entry) entries.poll()) != null) {
            int time = 0;
            while (time <= chunkDuration) {
                if (!e.sampleIterator.hasNext()) {
                    break;
                }
                SampleInfo si = (SampleInfo) e.sampleIterator.next();

                SampleInfo newSample = new SampleInfo(si.getNo(), curOffset,
                        si.getSize());
                newSample.setChunkNo(e.curChunk);
                newSample.setDuration(si.getDuration());
                newSample.setSampleDescr(si.getSampleDescr());
                newSample.setSyncSample(si.isSyncSample());
                newSample.setTimestamp(e.curTime);

                curOffset += si.getSize();
                e.newSamples.add(newSample);
                e.curTime += si.getDuration();

                SampleInfoEx six;
                if (e.oldTrack.getType() == 0) {
                    six = new SampleInfoEx(
                            si,
                            (int) ((double) e.curTime * 1000 / e.oldTrack.getMediaHeader().getTimescale()));
                } else {
                    six = new SampleInfoEx(si, -1);
                }
                samplesInWriteOrder.add(six);
                int realDuration = (int) ((double) si.getDuration() * 1000 / e.oldTrack.getMediaHeader().getTimescale());

                time += realDuration;
            }
            e.curChunk++;
            if (e.sampleIterator.hasNext()) {
                entries.add(e);
            } else {
                TrackInfo newTrack = new TrackInfo(e.oldTrack.getNo(),
                        e.oldTrack.getDuration(), e.oldTrack.getWidth(),
                        e.oldTrack.getHeight());
                newTrack.setMediaHeader(e.oldTrack.getMediaHeader());
                newTrack.setSampleInfos(e.newSamples);
                newTracks.add(newTrack);
            }
        }
        resultMovie = new Movie(sourceMovie.getMovieHeader(), newTracks);
    }

    public static long LAG_TIME = 60000;

    public void writeSamples(FileInputStream source, OutputStream dest,
                             boolean throttle) throws IOException {
        long startTime = System.currentTimeMillis();
        Iterator it = samplesInWriteOrder.iterator();
        while (it.hasNext()) {
            SampleInfoEx six = (SampleInfoEx) it.next();

            source.getChannel().position(six.sampleInfo.getOffset());
            long copied = YAIOUtil.copy(source, dest, six.sampleInfo.getSize());

            if (throttle) {
                long elapsedTime = System.currentTimeMillis() - startTime;
                if (six.realTime != -1 && six.realTime > elapsedTime + LAG_TIME) {
                    try {
                        Thread.sleep(six.realTime - elapsedTime - LAG_TIME);
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}