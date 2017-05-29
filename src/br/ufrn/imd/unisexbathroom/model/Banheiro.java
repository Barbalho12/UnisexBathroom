package br.ufrn.imd.unisexbathroom.model;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;

import br.ufrn.imd.unisexbathroom.Mensagens;
import br.ufrn.imd.unisexbathroom.util.Notes;

public class Banheiro {
	
	private int capacidade;
	private List<Pessoa> pessoas;
	private List<Pessoa> filaEspera;
	private Status status;
	private Semaphore entrando;
	private Semaphore esperandoAlguemSair;

	public Banheiro(int capacidade){
		this.capacidade = capacidade;
		this.status = Status.VAZIO;
		this.pessoas = new ArrayList<>();
		this.filaEspera = new ArrayList<>();

		esperandoAlguemSair = new Semaphore(0);
		entrando = new Semaphore(1);
		
	}
	
	public void tentarEntrar(Pessoa pessoa){

		try {

			entrando.acquire(); /////INICIO --- REGIÃO CRÍTICA DE ENTRADA NO BANEIRO
			
			/*Se banheiro está vazio*/
			if(status == Status.VAZIO){

				/*Define o status do banheiro de acordo com o gênero da pessoa*/
				definirStatus(pessoa);

				/*Pessoa entra no banheiro*/
				entrar(pessoa);
				
				entrando.release();  /////FIM --- REGIÃO CRÍTICA DE ENTRADA NO BANEIRO
				
			
			/*Se banheiro não está vazio*/
			}else if(status != Status.VAZIO){
				
				entrando.release();  /////FIM --- REGIÃO CRÍTICA (Não conseguiu entrar no banheiro vazio)
				
				/*Se a pessoa é homem e tem vaga no banheiro*/
				if(status == Status.MASCULINO && pessoa instanceof Homem && pessoas.size() < capacidade){
					
					entrando.acquire(); /////INICIO --- REGIÃO CRÍTICA DE ENTRADA NO BANEIRO

					entrar(pessoa);
					
					entrando.release(); /////FIM --- REGIÃO CRÍTICA DE ENTRADA NO BANEIRO

				
				/*Se a pessoa é uma mulher e tem vaga no banheiro*/
				}else if(status == Status.FEMININO && pessoa instanceof Mulher && pessoas.size() < capacidade){

					entrando.acquire(); /////INICIO --- REGIÃO CRÍTICA DE ENTRADA NO BANEIRO

					entrar(pessoa);
					
					entrando.release(); /////FIM --- REGIÃO CRÍTICA DE ENTRADA NO BANEIRO
				
				/*Se não, ou por sexo oposto, ou por falta de vaga, ela espera*/	
				}else{

					
					entrando.acquire();  /////INICIO --- REGIÃO CRÍTICA DE ENTRADA NO FILA DO BANEIRO
					
					entrarNaFila(pessoa); //Entra Fila de espera
					
					entrando.release(); /////FIM --- REGIÃO CRÍTICA DE ENTRADA NO FILA DO BANEIRO
					
					
					esperandoAlguemSair.acquire(); ////Pessoa fica esperado na fila de espera alguém do banheiro sair
					
					//Se uma pessoa sair, todos da fila são liberados (Espécie de barreira)

					//Após liberadas, elas voltam a tentar entrar no banheiro
					tentarEntrar(pessoa);
				}
				
				
			}

		} catch (InterruptedException e) {
			System.err.println("Erro durante a entrada de pessoa no banheiro");
			System.exit(0);
//			e.printStackTrace();
		}
		
		
	}


	private void definirStatus(Pessoa pessoa) {
		if(pessoa instanceof Homem){
			
			setStatus(Status.MASCULINO);
			
		}else if(pessoa instanceof Mulher){

			setStatus(Status.FEMININO);
			
		}
	}
	
	private void entrar(Pessoa pessoa){

		//Sai da fila de espera (se entrou nela)
		sairDaFilaDeEspera(pessoa);

		//Entra no baheiro
		pessoas.add(pessoa);
		pessoa.noficarEntrada();
		mostrarBanheiro();

	}
	
	private void entrarNaFila(Pessoa pessoa){
		if(!filaEspera.contains(pessoa)){
			filaEspera.add(pessoa);
			Notes.print(this, Mensagens.BANHEIRO_FILA_AUMENTOU, pessoa.getID(), filaEspera.toString());
		}
		
	}
	
	private void sairDaFilaDeEspera(Pessoa pessoa){
		if(!filaEspera.isEmpty() && filaEspera.contains(pessoa)){
			filaEspera.remove(pessoa);
			Notes.print(this, Mensagens.BANHEIRO_FILA_DIMINUIU, pessoa.getID(), filaEspera.toString());
		}
	}
	
	
	public void sair(Pessoa pessoa){
		try {
			
			
			entrando.acquire(); ////INICIO --- REGIÃO CRÍTICA: IMPEDE QUE ALGUEM ENTRE, ENQUANTO A PESSOA ESTÁ SAINDO
			
			/*Ocupante sai do banheiro*/
			pessoas.remove(pessoa);
			pessoa.noficarSaida();
			mostrarBanheiro();
			
			/*Se banehiro ficou vazio, Status é alterado*/
			if(pessoas.isEmpty()){
				setStatus(Status.VAZIO);
			}
			
			/*Libera pessoas que estavam esperando para tentar entrar novamente*/
			esperandoAlguemSair.release(esperandoAlguemSair.getQueueLength());
			
			entrando.release(); ////FIM --- REGIÃO CRÍTICA: PESSOA JÁ SAIU, ENTRADA LIBERADA!
			
		} catch (InterruptedException e) {
			System.err.println("Erro durante a saída de pessoa do banheiro");
			System.exit(0);
		}
	}
	
	public void mostrarBanheiro(){
		Notes.print(this, Mensagens.BANHEIRO_OCUPANTES, pessoas.toString());
	}
	

	public List<Pessoa> getPessoas() {
		return pessoas;
	}

	public void setPessoas(List<Pessoa> pessoas) {
		this.pessoas = pessoas;
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
