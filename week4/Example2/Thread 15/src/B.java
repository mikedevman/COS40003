
public class B extends Thread{
	public void run()
	{
		while(true)
		{
			System.out.println("This is Thread B");
			try{
				Thread.sleep(3000);
			}catch(InterruptedException e){}
		}
	}
}

