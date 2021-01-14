package br.senac.sp.pi.entidade;

import lombok.Data;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class Pedido {
    private int idPedido;
    private int numeroPedido;
    private LocalDateTime data;
    private String emailCliente;
    private List<ProdutosCarrinho> produtosCarrinho;
    private Endereco endereco;
    private double precoTotal;
    private double precoFrete;
    private int qtdParcelas;
    private String status;
    private String formaPagamento;
}
