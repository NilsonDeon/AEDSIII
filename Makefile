# Makefile para compilar o projeto de TP01
# Autores: Gabriel Vargas Bento de Souza e Nilson Deon Cordeiro Filho
# Data: 03/2023

# Arquivos de origem
SOURCES := src/main/app/IO.java src/main/app/Musica.java src/main/hashing/Bucket.java src/main/hashing/Diretorio.java src/main/hashing/HashingExtensivel.java src/main/arvores/arvoreB/NoB.java src/main/arvores/arvoreB/ArvoreB.java src/main/arvores/arvoreBStar/NoBStar.java src/main/arvores/arvoreBStar/ArvoreBStar.java src/main/listaInvertida/ListaInvertida_AnoLancamento.java src/main/listaInvertida/ListaInvertida_Artistas.java src/main/listaInvertida/ListaInvertida.java src/main/compressao/No.java src/main/compressao/Huffman.java src/main/compressao/Dicionario.java src/main/compressao/LZW.java src/main/compressao/LZ78.java src/main/compressao/Compressao.java src/main/crud/CRUD.java src/main/sort/auxiliar/QuickSort.java src/main/sort/auxiliar/MinHeap.java src/main/sort/ComumSort.java src/main/sort/TamanhoVariavelSort.java src/main/sort/SelecaoPorSubstituicaoSort.java src/main/sort/OrdenacaoExterna.java src/main/casamentoPadroes/auxiliar/Contador.java src/main/casamentoPadroes/ForcaBruta.java src/main/casamentoPadroes/KMP.java src/main/casamentoPadroes/CasamentoPadroes.java src/main/Main.java

# Compilar
all: | src/bin $(SOURCES:.java=.class)

%.class: %.java
	javac -cp src/bin -d src/bin $<

# Executar
run: clean all
	java -cp src/bin Main

# Limpar
clean:
	-rm -rf src/bin

# Criar pasta bin, caso nao exista
src/bin:
	mkdir -p src/bin
