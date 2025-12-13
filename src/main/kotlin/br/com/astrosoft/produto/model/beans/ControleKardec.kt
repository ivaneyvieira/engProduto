package br.com.astrosoft.produto.model.beans

import br.com.astrosoft.produto.model.saci
import java.time.LocalDate

data class ControleKardec(
  var loja: Int? = null,
  var prdno: String? = null,
  var grade: String? = null,
  var data: LocalDate?,
  var doc: String? = null,
  var tipo: ETipoKardecControle? = null,
  var qtde: Int? = null,
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

enum class ETipoKardecControle(val num: String, val descricao: String) {
  VENDA("01", "Venda"),
}


