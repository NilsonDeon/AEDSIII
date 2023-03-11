# Makefile para compilar o projeto de TP01
# Autores: Gabriel Vargas Bento de Souza e Nilson Deon Cordeiro Filho
# Data: 03/2023

# Arquivos de origem
SOURCES := scr/main/java/IO.java scr/main/java/Musica.java scr/main/crud/CRUD.java scr/main/sort/auxiliar/QuickSort.java scr/main/sort/auxiliar/MinHeap.java scr/main/sort/ComumSort.java scr/main/sort/TamanhoVariavelSort.java scr/main/sort/SelecaoPorSubstituicaoSort.java scr/main/sort/OrdenacaoExterna.java scr/main/Main.java

# Compilar
all: | scr/bin $(SOURCES:.java=.class)

%.class: %.java
	javac -cp scr/bin -d scr/bin $<

# Executar
run: clean all
	java -cp scr/bin Main

# Limpar
clean:
	rm -rf scr/bin

# Criar pasta bin, caso nao exista
scr/bin:
	mkdir -p scr/bin