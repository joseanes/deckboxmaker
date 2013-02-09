package com.rahulbotics.boxmaker;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.Date;

import org.apache.log4j.Logger;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfWriter;

public class Box {
	static Logger logger = Logger.getLogger(Box.class);
	public enum Side {
	    LEFT, TOP, RIGHT, BOTTOM
	}
	// how many millimeters in one inch
	static final float MM_PER_INCH = 25.4f;
	// how many inches in one millimeter
    static final float INCH_PER_MM = 0.0393700787f;
    // the standard display DPI of the pdf (not the same as printing resolution to a pdf)
    static final float DPI = 72.0f;
	String fileName = null;
    // Output document.
    Document doc = null;
    // the writer underneath the PDF document, which we need to keep a reference to
    PdfWriter docPdfWriter;
    // the path that we are writing the file to
    String filePath; 
    /**
     * Create the document to write to (needed before any rendering can happen).
     * @param widthMm	the width of the document in millimeters
     * @param heightMm	the height of the document in millimeters
     * @param fileName  the name of the file to save
     * @throws FileNotFoundException
     * @throws DocumentException
     */
    Document openDoc(float widthMm,float heightMm,String fileName) throws FileNotFoundException, DocumentException{
        // the PDF document created
        Document doc;
    	float docWidth = widthMm*DPI*INCH_PER_MM;
		float docHeight = heightMm*DPI*INCH_PER_MM;
		//System.out.println("doc = "+docWidth+" x "+docHeight);
    	doc = new Document(new Rectangle(docWidth,docHeight));
		docPdfWriter = PdfWriter.getInstance(doc,new FileOutputStream(filePath));
		String appNameVersion = BoxMakerConstants.APP_NAME+" "+BoxMakerConstants.VERSION;
		doc.addAuthor(appNameVersion);
		doc.open();
		doc.add(new Paragraph(
                "Produced by "+BoxMakerConstants.APP_NAME+" "+BoxMakerConstants.VERSION+"\n"+
                "  on "+new Date()+"\n"+BoxMakerConstants.WEBSITE_URL )
		);
		return doc;
    }    
    /**
     * Close up the document (writing it to disk)
     */
    void closeDoc(){
		doc.close();    	
    }    
	/**
	 * Math utility function
	 * @param numd	a number
	 * @return		the closest odd number to the one passed in
	 */
    static int closestOddTo(double numd){
		int num=(int) (numd+0.5);
		if(num % 2 == 0) return num-1;
		return num;
    }    
	/**
     * Draw one horizontal notched line
     * @param x0			x-coord of the starting point of the line (lower left corner) 
     * @param y0			y-coord of the starting point of the line (lower left corner)
     * @param notchWidth	the width of each notch to draw in millimeters
     * @param notchCount	the number of notches to draw along the edge
     * @param notchHieght	the height of the notches to draw (the material thickness)
     * @param cutwidth		the width of the laser beam to compensate for
     * @param flip			should the first line (at x0,y0) be out or in
     * @param smallside		should this stop short of the full height or not
     */
     void drawHorizontalLine(float x0,float y0, float notchWidth,
    		int notchCount,float notchHieght /*material tickness*/,
    		float cutwidth,boolean flip,boolean smallside){
    	float x=x0,y=y0;
    	logger.debug(" side: "+notchCount+" steps @ ( "+x0+" , "+y0+" )");
    	
		for (int step=0;step<notchCount;step++)
		    {
			y=(((step%2)==0)^flip) ? y0 : y0+notchHieght;
	
			if(step==0){		//start first edge in the right place
			    if(smallside) drawLineByMm(x+notchHieght,y,x+notchWidth+cutwidth,y);
			    else drawLineByMm(x,y,x+notchWidth+cutwidth,y);
			} else if (step==(notchCount-1)){	//shorter last edge
			    drawLineByMm(x-cutwidth,y,x+notchWidth-notchHieght,y);
			} else if (step%2==0) {
			    drawLineByMm(x-cutwidth,y,x+notchWidth+cutwidth,y);
		    } else {
			    drawLineByMm(x+cutwidth,y,x+notchWidth-cutwidth,y);
		    }
			
			if (step<(notchCount-1)){
			    if (step%2==0){
					drawLineByMm(x+notchWidth+cutwidth,y0+notchHieght,x+notchWidth+cutwidth,y0);
			    } else {
					drawLineByMm(x+notchWidth-cutwidth,y0+notchHieght,x+notchWidth-cutwidth,y0);
			    }
			}
			
			x=x+notchWidth;
		}
    }

    /**
     * Draw one vertical notched line
     * @param x0			x-coord of the starting point of the line (lower left corner) 
     * @param y0			y-coord of the starting point of the line (lower left corner)
     * @param notchWidth	the width of each notch to draw in millimeters
     * @param notchCount	the number of notches to draw along the edge
     * @param notchHieght	the height of the notches to draw (the material thickness)
     * @param cutwidth		the width of the laser beam to compensate for
     * @param flip			should the first line (at x0,y0) be out or in
     * @param smallside		should this stop short of the full height or not
     */
     void drawVerticalLine(float x0,         float y0, 
    		               float stepLength, int numSteps,
    		               float mlength,    float cutwidth,
    		               boolean flip,boolean smallside){
		float x=x0,y=y0;
	
		for (int step=0;step<numSteps;step++) {
			x=(((step%2)==0)^flip) ? x0 : x0+mlength;
	
			if (step==0) {
				if(smallside) drawLineByMm(x,y+mlength,x,y+stepLength+cutwidth);
			    else drawLineByMm(x,y,x,y+stepLength+cutwidth);
			} else if (step==(numSteps-1)) {
			    //g.moveTo(x,y+cutwidth); g.lineTo(x,y+stepLength); g.stroke();
				if(smallside) drawLineByMm(x,y-cutwidth,x,y+stepLength-mlength);
			    else drawLineByMm(x,y-cutwidth,x,y+stepLength); 
			} else if (step%2==0) {
			    drawLineByMm(x,y-cutwidth,x,y+stepLength+cutwidth);
			} else {
			    drawLineByMm(x,y+cutwidth,x,y+stepLength-cutwidth);
			}
			
			if (step<(numSteps-1)) {
			    if (step%2==0) {
			    	drawLineByMm(x0+mlength,y+stepLength+cutwidth,x0,y+stepLength+cutwidth);
			    } else {
			    	drawLineByMm(x0+mlength,y+stepLength-cutwidth,x0,y+stepLength-cutwidth);
			    }
			}
			y=y+stepLength;
		}
    }

    /**
     * Low-level function to draw lines
     * @param fromXmm	start x pos on age (in millimeters)
     * @param fromYmm	start y pos on age (in millimeters)
     * @param toXmm		end x pos on age (in millimeters)
     * @param toYmm		end y pos on age (in millimeters)
     */
     void drawLineByMm(float fromXmm,float fromYmm,float toXmm,float toYmm){
    	PdfContentByte cb = docPdfWriter.getDirectContent();
		cb.setLineWidth(0f);
		float x0 = DPI*fromXmm*INCH_PER_MM;
		float y0 = DPI*fromYmm*INCH_PER_MM;
    	cb.moveTo(x0,y0);
    	float x1 = DPI*toXmm*INCH_PER_MM;
    	float y1 = DPI*toYmm*INCH_PER_MM;
    	cb.lineTo(x1, y1);
    	cb.stroke();
    	// System.out.println(" Line  - ( "+x0+" , "+y0+" ) to ( "+x1+" , "+y1+" )");
    }
    void drawArcByMm(float fromXmm,float fromYmm,
    		         float toXmm,float toYmm) {
		float x0 = DPI*fromXmm*INCH_PER_MM;
		float y0 = DPI*fromYmm*INCH_PER_MM;
    	float x1 = DPI*toXmm*INCH_PER_MM;
    	float y1 = DPI*toYmm*INCH_PER_MM;
    	
    	PdfContentByte cb = docPdfWriter.getDirectContent();
    	cb.saveState();
    	cb.arc(x0, y0, x1, y1, -90,180);
    	cb.restoreState();
    	logger.debug("drawArcByMm Arc:   - ( "+x0+" , "+y0+" ) to ( "+x1+" , "+y1+" )");    	
    }
    void drawRectangularArcByMm(float fromXmm,float fromYmm,float toXmm,float toYmm,
    		float space) {
		float x0 = DPI*fromXmm*INCH_PER_MM;
		float y0 = DPI*fromYmm*INCH_PER_MM;
    	float x1 = DPI*toXmm*INCH_PER_MM;
    	float y1 = DPI*toYmm*INCH_PER_MM;
    	
    	PdfContentByte cb = docPdfWriter.getDirectContent();
		cb.setLineWidth(0f);
		cb.moveTo(x0,y0);
    	cb.lineTo(x0 + space, y1);
		cb.stroke();    	
    	cb.lineTo(x1 - space, y1);
		cb.stroke();    	
    	cb.lineTo(x1, y0);     	
		cb.stroke();
    	System.out.println(" Arc:   - ( "+x0+" , "+y0+" ) to ( "+x1+" , "+y1+" )");    	
    }    
    /**
     * Draw a rectangle with based on the endpoints passed in
     * @param fromXmm
     * @param fromYmm
     * @param toXmm
     * @param toYmm
     */
     void drawBoxByMm(float fromXmm,float fromYmm,float toXmm,float toYmm){
     	PdfContentByte cb = docPdfWriter.getDirectContent();
		cb.setLineWidth(0f);
		float x0 = DPI*fromXmm*INCH_PER_MM;
		float y0 = DPI*fromYmm*INCH_PER_MM;
    	float x1 = DPI*toXmm*INCH_PER_MM;
    	float y1 = DPI*toYmm*INCH_PER_MM;
    	cb.rectangle(x0, y0, x1, y1);
    	cb.stroke();
    	logger.debug( "drawBoxByMm   - ( "+x0+" , "+y0+" ) to ( "+x1+" , "+y1+" )");
    }    
 	/**
 	 * Writes some annotations on the output PDF file so that you can remember
 	 * the dimensions of the box and how it was generated.
 	 * Useful when re-doing the work later.
 	 * @param specifiedInInches
 	 * @param width
 	 * @param height
 	 * @param depth
 	 * @param thickness
 	 * @param notchLength
 	 * @param cutwidth
 	 * @throws DocumentException
 	 */
 	 void writeAnnotations(boolean specifiedInInches, float width,
 			float height, float depth, float thickness, float notchLength,
 			float cutwidth, Document doc) throws DocumentException {
 		if(specifiedInInches) {
             doc.add(new Paragraph("Width (in): "+width*INCH_PER_MM));
             doc.add(new Paragraph("Height (in): "+height*INCH_PER_MM));
             doc.add(new Paragraph("Depth (in): "+depth*INCH_PER_MM));
             doc.add(new Paragraph("Thickness (in): "+thickness*INCH_PER_MM));
             doc.add(new Paragraph("Notch Length (in): "+notchLength*INCH_PER_MM));
             doc.add(new Paragraph("Cut Width (in): "+cutwidth*INCH_PER_MM));        
         } else {
             doc.add(new Paragraph("Width (mm): "+width));
             doc.add(new Paragraph("Height (mm): "+height));
             doc.add(new Paragraph("Depth (mm): "+depth));
             doc.add(new Paragraph("Thickness (mm): "+thickness));
             doc.add(new Paragraph("Notch Length (mm): "+notchLength));
             doc.add(new Paragraph("Cut Width (mm): "+cutwidth));        
         }
 	}  
     /**
      * Draw a bounding box around the whole thing.
      * 
      * @param margin	the offset to draw the box (in millimeters)
      * @param widthMM	the width of the box to draw (in millimeters)
      * @param heightMM	the height of the box to draw (in millimeters)
      * @throws DocumentException 
      */
      void drawBoundingBox(float margin,float widthMM, float heightMM, 
    		               boolean specifiedInInches, Document doc) 
         throws DocumentException {
     	drawBoxByMm(margin, margin, widthMM, heightMM);
 		if(specifiedInInches) {
             doc.add(new Paragraph("Bounding box (in): "+widthMM*INCH_PER_MM+" x "+heightMM*INCH_PER_MM));
 		} else {
 		    doc.add(new Paragraph("Bounding box (mm): "+widthMM+" x "+heightMM));
 		}
 	} 	 
      public void setFilePath (String fp) {
    	  filePath = fp;
      }
}
