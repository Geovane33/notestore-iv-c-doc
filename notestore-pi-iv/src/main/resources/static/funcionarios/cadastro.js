
funcionarios = [];

$(document).ready(function () {
    init();
});

function init() {
    formCadastro();
    editarFuncionario();
}

function loadMsg(msg) {
    Swal.fire({
        title: msg,
        onBeforeOpen: () => {
            Swal.showLoading();
        }
    });
}


function editarFuncionario() {
    if (localStorage.getItem('func-editar') != null) {
        funcionarios = JSON.parse(localStorage.getItem('func-editar'));
        $("#id").val(funcionarios.id);
        $("#nome").val(funcionarios.nome);
        $("#sobrenome").val(funcionarios.sobrenome);
        $("#cpf").val(funcionarios.cpf);
        $("#dataNascimento").val(funcionarios.dataNascimento);
        $("#telefone").val(funcionarios.telefone);
        $("#sexo").val(funcionarios.sexo);
        $("#cep").val(funcionarios.cep);
        $("#rua").val(funcionarios.rua);
        $("#numero").val(funcionarios.numero);
        $("#bairro").val(funcionarios.bairro);
        $("#estado").val(funcionarios.estado);
        $("#cidade").val(funcionarios.cidade);
        $("#perfil").val(funcionarios.perfil);
        $("#email").val(funcionarios.email);
        $("#email").addClass("ocultar");
        $("#emailLabel").addClass("ocultar");
        localStorage.removeItem('func-editar');
    }
}

function formCadastro() {
    $('#cadastro').ajaxForm({
        onsubmit: function (event) {
        },
        beforeSend: function (xhr) {
            loadMsg("Enviando!");
        },
        success: function (result, textStatus, jqXHR) {
        },
        error: function (jqXHR, textStatus, errorThrown) {
            Swal.fire({
                icon: 'error',
                title: 'Erro <br> Erro: 404',
                showConfirmButton: true
            })
        },
        statusCode: {
            200: function () {
                Swal.fire({
                    icon: 'success',
                    title: 'Salvo com sucesso',
                    showConfirmButton: false,
                    timer: 1500
                })
                setTimeout(function () {
                    window.location.reload();
                }, 1500);
            },
            201: function () {
                Swal.fire({
                    icon: 'success',
                    title: 'Salvo com sucesso',
                    showConfirmButton: false,
                    timer: 1500
                })
                setTimeout(function () {
                    window.location.reload();
                }, 1500);
            },
            400: function () {
                Swal.fire({
                    icon: 'warning',
                    title: 'Revise os campos <br> Erro: 400',
                    showConfirmButton: true
                })
            },
            401: function () {
                Swal.fire({
                    icon: 'error',
                    title: 'Não autorizado',
                    showConfirmButton: true
                })
            },
            304: function () {
                Swal.fire({
                    icon: 'warning',
                    title: 'Não foi possivel salvar! <br> CPF ou e-mail já utilizado(s)',
                    showConfirmButton: true
                })
            },
            500: function () {
                Swal.fire({
                    icon: 'error',
                    title: 'Erro ao processar os dados <br> Erro: 500',
                    showConfirmButton: true
                })
            }
        }
        //error: function (jqXHR, textStatus, errorThrown) {}
    });
}