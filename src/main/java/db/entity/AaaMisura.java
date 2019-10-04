package db.entity;


public class AaaMisura {

  private long id;
  private java.sql.Date timestamp;
  private double valore;
  private long idclasse;
  private long idstrumento;


  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }


  public java.sql.Date getTimestamp() {
    return timestamp;
  }

  public void setTimestamp(java.sql.Date timestamp) {
    this.timestamp = timestamp;
  }


  public double getValore() {
    return valore;
  }

  public void setValore(double valore) {
    this.valore = valore;
  }


  public long getIdclasse() {
    return idclasse;
  }

  public void setIdclasse(long idclasse) {
    this.idclasse = idclasse;
  }


  public long getIdstrumento() {
    return idstrumento;
  }

  public void setIdstrumento(long idstrumento) {
    this.idstrumento = idstrumento;
  }


  public String toString(){
    return "ID: " + id +
            " - TIMESTAMP: " + timestamp +
            " - VALORE: " + valore +
            " - CLASSE: " + idclasse +
            " - STRUMENTO: " + idstrumento;
  }
}
