package br.com.astrosoft.produto.view.reposicao

import br.com.astrosoft.framework.model.config.AppConfig
import br.com.astrosoft.framework.view.vaadin.TabPanelGrid
import br.com.astrosoft.framework.view.vaadin.helper.*
import br.com.astrosoft.framework.view.vaadin.right
import br.com.astrosoft.produto.model.beans.*
import br.com.astrosoft.produto.viewmodel.reposicao.ITabReposicaoEnt
import br.com.astrosoft.produto.viewmodel.reposicao.TabReposicaoEntViewModel
import com.github.mvysny.karibudsl.v10.datePicker
import com.github.mvysny.karibudsl.v10.select
import com.github.mvysny.karibudsl.v10.textField
import com.vaadin.flow.component.datepicker.DatePicker
import com.vaadin.flow.component.grid.Grid
import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.component.select.Select
import com.vaadin.flow.component.textfield.TextField
import java.time.LocalDate

class TabReposicaoEnt(
  val viewModel: TabReposicaoEntViewModel,
  val codigo: String,
  val grade: String,
) : TabPanelGrid<Reposicao>(Reposicao::class), ITabReposicaoEnt {
  private var dlgProduto: DlgProdutosReposEnt? = null
  private lateinit var edtDataInicial: DatePicker
  private lateinit var edtDataFinal: DatePicker
  private lateinit var cmbLoja: Select<Loja>
  private lateinit var edtPesquisa: TextField

  fun init() {
    cmbLoja.setItems(viewModel.findAllLojas() + listOf(Loja.lojaZero))
    cmbLoja.value = viewModel.findLoja(0) ?: Loja.lojaZero
  }

  override fun filtroProduto(): Boolean = codigo != "" || grade != ""

  override fun HorizontalLayout.toolBarConfig() {
    this.isVisible = !filtroProduto()
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
    edtPesquisa = textField("Pesquisa") {
      this.width = "300px"
      addValueChangeListener {
        viewModel.updateView()
      }
    }
    edtDataInicial = datePicker("Data Inicial") {
      this.value = LocalDate.now()
      this.localePtBr()
      addValueChangeListener {
        viewModel.updateView()
      }
    }
    edtDataFinal = datePicker("Data Final") {
      this.value = LocalDate.now()
      this.localePtBr()
      addValueChangeListener {
        viewModel.updateView()
      }
    }
  }

  override fun Grid<Reposicao>.gridPanel() {
    this.addClassName("styling")
    this.format()

    addColumnButton(VaadinIcon.PRINT, "Preview", "Preview") { pedido ->
      viewModel.previewPedido(pedido) {
        viewModel.marcaImpressao(pedido)
      }
    }
    columnGridProduto()
    columnGrid(Reposicao::loja, "Loja")
    columnGrid(Reposicao::numero, "Pedido")
    columnGrid(Reposicao::data, "Data")
    columnGrid(Reposicao::localizacao, "Loc")

    if (!filtroProduto()) {
      addColumnButton(VaadinIcon.SIGN_IN, "Assina", "Assina") { pedido ->
        viewModel.formEntregue(pedido)
      }
    }
    columnGrid(Reposicao::entregueSNome, "Entregue")
    if (!filtroProduto()) {
      addColumnButton(VaadinIcon.SIGN_IN, "Assina", "Assina") { pedido ->
        viewModel.formRecebido(pedido)
      }
    }
    columnGrid(Reposicao::recebidoSNome, "Recebido")
    if (!filtroProduto()) {
      columnGrid(Reposicao::usuarioApp, "Login")
    }

    if(filtroProduto()) {
      columnGrid({
        val reposicao = it.produtos.filter { prd ->
          prd.codigo == codigo && prd.grade == grade
        }.firstOrNull()
        reposicao?.quantidade ?: 0
      }, "Quant").right()
    }
  }

  private fun Grid<Reposicao>.columnGridProduto() {
    this.addColumnButton(VaadinIcon.FILE_TABLE, "Produtos", "Produtos") { ressuprimento ->
      dlgProduto = DlgProdutosReposEnt(viewModel, listOf(ressuprimento), filtroProduto())
      dlgProduto?.showDialog {
        viewModel.updateView()
      }
    }
  }

  override fun filtro(): FiltroReposicao {
    val user = AppConfig.userLogin() as? UserSaci
    val localizacao = if (user?.admin == true) {
      listOf("TODOS")
    } else {
      user?.localizacaoRepo.orEmpty().toList()
    }
    return FiltroReposicao(
      loja = cmbLoja.value.no,
      pesquisa = edtPesquisa.value ?: "",
      marca = EMarcaReposicao.ENT,
      localizacao = localizacao,
      dataInicial = edtDataInicial.value,
      dataFinal = edtDataFinal.value,
      codigo = codigo,
      grade = grade,
    )
  }

  override fun updateReposicoes(reposicoes: List<Reposicao>) {
    this.updateGrid(reposicoes)
  }

  override fun formEntregue(pedido: Reposicao) {
    val form = FormAutoriza()
    DialogHelper.showForm(caption = "Entregue", form = form) {
      viewModel.entreguePedido(pedido, form.login, form.senha)
    }
  }

  override fun formRecebe(pedido: Reposicao) {
    val form = FormFuncionario()
    DialogHelper.showForm(caption = "Recebido", form = form) {
      viewModel.recebePedido(pedido, form.numero)
    }
  }

  override fun updateProdutos(reposicoes: List<Reposicao>) {
    dlgProduto?.update(reposicoes)
  }

  override fun isAuthorized(): Boolean {
    val username = AppConfig.userLogin() as? UserSaci
    return username?.reposicaoEnt == true
  }

  override val label: String
    get() = "Entregue"

  override fun updateComponent() {
    viewModel.updateView()
  }

  override fun printerUser(): List<String> {
    val user = AppConfig.userLogin() as? UserSaci
    val impressora = user?.impressoraRepo ?: return emptyList()
    return impressora.toList()
  }
}