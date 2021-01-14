/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.senac.sp.pi.entidade;

import lombok.Data;

/**
 * @author geovane.saraujo
 */
@Data
public class Faq {
    private int id;
    private String pergunta;
    private String resposta;
}
