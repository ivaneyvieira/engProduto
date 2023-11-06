package br.com.astrosoft.produto.view.produto.columns

import br.com.astrosoft.framework.view.vaadin.helper.columnGrid
import br.com.astrosoft.produto.model.beans.ProdutoRetiraEntrega
import com.vaadin.flow.component.grid.Grid

object ProdutoRetiraEntregaViewColumns {
  fun Grid<ProdutoRetiraEntrega>.retiraEntregaLoja() = columnGrid(ProdutoRetiraEntrega::loja) {
    this.setHeader("Loja")
  }

  fun Grid<ProdutoRetiraEntrega>.retiraEntregaPedido() = columnGrid(ProdutoRetiraEntrega::pedido) {
    this.setHeader("Pedido")
  }

  fun Grid<ProdutoRetiraEntrega>.retiraEntregaData() = columnGrid(ProdutoRetiraEntrega::data) {
    this.setHeader("Data")
  }

  fun Grid<ProdutoRetiraEntrega>.retiraEntregaNota() = columnGrid(ProdutoRetiraEntrega::nota) {
    this.setHeader("Nota")
  }

  fun Grid<ProdutoRetiraEntrega>.retiraEntregaTipo() = columnGrid(ProdutoRetiraEntrega::tipo) {
    this.setHeader("Tipo")
  }

  fun Grid<ProdutoRetiraEntrega>.retiraEntregaCliente() = columnGrid(ProdutoRetiraEntrega::cliente) {
    this.setHeader("Cliente")
  }

  fun Grid<ProdutoRetiraEntrega>.retiraEntregaEmpno() = columnGrid(ProdutoRetiraEntrega::empno) {
    this.setHeader("Vend")
  }

  fun Grid<ProdutoRetiraEntrega>.retiraEntregaCodigo() = columnGrid(ProdutoRetiraEntrega::codigo) {
    this.setHeader("Código")
  }

  fun Grid<ProdutoRetiraEntrega>.retiraEntregaDescricao() = columnGrid(ProdutoRetiraEntrega::descricao) {
    this.setHeader("Descrição")
  }

  fun Grid<ProdutoRetiraEntrega>.retiraEntregaGrade() = columnGrid(ProdutoRetiraEntrega::grade) {
    this.setHeader("Grade")
  }

  fun Grid<ProdutoRetiraEntrega>.retiraEntregaGradeAlternativa() =
          columnGrid(ProdutoRetiraEntrega::gradeAlternativa) {
            this.setHeader("Grade")
          }

  fun Grid<ProdutoRetiraEntrega>.retiraEntregaLocalizacao() = columnGrid(ProdutoRetiraEntrega::localizacao) {
    this.setHeader("Loc")
  }

  fun Grid<ProdutoRetiraEntrega>.retiraEntregaQuant() = columnGrid(ProdutoRetiraEntrega::quant) {
    this.setHeader("Qntd")
  }

  fun Grid<ProdutoRetiraEntrega>.retiraEntregaEstSaci() = columnGrid(ProdutoRetiraEntrega::estSaci) {
    this.setHeader("Est Saci")
  }

  fun Grid<ProdutoRetiraEntrega>.retiraEntregaSaldo() = columnGrid(ProdutoRetiraEntrega::saldo) {
    this.setHeader("saldo")
  }

  fun Grid<ProdutoRetiraEntrega>.retiraEntregaVendno() = columnGrid(ProdutoRetiraEntrega::vendno) {
    this.setHeader("Fornecedor")
  }

  fun Grid<ProdutoRetiraEntrega>.retiraEntregaTypeNo() = columnGrid(ProdutoRetiraEntrega::typeno) {
    this.setHeader("Tipo")
  }

  fun Grid<ProdutoRetiraEntrega>.retiraEntregaClno() = columnGrid(ProdutoRetiraEntrega::clno) {
    this.setHeader("Centro de lucro")
  }
}