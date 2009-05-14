package scene.action;

import javax.swing.*;
import java.awt.event.*;

/**
 * User: jim
* Date: May 14, 2009
* Time: 1:00:18 AM
*/             public 
class QuitAction extends AbstractAction {
    public QuitAction() {
        super("Quit");
    }

    public void actionPerformed(ActionEvent actionEvent) {

        System.exit(0);


    }
}
