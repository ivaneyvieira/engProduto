package br.com.astrosoft.produto.model.planilha

import br.com.astrosoft.framework.model.planilha.Planilha
import br.com.astrosoft.produto.model.beans.NotaResumo

class PlanilhaResumo : Planilha<NotaResumo>("Vendas") {
  init {
    columnSheet(NotaResumo::loja, header = "Loja")
    columnSheet(NotaResumo::data, header = "Data")
    columnSheet(NotaResumo::numMetodo, header = "Met")
    columnSheet(NotaResumo::nomeMetodo, header = "Nome Met")
    columnSheet(NotaResumo::mult, pattern = "#,##0.0000", header = "Mlt")
    columnSheet(NotaResumo::documento, header = "Documento")
    columnSheet(NotaResumo::quantParcelas, header = "Parc")
    columnSheet(NotaResumo::mediaPrazo, header = "Pz M")
    columnSheet(NotaResumo::tipoPgto, header = "Tipo Pgto")
    columnSheet(NotaResumo::valor, header = "Valor NF")
    columnSheet(NotaResumo::valorTipo, header = "Valor TP")
  }
}