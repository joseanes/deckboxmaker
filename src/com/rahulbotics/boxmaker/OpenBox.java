package com.rahulbotics.boxmaker;

import java.io.FileNotFoundException;

import com.lowagie.text.DocumentException;

public class OpenBox extends Box {
	double mmWidth,mmHeight,mmDepth,mmThickness,mmCutWidth,mmNotchLength,
	drawBoundingBox,specifiedInInches;

    public void drawAllSides(double mmWidth,double mmHeight,double mmDepth,double mmThickness,
		    double mmCutWidth,double mmNotchLength, boolean drawBoundingBox,
		    boolean specifiedInInches, String fileName) 
            throws FileNotFoundException, DocumentException {
    	drawAllSides( mmWidth, mmHeight, mmDepth, mmThickness,
    		     mmCutWidth, mmNotchLength,  drawBoundingBox,
    		     specifiedInInches,  fileName,
    		     false, true);
    }
    /**
     * Actually draw all the faces of the box
     * @param mmWidth			the width of the box in millimeters
     * @param mmHeight			the height of the box in millimeters
     * @param mmDepth			the depth of the box in millimeters
     * @param mmThickness		the thickness of the material in millimeters
     * @param mmCutWidth		the width of the laser beam
     * @param mmNotchLength		the length of the notch to use to hold the box together
     * @param drawBoundingBox 	draw an outer edge with a dimension (for easier DXF import)
     * @param specifiedInInches the user specified the box in inches?
     * @param fileName          the name of the file to write
     * @throws FileNotFoundException
     * @throws DocumentException
     */
    public void drawAllSides(double mmWidth,double mmHeight,double mmDepth,double mmThickness,
			    double mmCutWidth,double mmNotchLength, boolean drawBoundingBox,
			    boolean specifiedInInches, String fileName, boolean drawTop, boolean drawBottom) 
    		throws FileNotFoundException, DocumentException{
		float width =  (float) mmWidth;
		float height =  (float) mmHeight;
		float depth =  (float) mmDepth;
		float thickness =  (float) mmThickness;
		float notchLength =  (float) mmNotchLength;
		float cutwidth = (float) mmCutWidth;
		
		// enlarge the box to compensate for cut width
		width+=cutwidth;
		height+=cutwidth;
		depth+=cutwidth;
	
		//figure out how many notches for each side, trying to make notches about the right length.
		int numNotchesW = closestOddTo(width / notchLength);
		int numNotchesH = closestOddTo(height / notchLength);
		int numNotchesD = closestOddTo(depth / notchLength);
		
		// compute exact notch lengths
		float notchLengthW = width / (numNotchesW);
		float notchLengthH = height / (numNotchesH);
		float notchLengthD = depth / (numNotchesD);
	
		//and compute the new width based on that (should be a NO-OP)
		float margin=10+cutwidth;
		width = numNotchesW*notchLengthW;
		height = numNotchesH*notchLengthH;
		depth = numNotchesD*notchLengthD;
			
		//initialize the eps file
		float boxPiecesWidth = (depth*2+width);		// based on layout of pieces
		float boxPiecesHeight = (height*2+depth*2); // based on layout of pieces
		if (null == doc) {
		  doc = openDoc( (float) (boxPiecesWidth+margin*4  + thickness * 10),
		  		         (float) (boxPiecesHeight+margin*5 + thickness * 10),
				         fileName);
		}
        writeAnnotations(specifiedInInches, width, 
        		         height, depth, thickness,
				         notchLength, cutwidth, doc);
		if(drawBoundingBox) drawBoundingBox(margin,boxPiecesWidth+margin*2,
				                            boxPiecesHeight+margin*3,
				                            specifiedInInches,  doc);

		//start the drawing phase
		float xOrig = 0;
		float yOrig = 0;
	
		// compensate for the cut width (in part) by increasing mwidth (eolson)
		// no, don't do that, because the cut widths cancel out. (eolson)
		//	    mwidth+=cutwidth/2; 
        if (drawTop) {
		  //1. a W x H side (the back)
		  xOrig = depth + margin*2;
		  yOrig = margin;
		  // drawHorizontalLine(xOrig,yOrig,notchLengthW,numNotchesW,thickness,cutwidth/2,false,false);					//top
		  drawHorizontalLine(xOrig,yOrig+height-thickness,notchLengthW,numNotchesW,thickness,cutwidth/2,true,false);	//bottom
		  drawVerticalLine(xOrig,yOrig,notchLengthH,numNotchesH,thickness,cutwidth/2,false,false);					//left
		  drawVerticalLine(xOrig+width-thickness,yOrig,notchLengthH,numNotchesH,thickness,-cutwidth/2,false,false);	//right
		  drawLineByMm (xOrig+width-thickness,yOrig, xOrig,yOrig);
	      drawLineByMm (xOrig+thickness,yOrig+thickness, xOrig+thickness,yOrig);
		  drawLineByMm (xOrig+width-thickness,yOrig+thickness, xOrig+width-thickness,yOrig);			  
        }
		//2. a D x H side (the left side)
		xOrig = margin;
		yOrig = height + depth+ margin*3;
		drawHorizontalLine(xOrig,yOrig,notchLengthD,numNotchesD,thickness,cutwidth/2,false,false);					//top
		drawHorizontalLine(xOrig,yOrig+height-thickness,notchLengthD,numNotchesD,thickness,cutwidth/2,true,false);	//bottom
		//drawVerticalLine(xOrig,yOrig,notchLengthH,numNotchesH,thickness,cutwidth/2,false,false);					//left
		drawVerticalLine(xOrig+depth-thickness,yOrig,notchLengthH,numNotchesH,thickness,-cutwidth/2,false,false);	//right
		drawLineByMm (xOrig,yOrig+height, xOrig,yOrig);
		
        // if (drawTop) {		
		  //3. a W x D side (the bottom)
		  xOrig = depth + margin*2;
		  yOrig = height + margin*2;
		  //drawHorizontalLine(xOrig,yOrig,notchLengthW,numNotchesW,thickness,-cutwidth/2,true,true);				//top
		  drawHorizontalLine(xOrig,yOrig+depth-thickness,notchLengthW,numNotchesW,thickness,-cutwidth/2,false,true);	//bottom
		  drawVerticalLine(xOrig,yOrig,notchLengthD,numNotchesD,thickness,-cutwidth/2,true,true);				//left
		  drawVerticalLine(xOrig+width-thickness,yOrig,notchLengthD,numNotchesD,thickness,-cutwidth/2,false,true);	//right
		  drawLineByMm (xOrig+thickness,       yOrig, xOrig+width-thickness,yOrig);
	      drawLineByMm (xOrig+thickness,       yOrig+thickness, xOrig+thickness,      yOrig);
		  drawLineByMm (xOrig+width-thickness, yOrig+thickness, xOrig+width-thickness,yOrig);		  
        // }
		//4. a D x H side (the right side)
		xOrig = depth + width + margin*3;
		yOrig = height + depth+ margin*3;		
		drawHorizontalLine(xOrig,yOrig,notchLengthD,numNotchesD,thickness,cutwidth/2,false,false);					//top
		drawHorizontalLine(xOrig,yOrig+height-thickness,notchLengthD,numNotchesD,thickness,cutwidth/2,true,false);	//bottom
		drawVerticalLine(xOrig,yOrig,notchLengthH,numNotchesH,thickness,cutwidth/2,false,false);					//left
		//drawVerticalLine(xOrig+depth-thickness,yOrig,notchLengthH,numNotchesH,thickness,-cutwidth/2,false,false);	//right
		drawLineByMm (xOrig+depth,yOrig+height, xOrig+depth,yOrig);
		drawLineByMm (xOrig+depth,yOrig, xOrig+depth-thickness,yOrig);
		drawLineByMm (xOrig+depth,yOrig+height, xOrig+depth-thickness,yOrig+height);
        if (drawBottom) {
		  //5. a W x H side (the front)
		  xOrig = depth + margin*2;
		  yOrig = height + depth+ margin*3;
		  drawHorizontalLine(xOrig,yOrig,notchLengthW,numNotchesW,thickness,cutwidth/2,false,false);					//top
		  drawHorizontalLine(xOrig,yOrig+height-thickness,notchLengthW,numNotchesW,thickness,cutwidth/2,true,false);	//bottom
		  drawVerticalLine(xOrig,yOrig,notchLengthH,numNotchesH,thickness,cutwidth/2,false,false);					//left
		  drawVerticalLine(xOrig+width-thickness,yOrig,notchLengthH,numNotchesH,thickness,-cutwidth/2,false,false);	//right
		  //drawLineByMm (xOrig+width-thickness,yOrig+height, xOrig,yOrig+height);
        }
		//if (drawTop) {
		  //3. a W x D side (the top)
		  xOrig = depth + margin*2;
		  yOrig = height*2 + depth + margin*4;
		  drawHorizontalLine(xOrig,yOrig,notchLengthW,numNotchesW,thickness,-cutwidth/2,true,true);				//top
		  //drawHorizontalLine(xOrig,yOrig+depth-thickness,notchLengthW,numNotchesW,thickness,-cutwidth/2,false,true);	//bottom
		  drawVerticalLine(xOrig,yOrig,notchLengthD,numNotchesD,thickness,-cutwidth/2,true,true);				//left
		  drawVerticalLine(xOrig+width-thickness,yOrig,notchLengthD,numNotchesD,thickness,-cutwidth/2,false,true);	//right
		  drawLineByMm (xOrig+thickness,yOrig+depth, xOrig+width-thickness,yOrig+depth);
	      drawLineByMm (xOrig+thickness,      yOrig+depth-thickness, xOrig+thickness,      yOrig+depth);
		  drawLineByMm (xOrig+width-thickness,yOrig+depth-thickness, xOrig+width-thickness,yOrig+depth);
	  
		//}	
		// closeDoc();
    }
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
