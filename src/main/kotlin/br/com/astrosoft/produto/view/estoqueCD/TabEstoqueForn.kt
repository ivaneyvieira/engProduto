package br.com.astrosoft.produto.view.estoqueCD

import br.com.astrosoft.framework.model.config.AppConfig
import br.com.astrosoft.framework.view.vaadin.TabPanelGrid
import br.com.astrosoft.framework.view.vaadin.helper.*
import br.com.astrosoft.produto.model.beans.FiltroFornecedorLoja
import br.com.astrosoft.produto.model.beans.FornecedorLoja
import br.com.astrosoft.produto.model.beans.LoginBean
import br.com.astrosoft.produto.model.beans.UserSaci
import br.com.astrosoft.produto.viewmodel.estoqueCD.ITabEstoqueForn
import br.com.astrosoft.produto.viewmodel.estoqueCD.TabEstoqueFornViewModel
import com.github.mvysny.karibudsl.v10.datePicker
import com.github.mvysny.karibudsl.v10.textField
import com.vaadin.flow.component.datepicker.DatePicker
import com.vaadin.flow.component.grid.Grid
import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.component.textfield.TextField
import com.vaadin.flow.data.value.ValueChangeMode

class TabEstoqueForn(val viewModel: TabEstoqueFornViewModel) :
  TabPanelGrid<FornecedorLoja>(FornecedorLoja::class), ITabEstoqueForn {
  private lateinit var edtPesquisa: TextField
  private lateinit var edtDateInicial: DatePicker
  private lateinit var edtDateFinal: DatePicker

  override fun HorizontalLayout.toolBarConfig() {
    edtPesquisa = textField("Pesquisa") {
      this.width = "300px"
      this.valueChangeMode = ValueChangeMode.LAZY
      this.valueChangeTimeout = 1500
      addValueChangeListener {
        viewModel.updateView()
      }
    }

    edtDateInicial = datePicker("Data Inicial") {
      this.localePtBr()
      this.isClearButtonVisible = true
      addValueChangeListener {
        viewModel.updateView()
      }
    }

    edtDateFinal = datePicker("Data Final") {
      this.localePtBr()
      this.isClearButtonVisible = true
      addValueChangeListener {
        viewModel.updateView()
      }
    }
  }

  override fun Grid<FornecedorLoja>.gridPanel() {
    selectionMode = Grid.SelectionMode.SINGLE
    val user = AppConfig.userLogin() as? UserSaci
    val lojaConferencia = if (user?.admin == true) {
      0
    } else {
      user?.lojaConferencia ?: 0
    }

    this.withEditor(
      classBean = FornecedorLoja::class,
      openEditor = {
//        val edit = this.columns.firstOrNull { it is Focusable<*> } as? Focusable<*>
        //      edit?.focus()
      },
      closeEditor = {
        viewModel.saveForn(it.bean)
      })
    this.columnGroup("Fornecedor") {
      this.addColumnSeq("Seq")
      this.columnGrid(property = FornecedorLoja::vendno, header = "Codigo")
      this.columnGrid(property = FornecedorLoja::abrev, header = "Nome", width = "200px")
    }
    if (lojaConferencia == 2 || lojaConferencia == 0) {
      this.columnGroup("DS") {
        this.columnGrid(property = FornecedorLoja::dataDS, header = "Data").dateFieldEditor()
        this.addColumnButton(VaadinIcon.SIGN_IN, "Assina", "Assina") { forn ->
          viewModel.formAutoriza(forn, 2)
        }.width = "2rem"
        this.columnGrid(property = FornecedorLoja::loginDS, header = "Login", width = "5rem")
      }
    }

    if (lojaConferencia == 3 || lojaConferencia == 0) {
      this.columnGroup("MR") {
        this.columnGrid(property = FornecedorLoja::dataMR, header = "Data").dateFieldEditor()
        this.addColumnButton(VaadinIcon.SIGN_IN, "Assina", "Assina") { forn ->
          viewModel.formAutoriza(forn, 3)
        }
        this.columnGrid(property = FornecedorLoja::loginMR, header = "Login", width = "5rem")
      }
    }

    if (lojaConferencia == 4 || lojaConferencia == 0) {
      this.columnGroup("MF") {
        this.columnGrid(property = FornecedorLoja::dataMF, header = "Data").dateFieldEditor()
        this.addColumnButton(VaadinIcon.SIGN_IN, "Assina", "Assina") { forn ->
          viewModel.formAutoriza(forn, 4)
        }
        this.columnGrid(property = FornecedorLoja::loginMF, header = "Login", width = "5rem")
      }
    }

    if (lojaConferencia == 5 || lojaConferencia == 0) {
      this.columnGroup("PK") {
        this.columnGrid(property = FornecedorLoja::dataPK, header = "Data").dateFieldEditor()
        this.addColumnButton(VaadinIcon.SIGN_IN, "Assina", "Assina") { forn ->
          viewModel.formAutoriza(forn, 5)
        }.width = "2rem"
        this.columnGrid(property = FornecedorLoja::loginPK, header = "Login", width = "5rem")
      }
    }

    if (lojaConferencia == 8 || lojaConferencia == 0) {
      this.columnGroup("TM") {
        this.columnGrid(property = FornecedorLoja::dataTM, header = "Data").dateFieldEditor()
        this.addColumnButton(VaadinIcon.SIGN_IN, "Assina", "Assina") { forn ->
          viewModel.formAutoriza(forn, 8)
        }.width = "2rem"
        this.columnGrid(property = FornecedorLoja::loginTM, header = "Login", width = "5rem")
      }
    }
  }

  override fun filtro(): FiltroFornecedorLoja {
    return FiltroFornecedorLoja(
      pesquisa = edtPesquisa.value ?: "",
      dataInicial = edtDateInicial.value,
      dataFinal = edtDateFinal.value
    )
  }

  override fun formAutoriza(block: LoginBean.() -> Unit) {
    val form = FormLoginForn()
    DialogHelper.showForm(caption = "Assinatura", form = form) {
      val login = LoginBean(login = form.login, senha = form.senha)
      login.block()
    }
  }

  override fun isAuthorized(): Boolean {
    val username = AppConfig.userLogin() as? UserSaci
    return username?.estoqueForn == true
  }

  override val label: String
    get() = "Fornecedor"

  override fun updateComponent() {
    viewModel.updateView()
  }
}