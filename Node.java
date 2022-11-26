import java.util.*;
import java.io.*;
public class Node {
     int id;
     File ipFile;
     File opFile;
     int timer;
     int destination;
     String message;
     HashMap<Integer,List<Integer>> inTree;

     public Node(int i, int t,int d, String m)
     {  id =i;
        timer =t;
        destination= d;
        message=m;
        inTree= new HashMap<>();
        createFiles();
        startNode();
     
     }
     private void createFiles()
     {    String inputFileName = "inputs/input_"+id+".txt";
          String outputFileName = "outputs/output_"+id+".txt";
         try{
            ipFile =new File(inputFileName);
            ipFile.createNewFile();
            opFile =new File(outputFileName);
            opFile.createNewFile();
         }
         catch(Exception e)
            {
                e.printStackTrace();
            }
     }

    private void sendHello(int i )
    { 
        String message= "hello from "+id+" at time "+i;
        writeToFile(message);

    }
    private void writeToFile(String message)
    {
    try (BufferedWriter writer = new BufferedWriter(new FileWriter(opFile, true))) {
        writer.write(message);
        writer.write(System.lineSeparator());
  
        
        writer.close();
    } catch (IOException e) {
        
        e.printStackTrace();
    }
    }


    private void sendIntree(int i)
    {}

     private void startNode()
     {
         for(int i =1;i<=timer;i++)
         {    if(i%5==0)
                  sendHello(i);
              if(i%10==0)
                  sendIntree(i);    

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
         }
     }

     public static void main(String[] args)
     {
         Integer id =Integer.parseInt(args[0]);
         Integer duration = Integer.parseInt(args[1]);
         Integer destination = Integer.parseInt(args[2]);
         String message= null;
         if(destination!=-1)
           message = args[3];

        Node n = new Node(id,duration,destination,message);   

     }
     

    
}
