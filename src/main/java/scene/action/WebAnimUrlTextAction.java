package scene.action;

import scene.dnd.WebViewDropTargetListener;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;
import java.net.MalformedURLException;

/**
 * Copyright hideftvads.com 2009 all rights reserved.
 * <p/>
 * User: jim
 * Date: May 21, 2009
 * Time: 2:44:43 PM
 */
public class WebAnimUrlTextAction implements ActionListener {
    private WebViewDropTargetListener webViewDropTargetListener;

    public WebAnimUrlTextAction(WebViewDropTargetListener webViewDropTargetListener) {
        this.webViewDropTargetListener = webViewDropTargetListener;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        try {
            this.webViewDropTargetListener.frame.updateEditor(new URL(webViewDropTargetListener.getFrame().getUrlText().getText()));
        } catch (MalformedURLException e1) {
            e1.printStackTrace();  //TODO: verify for a purpose
        }
    }
}
