package br.senac.sp.pi.controller;

import br.senac.sp.pi.entidade.Cliente;
import br.senac.sp.pi.entidade.Usuario;
import br.senac.sp.pi.utils.FiltroUsuario;
import br.senac.sp.pi.utils.UsuarioUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;

@Controller
public class ControllerLogin {
    @GetMapping("/acesso")
    public ResponseEntity<Usuario> acesso(HttpServletRequest request) {
        return new ResponseEntity(UsuarioUtils.getUsuarioLogado(request), HttpStatus.OK);
    }

    @PostMapping("/login")
    public ResponseEntity logar(FiltroUsuario filtro, HttpServletRequest request) {
        List<Usuario> usuarios = UsuarioUtils.getUsuarios(filtro);
        if (!usuarios.isEmpty()) {
            Usuario usuarioLogado = usuarios.get(0);
            if (usuarioLogado != null && usuarioLogado.validarSenha(filtro.getSenha())) {
                HttpSession sessao = request.getSession();
                sessao.setAttribute("usuario", usuarioLogado);
                UsuarioUtils.salvarCarrinhoNoBanco(request);
                return new ResponseEntity("200", HttpStatus.OK);
            }
        }
        return new ResponseEntity(HttpStatus.NOT_FOUND);
    }

    @GetMapping("logout")
    public String logout(HttpServletRequest request) {
        HttpSession sessao = request.getSession();
        sessao.invalidate();
        return "redirect:/login";
    }

    @GetMapping("login")
    public ModelAndView login(HttpServletRequest request) {
        return new ModelAndView("../static/notestore/login");
    }

    @GetMapping("nao-autorizado")
    public ModelAndView acessoNegado() {
        return new ModelAndView("../static/nao-autorizado");
    }

}
