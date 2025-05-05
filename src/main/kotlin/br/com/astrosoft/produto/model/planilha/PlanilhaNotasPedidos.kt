package br.com.astrosoft.produto.model.planilha

import br.com.astrosoft.framework.model.planilha.Planilha
import br.com.astrosoft.produto.model.beans.NotaRecebimentoProdutoDev

class PlanilhaNotasPedidos : Planilha<NotaRecebimentoProdutoDev>("Nota Dev"){
  init {
    columnSheet(header = "Rótulo", property = NotaRecebimentoProdutoDev::rotulo)
    columnSheet(header = "Fornecedor", property = NotaRecebimentoProdutoDev::vendno)
    columnSheet(header = "NI", property = NotaRecebimentoProdutoDev::ni)
    columnSheet(header = "CFOP", property = NotaRecebimentoProdutoDev::cfop)
    columnSheet(header = "Emissão", property = NotaRecebimentoProdutoDev::dateInvStr)
    columnSheet(header = "NF", property = NotaRecebimentoProdutoDev::nfEntrada)
    columnSheet(header = "Ref do Fab", property = NotaRecebimentoProdutoDev::refFor)
    columnSheet(header = "Código", property = NotaRecebimentoProdutoDev::codigo)
    columnSheet(header = "Descrição", property = NotaRecebimentoProdutoDev::descricao)
    columnSheet(header = "Grade", property = NotaRecebimentoProdutoDev::grade)
    columnSheet(header = "NCM", property = NotaRecebimentoProdutoDev::ncm)
    columnSheet(header = "CST", property = NotaRecebimentoProdutoDev::cst)
    columnSheet(header = "CFOP", property = NotaRecebimentoProdutoDev::cfop)
    columnSheet(header = "Unid", property = NotaRecebimentoProdutoDev::un)
    columnSheet(header = "Quant", property = NotaRecebimentoProdutoDev::qtde)
    columnSheet(header = "V. Unit", property = NotaRecebimentoProdutoDev::valorUnitario)
    columnSheet(header = "V. Total", property = NotaRecebimentoProdutoDev::valorTotal)
    columnSheet(header = "B. Cálc. ICMS", property = NotaRecebimentoProdutoDev::baseICMS)
    columnSheet(header = "MVA", property = NotaRecebimentoProdutoDev::valorMVA)
    columnSheet(header = "B. Cálc. ST", property = NotaRecebimentoProdutoDev::baseSt)
    columnSheet(header = "Valor ST", property = NotaRecebimentoProdutoDev::valorST)
    columnSheet(header = "Valor ICMS", property = NotaRecebimentoProdutoDev::valorICMS)
    columnSheet(header = "Valor IPI", property = NotaRecebimentoProdutoDev::valorIPI)
    columnSheet(header = "Alíq. ICMS", property = NotaRecebimentoProdutoDev::icmsAliq)
    columnSheet(header = "Alíq. IPI", property = NotaRecebimentoProdutoDev::ipiAliq)
    columnSheet(header = "V. Total", property = NotaRecebimentoProdutoDev::valorTotalGeral)
    columnSheet(header = "Chave", property = NotaRecebimentoProdutoDev::chaveUlt)
    columnSheet(header = "Chave Sefaz", property = NotaRecebimentoProdutoDev::chaveSefaz)
  }
}
