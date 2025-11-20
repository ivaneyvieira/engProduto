package br.com.astrosoft.produto.view.devCliente

import br.com.astrosoft.framework.view.vaadin.SubWindowForm
import br.com.astrosoft.framework.view.vaadin.helper.*
import br.com.astrosoft.produto.model.beans.*
import br.com.astrosoft.produto.view.expedicao.columns.ProdutoNFNFSViewColumns.produtoAutorizacaoExp
import br.com.astrosoft.produto.view.expedicao.columns.ProdutoNFNFSViewColumns.produtoNFBarcode
import br.com.astrosoft.produto.view.expedicao.columns.ProdutoNFNFSViewColumns.produtoNFCodigo
import br.com.astrosoft.produto.view.expedicao.columns.ProdutoNFNFSViewColumns.produtoNFDescricao
import br.com.astrosoft.produto.view.expedicao.columns.ProdutoNFNFSViewColumns.produtoNFDev
import br.com.astrosoft.produto.view.expedicao.columns.ProdutoNFNFSViewColumns.produtoNFGrade
import br.com.astrosoft.produto.view.expedicao.columns.ProdutoNFNFSViewColumns.produtoNFLocalizacao
import br.com.astrosoft.produto.view.expedicao.columns.ProdutoNFNFSViewColumns.produtoNFPrecoTotal
import br.com.astrosoft.produto.view.expedicao.columns.ProdutoNFNFSViewColumns.produtoNFPrecoUnitario
import br.com.astrosoft.produto.view.expedicao.columns.ProdutoNFNFSViewColumns.produtoNFQuantidade
import br.com.astrosoft.produto.view.expedicao.columns.ProdutoNFNFSViewColumns.produtoNFQuantidadeDevolucao
import br.com.astrosoft.produto.view.expedicao.columns.ProdutoNFNFSViewColumns.produtoNFTemProduto
import br.com.astrosoft.produto.viewmodel.devCliente.TabDevAutorizaViewModel
import com.github.mvysny.karibudsl.v10.*
import com.github.mvysny.karibudsl.v23.multiSelectComboBox
import com.vaadin.flow.component.checkbox.CheckboxGroup
import com.vaadin.flow.component.checkbox.CheckboxGroupVariant
import com.vaadin.flow.component.combobox.MultiSelectComboBox
import com.vaadin.flow.component.grid.Grid
import com.vaadin.flow.component.grid.GridVariant
import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.component.select.Select
import com.vaadin.flow.component.textfield.IntegerField
import com.vaadin.flow.component.textfield.TextFieldVariant

class DlgProdutosVenda(val viewModel: TabDevAutorizaViewModel, val nota: NotaVenda) {
  private var form: SubWindowForm? = null
  private val gridDetail = Grid(ProdutoNFS::class.java, false)

  private var edtTipo: Select<ESolicitacaoTroca>? = null
  private var edtProduto: Select<EProdutoTroca>? = null
  private var edtNotaEntRet: IntegerField? = null
  private var edtMotivo: MultiSelectComboBox<EMotivoTroca>? = null

  fun showDialog(onClose: () -> Unit) {
    val readOnly = (nota.userSolicitacao ?: 0) != 0
    form = SubWindowForm(
      "Produtos da expedicao ${nota.nota} loja: ${nota.loja}",
      toolBar = {
        edtTipo = select("Tipo") {
          this.isReadOnly = readOnly
          this.setItems(ESolicitacaoTroca.entries)
          this.setItemLabelGenerator { item -> item.descricao }
          this.width = "10rem"
          this.value = nota.solicitacaoTrocaEnnum ?: ESolicitacaoTroca.Troca
        }

        edtProduto = select("Produto") {
          this.isReadOnly = readOnly
          this.setItems(EProdutoTroca.entries)
          this.setItemLabelGenerator { item -> item.descricao }
          this.width = "10rem"
          this.value = nota.produtoTrocaEnnum ?: EProdutoTroca.Com

          addValueChangeListener {
            updateSelectionProdutos(it.value)
          }
        }

        if (nota.tipoNf == "ENTRE FUT") {
          edtNotaEntRet = integerField {
            this.isReadOnly = readOnly
            this.value = nota.nfEntRet
            this.width = "6rem"
            this.isAutoselect = true
            this.addThemeVariants(TextFieldVariant.LUMO_ALIGN_RIGHT)
          }
        }

        edtMotivo = multiSelectComboBox("Motivo:") {
          this.isReadOnly = readOnly
          this.setItems(EMotivoTroca.entries)
          this.setItemLabelGenerator { item -> item.descricao }
          this.value = nota.setMotivoTroca
          this.width = "12rem"
        }

        button("Processar") {
          this.icon = VaadinIcon.COG.create()
          this.isEnabled = !readOnly

          this.onClick {
            nota.solicitacaoTrocaEnnum = edtTipo?.value
            nota.produtoTrocaEnnum = edtProduto?.value
            nota.nfEntRet = edtNotaEntRet?.value ?: 0
            nota.setMotivoTroca = edtMotivo?.value ?: emptySet()
            viewModel.processaSolicitacao(nota)
          }
        }
      },
      onClose = {
        onClose()
      }) {
      HorizontalLayout().apply {
        setSizeFull()
        createGridProdutos()
      }
    }
    form?.open()
  }

  private fun updateSelectionProdutos(value: EProdutoTroca?) {
    /*when (value) {

      EProdutoTroca.Com   -> {
        gridDetail.selectionMode = Grid.SelectionMode.MULTI
        val allItem = gridDetail.list()
        gridDetail.asMultiSelect().select(allItem)
      }

      EProdutoTroca.Sem   -> {
        gridDetail.selectionMode = Grid.SelectionMode.NONE
      }

      EProdutoTroca.Misto -> {
        gridDetail.selectionMode = Grid.SelectionMode.MULTI
        gridDetail.asMultiSelect().deselectAll()
      }

      else                -> {}
    }*/
  }

  private fun HorizontalLayout.createGridProdutos() {
    gridDetail.apply {
      this.addClassName("styling")
      setSizeFull()
      addThemeVariants(GridVariant.LUMO_COMPACT)
      isMultiSort = false
      selectionMode = Grid.SelectionMode.NONE

      this.withEditor(
        classBean = ProdutoNFS::class,
        isBuffered = false,
        openEditor = {
          this.focusEditor(ProdutoNFS::dev)
        },
        closeEditor = {
          viewModel.updateProduto(it.bean)
          abreProximo(it.bean)
        },
        saveEditor = {
          viewModel.updateProduto(it.bean)
          abreProximo(it.bean)
        }
      )

      produtoNFDev().checkBoxEditor()
      produtoNFTemProduto().checkBoxEditor()
      produtoNFQuantidadeDevolucao().integerFieldEditor()
      produtoNFCodigo()
      produtoNFBarcode()
      produtoAutorizacaoExp()
      produtoNFDescricao()
      produtoNFGrade()
      produtoNFLocalizacao()
      produtoNFQuantidade()
      produtoNFPrecoUnitario()
      produtoNFPrecoTotal()

      this.setPartNameGenerator {
        val marca = it.marca
        val marcaImpressao = it.marcaImpressao ?: 0
        when {
          marcaImpressao > 0          -> "azul"
          marca == EMarcaNota.CD.num  -> "amarelo"
          marca == EMarcaNota.ENT.num -> "amarelo"
          else                        -> null
        }
      }
    }
    this.addAndExpand(gridDetail)

    update()
    updateSelectionProdutos(edtProduto?.value)
  }

  private fun abreProximo(bean: ProdutoNFS) {
    val items = gridDetail.list()
    val index = items.indexOf(bean)
    if (index >= 0) {
      val nextIndex = index + 1
      if (nextIndex < items.size) {
        val nextBean = items[nextIndex]
        //gridDetail.select(nextBean)
        gridDetail.editor.editItem(nextBean)
      }
    }
    updateSelectionProdutos(edtProduto?.value)
  }

  fun itensSelecionados(): List<ProdutoNFS> {
    return gridDetail.selectedItems.toList()
  }

  fun update() {
    val listProdutos = nota.produtos()
    gridDetail.setItems(listProdutos)
  }
}