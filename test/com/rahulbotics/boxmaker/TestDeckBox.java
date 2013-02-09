package com.rahulbotics.boxmaker;


import java.io.FileNotFoundException;

import org.junit.Before;
import org.junit.Test;

import com.itextpdf.text.DocumentException;
/**
 * On a tall deckbox (vertically stored cards):
 *     mmDepth is longest card measurement.
 *     mmHeight is thickness of deck.
 *     mmWidth is shortest card measurement.
 * On a wide deckbox (cards stored horizontally on their side):
 *     mmDepth is longest card measurement.
 *     mmHeight is thickness of deck.
 *     mmWidth is shortest card measurement.   
 * @author anes
 *
 */

public class TestDeckBox {
    double mmThickness        = 0.125 * Box.MM_PER_INCH;
	double mmWidth            = 58 + 2 * mmThickness; // 65 // 100
	double mmHeight           = 31 + 2* mmThickness; // 30 // 65
	double mmDepth            = 90 + 1 * mmThickness; // 90 // 140
	double mmCutWidth         = 0 * Box.MM_PER_INCH;
    double mmNotchLength      = 0.5625 * Box.MM_PER_INCH;

    DeckBox.Dimension opening = DeckBox.Dimension.DEPTH;
    double insideClearance    = 0;
    double outsideSizePercentage = 100;
    boolean specifiedInInches = false;
	String fileName	          = "test.pdf";
	boolean drawBoundingBox   = false;
	@Before
	public void setUp() throws Exception {
	}
	@Test
	public void testDrawAllSides() throws FileNotFoundException, DocumentException {
//		mmWidth = 140 + 2 * mmThickness;
//		mmHeight = 100 + 2 * mmThickness;
//		mmDepth = 65+ 2 * mmThickness;  
//		// Guillotine or Two Playing Card Decks
//		mmWidth  = 64 + 2 * mmThickness;
//		mmHeight = 20 + 20 + 3 * mmThickness;
//		mmDepth  = 89 + 1 * mmThickness;   	
		// Dr Who Box // Internal
		mmWidth  = 120 + 2 * mmThickness;
		mmHeight = 93  + 2 * mmThickness;
		mmDepth  = 65  + 2 * mmThickness;  
		// Dr Who Box // External
		mmWidth  = 93  + 2 * mmThickness;   // 2
		mmHeight = 65  + 2 * mmThickness;   // 3
		mmDepth  = 120 + 2 * mmThickness; 	// Longest	
		// Bohanza 
		mmWidth  = 57   + 2 * mmThickness;
		mmHeight = 55   + 1 * mmThickness;
		mmDepth  = 87.5 + 1 * mmThickness;   
		// Magic The Gathering 100 Card Sleeved Deck
		mmWidth  = 70   + 2 * mmThickness;
		mmHeight = 60   + 2 * mmThickness;
		mmDepth  = 95 + 1 * mmThickness;   		
    	DeckBox myRenderer = new DeckBox();
    	myRenderer.setFilePath(fileName);
    	myRenderer.drawAllSides(mmWidth, mmHeight, mmDepth,
    			mmThickness, mmCutWidth, mmNotchLength,
    			drawBoundingBox, specifiedInInches, fileName,
    			opening,
    			false,
    			false, 
    			outsideSizePercentage);
    	myRenderer.closeDoc();
	}
}
