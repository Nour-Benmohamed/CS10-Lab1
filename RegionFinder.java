package lab1;
import java.awt.*;
import java.awt.image.*;
import java.util.*;

/**
 * Region growing algorithm: finds and holds regions in an image.
 * Each region is a list of contiguous points with colors similar to a target color.
 * Scaffold for PS-1, Dartmouth CS 10, Fall 2016
 * 
 * @author Chris Bailey-Kellogg, Winter 2014 (based on a very different structure from Fall 2012)
 * @author Travis W. Peters, Dartmouth CS 10, Updated Winter 2015
 * @author CBK, Spring 2015, updated for CamPaint
 * 
 * edited by Nour Benmohamed
 * problem set 1
 * 4/12/2018
 * worked with lab partner: Jackson Harris 
 * 
 */
public class RegionFinder {
	private static final int maxColorDiff =20;				// how similar a pixel color must be to the target color, to belong to a region
	private static final int minRegion = 50; 				// how many points in a region to be worth considering

	private BufferedImage image;                            // the image in which to find regions
	private BufferedImage recoloredImage;                   // the image with identified regions recolored

	public ArrayList<ArrayList<Point>> regions=new ArrayList<ArrayList<Point>>();			// a region is a list of points
																						// so the identified regions are in a list of lists of points
	private ArrayList<Point> Matches=new ArrayList<Point>();					// list of points with colors that match the targetColor
	private ArrayList<Point> tovisit ;											// a list of neighbors of a match pixel, they are only added to this list if they have not already been visited
	private Color c2;															//color of a neighbor pixel
	private Color c;															// color of the pixel in the outer loop 
	private BufferedImage visited;												// image where the visited pixels will be marked
	
	public RegionFinder() {
		this.image = null;
	}

	public RegionFinder(BufferedImage image) {
		this.image = image;		
	}

	public void setImage(BufferedImage image) {
		this.image = image;
	}

	public BufferedImage getImage() {
		return image;
	}

	public BufferedImage getRecoloredImage() {
		return recoloredImage;
	}

	/**
	 * Sets regions to the flood-fill regions in the image, similar enough to the trackColor.
	 */
	public void findRegions(Color targetColor) { 
		// TODO: YOUR CODE HERE
		visited = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_ARGB);
		tovisit = new ArrayList<Point>();
		for (int y = 0; y < image.getHeight(); y++) {				// this loop will go through all the non visited pixel of the image
			for (int x = 0; x < image.getWidth(); x++) {
				
				if (visited.getRGB(x, y) == 0) {
					//System.out.println("the coordinates are "+x+" , "+y);
					c = new Color (image.getRGB(x,y));
					if (colorMatch(c, targetColor) ){				// the search for the region onkyy begins if there is a match that will consist the beginning of a new region
					    
						//System.out.println(Matches.size());
						visited.setRGB(x, y, 1);					// mark the first match pixel as visited
						
						Point point= new Point(x,y);
						Matches.add(point);							// first element of the matches list
						//System.out.println(Matches.size());
					
						int k=0;
						while ( k<Matches.size() ) {			 //this loop goes thru the indices of list of matches
							Point q= Matches.get(k);
							//System.out.println("the coordinates of the center are "+q.x+" , "+q.y);
							for (int i = Math.max(0, q.y-1); i < Math.min(image.getHeight(), q.y+2); i++) {		// this nested loop goes through the neighbors and adds them to a list to visit
								for (int j = Math.max(0, q.x-1); j < Math.min(image.getWidth(), q.x+2); j++) {
									//System.out.println("aaaa"+j+" "+i);
									if (visited.getRGB(j,i) == 0) {
										//System.out.println("the coordinates of the tovisit points are "+j+" , "+i);
										Point p=new Point(j,i);		
										//System.out.println("this ran2");
										tovisit.add(p);
										//System.out.println(tovisit);
										visited.setRGB(p.x, p.y, 1);
									}
								}																 
							}														// this nested loop ends here
							
								
							//System.out.println(tovisit.size());
							for (Point p1 : tovisit) {		// this loop goes thru the list of tovisit and check if they are matches
								//System.out.println("this ran 3");
								c2= new Color(image.getRGB(p1.x,p1.y));
								if (colorMatch(c2, targetColor)) {   // if they are matches, it adds them to the list of Matches, thus growing the region										 								// marks them as visited
									Matches.add(p1);
									//System.out.println(Matches.size());
								}
									//System.out.println(Matches.size());	
								    
							}			// this loop ends here
							
							tovisit.clear();     						// tovisit is cleared for the next matched point 
							k++;										// if there is a match, matches list will have another elemnt whos neighbors have not been checked sot the condition for k will hold 
							
						}
						//System.out.println(Matches.size());
					
						
						if (Matches.size()>=minRegion) { 						// this if statement will add the list of matches to regions if it's big enough	
							//System.out.println(Matches.size());
							ArrayList<Point> copy= new ArrayList<Point>();     // it creates a copy of matches because matches will be reused if the next iteration of the outer loop find a match
							for (Point p : Matches) {
								copy.add(new Point(p.x,p.y));
							}
							regions.add(copy);
							
							//System.out.println(regions.size());
						}
					
						Matches.clear();										// clear matches for the next region
					}
				}				
			}
		}
	 //add every list of region point to the list of lists to make up the big list of lists
	}

	/**
	 * Tests whether the two colors are "similar enough" (your definition, subject to the maxColorDiff threshold, which you can vary).
	 */
	private static boolean colorMatch(Color c1, Color c2) {
		
		// TODO: YOUR CODE HERE
		int R = Math.abs((c1.getRed()-c2.getRed()));
		int B=	Math.abs((c1.getBlue()-c2.getBlue()));
		int G=	Math.abs((c1.getGreen()-c2.getGreen()));
		//System.out.println("r ="+R + " B = "+B+" G = "+G);
		return (R<= maxColorDiff && B <= maxColorDiff && G <=maxColorDiff);
	}

	/**
	 * Returns the largest region detected (if any region has been detected)
	 */
	public ArrayList<Point> largestRegion() {
		// TODO: YOUR CODE HERE
																			//find max of list of lists
		ArrayList<Point> biggest=regions.get(0);
		for (int i=1; i <=regions.size()-1;i++) {
			if (regions.get(i).size()>biggest.size()) {
				biggest= regions.get(i);
			}
		}
		
		return biggest;
	}

	/**
	 * Sets recoloredImage to be a copy of image, 
	 * but with each region a uniform random color, 
	 * so we can see where they are
	 */
	public void recolorImage() {
		// First copy the original
		recoloredImage = new BufferedImage(image.getColorModel(), image.copyData(null), image.getColorModel().isAlphaPremultiplied(), null);
		// Now recolor the regions in it
		// TODO: YOUR CODE HERE
		Color c3 = new Color((int)(Math.random()*256),(int)(Math.random()*256),(int)(Math.random()*256));
		for (ArrayList<Point> p : regions) { 					// p is a list
						
			//System.out.println(regions);

			for (Point p2 : p) {								// p2 is a point in list p 
				//System.out.println("this ran");
				
				recoloredImage.setRGB(p2.x,p2.y, c3.getRGB());
			}
		}	
	}
}
