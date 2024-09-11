package br.com.astrosoft.produto.model.beans

import br.com.astrosoft.produto.model.saci
import java.time.LocalDate

class ReposicaoProduto(
  var loja: Int?,
  var numero: Int?,
  var data: LocalDate?,
  var localizacao: String?,
  var marca: Int?,
  var observacao: String?,
  var entregueNo: Int?,
  var entregueNome: String?,
  var entregueSNome: String?,
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
  var mult: Int?,
) {
  val tipoMetodo: String
    get() = when (metodo) {
      431  -> "Reposição"
      432  -> "Retorno"
      433  -> "Acerto"
      else -> ""
    }

  fun chave() = "${loja}:${numero}:${localizacao}:${prdno}:${grade}"

  fun salva() {
    saci.updateReposicaoProduto(this)
  }

  fun isSEP() = marca == EMarcaReposicao.SEP.num

  fun isNaoRecebido() = recebidoNo == 0

  fun isNaoEntregue() = entregueNo == 0

  fun isSEPNaoAssinado() = isSEP() || isNaoRecebido() || isNaoEntregue()

  fun isENT() = marca == EMarcaReposicao.ENT.num

  val selecionadoOrdemENT
    get() = if (marca == EMarcaReposicao.ENT.num) selecionado else 0
}