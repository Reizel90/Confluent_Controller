package db.entity;


public class AaaStrumento {

  private long id;
  private String strumento;
  private double precisione;
  private String descrizione;


  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }


  public String getStrumento() {
    return strumento;
  }

  public void setStrumento(String strumento) {
    this.strumento = strumento;
  }


  public double getPrecisione() {
    return precisione;
  }

  public void setPrecisione(double precisione) {
    this.precisione = precisione;
  }


  public String getDescrizione() {
    return descrizione;
  }

  public void setDescrizione(String descrizione) {
    this.descrizione = descrizione;
  }

}
