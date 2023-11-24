package br.com.astrosoft.produto.model.beans

import br.com.astrosoft.framework.model.IUser
import br.com.astrosoft.framework.model.config.AppConfig
import br.com.astrosoft.produto.model.saci
import kotlin.math.pow
import kotlin.reflect.KProperty

class UserSaci : IUser {
  override var no: Int = 0
  var name: String = ""
  override var login: String = ""
  override var senha: String = ""
  var bitAcesso: Long = 0
  var storeno: Int = 0
  var locais: String = ""
  var listaImpressora: String = ""
  var listaLoja: String = ""
  var impressora: String? = ""
  override var ativo by DelegateAuthorized(0)
  var produtoList by DelegateAuthorized(1)
  var produtoReserva by DelegateAuthorized(2)
  var produtoRetiraEntrega by DelegateAuthorized(3)
  var produtoRetiraEntregaEdit by DelegateAuthorized(4)
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
  var notaEntradaPendente by DelegateAuthorized(15)
  var notaEntradaRecebido by DelegateAuthorized(16)
  var nfceExpedicao by DelegateAuthorized(17)
  var vendaExpedicao by DelegateAuthorized(18)
  var entRetExpedicao by DelegateAuthorized(19)
  var transfExpedicao by DelegateAuthorized(20)
  var vendaFExpedicao by DelegateAuthorized(21)

  //Permissões da tela receber
  var receberQuantidade by DelegateAuthorized(22)
  var receberExcluir by DelegateAuthorized(23)
  var receberAdicionar by DelegateAuthorized(24)
  var receberProcessar by DelegateAuthorized(25)
  var notaEntradaBase by DelegateAuthorized(26)
  var pedidoTransfReserva by DelegateAuthorized(27)
  var pedidoTransfEnt by DelegateAuthorized(28)
  var pedidoTransfAutorizada by DelegateAuthorized(29)
  var pedidoTransfRessu4 by DelegateAuthorized(30)
  var devCliValeTroca by DelegateAuthorized(31)
  var devCliValeTrocaImp by DelegateAuthorized(32)
  var devCliValeTrocaProduto by DelegateAuthorized(33)

  var lojas: List<String>
    get() = listaLoja.split(",").map { print ->
      print.trim()
    }
    set(value) {
      listaLoja = value.joinToString(",") { print ->
        print
      }
    }

  var lojaVale: Int?
    get() = lojas.getOrNull(0)?.toIntOrNull()
    set(value) {
      val listLojas = lojas
      lojas = listOf(
        value?.toString() ?: "",
        listLojas.getOrNull(1) ?: "",
        listLojas.getOrNull(2) ?: ""
      )
    }

  var impressoraTrans: String?
    get() = lojas.getOrNull(1)
    set(value) {
      val listLojas = lojas
      lojas = listOf(
        listLojas.getOrNull(0) ?: "",
        value ?: "",
        listLojas.getOrNull(2) ?: ""
      )
    }

  var impressoraDev: String?
    get() = lojas.getOrNull(2)
    set(value) {
      val listLojas = lojas
      lojas = listOf(
        listLojas.getOrNull(0) ?: "",
        listLojas.getOrNull(1) ?: "",
        value ?: ""
      )
    }

  val lojaUsuario
    get() = no.toString().substring(0, 1).toIntOrNull() ?: 0

  val produto
    get() = produtoList || produtoReserva || produtoRetiraEntrega || produtoRetiraEntregaEdit || admin
  val nota
    get() = notaExp || notaCD || notaEnt || admin
  val pedido
    get() = pedidoCD || pedidoEnt || admin
  val pedidoTransf
    get() = pedidoTransfReserva || pedidoTransfAutorizada || pedidoTransfRessu4 || pedidoTransfEnt || admin
  val ressuprimento
    get() = ressuprimentoCD || ressuprimentoEnt || admin
  val notaEntrada
    get() = notaEntradaBase || notaEntradaReceber || notaEntradaRecebido || admin

  val fornecedor
    get() = produtoList

  val devCliente
    get() = devCliValeTroca || devCliValeTrocaImp || devCliValeTrocaProduto || admin

  var listLocais: Set<String>
    get() = locais.split(",").filter { it.isNotBlank() }.toSet()
    set(value) {
      locais = value.joinToString(separator = ",")
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

    fun userLocais(): List<String> {
      val username = AppConfig.userLogin() as? UserSaci
      if (username?.admin == true) return listOf("TODOS")
      return username?.listLocais?.toList() ?: emptyList()
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


