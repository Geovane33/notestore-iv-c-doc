//package br.senac.sp.pi.dao;
//
//import br.senac.sp.pi.db.ConexaoDB;
//import br.senac.sp.pi.entidade.Usuario;
//import br.senac.sp.pi.utils.Filtro;
//import br.senac.sp.pi.utils.FiltroUsuario;
//import br.senac.sp.pi.utils.PerfilType;
//
//import java.sql.*;
//import java.util.ArrayList;
//import java.util.List;
//
//public class UsuarioDAO implements DAO<Usuario>{
//
//    @Override
//    public boolean salvar(Usuario usuario) {
//        Connection conexao = null;
//        PreparedStatement ps = null;
//        try {
//            conexao = ConexaoDB.getConexao();
//            String sql = "INSERT INTO usuarios VALUES "
//                    + "(?, ?, ?, ?, ?, default)";
//            ps = conexao.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
//            ps.setInt(1, usuario.getId());
//            ps.setString(2, usuario.encodeSenha(usuario.getSenha()));
//            ps.setString(3, usuario.getPerfil().name());
//            ps.setString(4, usuario.getEmail());
//            ps.setString(5, usuario.getCpf());
//            return ps.executeUpdate() > 0;
//        } catch (SQLException ex) {
//            System.out.println(ex.getMessage());
//            System.out.println("Erro ao salvar usuario");
//            System.out.println("SQLException: " + ex.getMessage());
//            System.out.println("SQLState: " + ex.getSQLState());
//            System.out.println("VendorError: " + ex.getErrorCode());
//            throw new IllegalArgumentException("Erro ao salvar usuario");
//        } finally {
//            //Libera os recursos da memória
//            try {
//                if (ps != null) {
//                    ps.close();
//                }
//                ConexaoDB.fecharConexao(conexao);
//            } catch (SQLException ex) {
//                System.out.println("Erro ao fechar conexãoDB");
//                System.out.println("SQLException: " + ex.getMessage());
//                System.out.println("SQLState: " + ex.getSQLState());
//                System.out.println("VendorError: " + ex.getErrorCode());
//            }
//        }
//    }
//
//    @Override
//    public List<Usuario> consultar(Filtro filtro) {
//        FiltroUsuario filtroUsuario = (FiltroUsuario) filtro;
//        ResultSet rs = null;
//        PreparedStatement ps;
//        Connection conexao = ConexaoDB.getConexao();
//        List<Usuario> listaUsuarios = new ArrayList<>();
//        try {
//            ps = conexao.prepareStatement(getConsulta(filtroUsuario));
//            rs = ps.executeQuery();
//            while (rs.next()) {
//                Usuario usuario = new Usuario();
//                usuario.setId(rs.getInt("id_usuario"));
//                usuario.setSenha(rs.getString("senha_usuario"));
//                usuario.setPerfil(PerfilType.valueOf(rs.getString("perfil_usuario")));
//                usuario.setEmail(rs.getString("email_usuario"));
//                usuario.setCpf(rs.getString("cpf_usuario"));
//                listaUsuarios.add(usuario);
//            }
//        } catch (SQLException ex) {
//            System.out.println("Erro ao consultar usuario");
//            System.out.println("SQLException: " + ex.getMessage());
//            System.out.println("SQLState: " + ex.getSQLState());
//            System.out.println("VendorError: " + ex.getErrorCode());
//        } finally {
//            //Libero os recursos da memória
//            try {
//                if (rs != null) {
//                    rs.close();
//                }
//                ConexaoDB.fecharConexao(conexao);
//
//            } catch (SQLException ex) {
//                System.out.println("Erro ao fechar conexãoDB");
//                System.out.println("SQLException: " + ex.getMessage());
//                System.out.println("SQLState: " + ex.getSQLState());
//                System.out.println("VendorError: " + ex.getErrorCode());
//            }
//
//        }
//        return listaUsuarios;
//    }
//
//    @Override
//    public boolean atualizar(Usuario usuario) {
//        Connection conexao = null;
//        PreparedStatement ps = null;
//        try {
//            conexao = ConexaoDB.getConexao();
//
//            ps = conexao.prepareStatement("UPDATE usuarios SET "
//                            + "senha_usuario = ?, perfil_usuario = ?, cpf_usuario = ?"
//                            + "WHERE id_usuario = ?;",
//                    Statement.RETURN_GENERATED_KEYS);  //Caso queira retornar o ID
//            //Adiciono os parâmetros ao meu comando SQL
//            ps.setString(1,usuario.encodeSenha(usuario.getSenha()));
//            ps.setString(2, usuario.getPerfil().name());
//            ps.setString(3, usuario.getCpf());
//            ps.setInt(4, usuario.getId());
//            return ps.executeUpdate() > 0;
//        } catch (SQLException ex) {
//            System.out.println("Erro ao atualizar usuario");
//            System.out.println("SQLException: " + ex.getMessage());
//            System.out.println("SQLState: " + ex.getSQLState());
//            System.out.println("VendorError: " + ex.getErrorCode());
//            return false;
//        } finally {
//            //Libero os recursos da memória
//            try {
//                if (ps != null) {
//                    ps.close();
//                }
//
//                ConexaoDB.fecharConexao(conexao);
//            } catch (SQLException ex) {
//                System.out.println("Erro ao fechar conexãoDB");
//                System.out.println("SQLException: " + ex.getMessage());
//                System.out.println("SQLState: " + ex.getSQLState());
//                System.out.println("VendorError: " + ex.getErrorCode());
//            }
//        }
//    }
//
//    @Override
//    public boolean excluir(int id) {
//        Connection conexao = null;
//        PreparedStatement ps = null;
//        try {
//            conexao = ConexaoDB.getConexao();
//            ps = conexao.prepareStatement("UPDATE usuarios SET deleted = TRUE WHERE"
//                    + " id_usuario = ?");
//            ps.setInt(1, id);
//            return ps.executeUpdate() > 0;
//        } catch (SQLException ex) {
//            System.out.println("Erro ao excluir usuario");
//            System.out.println("SQLException: " + ex.getMessage());
//            System.out.println("SQLState: " + ex.getSQLState());
//            System.out.println("VendorError: " + ex.getErrorCode());
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
//
//            }
//        }
//        return false;
//    }
//
//    private String getConsulta(FiltroUsuario filtro) {
//        return "SELECT * FROM usuarios WHERE email_usuario like '%" + filtro.getEmail() + "%' and deleted = FALSE";
//        //' and cpf_usuario like '%" + filtro.getNome() + "%';";
//    }
//}
