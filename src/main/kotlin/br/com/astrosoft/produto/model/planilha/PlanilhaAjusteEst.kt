package br.com.astrosoft.produto.model.planilha

import br.com.astrosoft.framework.model.planilha.Planilha
import br.com.astrosoft.produto.model.beans.AjusteEst

class PlanilhaAjusteEst : Planilha<AjusteEst>("Ajuste Estoque") {
  init {
    columnSheet(AjusteEst::loja, header = "Loja")
    columnSheet(AjusteEst::codigo, header = "Código")
    columnSheet(AjusteEst::descricao, header = "Descrição")
    columnSheet(AjusteEst::gradeProduto, header = "Grade")
    columnSheet(AjusteEst::unidade, header = "Unidade")
    columnSheet(AjusteEst::estoqueLojas, header = "Est Lojas")
    columnSheet(AjusteEst::qttyVarejo, header = "Varejo")
    columnSheet(AjusteEst::qttyAtacado, header = "Atacado")
    columnSheet(AjusteEst::qttyTotal, header = "Total")
    columnSheet(AjusteEst::tributacao, header = "Trib")
    columnSheet(AjusteEst::rotulo, header = "Rotulo")
    columnSheet(AjusteEst::ncm, header = "NCM")
    columnSheet(AjusteEst::fornecedor, header = "For")
    columnSheet(AjusteEst::abrev, header = "Abrev")
    columnSheet(AjusteEst::tipo, header = "Tipo")
    columnSheet(AjusteEst::cl, header = "Cl")
  }
}