package scene.action;

//import org.lobobrowser.html.gui.*;
import scene.*;
import scene.anim.*;

import javax.imageio.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.io.*;
import java.util.*;
import java.util.concurrent.*;

/**
 * Copyright hideftvads.com 2009 all rights reserved.
 * <p/>
 * User: jim
 * Date: May 21, 2009
 * Time: 1:28:00 PM
 */
public class RecordWebScrollerPngDir extends AbstractAction {
    private WebAnimatorImpl webAnimator;

    public RecordWebScrollerPngDir(WebAnimatorImpl webAnimator) {
        super(">.mp4");
        this.webAnimator = webAnimator;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        final Exchanger<BufferedImage> engine = new Exchanger<BufferedImage>();
        JScrollBar slider = null /*= webAnimator.getJScrollPane().getVerticalScrollBar();*/;
        final Component[] renderable = webAnimator.getHtmlPanel().getComponents();
        for (Component component : renderable) {
            System.err.println("" + component.getClass().getName());

            final int iend = (int) webAnimator.getStopSlider().getValue();
            final int beg = webAnimator.getStartSlider().getValue();
            final boolean custom = iend > beg/*beg == end*/;
            slider.setValue(custom ? webAnimator.getStartSlider().getValue() : slider.getMinimum());

            final JScrollBar slider1 = slider;
            Runnable painterThread = new Runnable() {
                public void run() {
                    BufferedImage image = null;
                    final double end = custom ? iend : slider1.getMaximum() - slider1.getModel().getExtent();
                    while (slider1.getValue() < end) {
                        if (image == null) {
                            image = new BufferedImage(webAnimator.getPanel().getWidth(), webAnimator.getPanel().getHeight(), BufferedImage.TYPE_INT_ARGB);
                        }

                        webAnimator.getPanel().paint(image.getGraphics());
                        try {
                            image = engine.exchange(image);
                        } catch (InterruptedException e1) {
                            return;
                        }
                        int maximum = slider1.getMaximum();
                        int value = slider1.getValue();

                        slider1.setValue(value + 1);
                    }
                    Thread.currentThread().interrupt();
                }
            };

            SceneLayoutApp.threadPool.submit(painterThread);

            final JFileChooser chooser = new JFileChooser("/tmp/");

            if (JFileChooser.APPROVE_OPTION == chooser.showSaveDialog(SceneLayoutApp.desktopPane)) {


                final File selectedFile = chooser.getSelectedFile();

                if (!selectedFile.exists()) {
                    final boolean b = selectedFile.mkdirs();
                }
                int c = 100000;
                BufferedImage bi = null;
                ImageIO.scanForPlugins();
                final String[] formatNames = ImageIO.getWriterFormatNames();


                System.err.println(Arrays.toString(formatNames));
                try {
                    while (null != (bi = engine.exchange(bi, 5, TimeUnit.SECONDS))) {
                        ImageIO.write(bi, "png", new File(selectedFile.getPath() + '/' + "hideftvads" + (c++) + ".png"));
                    }
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                } catch (TimeoutException e1) {
                    System.err.println("timeout as planned..");
                } catch (IOException e1) {
                }

                Callable<Integer> callable = new Callable<Integer>() {
                    public Integer call() throws Exception {
                        final String[] cmdarray = {
                                "ffmpeg",
                                "-r",
                                "25",
                                "-i",
                                selectedFile.getAbsolutePath() + "/hideftvads1%05d.png",
                                "-threads",
                                "0",
                                "-vcodec",
                                "libx264",
                                "-vpre",
                                "hq",
                                "-mbd",
                                "rd",
                                "-level",
                                "51",
                                "-cropbottom",
                                "2",
                                "-y",
                                selectedFile.getAbsolutePath() + ".mp4"
                        };

                        System.err.println("" +
                                Arrays.toString(cmdarray)
                        );
                        final ProcessBuilder builder = new ProcessBuilder();
                        builder.command(cmdarray);
                        builder.redirectErrorStream(true);
                        final Process process = builder.start();


                        final int i = process.waitFor();
                        return i;
                    }
                };
                final Future<Integer> integerFuture = SceneLayoutApp.threadPool.submit(callable);
                Callable<Boolean> callable2 = new Callable<Boolean>() {
                    public Boolean call() throws Exception {
                        if (integerFuture.get() == 0) {
                            final File[] files = selectedFile.listFiles();
                            for (File file : files) {
                                file.delete();
                            }
                            return selectedFile.delete();
                        }

                        return false;
                    }
                };
                SceneLayoutApp.threadPool.submit(callable2);
            }
        }
     }
}
