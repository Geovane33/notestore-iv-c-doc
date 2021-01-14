package br.senac.sp.pi.utils;

import com.mysql.cj.util.StringUtils;
import lombok.Data;

@Data
public class FiltroUsuario extends Filtro {

    private String email;
    private String cpf;
    private String senha;

    public boolean withEmail() {
        return !StringUtils.isNullOrEmpty(email);
    }

    public boolean withCpf() {
        return !StringUtils.isNullOrEmpty(cpf);
    }

}
