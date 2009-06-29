package scene.action;

import org.lobobrowser.html.gui.*;
import scene.*;
import scene.anim.*;
import scene.gif.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.io.*;
import java.util.concurrent.*;

/**
 * Copyright hideftvads.com 2009 all rights reserved.
 * <p/>
 * User: jim
 * Date: May 21, 2009
 * Time: 1:28:00 PM
 */
public class RecordWebScrollerGifAnim extends AbstractAction {
    private WebAnimatorImpl webAnimator;

    public RecordWebScrollerGifAnim(WebAnimatorImpl webAnimator) {
        super(">.gif");
        this.webAnimator = webAnimator;
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        final Exchanger<BufferedImage> engine = new Exchanger<BufferedImage>();
        JScrollBar slider = null /*= webAnimator.getJScrollPane().getVerticalScrollBar();*/;
        final Component[] renderable = webAnimator.getHtmlPanel().getComponents();
        for (Component component : renderable) {
            System.err.println("" + component.getClass().getName());
            if (component instanceof HtmlBlockPanel) {
                HtmlBlockPanel p = (HtmlBlockPanel) component;

                final Component[] components = p.getComponents();

                for (Component o1 : components) {


                    if (o1 instanceof JScrollBar && ((JScrollBar) o1).getOrientation() == JScrollBar.VERTICAL) ;
                    {
                        slider = (JScrollBar) o1;
                        break;
                    }
                }
            }

            final int iend = (int) webAnimator.getStopSlider().getValue();
            final int beg = webAnimator.getStartSlider().getValue();
            final boolean custom = iend > beg/*beg == end*/;
            slider.setValue(custom ? webAnimator.getStartSlider().getValue() : slider.getMinimum());

            final JScrollBar slider1 = slider;


            final JScrollBar slider2 = slider;
            Runnable painterThread = new Runnable() {
                public void run() {
                    try {
                    } catch (Exception e1) {
                    }
                    BufferedImage image = null;
                    final double end = custom ? iend : slider2.getMaximum();
                    while (slider2.getValue() < end) {


                        if (image == null) {
                            image = (BufferedImage) webAnimator.getPanel().createImage(webAnimator.getPanel().getWidth(), webAnimator.getPanel().getHeight());
                        }

                        webAnimator.getPanel().paint(image.getGraphics());
                        try {
                            image = engine.exchange(image);
                        } catch (InterruptedException e1) {
                            return;
                        }
                        int maximum = slider2.getMaximum();
                        int value = slider2.getValue();

                        slider2.setValue(value + 1);
                    }

                    try {
                        engine.exchange(null);
                    } catch (InterruptedException ignored) {
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
}

