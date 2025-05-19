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

  private val produtos = mutableListOf<NotaSaidaDevProduto>()

  val total
    get() = produtos.sumOf { it.total ?: 0.00 }
  val desconto
    get() = produtos.sumOf { it.desconto ?: 0.00 }
  val frete
    get() = produtos.sumOf { it.frete ?: 0.00 }
  val despesas
    get() = produtos.sumOf { it.despesas ?: 0.00 }
  val baseIcms
    get() = produtos.sumOf { it.baseIcms ?: 0.00 }
  val valorSubst
    get() = produtos.sumOf { it.valorSubst ?: 0.00 }
  val valorIcms
    get() = produtos.sumOf { it.valorIcms ?: 0.00 }
  val valorIpi
    get() = produtos.sumOf { it.valorIpi ?: 0.00 }
  val totalGeral
    get() = produtos.sumOf { it.totalGeral }

  fun updateProdutos() {
    val produtosNovos = saci.findNotaSaidaDevolucaoProduto(this)
    produtos.clear()
    produtos.addAll(produtosNovos)
  }

  fun obetemProdutos(): List<NotaSaidaDevProduto> {
    return produtos.toList()
  }

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

