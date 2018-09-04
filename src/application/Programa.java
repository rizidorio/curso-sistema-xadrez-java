package application;

import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

import xadrez.PartidaXadrez;
import xadrez.PecaXadrez;
import xadrez.XadrezExcepition;
import xadrez.XadrezPosicao;

public class Programa {

	public static void main(String[] args) {
		
		Scanner sc = new Scanner(System.in);
		PartidaXadrez partida = new PartidaXadrez();
		List<PecaXadrez> capturadas = new ArrayList<>();
		
		while (!partida.getXequeMate()) {
			try {
				UI.limparTela();
				UI.imprimirPartida(partida, capturadas);
				System.out.println();
				System.out.print("Origem: ");
				XadrezPosicao origem = UI.lerPosicao(sc);
				
				boolean[][] movimentosPossiveis = partida.movimentosPossiveis(origem);
				UI.limparTela();
				UI.imprimirTabuleiro(partida.getPecas(), movimentosPossiveis);
				System.out.println();
				System.out.print("Destino: ");
				XadrezPosicao destino = UI.lerPosicao(sc);
				
				PecaXadrez pecaCapturada = partida.perfomanceMovimento(origem, destino);
				
				if(pecaCapturada != null) {
					capturadas.add(pecaCapturada);
				}
				
				if (partida.getPromocao() != null) {
					System.out.print("Entre com a peca da promocao (B/C/T/Q): ");
					String tipo = sc.nextLine();
					partida.trocarPecaPromovida(tipo);
				}
			}
			catch(XadrezExcepition e){
				System.out.println(e.getMessage());
				sc.nextLine();
			}
			catch(InputMismatchException e){
				System.out.println(e.getMessage());
				sc.nextLine();
			}
		}
		UI.limparTela();
		UI.imprimirPartida(partida, capturadas);
	}
}
