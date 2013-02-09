package com.rahulbotics.boxmaker;

import java.io.FileNotFoundException;

import org.apache.log4j.Logger;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.BaseColor;

public class OpenBox extends Box {
	static Logger logger = Logger.getLogger(OpenBox.class);

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
    public void drawAllSides(double mmWidth,
    		                 double mmHeight,
    		                 double mmDepth,
    		                 double mmThickness,
			                 double mmCutWidth,
			                 double mmNotchLength, 
			                 boolean drawBoundingBox,
			                 boolean specifiedInInches, 
			                 String fileName, 
			                 boolean drawTop, 
			                 boolean drawBottom) 
    		    throws FileNotFoundException, 
    		    DocumentException {
		float width       =  (float) mmWidth;
		float height      =  (float) mmHeight;
		float depth       =  (float) mmDepth;
		float thickness   =  (float) mmThickness;
		float notchLength =  (float) mmNotchLength;
		float cutwidth    = (float) mmCutWidth;
		
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
		  logger.debug("1. a W x H side (the back)");
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
		  
		logger.debug("2. a D x H side (the left side)");
		xOrig = margin;
		yOrig = height + depth+ margin*3;
        drawArcToTheSide (xOrig - thickness * 5, 
        		          yOrig,
		                  xOrig + thickness * 5,
		                  yOrig + height,
		                  35,
		                  Box.Side.LEFT);		
		drawHorizontalLine(xOrig,yOrig,notchLengthD,numNotchesD,thickness,cutwidth/2,false,false);					//top
		drawHorizontalLine(xOrig,yOrig+height-thickness,notchLengthD,numNotchesD,thickness,cutwidth/2,true,false);	//bottom
		//drawVerticalLine(xOrig,yOrig,notchLengthH,numNotchesH,thickness,cutwidth/2,false,false);					//left
		drawVerticalLine(xOrig+depth-thickness,
				         yOrig,
				         notchLengthH,
				         numNotchesH,
				         thickness,-cutwidth/2,
				         false,false);	//right
		drawLineByMm (xOrig,yOrig+height, xOrig,yOrig);
		// drawArcByMm (xOrig, yOrig, xOrig , yOrig + 100);
		// drawRectangularArcByMm (xOrig, yOrig, xOrig + depth, yOrig+height, 10);

		
        logger.debug("3. a W x D side (the bottom)");
		  xOrig = depth + margin*2;
		  yOrig = height + margin*2;
		  //drawHorizontalLine(xOrig,yOrig,notchLengthW,numNotchesW,thickness,-cutwidth/2,true,true);				//top
		  drawHorizontalLine(xOrig,yOrig+depth-thickness,notchLengthW,numNotchesW,thickness,-cutwidth/2,false,true);	//bottom
		  drawVerticalLine(xOrig,yOrig,notchLengthD,numNotchesD,thickness,-cutwidth/2,true,true);				//left
		  drawVerticalLine(xOrig+width-thickness,yOrig,notchLengthD,numNotchesD,thickness,-cutwidth/2,false,true);	//right
		  drawLineByMm (xOrig+thickness,       yOrig, xOrig+width-thickness,yOrig);
	      drawLineByMm (xOrig+thickness,       yOrig+thickness, xOrig+thickness,      yOrig);
		  drawLineByMm (xOrig+width-thickness, yOrig+thickness, xOrig+width-thickness,yOrig);		  
		logger.debug("4. a D x H side (the right side)");
		xOrig = depth + width + margin*3;
		yOrig = height + depth+ margin*3;	
        drawArcToTheSide (xOrig - thickness * 5 + depth, 
		                  yOrig,
                          xOrig + thickness * 5 + depth,
                          yOrig + height,
                          35,
                          Box.Side.RIGHT);			
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

    }
private void drawArcToTheSide(float fromXmm, float fromYmm,
		                      float toXmm, float toYmm,
		                      float space,
		                      Box.Side side) {
	logger.debug("drawArcToTheSide Arc - natural:   - ( "+fromXmm+" , "+fromYmm+" ) to ( "+toXmm+" , "+toYmm+" )"); 
	float x0 = Box.DPI * fromXmm * Box.INCH_PER_MM;
	float y0 = Box.DPI * fromYmm * Box.INCH_PER_MM;
	float x1 = Box.DPI * toXmm   * Box.INCH_PER_MM;
	float y1 = Box.DPI * toYmm   * Box.INCH_PER_MM;
	float angle1 = 0;
	float angle2 = 0;
	PdfContentByte cb = docPdfWriter.getDirectContent();

	logger.debug("drawArcToTheSide Arc - converted:   - ( "+x0+" , "+y0+" ) to ( "+x1+" , "+y1+" )"); 	
	if (side == Box.Side.LEFT) { 
	  angle1 = -90;
	  angle2 = 180;
	  y0 += space;
	  y1 -= space;
	} else if (side == Box.Side.RIGHT) {
	  angle1 = 90;
	  angle2 = 180;	
	  y0 += space;
	  y1 -= space;	  
	}else if (side == Box.Side.TOP) {
	  angle1 = 180;
	  angle2 = 180;		
	  x0 += space;
	  x1 -= space;	  
	}else if (side == Box.Side.BOTTOM) {
	  angle1 = 0;
	  angle2 = 180;	
	  x0 += space;
	  x1 -= space;		  
	} else {
		logger.error("drawArcToTheSide: wrong side!");
		System.exit(1);
	}
	cb.saveState();
	cb.arc(x0, y0, x1, y1, angle1,angle2);
	cb.restoreState();
}
//    void drawArcByMm(float fromXmm,float fromYmm,float toXmm,float toYmm) {
//		float x0 = DPI*fromXmm*INCH_PER_MM;
//		float y0 = DPI*fromYmm*INCH_PER_MM;
//    	float x1 = DPI*toXmm*INCH_PER_MM;
//    	float y1 = DPI*toYmm*INCH_PER_MM;
//    	PdfContentByte canvas = docPdfWriter.getDirectContent();
//        canvas.saveState();
////        canvas.setColorStroke(new .CYAN (0.2f));
////        canvas.setColorFill(new GrayColor(0.9f));
//        canvas.arc(  x1,
//              y1,
//              x0,
//              y0,
//              45, 270);
////        canvas.ellipse(170, 270, 270, 330);
////        canvas.circle(320, 300, 30);
////        canvas.roundRectangle(370, 270, 80, 60, 20);
////        canvas.fillStroke();
//        canvas.restoreState();    	
////    	
//    	System.out.println(" Arc:   - ( "+x0+" , "+y0+" ) to ( "+x1+" , "+y1+" )");      	
////    	PdfContentByte cb = docPdfWriter.getDirectContent();
////		cb.setLineWidth(0f);
////    	cb.
////    	cb.stroke();
////		cb.moveTo(x0,y0);
////		cb.curveTo(x0, y0, 100, y1);
////		cb.stroke();
//    }
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
