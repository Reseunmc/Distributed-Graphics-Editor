import java.util.*;
import java.awt.*;

/**
 * Sketch class for essentially keeping track of all the shapes in the collaborative graphical editor
 * Uses an arraylist in order to keep track of each shape, each index represents each shape's global ID
 * @author joonpark & Reseun McClendon
 */
public class Sketch {
	private ArrayList<Shape> shapelist;	//ArrayList instance variable
	
	/**
	 * Sketch constructor
	 */
	public Sketch() {
		//Initialize the shapelist
		shapelist = new ArrayList<Shape>();
	}
	
	/**
	 * Method for obtaining the size of the shapelist
	 */
	public synchronized int size() {
		return shapelist.size();
	}
	
	/**
	 * Method for obtaining a shape at a specific index of the shapelist
	 */
	public synchronized Shape get (int index) {
		return shapelist.get(index);
	}
	
	
	/**
	 * Method for finding the index of the shape that a point is in
	 */
	public synchronized int find (int x, int y) {
		//Index through the shapelist
		for (int i = shapelist.size() - 1; i >= 0; i--) {
			
			//If the shape exists and the shape contains the point
			if (shapelist.get(i) != null && shapelist.get(i).contains(x, y)) {
				//Return the index pertaining to the shape
				return i;
			}
		}
		//Otherwise return -1 for no shape containing the point
		return -1;
	}
	
	/**
	 * Method for adding a new shape to the shape list
	 */
	public synchronized void AddNew(Shape shape) {
		shapelist.add(shape);
	}
	
	/**
	 * Method for recoloring a shape within the shape list
	 */
	public synchronized void Recolor(int i, Color color) {
		shapelist.get(i).setColor(color);
	}
	
	/**
	 * Method for moving a shape in the shape list
	 */
	public synchronized void Move(int idx, int dx, int dy) { 
		shapelist.get(idx).moveBy(dx, dy); 
	}
	
	/**
	 * Method for deleting a shape within the shape list
	 */
	public synchronized void Delete (int i) {
		if (i != -1) {
			shapelist.set(i, null);
		}
	}
	
	/**
	 * Method for drawing everything in the shapelist
	 */
	public synchronized void draw (Graphics g) {
		for (int i = 0; i < shapelist.size(); i++) {
			if (shapelist.get(i) != null) {
				shapelist.get(i).draw(g);
			}
		}
	}
}
	
