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
	private Semaphore semaphore;
	private Semaphore entrando;
	private Semaphore esperandoEsvaziar;


	public Banheiro(int capacidade){
		this.capacidade = capacidade;
		this.status = Status.VAZIO;
		this.pessoas = new ArrayList<>();
		this.filaEspera = new ArrayList<>();

		semaphore = new Semaphore(capacidade);
		esperandoEsvaziar = new Semaphore(0);
		entrando = new Semaphore(1);
		
	}
	
	public void tentarEntrar(Pessoa pessoa){

		try {
			
			entrando.acquire();
			
			if(status == Status.VAZIO){
				
				if(pessoa instanceof Homem){
					
					setStatus(Status.MASCULINO);
					
				}else if(pessoa instanceof Mulher){

					setStatus(Status.FEMININO);
					
				}

				entrando.release();
				
				entrar(pessoa);


			}else{
				
				entrando.release();
				
				if(status == Status.MASCULINO && pessoa instanceof Homem && semaphore.availablePermits() > 0){

					entrar(pessoa);

				}else if(status == Status.FEMININO && pessoa instanceof Mulher && semaphore.availablePermits() > 0){

					entrar(pessoa);
					
				}else{

					entrando.acquire();
					
					entrarNaFila(pessoa);
					
					entrando.release();
					
					esperandoEsvaziar.acquire();

					tentarEntrar(pessoa);
				}
				
				
			}

		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		
	}
	
	private void entrar(Pessoa pessoa){
		try {
			semaphore.acquire();
			
			entrando.acquire();
	
			pessoas.add(pessoa);
			pessoa.noficarEntrada();
			mostrarBanheiro();
			
			
			sairDaFila(pessoa);
			
			entrando.release();
			
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	private void entrarNaFila(Pessoa pessoa){
		if(!filaEspera.contains(pessoa)){
			filaEspera.add(pessoa);
			Notes.print(this, Mensagens.BANHEIRO_FILA_AUMENTOU, pessoa.getID(), filaEspera.toString());
		}
		
	}
	
	private void sairDaFila(Pessoa pessoa){
		if(!filaEspera.isEmpty() && filaEspera.contains(pessoa)){
			filaEspera.remove(pessoa);
			Notes.print(this, Mensagens.BANHEIRO_FILA_DIMINUIU, pessoa.getID(), filaEspera.toString());
		}
	}
	
	
	public void sair(Pessoa pessoa){
		try {
			
			
			entrando.acquire();
			
			pessoas.remove(pessoa);
			
			if(pessoas.isEmpty()){
				setStatus(Status.VAZIO);
				esperandoEsvaziar.release(esperandoEsvaziar.getQueueLength());
			}
			
			semaphore.release();
			
			pessoa.noficarSaida();
			mostrarBanheiro();
			
			entrando.release();
			
		} catch (InterruptedException e) {
			e.printStackTrace();
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
