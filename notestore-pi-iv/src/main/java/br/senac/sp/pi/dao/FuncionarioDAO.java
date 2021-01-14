package br.senac.sp.pi.dao;

import br.senac.sp.pi.db.ConexaoDB;
import br.senac.sp.pi.entidade.Funcionario;
import br.senac.sp.pi.utils.Filtro;
import br.senac.sp.pi.utils.FiltroUsuario;
import br.senac.sp.pi.utils.PerfilType;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class FuncionarioDAO implements DAO<Funcionario> {

    @Override
    public boolean salvar(Funcionario funcionario) {
        Connection conexao = null;
        PreparedStatement ps = null;
        try {
            conexao = ConexaoDB.getConexao();
            String sql = "INSERT INTO funcionarios VALUES"
                    + "(default, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, default)";
            ps = conexao.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, funcionario.getNome());
            ps.setString(2, funcionario.getSobrenome());
            ps.setString(3, funcionario.getCpf());
            ps.setString(4, funcionario.getEmail());
            ps.setString(5, funcionario.encodeSenha(funcionario.getSenha()));
            ps.setDate(6, Date.valueOf(funcionario.getDataNascimento()));
            ps.setString(7, funcionario.getSexo());
            ps.setString(8, funcionario.getTelefone());
            ps.setString(9, funcionario.getCep());
            ps.setString(10, funcionario.getRua());
            ps.setString(11, funcionario.getNumero());
            ps.setString(12, funcionario.getBairro());
            ps.setString(13, funcionario.getEstado());
            ps.setString(14, funcionario.getCidade());
            ps.setString(15, funcionario.getPerfil().name());
            return ps.executeUpdate() > 0;
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
            System.out.println("Erro ao salvar funcionario");
            System.out.println("SQLException: " + ex.getMessage());
            System.out.println("SQLState: " + ex.getSQLState());
            System.out.println("VendorError: " + ex.getErrorCode());
            throw new RuntimeException("Erro ao salvar funcionario");
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
    public List<Funcionario> consultar(Filtro filtro) {
        FiltroUsuario filtroUsuario = (FiltroUsuario) filtro;
        ResultSet rs = null;
        PreparedStatement ps;
        Connection conexao = ConexaoDB.getConexao();
        List<Funcionario> listaFuncionarios = new ArrayList<>();
        try {
            ps = conexao.prepareStatement(getConsulta(filtroUsuario));
            rs = ps.executeQuery();
            while (rs.next()) {
                Funcionario funcionario = new Funcionario();
                funcionario.setId(rs.getInt("id_funcionario"));
                funcionario.setNome(rs.getString("nome_funcionario"));
                funcionario.setSobrenome(rs.getString("sobrenome_funcionario"));
                funcionario.setCpf(rs.getString("cpf_funcionario"));
                funcionario.setEmail(rs.getString("email_funcionario"));
                funcionario.setSenha(rs.getString("senha_funcionario"));
                funcionario.setDataNascimento(rs.getDate("data_nascimento_funcionario").toLocalDate());
                funcionario.setSexo(rs.getString("sexo_funcionario"));
                funcionario.setTelefone(rs.getString("telefone_funcionario"));
                funcionario.setCep(rs.getString("cep_funcionario"));
                funcionario.setRua(rs.getString("rua_ou_avenida_funcionario"));
                funcionario.setNumero(rs.getString("numero_funcionario"));
                funcionario.setBairro(rs.getString("bairro_funcionario"));
                funcionario.setEstado(rs.getString("estado_funcionario"));
                funcionario.setCidade(rs.getString("cidade_funcionario"));
                funcionario.setPerfil(PerfilType.valueOf(rs.getString("perfil_funcionario")));
                listaFuncionarios.add(funcionario);
            }
        } catch (SQLException ex) {
            System.out.println("Erro ao consultar funcionario");
            System.out.println("SQLException: " + ex.getMessage());
            System.out.println("SQLState: " + ex.getSQLState());
            System.out.println("VendorError: " + ex.getErrorCode());
            throw new RuntimeException("Erro ao consultar funcionario");
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
        return listaFuncionarios;
    }

    private String getConsulta(FiltroUsuario filtro) {
        if (filtro.withCpf() && filtro.AndDeleted()) {
            return "SELECT * FROM funcionarios  WHERE  cpf_funcionario like '%" + filtro.getCpf() + "%';";
        }
        if (filtro.withID() && filtro.AndDeleted()) {
            return "SELECT * FROM funcionarios  WHERE id_funcionario like '%" + filtro.getId() + "%';";
        }
        if (filtro.withID()) {
            return "SELECT * FROM funcionarios  WHERE id_funcionario like '%" + filtro.getId() + "%' and deleted = FALSE;";
        }
        if (filtro.withCpf()) {
            return "SELECT * FROM funcionarios  WHERE  cpf_funcionario like '%" + filtro.getCpf() + "%' and deleted = FALSE;";
        }
        if (filtro.withEmail()) {
            return "SELECT * FROM funcionarios  WHERE email_funcionario like '%" + filtro.getEmail() + "%' and deleted = FALSE;";
        }

        return "SELECT * FROM funcionarios  WHERE  deleted = FALSE;";
    }


    @Override
    public boolean atualizar(Funcionario funcionario) {
        Connection conexao = null;
        PreparedStatement ps = null;
        try {
            conexao = ConexaoDB.getConexao();

            ps = conexao.prepareStatement("UPDATE funcionarios SET "
                            + "nome_funcionario = ?, sobrenome_funcionario = ?, "
                            + "cpf_funcionario = ?, senha_funcionario = ?,"
                            + "data_nascimento_funcionario = ?, sexo_funcionario = ?,"
                            + "telefone_funcionario = ?, "
                            + "cep_funcionario = ?, rua_ou_avenida_funcionario = ?, "
                            + "numero_funcionario = ?,bairro_funcionario = ?,"
                            + "estado_funcionario = ?, cidade_funcionario = ?,"
                            + "perfil_funcionario = ?, deleted = FALSE "
                            + "WHERE id_funcionario = ?;",
                    Statement.RETURN_GENERATED_KEYS);  //Caso queira retornar o ID
            //Adiciono os parâmetros ao meu comando SQL
            ps.setString(1, funcionario.getNome());
            ps.setString(2, funcionario.getSobrenome());
            ps.setString(3, funcionario.getCpf());
            ps.setString(4, funcionario.encodeSenha(funcionario.getSenha()));
            ps.setDate(5, Date.valueOf(funcionario.getDataNascimento()));
            ps.setString(6, funcionario.getSexo());
            ps.setString(7, funcionario.getTelefone());
            ps.setString(8, funcionario.getCep());
            ps.setString(9, funcionario.getRua());
            ps.setString(10, funcionario.getNumero());
            ps.setString(11, funcionario.getBairro());
            ps.setString(12, funcionario.getEstado());
            ps.setString(13, funcionario.getCidade());
            ps.setString(14, funcionario.getPerfil().name());
            ps.setInt(15, funcionario.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException ex) {
            System.out.println("Erro ao atualizar funcionario");
            System.out.println("SQLException: " + ex.getMessage());
            System.out.println("SQLState: " + ex.getSQLState());
            System.out.println("VendorError: " + ex.getErrorCode());
            throw new RuntimeException("Erro ao atualizar funcionario");
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
            FiltroUsuario filtroUsuario = new FiltroUsuario();
            filtroUsuario.setAndDeleted(true);
            filtroUsuario.setId(String.valueOf(id));
            List<Funcionario> funcionarios = this.consultar(filtroUsuario);
            Funcionario funcionario;
            if (funcionarios.size() == 1) {
                funcionario = funcionarios.get(0);
            } else {
                return false;
            }
            ps = conexao.prepareStatement("UPDATE funcionarios SET deleted = TRUE WHERE"
                    + " id_funcionario = ?");
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException ex) {
            System.out.println("Erro ao excluir funcionario");
            System.out.println("SQLException: " + ex.getMessage());
            System.out.println("SQLState: " + ex.getSQLState());
            System.out.println("VendorError: " + ex.getErrorCode());
            throw new RuntimeException("Erro ao excluir funcionario");
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
