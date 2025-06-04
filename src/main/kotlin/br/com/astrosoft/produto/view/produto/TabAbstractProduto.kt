package br.com.astrosoft.produto.view.produto

import br.com.astrosoft.framework.model.config.AppConfig
import br.com.astrosoft.framework.view.vaadin.TabPanelGrid
import br.com.astrosoft.produto.model.beans.*
import br.com.astrosoft.produto.model.planilha.PlanilhaProduto
import br.com.astrosoft.produto.viewmodel.produto.ITabAbstractProdutoViewModel
import br.com.astrosoft.produto.viewmodel.produto.TabAbstractProdutoViewModel
import com.github.mvysny.karibudsl.v10.*
import com.github.mvysny.kaributools.header2
import com.vaadin.flow.component.HasComponents
import com.vaadin.flow.component.button.ButtonVariant
import com.vaadin.flow.component.checkbox.Checkbox
import com.vaadin.flow.component.grid.Grid
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

abstract class TabAbstractProduto<T : ITabAbstractProdutoViewModel>(
  open val viewModel: TabAbstractProdutoViewModel<T>,
  val showDatas: Boolean = true
) :
  TabPanelGrid<Produtos>(Produtos::class), ITabAbstractProdutoViewModel {
  private lateinit var edtPesquisa: TextField
  private lateinit var edtCodigo: IntegerField
  private lateinit var edtVal: IntegerField
  private lateinit var cmbPontos: Select<EMarcaPonto>
  private lateinit var edtListVend: TextField
  protected lateinit var edtType: IntegerField
  protected lateinit var edtCl: IntegerField
  protected lateinit var edtTributacao: TextField

  private lateinit var edtVenda: Select<MesAno>
  protected lateinit var edtCompra: Select<MesAno>
  private lateinit var cmbLoja: Select<Loja>
  private lateinit var chkGrade: Checkbox
  private lateinit var edtGrade: TextField

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

        edtGrade = textField("Grade") {
          this.valueChangeMode = ValueChangeMode.LAZY
          this.valueChangeTimeout = 1500
          this.width = "100px"
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

        edtTributacao = textField("Trib") {
          this.valueChangeMode = ValueChangeMode.LAZY
          this.valueChangeTimeout = 1500
          this.width = "80px"
          addValueChangeListener {
            viewModel.updateView()
          }
        }

        edtType = integerField("Tipo") {
          this.valueChangeMode = ValueChangeMode.LAZY
          this.valueChangeTimeout = 1500
          this.width = "100px"
          addValueChangeListener {
            viewModel.updateView()
          }
        }

        edtCl = integerField("C Lucro") {
          this.width = "100px"
          this.valueChangeMode = ValueChangeMode.LAZY
          this.valueChangeTimeout = 1500
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

        chkGrade = checkBox("Grade") {
          this.isVisible = lojaProduto() == 0

          this.value = true
          this.addValueChangeListener {
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

        edtCompra = select("Compra ") {
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

        addAditionaisFields()
        downloadExcel(PlanilhaProduto())
      }
    }
  }

  private fun filename(): String {
    val sdf = DateTimeFormatter.ofPattern("yyMMddHHmmss")
    val textTime = LocalDateTime.now().format(sdf)
    return "produto$textTime.xlsx"
  }

  protected abstract fun HorizontalLayout.addAditionaisFields()

  override fun filtro() = FiltroListaProduto(
    pesquisa = edtPesquisa.value ?: "",
    marcaPonto = cmbPontos.value ?: EMarcaPonto.TODOS,
    inativo = EInativo.NAO,
    codigo = edtCodigo.value ?: 0,
    listVend = edtListVend.value?.split(",")?.mapNotNull { it.toIntOrNull() } ?: emptyList(),
    tributacao = edtTributacao.value ?: "",
    typeno = edtType.value ?: 0,
    clno = edtCl.value ?: 0,
    loja = cmbLoja.value?.no ?: 0,
    diVenda = edtVenda.value?.firstDay,
    dfVenda = MesAno.now().lastDay,
    diCompra = edtCompra.value?.firstDay,
    dfCompra = MesAno.now().lastDay,
    temGrade = chkGrade.value,
    grade = edtGrade.value ?: "",
    estoque = estoque(),
    saldo = saldo(),
    validade = edtVal.value ?: 0,
    temValidade = temValidade()
  )

  abstract fun estoque(): EEstoqueList
  abstract fun saldo(): Int
  abstract fun temValidade(): Boolean

  override fun Grid<Produtos>.gridPanel() {
    setSelectionMode(Grid.SelectionMode.MULTI)
    this.colunasGrid()
    printColunas()
  }

  abstract fun Grid<Produtos>.colunasGrid()

  private fun printColunas() {
    val colList = gridPanel.columns.joinToString("\n") { column ->
      "CampoNumber(\"${column.header2}\") { ${column.key} ?: 0.00 },"
    }
    println(label)
    println(colList)
  }

  private fun HasComponents.downloadExcel(planilha: PlanilhaProduto) {
    val button = LazyDownloadButton(VaadinIcon.TABLE.create(), { filename() }, {
      val bytes = planilha.write(itensSelecionados())
      ByteArrayInputStream(bytes)
    })
    button.addThemeVariants(ButtonVariant.LUMO_SMALL)
    add(button)
  }
}


