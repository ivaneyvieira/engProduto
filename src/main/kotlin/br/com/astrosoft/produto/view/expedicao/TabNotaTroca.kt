package br.com.astrosoft.produto.view.expedicao

import br.com.astrosoft.framework.model.config.AppConfig
import br.com.astrosoft.framework.view.vaadin.TabPanelGrid
import br.com.astrosoft.framework.view.vaadin.helper.*
import br.com.astrosoft.produto.model.beans.*
import br.com.astrosoft.produto.view.expedicao.columns.NotaColumns.colunaHora
import br.com.astrosoft.produto.view.expedicao.columns.NotaColumns.colunaNFCliente
import br.com.astrosoft.produto.view.expedicao.columns.NotaColumns.colunaNFData
import br.com.astrosoft.produto.view.expedicao.columns.NotaColumns.colunaNFEntregaRetira
import br.com.astrosoft.produto.view.expedicao.columns.NotaColumns.colunaNFLoja
import br.com.astrosoft.produto.view.expedicao.columns.NotaColumns.colunaNFNota
import br.com.astrosoft.produto.view.expedicao.columns.NotaColumns.colunaNFSituacao
import br.com.astrosoft.produto.view.expedicao.columns.NotaColumns.colunaNFTipo
import br.com.astrosoft.produto.view.expedicao.columns.NotaColumns.colunaNFValor
import br.com.astrosoft.produto.view.expedicao.columns.NotaColumns.colunaNFVendedor
import br.com.astrosoft.produto.view.expedicao.columns.NotaColumns.colunaNomeCliente
import br.com.astrosoft.produto.view.expedicao.columns.NotaColumns.colunaRota
import br.com.astrosoft.produto.viewmodel.expedicao.ITabNotaTroca
import br.com.astrosoft.produto.viewmodel.expedicao.TabNotaTrocaViewModel
import com.github.mvysny.karibudsl.v10.datePicker
import com.github.mvysny.karibudsl.v10.select
import com.github.mvysny.karibudsl.v10.textField
import com.vaadin.flow.component.datepicker.DatePicker
import com.vaadin.flow.component.grid.Grid
import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.component.select.Select
import com.vaadin.flow.component.textfield.TextField
import com.vaadin.flow.data.value.ValueChangeMode
import java.time.LocalDate

class TabNotaTroca(val viewModel: TabNotaTrocaViewModel) : TabPanelGrid<NotaSaida>(NotaSaida::class), ITabNotaTroca {
  private var colRota: Grid.Column<NotaSaida>? = null
  private var dlgProduto: DlgProdutosTroca? = null
  private lateinit var cmbLoja: Select<Loja>
  private lateinit var cmbNota: Select<ETipoNotaFiscal>
  private lateinit var edtDataInicial: DatePicker
  private lateinit var edtDataFinal: DatePicker
  private lateinit var edtPesquisa: TextField

  fun init() {
    val user = AppConfig.userLogin() as? UserSaci
    val loja = user?.lojaNota ?: 0
    val lojaSelecionada = viewModel.findAllLojas().firstOrNull { it.no == loja }
    cmbLoja.isReadOnly = (user?.admin == false) && (lojaSelecionada?.no != 0)
    cmbLoja.setItems(viewModel.findAllLojas() + listOf(Loja.lojaZero))
    cmbLoja.value = lojaSelecionada ?: Loja.lojaZero
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
    init()
    cmbNota = select("Nota") {
      val user = AppConfig.userLogin() as? UserSaci
      val tiposNota = user?.tipoNotaExpedicao.let { tipo ->
        if (tipo == null) ETipoNotaFiscal.entries
        else {
          if (tipo.contains(ETipoNotaFiscal.TODOS)) ETipoNotaFiscal.entries
          else tipo.ifEmpty { ETipoNotaFiscal.entries }
        }
      }.filter { tipo ->
        tipo == ETipoNotaFiscal.ENTRE_FUT ||
        tipo == ETipoNotaFiscal.SIMP_REME_L ||
        tipo == ETipoNotaFiscal.TODOS
      }
      setItems(tiposNota)
      value = tiposNota.firstOrNull()

      this.setItemLabelGenerator {
        it.descricao
      }
      addValueChangeListener {
        if (it.isFromClient)
          viewModel.updateView()
        colRota?.isVisible = it.value == ETipoNotaFiscal.ENTRE_FUT
      }
    }
    edtPesquisa = textField("Pesquisa") {
      this.valueChangeMode = ValueChangeMode.LAZY
      this.valueChangeTimeout = 1500
      addValueChangeListener {
        viewModel.updateView()
      }
    }
    edtDataInicial = datePicker("Data Inicial") {
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
  }

  override fun Grid<NotaSaida>.gridPanel() {
    this.addClassName("styling")
    this.format()

    colunaNFLoja()

    columnGrid(NotaSaida::usuarioSingExp, "Autoriza")

    addColumnButton(VaadinIcon.FILE_TABLE, "Produtos", "Produtos") { nota ->
      dlgProduto = DlgProdutosTroca(viewModel, nota)
      dlgProduto?.showDialog {
        viewModel.updateView()
      }
    }
    colunaNFNota()
    colunaNFData()
    colunaHora()
    colRota = colunaRota()
    colunaNFCliente()
    colunaNomeCliente()
    colunaNFVendedor()
    //colunaNomeVendedor()
    colunaNFValor()
    colunaNFTipo()
    colunaNFEntregaRetira()
    colunaNFSituacao()

    this.setPartNameGenerator {
      val countEnt = it.countEnt ?: 0
      val countImp = it.countImp ?: 0
      val cancelada = it.cancelada ?: "N"
      when {
        cancelada == "S" -> "vermelho"

        countImp > 0     -> "azul"

        countEnt > 0     -> "amarelo"

        else             -> null
      }
    }
  }

  override fun filtro(marca: EMarcaNota): FiltroNota {
    return FiltroNota(
      marca = marca,
      tipoNota = cmbNota.value ?: ETipoNotaFiscal.TODOS,
      loja = cmbLoja.value?.no ?: 0,
      dataInicial = edtDataInicial.value,
      dataFinal = edtDataFinal.value,
      pesquisa = edtPesquisa.value ?: "",
    )
  }

  override fun updateNotas(notas: List<NotaSaida>) {
    updateGrid(notas)
  }

  override fun findNota(): NotaSaida? {
    return dlgProduto?.nota
  }

  override fun updateProdutos() {
    dlgProduto?.update()
  }

  override fun produtosSelecionados(): List<ProdutoNFS> {
    return dlgProduto?.itensSelecionados().orEmpty()
  }

  override fun isAuthorized(): Boolean {
    val username = AppConfig.userLogin() as? UserSaci
    return username?.notaTroca == true
  }

  override val label: String
    get() = "Troca"

  override fun updateComponent() {
    viewModel.updateView()
  }

  override fun printerUser(): List<String> {
    val user = AppConfig.userLogin() as? UserSaci
    return user?.impressoraNotaTermica?.toList().orEmpty()
  }

  override fun formAutoriza(lista: List<ProdutoNFS>, marca: (userno: Int) -> Unit) {
    val form = FormAutoriza()
    DialogHelper.showForm(caption = "Entregue", form = form) {
      val user = viewModel.autorizaProduto(lista, form.login, form.senha)
      if (user != null) {
        marca(user.no)
      }
    }
  }
}