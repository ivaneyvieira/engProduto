package br.com.astrosoft.produto.model.beans

import br.com.astrosoft.produto.model.saci
import java.time.LocalDate

class DadosCredito {
  var custno: Int = 0
  var nome: String = ""
  var cpfCnpj: String = ""
  var tipoPessoa: String = ""
  var tipoCliente: String = ""
  var dataCredito: LocalDate? = null
  var limiteCredito: Double = 0.00
  var ultCompra: LocalDate? = null
  var valorAberto: Double = 0.00
  var valorAtrasado: Double = 0.00
  var valorDisponivel: Double = 0.00

  companion object {
    fun findAll(filtro: FiltroDadosCredito): List<DadosCredito> {
      return saci.selectCredito(filtro)
    }
  }
}

data class FiltroDadosCredito(
  val pesquisa: String,
  val operacao: OperacaoCredito,
  val credito: Double,
)

enum class OperacaoCredito(val cod: String, val descricao: String) {
  IGUAL("=", "="),
  MENOR("<", "<"),
  MAIOR(">", ">"),
  TODOS("T", "Todos")
}