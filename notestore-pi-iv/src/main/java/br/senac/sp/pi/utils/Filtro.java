package br.senac.sp.pi.utils;

import com.mysql.cj.util.StringUtils;
import lombok.Data;

@Data
public abstract class Filtro {
    private String id;
    private String nome;
    private boolean andDeleted;

    public void setAndDeleted(boolean deleted) {
        this.andDeleted = deleted;
    }

    public boolean withID() {
        return !StringUtils.isNullOrEmpty(id);
    }

    public boolean withNome() {
        return !StringUtils.isNullOrEmpty(nome);
    }

    public boolean AndDeleted() {
        return andDeleted;
    }
}
