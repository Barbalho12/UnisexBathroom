package br.ufrn.imd.unisexbathroom.model;

import br.ufrn.imd.unisexbathroom.Mensagens;
import br.ufrn.imd.unisexbathroom.util.Notes;
import br.ufrn.imd.unisexbathroom.util.RandInt;

public class Mulher extends Pessoa{
	
	private final static int MAX_TEMPO_ESCRITORIO = 25;
	private final static int MIN_TEMPO_ESCRITORIO = 10;
	
	private final static int MAX_TEMPO_BANHEIRO = 10;
	private final static int MIN_TEMPO_BANHEIRO = 5;
	
	private RandInt randTempoEscritorio;
	private RandInt randTempoBanheiro;
	
	public Mulher(String id, Banheiro banheiroREF) {
		super(id, banheiroREF);
		this.randTempoEscritorio = new RandInt(MIN_TEMPO_ESCRITORIO, MAX_TEMPO_ESCRITORIO);
		this.randTempoBanheiro = new RandInt(MIN_TEMPO_BANHEIRO, MAX_TEMPO_BANHEIRO);
	}

	@Override
	protected boolean condicao() {
		return true;
	}

	@Override
	protected void sairDoBanheiro() {
		getBanheiroREF().sair(this);

	}

	@Override
	protected void irAoBanheiro() {
			getBanheiroREF().tentarEntrar(this);	
	}

	@Override
	protected void tempoNoEscritorio() throws InterruptedException {
		int tempo = randTempoEscritorio.rand();
		Notes.print(this, Mensagens.MULHER_NO_ESCRITORIO, toString(), tempo);
		Thread.sleep(tempo *1000);
	}

	@Override
	protected void tempoNoBanheiro() throws InterruptedException {
		int tempo = randTempoBanheiro.rand();
		Notes.print(this, Mensagens.MULHER_TEMPO_NO_BANHEIRO, toString(), tempo);
		Thread.sleep(tempo *1000);
		
	}

}
