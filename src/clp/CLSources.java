package clp;

import java.io.InputStream;

public class CLSources {

	public static String getMandelbrotSource()
	{
		InputStream input = CLSources.class.getResourceAsStream("/clsrc/mandelbrot.cl");
		
		java.util.Scanner s = new java.util.Scanner(input);
		s.useDelimiter("\\A");
		String ret = s.hasNext() ? s.next() : "";
		s.close();
		return ret;
	}
}
