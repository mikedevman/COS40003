
public class B extends Thread{
	int array[];

	public B(int input[]) {
		this.array = input;
	}
	public void run()
	{
		int max = Integer.MIN_VALUE;
		for(int i=0; i<array.length; i++)
			if (array[i] > max)
				max = array[i];

		System.out.println("Max is "+ max);
	}
}

