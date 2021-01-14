package br.senac.sp.pi.dao;

import br.senac.sp.pi.db.ConexaoDB;
import br.senac.sp.pi.entidade.Endereco;
import br.senac.sp.pi.utils.Filtro;
import br.senac.sp.pi.utils.FiltroUsuario;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EnderecoDAO implements DAO<Endereco> {

    @Override
    public List<Endereco> consultar(Filtro filtro) {
        FiltroUsuario filtroUsuario = (FiltroUsuario) filtro;
        ResultSet rs = null;
        PreparedStatement ps = null;
        Connection conexao = ConexaoDB.getConexao();
        List<Endereco> listaEnderecos = new ArrayList<>();
        try {
            ps = conexao.prepareStatement("SELECT * FROM enderecos WHERE email_cliente like '%" + filtroUsuario.getEmail() + "%';");
            rs = ps.executeQuery();
            while (rs.next()) {
                Endereco endereco = new Endereco();
                endereco.setEmailCliente(rs.getString("email_cliente"));
                endereco.setId(rs.getInt("id_endereco"));
                endereco.setNome(rs.getString("nome_endereco"));
                endereco.setRua(rs.getString("rua_endereco"));
                endereco.setBairro(rs.getString("bairro_endereco"));
                endereco.setNumero(rs.getString("numero_endereco"));
                endereco.setCidade(rs.getString("cidade_endereco"));
                endereco.setEstado(rs.getString("estado_endereco"));
                endereco.setCep(rs.getString("cep_endereco"));
                listaEnderecos.add(endereco);
            }
        } catch (SQLException ex) {
            System.out.println("Erro ao consultar enderecos");
            System.out.println("SQLException: " + ex.getMessage());
            System.out.println("SQLState: " + ex.getSQLState());
            System.out.println("VendorError: " + ex.getErrorCode());
            throw new RuntimeException("Erro ao consultar enderecos");
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
        return listaEnderecos;
    }

    @Override
    public boolean salvar(Endereco endereco) {
        Connection conexao = null;
        PreparedStatement ps = null;
        try {
            conexao = ConexaoDB.getConexao();
            String sql = "INSERT INTO enderecos VALUES"
                    + "(default,?, ?, ?, ?, ?, ?, ?, ?)";
            ps = conexao.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, endereco.getEmailCliente());
            ps.setString(2, endereco.getNome());
            ps.setString(3, endereco.getCep());
            ps.setString(4, endereco.getRua());
            ps.setString(5, endereco.getNumero());
            ps.setString(6, endereco.getBairro());
            ps.setString(7, endereco.getEstado());
            ps.setString(8, endereco.getCidade());
            return ps.executeUpdate() > 0;
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
            System.out.println("Erro ao salvar endereco");
            System.out.println("SQLException: " + ex.getMessage());
            System.out.println("SQLState: " + ex.getSQLState());
            System.out.println("VendorError: " + ex.getErrorCode());
            throw new RuntimeException("Erro ao salvar endereco");
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
            ps = conexao.prepareStatement("DELETE FROM enderecos WHERE"
                    + " id_endereco = ?");
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException ex) {
            System.out.println("Erro ao excluir endereco");
            System.out.println("SQLException: " + ex.getMessage());
            System.out.println("SQLState: " + ex.getSQLState());
            System.out.println("VendorError: " + ex.getErrorCode());
            throw new RuntimeException("Erro ao excluir endereco");
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
    public boolean atualizar(Endereco endereco) {
        Connection conexao = null;
        PreparedStatement ps = null;
        try {
            conexao = ConexaoDB.getConexao();

            ps = conexao.prepareStatement("UPDATE enderecos SET "
                            + "nome_endereco = ?, cep_endereco = ?, "
                            + "rua_endereco = ?, numero_endereco = ?, "
                            + " bairro_endereco = ?,"
                            + "estado_endereco = ?, cidade_endereco = ? "
                            + " WHERE id_endereco = ?;",
                    Statement.RETURN_GENERATED_KEYS);  //Caso queira retornar o ID
            //Adiciono os parâmetros ao meu comando SQL
            ps.setString(1, endereco.getNome());
            ps.setString(2, endereco.getCep());
            ps.setString(3, endereco.getRua());
            ps.setString(4, endereco.getNumero());
            ps.setString(5, endereco.getBairro());
            ps.setString(6, endereco.getEstado());
            ps.setString(7, endereco.getCidade());
            ps.setInt(8, endereco.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException ex) {
            System.out.println("Erro ao atualizar endereco");
            System.out.println("SQLException: " + ex.getMessage());
            System.out.println("SQLState: " + ex.getSQLState());
            System.out.println("VendorError: " + ex.getErrorCode());
            throw new RuntimeException("Erro ao atualizar endereco");
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

}
