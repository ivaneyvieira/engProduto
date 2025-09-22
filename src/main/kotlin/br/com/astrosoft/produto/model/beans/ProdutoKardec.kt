package br.com.astrosoft.produto.model.beans

import br.com.astrosoft.produto.model.saci
import java.time.LocalDate

data class ProdutoKardec(
  var loja: Int? = null,
  var prdno: String? = null,
  var grade: String? = null,
  var data: LocalDate?,
  var doc: String? = null,
  var nfEnt: String? = null,
  var tipo: ETipoKardec? = null,
  var observacao: String?,
  var vencimento: LocalDate? = null,
  var qtde: Int? = null,
  var saldo: Int? = null,
  var userLogin: String? = null,
) {
  fun save() {
    saci.saveKardec(this)
  }

  val observacaoAbrev
    get() = observacao?.trim()?.split(" ")?.firstOrNull()

  val saldoEmb: Double
    get() {
      val prdno = this.prdno ?: return 0.00
      val prdEmb = ProdutoEmbalagem.findEmbalagem(prdno)
      val fator = prdEmb?.qtdEmbalagem ?: 1.0
      return (saldo ?: 0) * 1.00 / fator
    }

  val codigo: Int
    get() = prdno?.trim()?.toIntOrNull() ?: 0
  val tipoDescricao: String
    get() = tipo?.descricao ?: ""

  companion object {
    fun deleteKarde(produto: ProdutoEstoque) {
      saci.deleteKardec(produto)
    }

    fun findKardec(produto: ProdutoEstoque, dataIncial : LocalDate?): List<ProdutoKardec> {
      return saci.selectKardec(produto, dataIncial)
    }
  }
}

enum class ETipoKardec(val descricao: String) {
  RECEBIMENTO("Recebimento"),
  RESSUPRIMENTO("Ressuprimento"),
  EXPEDICAO("Expedição"),
  REPOSICAO("Reposição Loja"),
  ACERTO_ESTOQUE("Acerto Estoque"),
  ENTREGA("Entrega"),
  INICIAL("Inicial"),
  RETORNO("Retorno Loja"),
  ACERTO("Acerto Estoque"),
}

