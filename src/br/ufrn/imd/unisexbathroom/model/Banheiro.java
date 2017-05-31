package br.ufrn.imd.unisexbathroom.model;

import java.util.ArrayList;
import java.util.List;

import br.ufrn.imd.unisexbathroom.Mensagens;
import br.ufrn.imd.unisexbathroom.util.Notes;

public class Banheiro {
	
	private int capacidade;
	private List<Pessoa> ocupantes;
	private List<Pessoa> listaDeEspera;
	private Status status;
	private Barreira barreira;

	public Banheiro(int capacidade){
		this.capacidade = capacidade;
		this.status = Status.VAZIO;
		this.ocupantes = new ArrayList<>();
		this.listaDeEspera = new ArrayList<>();
		
		barreira = new Barreira();
	}

	/**
	 * Pessoa tenta entrar, ou fica esperando até que alguem saia para tentar entrar novamente (até conseguir). 
	 * @param pessoa homem ou mulher que ocupará o banheiro
	 */
	public void tentarEntrar(Pessoa pessoa){
		try {
			
			/*Se pessoa não conseguir acessar o banheiro*/
			if( !acessarBanheiro(pessoa) ){
				
				/*Fica esperando, até que alguem saia*/
				esperar(pessoa);
				
				/*Quando alguem sair, tenta entrar novamente*/
				tentarEntrar(pessoa);
			}

		} catch (InterruptedException e) {
			System.err.println("Erro durante a entrada de pessoa no banheiro");
			System.exit(0);
		}
	}
	
	/**
	 * A pessoa sai do banheiro
	 * @param pessoa homem ou mulher que ocupará o banheiro
	 */
	public void sair(Pessoa pessoa){
		atualizarOcupantes(pessoa, Sentido.SAIR);
	}
	

	/**
	 * A pessoa tenta acessar o banehiro (Uma pessoa por vez)
	 * @param pessoa homem ou mulher que ocupará o banheiro
	 * @return 
	 * @throws InterruptedException erro durante a entrada
	 */
	private synchronized boolean acessarBanheiro(Pessoa pessoa) throws InterruptedException{
		if(status == Status.VAZIO || (ocupantes.size() < capacidade && validarStatus(pessoa))){
			atualizarOcupantes(pessoa, Sentido.ENTRAR);
			return true;
		}
		
		return false;
	}
	
	/**
	 * A lista de espera é atualizada (com exclusividade) e a pessoa fica esperando até que alguém saia
	 * @param pessoa homem ou mulher que ocupará o banheiro
	 * @throws InterruptedException erro durante a entrada
	 */
	private void esperar(Pessoa pessoa) throws InterruptedException{
		atualizarListaEspera(pessoa, Sentido.ENTRAR); 		
		barreira.chegada(false);
	}
	
	/**
	 * A lista de ocupantes do banheiro é atualizada, pelo sentido, sair ou entrar. 
	 * @param pessoa homem ou mulher que ocupará o banheiro
	 * @param sentido entrada ou saída de pessoa
	 */
	private synchronized void atualizarOcupantes(Pessoa pessoa, Sentido sentido){
		
		if(sentido == Sentido.ENTRAR){
			/*Caso o banheiro esteja vazio, passa a ter um novo status de gênero*/
			if(status == Status.VAZIO){
				definirStatus(pessoa);
			}

			/*Sai da fila de espera (se entrou nela)*/
			atualizarListaEspera(pessoa, Sentido.SAIR);
			
			/*Entra no banheiro*/
			ocupantes.add(pessoa);
			pessoa.noficarEntrada();
			Notes.print(this, Mensagens.BANHEIRO_OCUPANTES, ocupantes.toString());
			
		}else if(sentido == Sentido.SAIR){
			
			/*Ocupante sai do banheiro*/
			ocupantes.remove(pessoa);
			pessoa.noficarSaida();
			Notes.print(this, Mensagens.BANHEIRO_OCUPANTES, ocupantes.toString());
			
			/*Se banheiro ficou vazio, Status é alterado*/
			if(ocupantes.isEmpty()){
				setStatus(Status.VAZIO);
			}
			
			/*Libera pessoas que estavam esperando para tentar entrar novamente*/
			barreira.chegada(true);
		}
		
	}
	
	/**
	 * Atualiza lista de espera do banheiro, pelo sentido, sair ou entrar. 
	 * @param pessoa homem ou mulher que ocupará o banheiro
	 * @param sentido entrada ou saída de pessoa
	 */
	private synchronized void atualizarListaEspera(Pessoa pessoa, Sentido sentido){
		if(sentido == Sentido.ENTRAR && !listaDeEspera.contains(pessoa)){
			listaDeEspera.add(pessoa);
			Notes.print(this, Mensagens.BANHEIRO_FILA_AUMENTOU, pessoa.toString(), listaDeEspera.toString());
		}else if(sentido == Sentido.SAIR && listaDeEspera.contains(pessoa)){
			listaDeEspera.remove(pessoa);
			Notes.print(this, Mensagens.BANHEIRO_FILA_DIMINUIU, pessoa.toString(), listaDeEspera.toString());
		}
	}

	/**
	 * Verifica se o estado da banheiro confere com o sexo da pessoa
	 * @param pessoa homem ou mulher que ocupará o banheiro
	 * @return verdadeiro se conferir, falso caso contrário
	 */
	private boolean validarStatus(Pessoa pessoa){
		if(status == Status.MASCULINO && pessoa instanceof Homem){
			return true;
		}else if(status == Status.FEMININO && pessoa instanceof Mulher){
			return true;
		}else{
			return false;
		}
	}
	
	/**
	 * Define o status do banheiro pelo gênero da pessoa
	 * @param pessoa homem ou mulher que ocupará o banheiro
	 */
	private void definirStatus(Pessoa pessoa) {
		if(pessoa instanceof Homem){
			setStatus(Status.MASCULINO);
		}else if(pessoa instanceof Mulher){
			setStatus(Status.FEMININO);
		}
	}


	public List<Pessoa> getPessoas() {
		return ocupantes;
	}

	public void setPessoas(List<Pessoa> pessoas) {
		this.ocupantes = pessoas;
	}
	
	public int getCapacidade() {
		return capacidade;
	}

	public void setCapacidade(int capacidade) {
		this.capacidade = capacidade;
	}
	
	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

}
