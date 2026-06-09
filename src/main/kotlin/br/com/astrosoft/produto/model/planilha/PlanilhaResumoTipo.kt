package br.com.astrosoft.produto.model.planilha

import br.com.astrosoft.framework.model.planilha.Planilha
import br.com.astrosoft.produto.model.beans.NotaResumoPgto

class PlanilhaResumoTipo : Planilha<NotaResumoPgto>("Vendas") {
  init {
    columnSheet(NotaResumoPgto::loja, header = "Loja")
    columnSheet(NotaResumoPgto::dataFormatada, header = "Data")
    columnSheet(NotaResumoPgto::tipoPgto, header = "Tipo Pgto")
    columnSheet(NotaResumoPgto::valorTipo, header = "Valor Total")
    columnSheet(NotaResumoPgto::perVenda, header = "% Venda")
  }
}