package db.entity;


public class Zone {

  private long id;
  private String codice;
  private String descr;
  private long iddivisioni;
  private long idrepcdc;
  private long idrepcdcCosto;


  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }


  public String getCodice() {
    return codice;
  }

  public void setCodice(String codice) {
    this.codice = codice;
  }


  public String getDescr() {
    return descr;
  }

  public void setDescr(String descr) {
    this.descr = descr;
  }


  public long getIddivisioni() {
    return iddivisioni;
  }

  public void setIddivisioni(long iddivisioni) {
    this.iddivisioni = iddivisioni;
  }


  public long getIdrepcdc() {
    return idrepcdc;
  }

  public void setIdrepcdc(long idrepcdc) {
    this.idrepcdc = idrepcdc;
  }


  public long getIdrepcdcCosto() {
    return idrepcdcCosto;
  }

  public void setIdrepcdcCosto(long idrepcdcCosto) {
    this.idrepcdcCosto = idrepcdcCosto;
  }

}
