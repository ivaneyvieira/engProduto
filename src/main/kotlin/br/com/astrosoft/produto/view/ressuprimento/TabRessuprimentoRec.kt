package br.com.astrosoft.produto.view.ressuprimento

import br.com.astrosoft.framework.model.config.AppConfig
import br.com.astrosoft.framework.view.vaadin.TabPanelGrid
import br.com.astrosoft.framework.view.vaadin.helper.addColumnButton
import br.com.astrosoft.framework.view.vaadin.helper.format
import br.com.astrosoft.framework.view.vaadin.helper.localePtBr
import br.com.astrosoft.produto.model.beans.*
import br.com.astrosoft.produto.view.ressuprimento.columns.RessuprimentoColumns.colunaRessuprimentoData
import br.com.astrosoft.produto.view.ressuprimento.columns.RessuprimentoColumns.colunaRessuprimentoDataBaixa
import br.com.astrosoft.produto.view.ressuprimento.columns.RessuprimentoColumns.colunaRessuprimentoNotaBaixa
import br.com.astrosoft.produto.view.ressuprimento.columns.RessuprimentoColumns.colunaRessuprimentoNumero
import br.com.astrosoft.produto.view.ressuprimento.columns.RessuprimentoColumns.colunaRessuprimentoRecebidoPor
import br.com.astrosoft.produto.view.ressuprimento.columns.RessuprimentoColumns.colunaRessuprimentoSing
import br.com.astrosoft.produto.view.ressuprimento.columns.RessuprimentoColumns.colunaRessuprimentoTransportadorPor
import br.com.astrosoft.produto.view.ressuprimento.columns.RessuprimentoColumns.colunaRessuprimentoUsuarioApp
import br.com.astrosoft.produto.viewmodel.ressuprimento.ITabRessuprimentoRec
import br.com.astrosoft.produto.viewmodel.ressuprimento.TabRessuprimentoRecViewModel
import com.github.mvysny.karibudsl.v10.datePicker
import com.github.mvysny.karibudsl.v10.integerField
import com.github.mvysny.karibudsl.v10.select
import com.github.mvysny.karibudsl.v10.textField
import com.vaadin.flow.component.datepicker.DatePicker
import com.vaadin.flow.component.grid.Grid
import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.component.select.Select
import com.vaadin.flow.component.textfield.IntegerField
import com.vaadin.flow.component.textfield.TextField
import com.vaadin.flow.data.value.ValueChangeMode
import java.time.LocalDate

class TabRessuprimentoRec(val viewModel: TabRessuprimentoRecViewModel) :
  TabPanelGrid<Ressuprimento>(Ressuprimento::class), ITabRessuprimentoRec {
  private var dlgProduto: DlgProdutosRessuRec? = null
  private lateinit var edtRessuprimento: IntegerField
  private lateinit var edtPesquisa: TextField
  private lateinit var edtDataInicial: DatePicker
  private lateinit var edtDataFinal: DatePicker
  private lateinit var cmbLoja: Select<Loja>

  init {
    cmbLoja.setItems(viewModel.findAllLojas() + listOf(Loja.lojaZero))
    val user = AppConfig.userLogin() as? UserSaci
    cmbLoja.isReadOnly = user?.lojaRessu != 0
    cmbLoja.value = viewModel.findLoja(user?.lojaRessu ?: 0) ?: Loja.lojaZero
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

    edtRessuprimento = integerField("Número") {
      this.isVisible = false
      this.value = 0
      valueChangeMode = ValueChangeMode.TIMEOUT
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

  override fun Grid<Ressuprimento>.gridPanel() {
    this.addClassName("styling")
    this.format()
    addColumnButton(VaadinIcon.PRINT, "Preview", "Preview") { pedido ->
      viewModel.previewPedido(pedido) { impressora ->
        //viewModel.marcaImpressao(pedido, impressora)
      }
    }
    addColumnButton(VaadinIcon.FILE_TABLE, "Produtos", "Produtos") { ressuprimento ->
      dlgProduto = DlgProdutosRessuRec(viewModel, ressuprimento)
      dlgProduto?.showDialog {
        viewModel.updateView()
      }
    }
    colunaRessuprimentoNumero()
    colunaRessuprimentoData()
    colunaRessuprimentoNotaBaixa()
    colunaRessuprimentoDataBaixa()
    colunaRessuprimentoSing()
    colunaRessuprimentoTransportadorPor()
    colunaRessuprimentoRecebidoPor()
    colunaRessuprimentoUsuarioApp()

    this.setPartNameGenerator {
      val marca = it.marcaEnt ?: 0
      if (marca > 0) {
        "amarelo"
      } else null
    }
  }

  override fun filtro(marca: EMarcaRessuprimento): FiltroRessuprimento {
    val user = AppConfig.userLogin() as? UserSaci
    return FiltroRessuprimento(
      numero = edtRessuprimento.value ?: 0,
      pesquisa = edtPesquisa.value ?: "",
      marca = marca,
      temNota = ETemNota.TODOS,
      lojaRessu = cmbLoja.value?.no ?: 0,
      dataNotaInicial = edtDataInicial.value,
      dataNotaFinal = edtDataFinal.value,
    )
  }

  override fun updateRessuprimentos(ressuprimentos: List<Ressuprimento>) {
    updateGrid(ressuprimentos)
  }

  override fun updateProdutos() {
    dlgProduto?.update()
  }

  override fun produtosSelecionados(): List<ProdutoRessuprimento> {
    return dlgProduto?.itensSelecionados().orEmpty()
  }

  override fun produtosCodigoBarras(codigoBarra: String): ProdutoRessuprimento? {
    return dlgProduto?.produtosCodigoBarras(codigoBarra)
  }

  override fun updateProduto(produto: ProdutoRessuprimento) {
    dlgProduto?.updateProduto(produto)
  }

  override fun isAuthorized(): Boolean {
    val username = AppConfig.userLogin() as? UserSaci
    return username?.ressuprimentoRec == true
  }

  override val label: String
    get() = "Recebido"

  override fun updateComponent() {
    viewModel.updateView()
  }

  override fun printerUser(): List<String> {
    val username = AppConfig.userLogin() as? UserSaci
    val impressoraRessu = username?.impressoraRessu ?: return emptyList()
    return if (impressoraRessu == "TODOS") emptyList() else listOf(impressoraRessu)
  }
}