import java.io.*;
import java.awt.*;
import java.net.Socket;

/**
 * Handles communication to/from the server for the editor
 * 
 * @author Chris Bailey-Kellogg, Dartmouth CS 10, Fall 2012
 * @author Chris Bailey-Kellogg; overall structure substantially revised Winter 2014
 * @author Travis Peters, Dartmouth CS 10, Winter 2015; remove EditorCommunicatorStandalone (use echo server for testing)
 */
public class EditorCommunicator extends Thread {
	private PrintWriter out;		// to server
	private BufferedReader in;		// from server
	protected Editor editor;		// handling communication for

	/**
	 * Establishes connection and in/out pair
	 */
	public EditorCommunicator(String serverIP, Editor editor) {
		this.editor = editor;
		System.out.println("connecting to " + serverIP + "...");
		try {
			Socket sock = new Socket(serverIP, 4242);
			out = new PrintWriter(sock.getOutputStream(), true);
			in = new BufferedReader(new InputStreamReader(sock.getInputStream()));
			System.out.println("...connected");
		}
		catch (IOException e) {
			System.err.println("couldn't connect");
			System.exit(-1);
		}
	}

	/**
	 * Sends message to the server
	 */
	public void send(String msg) {
		out.println(msg);
	}

	/**
	 * Keeps listening for and handling (your code) messages from the server
	 */
	public void run() {
		try {
			//Handle messages
			//String for holding the line
			String line;
			
			//While there is a line to be read
			while ((line = in.readLine()) != null) {
				//Process the line as a message
				Message msg = new Message(line);
				msg.process(editor.getSketch());
				editor.repaint();
			}
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		finally {
			System.out.println("server hung up");
		}
	}	

	// Send editor requests to the server
	
	//Request for adding a new shape to the sketch
	public void AddNew(Shape shape) {
		send("AddNew " + shape.toString() + " -1");
	}
	
	//Request for recoloring
	public void Recolor(int i, Color c) {
		send("Recolor " + editor.getSketch().get(i).toString() + " " + c.hashCode() + " " + Integer.toString(i));
	}
	
	//Request for moving a shape
	public void Move(int i, int dx, int dy) { 
		send("Move " + editor.getSketch().get(i).toString() + " " + dx + " " + dy + " " + Integer.toString(i) ); 	
	}
	
	//Request for deleting a shape
	public void Delete(int i) {
		send("Delete " + editor.getSketch().get(i).toString() + " "+ Integer.toString(i)); 
	}
}
