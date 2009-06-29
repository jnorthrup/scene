package scene.action;

import scene.SceneLayoutApp;
import scene.anim.*;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.*;

  
public class CreateWebViewV1Action extends AbstractAction {

    public CreateWebViewV1Action() {
        super("WebV1");
    }

    /**
     * Invoked when an action occurs.
     */
    @Override
    public void actionPerformed(ActionEvent e) {

        final WebAnimator  animator = new WebAnimatorV1();

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                SceneLayoutApp.desktopPane.add((Component) animator);
                animator.setSize(400, 120);
                animator.show();
                animator.pack();
            }
        });
    } ;

};