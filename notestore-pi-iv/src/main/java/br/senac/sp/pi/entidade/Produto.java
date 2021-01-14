package br.senac.sp.pi.entidade;

import java.time.LocalDate;
import java.util.List;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

@Data
public class Produto {
    private int id;
    private String nome;
    private String marca;
    private int quantidade;
    private double preco;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate dataEntrada;
    private String descricao;
    private String palavrasChaves;
    private String categoria;
    private List<Imagem> imagens;
    private List<Faq> faqs;
}
