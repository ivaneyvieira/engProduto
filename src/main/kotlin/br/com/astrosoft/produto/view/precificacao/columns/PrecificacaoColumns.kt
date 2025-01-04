package br.com.astrosoft.produto.view.precificacao.columns

import br.com.astrosoft.framework.view.vaadin.helper.columnGrid
import br.com.astrosoft.produto.model.beans.Precificacao
import com.github.mvysny.karibudsl.v10.isExpand
import com.vaadin.flow.component.grid.Grid

object PrecificacaoColumns {
  fun Grid<Precificacao>.promocaoCodigo() = columnGrid(Precificacao::codigo) {
    this.setHeader("Cod")
  }

  fun Grid<Precificacao>.promocaoDescricao() = columnGrid(Precificacao::descricao) {
    this.setHeader("Descrição")
  }

  fun Grid<Precificacao>.promocaoRotulo() = columnGrid(Precificacao::rotulo) {
    this.setHeader("Rótulo")
  }

  fun Grid<Precificacao>.promocaoVendno() = columnGrid(Precificacao::vendno) {
    this.setHeader("Cod For")
    this.isExpand = false
  }

  fun Grid<Precificacao>.promocaoTypeno() = columnGrid(Precificacao::typeno) {
    this.setHeader("Tipo")
    this.isExpand = false
  }

  fun Grid<Precificacao>.promocaoClno() = columnGrid(Precificacao::clno) {
    this.setHeader("CL")
  }

  fun Grid<Precificacao>.promocaoNcm() = columnGrid(Precificacao::ncm) {
    this.setHeader("NCM")
  }

  fun Grid<Precificacao>.promocaoFornecedor() = columnGrid(Precificacao::fornecedor) {
    this.setHeader("Fornecedor")
  }

  fun Grid<Precificacao>.promocaoCpmf() = columnGrid(Precificacao::cpmf) {
    this.setHeader("CPMF")
  }

  fun Grid<Precificacao>.promocaoIcmsSai() = columnGrid(Precificacao::icms) {
    this.setHeader("ICM Sai")
  }

  fun Grid<Precificacao>.promocaoPis() = columnGrid(Precificacao::pis) {
    this.setHeader("Pis")
  }

  fun Grid<Precificacao>.promocaoIR() = columnGrid(Precificacao::ir) {
    this.setHeader("IR")
  }

  fun Grid<Precificacao>.promocaoCS() = columnGrid(Precificacao::contrib) {
    this.setHeader("CS")
  }

  fun Grid<Precificacao>.promocaoDesp() = columnGrid(Precificacao::fixa) {
    this.setHeader("Desp")
  }

  fun Grid<Precificacao>.promocaoOut() = columnGrid(Precificacao::outras) {
    this.setHeader("Out")
  }

  fun Grid<Precificacao>.promocaoLucro() = columnGrid(Precificacao::lucroLiq) {
    this.setHeader("Lucro")
  }

  fun Grid<Precificacao>.promocaoPSug() = columnGrid(Precificacao::precoSug) {
    this.setHeader("P.Sug.")
    this.isExpand = false
    this.isAutoWidth = false
    this.width = "100px"
  }

  fun Grid<Precificacao>.promocaoPRef() = columnGrid(Precificacao::precoRef) {
    this.setHeader("P.Ref.")
    this.isExpand = false
    this.isAutoWidth = false
    this.width = "100px"
  }

  fun Grid<Precificacao>.promocaoPDif() = columnGrid(Precificacao::precoDif) {
    this.setHeader("Dif")
    this.isExpand = false
    this.isAutoWidth = false
    this.width = "100px"
  }

  fun Grid<Precificacao>.promocaoFCP() = columnGrid(Precificacao::fcp) {
    this.setHeader("FCP")
  }

  fun Grid<Precificacao>.promocaoMva() = columnGrid(Precificacao::mvap) {
    this.setHeader("MVA")
  }

  fun Grid<Precificacao>.promocaoMvaMaOriginal() = columnGrid(Precificacao::mvaMaOrig) {
    this.setHeader("MVA ORG")
  }

  fun Grid<Precificacao>.promocaoNcmMa() = columnGrid(Precificacao::ncmMa) {
    this.setHeader("NCM MA")
  }

  fun Grid<Precificacao>.promocaoMvaMa00() = columnGrid(Precificacao::mvaMa00) {
    this.setHeader("MVA 0")
  }

  fun Grid<Precificacao>.promocaoMvaMa04() = columnGrid(Precificacao::mvaMa04) {
    this.setHeader("MVA 4")
  }

  fun Grid<Precificacao>.promocaoMvaMa07() = columnGrid(Precificacao::mvaMa07) {
    this.setHeader("MVA 7")
  }

  fun Grid<Precificacao>.promocaoMvaMa12() = columnGrid(Precificacao::mvaMa12) {
    this.setHeader("MVA 12")
  }

  fun Grid<Precificacao>.promocaoIcmsEnt() = columnGrid(Precificacao::creditoICMS) {
    this.setHeader("ICMS Ent")
  }

  fun Grid<Precificacao>.promocaoPFabrica() = columnGrid(Precificacao::pcfabrica) {
    this.setHeader("P. Fab")
    this.isExpand = false
    this.isAutoWidth = false
    this.width = "100px"
  }

  fun Grid<Precificacao>.promocaoIpi() = columnGrid(Precificacao::ipi) {
    this.setHeader("IPI")
  }

  fun Grid<Precificacao>.promocaoEmbalagem() = columnGrid(Precificacao::embalagem) {
    this.setHeader("Emb")
  }

  fun Grid<Precificacao>.promocaoRetido() = columnGrid(Precificacao::retido) {
    this.setHeader("IR ST")
  }

  fun Grid<Precificacao>.promocaoIcms() = columnGrid(Precificacao::icmsp) {
    this.setHeader("C. ICMS")
  }

  fun Grid<Precificacao>.promocaoFrete() = columnGrid(Precificacao::frete) {
    this.setHeader("Frete")
  }

  fun Grid<Precificacao>.promocaoFreteIcmsCalc() = columnGrid(Precificacao::freteICMSCalc) {
    this.setHeader("ICMS Calc F")
  }

  fun Grid<Precificacao>.promocaoFreteIcms() = columnGrid(Precificacao::freteICMS) {
    this.setHeader("ICMS F")
  }

  fun Grid<Precificacao>.promocaoContabil() = columnGrid(Precificacao::custoContabil) {
    this.setHeader("C.Cont")
  }

  fun Grid<Precificacao>.promocaoCFinanceiro() = columnGrid(Precificacao::cfinanceiro) {
    this.setHeader("C. Fin")
  }

  fun Grid<Precificacao>.promocaoPrecoCusto() = columnGrid(Precificacao::precoCusto) {
    this.setHeader("P.Custo")
  }

  fun Grid<Precificacao>.promocaoDiferenca() = columnGrid(Precificacao::diferencaCusto) {
    this.setHeader("Dif")
  }

  fun Grid<Precificacao>.promocaoTributacao() = columnGrid(Precificacao::tributacao) {
    this.setHeader("Trib")
  }
}