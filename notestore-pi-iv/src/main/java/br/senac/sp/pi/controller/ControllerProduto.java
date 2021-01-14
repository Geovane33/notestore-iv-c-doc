package br.senac.sp.pi.controller;

import br.senac.sp.pi.api.ImagemAPI;
import br.senac.sp.pi.dao.ProdutoDAO;
import br.senac.sp.pi.entidade.Faq;
import br.senac.sp.pi.entidade.Produto;
import br.senac.sp.pi.entidade.Usuario;
import br.senac.sp.pi.utils.Filtro;
import br.senac.sp.pi.utils.FiltroProduto;
import com.mysql.cj.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

import org.springframework.boot.context.properties.bind.DefaultValue;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.multipart.MultipartRequest;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * @author geovane.saraujo
 */
@Controller
@RequestMapping("/produtos")
public class ControllerProduto {

    private ProdutoDAO produtoDAO = new ProdutoDAO();

    @GetMapping
    public ResponseEntity<Produto> consultar(@ModelAttribute FiltroProduto filtro) {
        List<Produto> produtos;
        produtos = produtoDAO.consultar(filtro);
        return new ResponseEntity(produtos, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity adicionar(@ModelAttribute Produto produto, HttpServletRequest request, MultipartRequest multpartImagens) {
        produto.setFaqs(getFaqs(request));
        HttpSession sessao = request.getSession();
        Usuario usuario = new Usuario();
        if (sessao == null) {
            return new ResponseEntity(HttpStatus.UNAUTHORIZED);
        } else {
            usuario = (Usuario) sessao.getAttribute("usuario");
        }
        if (usuario.isEstoquista()) {
            produtoDAO.atualizarEstoque(produto);
            return new ResponseEntity("200", HttpStatus.OK);
        }
        if (produto.getId() == 0 && usuario.isAdmin()) {
            produtoDAO.salvar(produto);
            produtoDAO.salvarImagens(produto, multpartImagens);
            return new ResponseEntity("200", HttpStatus.CREATED);
        } else {
            produtoDAO.atualizar(produto);
            produtoDAO.salvarImagens(produto, multpartImagens);
            return new ResponseEntity("200", HttpStatus.OK);
        }
    }

    @DeleteMapping
    public ResponseEntity desativar(@DefaultValue("") String id, String imagemPath, HttpServletRequest request) {
        HttpSession sessao = request.getSession();
        Usuario usuario = new Usuario();
        if (sessao == null) {
            return new ResponseEntity(HttpStatus.UNAUTHORIZED);
        } else {
            usuario = (Usuario) sessao.getAttribute("usuario");
        }
        if (usuario.isEstoquista()) {
            return new ResponseEntity(HttpStatus.UNAUTHORIZED);
        } else if (usuario.isAdmin()) {
            if (StringUtils.isNullOrEmpty(imagemPath)) {
                if (produtoDAO.desativar(Integer.parseInt(id))) {
                    return new ResponseEntity(HttpStatus.OK);
                }
            }
            ImagemAPI imagemAPI = new ImagemAPI();
            imagemAPI.deleteImagemNuvem(imagemPath);
            if (produtoDAO.excluirImagens(imagemPath)) {
                return new ResponseEntity("200", HttpStatus.OK);
            }
        }
        return new ResponseEntity("500", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @RequestMapping(value = "/delete/{id}", method = RequestMethod.GET)
    public ResponseEntity excluirProdPermanente(@PathVariable String id) {
        if (produtoDAO.excluir(Integer.parseInt(id))) {
            return new ResponseEntity("200", HttpStatus.OK);
        }
        return new ResponseEntity("500", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private List<Faq> getFaqs(HttpServletRequest request) {
        List<Faq> faqs = new ArrayList<>();
        for (int i = 1; request.getParameter("faqPergunta" + i) != null; i++) {
            Faq faq = new Faq();
            faq.setPergunta(request.getParameter("faqPergunta" + i));
            faq.setResposta(request.getParameter("faqResposta" + i));
            faqs.add(faq);
        }
        return faqs;
    }

    @GetMapping("/cadastro")
    public ModelAndView produtoCadastro() {
        return new ModelAndView("../static/produtos/cadastro");
    }

    @GetMapping("/estoque")
    public ModelAndView produtoEstoque() {
        return new ModelAndView("../static/produtos/estoque");
    }

    @GetMapping("/estoquista")
    public ModelAndView produtoEstoquista() {
        return new ModelAndView("../static/produtos/estoquista");
    }

    @GetMapping("/detalhes")
    public ModelAndView index() {
        return new ModelAndView("../static/produtos/detalhes");
    }


}
