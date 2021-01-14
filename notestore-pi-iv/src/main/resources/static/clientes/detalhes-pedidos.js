enderecos = [];
$(document).ready(function () {
    init();
});

function init() {
    carregarEnderecos();
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

function logout(){
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
    for (i = 0; i < enderecos.length-contCard; i++) {
        cardsEndereco.push('<div class="row">');
        for (j = contCard; j < contCard + 2; j++) {
            cardsEndereco.push(cardEnderecos(j));
            contCard++;
            if(enderecos.length === contCard){
                    break;
            }
        }
        cardsEndereco.push("</div>");
    }
    $("#enderecos").append(cardsEndereco.join(""));
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

function excluirEndereco(i) {
    if(enderecos.length === 1){
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
                    200: function(){
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
                $("#aLogin").hide();
                $("#aNome").html(result.nome);
        },
        error: function (jqXHR, textStatus, errorThrown) {

        },
    });
}