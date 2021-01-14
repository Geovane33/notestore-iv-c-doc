endereco = {};
cepValido = true;
$(document).ready(function () {
    init();
});

function init() {
sessionStorage.removeItem('index');
carregarEnderecos();
setItensVisible();
// getAcesso();
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
                        +'<hr><a class="card-link text-black-50" style="cursor:pointer" onclick="selecionarEndereco('+j+')" >Selecionar</a>'
                     +'</div>'
                +'</div>'
            +'</div>'
        +'</div>';
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
    $.ajax({
        type: 'GET',
        url: 'https://viacep.com.br/ws/'+cep+'/json',
        contentType: 'application/json;charset=UTF-8',
        headers: {
            Accept: "application/json;charset=UTF-8",
            "Content-Type": "application/json;charset=UTF-8"
        },
        success: function (cepResult) {
        $('#msgCep').html("");
        $("#rua").val(cepResult.logradouro);
        $("#bairro").val(cepResult.bairro);
        $("#cidade").val(cepResult.localidade);
        $("#estado").val(cepResult.uf);
        $("#cep").val(cepResult.cep);
            cepValido = true;
        },
        error: function (jqXHR, textStatus, errorThrown) {
          $('#msgCep').html("CEP inválido, digite um CEP válido");
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


function selecionarEndereco(i) {
    localStorage.setItem('endereco-selecionado', JSON.stringify(enderecos[i]));
    if (localStorage.getItem('endereco')==='true') {
        localStorage.setItem('frete', JSON.stringify(Math.floor(Math.random() * 100) + 5));
        localStorage.setItem('endereco', false);
        window.location.href = '/pedido/verificar';
        return;
    }
    // Swal.fire({
    //     title: 'Forma de pagamento',
    //     showCancelButton: true,
    //     confirmButtonText: 'Boleto',
    //     cancelButtonText: 'Cartão',
    // })
//     title: 'Are you sure?',
//   text: "You won't be able to revert this!",
//   icon: 'warning',
//   showCancelButton: true,
//   confirmButtonText: 'Yes, delete it!',
//   cancelButtonText: 'No, cancel!',
//   reverseButtons: true
const swalWithBootstrapButtons = Swal.mixin({
    customClass: {
      confirmButton: 'btn btn-black',
      cancelButton: 'btn btn-black'
    },
    buttonsStyling: false
  })
  
  swalWithBootstrapButtons.fire({
     title: 'Forma de pagamento',
     showCancelButton: true,
     confirmButtonText: 'Boleto',
     cancelButtonText: 'Cartão',
  }).then((result) => {
    if (result.isConfirmed) {
        pagamento = {
            qtdParcela: 0,
            formaPagameto: 'Boleto',
        }
            localStorage.setItem('pagamento', JSON.stringify(pagamento));
            window.location.href = '/pedido/verificar';
    } else if (
      /* Read more about handling dismissals below */
      result.dismiss === Swal.DismissReason.cancel
    ) {
        window.location.href = '/pedido/cad-cartao';
    }
  })

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
function setItensVisible(){
    if (localStorage.getItem('endereco')==='true') {
        $("#imgEtapas").hide();
    }
};

function adicionarEndereco() {
formEndereco();
    (async () => {
             const { value: formValues } = await Swal.fire({
           title: 'Novo endereço',
            html:'<form id="endereco" action="/clientes/enderecos" method="POST">'
            +'<input id="nome" name="nome" required placeholder="nome/apelido" class="swal2-input">'
            +'<span id="msgCep" style="color: red"></span>'
            +'<input id="cep" name="cep" required placeholder="cep" onchange="validarCep()" class="swal2-input">'
            +'<input id="rua" name="rua" required placeholder="rua" class="swal2-input">'
            +'<input id="bairro" name="bairro" required placeholder="bairro" class="swal2-input">'
            +'<input id="cidade" name="cidade" required placeholder="cidade" class="swal2-input">'
            +'<input id="numero" name="numero" required placeholder="numero" class="swal2-input">'
            +'<input id="estado" name="estado" required placeholder="estado" class="swal2-input">'
            +'<button class="btn btn-block shadow btn-black" type="submit"> SALVAR </button>'
            +'</form>',
            focusConfirm: false,
            showConfirmButton: false,
            preConfirm: () => {
              return []
            }
          })
        })()
        formEndereco();
}

function formEndereco() {
    $('#endereco').ajaxForm({
        onsubmit: function (event) {
        }
        , beforeSend: function (xhr) {
        $('#msgCep').html("");
            if(!cepValido){
            $('#msgCep').html("CEP inválido, digite um CEP válido");
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
                title: 'Erro ao salvar',
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
                                                      window.location.href = '/pedido/endereco';
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
