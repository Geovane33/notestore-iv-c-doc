package br.senac.sp.pi.utils;

import com.mysql.cj.util.StringUtils;
import lombok.Data;

@Data
public class FiltroProduto extends Filtro {

    private String quantidade;
    private String palavrasChave;
    private boolean imgsAtualizadas;

    public boolean withPalavrasChave() {
        return !StringUtils.isNullOrEmpty(palavrasChave);
    }

    public boolean withQuantidade() {
        return !StringUtils.isNullOrEmpty(quantidade);
    }

}
