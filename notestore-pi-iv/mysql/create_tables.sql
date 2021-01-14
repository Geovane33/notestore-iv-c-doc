CREATE TABLE produtos (
    id_produto INT(11) AUTO_INCREMENT  PRIMARY KEY,
    nome_produto VARCHAR(300) NOT NULL,
    marca_produto VARCHAR(300) DEFAULT NULL,
    quantidade_produto INT(11) NOT NULL,
    preco_produto DOUBLE NOT NULL,
    data_entrada DATE NOT NULL,
    desc_produto VARCHAR(2000) DEFAULT NULL,
    palavra_chave_produto VARCHAR(2000) DEFAULT NULL,
    categoria VARCHAR(30) NOT NULL,
    deleted BOOLEAN NOT NULL DEFAULT FALSE
);

CREATE TABLE clientes (
    id_cliente INT AUTO_INCREMENT PRIMARY KEY,
    nome_cliente VARCHAR(30) NOT NULL,
    sobrenome_cliente VARCHAR(50) NOT NULL,
    cpf_cliente VARCHAR(15) UNIQUE NOT NULL,
    data_nascimento_cliente DATE NOT NULL,
    sexo_cliente VARCHAR(10),
    email_cliente VARCHAR(30) NOT NULL,
    telefone_cliente VARCHAR(16) NOT NULL,
    cep_cliente VARCHAR(15) NOT NULL,
    rua_ou_avenida_cliente VARCHAR(50) NOT NULL,
    numero_cliente VARCHAR(8),
    bairro_cliente VARCHAR(20),
    estado_cliente VARCHAR(20),
    cidade_cliente VARCHAR(20)
);

CREATE TABLE imagens (
    id_imagem INT(11) AUTO_INCREMENT PRIMARY KEY,
    path_imagem VARCHAR(50) NOT NULL,
    id_produto INT(11) NOT NULL,
    FOREIGN KEY (id_produto)
        REFERENCES produtos (id_produto)
);

CREATE TABLE faqs (
    id_faq INT(11) AUTO_INCREMENT PRIMARY KEY,
    pergunta VARCHAR(300) NOT NULL,
    resposta VARCHAR(300) NOT NULL,
    id_produto INT(11) NOT NULL,
    FOREIGN KEY (id_produto)
        REFERENCES produtos (id_produto)
);