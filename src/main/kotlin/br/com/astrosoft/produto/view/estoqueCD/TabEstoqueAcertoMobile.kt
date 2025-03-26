package br.com.astrosoft.produto.view.estoqueCD

import br.com.astrosoft.framework.model.IUser
import br.com.astrosoft.framework.model.config.AppConfig
import br.com.astrosoft.framework.util.format
import br.com.astrosoft.framework.view.vaadin.TabPanelGridMobile
import br.com.astrosoft.framework.view.vaadin.helper.DialogHelper
import br.com.astrosoft.framework.view.vaadin.helper.localePtBr
import br.com.astrosoft.produto.model.beans.*
import br.com.astrosoft.produto.viewmodel.estoqueCD.ITabEstoqueAcertoMobile
import br.com.astrosoft.produto.viewmodel.estoqueCD.TabEstoqueAcertoMobileViewModel
import com.github.mvysny.karibudsl.v10.*
import com.vaadin.flow.component.HasComponents
import com.vaadin.flow.component.button.ButtonVariant
import com.vaadin.flow.component.datepicker.DatePicker
import com.vaadin.flow.component.datepicker.DatePickerVariant
import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import com.vaadin.flow.component.select.Select
import com.vaadin.flow.component.select.SelectVariant
import com.vaadin.flow.component.textfield.IntegerField
import com.vaadin.flow.component.textfield.TextFieldVariant
import com.vaadin.flow.data.value.ValueChangeMode
import com.vaadin.flow.theme.lumo.LumoUtility
import java.time.LocalDate

class TabEstoqueAcertoMobile(val viewModel: TabEstoqueAcertoMobileViewModel) :
  TabPanelGridMobile<EstoqueAcerto>(EstoqueAcerto::class), ITabEstoqueAcertoMobile {
  private var dlgEstoque: DlgEstoqueAcertoMobile? = null
  private lateinit var edtNumero: IntegerField
  private lateinit var edtDateIncial: DatePicker
  private lateinit var edtDateFinal: DatePicker
  private lateinit var cmbLoja: Select<Loja>

  fun init() {
    val user = AppConfig.userLogin() as? UserSaci
    val itens = if (user?.admin == true) {
      listOf(Loja.lojaZero) + viewModel.findAllLojas()
    } else {
      viewModel.findAllLojas().filter { it.no == (user?.lojaConferencia ?: 0) }
    }
    cmbLoja.setItems(itens)
    cmbLoja.value = itens.firstOrNull()
  }

  override fun HorizontalLayout.toolBarConfig() {
    cmbLoja = select("Loja") {
      this.addClassName("mobile")
      this.addThemeVariants(SelectVariant.LUMO_SMALL)
      this.width = "10em"
      this.setItemLabelGenerator { item ->
        item.descricao
      }
      addValueChangeListener {
        if (it.isFromClient)
          viewModel.updateView()
      }
    }

    init()

    edtNumero = integerField("Número") {
      this.addClassName("mobile")
      this.addThemeVariants(TextFieldVariant.LUMO_SMALL)
      this.width = "10em"
      this.valueChangeMode = ValueChangeMode.LAZY
      this.valueChangeTimeout = 1500
      addValueChangeListener {
        viewModel.updateView()
      }
    }

    edtDateIncial = datePicker("Data Inicial") {
      this.addClassName("mobile")
      this.addThemeVariants(DatePickerVariant.LUMO_SMALL)
      this.width = "8.5em"
      this.value = LocalDate.now()
      this.localePtBr()
      addValueChangeListener {
        viewModel.updateView()
      }
    }

    edtDateFinal = datePicker("Data Final") {
      this.addClassName("mobile")
      this.addThemeVariants(DatePickerVariant.LUMO_SMALL)
      this.width = "8.5em"
      this.value = LocalDate.now()
      this.localePtBr()
      addValueChangeListener {
        viewModel.updateView()
      }
    }

    button("Cancelar") {
      this.addClassName("mobile")
      this.addThemeVariants(ButtonVariant.LUMO_SMALL)
      this.icon = VaadinIcon.CLOSE.create()
      onClick {
        viewModel.cancelarAcerto()
      }
    }
  }

  override fun VerticalLayout.renderCard(item: EstoqueAcerto) {
    horizontalLayout {
      isWrap = true
      fieldPanel(item.lojaSigla, header = "Loja", width = 5.00)
      fieldPanel(item.numero.toString(), header = "Acerto", isRight = true, width = 5.00)
      fieldPanel(item.data.format(), header = "Data", width = 8.0)
      fieldPanel(item.hora.format(), header = "Hora", width = 4.0)
      fieldPanel(item.login, header = "Usuário", width = 5.0)
      fieldPanel(item.transacaoEnt, header = "Trans Ent", width = 6.0)
      fieldPanel(item.transacaoSai, header = "Trans Sai", width = 6.0)
      fieldPanel(item.gravadoStr, header = "Gravado", width = 6.0)
      fieldPanel(item.gravadoLoginStr, header = "Login", width = 6.0)
      fieldPanel(item.processadoStr, header = "Processado", width = 6.0)
    }
    button("Pedido") {
      addThemeVariants(ButtonVariant.LUMO_SMALL)
      this.icon = VaadinIcon.FILE_TABLE.create()
      this.onClick {
        dlgEstoque = DlgEstoqueAcertoMobile(viewModel, item)
        dlgEstoque?.showDialog {
          viewModel.updateView()
        }
      }
    }
  }

  override fun filtro(): FiltroAcerto {
    return FiltroAcerto(
      numLoja = cmbLoja.value?.no ?: 0,
      numero = edtNumero.value ?: 0,
      dataInicial = edtDateIncial.value ?: LocalDate.now(),
      dataFinal = edtDateFinal.value ?: LocalDate.now()
    )
  }

  override fun updateProduto(produtos: List<EstoqueAcerto>) {
    updateGrid(produtos)
    dlgEstoque?.updateAcerto(produtos)
  }

  override fun filtroVazio(): FiltroProdutoEstoque {
    val user = AppConfig.userLogin() as? UserSaci
    val listaUser = user?.listaEstoque.orEmpty().toList().ifEmpty {
      listOf("TODOS")
    }

    return FiltroProdutoEstoque(
      loja = cmbLoja.value?.no ?: 0,
      pesquisa = "",
      codigo = 0,
      grade = "",
      caracter = ECaracter.TODOS,
      localizacao = "",
      fornecedor = "",
      centroLucro = 0,
      estoque = EEstoque.TODOS,
      saldo = 0,
      inativo = EInativo.TODOS,
      uso = EUso.TODOS,
      listaUser = listaUser,
    )
  }

  override fun autorizaAcerto(block: (IUser) -> Unit) {
    val form = FormAutorizaAcerto()
    DialogHelper.showForm(caption = "Autoriza gravação do acerto", form = form) {
      val user = AppConfig.findUser(form.login, form.senha)
      if (user != null) {
        block(user)
      } else {
        DialogHelper.showWarning("Usuário ou senha inválidos")
      }
    }
  }

  override fun isAuthorized(): Boolean {
    val username = AppConfig.userLogin() as? UserSaci
    return username?.estoqueAcertoMobile == true
  }

  override val label: String
    get() = "Coletor"

  override fun updateComponent() {
    viewModel.updateView()
  }

  override fun printerUser(): List<String> {
    val user = AppConfig.userLogin() as? UserSaci
    return user?.impressoraEstoque.orEmpty().toList()
  }
}

/*
fun HasComponents.fieldPanel(value: String?, header: String, isRight: Boolean = false, width: Double = 0.00) {
  this.textField(header) {
    this.addClassName("mobile")
    this.isReadOnly = false
    this.value = value ?: ""
    this.addThemeVariants(TextFieldVariant.LUMO_SMALL)
    if (isRight) {
      this.addThemeVariants(TextFieldVariant.LUMO_ALIGN_RIGHT)
    }
    if (width > 0) {
      this.width = "${width}em"
    }
  }
}*/

fun HasComponents.fieldPanel(value: String?, header: String, isRight: Boolean = false, width: Double = 0.00) {
  this.verticalLayout {
    this.isSpacing = false
    this.isMargin = false
    this.isPadding = false
    this.isSpacing = false
    this.width = if (width > 0) "${width}em" else null
    this.p(header) {
      this.addClassNames(
        LumoUtility.FontSize.XSMALL,
        LumoUtility.FontWeight.BOLD,
        LumoUtility.Margin.NONE,
        LumoUtility.Padding.NONE
      )
    }
    val text = value.let {
      if (it.isNullOrBlank()) {
        "\u00A0"
      } else {
        it
      }
    }
    this.p(text) {
      this.addClassNames(
        LumoUtility.FontSize.MEDIUM,
        LumoUtility.FontWeight.NORMAL,
        LumoUtility.Border.ALL,
        LumoUtility.Padding.Left.XSMALL,
        LumoUtility.Padding.Right.XSMALL,
        LumoUtility.BorderRadius.SMALL,
        LumoUtility.Margin.NONE
      )
      this.width = "100%"
      if (isRight) {
        this.addClassNames(LumoUtility.TextAlignment.RIGHT)
      }
    }
  }
}