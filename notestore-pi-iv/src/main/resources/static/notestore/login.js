$(document).ready(function () {
    init();
});
function init() {
    formLogin();
}

function loadMsg(msg) {
    Swal.fire({
        title: msg,
        onBeforeOpen: () => {
            Swal.showLoading();

        }
    });
}


function formLogin() {
    $('#formLogin').ajaxForm({
        onsubmit: function (event) {
        }
        , beforeSend: function (xhr) {
            loadMsg("");
        },
        success: function (result, textStatus, jqXHR) {
            if (result === '200'){
                setTimeout(function () {
                    Swal.close();
                    if(sessionStorage.getItem('index') === 'false'){
                       localStorage.removeItem('index');
                       window.location.href = '/pedido/endereco';
                    }else{
                    sessionStorage.removeItem('index');
                    window.location.href = '/produtos/estoque';
                    }

                }, 350);
            }
        },
        statusCode: {
            400: function () {
                Swal.fire({
                    icon: 'warning',
                    title: 'Revise os campos',
                    showConfirmButton: true
                })
            },
            401: function () {
                Swal.fire({
                    icon: 'warning',
                    title: 'Endereço de e-mail e/ou senha incorreta.',
                    showConfirmButton: true
                })
            },
            500: function(){
                Swal.fire({
                    icon: 'warning',
                    title: 'Erro no servidor ao processar dados',
                    showConfirmButton: true
                }) 
            },
            404: function(){
                Swal.fire({
                    icon: 'warning',
                    title: 'Endereço de e-mail e/ou senha incorreta.',
                    showConfirmButton: true
                })
            }
            
        },
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