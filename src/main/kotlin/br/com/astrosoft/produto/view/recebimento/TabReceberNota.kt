package br.com.astrosoft.produto.view.recebimento

import br.com.astrosoft.framework.model.config.AppConfig
import br.com.astrosoft.framework.view.vaadin.TabPanelGrid
import br.com.astrosoft.framework.view.vaadin.helper.*
import br.com.astrosoft.produto.model.beans.*
import br.com.astrosoft.produto.viewmodel.recebimento.ITabReceberNota
import br.com.astrosoft.produto.viewmodel.recebimento.TabReceberNotaViewModel
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

class TabReceberNota(val viewModel: TabReceberNotaViewModel) :
  TabPanelGrid<NotaRecebimento>(NotaRecebimento::class), ITabReceberNota {
  private var dlgProduto: DlgProdutosReceberNota? = null
  private lateinit var cmbLoja: Select<Loja>
  private lateinit var edtPesquisa: TextField
  private lateinit var edtDataInicial: DatePicker
  private lateinit var edtDataFinal: DatePicker
  private lateinit var edtTipoNota: Select<EListaContas>

  fun init() {
    val allLojas = viewModel.findAllLojas() + listOf(Loja.lojaZero)
    cmbLoja.setItems(allLojas)
    val user = AppConfig.userLogin() as? UserSaci
    val lojaRec = user?.lojaRec ?: 0
    cmbLoja.isReadOnly = lojaRec != 0
    cmbLoja.value = if (lojaRec == 0) {
      allLojas.firstOrNull { it.no == 4 }
    } else {
      allLojas.firstOrNull { it.no == lojaRec } ?: Loja.lojaZero
    }
  }
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
  edtTipoNota = select("Tipo Nota") {
    this.setItems(EListaContas.entries)
    this.value = EListaContas.RECEBIMENTO
    this.setItemLabelGenerator {
      it.descricao
    }
    addValueChangeListener {
      viewModel.updateView()
    }
  }
  edtPesquisa = textField("Pesquisa") {
    this.width = "300px"
    this.valueChangeMode = ValueChangeMode.LAZY
    this.valueChangeTimeout = 1500
    addValueChangeListener {
      viewModel.updateView()
    }
  }
  edtDataInicial = datePicker("Data Inicial") {
    value = LocalDate.now()
    this.localePtBr()
    addValueChangeListener {
      viewModel.updateView()
    }
  }
  edtDataFinal = datePicker("Data Final") {
    value = LocalDate.now()
    this.localePtBr()
    addValueChangeListener {
      viewModel.updateView()
    }
  }
}

override fun Grid<NotaRecebimento>.gridPanel() {
  this.addClassName("styling")
  this.format()

  columnGrid(NotaRecebimento::loja, header = "Loja")
  columnGrid(NotaRecebimento::usuarioRecebe, "Recebe")
  columnGrid(NotaRecebimento::tipoNota, "Tipo Nota")

  addColumnButton(VaadinIcon.FILE_TABLE, "Produtos", "Produtos") { nota ->
    dlgProduto = DlgProdutosReceberNota(viewModel, nota)
    dlgProduto?.showDialog {
      viewModel.updateView()
    }
  }

  columnGrid(NotaRecebimento::data, header = "Data")
  columnGrid(NotaRecebimento::emissao, header = "Emissão")
  columnGrid(NotaRecebimento::ni, header = "NI")
  columnGrid(NotaRecebimento::nfEntrada, header = "NF Entrada")
  columnGrid(NotaRecebimento::vendnoProduto, header = "For Cad")
  columnGrid(NotaRecebimento::vendno, header = "For NF")
  columnGrid(NotaRecebimento::fornecedor, header = "Nome Fornecedor")
  columnGrid(NotaRecebimento::valorNF, header = "Valor NF")
  columnGrid(NotaRecebimento::observacaoNota, header = "Observação")
  columnGrid(NotaRecebimento::pedComp, header = "Ped Comp")
  columnGrid(NotaRecebimento::transp, header = "Transp")
  columnGrid(NotaRecebimento::cte, header = "CTe")
  columnGrid(NotaRecebimento::volume, header = "Volume")
  columnGrid(NotaRecebimento::peso, header = "Peso")
}

override fun filtro(): FiltroNotaRecebimentoProduto {
  val usr = AppConfig.userLogin() as? UserSaci
  return FiltroNotaRecebimentoProduto(
    loja = cmbLoja.value?.no ?: 0,
    pesquisa = edtPesquisa.value ?: "",
    marca = EMarcaRecebimento.RECEBER,
    dataFinal = edtDataFinal.value,
    dataInicial = edtDataInicial.value,
    localizacao = usr?.localizacaoRec.orEmpty().toList(),
    tipoNota = edtTipoNota.value ?: EListaContas.TODOS,
  )
}

override fun updateNota(notas: List<NotaRecebimento>) {
  this.updateGrid(notas)
  dlgProduto?.nota?.let { notaDlg ->
    notas.firstOrNull { it.ni == notaDlg.ni }?.let { notaAtualizada ->
      dlgProduto?.update(notaAtualizada)
    }
  }
}

override fun updateProduto(): NotaRecebimento? {
  return dlgProduto?.updateProduto()
}

override fun closeDialog() {
  dlgProduto?.close()
}

override fun focusCodigoBarra() {
  dlgProduto?.focusCodigoBarra()
}

override fun produtosSelecionados(): List<NotaRecebimentoProduto> {
  return this.dlgProduto?.produtosSelecionados().orEmpty()
}

override fun openValidade(tipoValidade: Int, tempoValidade: Int, block: (ValidadeSaci) -> Unit) {
  dlgProduto?.openValidade(tipoValidade, tempoValidade, block)
}

override fun formAssina(produtos: List<NotaRecebimentoProduto>) {
  val form = FormAutoriza()
  DialogHelper.showForm(caption = "Recebe Nota", form = form) {
    viewModel.recebeNotaProduto(produtos, form.login, form.senha)
  }
}

override fun reloadGrid() {
  dlgProduto?.reloadGrid()
}

fun showDlgProdutos(nota: NotaRecebimento) {
  dlgProduto = DlgProdutosReceberNota(viewModel, nota)
  dlgProduto?.showDialog {
    viewModel.updateView()
  }
}

override fun isAuthorized(): Boolean {
  val username = AppConfig.userLogin() as? UserSaci
  return username?.recebimentoReceberNota == true
}

override val label: String
  get() = "Recebe Nota"

override fun updateComponent() {
  viewModel.updateView()
}
}
