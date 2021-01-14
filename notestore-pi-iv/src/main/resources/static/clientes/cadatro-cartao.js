$(document).ready(function () {
    init();
});

function init() {
    formCadCartao();
}

function loadMsg(msg) {
    Swal.fire({
        title: msg,
        onBeforeOpen: () => {
            Swal.showLoading();
        }
    });
}

function salvarCartao() {
    pagamento = {
        qtdParcela: $('#qtdParcelas').val(),
        formaPagameto: 'Cartão',
    }
    localStorage.setItem('pagamento', JSON.stringify(pagamento));
    window.location.href = '/pedido/verificar';
}

function formCadCartao() {
    $('#cadCartao').ajaxForm({
        onsubmit: function (event) {

        },
        beforeSend: function (xhr) {
            salvarCartao();
        },
    });
}