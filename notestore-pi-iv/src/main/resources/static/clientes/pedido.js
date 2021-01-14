clienteLogado = false;

pedido = {
    frete: 0,
    subTotal: 0,
    total: 0,
    qtdParcela: 0,
    precoParcela: 0,
    formaPagamento: 'Não informada',
    textoParcela: ''
};
carrinho = [];
endereco = {};

$(document).ready(function () {
    init();
});

function init() {
    carregarCarrinho();
    getEndereco();
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

function carregarDados() {
    loadMsg("Carregando!");
    $.ajax({
        type: 'GET',
        url: '/clientes',
        contentType: 'application/json;charset=UTF-8',
        headers: {
            Accept: "application/json;charset=UTF-8",
            "Content-Type": "application/json;charset=UTF-8"
        },
        success: function (result) {
            if (result.length === 0) {
                Swal.fire({
                    icon: 'warning',
                    title: 'Nenhuma informação encontrada',
                    showConfirmButton: true
                })
            } else {
                clienteLogado = result;
                listarDados();
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


function carregarCarrinho() {
    loadMsg('Carregando');
    $.ajax({
        type: 'GET',
        url: '/carrinho/consultar',
        contentType: 'application/json;charset=UTF-8',
        headers: {
            Accept: "application/json;charset=UTF-8",
            "Content-Type": "application/json;charset=UTF-8"
        },
        success: function (result) {
            carrinho = result;
            listarCarrinho();
            Swal.close();
        },
        error: function (jqXHR, textStatus, errorThrown) {

        },
    });
}

function listarCarrinho() {
    listaCarrinho = [];
    $("#listaCarrinho").html("");
    for (i = 0; i < carrinho.length; i++) {
        precoProduto = formatPreco(carrinho[i].produto.preco);
        listaCarrinho.push('<div class="row">'
            + '<div class="col">'
            + '<div class="row no-gutters border">'
            + '<div class="col-4"><img class="img-preview" src="' + carrinho[i].produto.imagens[0].link + '"></div>'
            + '<div class="col">'
            + '<div class="row">'
            + '<div class="col"><strong class="d-inline-block">' + carrinho[i].produto.nome + '<br></strong></div>'
            + '</div>'
            + '<div class="row">'
            + '<div class="col"><span><strong>Marca:</strong>' + carrinho[i].produto.marca + '</span></div>'
            + '</div>'
            + '<div class="row">'
            + '<div class="col"><span><strong>Unidade:</strong>&nbsp;' + precoProduto + '</span></div>'
            + '</div>'
            + '<div class="row">'
            + '<div class="col"><span><strong>Quantidade:&nbsp;</strong><strong>&nbsp;</strong><span><strong>&nbsp;' + carrinho[i].quantidadeCompra + '&nbsp;</strong></span></span>'
            + '</div>'
            + '</div>'
            + '<div class="row">'
            + '<div class="col"><span><strong>Total:</strong>&nbsp;' + formatPreco((carrinho[i].produto.preco * carrinho[i].quantidadeCompra)) + '</span></div>'
            + '</div>'
            + '</div>'
            + '</div>'
            + '</div>'
            + '</div>'
            + '</div>'
            + '<hr>')
    }
    $("#listaCarrinho").append(listaCarrinho.join(""));
    resumoCompra();
}



function resumoCompra() {
    gerarPedido();
    $("#resumoCompra").html("");
    $("#resumoCompra").append('<div class="col" style="background: rgba(222,222,222,0.58);border-radius: 5px;">'
        + '<span class="d-xl-flex align-items-xl-start"><strong>Endereço de entrega</strong></span><span'
        + 'class="d-xl-flex align-items-xl-start">' + endereco.nome + '</span><span class="d-xl-flex align-items-xl-start">' + endereco.cidade + ', ' + endereco.rua + ', '
        + endereco.numero + '<br/>' + endereco.bairro + '<br/>' + endereco.estado + '</span>'
        + '<span class="d-xl-flex align-items-xl-start">CEP: ' + endereco.cep + '<br /></span>'
        + '<a class="card-link text-black-50" style="cursor:pointer" onclick="alterarEndereco()" >Alterar endereço</a>'
        + '<hr /><span class="d-xl-flex align-items-xl-start"><strong>Forma de pagamento</strong></span><span'
        + 'class="d-xl-flex align-items-xl-start">' + pedido.formaPagamento + ': ' + formatPreco(pedido.total) + ' ' + pedido.textoParcela + '</span>'
        + '<hr /><span class="d-xl-flex align-items-xl-start"><strong>Total da compra</strong></span><span'
        + 'class="d-xl-flex align-items-xl-start">Subtotal de produtos: ' + formatPreco(pedido.subTotal) + '<br /></span><span'
        + 'class="d-xl-flex align-items-xl-start">Frete: ' + formatPreco(pedido.frete) + '<br /></span>'
        + '<span class="d-xl-flex align-items-xl-start">Descontos: R$ 0,00<br /></span>'
        + '<hr /><span class="d-xl-flex align-items-xl-start"><strong>Valor total: ' + formatPreco(pedido.total) + '</strong></span>'
        + '<div class="form-row justify-content-center">'
        + '<div class="col-md-4"><button class="btn btn-block border rounded border-dark shadow btn-black mt-3" onclick="salvarPedido()" type="button"><strong>CONCLUIR</strong></button></div>'
        + '</div>'
        + '</div>'
    );
}

function gerarPedido() {
    pagamento = JSON.parse(localStorage.getItem('pagamento'));
    pedido.frete = JSON.parse(localStorage.getItem('frete'));
    if(pedido.frete === 0){
     pedido.frete = Math.floor(Math.random() * 100) + 5;
    }
    pedido.subTotal = calcultarTotal();
    pedido.total = pedido.subTotal + pedido.frete;
    pedido.qtdParcela = pagamento.qtdParcela;
    pedido.precoParcela = pedido.total / pedido.qtdParcela;
    pedido.formaPagamento = pagamento.formaPagameto;
    if (pedido.formaPagamento == 'Cartão') {
        pedido.textoParcela = '<br/>(Parcelado em ' + pagamento.qtdParcela + 'x de '
            + formatPreco(pedido.precoParcela) + ')<br/>';
    }
}

function calcultarTotal() {
    subTotal = 0;
    for (i = 0; i < carrinho.length; i++) {
        subTotal += carrinho[i].produto.preco * carrinho[i].quantidadeCompra;
    }
    return subTotal;
}

function formatPreco(preco) {
    return preco.toLocaleString('pt-BR', { style: 'currency', currency: 'BRL' });
}

function calcultarItens() {
    itens = 0;
    for (i = 0; i < carrinho.length; i++) {
        itens += carrinho[i].quantidadeCompra;
    }
    return itens;
}

function alterarCEP() {
    cep = "";
    (async () => {
        //  const { value: formValues } = await Swal.fire({
        //    title: 'Informe o cep',
        //    html:'<input id="cep" class="swal2-input">',
        //    focusConfirm: false,
        //    preConfirm: () => {
        //      return [
        //          cep = document.getElementById('cep').value,
        //      ]
        //    }
        //  })

        // const { value: formValues } = await Swal.fire({

        //     title: 'Informe o cep',
        //     html: '<div class="col-md-9" > <h3 style="color: rgb(0,0,0);">Endereços</h3><div id="enderecos"></div><div class="row row-cols-5 justify-content-center">'
        //         + '<div class="col offset-1"><button class="btn btn-block border rounded shadow btn-black" type="button" onclick="listarEnderecos()" style="margin-top: 38px;">Adicionar mais</button></div>'
        //         + '</div>'
        //         + '</div >',
        //     focusConfirm: false,
        //     preConfirm: () => {
        //         return [
        //             cep = document.getElementById('cep').value,
        //         ]
        //     }
        // })

        const { value: cep } = await Swal.fire({
            title: 'Informe o cep',
            input: 'text',
            inputPlaceholder: 'Informe o cep'
        })
        if (cep) {
            valorFrete = Math.floor(Math.random() * 100) + 5;
            preencherCEP = "Alterar CEP";
            $("#btnCEP").html("Alterar CEP");
            $("#numCEP").html("FRETE: " + cep);
            $("#precoCEP").html("");
            $("#precoCEP").html(formatPreco(precoFrete));
            precoTotal = calcultarTotal(precoFrete);
            $("#precoTotal").html(precoTotal);
        }
    })()
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
            if (result.perfil === 'Cliente') {
                clienteLogado = true;
                //  $("#aLogin").hide();
                //  $("#aNome").html(result.nome);
            } else {
                //  $("#aConta").hide();
                //  $("#aPedidos").hide();
                //  $("#aLogout").hide();
                //  $("#aEnderecos").hide();
            }
        },
        error: function (jqXHR, textStatus, errorThrown) {

        },
    });
}

function salvarPedido() {
    calcultarTotal(0);
    precoFrete = 16;
    qtdParcelas = 3;
    loadMsg("Concluindo pedido")
    $.ajax({
        type: 'POST',
        url: '/pedido?idEndereco=' + endereco.id + '&precoTotal=' + pedido.total + '&precoFrete=' + pedido.frete + '&qtdParcelas=' + pedido.qtdParcela + '&formaPagamento=' + pedido.formaPagamento,
        contentType: 'application/json;charset=UTF-8',
        headers: {
            Accept: "application/json;charset=UTF-8",
            "Content-Type": "application/json;charset=UTF-8"
        },
        success: function (resultPedido) {
            const swalWithBootstrapButtons = Swal.mixin({
                customClass: {
                    confirmButton: 'btn btn-black',
                    cancelButton: 'btn btn-black'
                },
            })

            swalWithBootstrapButtons.fire({
                icon: 'success',
                title: '<span style="color: rgb(104,201,45);">Pedido concluído com sucesso</span>',
                text: 'Nº pedido:' + resultPedido.numeroPedido + ' | valor total:' + formatPreco(resultPedido.precoTotal),
                showCancelButton: true,
                confirmButtonText: 'Comprar mais',
                cancelButtonText: 'Ver pedidos',
            }).then((result) => {
                if (result.isConfirmed) {
                    window.location.href = '/index';
                } else if (
                    /* Read more about handling dismissals below */
                    result.dismiss === Swal.DismissReason.cancel
                ) {
                    window.location.href = '/clientes/pedidos';
                }
            })
        },
        error: function (jqXHR, textStatus, errorThrown) {

        },
    });
}

function getEndereco() {
    endereco = JSON.parse(localStorage.getItem('endereco-selecionado'));
}

function alterarEndereco() {
    localStorage.setItem('endereco', true);
    window.location.href = '/pedido/endereco';
}