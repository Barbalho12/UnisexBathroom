package br.ufrn.imd.unisexbathroom;

import java.util.ArrayList;
import java.util.List;

import br.ufrn.imd.unisexbathroom.model.Banheiro;
import br.ufrn.imd.unisexbathroom.model.Homem;
import br.ufrn.imd.unisexbathroom.model.Mulher;
import br.ufrn.imd.unisexbathroom.model.Pessoa;
import br.ufrn.imd.unisexbathroom.util.Notes;
import br.ufrn.imd.unisexbathroom.util.RandInt;

public class Main {

	private static final int CAPACIDADE_PESSOAS_BANHEIRO = 3;
	
	public static void main(String args[]){
		
		int capacidade_banheiro = receberArg(args);
		
		Notes.print(new Main(), Mensagens.MAIN_PROGRAMA_INICIADO);
		
		Banheiro banheiro = new Banheiro(capacidade_banheiro); 
		
		RandInt rand = new RandInt(1, 2);
		
		List<Pessoa> pessoas = new ArrayList<>();
		
		for (int i = 0; i < 12; i++) {
			int randomico = rand.rand();

			if(randomico == 1){
				Homem homem = new Homem("H"+i, banheiro);
				pessoas.add(homem);
				Notes.print(new Main(), Mensagens.MAIN_CRIACAO, homem.getID());
			}else if(randomico == 2){
				Mulher mulher = new Mulher("M"+i, banheiro);
				pessoas.add(mulher);
				Notes.print(new Main(), Mensagens.MAIN_CRIACAO, mulher.getID());
			}
		}
		
		for(Pessoa p : pessoas){
			p.start();
		}
		
		for(Pessoa p : pessoas){
			try {
				p.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	public static int receberArg(String args[]) {
		int capacidade_banheiro = 0;
		
		if (args.length > 0) {
			try {
				capacidade_banheiro = Integer.parseInt(args[0]);
			} catch(Exception e) {
				e.printStackTrace();
			}
		} else  {
			capacidade_banheiro = CAPACIDADE_PESSOAS_BANHEIRO;
		}
		
		return capacidade_banheiro;
	}

}
