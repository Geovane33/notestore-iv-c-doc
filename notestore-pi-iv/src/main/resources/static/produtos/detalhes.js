$(document).ready(function () {
    init();
});

produto = {};
visualizarCar = false;
function init() {
    carregarProduto();
}

function loadMsg(msg) {
    Swal.fire({
        title: msg,
        onBeforeOpen: () => {
            Swal.showLoading();
        }
    });
}

function carregarProduto() {
    if (localStorage.getItem('produto-detalhes') != null) {
        produto = JSON.parse(localStorage.getItem('produto-detalhes'));
        $("#id").val(produto.id);
        $("#nome").html(produto.nome);
        $("#marca").html("<strong>Marca: </strong>" + produto.marca);
        $("#quantidade").html("Quantidade Disponivel: " + produto.quantidade);
        $("#preco").html(produto.preco.toLocaleString('pt-BR', { style: 'currency', currency: 'BRL' }));
        carregarLinksImagens();
        carregarFaqs(produto);
        $("#descricao").html(produto.descricao);
        $("#faq").html(produto.faq);
        //localStorage.removeItem('produto-detalhes');
    }
}

function carregarLinksImagens() {
    $("#imagens").append('<div class="carousel-item active"><img class="img-fluid width" src="' + produto.imagens[0].link + '" alt="Imagem do produto"></div>');
    for (var i = 1; i < produto.imagens.length; i++) {
        $("#imagens").append('<div class="carousel-item"><img class="img-fluid width" src="' + produto.imagens[i].link + '" alt="Imagem do produto"></div>');
    }
}

function carregarFaqs(produto){
    for(var i =0 ;i < produto.faqs.length ; i++){
        $('#faq').append('<p class="m-3"> <strong>Pergunta: </strong> '+produto.faqs[i].pergunta+' </p><p class="m-3"> <strong>Resposta: </strong> '+produto.faqs[i].resposta+' </p>');
    }
}

function addProdCar() {
    loadMsg('Carregando');
    visualizarCar = false;
    $.ajax({
        type: 'POST',
        url: '/carrinho?idProduto=' + produto.id + '&quantidade=1',
        contentType: 'application/json;charset=UTF-8',
        headers: {
            Accept: "application/json;charset=UTF-8",
            "Content-Type": "application/json;charset=UTF-8"
        },
        success: function (result) {
            if(visualizarCar){
                window.location.href = '/clientes/carrinho';
            }
            Swal.close();
        },
        error: function (jqXHR, textStatus, errorThrown) {

        },
    });
}
function visualizarCarrinho(){
   visualizarCar = true;
}