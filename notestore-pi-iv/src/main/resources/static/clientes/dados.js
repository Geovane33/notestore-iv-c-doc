clienteLogado = {};
$(document).ready(function () {
    init();
});

function init() {
    carregarDados();
    formAlterarDados();
    formAlterarSenha();
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



function listarDados() {
    $("#id").val(clienteLogado.id);
    $("#nome").val(clienteLogado.nome);
    $("#sobrenome").val(clienteLogado.sobrenome);
    $("#cpf").val(clienteLogado.cpf);
    $("#dataNascimento").val(clienteLogado.dataNascimento);
    $("#telefone").val(clienteLogado.telefone);
    if(clienteLogado.sexo === 'Feminino'){
        $("#sexoFemi").attr('checked', !$(this).is(':checked'));
    }else{
        $("#sexoMasc").attr('checked', !$(this).is(':checked'));
    }
    
    $("#email").val(clienteLogado.email);
}

function formAlterarDados() {
    $('#formAlterarDados').ajaxForm({
        onsubmit: function (event) {
        }
        , beforeSend: function (xhr) {
            loadMsg("Enviando!");
        },
        success: function (result, textStatus, jqXHR) {
        },
        error: function (jqXHR, textStatus, errorThrown) {
            Swal.fire({
                icon: 'error',
                title: 'Erro ao alterar dados',
                showConfirmButton: true
            })
        },
        statusCode: {
            200: function(){
                Swal.fire({
                    icon: 'success',
                    title: 'Dados alterados com sucesso!',
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
    }
    );
}

function formAlterarSenha() {
    $('#formAlterarSenha').ajaxForm({
        onsubmit: function (event) {
        }
        , beforeSend: function (xhr) {
            loadMsg("Enviando!");
        },
        success: function (result, textStatus, jqXHR) {
        },
        error: function (jqXHR, textStatus, errorThrown) {
            Swal.fire({
                icon: 'error',
                title: 'Erro ao alterar senha',
                showConfirmButton: true
            })
        },
        statusCode: {
            200: function(){
                Swal.fire({
                    icon: 'success',
                    title: 'senha alterada com sucesso!',
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
    }
    );
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