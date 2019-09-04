package db.entity;

public class AAAEsempio {

    public int ID;
    public String TIMESTAMP = "0";
    public int VALORE;
    public String CLASSE;

    public AAAEsempio(){

    }

    public AAAEsempio(int ID, int VALORE, String CLASSE){
        this.ID = ID;
        this.VALORE = VALORE;
        this.CLASSE = CLASSE;
    }

    public AAAEsempio(int ID,String TIMESTAMP, int VALORE, String CLASSE){
        this.ID = ID;
        this.TIMESTAMP = TIMESTAMP;
        this.VALORE = VALORE;
        this.CLASSE = CLASSE;
    }

    public int getID(){
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public void setTIMESTAMP(String TIMESTAMP) {
        this.TIMESTAMP = TIMESTAMP;
    }

    public String getTIMESTAMP(){
        return TIMESTAMP;
    }

    public int getVALORE() {
        return VALORE;
    }

    public void setVALORE(int VALORE) {
        this.VALORE = VALORE;
    }

    public String getCLASSE() {
        return CLASSE;
    }

    public void setCLASSE(String CLASSE) {
        this.CLASSE = CLASSE;
    }

}
