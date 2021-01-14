package br.senac.sp.pi;

import br.senac.sp.pi.dao.PedidoDAO;
import br.senac.sp.pi.entidade.Usuario;
import br.senac.sp.pi.utils.AtualizarLinkImagens;
import br.senac.sp.pi.utils.FiltroUsuario;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class NotestoreApplication {

    public static void main(String[] args) {
        SpringApplication.run(NotestoreApplication.class, args);
        AtualizarLinkImagens atualizarLinkImagens = new AtualizarLinkImagens();
        atualizarLinkImagens.start();
    }

}
