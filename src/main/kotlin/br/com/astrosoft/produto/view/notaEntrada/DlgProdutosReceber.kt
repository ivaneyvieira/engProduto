package br.com.astrosoft.produto.view.notaEntrada

import br.com.astrosoft.framework.model.config.AppConfig
import br.com.astrosoft.framework.view.vaadin.SubWindowForm
import br.com.astrosoft.framework.view.vaadin.helper.integerFieldEditor
import br.com.astrosoft.framework.view.vaadin.helper.withEditor
import br.com.astrosoft.produto.model.beans.NotaEntrada
import br.com.astrosoft.produto.model.beans.ProdutoNFE
import br.com.astrosoft.produto.model.beans.UserSaci
import br.com.astrosoft.produto.view.notaEntrada.columns.ProdutoNFEViewColumns.produtoNFEBarcode
import br.com.astrosoft.produto.view.notaEntrada.columns.ProdutoNFEViewColumns.produtoNFECodigo
import br.com.astrosoft.produto.view.notaEntrada.columns.ProdutoNFEViewColumns.produtoNFEDescricao
import br.com.astrosoft.produto.view.notaEntrada.columns.ProdutoNFEViewColumns.produtoNFEGrade
import br.com.astrosoft.produto.view.notaEntrada.columns.ProdutoNFEViewColumns.produtoNFEMesesGarantia
import br.com.astrosoft.produto.view.notaEntrada.columns.ProdutoNFEViewColumns.produtoNFEQuantidade
import br.com.astrosoft.produto.view.notaEntrada.columns.ProdutoNFEViewColumns.produtoNFEQuantidadePacote
import br.com.astrosoft.produto.view.notaEntrada.columns.ProdutoNFEViewColumns.produtoNFEReferencia
import br.com.astrosoft.produto.view.notaEntrada.columns.ProdutoNFEViewColumns.produtoNFEUnidade
import br.com.astrosoft.produto.viewmodel.notaEntrada.TabNotaEntradaReceberViewModel
import com.github.mvysny.karibudsl.v10.*
import com.github.mvysny.kaributools.fetchAll
import com.github.mvysny.kaributools.getColumnBy
import com.github.mvysny.kaributools.selectAll
import com.vaadin.flow.component.Focusable
import com.vaadin.flow.component.HasComponents
import com.vaadin.flow.component.grid.Grid
import com.vaadin.flow.component.grid.GridVariant
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.component.textfield.IntegerField
import com.vaadin.flow.component.textfield.TextField
import com.vaadin.flow.component.textfield.TextFieldVariant
import com.vaadin.flow.data.value.ValueChangeMode

class DlgProdutosReceber(val viewModel: TabNotaEntradaReceberViewModel, val nota: NotaEntrada) {
  private lateinit var edtQuant: IntegerField
  private lateinit var edtCodbar: TextField
  private var form: SubWindowForm? = null
  private val gridDetail = Grid(ProdutoNFE::class.java, false)

  private val userSaci
    get() = AppConfig.userLogin() as? UserSaci

  fun showDialog(onClose: () -> Unit) {
    val status = if (userSaci?.receberProcessar == true) "Pronto para processar" else ""
    val txtStatus = if (status == "") "" else "($status)"
    form = SubWindowForm("Produtos da Nota de Entrada ${nota.nota} loja ${nota.loja} $txtStatus", toolBar = {
      formAdicionarItem()
      botaoProcessar()
      botaoExcluir()
    }, onClose = {
      onClose()
    }) {
      HorizontalLayout().apply {
        setSizeFull()
        createGridProdutos()
      }
    }
    form?.open()
  }

  private fun HasComponents.formAdicionarItem() {
    if (userSaci?.receberAdicionar == true) {
      edtCodbar = textField("Código de barras") {
        this.valueChangeMode = ValueChangeMode.ON_CHANGE
        this.isAutoselect = true
        addValueChangeListener {
          if (it.isFromClient) {
            viewModel.marcaProdutos(edtCodbar.value, edtQuant.value ?: 0)
            edtQuant.value = 0
            edtQuant.focus()
          }
        }
      }
      edtQuant = integerField("Quantidade") {
        this.isAutoselect = true
        this.addThemeVariants(TextFieldVariant.LUMO_ALIGN_RIGHT)
        this.valueChangeMode = ValueChangeMode.ON_CHANGE
        addValueChangeListener {
          if (it.isFromClient) {
            viewModel.marcaProdutos(edtCodbar.value, edtQuant.value)
            edtCodbar.value = ""
            edtQuant.value = 0
            edtCodbar.selectAll()
            edtCodbar.focus()
          }
        }
      }
    }
  }

  private fun HasComponents.botaoProcessar() {
    if (userSaci?.receberProcessar == true) {
      button("Processa") {
        onClick {
          viewModel.processaProdutos()
        }
      }
    } else {
      button("Concluir") {
        onClick {
          viewModel.receberConcluir()
        }
      }
    }
  }

  private fun HorizontalLayout.createGridProdutos() {
    gridDetail.apply {
      setSizeFull()
      addThemeVariants(GridVariant.LUMO_COMPACT)
      isMultiSort = false
      if (userSaci?.receberExcluir == true) {
        setSelectionMode(Grid.SelectionMode.MULTI)
      } else {
        setSelectionMode(Grid.SelectionMode.SINGLE)
      }

      withEditor(ProdutoNFE::class, openEditor = {
        (getColumnBy(ProdutoNFE::quantidade).editorComponent as? Focusable<*>)?.focus()
        userSaci?.receberQuantidade == true
      }, closeEditor = { binder ->
        viewModel.saveProduto(binder.bean)
      })

      produtoNFECodigo()
      produtoNFEReferencia()
      produtoNFEBarcode()
      produtoNFEDescricao()
      produtoNFEGrade()
      produtoNFEUnidade()
      produtoNFEQuantidade().integerFieldEditor().apply {
        this.setClassNameGenerator { produto ->
          if (userSaci?.receberProcessar == true) {
            if (produto.quantidade != produto.qttyRef) "amarelo"
            else null
          } else null
        }
      }
      produtoNFEMesesGarantia()
      produtoNFEQuantidadePacote()
      this.setClassNameGenerator { produto ->
        if (userSaci?.receberProcessar == true) {
          if (produto.qttyRef == null) "amarelo"
          else null
        } else null
      }
    }
    this.addAndExpand(gridDetail)
    update()
  }

  private fun HasComponents.botaoExcluir() {
    if (userSaci?.receberExcluir == true) {
      button("Remover") {
        onClick {
          viewModel.removeProduto()
        }
      }
    }
  }

  fun update() {
    val listProdutos = viewModel.produtos()
    gridDetail.setItems(listProdutos)
  }

  fun produtosNota(): List<ProdutoNFE> {
    return gridDetail.dataProvider.fetchAll()
  }

  fun produtosSelecionados(): List<ProdutoNFE> {
    return gridDetail.selectedItems.toList()
  }
}