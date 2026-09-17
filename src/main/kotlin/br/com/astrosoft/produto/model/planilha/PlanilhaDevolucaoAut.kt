package br.com.astrosoft.produto.model.planilha

import br.com.astrosoft.framework.model.planilha.Planilha
import br.com.astrosoft.produto.model.beans.NotaRecebimentoProdutoDev

class PlanilhaDevolucaoAut() : Planilha<NotaRecebimentoProdutoDev>("Devolução Aut") {
  init {
    columnSheet(NotaRecebimentoProdutoDev::motivoDevolucaoName, header = "Motivo da Reclamação")
    columnSheet(NotaRecebimentoProdutoDev::codigo, header = "Código")
    columnSheet(NotaRecebimentoProdutoDev::descricao, header = "Descrição")
    columnSheet(NotaRecebimentoProdutoDev::quant, header = "Qaunt")
    columnSheet(NotaRecebimentoProdutoDev::un, header = "Unidade")
    columnSheet(NotaRecebimentoProdutoDev::nfEntrada, header = "NF")
  }
}