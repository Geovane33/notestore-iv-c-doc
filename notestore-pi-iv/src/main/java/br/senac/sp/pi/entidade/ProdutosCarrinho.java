package br.senac.sp.pi.entidade;

import lombok.Data;


@Data
public class ProdutosCarrinho {
    private Produto produto;
    private int idCarrinho;
    private String emailCliente;
    private int quantidadeCompra;
    private double precoCompra;
    private int idPedido;
}
