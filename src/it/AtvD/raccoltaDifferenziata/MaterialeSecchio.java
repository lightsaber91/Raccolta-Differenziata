package it.AtvD.raccoltaDifferenziata;

public class MaterialeSecchio {

	private String Descrizione;
	private String Colore;

	public MaterialeSecchio(String Descrizione, String Colore) {
		this.Descrizione = Descrizione;
		this.Colore = Colore;
	}

	public String getDescrizione() {
		return Descrizione;
	}

	public void setDescrizione(String descrizione) {
		Descrizione = descrizione;
	}

	public String getColore() {
		return Colore;
	}

	public void setColore(String colore) {
		Colore = colore;
	}

}
