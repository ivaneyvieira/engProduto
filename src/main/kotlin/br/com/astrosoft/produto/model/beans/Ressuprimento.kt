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
  var marcaCD: Int?,
  var marcaEnt: Int?,
  var marcaRec: Int?,
  var cancelada: String?,
  var notaBaixa: String?,
  var dataBaixa: LocalDate?,
  var valorNota: Double?,
  var singno: Int?,
  var sing: String?,
  var transportadoNo: Int?,
  var transportadoPor: String?,
  var recebidoNo: Int?,
  var recebidoPor: String?,
  var usuarioNo: Int?,
  var usuario: String?,
  var observacao: String?,
) {
  val lojaRessu
    get() = numero.toString().substring(0, 1).toIntOrNull()

  val rotaRessuprimento
    get() = if (lojaRessu == null) "" else "Rota4${lojaRessu}"

  val usuarioApp: String?
    get() {
      val user = AppConfig.userLogin() as? UserSaci
      return user?.name
    }
  val situacao
    get() = if (cancelada == "S") "Cancelada" else ""

  fun produtos(marca: EMarcaRessuprimento) = saci.findProdutoRessuprimento(this, marca, userRessuprimentoLocais())
  fun autoriza(user: UserSaci) {
    this.singno = user.no
    saci.autorizaRessuprimento(this)
  }

  fun autorizaRecebido(user: UserSaci) {
    this.recebidoNo = user.no
    saci.recebeRessuprimento(this)
  }

  fun entregue(funcionario: Funcionario) {
    this.singno = funcionario.codigo
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

  fun exclui(): Int {
    return saci.excluiRessuprimento(this)
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