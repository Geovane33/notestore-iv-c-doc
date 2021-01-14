package br.senac.sp.pi.controller;

import br.senac.sp.pi.dao.CarrinhoDAO;
import br.senac.sp.pi.dao.ProdutoDAO;
import br.senac.sp.pi.entidade.Pedido;
import br.senac.sp.pi.entidade.ProdutosCarrinho;
import br.senac.sp.pi.entidade.Produto;
import br.senac.sp.pi.entidade.Usuario;
import br.senac.sp.pi.utils.Filtro;
import br.senac.sp.pi.utils.FiltroProduto;
import br.senac.sp.pi.utils.FiltroUsuario;
import br.senac.sp.pi.utils.UsuarioUtils;
import org.springframework.boot.context.properties.bind.DefaultValue;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/carrinho")
public class ControllerCarrinho {

    private final CarrinhoDAO carrinhoDAO = new CarrinhoDAO();
    private final ProdutoDAO produtoDAO = new ProdutoDAO();

    @GetMapping("/consultar")
    public ResponseEntity<List<ProdutosCarrinho>> consultar(HttpServletRequest request) {
        List<ProdutosCarrinho> carrinho;
        FiltroUsuario filtroUsuario = new FiltroUsuario();
        Usuario usuario = UsuarioUtils.getUsuarioLogado(request);
        if (usuario == null) {
            carrinho = getCarrinhoSessao(request);
            List<ProdutosCarrinho> carrinho2 = carrinho.stream().filter(produto -> produto.getQuantidadeCompra() > 0).collect(Collectors.toList());
            return new ResponseEntity(carrinho2, HttpStatus.OK);
        }
        filtroUsuario.setEmail(usuario.getEmail());
        carrinho = carrinhoDAO.consultar(filtroUsuario);
        return new ResponseEntity(carrinho, HttpStatus.OK);
    }

    @DeleteMapping()
    public ResponseEntity excluirProdutosCarrinho(@DefaultValue("0") int id, HttpServletRequest request) {
        if (getCarrinhoSessao(request).isEmpty()) {
            if (carrinhoDAO.excluir(id)) {
                return new ResponseEntity(HttpStatus.OK);
            }
        } else {
            List<ProdutosCarrinho> carrinho = getCarrinhoSessao(request);
            carrinho.removeIf(p -> p.getProduto().getId() == id);
            return new ResponseEntity(HttpStatus.OK);
        }
        return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @PostMapping
    public ResponseEntity adicionar(String idProduto, int quantidade, HttpServletRequest request) {
        String estoqueSuficiente = "200";
        FiltroUsuario filtroUsuario = new FiltroUsuario();
        List<ProdutosCarrinho> carrinho = new ArrayList<>();
        ProdutosCarrinho produtosCarrinho = new ProdutosCarrinho();
        Usuario usuario = UsuarioUtils.getUsuarioLogado(request);
        if (usuario == null) {
            estoqueSuficiente = carrinhoSessao(request, idProduto, quantidade);
            return new ResponseEntity(estoqueSuficiente, HttpStatus.OK);
        }
        filtroUsuario.setId(idProduto);
        filtroUsuario.setEmail(usuario.getEmail());
        carrinho = carrinhoDAO.consultar(filtroUsuario);
        if (carrinho.isEmpty()) {
            FiltroProduto filtroProduto = new FiltroProduto();
            filtroProduto.setId(idProduto);
            if (carrinhoDAO.salvar(addProdCar(usuario, filtroProduto, quantidade, estoqueSuficiente))) {
                return new ResponseEntity(HttpStatus.OK);
            }
        } else {
            produtosCarrinho = carrinho.get(0);
            if (validarQuantidadeEstoque(produtosCarrinho, quantidade)) {
                produtosCarrinho.setQuantidadeCompra(produtosCarrinho.getQuantidadeCompra() + quantidade);
                if (carrinhoDAO.atualizar(produtosCarrinho)) {
                    return new ResponseEntity(HttpStatus.OK);
                }
            } else {
                estoqueSuficiente = "202";
                return new ResponseEntity(estoqueSuficiente, HttpStatus.OK);
            }
        }
        return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private String carrinhoSessao(HttpServletRequest request, String idProduto, int quantidade) {
        String estoqueSuficiente = "200";
        ProdutosCarrinho produtosCarrinho = new ProdutosCarrinho();
        List<ProdutosCarrinho> carrinho = new ArrayList<>();
        FiltroProduto filtroProduto = new FiltroProduto();
        filtroProduto.setId(idProduto);
        carrinho = getCarrinhoSessao(request);
        List<ProdutosCarrinho> carrinho2 = carrinho.stream().filter(p -> p.getProduto().getId() == Integer.parseInt(idProduto)).collect(Collectors.toList());
        if (carrinho2.size() == 1) {
            produtosCarrinho = carrinho2.get(0);
            if (validarQuantidadeEstoque(produtosCarrinho, quantidade)) {
                produtosCarrinho.setQuantidadeCompra(produtosCarrinho.getQuantidadeCompra() + quantidade);
            } else {
                estoqueSuficiente = "202";
            }
        } else {
            carrinho.add(addProdCar(new Usuario(), filtroProduto, quantidade, estoqueSuficiente));
        }
        setCarrinhoSessao(request, carrinho);
        return estoqueSuficiente;
    }

    private ProdutosCarrinho addProdCar(Usuario usuario, Filtro filtroProduto, int quantidade, String estoqueSuficiente) {
        ProdutosCarrinho produtosCarrinho = new ProdutosCarrinho();
        Produto p = produtoDAO.consultar(filtroProduto).get(0);
        produtosCarrinho.setProduto(p);
        if (validarQuantidadeEstoque(produtosCarrinho, quantidade)) {
            produtosCarrinho.setQuantidadeCompra(quantidade);
            estoqueSuficiente = "202";
        } else {
            produtosCarrinho.setQuantidadeCompra(p.getQuantidade());
        }
        produtosCarrinho.setEmailCliente(usuario.getEmail());
        return produtosCarrinho;
    }

    private boolean validarQuantidadeEstoque(ProdutosCarrinho produtosCarrinho, int quantidade) {
        return produtosCarrinho.getQuantidadeCompra() + quantidade <= produtosCarrinho.getProduto().getQuantidade();
    }

    private List<ProdutosCarrinho> getCarrinhoSessao(HttpServletRequest request) {
        HttpSession sessao = request.getSession();
        if (sessao.getAttribute("carrinho") == null) {
            sessao.setAttribute("carrinho", new ArrayList<>());
        }
        return (List<ProdutosCarrinho>) sessao.getAttribute("carrinho");
    }

    private void setCarrinhoSessao(HttpServletRequest request, List<ProdutosCarrinho> carrinho) {
        HttpSession sessao = request.getSession();
        sessao.setAttribute("carrinho", carrinho);
    }

}
//    create table carrinho (
//            id_carrinho int auto_increment primary key,
//            email_cliente varchar(70) not null,
//    id_produto int not null,
//    quantidade_produto int not null,
//    preco_produto double not null,
//    foreign key (id_produto) references produtos(id_produto)
//            )

