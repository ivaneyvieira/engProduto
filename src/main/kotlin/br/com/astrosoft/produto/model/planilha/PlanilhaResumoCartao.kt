package br.com.astrosoft.produto.model.planilha

import br.com.astrosoft.framework.model.planilha.Planilha
import br.com.astrosoft.produto.model.beans.NotaResumoCartao

class PlanilhaResumoCartao : Planilha<NotaResumoCartao>("Vendas") {
  init {
    columnSheet(NotaResumoCartao::loja, header = "Loja")
    columnSheet(NotaResumoCartao::data, header = "Data")
    columnSheet(NotaResumoCartao::mult, pattern = "#,##0.0000", header = "Mlt")
    columnSheet(NotaResumoCartao::documento, header = "Documento")
    columnSheet(NotaResumoCartao::quantParcelas, header = "Parc")
    columnSheet(NotaResumoCartao::mediaPrazo, header = "Pz M")
    columnSheet(NotaResumoCartao::tipoPgto, header = "Tipo Pgto")
    columnSheet(NotaResumoCartao::valor, header = "Valor NF")
    columnSheet(NotaResumoCartao::valorTipo, header = "Valor TP")
  }
}