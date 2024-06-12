package br.com.astrosoft.produto.view.produto

import br.com.astrosoft.framework.model.config.AppConfig
import br.com.astrosoft.framework.view.vaadin.TabPanelGrid
import br.com.astrosoft.framework.view.vaadin.helper.*
import br.com.astrosoft.produto.model.beans.*
import br.com.astrosoft.produto.model.planilha.PlanilhaDadosValidade
import br.com.astrosoft.produto.viewmodel.produto.ITabDadosValidade
import br.com.astrosoft.produto.viewmodel.produto.TabDadosValidadeViewModel
import com.github.mvysny.karibudsl.v10.button
import com.github.mvysny.karibudsl.v10.integerField
import com.github.mvysny.karibudsl.v10.select
import com.github.mvysny.karibudsl.v10.textField
import com.github.mvysny.kaributools.asc
import com.github.mvysny.kaributools.fetchAll
import com.github.mvysny.kaributools.sort
import com.vaadin.flow.component.HasComponents
import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.grid.Grid
import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.component.orderedlayout.FlexComponent
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.component.select.Select
import com.vaadin.flow.component.textfield.IntegerField
import com.vaadin.flow.component.textfield.TextField
import com.vaadin.flow.data.value.ValueChangeMode
import org.vaadin.stefan.LazyDownloadButton
import java.io.ByteArrayInputStream
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class TabDadosValidade(val viewModel: TabDadosValidadeViewModel) :
  TabPanelGrid<DadosValidade>(DadosValidade::class),
  ITabDadosValidade {
  private lateinit var edtPesquisa: TextField
  private lateinit var edtCodigo: TextField
  private lateinit var edtInventario: IntegerField
  private lateinit var edtAno: IntegerField
  private lateinit var edtMes: IntegerField
  private lateinit var edtGrade: TextField
  private lateinit var cmbLoja: Select<Loja>
  private lateinit var cmbCartacer: Select<ECaracter>
  private lateinit var btnAdiciona: Button
  private lateinit var btnRemover: Button

  fun init() {
    cmbLoja.setItems(viewModel.findAllLojas() + listOf(Loja.lojaZero))
    val user = AppConfig.userLogin() as? UserSaci
    cmbLoja.isReadOnly = user?.lojaProduto != 0
    cmbLoja.value = viewModel.findLoja(user?.lojaProduto ?: 0) ?: Loja.lojaZero
  }

  override fun HorizontalLayout.toolBarConfig() {
    verticalBlock {
      horizontalBlock {
        isSpacing = true
        cmbLoja = select("Loja") {
          this.setItemLabelGenerator { item ->
            item.descricao
          }
          addValueChangeListener {
            if (it.isFromClient)
              viewModel.updateView()
          }
        }

        edtPesquisa = textField("Pesquisa") {
          this.width = "300px"
          this.isClearButtonVisible = true
          valueChangeMode = ValueChangeMode.TIMEOUT
          addValueChangeListener {
            viewModel.updateView()
          }
        }

        init()

        edtGrade = textField("Grade") {
          this.width = "100px"
          this.isClearButtonVisible = true
          valueChangeMode = ValueChangeMode.TIMEOUT
          addValueChangeListener {
            viewModel.updateView()
          }
        }

        edtInventario = integerField("Validade") {
          this.width = "100px"
          this.isClearButtonVisible = true
          valueChangeMode = ValueChangeMode.TIMEOUT
          addValueChangeListener {
            viewModel.updateView()
          }
        }

        edtMes = integerField("Mês") {
          this.width = "100px"
          this.isClearButtonVisible = true
          valueChangeMode = ValueChangeMode.LAZY
          valueChangeTimeout = 500
          addValueChangeListener {
            viewModel.updateView()
          }
        }

        edtAno = integerField("Ano") {
          this.width = "100px"
          this.isClearButtonVisible = true
          valueChangeMode = ValueChangeMode.LAZY
          valueChangeTimeout = 500
          addValueChangeListener {
            viewModel.updateView()
          }
        }

        cmbCartacer = select("Caracter") {
          this.setItems(ECaracter.entries)
          this.setItemLabelGenerator { item ->
            item.descricao
          }
          this.value = ECaracter.TODOS
          addValueChangeListener {
            viewModel.updateView()
          }
        }
      }
      horizontalBlock {
        isSpacing = true
        this.alignItems = FlexComponent.Alignment.BASELINE

        edtCodigo = textField("Código") {
          this.width = "110px"
          this.isClearButtonVisible = true
          valueChangeMode = ValueChangeMode.TIMEOUT
          addValueChangeListener {
            viewModel.updateView()
          }
        }

        btnAdiciona = button("Adicionar") {
          this.icon = VaadinIcon.PLUS.create()
          addClickListener {
            viewModel.adicionarLinha()
          }
        }

        btnRemover = button("Remover") {
          this.icon = VaadinIcon.TRASH.create()
          addClickListener {
            viewModel.removerLinha()
          }
        }

        downloadExcel(PlanilhaDadosValidade())
      }
    }
  }

  override fun Grid<DadosValidade>.gridPanel() {
    this.addClassName("styling")
    setSelectionMode(Grid.SelectionMode.MULTI)
    this.withEditor(
      DadosValidade::class,
      openEditor = {
        this.focusEditor(DadosValidade::vencimentoStr)
      },
      closeEditor = {
        viewModel.salvaInventario(it.bean)
      })
    this.addColumnSeq("Seq", width = "50px")
    this.columnGrid(DadosValidade::codigo, header = "Código")
    this.columnGrid(DadosValidade::descricao, header = "Descrição").expand()
    this.columnGrid(DadosValidade::grade, header = "Grade", width = "100px")
    this.columnGrid(DadosValidade::abrevLoja, header = "Loja", width = "70px") {
      this.setComparator(Comparator.comparingInt { produto -> produto.loja })
    }
    this.columnGrid(DadosValidade::estoqueLoja, header = "Total")
    columnGrid(DadosValidade::vencimentoStr, header = "Venc", width = "130px") {
      this.setComparator(Comparator.comparingInt { produto -> produto.vencimento })
    }.mesAnoFieldEditor()
    columnGrid(DadosValidade::inventario, header = "Inv", width = "100px").integerFieldEditor()
    columnGrid(DadosValidade::dataEntrada, header = "Data Mov", width = "120px").dateFieldEditor {
      it.value = LocalDate.now()
    }
    columnGrid(DadosValidade::validade, header = "Val")
    columnGrid(DadosValidade::unidade, header = "Un")
    columnGrid(DadosValidade::vendno, header = "For")

    this.sort(
      DadosValidade::abrevLoja.asc,
      DadosValidade::codigo.asc,
      DadosValidade::grade.asc,
      DadosValidade::vencimentoStr.asc
    )

    this.dataProvider.addDataProviderListener {
      updateTotais()
    }
  }

  private fun updateTotais() {
    if (!edtCodigo.value.isNullOrBlank()) {
      val list = gridPanel.dataProvider.fetchAll()
      val totalEstoqueLoja = list.groupBy { "${it.loja} ${it.prdno} ${it.grade}" }.map {
        it.value.firstOrNull()?.estoqueLoja ?: 0
      }.sum()

    } else {

    }
  }

  override fun filtro(): FiltroDadosValidade {
    val user = AppConfig.userLogin() as? UserSaci
    return FiltroDadosValidade(
      pesquisa = edtPesquisa.value ?: "",
      codigo = edtCodigo.value ?: "",
      validade = edtInventario.value ?: 0,
      grade = edtGrade.value ?: "",
      caracter = cmbCartacer.value ?: ECaracter.TODOS,
      ano = edtAno.value ?: 0,
      mes = edtMes.value ?: 0,
      storeno = cmbLoja.value?.no ?: user?.lojaProduto ?: 0,
    )
  }

  override fun updateProdutos(produtos: List<DadosValidade>) {
    updateGrid(produtos)
  }

  override fun produtosSelecionados(): List<DadosValidade> {
    return itensSelecionados()
  }

  override fun isAuthorized(): Boolean {
    val username = AppConfig.userLogin() as? UserSaci
    return username?.produtoEditor == true
  }

  override val label: String
    get() = "Editor"

  override fun updateComponent() {
    viewModel.updateView()
  }

  private fun HasComponents.downloadExcel(planilha: PlanilhaDadosValidade) {
    val button = LazyDownloadButton(VaadinIcon.TABLE.create(), { filename() }, {
      val bytes = planilha.write(itensSelecionados())
      ByteArrayInputStream(bytes)
    })
    button.text = "Planilha"
    add(button)
  }

  private fun filename(): String {
    val sdf = DateTimeFormatter.ofPattern("yyMMddHHmmss")
    val textTime = LocalDateTime.now().format(sdf)
    return "produto$textTime.xlsx"
  }
}