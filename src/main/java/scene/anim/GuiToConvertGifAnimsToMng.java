package scene.anim;

import com.keypoint.PngEncoder;
import com.keypoint.PngEncoderB;
import gif.GifDecoder;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.IndexColorModel;
import java.io.*;
import java.util.zip.CRC32;

public class GuiToConvertGifAnimsToMng extends Frame {

    static final boolean compression = true;

    public static void main(String[] args) {
        if (args.length == 0)
            (new GuiToConvertGifAnimsToMng()).ouvrir();
        else {
            if (args.length > 1 && "PNG".equals(args[1]))
                (new GuiToConvertGifAnimsToMng()).ouvrir(args[0], true);
            else
                (new GuiToConvertGifAnimsToMng()).ouvrir(args[0], false);
        }
    }

    public void ouvrir(String fname, boolean forcePNG) {
        File f = new File(fname);
        if (!f.exists()) {
            System.err.println("Le fichier " + fname + " n'existe pas");
            System.exit(1);
        }
        amort(f, forcePNG);
        System.exit(0);
    }

    public void ouvrir() {
        FileDialog fdlg = new FileDialog(this, "Convertir un fichier gif");
        fdlg.setFilenameFilter(new ExtFilter("gif"));
        fdlg.show();
        String fname = fdlg.getFile();
        if (fname == null) {
            System.exit(1);
        }
        if (fdlg.getDirectory() != null) {
            fname = fdlg.getDirectory() + File.separatorChar + fname;
        }
        ouvrir(fname, false);
    }

    public void amort(File f, boolean forcePNG) {
        System.out.println("Conversion du fichier: " + f.getPath() + "...");
        GifDecoder decodeur = decoderGIF(f, true);

        if (decodeur == null) {
            System.err.println("Erreur au décodage du GIF");
            System.exit(1);
        }
        int n = decodeur.getFrameCount();
        if (n == 0) {
            System.err.println("Erreur au décodage de " + f.getPath() + " (nb frames = 0)");
            return;
        }
        if (n == 1 || forcePNG) {
            System.out.println("PNG");
            decodeur = decoderGIF(f, false); // pas de palette: mieux pour les petites images (sauf si peu de couleurs?)
            File f2 = new File(sansExtension(f.getPath()) + ".png");
            BufferedImage frame = decodeur.getFrame(0);
            enregistrerPNG(f2, frame);
        } else {
            System.out.println("MNG-LC");
            File f2 = new File(sansExtension(f.getPath()) + ".mng");
            enregistrerMNG(f2, decodeur);
        }
    }

    public static GifDecoder decoderGIF(File fichier, boolean indexation) {
        GifDecoder d = new GifDecoder();
        d.setIndexation(indexation);
        int err = d.read(fichier.getPath());
        if (err != GifDecoder.STATUS_OK) {
            if (err == GifDecoder.STATUS_FORMAT_ERROR)
                System.err.println("GifDecoder: format error");
            else if (err == GifDecoder.STATUS_OPEN_ERROR)
                System.err.println("GifDecoder: open error");
        }
        return (d);
    }

    public static void enregistrerPNG(File fichier, BufferedImage img) {
        byte[] pngbytes;
        PngEncoder png = new PngEncoderB(img, PngEncoderB.ENCODE_ALPHA, PngEncoderB.FILTER_NONE, 9);

        try {
            FileOutputStream outfile = new FileOutputStream(fichier);
            pngbytes = png.pngEncode();
            if (pngbytes == null)
                System.out.println("image vide");
            else
                outfile.write(pngbytes);
            outfile.flush();
            outfile.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected void writePalette(DataOutputStream outfile, IndexColorModel icm) throws IOException {
        byte[] redPal = new byte[256];
        byte[] greenPal = new byte[256];
        byte[] bluePal = new byte[256];
        byte[] allPal = new byte[768];

        icm.getReds(redPal);
        icm.getGreens(greenPal);
        icm.getBlues(bluePal);
        for (int i = 0; i < 256; i++) {
            allPal[i * 3] = redPal[i];
            allPal[i * 3 + 1] = greenPal[i];
            allPal[i * 3 + 2] = bluePal[i];
        }
        bytePos = 0;
        writeInt4(768);
        writeString("PLTE");
        CRC32 crc = new CRC32();
        crc.update("PLTE".getBytes());
        writeBytes(allPal);
        crc.update(allPal);
        writeInt4((int) crc.getValue());
        outfile.write(pngBytes, 0, bytePos);

        // tRNS chunk
        if (icm.getTransparentPixel() != -1) {
            int indtrans = icm.getTransparentPixel();
            bytePos = 0;
            writeInt4(indtrans + 1);
            writeString("tRNS");
            crc.reset();
            crc.update("tRNS".getBytes());
            for (int i = 0; i < indtrans; i++) {
                writeByte(255);
                crc.update(255);
            }
            writeByte(0);
            crc.update(0);
            writeInt4((int) crc.getValue());
            outfile.write(pngBytes, 0, bytePos);
        }
    }

    protected void writeDEFI(DataOutputStream outfile, int x, int y) throws IOException {
        outfile.writeInt(12);
        bytePos = 0;
        writeString("DEFI");
        writeInt2(0); // Object_id
        writeByte(0); // Do_not_show
        writeByte(0); // Concrete_flag
        writeInt4(x); // X_location
        writeInt4(y); // Y_location

        CRC32 crc = new CRC32();
        crc.reset();
        crc.update(pngBytes, 0, bytePos);
        writeInt4((int) crc.getValue());
        outfile.write(pngBytes, 0, bytePos);
    }

    /*
     protected void writeBACK(DataOutputStream outfile, int backrgb) throws IOException {
             outfile.writeInt(7);
             bytePos = 0;
             writeString( "BACK" );
             writeInt2( ((backrgb >> 16) & 0x00FF) << 8 );
             writeInt2( ((backrgb >> 8) & 0x0000FF) << 8 );
             writeInt2( (backrgb & 0x000000FF) << 8 );
             writeByte( 1 ); // mandatory background color

             CRC32 crc = new CRC32();
         crc.reset();
         crc.update( pngBytes, 0, bytePos );
             writeInt4( (int) crc.getValue());
         outfile.write( pngBytes, 0, bytePos );
     }
     */

    protected void writeFRAMmode(DataOutputStream outfile, int mode) throws IOException {
        // FRAM chunk (pour remettre le fond - du coup ce n'est pas du MNG-VLC mais du MNG-LC)
        bytePos = 0;
        outfile.writeInt(1); // FRAM chunk size
        writeString("FRAM");
        writeByte(mode); // Framing_mode (1 byte)

        outfile.write(pngBytes, 0, bytePos);
        CRC32 crc = new CRC32();
        crc.reset();
        crc.update(pngBytes, 0, bytePos);
        outfile.writeInt((int) crc.getValue()); // CRC (4 bytes)
    }

    protected void writeFRAMdelay(DataOutputStream outfile, int mode, int ticks) throws IOException {
        bytePos = 0;
        outfile.writeInt(10); // FRAM chunk size
        writeString("FRAM");
        writeByte(mode); // Framing_mode (1 byte)
        //writeByte( 0 ); // Subframe_name (1 byte)
        writeByte(0); // Separator (1 byte)
        writeByte(2); // Change_interframe_delay (1 byte)
        writeByte(0); // Change_timeout_and_termination (1 byte)
        writeByte(0); // Change_layer_clipping_boundaries (1 byte)
        writeByte(0); // Change_sync_id_list (1 byte)
        writeInt4(ticks); // Interframe_delay (4 bytes)

        outfile.write(pngBytes, 0, bytePos);
        CRC32 crc = new CRC32();
        crc.reset();
        crc.update(pngBytes, 0, bytePos);
        outfile.writeInt((int) crc.getValue()); // CRC (4 bytes)
    }

    public IndexColorModel icmTrans(IndexColorModel icm) {
        IndexColorModel icm2;
        // on ajouter une couleur transparente ou on en modifie une pour qu'elle devienne transparente
        if (icm.getMapSize() < 256) {
            int count = icm.getMapSize() + 1;
            System.out.print("ajout couleur (count=" + count + ")");
            byte reds[] = new byte[count];
            byte greens[] = new byte[count];
            byte blues[] = new byte[count];
            icm.getReds(reds);
            icm.getGreens(greens);
            icm.getBlues(blues);
            for (int i = count - 1; i > 0; i--) {
                reds[i] = reds[i - 1];
                greens[i] = greens[i - 1];
                blues[i] = blues[i - 1];
            }
            reds[0] = (byte) 0xFF;
            greens[0] = (byte) 0xFF;
            blues[0] = (byte) 0xFF;
            icm2 = new IndexColorModel(8, count, reds, greens, blues, 0);
        } else {
            // on demande un volontaire pour devenir transparent
            int count = icm.getMapSize();
            int indtrans = -1;
            long dist = -1;
            for (int i = 0; i < count; i++) {
                int rgbi = icm.getRGB(i);
                int ri = (rgbi >> 16) & 0x00FF;
                int gi = (rgbi >> 8) & 0x0000FF;
                int bi = rgbi & 0x000000FF;
                for (int j = i + 1; j < count; j++) {
                    int rgbj = icm.getRGB(j);
                    int rj = (rgbj >> 16) & 0x00FF;
                    int gj = (rgbj >> 8) & 0x0000FF;
                    int bj = rgbj & 0x000000FF;
                    long diff = (ri - rj) * (ri - rj) + (gi - gj) * (gi - gj) + (bi - bj) * (bi - bj);
                    if (dist == -1 || diff < dist) {
                        dist = diff;
                        indtrans = i;
                    }
                }
            }
            //System.out.print("volontaire transparent: " + indtrans + " dist="+dist+" ");
            //System.out.println("rgb=" + Long.toString(icm.getRGB(indtrans) & 0xFFFFFFFFL, 16).toUpperCase());
            byte reds[] = new byte[count];
            byte greens[] = new byte[count];
            byte blues[] = new byte[count];
            icm.getReds(reds);
            icm.getGreens(greens);
            icm.getBlues(blues);
            icm2 = new IndexColorModel(icm.getPixelSize(), count, reds, greens, blues, indtrans);
        }
        return (icm2);
    }

    public BufferedImage optimiser(BufferedImage frame, BufferedImage oldframe, int backrgb,
                                   IndexColorModel icm2, boolean arriere_plan, DataOutputStream outfile) throws IOException {
        int width = frame.getWidth();
        int height = frame.getHeight();
        IndexColorModel icm = null;
        if (frame.getColorModel() instanceof IndexColorModel)
            icm = (IndexColorModel) frame.getColorModel();
        if (oldframe != null) {
            // détermination de la zone de différences
            int xmin = -1;
            int ymin = -1;
            int xmax = -1;
            int ymax = -1;
            for (int ix = 0; ix < width; ix++)
                for (int iy = 0; iy < height; iy++) {
                    if (frame.getRGB(ix, iy) != oldframe.getRGB(ix, iy)) {
                        if (xmin == -1 || xmin > ix)
                            xmin = ix;
                        if (ymin == -1 || ymin > iy)
                            ymin = iy;
                        if (xmax == -1 || xmax < ix)
                            xmax = ix;
                        if (ymax == -1 || ymax < iy)
                            ymax = iy;
                    }
                }
            if (xmin == -1) {// aucune différence -> on met juste un pixel
                xmin = 0;
                ymin = 0;
                xmax = 0;
                ymax = 0;
            }
            int width2 = xmax - xmin + 1;
            int height2 = ymax - ymin + 1;
            //System.out.println("xmin="+xmin+" xmax="+xmax+" ymin="+ymin+" ymax="+ymax);
            // création petite image
            BufferedImage frame2;
            int transrgb = -1;
            int indtrans;
            if (icm2 == null) {
                if (icm != null) {
                    indtrans = icm.getTransparentPixel();
                    if (indtrans != -1) {
                        transrgb = icm.getRGB(indtrans);
                        icm2 = icm;
                    } else {
                        icm2 = icmTrans(icm);
                        indtrans = icm2.getTransparentPixel();
                        transrgb = icm2.getRGB(indtrans);
                    }
                }
                if (icm2 == null)
                    frame2 = new BufferedImage(width2, height2, BufferedImage.TYPE_INT_ARGB);
                else
                    frame2 = new BufferedImage(width2, height2, BufferedImage.TYPE_BYTE_INDEXED, icm2);
            } else {
                indtrans = icm2.getTransparentPixel();
                if (indtrans != -1)
                    transrgb = icm2.getRGB(indtrans);
                frame2 = new BufferedImage(width2, height2, BufferedImage.TYPE_BYTE_INDEXED, icm2);
            }
            int[] rgbs = frame.getRGB(xmin, ymin, width2, height2, null, 0, width2);
            for (int ix = 0; ix < width2; ix++)
                for (int iy = 0; iy < height2; iy++) {
                    int rgb = rgbs[iy * width2 + ix];
                    int orgb = oldframe.getRGB(ix + xmin, iy + ymin);
                    if (rgb == orgb) {
                        if (transrgb == -1)
                            rgbs[iy * width2 + ix] = rgb & 0x00FFFFFF; // le pixel doit ętre transparent
                        else
                            rgbs[iy * width2 + ix] = transrgb;
                    } else if (arriere_plan && (rgb & 0xFF000000) == 0) {
                        rgbs[iy * width2 + ix] = rgb | 0xFF000000; // le pixel doit ętre opaque
                    }
                }
            frame2.setRGB(0, 0, width2, height2, rgbs, 0, width2);
            // DEFI chunk (position de l'objet)
            writeDEFI(outfile, xmin, ymin);
            return (frame2);
        } else {
            // la premičre image doit ętre opaque
            BufferedImage frame2;
            if (icm2 == null && icm == null)
                frame2 = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
            else if (icm2 == null) {
                icm2 = icm;
                frame2 = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_INDEXED, icm2);
            } else
                frame2 = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_INDEXED, icm2);

            Graphics2D g2 = frame2.createGraphics();
            g2.setColor(new Color(backrgb | 0xFF000000));
            g2.fillRect(0, 0, width, height);
            g2.drawImage(frame, 0, 0, null);// marche pas encore avec la beta de Java1.4/Mac

            // begin windoz bug workaround
            if (System.getProperty("os.name").startsWith("Windows")) {
                int[] rgbs = frame2.getRGB(0, 0, width, height, null, 0, width);
                for (int ix = 0; ix < width; ix++)
                    for (int iy = 0; iy < height; iy++) {
                        int rgb = rgbs[iy * width + ix];
                        if ((rgb & 0xFF000000) == 0)
                            rgbs[iy * width + ix] = rgb | 0xFF000000; // le pixel doit ętre opaque
                    }
                frame2.setRGB(0, 0, width, height, rgbs, 0, width);
            }
            // end windoz bug workaround

            return (frame2);
        }
    }

    public void enregistrerMNG(File fichier, GifDecoder decodeur) {
        CRC32 crc = new CRC32();
        try {
            DataOutputStream outfile = new DataOutputStream(new FileOutputStream(fichier));

            // signature MNG (8 bytes)
            outfile.writeByte(0x8A);
            outfile.writeBytes("MNG\r\n");
            outfile.writeByte(0x1A);
            outfile.writeByte('\n');

            // pour savoir les dimensions et la temporisation, on lit la premičre image:
            int n = decodeur.getFrameCount();
            System.out.println(n + " images");
            BufferedImage frame1 = decodeur.getFrame(0);
            /*int default_delay = decodeur.getDelay(0);  // display duration of frame in milliseconds
               if (default_delay == 0)
                   default_delay = 200;
               System.out.println("tempo: " + default_delay + "ms (" + (1000/default_delay) + " images/s)");*/
            int width = frame1.getWidth();
            int height = frame1.getHeight();
            boolean arriere_plan = false;
            if (decodeur.getDispose() == 2)
                arriere_plan = true;
            System.out.println("revenir sur l'arričre-plan: " + arriere_plan);
            boolean palette_globale = decodeur.getPaletteGlobale();
            System.out.println("palette globale: " + palette_globale);

            // check the loop count
            int loopCount = decodeur.getLoopCount();
            if (loopCount == 0) {
                loopCount = 0x7fffffff;
                System.out.println("boucle infinie");
            } else
                System.out.println(n + " boucles");

            // bordel de CRC
            pngBytes = new byte[1000];
            bytePos = 0;

            // MHDR  (header)

            outfile.writeInt(28); // header size: 28 bytes
            writeString("MHDR");
            writeInt4(width); // Frame_width (4 bytes)
            writeInt4(height); // Frame_height (4 bytes)
            //writeInt4( 1000/default_delay ); // Ticks_per_second (4 bytes)
            writeInt4(1000); // Ticks_per_second (4 bytes)
            writeInt4(0); // Nominal_layer_count (4 bytes)
            writeInt4(n); // Nominal_frame_count (4 bytes)
            writeInt4(0); // Nominal_play_time (4 bytes)
            //if (arriere_plan || compression)
            writeInt4(459); // Simplicity_profile (4 bytes)  MNG-LC
            //  interframe delay variable -> toujours LC
            //else
            //	writeInt4( 457 ); // Simplicity_profile (4 bytes)  MNG-VLC

            outfile.write(pngBytes, 0, bytePos);
            crc.reset();
            crc.update(pngBytes, 0, bytePos);
            outfile.writeInt((int) crc.getValue()); // CRC (4 bytes)

            // TERM chunk
            bytePos = 0;
            outfile.writeInt(10); // TERM chunk size: 10 bytes
            writeString("TERM");
            writeByte(3); // Termination_action (1 byte)
            writeByte(0); // Action_after_iterations (1 byte)
            writeInt4(decodeur.getDelay(n - 1)); // Delay (4 bytes)  (in ticks, before repeating the sequence)
            writeInt4(loopCount); // Iteration_max (4 bytes)

            outfile.write(pngBytes, 0, bytePos);
            crc.reset();
            crc.update(pngBytes, 0, bytePos);
            outfile.writeInt((int) crc.getValue()); // CRC (4 bytes)

            // palette
            IndexColorModel icm2 = null;
            if (palette_globale) {
                if (compression && ((IndexColorModel) frame1.getColorModel()).getTransparentPixel() == -1) {
                    icm2 = icmTrans((IndexColorModel) frame1.getColorModel());
                    writePalette(outfile, icm2);
                } else
                    writePalette(outfile, (IndexColorModel) frame1.getColorModel());
            }

            /*if (compression) {
                   IndexColorModel icm =  (IndexColorModel)frame1.getColorModel();
                   int indback = decodeur.getBgIndex();
                   writeBACK(outfile, icm.getRGB(indback));
               }*/

            if (arriere_plan && !compression) {
                writeFRAMmode(outfile, 3);
            }

            boolean alpha = decodeur.getTransparency(0); // a nécessité une modif de GifDecoder
            System.out.println("alpha: " + alpha);
            if (compression)
                alpha = true;
            boolean encode_alpha;
            if (alpha)
                encode_alpha = PngEncoderB.ENCODE_ALPHA;
            else
                encode_alpha = PngEncoderB.NO_ALPHA;

            BufferedImage oldframe = null;

            int indback = decodeur.getBgIndex();
            int backrgb = ((IndexColorModel) frame1.getColorModel()).getRGB(indback);
            int lastDelay = 1;
            for (int i = 0; i < n; i++) {
                System.out.print(".");
                BufferedImage frame = decodeur.getFrame(i);  // frame i
                int thisDelay = decodeur.getDelay(i);
                if (thisDelay != lastDelay) {
                    writeFRAMdelay(outfile, (arriere_plan && !compression) ? 3 : 1, thisDelay);
                    lastDelay = thisDelay;
                }
                if (compression) {
                    //int indback = decodeur.getBgIndex();
                    //IndexColorModel icm =  (IndexColorModel)frame.getColorModel();
                    //int backrgb = icm.getRGB(indback);
                    BufferedImage frame2 = optimiser(frame, oldframe, backrgb, icm2, arriere_plan, outfile);
                    oldframe = frame;
                    frame = frame2;
                }
                byte[] pngbytes;
                PngEncoderB png = new PngEncoderB(frame, encode_alpha, PngEncoderB.FILTER_NONE, 9);
                png.setPasDePalette(palette_globale);
                pngbytes = png.pngEncode();

                if (pngbytes == null)
                    System.out.println("image vide");
                else
                    outfile.write(pngbytes, 8, pngbytes.length - 8); // IHDR -> IEND
            }
            System.out.println();

            // MEND chunk
            outfile.writeInt(0); // MEND chunk size: 0 bytes
            bytePos = 0;
            writeString("MEND");
            outfile.write(pngBytes, 0, bytePos);
            crc.reset();
            crc.update(pngBytes, 0, bytePos);
            outfile.writeInt((int) crc.getValue()); // CRC (4 bytes)

            outfile.flush();
            outfile.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String sansExtension(String nomFichier) {
        int p = nomFichier.lastIndexOf('.');
        if (p == -1)
            return (nomFichier);
        else
            return (nomFichier.substring(0, p));
    }

    // utilitaires piqués dans PngEncoder:

    protected byte[] pngBytes;
    protected int bytePos;

    /**
     * Increase or decrease the length of a byte array.
     *
     * @param array     The original array.
     * @param newLength The length you wish the new array to have.
     * @return Array of newly desired length. If shorter than the
     *         original, the trailing elements are truncated.
     */
    protected byte[] resizeByteArray(byte[] array, int newLength) {
        byte[] newArray = new byte[newLength];
        int oldLength = array.length;

        System.arraycopy(array, 0, newArray, 0,
                Math.min(oldLength, newLength));
        return newArray;
    }

    /**
     * Write an array of bytes into the pngBytes array.
     * Note: This routine has the side effect of updating
     * maxPos, the largest element written in the array.
     * The array is resized by 1000 bytes or the length
     * of the data to be written, whichever is larger.
     *
     * @param data The data to be written into pngBytes.
     * @return The next place to be written to in the pngBytes array.
     */
    protected void writeBytes(byte[] data) {
        int offset = bytePos;
        if (data.length + offset > pngBytes.length) {
            pngBytes = resizeByteArray(pngBytes, pngBytes.length +
                    Math.max(1000, data.length));
        }
        System.arraycopy(data, 0, pngBytes, offset, data.length);
        bytePos = offset + data.length;
    }

    /**
     * Write an array of bytes into the pngBytes array, specifying number of bytes to write.
     * Note: This routine has the side effect of updating
     * maxPos, the largest element written in the array.
     * The array is resized by 1000 bytes or the length
     * of the data to be written, whichever is larger.
     *
     * @param data   The data to be written into pngBytes.
     * @param nBytes The number of bytes to be written.
     */
    protected void writeBytes(byte[] data, int nBytes) {
        int offset = bytePos;
        if (nBytes + offset > pngBytes.length) {
            pngBytes = resizeByteArray(pngBytes, pngBytes.length +
                    Math.max(1000, nBytes));
        }
        System.arraycopy(data, 0, pngBytes, offset, nBytes);
        bytePos = offset + nBytes;
    }

    /**
     * Write a two-byte integer into the pngBytes array at a given position.
     *
     * @param n The integer to be written into pngBytes.
     */
    protected void writeInt2(int n) {
        byte[] temp = {(byte) ((n >> 8) & 0xff),
                (byte) (n & 0xff)};
        writeBytes(temp);
    }

    /**
     * Write a four-byte integer into the pngBytes array at a given position.
     *
     * @param n The integer to be written into pngBytes.
     * @return The next place to be written to in the pngBytes array.
     */
    protected void writeInt4(int n) {
        byte[] temp = {(byte) ((n >> 24) & 0xff),
                (byte) ((n >> 16) & 0xff),
                (byte) ((n >> 8) & 0xff),
                (byte) (n & 0xff)};
        writeBytes(temp);
    }

    /**
     * Write a single byte into the pngBytes array at a given position.
     *
     * @param b The integer to be written into pngBytes.
     */
    protected void writeByte(int b) {
        byte[] temp = {(byte) b};
        writeBytes(temp);
    }

    /**
     * Write a string into the pngBytes array at a given position.
     * This uses the getBytes method, so the encoding used will
     * be its default.
     *
     * @return The next place to be written to in the pngBytes array.
     * @see java.lang.String#getBytes()
     */
    protected void writeString(String s) {
        writeBytes(s.getBytes());
    }

    /**
     * Filtrage des .gif dans le FileDialog
     */
    class ExtFilter implements FilenameFilter {
        String ext;

        public ExtFilter(String extension) {
            ext = extension;
        }

        public boolean accept(File dir, String name) {
            return (name.endsWith("." + ext));
        }
    }
}