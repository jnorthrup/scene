package mp4.util;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import mp4.util.atom.Atom;
import mp4.util.atom.AtomException;
import mp4.util.atom.ContainerAtom;
import mp4.util.atom.DefaultAtomVisitor;
import mp4.util.atom.FtypAtom;
import mp4.util.atom.LeafAtom;
import mp4.util.atom.MdatAtom;
import mp4.util.atom.MoovAtom;


/**
 * This class is used to split an mpeg4 file.
 *
 * The mpeg4 file format is the 
 * <a href="http://developer.apple.com/DOCUMENTATION/QuickTime/QTFF/qtff.pdf">Quicktime format</a>
 * 
 * The mpeg4 format is also ISO standard 14496-12.  That is, Part 12 of
 * the ISO mpeg4 specificatio.
 * 
 * Splitting the mpeg4 file requires rewritting the stbl atom container
 * with new data and cutting off the mdat section.
 */
public class Mp4Split extends DefaultAtomVisitor {
  // the input mp4 file
  private DataInputStream mp4file;
  
  public static String inputFile;
  public static String outputFile;
  public static boolean mdat = true;
  public static float time;
  
  @Override
  protected void defaultAction(Atom atom) throws AtomException {
    if (atom.isContainer()) {
      long bytesRead = 0;
      long bytesToRead = atom.dataSize();
      while (bytesRead < bytesToRead) {
        Atom child = parseAtom();
        ((ContainerAtom)atom).addChild(child);
        bytesRead += child.size();
      }
    }
    else {
      // the default action for a leaf is to read the data in to a buffer
      ((LeafAtom)atom).readData(mp4file);
    }
  }

  /**
   * Don't the the mdat atom since that's the biggest segment of the 
   * file.  It contains the video and sound data.  Plus, we'll just
   * skip over the beginning when we cut the movie.
   */
  @Override
  public void visit(MdatAtom atom) throws AtomException {
    atom.setInputStream(mp4file);
  }
  
  /**
   * Parse an atom from the mpeg4 file.
   * @return the number of bytes read
   * @throws AtomException
   */
  private Atom parseAtom() throws AtomException {
    // get the atom size
    byte[] word = new byte[Atom.ATOM_WORD];
    int num;
    try {
      num = mp4file.read(word);
    } catch (IOException e1) {
      throw new AtomException("IOException while reading file");
    }
    // check for end of file
    if (num == -1) {
      return null;
    }
    if (num != Atom.ATOM_WORD) {
      throw new AtomException("Unable to read enough bytes for atom");
    }
    long size = Atom.byteArrayToUnsignedInt(word, 0);
    // get the atom type
    try {
      num = mp4file.read(word);
    } catch (IOException e1) {
      throw new AtomException("IOException while reading file");
    }
    if (num != Atom.ATOM_WORD) {
      throw new AtomException("Unable to read enough bytes for atom");  
    }
    try {
      Class<?> cls = Class.forName(Atom.typeToClassName(word));
      Atom atom = (Atom) cls.newInstance();
      atom.setSize(size);
      atom.accept(this);
      return atom;
    } catch (ClassNotFoundException e) {
      throw new AtomException("Class not found");
    } catch (InstantiationException e) {
      throw new AtomException("Unable to instantiate atom");
    } catch (IllegalAccessException e) {
      throw new AtomException("Unabel to access atom object");
    }
  }

  /**
   * Constructor for the Mpeg-4 file splitter.  It opens the 
   * @param fn
   */
  public Mp4Split(String fn) {
    try {
      mp4file = new DataInputStream(new FileInputStream(fn));
      System.out.println("DBG: file size " + new File(fn).length());
    } catch (FileNotFoundException e) {
      System.err.println("File not found " + fn);
      System.exit(-1);
    }
  }
    
  public void splitMp4() {
    try {
      FtypAtom ftyp = (FtypAtom) parseAtom();
      MoovAtom moov = (MoovAtom) parseAtom();
      MdatAtom mdat = (MdatAtom) parseAtom();
      
      System.out.println("DBG: moov size " + moov.dataSize());
      System.out.println("DBG: mdat size " + mdat.dataSize());
  
      MoovAtom cutMoov = moov.cut(time);
      System.out.println("DBG: moov chunk " + moov.firstDataByteOffset());
      System.out.println("DBG: cut moov chunk " + cutMoov.firstDataByteOffset());
      long mdatSkip = cutMoov.firstDataByteOffset() - moov.firstDataByteOffset();
      MdatAtom cutMdat = mdat.cut(mdatSkip);
      
      // update stco segment by mdatSkip + difference in moov size
      long updateAmount = mdatSkip + (moov.size() - cutMoov.size());
      
      System.out.println("DBG: updateAmount " + updateAmount);
      cutMoov.fixupOffsets(-updateAmount);
      
      System.out.println("DBG: movie skip " + mdatSkip);
      
      System.out.println("DBG: Cut Movie time " + cutMoov.getMvhd().getDurationNormalized() + " sec ");
      
      DataOutputStream dos = new DataOutputStream(new FileOutputStream(outputFile));
      ftyp.writeData(dos);
      cutMoov.writeData(dos);
      if (Mp4Split.mdat) {
        cutMdat.writeData(dos);
      }
      
     } catch (AtomException e) {
      System.err.println("Error parseing Mp4 file " + e);
    } catch (FileNotFoundException e) {
      System.err.println("Error creating file output stream");
    } catch (IOException e) {
      System.err.println("Error writing output ");
      e.printStackTrace();
    }
  }
  
  /**
   * Process the command line arguments.
   * @param args the user-specified arguments
   */
  private static void processArgs(String[] args) {
    int i = 0;
    while (i < args.length) {
      String arg = args[i];
      if (arg.equals("-in")) {
        inputFile = args[++i];
      }
      else if (arg.equals("-out")) {
        outputFile = args[++i];
      }
      else if (arg.equals("-time")) {
        time = Float.valueOf(args[++i]);
      }
      else if (arg.equals("-no_mdat")) {
        mdat = false;
      }
      else {
        help();
      }
      i++;
    }
    if (inputFile == null) {
      help();
    }
  }
  
  private static void help() {
    System.out.println("Mp4Split <args>");
    System.out.println("  -in <inputfile.mp4>");
    System.out.println("  -out <outputfile.mp4>");
    System.out.println("  -time <seconds>");
    System.out.println("  [-no_mdat]");
    System.exit(-1);
  }
    
  /**
   * @param args
   */
  public static void main(String[] args) {
    processArgs(args);
    Mp4Split splitter = new Mp4Split(inputFile);
    splitter.splitMp4();
  }

}
