package conexidadecaminhamento;

import java.util.*;

import static javafx.scene.input.KeyCode.I;

public class ConexidadeCaminhamento {

    public static void main(String[] args) {
        //INPUT do usuário questão 1
        int numeroVertices = 7;
        char origem = 'F';
        char destino = 'A';
        Object[][] matriz = {
            {0, I, I, I, I, I, I},
            {I, 0, 1, I, I, I, I},
            {4, I, 0, I, I, I, I},
            {I, 3, I, 0, 1, I, I},
            {I, I, 2, I, 0, I, I},
            {I, I, I, 3, I, 0, 2},
            {I, I, I, I, 5, I, 0}
        };
        //INPUT do usuário questão 2
        int quantidadeDeGrafos = 1;
        int quantidadeDeVertices = 10;
        int quantidadeDeArestas = 15;
        String conjuntos = "ACBAC BC FD AD CECE DFBFGF HH GH II JJ H";

        //Questão 1
        int verticeOrigem = setIndex(origem);
        int verticeDestino = setIndex(destino);

        int[][] matrizAdjacencia = new int[numeroVertices][numeroVertices];
        for (int i = 0; i < matriz.length; i++) {
            for (int j = 0; j < matriz[i].length; j++) {
                if (matriz[i][j] == I) {
                    matrizAdjacencia[i][j] = Integer.MAX_VALUE;
                } else {
                    matrizAdjacencia[i][j] = (int) matriz[i][j];
                }
            }
        }

        int[][] d = dijkstra(matrizAdjacencia, numeroVertices, verticeOrigem);
        int distanciaMinima = d[verticeDestino][0];
        System.out.println("Caminho mínimo: " + obterCaminho(d, verticeOrigem, verticeDestino));
        System.out.println("Custo: " + distanciaMinima);

        //Questão 2
        ComponentesFortementeConexas grafo = new ComponentesFortementeConexas(quantidadeDeVertices);
        String tratarEspaco = conjuntos.replaceAll("\\s+", "");
        for (int i = 0; i < tratarEspaco.length(); i += 2) {
            if (i + 1 < tratarEspaco.length()) {
                String letra1 = tratarEspaco.substring(i, i + 1);
                String letra2 = tratarEspaco.substring(i + 1, i + 2);
                grafo.adicionarAresta(setIndex(letra1.charAt(0)), setIndex(letra2.charAt(0)));
            }
        }

        System.out.println("#" + quantidadeDeGrafos);
        System.out.println("##" + grafo.imprimirComponentesFortementeConexas());

    }

    public static int encontrarMenorDistancia(int[][] d, boolean[] visitados) {
        int valorMinimo = Integer.MAX_VALUE;
        int indice = -1;
        for (int i = 0; i < d.length; i++) {
            if (!visitados[i] && d[i][0] < valorMinimo) {
                valorMinimo = d[i][0];
                indice = i;
            }
        }
        return indice;
    }

    public static StringBuilder obterCaminho(int[][] d, int verticeOrigem, int verticeDestino) {
        StringBuilder caminho = new StringBuilder();
        int verticeAtual = verticeDestino;

        caminho.append(getLabel(verticeAtual));

        while (verticeAtual != verticeOrigem) {
            verticeAtual = d[verticeAtual][1];
            caminho.insert(0, getLabel(verticeAtual) + " -> ");
        }

        return caminho;
    }

    static char getLabel(int vertex) {
        return (char) ('A' + vertex);
    }

    static int setIndex(char letter) {
        return letter - 'A';
    }

    public static int[][] dijkstra(int[][] matrizAdjacencia, int numeroVertices, int verticeOrigem) {
        int[][] d = new int[numeroVertices][2];
        boolean[] visitados = new boolean[numeroVertices];

        for (int i = 0; i < numeroVertices; i++) {
            d[i][0] = Integer.MAX_VALUE;
            d[i][1] = verticeOrigem;
        }

        d[verticeOrigem][0] = 0;

        for (int i = 0; i < numeroVertices - 1; i++) {
            int verticeAtual = encontrarMenorDistancia(d, visitados);
            visitados[verticeAtual] = true;

            for (int v = 0; v < numeroVertices; v++) {
                if (!visitados[v] && matrizAdjacencia[verticeAtual][v] != Integer.MAX_VALUE
                        && d[verticeAtual][0] != Integer.MAX_VALUE
                        && d[verticeAtual][0] + matrizAdjacencia[verticeAtual][v] < d[v][0]) {
                    d[v][0] = d[verticeAtual][0] + matrizAdjacencia[verticeAtual][v];
                    d[v][1] = verticeAtual;
                }
            }
        }

        return d;
    }

    static class ComponentesFortementeConexas {

        private int quantidadeVertices;
        private List<List<Integer>> listaAdjacencia;

        public int imprimirComponentesFortementeConexas() {
            int countConjuntos = 0;
            Stack<Integer> pilha = new Stack<>();
            boolean[] visitados = new boolean[quantidadeVertices];

            for (int i = 0; i < quantidadeVertices; i++) {
                if (!visitados[i]) {
                    controle(i, visitados, pilha);
                }
            }

            ComponentesFortementeConexas gt = obtergt();

            Arrays.fill(visitados, false);

            while (!pilha.empty()) {
                int vertice = pilha.pop();
                if (!visitados[vertice]) {
                    System.out.print("{");
                    gt.dfs(vertice, visitados);
                    countConjuntos++;
                    System.out.println("}");
                }
            }
            return countConjuntos;
        }

        public ComponentesFortementeConexas(int quantidadeVertices) {
            this.quantidadeVertices = quantidadeVertices;
            listaAdjacencia = new ArrayList<>(quantidadeVertices);
            for (int i = 0; i < quantidadeVertices; i++) {
                listaAdjacencia.add(new ArrayList<>());
            }
        }

        public void adicionarAresta(int origem, int destino) {
            listaAdjacencia.get(origem).add(destino);
        }

        private ComponentesFortementeConexas obtergt() {
            ComponentesFortementeConexas gt = new ComponentesFortementeConexas(quantidadeVertices);

            for (int i = 0; i < quantidadeVertices; i++) {
                for (Integer vizinho : listaAdjacencia.get(i)) {
                    gt.adicionarAresta(vizinho, i);
                }
            }

            return gt;
        }

        private void dfs(int v, boolean[] visitados) {
            visitados[v] = true;
            System.out.print(getLabel(v) + " ");

            for (Integer vizinho : listaAdjacencia.get(v)) {
                if (!visitados[vizinho]) {
                    dfs(vizinho, visitados);
                }
            }
        }

        private void controle(int vertice, boolean[] visitados, Stack<Integer> pilha) {
            visitados[vertice] = true;

            for (Integer vizinho : listaAdjacencia.get(vertice)) {
                if (!visitados[vizinho]) {
                    controle(vizinho, visitados, pilha);
                }
            }

            pilha.push(vertice);
        }
    }
}
