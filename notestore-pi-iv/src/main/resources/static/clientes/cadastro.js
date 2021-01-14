cepValido = false;
cpfValido = false;

$(document).ready(function () {
    init();
});

function init() {
    formCadastro();
    setMask();
}

function loadMsg(msg) {
    Swal.fire({
        title: msg,
        onBeforeOpen: () => {
            Swal.showLoading();
        }
    });
}

function formCadastro() {
    $('#cadastro').ajaxForm({
        onsubmit: function (event) {
        },
        beforeSend: function (xhr) {
                    if(!cepValido){
                        Swal.fire({
                            icon: 'error',
                            title: 'Verifique o cep informado',
                            showConfirmButton: true
                        })
                           return false;
                    }
                                    if(!cpfValido){
                                            Swal.fire({
                                                icon: 'error',
                                                title: 'cpf inválido, verifique o cpf digitado',
                                                showConfirmButton: true
                                            })
                                               return false;
                                        }
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
                    title: 'Cadastro realizado com sucesso',
                    showConfirmButton: false,
                    timer: 1500
                })
                setTimeout(function () {
                    window.location.href = '/login';
                }, 1500);

            },
            201: function () {
                Swal.fire({
                    icon: 'success',
                    title: 'Cadastro realizado com sucesso',
                    showConfirmButton: false,
                    timer: 1500
                })
                setTimeout(function () {
                    window.location.href = '/login';
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
    });
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

function validarCPF() {
        strCPF = $('#cpf').val();
        strCPF = strCPF.replace(/([^\d])+/gim, '');
		var Soma, Resto, borda_original;
		Soma = 0;
                if (strCPF.length != 11 ||
                    strCPF == "00000000000" ||
                    strCPF == "11111111111" ||
                    strCPF == "22222222222" ||
                    strCPF == "33333333333" ||
                    strCPF == "44444444444" ||
                    strCPF == "55555555555" ||
                    strCPF == "66666666666" ||
                    strCPF == "77777777777" ||
                    strCPF == "88888888888" ||
                    strCPF == "99999999999"){
                       document.getElementById("cpf").setCustomValidity('CPF inválido');
                       cpfValido = false;
                       return;
                    }


		if (strCPF == "00000000000"){
			document.getElementById("cpf").setCustomValidity('CPF inválido');
			cpfValido = false;
			return;
		}

		for (i=1; i<=9; i++){
			Soma = Soma + parseInt(strCPF.substring(i-1, i)) * (11 - i);
		}

		Resto = (Soma * 10) % 11;
		if ((Resto == 10) || (Resto == 11)){
			Resto = 0;
		}

		if (Resto != parseInt(strCPF.substring(9, 10))){
			document.getElementById("cpf").setCustomValidity('CPF inválido');
			cpfValido = false;
            return;
		}

		Soma = 0;
		for (i = 1; i <= 10; i++){
			Soma = Soma + parseInt(strCPF.substring(i-1, i)) * (12 - i);
		}

		Resto = (Soma * 10) % 11;
		if ((Resto == 10) || (Resto == 11)){
			Resto = 0;
		}

		if (Resto != parseInt(strCPF.substring(10, 11))){
			document.getElementById("cpf").setCustomValidity('CPF inválido');
			cpfValido = false;
            return;
		}

		document.getElementById("cpf").setCustomValidity('');
		cpfValido = true;
		return;
	}

function setMask() {
        $('#cpf').mask('ZZZ.ZZZ.ZZZ-ZZ', {
            translation: {
                'Z': {
                    pattern: /[0-9]/, optional: false
                }
            },
            placeholder: "___.___.___-__"
        });
    }