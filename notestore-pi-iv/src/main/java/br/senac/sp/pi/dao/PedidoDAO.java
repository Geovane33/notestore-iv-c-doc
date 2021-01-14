package br.senac.sp.pi.dao;

import br.senac.sp.pi.db.ConexaoDB;
import br.senac.sp.pi.entidade.*;
import br.senac.sp.pi.utils.Filtro;
import br.senac.sp.pi.utils.FiltroProduto;
import br.senac.sp.pi.utils.FiltroUsuario;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class PedidoDAO {

    public List<Pedido> consultar(Filtro filtro) {
        ResultSet rs = null;
        FiltroUsuario filtroUsuario = (FiltroUsuario) filtro;
        PreparedStatement ps;
        Connection conexao = ConexaoDB.getConexao();
        List<Pedido> listaProdutoCarrinhos = new ArrayList<>();
        try {
            if (filtroUsuario.withEmail()) {
                ps = conexao.prepareStatement("SELECT * FROM pedidos WHERE email_cliente like '%" + filtroUsuario.getEmail() + "%';");
            } else {
                ps = conexao.prepareStatement("SELECT * FROM pedidos");
            }
            rs = ps.executeQuery();
            while (rs.next()) {
                Pedido pedido = new Pedido();
                pedido.setIdPedido(rs.getInt("id_pedido"));
                pedido.setNumeroPedido(rs.getInt("numero_pedido"));
                pedido.setEmailCliente(rs.getString("email_cliente"));
                pedido.setData(rs.getTimestamp("data_pedido").toLocalDateTime().minusHours(6));
                pedido.setPrecoFrete(rs.getDouble("frete_pedido"));
                pedido.setPrecoTotal(rs.getDouble("preco_total_pedido"));
                pedido.setQtdParcelas(rs.getInt("qtd_parcelas_pedido"));
                pedido.setStatus(rs.getString("status_pedido"));
                EnderecoDAO enderecoDAO = new EnderecoDAO();
                filtroUsuario.setEmail(pedido.getEmailCliente());
                int idEnd = rs.getInt("id_endereco");
                List<Endereco> enderecos = enderecoDAO.consultar(filtroUsuario);
                pedido.setEndereco(enderecos.stream().filter(end -> end.getId() == idEnd)
                        .collect(Collectors.toList()).get(0));
                pedido.setFormaPagamento(rs.getString("forma_pagamento_pedido"));
                pedido.setProdutosCarrinho(this.getProdutosPedido(rs.getInt("id_pedido")));
                listaProdutoCarrinhos.add(pedido);
            }
        } catch (SQLException ex) {
            System.out.println("Erro ao consultar carrinho");
            System.out.println("SQLException: " + ex.getMessage());
            System.out.println("SQLState: " + ex.getSQLState());
            System.out.println("VendorError: " + ex.getErrorCode());
            throw new RuntimeException("Erro ao consultar carrinho");
        } finally {
            //Libero os recursos da memória
            try {
                if (rs != null) {
                    rs.close();
                }
                ConexaoDB.fecharConexao(conexao);

            } catch (SQLException ex) {
                System.out.println("Erro ao fechar conexãoDB");
                System.out.println("SQLException: " + ex.getMessage());
                System.out.println("SQLState: " + ex.getSQLState());
                System.out.println("VendorError: " + ex.getErrorCode());
                throw new RuntimeException("Erro ao fechar conexãoDB");
            }

        }
        return listaProdutoCarrinhos;
    }

    private List<ProdutosCarrinho> getProdutosPedido(int id) {
        ResultSet rs = null;
        PreparedStatement ps;
        Connection conexao = ConexaoDB.getConexao();
        List<ProdutosCarrinho> listaProdutoCarrinhos = new ArrayList<>();
        try {
            ps = conexao.prepareStatement("SELECT * FROM produtos_pedido where id_pedido =" + id);
            rs = ps.executeQuery();
            while (rs.next()) {
                ProdutosCarrinho produtosPedido = new ProdutosCarrinho();
                produtosPedido.setEmailCliente(rs.getString("email_cliente"));
                produtosPedido.setPrecoCompra(rs.getInt("preco_produto"));
                produtosPedido.setQuantidadeCompra(rs.getInt("quantidade_produto"));
                ProdutoDAO produtoDAO = new ProdutoDAO();
                Filtro filtroProduto = new FiltroProduto();
                filtroProduto.setId(rs.getInt("id_produto") + "");
                produtosPedido.setProduto(produtoDAO.consultar(filtroProduto).get(0));
                listaProdutoCarrinhos.add(produtosPedido);
            }
        } catch (SQLException ex) {
            System.out.println("Erro ao consultar carrinho");
            System.out.println("SQLException: " + ex.getMessage());
            System.out.println("SQLState: " + ex.getSQLState());
            System.out.println("VendorError: " + ex.getErrorCode());
            throw new RuntimeException("Erro ao consultar carrinho");
        } finally {
            //Libero os recursos da memória
            try {
                if (rs != null) {
                    rs.close();
                }
                ConexaoDB.fecharConexao(conexao);

            } catch (SQLException ex) {
                System.out.println("Erro ao fechar conexãoDB");
                System.out.println("SQLException: " + ex.getMessage());
                System.out.println("SQLState: " + ex.getSQLState());
                System.out.println("VendorError: " + ex.getErrorCode());
                throw new RuntimeException("Erro ao fechar conexãoDB");
            }

        }
        return listaProdutoCarrinhos;
    }

    public boolean salvar(Pedido pedido) {
        Connection conexao = null;
        PreparedStatement ps = null;
        try {
            conexao = ConexaoDB.getConexao();
            String sql = "INSERT INTO pedidos VALUES"
                    + "(default,?,?, default, ?,?,?,?,default,?)";
            ps = conexao.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1, pedido.getNumeroPedido());
            ps.setString(2, pedido.getEmailCliente());
            ps.setInt(3, pedido.getEndereco().getId());
            ps.setDouble(4, pedido.getPrecoTotal());
            ps.setDouble(5, pedido.getPrecoFrete());
            ps.setInt(6, pedido.getQtdParcelas());
            ps.setString(7, pedido.getFormaPagamento() == null ? "Boleto" : pedido.getFormaPagamento());
            ps.executeUpdate();
            ResultSet generatedKeys = ps.getGeneratedKeys();
            List<ProdutosCarrinho> carrinho = pedido.getProdutosCarrinho();
            if (generatedKeys.next()) {
                pedido.setIdPedido(generatedKeys.getInt(1));
                for (ProdutosCarrinho prodCar : carrinho) {
                    prodCar.setIdPedido(pedido.getIdPedido());
                    this.salvarProdutosPedido(prodCar);
                }
                CarrinhoDAO carrinhoDAO = new CarrinhoDAO();
                carrinhoDAO.excluirPorEmail(pedido.getEmailCliente());
            }
            return true;
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
            System.out.println("Erro ao salvar pedido");
            System.out.println("SQLException: " + ex.getMessage());
            System.out.println("SQLState: " + ex.getSQLState());
            System.out.println("VendorError: " + ex.getErrorCode());
            throw new RuntimeException("Erro ao salvar pedido");
        } finally {
            //Libera os recursos da memória
            try {
                if (ps != null) {
                    ps.close();
                }
                ConexaoDB.fecharConexao(conexao);
            } catch (SQLException ex) {
                System.out.println("Erro ao fechar conexãoDB");
                System.out.println("SQLException: " + ex.getMessage());
                System.out.println("SQLState: " + ex.getSQLState());
                System.out.println("VendorError: " + ex.getErrorCode());
                throw new RuntimeException("Erro ao fechar conexãoDB");
            }

        }
    }


    private boolean salvarProdutosPedido(ProdutosCarrinho produtosPedido) {
        Connection conexao = null;
        PreparedStatement ps = null;
        try {
            conexao = ConexaoDB.getConexao();
            ps = conexao.prepareStatement("INSERT INTO produtos_pedido VALUES" +
                    "(default, ?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);  //Caso queira retornar o ID
            //Adiciono os parâmetros ao meu comando SQL
            ps.setString(1, produtosPedido.getEmailCliente());
            ps.setInt(2, produtosPedido.getProduto().getId());
            ps.setInt(3, produtosPedido.getQuantidadeCompra());
            ps.setDouble(4, produtosPedido.getProduto().getPreco());
            ps.setInt(5, produtosPedido.getIdPedido());
            return ps.executeUpdate() > 0;
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
            System.out.println("Erro ao salvar pedido");
            System.out.println("SQLException: " + ex.getMessage());
            System.out.println("SQLState: " + ex.getSQLState());
            System.out.println("VendorError: " + ex.getErrorCode());
            throw new RuntimeException("Erro ao salvar pedido");
        } finally {
            //Libera os recursos da memória
            try {
                if (ps != null) {
                    ps.close();
                }
                ConexaoDB.fecharConexao(conexao);
            } catch (SQLException ex) {
                System.out.println("Erro ao fechar conexãoDB");
                System.out.println("SQLException: " + ex.getMessage());
                System.out.println("SQLState: " + ex.getSQLState());
                System.out.println("VendorError: " + ex.getErrorCode());
                throw new RuntimeException("Erro ao fechar conexãoDB");
            }

        }
    }

    public boolean atualizar(Pedido pedido) {
        Connection conexao = null;
        PreparedStatement ps = null;
        try {
            //Tenta estabeler a conexão com o SGBD e cria comando a ser executado conexão
            //Obs: A classe ConexãoDB já carrega o Driver e define os parâmetros de conexão
            conexao = ConexaoDB.getConexao();
            ps = conexao.prepareStatement("UPDATE pedidos SET "
                            + "status_pedido = ? "
                            + "WHERE id_pedido = ?;",
                    Statement.RETURN_GENERATED_KEYS);  //Caso queira retornar o ID
            ps.setString(1, pedido.getStatus());
            ps.setInt(2, pedido.getIdPedido());
            return ps.executeUpdate() > 0;
        } catch (SQLException ex) {
            System.out.println("Erro ao atualizar pedido");
            System.out.println("SQLException: " + ex.getMessage());
            System.out.println("SQLState: " + ex.getSQLState());
            System.out.println("VendorError: " + ex.getErrorCode());
            throw new IllegalArgumentException("Erro ao atualizar pedido");
        } finally {
            //Libero os recursos da memória
            try {
                if (ps != null) {
                    ps.close();
                }

                ConexaoDB.fecharConexao(conexao);

            } catch (SQLException ex) {
                System.out.println("Erro ao fechar conexãoDB");
                System.out.println("SQLException: " + ex.getMessage());
                System.out.println("SQLState: " + ex.getSQLState());
                System.out.println("VendorError: " + ex.getErrorCode());
            }
        }
    }

//    @Override
//    public boolean excluir(int id) {
//        Connection conexao = null;
//        PreparedStatement ps = null;
//        conexao = ConexaoDB.getConexao();
//        try {
//            ps = conexao.prepareStatement("DELETE FROM carrinho WHERE"
//                    + " id_produto = ?");
//            ps.setInt(1, id);
//            return ps.executeUpdate() > 0;
//        } catch (SQLException ex) {
//            System.out.println("Erro ao excluir carrinho");
//            System.out.println("SQLException: " + ex.getMessage());
//            System.out.println("SQLState: " + ex.getSQLState());
//            System.out.println("VendorError: " + ex.getErrorCode());
//            throw new RuntimeException("Erro ao excluir carrinho");
//        } finally {
//            //Libero os recursos da memória
//            try {
//                if (ps != null) {
//                    ps.close();
//                }
//                ConexaoDB.fecharConexao(conexao);
//
//            } catch (SQLException ex) {
//                System.out.println("Erro ao fechar conexãoDB");
//                System.out.println("SQLException: " + ex.getMessage());
//                System.out.println("SQLState: " + ex.getSQLState());
//                System.out.println("VendorError: " + ex.getErrorCode());
//                throw new RuntimeException("Erro ao fechar conexãoDB");
//            }
//        }
//    }

//    @Override
//    public boolean atualizar(Pedido produtosCarrinho) {
//        Connection conexao = null;
//        PreparedStatement ps = null;
//        try {
//            conexao = ConexaoDB.getConexao();
//
//            ps = conexao.prepareStatement("UPDATE carrinho SET "
//                            + "id_produto = ?, quantidade_produto = ?, "
//                            + "preco_produto = ? WHERE id_carrinho = ?;",
//                    Statement.RETURN_GENERATED_KEYS);  //Caso queira retornar o ID
//            //Adiciono os parâmetros ao meu comando SQL
//            ps.setInt(1, produtosCarrinho.getProduto().getId());
//            ps.setInt(2, produtosCarrinho.getQuantidadeCompra());
//            ps.setDouble(3, produtosCarrinho.getPrecoCompra());
//            ps.setInt(4, produtosCarrinho.getIdCarrinho());
//            return ps.executeUpdate() > 0;
//        } catch (SQLException ex) {
//            System.out.println("Erro ao atualizar carrinho");
//            System.out.println("SQLException: " + ex.getMessage());
//            System.out.println("SQLState: " + ex.getSQLState());
//            System.out.println("VendorError: " + ex.getErrorCode());
//            throw new RuntimeException("Erro ao atualizar carrinho");
//        } finally {
//            //Libero os recursos da memória
//            try {
//                if (ps != null) {
//                    ps.close();
//                }
//                ConexaoDB.fecharConexao(conexao);
//
//            } catch (SQLException ex) {
//                System.out.println("Erro ao fechar conexãoDB");
//                System.out.println("SQLException: " + ex.getMessage());
//                System.out.println("SQLState: " + ex.getSQLState());
//                System.out.println("VendorError: " + ex.getErrorCode());
//                throw new RuntimeException("Erro ao fechar conexãoDB");
//            }
//        }
//    }
//    private String getConsulta(Filtro filtro) {
//        FiltroUsuario filtroUsuario = (FiltroUsuario) filtro;
//        if (filtro.withID() && filtroUsuario.withEmail()) {
//            return "SELECT * FROM carrinho WHERE id_produto = " + filtro.getId() + " AND email_cliente ='"+filtroUsuario.getEmail()+"';";
//        }
//        if(filtroUsuario.withEmail()){
//            return "SELECT * FROM carrinho WHERE email_cliente = '"+ filtroUsuario.getEmail() +"' AND quantidade_produto > 0;";
//        }
//        if (filtro.withID()) {
//            return "SELECT * FROM carrinho WHERE id_produto = " + filtro.getId() + ";";
//        }
//        return "SELECT * FROM carrinho";
//    }

}
