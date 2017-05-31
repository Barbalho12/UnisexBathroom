package br.ufrn.imd.unisexbathroom.model;

import br.ufrn.imd.unisexbathroom.Mensagens;
import br.ufrn.imd.unisexbathroom.util.Notes;

public abstract class Pessoa extends Thread{
	
	private String ID;
	private Banheiro banheiroREF;
	
	public Pessoa(String id, Banheiro banheiroREF){
		this.setID(id);
		this.setBanheiroREF(banheiroREF);
	}

	@Override
	public void run() {
		while(true){
			try {
				irAoBanheiro();
				tempoNoBanheiro();
				sairDoBanheiro();
				break;
			} catch (InterruptedException e) {
				e.printStackTrace();
				System.exit(0);
			}
		}
	}
	
	public void noficarEntrada(){
		Notes.print(this, Mensagens.PESSOA_ENTROU_BANHEIRO, toString());
	}
	
	public void noficarSaida(){
		Notes.print(this, Mensagens.PESSOA_SAIU_BANHEIRO, toString());
	}
	
	private void sairDoBanheiro() {
		getBanheiroREF().sair(this);
	}

	private void irAoBanheiro() {
		getBanheiroREF().tentarEntrar(this);	
	}

	protected abstract void tempoNoBanheiro() throws InterruptedException;

	public String getID() {
		return ID;
	}

	public void setID(String iD) {
		ID = iD;
	}

	protected Banheiro getBanheiroREF() {
		return banheiroREF;
	}

	protected void setBanheiroREF(Banheiro banheiroREF) {
		this.banheiroREF = banheiroREF;
	}

	@Override
	public String toString() {
		return "["+ID+"]";
	}
	
}
