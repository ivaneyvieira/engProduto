package br.com.astrosoft.produto.view.expedicao

import br.com.astrosoft.framework.model.config.AppConfig
import br.com.astrosoft.framework.view.vaadin.SubWindowForm
import br.com.astrosoft.framework.view.vaadin.helper.comboFieldEditor
import br.com.astrosoft.framework.view.vaadin.helper.withEditor
import br.com.astrosoft.produto.model.beans.*
import br.com.astrosoft.produto.view.expedicao.columns.ProdutoNFNFSViewColumns.produtoAutorizacaoCD
import br.com.astrosoft.produto.view.expedicao.columns.ProdutoNFNFSViewColumns.produtoAutorizacaoExp
import br.com.astrosoft.produto.view.expedicao.columns.ProdutoNFNFSViewColumns.produtoNFBarcode
import br.com.astrosoft.produto.view.expedicao.columns.ProdutoNFNFSViewColumns.produtoNFCodigo
import br.com.astrosoft.produto.view.expedicao.columns.ProdutoNFNFSViewColumns.produtoNFDescricao
import br.com.astrosoft.produto.view.expedicao.columns.ProdutoNFNFSViewColumns.produtoNFGrade
import br.com.astrosoft.produto.view.expedicao.columns.ProdutoNFNFSViewColumns.produtoNFGradeAlternativa
import br.com.astrosoft.produto.view.expedicao.columns.ProdutoNFNFSViewColumns.produtoNFLocalizacao
import br.com.astrosoft.produto.view.expedicao.columns.ProdutoNFNFSViewColumns.produtoNFPrecoTotal
import br.com.astrosoft.produto.view.expedicao.columns.ProdutoNFNFSViewColumns.produtoNFPrecoUnitario
import br.com.astrosoft.produto.view.expedicao.columns.ProdutoNFNFSViewColumns.produtoNFQuantidade
import br.com.astrosoft.produto.view.expedicao.columns.ProdutoNFNFSViewColumns.produtoNFUsuarioSep
import br.com.astrosoft.produto.viewmodel.expedicao.TabNotaCDViewModel
import com.github.mvysny.karibudsl.v10.button
import com.github.mvysny.karibudsl.v10.onClick
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
    form = SubWindowForm("Produtos da expedicao ${nota.nota} loja: ${nota.loja}$lblCancel", toolBar = {
      button("Volta") {
        val user = AppConfig.userLogin() as? UserSaci
        isVisible = user?.voltarCD == true || user?.admin == true
        icon = VaadinIcon.ARROW_LEFT.create()
        onClick {
          viewModel.marcaExp()
        }
      }
      button("Entregue") {
        isEnabled = nota.cancelada == "N"
        val user = AppConfig.userLogin() as? UserSaci
        isVisible = user?.voltarCD == true || user?.admin == true
        icon = VaadinIcon.ARROW_RIGHT.create()
        onClick {
          viewModel.marcaEnt()
        }
      }
      textField("Código de barras") {
        this.valueChangeMode = ValueChangeMode.LAZY
        this.valueChangeTimeout = 1500
        addValueChangeListener {
          if (it.isFromClient) {
            viewModel.selecionaProduto(it.value)

            this@textField.value = ""
            this@textField.focus()
            gridDetail.dataProvider.refreshAll()
          }
        }
      }
      button("Enviar") {
        icon = VaadinIcon.ARROW_RIGHT.create()
        onClick {
          viewModel.marcaEntProdutos()
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
      selectionMode = Grid.SelectionMode.MULTI
      this.addClassName("styling")

      withEditor(ProdutoNFS::class, openEditor = {
        (getColumnBy(ProdutoNFS::gradeAlternativa).editorComponent as? Focusable<*>)?.focus()
        when {
          it.bean?.clno?.startsWith("01") == false -> {
            show("O produto não está no grupo de piso")
          }

          it.bean.tipoNota != 4                    -> {
            show("Não é uma expedicao de edtrega futura")
          }

          nota.cancelada == "S"                    -> {
            show("A expedicao está cancelada")
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
      produtoAutorizacaoExp()
      produtoAutorizacaoCD()
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
      produtoNFPrecoUnitario()
      produtoNFPrecoTotal()
      produtoNFUsuarioSep()

      this.setPartNameGenerator {
        if (it?.selecionado == true) "amarelo" else null
      }
    }
    this.addAndExpand(gridDetail)
    update()
  }

  fun itensSelecionados(): List<ProdutoNFS> {
    return gridDetail.selectedItems.toList()
  }

  fun update() {
    val listProdutos = nota.produtos(EMarcaNota.CD, todosLocais = false)
    val user = AppConfig.userLogin() as? UserSaci
    if(nota.isRessuprimento()){
      listProdutos.forEach {prd ->
        prd.selecionado = (prd.marca ?: 0) >= 1
      }
    }
    gridDetail.setItems(listProdutos)
  }

  fun produtosCodigoBarras(codigoBarra: String): List<ProdutoNFS> {
    return gridDetail.dataProvider.fetchAll().filter {
      val barcodes = it.barcodes
      codigoBarra.trim() in barcodes
    }
  }

  fun itensMarcados(): List<ProdutoNFS> {
    return gridDetail.dataProvider.fetchAll().filter { it.selecionado }
  }

  fun itensNaoMarcados(): List<ProdutoNFS> {
    return gridDetail.dataProvider.fetchAll().filter { !it.selecionado }
  }
}