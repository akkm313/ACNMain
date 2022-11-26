import java.io.*;
import java.util.*;

public class Controller {

HashMap<Integer, List<Integer>> networkTopology;
File topology;
int timer;
networkNode[] nodes =new networkNode[10];

public Controller(int t)
{
    networkTopology = new HashMap<>();
    topology = new File("topology.txt");
    timer = t;
    Arrays.fill(nodes, null);
    setupTopology();
    runController();
}

private void setupNetworkNodes()
{
    for(int i =0;i<10;i++)
    {
        if(nodes[i]==null)
        {
            String ipFileName = "inputs/input_"+i+".txt";
            String opFileName = "outputs/output_"+i+".txt";
            File ip = new File(ipFileName);
            File op = new File(opFileName);
            if(ip.exists() && op.exists())
            {
                nodes[i]= new networkNode(i,op,ip);
            }
        }
    }
}

private void runController()
{
    long start =System.currentTimeMillis();
    while(System.currentTimeMillis()<= start+ (timer*1000)) // run each second
    {  
        setupNetworkNodes();
        for(int i =0;i<10;i++)
        {
            if(nodes[i]!=null)
              nodes[i].readFromFile();
        }
        
    }
}

class networkNode 
{
    int id;
    List<Integer> neighbors;
    File ipFile;
    File opFile;
    long lastRead;

    public networkNode( int i, File ip, File op )
    {
        id = i;
        ipFile =ip;
        opFile= op;
        neighbors = networkTopology.getOrDefault(id,new ArrayList<Integer>());
        lastRead =0;
        System.out.println("Created node "+id);

    }

private void writeToFile(String line)  throws IOException
{
    BufferedWriter writer = new BufferedWriter(new FileWriter(opFile, true));
                                
    writer.write(line);
    writer.write(System.lineSeparator());
    System.out.println("Controller written data");
    
    writer.close();
}

public void readFromFile()
{  
    try{
    if (lastRead < ipFile.length())
    {
        BufferedReader br = new BufferedReader(new FileReader(ipFile)); 
        br.skip(lastRead);
        String reading =null;
        while((reading=br.readLine())!=null)
        {   if(neighbors.size()>0)
             { 
            for(Integer i : neighbors)
            {
                if(nodes[i]!=null)
                {
                    nodes[i].writeToFile(reading);
                }
            }
        }
        }
        lastRead = ipFile.length();
        br.close();

    }

} 

catch(Exception e )
{e.printStackTrace();}
}
   
}







private void setupTopology()
{
    try {
        BufferedReader fileReader = new BufferedReader(new FileReader(topology));
        String currentLine = fileReader.readLine();
        while(currentLine!=null)
        { String [] split= currentLine.split(" ");
          Integer source =Integer.parseInt(split[0]);
          Integer destination = Integer.parseInt(split[1]);
          List<Integer> li =   networkTopology.getOrDefault(source, new ArrayList<Integer>());
          li.add(destination);
          networkTopology.put(source, li);
          currentLine = fileReader.readLine();
        }



    } catch (FileNotFoundException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
    } catch (IOException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
    }

    System.out.println(networkTopology);

}


public static void main(String[]args)
{   Integer time = Integer.parseInt(args[0]);
    Controller c =new Controller(time);
   
}


}