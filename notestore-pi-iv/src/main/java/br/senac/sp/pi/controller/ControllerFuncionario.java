package br.senac.sp.pi.controller;

import br.senac.sp.pi.dao.FuncionarioDAO;
import br.senac.sp.pi.entidade.Funcionario;

import java.util.List;

import br.senac.sp.pi.utils.FiltroUsuario;
import br.senac.sp.pi.utils.UsuarioUtils;
import org.springframework.boot.context.properties.bind.DefaultValue;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/funcionarios")
public class ControllerFuncionario {

    private FuncionarioDAO funcionarioDAO = new FuncionarioDAO();

    @GetMapping
    public ResponseEntity<Funcionario> consultar(@ModelAttribute FiltroUsuario filtro) {//, HttpServletRequest request) {
        List<Funcionario> funcionarios;
        funcionarios = funcionarioDAO.consultar(filtro);
        return new ResponseEntity(funcionarios, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity adicionar(@ModelAttribute Funcionario funcionario) {

        if (funcionario.getId() == 0) {
            if (UsuarioUtils.cpfUnique(funcionarioDAO, funcionario) && UsuarioUtils.emailUnique(funcionario)) {
                funcionarioDAO.salvar(funcionario);
                return new ResponseEntity("200", HttpStatus.CREATED);
            } else {
                return new ResponseEntity(HttpStatus.NOT_MODIFIED);
            }
        } else {
            if (UsuarioUtils.cpfUnique(funcionarioDAO, funcionario)) {
                if (funcionarioDAO.atualizar(funcionario)) {
                    return new ResponseEntity(HttpStatus.OK);
                } else {
                    return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
                }
            }
            return new ResponseEntity(HttpStatus.NOT_MODIFIED);
        }
    }

    @DeleteMapping
    public ResponseEntity desativar(@DefaultValue("0") int id, HttpServletRequest request) {
        if (UsuarioUtils.usuarioLogado(funcionarioDAO, id, request)) {
            return new ResponseEntity(HttpStatus.NOT_MODIFIED);
        }
        if (funcionarioDAO.excluir(id)) {
            return new ResponseEntity(HttpStatus.OK);
        }
        return new ResponseEntity(HttpStatus.NOT_MODIFIED);
    }

    @GetMapping("/cadastro")
    public ModelAndView FuncionarioCadastro() {
        return new ModelAndView("../static/funcionarios/cadastro");
    }

    @GetMapping("/consultar")
    public ModelAndView FuncionarioListar() {
        return new ModelAndView("../static/funcionarios/consultar");
    }
}