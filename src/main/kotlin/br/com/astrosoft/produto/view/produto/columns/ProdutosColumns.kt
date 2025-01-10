package br.com.astrosoft.promocao.view.produtos.columns

import br.com.astrosoft.framework.view.vaadin.helper.columnGrid
import br.com.astrosoft.produto.model.beans.Produtos
import com.github.mvysny.karibudsl.v10.isExpand
import com.vaadin.flow.component.grid.Grid

object ProdutosColumns {
  fun Grid<Produtos>.produto_codigo() = columnGrid(Produtos::codigo) {
    this.setHeader("Código")
    this.config()
  }

  fun Grid<Produtos>.produto_descricao() = columnGrid(Produtos::descricao) {
    this.setHeader("Descrição")
    this.isResizable = true
  }

  fun Grid<Produtos>.produto_grade() = columnGrid(Produtos::grade) {
    this.setHeader("Grade")
    this.config()
  }

  fun Grid<Produtos>.produto_forn() = columnGrid(Produtos::forn) {
    this.setHeader("Forn")
    this.config()
  }

  fun Grid<Produtos>.produto_abrev() = columnGrid(Produtos::abrev) {
    this.setHeader("Abrev")
    this.config()
  }

  fun Grid<Produtos>.produto_tributacao() = columnGrid(Produtos::tributacao) {
    this.setHeader("Trib")
    this.config()
  }

  fun Grid<Produtos>.produto_tipo() = columnGrid(Produtos::tipo) {
    this.setHeader("Tipo")
    this.config()
  }

  fun Grid<Produtos>.produto_cl() = columnGrid(Produtos::cl) {
    this.setHeader("C Lucro")
    this.config()
  }

  fun Grid<Produtos>.produto_codBar() = columnGrid(Produtos::codBar) {
    this.setHeader("Cód. Barras")
    this.config()
  }

  fun Grid<Produtos>.produto_quantCompra() = columnGrid(Produtos::qttyCompra) {
    this.setHeader("Compra")
    this.config()
  }

  fun Grid<Produtos>.produto_quantVenda() = columnGrid(Produtos::qttyVendas) {
    this.setHeader("Venda")
    this.config()
  }

  fun Grid<Produtos>.produto_DS_TT() = columnGrid(Produtos::DS_TT) {
    this.setHeader("DS")
    this.config()
  }

  fun Grid<Produtos>.produto_MR_TT() = columnGrid(Produtos::MR_TT) {
    this.setHeader("MR")
    this.config()
  }

  fun Grid<Produtos>.produto_MF_TT() = columnGrid(Produtos::MF_TT) {
    this.setHeader("MF")
    this.config()
  }

  fun Grid<Produtos>.produto_MF_App() = columnGrid(Produtos::MF_App) {
    this.setHeader("MF App")
    this.config()
  }

  fun Grid<Produtos>.produto_MF_Dif() = columnGrid(Produtos::MF_Dif) {
    this.setHeader("MF Dif")
    this.config()
  }

  fun Grid<Produtos>.produto_PK_TT() = columnGrid(Produtos::PK_TT) {
    this.setHeader("PK")
    this.config()
  }

  fun Grid<Produtos>.produto_TM_TT() = columnGrid(Produtos::TM_TT) {
    this.setHeader("TM")
    this.config()
  }

  fun Grid<Produtos>.produto_qttyInv() = columnGrid(Produtos::qttyInv) {
    this.setHeader("Inv")
    this.config()
  }

  fun Grid<Produtos>.produto_total() = columnGrid(Produtos::estoque) {
    this.setHeader("Total")
    this.config()
  }

  fun Grid<Produtos>.produto_val() = columnGrid(Produtos::mesesGarantia) {
    this.setHeader("Val")
    this.config()
  }

  fun Grid<Produtos>.produto_Rotulo() = columnGrid(Produtos::rotulo) {
    this.setHeader("Rotulo")
    this.config()
  }

  fun Grid<Produtos>.produto_Trib() = columnGrid(Produtos::trib) {
    this.setHeader("Trib")
    this.config()
  }

  fun Grid<Produtos>.produto_RefForn() = columnGrid(Produtos::refForn) {
    this.setHeader("Ref Forn")
    this.config()
  }

  fun Grid<Produtos>.produto_PesoBruto() = columnGrid(Produtos::pesoBruto) {
    this.setHeader("P. Bruto")
    this.config()
  }

  fun Grid<Produtos>.produto_UGar() = columnGrid(Produtos::uGar) {
    this.setHeader("U. Gar")
    this.config()
  }

  fun Grid<Produtos>.produto_TGar() = columnGrid(Produtos::tGar) {
    this.setHeader("T Gar")
    this.config()
  }

  fun Grid<Produtos>.produto_Emb() = columnGrid(Produtos::emb) {
    this.setHeader("Emb")
    this.config()
  }

  fun Grid<Produtos>.produto_Ncm() = columnGrid(Produtos::ncm) {
    this.setHeader("NCM")
    this.config()
  }

  fun Grid<Produtos>.produto_Site() = columnGrid(Produtos::site) {
    this.setHeader("Site")
    this.config()
  }

  fun Grid<Produtos>.produto_Unidade() = columnGrid(Produtos::unidade) {
    this.setHeader("UN")
    this.config()
  }

  fun Grid<Produtos>.produto_Localizacao() = columnGrid(Produtos::localizacao) {
    this.setHeader("LOC")
    this.config()
  }

  fun Grid<Produtos>.produto_FLinha() = columnGrid(Produtos::foraLinha) {
    this.setHeader("F. Linha")
    this.config()
  }

  private fun Grid.Column<Produtos>.config() {
    this.isExpand = false
    this.isAutoWidth = true
    this.isResizable = true
  }
}