package br.com.astrosoft.produto.view.recebimento

import br.com.astrosoft.framework.model.config.AppConfig
import br.com.astrosoft.framework.view.vaadin.TabPanelGrid
import br.com.astrosoft.framework.view.vaadin.helper.*
import br.com.astrosoft.produto.model.beans.*
import br.com.astrosoft.produto.viewmodel.recebimento.ITabNotaRecebida
import br.com.astrosoft.produto.viewmodel.recebimento.TabNotaRecebidaViewModel
import com.github.mvysny.karibudsl.v10.*
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
  private lateinit var edtTemAnexo: Select<ETemAnexo>
  private lateinit var cmbDoc: Select<ENotaDoc>
  private lateinit var cmbProtocolo: Select<EProtocolo>

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
      horizontalLayout {
        button("Ass Envio Doc") {
          this.icon = VaadinIcon.SIGN_IN.create()
          onClick {
            viewModel.assinaEnvio()
          }
        }
        button("Ass Recebe Doc") {
          this.icon = VaadinIcon.SIGN_IN.create()
          onClick {
            viewModel.assinaRecebe()
          }
        }
        button("Imp Lista Doc") {
          this.icon = VaadinIcon.PRINT.create()
          onClick {
            viewModel.imprimeListaDoc()
          }
        }
        cmbDoc = select("Doc") {
          this.setItems(ENotaDoc.entries)
          this.value = ENotaDoc.DOC_TODOS
          this.setItemLabelGenerator {
            it.descricao
          }
          addValueChangeListener {
            viewModel.updateView()
          }
        }
        cmbProtocolo = select("Protocolo") {
          this.setItems(EProtocolo.entries)
          this.value = EProtocolo.TODOS
          this.setItemLabelGenerator {
            it.descricao
          }
          addValueChangeListener {
            viewModel.updateView()
          }
        }
      }
    }
  }

  override fun Grid<NotaRecebimento>.gridPanel() {
    this.addClassName("styling")
    this.format()

    columnGrid(NotaRecebimento::loja, header = "Loja")
    columnGrid(NotaRecebimento::usuarioLogin, header = "Recebedor")
    columnGrid(NotaRecebimento::loginEnvio, header = "Envio Doc")
    columnGrid(NotaRecebimento::loginReceb, header = "Recebe Doc")
    columnGrid(NotaRecebimento::protocolo, "Protocolo").right()
    columnGrid(NotaRecebimento::tipoNota, "Tipo Nota")

    addColumnButton(VaadinIcon.FILE_TABLE, "Produtos", "Produtos") { nota ->
      dlgProduto = DlgProdutosNotaRecebida(viewModel, nota)
      dlgProduto?.showDialog {
        viewModel.updateView()
      }
    }

    addColumnButton(VaadinIcon.FILE, "Arquivo", "Arquivo", configIcon = { icon, bean ->
      if (bean.arquivos().isNotEmpty()) {
        icon.element.style.set("color", "yellow")
      }
    }) { nota ->
      dlgArquivo = DlgArquivoNotaRecebida(viewModel, nota)
      dlgArquivo?.showDialog {
        viewModel.updateView()
      }
    }


    this.selectionMode = Grid.SelectionMode.MULTI

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
      temAnexo = edtTemAnexo.value ?: ETemAnexo.TODOS,
      docNota = cmbDoc.value ?: ENotaDoc.DOC_TODOS,
      protocolo = cmbProtocolo.value ?: EProtocolo.TODOS
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

  override fun formAssinaEnvio(itens: List<NotaRecebimento>) {
    val form = FormAutoriza()
    DialogHelper.showForm(caption = "Assina Envio", form = form) {
      viewModel.assinaEnvio(itens, form.login, form.senha)
    }
  }

  override fun formAssinaRecebe(itens: List<NotaRecebimento>) {
    val form = FormAutoriza()
    DialogHelper.showForm(caption = "Assina Recebimento", form = form) {
      viewModel.assinaRecebe(itens, form.login, form.senha)
    }
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

  override fun printerUser(): List<String> {
    val user = AppConfig.userLogin() as? UserSaci
    return user?.impressoraRec.orEmpty().toList()
  }
}