package br.com.astrosoft.produto.view.produto.columns

import br.com.astrosoft.framework.view.addColumnDouble
import br.com.astrosoft.framework.view.addColumnInt
import br.com.astrosoft.framework.view.addColumnString
import br.com.astrosoft.produto.model.beans.Produto
import com.vaadin.flow.component.grid.Grid

object ProdutoViewColumns {
  fun Grid<Produto>.produtoLoja() = addColumnInt(Produto::loja) {
    this.setHeader("Loja")
  }

  fun Grid<Produto>.produtoPedido() = addColumnInt(Produto::pedido) {
    this.setHeader("Pedido")
  }

  fun Grid<Produto>.produtoNota() = addColumnString(Produto::nota) {
    this.setHeader("Nota")
  }


  fun Grid<Produto>.produtoTipo() = addColumnString(Produto::tipo) {
    this.setHeader("Tipo")
  }

  fun Grid<Produto>.produtoCliente() = addColumnInt(Produto::cliente) {
    this.setHeader("Cliente")
  }

  fun Grid<Produto>.produtoEmpno() = addColumnInt(Produto::empno) {
    this.setHeader("Vend")
  }

  fun Grid<Produto>.produtoCodigo() = addColumnString(Produto::codigo) {
    this.setHeader("Código")
  }

  fun Grid<Produto>.produtoDescricao() = addColumnString(Produto::descricao) {
    this.setHeader("Descrição")
  }

  fun Grid<Produto>.produtoGrade() = addColumnString(Produto::grade) {
    this.setHeader("Grade")
  }

  fun Grid<Produto>.produtoQuant() = addColumnInt(Produto::quant) {
    this.setHeader("Qntd")
  }

  fun Grid<Produto>.produtoEstSaci() = addColumnInt(Produto::estSaci) {
    this.setHeader("Est Saci")
  }

  fun Grid<Produto>.produtoSaldo() = addColumnInt(Produto::saldo) {
    this.setHeader("saldo")
  }

  fun Grid<Produto>.produtoVendno() = addColumnInt(Produto::vendno) {
    this.setHeader("Fornecedor")
  }

  fun Grid<Produto>.produtoTypeNo() = addColumnInt(Produto::typeno) {
    this.setHeader("Tipo")
  }

  fun Grid<Produto>.produtoClno() = addColumnString(Produto::clno) {
    this.setHeader("Centro de lucro")
  }
}