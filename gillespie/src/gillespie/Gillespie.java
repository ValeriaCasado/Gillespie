/**
 * 
 */
package gillespie;
import java.awt.EventQueue;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import org.jfree.chart.ChartFactory;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.xy.XYSeries;
/**
 * @author Valeria Casado
 *
 */
public class Gillespie {
	
	ArrayList<Integer> X = new ArrayList<Integer>() { { add(0);} }; 
	List<Double> t = new ArrayList<Double>() { { add(0.0);} }; 
	XYSeries series;
	DefaultCategoryDataset histo = new DefaultCategoryDataset();  
	int average_systemsize = 0;
	int[] systemsizes = new int[] {0,0,0,0,0,0};
	int times_in_state = 0;
	double[] leaps = new double[100];
	
	// 0: rate of leaving, 1: rate of arriving
	double[][] rates = new double[][] {

		{0.0,1.0},
		{0.3,0.7},
		{0.5,0.5},
		{0.6,0.4},
		{0.6,0.4},
		{1.0,0.0}
	};
	

	public Gillespie() {

		this.series = new XYSeries("X");
		this.series.add(0.0,0);

	}

	public void algorithm(int transitions) {
	
		for (int transition = 0; transition < transitions; transition ++) {
			
			// Get current number of people in the system
			int currentX = this.X.get(transition);
			
			// Get rates for the current number of people in the system
			double[] c = this.rates[currentX];

			// Without h, because these are not chemical reactions
			double a_0 = c[0] + c[1];
			
			// Random numbers
			Random rand = new Random();
			double r_1 = rand.nextDouble();
			double r_2 = rand.nextDouble();
			
			// Calculate the time-step
			double ln =  Math.log(1/r_1);
			double tau_leap = (1/a_0) * ln;
			
			
			/* Now we calculate mu according to: [page 2345 (not ironic)]
			 * 
			 * The generating procedure (21) is easy to code in Fortran.
				In particular, (21b) may be implemented simply by cumulatively 
				adding the successive values al, a2, ... in a
				do-loop until their sum is observed to equal or exceed r2a0,
				whereupon mu is set equal to the index of the last a, term
				added.
			 */
			
			double r2a0 = r_2 * a_0;
			int mu = 0;
			double sum = 0;
			for (int i = 0; i <= 1; i++) {
				sum += c[i];
				if ( sum >= r2a0) {
				  mu = i;
				  break;
				}	
			}
			

			// Update time-step
			t.add(t.get(transition) + tau_leap);
			
			// Update system size
			// 0: rate of leaving, 1: rate of arriving
			if (mu==0) this.X.add(currentX - 1);
			else this.X.add(currentX + 1); 
			

			
			// Stats
			this.leaps[transition] = tau_leap;
			this.systemsizes[currentX] +=1;
			this.series.add(this.t.get(transition+1), this.X.get(transition+1));
		}
		
		int maxValue = 0;
		int winner = 0;
		 for (int i = 0; i < this.systemsizes.length; ++i) {
			 if (this.systemsizes[i] >= maxValue) {
				 maxValue = this.systemsizes[i];
			     winner = i;
			 }
		 }
		 this.average_systemsize = winner;	
		 this.times_in_state = maxValue;
	}
	
	public double[] getLeaps() {
		return leaps;
	}

	public int getTimes_in_state() {
		return times_in_state;
	}
	
	public XYSeries getSeries() {
		return series;
	}
	
	public int[] buildHistogramDataset() {
		
		double largestVal = 0;
		for (int i = 0; i < this.leaps.length; i ++){
			if (this.leaps[i] > largestVal) {
				largestVal = this.leaps[i];
			}
		}
		
		int nBins = (int) largestVal * 5;
		
		int[] bins = new int[nBins];
	
		for (int i = 0; i < this.leaps.length; i ++){
			innerloop:
			for (int j = nBins-1; j >= 0; j-- ) {
				
				if (this.leaps[i] >= (double)j*0.2 && this.leaps[i] <= 5.0) {
					bins[j] += 1;
					break innerloop;
				} else if (this.leaps[i] > 5.0) {
					bins[24] += 1;
					break innerloop;
				}
			}
		}
		
		
		return Arrays.copyOfRange(bins, 0, 25);
	}
	
	public int getAvgSystemSize() {
		return this.average_systemsize;
	}
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(() -> {

            var ex = new Graph();
            ex.setVisible(true);
        });
		
	}

}
