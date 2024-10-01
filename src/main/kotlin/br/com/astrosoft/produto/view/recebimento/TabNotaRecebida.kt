package br.com.astrosoft.produto.view.recebimento

import br.com.astrosoft.framework.model.config.AppConfig
import br.com.astrosoft.framework.view.vaadin.TabPanelGrid
import br.com.astrosoft.framework.view.vaadin.helper.addColumnButton
import br.com.astrosoft.framework.view.vaadin.helper.columnGrid
import br.com.astrosoft.framework.view.vaadin.helper.format
import br.com.astrosoft.framework.view.vaadin.helper.localePtBr
import br.com.astrosoft.produto.model.beans.*
import br.com.astrosoft.produto.viewmodel.recebimento.ITabNotaRecebida
import br.com.astrosoft.produto.viewmodel.recebimento.TabNotaRecebidaViewModel
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

class TabNotaRecebida(val viewModel: TabNotaRecebidaViewModel) :
  TabPanelGrid<NotaRecebimento>(NotaRecebimento::class), ITabNotaRecebida {
  private var dlgProduto: DlgProdutosNotaRecebida? = null
  private var dlgArquivo: DlgArquivoNotaRecebida? = null
  private lateinit var cmbLoja: Select<Loja>
  private lateinit var edtPesquisa: TextField
  private lateinit var edtDataInicial: DatePicker
  private lateinit var edtDataFinal: DatePicker
  private lateinit var edtTipoNota: Select<EListaContas>

  fun init() {
    cmbLoja.setItems(viewModel.findAllLojas() + listOf(Loja.lojaZero))
    val user = AppConfig.userLogin() as? UserSaci
    cmbLoja.isReadOnly = user?.lojaRec != 0
    cmbLoja.value = viewModel.findLoja(user?.lojaRec ?: 0) ?: Loja.lojaZero
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
      this.value = EListaContas.TODOS
      this.setItemLabelGenerator {
        it.descricao
      }
      addValueChangeListener {
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
    columnGrid(NotaRecebimento::usuarioLogin, header = "Recebedor")
    columnGrid(NotaRecebimento::tipoNota, "Tipo Nota")

    addColumnButton(VaadinIcon.FILE_TABLE, "Produtos", "Produtos") { nota ->
      dlgProduto = DlgProdutosNotaRecebida(viewModel, nota)
      dlgProduto?.showDialog {
        viewModel.updateView()
      }
    }

    addColumnButton(VaadinIcon.FILE, "Arquivo", "Arquivo") { nota ->
      dlgArquivo = DlgArquivoNotaRecebida(viewModel, nota)
      dlgArquivo?.showDialog {
        viewModel.updateView()
      }
    }.setPartNameGenerator {
      if(it.quantFile > 0) {
        "amarelo"
      } else {
        ""
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
      marca = EMarcaRecebimento.RECEBIDO,
      dataFinal = edtDataFinal.value,
      dataInicial = edtDataInicial.value,
      localizacao = usr?.localizacaoRec.orEmpty().toList(),
      tipoNota = edtTipoNota.value ?: EListaContas.TODOS,
    )
  }

  override fun updateNota(notas: List<NotaRecebimento>) {
    this.updateGrid(notas)
  }

  override fun updateArquivos() {
    dlgArquivo?.update()
  }

  override fun arquivosSelecionados(): List<InvFile> {
    return dlgArquivo?.produtosSelecionados().orEmpty()
  }

  fun showDlgProdutos(nota: NotaRecebimento) {
    dlgProduto = DlgProdutosNotaRecebida(viewModel, nota)
    dlgProduto?.showDialog {
      viewModel.updateView()
    }
  }

  override fun isAuthorized(): Boolean {
    val username = AppConfig.userLogin() as? UserSaci
    return username?.recebimentoNotaRecebida == true
  }

  override val label: String
    get() = "Nota Recebida"

  override fun updateComponent() {
    viewModel.updateView()
  }
}