package br.com.astrosoft.produto.model.beans

import br.com.astrosoft.framework.util.format
import br.com.astrosoft.produto.model.saci
import java.time.LocalDate

class FornecedorDevolucao(
  val custno: Int,
  val fornecedor: String,
  val vendno: Int,
  val fornecedorSap: Int,
  val email: String,
  val tipo: String,
  var obs: String,
  val notas: List<NotaSaidaDevolucao>,
) {
  fun listRepresentantes() = saci.representante(vendno)

  fun parcelasFornecedor() = saci.listParcelasFornecedor(vendno)

  fun pedidosFornecedor() = saci.listPedidosFornecedor(vendno)

  val ultimaData
    get() = notas.mapNotNull { nota ->
      if (nota.tipo == "PED") nota.dataPedido else nota.dataNota
    }.minOfOrNull { it }

  val primeiraData
    get() = notas.mapNotNull { nota ->
      if (nota.tipo == "PED") nota.dataPedido else nota.dataNota
    }.minOfOrNull { it }

  val dataAgenda
    get() = notaObs?.dataAgenda

  val notaObs
    get() = if (notas.any { it.observacaoAuxiliar.isNullOrBlank() }) null
    else notas.sortedBy { it.dataNota }.firstOrNull()

  val chaveDesconto: String
    get() {
      val nota = notaObs ?: return ""
      return nota.chaveDesconto ?: ""
    }

  val ultimaDataStr
    get() = ultimaData.format()

  val primeiraDataStr
    get() = primeiraData.format()

  val valorTotal
    get() = notas.sumOf { it.valor }

  /********** Campos Situação ************/

  val dataSituacao: LocalDate?
    get() = notaObs?.dataSituacao
  val dataNotaEditavel: LocalDate?
    get() = notaObs?.dataNotaEditavel
  val situacao: String
    get() = notaObs?.situacao ?: ""
  val situacaoStr: String
    get() = notaObs?.situacaoStr ?: ""
  val notaEditavel: String
    get() = notaObs?.notaEditavel ?: ""
  val situacaoPendencia
    get() = notaObs?.situacaoPendencia
  val usuarioSituacao: String
    get() = notaObs?.usuarioSituacao ?: ""
  val notaSituacao: String
    get() = notaObs?.notaSituacao ?: ""
  val tituloSituacao: String
    get() = notaObs?.tituloSituacao ?: ""
  val docSituacao: String
    get() = notaObs?.docSituacao ?: ""
  val niSituacao: String
    get() = notaObs?.niSituacao ?: ""
  val remarks: String
    get() = notas.sortedBy { it.dataNota }.firstOrNull()?.remarks ?: ""

  /********************************************/

  fun listEmail(): List<String> {
    val list = listRepresentantes().map {
      it.email
    } + email

    return list.distinct().filter {
      it != ""
    }.sorted()
  }

  fun saveRmkVend() {
    saci.saveRmkVend(this)
  }



  val labelTitle
    get() = "DEV FORNECEDOR: ${this.custno} ${this.fornecedor} (${this.vendno}) FOR SAP ${this.fornecedorSap}"
}

data class FiltroFornecedor(val query: String, val loja: Loja = Loja.lojaZero)