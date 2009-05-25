package scene.action;

import scene.SceneLayoutApp;
import scene.anim.WebAnimator;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.Exchanger;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Copyright hideftvads.com 2009 all rights reserved.
 * <p/>
 * User: jim
 * Date: May 21, 2009
 * Time: 1:28:00 PM
 */
public class RecordWebScrollerPngDir extends AbstractAction {
    private WebAnimator webAnimator;

    public RecordWebScrollerPngDir(WebAnimator webAnimator) {
        super(">.png");
        this.webAnimator = webAnimator;
    }

    @Override
    public void actionPerformed(ActionEvent e) {


        final Exchanger<BufferedImage> engine = new Exchanger<BufferedImage>();

        final Adjustable slider = webAnimator.getJScrollPane().getVerticalScrollBar();

        final int iend = (int) webAnimator.stopSlider.getValue();
        final int beg = webAnimator.startSlider.getValue();
        final boolean custom = iend > beg/*beg == end*/;

        slider.setValue(custom ? webAnimator.startSlider.getValue() : slider.getMinimum());
//        try {
//            engine.exchange(null, 1, TimeUnit.NANOSECONDS);
//        } catch (Exception ignored) {
//        }


        Runnable painterThread = new Runnable() {
            public void run() {
                BufferedImage image = null;
                final double end = custom ? iend : slider.getMaximum();
                while (slider.getValue() < end) {
                    if (image == null) {
                        image = new BufferedImage(webAnimator.getPanel().getWidth(), webAnimator.getPanel().getHeight(), BufferedImage.TYPE_INT_ARGB);
                    }

                    webAnimator.getPanel().paint(image.getGraphics());
                    try {
                        image = engine.exchange(image);
                    } catch (InterruptedException e1) {
                        return;
                    }
                    int maximum = slider.getMaximum();
                    int value = slider.getValue();

                    slider.setValue(value + 1);
                }
                Thread.currentThread().interrupt();
            }
        };

        SceneLayoutApp.threadPool.submit(painterThread);

        final JFileChooser chooser = new JFileChooser("/tmp/");

        if (JFileChooser.APPROVE_OPTION == chooser.showSaveDialog(SceneLayoutApp.desktopPane)) {


            File selectedFile = chooser.getSelectedFile();

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
        }
    }
}

