import java.util.*;
import java.io.*;

public class Node {
    int id;
    File ipFile;
    File opFile;
    File rcvdFile;
    int timer;
    int destination;
    String message;
    HashMap<Integer, List<Integer>> inTree;
    List<Map<Integer, List<Integer>>> receivedTrees = new ArrayList<Map<Integer,List<Integer>>>();

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

        for(int idx=0;idx<10;idx++)
        {
            receivedTrees.add(null);
        }

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
        String receivedFileName = "received/received_" + id + ".txt";
        try {
            rcvdFile = new File(receivedFileName);
            rcvdFile.createNewFile();
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
        if(i==-1)
         {System.out.println("[LOG]: Intree resent due to chage"); }
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

        // System.out.println("At Hop "+ hopCount+" t1 tree neighbors are "+t1Neighbors.size()+" in size and t2 tree neighbors are "+t2Neighbors.size()+" in size" );
        // System.out.println("T1 neighbors are : ");
        // for(ItreeNeighbor it:t1Neighbors)
        // System.out.print(it.id+" ");
        // System.out.println("T2 neighbors are : ");
        // for(ItreeNeighbor it:t2Neighbors)
        // System.out.print(it.id+" ");
        // System.out.println("");


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
                 else if(first.parent>second.parent)
                 {
                   
                    List<Integer> li =   combinedTree.getOrDefault(second.id,new ArrayList<>());
                    if(!li.contains(second.parent))
                       li.add(second.parent);
                    combinedTree.put(second.id,li);
                    if(t1.containsKey(second.id))
                      t1.remove(second.id); // remove from t1
    

                 }

                 else{
                    List<Integer> li =   combinedTree.getOrDefault(second.id,new ArrayList<>());
                    if(!li.contains(second.parent))
                       li.add(second.parent);
                    combinedTree.put(second.id,li);

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

         if(t1Neighbors.size()==0 && t2Neighbors.size()==0)
         break;
       

        }
   System.out.println("***********************************************"); 
   System.out.println("Combined Tree  for node "+id+" is "+combinedTree);
   System.out.println("***********************************************"); 
   boolean changed = treeChanged(inTree, combinedTree);
   if(changed)
    {
   inTree = new HashMap<>(combinedTree);
   sendIntree(-1);
    }
        

   }

   private boolean treeChanged(HashMap<Integer, List<Integer>> myTree, HashMap<Integer,List<Integer>>newTree)
   {
    if(myTree.keySet().size()!= newTree.keySet().size())
       return true;
    for(Integer i : myTree.keySet())
    {   if(!newTree.containsKey(i))
           return true; 
        

        List<Integer> l1 = myTree.get(i);
        List<Integer>l2 = newTree.get(i);
        if(l1.size()!=l2.size())
        return true;
        for(int t =0;t<l1.size();t++)
        {
            if(l1.get(t)!=l2.get(t))
               return true;
        }
    }   

    return false;
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


  // System.out.println("Processing "+hop+" neighbors of "+id+" using"+temp); 
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
                        
                        receivedTrees.set(senderId, new HashMap<>(t2)); 


                      //  System.out.println("t2 at  " + id + " :  " + t2 + "received from "+senderId);
                        
                        
                        if(t2.containsKey(id))
                           t2.remove(id); // remove tree rooted at current node

                        List<Integer> temp = t2.getOrDefault(senderId, new ArrayList<>());
                        if(!temp.contains(id))
                           temp.add(id);
                        t2.put(senderId,temp); // adding edge from sender --> me in the tree

                       HashMap<Integer, List<Integer>> t1 =new HashMap<>(inTree);
                      
                       combineIntrees(t1,t2);    
                    }
                  else if (msgSplit[0].equals("data"))
                  { int source = Integer.parseInt(msgSplit[1]);
                    String temp = msgSplit[2];
                    String sourceRoute = temp.substring(1,temp.length()-1);
                    String [] sourceRouteElements = sourceRoute.split(",");
                    int immediateDestination = Integer.parseInt(""+sourceRouteElements[0]);
                    if(immediateDestination!=id)
                        { continue;}
                    if(sourceRouteElements.length>1) // still in source Routing phase
                    {
                       StringBuilder newSourceRoute = new StringBuilder();
                       newSourceRoute.append("(");
                       for(int i=1;i<sourceRouteElements.length;i++)
                       {
                           newSourceRoute.append(sourceRouteElements[i]);
                           newSourceRoute.append(",");
                       }
                       newSourceRoute.append(")");
                       StringBuilder newMsg = new StringBuilder();
                       newMsg.append("data ");
                       newMsg.append(source);
                       newMsg.append(" ");
                       newMsg.append(newSourceRoute.toString());
                       for(int i=3;i<msgSplit.length;i++)
                       {
                           newMsg.append(" ");
                           newMsg.append(msgSplit[i]);
                       }
                       System.out.println("[LOG]: Node "+id+" still in source routing phase, hence forwarding message");
                       writeToFile(newMsg.toString());

                    }
                    else
                    {    // source Route length is only 1 which means we are at the end of the source route.
                         if(msgSplit[3].equals("begin"))
                         {  // there is nothing more after the source route meaning we are at the final destination.
                            System.out.println("[SUCCESS]: At Node "+id+", message has reached final destination");
                            StringBuilder rcvdMsg = new StringBuilder();
                            for(int i =4;i<msgSplit.length;i++)
                            {
                                rcvdMsg.append(msgSplit[i]);
                                rcvdMsg.append(" ");

                            }
                            rcvdMsg.deleteCharAt(rcvdMsg.length()-1);
                            writeToReceivedFile(rcvdMsg.toString());

                         }
                         else {  // source route is done but there are more nodes in the rest of the path which needs to be handled
                                 int nextHop = Integer.parseInt(msgSplit[3]);

                                 List<Integer> path =new ArrayList<>();
                                 if(!inTree.containsKey(nextHop))
                                     {
                                         System.out.println("[ABORT]: In tree does not contain entry for next hop");
                                         continue;
                                     }
                                 int curr = inTree.get(nextHop).get(0);
                                 path.add(nextHop);

                                 while(curr!=id)
                                 { 
                                    path.add(curr);
                                    curr= inTree.get(curr).get(0);
                                 }
                                Collections.reverse(path);
                                int firstHop = path.get(0);
                                path.remove(0);
                                if(receivedTrees.get(firstHop)==null)
                                  { System.out.println("[ABORT]:  Aborting Message send as relevant intree not received **"); 
                                    continue;
                                  }
                                
                              
                      
                                List<Integer> initialPath  = new ArrayList<>();
                                int initialFirstHop = receivedTrees.get(firstHop).get(id).get(0);
                              
                                while(initialFirstHop!=firstHop)
                                {
                                    initialPath.add(initialFirstHop);
                                    initialFirstHop = receivedTrees.get(firstHop).get(initialFirstHop).get(0);
                                }
                                initialPath.add(firstHop); 
                               
                                StringBuilder newMsg = new StringBuilder();
                                newMsg.append("data ");
                                newMsg.append(source);
                                newMsg.append(" ");
                                if(initialPath.size()>0)
                                {
                                    newMsg.append("(");
                                    for(Integer i :initialPath)
                                      {
                                          newMsg.append(i);
                                          newMsg.append(",");
                                      }
                                      newMsg.append(")");
                                }
                                newMsg.append(" ");
                                if(path.size()>0)
                                {
                                for(Integer i :path)
                                {
                                    newMsg.append(i);
                                    newMsg.append(" ");
                                }  
                            }
                            for(int i=4;i<msgSplit.length;i++)
                            {
                                newMsg.append(msgSplit[i]);
                                newMsg.append(" ");
                            }
                            newMsg.deleteCharAt(newMsg.length()-1); 
                           System.out.println("[LOG]:Calculated new source route writing to file");
                           writeToFile(newMsg.toString());

                         }


                    }

                    
                    
                  }


                }
                br.close();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return ipFile.length();

    }


    private void writeToReceivedFile(String msg)
    {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(rcvdFile, true))) {
            writer.write(msg);
            writer.write(System.lineSeparator());

            writer.close();
        } catch (IOException e) {

            e.printStackTrace();
        }

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


                    if(i%15==0 && message!=null)
                    {
                        prepareDataMessage(i);
                    }    

                    lastRead = readMyMessages(lastRead);
                    obj.wait(1000);

                }
            }
        }

        catch (Exception e) {

        }
    }

    private void prepareDataMessage(int i )
    {
          if(!inTree.containsKey(destination))
            {
                System.out.println("[ABORT]: Aborting Message send in node"+id+" for message  "+message+" due to lack of path**");
                return;
            }
          List<Integer> path = new ArrayList<>();
          path.add(destination);
          int currHop = inTree.get(destination).get(0); 
          while(currHop!=id)
          {
              path.add(currHop);
              if(!inTree.containsKey(currHop)) 
              {   // intermediate path not found
                  System.out.println("[ABORT]: Aborting Message send in node " +id+" for hop "+currHop+" due to lack of path**");
                  return;
              }
              currHop= inTree.get(currHop).get(0);

          } 
          Collections.reverse(path);
          
          int firstHop = path.get(0);
          path.remove(0);

          if(receivedTrees.get(firstHop)==null)
            { System.out.println("[ABORT]:  Aborting Message send as relevant intree not received **"); 
              return;
            }
          
        

          List<Integer> initialPath  = new ArrayList<>();
          int initialFirstHop = receivedTrees.get(firstHop).get(id).get(0);
        
          while(initialFirstHop!=firstHop)
          {
              initialPath.add(initialFirstHop);
              initialFirstHop = receivedTrees.get(firstHop).get(initialFirstHop).get(0);
          }
          initialPath.add(firstHop);
          System.out.println("[LOG]: Original Message "+message+" from "+id +" to destination "+destination+" will take path "+initialPath+"&&&&&");
          

          String messageToBeSent = convertToDataMessage(message,initialPath,path,id);

          writeToFile(messageToBeSent);
        //   if(nextHop == destination)
        //    {   String msgString = "data "+id+" "+nextHop+" begin "+message+" sent at time "+i;
        //        writeToFile(msgString);
        //    }

        //   if(receivedTrees.get(nextHop)==null)
        //    {
        //        System.out.println("** Aborting Message send as relevant intree not received");
        //    }

    }

  private String convertToDataMessage(String msg, List<Integer>initialPath, List<Integer>path, int src)
  {   StringBuilder sb = new StringBuilder();  
      sb.append("data ");
      sb.append(src);
      sb.append(" ");
      if(initialPath.size()>0)
      {
      sb.append("(");
      for(Integer i: initialPath)
      {
          sb.append(i);
          sb.append(",");
      }
      sb.deleteCharAt(sb.length()-1);
      sb.append(")");
      sb.append(" ");

    }

    if(path.size()>0)
    { 
      for(Integer i: path) 
      {
          sb.append(i);
          sb.append(" ");
      }
    }
      sb.append("begin ");
      sb.append(msg);

      return sb.toString();
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
