package diagnostics;

import com.sun.media.*;

import javax.media.*;


/**
 * Check for version JMF 2.0.
 */
public class 
        QueryJMF20 implements QueryJMF
{
    public String getVersion()
    {
        return Manager.getVersion();
    }

    public String getDetails()
    {
        String string = "";
        try
        {
            Class.forName("com.sun.media.renderer.video.XILRenderer");
            string = new StringBuffer(String.valueOf(string)).append("Solaris Build\n").toString();
        }
        catch (Throwable throwable2)
        {
            try
            {
                Class.forName("com.sun.media.protocol.vfw.DataSource");
                string = new StringBuffer(String.valueOf(string)).append("Win32 Build\n").toString();
            }
            catch (Throwable throwable1)
            {
                string = new StringBuffer(String.valueOf(string)).append("All Java Build\n").toString();
            }
        }
        try
        {
            JMFSecurityManager.loadLibrary("jmutil");
            string = new StringBuffer(String.valueOf(string)).append("Native Libraries Found\n").toString();
        }
        catch (UnsatisfiedLinkError e)
        {
            string = new StringBuffer(String.valueOf(string)).append("Native Libraries Not Found\n").toString();
        }
        return string;
    }

    public QueryJMF20()
    {
    }

    static 
    {
        try
        {
            Class.forName("javax.media.Codec");
        }
        catch (Throwable throwable)
        {
            throw new RuntimeException("Not JMF 2.0");
        }
    }
}
