package br.senac.sp.pi.controller;

import br.senac.sp.pi.dao.CarrinhoDAO;
import br.senac.sp.pi.dao.EnderecoDAO;
import br.senac.sp.pi.dao.PedidoDAO;
import br.senac.sp.pi.dao.ProdutoDAO;
import br.senac.sp.pi.entidade.*;
import br.senac.sp.pi.utils.Filtro;
import br.senac.sp.pi.utils.FiltroProduto;
import br.senac.sp.pi.utils.FiltroUsuario;
import br.senac.sp.pi.utils.UsuarioUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/pedido")
public class ControllerPedido {

    private final CarrinhoDAO carrinhoDAO = new CarrinhoDAO();
    private final ProdutoDAO produtoDAO = new ProdutoDAO();
    private final EnderecoDAO enderecoDAO = new EnderecoDAO();
    private final PedidoDAO pedidoDAO = new PedidoDAO();

    @GetMapping("/consultar")
    public ResponseEntity<Pedido> consultar(HttpServletRequest request) {
        List<Pedido> pedidos;
        FiltroUsuario filtroUsuario = new FiltroUsuario();
        Usuario usuario = UsuarioUtils.getUsuarioLogado(request);
        if (usuario.isEstoquista()) {
            pedidos = pedidoDAO.consultar(filtroUsuario)
                    .stream().sorted(Comparator.comparing(Pedido::getData))
                    .collect(Collectors.toList());
            //.stream().sorted((pedido, t1) -> t1.getData().compareTo(pedido.getData())).collect(Collectors.toList())
        } else {
            filtroUsuario.setEmail(usuario.getEmail());
            pedidos = pedidoDAO.consultar(filtroUsuario)
                    .stream().sorted((pedido, t1) -> t1.getData().compareTo(pedido.getData()))
                    .collect(Collectors.toList());

        }
        return new ResponseEntity(pedidos, HttpStatus.OK);
    }


    @PostMapping()
    public ResponseEntity salvarPedido(@ModelAttribute Pedido pedido, Integer idEndereco, HttpServletRequest request) {
        Usuario usuario = UsuarioUtils.getUsuarioLogado(request);
        FiltroUsuario filtroUsuario = new FiltroUsuario();
        if (pedido.getIdPedido() == 0) {
            pedido.setEmailCliente(usuario.getEmail());
            pedido.setNumeroPedido(new Random().nextInt(10000));
            filtroUsuario.setEmail(usuario.getEmail());

            List<Endereco> enderecos = enderecoDAO.consultar(filtroUsuario);
            enderecos = enderecos.stream().filter(endereco -> endereco.getId() == idEndereco).collect(Collectors.toList());
            pedido.setEndereco(enderecos.get(0));
            pedido.setProdutosCarrinho(carrinhoDAO.consultar(filtroUsuario));
            if (pedidoDAO.salvar(pedido)) {
                for (ProdutosCarrinho produtosCarrinho : pedido.getProdutosCarrinho()) {
                    produtosCarrinho.getProduto().setQuantidade(produtosCarrinho.getProduto()
                            .getQuantidade() - produtosCarrinho.getQuantidadeCompra());
                    produtoDAO.atualizar(produtosCarrinho.getProduto());
                }
                return new ResponseEntity(pedido, HttpStatus.OK);
            }
        } else {
            if (pedidoDAO.atualizar(pedido)) {
                return new ResponseEntity(HttpStatus.OK);
            }

        }

        return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @GetMapping("/cad-cartao")
    public ModelAndView pedidoCartao() {
        return new ModelAndView("../static/clientes/cadastro-cartao");
    }

    @GetMapping("/verificar")
    public ModelAndView pedidoVerificar() {
        return new ModelAndView("../static/clientes/pedido");
    }


    @GetMapping("/endereco")
    public ModelAndView pedidoEndereco() {
        return new ModelAndView("../static/clientes/selecione-endereco");
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

