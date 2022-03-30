package SEIS.Trust;



import java.util.*;

/*
Copyright (C) 2007  Ernst Gunnar Gran

This program is free software; you can redistribute it and/or modify it
under the terms of the GNU General Public License as published by the Free
Software Foundation; either version 2 of the License, or (at your option)
any later version.

This program is distributed in the hope that it will be useful, but without
any warranty; without even the implied warranty of merchantability or
fitness for a particular purpose. See the GNU General Public License for
more details. You should have received a copy of the GNU General Public
License along with this program; if not, write to the Free Software
Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA 02111-1307, USA.
*/

/**
* This class implements Zipf distribution. The main method, nextZipf(), returns an integer
* between 0 and <range> (0 included, <range> excluded). Over time the integers drawn is zipf
* distributed (<range> given to the class constructor). Zipf distribution of another range
* of numbers or zipf distribution of other kinds of elements should be possible using an
* array for mapping (using the return value from nextZipf() as index for that array).
*
* @author Ernst Gunnar Gran, ernstgr@simula.no
* @version January, 2007
*
*/

public class Zipf
{
    private double h;
    private int size;
    private double temp = 0;
    private double[] pDist; // - This array are to contain the accumulated zipf probabilities
                            //   calculated from the size of the number range (the numbers
                            //   [0..size-1] that are to be Zipf distributed).
    private int[] numbers;  // - ...while these elements contain the corresponding return values,
	                    //   the numbers that will actually be Zipf distributed.
    private boolean sa = true; // 'true' if the array 'numbers' is to be shuffled
    private Random r;
    
    // Array for distribution testing and debugging:
    private boolean debug = false;
    private int[] tester;
    
    /** Constructor. Using this constructor the method nextZipf() will return integers 
     *  between 0 (included) and <range> (excluded) corresponding to a random zipf
     *  distribution.
     */     
    public Zipf(int range){
	r = new Random();
	init(range);
    }
    
    /** Constructor. Using this constructor the method nextZipf() will return integers 
     *  between 0 (included) and <range> (excluded) corresponding to a random zipf
     *  distribution. The <seed> will be used to determine the randomness, that is
     *  using the same seed will make nextZipf() return the same integers from
     *  the same Zipf distribution.
     */     
    public Zipf(int range, long seed){
	r = new Random(seed);
	init(range);
    }
    
    /** Constructor. Using this constructor the method nextZipf() will return integers 
     *  between 0 (included) and <range> (excluded) corresponding to a random zipf
     *  distribution. If <shuffle_numbers> is 'false' the zipf distribution will return
     *  '0' the most, '1' the second most, '2' the third most and so on. If
     *  <shuffle_numbers> is 'true' Knuth's shuffle is used as the shuffle
     *  algorithm to permute the numbers at the time of initialization. This is the
     *  same as using the constructor Zipf(int range).
     */     
    public Zipf(int range, boolean shuffle_numbers){
	sa = shuffle_numbers;
	r = new Random();
	init(range);
    }
    
    /** Constructor. Using this constructor the method nextZipf() will return integers 
     *  between 0 (included) and <range> (excluded) corresponding to a random zipf
     *  distribution. The <seed> will be used to determine the randomness, that is
     *  using the same seed will make nextZipf() return the same integers from
     *  the same Zipf distribution. If <shuffle_numbers> is 'false' the zipf
     *  distribution will return '0' the most, '1' the second most, '2' the third most
     *  and so on. If <shuffle_numbers> is 'true' Knuth's shuffle is used as the shuffle
     *  algorithm to permute the numbers at the time of initialization. This is the
     *  same as using the constructor Zipf(int range, long seed).
     */     
    public Zipf(int range, long seed, boolean shuffle_numbers){
	sa = shuffle_numbers;
	r = new Random(seed);
	init(range);
    }

    // Common init method for all constructors
    private void init(int r){
	size = r;
	pDist = new double[size];
	numbers = new int[size];
	if(debug)
	    tester = new int[size];

	populate();
	if(sa)
	    shuffle();
 
	// Test init:
	if(debug){
	    for(int i = 0;i<tester.length;i++)
		tester[i]=0;
	    for(int i=0;i<size;i++) {
		System.out.println("Index: " + i + ", Dist-array values (" + pDist[i] + ", " + numbers[i] + ")");
	    }
	}
   }

    private void populate(){
	// Compute the harmonic and then populate the pDist-array with the accumulated
	// zipf probabilities...
	h = computeHarmonic(size);

	for(int i=0;i<size;i++) {
	    temp += density((double)(i+1),h);
	    pDist[i] = temp;
	    numbers[i] = i;
	    //System.out.println("Index: " + i + ", Zipf-P: " + density((double)i,h) +
	    //		       ", Dist-array values (" + pDist[i] + ", " + numbers[i] + ")");
	}
	//System.out.println("Total P: " + temp);
    }

    private void shuffle(){
	// Shuffle the number array to make sure the returned Zipf distribution is not
	// always the same (that is 0 is not always the one drawn the most, 1 the
	// one drawn second most and so on...). Using Knuth's shuffle:
	int t, swap;
	for(int i=size;i>1;i--){
	    t = r.nextInt(i); //random between 0 (inclusive) and i (exclusive).
	    swap = numbers[t];
	    numbers[t] = numbers[i-1];
	    numbers[i-1] = swap;
	}
    }

    /** This method returns the next integer corresponding to the Zipf distribution
     *  determined by the choise of constructor.
     */
    public int nextZipf(){
	int index;

	temp = r.nextDouble();
	
	if(temp<=pDist[0]) {
	    if(debug) tester[0]++;
	    return numbers[0];
	}
	else if(temp==1.0) {
	    if(debug) tester[size-1]++;
	    return numbers[size-1];
	}
	else{ // We need to search for the right number...
	    index = 1;
	    while(temp>pDist[index])
		index++;
	    if(debug) tester[index]++;
	    return numbers[index];
	}
    }   
    
    public void printTestArray(){
	if(debug){
	    for(int i = 0;i<tester.length;i++)
		System.out.println(numbers[i] + "\t" + tester[i]);
	} else {
	    System.out.println("Debuging not enabled...");
	}
    }

    private double computeHarmonic(int n){
	int k;
	double sum=0;
	
	for (k=1;k<=n;k++){
	    sum += (1.0/(double)k);
	}
	
	return sum;
    }
    
    private double density(double d, double h){
	
	return 1.0/(d*h);
	
    }
    
}

