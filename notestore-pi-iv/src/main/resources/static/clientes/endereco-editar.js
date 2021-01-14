endereco = {};
cepValido = true;
$(document).ready(function () {
    init();
});

function init() {
carregarEndereco();
formEndereco();
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
                        +'<hr><a class="card-link text-black-50" href="#" onclick="alterarEndereco('+j+')" ><i class="fa fa-edit"></i>Alterar</a><a class="card-link text-black-50" href="#" onclick="excluirEndereco('+j+')"><i class="fas fa-trash-alt"></i>Excluir</a>'
                     +'</div>'
                +'</div>'
            +'</div>'
        +'</div>';
}

function editEndereco(indice) {
    localStorage.setItem('produto-detalhes', JSON.stringify(produtos[indice]));
    window.location.href = '/produtos/detalhes.html';
}


function formEndereco() {
    $('#endereco').ajaxForm({
        onsubmit: function (event) {
        }
        , beforeSend: function (xhr) {
            if(!cepValido){
                Swal.fire({
                    icon: 'error',
                    title: 'Verifique o cep informado',
                    showConfirmButton: true
                })
                   return false;
            }
            loadMsg("Enviando!");
        },
        success: function (result, textStatus, jqXHR) {
        }
        ,
        error: function (jqXHR, textStatus, errorThrown) {
            Swal.fire({
                icon: 'error',
                title: 'Erro ao consultar',
                showConfirmButton: true
            })
        },
                         statusCode: {
                             200: function(){
                                 Swal.fire({
                                     icon: 'success',
                                     title: 'Salvo com sucesso!',
                                     showConfirmButton: false,
                                     timer: 1500
                                 });
                                             setTimeout(function () {
                                                      window.location.href = '/clientes/enderecos-visualizar';
                                                 }, 1500);

                             },
                             400: function () {
                                 Swal.fire({
                                     icon: 'error',
                                     title: 'Erro: 400',
                                     showConfirmButton: true
                                 })
                             },
                             400: function () {
                                 Swal.fire({
                                     icon: 'error',
                                     title: 'Erro interno: 500',
                                     showConfirmButton: true
                                 })
                             },
                         },
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

function carregarEndereco() {
    if (localStorage.getItem('edit-endereco') != null) {
        endereco = JSON.parse(localStorage.getItem('edit-endereco'));
        $("#id").val(endereco.id);
        $("#nome").val(endereco.nome);
        $("#numero").val(endereco.numero);
        $("#rua").val(endereco.rua);
        $("#bairro").val(endereco.bairro);
        $("#cidade").val(endereco.cidade);
        $("#estado").val(endereco.estado);
        $("#cep").val(endereco.cep);
    }
}
  
function validarCep() {
    cep = $('#cep').val();
    loadMsg("validando cep!");
    $.ajax({
        type: 'GET',
        url: 'https://viacep.com.br/ws/'+cep+'/json',
        contentType: 'application/json;charset=UTF-8',
        headers: {
            Accept: "application/json;charset=UTF-8",
            "Content-Type": "application/json;charset=UTF-8"
        },
        success: function (cepResult) {
            $("#rua").val(cepResult.logradouro);
            $("#bairro").val(cepResult.bairro);
            $("#cidade").val(cepResult.localidade);
            $("#estado").val(cepResult.uf);
            $("#cep").val(cepResult.cep);
            Swal.close();
            cepValido = true;
        },
        error: function (jqXHR, textStatus, errorThrown) {
            Swal.fire({
                icon: 'error',
                title: 'Erro ao validar cep, verifique o cep digitado',
                showConfirmButton: true
            })
            cepValido = false;
        },
        statusCode: {
            400: function () {
                Swal.fire({
                    icon: 'error',
                    title: 'Verifique o cep digitado <br> Erro 400',
                    showConfirmButton: true
                })
                cepValido = false;
            },
        },
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
            if (result.perfil === 'Cliente') {
                $("#aLogin").hide();
                $("#aNome").html(result.nome);
            } else {
                $("#aConta").hide();
                $("#aPedidos").hide();
                $("#aLogout").hide();
                $("#aEnderecos").hide();
            }
        },
        error: function (jqXHR, textStatus, errorThrown) {

        },
    });
}