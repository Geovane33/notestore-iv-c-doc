package br.senac.sp.pi.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ConexaoDB {

    private static String STATUS = "Não conectado";
    private static String DRIVER = "com.mysql.cj.jdbc.Driver";

    private static String DB_ADDRESS = "jdbc:mysql://bwzswmyrwg0zmjznjm4o-mysql.services.clever-cloud.com:3306/bwzswmyrwg0zmjznjm4o";
    private static String USER = "us0q075f1etjk7ni";
    private static String PASSWORD = "rzl4CeP9sKCD6OdMHdA8";
    private static Connection CONEXAO = null;

    static {
        try {
            Class.forName(DRIVER);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(ConexaoDB.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Verificar se existe uma conexão com o banco de dados
     */
    public static void getStatus() {
        System.out.println(STATUS);
    }

    /**
     * Obter conexão com o banco de dados
     *
     * @return Connection - se for possivel realizar a conexão com o banco de
     * dados é retornado uma Connection
     */
    public static Connection getConexao() {
        String dbURL = DB_ADDRESS + "?useTimezone=true&serverTimezone=GMT-3&useSSL=false";

        try {
            CONEXAO = DriverManager.getConnection(dbURL, USER, PASSWORD);
            STATUS = "conectado";
        } catch (SQLException ex) {
            System.out.println("Erro ao conectar no banco");
            System.out.println("SQLException: " + ex.getMessage());
            System.out.println("SQLState: " + ex.getSQLState());
            System.out.println("VendorError: " + ex.getErrorCode());
        }
        return CONEXAO;
    }

    /**
     * fechar conexão do banco de dados
     *
     * @param conexao
     * @return boolean - true: conexão fechada com sucesso false: erro ao fechar
     * conexão
     */
    public static boolean fecharConexao(Connection conexao) {
        try {
            if (conexao != null) {
                if (!conexao.isClosed()) {
                    conexao.close();
                }
            }
            STATUS = "Não conectado";
            return true;

        } catch (SQLException ex) {
            System.out.println("Erro ao fechar conexao do banco de dados");
            System.out.println("SQLException: " + ex.getMessage());
            System.out.println("SQLState: " + ex.getSQLState());
            System.out.println("VendorError: " + ex.getErrorCode());
            return false;
        }
    }
}
