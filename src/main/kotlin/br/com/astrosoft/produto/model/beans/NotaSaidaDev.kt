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
  var cfop: String?,
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
  var situacaoDev: Int?,
  var invno: Int?,
  var numeroDev: Int?,
  var tipoDevolucao: Int?,
  var quantArquivos: Int?,
) {
  val fornecedor: String
    get() = TODO()
  
  val dataColetaStr: String
    get() {
      return ""/*val notaNFD = notaDevolucao ?: ""
      return if (notaNFD.isNotBlank() && dataColeta == null) {
        "Pendente"
      } else {
        dataColeta?.format("dd/MM/yyyy") ?: ""
      }*/
    }
  
  val situacaoDevName: String?
    get() {
      situacaoDev ?: return ""
      return EStituacaoDev.findByNum(situacaoDev ?: 0)?.descricao
    }

  var motivoDevolucaoEnun
    get() = EMotivoDevolucao.findByNum(tipoDevolucao ?: 0)
    set(value) {
      tipoDevolucao = value?.num
    }

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

//  fun save() {
//    saci.saveNotaSaida(this)
//  }

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
  val baseSubst
    get() = produtos.sumOf { it.baseSubst ?: 0.00 }
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

  fun listArquivos(): List<NotaSaidaDevFile> {
    val saida = saci.notaSaidaDevolucaoSaidaSelect(this)
    val entrada = saci.notaSaidaDevolucaoEntradaSelect(this)
    return saida + entrada
  }
  
  fun salvaObservacao() {
    saci.salvaObservacao(this)
  }
  
  fun observacaoPadrao(): String {
    val nomeReduzido = this.motivoDevolucaoEnun?.nomeReduzido ?: ""
    
    if (nomeReduzido.isEmpty()) {
      return ""
    }
    
    return "PED ${numeroDev?.toString() ?: ""} - $nomeReduzido NFO ${nota ?: ""}"
  }
  
  fun obsNfVazia(): Boolean {
    val obs = this.observacaoNota ?: ""
    return obs.isEmpty()
  }
  
  fun addNotaSaida(nfSaida: String) {
    val loja = this.loja ?: throw Exception("Loja não encontrada")
    val niDev = this.numeroDev ?: throw Exception("Numero da devolução não encontrado")
    val numero = nfSaida.split("/").getOrNull(0) ?: throw Exception("Numero da nota não encontrada")
    val serie = nfSaida.split("/").getOrNull(1) ?: throw Exception("Série da nota não encontrada")
    val notaSaida = saci.localizaNotaSaida(loja, numero, serie) ?: throw Exception("Nota de saída não encontrada")
    this.loja = notaSaida.storeno ?: 0
    this.pdvno = notaSaida.pdvno ?: 0
    this.xano = notaSaida.xano?.toLong() ?: 0
    saci.adicionaNIDev(notaSaida, niDev)
  }
  
  val chaveEmail: String
    get() {
      val motivo = motivoDevolucaoEnun
      return if (motivo?.notasMultiplas == true) {
        "$loja-$motivo-$numeroDev"
      } else {
        "$loja-$invno-$motivo-$numeroDev"
      }
    }
  
  fun contaChave(): ChaveEmail {
    return saci.countChaveEmail(chaveEmail)
  }
  
  fun refreshProdutosDev(): NotaSaidaDev {
    TODO()
  }
  
  fun listRepresentantes(): List<Representante> {
    TODO()
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

