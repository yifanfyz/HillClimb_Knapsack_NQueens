import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;



public class State {

    int n = -1;
    int num_line = 0;
    long target;
    long maxWeight;
    long totalValue = 0;
    long error;
    ArrayList<State> reachableStates = new ArrayList<State>();
    ArrayList<String> start = new ArrayList<String>();
    ArrayList<Items> items = new ArrayList<Items>();
    
    public State(long target, long maxWeight, ArrayList<String> start, ArrayList<Items> items){
        this.target = target;
        this.maxWeight = maxWeight;
        this.start = start;
        this.items = items;
        this.error = Value();
        for(Items a: items){
            for(int i=0; i<start.size(); i++){
                if(start.get(i).equals(a.getName())){
                    totalValue = totalValue + a.getValue();   
                }                
            }
        }
    }

    public State(int n, ArrayList<String> start){
        this.n = n;
        this.start = start;
        this.error = ValueQ();
        this.totalValue = this.num_line;
    }

    public ArrayList<String> getStart(){
        return start;
    }

    public ArrayList<Items> getItems(){
        return items;
    }

    public long getTarget(){
        return target;
    }
    
    public long getMaxWeight(){
        return maxWeight;
    }

    public long getItemValue(String a){
        for(int i=0; i<items.size(); i++){
            if(items.get(i).getName().equals(a)){
                return items.get(i).value;
            }
        }
        return -1;
    }
    
    public long getItemWeight(String a){
        for(int i=0; i<items.size(); i++){
            if(items.get(i).getName().equals(a)){
                return items.get(i).weight;
            }
        }
        return -1;
    }

    public long Value(){
        long itemsValue = 0;
        long itemsWeight = 0;
        if(start.size()==0){
            return this.getTarget() + this.getMaxWeight();
        }

        for(int i=0; i<this.start.size(); i++){
            itemsValue = itemsValue + this.getItemValue(start.get(i));
            itemsWeight = itemsWeight + this.getItemWeight(start.get(i));
        }
        long maxWeight = this.getMaxWeight();
        long target = this.getTarget();
        long stateError = Math.max(itemsWeight-maxWeight, 0)+ Math.max(target-itemsValue, 0);
        return stateError;
    }

    public long ValueQ(){
        long value = 0;
        long count = 0;
        ArrayList<String> visitedPositive = new ArrayList<String>();
        ArrayList<String> visitedNegative = new ArrayList<String>();

        for(int i=0; i<n; i++){
            count = 1;
            for(int j=i+1; j<n; j++){
                if(visitedPositive.contains(start.get(j))){
                    continue;
                }
                double x = i;
                double y = Double.valueOf(start.get(i));
                double xx = j;
                double yy = Double.valueOf(start.get(j));
                double slope = (yy-y)/(xx-x);

                if(slope==1){
                    this.num_line = this.num_line - 1;
                    count = count + 1;
                    visitedPositive.add(start.get(j));
                }
            }    

            value = value + (count*(count-1))/2; 
            if(count!=1){
                visitedPositive.add(start.get(i));   
            }
           
        }
        
        for(int i=0; i<n; i++){
            count = 1;
            for(int j=i+1; j<n; j++){
                if(visitedNegative.contains(start.get(j))){
                    continue;
                }
                double x = (double)i;
                double y = Double.valueOf(start.get(i));
                double xx = (double)j;
                double yy = Double.valueOf(start.get(j));
                double slope = (yy-y)/(xx-x);

                if(slope==-1){
                    this.num_line = this.num_line - 1;
                    count = count + 1;
                    visitedNegative.add(start.get(j));  
                }
            }    

            value = value + (count*(count-1))/2; 
            if(count!=1){
                visitedNegative.add(start.get(i));   
            }
        }
        return value;
    }

    public void setStart(ArrayList<String> start){
        this.start = start;
    }

    public void Next(){
        for(int i=0; i<items.size(); i++){
            if(!start.contains(items.get(i).getName())){
                ArrayList<String> newStart = duplicate(start);
                newStart.add(items.get(i).getName());
                State nextState = new State(this.target, this.maxWeight, newStart, this.items);
                this.reachableStates.add(nextState);
            }
        }
        
        for(int i=0; i<start.size(); i++){
            ArrayList<String> newStart = duplicate(start);
            newStart.remove(i);
            State nextState = new State(this.target, this.maxWeight, newStart, this.items);
            this.reachableStates.add(nextState);
        }
         
        for(int i=0; i<items.size(); i++){
            if(!start.contains(items.get(i).getName())){
                for(int j=0; j<start.size(); j++){
                    ArrayList<String> newStart = duplicate(start);
                    newStart.remove(start.get(j));
                    newStart.add(items.get(i).getName());
                    State nextState = new State(this.target, this.maxWeight, newStart, this.items);
                    this.reachableStates.add(nextState);
                }
            }
        }

    }

    public void NextQ(){
        for(int i=0; i<n; i++){
            for(int j=i+1; j<n; j++){
                ArrayList<String> tempStart = duplicate(this.start);
                Collections.swap(tempStart, i, j);
                State tempState = new State(this.n,tempStart);
                this.reachableStates.add(tempState);
            }
        }
    }

    public ArrayList<String> duplicate(ArrayList<String> oldList){
        ArrayList<String> newList = new ArrayList<String>();
        for(int i=0; i<oldList.size(); i++){
            newList.add(oldList.get(i));
        }
        return newList;
    }

    public State Restart(){  
        ArrayList<String> newStart = new ArrayList<String>();
        State newState = new State(this.target, this.maxWeight, newStart, this.items);
        Random rand = new Random();
        int rand_num = rand.nextInt(items.size()+1);
        for(int i=0; i<rand_num; i++){
            int rand_numm = rand.nextInt(items.size());
            if(!newStart.contains(items.get(rand_numm).getName())){
                newStart.add(items.get(rand_numm).getName());
            }
        }
        newState.setStart(newStart);
        return newState;
    }

    public State RestartQ(){
        ArrayList<String> newStart = new ArrayList<String>();
        Random rand = new Random();
        while(newStart.size()<n){
            int rand_num = rand.nextInt(n);
            if(!newStart.contains(String.valueOf(rand_num))){
                newStart.add(String.valueOf(rand_num));
            }
        }
        State newState = new State(n, newStart);
        return newState;
    }

    public String toString(){
        return "start:"+start+"\nerror:"+error;
    }

}
