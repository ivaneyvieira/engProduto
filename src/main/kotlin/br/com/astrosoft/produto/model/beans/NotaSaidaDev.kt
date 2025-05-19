package br.com.astrosoft.produto.model.beans

import br.com.astrosoft.framework.model.config.AppConfig
import br.com.astrosoft.framework.util.format
import br.com.astrosoft.produto.model.saci
import java.time.LocalDate
import java.time.LocalTime

class NotaSaidaDev(
  var loja: Int,
  var pdvno: Int,
  var xano: Long,
  var numero: Int,
  var pedido: String?,
  var serie: String?,
  var cliente: Int?,
  var nomeCliente: String?,
  var codTransportadora: Int?,
  var nomeTransportadora: String?,
  var valorNota: Double?,
  var dataEmissao: LocalDate?,
  var hora: LocalTime?,
  var volume: Double?,
  var peso: Double?,
  var vendedor: Int?,
  var totalProdutos: Double?,
  var cancelada: String?,
  var entrega: LocalDate?,
  var observacaoPrint: String?,
  var observacaoNota: String?,
  var observacaoAdd: String?,
  var situacaoDup: String?,
  var duplicata: String?,
) {
  val dataStr
    get() = dataEmissao?.format() ?: ""

  val hotaTime
    get() = hora?.toString() ?: ""

  val nota
    get() = "$numero/$serie"

  val situacao
    get() = if (cancelada == "S") "Cancelada" else ""

  val tipoNotaSaidaDesc: String
    get() = "Devoluçao"

  fun save() {
    saci.saveNotaSaida(this)
  }

  fun produtos() =
      saci.findNotaSaidaDevolucaoProduto(this)

  fun saveObs() {
    saci.notaSaidaObservacaoSave(this)
  }

  companion object {
    fun findDevolucao(filtro: FiltroNotaDev): List<NotaSaidaDev> {
      val notas = saci.findNotaSaidaDevolucao(filtro = filtro)
      return notas
    }
  }
}

private val user = AppConfig.userLogin() as? UserSaci

data class FiltroNotaDev(
  val loja: Int,
  val dataInicial: LocalDate?,
  val dataFinal: LocalDate?,
  val pesquisa: String,
  val prdno: String = "",
  val grade: String = "",
  val localizacaoNota: List<String> = user?.localizacaoNota?.toList() ?: listOf("TODOS"),
)

