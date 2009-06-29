package scene.action;

import scene.anim.*;
import scene.*;

import javax.swing.*;
import java.awt.event.*;
import java.awt.*;

/**
 * Copyright hideftvads.com 2009 all rights reserved.
 * <p/>
 * User: jim
 * Date: May 17, 2009
 * Time: 12:51:29 AM
 */
public class CreateWebViewV2Action extends AbstractAction {

    public CreateWebViewV2Action() {
        super("WebV2");
    }

    /**
     * Invoked when an action occurs.
     */
    @Override
    public void actionPerformed(ActionEvent e) {

        final WebAnimator  animator = new WebAnimatorV2();

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                SceneLayoutApp.desktopPane.add((Component) animator);
                animator.setSize(400, 120);
                animator.show();
                animator.pack();
            }
        });
    }

    ;;

}
