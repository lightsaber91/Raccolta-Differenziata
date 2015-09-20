package it.AtvD.raccoltaDifferenziata;

public class ProdottoMateriale {

	private String Descrizione;
	private String Materiale;

	public ProdottoMateriale(String Materiale, String Descrizione) {
		this.Descrizione = Descrizione;
		this.Materiale = Materiale;
	}

	public String getDescrizione() {
		return Descrizione;
	}

	public void setDescrizione(String descrizione) {
		Descrizione = descrizione;
	}

	public String getMateriale() {
		return Materiale;
	}

	public void setMateriale(String materiale) {
		Materiale = materiale;
	}

}
