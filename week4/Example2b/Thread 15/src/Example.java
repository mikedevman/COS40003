import java.util.Random;

public class Example {

	public static void main(String[] args) {
		
		Random rand = new Random();
		
		int anArray[] = new int[10];
		for(int i=0; i<10; i++)
			anArray[i] = rand.nextInt();
		
		Thread t1 = new A(anArray);
		
		Thread t2 = new B(anArray);
		
		t1.start();
		t2.start();
		
		try{
			t1.join();
			t2.join();
		}catch(InterruptedException e) {
			
		}
		
		for(int i=0; i<10; i++)
			System.out.println("Number "+ "i: " + anArray[i]);
		
	}
}
