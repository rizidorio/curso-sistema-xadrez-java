package application;

import java.util.InputMismatchException;
import java.util.Scanner;

import xadrez.PartidaXadrez;
import xadrez.PecaXadrez;
import xadrez.XadrezExcepition;
import xadrez.XadrezPosicao;

public class Programa {

	public static void main(String[] args) {
		
		Scanner sc = new Scanner(System.in);
		PartidaXadrez partida = new PartidaXadrez();
		
		while (true) {
			try {
				UI.limparTela();
				UI.imprimirPartida(partida);
				System.out.println();
				System.out.print("Origem: ");
				XadrezPosicao origem = UI.lerPosicao(sc);
				
				boolean[][] movimentosPossiveis = partida.movimentosPossiveis(origem);
				UI.limparTela();
				UI.imprimirTabuleiro(partida.getPecas(), movimentosPossiveis);
				System.out.println();
				System.out.print("Destino: ");
				XadrezPosicao destino = UI.lerPosicao(sc);
				
				PecaXadrez capturada = partida.perfomanceMovimento(origem, destino);
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
	}
}
