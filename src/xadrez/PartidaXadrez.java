package xadrez;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import tabuleirojogo.Peca;
import tabuleirojogo.Posicao;
import tabuleirojogo.Tabuleiro;
import xadrez.peca.Bispo;
import xadrez.peca.Cavalo;
import xadrez.peca.Peao;
import xadrez.peca.Rainha;
import xadrez.peca.Rei;
import xadrez.peca.Torre;

public class PartidaXadrez {
	
	private int turno;
	private Cor jogadorAtual;
	private Tabuleiro tabuleiro;
	private boolean xeque;
	private boolean xequeMate;
	private PecaXadrez enPassantVuneravel;
	private PecaXadrez promocao;
	
	private List<Peca> pecasNoTabuleiro = new ArrayList<>();
	private List<Peca> pecasCapturadas = new ArrayList<>();
	
	public PartidaXadrez() {
		tabuleiro = new Tabuleiro(8, 8);
		turno = 1;
		jogadorAtual = Cor.BRANCO;
		inicioSetup();
	}

	public int getTurno() {
		return turno;
	}
	
	public Cor getJogadorAtual() {
		return jogadorAtual;
	}
	
	public boolean getXeque() {
		return xeque;
	}
	
	public boolean getXequeMate() {
		return xequeMate;
	}
	
	public PecaXadrez getEnPanssantVuneralvel() {
		return enPassantVuneravel;
	}
	
	public PecaXadrez getPromocao() {
		return promocao;
	}
	
	public PecaXadrez[][] getPecas(){
		PecaXadrez[][] mat = new PecaXadrez[tabuleiro.getLinhas()][tabuleiro.getColunas()];
		for(int i=0; i<tabuleiro.getLinhas(); i++) {
			for(int j=0; j<tabuleiro.getColunas(); j++) {
				mat[i][j] = (PecaXadrez) tabuleiro.peca(i, j);
			}
		}
		return mat;
	}
	
	public boolean[][] movimentosPossiveis(XadrezPosicao origemPosicao){
		Posicao posicao = origemPosicao.toPosicao();
		validarPosicaoOrigem(posicao);
		return tabuleiro.peca(posicao).movimentosPossiveis();
	}
	
	public PecaXadrez perfomanceMovimento(XadrezPosicao posicaoOrigem, XadrezPosicao posicaoDestino) {
		Posicao origem = posicaoOrigem.toPosicao();
		Posicao destino = posicaoDestino.toPosicao();
		validarPosicaoOrigem(origem);
		validarPosicaoDestino(origem, destino);
		Peca pecaCapturada = realizarMovimento(origem, destino);
		
		if(testeXeque(jogadorAtual)) {
			desfazerMovimento(origem, destino, pecaCapturada);
			throw new XadrezExcepition("Voce nao se pode colocar em xeque!");
		}
		
		PecaXadrez pecaMovida = (PecaXadrez)tabuleiro.peca(destino);
		
		// #movimentoespecial promocao
		promocao = null;
		if (pecaMovida instanceof Peao) {
			if((pecaMovida.getCor() == Cor.BRANCO && destino.getLinha() == 0)|| (pecaMovida.getCor() == Cor.PRETO && destino.getLinha() == 7)) {
				promocao = (PecaXadrez)tabuleiro.peca(destino);
				promocao = trocarPecaPromovida("Q");
			}
			
		}
		
		xeque = (testeXeque(oponente(jogadorAtual))) ? true : false;
		
		if (testeXequeMate(oponente(jogadorAtual))) {
			xequeMate = true;
		}
		else {
			proximoTurno();
		}
		
		// #movimentoespecial En Passant
		if (pecaMovida instanceof Peao && (destino.getLinha() == origem.getLinha() - 2 || destino.getLinha() == origem.getLinha() + 2)) {
			enPassantVuneravel = pecaMovida;
		}
		else {
			enPassantVuneravel = null;
		}
		
		return (PecaXadrez) pecaCapturada;
	}
	
	public PecaXadrez trocarPecaPromovida(String tipo) {
		if (promocao == null) {
			throw new IllegalStateException("Essa peca nao pode ser promovida");
		}
		if (!tipo.equals("B") && !tipo.equals("C") && !tipo.equals("T") && !tipo.equals("Q")) {
			throw new InvalidParameterException("Tipo invalido para promocao");
		}
		
		Posicao pos = promocao.getXadrezPosicao().toPosicao();
		Peca p = tabuleiro.removerPeca(pos);
		pecasNoTabuleiro.remove(p);
		
		PecaXadrez novaPeca = novaPeca(tipo, promocao.getCor());
		tabuleiro.colocaPeca(novaPeca, pos);
		pecasNoTabuleiro.add(novaPeca);
		
		return novaPeca;
	}
	
	private PecaXadrez novaPeca (String tipo, Cor cor) {
		if (tipo.equals("B")) return new Bispo(tabuleiro, cor);
		if (tipo.equals("C")) return new Cavalo(tabuleiro, cor);
		if (tipo.equals("Q")) return new Rainha(tabuleiro, cor);
		return new Torre(tabuleiro, cor);
	}
	
	private Peca realizarMovimento(Posicao origem, Posicao destino) {
		PecaXadrez p = (PecaXadrez)tabuleiro.removerPeca(origem);
		p.aumentarMovimento();
		Peca pecaCapturada = tabuleiro.removerPeca(destino);
		tabuleiro.colocaPeca(p, destino);
		if(pecaCapturada != null) {
			pecasNoTabuleiro.remove(pecaCapturada);
			pecasCapturadas.add(pecaCapturada);
		}
		
		// #movimento especial roque pequeno
		if(p instanceof Rei && destino.getColuna() == origem.getColuna() + 2) {
			Posicao origemT = new Posicao(origem.getLinha(), origem.getColuna() + 3);
			Posicao destinoT = new Posicao(origem.getLinha(), origem.getColuna() + 1);
			PecaXadrez torre = (PecaXadrez)tabuleiro.removerPeca(origemT);
			tabuleiro.colocaPeca(torre, destinoT);
			torre.aumentarMovimento();
		}
		
		// #movimento especial roque grande
		if(p instanceof Rei && destino.getColuna() == origem.getColuna() - 2) {
			Posicao origemT = new Posicao(origem.getLinha(), origem.getColuna() - 4);
			Posicao destinoT = new Posicao(origem.getLinha(), origem.getColuna() - 1);
			PecaXadrez torre = (PecaXadrez)tabuleiro.removerPeca(origemT);
			tabuleiro.colocaPeca(torre, destinoT);
			torre.aumentarMovimento();
		}
		
		// #movimentospecial En Passant
		if (p instanceof Peao) {
			if(origem.getColuna() != destino.getColuna() && pecaCapturada == null) {
				Posicao posicaoPeao;
				if(p.getCor() == Cor.BRANCO) {
					posicaoPeao = new Posicao(destino.getLinha() + 1, destino.getColuna());
				}
				else {
					posicaoPeao = new Posicao(destino.getLinha() - 1, destino.getColuna());
				}
				pecaCapturada = tabuleiro.removerPeca(posicaoPeao);
				pecasCapturadas.add(pecaCapturada);
				pecasNoTabuleiro.remove(pecaCapturada);
			}
		}
		return pecaCapturada;
	}
	
	private void desfazerMovimento(Posicao origem, Posicao destino, Peca pecaCapturada) {
		PecaXadrez p = (PecaXadrez)tabuleiro.removerPeca(destino);
		p.diminuirMovimento();
		tabuleiro.colocaPeca(p, origem);
		
		if (pecaCapturada != null) {
			tabuleiro.colocaPeca(pecaCapturada, destino);
			pecasCapturadas.remove(pecaCapturada);
			pecasNoTabuleiro.add(pecaCapturada);
		}
		
		// #movimento especial roque pequeno
		if(p instanceof Rei && destino.getColuna() == origem.getColuna() + 2) {
			Posicao origemT = new Posicao(origem.getLinha(), origem.getColuna() + 3);
			Posicao destinoT = new Posicao(origem.getLinha(), origem.getColuna() + 1);
			PecaXadrez torre = (PecaXadrez)tabuleiro.removerPeca(destinoT);
			tabuleiro.colocaPeca(torre, origemT);
			torre.diminuirMovimento();
		}
		
		// #movimento especial roque grande
		if(p instanceof Rei && destino.getColuna() == origem.getColuna() - 2) {
			Posicao origemT = new Posicao(origem.getLinha(), origem.getColuna() - 4);
			Posicao destinoT = new Posicao(origem.getLinha(), origem.getColuna() - 1);
			PecaXadrez torre = (PecaXadrez)tabuleiro.removerPeca(destinoT);
			tabuleiro.colocaPeca(torre, origemT);
			torre.diminuirMovimento();
		}	
	
		// #movimentospecial En Passant
		if (p instanceof Peao) {
			if(origem.getColuna() != destino.getColuna() && pecaCapturada == enPassantVuneravel) {
				PecaXadrez peao = (PecaXadrez)tabuleiro.removerPeca(destino);
				Posicao posicaoPeao;
				if(p.getCor() == Cor.BRANCO) {
					posicaoPeao = new Posicao(3, destino.getColuna());
				}
				else {
					posicaoPeao = new Posicao(4, destino.getColuna());
				}
				tabuleiro.colocaPeca(peao, posicaoPeao);
			}
		}
	}

	private void validarPosicaoOrigem(Posicao posicao) {
		if(!tabuleiro.temPeca(posicao)){
			throw new XadrezExcepition("Nao existe peca na posicao de origem!");
		}
		if(jogadorAtual != ((PecaXadrez)tabuleiro.peca(posicao)).getCor()) {
			throw new XadrezExcepition("Essa peca nao e sua!");
		}
		if (!tabuleiro.peca(posicao).existeAlgumMovimentoPossivel()) {
			throw new XadrezExcepition("Nao existe movimento possivel para a peca selecionada");
		}
	}
	
	private void validarPosicaoDestino(Posicao origem, Posicao destino) {
		if (!tabuleiro.peca(origem).movivemtosPossiveis(destino)) {
			throw new XadrezExcepition("Nao e possivel realizar o movimento para a posicao de destino");
		}
	}
	
	
	
	private void proximoTurno() {
		turno++;
		jogadorAtual = (jogadorAtual == Cor.BRANCO) ? Cor.PRETO : Cor.BRANCO;
	}
	
	private Cor oponente(Cor cor) {
		return (cor == Cor.BRANCO) ? cor.PRETO : cor.BRANCO;
	}
	
	private PecaXadrez rei(Cor cor) {
		List<Peca> list = pecasNoTabuleiro.stream().filter(x -> ((PecaXadrez)x).getCor() == cor).collect(Collectors.toList());
		for(Peca p : list) {
			if (p instanceof Rei) {
				return (PecaXadrez)p;
			}
		}
		throw new IllegalStateException("Não exite nas pecas " + cor + "Rei no tabuleiro");
	}
	
	private boolean testeXeque(Cor cor) {
		Posicao reiPosicao = rei(cor).getXadrezPosicao().toPosicao();
		List<Peca> pecasOponente = pecasNoTabuleiro.stream().filter(x -> ((PecaXadrez)x).getCor() == oponente(cor)).collect(Collectors.toList());
		for(Peca p : pecasOponente) {
			boolean[][] mat = p.movimentosPossiveis();
			if(mat[reiPosicao.getLinha()][reiPosicao.getColuna()]) {
				return true;
			}
		}
		
		return false;
	}
	
	private boolean testeXequeMate(Cor cor) {
		if (!testeXeque(cor)) {
			return false;
		}
		List<Peca> list = pecasNoTabuleiro.stream().filter(x -> ((PecaXadrez)x).getCor() == cor).collect(Collectors.toList());
		for (Peca p : list) {
			boolean[][] mat = p.movimentosPossiveis();
			for(int i = 0; i < tabuleiro.getLinhas(); i++) {
				for(int j = 0; j < tabuleiro.getColunas(); j++) {
					if (mat[i][j]) {
						Posicao origem = ((PecaXadrez)p).getXadrezPosicao().toPosicao();
						Posicao destino = new Posicao(i, j);
						Peca pecaCapturada = realizarMovimento(origem, destino);
						boolean testeXeque = testeXeque(cor);
						desfazerMovimento(origem, destino, pecaCapturada);
						if(!testeXeque) {
							return false;
						}
					}
				}
			}
		}
		return true;
	}
	
 	private void colocarNovaPeca(char coluna, int linha, PecaXadrez peca) {
		tabuleiro.colocaPeca(peca, new XadrezPosicao(coluna, linha).toPosicao());
		pecasNoTabuleiro.add(peca);
	}
	
	private void inicioSetup() {
		colocarNovaPeca('a', 1,  new Torre(tabuleiro, Cor.BRANCO));
		colocarNovaPeca('b', 1,  new Cavalo(tabuleiro, Cor.BRANCO));
		colocarNovaPeca('c', 1,  new Bispo(tabuleiro, Cor.BRANCO));
		colocarNovaPeca('d', 1,  new Rainha(tabuleiro, Cor.BRANCO));
		colocarNovaPeca('e', 1,  new Rei(tabuleiro, Cor.BRANCO, this));
		colocarNovaPeca('f', 1,  new Bispo(tabuleiro, Cor.BRANCO));
		colocarNovaPeca('g', 1,  new Cavalo(tabuleiro, Cor.BRANCO));
		colocarNovaPeca('h', 1,  new Torre(tabuleiro, Cor.BRANCO));
		colocarNovaPeca('a', 2,  new Peao(tabuleiro, Cor.BRANCO, this));
		colocarNovaPeca('b', 2,  new Peao(tabuleiro, Cor.BRANCO, this));
		colocarNovaPeca('c', 2,  new Peao(tabuleiro, Cor.BRANCO, this));
		colocarNovaPeca('d', 2,  new Peao(tabuleiro, Cor.BRANCO, this));
		colocarNovaPeca('e', 2,  new Peao(tabuleiro, Cor.BRANCO, this));
		colocarNovaPeca('f', 2,  new Peao(tabuleiro, Cor.BRANCO, this));
		colocarNovaPeca('g', 2,  new Peao(tabuleiro, Cor.BRANCO, this));
		colocarNovaPeca('h', 2,  new Peao(tabuleiro, Cor.BRANCO, this));
				
		colocarNovaPeca('a', 8, new Torre(tabuleiro, Cor.PRETO));
		colocarNovaPeca('b', 8, new Cavalo(tabuleiro, Cor.PRETO));
		colocarNovaPeca('c', 8, new Bispo(tabuleiro, Cor.PRETO));
		colocarNovaPeca('d', 8, new Rainha(tabuleiro, Cor.PRETO));
		colocarNovaPeca('e', 8, new Rei(tabuleiro, Cor.PRETO, this));
		colocarNovaPeca('f', 8, new Bispo(tabuleiro, Cor.PRETO));
		colocarNovaPeca('g', 8, new Cavalo(tabuleiro, Cor.PRETO));
		colocarNovaPeca('h', 8, new Torre(tabuleiro, Cor.PRETO));
		colocarNovaPeca('a', 7, new Peao(tabuleiro, Cor.PRETO, this));
		colocarNovaPeca('b', 7, new Peao(tabuleiro, Cor.PRETO, this));
		colocarNovaPeca('c', 7, new Peao(tabuleiro, Cor.PRETO, this));
		colocarNovaPeca('d', 7, new Peao(tabuleiro, Cor.PRETO, this));
		colocarNovaPeca('e', 7, new Peao(tabuleiro, Cor.PRETO, this));
		colocarNovaPeca('f', 7, new Peao(tabuleiro, Cor.PRETO, this));
		colocarNovaPeca('g', 7, new Peao(tabuleiro, Cor.PRETO, this));
		colocarNovaPeca('h', 7, new Peao(tabuleiro, Cor.PRETO, this));
	}
}
