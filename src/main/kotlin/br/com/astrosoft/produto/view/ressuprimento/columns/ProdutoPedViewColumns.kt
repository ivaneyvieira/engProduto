package br.com.astrosoft.produto.view.ressuprimento.columns

import br.com.astrosoft.framework.view.vaadin.helper.columnGrid
import br.com.astrosoft.produto.model.beans.ProdutoRessuprimento
import com.vaadin.flow.component.grid.Grid

object ProdutoRessuViewColumns {
  fun Grid<ProdutoRessuprimento>.produtoRessuprimentoCodigo() =
      columnGrid(ProdutoRessuprimento::codigo) {
        this.setHeader("Código")
      }

  fun Grid<ProdutoRessuprimento>.produtoRessuprimentoCodigoCorrecao() =
      columnGrid(ProdutoRessuprimento::codigoCorrecao) {
        this.setHeader("Código")
      }

  fun Grid<ProdutoRessuprimento>.produtoRessuprimentoGradeCorrecao() =
      columnGrid(ProdutoRessuprimento::gradeCorrecao) {
        this.setHeader("Grade")
      }

  fun Grid<ProdutoRessuprimento>.produtoRessuprimentoDescricaoCorrecao() =
      columnGrid(ProdutoRessuprimento::descricaoCorrecao) {
        this.setHeader("Descrição")
      }

  fun Grid<ProdutoRessuprimento>.produtoRessuprimentoGrade() =
      columnGrid(ProdutoRessuprimento::grade) {
        this.setHeader("Grade")
      }

  fun Grid<ProdutoRessuprimento>.produtoRessuprimentoLocalizacao() =
      columnGrid(ProdutoRessuprimento::localizacao) {
        this.setHeader("Loc")
      }

  fun Grid<ProdutoRessuprimento>.produtoRessuprimentoDataNF() =
      columnGrid(ProdutoRessuprimento::dataNota) {
        this.setHeader("Data")
      }

  fun Grid<ProdutoRessuprimento>.produtoRessuprimentoNumeroNF() =
      columnGrid(ProdutoRessuprimento::numeroNota) {
        this.setHeader("NF")
      }

  fun Grid<ProdutoRessuprimento>.produtoRessuprimentoTipo() = columnGrid(ProdutoRessuprimento::statusStr) {
    this.setHeader("Status")
  }

  fun Grid<ProdutoRessuprimento>.produtoRessuprimentoBarcode() = columnGrid(ProdutoRessuprimento::barcode) {
    this.setHeader("Código de Barras")
  }

  fun Grid<ProdutoRessuprimento>.produtoRessuprimentoDescricao() = columnGrid(ProdutoRessuprimento::descricao) {
    this.setHeader("Descrição")
  }

  fun Grid<ProdutoRessuprimento>.produtoRessuprimentoVendno() = columnGrid(ProdutoRessuprimento::vendno) {
    this.setHeader("Fornecedor")
  }

  fun Grid<ProdutoRessuprimento>.produtoRessuprimentoFornecedor() = columnGrid(ProdutoRessuprimento::fornecedor) {
    this.setHeader("Nome Fornecedor")
  }

  fun Grid<ProdutoRessuprimento>.produtoRessuprimentoTypeNo() = columnGrid(ProdutoRessuprimento::typeno) {
    this.setHeader("Tipo")
  }

  fun Grid<ProdutoRessuprimento>.produtoRessuprimentoTypeName() = columnGrid(ProdutoRessuprimento::typeName) {
    this.setHeader("Nome Tipo")
  }

  fun Grid<ProdutoRessuprimento>.produtoRessuprimentoClno() = columnGrid(ProdutoRessuprimento::clno) {
    this.setHeader("Centro de lucro")
  }

  fun Grid<ProdutoRessuprimento>.produtoRessuprimentoClName() = columnGrid(ProdutoRessuprimento::clname) {
    this.setHeader("Nome Centro de Lucro")
  }

  fun Grid<ProdutoRessuprimento>.produtoRessuprimentoAltura() = columnGrid(ProdutoRessuprimento::altura) {
    this.setHeader("Altura")
  }

  fun Grid<ProdutoRessuprimento>.produtoRessuprimentoComprimento() = columnGrid(ProdutoRessuprimento::comprimento) {
    this.setHeader("Comprimento")
  }

  fun Grid<ProdutoRessuprimento>.produtoRessuprimentoLargura() = columnGrid(ProdutoRessuprimento::largura) {
    this.setHeader("Largura")
  }

  fun Grid<ProdutoRessuprimento>.produtoRessuprimentoPrecoCheio() = columnGrid(ProdutoRessuprimento::precoCheio) {
    this.setHeader("Preço a vista")
  }

  fun Grid<ProdutoRessuprimento>.produtoRessuprimentoQtPedido() = columnGrid(ProdutoRessuprimento::qtPedido) {
    this.setHeader("Quant")
  }

  fun Grid<ProdutoRessuprimento>.produtoRessuprimentoQtNf() = columnGrid(ProdutoRessuprimento::qtQuantNF) {
    this.setHeader("Qnt NF")
  }

  fun Grid<ProdutoRessuprimento>.produtoRessuprimentoQtRecebido() = columnGrid(ProdutoRessuprimento::qtRecebido) {
    this.setHeader("Recebido")
  }

  fun Grid<ProdutoRessuprimento>.produtoRessuprimentoQtAvaria() = columnGrid(ProdutoRessuprimento::qtAvaria) {
    this.setHeader("Avaria")
  }

  fun Grid<ProdutoRessuprimento>.produtoRessuprimentoQtEntregue() = columnGrid(ProdutoRessuprimento::qtEntregue) {
    this.setHeader("Entregue")
  }

  fun Grid<ProdutoRessuprimento>.produtoRessuprimentoEstoque() = columnGrid(ProdutoRessuprimento::estoque) {
    this.setHeader("Estoque")
  }

  fun Grid<ProdutoRessuprimento>.produtoRessuprimentoPrecoUnitario() = columnGrid(ProdutoRessuprimento::preco) {
    this.setHeader("Preço")
  }

  fun Grid<ProdutoRessuprimento>.produtoRessuprimentoPrecoTotal() = columnGrid(ProdutoRessuprimento::total) {
    this.setHeader("Total")
  }
}