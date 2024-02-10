//
//import java.awt.Color;
//
//import java.awt.Graphics;
//import java.awt.Point;
//import java.util.ArrayList;
//import java.util.List;
//
///**
// * A multi-segment Shape, with straight lines connecting "joint" points -- (x1,y1) to (x2,y2) to (x3,y3) ...
// * 
// * @author Chris Bailey-Kellogg, Dartmouth CS 10, Spring 2016
// * @author CBK, updated Fall 2016
// */
//public class Polyline implements Shape {
//	private int x1, y1;		// upper left and lower right
//	private Color color;
//	private List<Integer> mousepoints_x;
//	private List<Integer> mousepoints_y;
//	// TODO: YOUR CODE HERE
//
//	public Polyline(int x1, int y1, Color color){
//		this.color=color;
//		this.x1 = x1;
//		this.y1 = y1;
//		mousepoints_y= new ArrayList<Integer>();
//		mousepoints_x= new ArrayList<Integer>();
//		mousepoints_x.add(x1);
//		mousepoints_y.add(y1);
//	}
//	
//	public void update(int x, int y) {
//
//		mousepoints_x.add(x);
//		mousepoints_y.add(y);
//		System.out.println(mousepoints_x);
//		System.out.println(mousepoints_y);
//		System.out.println(" ");
//	}
//	
//	@Override
//	public void moveBy(int dx, int dy) {
//		x1 += dx; y1 += dy;
//		
//	}
//
//	@Override
//	public Color getColor() {
//		return color;
//	}
//
//	@Override
//	public void setColor(Color color) {
//		this.color=color;
//	}
//	
//	@Override
//	public boolean contains(int x, int y) {
//		for (int i=0;i<mousepoints_x.size()-1; i++){
//			if(Segment.pointToSegmentDistance(x, y, mousepoints_x.get(i), mousepoints_y.get(i), mousepoints_x.get(i+1), mousepoints_y.get(i+1)) <= 3)
//				return true;
//			}
//		return false;
//		}
//			
//	
//
//	@Override
//	public void draw(Graphics g) {
//		System.out.println(mousepoints_x);
//		System.out.println(mousepoints_y);
//		System.out.println(" ");
//	
//		g.setColor(color);
//		for (int i=0;i<mousepoints_x.size() -1; i++){
//			g.drawLine(mousepoints_x.get(i), mousepoints_y.get(i), mousepoints_x.get(i+1), mousepoints_y.get(i+1));
//
//			
//		}
//	
//	}
//
//	@Override
//	public String toString() {
//		return "freehand "+x1+" "+y1+" "+color.getRGB();
//	}
//}

import java.awt.Color;

import java.awt.Graphics;
import java.awt.Point;
import java.util.ArrayList;

/**
 * A multi-segment Shape, with straight lines connecting "joint" points -- (x1,y1) to (x2,y2) to (x3,y3) ...
 * 
 * @author Chris Bailey-Kellogg, Dartmouth CS 10, Spring 2016
 * @author CBK, updated Fall 2016
 */
public class Polyline implements Shape {
	private int x1, y1;		// upper left and lower right
	private Color color;
	//private List<Integer> mousepoints_x;
	//private List<Integer> mousepoints_y;
	private ArrayList<Point> mousepoints;
	// TODO: YOUR CODE HERE

	public Polyline(int x1, int y1, Color color){
		this.color=color;
	
		mousepoints= new ArrayList<Point>();
		Point start= new Point(x1,y1);
		mousepoints.add(start);
		
	
		
	}
	Public PolyLine(ArrayList<Point> mousepoints, Color color){
		this.mousepoints=mousepoints;
		this.color=color;
	}
	
	public void update(Point p) {

		mousepoints.add(p);
		System.out.println("updating");
		System.out.println(mousepoints + "\n");
		

	}
	
	
	@Override
	public void moveBy(int dx, int dy) {
		x1 += dx; y1 += dy;
		
	}

	@Override
	public Color getColor() {
		return color;
	}

	@Override
	public void setColor(Color color) {
		this.color=color;
	}
	
	@Override
	public boolean contains(int x, int y) {
		for (int i=0;i<mousepoints.size()-1; i++){
			if(Segment.pointToSegmentDistance(x, y, mousepoints.get(i).x, mousepoints.get(i).y, mousepoints.get(i+1).x, mousepoints.get(i+1).y) <= 3)
				return true;
			}
		return false;
		}
			
	

	@Override
	public void draw(Graphics g) {
	
		System.out.println("drawing");
		System.out.println(mousepoints.size());
		for (int i=0;i<mousepoints.size() -1; i++){
			Segment segment = new Segment(mousepoints.get(i).x, mousepoints.get(i).y, mousepoints.get(i+1).x, mousepoints.get(i+1).y, color);
			segment.draw(g);
			
		} 
		
	
	}

	@Override
	public String toString() {
		String points="";
		for (int i=0;i<mousepoints.size() -1; i++){
			points= points + mousepoints.get(i);
			
		}
		return "freehand "+ points +color.getRGB();
	}
}

