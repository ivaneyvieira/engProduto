package br.com.astrosoft.produto.view.produto.columns

import br.com.astrosoft.framework.view.addColumnInt
import br.com.astrosoft.framework.view.addColumnLocalDate
import br.com.astrosoft.framework.view.addColumnString
import br.com.astrosoft.produto.model.beans.ProdutoRetiraEntrega
import com.vaadin.flow.component.grid.Grid

object ProdutoRetiraEntregaViewColumns {
  fun Grid<ProdutoRetiraEntrega>.produtoLoja() = addColumnInt(ProdutoRetiraEntrega::loja) {
    this.setHeader("Loja")
  }

  fun Grid<ProdutoRetiraEntrega>.produtoPedido() = addColumnInt(ProdutoRetiraEntrega::pedido) {
    this.setHeader("Pedido")
  }

  fun Grid<ProdutoRetiraEntrega>.produtoData() = addColumnLocalDate(ProdutoRetiraEntrega::data) {
    this.setHeader("Data")
  }

  fun Grid<ProdutoRetiraEntrega>.produtoNota() = addColumnString(ProdutoRetiraEntrega::nota) {
    this.setHeader("Nota")
  }


  fun Grid<ProdutoRetiraEntrega>.produtoTipo() = addColumnString(ProdutoRetiraEntrega::tipo) {
    this.setHeader("Tipo")
  }

  fun Grid<ProdutoRetiraEntrega>.produtoCliente() = addColumnInt(ProdutoRetiraEntrega::cliente) {
    this.setHeader("Cliente")
  }

  fun Grid<ProdutoRetiraEntrega>.produtoEmpno() = addColumnInt(ProdutoRetiraEntrega::empno) {
    this.setHeader("Vend")
  }

  fun Grid<ProdutoRetiraEntrega>.produtoCodigo() = addColumnString(ProdutoRetiraEntrega::codigo) {
    this.setHeader("Código")
  }

  fun Grid<ProdutoRetiraEntrega>.produtoDescricao() = addColumnString(ProdutoRetiraEntrega::descricao) {
    this.setHeader("Descrição")
  }

  fun Grid<ProdutoRetiraEntrega>.produtoGrade() = addColumnString(ProdutoRetiraEntrega::grade) {
    this.setHeader("Grade")
  }

  fun Grid<ProdutoRetiraEntrega>.produtoQuant() = addColumnInt(ProdutoRetiraEntrega::quant) {
    this.setHeader("Qntd")
  }

  fun Grid<ProdutoRetiraEntrega>.produtoEstSaci() = addColumnInt(ProdutoRetiraEntrega::estSaci) {
    this.setHeader("Est Saci")
  }

  fun Grid<ProdutoRetiraEntrega>.produtoSaldo() = addColumnInt(ProdutoRetiraEntrega::saldo) {
    this.setHeader("saldo")
  }

  fun Grid<ProdutoRetiraEntrega>.produtoVendno() = addColumnInt(ProdutoRetiraEntrega::vendno) {
    this.setHeader("Fornecedor")
  }

  fun Grid<ProdutoRetiraEntrega>.produtoTypeNo() = addColumnInt(ProdutoRetiraEntrega::typeno) {
    this.setHeader("Tipo")
  }

  fun Grid<ProdutoRetiraEntrega>.produtoClno() = addColumnString(ProdutoRetiraEntrega::clno) {
    this.setHeader("Centro de lucro")
  }
}