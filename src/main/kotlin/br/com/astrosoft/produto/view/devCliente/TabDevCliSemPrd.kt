package br.com.astrosoft.produto.view.devCliente

import br.com.astrosoft.framework.model.config.AppConfig
import br.com.astrosoft.framework.view.vaadin.TabPanelGrid
import br.com.astrosoft.framework.view.vaadin.helper.*
import br.com.astrosoft.framework.view.vaadin.right
import br.com.astrosoft.produto.model.beans.*
import br.com.astrosoft.produto.viewmodel.devCliente.ITabDevCliSemPrd
import br.com.astrosoft.produto.viewmodel.devCliente.TabDevCliSemPrdViewModel
import com.github.mvysny.karibudsl.v10.*
import com.vaadin.flow.component.Focusable
import com.vaadin.flow.component.datepicker.DatePicker
import com.vaadin.flow.component.formlayout.FormLayout
import com.vaadin.flow.component.grid.Grid
import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.component.select.Select
import com.vaadin.flow.component.textfield.TextField
import com.vaadin.flow.data.value.ValueChangeMode
import java.time.LocalDate

class TabDevCliSemPrd(val viewModel: TabDevCliSemPrdViewModel) :
  TabPanelGrid<NotaAutorizacao>(NotaAutorizacao::class),
  ITabDevCliSemPrd {
  private lateinit var cmbLoja: Select<Loja>
  private lateinit var edtPesquisa: TextField
  private lateinit var edtDataInicial: DatePicker
  private lateinit var edtDataFinal: DatePicker

  init {
    cmbLoja.setItems(viewModel.findAllLojas() + listOf(Loja.lojaZero))
    val user = AppConfig.userLogin() as? UserSaci
    cmbLoja.isReadOnly = user?.lojaVale != 0
    cmbLoja.value = viewModel.findLoja(user?.lojaVale ?: 0) ?: Loja.lojaZero
  }

  override fun printerUser(): List<String> {
    val username = AppConfig.userLogin() as? UserSaci
    return listOfNotNull(username?.impressoraDev)
  }

  override fun HorizontalLayout.toolBarConfig() {
    cmbLoja = select("Loja") {
      this.setItemLabelGenerator { item ->
        item.descricao
      }
      addValueChangeListener {
        if (it.isFromClient)
          viewModel.updateView()
      }
    }
    edtPesquisa = textField("Pesquisa") {
      this.width = "300px"
      valueChangeMode = ValueChangeMode.TIMEOUT
      addValueChangeListener {
        viewModel.updateView()
      }
    }
    edtDataInicial = datePicker("Data inicial") {
      this.localePtBr()
      this.value = LocalDate.now()
      addValueChangeListener {
        viewModel.updateView()
      }
    }
    edtDataFinal = datePicker("Data Final") {
      this.localePtBr()
      this.value = LocalDate.now()
      addValueChangeListener {
        viewModel.updateView()
      }
    }

    button("Adicionar Nota") {
      val user = AppConfig.userLogin() as? UserSaci
      this.isVisible = user?.devCliSemPrdInsert == true
      this.icon = VaadinIcon.PLUS_CIRCLE_O.create()
      this.addClickListener {
        val cmbLoja = select<Loja>("Loja") {
          this.setItemLabelGenerator { item ->
            item.descricao
          }
          this.setItems(viewModel.findAllLojas())
          this.value = cmbLoja.value
        }
        val edtNota = textField("Nota Fiscal") {
          this.valueChangeMode = ValueChangeMode.TIMEOUT
        }
        val form = FormLayout().apply {
          add(cmbLoja)
          add(edtNota)
        }
        DialogHelper.showForm("Nota Fiscal", form) {
          val chave = NotaAutorizacaoChave(
            loja = cmbLoja.value?.no ?: 0,
            notaFiscal = edtNota.value ?: ""
          )
          viewModel.addNota(chave)
        }
      }
    }

    button("Excluir Nota") {
      val user = AppConfig.userLogin() as? UserSaci
      this.isVisible = user?.devCliSemPrdDelete == true
      this.icon = VaadinIcon.MINUS_CIRCLE_O.create()
      this.addClickListener {
        val notas = itensSelecionados()
        viewModel.deleteNota(notas)
      }
    }

    button("Impressão") {
      icon = VaadinIcon.PRINT.create()
      onLeftClick {
        viewModel.imprimeProdutos()
      }
    }
  }

  override fun Grid<NotaAutorizacao>.gridPanel() {
    this.addClassName("styling")
    this.setSelectionMode(Grid.SelectionMode.MULTI)

    this.withEditor(NotaAutorizacao::class,
      openEditor = {
        val colunas = this.columns
        val componente = colunas.firstOrNull {
          it.editorComponent != null && (it.editorComponent is Focusable<*>)
        }
        val focusable = componente?.editorComponent as? Focusable<*>
        focusable?.focus()
      },
      closeEditor = {
        viewModel.updateAutorizacao(it.bean)
      })

    columnGrid(NotaAutorizacao::loja, header = "Loja")
    addColumnButton(VaadinIcon.PRINT, "Imprimir vale troca", "Imprimir") { nota ->
      viewModel.imprimeValeTroca(nota)
    }
    columnGrid(NotaAutorizacao::nfVenda, header = "NF Venda").right()
    columnGrid(NotaAutorizacao::dataEmissao, header = "Data")
    columnGrid(NotaAutorizacao::codCliente, header = "Cód Cli")
    columnGrid(NotaAutorizacao::nomeCliente, header = "Nome do Cliente").expand()
    columnGrid(NotaAutorizacao::valorVenda, header = "Valor Venda")
    columnGrid(NotaAutorizacao::tipoDev, header = "Tipo Dev", width = "8em").comboFieldEditor {
      it.setItems("Est Boleto", "Est Cartão", "Est Deposito", "Muda Cliente", "Muda Nota", "Troca")
    }
    addColumnButton(VaadinIcon.SIGN_IN, "Autoriza", "Autoriza") { nota ->
      viewModel.formAutoriza(nota)
    }
    columnGrid(NotaAutorizacao::autorizacao, header = "Autorização")
    columnGrid(NotaAutorizacao::ni, header = "NI", width = "7em")
    columnGrid(NotaAutorizacao::nfDev, header = "NF Dev", width = "8em")
    columnGrid(NotaAutorizacao::data, header = "Data")
    columnGrid(NotaAutorizacao::valorDev, header = "Valor Dev")
    columnGrid(NotaAutorizacao::loginDev, header = "Usuário", width = "10em")
    columnGrid(NotaAutorizacao::observacao, header = "Observação", width = "17em").textFieldEditor()
  }

  override fun formAutoriza(nota: NotaAutorizacao) {
    val form = FormAutorizaNota()
    DialogHelper.showForm(caption = "Autoriza pedido", form = form) {
      viewModel.autorizaNota(nota, form.login, form.senha)
    }
  }

  override fun itensNotasSelecionados(): List<NotaAutorizacao> {
    return this.itensSelecionados()
  }

  override fun filtro(): FiltroNotaAutorizacao {
    return FiltroNotaAutorizacao(
      loja = cmbLoja.value?.no ?: 0,
      pesquisa = edtPesquisa.value ?: "",
      dataInicial = edtDataInicial.value,
      dataFinal = edtDataFinal.value,
    )
  }

  override fun updateNotas(notas: List<NotaAutorizacao>) {
    updateGrid(notas)
  }

  override fun isAuthorized(): Boolean {
    val username = AppConfig.userLogin() as? UserSaci
    return username?.devCliSemPrd == true
  }

  override val label: String
    get() = "Dev Sem Prd"

  override fun updateComponent() {
    viewModel.updateView()
  }
}