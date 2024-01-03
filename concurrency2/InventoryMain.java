public class InventoryMain  {
    private static int inventorySize = 0; // Number of items in stock
    private static int addCommandCount = 0; // Number of items to add to inventory
    private static int removeCommandCount = 0;// Number of items to remove from inventory
  
    public static int bugFlag; // variable holding bug flag value
    private static boolean bugFlagbool = false; //set flag false initially

    public static boolean addThreadFinished = false;
    public static boolean removeThreadFinished = false;
    
    public static void main(String[] args)
    {
        if (args.length != 3)
        {
            System.out.println("Invalid number of parameters");
        }
        else
        {
            try
            {
                //declare containing the number
                addCommandCount = Integer.parseInt(args[0]);

                //declare variable containing the number
                removeCommandCount = Integer.parseInt(args[1]);

                //declare bug flag
                bugFlag = Integer.parseInt(args[2]);

                if (bugFlag != 0 && bugFlag != 1)

                {
                    System.out.println("Invalid bug flag");
                    return;

                }
                else
                {
                    bugFlagbool = bugFlag ==1; // 1=> TRUE, 0=> FALSE
                }
            } 

            catch (Exception e)
            {
                System.out.println(e.getMessage());
                return;
            }
        }

        StartProgram();

    }
    private static void StartProgram()
    {
        RunAddThread();
        RunRemoveThread();

        while(addThreadFinished == false || removeThreadFinished == false)
        {
            try
            {
                Thread.sleep(50);
            }
            catch (Exception e)
            {
                //do nothing
            }
        }

        System.out.println("\nFinal Inventory size =");
        System.out.println(inventorySize);
    }
    public static void RunAddThread()
    {
        new Thread()
        {
            public void run(){
                for(int i = 0; i < addCommandCount; i ++)
                {
                    // logic
                    inventorySize++;
                    System.out.print("\nAdded. Inventory size =");
                    System.out.println(inventorySize);
                }
                addThreadFinished = true;
            }
        }.start();
    }
    
    public static void RunRemoveThread()
    {
        new Thread()
        {
            public void run() {
                for(int i = 0; i < removeCommandCount; i ++)
                {
                    // logic
                    inventorySize--;
                    System.out.print("\nRemoved. Inventory size =");
                    System.out.println(inventorySize);
                }
                removeThreadFinished = true;
            }
        }.start();
    }

}
