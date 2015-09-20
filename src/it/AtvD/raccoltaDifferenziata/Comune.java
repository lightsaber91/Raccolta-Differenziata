package it.AtvD.raccoltaDifferenziata;

public class Comune {

	String nome;
	int id;

	public Comune(String nome, int id) {
		this.nome = nome;
		this.id = id;
	}

	public Comune() {

	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

}
