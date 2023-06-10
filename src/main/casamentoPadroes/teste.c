#include <stdbool.h>
#include <stdio.h>

void montarPrefixoBasico() {
    int padraoBytes[] = {1,2,7,1,3,1,4,1,2,7,1,3,1};
    int prefixoBasico[sizeof(padraoBytes) / sizeof(int)];

    // Duas primeiras posicoes, por definicao, -1 e 0
    prefixoBasico[0] = -1;
    if (sizeof(padraoBytes) / sizeof(int) > 1)
        prefixoBasico[1] = 0;

    // Adicionar valor para outras posicoes
    for (int i = 2; i < sizeof(padraoBytes) / sizeof(int); i++) {
        int byteAnterior = padraoBytes[i-1];
        int pos = i-2;
        bool stop = false;

        prefixoBasico[i] = prefixoBasico[i-1] + 1;

        while (!stop) {
            // Tentar encontrar padrao repetido
            while (pos >= 0 && byteAnterior != padraoBytes[pos]) {
                prefixoBasico[i] = pos;
                pos--;
            }

            // Testar se encontrou de fato
            if (pos > 0) {
                int newPos = pos;
                int dif = i-1;

                while (newPos > 0 && padraoBytes[newPos--] == padraoBytes[dif--]);

                // Corrigir se houve quebra no padrao
                if (newPos == 0 && padraoBytes[newPos] == padraoBytes[dif]) {
                    stop = true;
                } else {
                    prefixoBasico[i] = 0;
                }
            }
            else {
                stop = true;
            }
            
            pos--;
        }
    }

    for(int i = 0; i < 13; i++) {
        printf("%d\n", prefixoBasico[i]);
    }

}

int main () {
    montarPrefixoBasico();
}