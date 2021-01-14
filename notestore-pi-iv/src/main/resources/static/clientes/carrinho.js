clienteLogado = false;
precoFrete = 0;
precoTotal = 0;
carrinho = [];
enderecos = [];
preencherCEP = "Adicionar CEP";
enderecos = [];
numeroCep = 0;
cepValido= true;
$(document).ready(function () {
    init();
});

function init() {
    carregarCarrinho();
    carregarEnderecos();
    setItensVisible();
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
            + '<div class="col"><strong class="d-inline-block">' + carrinho[i].produto.nome + '<br></strong><a class="text-body" style="cursor: pointer" onclick="excluirProdCar(' + i + ')" ><img  src="/icons/remove.png" class="icon float-right"></img></a></div>'
            + '</div>'
            + '<div class="row">'
            + '<div class="col"><span><strong>Marca:</strong>' + carrinho[i].produto.marca + '</span></div>'
            + '</div>'
            + '<div class="row">'
            + '<div class="col"><span><strong>Unidade:</strong>&nbsp;' + precoProduto + '</span></div>'
            + '</div>'
            + '<div class="row">'
            + '<div class="col"><span><strong>Quantidade:&nbsp;</strong></span><a class="text-body" style="cursor: pointer" onClick="atualizarQtd(' + i + ',-1)" ><img src="../icons/rounded-remove.png" class="icon" alt=""></a><span><strong>&nbsp;</strong><span class="border"><strong>&nbsp;' + carrinho[i].quantidadeCompra + '&nbsp;</strong></span></span>'
            + '<a class="text-body" style="cursor: pointer" onClick="atualizarQtd(' + i + ',1)" ><img src="/icons/add.png" class="icon" alt=""></a>'
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

function atualizarQtd(i, qtd) {
    loadMsg('Carregando');
    $.ajax({
        type: 'POST',
        url: '/carrinho?idProduto=' + carrinho[i].produto.id + '&quantidade=' + qtd,
        contentType: 'application/json;charset=UTF-8',
        headers: {
            Accept: "application/json;charset=UTF-8",
            "Content-Type": "application/json;charset=UTF-8"
        },
        success: function (result) {
            if (result === 202) {
                Swal.fire({
                    icon: 'warning',
                    title: 'Não temos estoque suficiente para a quantidade informada',
                    showConfirmButton: true
                });
            } else {
                carrinho[i].quantidadeCompra = carrinho[i].quantidadeCompra + qtd;
                if (carrinho[i].quantidadeCompra <= 0) {
                    carrinho.splice(i, 1);
                }
                listarCarrinho();
                Swal.close();
            }

        },
        error: function (jqXHR, textStatus, errorThrown) {

        },
    });
}

function excluirProdCar(i) {
    $.ajax({
        type: 'DELETE',
        url: '/carrinho?id=' + carrinho[i].produto.id,
        contentType: 'application/json;charset=UTF-8',
        headers: {
            Accept: "application/json;charset=UTF-8",
            "Content-Type": "application/json;charset=UTF-8"
        },
        success: function (result) {
            carrinho.splice(i, 1);
            listarCarrinho();
        },
        error: function (jqXHR, textStatus, errorThrown) {

        },
    });
}

function resumoCompra() {
    $("#resumoCompra").html("");
    subTotal = calcultarTotal(0);
    qtdItens = calcultarItens();
    strItem = 'item'
    if (qtdItens > 1) {
        strItem = 'itens';
    }
    $("#resumoCompra").append('<div class="col">'
        + '<div class="border rounded resumo-compras">'
        + '<div class="col"><span>Subtotal(' + qtdItens + ' ' + strItem + ')</span><strong class="float-right">' + subTotal + '</strong></div>'
        + '<hr>'
        + '<div class="col"><strong id="numCEP">INFORME O CEP PARA VER O VALOR DE ENTREGA</strong><strong class="float-right" id="precoCEP"></strong></div>'
        + '<div class="col"><a class="text-black-50 d-inline-block" st><span style="cursor: pointer" class="texto-underline" id="btnCEP" onClick="alterarCEP()">' + preencherCEP + '</span> </a><span class="float-right"></span></div>'
        + '<hr>'
        + '<div class="col"><span>Descontos</span><strong class="float-right" >R$ 0,00</strong>'
        + '</div>'
        + '<hr>'
        + '<div class="col"><span>Valor total</span><strong class="float-right"id="precoTotal">' + subTotal + '</strong></div>'
        + '<button class="btn btn-block shadow btn-black mt-3" onClick="prosseguirComPedido()" type="button"><strong>PROSSEGUIR</strong></button>'
        + '<form action="/index" method="GET">'
        + '<button class="btn btn-block shadow btn-black mt-3" type="submit"><strong>ESCOLHER MAIS PRODUTOS</strong></button>'
        + '</form>'
        + '</div>'
        + '</div>');
     if(enderecos.length < 1000 && enderecos.length > 0){
        setCep(enderecos[0].cep)
      }else if(precoFrete > 0){
       setCep(numeroCep)
      }
}

function calcultarTotal(maisValor) {
    subTotal = 0;
    for (i = 0; i < carrinho.length; i++) {
        subTotal += carrinho[i].produto.preco * carrinho[i].quantidadeCompra;
    }
    return formatPreco(subTotal + maisValor);
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
        numeroCep = cep;
        validarCep();
        }
    })()
}

function setCep(cep){
if(precoFrete === 0){
   precoFrete = Math.floor(Math.random() * 100) + 5;
   }
            preencherCEP = "Alterar CEP";
            $("#btnCEP").html("Alterar CEP");
            $("#numCEP").html("FRETE: " + cep);
            $("#precoCEP").html("");
            $("#precoCEP").html(formatPreco(precoFrete));
            precoTotal = calcultarTotal(precoFrete);
            $("#precoTotal").html(precoTotal);
}

function cardEnderecos(j) {
    return  '<div class="col-6">'
                +'<div style="box-shadow: 0 0 8px 0px;">'
                    +'<div class="card">'
                         +'<div class="card-body">'
                            +'<h4 class="card-title">'+ enderecos[j].nome +'</h4>'
                            +'<p class="card-text">'+ enderecos[j].rua + ', ' + enderecos[j].numero
                            + '<br>' + enderecos[j].bairro + ' - ' + enderecos[j].cidade + ' - ' + enderecos[j].estado
                            + '<br>'+ enderecos[j].cep +'</p>'
                            +'<hr><a class="card-link text-black-50" href="#" onclick="editarEndereco('+j+')" ><i class="fa fa-edit"></i>Alterar</a><a class="card-link text-black-50" href="#" onclick="excluirEndereco('+j+')"><i class="fas fa-trash-alt"></i>Excluir</a>'
                         +'</div>'
                    +'</div>'
                +'</div>'
            +'</div>';
    }


    function listarEnderecos() {
        $("#enderecos").html("");
        cardsEndereco = [];
        contCard = 0;
        for (i = 0; i < enderecos.length-contCard; i++) {
            cardsEndereco.push('<div class="row">');
            for (j = contCard; j < contCard + 2; j++) {
                cardsEndereco.push(cardEnderecos(0));
                contCard++;
                if(enderecos.length === contCard){
                        break;
                }
            }
            cardsEndereco.push("</div>");
        }
        $("#enderecos").append(cardsEndereco.join(""));
    }

    function carregarEnderecos() {
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
                }
    
            },
            error: function (jqXHR, textStatus, errorThrown) {
//                Swal.fire({
//                    icon: 'error',
//                    title: 'Erro ao carregar',
//                    showConfirmButton: true
//                })
            }
        });
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
            endereco = result.endereco;
            if (result.perfil === 'Cliente') {
                cepValido = true;
                //clienteLogado = true;
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

function prosseguirComPedido(){
if(carrinho.length ===0){
 Swal.fire({
                icon: 'error',
                title: 'Carrinho vazio',
                showConfirmButton: true
            })
            return;
}
if(cepValido){
  sessionStorage.setItem('index', false);
  localStorage.setItem('frete', JSON.stringify(precoFrete));
  window.location.href = '/pedido/endereco';
}else{
 Swal.fire({
                icon: 'error',
                title: 'Verifique o cep, cep não informado ou inválido',
                showConfirmButton: true
            })
}

}

function setItensVisible(){

};



function validarCep() {
    loadMsg("validando cep!");
    $.ajax({
        type: 'GET',
        url: 'https://viacep.com.br/ws/'+numeroCep+'/json',
        contentType: 'application/json;charset=UTF-8',
        headers: {
            Accept: "application/json;charset=UTF-8",
            "Content-Type": "application/json;charset=UTF-8"
        },
        success: function (cepResult) {
            precoFrete =0;
            setCep(numeroCep);
            Swal.close();
            cepValido = true;
        },
        error: function (jqXHR, textStatus, errorThrown) {
            Swal.fire({
                icon: 'error',
                title: 'Erro ao validar cep, verifique o cep digitado',
                showConfirmButton: true
            })
        },
        statusCode: {
            400: function () {
                Swal.fire({
                    icon: 'error',
                    title: 'Verifique o cep digitado <br> Erro 400',
                    showConfirmButton: true
                })
            },
        },
    });
}
