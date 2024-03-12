package br.com.astrosoft.produto.model.beans

import br.com.astrosoft.framework.model.config.AppConfig
import br.com.astrosoft.produto.model.beans.UserSaci.Companion.userRessuprimentoLocais
import br.com.astrosoft.produto.model.saci
import java.time.LocalDate

class Ressuprimento(
  var numero: Int,
  var fornecedor: Int,
  var data: LocalDate?,
  var comprador: Int,
  var localizacao: String?,
  var marca: Int?,
  var notaBaixa: String?,
  var dataBaixa: LocalDate?,
  var valorNota: Double?,
  var entregueNo: Int?,
  var entreguePor: String?,
  var entregueSPor: String?,
  var transportadoNo: Int?,
  var transportadoPor: String?,
  var transportadoSPor: String?,
  var recebidoNo: Int?,
  var recebidoPor: String?,
  var recebidoSPor: String?,
  var usuarioNo: Int?,
  var usuario: String?,
  var login: String?,
  var observacao: String?,
  var marcaEnt: Int?,
  var marcaRec: Int?,
) {
  val lojaRessu
    get() = numero.toString().substring(0, 1).toIntOrNull()

  val rotaRessuprimento
    get() = if (lojaRessu == null) "" else "Rota4${lojaRessu}"

  val usuarioApp: String?
    get() {
      val user = AppConfig.userLogin() as? UserSaci
      return user?.login
    }

  fun produtos(): List<ProdutoRessuprimento> {
    val marcaRessu = EMarcaRessuprimento.entries.firstOrNull { it.num == marca } ?: return emptyList()
    return saci.findProdutoRessuprimento(this, marcaRessu, userRessuprimentoLocais())
  }

  fun autoriza(user: UserSaci) {
    this.entregueNo = user.no
    saci.autorizaRessuprimento(this)
  }

  fun autorizaRecebido(user: UserSaci) {
    this.recebidoNo = user.no
    saci.recebeRessuprimento(this)
  }

  fun entregue(funcionario: Funcionario) {
    this.entregueNo = funcionario.codigo
    saci.entregueRessuprimento(this)
  }

  fun recebe(funcionario: Funcionario) {
    this.recebidoNo = funcionario.codigo
    saci.recebeRessuprimento(this)
  }

  fun transportado(funcionario: Funcionario) {
    this.transportadoNo = funcionario.codigo
    saci.transportadoRessuprimento(this)
  }

  fun exclui() {
    saci.excluiRessuprimento(this)
  }

  fun salva() {
    saci.salvaRessuprimento(this)
  }

  companion object {
    fun find(filtro: FiltroRessuprimento) = saci.findRessuprimento(filtro, userRessuprimentoLocais())
  }
}

data class FiltroRessuprimento(
  val numero: Int,
  val pesquisa: String,
  val marca: EMarcaRessuprimento,
  val temNota: Boolean,
  val lojaRessu: Int,
  val dataPedidoInicial: LocalDate? = null,
  val dataPedidoFinal: LocalDate? = null,
  val dataNotaInicial: LocalDate? = null,
  val dataNotaFinal: LocalDate? = null,
)

enum class EMarcaRessuprimento(val num: Int, val descricao: String) {
  CD(0, "CD"),
  ENT(1, "Entregue"),
  REC(2, "Recebido")
}