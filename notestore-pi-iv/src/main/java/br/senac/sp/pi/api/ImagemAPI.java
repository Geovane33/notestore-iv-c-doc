package br.senac.sp.pi.api;

import br.senac.sp.pi.entidade.Imagem;
import com.dropbox.core.DbxException;
import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.files.FileMetadata;
import com.dropbox.core.v2.files.GetTemporaryLinkResult;
import com.dropbox.core.v2.files.ListFolderResult;
import com.dropbox.core.v2.files.Metadata;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.web.multipart.MultipartFile;

public class ImagemAPI {

    private static final String ACCESS_TOKEN = "v_DvMRZawEcAAAAAAAAAASESlDU0UXQC_sJE2e_jz8kL2Gg13-Uc9b-sjJB2oss7";

    /**
     * Obter um link temporario de uma determinada imagem
     *
     * @param pathImagens lista com o caminho das imagens que deseja obter o link
     * @return
     */
    public List<Imagem> getLinkImagem(List<Imagem> pathImagens) {
        List<Imagem> linkImagens = new ArrayList();
        DbxClientV2 client = getClientAcesso();

        try {
            if (pathImagens.isEmpty()) {
                return linkImagens;
            }
            for (Imagem imagem : pathImagens) {
                GetTemporaryLinkResult linkTemporario = client.files().getTemporaryLink(imagem.getPath());
                imagem.setLink(linkTemporario.getLink());
                linkImagens.add(imagem);
            }
        } catch (DbxException ex) {
            Logger.getLogger(ImagemAPI.class.getName()).log(Level.SEVERE, null, ex);
        }
        return linkImagens;
    }

    /**
     * Realizar o upload de uma determinada imagem
     *
     * @param imagem      arquivo imagem
     * @param idProduto   do produto
     * @param nomeProduto nome do produto
     * @return String path da imagem
     */
    public String uploadImagem(MultipartFile imagem, int idProduto, String nomeProduto) {
        DbxClientV2 client = getClientAcesso();
        //ler o arquivo e realizar o upload 
        try (InputStream arqUpload = imagem.getInputStream()) {
            FileMetadata metadata = client.files().uploadBuilder("/" + idProduto + "/" + nomeProduto + "-" + new Random().nextInt(9999) + ".png")
                    .uploadAndFinish(arqUpload);

            return "/" + idProduto + "/" + metadata.getName();
        } catch (FileNotFoundException | DbxException ex) {
            Logger.getLogger(ImagemAPI.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(ImagemAPI.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public void deleteImagemNuvem(String pathNuvem) {
        try {
            DbxClientV2 client = getClientAcesso();
            client.files().deleteV2(pathNuvem);
        } catch (DbxException ex) {
            Logger.getLogger(ImagemAPI.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private List<String> listarImagensFromPath(int idProduto) {
        List<String> imagens = new ArrayList();
        try {
            DbxClientV2 client = getClientAcesso();
            ListFolderResult result = client.files().listFolder("/" + idProduto);
            for (Metadata metadata : result.getEntries()) {
                imagens.add(metadata.getName());
            }
        } catch (DbxException ex) {
            Logger.getLogger(ImagemAPI.class.getName()).log(Level.SEVERE, null, ex);
        }
        return imagens;
    }

    private DbxClientV2 getClientAcesso() {
        // Criar um cliente para acessar os arquivos
        DbxRequestConfig config = DbxRequestConfig.newBuilder("notestore").build();
        return new DbxClientV2(config, ACCESS_TOKEN);
    }
}
