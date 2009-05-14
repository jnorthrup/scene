package diagnostics;

import com.sun.media.*;


/**
 * Check for version JMF 1.1.
 */
public class QueryJMF11 implements QueryJMF
{
    public String getVersion()
    {
        return "1.1";
    }

    public String getDetails()
    {
        String string = "";
        try
        {
            Class.forName("com.sun.media.blitter.xlib.Blitter");
            string = new StringBuffer(String.valueOf(string)).append("Solaris Build\n").toString();
        }
        catch (Throwable throwable1)
        {
        }
        try
        {
            Class.forName("com.sun.media.blitter.directx.Blitter");
            string = new StringBuffer(String.valueOf(string)).append("Win32 Build\n").toString();
        }
        catch (Throwable throwable2)
        {
        }
        try
        {
            JMFSecurityManager.loadLibrary("jmindeo");
            string = new StringBuffer(String.valueOf(string)).append("Native Libraries Found\n").toString();
        }
        catch (UnsatisfiedLinkError e)
        {
            string = new StringBuffer(String.valueOf(string)).append("Native Libraries Not Found\n").toString();
        }
        return string;
    }

    public QueryJMF11()
    {
    }

    static 
    {
        try
        {
            Class.forName("com.sun.media.codec.video.h263.H263Decoder");
        }
        catch (Throwable throwable)
        {
            throw new RuntimeException("Not JMF 1.1");
        }
    }
}
