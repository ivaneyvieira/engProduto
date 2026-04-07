package br.com.astrosoft.produto.model.beans

import br.com.astrosoft.produto.model.saci

class AjusteEst(
  var loja: Int?,
  var prdno: String?,
  var codigo: Int?,
  var descricao: String?,
  var gradeProduto: String?,
  var unidade: String?,
  var tipoValidade: String?,
  var mesesGarantia: Int?,
  var estoqueLojas: Int?,
  var estoqueLojasVarejo: Int?,
  var estoqueLojasAtacado: Int?,
  var qttyVarejo: Int?,
  var qttyAtacado: Int?,
  var qttyTotal: Int?,
  var custoAtacado: Double?,
  var custoVarejo: Double?,
  var custoVarejoUnitario: Double?,
  var custoLojasAtacado: Double?,
  var tributacao: String?,
  var rotulo: String?,
  var ncm: String?,
  var fornecedor: Int?,
  var abrev: String?,
  var tipo: Int?,
  var cl: Int?,
  var localizacao: String?,
  var prdnoRel: String?,
  var codigoRel: Int?
) {
  val codigoStr
    get() = this.codigo?.toString() ?: ""

  companion object {
    fun findAjusteEst(filtro: FiltroAjusteEst): List<AjusteEst> {
      return saci.findAjusteEst(filtro)
    }
  }
}

data class FiltroAjusteEst(
  val loja: Int,
  val pesquisa: String,
  val codigo: Int,
  val fornecedor: Int,
  val tributacao: String,
  val rotulo: String,
  val tipo: Int,
  val cl: Int,
  val caracter: ECaracter,
  val letraDup: ELetraDup,
  val grade: Boolean,
  val tipoSaldo: ETipoSaldo,
  val estoque: EEstoque,
  val saldo: Int,
  val consumo: EConsumo,
  val update: Boolean,
) {
  fun lojaSigla(): String {
    return saci.allLojas().firstOrNull { it.no == loja }?.sname ?: ""
  }
}
