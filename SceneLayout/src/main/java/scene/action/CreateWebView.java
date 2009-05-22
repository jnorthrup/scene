package scene.action;

import scene.SceneLayoutApp;
import scene.anim.WebAnimator;

import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 * Copyright hideftvads.com 2009 all rights reserved.
 * <p/>
 * User: jim
 * Date: May 17, 2009
 * Time: 12:51:29 AM
 */
public class CreateWebView extends AbstractAction {

    public CreateWebView() {
        super("Web");
    }

    /**
     * Invoked when an action occurs.
     */
    @Override
    public void actionPerformed(ActionEvent e) {

        final WebAnimator animator = new WebAnimator();

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                SceneLayoutApp.desktopPane.add(animator);
                animator.setSize(400, 120);
                animator.show();
                animator.pack();
            }
        });
    }

    ;;

};
