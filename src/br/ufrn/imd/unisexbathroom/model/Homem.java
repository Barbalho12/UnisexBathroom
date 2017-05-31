package br.ufrn.imd.unisexbathroom.model;

import br.ufrn.imd.unisexbathroom.Mensagens;
import br.ufrn.imd.unisexbathroom.util.Notes;
import br.ufrn.imd.unisexbathroom.util.RandInt;

public class Homem extends Pessoa{
	
	private final static int MAX_TEMPO_BANHEIRO = 5;
	private final static int MIN_TEMPO_BANHEIRO = 2;
	
	private RandInt randTempoBanheiro;

	public Homem(String id, Banheiro banheiroREF) {
		super(id, banheiroREF);
		this.randTempoBanheiro = new RandInt(MIN_TEMPO_BANHEIRO, MAX_TEMPO_BANHEIRO);
	}

	@Override
	protected void tempoNoBanheiro() throws InterruptedException {
		int tempo = randTempoBanheiro.rand();
		Notes.print(this, Mensagens.HOMEM_TEMPO_NO_BANHEIRO, toString(), tempo);
		Thread.sleep(tempo *1000);
	}


}
