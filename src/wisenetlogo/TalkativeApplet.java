package wisenetlogo;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;

import javax.swing.SwingUtilities;

import netscape.javascript.JSObject;

import org.nlogo.api.CompilerException;
import org.nlogo.api.NetLogoAdapter;
import org.nlogo.job.JobManager;
import org.nlogo.lite.Applet;

public class TalkativeApplet extends Applet {

	private static final long serialVersionUID = 1L;
	
	//lookup list of listeners added by the javascript in the embedding page.
	private HashMap<String,JSListener> jsEventListeners = new HashMap<String,JSListener>();
	
	//add a callback for all netlogo events to this js function
	public void addJSListener( String callbackname )
	{
		JSListener alistener = new JSListener( callbackname, this );
		this.panel().workspace().listenerManager.addListener(alistener);
		jsEventListeners.put( callbackname, alistener );
	}
	
	//stop receiving events at this callback
	public void removeJSListener( String callbackname )
	{
		JSListener toremove = jsEventListeners.get(callbackname);
		if ( toremove != null)
		{
			this.panel().workspace().listenerManager.removeListener(toremove);
			jsEventListeners.remove(callbackname);
		}
	}
	
	//stop receiving any events to JS callbacks
	public void removeAllJSListeners()
	{
		ArrayList<String> toremove = new ArrayList<String>();
		for (String call : jsEventListeners.keySet() )
		{
			toremove.add(call);
		}
		for (String cback : toremove)
		{
			removeJSListener( cback );
		}
	}
	
	//class for sending notice of netlogo events to javascript.
	public class JSListener implements NetLogoAdapter {
		
		private JSObject window;
		private String callback;
		
		public JSListener( String callbackfunction, Applet appl )
		{
			window = JSObject.getWindow(appl);
			callback = callbackfunction;
		}
		
		@Override
		public void buttonPressed(String buttonName) {	
			Object[] args = { "button-pressed", buttonName };
			window.call(callback, args);
		}

		@Override
		public void buttonStopped(String buttonName) {
			Object[] args = { "button-stopped", buttonName };
			window.call(callback, args);
		}

		@Override
		public void chooserChanged(String name, Object value, boolean arg2) {
			Object[] args = { "chooser-changed", name, value };
			window.call(callback, args);
		}

		@Override
		public void codeTabCompiled(String arg0, CompilerException arg1) {
			//not sent
		}

		@Override
		public void commandEntered(String arg0, String arg1, char arg2,
				CompilerException arg3) {
			//not sent
		}

		@Override
		public void inputBoxChanged(String name, Object value, boolean arg2) {
			Object[] args = { "input-box-changed", name, value };
			window.call(callback, args);
		}

		@Override
		public void modelOpened(String arg0) {
			//not sent
		}

		@Override
		public void possibleViewUpdate() {
			//not sent
		}

		@Override
		public void sliderChanged(String name, double value, double min,
				double incr, double max, boolean arg5, boolean arg6) {
			Object[] args = { "slider-changed", name, value, min, incr, max  };
			window.call(callback, args);
		}

		@Override
		public void switchChanged(String name, boolean value, boolean arg2) {
			String val = "NO";
			if ( value )
			{ val = "YES"; }
			Object[] args = { "switch-changed", name, val  };
			window.call(callback, args);
		}

		@Override
		public void tickCounterChanged(double value) {
			Object[] args = { "tick-counter-changed", value  };
			window.call(callback, args);
		}
	}
	
	
	
	
	
	//import this string as a NL workd, and then call the js funciton when it's complete.
	public void importWorldFromString(final String str, final String callback) 
	{	
		final JSObject window = JSObject.getWindow(this);
		org.nlogo.awt.EventQueue.invokeLater( new Runnable() {
			public void run() {
				try {
					StringReader sr = new StringReader(str);
					panel().workspace().importWorld( sr );
					panel().commandLater("display");
					window.call(callback, null);
					//window.eval(callback);
				} catch (IOException e) {
					e.printStackTrace();
				} catch (CompilerException e) {
					e.printStackTrace();
				}
			} } );
	}

	
	//export world to a string variable which is passed as an argument to the js callback function
	public void exportWorldStateAsynchronously( final String callback )
	{
		final JSObject window = JSObject.getWindow(this);
		org.nlogo.awt.EventQueue.invokeLater( new Runnable() {
			public void run() {
				StringWriter sw = new StringWriter();
				panel().workspace().exportWorld(new PrintWriter(sw));//exportWorld(new PrintWriter(sw) );
				String state = sw.toString();
				Object[] args = { state };
				window.call(callback, args);
			} });
	}
	
	
//	public void exportWorldStateAsync( final String variableName, final String callback )
//	{
//		final JSObject window = JSObject.getWindow(this);
//		org.nlogo.awt.EventQueue.invokeLater( new Runnable() {
//			public void run() {
//				StringWriter sw = new StringWriter();
//				panel().workspace().exportWorld(new PrintWriter(sw));//exportWorld(new PrintWriter(sw) );
//				String state = sw.toString();
//				window.setMember(variableName, state);
//				window.eval(callback);
//			} });
//	}
	

	//send a netlogo command asynchronously; receive notice of completion at this callback
	public void asynchronousCommand( final String cmd, final String callback )
	{
		final JSObject window = JSObject.getWindow(this);
		
		if (  SwingUtilities.isEventDispatchThread() )
		{
			Thread t = new Thread("command thread off of EDT") {
				public void run() {
					try {
						panel().command( cmd );
						window.call(callback, null);
					} catch (CompilerException e) {
						e.printStackTrace();
					}
				}

			};
			t.start();
		}
		else
		{
			try {
				panel().command( cmd );
				window.call(callback, null);
			} catch (CompilerException e) {
				e.printStackTrace();
			}
		}
	}
	
	
	//evaluate a netlogo report asynchronously; return the result to this callback as the argument
	public void asynchronousReport( final String cmd, final String callback )
	{
		final JSObject window = JSObject.getWindow(this);

		if (  SwingUtilities.isEventDispatchThread() )
		{
			Thread t = new Thread("reporter thread off of EDT") {
				public void run() {
					Object retval = null;
					try {
						retval = panel().report( cmd );
						Object[] args = { retval };
						window.call(callback, args);
					} catch (CompilerException e) {
						e.printStackTrace();
					}
				}

			};
			t.start();
		}
		else
		{
			Object retval = null;
			try {
				retval = panel().report( cmd );
				Object[] args = { retval };
				window.call(callback, args);
			} catch (CompilerException e) {
				e.printStackTrace();
			}
		}	
	}
	
	
	
	
	
}
