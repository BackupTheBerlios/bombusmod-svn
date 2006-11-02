package ui.polish;

import javax.microedition.lcdui.Graphics;

/**
 * <p>Provides functions for drawing shadows, polygons, gradients, etc.</p>
 *
 * <p>Copyright (c) 2005, 2006 Enough Software</p>
 * <pre>
 * history
 *        Nov 23, 2005 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public final class DrawUtil {


	public final static void fillPolygon( int[] xValues, int[] yValues, int color, Graphics g ) {

	}
	
	/**
	 * Creates a gradient of colors.
	 * This method is highly optimized and only uses bit-shifting and additions (no multitplication nor devision).
	 * 
	 * @param startColor the first color
	 * @param endColor the last color
	 * @param steps the number of colors in the gradient, 
	 *        when 2 is given, the first one will be the startColor and the second one will the endColor.  
	 * @return an int array with the gradient.
	 */
	public static final int[] getGradient( int startColor, int endColor, int steps ) {
		int[] gradient = new int[ steps ];
		getGradient(startColor, endColor, gradient);
		return gradient;

	}

	/**
	 * Creates a gradient of colors.
	 * This method is highly optimized and only uses bit-shifting and additions (no multitplication nor devision).
	 * 
	 * @param startColor the first color
	 * @param endColor the last color
	 * @param gradient the array in which the gradient colors are stored.  
	 */
	public static void getGradient(int startColor, int endColor, int[] gradient) {
		int steps = gradient.length;
		if (steps == 0) {
			return;
		}
		int startAlpha = startColor >>> 24;
		int startRed = (startColor >>> 16) & 0x00FF;
		int startGreen = (startColor >>> 8) & 0x0000FF;
		int startBlue = startColor  & 0x00000FF;

		int endAlpha = endColor >>> 24;
		int endRed = (endColor >>> 16) & 0x00FF;
		int endGreen = (endColor >>> 8) & 0x0000FF;
		int endBlue = endColor  & 0x00000FF;
		
		int stepAlpha = ((endAlpha - startAlpha) << 8) / (steps-1);
		int stepRed = ((endRed -startRed) << 8) / (steps-1);
		int stepGreen = ((endGreen - startGreen) << 8) / (steps-1);
		int stepBlue = ((endBlue - startBlue) << 8) / (steps-1);
//		System.out.println("step red=" + Integer.toHexString(stepRed));
//		System.out.println("step green=" + Integer.toHexString(stepGreen));
//		System.out.println("step blue=" + Integer.toHexString(stepBlue));
		
		startAlpha <<= 8;
		startRed <<= 8;
		startGreen <<= 8;
		startBlue <<= 8;
		
		gradient[0] = startColor;
		for (int i = 1; i < steps; i++) {
			startAlpha += stepAlpha;
			startRed += stepRed;
			startGreen += stepGreen;
			startBlue += stepBlue;
			
			gradient[i] = (( startAlpha << 16) & 0xFF000000)
				| (( startRed << 8) & 0x00FF0000)
				| ( startGreen & 0x0000FF00)
				| ( startBlue >>> 8);
				//| (( startBlue >>> 8) & 0x000000FF);
		}	
	}
	
	/**
	 * Retrieves the complementary color to the specified one.
	 * 
	 * @param color the original argb color
	 * @return the complementary color with the same alpha value
	 */
	public static int getComplementaryColor( int color ) {
		return  ( 0xFF000000 & color )
			| ((255 - (( 0x00FF0000 & color ) >> 16)) << 16)
			| ((255 - (( 0x0000FF00 & color ) >> 8)) << 8)
			| (255 - ( 0x000000FF & color ) );				
	}

	
	/**
	 * <p>Paints a dropshadow behind a given ARGB-Array, whereas you are able to specify
	 *  the shadows inner and outer color.</p>
	 * <p>Note that the dropshadow just works for fully opaque pixels and that it needs 
	 * a transparent margin to draw the shadow.
	 * </p>
	 * <p>Choosing the same inner and outer color and varying the transparency is recommended.
	 *  Dropshadow just works for fully opaque pixels.</p>
	 * 
	 * @param argbData the images ARGB-Array
	 * @param width the width of the ARGB-Array
	 * @param height the width of the ARGB-Array
	 * @param xOffset use this for finetuning the shadow's horizontal position. Negative values move the shadow to the left.
	 * @param yOffset use this for finetuning the shadow's vertical position. Negative values move the shadow to the top.
	 * @param size use this for finetuning the shadows radius.
	 * @param innerColor the inner color of the shadow, which should be less opaque than the text.
	 * @param outerColor the outer color of the shadow, which should be less than opaque the inner color.
	 * 
	 */
	public final static void dropShadow(int[] argbData, int width, int height,int xOffset, int yOffset, int size, int innerColor, int outerColor){
		
		// additional Margin for the image because of the shadow
		int iLeft = size-xOffset<0 ? 0 : size-xOffset;
		int iRight = size+xOffset<0 ? 0 : size+xOffset;
		int iTop = size-yOffset<0 ? 0 : size-yOffset;
		int iBottom = size+yOffset<0 ? 0 : size+yOffset;
		
		// set colors
		int[] gradient = DrawUtil.getGradient( innerColor, outerColor, size );
		
		// walk over the text and look for non-transparent Pixels	
		for (int ix=-size+1; ix<size; ix++){
			for (int iy=-size+1; iy<size; iy++){
				//int gColor=gradient[ Math.max(Math.abs(ix),Math.abs(iy))];
				//int gColor=gradient[(Math.abs(ix)+Math.abs(iy))/2];

				// compute the color and draw all shadowPixels with offset (ix, iy)
				if ( r<size) {
					int gColor = gradient[ r ];
					
					for (int col=iLeft,row; col<width/*+iLeft*/-iRight; col++) { 
						for (row=iTop;row<height-iBottom/*+iTop*/-1;row++){
							
							// draw if an opaque pixel is found and the destination is less opaque then the shadow
							if (argbData[row*(width /*+ size*2*/) + col]>>>24==0xFF 
									&& argbData[(row+yOffset+iy)*(width /* size*2*/) + col+xOffset+ix]>>>24 < gColor>>>24)
							{
								argbData[(row+yOffset+iy)*(width /*+ size*2*/) + col+xOffset+ix]=gColor;
							}
						}
					}
				}
			}
		}

	} 
	
	static int COLOR_BIT_MASK	= 0x000000FF;
	public static byte[][] FILTER_GAUSSIAN_2 = // a small and fast gaussian filtermatrix
									 {{1,2,1},
									  {2,4,2},
									  {1,2,1}};
	public static byte[][] FILTER_GAUSSIAN_3 = // a gaussian filtermatrix
	       			        {{0,1,2,1,0},
	       					 {1,3,5,3,1},
	       					 {2,5,9,5,2},
	       					 {1,3,5,3,1},
	       					 {0,1,2,1,0}};

    private static int r;
	
	/**
	 * Performs a convolution of an image with a given matrix. 
	 * @param filterMatrix a matrix, which should have odd rows an colums (not neccessarily a square). The matrix is used for a 2-dimensional convolution. Negative values are possible.  
	 * @param brightness you can vary the brightness of the image measured in percent. Note that the algorithm tries to keep the original brightness as far as is possible.
	 * @param argbData the image (RGB+transparency)
	 * @param width of the given Image
	 * @param height of the given Image
	 * Be aware that the computation time depends on the size of the matrix.
	 */
	public final static void applyFilter(byte[][] filterMatrix, int brightness, int[] argbData, int width, int height) {
		
		// check whether the matrix is ok
		if (filterMatrix.length % 2 !=1 || filterMatrix[0].length % 2 !=1 ){
			 throw new IllegalArgumentException();
		}
		
		int fhRadius=filterMatrix.length/2+1;
		int fwRadius=filterMatrix[0].length/2+1;
		int currentPixel=0;
		int newTran, newRed, newGreen, newBlue;
		
		// compute the bightness 
		int divisor=0;
		for (int fCol, fRow=0; fRow < filterMatrix.length; fRow++){
			for (fCol=0; fCol < filterMatrix[0].length; fCol++){
				divisor+=filterMatrix[fRow][fCol];
			}
		}
		// TODO: if (divisor==0), because of negativ matrixvalues
		if (divisor==0) {
			return; // no brightness
		}
		
		// copy the neccessary imagedata into a small buffer
		int[] tmpRect=new int[width*(filterMatrix.length)];
		System.arraycopy(argbData,0, tmpRect,0, width*(filterMatrix.length));
		
		for (int fCol, fRow, col, row=fhRadius-1; row+fhRadius<height+1; row++){
			for (col=fwRadius-1; col+fwRadius<width+1; col++){
				
				// perform the convolution
				newTran=0; newRed=0; newGreen=0; newBlue=0;
				
				for (fRow=0; fRow<filterMatrix.length; fRow++){
					
					for (fCol=0; fCol<filterMatrix[0].length;fCol++){

						// take the Data from the little buffer and skale the color 
						currentPixel = tmpRect[fRow*width+col+fCol-fwRadius+1];
						if (((currentPixel >>> 24) & COLOR_BIT_MASK) != 0) {
							newTran	+= filterMatrix[fRow][fCol] * ((currentPixel >>> 24) & COLOR_BIT_MASK);
							newRed	+= filterMatrix[fRow][fCol] * ((currentPixel >>> 16) & COLOR_BIT_MASK);
							newGreen+= filterMatrix[fRow][fCol] * ((currentPixel >>> 8) & COLOR_BIT_MASK);
							newBlue	+= filterMatrix[fRow][fCol] * (currentPixel & COLOR_BIT_MASK);
						}
						
					}
				}
				
				// calculate the color	
				newTran = newTran * brightness/100/divisor;
				newRed  = newRed  * brightness/100/divisor;
				newGreen= newGreen* brightness/100/divisor;
				newBlue = newBlue * brightness/100/divisor;
			
				newTran =Math.max(0,Math.min(255,newTran));
				newRed  =Math.max(0,Math.min(255,newRed));
				newGreen=Math.max(0,Math.min(255,newGreen));
				newBlue =Math.max(0,Math.min(255,newBlue));
				argbData[(row)*width+col]=(newTran<<24 | newRed<<16 | newGreen <<8 | newBlue);
				
			}
			
			// shift the buffer if we are not near the end
			if (row+fhRadius!=height) { 
				System.arraycopy(tmpRect,width, tmpRect,0, width*(filterMatrix.length-1));	// shift it back
				System.arraycopy(argbData,width*(row+fhRadius), tmpRect,width*(filterMatrix.length-1), width);	// add new data
			}
		}
		
	}
	/**
	 * This class is used for fadeEffects (FadeTextEffect and FadinAlienGlowEffect).
	 * The you can set a start and an end color as well as some durations.
	 * 
	 * Note: stepsIn has to be the same as  stepsOut or 0!
	 * 
	 * @author Simon Schmitt
	 */
	public static class FadeUtil{
		public final int FADE_IN =1;
		public final int FADE_OUT=2;
		public final int FADE_LOOP=3;
		public final int FADE_BREAK=0;
		
		public int[] gradient;
		public boolean changed;
		
		public int startColor	=0xFF0080FF;
		public int endColor	=0xFF80FF00;
		
		public int steps;
		public int delay=0; 				// time till the effect starts
		public int stepsIn=5,stepsOut=5;  	// fading duration
		public int sWaitTimeIn=10; 		// time to stay faded in
		public int sWaitTimeOut=0; 		// time to stay faded out
		public int mode=this.FADE_LOOP;
		
		public int cColor;
		public int cStep;
		
		private void initialize(){
			//System.out.println(" init");

			this.cStep=0;
			
			switch (this.mode){
			case FADE_OUT:
				this.stepsIn=0;
				this.sWaitTimeIn=0;
				this.cColor=this.endColor;
				break;
			case FADE_IN:
				this.stepsOut=0;
				this.sWaitTimeOut=0;
				this.cColor=this.startColor;
				break;
			default://loop
				this.cColor=this.startColor;
			}

			this.cStep-=this.delay;
			
			this.steps= this.stepsIn+this.stepsOut+this.sWaitTimeIn+this.sWaitTimeOut;
			
			this.gradient = DrawUtil.getGradient(this.startColor,this.endColor,Math.max(this.stepsIn, this.stepsOut));

			
		}
		
		public boolean step(){
			this.cStep++;
			
			// (re)define everything, if something changed 
			if (this.gradient==null | this.changed) {
				initialize();
			} 
			this.changed=false;
			
			// exit, if no animation is neccessary
			if (this.mode==this.FADE_BREAK){
				return false; 
			}
			// we have to ensure that a new picture is drawn
			if (this.cStep<0){
				return true;
			}
			
			// set counter to zero (in case of a loop) or stop the engine, when we reached the end
			if (this.cStep==this.steps){
				this.cStep=0;
				
				if (this.mode!=this.FADE_LOOP) {
					this.mode=this.FADE_BREAK;
					return true;
				}
			}
			
			if (this.cStep<this.stepsIn){	
				// fade in
				this.cColor=this.gradient[this.cStep];	
				//System.out.println("  [in] color:"+this.cStep);
				return true;
				
			} else if (this.cStep<this.stepsIn+this.sWaitTimeIn){
				// have a break
				if (this.cColor!=this.endColor){
					this.cColor=this.endColor;
					return true;
				}
				
				//System.out.println("  color:end color");
				
			} else if( this.cStep<this.stepsIn+this.sWaitTimeIn+this.stepsOut){ 
				// fade out 
				this.cColor=this.gradient[this.stepsIn+this.sWaitTimeIn+this.stepsOut-this.cStep-1];
				//System.out.println("  [out] color:"+(this.stepsIn+this.sWaitTimeIn+this.stepsOut-this.cStep-1));
				return true;
				
			} else { 
				// have another break
				if (this.cColor!=this.startColor){
					this.cColor=this.startColor;
					return true;
				}
				//System.out.println("  color:start color");
			} 
			
			// it sees as if we had no change...
			return false;
		}
	}

	
}
