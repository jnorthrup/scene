package scene.action;

import scene.SceneLayoutApp;
import scene.anim.WebAnimator;
import scene.gif.AnimatedGifEncoder;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.concurrent.Exchanger;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * Copyright hideftvads.com 2009 all rights reserved.
 * <p/>
 * User: jim
 * Date: May 21, 2009
 * Time: 1:28:00 PM
 */
public class RecordWebScrollerAction extends AbstractAction {
    private WebAnimator webAnimator;

    public RecordWebScrollerAction(WebAnimator webAnimator) {
        super("Record");
        this.webAnimator = webAnimator;
    }

    @Override
    public void actionPerformed(ActionEvent e) {


        final Exchanger<BufferedImage> engine = new Exchanger<BufferedImage>();

        final Adjustable slider = webAnimator.getJScrollPane().getVerticalScrollBar();


        slider.setValue(slider.getMinimum());
        try {
            engine.exchange(null, 1, TimeUnit.NANOSECONDS);
        } catch (Exception ignored) {
        }


        Runnable painterThread = new Runnable() {
            public void run() {
                try {
                } catch (Exception e1) {
                }
                BufferedImage image = null;
                double end = 0.9 * slider.getMaximum();
                while (slider.getValue() < end) {


                    if (image == null) {
                        image = (BufferedImage) webAnimator.getPanel().createImage(webAnimator.getPanel().getWidth(), webAnimator.getPanel().getHeight());
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

                try {
                    engine.exchange(null);
                } catch (InterruptedException e1) {
                    return;
                }

            }
        };

        final JFileChooser chooser = new JFileChooser("/tmp/");

        if (JFileChooser.APPROVE_OPTION == chooser.showSaveDialog(SceneLayoutApp.desktopPane)) {


            File selectedFile = chooser.getSelectedFile();
            AnimatedGifEncoder gifEncoder = new AnimatedGifEncoder();

            gifEncoder.setFrameRate(10);
            gifEncoder.setQuality(10);
            gifEncoder.setDelay(100);
            gifEncoder.start(selectedFile.getAbsolutePath());
            Future<Object> future = (Future<Object>) SceneLayoutApp.threadPool.submit(painterThread);


            BufferedImage image = null;
            try {
                do {
                    image = engine.exchange(image);
                    gifEncoder.addFrame(image);

                }
                while (image != null);
                future.get();
            } catch (Exception e1) {
            }
            System.out.println("gifencoder.finish() =" + gifEncoder.finish());
            webAnimator.pack();

        }
    }
}
