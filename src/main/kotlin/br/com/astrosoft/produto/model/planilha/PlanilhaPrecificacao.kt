package br.com.astrosoft.produto.model.planilha

import br.com.astrosoft.framework.model.planilha.Planilha
import br.com.astrosoft.produto.model.beans.Precificacao

class PlanilhaPrecificacao : Planilha<Precificacao>("Precificação") {
  init {
    columnSheet(property = Precificacao::codigo, header = "Cod")
    columnSheet(Precificacao::descricao, "Descrição")

    columnSheet(Precificacao::estoque, "Est")

    columnSheet(Precificacao::nfValor, "V. NF")
    columnSheet(Precificacao::pcfabrica, "P. Fab")

    columnSheet(Precificacao::nfIpi, "IPI NF")
    columnSheet(Precificacao::ipi, "IPI")

    columnSheet(Precificacao::nfIrst, "IR ST NF")
    columnSheet(Precificacao::retido, "IR ST")

    columnSheet(Precificacao::nfIcms, "ICMS NF")
    columnSheet(Precificacao::icmsp, "C. ICMS")

    columnSheet(Precificacao::nfFrete, "Frete NF")
    columnSheet(Precificacao::frete, "Frete")

    columnSheet(Precificacao::pisCofins, "Pis/Cofins")
    columnSheet(Precificacao::custoContabil, "C.Cont")
    columnSheet(Precificacao::embalagem, "Emb")
    columnSheet(Precificacao::vendno, "Cod For")
    columnSheet(Precificacao::typeno, "Tipo")
    columnSheet(Precificacao::clno, "CL")
    columnSheet(Precificacao::ncm, "NCM")
    columnSheet(Precificacao::rotulo, "Rótulo")
    columnSheet(Precificacao::tributacao, "Trib")
    columnSheet(Precificacao::mvap, "MVA")
    columnSheet(Precificacao::creditoICMS, "ICMS Ent")
    columnSheet(Precificacao::freteICMSCalc, "ICMS Calc F")
    columnSheet(Precificacao::freteICMS, "ICMS F")

    columnSheet(Precificacao::cfinanceiro, "C. Fin")
    columnSheet(Precificacao::precoCusto, "P.Custo")
    columnSheet(Precificacao::diferencaCusto, "Dif")

  }
}

