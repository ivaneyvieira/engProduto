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
  var valorNota: Double?,
  var data: LocalDate?,
  var hora: LocalTime?,
  var vendedor: Int?,
  var nomeVendedor: String?,
  var usuarioExp: String?,
  var usuarioCD: String?,
  var totalProdutos: Double?,
  var marca: Int?,
  var cancelada: String?,
  var tipoNotaSaida: String?,
  var dataEntrega: LocalDate?,
  var tipo: String?,
  var countEnt: Int?,
  var countImp: Int?,
  var retiraFutura: Boolean?,
  var rota: String?,
  var entrega: LocalDate?,
  var empnoMotorista: Int?,
  var usernoPrint: Int?,
  var usuarioPrint: String?,
  var usuarioSingExp: String?,
  var usuarioSep: String?,
  var observacaoPrint: String?,
) {
  val dataStr
    get() = data?.format() ?: ""

  val hotaTime
    get() = hora?.toString() ?: ""

  val nota
    get() = "$numero/$serie"

  val situacao
    get() = if (cancelada == "S") "Cancelada" else ""

  val tipoNotaSaidaDesc: String
    get() {
      return ETipoNotaFiscal.entries.firstOrNull {
        it.name == tipoNotaSaida
      }?.descricao ?: ""
    }

  fun save() {
    saci.saveNotaSaida(this)
  }

  fun produtos(prdno: String = "", grade: String = "", todosLocais: Boolean) =
    saci.findProdutoNF(this, prdno, grade, todosLocais)

  companion object {
    fun findDevolucao(filtro: FiltroNotaDev): List<NotaSaidaDev> {
      val notas = saci.findNotaSaidaDevolucao(filtro = filtro)
      return notas
    }
  }
}

private val user = AppConfig.userLogin() as? UserSaci

data class FiltroNotaDev(
  val tipoNota: ETipoNotaFiscal,
  val loja: Int,
  val dataInicial: LocalDate?,
  val dataFinal: LocalDate?,
  val dataEntregaInicial: LocalDate? = null,
  val dataEntregaFinal: LocalDate? = null,
  //val notaEntreg2: String = "T",
  val pesquisa: String,
  val prdno: String = "",
  val grade: String = "",
  val todosLocais: Boolean,
  val localizacaoNota: List<String> = user?.localizacaoNota?.toList() ?: listOf("TODOS"),
)

