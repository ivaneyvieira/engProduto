package br.com.astrosoft.produto.model.beans

import java.time.LocalDate

data class ProdutoKardec(
  val loja: Int,
  val prdno: String,
  val grade: String,
  val data: LocalDate?,
  val doc: String,
  val tipo: ETipoKardec,
  val vencimento: LocalDate? = null,
  val qtde: Int,
  val saldo: Int = 0,
  val userLogin: String,
) {
  val codigo: Int
    get() = prdno.trim().toIntOrNull() ?: 0
  val tipoDescricao: String
    get() = tipo.descricao
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

