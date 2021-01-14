package br.senac.sp.pi.utils;

import br.senac.sp.pi.dao.CarrinhoDAO;
import br.senac.sp.pi.dao.ClienteDAO;
import br.senac.sp.pi.dao.DAO;
import br.senac.sp.pi.dao.FuncionarioDAO;
import br.senac.sp.pi.entidade.ProdutosCarrinho;
import br.senac.sp.pi.entidade.Usuario;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class UsuarioUtils {
    private static final CarrinhoDAO carrinhoDAO = new CarrinhoDAO();
    private static final FuncionarioDAO funcionarioDAO = new FuncionarioDAO();

    public static List<Usuario> getUsuarios(Filtro filtro) {
        ClienteDAO clienteDAO = new ClienteDAO();
        List usuarios = funcionarioDAO.consultar(filtro);
        usuarios.addAll(clienteDAO.consultar(filtro));
        if (usuarios.size() == 0) {
            return Collections.emptyList();
        } else {
            return usuarios;
        }
    }

    public static boolean usuarioLogado(DAO dao, int id, HttpServletRequest request) {
        Usuario usuario = UsuarioUtils.getUsuarioLogado(request);
        FiltroUsuario filtroUsuario = new FiltroUsuario();
        filtroUsuario.setAndDeleted(true);
        filtroUsuario.setId(String.valueOf(id));
        List<Usuario> usuarios = dao.consultar(filtroUsuario);
        return usuarios.size() == 1 && usuarios.get(0).getEmail().equals(usuario.getEmail());
    }

    public static Usuario getUsuarioLogado(HttpServletRequest request) {
        HttpSession sessao = request.getSession();
        if (sessao == null) {
            return null;
        }
        return (Usuario) sessao.getAttribute("usuario");
    }

    public static boolean cpfUnique(DAO dao, Usuario usuario) {
        FiltroUsuario filtro = new FiltroUsuario();
        filtro.setCpf(usuario.getCpf());
        filtro.setAndDeleted(true);
        List<Usuario> usuarios = getUsuarios(filtro);
        return (usuarios.isEmpty() || usuarios.size() == 1 && usuarios.get(0).getId() == usuario.getId());
    }

    public static boolean emailUnique(Usuario usuario) {
        FiltroUsuario filtroUsuario = new FiltroUsuario();
        filtroUsuario.setEmail(usuario.getEmail());
        filtroUsuario.setAndDeleted(true);
        List<Usuario> usuarios = getUsuarios(filtroUsuario);
        return usuarios.isEmpty();
    }

    public static void salvarCarrinhoNoBanco(HttpServletRequest request) {
        HttpSession sessao = request.getSession();
        Usuario usuario = getUsuarioLogado(request);
        List<ProdutosCarrinho> carrinho;
        if (sessao.getAttribute("carrinho") != null) {
            carrinho = (List<ProdutosCarrinho>) sessao.getAttribute("carrinho");

            FiltroUsuario filtroUsuario = new FiltroUsuario();
            filtroUsuario.setEmail(usuario.getEmail());
            List<ProdutosCarrinho> carrinhoBanco = carrinhoDAO.consultar(filtroUsuario);
            carrinho.forEach(c -> c.setEmailCliente(usuario.getEmail()));
            carrinho.removeIf(c -> carrinhoBanco.stream().filter(c2 -> c.getProduto().getId() == c2.getProduto().getId()).count() > 0);
            carrinho.removeIf(c -> carrinhoDAO.salvar(c));
            //mesmo produtos na sessão ao tentar salvar no banco ele duplica e o correto é atualizar a quantidade
            sessao.setAttribute("carrinho", null);
            if (!carrinho.isEmpty()) {
                new RuntimeException("Erro ao salvar carrinho da sessao no banco de dados");
            }
        }
    }

}
