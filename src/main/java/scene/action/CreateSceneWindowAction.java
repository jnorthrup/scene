package scene.action;

import scene.SceneLayoutApp;
import scene.ScenePanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

/**
 * User: jim
 * Date: May 14, 2009
 * Time: 1:00:28 AM
 */
public class CreateSceneWindowAction extends AbstractAction {


    public void actionPerformed(final ActionEvent actionEvent) {

        final JComboBox source = (JComboBox) actionEvent.getSource();

        final Object o = source.getSelectedItem();
        final String[] strings = o
                .toString().split(":");

        final JInternalFrame vw = new JInternalFrame();

        final JPanel iView = new ScenePanel();
        iView.setLayout(null);
        vw.setContentPane(iView);

        final Integer w = Integer.decode(strings[0]);
        final Integer h = Integer.decode(strings[1]);

        vw.setResizable(false);


        SceneLayoutApp.desktopPane.add(vw);

        vw.show();
        vw.setClosable(true);
        vw.setDefaultCloseOperation(JInternalFrame.DISPOSE_ON_CLOSE);

        final Dimension dimension = new Dimension(w, h);
        iView.setPreferredSize(dimension);
        iView.setMaximumSize(dimension);

        vw.pack();
    }

}
