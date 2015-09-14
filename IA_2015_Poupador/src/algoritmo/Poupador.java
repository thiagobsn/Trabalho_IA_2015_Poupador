package algoritmo;

import java.awt.Point;
import java.util.ArrayList;

import controle.Constantes;

public class Poupador extends ProgramaPoupador {

	//VISAO POUPADOR
	static final Integer SEM_VISAO = -2;
	static final Integer FORA = -1;
	static final Integer VAZIO = 0;
	static final Integer PAREDE = Constantes.numeroParede; // 1
	static final Integer BANCO = 3;
	static final Integer MOEDA = Constantes.numeroMoeda; //4
	static final Integer PASTILHA_PODER = Constantes.numeroPastinhaPoder; //5
	static final Integer POUPADOR = 100;
	static final Integer LADRAO = 200;
	
	static final Integer PESO_SEM_VISAO = -30;
	static final Integer PESO_FORA = -20;
	static final Integer PESO_PAREDE = -10;
	static final Integer PESO_BANCO = 50;
	static final Integer PESO_VAZIO = 100;
	static final Integer PESO_MOEDA = 2500;
	static final Integer PESO_PASTILHA_PODER = 150;

	static final Integer PESO_BAIXO = 1;
	static final Integer PESO_ESQUERDA = 2;
	static final Integer PESO_DIREITA = 3;
	static final Integer PESO_CIMA = 4;
		
	static Integer CIMA = 7;
	static Integer ESQUERDA = 11;
	static Integer DIREITA = 12;
	static Integer BAIXO = 16;
	
	private ArrayList<Point> pontosVisitados = new ArrayList<Point>();
	private Point posicaoAtual = null;
	private int[] visao;
	private Point locBanco = Constantes.posicaoBanco;
	private int[] listaPesos = null;
	
	public int acao() {
		Integer acaoRetorno = null;
		
		posicaoAtual = sensor.getPosicao();
		pontosVisitados.add(posicaoAtual);
		visao = sensor.getVisaoIdentificacao();
		
		definirPesosVisao();
		acaoRetorno = determinarDirecao();
		
		return acaoRetorno;
	}
	
	public void definirPesosVisao(){
		listaPesos = new int[visao.length];
		for(int i=0;i<visao.length; i++){
			int peso = 0;
			if(visao[i] == MOEDA){
				peso = PESO_MOEDA;
			}
			if(visao[i] == PASTILHA_PODER){
				peso = PESO_PASTILHA_PODER;
			}
			if(visao[i] == BANCO){
				peso = PESO_BANCO;
			}
			if(visao[i] == PAREDE){
				peso = PESO_PAREDE;
			}
			if(visao[i] == VAZIO){
				peso = PESO_VAZIO;
			}
			if(visao[i] == FORA){
				peso = PESO_FORA;
			}
			if(visao[i] == SEM_VISAO){
				peso = PESO_SEM_VISAO;
			}
			listaPesos[i] = peso;
		}
		System.out.println("*********** Pesos ***********");
		printMatriz(listaPesos);
		
	}
	
	public int determinarDirecao(){
		int retorno = 0;
		int pesoTotalBaixo = (listaPesos[14] + listaPesos[15] + listaPesos[16] + listaPesos[17] + listaPesos[18] + listaPesos[19] + listaPesos[20] + listaPesos[21] + listaPesos[22] + listaPesos[23]);
		int pesoTotalEsquerda = (listaPesos[0] + listaPesos[5] + listaPesos[10] + listaPesos[14] + listaPesos[19] + listaPesos[1] + listaPesos[6] + listaPesos[11] + listaPesos[15] + listaPesos[20]);
		int pesoTotalDiretita = (listaPesos[3] + listaPesos[8] + listaPesos[12] + listaPesos[17] + listaPesos[22] + listaPesos[4] + listaPesos[9] + listaPesos[13] + listaPesos[18] + listaPesos[23]);
		int pesoTotalCima = (listaPesos[0] + listaPesos[1] + listaPesos[2] + listaPesos[3] + listaPesos[4] + listaPesos[5] + listaPesos[6] + listaPesos[7] + listaPesos[8] + listaPesos[9]);
		
		int maior = pesoTotalBaixo;
		retorno = MoverBaixo();
		
		if(maior < pesoTotalEsquerda){
			maior = pesoTotalEsquerda;
			retorno = MoverEsquerda();
		}
		if(maior < pesoTotalDiretita){
			maior = pesoTotalDiretita;
			retorno = MoverDireita();
		}
		if(maior < pesoTotalCima){
			retorno = MoverCima();
		}
		return retorno;
	}
	
	public Integer FicarParado(){
		return 0;
	}
	public Integer MoverCima(){
		return 1;
	}
	public Integer MoverBaixo(){
		return 2;
	}
	public Integer MoverDireita(){
		return 3;
	}
	public Integer MoverEsquerda(){
		return 4;
	}
	
	
	public void printMatriz(int m[]){
		System.out.println("\n["+m[0]+"]"+"["+m[1]+"]"+"["+m[2]+"]"+"["+m[3]+"]"+"["+m[4]+"]");
		System.out.println("["+m[5]+"]"+"["+m[6]+"]"+"["+m[7]+"]"+"["+m[8]+"]"+"["+m[9]+"]");
		System.out.println("["+m[10]+"]"+"["+m[11]+"]"+"[V]"+"["+m[12]+"]"+"["+m[13]+"]");
		System.out.println("["+m[14]+"]"+"["+m[15]+"]"+"["+m[16]+"]"+"["+m[17]+"]"+"["+m[18]+"]");
		System.out.println("["+m[19]+"]"+"["+m[20]+"]"+"["+m[21]+"]"+"["+m[22]+"]"+"["+m[23]+"]\n");
	}
	
}