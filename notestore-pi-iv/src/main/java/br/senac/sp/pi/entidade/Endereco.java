package br.senac.sp.pi.entidade;

import lombok.Data;

@Data
public class Endereco {
    private String emailCliente;
    private int id;
    private String nome;
    private String cep;
    private String rua;
    private String numero;
    private String bairro;
    private String estado;
    private String cidade;
}
