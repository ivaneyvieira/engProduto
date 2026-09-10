package br.com.astrosoft.produto.model.planilha

import br.com.astrosoft.framework.model.planilha.Planilha
import br.com.astrosoft.produto.model.beans.NotaRecebimentoDev

class PlanilhaPedidosDev : Planilha<NotaRecebimentoDev>("NFD Aberta") {
  init {
    columnSheet(NotaRecebimentoDev::loja, header = "Loja")
    columnSheet(NotaRecebimentoDev::situacaoDevName, header = "Aba")
    columnSheet(NotaRecebimentoDev::dataColetaStr, header = "Coleta")
    columnSheet(NotaRecebimentoDev::motivoDevolucaoName, header = "Motivo Devolução")
    columnSheet(NotaRecebimentoDev::situacaoDup, header = "Status Dup")
    columnSheet(NotaRecebimentoDev::numeroDevolucao, header = "Pedido")
    columnSheet(NotaRecebimentoDev::valorNFDevolucao, header = "Valor Ped")
    columnSheet(NotaRecebimentoDev::notaDevolucao, header = "NFD")
    columnSheet(NotaRecebimentoDev::emissaoDevolucao, header = "Emissão")
    columnSheet(NotaRecebimentoDev::valorDevolucao, header = "Valor NFD")
    columnSheet(NotaRecebimentoDev::vendnoNF, header = "For NF")
    columnSheet(NotaRecebimentoDev::fornecedorNF, header = "Nome Fornecedor")
    columnSheet(NotaRecebimentoDev::obsDup, header = "Obs Dup")
    columnSheet(NotaRecebimentoDev::userDevolucao, header = "Usuário")
    columnSheet(NotaRecebimentoDev::observacaoDev, header = "Observação")
  }
}
