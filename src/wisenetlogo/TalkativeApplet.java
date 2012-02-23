package wisenetlogo;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;

import org.nlogo.api.CompilerException;
import org.nlogo.lite.Applet;

public class TalkativeApplet extends Applet {

	private static final long serialVersionUID = 1L;
	public boolean exportPending = false;

	public String state = null;
	public String getState() { if (exportPending) return "Export Pending"; else return state; }
	
	public void importWorldFromString(String str) 
	{	
		try {
			StringReader sr = new StringReader(str);
			panel().workspace().importWorld( sr );
			panel().repaint();
			this.repaint();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void exportWorldState()
	{
		exportPending = true;
		org.nlogo.awt.EventQueue.invokeLater( new Runnable() {
			public void run() {
				StringWriter sw = new StringWriter();
				panel().workspace().exportWorld(new PrintWriter(sw));//exportWorld(new PrintWriter(sw) );
				state = sw.toString();
				exportPending = false;
			} });
	}


	public void command( String cmd )
	{
		try {
			panel().command( cmd );
		} catch (CompilerException e) {
			e.printStackTrace();
		}
	}
	
	public void commandLater( String cmd )
	{
		try {
			panel().commandLater( cmd );
		} catch (CompilerException e) {
			e.printStackTrace();
		}
	}
	
	public Object report( String cmd )
	{
		Object toreturn = null;
		try {
			toreturn = panel().report( cmd );
		} catch (CompilerException e) {
			e.printStackTrace();
		}
		return toreturn;
	}
	
	
}
