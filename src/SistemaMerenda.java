import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;

class NoDistancia {
    int idDestino;
    double distancia;
    NoDistancia proximo;

    public NoDistancia(int idDestino, double distancia) {
        this.idDestino = idDestino;
        this.distancia = distancia;
        this.proximo = null;
    }
}

class NoCreche {
    int id;
    String nome;
    NoDistancia distancias;
    NoCreche proximo;

    public NoCreche(int id, String nome) {
        this.id = id;
        this.nome = nome;
        this.distancias = null;
        this.proximo = null;
    }

    public void adicionarDistancia(int idDestino, double distancia) {
        NoDistancia novo = new NoDistancia(idDestino, distancia);
        if (distancias == null) {
            distancias = novo;
        } else {
            NoDistancia atual = distancias;
            while (atual.proximo != null) {
                atual = atual.proximo;
            }
            atual.proximo = novo;
        }
    }
}

class ListaEncadeada {
    NoCreche inicio;
    int tamanho;

    public ListaEncadeada() {
        this.inicio = null;
        this.tamanho = 0;
    }

    public int adicionarCreche(String nome) {
        int idExistente = buscarIdPorNome(nome);
        if (idExistente != -1) return idExistente;

        int novoId = tamanho;
        NoCreche novo = new NoCreche(novoId, nome);

        if (inicio == null) {
            inicio = novo;
        } else {
            NoCreche atual = inicio;
            while (atual.proximo != null) {
                atual = atual.proximo;
            }
            atual.proximo = novo;
        }
        tamanho++;
        return novoId;
    }

    public int buscarIdPorNome(String nome) {
        NoCreche atual = inicio;
        while (atual != null) {
            if (atual.nome.equalsIgnoreCase(nome)) return atual.id;
            atual = atual.proximo;
        }
        return -1;
    }

    public String buscarNomePorId(int id) {
        NoCreche atual = inicio;
        while (atual != null) {
            if (atual.id == id) return atual.nome;
            atual = atual.proximo;
        }
        return "Desconhecido";
    }

    public NoCreche buscarCrechePorId(int id) {
        NoCreche atual = inicio;
        while (atual != null) {
            if (atual.id == id) return atual;
            atual = atual.proximo;
        }
        return null;
    }
}

public class SistemaMerenda {
    private static final int MAX_CRECHES = 50;
    private static int[][] matrizAdjacencia = new int[MAX_CRECHES][MAX_CRECHES];
    private static ListaEncadeada listaCreches = new ListaEncadeada();

    public static void main(String[] args) {
        carregarDadosDoArquivo("Grafo.txt");
        Scanner scanner = new Scanner(System.in);
        int opcao;

        do {
            System.out.println("\n--- SECRETARIA DE EDUCACAO: MERENDAS ---");
            System.out.println("1. Informar numero de conexoes por creche");
            System.out.println("2. Listar conexoes de uma creche (Ordem Crescente)");
            System.out.println("3. Informar distancia entre duas creches");
            System.out.println("4. Incluir nova conexao");
            System.out.println("0. Sair");
            System.out.print("Escolha uma opcao: ");
            opcao = scanner.nextInt();
            scanner.nextLine();

            switch (opcao) {
                case 1:
                    contarConexoesPorCreche();
                    break;
                case 2:
                    System.out.print("Digite o nome da creche: ");
                    String origemListar = scanner.nextLine().trim();
                    listarConexoesOrdenadas(origemListar);
                    break;
                case 3:
                    System.out.print("Creche A: ");
                    String cA = scanner.nextLine().trim();
                    System.out.print("Creche B: ");
                    String cB = scanner.nextLine().trim();
                    consultarDistancia(cA, cB);
                    break;
                case 4:
                    System.out.print("Nome da Creche A: ");
                    String nA = scanner.nextLine().trim();
                    System.out.print("Nome da Creche B: ");
                    String nB = scanner.nextLine().trim();
                    System.out.print("Distancia em Km: ");
                    double dist = scanner.nextDouble();
                    inserirConexao(nA, nB, dist);
                    System.out.println("Conexao incluida com sucesso!");
                    break;
                case 0:
                    System.out.println("Encerrando...");
                    break;
                default:
                    System.out.println("Opcao invalida!");
            }
        } while (opcao != 0);

        scanner.close();
    }

    private static void carregarDadosDoArquivo(String nomeArquivo) {
        try (BufferedReader br = new BufferedReader(new FileReader(nomeArquivo))) {
            String linha;
            while ((linha = br.readLine()) != null) {
                if (linha.trim().isEmpty()) continue;
                String[] partes = linha.split(";");
                if(partes.length == 3) {
                    inserirConexao(partes[0].trim(), partes[1].trim(), Double.parseDouble(partes[2].trim()));
                }
            }
            System.out.println("Grafo.txt carregado com sucesso!");
        } catch (IOException e) {
            System.out.println("Arquivo '" + nomeArquivo + "' nao encontrado. O sistema iniciara vazio.");
        }
    }


    private static void inserirConexao(String origem, String destino, double distancia) {
        int idOrigem = listaCreches.adicionarCreche(origem);
        int idDestino = listaCreches.adicionarCreche(destino);


        matrizAdjacencia[idOrigem][idDestino] = 1;
        matrizAdjacencia[idDestino][idOrigem] = 1;


        NoCreche crecheOrigem = listaCreches.buscarCrechePorId(idOrigem);
        NoCreche crecheDestino = listaCreches.buscarCrechePorId(idDestino);

        crecheOrigem.adicionarDistancia(idDestino, distancia);
        crecheDestino.adicionarDistancia(idOrigem, distancia); // Grafo.txt não-direcionado
    }


    private static void contarConexoesPorCreche() {
        System.out.println("\n--- CONEXOES POR CRECHE ---");
        for (int i = 0; i < listaCreches.tamanho; i++) {
            String nome = listaCreches.buscarNomePorId(i);
            int conexoes = 0;
            for (int j = 0; j < listaCreches.tamanho; j++) {
                if (matrizAdjacencia[i][j] == 1) conexoes++;
            }
            System.out.println(nome + ": " + conexoes + " conexao(oes).");
        }
    }


    private static void listarConexoesOrdenadas(String nomeCreche) {
        int id = listaCreches.buscarIdPorNome(nomeCreche);
        if (id == -1) {
            System.out.println("Creche nao encontrada.");
            return;
        }

        NoCreche creche = listaCreches.buscarCrechePorId(id);


        int totalConexoes = 0;
        NoDistancia temp = creche.distancias;
        while (temp != null) {
            totalConexoes++;
            temp = temp.proximo;
        }

        if (totalConexoes == 0) {
            System.out.println("Nenhuma conexao encontrada para esta creche.");
            return;
        }


        int[] ids = new int[totalConexoes];
        double[] dists = new double[totalConexoes];

        temp = creche.distancias;
        int idx = 0;
        while (temp != null) {
            ids[idx] = temp.idDestino;
            dists[idx] = temp.distancia;
            idx++;
            temp = temp.proximo;
        }


        for (int i = 0; i < totalConexoes - 1; i++) {
            for (int j = 0; j < totalConexoes - i - 1; j++) {
                if (dists[j] > dists[j + 1]) {
                    // Troca distâncias
                    double tempDist = dists[j];
                    dists[j] = dists[j + 1];
                    dists[j + 1] = tempDist;
                    // Troca IDs atrelados
                    int tempId = ids[j];
                    ids[j] = ids[j + 1];
                    ids[j + 1] = tempId;
                }
            }
        }

        System.out.println("\nConexoes de [" + nomeCreche + "] em ordem crescente:");
        for (int i = 0; i < totalConexoes; i++) {
            System.out.println(" -> " + listaCreches.buscarNomePorId(ids[i]) + " (" + dists[i] + " Km)");
        }
    }


    private static void consultarDistancia(String crecheA, String crecheB) {
        int idA = listaCreches.buscarIdPorNome(crecheA);
        int idB = listaCreches.buscarIdPorNome(crecheB);

        if (idA == -1 || idB == -1) {
            System.out.println("Uma ou ambas as creches nao existem no sistema.");
            return;
        }

        if (matrizAdjacencia[idA][idB] == 1) {
            NoCreche creche = listaCreches.buscarCrechePorId(idA);
            NoDistancia atual = creche.distancias;
            while (atual != null) {
                if (atual.idDestino == idB) {
                    System.out.println("A distancia entre " + crecheA + " e " + crecheB + " e de " + atual.distancia + " Km.");
                    return;
                }
                atual = atual.proximo;
            }
        } else {
            System.out.println("Nao existe conexao direta entre as creches informadas.");
        }
    }
}