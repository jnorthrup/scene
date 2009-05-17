package ua.org.jplayer.javformat.iso.model.box;

import ua.org.jplayer.javformat.iso.boxes.*;

import java.net.URLDecoder;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListMap;

/**
 * Box type
 *
 * @author Stanislav Vitvitskiy
 */
public enum AtomType {
    FILE_Atom_TYPE("ftyp", LeafBox.class, true),
    MOVIE("moov", NodeBox.class, false),
    MDAT("mdat", LeafBox.class, true),
    IODS("iods", LeafBox.class, true, MOVIE),
    MOVIE_HEADER("mvhd", MovieHeaderBox.class, true, MOVIE),
    TRACK("trak", NodeBox.class, false, MOVIE),
    TRACK_HEADER("tkhd", TrackHeaderBox.class, true, TRACK),
    TRACK_REF("tref", LeafBox.class, true, TRACK),
    EDIT_LIST_CONT("edts", NodeBox.class, false, TRACK),
    EDIT_LIST("elst", LeafBox.class, true, EDIT_LIST_CONT),
    MEDIA("mdia", NodeBox.class, false, TRACK),
    MEDIA_HEADER("mdhd", MediaHeaderBox.class, true, MEDIA),
    MEDIA_INFO("minf", NodeBox.class, false, MEDIA),
    HANDLER("hdlr", HandlerBox.class, MEDIA, MEDIA_INFO),
    VIDEO_MEDIA_HEADER("vmhd", LeafBox.class, true, MEDIA_INFO),
    SOUND_MEDIA_HEADER("smhd", LeafBox.class, true, MEDIA_INFO),
    HINT_MEDIA_HEADER("hmhd", LeafBox.class, true, MEDIA_INFO),
    NULL_MEDIA_HEADER("nmhd", LeafBox.class, true, MEDIA_INFO),
    DATA_INFO("dinf", NodeBox.class, false, MEDIA_INFO),
    DATA_REF("dref", LeafBox.class, true, DATA_INFO),
    SAMPLE_TABLE("stbl", NodeBox.class, false, MEDIA_INFO),
    SAMPLE_DESC("stsd", LeafBox.class, true, SAMPLE_TABLE),
    TIME_TO_SAMPLE("stts", TimeToSampleBox.class, true, SAMPLE_TABLE),
    SYNCH_SAMPLE("stss", SyncSamplesBox.class, true, SAMPLE_TABLE),
    SAMPLE_TO_CHUNK("stsc", SampleToChunkBox.class, true, SAMPLE_TABLE),
    SAMPLE_SIZE("stsz", SampleSizesBox.class, true, SAMPLE_TABLE),
    COMPACT_SAMPLE_SIZE("stz2", LeafBox.class, true, SAMPLE_TABLE),
    SHADOW_SYNCH_SAMPLE("stsh", LeafBox.class, true, SAMPLE_TABLE),
    PADDING_BITS("padb", LeafBox.class, true, SAMPLE_TABLE),
    SAMPLE_DEGRAD("stdp", LeafBox.class, true, SAMPLE_TABLE),
    SAMPLE_GROUP("sgpd", LeafBox.class, true, SAMPLE_TABLE),
    CHUNK_OFFSET("stco", ChunkOffsetsBox.class, true, SAMPLE_TABLE),
    CHUNK_OFFSET_64("co64", LeafBox.class, true, SAMPLE_TABLE),
    COMPOS_TIME_TO_SAMPLE("ctts", LeafBox.class, true, SAMPLE_TABLE),
    MOVIE_EX("mvex", NodeBox.class, false, MOVIE),
    MOVIE_EX_HEADER("mehd", LeafBox.class, true, MOVIE_EX),
    TRACK_EX("trex", LeafBox.class, true, MOVIE_EX),
    MOVIE_FRAGMENT("moof", NodeBox.class, false),
    FRAGMET_HEADER("mfhd", LeafBox.class, true, MOVIE_FRAGMENT),
    TRACK_FRAGMENT("traf", NodeBox.class, false, MOVIE_FRAGMENT),
    FRAGMENT_HEADER("tfhd", LeafBox.class, true, TRACK_FRAGMENT),
    FRAGMENT_RUN("trun", LeafBox.class, true, TRACK_FRAGMENT),
    DISP_SAMPLES("sdtp", LeafBox.class, SAMPLE_TABLE, TRACK_FRAGMENT),
    SAMPLE_TO_GROUP("sbgp", LeafBox.class, SAMPLE_TABLE, TRACK_FRAGMENT),
    SUBSAMPLE_INFO("subs", LeafBox.class, SAMPLE_TABLE, TRACK_FRAGMENT),
    MFRA("mfra", NodeBox.class, false),
    TFRA("tfra", LeafBox.class, true, MFRA),
    MFRO("mfro", LeafBox.class, true, MFRA),
    FREE("free", LeafBox.class, true),
    SKIP("skip", NodeBox.class, false),
    UDTA("udta", LeafBox.class, true, SKIP),
    CPRT("cprt", LeafBox.class, true, SKIP),
    META("meta", NodeBox.class, false),
    HDLR("hdlr", LeafBox.class, true, META),
    DINF("dinf", NodeBox.class, false, META),
    DREF("dref", LeafBox.class, true, DINF),
    ILOC("iloc", LeafBox.class, true, META),
    IPMP_CONTROL("ipmc", LeafBox.class, MOVIE, META),
    IPRO("ipro", NodeBox.class, false, META),
    SINF("sinf", NodeBox.class, false, IPRO),
    FRMA("frma", LeafBox.class, true, SINF),
    IMIF("imif", LeafBox.class, true, SINF),
    SCHM("schm", LeafBox.class, true, SINF),
    SCHI("schi", LeafBox.class, true, SINF),
    IINF("iinf", LeafBox.class, true, META),
    XML("xml", LeafBox.class, true, META),
    BXML("bxml", LeafBox.class, true, META),
    PITM("pitm", LeafBox.class, true, META),;

    String sign;
    boolean leaf;
    Set<AtomType> parents = new HashSet<AtomType>();
    Class<? extends Box> claz;

    AtomType() {
        init();
    }

    private void init() {
        final ByteBuffer buffer1 = UTF8.encode(URLDecoder.decode(getSign()));
        LOOKUP.put(buffer1, this);
    }

    AtomType(final String sign, final Class<? extends Box> claz, final boolean leaf) {
        this.sign = sign;
        this.claz = claz;
        this.leaf = leaf;
        init();
    }

    AtomType(final String sign, final Class<? extends Box> claz, final boolean leaf, final AtomType parent) {
        this.sign = sign;
        this.claz = claz;
        this.leaf = leaf;
        parents.add(parent);
        init();
    }

    AtomType(final String sign, final Class<? extends Box> claz, final AtomType parent,
             final AtomType parent2) {
        this.sign = sign;
        this.claz = claz;
        this.leaf = true;
        parents.add(parent);
        parents.add(parent2);
        init();
    }

    private static final Charset UTF8 = Charset.forName("UTF8");

    private static final ConcurrentSkipListMap<ByteBuffer, AtomType> LOOKUP = new ConcurrentSkipListMap<ByteBuffer, AtomType>();

    static AtomType fromSign(final byte[] sign) {

        return LOOKUP.get(ByteBuffer.wrap(sign));

    }

    public String getSign() {
        return sign;
    }

    public boolean isLeaf() {
        return leaf;
    }

    public Set<AtomType> getParents() {
        return parents;
    }

    public Class<? extends Object> getClaz() {
        return claz;
    }
}