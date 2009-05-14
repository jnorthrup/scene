package diagnostics;

import java.awt.*;
import java.applet.Applet;


/**
 * JMF Diagnostic Applet to test for different versions of JMF.
 */
public class JMFDiagnostics extends Applet
{
    TextArea ta;

    public void init()
    {
        resize(512, 212);
        setBackground(Color.white);
        setLayout(new BorderLayout());
        ta = new TextArea();
        add("Center", ta);
    }

    public void start()
    {
        boolean flag2 = false;
        float f2 = 0.0F;
        Class clz;
        String string1 = "JMF Diagnostics:\n\n";
        string1 = new StringBuffer(String.valueOf(string1)).append("Java 1.1 compliant browser.....").toString();
        ta.setText(string1);

	// Check for JDK.
        try
        {
            Class.forName("java.awt.event.ComponentAdapter");
            string1 = new StringBuffer(String.valueOf(string1)).append("Maybe\n").toString();
            string1 = new StringBuffer(String.valueOf(string1)).append("JMF classes.....").toString();
            ta.setText(string1);
        }
        catch (Throwable throwable1)
        {
            string1 = new StringBuffer(String.valueOf(string1)).append("No\n").toString();
            ta.setText(string1);
            return;
        }


	// Check for basic JMF classes.
        try
        {
            Class.forName("javax.media.Player");
            string1 = new StringBuffer(String.valueOf(string1)).append("Found\n").toString();
            boolean flag1 = false;
            float f1 = 0.0F;
            clz = null;
        }
        catch (Throwable throwable2)
        {
            string1 = new StringBuffer(String.valueOf(string1)).append("Not Found\n").toString();
            ta.setText(string1);
            return;
        }


	// Identify the versions.
        try
        {
            Class.forName("com.sun.media.util.LoopThread");
            flag2 = true;
            f2 = 0.0F;
        }
        catch (Throwable throwable3)
        {
        }


        if (flag2)
        {

	    // Identify the 2.0 minor versions.
            try
            {
                clz = Class.forName("QueryJMF20");
                f2 = 2.0F;
            }
            catch (Throwable throwable4)
            {
            }

            if (f2 < 2.0F)
            {
		// Check for v1.1
                try
                {
                    clz = Class.forName("QueryJMF11");
                    f2 = 1.1F;
                }
                catch (Throwable throwable5)
                {
                }
            }

            if (f2 < 1.1)
            {
		// Check for v1.0
                try
                {
                    clz = Class.forName("QueryJMF10");
                    f2 = 1.02F;
                }
                catch (Throwable throwable6)
                {
                }
            }

            if (f2 >= 1.02 && clz != null)
            {
                QueryJMF queryJMF = null;
                try
                {
                    queryJMF = (QueryJMF)clz.newInstance();
                    String string2 = queryJMF.getVersion();
                    String string3 = queryJMF.getDetails();
                    string1 = "\nJMF Version... " + string2 + "\n\n";
                    string1 = new StringBuffer(String.valueOf(string1)).append(string3).toString();
                    ta.setText(string1);
                    return;
                }
                catch (Throwable throwable7)
                {
                }
            }

            string1 = new StringBuffer(String.valueOf(string1)).append("\nJMF Version... pre 1.0.2\n").toString();
            string1 = new StringBuffer(String.valueOf(string1)).append("Please upgrade to a newer version\n").toString();
            ta.setText(string1);
            return;
        }

        string1 = new StringBuffer(String.valueOf(string1)).append("\nUnknown JMF implementation!\n").toString();
        ta.setText(string1);
    }

    public void stop()
    {
    }

    public JMFDiagnostics()
    {
    }
}
