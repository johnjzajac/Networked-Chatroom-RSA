We have a working RSA class that does generate keys that can be used properly.

It is testable by running the main that is in the RSA.java class and declaring/allocating
an RSA object. When the object is made, the class is given 3 key ints:

	private int private_rsa; // d value (half of private key)
	public int public_rsa; // e value (half of public key)
	public int shared_rsa; // n value (half of both private and public key)

These are to be used for the methods:

	public static ArrayList<Long> encryption(String message, int public_rsa_e, int shared_rsa_n)
	public static String decryption(ArrayList<Long> encoded, int private_rsa_d, int shared_rsa_n)

When you feed in a string into message and give in public_rsa and shared_rsa (e, n) into
encryption, it will return a ArrayList<Long> that contains encrypted characters, two 
characters per long (in the ArrayList<Long>).

With this ArrayList<Long>, you can decrypt it by sending it into decryption with
private_rsa and shared_rsa (d, n). This will convert it back into the original string that
was given to the encryption method.


The above explains how to show that our RSA does work and work properly. If tested, the
results will show it working. When it comes to actual implementation into the chat system,
we were unable to pass ArrayList<ClientThread> properly between clients. 
When in Server.java and passing it ClientGUI.java, the ArrayList<ClientThread>'s information
was lost. We posted about it on piazza and hope to get a reply, but after working on it for
hours (and many google searches) we were unable to solve it. This does not effect how well
RSA.java should work though and hope that it will prove that we got it to work correctly,
but just not into the implementation of the program.


