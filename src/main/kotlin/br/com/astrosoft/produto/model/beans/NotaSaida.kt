package br.com.astrosoft.produto.model.beans

import br.com.astrosoft.framework.model.config.AppConfig
import br.com.astrosoft.framework.util.format
import br.com.astrosoft.produto.model.saci
import java.time.LocalDate
import java.time.LocalTime

class NotaSaida(
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
  var nomeCompletoVendedor: String?,
  //var locais: String?,
  var usuarioExp: String?,
  var usuarioCD: String?,
  var totalProdutos: Double?,
  var marca: Int?,
  var cancelada: String?,
  var tipoNotaSaida: String?,
  var notaEntrega: String?,
  var usuarioEntrega: String?,
  var dataEntrega: LocalDate?,
  var tipo: String?,
  var countExp: Int?,
  var countCD: Int?,
  var countEnt: Int?,
  var countImp: Int?,
  var countNImp: Int?,
  var retiraFutura: Boolean?,
  var rota: String?,
  var agendado: String?,
  var entrega: LocalDate?,
  var enderecoCliente: String?,
  var bairroCliente: String?,
  var empnoMotorista: Int?,
  var nomeMotorista: String?,
  var usernoPrint: Int?,
  var usuarioPrint: String?,
  var usernoSingCD: Int?,
  var usuarioSingCD: String?,
  var usernoSingExp: Int?,
  var usuarioSingExp: String?,
  var usuarioSep: String?,
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

  fun produtos(marca: EMarcaNota, prdno: String = "", grade: String = "", todosLocais: Boolean) =
      saci.findProdutoNF(this, marca, prdno, grade, todosLocais)

  fun marcaImpressao() {
    val user = AppConfig.userLogin() as? UserSaci
    this.usernoPrint = user?.no
    saci.saveNotaSaidaPrint(this)
  }

  companion object {
    fun find(filtro: FiltroNota): List<NotaSaida> {
      val notas = saci.findNotaSaida(filtro = filtro)/* +
                  saci.findNotaSaida(
                    filtro = filtro.copy(
                      tipoNota = filtro.tipoNota,
                    )
                  )*/
      return notas/*.distinctBy { "${it.loja} ${it.numero} ${it.serie} ${it.xano} ${it.pdvno}" }*/
    }
  }
}

private val user = AppConfig.userLogin() as? UserSaci

data class FiltroNota(
  val marca: EMarcaNota,
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

enum class EMarcaNota(val num: Int, val descricao: String) {
  EXP(0, "Expedição"), CD(1, "CD"), ENT(2, "Entregue"), TODOS(999, "Todos")
}

enum class ETipoNotaFiscal(val descricao: String) {
  NFCE("NFCE"),
  NFE("NFE"),
  TRANSFERENCIA("Transferência"),
  ENTRE_FUT("Entrega Futura"),
  SIMP_REME("Retira Futura"),
  SIMP_REME_L("Retira Futura L"),
  ENTREGA_WEB("Entrega Web"),
  DEVOLUCAO("Dev Fornecedor"),
  TODOS("Todos"),
}