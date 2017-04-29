package util;

/**
 * In dieser Klasse sind alle TAGs enthalten die der Chat benutzt.
 * @author Höling
 *
 */
public class TAG {
	
	private static final String praefix = "<";
	private static final String praefixClose = "</";
	private static final String suffix = ">";
	
	public static final String from = praefix+"from"+suffix;
	public static final String fromClose = praefixClose+from+suffix;
	
	public static final String to = praefix+"to"+suffix;
	public static final String toClose = praefixClose+to+suffix;

}
