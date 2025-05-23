package br.com.astrosoft.produto.model.planilha

import br.com.astrosoft.framework.model.planilha.Planilha
import br.com.astrosoft.framework.view.vaadin.helper.right
import br.com.astrosoft.produto.model.beans.NotaRecebimentoProduto

class PlanilhaNotasEntrada : Planilha<NotaRecebimentoProduto>("Nota Entrada") {
  init {
    columnSheet(NotaRecebimentoProduto::codigo, "Código")
    columnSheet(NotaRecebimentoProduto::barcodeStrListEntrada, "Código de Barras")
    columnSheet(NotaRecebimentoProduto::refFabrica, "Ref Fabrica")
    columnSheet(NotaRecebimentoProduto::descricao, "Descrição")
    columnSheet(NotaRecebimentoProduto::grade, "Grade")
    columnSheet(NotaRecebimentoProduto::cfop, "CFOP")
    columnSheet(NotaRecebimentoProduto::cst, "CST")
    columnSheet(NotaRecebimentoProduto::un, "UN")
    columnSheet(NotaRecebimentoProduto::quant, "Quant")
    columnSheet(NotaRecebimentoProduto::valorUnit, "Valor Unit")
    columnSheet(NotaRecebimentoProduto::valorTotal, "Valor Total")
    columnSheet(NotaRecebimentoProduto::valorDesconto, "Desc")
    columnSheet(NotaRecebimentoProduto::frete, "Frete")
    columnSheet(NotaRecebimentoProduto::outDesp, "Desp")
    columnSheet(NotaRecebimentoProduto::baseIcms, "Base ICMS")
    columnSheet(NotaRecebimentoProduto::baseSubst, "Base ST")
    columnSheet(NotaRecebimentoProduto::icmsSubst, "Valor ST")
    columnSheet(NotaRecebimentoProduto::valIcms, "V. ICMS")
    columnSheet(NotaRecebimentoProduto::valIPI, "V. IPI")
    columnSheet(NotaRecebimentoProduto::icms, "ICMS")
    columnSheet(NotaRecebimentoProduto::ipi, "IPI")
    columnSheet(NotaRecebimentoProduto::totalGeral, "Total")
  }
}
