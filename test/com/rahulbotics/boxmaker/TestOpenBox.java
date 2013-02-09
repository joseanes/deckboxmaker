package com.rahulbotics.boxmaker;

import static org.junit.Assert.*;

import java.io.FileNotFoundException;

import org.junit.Before;
import org.junit.Test;

import com.itextpdf.text.DocumentException;

public class TestOpenBox {

	double mmWidth            = 8 * Box.MM_PER_INCH;
	double mmHeight           = 3 * Box.MM_PER_INCH;
	double mmDepth            = 5 * Box.MM_PER_INCH;
	double mmThickness        = 0.125 * Box.MM_PER_INCH;
	double mmCutWidth         = 0 * Box.MM_PER_INCH;
    double mmNotchLength      = 0.5625 * Box.MM_PER_INCH;
    boolean specifiedInInches = true;
	String fileName	          = "test.pdf";
	boolean drawBoundingBox   = false;
	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void testDrawAllSides() throws FileNotFoundException, DocumentException {
		mmWidth = 140;
		mmHeight = 100;
		mmDepth = 65;
		specifiedInInches = false;
    	OpenBox myRenderer = new OpenBox();
    	myRenderer.setFilePath(fileName);
    	myRenderer.drawAllSides(mmWidth, mmHeight, mmDepth,
    			mmThickness, mmCutWidth, mmNotchLength,
    			drawBoundingBox, specifiedInInches, fileName);
    	myRenderer.closeDoc();    	

	}
}
