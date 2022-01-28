package br.com.astrosoft.produto.view.ressuprimento.columns

import br.com.astrosoft.framework.view.addColumnDouble
import br.com.astrosoft.framework.view.addColumnInt
import br.com.astrosoft.framework.view.addColumnString
import br.com.astrosoft.produto.model.beans.ProdutoRessuprimento
import com.vaadin.flow.component.grid.Grid

object ProdutoRessuViewColumns {
  fun Grid<ProdutoRessuprimento>.produtoRessuprimentoUsuarioNameCD() =
          addColumnString(ProdutoRessuprimento::usuarioNameCD) {
            this.setHeader("Usuário CD")
          }

  fun Grid<ProdutoRessuprimento>.produtoRessuprimentoCodigo() = addColumnString(ProdutoRessuprimento::codigo) {
    this.setHeader("Código")
  }

  fun Grid<ProdutoRessuprimento>.produtoRessuprimentoGrade() = addColumnString(ProdutoRessuprimento::grade) {
    this.setHeader("Grade")
  }

  fun Grid<ProdutoRessuprimento>.produtoRessuprimentoLocalizacao() =
          addColumnString(ProdutoRessuprimento::localizacao) {
            this.setHeader("Loc")
          }

  fun Grid<ProdutoRessuprimento>.produtoRessuprimentoTipo() = addColumnString(ProdutoRessuprimento::statusStr) {
    this.setHeader("Status")
  }

  fun Grid<ProdutoRessuprimento>.produtoRessuprimentoBarcode() = addColumnString(ProdutoRessuprimento::barcode) {
    this.setHeader("Código de Barras")
  }

  fun Grid<ProdutoRessuprimento>.produtoRessuprimentoDescricao() = addColumnString(ProdutoRessuprimento::descricao) {
    this.setHeader("Descrição")
  }

  fun Grid<ProdutoRessuprimento>.produtoRessuprimentoVendno() = addColumnInt(ProdutoRessuprimento::vendno) {
    this.setHeader("Fornecedor")
  }

  fun Grid<ProdutoRessuprimento>.produtoRessuprimentoFornecedor() = addColumnString(ProdutoRessuprimento::fornecedor) {
    this.setHeader("Nome Fornecedor")
  }

  fun Grid<ProdutoRessuprimento>.produtoRessuprimentoTypeNo() = addColumnInt(ProdutoRessuprimento::typeno) {
    this.setHeader("Tipo")
  }

  fun Grid<ProdutoRessuprimento>.produtoRessuprimentoTypeName() = addColumnString(ProdutoRessuprimento::typeName) {
    this.setHeader("Nome Tipo")
  }

  fun Grid<ProdutoRessuprimento>.produtoRessuprimentoClno() = addColumnString(ProdutoRessuprimento::clno) {
    this.setHeader("Centro de lucro")
  }

  fun Grid<ProdutoRessuprimento>.produtoRessuprimentoClName() = addColumnString(ProdutoRessuprimento::clname) {
    this.setHeader("Nome Centro de Lucro")
  }

  fun Grid<ProdutoRessuprimento>.produtoRessuprimentoAltura() = addColumnInt(ProdutoRessuprimento::altura) {
    this.setHeader("Altura")
  }

  fun Grid<ProdutoRessuprimento>.produtoRessuprimentoComprimento() = addColumnInt(ProdutoRessuprimento::comprimento) {
    this.setHeader("Comprimento")
  }

  fun Grid<ProdutoRessuprimento>.produtoRessuprimentoLargura() = addColumnInt(ProdutoRessuprimento::largura) {
    this.setHeader("Largura")
  }

  fun Grid<ProdutoRessuprimento>.produtoRessuprimentoPrecoCheio() = addColumnDouble(ProdutoRessuprimento::precoCheio) {
    this.setHeader("Preço a vista")
  }

  fun Grid<ProdutoRessuprimento>.produtoRessuprimentoQuantidade() = addColumnInt(ProdutoRessuprimento::quantidade) {
    this.setHeader("Quant")
  }

  fun Grid<ProdutoRessuprimento>.produtoRessuprimentoEstoque() = addColumnInt(ProdutoRessuprimento::estoque) {
    this.setHeader("Estoque")
  }

  fun Grid<ProdutoRessuprimento>.produtoRessuprimentoPrecoUnitario() = addColumnDouble(ProdutoRessuprimento::preco) {
    this.setHeader("Preço")
  }

  fun Grid<ProdutoRessuprimento>.produtoRessuprimentoPrecoTotal() = addColumnDouble(ProdutoRessuprimento::total) {
    this.setHeader("Total")
  }
}