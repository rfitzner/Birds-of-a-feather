package Tools;

import java.util.Random;

/**
 * A library of methods that create realisations of random variable. 
 * @author Robert Fitzner
 *
 */
public class Mathtool {

	/**
	 * Create a random integer between 0 and give input n. 
	 * @param n
	 * @return
	 */
	public static long randomLong(long n) {
		Random rng = new Random();
		long bits, val;
		do {
			bits = (rng.nextLong() << 1) >>> 1;
			val = bits % n;
		} while (bits - val + (n - 1) < 0L);
		return val;
	}

	public static int sampleYuleSimon(double rho) {
		// First, generate exponential(shape)
		final double u = Math.random();
		final double e = Math.log(u) / -rho;

		// Next, generate geometric(Math.exp(-e))
		final double geo = Math.exp(-e);
		final double denom = Math.log(1.0 - geo);
		final double lnu = Math.log(Math.random());
		return (int) Math.max(1, Math.floor(lnu / denom) + 1);
	}

	/**
	 * The implementation is a variant of Luc Devroye's
	 * "Second Waiting Time Method" on page 525 of
	 * "Non-Uniform Random Variate Generation." Remark: In comparison to the
	 * book we flip the sign of sum and q and use E=-Log(U(0,1).
	 * 
	 * @param n
	 * @param p
	 * @return
	 */
	public static int getBinomial(int n, double p) {
		if (p == 1)
			return n;
		double logq = Math.log(1.0 - p);
		int x = 0;

		double sum = 0;
		while (true) {
			sum += Math.log(Math.random()) / (n - x);
			if (sum < logq) {
				return x;
			}
			x++;
		}
	}

	public static int getPoisson(double lambda) {
		double L = Math.exp(-lambda);
		double p = 1.0;
		int k = 0;
		do {
			k++;
			p *= Math.random();
		} while (p > L);
		return k - 1;
	}
	
	/**
	 * Computes the mean and variance of the value given in the list.   
	 * @return
	 */
	public static double[] roundedStaticts(int[] data){
		double[] result=new double[2];
		if (data==null) System.out.println("WTF");
		long tmp=0;
		for (int i=0;i<data.length;i++) {	
			tmp+=data[i];
		}
		result[0]=tmp*1.0/data.length;
		double tmp2=0;
		for (int i=0;i<data.length;i++) tmp2+=(data[i]-result[0])*(data[i]-result[0]);
		if (data.length>1) result[1]=Math.pow(tmp2*1.0/(data.length-1),0.5);
		else {result[0]=0; result[1]=0;}
		result[0]=Math.round(result[0]*1000)*1.0/1000;
		result[1]=Math.round(result[1]*1000)*1.0/1000;
		return result;
	}
	
	
}
