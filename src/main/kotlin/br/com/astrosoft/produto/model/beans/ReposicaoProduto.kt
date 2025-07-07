package br.com.astrosoft.produto.model.beans

import br.com.astrosoft.produto.model.saci
import java.time.LocalDate

class ReposicaoProduto(
  var loja: Int?,
  var numero: Int?,
  var cliente: String?,
  var data: LocalDate?,
  var localizacao: String?,
  var marca: Int?,
  var observacao: String?,
  var entregueNo: Int?,
  var entregueNome: String?,
  var entregueSNome: String?,
  var finalizadoNo: Int?,
  var finalizadoNome: String?,
  var finalizadoSNome: String?,
  var recebidoNo: Int?,
  var recebidoNome: String?,
  var recebidoSNome: String?,
  var prdno: String?,
  var codigo: String?,
  var barcode: String?,
  var grade: String?,
  var descricao: String?,
  var quantidade: Int?,
  var qtRecebido: Int?,
  var selecionado: Int?,
  var posicao: Int?,
  var qtEstoque: Int?,
  var metodo: Int?,
  var multAcerto: Int?,
) {
  val recebidoNomeAjuste: String? = when (metodo) {
    EMetodo.RETORNO.num -> {
      entregueNome
    }

    EMetodo.ACERTO.num  -> {
      finalizadoNome
    }

    else                -> {
      recebidoNome
    }
  }
  val entregueNomeAjuste: String? = when (metodo) {
    EMetodo.RETORNO.num -> {
      recebidoNome
    }

    else                -> {
      entregueNome
    }
  }

  fun chave() = "${loja}:${numero}:${localizacao}:${prdno}:${grade}"

  fun salva() {
    saci.updateReposicaoProduto(this)
  }

  fun isSep() = marca == EMarcaReposicao.SEP.num

  fun isNaoRecebido() = recebidoNo == 0

  fun isNaoFinalizado() = finalizadoNo == 0

  fun isNaoEntregue() = entregueNo == 0

  private fun isNaoAssinado() = if (metodo == EMetodo.ACERTO.num) {
    isNaoEntregue() || isNaoFinalizado()
  } else {
    isNaoEntregue() || isNaoRecebido()
  }

  fun isSepNaoAssinado() = isSep() || isNaoAssinado()

  fun isSelecionado() = selecionado == EMarcaReposicao.ENT.num

  fun isEnt() = marca == EMarcaReposicao.ENT.num

  val selecionadoOrdemENT
    get() = if (marca == EMarcaReposicao.ENT.num) selecionado else 0
}