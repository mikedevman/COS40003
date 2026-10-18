
public class PrintNumbers {

	public static void printNumbers()
	{
		for(int i=0; i<500; i++)
		{
			System.out.println(Thread.currentThread().getName()+" prints "+i);
		}
	}
}
