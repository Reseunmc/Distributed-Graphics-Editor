import java.util.ArrayList;
import java.util.List;
import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

/**
 * Client-server graphical editor
 * 
 * @author Chris Bailey-Kellogg, Dartmouth CS 10, Fall 2012; loosely based on CS 5 code by Tom Cormen
 * @author CBK, winter 2014, overall structure substantially revised
 * @author Travis Peters, Dartmouth CS 10, Winter 2015; remove EditorCommunicatorStandalone (use echo server for testing)
 * @author CBK, spring 2016 and Fall 2016, restructured Shape and some of the GUI
 */

public class Editor extends JFrame {	
	private static String serverIP = "localhost";			// IP address of sketch server
	// "localhost" for your own machine;
	// or ask a friend for their IP address

	private static final int width = 800, height = 800;		// canvas size

	// Current settings on GUI
	public enum Mode {
		DRAW, MOVE, RECOLOR, DELETE
	}
	private Mode mode = Mode.DRAW;				// drawing/moving/recoloring/deleting objects
	private String shapeType = "ellipse";		// type of object to add
	private Color color = Color.black;			// current drawing color

	// Drawing state
	// these are remnants of my implementation; take them as possible suggestions or ignore them
	private Shape curr = null;					// current shape (if any) being drawn
	private Sketch sketch;						// holds and handles all the completed objects
	private int movingId = -1;					// current shape id (if any; else -1) being moved
	private Point drawFrom = null;				// where the drawing started
	private Point moveFrom = null;				// where object is as it's being dragged

    // Other state
    // TODO: YOUR CODE HERE
	

	// Communication
	private EditorCommunicator comm;			// communication with the sketch server

	public Editor() {
		super("Graphical Editor");

		sketch = new Sketch();

		// Connect to server
		comm = new EditorCommunicator(serverIP, this);
		comm.start();

		// Helpers to create the canvas and GUI (buttons, etc.)
		JComponent canvas = setupCanvas();
		JComponent gui = setupGUI();

		// Put the buttons and canvas together into the window
		Container cp = getContentPane();
		cp.setLayout(new BorderLayout());
		cp.add(canvas, BorderLayout.CENTER);
		cp.add(gui, BorderLayout.NORTH);

		// Usual initialization
		setLocationRelativeTo(null);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		pack();
		setVisible(true);
	}

	/**
	 * Creates a component to draw into
	 */
	private JComponent setupCanvas() {
		JComponent canvas = new JComponent() {
			public void paintComponent(Graphics g) {
				super.paintComponent(g);
				drawSketch(g);
			}
		};
		
		canvas.setPreferredSize(new Dimension(width, height));

		canvas.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent event) {
				handlePress(event.getPoint());
			}

			public void mouseReleased(MouseEvent event) {
				handleRelease();
			}
		});		

		canvas.addMouseMotionListener(new MouseAdapter() {
			public void mouseDragged(MouseEvent event) {
				handleDrag(event.getPoint());
			}
		});
		
		return canvas;
	}

	/**
	 * Creates a panel with all the buttons
	 */
	private JComponent setupGUI() {
		// Select type of shape
		String[] shapes = {"ellipse", "freehand", "rectangle", "segment"};
		JComboBox<String> shapeB = new JComboBox<String>(shapes);
		shapeB.addActionListener(e -> shapeType = (String)((JComboBox<String>)e.getSource()).getSelectedItem());

		// Select drawing/recoloring color
		// Following Oracle example
		JButton chooseColorB = new JButton("choose color");
		JColorChooser colorChooser = new JColorChooser();
		JLabel colorL = new JLabel();
		colorL.setBackground(Color.black);
		colorL.setOpaque(true);
		colorL.setBorder(BorderFactory.createLineBorder(Color.black));
		colorL.setPreferredSize(new Dimension(25, 25));
		JDialog colorDialog = JColorChooser.createDialog(chooseColorB,
				"Pick a Color",
				true,  //modal
				colorChooser,
				e -> { color = colorChooser.getColor(); colorL.setBackground(color); },  // OK button
				null); // no CANCEL button handler
		chooseColorB.addActionListener(e -> colorDialog.setVisible(true));

		// Mode: draw, move, recolor, or delete
		JRadioButton drawB = new JRadioButton("draw");
		drawB.addActionListener(e -> mode = Mode.DRAW);
		drawB.setSelected(true);
		JRadioButton moveB = new JRadioButton("move");
		moveB.addActionListener(e -> mode = Mode.MOVE);
		JRadioButton recolorB = new JRadioButton("recolor");
		recolorB.addActionListener(e -> mode = Mode.RECOLOR);
		JRadioButton deleteB = new JRadioButton("delete");
		deleteB.addActionListener(e -> mode = Mode.DELETE);
		ButtonGroup modes = new ButtonGroup(); // make them act as radios -- only one selected
		modes.add(drawB);
		modes.add(moveB);
		modes.add(recolorB);
		modes.add(deleteB);
		JPanel modesP = new JPanel(new GridLayout(1, 0)); // group them on the GUI
		modesP.add(drawB);
		modesP.add(moveB);
		modesP.add(recolorB);
		modesP.add(deleteB);

		// Put all the stuff into a panel
		JComponent gui = new JPanel();
		gui.setLayout(new FlowLayout());
		gui.add(shapeB);
		gui.add(chooseColorB);
		gui.add(colorL);
		gui.add(modesP);
		return gui;
	}

	/**
	 * Getter for the sketch instance variable
	 */
	public Sketch getSketch() {
		return sketch;
	}

	/**
	 * Draws all the shapes in the sketch,
	 * along with the object currently being drawn in this editor (not yet part of the sketch)
	 */
	public void drawSketch(Graphics g) {
		if (curr != null )
		curr.draw(g);
		sketch.draw(g);
	}

	// Helpers for event handlers
	
	/**
	 * Helper method for press at point
	 * In drawing mode, start a new object;
	 * in moving mode, (request to) start dragging if clicked in a shape;
	 * in recoloring mode, (request to) change clicked shape's color
	 * in deleting mode, (request to) delete clicked shape
	 */
	private void handlePress(Point p) {
		//In drawing mode
		if (mode == Mode.DRAW) {
			//If the shape is an ellipse
			if (shapeType.equals("ellipse")) {
				//Create a new ellipse and set current to it
				curr = new Ellipse(p.x, p.y, color);
				drawFrom = p;
			}
			
			//If the shape is a rectangle
			if (shapeType.equals("rectangle")) {
				//Create a new rectangle and set current to it
				curr = new Rectangle(p.x, p.y, color);
				drawFrom = p;
			}
			
			//If the shape is a segment
			if (shapeType.equals("segment")) {
				//Create a new segment and set current to it
				curr = new Segment(p.x, p.y, color);
				drawFrom = p;
			}
			
			//If the shape is a polyline
			if (shapeType.equals("freehand")) {
				//Create a new polyline and set current to it
				curr = new Polyline(p.x, p.y, color);
				drawFrom = p;
			}
		}
		
		//In other modes
		else {
			//Find and set the moving Id
			movingId = sketch.find(p.x, p.y);
			
			//As long as the movingId exists
			if (movingId != -1) {
				//In recolor mode
				if (mode == Mode.RECOLOR) {
					//Request recoloring
					comm.Recolor(movingId, color);
				}
				
				//In moving mode
				else if (mode == Mode.MOVE) {
					//Initialize moveFrom
					moveFrom = p;
				}
				
				//In deleting mode
				else if (mode == Mode.DELETE) {
					//Request deletion and set curr equal to null and reset the movingId
					comm.Delete(movingId);
					curr = null;
					movingId = -1;
				}
			}
		}
		repaint();
	}
	
	/**
	 * Helper method for drag to new point
	 * In drawing mode, update the other corner of the object;
	 * in moving mode, (request to) drag the object
	 */
	private void handleDrag(Point p) {
		//In drawing mode
		if (mode == Mode.DRAW) {
			//For each shape type
			if (shapeType.equals("ellipse")) {
				//Type cast curr to the shape type and essentially update the shape's parameters
				((Ellipse)this.curr).setCorners(drawFrom.x, drawFrom.y, p.x, p.y);
//				repaint();
			}
			
			if (shapeType.equals("rectangle")) {
				((Rectangle)this.curr).setCorners(drawFrom.x, drawFrom.y, p.x, p.y);
//				repaint();
			}
			
			if (shapeType.equals("segment")) {
				((Segment)this.curr).setStart(drawFrom.x, drawFrom.y);
				((Segment)this.curr).setEnd(p.x, p.y);
//				repaint();
			}
			
			if (shapeType.equals("freehand")) {
				((Polyline)this.curr).update(p);
//				repaint();
			}
		}
		
		//In moving mode given that moveFrom exists
		else if (mode == Mode.MOVE && moveFrom != null) {
			//Request movement
			comm.Move(movingId, p.x - moveFrom.x, p.y - moveFrom.y);
			moveFrom = p;
//			repaint();
			
		}
		repaint();
	}

	/**
	 * Helper method for release
	 * In drawing mode, pass the add new object request on to the server;
	 * in moving mode, release it		
	 */
	private void handleRelease() {
		//In drawing mode
		if (mode == Mode.DRAW) {
			//Request the adding of the completed shape to sketch
			comm.AddNew(curr);
			curr = null;
		}
		
		//In moving mode
		else if (mode == Mode.MOVE && movingId != -1) {
			//Set moveFrom equal to null
			moveFrom = null;
		}
	}

	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				new Editor();
			}
		});	
	}
}
