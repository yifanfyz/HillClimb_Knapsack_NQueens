
public class Items {

    String name;
    long value;
    long weight;

    public Items(String name, long value, long weight){
        this.name = name;
        this.value = value;
        this.weight = weight;
    }

    public String getName(){
        return name;
    }

    public long getValue(){
        return value;
    }

    public String toString(){
        return "name:"+name+", value:"+value+", weight:"+weight+"\n";
    }
}
