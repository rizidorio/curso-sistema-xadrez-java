package application;

import java.util.Scanner;

import xadrez.PartidaXadrez;
import xadrez.PecaXadrez;
import xadrez.XadrezPosicao;

public class Programa {

	public static void main(String[] args) {
		
		Scanner sc = new Scanner(System.in);
		PartidaXadrez partida = new PartidaXadrez();
		
		while (true) {
		UI.imprimirTabuleiro(partida.getPecas());
		System.out.println();
		System.out.print("Origem: ");
		XadrezPosicao origem = UI.lerPosicao(sc);
		
		System.out.println();
		System.out.print("Destino: ");
		XadrezPosicao destino = UI.lerPosicao(sc);
		
		PecaXadrez capturada = partida.perfomanceMovimento(origem, destino);
		
		}
		
		
	}
		

}
