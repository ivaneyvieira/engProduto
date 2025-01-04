package br.com.astrosoft.produto.model.planilha

import br.com.astrosoft.framework.model.planilha.Planilha
import br.com.astrosoft.produto.model.beans.Precificacao

class PlanilhaPrecificacao : Planilha<Precificacao>("Precificação") {
  init {
    columnSheet(header = "Cod", property = Precificacao::codigo)
    columnSheet(header = "Descrição", property = Precificacao::descricao)
    columnSheet(header = "Cod For", property = Precificacao::vendno)
    columnSheet(header = "Tipo", property = Precificacao::typeno)
    columnSheet(header = "CL", property = Precificacao::clno)
    columnSheet(header = "NCM", property = Precificacao::ncm)
    columnSheet(header = "Rotulo", property = Precificacao::rotulo)
    columnSheet(header = "Trib", property = Precificacao::tributacao)
    columnSheet(header = "MVA", property = Precificacao::mvap)
    columnSheet(header = "ICMS Ent", property = Precificacao::creditoICMS)
    columnSheet(header = "P. Fab", property = Precificacao::pcfabrica)
    columnSheet(header = "IPI", property = Precificacao::ipi)
    columnSheet(header = "Emb", property = Precificacao::embalagem)
    columnSheet(header = "IR ST", property = Precificacao::retido)
    columnSheet(header = "C. ICMS", property = Precificacao::icmsp)
    columnSheet(header = "Frete", property = Precificacao::frete)
    columnSheet(header = "ICMS Calc F", property = Precificacao::freteICMSCalc)
    columnSheet(header = "ICMS F", property = Precificacao::freteICMS)
    columnSheet(header = "C.Cont", property = Precificacao::custoContabil)
    columnSheet(header = "P.Custo", property = Precificacao::precoCusto)
    columnSheet(header = "Dif", property = Precificacao::diferencaCusto)
    columnSheet(header = "ICM Sai", property = Precificacao::icms)
    columnSheet(header = "FCP", property = Precificacao::fcp)
    columnSheet(header = "Pis", property = Precificacao::pis)
    columnSheet(header = "IR", property = Precificacao::ir)
    columnSheet(header = "CS", property = Precificacao::contrib)
    columnSheet(header = "CPMF", property = Precificacao::cpmf)
    columnSheet(header = "Desp", property = Precificacao::fixa)
    columnSheet(header = "Out", property = Precificacao::outras)
    columnSheet(header = "Lucro", property = Precificacao::lucroLiq)
    columnSheet(header = "P.Sug.", property = Precificacao::precoSug)
    columnSheet(header = "P.Ref.", property = Precificacao::precoRef)
  }
}

