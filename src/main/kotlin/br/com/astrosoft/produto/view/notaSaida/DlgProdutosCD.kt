package br.com.astrosoft.produto.view.notaSaida

import br.com.astrosoft.framework.model.config.AppConfig
import br.com.astrosoft.framework.view.vaadin.SubWindowForm
import br.com.astrosoft.framework.view.vaadin.helper.comboFieldEditor
import br.com.astrosoft.framework.view.vaadin.helper.withEditor
import br.com.astrosoft.produto.model.beans.*
import br.com.astrosoft.produto.view.notaSaida.columns.ProdutoNFNFSViewColumns.produtoNFBarcode
import br.com.astrosoft.produto.view.notaSaida.columns.ProdutoNFNFSViewColumns.produtoNFCodigo
import br.com.astrosoft.produto.view.notaSaida.columns.ProdutoNFNFSViewColumns.produtoNFDescricao
import br.com.astrosoft.produto.view.notaSaida.columns.ProdutoNFNFSViewColumns.produtoNFGrade
import br.com.astrosoft.produto.view.notaSaida.columns.ProdutoNFNFSViewColumns.produtoNFGradeAlternativa
import br.com.astrosoft.produto.view.notaSaida.columns.ProdutoNFNFSViewColumns.produtoNFLocalizacao
import br.com.astrosoft.produto.view.notaSaida.columns.ProdutoNFNFSViewColumns.produtoNFPrecoTotal
import br.com.astrosoft.produto.view.notaSaida.columns.ProdutoNFNFSViewColumns.produtoNFPrecoUnitario
import br.com.astrosoft.produto.view.notaSaida.columns.ProdutoNFNFSViewColumns.produtoNFQuantidade
import br.com.astrosoft.produto.view.notaSaida.columns.ProdutoNFNFSViewColumns.produtoNFQuantidadeSaldo
import br.com.astrosoft.produto.viewmodel.notaSaida.TabNotaCDViewModel
import com.github.mvysny.karibudsl.v10.button
import com.github.mvysny.karibudsl.v10.onLeftClick
import com.github.mvysny.karibudsl.v10.textField
import com.github.mvysny.kaributools.fetchAll
import com.github.mvysny.kaributools.getColumnBy
import com.vaadin.flow.component.Component
import com.vaadin.flow.component.Focusable
import com.vaadin.flow.component.grid.Grid
import com.vaadin.flow.component.grid.GridVariant
import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.component.notification.Notification.show
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.component.select.Select
import com.vaadin.flow.data.value.ValueChangeMode

class DlgProdutosCD(val viewModel: TabNotaCDViewModel, val nota: NotaSaida) {
  private var form: SubWindowForm? = null
  private val gridDetail = Grid(ProdutoNFS::class.java, false)
  val lblCancel = if (nota.cancelada == "S") " (Cancelada)" else ""
  fun showDialog(onClose: () -> Unit) {
    form = SubWindowForm("Produtos da notaSaida ${nota.nota} loja: ${nota.loja}$lblCancel", toolBar = {
      button("Volta") {
        val user = AppConfig.userLogin() as? UserSaci
        isVisible = user?.voltarCD == true || user?.admin == true
        icon = VaadinIcon.ARROW_LEFT.create()
        onLeftClick {
          viewModel.marcaExp()
        }
      }
      button("Entregue") {
        isEnabled = nota.cancelada == "N"
        val user = AppConfig.userLogin() as? UserSaci
        isVisible = user?.voltarCD == true || user?.admin == true
        icon = VaadinIcon.ARROW_RIGHT.create()
        onLeftClick {
          viewModel.marcaEnt()
        }
      }
      textField("Código de barras") {
        this.valueChangeMode = ValueChangeMode.ON_CHANGE
        addValueChangeListener {
          if (it.isFromClient) {
            viewModel.marcaEntProdutos(it.value)
            this@textField.value = ""
            this@textField.focus()
          }
        }
      }
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

  private fun HorizontalLayout.createGridProdutos() {
    gridDetail.apply {
      setSizeFull()
      addThemeVariants(GridVariant.LUMO_COMPACT)
      isMultiSort = false
      setSelectionMode(Grid.SelectionMode.MULTI)

      withEditor(ProdutoNFS::class, openEditor = {
        (getColumnBy(ProdutoNFS::gradeAlternativa).editorComponent as? Focusable<*>)?.focus()
        when {
          it.bean?.clno?.startsWith("01") == false -> {
            show("O produto não está no grupo de piso")
          }

          it.bean.tipoNota != 4                    -> {
            show("Não é uma notaSaida de edtrega futura")
          }

          nota.cancelada == "S"                    -> {
            show("A notaSaida está cancelada")
          }
        }
      }, closeEditor = { binder ->
        this.dataProvider.refreshItem(binder.bean)
      })

      addItemDoubleClickListener { e ->
        editor.editItem(e.item)
        val editorComponent: Component = e.column.editorComponent
        if (editorComponent is Focusable<*>) {
          (editorComponent as Focusable<*>).focus()
        }
      }

      produtoNFCodigo()
      produtoNFBarcode()
      produtoNFDescricao()
      produtoNFGrade()
      produtoNFGradeAlternativa().comboFieldEditor { combo: Select<String> ->
        combo.setItems("")
        editor.addOpenListener { e ->
          if (e.source.isOpen) {
            val produto = e.item
            val list = mutableListOf<PrdGrade>()

            viewModel.findGrade(produto) { prds ->
              list.addAll(prds)
            }
            combo.style.set("--vaadin-combo-box-overlay-width", "300px")
            combo.setItems(list.map { it.grade })
            combo.setItemLabelGenerator { grade ->
              val saldo = list.firstOrNull { it.grade == grade }?.saldo ?: 0
              if (grade == null) ""
              else "$grade Saldo: $saldo"
            }
          }
        }
      }
      produtoNFLocalizacao()
      produtoNFQuantidade()
      produtoNFQuantidadeSaldo()
      produtoNFPrecoUnitario()
      produtoNFPrecoTotal()
    }
    this.addAndExpand(gridDetail)
    update()
  }

  fun itensSelecionados(): List<ProdutoNFS> {
    return gridDetail.selectedItems.toList()
  }

  fun update() {
    val listProdutos = nota.produtos(EMarcaNota.CD)
    gridDetail.setItems(listProdutos)
  }

  fun produtosCodigoBarras(codigoBarra: String): ProdutoNFS? {
    return gridDetail.dataProvider.fetchAll().firstOrNull {
      val barcodes = it.barcodes
      codigoBarra.trim() in barcodes
    }
  }
}