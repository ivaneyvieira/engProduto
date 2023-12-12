package br.com.astrosoft.produto.model.beans

import br.com.astrosoft.framework.model.config.AppConfig
import br.com.astrosoft.framework.util.localDate
import br.com.astrosoft.framework.util.mid
import br.com.astrosoft.framework.util.toSaciDate
import br.com.astrosoft.produto.model.saci
import java.sql.Time
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

class Pedido(
  var loja: Int,
  var nomeLoja: String?,
  var siglaLoja: String?,
  var pedido: Int,
  var marca: String?,
  var separado: String?,
  var zonaCarga: String?,
  var entrega: LocalDate?,
  var data: LocalDate?,
  var dataEntrega: LocalDate?,
  var pdvno: Int?,
  var pdvnoVenda: Int?,
  var hora: Time?,
  var nfnoFat: String?,
  var nfseFat: String?,
  var dataFat: LocalDate?,
  var horaFat: Time?,
  var valorFat: Double?,
  var nfnoEnt: String?,
  var nfseEnt: String?,
  var dataEnt: LocalDate?,
  var horaEnt: Time?,
  var valorEnt: Double?,
  var vendno: Int?,
  var vendedor: String?,
  var custno: Int?,
  var cliente: String?,
  var foneCliente: String?,
  var endereco: String?,
  var bairro: String?,
  var cidade: String?,
  var estado: String?,
  var enderecoEntrega: String?,
  var bairroEntrega: String?,
  var frete: Double?,
  var valor: Double?,
  var status: String?,
  var area: String?,
  var rota: String?,
  var obs: String?,
  var codArea: Int?,
  var userno: Int?,
  var username: String?,
  var dataPrint: LocalDate?,
  var horaPrint: LocalTime?,
  var userPrint: Int?,
  var userPrintName: String?,
  var obs1: String?,
  var obs2: String?,
  var obs3: String?,
  var obs4: String?,
  var obs5: String?,
  var obs6: String?,
  var obs7: String?,
  var tipo: String?,
  var metodo: String?,
  var piso: Int?,
  var loc: String?,
  var obsNota: String?,
) {
  var seq: Int = 0

  fun listObs(): List<String> = listOf(obs1, obs2, obs3, obs4, obs5, obs6, obs7)
    .mapNotNull { it?.trim() }
    .filter { it != "" }
    .flatMap { obs ->
      if (obs.length > 40)
        listOf(obs.mid(0, 40), obs.mid(40, obs.length))
      else
        listOf(obs)
    }

  val observacao: String
    get() {
      return listObs().joinToString(separator = " / ")
    }

  val dataHoraPrint
    get() = if (dataPrint == null || horaPrint == null) null
    else LocalDateTime.of(dataPrint, horaPrint)
  val nfFat: String
    get() = numeroNota(nfnoFat, nfseFat)
  val nfEnt: String
    get() = numeroNota(nfnoEnt, nfseEnt)
  val tipoStr
    get() = if (tipo == "E") "Entrega" else "Retira"

  val descricaoZonaCarga: String?
    get() = EZonaCarga.values().firstOrNull {
      it.codigo.toString() == zonaCarga
    }?.descricao

  val rotaArea
    get() = when {
      area?.startsWith("NORTE") == true    -> "Norte"
      area?.startsWith("SUL") == true      -> "Sul"
      area?.startsWith("LESTE") == true    -> "Leste"
      area?.startsWith("NORDESTE") == true -> "Nordeste"
      area?.startsWith("SUDESTE") == true  -> "Sudeste"
      else                                 -> null
    }

  val isEcommerce
    get() = vendno == 440 && loja == 4
  val tipoEcommece
    get() = if (isEcommerce) "WEB" else ""
  val paraImprimir: Boolean
    get() = (marca != "S") && (nfnoEnt == "")
  val impressoSemNota: Boolean
    get() = (marca == "S") && (nfnoEnt == "")
  val impresso: Boolean
    get() = (marca == "S")
  val impressoComNota: Boolean
    get() = (nfnoEnt != "")
  val pedidoPendente: Boolean
    get() = (nfnoEnt == "")
  val valorComFrete
    get() = valorFat

  fun marcaImpresso() {
    saci.ativaMarca(loja, pedido, "S")
  }

  fun desmarcaImpresso() {
    saci.ativaMarca(loja, pedido, " ")
    desmarcaDataHora()
  }

  fun marcaSeparado(marca: String) {
    saci.marcaSeparado(loja, pedido, marca)
  }

  fun marcaDataHora(dataHora: LocalDateTime) {
    saci.ativaDataHoraImpressao(loja, pedido, dataHora.toLocalDate(), dataHora.toLocalTime(), AppConfig.userLogin()?.no ?: 0)
  }

  private fun desmarcaDataHora() {
    saci.ativaDataHoraImpressao(loja, pedido, null, null, 0)
  }

  fun canPrint(): Boolean = dataHoraPrint == null || (AppConfig.userLogin()?.admin == true)

  fun produtos(): List<ProdutoPedido> = saci.produtoPedido(loja, pedido, tipo ?: "").map { produto ->
    produto.pedido = this
    produto
  }

  fun marcaCarga(carga: EZonaCarga, entrega: LocalDate?) {
    saci.marcaCarga(loja, pedido, carga, entrega)
  }

  fun removeCarga() {
    saci.marcaCarga(loja, pedido, EZonaCarga.SemZona, entrega = null)
  }

  companion object {
    fun listaPedido(filtro: FiltroPedido): List<Pedido> {
      val lista = saci.listaPedido(filtro)
      return lista.sortedWith(compareBy<Pedido> { it.data }.thenBy {
        it.hora
      })
    }

    fun listaPedidoImprimir(filtro: FiltroPedido): List<Pedido> = listaPedido(filtro).filter { it.paraImprimir }

    fun listaPedidoImpressoSemNota(filtro: FiltroPedido): List<Pedido> =
        listaPedido(filtro).filter { it.impressoSemNota }

    fun listaPedidoImpressoSeparar(filtro: FiltroPedido): List<Pedido> = listaPedido(filtro).filter { it.impresso }
    fun listaPedidoImpressoCarga(filtro: FiltroPedido): List<Pedido> = listaPedido(filtro).filter { it.impressoSemNota }
    fun listaPedidoImpressoSeparado(filtro: FiltroPedido): List<Pedido> = listaPedido(filtro).filter { it.impresso }

    fun listaPedidoImpressoComNota(filtro: FiltroPedido): List<Pedido> =
        listaPedido(filtro).filter { it.impressoComNota }

    fun listaPedidoPendente(filtro: FiltroPedido): List<Pedido> = listaPedido(filtro).filter { it.pedidoPendente }
  }
}

enum class ETipoPedido(val sigla: String) {
  ENTREGA("E"), RETIRA("R")
}

fun List<Pedido>.rotaPedido(): List<RotaPedido> = map {
  RotaPedido(
    pedido = it.pedido,
    data = it.data,
    nfFat = it.nfFat,
    dataFat = it.dataFat,
    valorFat = it.valorFat,
    nfEnt = it.nfEnt,
    dataEnt = it.dataEnt,
    vendno = it.vendno,
    custno = it.custno,
    frete = it.frete,
    area = it.area,
    rota = it.rota,
    quantEntradas = null,
    listRota = emptyList(),
    listPedidos = emptyList()
  )
}

fun List<Pedido>.groupRotaLoja() = this.groupBy { pedido ->
  "${pedido.rotaArea} - ${pedido.loja}"
}.mapNotNull { entry ->
  val rota = entry.value.firstOrNull()?.rotaArea ?: return@mapNotNull null
  val loja = entry.value.firstOrNull()?.loja ?: return@mapNotNull null
  val pedidos = entry.value
  RotaPedido(
    nomeRota = rota,
    loja = loja,
    valorFat = pedidos.sumOf { it.valorFat ?: 0.00 },
    frete = pedidos.sumOf { it.frete ?: 0.00 },
    quantEntradas = pedidos.size
  )
}.sortedBy { it.data }

fun List<Pedido>.groupRotas() = this.groupBy { pedido ->
  pedido.rotaArea
}.mapNotNull { entry ->
  val nomeRota = entry.key ?: return@mapNotNull null
  val pedidos = entry.value
  RotaPedido(
    nomeRota = nomeRota,
    valorFat = pedidos.sumOf { it.valorFat ?: 0.00 },
    frete = pedidos.sumOf { it.frete ?: 0.00 },
    quantEntradas = pedidos.size,
    listRota = pedidos.rotaPedido(),
    listPedidos = pedidos
  )
}.sortedBy { it.nomeRota }

fun List<Pedido>.groupRoot() = this.groupBy { pedido ->
  pedido.loja
}.mapNotNull { entry ->
  val loja = entry.key
  val pedidos = entry.value
  val rotas = pedidos.groupRotas()
  RotaPedido(
    nomeRota = "",
    loja = loja,
    valorFat = pedidos.sumOf { it.valorFat ?: 0.00 },
    frete = pedidos.sumOf { it.frete ?: 0.00 },
    quantEntradas = pedidos.size,
    listRota = rotas,
    listPedidos = pedidos
  )
}.sortedBy { it.loja }

data class RotaPedido(
  val nomeRota: String? = "",
  val loja: Int? = null,
  val pedido: Int? = null,
  val data: LocalDate? = null,
  val area: String? = "",
  val rota: String? = "",
  val nfFat: String? = "",
  val dataFat: LocalDate? = null,
  val nfEnt: String? = "",
  val dataEnt: LocalDate? = null,
  val vendno: Int? = null,
  val frete: Double? = null,
  val valorFat: Double? = null,
  val custno: Int? = null,
  val quantEntradas: Int? = null,
  val listRota: List<RotaPedido> = emptyList(),
  val listPedidos: List<Pedido> = emptyList()
)

private fun numeroNota(nfno: String?, nfse: String?): String {
  return when {
    nfno.isNullOrBlank() -> ""
    nfno == ""           -> ""
    nfse.isNullOrBlank() -> nfno
    else                 -> "$nfno/$nfse"
  }
}

data class FiltroPedido(
  val tipo: ETipoPedido,
  val pesquisa: String = "",
  val ecommerce: Boolean,
  val dataInicial: LocalDate?,
  val dataFinal: LocalDate?
)

data class PedidoChave(
  val carga: String?,
  val data: Int?,
  val loc: String,
)

data class PedidoGroup(
  val carga: String?,
  val data: LocalDate?,
  val loc: String,
  val piso: Int,
  val total: Double,
  val quant: Int,
  val list: List<Pedido>,
)

fun List<Pedido>.groupBy(): List<PedidoGroup> {
  return this.groupBy {
    PedidoChave(
      carga = it.descricaoZonaCarga,
      data = if (it.descricaoZonaCarga.isNullOrBlank()) it.data.toSaciDate() else it.entrega.toSaciDate(),
      loc = it.loc ?: ""
    )
  }.map { entry ->
    PedidoGroup(
      carga = entry.key.carga,
      data = entry.key.data?.localDate(),
      loc = entry.key.loc,
      piso = entry.value.sumOf { it.piso ?: 0 },
      total = entry.value.sumOf { it.valorComFrete ?: 0.00 },
      quant = entry.value.size,
      list = entry.value,
    )
  }
}

enum class EZonaCarga(val codigo: Char, val descricao: String) {
  Leste1('A', "Leste 1"),
  Leste2('B', "Leste 2"),
  Leste3('I', "Leste 3"),
  Norte1('C', "Norte 1"),
  Norte2('D', "Norte 2"),
  Norte3('J', "Norte 3"),
  Sul1('E', "Sul 1"),
  Sul2('F', "Sul 2"),
  Sul3('G', "Sul 3"),
  Motoboy('K', "Motoboy"),
  Timon('H', "Timon"),
  SemZona(' ', ""),
  Separado('Z', "Sem carga")
}