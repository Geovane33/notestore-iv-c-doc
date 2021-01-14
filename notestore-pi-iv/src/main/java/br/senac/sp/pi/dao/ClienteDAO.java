/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.senac.sp.pi.dao;

import br.senac.sp.pi.db.ConexaoDB;
import br.senac.sp.pi.entidade.Cliente;
import br.senac.sp.pi.entidade.Endereco;
import br.senac.sp.pi.utils.Filtro;
import br.senac.sp.pi.utils.FiltroProduto;
import br.senac.sp.pi.utils.FiltroUsuario;
import br.senac.sp.pi.utils.PerfilType;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Jonathan
 */
public class ClienteDAO implements DAO<Cliente> {

    @Override
    public List<Cliente> consultar(Filtro filtro) {
        FiltroUsuario filtroUsuario = (FiltroUsuario) filtro;
        ResultSet rs = null;
        PreparedStatement ps = null;
        Connection conexao = ConexaoDB.getConexao();
        List<Cliente> listaClientes = new ArrayList<>();
        try {
            ps = conexao.prepareStatement(getConsulta(filtroUsuario));
            rs = ps.executeQuery();
            while (rs.next()) {
                Cliente cliente = new Cliente();
                cliente.setId(rs.getInt("id_cliente"));
                cliente.setNome(rs.getString("nome_cliente"));
                cliente.setSenha(rs.getString("senha_cliente"));
                cliente.setSobrenome(rs.getString("sobrenome_cliente"));
                cliente.setCpf(rs.getString("cpf_cliente"));
                cliente.setDataNascimento(rs.getDate("data_nascimento_cliente").toLocalDate());
                cliente.setSexo(rs.getString("sexo_cliente"));
                cliente.setEmail(rs.getString("email_cliente"));
                cliente.setTelefone(rs.getString("telefone_cliente"));
                cliente.setPerfil(PerfilType.valueOf(rs.getString("perfil_cliente")));
                listaClientes.add(cliente);
            }
        } catch (SQLException ex) {
            System.out.println("Erro ao consultar cliente");
            System.out.println("SQLException: " + ex.getMessage());
            System.out.println("SQLState: " + ex.getSQLState());
            System.out.println("VendorError: " + ex.getErrorCode());
            throw new RuntimeException("Erro ao consultar cliente");
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
        return listaClientes;
    }

    private String getConsulta(FiltroUsuario filtro) {
        if (filtro.withCpf() && filtro.AndDeleted()) {
            return "SELECT * FROM clientes  WHERE  cpf_cliente like '%" + filtro.getCpf() + "%';";
        }
        if (filtro.withID() && filtro.AndDeleted()) {
            return "SELECT * FROM clientes  WHERE id_cliente like '%" + filtro.getId() + "%';";
        }
        if (filtro.withCpf()) {
            return "SELECT * FROM clientes WHERE deleted = FALSE AND cpf_cliente like '%" + filtro.getCpf() + "%';";
        }
        if (filtro.withNome()) {
            return "SELECT * FROM clientes WHERE deleted = FALSE AND nome_cliente like '%" + filtro.getNome() + "%';";
        }
        if (filtro.withEmail()) {
            return "SELECT * FROM clientes WHERE deleted = FALSE AND email_cliente like '%" + filtro.getEmail() + "%';";
        }
        return "SELECT * FROM clientes  WHERE deleted = FALSE;";
    }

    @Override
    public boolean salvar(Cliente cliente) {
        Connection conexao = null;
        PreparedStatement ps = null;
        try {
            conexao = ConexaoDB.getConexao();
            String sql = "INSERT INTO clientes VALUES"
                    + "(default,?, ?, ?, ?, ?, ?, ?, ?, ?, default)";
            ps = conexao.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, cliente.getNome());
            ps.setString(2, cliente.getSobrenome());
            ps.setString(3, cliente.getCpf());
            ps.setString(4, cliente.getEmail());
            ps.setString(5, cliente.encodeSenha(cliente.getSenha()));
            ps.setDate(6, Date.valueOf(cliente.getDataNascimento()));
            ps.setString(7, cliente.getSexo());
            ps.setString(8, cliente.getTelefone());
            ps.setString(9, cliente.getPerfil().name());
            ps.executeUpdate();
            Endereco endereco = new Endereco();
            endereco.setEmailCliente(cliente.getEmail());
            endereco.setNome("Principal");
            endereco.setCep(cliente.getCep());
            endereco.setRua(cliente.getRua());
            endereco.setNumero(cliente.getNumero());
            endereco.setBairro(cliente.getBairro());
            endereco.setEstado(cliente.getEstado());
            endereco.setCidade(cliente.getCidade());
            EnderecoDAO enderecoDAO = new EnderecoDAO();
            return enderecoDAO.salvar(endereco);
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
            System.out.println("Erro ao salvar cliente");
            System.out.println("SQLException: " + ex.getMessage());
            System.out.println("SQLState: " + ex.getSQLState());
            System.out.println("VendorError: " + ex.getErrorCode());
            throw new RuntimeException("Erro ao salvar cliente");
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
    public boolean atualizar(Cliente cliente) {
        Connection conexao = null;
        PreparedStatement ps = null;
        boolean ok = false;

        try {
            conexao = ConexaoDB.getConexao();

            ps = conexao.prepareStatement("UPDATE clientes SET "
                            + "nome_cliente = ?, sobrenome_cliente = ?, "
                            + "data_nascimento_cliente = ?, sexo_cliente = ?,"
                            + "telefone_cliente = ?"
                            + " WHERE email_cliente = ?;",
                    Statement.RETURN_GENERATED_KEYS);  //Caso queira retornar o ID
            //Adiciono os parâmetros ao meu comando SQL
            ps.setString(1, cliente.getNome());
            ps.setString(2, cliente.getSobrenome());
            ps.setDate(3, Date.valueOf(cliente.getDataNascimento()));
            ps.setString(4, cliente.getSexo());
            ps.setString(5, cliente.getTelefone());
            ps.setString(6, cliente.getEmail());
            return ps.executeUpdate() > 0;
        } catch (SQLException ex) {
            System.out.println("Erro ao atualizar cliente");
            System.out.println("SQLException: " + ex.getMessage());
            System.out.println("SQLState: " + ex.getSQLState());
            System.out.println("VendorError: " + ex.getErrorCode());
            throw new RuntimeException("Erro ao atualizar cliente");
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

    public boolean atualizarSenha(Cliente cliente) {
        Connection conexao = null;
        PreparedStatement ps = null;
        boolean ok = false;

        try {
            conexao = ConexaoDB.getConexao();
            ps = conexao.prepareStatement("UPDATE clientes SET "
                            + "senha_cliente = ?"
                            + " WHERE email_cliente = ?;",
                    Statement.RETURN_GENERATED_KEYS);  //Caso queira retornar o ID
            //Adiciono os parâmetros ao meu comando SQL
            ps.setString(1, cliente.encodeSenha(cliente.getSenha()));
            ps.setString(2, cliente.getEmail());
            return ps.executeUpdate() > 0;
        } catch (SQLException ex) {
            System.out.println("Erro ao atualizar senha do cliente");
            System.out.println("SQLException: " + ex.getMessage());
            System.out.println("SQLState: " + ex.getSQLState());
            System.out.println("VendorError: " + ex.getErrorCode());
            throw new RuntimeException("Erro ao atualizar senha do cliente");
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
    public boolean excluir(int id) {
        Connection conexao = null;
        PreparedStatement ps = null;
        conexao = ConexaoDB.getConexao();
        try {
            FiltroProduto filtro = new FiltroProduto();
            filtro.setId(String.valueOf(id));
            List<Cliente> clientes = this.consultar(filtro);
            Cliente cliente;
            if (clientes.size() == 1) {
                cliente = clientes.get(0);
            } else {
                return false;
            }
            ps = conexao.prepareStatement("UPDATE clientes SET deleted = TRUE WHERE"
                    + " id_cliente = ?");
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException ex) {
            System.out.println("Erro ao excluir cliente");
            System.out.println("SQLException: " + ex.getMessage());
            System.out.println("SQLState: " + ex.getSQLState());
            System.out.println("VendorError: " + ex.getErrorCode());
            throw new RuntimeException("Erro ao excluir cliente");
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
