produto = [];
var qtdFaq = 1;

imgNumeroInputs = 1;
$(document).ready(function () {
    init();
});
function init() {
    formCadastro();
    editarProduto();
    removeAddImagem();
}

function addImagem() {
    $("#removerInput").show();
    imgNumeroInputs++;
    var inputImage = '<input class="form-control input-imagem" onchange="visualizarImagemDoInput(' + imgNumeroInputs + ')" required type="file" accept="image/png, image/jpeg" id="imagem-' + imgNumeroInputs + '" name="imagem-' + imgNumeroInputs + '">'
    $("#imagem").append(inputImage);

    var img = '<img width="50px" id="img-' + imgNumeroInputs + '"  onclick="removerImagem(' + imgNumeroInputs + ')" src="" alt="visualizar imagem...">';
    $("#preview-imagem").append(img);
    $('#img-' + imgNumeroInputs).hide();
}

function removeAddImagem() {
    $("#removerInput").click(function () {
        $("#imagem-" + imgNumeroInputs).remove();
        $("#img-" + imgNumeroInputs).remove();
        imgNumeroInputs--;
        if (imgNumeroInputs == 1) {
            $("#removerInput").hide();
        }
    });
}

function removerImagem(imgNumero) {
    $("#imagem-" + imgNumero).remove();
    $("#img-" + imgNumero).remove();
    if (imgNumeroInputs == 1) {
        $("#removerInput").hide();
    }
}


function addFaq() {
    qtdFaq++;
    $("#faq").append('<label id="faqP' + qtdFaq + '" for="faqPergunta' + qtdFaq + '">Pergunta</label>'
        + '<input required type="text" class="form-control bg-light btn-outline-dark text-body" id="faqPergunta' + qtdFaq + '" name="faqPergunta'+qtdFaq+'">'
        + '<label id="faqR' + qtdFaq + '" for="faqResposta' + qtdFaq + '">Resposta</label>'
        + '<input required type="text" class="form-control bg-light btn-outline-dark text-body" id="faqResposta' + qtdFaq + '" name="faqResposta'+qtdFaq+'"></input>');
}

function removeFaq() {
    if (qtdFaq > 1) {
        $("#faqP" + qtdFaq).remove();
        $("#faqR" + qtdFaq).remove();
        $("#faqPergunta" + qtdFaq).remove();
        $("#faqResposta" + qtdFaq).remove();
        qtdFaq--;
    }

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
        enctype: 'multipart/form-data',
        onsubmit: function (event) {
        },
        beforeSend: function (xhr) {
            loadMsg("Enviando!");
        },
        success: function (result, textStatus, jqXHR) {
            if (result === '200') {
                Swal.fire({
                    icon: 'success',
                    title: 'Salvo com sucesso',
                    showConfirmButton: false,
                    timer: 1500
                })
                setTimeout(function () {
                    window.location.reload();
                }, 1500);
            } else {
                Swal.fire({
                    icon: 'error',
                    title: 'Erro no servidor ao processar dados',
                    showConfirmButton: true
                })
            }
        },
        error: function (jqXHR, textStatus, errorThrown) {
            Swal.fire({
                icon: 'error',
                title: 'Erro ao salvar',
                showConfirmButton: true
            })
        }
    });
}

function editarProduto() {
    if (localStorage.getItem('produto-editar') != null) {
        produto = JSON.parse(localStorage.getItem('produto-editar'));
        $("#id").val(produto.id);
        $("#nome").val(produto.nome);
        $("#marca").val(produto.marca);
        $("#quantidade").val(produto.quantidade);
        $("#dataEntrada").val(produto.dataEntrada);
        $("#preco").val(produto.preco);
        $("#categoria").val(produto.categoria);
        carregarImagens();
        $("#imagem-1").removeAttr('required');
        carregarFaqs();
        $("#descricao").val(produto.descricao);
        $("#palavrasChaves").val(produto.palavrasChaves);
        localStorage.removeItem('produto-editar')
    }
}

function carregarImagens() {
    for (var i = 0; i < produto.imagens.length; i++) {
        var img = '<img id="img-p-' + i + '" title="Clique para excluir a imagem" onclick="excluirImagem(' + i + ')" width="50px" src="' + produto.imagens[i].link + '">';
        $("#preview-imagem").append(img);
    }
}

function carregarFaqs() {
    for (var i = 0; i < produto.faqs.length; i++) {
        if(i===0){
            $("#faqPergunta1").val(produto.faqs[i].pergunta); 
            $("#faqResposta1").val(produto.faqs[i].resposta); 
        }else{
            addFaq();
            $("#faqPergunta"+qtdFaq).val(produto.faqs[i].pergunta); 
            $("#faqResposta"+qtdFaq).val(produto.faqs[i].resposta); 
        }
    }
}


function excluirImagem(i) {
    Swal.queue([{
        title: 'Você tem certeza',
        text: "Você não poderá reverter isso!",
        icon: 'warning',
        showLoaderOnConfirm: true,
        showCancelButton: true,
        confirmButtonColor: '#3085d6',
        cancelButtonColor: '#d33',
        confirmButtonText: 'Sim!',
        cancelButtonText: 'Não',
        preConfirm: () => {
            return $.ajax({
                type: 'DELETE',
                url: '../../produtos?imagemPath=' + produto.imagens[i].path,
                beforeSend: function (xhr) {
                    loadMsg("Excluindo!");
                },
                headers: {
                    Accept: "application/json; charset=utf-8",
                    "Content-Type": "application/json; charset=utf-8"
                },
                error: function (jqXHR, textStatus, errorThrown) {
                    Swal.fire({
                        icon: 'error',
                        title: 'Erro ao excluir Imagem',
                        showConfirmButton: true
                    })
                },
                success: function (result) {
                    $("#img-p-"+i).remove();
                    Swal.fire({
                        icon: 'success',
                        title: 'Excluida com sucesso!',
                        showConfirmButton: false,
                        timer: 1500
                    });
                }
            });
        }
    }]);
}

function visualizarImagemDoInput(numeroInput) {
    $('#img-' + numeroInput).show();
    const file = document.querySelector('#imagem-' + numeroInput).files[0];
    const reader = new FileReader();
    reader.addEventListener("load", function () {
        // converter a imagem em base64
        atualizarImgView(reader.result, numeroInput);
    }, false);

    if (file) {
        reader.readAsDataURL(file);
    }
}

function atualizarImgView(result, numeroInput) {
    $('#img-' + numeroInput).empty();
    $('#img-' + numeroInput).attr("src", result);
}