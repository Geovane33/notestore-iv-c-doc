package br.senac.sp.pi.entidade;

import java.time.LocalDate;

import at.favre.lib.crypto.bcrypt.BCrypt;
import br.senac.sp.pi.utils.PerfilType;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

@Data
public class Usuario {
    private int id;
    private String senha;
    private PerfilType perfil;
    private String nome;
    private String sobrenome;
    private String cpf;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate dataNascimento;
    private String sexo;
    private String email;
    private String telefone;
    private String cep;
    private String rua;
    private String numero;
    private String bairro;
    private String estado;
    private String cidade;

    /**
     * Criptografar senha do usuario
     *
     * @param senhaAberta - senha para criptografas
     * @return
     */
    public String encodeSenha(String senhaAberta) {
        return BCrypt.withDefaults().hashToString(12, senhaAberta.toCharArray());
    }

    /**
     * validar a senha do usuario
     *
     * @param senhaAberta - senha do usuario
     * @return
     */
    public boolean validarSenha(String senhaAberta) {
        BCrypt.Result result = BCrypt.verifyer().verify(senhaAberta.toCharArray(), this.senha);
        return result.verified;
    }

    public boolean isAdmin() {
        return PerfilType.Administrador.equals(this.perfil);
    }

    public boolean isEstoquista() {
        return PerfilType.Estoquista.equals(this.perfil);
    }

    public boolean isGerente() {
        return PerfilType.Gerente.equals(this.perfil);
    }

    public boolean isClient() {
        return PerfilType.Cliente.equals(this.perfil);
    }

}
