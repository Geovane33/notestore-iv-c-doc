/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.senac.sp.pi.controller;

import br.senac.sp.pi.dao.ClienteDAO;
import br.senac.sp.pi.dao.EnderecoDAO;
import br.senac.sp.pi.entidade.Cliente;

import java.util.ArrayList;
import java.util.List;

import br.senac.sp.pi.entidade.Endereco;
import br.senac.sp.pi.entidade.Usuario;
import br.senac.sp.pi.utils.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.boot.context.properties.bind.DefaultValue;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;

/**
 * @author Jonathan
 */
@Controller
@RequestMapping("/clientes")
public class ControllerCliente {

    private ClienteDAO clienteDAO = new ClienteDAO();
    private EnderecoDAO enderecoDAO = new EnderecoDAO();

    public ResponseEntity<Cliente> consultar(HttpServletRequest request) {
        List<Cliente> clientes = new ArrayList();
        Usuario usuario = UsuarioUtils.getUsuarioLogado(request);
        FiltroUsuario filtroUsuario = new FiltroUsuario();
        if (usuario != null) {
            filtroUsuario.setEmail(usuario.getEmail());
            clientes = clienteDAO.consultar(filtroUsuario);
            if (clientes.size() == 1) {
                return new ResponseEntity(clientes.get(0), HttpStatus.OK);
            }
        }
        return new ResponseEntity(new Usuario(), HttpStatus.OK);
    }

    @GetMapping("/enderecos")
    public ResponseEntity<List<Endereco>> consultarEnderecos(HttpServletRequest request) {
        List<Endereco> enderecos = new ArrayList();
        Usuario usuario = UsuarioUtils.getUsuarioLogado(request);
        FiltroUsuario filtroUsuario = new FiltroUsuario();
        filtroUsuario.setEmail(usuario.getEmail());
        enderecos = enderecoDAO.consultar(filtroUsuario);
        if (enderecos.size() > 0) {
            return new ResponseEntity(enderecos, HttpStatus.OK);
        } else {
            return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/enderecos")
    public ResponseEntity excluirEnderecos(@DefaultValue("0") int id) {

        if (enderecoDAO.excluir(id)) {
            return new ResponseEntity(HttpStatus.OK);
        } else {
            return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/enderecos")
    public ResponseEntity enderecos(Endereco endereco, HttpServletRequest request) {
        endereco.setEmailCliente(UsuarioUtils.getUsuarioLogado(request).getEmail());
        if (endereco.getId() == 0) {
            if (enderecoDAO.salvar(endereco)) {
                return new ResponseEntity(HttpStatus.OK);
            } else {
                return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } else {
            if (enderecoDAO.atualizar(endereco)) {
                return new ResponseEntity(HttpStatus.OK);
            } else {
                return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }

    }

    @PostMapping("/senha")
    public ResponseEntity alterarSenha(Cliente cliente, HttpServletRequest request) {
        Usuario usuarioLogado = UsuarioUtils.getUsuarioLogado(request);
        cliente.setEmail(usuarioLogado.getEmail());
        if (clienteDAO.atualizarSenha(cliente)) {
            return new ResponseEntity(HttpStatus.OK);
        } else {
            return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping
    public ResponseEntity adicionar(@ModelAttribute Cliente cliente) {
        FiltroUsuario filtro = new FiltroUsuario();
        filtro.setCpf(cliente.getCpf());
        filtro.setAndDeleted(true);
        cliente.setPerfil(PerfilType.Cliente);
        if (cliente.getId() == 0) {
            if (UsuarioUtils.cpfUnique(clienteDAO, cliente) && UsuarioUtils.emailUnique(cliente)) {
                if (clienteDAO.salvar(cliente)) {
                    return new ResponseEntity(HttpStatus.CREATED);
                } else {
                    return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
                }
            } else {
                return new ResponseEntity(HttpStatus.NOT_MODIFIED);
            }
        }
        return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @PostMapping("alterar-dados")
    public ResponseEntity alterarDados(@ModelAttribute Cliente cliente, HttpServletRequest request) {
        cliente.setEmail(UsuarioUtils.getUsuarioLogado(request).getEmail());
        if (clienteDAO.atualizar(cliente)) {
            return new ResponseEntity(HttpStatus.OK);
        } else {
            return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @PutMapping
    public ResponseEntity atualizar(@ModelAttribute Cliente cliente) {
        System.out.println(cliente.getNome());
        clienteDAO.atualizar(cliente);
        return new ResponseEntity(HttpStatus.OK);
    }

    @DeleteMapping
    public ResponseEntity desativar(@DefaultValue("0") int id, HttpServletRequest request) {
        if (UsuarioUtils.usuarioLogado(clienteDAO, id, request)) {
            return new ResponseEntity(HttpStatus.NOT_MODIFIED);
        }
        if (clienteDAO.excluir(id)) {
            return new ResponseEntity(HttpStatus.OK);
        } else {
            return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/cadastro")
    public ModelAndView clienteCadastro() {
        return new ModelAndView("../static/clientes/cadastro");
    }

    @GetMapping("/consultar")
    public ModelAndView clienteListar() {
        return new ModelAndView("../static/clientes/consultar");
    }

    @GetMapping("/enderecos-visualizar")
    public ModelAndView clienteEnderecos() {
        return new ModelAndView("../static/clientes/enderecos");
    }

    @GetMapping("/dados")
    public ModelAndView clienteDados() {
        return new ModelAndView("../static/clientes/dados");
    }

    @GetMapping("/endereco-adicionar")
    public ModelAndView alterarAdicionar() {
        return new ModelAndView("../static/clientes/endereco-editar");
    }

    @GetMapping("/endereco-alterar")
    public ModelAndView alterarEndereco() {
        return new ModelAndView("../static/clientes/endereco-editar");
    }

    @GetMapping("/pedidos")
    public ModelAndView pedidos() {
        return new ModelAndView("../static/clientes/consultar-pedidos");
    }

    @GetMapping("/carrinho")
    public ModelAndView carrinho() {
        return new ModelAndView("../static/clientes/carrinho");
    }
}

