import java.util.*;
import java.util.spi.ToolProvider;
import java.io.*;

public class Node {
    int id;
    File ipFile;
    File opFile;
    int timer;
    int destination;
    String message;
    HashMap<Integer, List<Integer>> inTree;

    public Node(int i, int t, int d, String m) {
        id = i;
        timer = t;
        destination = d;
        message = m;
        inTree = new HashMap<>();

        List<Integer> li = new ArrayList<Integer>();
        li.add(i);
        inTree.put(i, li);
        // remove

        createFiles();
        startNode();

    }



    class ItreeNeighbor {
        int id;
        int parent;

        public ItreeNeighbor(int i, int p)
        {
            id= i;
            parent=p;
        }
    }

    private void createFiles() {
        String inputFileName = "inputs/input_" + id + ".txt";
        String outputFileName = "outputs/output_" + id + ".txt";
        try {
            ipFile = new File(inputFileName);
            ipFile.createNewFile();
            opFile = new File(outputFileName);
            opFile.createNewFile();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void sendHello(int i) {
        String message = "hello " + id + " " + i;
        writeToFile(message);

    }

    private void writeToFile(String message) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(opFile, true))) {
            writer.write(message);
            writer.write(System.lineSeparator());

            writer.close();
        } catch (IOException e) {

            e.printStackTrace();
        }
    }

    private void sendIntree(int i) {
        String iTree = "intree " + id + " " + stringifyIntree();
        writeToFile(iTree);
    }

    private String stringifyIntree() {
        StringBuilder res = new StringBuilder();

        for (Integer i : inTree.keySet()) {
            res.append(i + ":");
            List<Integer> el = inTree.get(i);
            for (Integer e : el) {
                res.append(e + ",");
            }
            res.append("|");
        }
        return res.toString();

    }

    private HashMap<Integer, List<Integer>> mapifyIntree(String tree) {
        HashMap<Integer, List<Integer>> tempInTree = new HashMap<>();
        if(tree.length()==0) return tempInTree;
        String[] entries = tree.split("\\|");
        for (String e : entries) {
            Integer key = Integer.parseInt(e.split(":")[0]);
            List<Integer> li = new ArrayList<>();

            String[] vals = e.split(":")[1].split(",");

            for (String v : vals) {
                li.add(Integer.parseInt(v));
            }
            if(li.contains(key))  // remove?
              li.remove(key);
            if(li.size()>0)  
            tempInTree.put(key, li);
        }
        return tempInTree;

    }

   private void combineIntrees(HashMap<Integer, List<Integer>>t1,HashMap<Integer, List<Integer>>t2)
   {
        int hopCount=1;
        List<ItreeNeighbor> t1Neighbors = getHopNeighbors(t1,hopCount);
        List<ItreeNeighbor> t2Neighbors = getHopNeighbors(t2, hopCount);

        System.out.println("At Hop "+ hopCount+" t1 tree neighbors are "+t1Neighbors.size()+" in size and t2 tree neighbors are "+t2Neighbors.size()+" in size" );
        System.out.println("T1 neighbors are : ");
        for(ItreeNeighbor it:t1Neighbors)
        System.out.print(it.id+" ");
        System.out.println("T2 neighbors are : ");
        for(ItreeNeighbor it:t2Neighbors)
        System.out.print(it.id+" ");
        System.out.println("");


        HashMap<Integer, List<Integer>> combinedTree = new HashMap<>();

        while(hopCount<10) 
        {  
          
          Collections.sort(t1Neighbors,(a,b)-> a.id-b.id);
          Collections.sort(t2Neighbors,(a,b)-> a.id-b.id);
          int s1= t1Neighbors.size();
          int s2 =t2Neighbors.size();
          int i=0,j=0;
          while(i<s1 && j<s2)
          {  
              ItreeNeighbor first = t1Neighbors.get(i);
              ItreeNeighbor second = t2Neighbors.get(j);

              if(first.id<second.id)
              {
                  i++;
                List<Integer> li =   combinedTree.getOrDefault(first.id,new ArrayList<>());
                if(!li.contains(first.parent))
                   li.add(first.parent);
                combinedTree.put(first.id,li);
                if(t2.containsKey(first.id))
                  t2.remove(first.id); // remove from t2
              }

            else if (second.id<first.id)
            {
                j++;
                List<Integer> li =   combinedTree.getOrDefault(second.id,new ArrayList<>());
                if(!li.contains(second.parent))
                   li.add(second.parent);
                combinedTree.put(second.id,li);
                if(t1.containsKey(second.id))
                  t1.remove(second.id); // remove from t1

            }
            
            else
            {   i++; j++;
                if(first.parent<second.parent)
                 {
                    
                    List<Integer> li =   combinedTree.getOrDefault(first.id,new ArrayList<>());
                    if(!li.contains(first.parent))
                       li.add(first.parent);
                    combinedTree.put(first.id,li);
                    if(t2.containsKey(first.id))
                      t2.remove(first.id); // remove from t2
                 }
                 else
                 {
                   
                    List<Integer> li =   combinedTree.getOrDefault(second.id,new ArrayList<>());
                    if(!li.contains(second.parent))
                       li.add(second.parent);
                    combinedTree.put(second.id,li);
                    if(t1.containsKey(second.id))
                      t1.remove(second.id); // remove from t1
    

                 }
            }

          }

          while(i<s1)
          {  
              ItreeNeighbor curr = t1Neighbors.get(i);
              
              List<Integer> li =   combinedTree.getOrDefault( curr.id,new ArrayList<>());
                    if(!li.contains(curr.parent))
                       li.add(curr.parent);
                    combinedTree.put(curr.id,li);


                    // test 
                    if(t2.containsKey(curr.id))
                      t2.remove(curr.id); // remove from t2
                      //endtest
               i++;
          }
          while(j<s2)
          {

            ItreeNeighbor curr = t2Neighbors.get(j);
              
            List<Integer> li =   combinedTree.getOrDefault( curr.id,new ArrayList<>());
                  if(!li.contains(curr.parent))
                     li.add(curr.parent);
                  combinedTree.put(curr.id,li);
                    // test 
                    if(t1.containsKey(curr.id))
                      t1.remove(curr.id); // remove from t2
                      //endtest
             j++;

          }


         hopCount++;
         t1Neighbors= getHopNeighbors(t1, hopCount);
         t2Neighbors = getHopNeighbors(t2, hopCount);
         System.out.println("At Hop "+ hopCount+" t1 tree neighbors are "+t1Neighbors.size()+" in size and t2 tree neighbors are "+t2Neighbors.size()+" in size" );
         System.out.println("T1 neighbors are : ");
         for(ItreeNeighbor it:t1Neighbors)
         System.out.print(it.id+" ");
         System.out.println("T2 neighbors are : ");
         for(ItreeNeighbor it:t2Neighbors)
         System.out.print(it.id+" ");
         System.out.println("");

         if(t1Neighbors.size()==0 && t2Neighbors.size()==0)
         break;
       

        }

   System.out.println("Combined Tree  for node "+id+" is "+combinedTree);
   inTree = new HashMap<>(combinedTree);
        

   }
   private List<ItreeNeighbor> getHopNeighbors(HashMap<Integer,List<Integer>> h , int hop)
   {
         

       List<Integer> res =new ArrayList<>();
       HashMap<Integer, List<Integer>>temp = new HashMap<>();
       for(int i=0;i<10;i++)
       { 
           for(Integer k : h.keySet())
           {
               List<Integer> li = h.get(k);
               if(li.contains(i))
               {
                   List<Integer> neighbors = temp.getOrDefault(i, new ArrayList<>());
                   neighbors.add(k);
                   temp.put(i,neighbors);
               }
           }
       }
   if(temp.containsKey(id))
   {
   List<Integer> rm = temp.get(id);
   if(rm.contains(id)) rm.remove(Integer.valueOf(id));  
   if(rm.size()==0) temp.remove(id);
   else
   temp.put(id,rm);
   }


   System.out.println("Processing "+hop+" neighbors of "+id+" using"+temp); 
    List<ItreeNeighbor> toBeProcessed =new ArrayList<>();
    toBeProcessed.add(new ItreeNeighbor(id, -1));
    int currIter=0;
    while(currIter<hop)
    {
        toBeProcessed = processHop(toBeProcessed,temp);
        currIter++;
    }

     return toBeProcessed;


   } 

 private List<ItreeNeighbor> processHop( List<ItreeNeighbor> toBeProcessed, HashMap<Integer, List<Integer>> temp)
 {
     List<Integer> res= new ArrayList<>();
     List<ItreeNeighbor> finRes = new ArrayList<>();

     for(ItreeNeighbor it : toBeProcessed)
     {   int i = it.id;
         if(temp.containsKey(i))
         {
             List<Integer> li= temp.get(i);
             for(Integer l :li)
             {
                 if(!res.contains(l))
                  {res.add(l);   finRes.add(new ItreeNeighbor(l, i));    }
             }
         }
     }

     return finRes;
 }  

 

    private long readMyMessages(long last) {
        try {
            if (last < ipFile.length()) {
                BufferedReader br = new BufferedReader(new FileReader(ipFile));
                br.skip(last);

                String msg = null;
                while ((msg = br.readLine()) != null) {
                    String[] msgSplit = msg.split(" ");
                    if (msgSplit[0].equals("intree")) {

                        Integer senderId = Integer.parseInt(msgSplit[1]);
                        String hString = msgSplit.length>2?msgSplit[2]:"";
                        HashMap<Integer, List<Integer>> t2 = mapifyIntree(hString);
                        System.out.println("t2 at  " + id + " :  " + t2 + "received from "+senderId);
                        
                        
                        if(t2.containsKey(id))
                           t2.remove(id); // remove tree rooted at current node

                        List<Integer> temp = t2.getOrDefault(senderId, new ArrayList<>());
                        if(!temp.contains(id))
                           temp.add(id);
                        t2.put(senderId,temp); // adding edge from sender --> me in the tree

                       HashMap<Integer, List<Integer>> t1 =new HashMap<>(inTree);
                       System.out.println("combining");
                       combineIntrees(t1,t2);    
                    }

                }
                br.close();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return ipFile.length();

    }

    private void startNode() {

        Object obj = new Object();
        try {
            synchronized (obj) {
                long lastRead = 0;
                for (int i = 0; i <timer+5; i++) {
                    if (i % 5 == 0)
                        sendHello(i);
                    if (i % 10 == 0)
                        sendIntree(i);

                    lastRead = readMyMessages(lastRead);
                    obj.wait(1000);

                }
            }
        }

        catch (Exception e) {

        }
    }

    public static void main(String[] args) {
        Integer id = Integer.parseInt(args[0]);
        Integer duration = Integer.parseInt(args[1]);
        Integer destination = Integer.parseInt(args[2]);
        String message = null;
        if (destination != -1)
            message = args[3];

        Node n = new Node(id, duration, destination, message);

    }

}
