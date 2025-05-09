package br.com.astrosoft.produto.model.beans

import br.com.astrosoft.framework.model.IUser
import br.com.astrosoft.framework.model.config.AppConfig
import br.com.astrosoft.framework.util.localDate
import br.com.astrosoft.framework.util.toSaciDate
import br.com.astrosoft.produto.model.saci
import java.time.LocalDate
import kotlin.math.pow
import kotlin.reflect.KProperty

class UserSaci : IUser {
  override var no: Int = 0
  override var name: String = ""
  override var login: String = ""
  override var senha: String = ""
  var bitAcesso: Long = 0
  var bitAcesso2: Long = 0
  var storeno: Int = 0
  var locais: String = ""
  var listaImpressora: String = ""
  var listaLoja: String = ""
  var impressora: String? = ""
  var ativoSaci: String = ""
  override var ativo by DelegateAuthorized(0)
  var produtoList by DelegateAuthorized(1)
  var produtoEstoqueGiro by DelegateAuthorized(2)

  //var produtoEstoqueValidade by DelegateAuthorized(3)
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
  var autorizaTrocaP by DelegateAuthorized(46)
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

  var tabVendaRef by DelegateAuthorized(62)
  var notaSep by DelegateAuthorized2(63)/*63 44 */

  var reposicaoAcerto by DelegateAuthorized2(64)/*64 45*/
  var autorizaAcerto by DelegateAuthorized2(65)/*65 46*/
  var reposicaoRetorno by DelegateAuthorized2(66)/*66 53 */

  var recebimentoReceberNota by DelegateAuthorized2(67)/*67 54*/
  var recebimentoNotaRecebida by DelegateAuthorized2(68)/*68 55*/
  var recebimentoAgenda by DelegateAuthorized2(69)/*69 56 */
  var recebimentoPedido by DelegateAuthorized2(70)/*70 57 */
  var produtoCadastro by DelegateAuthorized2(71)/*71 58*/
  var produtoSped by DelegateAuthorized2(72)/*72 59*/
  var recebimentoXML by DelegateAuthorized2(73)
  var recebimentoPreEnt by DelegateAuthorized2(74)
  var notaTroca by DelegateAuthorized2(75)
  var nfdDevFor by DelegateAuthorized2(76)
  var autorizaEstorno by DelegateAuthorized2(77)
  var autorizaReembolso by DelegateAuthorized2(78)
  var autorizaMuda by DelegateAuthorized2(79)
  var autorizaTroca by DelegateAuthorized2(80)
  var recebimentoNotaEntrada by DelegateAuthorized2(81)
  var pedidoRessuprimento by DelegateAuthorized2(82)
  var ressuprimentoDuplica by DelegateAuthorized2(83)
  var ressuprimentoRemove by DelegateAuthorized2(84)
  var ressuprimentoSepara by DelegateAuthorized2(85)
  var ressuprimentoRemoveProd by DelegateAuthorized2(86)
  var ressuprimentoEditaQuant by DelegateAuthorized2(87)
  var ressuprimentoExibePedidoPai by DelegateAuthorized2(88)
  var acertoPedido by DelegateAuthorized2(89)
  var acertoRemoveProd by DelegateAuthorized2(90)
  var precificacaoPrecificacao by DelegateAuthorized2(91)
  var precificacaoEntrada: Boolean by DelegateAuthorized2(92)
  var precificacaoSaida: Boolean by DelegateAuthorized2(93)
  var precificacaoEntradaMa: Boolean by DelegateAuthorized2(94)
  var produtoEstoqueValidadeLoja by DelegateAuthorized2(95)
  var recebimentoValidade by DelegateAuthorized2(96)
  var estoqueEditaLoc by DelegateAuthorized2(97)
  var estoqueCopiaLoc by DelegateAuthorized2(98)
  var estoqueConf by DelegateAuthorized2(99)
  var estoqueAcerto by DelegateAuthorized2(100)
  var estoqueGravaAcerto by DelegateAuthorized2(101)
  var estoqueInventario by DelegateAuthorized2(102)
  var estoqueAcertoMobile by DelegateAuthorized2(103)
  var avariaRecEditor by DelegateAuthorized2(104)
  var avariaRecPendente by DelegateAuthorized2(105)
  var avariaRecTransportadora by DelegateAuthorized2(106)
  var avariaRecEmail by DelegateAuthorized2(107)
  var avariaRecNFD by DelegateAuthorized2(108)
  var avariaRecAcerto by DelegateAuthorized2(109)
  var avariaRecReposto by DelegateAuthorized2(110)
  var ressuprimentoCopiaPedido by DelegateAuthorized2(111)
  var devFor2NotaPendencia by DelegateAuthorized2(112)
  var devFor2NotaNFD by DelegateAuthorized2(113)
  var devFor2NotaTransportadora by DelegateAuthorized2(114)
  var devFor2NotaEmail by DelegateAuthorized2(115)
  var devFor2NotaReposto by DelegateAuthorized2(116)
  var devFor2NotaAcerto by DelegateAuthorized2(117)
  var devFor2NotaGarantia by DelegateAuthorized2(118)
  var estoqueGravaGarantia by DelegateAuthorized2(119)
  var estoqueGarantia by DelegateAuthorized2(120)

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

  var dataIncialKardec: LocalDate
    get() = lojas.getOrNull(22)?.toIntOrNull()?.localDate() ?: LocalDate.now().withDayOfMonth(1)
    set(value) {
      lojas = lojas.setValue(22, value.toSaciDate().toString())
    }

  var tipoMetodo: Set<EMetodo>
    get() = lojas.getOrNull(23)?.split(",").orEmpty().mapNotNull { tipo ->
      tipo.toIntOrNull()?.let { num ->
        EMetodo.entries.firstOrNull { it.num == num }
      }
    }.toSet()
    set(value) {
      lojas = lojas.setValue(23, value.joinToString(",") {
        it.num.toString()
      })
    }

  var impressoraRec: Set<String>
    get() = lojas.getOrNull(24)?.toString()?.split(":").orEmpty().toSet()
    set(value) {
      lojas = lojas.setValue(24, value.joinToString(":"))
    }

  var valorMinimoTrocaP: Int
    get() = lojas.getOrNull(25)?.toIntOrNull() ?: 500
    set(value) {
      lojas = lojas.setValue(25, value.toString())
    }

  var valorMinimoTroca: Int
    get() = lojas.getOrNull(26)?.toIntOrNull() ?: 0
    set(value) {
      lojas = lojas.setValue(26, value.toString())
    }

  var valorMinimoEstorno: Int
    get() = lojas.getOrNull(27)?.toIntOrNull() ?: 0
    set(value) {
      lojas = lojas.setValue(27, value.toString())
    }

  var valorMinimoReembolso: Int
    get() = lojas.getOrNull(28)?.toIntOrNull() ?: 0
    set(value) {
      lojas = lojas.setValue(28, value.toString())
    }

  var valorMinimoMuda: Int
    get() = lojas.getOrNull(29)?.toIntOrNull() ?: 0
    set(value) {
      lojas = lojas.setValue(29, value.toString())
    }

  var impressoraAcerto: Set<String>
    get() = lojas.getOrNull(30)?.toString()?.split(":").orEmpty().toSet()
    set(value) {
      lojas = lojas.setValue(30, value.joinToString(":"))
    }

  var lojaAcerto: Int?
    get() = lojas.getOrNull(31)?.toIntOrNull()
    set(value) {
      lojas = lojas.setValue(31, value?.toString() ?: "")
    }

  var impressoraEstoque: Set<String>
    get() = lojas.getOrNull(32)?.toString()?.split(":").orEmpty().toSet()
    set(value) {
      lojas = lojas.setValue(32, value.joinToString(":"))
    }

  var lojaConferencia: Int?
    get() = lojas.getOrNull(33)?.toIntOrNull()
    set(value) {
      lojas = lojas.setValue(33, value?.toString() ?: "")
    }

  var devFor2Loja: Int?
    get() = lojas.getOrNull(34)?.toIntOrNull()
    set(value) {
      lojas = lojas.setValue(34, value?.toString() ?: "")
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
    get() = produtoList || produtoCadastro || produtoSped || produtoEstoqueGiro ||
            produtoInventario || produtoEditor || produtoInventarioAgrupado || produtoEstoqueValidadeLoja || admin
    set(value) {
      produtoList = value
      produtoEstoqueGiro = value
      produtoInventario = value
      produtoInventarioAgrupado = value
      produtoEditor = value
      produtoEstoqueValidadeLoja = value
    }
  var nfd
    get() = nfdDevFor || admin
    set(value) {
      nfdDevFor = value
    }
  var nota
    get() = notaSep || notaTroca || notaExp || notaCD || notaEnt || notaRota || admin
    set(value) {
      notaSep = value
      notaTroca = value
      notaExp = value
      notaCD = value
      notaEnt = value
      notaRota = value
    }
  var devFor2
    get() = devFor2NotaPendencia || devFor2NotaNFD || devFor2NotaTransportadora || devFor2NotaEmail ||
            devFor2NotaReposto || devFor2NotaAcerto || devFor2NotaGarantia || recebimentoNotaEntrada || admin
    set(value) {
      devFor2NotaPendencia = value
      devFor2NotaNFD = value
      devFor2NotaTransportadora = value
      devFor2NotaEmail = value
      devFor2NotaAcerto = value
      devFor2NotaReposto = value
      devFor2NotaGarantia = value
      recebimentoNotaEntrada = value
    }
  val menuDevolucaoAvariaRec: Boolean
    get() {
      return true
    }

  var vendaRef: Boolean
    get() = tabVendaRef || admin
    set(value) {
      tabVendaRef = value
    }

  var recebimento: Boolean
    get() = recebimentoPedido || recebimentoAgenda || recebimentoXML || recebimentoPreEnt || recebimentoValidade
            || recebimentoReceberNota || recebimentoNotaRecebida || admin
    set(value) {
      recebimentoPedido = value
      recebimentoAgenda = value
      recebimentoXML = value
      recebimentoPreEnt = value
      recebimentoReceberNota = value
      recebimentoNotaRecebida = value
      recebimentoValidade = value
    }
  var ressuprimento
    get() = pedidoRessuprimento || ressuprimentoCD || ressuprimentoEnt || ressuprimentoPen || ressuprimentoSep || admin
    set(value) {
      pedidoRessuprimento = value
      ressuprimentoCD = value
      ressuprimentoEnt = value
      ressuprimentoPen = value
      ressuprimentoSep = value
    }

  var reposicao
    get() = reposicaoSep || reposicaoEnt || reposicaoAcerto || reposicaoRetorno || admin
    set(value) {
      reposicaoSep = value
      reposicaoEnt = value
      reposicaoAcerto = value
      reposicaoRetorno = value
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

  var precificacao
    get() = precificacaoPrecificacao || precificacaoEntrada || precificacaoEntradaMa ||
            precificacaoSaida || admin
    set(value) {
      precificacaoPrecificacao = value
      precificacaoEntrada = value
      precificacaoEntradaMa = value
      precificacaoSaida = value
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
    get() = estoqueMov || estoqueCad || estoqueCD1A || estoqueSaldo || estoqueConf || estoqueAcerto ||
            estoqueAcertoMobile || estoqueInventario || estoqueGarantia || admin
    set(value) {
      estoqueMov = value
      estoqueCad = value
      estoqueCD1A = value
      estoqueConf = value
      estoqueAcerto = value
      estoqueAcertoMobile = value
      estoqueInventario = value
      estoqueGarantia = value
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
    get() = acertoPedido || acertoEntrada || acertoSaida || acertoMovManualSaida || acertoMovManualEntrada
            || acertoMovAtacado || admin
    set(value) {
      acertoPedido = value
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
      login ?: return emptyList()
      if (login.isBlank()) {
        return emptyList()
      }
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

    fun userAdmin(): UserSaci? {
      return findUser("ADM").firstOrNull()
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

class DelegateAuthorized2(numBit2: Int) {
  private val bit = 2.toDouble().pow(numBit2 - 62).toLong()

  operator fun getValue(thisRef: UserSaci?, property: KProperty<*>): Boolean {
    thisRef ?: return false
    return (thisRef.bitAcesso2 and bit) != 0L || thisRef.admin
  }

  operator fun setValue(thisRef: UserSaci?, property: KProperty<*>, value: Boolean?) {
    thisRef ?: return
    val v = value ?: false
    thisRef.bitAcesso2 = when {
      v    -> thisRef.bitAcesso2 or bit
      else -> thisRef.bitAcesso2 and bit.inv()
    }
  }
}