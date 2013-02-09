package com.rahulbotics.boxmaker;

import java.io.FileNotFoundException;

import org.apache.log4j.Logger;

import com.itextpdf.text.DocumentException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
/**
 * A Deck Box is a two sided box.  Or two OpenBoxes that close one on top of
 * the other.
 * 
 * @author anes
 *
 */
public class DeckBox extends OpenBox {
	static Logger logger = Logger.getLogger(DeckBox.class);
    public enum Dimension { NONE, DEPTH, HEIGHT, WIDTH }
    
    public void drawAllSides(double mmWidth,double mmHeight,
    		                 double mmDepth,double mmThickness,
                             double mmCutWidth,
                             double mmNotchLength, 
                             boolean drawBoundingBox,
	                         boolean specifiedInInches, String fileName,
	                         Dimension opening,
	                         boolean insideNothch,
	                         boolean outsideNotch,
	                         double outsideSizePercentage) 
                    throws FileNotFoundException, DocumentException {
    	logger.debug("Drawing Inside box.");
    	// First draw outside
    	setInsideOutside("Inside");
    	notches.clear();
    	notches.add(Box.Side.LEFT);
    	notches.add(Box.Side.RIGHT);
    	drawAllSides( mmWidth, mmHeight, mmDepth, mmThickness,
    	  	          mmCutWidth, mmNotchLength,  drawBoundingBox,
    		          specifiedInInches,  fileName,
    		          false, true);
    	// Now draw inside, reducing the size of the box by the thickness
    	// of the material.
    	logger.debug("Drawing Outside box.");
    	notches.clear();
    	notches.add(Box.Side.TOP);
    	notches.add(Box.Side.BOTTOM);    	
    	setInsideOutside("Outside");
    	doc.newPage();
    	double outerMmDepth  = mmDepth+2*mmThickness;
    	double outerMmHeight = mmHeight+2*mmThickness;
    	double outerMmWidth  = mmWidth+2*mmThickness;
    	if (opening == Dimension.DEPTH) {
    		outerMmDepth  -= mmThickness;
    		outerMmDepth  = outerMmDepth * outsideSizePercentage / 100;
    	}
    	if (opening == Dimension.HEIGHT) {
    		outerMmHeight -= mmThickness;
    		outerMmHeight = outerMmHeight * outsideSizePercentage / 100;
    	}
    	if (opening == Dimension.WIDTH) {
    		outerMmWidth -= mmThickness;
    		outerMmWidth = outerMmWidth  * outsideSizePercentage / 100;
    	}    	
    	drawAllSides( outerMmWidth, 
    			      outerMmHeight, 
    			      outerMmDepth, 
    			      mmThickness,
   		              mmCutWidth, mmNotchLength,  drawBoundingBox,
   		              specifiedInInches,  fileName,
   		              false, true );    	
    }
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
