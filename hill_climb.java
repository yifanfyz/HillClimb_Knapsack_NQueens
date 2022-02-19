import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.random.*;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.*;



public class hill_climb {

    public static void hillClimbAlgorithm(int v, int r, int s, State initialState){ 
        if(initialState.n == -1){
            if(initialState.Value()==0){
                System.out.println("Goal: "+initialState.getStart()+" = "+initialState.error);
                System.exit(0);
            }
        }else{
            if(initialState.ValueQ()==0){
                System.out.println("Goal: "+initialState.getStart()+" = "+initialState.error);
                System.exit(0);
            }
        }
        
        if(initialState.n == -1){
            initialState.Next();
        }else{
            initialState.NextQ();
        }
        
        long minError = initialState.error;
        State selectedState = initialState;
        for(int i=0; i<initialState.reachableStates.size(); i++){
            int currS = s;
            State currState = initialState.reachableStates.get(i);
            if(currState.error<minError){
                currS = s;
                selectedState = currState;
                minError = selectedState.error;
            }else if(currState.error==minError){
                if(currState.totalValue>selectedState.totalValue && currS>0){
                    selectedState = currState;
                    currS = currS - 1;
                }       
            }

            if(v==1){
                System.out.println(currState.getStart()+" = "+ currState.error);
            }
        }
        
        if(initialState.getStart().equals(selectedState.getStart())){
            if(r<=0){
                System.out.println("Failed.");
                System.exit(-1);
            }
            else if(r>0){

                if(selectedState.n==-1){
                    State reStartState = selectedState.Restart();
                    System.out.println("Restart with: "+ reStartState.getStart());
                    r = r - 1;
                    hillClimbAlgorithm(v, r, s, reStartState);
                }
                else{
                    State reStartState = selectedState.RestartQ();
                    System.out.println("Restart with: "+ reStartState.getStart());
                    r = r - 1;
                    hillClimbAlgorithm(v, r, s, reStartState);
                }
            }       
        }

        System.out.println("choose: "+ selectedState.getStart()+" = "+ selectedState.error);
        hillClimbAlgorithm(v, r, s, selectedState);
    }

    public static State setUpKnapsack(String[] args) throws Exception{
        BufferedReader reader = new BufferedReader(new FileReader(args[args.length-1]));
        String json = "";
        try {
            StringBuilder sb = new StringBuilder();
            String line = reader.readLine();

            while (line != null) {
                sb.append(line);
                sb.append("\n");
                line = reader.readLine();
            }
            json = sb.toString();
        } finally {
            reader.close();
        }

        Object object = new JSONParser().parse(json);
        JSONObject js = (JSONObject)object;
        if(!js.containsKey("T") || !js.containsKey("M")){
            throw new Exception("Missing T or M");
        }
        long t = (long)js.get("T");
        long m = (long)js.get("M");
        ArrayList<String> start = new ArrayList<String>();
        if(js.containsKey("Start")){
            JSONArray startArray = (JSONArray)js.get("Start");
            for(int i=0; i<startArray.size(); i++){
                start.add((String)startArray.get(i));
            }
        }
        JSONArray ItemsArray = (JSONArray)js.get("Items");
        ArrayList<Items> items = new ArrayList<Items>();
        for(int i=0; i<ItemsArray.size(); i++){
            JSONObject temp = (JSONObject)ItemsArray.get(i);
            String name = (String)temp.get("name");
            long value = (long)temp.get("V");
            long weight = (long)temp.get("W");
            Items tempItem = new Items(name, value, weight);
            items.add(tempItem);
        }
        State knapsack = new State(t, m, start, items);
        return knapsack;
    }

    public static State setUpQueens(String[] args){
        int n = 0;
        try{
            for(int i=0; i<args.length; i++){
                if(args[i].contains("-N")){
                    n =Integer.valueOf(args[i+1]);      
                }
            }
        }catch(Exception e){
            System.out.println("Argument format error. Check README file.");
        }
        ArrayList<String> start = new ArrayList<String>();
        for(int i=0;i<n;i++){
            start.add(String.valueOf(i));
        }

        State queens = new State(n, start);
        return queens;
    }

    public static int[] setUpInfo(String[] args) {
        int[] info = new int[3];
        int r = 0;
        int s = 0;
        int v = 0;
        
        try{
            for(int i=0; i<args.length; i++){
                if(args[i].contains("-verbose")){
                    v = 1;
                }else if(args[i].contains("-restarts")){
                    if(args[i].length()==9){
                        r = 0;
                    }else{
                        r = Integer.parseInt(args[i].substring(9));
                        if(r<0){
                            throw new Exception();
                        }
                    }
                }else if(args[i].contains("-sideways")){
                    if(args[i].length()==9){
                        s = 0;
                    }else{
                        s = Integer.parseInt(args[i].substring(9));
                        if(s<0){
                            throw new Exception();
                        }
                    }
                }
            }
        }catch(Exception e){
            System.out.println("Argument format error. Check README file.");
        }

        info[0] = r;
        info[1] = s;
        info[2] = v;
        return info;
    }

    public static void main(String[] args) throws Exception {
        int[] rsv = setUpInfo(args);
        int r = rsv[0];
        int s = rsv[1];
        int v = rsv[2];

        try{
            if(args[args.length-1].contains("knapsack")){
                State knapsack = setUpKnapsack(args);
                System.out.println("Start: "+knapsack.getStart()+" = "+knapsack.error);
                hillClimbAlgorithm(v,r,s, knapsack);
            }
            else if(args[args.length-2].contains("-N")){  
                State queens = setUpQueens(args);
                System.out.println("Start: "+queens.getStart()+" = "+queens.error);
                hillClimbAlgorithm(v, r, s, queens);
            }
        }catch(Exception e){
            throw new IOException("Argument format error. Check README file.");
        }
    }
}
