
public class PrintNumbers {

	public static void printNumbers()
	{
		for(int i=0; i<10; i++)
		{
			System.out.println("Thread "+Thread.currentThread().getId()+" prints "+i);
		}
	}
}
