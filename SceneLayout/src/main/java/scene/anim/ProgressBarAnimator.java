package scene.anim;

import scene.SceneLayoutApp;
import scene.gif.AnimatedGifEncoder;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.HierarchyBoundsListener;
import java.awt.event.HierarchyEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;

/**
 * Created by IntelliJ IDEA.
 * User: jim
 * Date: May 15, 2009
 * Time: 1:31:56 AM
 * To change this template use File | Settings | File Templates.
 */
public class ProgressBarAnimator {
    private JInternalFrame inf;

    public ProgressBarAnimator() {
        inf = new JInternalFrame("ProgressBarAnimator");
        SceneLayoutApp.desktopPane.add(inf);

        final JProgressBar bar = new JProgressBar();
        JPanel panel = new JPanel(new BorderLayout());
        inf.setContentPane(panel);

        panel.add(bar, BorderLayout.CENTER);
        JToolBar t = new JToolBar();
        panel.add(t, BorderLayout.NORTH);

        bar.setMinimum(0);
        bar.setMaximum(100);

        bar.setValue(0);



        final ArrayList<Image> frames = new ArrayList<Image>();

        final JTextField minVtext = new JTextField("0",4);
        minVtext.setBorder(BorderFactory.createTitledBorder("min"));
        t.add(minVtext);

        final JTextField maxVtext = new JTextField(String.valueOf(bar.getWidth()),4);
        bar.addHierarchyBoundsListener(new HierarchyBoundsListener() {
            @Override
            public void ancestorMoved(HierarchyEvent e) {

            }

            @Override
            public void ancestorResized(HierarchyEvent e) {

                maxVtext.setText(String.valueOf(bar.getWidth()));
}
        });

        maxVtext.setBorder(BorderFactory.createTitledBorder("max"));
        t.add(maxVtext);

        t.add(new AbstractAction("Record") {
            @Override
            public void actionPerformed(ActionEvent e) {


                frames.clear();
                bar.setMinimum(Integer.decode(minVtext.getText()));
                bar.setMaximum(Integer.decode(maxVtext.getText()));
                bar.setValue(bar.getMinimum());

                while (bar.getValue() < bar.getMaximum()) {
                    bar.setValue(bar.getValue() + 1);
                    Image image = bar.createImage(bar.getWidth(), bar.getHeight());
                    frames.add(image);
                    bar.paint(image.getGraphics());
                }

                JFileChooser chooser = new JFileChooser("/tmp/");

                if (JFileChooser.APPROVE_OPTION == chooser.showSaveDialog(SceneLayoutApp.desktopPane)) {
                    final File selectedFile = chooser.getSelectedFile();


                    final AnimatedGifEncoder gifEncoder = new AnimatedGifEncoder();
                    gifEncoder.setFrameRate(10);
                    gifEncoder.setQuality(20);
                    gifEncoder.setDelay(100);
                    gifEncoder.start(selectedFile.getAbsolutePath());

                    for (Image frame : frames) {
                        final BufferedImage image = (BufferedImage) frame;
                        gifEncoder.addFrame(image);

                    }

                    gifEncoder.finish();
                }

            }
        });

        inf.setResizable(true);
        inf.setMaximizable(false);
        inf.setIconifiable(false);
        inf.setClosable(true);
        inf.setDefaultCloseOperation(JInternalFrame.DISPOSE_ON_CLOSE);
        inf.pack();
        inf.show();

    }


    public static void main(String... a) {
        new SceneLayoutApp();
        new ProgressBarAnimator();

    }

}
