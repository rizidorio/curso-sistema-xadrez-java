package xadrez.peca;

import tabuleirojogo.Posicao;
import tabuleirojogo.Tabuleiro;
import xadrez.Cor;
import xadrez.PartidaXadrez;
import xadrez.PecaXadrez;

public class Peao extends PecaXadrez {
	
	private PartidaXadrez partidaXadrez;

	public Peao(Tabuleiro tabuleiro, Cor cor, PartidaXadrez partidaXadrez) {
		super(tabuleiro, cor);
		this.partidaXadrez = partidaXadrez;
	}

	@Override
	public boolean[][] movimentosPossiveis() {
		boolean[][] mat = new boolean[getTabuleiro().getLinhas()][getTabuleiro().getColunas()];
		
		Posicao p = new Posicao(0, 0);
		
		if (getCor() == Cor.BRANCO) {
			p.setValores(posicao.getLinha() - 1, posicao.getColuna());
			if (getTabuleiro().existePosicao(p) && !getTabuleiro().temPeca(p)) {
				mat[p.getLinha()][p.getColuna()] = true;
			}
			p.setValores(posicao.getLinha() - 2, posicao.getColuna());
			Posicao p2 = new Posicao(posicao.getLinha() - 1, posicao.getColuna());
			if (getTabuleiro().existePosicao(p) && !getTabuleiro().temPeca(p) && getTabuleiro().existePosicao(p2) && !getTabuleiro().temPeca(p2) && getContadorMovimento() == 0) {
				mat[p.getLinha()][p.getColuna()] = true;
			}
			p.setValores(posicao.getLinha() - 1, posicao.getColuna() - 1);
			if (getTabuleiro().existePosicao(p) && existePecaDoOponente(p)) {
				mat[p.getLinha()][p.getColuna()] = true;
			}
			p.setValores(posicao.getLinha() - 1, posicao.getColuna() + 1);
			if (getTabuleiro().existePosicao(p) && existePecaDoOponente(p)) {
				mat[p.getLinha()][p.getColuna()] = true;
			}
			
			// #movimento especial En Passant Branco
			if (posicao.getLinha() == 3) {
				Posicao esquerda = new Posicao(posicao.getLinha(), posicao.getColuna() - 1);
				if (getTabuleiro().existePosicao(esquerda) && existePecaDoOponente(esquerda) && getTabuleiro().peca(esquerda) == partidaXadrez.getEnPanssantVuneralvel()) {
					mat[esquerda.getLinha() - 1][esquerda.getColuna()] = true;
				}
				Posicao direita = new Posicao(posicao.getLinha(), posicao.getColuna() + 1);
				if (getTabuleiro().existePosicao(direita) && existePecaDoOponente(direita) && getTabuleiro().peca(direita) == partidaXadrez.getEnPanssantVuneralvel()) {
					mat[direita.getLinha() - 1][direita.getColuna()] = true;
				}
			}
		}
		else {
			p.setValores(posicao.getLinha() + 1, posicao.getColuna());
			if (getTabuleiro().existePosicao(p) && !getTabuleiro().temPeca(p)) {
				mat[p.getLinha()][p.getColuna()] = true;
			}
			p.setValores(posicao.getLinha() + 2, posicao.getColuna());
			Posicao p2 = new Posicao(posicao.getLinha() + 1, posicao.getColuna());
			if (getTabuleiro().existePosicao(p) && !getTabuleiro().temPeca(p) && getTabuleiro().existePosicao(p2) && !getTabuleiro().temPeca(p2) && getContadorMovimento() == 0) {
				mat[p.getLinha()][p.getColuna()] = true;
			}
			p.setValores(posicao.getLinha() + 1, posicao.getColuna() - 1);
			if (getTabuleiro().existePosicao(p) && existePecaDoOponente(p)) {
				mat[p.getLinha()][p.getColuna()] = true;
			}
			p.setValores(posicao.getLinha() + 1, posicao.getColuna() + 1);
			if (getTabuleiro().existePosicao(p) && existePecaDoOponente(p)) {
				mat[p.getLinha()][p.getColuna()] = true;
			}
			
			// #movimento especial En Passant Preto
			if (posicao.getLinha() == 4) {
				Posicao esquerda = new Posicao(posicao.getLinha(), posicao.getColuna() - 1);
				if (getTabuleiro().existePosicao(esquerda) && existePecaDoOponente(esquerda) && getTabuleiro().peca(esquerda) == partidaXadrez.getEnPanssantVuneralvel()) {
					mat[esquerda.getLinha() + 1][esquerda.getColuna()] = true;
				}
				Posicao direita = new Posicao(posicao.getLinha(), posicao.getColuna() + 1);
				if (getTabuleiro().existePosicao(direita) && existePecaDoOponente(direita) && getTabuleiro().peca(direita) == partidaXadrez.getEnPanssantVuneralvel()) {
					mat[direita.getLinha() + 1][direita.getColuna()] = true;
				}
			}
			
		}
		return mat;
	}
	
	@Override
	public String toString() {
		return "P";
	}

}
