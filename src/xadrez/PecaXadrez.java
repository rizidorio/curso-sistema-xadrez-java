package xadrez;

import tabuleirojogo.Peca;
import tabuleirojogo.Posicao;
import tabuleirojogo.Tabuleiro;

public abstract class PecaXadrez extends Peca {
	
	private Cor cor;
	private int contadorMovimento;

	public PecaXadrez(Tabuleiro tabuleiro, Cor cor) {
		super(tabuleiro);
		this.cor = cor;
	}

	public Cor getCor() {
		return cor;
	}
	
	public int getContadorMovimento() {
		return contadorMovimento;
	}
	
	public void aumentarMovimento(){
		contadorMovimento++;
	}
	
	public void diminuirMovimento(){
		contadorMovimento--;
	}
	
	public XadrezPosicao getXadrezPosicao() {
		return XadrezPosicao.daPosicao(posicao);
	}
	
	protected boolean existePecaDoOponente(Posicao posicao) {
		PecaXadrez p = (PecaXadrez)getTabuleiro().peca(posicao);
		return p != null && p.getCor() != cor;
	}

}
