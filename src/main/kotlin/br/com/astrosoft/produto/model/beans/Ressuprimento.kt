package br.com.astrosoft.produto.model.beans

import br.com.astrosoft.produto.model.beans.UserSaci.Companion.userLocais
import br.com.astrosoft.produto.model.saci
import java.time.LocalDate

class Ressuprimento(
  var numero: Int,
  var fornecedor: Int,
  var data: LocalDate?,
  var comprador: Int,
  var localizacao: String?,
  var usuarioCD: String?,
  var totalProdutos: Double,
  var marca: Int?,
  var cancelada: String?,
  var notaBaixa: String?,
  var dataBaixa: LocalDate?,
  var singno: Int?,
  var sing: String?,
  var transportadoNo: Int?,
  var transportadoPor: String?,
  var recebidoNo: Int?,
  var recebidoPor: String?,
  var usuarioNo: Int?,
  var usuario: String?,
) {
  val situacao
    get() = if (cancelada == "S") "Cancelada" else ""

  private fun splitCD(index: Int) = usuarioCD?.split("-")?.getOrNull(index) ?: ""

  val usuarioNameCD
    get() = splitCD(0)
  val dataCD
    get() = splitCD(1)
  val horaCD
    get() = splitCD(2)

  val chaveNovaCD: String
    get() = "$usuarioNameCD-$dataCD-$horaCD-$localizacao"

  fun produtos(marca: EMarcaRessuprimento) = saci.findProdutoRessuprimento(this, marca, userLocais())
  fun autoriza(user: UserSaci) {
    this.singno = user.no
    saci.autorizaRessuprimento(this)
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

  companion object {
    fun find(filtro: FiltroRessuprimento) = saci.findRessuprimento(filtro, userLocais())
  }
}

data class FiltroRessuprimento(val numero: Int, val marca: EMarcaRessuprimento)

enum class EMarcaRessuprimento(val num: Int, val descricao: String) {
  CD(0, "CD"), ENT(1, "Entregue"), TODOS(999, "Todos")
}