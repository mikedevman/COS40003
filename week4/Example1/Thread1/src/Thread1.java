


public class Thread1 extends Thread {

	public void run(){
		System.out.println("Thread id: "+Thread.currentThread().getId());
		
		PrintNumbers.printNumbers();
	}

	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		System.out.println("Main Thread Id: "+ Thread.currentThread().getId());
		
		for (int i=0; i<3; i++){
			new Thread1().start();
		}
		
		PrintNumbers.printNumbers();
	}
}
