package br.com.astrosoft.produto.model.beans

import br.com.astrosoft.produto.model.saci
import java.time.LocalDate

data class ProdutoKardex(
  var loja: Int? = null,
  var prdno: String? = null,
  var grade: String? = null,
  var data: LocalDate?,
  var doc: String? = null,
  var ljDoc: Int? = null,
  var pedido: String? = null,
  var nfEnt: String? = null,
  var tipo: ETipoKardec? = null,
  var observacao: String?,
  var vencimento: LocalDate? = null,
  var qtde: Int? = null,
  var saldo: Int? = null,
  var userLogin: String? = null,
  var recLogin: String? = null,
  var entLogin: String? = null,
  var quantDevolucao: Int? = null
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
    fun deleteKardec(produto: ProdutoEstoque, tipo: ETipoKardec = ETipoKardec.TODOS) {
      saci.deleteKardec(produto, tipo)
    }

    fun deleteKardecMov(produto: ProdutoEstoque, doc: String) {
      saci.deleteKardecMov(produto, doc)
      if (produto.loja != 4) {
        produto.loja = 4
        saci.deleteKardecMov(produto, doc)
      }
    }

    fun findKardec(produto: ProdutoEstoque): List<ProdutoKardex> {
      return saci.selectKardec(produto)
    }
  }
}

enum class ETipoKardec(val num: String, val descricao: String) {
  RECEBIMENTO("01", "Recebimento"),
  RESSUPRIMENTO("01", "Ressuprimento"),
  EXPEDICAO("01", "Expedição"),
  REPOSICAO("01", "Reposição Loja"),
  ACERTO_ESTOQUE("01", "Acerto Estoque"),
  ENTREGA("01", "Entrega"),
  INICIAL("00", "Inicial"),
  RETORNO("01", "Retorno Loja"),
  ACERTO("01", "Acerto Estoque"),
  DEVOLUCAO("01", "Devolução"),
  MOV_ENTREGA("01", "Movimentação de Entrega"),
  MOV_RECEBIMENTO("01", "Movimentação de Recebimento"),
  REPOSICAO_CDLJ("01", "Reposição Rota CD-LJ"),
  REPOSICAO_CDLJ2("01", "Reposição Rota CD-LJ2"),
  REPOSICAO_CDLJ3("01", "Reposição Rota CD-LJ3"),
  REPOSICAO_CDLJ5("01", "Reposição Rota CD-LJ5"),
  REPOSICAO_CDLJ8("01", "Reposição Rota CD-LJ8"),

  REPOSICAO_LJCD("01", "Reposição Rota LJ -CD"),
  TODOS("00", "Todos")
}

