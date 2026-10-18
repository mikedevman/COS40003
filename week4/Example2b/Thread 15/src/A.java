
public class A extends Thread{
	
	int myArray[];
	
	
	public A(int input[])
	{
		this.myArray = input;
	}
	
	
	public void run()
	{
		int min = Integer.MAX_VALUE;
		for(int i=0; i<myArray.length; i++)
			if (myArray[i] < min) 
				min = myArray[i];
		
		System.out.println("Min is "+ min);
	}
}


