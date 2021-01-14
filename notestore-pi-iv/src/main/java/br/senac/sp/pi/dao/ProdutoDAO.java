package br.senac.sp.pi.dao;

import br.senac.sp.pi.api.ImagemAPI;
import br.senac.sp.pi.db.ConexaoDB;
import br.senac.sp.pi.entidade.Faq;
import br.senac.sp.pi.entidade.Imagem;
import br.senac.sp.pi.entidade.Produto;
import br.senac.sp.pi.utils.AtualizarLinkImagens;
import br.senac.sp.pi.utils.Filtro;
import br.senac.sp.pi.utils.FiltroProduto;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.web.multipart.MultipartRequest;

public class ProdutoDAO implements DAO<Produto> {

    /**
     * Salvar produtos
     *
     * @param produto objeto produto para salvar
     * @return true: salvo com sucesso e false: erro ao salvar
     */
    @Override
    public boolean salvar(Produto produto) {
        boolean ok = false;
        Connection conexao = null;
        PreparedStatement ps = null;
        try {
            conexao = ConexaoDB.getConexao();
            String sql = "INSERT INTO produtos VALUES "
                    + "(default, ?, ?, ?, ?, ?, ?, ?, ?, default)";
            ps = conexao.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, produto.getNome());
            ps.setString(2, produto.getMarca());
            ps.setInt(3, produto.getQuantidade());
            ps.setDouble(4, produto.getPreco());
            ps.setDate(5, Date.valueOf(produto.getDataEntrada()));
            ps.setString(6, produto.getDescricao());
            ps.setString(7, produto.getPalavrasChaves());
            ps.setString(8, produto.getCategoria());
            ps.executeUpdate();
            ResultSet generatedKeys = ps.getGeneratedKeys();
            if (generatedKeys.next()) {
                produto.setId(generatedKeys.getInt(1));
                ok = salvarFaq(produto, conexao);
            }
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
            System.out.println("Erro ao salvar produto");
            System.out.println("SQLException: " + ex.getMessage());
            System.out.println("SQLState: " + ex.getSQLState());
            System.out.println("VendorError: " + ex.getErrorCode());
            throw new IllegalArgumentException("Erro ao salvar produto");
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
            }

        }
        return ok;
    }

    /**
     * metodo que realiza pesquisa de produto por nome
     *
     * @param filtro recebe dados da consulta
     * @return listaProdutos
     */
    @Override
    public List<Produto> consultar(Filtro filtro) {
        FiltroProduto filtroProduto = (FiltroProduto) filtro;

        ResultSet rs = null;
        PreparedStatement ps = null;
        Connection conexao = ConexaoDB.getConexao();
        List<Produto> listaProdutos = new ArrayList<>();
        Map<Integer, List<Imagem>> mapListImagens = getMapListImagens(conexao);
        Map<Integer, List<Faq>> mapListFaqs = getMapListFaqs(conexao);
        try {
            ps = conexao.prepareStatement(getConsulta(filtroProduto));
            rs = ps.executeQuery();
            while (rs.next()) {
                Produto produto = new Produto();
                produto.setId(rs.getInt("id_produto"));
                produto.setNome(rs.getString("nome_produto"));
                produto.setMarca(rs.getString("marca_produto"));
                produto.setQuantidade(rs.getInt("quantidade_produto"));
                produto.setPreco(rs.getInt("preco_produto"));
                produto.setDataEntrada(rs.getDate("data_entrada").toLocalDate());
                produto.setDescricao(rs.getString("desc_produto"));
                produto.setPalavrasChaves(rs.getString("palavra_chave_produto"));
                produto.setCategoria(rs.getString("categoria_produto"));
                produto.setFaqs(mapListFaqs.get(produto.getId()) == null ? new ArrayList() : mapListFaqs.get(produto.getId()));
                produto.setImagens(mapListImagens.get(produto.getId()));
                listaProdutos.add(produto);
            }
        } catch (SQLException ex) {
            System.out.println("Erro ao consultar produto");
            System.out.println("SQLException: " + ex.getMessage());
            System.out.println("SQLState: " + ex.getSQLState());
            System.out.println("VendorError: " + ex.getErrorCode());
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
            }

        }
        return listaProdutos;
    }

    /**
     * Atualizar produto
     *
     * @param produto recebe por parametro um objeto produto com os dados a
     *                serem atualizados
     * @return true caso o produto seja atulizado com sucesso e false caso de
     * erro ao atualizar o produto
     */
    @Override
    public boolean atualizar(Produto produto) {
        Connection conexao = null;
        PreparedStatement ps = null;
        boolean ok = false;
        try {
            //Tenta estabeler a conexão com o SGBD e cria comando a ser executado conexão
            //Obs: A classe ConexãoDB já carrega o Driver e define os parâmetros de conexão
            conexao = ConexaoDB.getConexao();
            ps = conexao.prepareStatement("UPDATE produtos SET "
                            + "nome_produto = ?, marca_produto = ?, "
                            + "quantidade_produto = ?, preco_produto = ?, "
                            + " data_entrada = ?, desc_produto = ?, "
                            + "palavra_chave_produto = ?, categoria_produto = ?,"
                            + "deleted = DEFAULT"
                            + " WHERE id_produto = ?;",
                    Statement.RETURN_GENERATED_KEYS);  //Caso queira retornar o ID do produto
            //Adiciono os parâmetros ao meu comando SQL
            ps.setString(1, produto.getNome());
            ps.setString(2, produto.getMarca());
            ps.setInt(3, produto.getQuantidade());
            ps.setDouble(4, produto.getPreco());
            ps.setDate(5, Date.valueOf(produto.getDataEntrada()));
            ps.setString(6, produto.getDescricao());
            ps.setString(7, produto.getPalavrasChaves());
            ps.setString(8, produto.getCategoria());
            ps.setInt(9, produto.getId());
            if (ps.executeUpdate() > 0) {
                ok = excluirFaq(produto, conexao);
                if (!ok) throw new IllegalArgumentException("Erro ao excluir FAQ");
                ok = salvarFaq(produto, conexao);
                if (!ok) throw new IllegalArgumentException("Erro ao salvar FAQ");
            }
        } catch (SQLException ex) {
            System.out.println("Erro ao atualizar produto");
            System.out.println("SQLException: " + ex.getMessage());
            System.out.println("SQLState: " + ex.getSQLState());
            System.out.println("VendorError: " + ex.getErrorCode());
            throw new IllegalArgumentException("Erro ao atualizar produto");
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
        return ok;
    }

    /**
     * Excluir produto
     *
     * @param id identificação do produto a ser excluido
     * @return boolean
     */
    @Override
    public boolean excluir(int id) {
        Connection conexao = null;
        PreparedStatement ps = null;
        conexao = ConexaoDB.getConexao();
        excluirImagensPorIDProd(id);
        ImagemAPI imagemAPI = new ImagemAPI();
        imagemAPI.deleteImagemNuvem("/" + id);
        try {
            ps = conexao.prepareStatement("DELETE FROM produtos WHERE id_produto = ?");
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException ex) {
            System.out.println("Erro ao excluir produto");
            System.out.println("SQLException: " + ex.getMessage());
            System.out.println("SQLState: " + ex.getSQLState());
            System.out.println("VendorError: " + ex.getErrorCode());
            throw new IllegalArgumentException("Erro ao excluir produto");
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

    /**
     * Ao desativar um produto a coluna deleted terá o valor 1. produtos com a
     * deleted em 0 está ativo produtos com a coluna em 1 está inativo
     *
     * @param id identificação do produto a ser desativado
     * @return boolean
     */
    public boolean desativar(int id) {
        Connection conexao = null;
        PreparedStatement ps = null;
        conexao = ConexaoDB.getConexao();
        try {
            ps = conexao.prepareStatement("UPDATE produtos SET deleted = TRUE WHERE"
                    + " id_produto = ?");
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException ex) {
            System.out.println("Erro ao desativar produto");
            System.out.println("SQLException: " + ex.getMessage());
            System.out.println("SQLState: " + ex.getSQLState());
            System.out.println("VendorError: " + ex.getErrorCode());
            throw new IllegalArgumentException("Erro ao desativar produto");
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

    public boolean atualizarEstoque(Produto produto) {
        Connection conexao = null;
        PreparedStatement ps = null;
        try {
            //Tenta estabeler a conexão com o SGBD e cria comando a ser executado conexão
            //Obs: A classe ConexãoDB já carrega o Driver e define os parâmetros de conexão
            conexao = ConexaoDB.getConexao();
            ps = conexao.prepareStatement("UPDATE produtos SET "
                            + "quantidade_produto = ?, "
                            + "deleted = DEFAULT"
                            + " WHERE id_produto = ?;",
                    Statement.RETURN_GENERATED_KEYS);  //Caso queira retornar o ID do produto
            //Adiciono os parâmetros ao meu comando SQL
            ps.setInt(1, produto.getQuantidade());
            ps.setInt(2, produto.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException ex) {
            System.out.println("Erro ao atualizar quantidade produto");
            System.out.println("SQLException: " + ex.getMessage());
            System.out.println("SQLState: " + ex.getSQLState());
            System.out.println("VendorError: " + ex.getErrorCode());
            throw new IllegalArgumentException("Erro ao quantidade produto");
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

    /**
     * Excluir imagem do produto
     *
     * @param imagemPath caminho da imagem a ser excluida
     * @return boolean
     */
    public boolean excluirImagens(String imagemPath) {
        PreparedStatement ps = null;
        Connection conexao = null;
        try {
            conexao = ConexaoDB.getConexao();
            ps = conexao.prepareStatement("DELETE FROM imagens WHERE"
                    + " path_imagem = ?");
            ps.setString(1, imagemPath);
            return ps.executeUpdate() > 0;
        } catch (SQLException ex) {
            System.out.println("Erro ao excluir produto");
            System.out.println("SQLException: " + ex.getMessage());
            System.out.println("SQLState: " + ex.getSQLState());
            System.out.println("VendorError: " + ex.getErrorCode());
            throw new IllegalArgumentException("Erro ao excluir imagens do banco de dados");
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

    /**
     * Excluir excluir todas imagems do produto
     *
     * @param idProd id do produto para excluir todas as imagens correspondentes
     * @return boolean
     */
    private boolean excluirImagensPorIDProd(int idProd) {
        PreparedStatement ps = null;
        Connection conexao = null;
        try {
            conexao = ConexaoDB.getConexao();
            ps = conexao.prepareStatement("DELETE FROM imagens WHERE"
                    + " id_produto = ?");
            ps.setInt(1, idProd);
            return ps.executeUpdate() > 0;
        } catch (SQLException ex) {
            System.out.println("Erro ao excluir todas imagens");
            System.out.println("SQLException: " + ex.getMessage());
            System.out.println("SQLState: " + ex.getSQLState());
            System.out.println("VendorError: " + ex.getErrorCode());
            throw new IllegalArgumentException("Erro ao excluir todas as imagem");
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

    public boolean salvarImagens(Produto produto, MultipartRequest multpartImagens) {
        PreparedStatement ps = null;
        Connection conexao = null;
        boolean ok = false;
        try {
            conexao = ConexaoDB.getConexao();
            produto.setImagens(uploadImagens(multpartImagens, produto));
            for (Imagem pathImg : produto.getImagens()) {
                String sql = "INSERT INTO imagens VALUES (default, ?, ?, ?)";
                ps = conexao.prepareStatement(sql);
                ps.setString(1, pathImg.getPath());
                ps.setInt(2, produto.getId());
                ps.setString(3, pathImg.getLink());
                ps.execute();
            }
            ok = true;
        } catch (SQLException ex) {
            Logger.getLogger(ProdutoDAO.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("Erro ao salvar imagens");
            System.out.println("SQLException: " + ex.getMessage());
            System.out.println("SQLState: " + ex.getSQLState());
            System.out.println("VendorError: " + ex.getErrorCode());
            throw new IllegalArgumentException("Erro ao salvar imagens");
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
        return ok;
    }

    private boolean salvarFaq(Produto produto, Connection conexao) {
        PreparedStatement ps = null;
        boolean ok = false;
        try {
            for (Faq faq : produto.getFaqs()) {
                String sql = "INSERT INTO faqs VALUES (default, ?, ?, ?)";
                ps = conexao.prepareStatement(sql);
                ps.setString(1, faq.getPergunta());
                ps.setString(2, faq.getResposta());
                ps.setInt(3, produto.getId());
                ps.execute();
            }
            ok = true;
        } catch (SQLException ex) {
            Logger.getLogger(ProdutoDAO.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("Erro ao salvar FAQ");
            System.out.println("SQLException: " + ex.getMessage());
            System.out.println("SQLState: " + ex.getSQLState());
            System.out.println("VendorError: " + ex.getErrorCode());
            throw new IllegalArgumentException("Erro ao salvar FAQ");
        }
        return ok;
    }

    private boolean excluirFaq(Produto produto, Connection conexao) {
        PreparedStatement ps = null;
        boolean ok = false;
        try {
            String sql = "DELETE from faqs where id_produto =" + produto.getId();
            ps = conexao.prepareStatement(sql);
            ps.execute();
            ok = true;
        } catch (SQLException ex) {
            Logger.getLogger(ProdutoDAO.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("Erro ao excluir faqs");
            System.out.println("SQLException: " + ex.getMessage());
            System.out.println("SQLState: " + ex.getSQLState());
            System.out.println("VendorError: " + ex.getErrorCode());
            throw new IllegalArgumentException("Erro ao excluir FAQ");
        }
        return ok;
    }

    private Map<Integer, List<Imagem>> getMapListImagens(Connection conexao) {
        Map<Integer, List<Imagem>> mapPathImagens = new HashMap();
        ResultSet rs = null;
        PreparedStatement ps = null;

        try {
            ps = conexao.prepareStatement("SELECT * FROM imagens;");

            rs = ps.executeQuery();

            while (rs.next()) {
                Imagem imagem = new Imagem();
                imagem.setId(rs.getInt("id_imagem"));
                imagem.setPath(rs.getString("path_imagem"));
                imagem.setLink(rs.getString("link_imagem"));
                if (mapPathImagens.get(rs.getInt("id_produto")) == null) {
                    List<Imagem> listPathImagens = new ArrayList();
                    listPathImagens.add(imagem);
                    mapPathImagens.put(rs.getInt("id_produto"), listPathImagens);
                } else {
                    List<Imagem> listPathImagens = mapPathImagens.get(rs.getInt("id_produto"));
                    listPathImagens.add(imagem);
                }
            }

        } catch (SQLException ex) {
            System.out.println("Erro ao consultar imagens");
            System.out.println("SQLException: " + ex.getMessage());
            System.out.println("SQLState: " + ex.getSQLState());
            System.out.println("VendorError: " + ex.getErrorCode());
            throw new IllegalArgumentException("Erro ao consultar imagens");
        }
        return mapPathImagens;
    }

    private Map<Integer, List<Faq>> getMapListFaqs(Connection conexao) {
        Map<Integer, List<Faq>> mapListFaqs = new HashMap();
        ResultSet rs = null;
        PreparedStatement ps = null;

        try {
            ps = conexao.prepareStatement("SELECT \n"
                    + "    pergunta, resposta, id_produto\n"
                    + "FROM\n"
                    + "    faqs;");

            rs = ps.executeQuery();

            while (rs.next()) {
                Faq faq = new Faq();
                faq.setPergunta(rs.getString("pergunta"));
                faq.setResposta(rs.getString("resposta"));
                if (mapListFaqs.get(rs.getInt("id_produto")) == null) {
                    List<Faq> listFaq = new ArrayList();
                    listFaq.add(faq);
                    mapListFaqs.put(rs.getInt("id_produto"), listFaq);
                } else {
                    List<Faq> listFaq = mapListFaqs.get(rs.getInt("id_produto"));
                    listFaq.add(faq);
                }
            }

        } catch (SQLException ex) {
            System.out.println("Erro ao consultar faqs");
            System.out.println("SQLException: " + ex.getMessage());
            System.out.println("SQLState: " + ex.getSQLState());
            System.out.println("VendorError: " + ex.getErrorCode());
            throw new IllegalArgumentException("Erro ao consultar FAQS");
        }
        return mapListFaqs;
    }

    private List<Imagem> uploadImagens(MultipartRequest multpartImagens, Produto produto) {
        ImagemAPI imagemAPI = new ImagemAPI();
        List<Imagem> pathImagens = new ArrayList();
        multpartImagens.getFileNames().forEachRemaining((String referenciaImg) -> {
            Imagem imagem = new Imagem();
            imagem.setPath(imagemAPI.uploadImagem(multpartImagens.getFile(referenciaImg), produto.getId(), produto.getNome()));
            pathImagens.add(imagem);
        });
        return imagemAPI.getLinkImagem(pathImagens);
    }

    private String getConsulta(FiltroProduto filtro) {
        if (filtro.isAndDeleted()) {
            return "SELECT * FROM produtos;";
        }
        if (filtro.withID()) {
            return "SELECT * FROM produtos WHERE id_produto = " + filtro.getId() + ";";
        }
        if (filtro.withQuantidade() && filtro.withNome()) {
            return "SELECT * FROM produtos WHERE quantidade_produto < " + filtro.getQuantidade() + " AND produto_nome like '%" + filtro.getNome() + "%' AND deleted = FALSE;";
        }
        if (filtro.withNome()) {
            return "SELECT * FROM produtos WHERE quantidade_produto > 0 AND nome_produto like '%" + filtro.getNome() + "%';";
        }
        if (filtro.withQuantidade()) {
            return "SELECT * FROM produtos WHERE quantidade_produto < " + filtro.getQuantidade() + ";";
        }
        if (filtro.withPalavrasChave()) {
            return "SELECT * FROM produtos WHERE quantidade_produto > 0 AND deleted = FALSE AND palavra_chave_produto LIKE '%" + filtro.getPalavrasChave() + "%'";
        }
        return "SELECT * FROM produtos WHERE quantidade_produto > 0 AND deleted = FALSE;";
    }
}
