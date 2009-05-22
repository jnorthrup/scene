package scene.anim;

import scene.action.RecordWebScrollerAction;
import scene.dnd.WebViewDropTargetListener;

import javax.swing.*;
import java.awt.*;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;

/**
 * Copyright hideftvads.com 2009 all rights reserved.
 * <p/>
 * User: jim
 * Date: May 21, 2009
 * Time: 1:26:29 PM
 */
public class WebAnimator extends JInternalFrame {
    private JToolBar bar = new JToolBar();
    private JTextField urlText = new JTextField();
    private JLabel qrCode = new JLabel();
    private JPanel panel = new JPanel(new BorderLayout());
    private JPanel content = new JPanel(new BorderLayout());
    private JEditorPane jEditorPane = new JEditorPane();
    private JScrollPane jScrollPane = new JScrollPane(jEditorPane);

    public WebAnimator(Object... a) {
        super("Create Web Animation");
        this.init();
    }

    private void init() {

        jEditorPane.setEditable(false);
        panel.add(getQrCode(), BorderLayout.WEST);
        jScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        panel.add(jScrollPane, BorderLayout.CENTER);
        bar.add(urlText);
        content.add(panel, BorderLayout.CENTER);
        content.add(bar, BorderLayout.NORTH);
        setContentPane(content);
        final JInternalFrame f = this;

        bar.add(new RecordWebScrollerAction(this));
//                f.setPreferredSize(getPreferredSize());
        f.setMaximizable(true);
        f.setClosable(true);
        f.setIconifiable(true);
        f.setResizable(true);
//        f.setSize(400, 400);
        f.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

        f.setDropTarget(new DropTarget(f, DnDConstants.ACTION_LINK, new WebViewDropTargetListener((WebAnimator) f), true));
    }


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

    public JEditorPane getJEditorPane() {
        return jEditorPane;
    }

    public void setJEditorPane(JEditorPane jEditorPane) {
        this.jEditorPane = jEditorPane;
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
}
