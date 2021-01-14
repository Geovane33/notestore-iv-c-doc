package br.senac.sp.pi.filter;

import br.senac.sp.pi.entidade.Usuario;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class AutorizacaoFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        HttpSession sessao = httpRequest.getSession();
        Usuario usuario;
        if (sessao.getAttribute("usuario") == null) {
            if (httpRequest.getRequestURI().contains("pedido/endereco")) {
                httpResponse.sendRedirect(httpRequest.getContextPath() + "/login");
            } else if (getAcessoTotal(httpRequest.getRequestURI()) || cadastroCliente(httpRequest)) {
                chain.doFilter(request, response);
            } else {
                httpResponse.sendRedirect(httpRequest.getContextPath() + "/index");
            }
            return;
        }

        usuario = (Usuario) sessao.getAttribute("usuario");
        if (usuario.isClient()) {
            if (getAcessoCliente(httpRequest.getRequestURI())) {
                chain.doFilter(request, response);
            } else {
                httpResponse.sendRedirect(httpRequest.getContextPath() + "/index");
            }
            return;
        }

        if (usuario.isEstoquista()) {
            if (httpRequest.getRequestURI().contains("cadastro.html")) {
                httpResponse.sendRedirect(httpRequest.getContextPath() + "/produtos/estoquista");
            } else if (getAcessoEstoquista(httpRequest.getRequestURI()) || httpRequest.getRequestURI().contains("nao-autorizado")) {
                chain.doFilter(request, response);
            } else if (httpRequest.getRequestURI().contains("estoquista")) {
                chain.doFilter(request, response);
            } else if (httpRequest.getRequestURI().contains("/login")) {
                httpResponse.sendRedirect(httpRequest.getContextPath() + "/produtos/estoque");
            } else {
                httpResponse.sendRedirect(httpRequest.getContextPath() + "/nao-autorizado");
            }
            return;
        }
        if (usuario.isAdmin() && !httpRequest.getRequestURI().contains("/login")) {
            chain.doFilter(request, response);
            return;
        } else {
            httpResponse.sendRedirect(httpRequest.getContextPath() + "/produtos/estoque");
        }

    }

    @Override
    public void destroy() {
    }

    @Override
    public void init(FilterConfig filterConfig) {
    }

    private boolean verificarAcesso(Usuario usuario, String urlAcessada) {
        if (usuario.isAdmin()) {
            return true;
        }
        if (usuario.isEstoquista() && getAcessoEstoquista(urlAcessada)) {
            return true;
        }
        if (usuario.isClient() && getAcessoCliente(urlAcessada)) {
            return true;
        }
        return false;
    }

    private boolean getAcessoCliente(String url) {
        List<String> urlsNaoPermitidas = new ArrayList<>();
        urlsNaoPermitidas.add("/login");

        List<String> urlsPermitidas = new ArrayList<>();
        urlsPermitidas.add("/produtos/detalhes");
        urlsPermitidas.add("/clientes");
        urlsPermitidas.add("/notestore-acesso");
        urlsPermitidas.add("/index");
        urlsPermitidas.add("/pedido");
        urlsPermitidas.add("/acesso");
        return urlsPermitidas.stream().filter(u -> url.contains(u))
                .collect(Collectors.toList()).size() > 0
                || getAcessoTotal(url)
                && urlsNaoPermitidas.stream().filter(u -> url.contains(u))
                .collect(Collectors.toList()).size() == 0;
    }

    private boolean getAcessoEstoquista(String url) {
        List<String> urlsNaoPermitidas = new ArrayList<>();
        urlsNaoPermitidas.add("/index");
        urlsNaoPermitidas.add("/funcionarios");
        urlsNaoPermitidas.add("/login");

        List<String> urlsPermitidas = new ArrayList<>();
        urlsPermitidas.add("/produtos");
        urlsPermitidas.add("/produtos/estoquista");
        urlsPermitidas.add("js");
        urlsPermitidas.add("css");
        urlsPermitidas.add("icon");
        urlsPermitidas.add("logo");
        urlsPermitidas.add("/notestore-acesso");
        urlsPermitidas.add("/clientes/pedidos");
        urlsPermitidas.add("/pedido");
        urlsPermitidas.add("/acesso");

        return urlsPermitidas.stream().filter(u -> url.contains(u))
                .collect(Collectors.toList()).size() > 0 && urlsNaoPermitidas.stream().filter(u -> url.contains(u))
                .collect(Collectors.toList()).size() == 0;
    }

    private boolean getAcessoTotal(String url) {
        List<String> urlsPermitidas = new ArrayList<>();
        urlsPermitidas.add("login");
        urlsPermitidas.add("index");
        urlsPermitidas.add("detalhes");
        urlsPermitidas.add("logout");
        urlsPermitidas.add("js");
        urlsPermitidas.add("css");
        urlsPermitidas.add("logo");
        urlsPermitidas.add("icons");
        urlsPermitidas.add("imgs");
        urlsPermitidas.add("clientes/cadastro");
        urlsPermitidas.add("carrinho");
        return urlsPermitidas.stream().filter(u -> url.contains(u))
                .collect(Collectors.toList()).size() > 0 || url.equals("/produtos");
    }

    private boolean cadastroCliente(HttpServletRequest httpRequest) {
        return httpRequest.getRequestURI().equals("/clientes") && httpRequest.getMethod().equals("POST");
    }

    private boolean getAcessoTotalForGet(String url) {
        List<String> urlsNaoPermitidas = new ArrayList<>();
        urlsNaoPermitidas.add("produtos/estoque");
        urlsNaoPermitidas.add("produtos/estoquista");
        urlsNaoPermitidas.add("produtos/cadastro");
        urlsNaoPermitidas.add("funcionarios");

        List<String> urlsPermitidas = new ArrayList<>();
        urlsPermitidas.add("notestore");
        urlsPermitidas.add("style.css");
        urlsPermitidas.add("index.html");
        urlsPermitidas.add("index.js");
        urlsPermitidas.add("favicon.icon");
        urlsPermitidas.add("produtos");
        urlsPermitidas.add("icons");
        urlsPermitidas.add("login");
        urlsPermitidas.add("logout");
        urlsPermitidas.add("/");
        return urlsPermitidas.stream().filter(u -> url.contains(u))
                .collect(Collectors.toList()).size() > 0 && urlsNaoPermitidas.stream().filter(u -> url.contains(u))
                .collect(Collectors.toList()).size() == 0;
    }

}
