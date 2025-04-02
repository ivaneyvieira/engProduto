package br.com.astrosoft.produto.view.devFor2

import br.com.astrosoft.framework.model.config.AppConfig
import br.com.astrosoft.framework.view.vaadin.TabPanelGrid
import br.com.astrosoft.framework.view.vaadin.helper.*
import br.com.astrosoft.produto.model.beans.*
import br.com.astrosoft.produto.viewmodel.devFor2.ITabNotaPendencia
import br.com.astrosoft.produto.viewmodel.devFor2.TabNotaPendenciaViewModel
import com.github.mvysny.karibudsl.v10.*
import com.vaadin.flow.component.datepicker.DatePicker
import com.vaadin.flow.component.grid.Grid
import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.component.select.Select
import com.vaadin.flow.component.textfield.TextField
import com.vaadin.flow.data.value.ValueChangeMode
import java.time.LocalDate

class TabNotaPendencia(val viewModel: TabNotaPendenciaViewModel) :
  TabPanelGrid<NotaRecebimento>(NotaRecebimento::class), ITabNotaPendencia {
  private var dlgProduto: DlgProdutosNotaPendencia? = null
  private var dlgArquivo: DlgArquivoNotaPendencia? = null
  private lateinit var cmbLoja: Select<Loja>
  private lateinit var edtPesquisa: TextField
  private lateinit var edtDataInicial: DatePicker
  private lateinit var edtDataFinal: DatePicker
  private lateinit var edtTipoNota: Select<EListaContas>
  private lateinit var edtTemAnexo: Select<ETemAnexo>

  fun init() {
    cmbLoja.setItems(viewModel.findAllLojas() + listOf(Loja.lojaZero))
    val user = AppConfig.userLogin() as? UserSaci
    cmbLoja.isReadOnly = user?.lojaRec != 0
    cmbLoja.value = viewModel.findLoja(4) ?: viewModel.findLoja(user?.lojaRec ?: 0) ?: Loja.lojaZero
  }

  override fun HorizontalLayout.toolBarConfig() {
    verticalLayout(spacing = false) {
      this.isMargin = false
      horizontalLayout {
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
        edtTemAnexo = select("Tem Anexo") {
          this.setItems(ETemAnexo.entries)
          this.value = ETemAnexo.TODOS
          this.setItemLabelGenerator {
            it.descricao
          }
          addValueChangeListener {
            viewModel.updateView()
          }
        }
      }
      horizontalLayout {
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
        button("Impressoa") {
          this.icon = VaadinIcon.PRINT.create()
          addClickListener {
            viewModel.imprimeNotas()
          }
        }
      }
    }
  }

  override fun Grid<NotaRecebimento>.gridPanel() {
    this.addClassName("styling")
    this.format()

    columnGrid(NotaRecebimento::loja, header = "Loja")

    addColumnButton(VaadinIcon.FILE_TABLE, "Produtos", "Produtos") { nota ->
      dlgProduto = DlgProdutosNotaPendencia(viewModel, nota)
      dlgProduto?.showDialog {
        viewModel.updateView()
      }
    }

    addColumnButton(VaadinIcon.FILE, "Arquivo", "Arquivo") { nota ->
      dlgArquivo = DlgArquivoNotaPendencia(viewModel, nota)
      dlgArquivo?.showDialog {
        viewModel.updateView()
      }
    }.setPartNameGenerator {
      if (it.quantFile > 0) {
        "amarelo"
      } else {
        ""
      }
    }

    this.selectionMode = Grid.SelectionMode.MULTI

    columnGrid(NotaRecebimento::ni, header = "NI").right()
    columnGrid(NotaRecebimento::nfEntrada, header = "NF Entrada").right()
    columnGrid(NotaRecebimento::emissao, header = "Emissão", width = null)
    columnGrid(NotaRecebimento::data, header = "Entrada", width = null)
    columnGrid(NotaRecebimento::vendnoProduto, header = "For Cad")
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
      marca = EMarcaRecebimento.TODOS,
      dataFinal = edtDataFinal.value,
      dataInicial = edtDataInicial.value,
      localizacao = usr?.localizacaoRec.orEmpty().toList(),
      tipoNota = edtTipoNota.value ?: EListaContas.TODOS,
      temAnexo = edtTemAnexo.value ?: ETemAnexo.TODOS,
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

  override fun produtosSelecionados(): List<NotaRecebimentoProduto> {
    return this.dlgProduto?.produtosSelecionados().orEmpty()
  }

  override fun notasSelecionadas(): List<NotaRecebimento> {
    return this.itensSelecionados()
  }

  override fun updateProduto(): NotaRecebimento? {
    return dlgProduto?.updateProduto()
  }

  fun showDlgProdutos(nota: NotaRecebimento) {
    dlgProduto = DlgProdutosNotaPendencia(viewModel, nota)
    dlgProduto?.showDialog {
      viewModel.updateView()
    }
  }

  override fun isAuthorized(): Boolean {
    val username = AppConfig.userLogin() as? UserSaci
    return username?.devFor2NotaPendencia == true
  }

  override val label: String
    get() = "Pendencia"

  override fun updateComponent() {
    viewModel.updateView()
  }

  override fun printerUser(): List<String> {
    val user = AppConfig.userLogin() as? UserSaci
    return user?.impressoraRec.orEmpty().toList()
  }
}