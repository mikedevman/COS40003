
public class Example {

	public static void main(String[] args) {
		
		Thread t1 = new A();
		Thread t2 = new B();
		
		t1.start();
		t2.start();
	}
}
