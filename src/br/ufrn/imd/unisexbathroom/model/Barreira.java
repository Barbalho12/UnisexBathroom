package br.ufrn.imd.unisexbathroom.model;

public class Barreira {
	
	public Barreira() {

	}
	
	public synchronized void chegada(boolean liberar) {
		if( !liberar ) {
			try {
				this.wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		} else {
			this.notifyAll();
		}
	} 

}
