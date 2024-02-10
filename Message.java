import java.awt.Color;
import java.awt.Point;
import java.util.*;

/**
 * Message class for handling requests which are sent as strings to the receiver
 * Parses the string requests and bins the items into arrays that inform certain functionalities
 * @author joonpark @ Reseun McClendon
 *
 */
public class Message {
	String message;	//For holding the string message
	String[] items;	//For holding the parsed items from the message
	String[] coordinates;
	private ArrayList<Point> mousepoints;
	
	/**
	 * Message constructor
	 */
	public Message(String msg) {
		message = msg; 
		
		/*
		 * Split the string by its spaces, the messages should be formatted so as
		 * to retrieve the request type, shape type, parameters, color, index in the 
		 * shape list, and if applicable, the dx dy values for movement in doing so.
		 */
		items = message.split(" "); 
	}
	
	/**
	 * Method that does all of the parsing and processing of the parsed items
	 */
	public void process (Sketch sketch) {
		//Take the first and second items and set them equal to the request type and shape type respectively
		String requestType = items[0];
		String shapeType = items[1];

		
		//If the request type is to add a new shape to the shape list
		if (requestType.equals("AddNew")) {
			//Obtain the color and index from the last two item arrays for all shape types
			Color color = Color.decode(items[items.length - 2]); 
			int index = Integer.parseInt(items[items.length - 1]);
			
			//For each shape, create a new object of the shape with the relevant parameters and add to the sketch
			if (shapeType.equals("ellipse")) {
				//The 3rd-6th items are the parameters (corners)
				int x1 = Integer.parseInt(items[2]); 
				int y1 = Integer.parseInt(items[3]); 
				int x2 = Integer.parseInt(items[4]); 
				int y2 = Integer.parseInt(items[5]); 
				Ellipse ellipse = new Ellipse(x1, y1, x2, y2, color);
				sketch.AddNew(ellipse);
			}
			
			if (shapeType.equals("rectangle")) {
				//The 3rd-6th items are the parameters (corners)
				int x1 = Integer.parseInt(items[2]); 
				int y1 = Integer.parseInt(items[3]); 
				int x2 = Integer.parseInt(items[4]); 
				int y2 = Integer.parseInt(items[5]); 
				Rectangle rectangle = new Rectangle(x1, y1, x2, y2, color);
				sketch.AddNew(rectangle);
			}
			
			if (shapeType.equals("segment")) {
				//The 3rd-6th items are the parameters (corners)
				int x1 = Integer.parseInt(items[2]); 
				int y1 = Integer.parseInt(items[3]); 
				int x2 = Integer.parseInt(items[4]); 
				int y2 = Integer.parseInt(items[5]); 
				Segment segment = new Segment(x1, y1, x2, y2, color); 
				sketch.AddNew(segment);
			}
			
			if (shapeType.equals("freehand")) {
				//Create a new instance of mousepoints
				mousepoints = new ArrayList<Point>();
				
				//Parse out the x and y coordinates for each point and create a new point using them
				for (int i = 2; i < items.length - 2; i = i+2) {
					int px = Integer.parseInt(items[i]);
					int py = Integer.parseInt(items[i + 1]);
					Point point = new Point(px, py);
					
					//Add the point to mousepoints
					mousepoints.add(point);
				}
				//Create a new polyline object and pass in the arraylist
				Polyline polyline = new Polyline(mousepoints, color);
				sketch.AddNew(polyline);
			}
		}

		
		//If the request type is to recolor
		if (requestType.equals("Recolor")) {
			//Again obtain the color and the index from the last two arrays and recolor the shape in the sketch
			Color color = Color.decode(items[items.length - 2]); 
			int index = Integer.parseInt(items[items.length - 1]);
			sketch.Recolor(index, color);
		}
		
		//If the request type is to delete
		if (requestType.equals("Delete")) {
			//Obtain the index from the last array and delete it from the sketch
			int index = Integer.parseInt(items[items.length - 1]);
			sketch.Delete(index);
		}
		
		//If the request type is to move
		if (requestType.equals("Move")) {
			//Obtain the dx, dy, and the index from the relevant arrays and move the shape in the corresponding sketch shape list
			int index = Integer.parseInt(items[items.length - 1]);
			int dx = Integer.parseInt(items[items.length - 3]);
			int dy = Integer.parseInt(items[items.length - 2]);
			sketch.Move(index, dx, dy);
		}
	}
	
	/**
	 * Method for turning the message into a string, simply return the string message form
	 */
	public String toString() {
		return message;
	}
}
