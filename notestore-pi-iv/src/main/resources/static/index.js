carousel = [];
produtos = [];
$(document).ready(function () {
    init();
});

function init() {
    carregarProdutos();
    formPesquisar();
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

function carregarProdutos() {
    loadMsg("Carregando!");
    $.ajax({
        type: 'GET',
        url: '../produtos',
        contentType: 'application/json;charset=UTF-8',
        headers: {
            Accept: "application/json;charset=UTF-8",
            "Content-Type": "application/json;charset=UTF-8"
        },
        success: function (result) {
            if (result.length === 0) {
                Swal.fire({
                    icon: 'warning',
                    title: 'Nenhum produto cadastrado',
                    showConfirmButton: true
                })
            } else {
                produtos = result;
                listarProdutos();
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



function listarProdutos() {
    $("#categoriaProf").hide();
    $("#categoriaGamer").hide();
    $("#categoriaDia").hide();
    $("#categoria" + 1).html("");
    $("#categoria" + 2).html("");
    $("#categoria" + 3).html("");

    iniciarCarousel(1);
    iniciarAddItem(0);
    var qtdItensPorCarousel = 0;
    var gamer = [];
    var profissinal = [];
    var diaADia = [];
    for (var i = 0; i < produtos.length; i++) {
        if (produtos[i].categoria === "GAMER") {
            gamer.push(produtos[i]);
        }
        if (produtos[i].categoria === "PROFISSIONAL") {
            profissinal.push(produtos[i]);
        }
        if (produtos[i].categoria === "DIA A DIA") {
            diaADia.push(produtos[i]);
        }
    }

    for (var i = 0; i < gamer.length; i++) {
        $("#categoriaGamer").show();
        indiceProd = 0;
        for (var j = 0; j < produtos.length; j++) {
            if (gamer[i].id === produtos[j].id) {
                indiceProd = j;
                break;
            }
        }
        addItemProd(indiceProd);
        qtdItensPorCarousel++;
        if (qtdItensPorCarousel === 4) {
            qtdItensPorCarousel = 0;
            finalizarAddItem();
            if (i < gamer.length - 1) {
                iniciarAddItem(1);
            }
        } else if (i >= gamer.length - 1) {
            finalizarAddItem();
        }
    }
    if (gamer.length <= 4) {
        finalizarCarouselSemNav();
    } else {
        finalizarCarouselComNav(1);
    }
    $("#categoria" + 1).append(carousel.join(""));
    carousel = [];
    //segunda categoria
    iniciarCarousel(2);
    iniciarAddItem(0);
    var qtdItensPorCarousel = 0;
    for (var i = 0; i < profissinal.length; i++) {
        $("#categoriaProf").show();
        indiceProd = 0;
        for (var j = 0; j < produtos.length; j++) {
            if (profissinal[i].id === produtos[j].id) {
                indiceProd = j;
                break;
            }
        }
        addItemProd(indiceProd);
        qtdItensPorCarousel++;
        if (qtdItensPorCarousel === 4) {
            qtdItensPorCarousel = 0;
            finalizarAddItem();
            if (i < profissinal.length - 1) {
                iniciarAddItem(1);
            }
        } else if (i >= profissinal.length - 1) {
            finalizarAddItem();
        }

    }

    if (profissinal.length <= 4) {
        finalizarCarouselSemNav();
    } else {
        finalizarCarouselComNav(2);
    }

    $("#categoria" + 2).append(carousel.join(""));
    carousel = [];

    //terceira categoria
    iniciarCarousel(3);
    iniciarAddItem(0);
    var iterarItemCaoursel = 0;
    var qtdItemsPorCasousel = 4;
    for (var i = 0; i < diaADia.length; i++) {
        $("#categoriaDia").show();
        indiceProd = 0;
        for (var j = 0; j < produtos.length; j++) {
            if (diaADia[i].id === produtos[j].id) {
                indiceProd = j;
                break;
            }
        }
        addItemProd(indiceProd);
        iterarItemCaoursel++;
        if (iterarItemCaoursel === qtdItemsPorCasousel) {
            iterarItemCaoursel = 0;
            finalizarAddItem();
            if (i < diaADia.length - 1) {
                iniciarAddItem(1);
            }
        } else if (i >= diaADia.length - 1) {
            finalizarAddItem();
        }
    }
    if (diaADia.length <= qtdItemsPorCasousel) {
        finalizarCarouselSemNav();
    } else {
        finalizarCarouselComNav(3);
    }

    $("#categoria" + 3).append(carousel.join(""));
    carousel = [];
}

function visualizarProd(indice) {
    localStorage.setItem('produto-detalhes', JSON.stringify(produtos[indice]));
    window.location.href = '/produtos/detalhes';
}

function iniciarCarousel(i) {
    carousel.push('<div id="carouselProdutos' + i + '" class="carousel slide">'
        + '<div class="carousel-inner">')
}

function iniciarAddItem(i) {
    if (i === 0) {
        carousel.push('<div class="carousel-item active">'
            + '<div id="" class="row container-fluid">'
            + '<div class="col-sm-2"></div>')
    } else {
        carousel.push('<div class="carousel-item">'
            + '<div id="" class="row container-fluid">'
            + '<div class="col-sm-2"></div>')
    }
}

function addItemProd(indice) {
    var precoProd = produtos[indice].preco.toLocaleString('pt-BR', { style: 'currency', currency: 'BRL' });
    carousel.push('<div class="card-produto col-sm-2">'
        + '<div class="border mt-1">'
        + '<span onclick="visualizarProd(' + indice + ')">'
        + '<img class="item-produto img-pointer" src="' + produtos[indice].imagens[0].link + '">'
        + '<div class="dados-produto">'
        + '<h6>' + precoProd + '</h6>'
        + '<span>' + produtos[indice].nome + '</span>'
        + '</div>'
        + '</span>'
        + '</div>'
        + '</div>')
}

function finalizarAddItem() {
    carousel.push('<div class="col-sm-2"></div>'
        + '</div>'
        + '</div>')
}

function finalizarCarouselSemNav() {
    carousel.push('</div>'
        + '</div>')
}

function finalizarCarouselComNav(i) {
    carousel.push('</div>'
        + '<a class="carousel-control-prev" href="#carouselProdutos' + i + '" role="button" data-slide="prev">'
        + '<img src="icons/chevron-left.svg" aria-hidden="true" alt="">'
        + '<span class=sr-only>Anterior</span>'
        + '</a>'
        + '<a class="carousel-control-next" href="#carouselProdutos' + i + '" role="button" data-slide="next">'
        + '<img src="icons/chevron-right.svg" aria-hidden="true" alt="">'
        + '<span class=sr-only>Avancar</span>'
        + '</a>'
        + '</div>')

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
                    title: 'Nenhum produto encontrado',
                    showConfirmButton: true
                })
            } else {
                produtos = [];
                produtos = result;
                listarProdutos();
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


function logout() {
    Swal.fire({
        icon: 'success',
        title: 'Logout efetuado com sucesso! <br> sessão finalizada',
        showConfirmButton: false
    })
    setTimeout(function () {
        window.location.href = '/logout';
    }, 2000);
}