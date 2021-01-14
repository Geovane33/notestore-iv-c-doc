funcionarios = [];
$(document).ready(function () {
    init();
});

function init() {
    carregarFuncionarios();
    formPesquisar();
}

function loadMsg(msg) {
    Swal.fire({
        title: msg,
        onBeforeOpen: () => {
            Swal.showLoading();

        }
    });
}

function carregarFuncionarios() {
    loadMsg("Carregando!");
    $.ajax({
        type: 'GET',
        url: '../funcionarios',
        contentType: 'application/json;charset=UTF-8',
        headers: {
            Accept: "application/json;charset=UTF-8",
            "Content-Type": "application/json;charset=UTF-8"
        },
        success: function (result) {
            if (result.length === 0) {
                Swal.fire({
                    icon: 'warning',
                    title: 'Nenhum funcionario encontrado',
                    showConfirmButton: true
                })
            } else {
                funcionarios = result;
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
                    title: 'Nenhum funcionario encontrado',
                    showConfirmButton: true
                })
            } else {
                funcionarios = [];
                funcionarios = result;
                carregaTabela(12);
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

function listarFuncionarios() {

}

/**
 * metodo para manter a tabela "atualizada" remove todas as linhas
 */
function removeLinha() {
    i = document.querySelectorAll("tr").length - 1;
    for (; i > 0; i--) {
        document.getElementById('lista-func').getElementsByTagName('tr')[0].remove();
    }
}

function carregaTabela(pagina) {
    removeLinha();
    var numeroPaginas;
    var numeroLinhas = 12;
    var qtdFuncionarios = funcionarios.length;
    for (numeroPaginas = 0; qtdFuncionarios >= numeroLinhas; numeroPaginas++) {
        qtdFuncionarios -= numeroLinhas;
    }
    if (qtdFuncionarios > 0) {
        numeroPaginas += 2;
    } else if (qtdFuncionarios === 0) {
        numeroPaginas += 1;
    }

    if (pagina >= funcionarios.length) {
        numeroLinhas = funcionarios.length - pagina + numeroLinhas;
        pagina = funcionarios.length;
    }

    for (var i = pagina - numeroLinhas; i < pagina; i++) {
        var linha = $("<tr>");
        var coluna = "";
        coluna += '<td>' + funcionarios[i].nome + '</td>';
        coluna += '<td>' + funcionarios[i].email + '</td>';
        coluna += '<td>' + funcionarios[i].perfil + '</td>';
        coluna += '<td><i class="fas fa-user-edit cursor-pointer" onclick="enviarFuncEdit(' + i + ')" style="font-size: 20px;"></i><i class="fa fa-remove ml-2 cursor-pointer" onclick="excluirFunc(' + i + ')" style="font-size: 21px;"></i></td>';
        linha.append(coluna);
        $("#lista-func").append(linha);
    }
    // $("#paginacao").html("");
    // if (numeroPaginas == 2) {
    //     numeroPaginas = numeroPaginas - 1;
    // }
    // for (var i = 1; i < numeroPaginas; i++) {
    //     var input = $('<input type="button" class="btn btn-black-m btn-dark texto btn-pag" onClick=carregaTabela(' + (12 * i) + ') value="' + i + '"/>');
    //     $("#paginacao").append(input);
    // }
}

function enviarFuncEdit(indice) {
    localStorage.setItem('func-editar', JSON.stringify(funcionarios[indice]));
    window.location.href = 'cadastro.html';
}

/**
 * Excluir funcionario
 * @param {number} idFunc
 */
function excluirFunc(i) {
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
                url: '/funcionarios?id=' + funcionarios[i].id,
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
                        title: 'Erro ao excluir Funcionário',
                        showConfirmButton: true
                    })
                },
                statusCode: {
                    200: function(){
                        funcionarios.splice(i, 1);
                        carregaTabela(12);
                        Swal.fire({
                            icon: 'success',
                            title: 'Funcionário excluído com sucesso!',
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
                    304: function () {
                        Swal.fire({
                            icon: 'warning',
                            title: 'Você não pode deletar seu própio usuário',
                            showConfirmButton: true
                        })
                    }
                },

            });
        }
    }]);
}