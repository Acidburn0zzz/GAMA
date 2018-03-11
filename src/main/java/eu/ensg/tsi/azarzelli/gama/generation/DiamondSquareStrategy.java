package eu.ensg.tsi.azarzelli.gama.generation;

/**
 * Diamond square DEM generation
 */
public final class DiamondSquareStrategy implements IGenerationStrategy {

    /**
     * Fills a matrix with diamond square generated values
     * 
     * @param matrix the double 2d array to fill
     */
	@Override
    public void generate(double[][] matrix) {
        int nrows = matrix.length;
        int ncols = matrix[0].length;
        
        // Getting the largest dimension of the matrix
        int largestDimension = Math.max(nrows, ncols);
        
        int powerOfTwoPlusOne = largestDimension + 1;
        // Calculating the smallest power of 2 above the largest dimension 
        // if it isn't a power of 2 itself using bitwise shift, and adding one
        if (Integer.highestOneBit(largestDimension) != Integer.lowestOneBit(largestDimension)) {
        	powerOfTwoPlusOne = Integer.highestOneBit(largestDimension << 1) + 1;
        } 
        
        
        double[][] firstSquare = new double[powerOfTwoPlusOne][powerOfTwoPlusOne];
        
        diamondSquare(firstSquare);
        
        for(int y = 0; y < matrix.length; y++) {
        	for(int x = 0; x < matrix[0].length; x++) {
        		matrix[y][x] = firstSquare[y][x];
        	}
        }
        
    }
	
	/**
	 * Fills a square matrix of width 2^n + 1 with diamond square
	 * generated values. 
	 * @param firstSquare Initial, filled with 0 matrix
	 */
	private void diamondSquare(double[][] firstSquare) {
		int witdth = firstSquare.length;
		
		firstSquare[0][0] 					= (Math.random() * 2 - 1) * witdth;
		firstSquare[0][witdth - 1] 			= (Math.random() * 2 - 1) * witdth;
		firstSquare[witdth - 1][0] 		    = (Math.random() * 2 - 1) * witdth;
		firstSquare[witdth - 1][witdth - 1] = (Math.random() * 2 - 1) * witdth;
		
		// Initial step size
		int stepSize = witdth - 1;
		
        // Iterating until all the values are calculated
        while (stepSize > 1) {
        	// Doing the diamond step, without the rightmost and bottom most points
        	// as we are using top left corners
        	for (int i = 0; i < witdth - stepSize; i += stepSize) {
        		for (int j = 0; j < witdth - stepSize; j += stepSize) {
        			diamondStep(firstSquare, i, j, stepSize);
        		}
        	}
        	
        	// Doing the square step
        	for (int i = 0; i < witdth; i += stepSize/2) {
        		// Initializing offset
        		int offset = 0;
        		// half of the time the offset is half the step size
        		if (i%stepSize == 0) {
        			offset = stepSize/2;
        		}

        		for (int j = offset; j < witdth; j += stepSize) {
        			squareStep(firstSquare, i, j, stepSize);
        		}
        	}
        	
        	stepSize /= 2;
        }
        
        
        // Fetching the maximum and minimum values of the array to rescale
        // the values between 0 and 1
        double minimumValue = firstSquare[0][0];
        double maximumValue = minimumValue;
        
        for (double[] row : firstSquare) {
        	for (double value : row) {
        		if (value > maximumValue) {
        			maximumValue = value;
        		}
        		if (value < minimumValue) {
        			minimumValue = value;
        		}
        	}
        }
        
        // Rescaling
        for (int i = 0; i < firstSquare.length; i++) {
        	for (int j = 0; j < firstSquare[0].length; j++) {
        		firstSquare[i][j] = (firstSquare[i][j] - minimumValue)/(maximumValue - minimumValue);
        	}
        }
	}
	
	
	/**
	 * Diamond step of the algorithm: setting from a square center position (i,j)
	 * and width stepSize its value (average + random value)
	 * @param matrix the matrix to fill
	 * @param i row index of the square center
	 * @param j column index of the square center
	 * @param stepSize width of the square
	 */
	private void diamondStep(double[][] matrix, int i, int j, int stepSize) {
		
		double average = matrix[i][j]     + matrix[i][j + stepSize]
				+ matrix[i + stepSize][j] + matrix[i + stepSize][j + stepSize];
		average /= 4;
		
		double randomValue = (Math.random() * 2 - 1) * stepSize/2;
		
		matrix[i + stepSize/2][j + stepSize/2] = average + randomValue;		
	}
	
	/**
	 * Square step of the algorithm: setting from a diamond center position (i,j)
	 * and diagonal stepSize its value (average + random value)
	 * @param matrix the matrix to fill
	 * @param i row index of the center of the diamond
	 * @param j column index of the center of the diamond
	 * @param stepSize diagonal length of the diamond
	 */
	private void squareStep(double[][] matrix, int i, int j, int stepSize) {
		
		int semiStepSize = stepSize/2;
		
		// Calculating the matrix width. Same as matrix[0].length since matrix
		// is a square
		int matrixWidth = matrix.length - 1;
		
		double average;
		
		// Handling limit cases
		if (i==0) {
			average = matrix[ i ][ wrapIndex(j - semiStepSize, matrixWidth) ] + 
					matrix[ i ][ wrapIndex(j + semiStepSize, matrixWidth) ] +
					matrix[ wrapIndex(i + semiStepSize,matrixWidth) ][ j ];
			average /= 3;
		} else if (j==0) {
			average = matrix[ i ][ wrapIndex(j + semiStepSize, matrixWidth) ] +
					matrix[ wrapIndex(i - semiStepSize,matrixWidth) ][ j ] + 
					matrix[ wrapIndex(i + semiStepSize,matrixWidth) ][ j ];
			average /= 3;
		} else if (i==matrixWidth) {
			average = matrix[ i ][ wrapIndex(j + semiStepSize, matrixWidth) ] +
					matrix[ i ][ wrapIndex(j - semiStepSize, matrixWidth) ] +
					matrix[ wrapIndex(i - semiStepSize,matrixWidth) ][ j ];
			average /= 3;
		} else if (j==matrixWidth) {
			average = matrix[ i ][ wrapIndex(j - semiStepSize, matrixWidth) ] +
					matrix[ wrapIndex(i - semiStepSize,matrixWidth) ][ j ] + 
					matrix[ wrapIndex(i + semiStepSize,matrixWidth) ][ j ];
			average /= 3;
		} else {

			average = matrix[ i ][ wrapIndex(j - semiStepSize, matrixWidth) ] + 
					matrix[ i ][ wrapIndex(j + semiStepSize, matrixWidth) ] +
					matrix[ wrapIndex(i - semiStepSize,matrixWidth) ][ j ] + 
					matrix[ wrapIndex(i + semiStepSize,matrixWidth) ][ j ];
			average /= 4;
		}
		
		double randomValue = (Math.random() * 2 - 1) * semiStepSize;
		
		matrix[i][j] = average + randomValue;
	}
	
	/**
	 * Allows an index to be out of bounds by wrapping the matrix
	 * @param index index to wrap
	 * @param width matrix width minus 1 (to have 2^n)
	 * @return the wrapped index
	 */
	private int wrapIndex(int index, int width) {
		if (index == width) {
			return index;
		} else {
			return index & (width - 1);
		}
	}

}
