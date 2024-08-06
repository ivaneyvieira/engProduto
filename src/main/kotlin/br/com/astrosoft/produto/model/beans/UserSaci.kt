package br.com.astrosoft.produto.model.beans

import br.com.astrosoft.framework.model.IUser
import br.com.astrosoft.framework.model.config.AppConfig
import br.com.astrosoft.produto.model.saci
import kotlin.math.pow
import kotlin.reflect.KProperty

class UserSaci : IUser {
  override var no: Int = 0
  override var name: String = ""
  override var login: String = ""
  override var senha: String = ""
  var bitAcesso: Long = 0
  var storeno: Int = 0
  var locais: String = ""
  var listaImpressora: String = ""
  var listaLoja: String = ""
  var impressora: String? = ""
  var ativoSaci: String = ""
  override var ativo by DelegateAuthorized(0)
  var produtoList by DelegateAuthorized(1)
  var produtoEstoqueGiro by DelegateAuthorized(2)
  var produtoEstoqueValidade by DelegateAuthorized(3)
  var produtoInventario by DelegateAuthorized(4)
  var notaExp by DelegateAuthorized(5)
  var notaCD by DelegateAuthorized(6)
  var notaEnt by DelegateAuthorized(7)
  var voltarCD by DelegateAuthorized(8)
  var voltarEnt by DelegateAuthorized(9)
  var pedidoCD by DelegateAuthorized(10)
  var pedidoEnt by DelegateAuthorized(11)
  var ressuprimentoCD by DelegateAuthorized(12)
  var ressuprimentoEnt by DelegateAuthorized(13)
  var notaEntradaReceber by DelegateAuthorized(14)
  var produtoEditor by DelegateAuthorized(15)
  var notaEntradaRecebido by DelegateAuthorized(16)
  var produtoInventarioAgrupado by DelegateAuthorized(17)
  var pedidoTransfCD5A by DelegateAuthorized(18)
  var clienteCadastro by DelegateAuthorized(19)
  var notaRota by DelegateAuthorized(20)
  var estoqueSaldo by DelegateAuthorized(21)

  //Permissões da tela receber
  var receberQuantidade by DelegateAuthorized(22)
  var receberExcluir by DelegateAuthorized(23)
  var receberAdicionar by DelegateAuthorized(24)
  var receberProcessar by DelegateAuthorized(25)
  var notaEntradaBase by DelegateAuthorized(26)
  var pedidoTransfReserva by DelegateAuthorized(27)
  var pedidoTransfEnt by DelegateAuthorized(28)
  var pedidoTransfImprimir by DelegateAuthorized(29)
  var pedidoTransfRessu4 by DelegateAuthorized(30)
  var devCliImprimir by DelegateAuthorized(31)
  var devCliImpresso by DelegateAuthorized(32)
  var devCliValeTrocaProduto by DelegateAuthorized(33)
  var pedidoTransfAutorizada by DelegateAuthorized(34)
  var retiraImprimir by DelegateAuthorized(35)
  var retiraImpresso by DelegateAuthorized(36)
  var devCliCredito by DelegateAuthorized(37)
  var devCliEditor by DelegateAuthorized(38)
  var devClienteTroca by DelegateAuthorized(39)
  var acertoEntrada by DelegateAuthorized(40)
  var acertoSaida by DelegateAuthorized(41)
  var devCliSemPrd by DelegateAuthorized(42)
  var recebimentoCadastraValidade by DelegateAuthorized(43)

  //var devCliSemPrdDelete by DelegateAuthorized(44)
  var devCliVenda by DelegateAuthorized(45)
  var autorizaDevolucao by DelegateAuthorized(46)
  var acertoMovManualSaida by DelegateAuthorized(47)
  var acertoMovManualEntrada by DelegateAuthorized(48)
  var acertoMovAtacado by DelegateAuthorized(49)
  var estoqueMov by DelegateAuthorized(50)
  var estoqueCad by DelegateAuthorized(51)
  var ressuprimentoRec by DelegateAuthorized(52)
  var ressuprimentoRecebedor by DelegateAuthorized(53)
  var ressuprimentoExclui by DelegateAuthorized(54)
  var estoqueCD1A by DelegateAuthorized(55)
  var ressuprimentoSep by DelegateAuthorized(56)
  var ressuprimentoPen by DelegateAuthorized(57)
  var reposicaoSep by DelegateAuthorized(58)
  var reposicaoEnt by DelegateAuthorized(59)
  var recebimentoReceber by DelegateAuthorized(60)
  var recebimentoRecebido by DelegateAuthorized(61)
  var tabVendaRef by DelegateAuthorized(62)
  var notaSep by DelegateAuthorized(44)


  //Locais
  private var localEstoque: String?
    get() = locais.split(":").getOrNull(0)
    set(value) {
      val listLocais = locais.split(":")
      locais = listOf(
        value ?: "",
        listLocais.getOrNull(1) ?: "",
      ).joinToString(":")
    }

  var localRessuprimento: String?
    get() = locais.split(":").getOrNull(1)
    set(value) {
      val listLocais = locais.split(":")
      locais = listOf(
        listLocais.getOrNull(0) ?: "",
        value ?: "",
      ).joinToString(":")
    }

  var listaEstoque: Set<String>
    get() = localEstoque?.split(",").orEmpty().filter { it.isNotBlank() }.toSet()
    set(value) {
      localEstoque = value.distinct().sorted().joinToString(",")
    }

  var listaRessuprimento: Set<String>
    get() = localRessuprimento?.split(",").orEmpty().filter { it.isNotBlank() }.toSet()
    set(value) {
      localRessuprimento = value.distinct().sorted().joinToString(",")
    }

  //Lojas

  var lojas: List<String>
    get() = listaLoja.split(",")
    set(value) {
      listaLoja = value.joinToString(",")
    }

  var lojaVale: Int?
    get() = lojas.getOrNull(0)?.toIntOrNull()
    set(value) {
      lojas = lojas.setValue(0, value?.toString() ?: "")
    }

  var impressoraTrans: Set<String>
    get() = lojas.getOrNull(1)?.split(":").orEmpty().map { print ->
      print.trim()
    }.filter { it.isNotBlank() }.toSet()
    set(value) {
      lojas = lojas.setValue(1, value.joinToString(":"))
    }

  var impressoraDev: Set<String>
    get() = lojas.getOrNull(2)?.split(":").orEmpty().map { print ->
      print.trim()
    }.filter { it.isNotBlank() }.toSet()
    set(value) {
      lojas = lojas.setValue(2, value.joinToString(":"))
    }

  var impressoraRet: Set<String>
    get() = lojas.getOrNull(3)?.split(":").orEmpty().map { print ->
      print.trim()
    }.filter { it.isNotBlank() }.toSet()
    set(value) {
      lojas = lojas.setValue(3, value.joinToString(":"))
    }

  var lojaRessu: Int?
    get() = lojas.getOrNull(4)?.toIntOrNull()
    set(value) {
      lojas = lojas.setValue(4, value?.toString() ?: "")
    }

  var impressoraRessu: Set<String>
    get() = lojas.getOrNull(5)?.toString()?.split(":").orEmpty().toSet()
    set(value) {
      lojas = lojas.setValue(5, value.joinToString(":"))
    }
  var localizacaoRepo: Set<String>
    get() = lojas.getOrNull(6)?.toString()?.split(":").orEmpty().toSet()
    set(value) {
      lojas = lojas.setValue(6, value.joinToString(":"))
    }
  var impressoraRepo: Set<String>
    get() = lojas.getOrNull(7)?.toString()?.split(":").orEmpty().toSet()
    set(value) {
      lojas = lojas.setValue(7, value.joinToString(":"))
    }

  var tipoNotaLivre: Int?
    get() = lojas.getOrNull(8)?.toIntOrNull()
    set(value) {
      lojas = lojas.setValue(8, value?.toString() ?: "")
    }

  var lojaNota: Int?
    get() = lojas.getOrNull(9)?.toIntOrNull()
    set(value) {
      lojas = lojas.setValue(9, value?.toString() ?: "")
    }

  var impressoraNota: Set<String>
    get() = lojas.getOrNull(10)?.split(":").orEmpty().toSet()
    set(value) {
      lojas = lojas.setValue(10, value.joinToString(":"))
    }

  var localizacaoNota: Set<String>
    get() = lojas.getOrNull(11)?.split(":").orEmpty().toSet()
    set(value) {
      lojas = lojas.setValue(11, value.joinToString(":"))
    }

  var entregaNotaLiberado: Boolean
    get() = lojas.getOrNull(12) == "S"
    set(value) {
      lojas = lojas.setValue(12, if (value) "S" else "N")
    }

  var lojaProduto: Int?
    get() = lojas.getOrNull(13)?.toIntOrNull()
    set(value) {
      lojas = lojas.setValue(13, value?.toString() ?: "")
    }

  var impressoraProduto: Set<String>
    get() = lojas.getOrNull(14)?.toString()?.split(":").orEmpty().toSet()
    set(value) {
      lojas = lojas.setValue(14, value.joinToString(":"))
    }

  var lojaLocExpedicao: Int?
    get() = lojas.getOrNull(15)?.toIntOrNull()
    set(value) {
      lojas = lojas.setValue(15, value?.toString() ?: "")
    }

  var tipoNotaExpedicao: Set<ETipoNotaFiscal>
    get() = lojas.getOrNull(16)?.toString()?.split(":").orEmpty().toSet().mapNotNull { tipo ->
      ETipoNotaFiscal.entries.firstOrNull { it.name == tipo }
      ?: ETipoNotaFiscal.entries.firstOrNull { it.name == tipo.replace(' ', '_') }
    }.toSet()
    set(value) {
      lojas = lojas.setValue(16, value.joinToString(":") {
        it.name
      })
    }

  var lojaRetira: Int?
    get() = lojas.getOrNull(17)?.toIntOrNull()
    set(value) {
      lojas = lojas.setValue(17, value?.toString() ?: "")
    }

  var retiraTipo: Set<ETipoRetira>
    get() = lojas.getOrNull(18)?.toString()?.split(":").orEmpty().toSet().mapNotNull { tipo ->
      ETipoRetira.entries.firstOrNull { it.name == tipo }
      ?: ETipoRetira.entries.firstOrNull { it.name == tipo.replace(' ', '_') }
    }.toSet()
    set(value) {
      lojas = lojas.setValue(18, value.joinToString(":") {
        it.name
      })
    }

  var lojaRec: Int?
    get() = lojas.getOrNull(19)?.toIntOrNull()
    set(value) {
      lojas = lojas.setValue(19, value?.toString() ?: "")
    }

  var localizacaoRec: Set<String>
    get() = if (admin) setOf("TODOS") else lojas.getOrNull(20)?.split(":").orEmpty().toSet()
    set(value) {
      lojas = lojas.setValue(20, value.joinToString(":"))
    }

  var impressoraNotaTermica: Set<String>
    get() = lojas.getOrNull(21)?.toString()?.split(":").orEmpty().toSet()
    set(value) {
      lojas = lojas.setValue(21, value.joinToString(":"))
    }

  //-------------------------------------------------

  fun List<String>.setValue(index: Int, value: String): List<String> {
    val list = this.toMutableList()
    while (index >= list.size) list.add("")
    list[index] = value
    return list.toList()
  }

  val localizacaoRepoStr
    get() = localizacaoRepo.joinToString(", ")

  val lojaUsuario
    get() = no.toString().substring(0, 1).toIntOrNull() ?: 0

  var produto
    get() = produtoList || produtoEstoqueGiro || produtoEstoqueValidade || produtoInventario || produtoEditor ||
            produtoInventarioAgrupado || admin
    set(value) {
      produtoList = value
      produtoEstoqueGiro = value
      produtoEstoqueValidade = value
      produtoInventario = value
      produtoInventarioAgrupado = value
      produtoEditor = value
    }
  var nota
    get() = notaSep || notaExp || notaCD || notaEnt || notaRota || admin
    set(value) {
      notaSep = value
      notaExp = value
      notaCD = value
      notaEnt = value
      notaRota = value
    }
  var vendaRef: Boolean
    get() = tabVendaRef || admin
    set(value) {
      tabVendaRef = value
    }

  var recebimento: Boolean
    get() = recebimentoReceber || recebimentoRecebido || admin
    set(value) {
      recebimentoReceber = value
      recebimentoRecebido = value
    }
  var ressuprimento
    get() = ressuprimentoCD || ressuprimentoEnt || ressuprimentoPen || ressuprimentoSep || admin
    set(value) {
      ressuprimentoCD = value
      ressuprimentoEnt = value
      ressuprimentoPen = value
      ressuprimentoSep = value
    }

  var reposicao
    get() = reposicaoSep || reposicaoEnt || admin
    set(value) {
      reposicaoSep = value
      reposicaoEnt = value
    }
  val pedido
    get() = pedidoCD || pedidoEnt || admin
  var pedidoTransf
    get() = pedidoTransfReserva || pedidoTransfRessu4 || pedidoTransfEnt ||
            pedidoTransfAutorizada || pedidoTransfCD5A || admin
    set(value) {
      pedidoTransfReserva = value
      pedidoTransfRessu4 = value
      pedidoTransfEnt = value
      pedidoTransfAutorizada = value
      pedidoTransfCD5A = value
    }
  val notaEntrada
    get() = notaEntradaBase || notaEntradaReceber || notaEntradaRecebido || admin
  var pedidoRetira
    get() = retiraImprimir || retiraImpresso || admin
    set(value) {
      retiraImprimir = value
      retiraImpresso = value
    }

  var estoqueCD
    get() = estoqueMov || estoqueCad || estoqueCD1A || estoqueSaldo || admin
    set(value) {
      estoqueMov = value
      estoqueCad = value
      estoqueCD1A = value
    }

  val fornecedor
    get() = produtoList

  var cliente
    get() = clienteCadastro || admin
    set(value) {
      clienteCadastro = value
    }

  var devCliente
    get() = devCliImprimir || devCliImpresso || devCliValeTrocaProduto || devCliCredito ||
            devCliEditor || devClienteTroca || devCliSemPrd || admin
    set(value) {
      devCliImprimir = value
      devCliImpresso = value
      devCliValeTrocaProduto = value
      devCliCredito = value
      devCliEditor = value
      devClienteTroca = value
      devCliSemPrd = value
    }

  var acertoEstoque
    get() = acertoEntrada || acertoSaida || acertoMovManualSaida || acertoMovManualEntrada || acertoMovAtacado || admin
    set(value) {
      acertoEntrada = value
      acertoSaida = value
      acertoMovManualSaida = value
      acertoMovManualEntrada = value
      acertoMovAtacado = value
    }

  override val admin
    get() = login == "ADM"

  companion object {
    fun findAll(): List<UserSaci> {
      return saci.findAllUser().filter { it.ativo }
    }

    fun updateUser(user: UserSaci) {
      saci.updateUser(user)
    }

    fun findUser(login: String?): List<UserSaci> {
      return saci.findUser(login)
    }

    fun userEstoqueLocais(): List<String> {
      val username = AppConfig.userLogin() as? UserSaci
      if (username?.admin == true) return listOf("TODOS")
      return username?.listaEstoque?.toList() ?: emptyList()
    }

    fun userRessuprimentoLocais(): List<String> {
      val username = AppConfig.userLogin() as? UserSaci
      if (username?.admin == true) return listOf("TODOS")
      return username?.listaRessuprimento?.toList() ?: emptyList()
    }
  }
}

class DelegateAuthorized(numBit: Int) {
  private val bit = 2.toDouble().pow(numBit).toLong()

  operator fun getValue(thisRef: UserSaci?, property: KProperty<*>): Boolean {
    thisRef ?: return false
    return (thisRef.bitAcesso and bit) != 0L || thisRef.admin
  }

  operator fun setValue(thisRef: UserSaci?, property: KProperty<*>, value: Boolean?) {
    thisRef ?: return
    val v = value ?: false
    thisRef.bitAcesso = when {
      v    -> thisRef.bitAcesso or bit
      else -> thisRef.bitAcesso and bit.inv()
    }
  }
}


