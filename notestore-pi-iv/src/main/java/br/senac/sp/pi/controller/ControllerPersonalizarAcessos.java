package br.senac.sp.pi.controller;

import br.senac.sp.pi.entidade.Produto;
import br.senac.sp.pi.entidade.Usuario;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MultipartRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/notestore-acesso")
public class ControllerPersonalizarAcessos {
    @GetMapping
    public ResponseEntity getAcesso(HttpServletRequest request) {
        HttpSession sessao = request.getSession();
        Usuario usuario = new Usuario();
        if (sessao == null) {
            return new ResponseEntity(HttpStatus.UNAUTHORIZED);
        } else {
            usuario = (Usuario) sessao.getAttribute("usuario");
            return new ResponseEntity(usuario.getPerfil(), HttpStatus.UNAUTHORIZED);
        }

    }
}
