package algoritmo;

import java.awt.Point;
import java.util.ArrayList;

import controle.Constantes;

public class Poupador extends ProgramaPoupador {
	//VISAO POUPADOR
	static final Integer SEM_VISAO = -2;
	static final Integer FORA = -1;
	static final Integer VAZIO = 0;
	static final Integer PAREDE = 1; 
	static final Integer BANCO = 3;
	static final Integer MOEDA = 4;
	static final Integer PASTILHA_PODER = 5;
	static final Integer POUPADOR = 100;
	static final Integer LADRAO = 200;
	//PESOS
	static final Integer PESO_LADRAO = -1000;
	static final Integer PESO_FORA = -800;
	static final Integer PESO_PAREDE = -800;
	static final Integer PESO_PASTILHA_PODER = -300;
	static final Integer PESO_SEM_VISAO = -100;
	static final Integer PESO_VAZIO = 0;
	static final Integer PESO_BANCO = 1500;
	static final Integer PESO_MOEDA = 3000;
	//DIRECAO ACAO MATRIX
	static Integer CIMA = 7;
	static Integer ESQUERDA = 11;
	static Integer DIREITA = 12;
	static Integer BAIXO = 16;
	//OUTROS
	private ArrayList<Caminho> pontosVisitados = new ArrayList<>();
	private ArrayList<Caminho> caminhoPercorrido = new ArrayList<Caminho>();
	private int[] visao;
	private Point locBanco = Constantes.posicaoBanco;
	private int[] listaPesos = null;
	
	public int acao() {
		Integer acaoRetorno = null;
		visao = sensor.getVisaoIdentificacao();
		
		definirPesosVisao();
		definirCheiroLadrao();
		definirCheiroPoupador();
		acaoRetorno = determinarDirecao();
		atualizarPontosVisitados();
		atulizarCaminhoPercorrido();
		
		return acaoRetorno;
	}
	
	public void definirPesosVisao(){
		listaPesos = new int[visao.length];
		for(int i=0;i<visao.length; i++){
			int peso = 0;
			if(visao[i] == MOEDA){
				peso += PESO_MOEDA;
			}
			if(visao[i] == PASTILHA_PODER){
				peso += PESO_PASTILHA_PODER;
			}
			if(visao[i] == BANCO){
				if(sensor.getNumeroDeMoedas() > 0){
					peso += sensor.getNumeroDeMoedas() * PESO_BANCO;
				}else{
					peso += -(10*PESO_BANCO);
				}
			}
			if(visao[i] == PAREDE){
				peso += PESO_PAREDE;
			}
			if(visao[i] == FORA){
				peso += PESO_FORA;
			}
			if(visao[i] == SEM_VISAO){
				peso += PESO_SEM_VISAO;
			}
			if(visao[i] >= LADRAO){
				if(sensor.getNumeroDeMoedas() > 0){
					peso += (10*PESO_LADRAO);
				}else{
					peso += PESO_LADRAO;
				}
			}
			listaPesos[i] = peso;
		}
		System.out.println("*********** Pesos ***********");
		printMatriz(listaPesos);
	}
	
	public int determinarDirecao(){
		int retorno = 0;
		Integer dX, dY;
		
		dX = locBanco.x - sensor.getPosicao().x;
		dY = locBanco.y - sensor.getPosicao().y;
		if(dX > 0){
			listaPesos[DIREITA] += (sensor.getNumeroDeMoedas() * 100);
		}
		if(dX < 0){
			listaPesos[ESQUERDA] += (sensor.getNumeroDeMoedas() * 100);
		}
		if(dY > 0){
			listaPesos[BAIXO] += (sensor.getNumeroDeMoedas() * 100);
		}
		if(dY < 0){
			listaPesos[CIMA] += (sensor.getNumeroDeMoedas() * 100);
		}
		
		
		int pesoTotalBaixo    = (listaPesos[14] + listaPesos[15] + listaPesos[16] + listaPesos[17] + listaPesos[18] + listaPesos[19] + listaPesos[20] + listaPesos[21] + listaPesos[22] + listaPesos[23]);
		int pesoTotalEsquerda = (listaPesos[0]  + listaPesos[5]  + listaPesos[10] + listaPesos[14] + listaPesos[19] + listaPesos[1]  + listaPesos[6]  + listaPesos[11] + listaPesos[15] + listaPesos[20]);
		int pesoTotalDireita  = (listaPesos[3]  + listaPesos[8]  + listaPesos[12] + listaPesos[17] + listaPesos[22] + listaPesos[4]  + listaPesos[9]  + listaPesos[13] + listaPesos[18] + listaPesos[23]);
		int pesoTotalCima     = (listaPesos[0]  + listaPesos[1]  + listaPesos[2]  + listaPesos[3]  + listaPesos[4]  + listaPesos[5]  + listaPesos[6]  + listaPesos[7]  + listaPesos[8]  + listaPesos[9]);
		
		
		Point posicaoAtual = sensor.getPosicao();
		if(visao[CIMA]!=PAREDE && visao[CIMA] != FORA){
			pesoTotalCima     += trazerPesoPosicao(posicaoAtual.x,  posicaoAtual.y-1);
			pesoTotalCima     += trazerPesoCaminho(posicaoAtual.x,  posicaoAtual.y-1);
		}
		if(visao[BAIXO]!=PAREDE && visao[BAIXO] != FORA){
			pesoTotalBaixo    += trazerPesoPosicao(posicaoAtual.x,  posicaoAtual.y+1);
			pesoTotalBaixo    += trazerPesoCaminho(posicaoAtual.x,  posicaoAtual.y+1);
		}
		if(visao[DIREITA]!=PAREDE && visao[DIREITA] != FORA){
			pesoTotalDireita  += trazerPesoPosicao(posicaoAtual.x+1,posicaoAtual.y);
			pesoTotalDireita  += trazerPesoCaminho(posicaoAtual.x+1,posicaoAtual.y);
		}
		if(visao[ESQUERDA]!=PAREDE && visao[ESQUERDA] != FORA){
			pesoTotalEsquerda += trazerPesoPosicao(posicaoAtual.x-1,posicaoAtual.y);
			pesoTotalEsquerda += trazerPesoCaminho(posicaoAtual.x-1,posicaoAtual.y);
			
		}

		//Pastilha
		if(visao[CIMA]==PASTILHA_PODER && (sensor.getNumeroDeMoedas() < 10 || sensor.getNumeroJogadasImunes() > 0)){
			pesoTotalCima -= 10000;
		}
		if(visao[BAIXO]==PASTILHA_PODER  && (sensor.getNumeroDeMoedas() < 10 || sensor.getNumeroJogadasImunes() > 0)){
			pesoTotalBaixo -= 10000;
		}
		if(visao[DIREITA]==PASTILHA_PODER && (sensor.getNumeroDeMoedas() < 10 || sensor.getNumeroJogadasImunes() > 0)){
			pesoTotalDireita -= 10000;
		}
		if(visao[ESQUERDA]==PASTILHA_PODER && (sensor.getNumeroDeMoedas() < 10 || sensor.getNumeroJogadasImunes() > 0)){
			pesoTotalEsquerda -= 10000;
		}
		
		//Parede ou Fora
		if(visao[CIMA]==PAREDE || visao[CIMA] == FORA){
			pesoTotalCima -= 15000;
		}
		if(visao[BAIXO]==PAREDE || visao[BAIXO] == FORA){
			pesoTotalBaixo -= 15000;
		}
		if(visao[DIREITA]==PAREDE || visao[DIREITA] == FORA){
			pesoTotalDireita -= 15000;
		}
		if(visao[ESQUERDA]==PAREDE || visao[ESQUERDA] == FORA){
			pesoTotalEsquerda -= 15000;	
		}
		
		//Moeda
		if(visao[CIMA]==MOEDA){
			pesoTotalCima += 1000;
		}
		if(visao[BAIXO]==MOEDA){
			pesoTotalBaixo += 1000;
		}
		if(visao[DIREITA]==MOEDA){
			pesoTotalDireita += 1000;
		}
		if(visao[ESQUERDA]==MOEDA){
			pesoTotalEsquerda += 1000;
		}
		
		//Ladrao ou Popador
		if(visao[CIMA]>=LADRAO || visao[CIMA]>=POUPADOR){
			pesoTotalBaixo += 3000;
		}
		if(visao[BAIXO]>=LADRAO || visao[BAIXO]>=POUPADOR){
			pesoTotalCima += 3000;
		}
		if(visao[DIREITA]>=LADRAO || visao[DIREITA]>=POUPADOR){
			pesoTotalEsquerda += 3000;
		}
		if(visao[ESQUERDA]>=LADRAO || visao[ESQUERDA]>=POUPADOR){
			pesoTotalDireita += 3000;
		}
		
		//1 2 3 4
		int[] pesosDirecao = { pesoTotalCima, pesoTotalBaixo, pesoTotalDireita, pesoTotalEsquerda };
		Integer maior = -987654321;
		
		for (int i = 0; i < pesosDirecao.length; i++) {
			if (pesosDirecao[i] > maior) {
				maior = pesosDirecao[i];
				retorno = (i+1);
			}
		}
		//Verifica se existe mais de um peso com o mesmo valor
		ArrayList<Integer> pesosIguais = new ArrayList<Integer>();
		for (int i = 0; i < pesosDirecao.length; i++) {
			if (pesosDirecao[i] == maior) {
				pesosIguais.add(i+1);
			}
		}
		//Verifica se Existe mais de um peso na lista, se hover faz um randon
		if (pesosIguais.size() > 1) {
			Integer i = (int) (Math.random() * (pesosIguais.size()));
			retorno = pesosIguais.get(i);
		}
		
		System.out.println("Cima: "+pesoTotalCima + " # X: "+(posicaoAtual.x)+" Y: "+(posicaoAtual.y-1));
		System.out.println("Baixo: "+pesoTotalBaixo + " # X: "+(posicaoAtual.x)+" Y: "+(posicaoAtual.y+1));
		System.out.println("Direita: "+pesoTotalDireita + " # X: "+(posicaoAtual.x+1)+" Y: "+(posicaoAtual.y));
		System.out.println("Esquerda: "+pesoTotalEsquerda + " # X: "+(posicaoAtual.x-1)+" Y: "+(posicaoAtual.y));

		return retorno;
	}
	
	
	public void printMatriz(int m[]){
		System.out.println("\n["+m[0]+"]"+"["+m[1]+"]"+"["+m[2]+"]"+"["+m[3]+"]"+"["+m[4]+"]");
		System.out.println("["+m[5]+"]"+"["+m[6]+"]"+"["+m[7]+"]"+"["+m[8]+"]"+"["+m[9]+"]");
		System.out.println("["+m[10]+"]"+"["+m[11]+"]"+"[V]"+"["+m[12]+"]"+"["+m[13]+"]");
		System.out.println("["+m[14]+"]"+"["+m[15]+"]"+"["+m[16]+"]"+"["+m[17]+"]"+"["+m[18]+"]");
		System.out.println("["+m[19]+"]"+"["+m[20]+"]"+"["+m[21]+"]"+"["+m[22]+"]"+"["+m[23]+"]\n");
	}
	
	public void atualizarPontosVisitados(){
		Point posicaoAtual = sensor.getPosicao();
		boolean existe = false;
		for (int i = 0; i < pontosVisitados.size(); i++) {
			if(posicaoAtual.x == pontosVisitados.get(i).ponto.x && posicaoAtual.y == pontosVisitados.get(i).ponto.y){
				pontosVisitados.get(i).fator++;
				existe = true;
			}
		}
		if(!existe){
			pontosVisitados.add(new Caminho(posicaoAtual));
		}
	}
	
	public Integer trazerPesoPosicao(Integer x, Integer y){
			for(Caminho c : pontosVisitados){
				if(c.ponto.x == x && c.ponto.y == y){
					return -(c.fator*100);
				}
			}
		return 0;
	}
	
	public void atulizarCaminhoPercorrido(){
		Point posicaoAtual = sensor.getPosicao();
		if(caminhoPercorrido.size() < 40){
			caminhoPercorrido.add(new Caminho(posicaoAtual,-6000));
		}else{
			caminhoPercorrido.remove((0));
			caminhoPercorrido.add(new Caminho(posicaoAtual,-6000));
		}
	}
	
	public Integer trazerPesoCaminho(Integer x, Integer y){
		for(Caminho c : caminhoPercorrido){
			if(c.ponto.x == x && c.ponto.y == y){
				return c.fator;
			}
		}
		return 0;
	}
	
	public void definirCheiroLadrao(){
		int[] olfato = sensor.getAmbienteOlfatoLadrao();
		for(int i=0; i<olfato.length;i++){
			if(i == 0 ){
				listaPesos[6] += (olfato[i]/-1000);
			}
			if(i == 1 ){
				listaPesos[7] += (olfato[i]/-1000);
			}
			if(i == 2 ){
				listaPesos[8] += (olfato[i]/-1000);
			}
			if(i == 3 ){
				listaPesos[11] += (olfato[i]/-1000);
			}
			if(i == 4 ){
				listaPesos[12] += (olfato[i]/-1000);
			}
			if(i == 5 ){
				listaPesos[15] += (olfato[i]/-1000);
			}
			if(i == 6 ){
				listaPesos[16] += (olfato[i]/-1000);
			}
			if(i == 7 ){
				listaPesos[17] += (olfato[i]/-1000);
			}
		}
	}
	
	public void definirCheiroPoupador(){
		int[] olfato = sensor.getAmbienteOlfatoLadrao();
		for(int i=0; i<olfato.length;i++){
			if(i == 0 ){
				listaPesos[6] += (olfato[i]/-500);
			}
			if(i == 1 ){
				listaPesos[7] += (olfato[i]/-500);
			}
			if(i == 2 ){
				listaPesos[8] += (olfato[i]/-500);
			}
			if(i == 3 ){
				listaPesos[11] += (olfato[i]/-500);
			}
			if(i == 4 ){
				listaPesos[12] += (olfato[i]/-500);
			}
			if(i == 5 ){
				listaPesos[15] += (olfato[i]/-500);
			}
			if(i == 6 ){
				listaPesos[16] += (olfato[i]/-500);
			}
			if(i == 7 ){
				listaPesos[17] += (olfato[i]/-500);
			}
		}
	}
	
}

class Caminho{
	Point ponto;
	Integer fator;
	
	public Caminho(Point p){
		this.ponto = p;
		fator = 1;
	}
	
	public Caminho(Point p, Integer fator){
		this.ponto = p;
		this.fator = fator;
	}
	
 }