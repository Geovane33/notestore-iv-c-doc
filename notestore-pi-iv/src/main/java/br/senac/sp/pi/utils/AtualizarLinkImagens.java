package br.senac.sp.pi.utils;

import br.senac.sp.pi.api.ImagemAPI;
import br.senac.sp.pi.dao.ProdutoDAO;
import br.senac.sp.pi.db.ConexaoDB;
import br.senac.sp.pi.entidade.Imagem;
import br.senac.sp.pi.entidade.Produto;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AtualizarLinkImagens extends Thread {

    @Override
    public void run() {
        ProdutoDAO produtoDAO = new ProdutoDAO();
        FiltroProduto filtro = new FiltroProduto();
        filtro.setImgsAtualizadas(true);
        filtro.setAndDeleted(true);
        List<Produto> produtos = produtoDAO.consultar(filtro);
        produtos.stream().forEach(this::updateLinkImage);
    }

    private void updateLinkImage(Produto produto) {
        ImagemAPI imagemAPI = new ImagemAPI();
        produto.setImagens(imagemAPI.getLinkImagem(produto.getImagens()));
        updateLinkImagens(produto);
    }

    private void updateLinkImagens(Produto produto) {
        PreparedStatement ps = null;
        Connection conexao = null;
        try {
            conexao = ConexaoDB.getConexao();
            for (Imagem pathImg : produto.getImagens()) {
                String sql = "UPDATE imagens SET link_imagem = ? WHERE id_imagem = ?;";
                ps = conexao.prepareStatement(sql);
                ps.setString(1, pathImg.getLink());
                ps.setInt(2, pathImg.getId());
                ps.executeUpdate();
            }
        } catch (SQLException ex) {
            Logger.getLogger(ProdutoDAO.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("Erro ao atualizar imagens");
            System.out.println("SQLException: " + ex.getMessage());
            System.out.println("SQLState: " + ex.getSQLState());
            System.out.println("VendorError: " + ex.getErrorCode());
            throw new RuntimeException("Erro ao atualizar imagens");
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
}
