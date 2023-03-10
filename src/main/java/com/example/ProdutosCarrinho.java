package com.example;

public class ProdutosCarrinho extends Produto {

    protected Produto produto;
    private int quantidadeCarrinho;
    public ProdutosCarrinho(Produto p) {
        this.produto = p;
        this.quantidadeCarrinho = 1;
    }

    public int getQuantidadeCarrinho() {
        return quantidadeCarrinho;
    }

    public void setQuantidadeCarrinho(int quantidade) {
        this.quantidadeCarrinho = this.quantidadeCarrinho + quantidade;
    }

    public boolean contains(Produto p) {
        if (this.produto == p) {
            return true;
        } else {
            return false;
        }
    }
}
