package org.tiaacref.ad;

import static java.util.regex.Pattern.compile;

import java.util.Arrays;
import java.util.Comparator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.maven.artifact.versioning.ComparableVersion;
 
/**
 * <p>Utility to compare version.</p>
 *
 * @author yclian
 * @since 20120507
 * @version 20120507
 */
public final class Version {
 
	private static final Pattern VERSION = compile("([\\d\\.]+\\.)*(\\d+)$");

	private static Comparator<String> ALPHABETICAL_ORDER = new Comparator<String>() {
	    public int compare(String str1, String str2) {
	        int retValue = 0;
	    	if(str1.replaceAll(" ([\\d\\.]+\\.)*(\\d+)$", "").equals(str2.replaceAll(" ([\\d\\.]+\\.)*(\\d+)$", "")))
	    	{
	    		String str1Version = str1.replaceAll(".* ","");
	    		String str2Version = str1.replaceAll(".* ","");
	    		retValue = compare(str1Version, str2Version);
	    		System.out.println(str1 + ">" + str1Version + "-(" + retValue + ")-" + str2 + ">" + str2Version);
	    	}
	    	else
	    	{
	    		retValue = str1.compareTo(str2);
	    		System.out.println(str1  + "-(" + retValue + ")-" + str2);
	    	}
	    	return retValue;
	    }
	};

	private static final Pattern PATTERN_APPROXIMATION = compile("^([\\d\\.]+\\.)*(\\d+)$");
 
    private ComparableVersion mVersion;
 
    private Version(String v) {
        mVersion = new ComparableVersion(v);
    }
 
    public static Version compare(String v) {
        return new Version(v);
    }
 
    /**
     * <p>Return the result of {@link Comparable#compareTo(Object)}. Very limited as it doesn't support {@code &gt;=}, {@code &lt;=} and {@code ~&gt;}.</p>
     *
     * @param v
     * @return
     */
    public int with(String v) {
        return mVersion.compareTo(new ComparableVersion(v));
    }
 
    public boolean eq(String v) { return with(v) == 0; }
    public boolean le(String v) { int c = with(v); return c == 0 || c == -1; }
    public boolean lt(String v) { return with(v) == -1; }
    public boolean ge(String v) { int c = with(v); return c == 0 || c == 1; }
    public boolean gt(String v) { return with(v) == 1; }
 
    /**
     * <p>Approximately greater than, inspired by (and works exactly like) RubyGems.</p>
     *
     * @see <a href="http://docs.rubygems.org/read/chapter/16">RubyGems Manuals - Specifying Versions</a>
     * @param v
     * @return
     */
    public boolean agt(String v) {
        return ge(v) && lt(approximateUpper(v));
    }
 
    private String approximateUpper(String v) {
        Matcher m = PATTERN_APPROXIMATION.matcher(v.split("\\.\\d+$")[0]);
        if (m.find()) {
            int i = Integer.parseInt(m.group(2));
            return (null != m.group(1) ? m.group(1) : "") + ++i;
        } else {
            return v;
        }
    }
    
	public static void test(String[] args)
    {
		
		Arrays.sort(args, ALPHABETICAL_ORDER);

		System.out.println(compare("1.0.1").with("1.0.2"));
		System.out.println(compare("2.5").with("2.11"));
    
    }
}