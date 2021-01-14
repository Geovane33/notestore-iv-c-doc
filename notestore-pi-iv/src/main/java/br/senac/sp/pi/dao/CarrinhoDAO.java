package br.senac.sp.pi.dao;

import br.senac.sp.pi.db.ConexaoDB;
import br.senac.sp.pi.entidade.ProdutosCarrinho;
import br.senac.sp.pi.utils.Filtro;
import br.senac.sp.pi.utils.FiltroProduto;
import br.senac.sp.pi.utils.FiltroUsuario;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CarrinhoDAO implements DAO<ProdutosCarrinho> {

    @Override
    public List<ProdutosCarrinho> consultar(Filtro filtro) {
        ResultSet rs = null;
        PreparedStatement ps;
        Connection conexao = ConexaoDB.getConexao();
        List<ProdutosCarrinho> listaProdutoCarrinhos = new ArrayList<>();
        try {
            ps = conexao.prepareStatement(getConsulta(filtro));// WHERE"); //email_cliente like '%" + filtroUsuario.getEmail() + "%';");
            rs = ps.executeQuery();
            while (rs.next()) {
                ProdutosCarrinho produtosCarrinho = new ProdutosCarrinho();
                produtosCarrinho.setIdCarrinho(rs.getInt("id_carrinho"));
                produtosCarrinho.setEmailCliente(rs.getString("email_cliente"));
                produtosCarrinho.setPrecoCompra(rs.getInt("preco_produto"));
                produtosCarrinho.setQuantidadeCompra(rs.getInt("quantidade_produto"));
                ProdutoDAO produtoDAO = new ProdutoDAO();
                Filtro filtroProduto = new FiltroProduto();
                filtroProduto.setId(rs.getInt("id_produto") + "");
                produtosCarrinho.setProduto(produtoDAO.consultar(filtroProduto).get(0));
                listaProdutoCarrinhos.add(produtosCarrinho);
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

    @Override
    public boolean salvar(ProdutosCarrinho produtosCarrinho) {
        Connection conexao = null;
        PreparedStatement ps = null;
        try {
            conexao = ConexaoDB.getConexao();
            String sql = "INSERT INTO carrinho VALUES"
                    + "(default,?, ?, ?, ?)";
            ps = conexao.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, produtosCarrinho.getEmailCliente());
            ps.setInt(2, produtosCarrinho.getProduto().getId());
            ps.setInt(3, produtosCarrinho.getQuantidadeCompra());
            ps.setDouble(4, produtosCarrinho.getProduto().getPreco());
            return ps.executeUpdate() > 0;
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
            System.out.println("Erro ao salvar carrinho");
            System.out.println("SQLException: " + ex.getMessage());
            System.out.println("SQLState: " + ex.getSQLState());
            System.out.println("VendorError: " + ex.getErrorCode());
            throw new RuntimeException("Erro ao salvar carrinho");
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

    @Override
    public boolean excluir(int id) {
        Connection conexao = null;
        PreparedStatement ps = null;
        conexao = ConexaoDB.getConexao();
        try {
            ps = conexao.prepareStatement("DELETE FROM carrinho WHERE"
                    + " id_produto = ?");
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException ex) {
            System.out.println("Erro ao excluir carrinho");
            System.out.println("SQLException: " + ex.getMessage());
            System.out.println("SQLState: " + ex.getSQLState());
            System.out.println("VendorError: " + ex.getErrorCode());
            throw new RuntimeException("Erro ao excluir carrinho");
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
                throw new RuntimeException("Erro ao fechar conexãoDB");
            }
        }
    }

    public boolean excluirPorEmail(String email) {
        Connection conexao = null;
        PreparedStatement ps = null;
        conexao = ConexaoDB.getConexao();
        try {
            ps = conexao.prepareStatement("DELETE FROM carrinho WHERE"
                    + " email_cliente = ?");
            ps.setString(1, email);
            return ps.executeUpdate() > 0;
        } catch (SQLException ex) {
            System.out.println("Erro ao excluir carrinho");
            System.out.println("SQLException: " + ex.getMessage());
            System.out.println("SQLState: " + ex.getSQLState());
            System.out.println("VendorError: " + ex.getErrorCode());
            throw new RuntimeException("Erro ao excluir carrinho");
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
                throw new RuntimeException("Erro ao fechar conexãoDB");
            }
        }
    }

    @Override
    public boolean atualizar(ProdutosCarrinho produtosCarrinho) {
        Connection conexao = null;
        PreparedStatement ps = null;
        try {
            conexao = ConexaoDB.getConexao();

            ps = conexao.prepareStatement("UPDATE carrinho SET " +
                            " quantidade_produto = ?, preco_produto = ? " +
                            " WHERE id_produto = ?;",
                    Statement.RETURN_GENERATED_KEYS);  //Caso queira retornar o ID
            //Adiciono os parâmetros ao meu comando SQL
            ps.setInt(1, produtosCarrinho.getQuantidadeCompra());
            ps.setDouble(2, produtosCarrinho.getPrecoCompra());
            ps.setInt(3, produtosCarrinho.getProduto().getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException ex) {
            System.out.println("Erro ao atualizar carrinho");
            System.out.println("SQLException: " + ex.getMessage());
            System.out.println("SQLState: " + ex.getSQLState());
            System.out.println("VendorError: " + ex.getErrorCode());
            throw new RuntimeException("Erro ao atualizar carrinho");
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
                throw new RuntimeException("Erro ao fechar conexãoDB");
            }
        }
    }

    private String getConsulta(Filtro filtro) {
        FiltroUsuario filtroUsuario = (FiltroUsuario) filtro;
        if (filtro.withID() && filtroUsuario.withEmail()) {
            return "SELECT * FROM carrinho WHERE id_produto = " + filtro.getId() + " AND email_cliente ='" + filtroUsuario.getEmail() + "';";
        }
        if (filtroUsuario.withEmail()) {
            return "SELECT * FROM carrinho WHERE email_cliente = '" + filtroUsuario.getEmail() + "' AND quantidade_produto > 0;";
        }
        if (filtro.withID()) {
            return "SELECT * FROM carrinho WHERE id_produto = " + filtro.getId() + ";";
        }

        return "SELECT * FROM carrinho";
    }

}
