$(document).ready(function () {
    init();
});

function init() {
    loadMsg("Não autorizado <br> redirecionando para a pagina inicial")
     setTimeout(function () {
     window.location.href = '/produtos/estoque.html';
     }, 2500);
}

function loadMsg(msg) {
    Swal.fire({
        title: msg,
        onBeforeOpen: () => {
            Swal.showLoading();

        }
    });
}