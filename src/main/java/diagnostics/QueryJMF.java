package diagnostics;

/**
 * Query for the particular version of JMF.
 */
public interface QueryJMF
{
    public static final String NATIVE = "Native Libraries Found\n";
    public static final String NONATIVE = "Native Libraries Not Found\n";

    public abstract String getVersion();
    public abstract String getDetails();
}
