package it.AtvD.raccoltaDifferenziata;

public class DbUtility {

	public static final String DB_NAME = "raccolta.db";

	// nomi tabelle
	public static final String TABLE_COMUNE = "comune";
	public static final String TABLE_MATERIALE = "materiali";
	public static final String TABLE_PRODOTTO = "prodotto";
	public static final String TABLE_RACCOLTA = "raccolta";
	public static final String TABLE_RACCOLTAMATERIALI = "raccoltamateriali";
	public static final String TABLE_MATERIALIPRODOTTI = "materialiprodotti";
	public static final String TABLE_COLORESECCHIO = "coloresecchio";

	// nomi colonne
	public static final String ID = "_id";
	public static final String ID_COMUNE = "_idcomune";
	public static final String ID_MATERIALE = "_idmateriale";
	public static final String ID_PRODOTTO = "_idprodotto";
	public static final String ID_RACCOLTA = "_idraccolta";
	public static final String EAN = "EAN";
	public static final String COLORE = "colore";
	public static final String DESCRIZIONE = "Descrizione";

	public static String getColName(String tabella, String colonna) {
		return tabella + "." + colonna;
	}

	public static String getCol(String tabella, String colonna) {
		return tabella + ".[" + colonna + "]";
	}

}
