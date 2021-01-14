produto = [];

$(document).ready(function () {
    init();
});
function init() {
    formEstoque();
    carregarProdutos();
    getAcesso();
}

function loadMsg(msg) {
    Swal.fire({
        title: msg,
        onBeforeOpen: () => {
            Swal.showLoading();

        }
    });
}

function formEstoque() {
    $('#estoque').ajaxForm({
        onsubmit: function (event) {
        }
        , beforeSend: function (xhr) {
            loadMsg("Enviando!");
        },
        success: function (result, textStatus, jqXHR) {
            if (result === '500') {
                Swal.fire({
                    icon: 'error',
                    title: 'Erro no servidor ao processar dados',
                    showConfirmButton: true
                })

            } else if (result.length === 0) {
                Swal.fire({
                    icon: 'warning',
                    title: 'Nenhum produto encontrado',
                    showConfirmButton: true
                })
            } else {
                produto = result;
                carregaTabela(12);
                setTimeout(function () {
                    Swal.close();
                }, 350);

            }
        },
        error: function (jqXHR, textStatus, errorThrown) {
            Swal.fire({
                icon: 'error',
                title: 'Erro ao consultar',
                showConfirmButton: true
            })
        },
        statusCode:{
            401: function(){
                Swal.fire({
                    icon: 'error',
                    title: 'Não autorizado',
                    showConfirmButton: true
                })
            }
        }
    }
    );
}
function carregarProdutos() {
    loadMsg("Carregando!");
    $.ajax({
        type: 'GET',
        url: '../../produtos',
        contentType: 'application/json;charset=UTF-8',
        headers: {
            Accept: "application/json;charset=UTF-8",
            "Content-Type": "application/json;charset=UTF-8"
        },
        success: function (result) {
            if (result === '200') {
                Swal.fire({
                    icon: 'warning',
                    title: 'Nenhum produto cadastrado',
                    showConfirmButton: true
                })
            } else {
                produto = result;
                carregaTabela(12);
                Swal.close();
            }

        },
        error: function (jqXHR, textStatus, errorThrown) {
            Swal.fire({
                icon: 'error',
                title: 'Erro ao carregar',
                showConfirmButton: true
            })
        }
    });
}


/**
 * metodo manter a tabela "atualizada" remove todas as linhas a cada vez que atualiza
 */
function removeLinha() {
    i = document.querySelectorAll("tr").length - 1;
    for (; i > 0; i--) {
        document.getElementById('lista-produtos').getElementsByTagName('tr')[0].remove();
    }
}

function carregaTabela(pagina) {
    removeLinha();
    var numeroPaginas;
    var numeroLinhas = 12;
    var qtdProdutos = produto.length;
    for (numeroPaginas = 0; qtdProdutos >= numeroLinhas; numeroPaginas++) {
        qtdProdutos -= numeroLinhas;
    }
    if (qtdProdutos > 0) {
        numeroPaginas += 2;
    } else if (qtdProdutos === 0) {
        numeroPaginas += 1;
    }

    if (pagina >= produto.length) {
        numeroLinhas = produto.length - pagina + numeroLinhas;
        pagina = produto.length;
    }

    for (var i = pagina - numeroLinhas; i < pagina; i++) {
        var linha = $("<tr>");
        var coluna = "";
        coluna += '<th scope="row"><button onclick="visualizarProd(' + i + ')">' + produto[i].id + '</button></th>';
        coluna += '<td>' + produto[i].nome + '</td>';
        coluna += '<td>' + produto[i].quantidade + '</td>';
        coluna += '<td>' + produto[i].preco + '</td>';
        coluna += '<td> <img class="icons-acoes" src="../icons/excluir.svg" onClick="excluirProd(' + i + ', ' + produto[i].id + ')">&nbsp&nbsp<img src="../icons/lapis.svg" class="icons-acoes" onClick="enviarProdEdit(' + i + ')"></td>';
        linha.append(coluna);
        $(".lista-produtos").append(linha);
    }
    $("#paginacao").html("");
    if (numeroPaginas == 2) {
        numeroPaginas = numeroPaginas - 1;
    }
    for (var i = 1; i < numeroPaginas; i++) {
        var input = $('<input type="button" class="btn btn-black-m btn-dark texto btn-pag" onClick=carregaTabela(' + (12 * i) + ') value="' + i + '"/>');
        $("#paginacao").append(input);
    }
}

function visualizarProd(indice) {
    localStorage.setItem('produto-detalhes', JSON.stringify(produto[indice]));
    window.location.href = 'detalhes.html';
}

function enviarProdEdit(indice) {
    localStorage.setItem('produto-editar', JSON.stringify(produto[indice]));
    window.location.href = 'cadastro.html?';
}

/**
 * Excluir produto
 * @param {number} i
 * @param {number} idProd
 */
function excluirProd(i, idProd) {
    Swal.queue([{
            title: 'Você tem certeza',
            text: "Você não poderá reverter isso!",
            icon: 'warning',
            showLoaderOnConfirm: true,
            showCancelButton: true,
            confirmButtonColor: '#3085d6',
            cancelButtonColor: '#d33',
            confirmButtonText: 'Sim!',
            cancelButtonText: 'Não',
            preConfirm: () => {
                return $.ajax({
                    type: 'DELETE',
                    url: '../../produtos?id=' + idProd,
                    beforeSend: function (xhr) {
                        loadMsg("Excluindo!");
                    },
                    headers: {
                        Accept: "application/json; charset=utf-8",
                        "Content-Type": "application/json; charset=utf-8"
                    },
                    error: function (jqXHR, textStatus, errorThrown) {
                        Swal.fire({
                            icon: 'error',
                            title: 'Erro ao excluir Produto',
                            showConfirmButton: true
                        })
                    },
                    success: function (result) {
                        produto.splice(i, 1);
                        carregaTabela(12);
                        Swal.fire({
                            icon: 'success',
                            title: 'Produto excluído com sucesso!',
                            showConfirmButton: false,
                            timer: 1500
                        });
                    },
                    statusCode: {
                        304: function () {
                            Swal.fire({
                                icon: 'warning',
                                title: 'Não modificado <br> Erro: 304',
                                showConfirmButton: true
                            })
                        },
                        400: function () {
                            Swal.fire({
                                icon: 'error',
                                title: 'Erro 400',
                                showConfirmButton: true
                            })
                        },
                        401: function () {
                            Swal.fire({
                                icon: 'error',
                                title: 'Não autorizado',
                                showConfirmButton: true
                            })
                        }
                    },
                
                });
            }
        }]);
}

function getAcesso() {
    $.ajax({
        type: 'GET',
        url: '/acesso',
        contentType: 'application/json;charset=UTF-8',
        headers: {
            Accept: "application/json;charset=UTF-8",
            "Content-Type": "application/json;charset=UTF-8"
        },
        success: function (usuario) {
            if (usuario.estoquista) {
                $("#estoque").html('<a href="/clientes/pedidos"><button class="btn btn-black-m btn-dark texto" type="button">PEDIDOS</button></a> <input type="text" placeholder="Nome do produto" name="nome"> <input  type="text" placeholder="Código do produto" name="id"> <label class="label-quantidade-estoque" for="quantidade-estoque">Estoque abaixo de: </label> <input class="quantidade-estoque" type="number" min="0" id="quantidade-estoque" name="quantidade"> <button class="btn btn-black-m btn-dark texto" type="submit">PROSSEGUIR</button>');
            } else {
                $("#estoque").html('<a href="cadastro.html"><button class="btn btn-black-m btn-dark texto" type="button">NOVO PRODUTO</button></a><a href="/clientes/pedidos"><button class="btn btn-black-m btn-dark texto" type="button">PEDIDOS</button></a> <input type="text" placeholder="Nome do produto" name="nome"> <input  type="text" placeholder="Código do produto" name="id"> <label class="label-quantidade-estoque" for="quantidade-estoque">Estoque abaixo de: </label> <input class="quantidade-estoque" type="number" min="0" id="quantidade-estoque" name="quantidade"> <button class="btn btn-black-m btn-dark texto" type="submit">PROSSEGUIR</button>');
            }
        },
        error: function (jqXHR, textStatus, errorThrown) {

        },
    });
}
