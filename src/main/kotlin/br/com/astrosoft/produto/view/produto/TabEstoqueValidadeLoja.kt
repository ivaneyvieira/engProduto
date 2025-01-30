package br.com.astrosoft.produto.view.produto

import br.com.astrosoft.framework.model.config.AppConfig
import br.com.astrosoft.framework.view.vaadin.TabPanelGrid
import br.com.astrosoft.framework.view.vaadin.helper.*
import br.com.astrosoft.produto.model.beans.*
import br.com.astrosoft.produto.model.planilha.PlanilhaProdutoLoja
import br.com.astrosoft.produto.viewmodel.produto.ITabEstoqueValidadeLojaViewModel
import br.com.astrosoft.produto.viewmodel.produto.TabEstoqueValidadeLojaViewModel
import br.com.astrosoft.promocao.view.produtos.columns.ProdutosColumns.produto_Unidade
import br.com.astrosoft.promocao.view.produtos.columns.ProdutosColumns.produto_abrev
import br.com.astrosoft.promocao.view.produtos.columns.ProdutosColumns.produto_codigo
import br.com.astrosoft.promocao.view.produtos.columns.ProdutosColumns.produto_descricao
import br.com.astrosoft.promocao.view.produtos.columns.ProdutosColumns.produto_forn
import br.com.astrosoft.promocao.view.produtos.columns.ProdutosColumns.produto_grade
import br.com.astrosoft.promocao.view.produtos.columns.ProdutosColumns.produto_loja
import br.com.astrosoft.promocao.view.produtos.columns.ProdutosColumns.produto_qttyInv
import br.com.astrosoft.promocao.view.produtos.columns.ProdutosColumns.produto_quantVenda
import br.com.astrosoft.promocao.view.produtos.columns.ProdutosColumns.produto_saldo
import br.com.astrosoft.promocao.view.produtos.columns.ProdutosColumns.produto_val
import com.github.mvysny.karibudsl.v10.*
import com.vaadin.flow.component.HasComponents
import com.vaadin.flow.component.button.ButtonVariant
import com.vaadin.flow.component.grid.Grid
import com.vaadin.flow.component.grid.GridMultiSelectionModel
import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.component.orderedlayout.FlexLayout
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.component.select.Select
import com.vaadin.flow.component.textfield.IntegerField
import com.vaadin.flow.component.textfield.TextField
import com.vaadin.flow.data.provider.DataCommunicator
import com.vaadin.flow.data.provider.ListDataProvider
import com.vaadin.flow.data.provider.Query
import com.vaadin.flow.data.value.ValueChangeMode
import com.vaadin.flow.data.value.ValueChangeMode.LAZY
import com.vaadin.flow.function.SerializablePredicate
import org.vaadin.stefan.LazyDownloadButton
import java.io.ByteArrayInputStream
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.stream.Collectors
import java.util.stream.Stream

class TabEstoqueValidadeLoja(val viewModel: TabEstoqueValidadeLojaViewModel) :
  TabPanelGrid<Produtos>(Produtos::class), ITabEstoqueValidadeLojaViewModel {
  private lateinit var edtPesquisa: TextField
  private lateinit var edtCodigo: IntegerField
  private lateinit var edtVal: IntegerField
  private lateinit var cmbPontos: Select<EMarcaPonto>
  private lateinit var edtListVend: TextField

  private lateinit var edtVenda: Select<MesAno>
  private lateinit var cmbLoja: Select<Loja>
  private lateinit var cmbEstoqueFiltro: Select<EEstoqueList>
  private lateinit var edtSaldo: IntegerField

  override fun isAuthorized() = (AppConfig.userLogin() as? UserSaci)?.produtoEstoqueValidadeLoja ?: false

  override val label: String
    get() = "Validade"

  fun estoque(): EEstoqueList {
    return cmbEstoqueFiltro.value ?: EEstoqueList.TODOS
  }

  fun saldo(): Int {
    return edtSaldo.value ?: 0
  }

  fun temValidade(): Boolean {
    return true
  }

  fun String?.toMesAno(): Int {
    this ?: return 0
    val mes = this.substring(0, 2).toIntOrNull() ?: return 0
    val ano = this.substring(3, 5).toIntOrNull() ?: return 0
    return mes + (ano + 2000) * 100
  }

  override fun updateComponent() {
    viewModel.updateView()
  }

  fun lojaProduto(): Int {
    val user = AppConfig.userLogin() as? UserSaci
    return user?.lojaProduto ?: 0
  }

  override fun produtosSelecionados(): List<Produtos> {
    val dataProvider: ListDataProvider<Produtos> = gridPanel.dataProvider as ListDataProvider<Produtos>
    val size = dataProvider.items.size
    val dataCommunicator: DataCommunicator<Produtos> = gridPanel.dataCommunicator
    val stream: Stream<Produtos> =
        dataProvider.fetch(
          Query<Produtos, SerializablePredicate<Produtos>>(
            0,
            size,
            dataCommunicator.backEndSorting,
            dataCommunicator.inMemorySorting,
            null
          )
        )
    val list: List<Produtos> = stream.collect(Collectors.toList())
    val selecionado = this.itensSelecionados()
    return list.filter { selecionado.contains(it) }
  }

  override fun HorizontalLayout.toolBarConfig() {
    flexLayout {
      this.isExpand = true
      this.flexWrap = FlexLayout.FlexWrap.WRAP
      this.alignContent = FlexLayout.ContentAlignment.SPACE_BETWEEN

      horizontalLayout {
        edtPesquisa = textField("Pesquisa") {
          this.width = "200px"
          this.valueChangeMode = LAZY
          this.valueChangeTimeout = 1500
          addValueChangeListener {
            viewModel.updateView()
          }
        }

        edtCodigo = integerField("Código") {
          this.width = "100px"
          this.valueChangeMode = LAZY
          addValueChangeListener {
            viewModel.updateView()
          }
        }

        edtVal = integerField("Val") {
          this.width = "80px"
          this.valueChangeMode = ValueChangeMode.LAZY
          this.valueChangeTimeout = 1500
          addValueChangeListener {
            viewModel.updateView()
          }
        }

        edtListVend = textField("Fornecedores") {
          this.valueChangeMode = ValueChangeMode.LAZY
          this.valueChangeTimeout = 1500
          this.width = "100px"
          addValueChangeListener {
            viewModel.updateView()
          }
        }

        cmbPontos = select("Caracteres") {
          this.width = "100px"
          setItems(EMarcaPonto.entries)
          value = EMarcaPonto.NAO
          this.setItemLabelGenerator {
            it.descricao
          }
          addValueChangeListener {
            viewModel.updateView()
          }
        }
      }

      span {
        this.width = "10px"
      }

      horizontalLayout {
        cmbLoja = select("Loja") {
          this.isVisible = lojaProduto() == 0

          val lojaTodas = Loja(0, "Todas", "Todas")

          val user = AppConfig.userLogin() as? UserSaci

          val lojaProduto = user?.lojaProduto ?: 0

          val lojas = if (lojaProduto == 0) {
            listOf(lojaTodas) + viewModel.allLojas()
          } else {
            listOf((viewModel.allLojas().firstOrNull { it.no == lojaProduto } ?: lojaTodas))
          }
          setItems(lojas)
          value = lojas.firstOrNull()
          this.setItemLabelGenerator {
            it.sname
          }
          addValueChangeListener {
            viewModel.updateView()
          }
          this.width = "6em"
        }

        edtVenda = select("Venda") {
          this.width = "100px"
          this.setItems(MesAno.values())
          this.value = MesAno.now()
          this.setItemLabelGenerator {
            it.mesAnoFormat
          }

          this.addValueChangeListener {
            viewModel.updateView()
          }
        }

        cmbEstoqueFiltro = select("Estoque") {
          this.width = "100px"
          setItems(EEstoqueList.entries)
          value = EEstoqueList.TODOS
          this.setItemLabelGenerator {
            it.descricao
          }
          addValueChangeListener {
            viewModel.updateView()
          }
        }

        edtSaldo = integerField("Saldo") {
          this.isAutofocus = true
          this.valueChangeMode = ValueChangeMode.LAZY
          this.valueChangeTimeout = 1500
          this.value = 0
          addValueChangeListener {
            viewModel.updateView()
          }
          this.width = "5em"
        }

        button("Relatório") {
          icon = VaadinIcon.PRINT.create()
          this.addClickListener {
            viewModel.geraRelatorio()
          }
        }

        downloadExcel(PlanilhaProdutoLoja())
      }
    }
  }

  private fun filename(): String {
    val sdf = DateTimeFormatter.ofPattern("yyMMddHHmmss")
    val textTime = LocalDateTime.now().format(sdf)
    return "produtoLoja$textTime.xlsx"
  }

  override fun filtro() = FiltroListaProduto(
    pesquisa = edtPesquisa.value ?: "",
    marcaPonto = cmbPontos.value ?: EMarcaPonto.TODOS,
    inativo = EInativo.NAO,
    codigo = edtCodigo.value ?: 0,
    listVend = edtListVend.value?.split(",")?.mapNotNull { it.toIntOrNull() } ?: emptyList(),
    tributacao = "",
    typeno = 0,
    clno = 0,
    loja = cmbLoja.value?.no ?: 0,
    diVenda = edtVenda.value?.firstDay,
    dfVenda = MesAno.now().lastDay,
    diCompra = MesAno.now().firstDay,
    dfCompra = MesAno.now().lastDay,
    temGrade = true,
    grade = "",
    estoque = estoque(),
    saldo = saldo(),
    validade = edtVal.value ?: 0,
    temValidade = temValidade()
  )

  override fun Grid<Produtos>.gridPanel() {
    this.selectionMode = Grid.SelectionMode.MULTI
    val sel = this.selectionModel as? GridMultiSelectionModel<Produtos>
    sel?.isDragSelect = true
    val user = AppConfig.userLogin() as? UserSaci

    this.withEditor(
      Produtos::class,
      openEditor = {
        this.focusEditor(Produtos::dataVenda)
      },
      closeEditor = {
        viewModel.salvaValidades(it.bean)
      })

    this.shiftSelect()
    produto_loja()
    produto_codigo()
    produto_descricao()
    produto_grade()
    produto_Unidade()
    produto_quantVenda()
    produto_saldo()
    produto_qttyInv()

    columnGrid(Produtos::dataVenda, "Data Conf", width = "100px").dateFieldEditor()

    addColumnButton(VaadinIcon.DATE_INPUT, "Validade", "Validade") { produto ->
      val form = FormValidadeQuant(produto)
      DialogHelper.showForm(caption = "Validade", form = form) {
        viewModel.salvaValidades(produto)
      }
    }

    columnGrid(Produtos::qttyDif01, "QTD 1")
    columnGrid(Produtos::venc01, "Vence 1", width = "80px") {
      this.setComparator(Comparator.comparingInt { produto -> produto.venc01.toMesAno() })
    }

    columnGrid(Produtos::qttyDif02, "QTD 2")
    columnGrid(Produtos::venc02, "Vence 2", width = "80px") {
      this.setComparator(Comparator.comparingInt { produto -> produto.venc02.toMesAno() })
    }

    columnGrid(Produtos::qttyDif03, "QTD 3")
    columnGrid(Produtos::venc03, "Vence 3", width = "80px") {
      this.setComparator(Comparator.comparingInt { produto -> produto.venc03.toMesAno() })
    }

    columnGrid(Produtos::qttyDif04, "QTD 4")
    columnGrid(Produtos::venc04, "Vence 4", width = "80px") {
      this.setComparator(Comparator.comparingInt { produto -> produto.venc04.toMesAno() })
    }

    produto_val()
    produto_forn()
    produto_abrev()
  }

  private fun HasComponents.downloadExcel(planilha: PlanilhaProdutoLoja) {
    val button = LazyDownloadButton(VaadinIcon.TABLE.create(), { filename() }, {
      val bytes = planilha.write(itensSelecionados())
      ByteArrayInputStream(bytes)
    })
    button.addThemeVariants(ButtonVariant.LUMO_SMALL)
    add(button)
  }
}
