package scene.anim;

import javax.accessibility.*;
import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.image.*;
import java.io.*;
import java.net.*;

/**
 * User: jim
 * Date: Jun 29, 2009
 * Time: 2:07:06 AM
 */
public interface WebAnimator extends ImageObserver, MenuContainer,    Serializable,   Accessible, WindowConstants, RootPaneContainer {
    JToolBar getBar();

    void setBar(JToolBar bar);

    JTextField getUrlText();

    void setUrlText(JTextField urlText);

    JComponent getJEditorPane();

    JScrollPane getJScrollPane();

    void setJScrollPane(JScrollPane jScrollPane);

    JLabel getQrCode();

    void setQrCode(JLabel qrCode);

    JPanel getPanel();

    void setPanel(JPanel panel);

    void updateEditor(URL url);

    JComponent getHtmlPanel();

    void setSize(int i, int i1);

    void show();

    void pack();

    public static class MyChangeListener implements ChangeListener {
        private final JScrollBar verticalScrollBar;

        public MyChangeListener(JScrollBar verticalScrollBar) {
            this.verticalScrollBar = verticalScrollBar;
        }

        @Override
        public void stateChanged(ChangeEvent e) {
            final int value = ((JSlider) e.getSource()).getValue();
            verticalScrollBar.setValue(value);
        }
    }
}
