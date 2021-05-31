package Hello.business;

public class Operator {
    private String str = "";
    public void append(String str) {
        if (this.str.isEmpty()) {
            this.str = this.str.concat(str);
        }else{
            this.str = this.str.concat(" -|- " + str);
        }
    }
    public String getStr(){
        return str;
    }
}
