import java.util.*;
import java.lang.Math;
import java.math.BigInteger;


/*	NOTES:
 *			Okay so this generates the keys.
 * 			Public key = (n,e) [shared_rsa, public_rsa]			This is sent to anyone wanting to talk to USER1.
 * 			Private key =(n,d) [shared_rsa, private_rsa]		This is meant only for USER1.
 * 			We use { C = [character]^e % n } 		to encrypt a character. We send things with USER1's PUBLIC KEY to USER1
 * 			USER1 uses { [character] = C^d % n }	to get the character as a message
 * 			
 * 			
 */

public class RSA {

	//private int block_size = 2;
	//private int max_block_value = 16384;
	private int least_prime = 131;
	private int max_prime = 997;
	private int private_rsa; // d value (half of private key)
	public int public_rsa; // e value (half of public key)
	public int shared_rsa; // n value (half of both private and public key)
	//public int gcd_result;
	
	private static ArrayList<Long> encry_mess;
	
	
	//makes keys by random
	//Page 4 of the program write up says we need to use file input instead of true random, but Ask guys
	// if we should when time.
	public RSA()
	{
		encry_mess = new ArrayList<Long>();
		Random rand = new Random();
		
		//random number for prime number selection
		int rand_num1 = rand.nextInt(25) + 1; //rand.nextInt(MAX) + MIN
		int rand_num2 = rand.nextInt(25) + 1;
		
		//THING TO EDIT!!!
		//MAKE IT SO THAT THE PRIMES MULTIPLIED ARE JUST OVER 16384 [max_block_value] INSTAD OF
		//MAKING THEM BOTH OVER 131.
		
		//or....
		
		//34955 private!
		//11267 is still too big?
		
		
		int prime_p = primeNumGen(max_prime, least_prime,rand_num1);
		int prime_q = primeNumGen(max_prime, least_prime,rand_num2);
		
		RSA_helper(prime_p, prime_q);
		
		
	}
	
	//makes keys by user input
	//promp user saying they need to enter a prime number over 131 for it to work.
	public RSA(int prime_p, int prime_q)
	{
		encry_mess = new ArrayList<Long>();
		
		boolean prime1 = isPrime(prime_p);
		boolean prime2 = isPrime(prime_q);
		
		if(!prime1 || !prime2)
		{
			//display error message saying that at least one number is not prime
			//re-enter prime numbers
		}
		else
		{
			if(prime_p < least_prime || prime_q < least_prime)
			{
				//display error message saying that at least one number is not high enough
				//re-enter prime numbers over 131 [for easy sake]
			}
			else
			{
				RSA_helper(prime_p, prime_q);
			}
		}
	}
	
	private void RSA_helper(int prime_p, int prime_q)
	{
		//Random rand = new Random();
		
		shared_rsa = prime_p*prime_q; //n generated
		
		int phi = (prime_p - 1)*(prime_q - 1);
		
		int porential_e = 2;
		
		boolean looper = true;
		//int rand_num3 = rand.nextInt(100) + 1; //used for even more random encryption of e.
		//int counter = 0;
		
		while(looper)
		{
			if(GCD(porential_e, phi) == 1)
			{
				//if(counter == rand_num3)
				//{
					looper = false;
					public_rsa = porential_e; //e generated
				//}
				//counter++;
			}
			
			porential_e++;
		}
		
		looper = true;
		
		double potential_d_helper1 = 0.0;
		double potential_d_helper2;
		
		while(looper)
		{
			potential_d_helper2 = (1.0 + (potential_d_helper1*phi)) / public_rsa; 
			
			if ((potential_d_helper2 == Math.floor(potential_d_helper2)) && !Double.isInfinite(potential_d_helper2)) {
			    // integer type
				setPrivate_rsa((int)potential_d_helper2); //generate d
				looper = false;
			}
			
			potential_d_helper1++;
		}
		
		//d = (1 + n*phi) / public_rsa
		
		
		//prime_p * prime_q must equal over max_block_value, so each must be AT LEAST 131, but for variety
		//lets make the max 997 so . 
		// the next 135 prime numbers after that works
	}
	
	
	private static int GCD(int number1, int number2) {
        //base case
        if(number2 == 0){
            return number1;
        }
        return GCD(number2, number1%number2);
    }
	
	
	//https://examples.javacodegeeks.com/java-basics/for-loop/generate-prime-numbers-with-for-loop/
	private static int primeNumGen(int max, int at_least, int rand_to_pick)
	{
		int this_will_never_be_returned = -999;
		int count = 0;
		//System.out.println("Generate Prime numbers between 1 and " + max);

		// loop through the numbers one by one
		for (int i = at_least; i<max; i++) {

			boolean isPrimeNumber = true;

			// check to see if the number is prime
			for (int j = 2; j < i; j++) {
				if (i % j == 0) {
					isPrimeNumber = false;
					break; // exit the inner for loop
				}
			}
			
			// print the number if prime
			if (isPrimeNumber) {
				//System.out.print(i + " ");
				//return i;
				count++;
				if(count == rand_to_pick)
				{
					return i;
				}
			}
		}
		
		return this_will_never_be_returned;
	}
	
	
	//used for when user wants to provide two prime numbers.
	//NOTES FOR USER PROVIDED PRIMES: p * q MUST BE BIGGER THAN max_block_value. Meaning both MUST BE BIGGER than 131, or at least
	//how I am going to make this @required_over_131
	//First check if prime, then check if each are over 131, then check if its over max block value. THEN generate numbers like normal.
	//https://stackoverflow.com/questions/14650360/very-simple-prime-number-test-i-think-im-not-understanding-the-for-loop
	private static boolean isPrime(int x)
	{
		int i;
	    for (i = 2; i <= x/2; i++) {
	        if (x % i == 0) {
	            return false;
	        }
	    }
	    return true;
	}
	
	public static ArrayList<Long> encryption(String message, int public_rsa_e, int shared_rsa_n)
	{
		if(!encry_mess.isEmpty())
		encry_mess.clear();
		
		int m_length = message.length();
		boolean isEven;
		
		if(m_length % 2 == 0)
		{
			isEven = true;
		}
		else
		{
			isEven = false;
		}
		
		int code;
		
		//message example: Hi!
		// length is 3, isEven returns false.
		
		for(int i = 0; i < m_length; i = i + 2)
		{
			code = 0;
			
			code = (int)message.charAt(i) * 128;
			
			if(!isEven)
			{
				if(i == (m_length-1))
				{
					code = code + 0; //for ending null character!
				}
				else
				{
					code = code + (int)message.charAt(i+1);
				}
			}
			else
			{
				code = code + (int)message.charAt(i+1);
			}
			
			BigInteger power_part = BigInteger.valueOf(code).pow(public_rsa_e);
			
			BigInteger encry_part = power_part.mod(BigInteger.valueOf(shared_rsa_n));
			
			long val = encry_part.longValue();
			
			encry_mess.add(val);
		} //9321 //4224
		
		return encry_mess;
		
	}
	
	public static String decryption(ArrayList<Long> encoded, int private_rsa_d, int shared_rsa_n)
	{
		char[] message = new char[300];
		
		//{ [character] = C^d % n }
		int counter = 0;
		int counter2 = 0;
		while(encoded.size() != counter)
		{
			long peice = encoded.get(counter);
			
			BigInteger big = BigInteger.valueOf(peice).pow(private_rsa_d);
			
			big = big.mod(BigInteger.valueOf(shared_rsa_n));
			
			int i = big.intValue();
			
			int ascii_val1 = i / (128);
			
			message[counter2] = (char)ascii_val1;
			counter2++;
			
			
			int ascii_val2 = i % (128);
			
			message[counter2] = (char)ascii_val2;
			counter2++;
			
			counter++;
		}
		
		String readable_message = new String(message);
		
		return readable_message;
	}

	public static void main(String[] args) {
		
		RSA meow = new RSA();
		
		ArrayList<Long> demo = encryption("How long does it take to get to the center of a tootsieroll pop?", meow.public_rsa, meow.shared_rsa);
		
		String cat = decryption(demo, meow.getPrivate_rsa(), meow.shared_rsa);
		
		System.out.print(cat);
		
	}

	public int getPrivate_rsa() {
		return private_rsa;
	}

	public void setPrivate_rsa(int private_rsa) {
		this.private_rsa = private_rsa;
	}
	
}
