package br.com.astrosoft.produto.model.beans

import java.time.LocalDate

data class ControleKardec(
  var loja: Int? = null,
  var prdno: String? = null,
  var grade: String? = null,
  var data: LocalDate?,
  var doc: String? = null,
  var tipo: ETipoKardecControle? = null,
  var qtde: Int? = null,
  var observacao: String? = null,
  var saldo: Int? = null,
) {
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
}

enum class ETipoKardecControle(val descricao: String) {
  VENDA("Venda"),
  TRANSF("Transferência"),
  FATURA("Fatura"),
  DEVOLUCAO("Devolução"),
  INICIAL("Inicial"),
  REPOSICAO("Reposição"),
  RETORNO("Retorno"),
  ACERTO("Acerto"),
  MOV_ENTREGA("Movimentação de Entrega"),
  MOV_RECEBIMENTO("Movimentação de Recebimento"),
}


