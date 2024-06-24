package br.com.alelvis.sistemLatency.model;

public class Host {
	private String nome;
	private String endereco;

	public Host(String nome, String endereco) {
		this.nome = nome;
		this.endereco = endereco;
	}

	public String getNome() {
		return nome;
	}

	public String getEndereco() {
		return endereco;
	}
}
