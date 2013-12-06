package test;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;

public class testExec {

	static public void main(String[] args) throws Exception {
		String command = "echo $LANG";
		String[] cmds = { "/bin/sh", "-c", new String(command.getBytes(), "utf8") };
		try {
			Process ps = Runtime.getRuntime().exec(cmds);
			System.out.print(loadStream(ps.getInputStream()));
			System.err.print(loadStream(ps.getErrorStream()));
		} catch(IOException ioe) {
			ioe.printStackTrace();
		}
	}

 // read an input-stream into a String
	static String loadStream(InputStream in) throws IOException {
		int ptr = 0;
		in = new BufferedInputStream(in);
		StringBuffer buffer = new StringBuffer();
		while( (ptr = in.read()) != -1 ) {
			buffer.append((char)ptr);
		}
		return buffer.toString();
	}

} 
