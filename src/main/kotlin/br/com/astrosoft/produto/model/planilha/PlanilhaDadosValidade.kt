package br.com.astrosoft.produto.model.planilha

import br.com.astrosoft.framework.model.planilha.Planilha
import br.com.astrosoft.produto.model.beans.DadosValidade

class PlanilhaDadosValidade : Planilha<DadosValidade>("Dados Validade") {
  init {
    columnSheet(DadosValidade::codigo, header = "Código")
    columnSheet(DadosValidade::descricao, header = "Descrição")
    columnSheet(DadosValidade::grade, header = "Grade")
    columnSheet(DadosValidade::unidade, header = "Un")
    columnSheet(DadosValidade::vendno, header = "Cod For")
    columnSheet(DadosValidade::validade, header = "Val")
    columnSheet(DadosValidade::vencimentoStr, header = "Venc")
  }
}