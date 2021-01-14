enderecos = [];
pedidos = [];
usuario = {};
$(document).ready(function () {
    init();
});

function init() {
 loadMsg("Carregando pedidos!");
    carregaPedidos();
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

function logout() {
    Swal.fire({
        icon: 'success',
        title: 'Logout efetuado com sucesso! <br> sessão finalizada',
        showConfirmButton: false
    })
    setTimeout(function () {
        window.location.href = '/logout';
    }, 2000);
}

function carregarEnderecos() {
    loadMsg("Carregando!");
    $.ajax({
        type: 'GET',
        url: '/clientes/enderecos',
        contentType: 'application/json;charset=UTF-8',
        headers: {
            Accept: "application/json;charset=UTF-8",
            "Content-Type": "application/json;charset=UTF-8"
        },
        success: function (result) {
            if (result.length === 0) {
                Swal.fire({
                    icon: 'warning',
                    title: 'Nenhum endereco cadastrado',
                    showConfirmButton: true
                })
            } else {
                enderecos = result;
                listarEnderecos();
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



function listarEnderecos() {
    $("#enderecos").html("");
    cardsEndereco = [];
    contCard = 0;
    for (i = 0; i < enderecos.length - contCard; i++) {
        cardsEndereco.push('<div class="row">');
        for (j = contCard; j < contCard + 2; j++) {
            cardsEndereco.push(cardEnderecos(j));
            contCard++;
            if (enderecos.length === contCard) {
                break;
            }
        }
        cardsEndereco.push("</div>");
    }
    $("#enderecos").append(cardsEndereco.join(""));
}

function cardEnderecos(j) {
    return '<div class="col-6">'
        + '<div style="box-shadow: 0 0 8px 0px;">'
        + '<div class="card">'
        + '<div class="card-body">'
        + '<h4 class="card-title">' + enderecos[j].nome + '</h4>'
        + '<p class="card-text">' + enderecos[j].rua + ', ' + enderecos[j].numero
        + '<br>' + enderecos[j].bairro + ' - ' + enderecos[j].cidade + ' - ' + enderecos[j].estado
        + '<br>' + enderecos[j].cep + '</p>'
        + '<hr><a class="card-link text-black-50" href="#" onclick="editarEndereco(' + j + ')" ><i class="fa fa-edit"></i>Alterar</a><a class="card-link text-black-50" href="#" onclick="excluirEndereco(' + j + ')"><i class="fas fa-trash-alt"></i>Excluir</a>'
        + '</div>'
        + '</div>'
        + '</div>'
        + '</div>';
}

function carregaPedidos() {
    $.ajax({
        type: 'GET',
        url: '/pedido/consultar',
        contentType: 'application/json;charset=UTF-8',
        headers: {
            Accept: "application/json;charset=UTF-8",
            "Content-Type": "application/json;charset=UTF-8"
        },
        success: function (result) {
            if (result.length === 0) {
                Swal.fire({
                    icon: 'warning',
                    title: 'Nenhum pedido encontrado',
                    showConfirmButton: true
                })
            } else {
                pedidos = result;
                cardPedidos();
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

function cardPedidos() {
    $("#pedidos").html("");
    cardPedido = [];
    for (i = 0; i < pedidos.length; i++) {
        dataPedido = moment(pedidos[i].data).format("DD/MM/YYYY HH:mm");
        cardPedido.push('<div class="row" style="border-radius: 4px;box-shadow: 0px 2px 17px;">'
            + '<div class="col">'
            + '<div class="row">');
            cardPedido.push('<div class="col"><strong class="d-xl-flex justify-content-xl-start">Status: ' + pedidos[i].status + '</strong>');

        for (j = 0; j < pedidos[i].produtosCarrinho.length; j++) {
            cardPedido.push('<span class="d-xl-flex align-items-xl-start"><br><strong>' + pedidos[i].produtosCarrinho[j].produto.nome + '</strong><br></span>');
        }
        cardPedido.push('</div>'
            + '<div class="col"><strong class="d-xl-flex justify-content-xl-start">Resumo da '
            + 'compra</strong><span>Pedido: ' + pedidos[i].numeroPedido + '</span><span'
            + 'class="d-xl-flex align-items-xl-start"><br>Data do pedido:'
            + '<strong> ' + dataPedido + '</strong></span><span'
            + 'class="d-xl-flex align-items-xl-start"><br>Valor total:'
            + '<strong> ' + formatPreco(pedidos[i].precoTotal) + '</strong></span><br>'
            + '<a style="cursor:pointer" onclick="detalharPedido(' + i + ')" ><span style="text-decoration: underline">VER DETALHES</span></a>'
            + '</div>'
            + '</div>'
            + '</div>'
            + '</div>'
            + '<hr>');
    }
    $("#pedidos").append(cardPedido.join(""));
}

function detalharPedido(i) {
    $("#pedidos").html("");
    pedidoDetalhado = [];
    pedidoDetalhado.push('<div class="row" style="border-radius: 4px;box-shadow: 0px 2px 0px;">'
        + '<div class="col">'
        + '<div class="row" style="padding-bottom: 50px;">'
        + '<div class="col">');
        if (usuario.estoquista) {
                    if (pedidos[i].status === 'Aguardando pagamento') {
                        pedidoDetalhado.push('<form id="atualizarPedido" action="/pedido" method="POST"><input type="hidden" name="idPedido" value="' + pedidos[i].idPedido + '">  <label for="status">Alterar Status:</label> <select name="status" id="status"> <option value="Aguardando pagamento" selected>Aguardando pagamento</option> <option value="Pagamento rejeitado">Pagamento rejeitado</option> <option value="Pagamento com sucesso">Pagamento com sucesso</option> <option value="Aguardando retirada">Aguardando retirada</option> <option value="Em transito">Em transito</option> <option value="Entregue">Entregue</option> </select> <input class="btn btn-black-m btn-dark texto" type="submit" value="Salvar"> </form><br>');
                    } else if (pedidos[i].status === 'Pagamento rejeitado') {
                        pedidoDetalhado.push('<form id="atualizarPedido" action="/pedido" method="POST"><input type="hidden" name="idPedido" value="' + pedidos[i].idPedido + '">  <label for="status">Alterar Status:</label> <select name="status" id="status"> <option value="Aguardando pagamento">Aguardando pagamento</option> <option value="Pagamento rejeitado" selected>Pagamento rejeitado</option> <option value="Pagamento com sucesso">Pagamento com sucesso</option> <option value="Aguardando retirada">Aguardando retirada</option> <option value="Em transito">Em transito</option> <option value="Entregue">Entregue</option> </select> <input class="btn btn-black-m btn-dark texto" type="submit" value="Salvar"> </form><br>');
                    } else if (pedidos[i].status === 'Pagamento com sucesso') {
                        pedidoDetalhado.push('<form id="atualizarPedido" action="/pedido" method="POST"><input type="hidden" name="idPedido" value="' + pedidos[i].idPedido + '">  <label for="status">Alterar Status:</label> <select name="status" id="status"> <option value="Aguardando pagamento">Aguardando pagamento</option> <option value="Pagamento rejeitado">Pagamento rejeitado</option> <option value="Pagamento com sucesso" selected>Pagamento com sucesso</option> <option value="Aguardando retirada">Aguardando retirada</option> <option value="Em transito">Em transito</option> <option value="Entregue">Entregue</option> </select> <input class="btn btn-black-m btn-dark texto" type="submit" value="Salvar"> </form><br>');
                    } else if (pedidos[i].status === 'Aguardando retirada') {
                        pedidoDetalhado.push('<form id="atualizarPedido" action="/pedido" method="POST"><input type="hidden" name="idPedido" value="' + pedidos[i].idPedido + '">  <label for="status">Alterar Status:</label> <select name="status" id="status"> <option value="Aguardando pagamento">Aguardando pagamento</option> <option value="Pagamento rejeitado">Pagamento rejeitado</option> <option value="Pagamento com sucesso">Pagamento com sucesso</option> <option value="Aguardando retirada" selected>Aguardando retirada</option> <option value="Em transito">Em transito</option> <option value="Entregue">Entregue</option> </select> <input class="btn btn-black-m btn-dark texto" type="submit" value="Salvar"> </form><br>');
                    } else if (pedidos[i].status === 'Em transito') {
                        pedidoDetalhado.push('<form id="atualizarPedido" action="/pedido" method="POST"><input type="hidden" name="idPedido" value="' + pedidos[i].idPedido + '">  <label for="status">Alterar Status:</label> <select name="status" id="status"> <option value="Aguardando pagamento">Aguardando pagamento</option> <option value="Pagamento rejeitado">Pagamento rejeitado</option> <option value="Pagamento com sucesso">Pagamento com sucesso</option> <option value="Aguardando retirada" >Aguardando retirada</option> <option value="Em transito" selected>Em transito</option> <option value="Entregue">Entregue</option> </select> <input class="btn btn-black-m btn-dark texto" type="submit" value="Salvar"> </form><br>');
                    }else{
                        pedidoDetalhado.push('<form id="atualizarPedido" action="/pedido" method="POST"><input type="hidden" name="idPedido" value="' + pedidos[i].idPedido + '">  <label for="status">Alterar Status:</label> <select name="status" id="status"> <option value="Aguardando pagamento">Aguardando pagamento</option> <option value="Pagamento rejeitado">Pagamento rejeitado</option> <option value="Pagamento com sucesso">Pagamento com sucesso</option> <option value="Aguardando retirada">Aguardando retirada</option> <option value="Em transito">Em transito</option> <option value="Entregue" selected>Entregue</option> </select> <input class="btn btn-black-m btn-dark texto" type="submit" value="Salvar"> </form><br>');
                    }
                } else {
                    pedidoDetalhado.push('<strong class="d-xl-flex justify-content-xl-start">Status do pedido: ' + pedidos[i].status + '</strong>');
                }

    for (j = 0; j < pedidos[i].produtosCarrinho.length; j++) {
        pedidoDetalhado.push('<div style="box-shadow: 0px 0px 6px;">'
            + '<div class="col"><img class="d-flex" style="width: 152px;height: 112px;" src="' + pedidos[i].produtosCarrinho[j].produto.imagens[0].link + '">'
            + '<strong>' + pedidos[i].produtosCarrinho[j].produto.nome + '</strong>'
            + '<span class="d-xl-flex align-items-xl-start">Marca: <strong>&nbsp;' + pedidos[i].produtosCarrinho[j].produto.marca + '</strong></span>'
            + '<span class="d-xl-flex align-items-xl-start">Quantidade: <strong>&nbsp;' + pedidos[i].produtosCarrinho[j].quantidadeCompra + '</strong></span>'
            + '<span class="d-xl-flex align-items-xl-start">Unidade: <strong>&nbsp;' + formatPreco(pedidos[i].produtosCarrinho[j].precoCompra) + '</strong></span>'
            + '<span class="d-xl-flex align-items-xl-start">Total: <strong>&nbsp;' + formatPreco((pedidos[i].produtosCarrinho[j].precoCompra * pedidos[i].produtosCarrinho[j].quantidadeCompra)) + '</strong></span>'
            + '</div>'
            + '</div>'
            + '<hr>');
    }
    pagamento = 'Boleto: ' + formatPreco(pedidos[i].precoTotal);
    precoParcelado = (pedidos[i].precoTotal) / pedidos[i].qtdParcelas;
    if (pedidos[i].formaPagamento == 'Cartão') {
        pagamento = 'Cartão: ' + formatPreco((pedidos[i].precoTotal)) + '<br/>(Parcelado em ' + pedidos[i].qtdParcelas + 'x de '
            + formatPreco(precoParcelado) + ')<br/>';
    }

    dataPedido = moment(pedidos[i].data).format("DD/MM/YYYY HH:mm");
    pedidoDetalhado.push('</div><div class="col" style="background: rgba(222,222,222,0.58);border-radius: 5px;">'
        + '<strong class="d-xl-flex justify-content-xl-start">Resumo da compra<a style="cursor:pointer; color:red; position: relative; left: 136px;" onclick="cardPedidos()" >Fechar detalhes</a></strong>'
        + '<span>Pedido: ' + pedidos[i].numeroPedido + '</span>'
        + '<span class="d-xl-flex align-items-xl-start">Data do pedido: <strong>&nbsp;' + dataPedido + '</strong></span>'
        + '<hr>'
        + '<span class="d-xl-flex align-items-xl-start"><strong>Endereço de entrega</strong></span>'
        + '<span class="d-xl-flex align-items-xl-start">' + pedidos[i].endereco.nome + '</span>'
        + '<span class="d-xl-flex align-items-xl-start">' + pedidos[i].endereco.rua + ', ' + pedidos[i].endereco.numero + ', ' + pedidos[i].endereco.cidade + '<br>' + pedidos[i].endereco.estado + '</span>'
        + '<span class="d-xl-flex align-items-xl-start">CEP: ' + pedidos[i].endereco.cep + '<br></span>'
        + '<hr>'
        + '<span class="d-xl-flex align-items-xl-start"><strong>Forma de pagamento</strong></span>'
        + '<span class="d-xl-flex align-items-xl-start">' + pagamento + '</span>'
        + '<hr><span class="d-xl-flex align-items-xl-start"><strong>Total da compra</strong></span>'
        + '<span class="d-xl-flex align-items-xl-start">Subtotal de produtos: ' + formatPreco(pedidos[i].precoTotal - pedidos[i].precoFrete) + '<br></span>'
        + '<span class="d-xl-flex align-items-xl-start">Frete: ' + formatPreco(pedidos[i].precoFrete) + '<br></span>'
        + '<span class="d-xl-flex align-items-xl-start">Descontos: R$ 0,00<br></span>'
        + '<hr>'
        + '<span class="d-xl-flex align-items-xl-start"><strong>Valor total: ' + formatPreco(pedidos[i].precoTotal) + '</strong></span>'
        + '</div>'
        + '</div>'
        + '</div>'
        + '</div>');
    $("#pedidos").append(pedidoDetalhado.join(""));
    formAtualizarPedido();
}
function formatPreco(preco) {
    return preco.toLocaleString('pt-BR', { style: 'currency', currency: 'BRL' });
}

function editEndereco(indice) {
    localStorage.setItem('produto-detalhes', JSON.stringify(produtos[indice]));
    window.location.href = '/produtos/detalhes.html';
}


function formPesquisar() {
    $('#formPesquisar').ajaxForm({
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
                    title: 'Nenhum endereço encontrado',
                    showConfirmButton: true
                })
            } else {

                setTimeout(function () {
                    Swal.close();
                }, 350);

            }
        }
        ,
        error: function (jqXHR, textStatus, errorThrown) {
            Swal.fire({
                icon: 'error',
                title: 'Erro ao consultar',
                showConfirmButton: true
            })
        }
    }
    );
}

function formAtualizarPedido() {
    $('#atualizarPedido').ajaxForm({
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
            } else {
                Swal.fire({
                    icon: 'success',
                    title: 'Status alterado com sucesso',
                    showConfirmButton: true
                })
                      setTimeout(function () {
                                     loadMsg("Atualizando pedidos!");
                                                     carregaPedidos();
                                }, 1000);

            }
        }
        ,
        error: function (jqXHR, textStatus, errorThrown) {
            Swal.fire({
                icon: 'error',
                title: 'Erro ao alterar status',
                showConfirmButton: true
            })
        }
    }
    );
    }


function excluirEndereco(i) {
    if (enderecos.length === 1) {
        Swal.fire({
            icon: 'warning',
            title: 'Obrigatório pelo menos 1 endereço! <br> Endereço de fatura.',
            showConfirmButton: true,
        });
        return;
    }
    Swal.queue([{
        title: 'Você tem certeza',
        text: "Você não poderá reverter isso!",
        icon: 'warning',
        showLoaderOnConfirm: true,
        showCancelButton: true,
        confirmButtonColor: '#000000',
        cancelButtonColor: '#d33',
        confirmButtonText: 'Sim!',
        cancelButtonText: 'Não',
        preConfirm: () => {
            return $.ajax({
                type: 'DELETE',
                url: '/clientes/enderecos?id=' + enderecos[i].id,
                beforeSend: function (xhr) {
                    loadMsg("Excluindo!");
                },
                headers: {
                    Accept: "application/json; charset=utf-8",
                    "Content-Type": "application/json; charset=utf-8"
                },
                success: function (result) {
                },
                error: function (jqXHR, textStatus, errorThrown) {
                    Swal.fire({
                        icon: 'error',
                        title: 'Erro ao excluir endereço',
                        showConfirmButton: true
                    })
                },
                statusCode: {
                    200: function () {
                        enderecos.splice(i, 1);
                        listarEnderecos();
                        Swal.fire({
                            icon: 'success',
                            title: 'Endereço excluído com sucesso!',
                            showConfirmButton: false,
                            timer: 1500
                        });
                    },
                    400: function () {
                        Swal.fire({
                            icon: 'error',
                            title: 'Erro 400',
                            showConfirmButton: true
                        })
                    },
                },

            });
        }
    }]);
}

function editarEndereco(indice) {
    localStorage.setItem('edit-endereco', JSON.stringify(enderecos[indice]));
    window.location.href = '/clientes/endereco-alterar';
}

function adicionarEndereco() {
    localStorage.removeItem('edit-endereco');
    window.location.href = '/clientes/endereco-adicionar';
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
        success: function (result) {
            usuario = result;
            $("#aNome").html(result.nome);
        },
        error: function (jqXHR, textStatus, errorThrown) {

        },
    });
}