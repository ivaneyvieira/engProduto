package br.com.astrosoft.produto.view.devForReceb

import br.com.astrosoft.framework.model.config.AppConfig
import br.com.astrosoft.framework.view.vaadin.TabPanelGrid
import br.com.astrosoft.framework.view.vaadin.helper.*
import br.com.astrosoft.produto.model.beans.*
import br.com.astrosoft.produto.viewmodel.devForRecebe.ITabNotaFornecedor
import br.com.astrosoft.produto.viewmodel.devForRecebe.ITabNotaNulo
import br.com.astrosoft.produto.viewmodel.devForRecebe.TabNotaFornecedorViewModel
import br.com.astrosoft.produto.viewmodel.devForRecebe.TabNotaNuloViewModel
import com.github.mvysny.karibudsl.v10.button
import com.github.mvysny.karibudsl.v10.onClick
import com.github.mvysny.karibudsl.v10.select
import com.github.mvysny.karibudsl.v10.textField
import com.github.mvysny.kaributools.getColumnBy
import com.vaadin.flow.component.Focusable
import com.vaadin.flow.component.grid.Grid
import com.vaadin.flow.component.grid.GridVariant
import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.component.select.Select
import com.vaadin.flow.component.textfield.TextField
import com.vaadin.flow.data.value.ValueChangeMode

class TabNotaFornecedor(val viewModel: TabNotaFornecedorViewModel) :
  TabPanelGrid<FornecedorClass>(FornecedorClass::class), ITabNotaFornecedor {
  private lateinit var edtPesquisa: TextField
  private var dlgArquivo: DlgArquivoFornecedor? = null
  private var dlgEdita: DlgEditaFornecedor? = null

  override fun HorizontalLayout.toolBarConfig() {
    edtPesquisa = textField("Pesquisa") {
      this.width = "300px"
      this.valueChangeMode = ValueChangeMode.LAZY
      this.valueChangeTimeout = 1500
      addValueChangeListener {
        viewModel.updateView()
      }
    }
  }

  override fun Grid<FornecedorClass>.gridPanel() {
    this.addClassName("styling")
    this.format()

    this.withEditor(
      classBean = FornecedorClass::class,
      openEditor = {
        val edit = getColumnBy(FornecedorClass::termDev) as? Focusable<*>
        edit?.focus()
      },
      closeEditor = {
        viewModel.saveForne(it.bean)
      })

    addColumnButton(
      iconButton = VaadinIcon.EDIT,
      tooltip = "Edita",
      header = "Edita",
      configIcon = { icon, bean ->
        if ((bean.obs ?: "") != "") {
          icon.element.style.set("color", "yellow")
        }
      }) { forn ->
      dlgEdita = DlgEditaFornecedor(viewModel, forn) {
        viewModel.updateView()
      }
      dlgEdita?.open()
    }

    addColumnButton(
      VaadinIcon.FILE_ADD, "Anexa", "Anexa",
      configIcon = { icon, bean ->
        if (bean.countArq?.let { it > 0 } == true) {
          icon.element.style.set("color", "yellow")
        }
      }) { nota ->
      dlgArquivo = DlgArquivoFornecedor(viewModel, nota)
      dlgArquivo?.showDialog {
        viewModel.updateView()
      }
    }

    addColumnButton(VaadinIcon.PHONE_LANDLINE, "Representantes", "Rep") { fornecedor ->
      DlgFornecedor().showDialogRepresentante(fornecedor)
    }

    columnGrid(FornecedorClass::no, header = "Forn", width = "3rem")
    columnGrid(FornecedorClass::descricao, header = "Nome", width = "20rem")
    columnGrid(FornecedorClass::custno, header = "Cliente")
    columnGrid(FornecedorClass::cnpjCpf, header = "CNPJ/CPF")
    columnGrid(FornecedorClass::termDev, header = "Term Dev", width = "10rem").textFieldEditor()
  }

  override fun filtro(): FiltroFornecedor {
    return FiltroFornecedor(
      pesquisa = edtPesquisa.value ?: "",
    )
  }

  override fun updateFornecedor(fornecedore: List<FornecedorClass>) {
    this.updateGrid(fornecedore)
  }

  override fun isAuthorized(): Boolean {
    val username = AppConfig.userLogin() as? UserSaci
    return username?.devFor2NotaFornecedor == true
  }

  override val label: String
    get() = "Forn"

  override fun updateComponent() {
    viewModel.updateView()
  }

  override fun updateArquivos() {
    dlgArquivo?.update()
  }

  override fun arquivosSelecionados(): List<FornecedorArquivo> {
    return dlgArquivo?.produtosSelecionados().orEmpty()
  }
}