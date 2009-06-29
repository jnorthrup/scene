package scene.anim;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.*;
import java.net.*;
import java.nio.charset.*;

/**
 * User: jim
 * Date: Jun 29, 2009
 * Time: 2:19:24 AM
 */
public abstract class WebAnimatorImpl extends JInternalFrame implements WebAnimator {
    private JToolBar bar = new JToolBar();
    private JTextField urlText = new JTextField();
    private JLabel qrCode = new JLabel();
    private JPanel panel = new JPanel(new BorderLayout());
    private JPanel content = new JPanel(new BorderLayout());
    private DataFlavor[] dataFlavors = new DataFlavor[]{
            DataFlavor.javaFileListFlavor,
            new DataFlavor("application/x-java-url;class=java.net.URL", "URL"),
            new DataFlavor("text/x-uri-list; class=java.util.List", "URI List"),
            new DataFlavor("text/uri-list; class=java.util.List", "URI List"),
    };
    private JSlider startSlider = new JSlider();
    private JSlider stopSlider = new JSlider();
    private static final Charset UTF8 = Charset.forName("UTF8");
    JScrollPane jScrollPane = null;

    public WebAnimatorImpl(String title) {
        super(title);
    }

    protected abstract void init();

    public JToolBar getBar() {
        return bar;
    }

    public void setBar(JToolBar bar) {
        this.bar = bar;
    }

    public JTextField getUrlText() {
        return urlText;
    }

    public void setUrlText(JTextField urlText) {
        this.urlText = urlText;
    }

    public JComponent getJEditorPane() {
        return this.getHtmlPanel();
    }

    public JScrollPane getJScrollPane() {
        return jScrollPane;
    }

    public void setJScrollPane(JScrollPane jScrollPane) {
        this.jScrollPane = jScrollPane;
    }

    public JLabel getQrCode() {
        return qrCode;
    }

    public void setQrCode(JLabel qrCode) {
        this.qrCode = qrCode;
    }

    public JPanel getPanel() {
        return panel;
    }

    public void setPanel(JPanel panel) {
        this.panel = panel;
    }

    public abstract void updateEditor(URL url);

    public abstract JComponent getHtmlPanel();
    public abstract  void setHtmlPanel(JComponent htmlPanel);

    JPanel getContent() {
        return content;
    }

    void setContent(JPanel content) {
        this.content = content;
    }

    DataFlavor[] getDataFlavors() {
        return dataFlavors;
    }

    void setDataFlavors(DataFlavor[] dataFlavors) {
        this.dataFlavors = dataFlavors;
    }

    public JSlider getStartSlider() {
        return startSlider;
    }

    void setStartSlider(JSlider startSlider) {
        this.startSlider = startSlider;
    }

    public JSlider getStopSlider() {
        return stopSlider;
    }

    void setStopSlider(JSlider stopSlider) {
        this.stopSlider = stopSlider;
    }

    static Charset getUtf8() {
        return UTF8;
    }

 }
