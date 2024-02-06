package br.com.astrosoft.produto.model.planilha

import br.com.astrosoft.framework.model.planilha.Planilha
import br.com.astrosoft.produto.model.beans.MovManual

class PlanilhaMovManual : Planilha<MovManual>("MovManual") {
  init {
    columnSheet(MovManual::loja, header = "Loja")
    columnSheet(MovManual::codigoProduto, header = "Código")
    columnSheet(MovManual::nomeProduto, header = "Descrição")
    columnSheet(MovManual::grade, header = "Grade")
    columnSheet(MovManual::data, header = "Data")
    columnSheet(MovManual::pedido, header = "Pedido")
    columnSheet(MovManual::transacao, header = "Transação")
    columnSheet(MovManual::tipo, header = "Tipo")
    columnSheet(MovManual::qtty, header = "Quant Mov")
    columnSheet(MovManual::estVarejo, header = "Est Varejo")
    columnSheet(MovManual::estAtacado, header = "Est Atacado")
    columnSheet(MovManual::estTotal, header = "Est Total")
  }
}