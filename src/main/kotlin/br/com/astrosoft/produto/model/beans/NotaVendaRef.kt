package br.com.astrosoft.produto.model.beans

import br.com.astrosoft.produto.model.saci
import java.time.LocalDate
import java.time.LocalTime

class NotaVendaRef(
    var loja: Int?,
    var siglaLoja: String?,
    var pdv: Int?,
    var transacao: Int?,
    var pedido: Int?,
    var numMetodo: Int?,
    var nomeMetodo: String?,
    var mult: Double?,
    var data: LocalDate?,
    var nota: String?,
    var tipoNf: String?,
    var hora: LocalTime?,
    var tipoPgto: String?,
    var tipo: String?,
    var documento: String?,
    var quantParcelas: Int?,
    var mediaPrazo: Int?,
    var valor: Double?,
    var valorComFrete: Double?,
    var cliente: Int?,
    var uf: String?,
    var nomeCliente: String?,
    var vendedor: String?,
    var valorTipo: Double?,
    var obs: String?,
    val obs1: String?,
    val obs2: String?,
    val obs3: String?,
    val obs4: String?,
    val obs5: String?,
    val obs6: String?,
    val obs7: String?,
    var enderecoEntrega: String?,
    var bairroEntrega: String?,
    var area: String?,
    var rota: String?,
    var endereco: String?,
    var bairro: String?,
    var nfEntrega: String?,
    var dataEntrega: LocalDate?,
    var impresso: Boolean?,
    var dataImpressao: LocalDate?,
    var horaImpessao: LocalTime?,
) {
  val impressoSN
    get() = if (impresso == true) "S" else "N"
  
  val tipoNome: String
    get() = ETipoNFEntFut.entries.firstOrNull { it.codigo == tipo }?.descricao ?: ""
  
  val documentoStr: String
    get() {
      val doc = documento ?: return ""
      val quant = if (quantParcelas == null) "" else " (${quantParcelas}x)"
      return "$doc $quant"
    }
  
  fun produtos(): List<ProdutoNFS> {
    return saci.findProdutoNF(this)
  }
  
  fun marcaImpressao() {
    saci.marcaNotaVendaRef(this)
  }
  
  val numeroInterno: Int?
    get() {
      val regex = Regex("""NI[^0-9A-Z]*(\d+)""")
      val obsInput = obs?.uppercase() ?: return null
      val match = regex.find(obsInput) ?: return null
      val groups = match.groupValues
      return groups.getOrNull(1)?.toIntOrNull()
    }
  
  companion object {
    fun findAll(filtro: FiltroNotaVendaRef): List<NotaVendaRef> {
      return saci.findNotaVendaRef(filtro)
    }
  }
}

data class FiltroNotaVendaRef(
    val loja: Int,
    val pdv: Int,
    val pesquisa: String,
    val dataInicial: LocalDate?,
    val dataFinal: LocalDate?,
    val tipoNF: String = "",
    val tipoNFEntFut: ETipoNFEntFut = ETipoNFEntFut.Todos,
    val temEntrega: ETemEntrega = ETemEntrega.NAO,
    val impresso: EImpressao = EImpressao.NAO,
)

enum class ETipoNFEntFut(val codigo: String, val descricao: String) {
  Entrega("E", "Entrega"),
  Retira("R", "Retira"),
  Todos("T", "Todos")
}

enum class ETemEntrega(val descricao: String) {
  SIM("Sim"),
  NAO("Não"),
  TODAS("Todas")
}

enum class EImpressao(val descricao: String) {
  SIM("Sim"),
  NAO("Não"),
  TODOS("Todos")
}