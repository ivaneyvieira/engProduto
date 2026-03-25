package br.com.astrosoft.produto.model.planilha

import br.com.astrosoft.framework.model.planilha.Planilha
import br.com.astrosoft.produto.model.beans.NotaResumoPgto

class PlanilhaResumoPgto : Planilha<NotaResumoPgto>("Vendas") {
  init {
    columnSheet(NotaResumoPgto::loja, header = "Loja")
    columnSheet(NotaResumoPgto::data, header = "Data")
    columnSheet(NotaResumoPgto::numMetodo, header = "Met")
    columnSheet(NotaResumoPgto::nomeMetodo, header = "Nome Met")
    columnSheet(NotaResumoPgto::mult, pattern = "#,##0.0000", header = "Mlt")
    columnSheet(NotaResumoPgto::documento, header = "Documento")
    columnSheet(NotaResumoPgto::quantParcelas, header = "Parc")
    columnSheet(NotaResumoPgto::mediaPrazo, header = "Pz M")
    columnSheet(NotaResumoPgto::tipoPgto, header = "Tipo Pgto")
    columnSheet(NotaResumoPgto::valor, header = "Valor NF")
    columnSheet(NotaResumoPgto::valorTipo, header = "Valor TP")
  }
}