package br.com.astrosoft.produto.view.produto.columns

import br.com.astrosoft.framework.view.addColumnInt
import br.com.astrosoft.framework.view.addColumnLocalDate
import br.com.astrosoft.framework.view.addColumnString
import br.com.astrosoft.produto.model.beans.ProdutoRetiraEntrega
import com.vaadin.flow.component.grid.Grid

object ProdutoRetiraEntregaViewColumns {
  fun Grid<ProdutoRetiraEntrega>.retiraEntregaLoja() = addColumnInt(ProdutoRetiraEntrega::loja) {
    this.setHeader("Loja")
  }

  fun Grid<ProdutoRetiraEntrega>.retiraEntregaPedido() = addColumnInt(ProdutoRetiraEntrega::pedido) {
    this.setHeader("Pedido")
  }

  fun Grid<ProdutoRetiraEntrega>.retiraEntregaData() = addColumnLocalDate(ProdutoRetiraEntrega::data) {
    this.setHeader("Data")
  }

  fun Grid<ProdutoRetiraEntrega>.retiraEntregaNota() = addColumnString(ProdutoRetiraEntrega::nota) {
    this.setHeader("Nota")
  }


  fun Grid<ProdutoRetiraEntrega>.retiraEntregaTipo() = addColumnString(ProdutoRetiraEntrega::tipo) {
    this.setHeader("Tipo")
  }

  fun Grid<ProdutoRetiraEntrega>.retiraEntregaCliente() = addColumnInt(ProdutoRetiraEntrega::cliente) {
    this.setHeader("Cliente")
  }

  fun Grid<ProdutoRetiraEntrega>.retiraEntregaEmpno() = addColumnInt(ProdutoRetiraEntrega::empno) {
    this.setHeader("Vend")
  }

  fun Grid<ProdutoRetiraEntrega>.retiraEntregaCodigo() = addColumnString(ProdutoRetiraEntrega::codigo) {
    this.setHeader("Código")
  }

  fun Grid<ProdutoRetiraEntrega>.retiraEntregaDescricao() = addColumnString(ProdutoRetiraEntrega::descricao) {
    this.setHeader("Descrição")
  }

  fun Grid<ProdutoRetiraEntrega>.retiraEntregaGrade() = addColumnString(ProdutoRetiraEntrega::grade) {
    this.setHeader("Grade")
  }

  fun Grid<ProdutoRetiraEntrega>.retiraEntregaLocalizacao() = addColumnString(ProdutoRetiraEntrega::localizacao) {
    this.setHeader("Loc")
  }

  fun Grid<ProdutoRetiraEntrega>.retiraEntregaQuant() = addColumnInt(ProdutoRetiraEntrega::quant) {
    this.setHeader("Qntd")
  }

  fun Grid<ProdutoRetiraEntrega>.retiraEntregaEstSaci() = addColumnInt(ProdutoRetiraEntrega::estSaci) {
    this.setHeader("Est Saci")
  }

  fun Grid<ProdutoRetiraEntrega>.retiraEntregaSaldo() = addColumnInt(ProdutoRetiraEntrega::saldo) {
    this.setHeader("saldo")
  }

  fun Grid<ProdutoRetiraEntrega>.retiraEntregaVendno() = addColumnInt(ProdutoRetiraEntrega::vendno) {
    this.setHeader("Fornecedor")
  }

  fun Grid<ProdutoRetiraEntrega>.retiraEntregaTypeNo() = addColumnInt(ProdutoRetiraEntrega::typeno) {
    this.setHeader("Tipo")
  }

  fun Grid<ProdutoRetiraEntrega>.retiraEntregaClno() = addColumnString(ProdutoRetiraEntrega::clno) {
    this.setHeader("Centro de lucro")
  }
}