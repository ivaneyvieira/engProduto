package br.com.astrosoft.produto.view.precificacao

import br.com.astrosoft.framework.model.config.AppConfig
import br.com.astrosoft.framework.util.format
import br.com.astrosoft.framework.view.vaadin.TabPanelGrid
import br.com.astrosoft.framework.view.vaadin.helper.DialogHelper
import br.com.astrosoft.framework.view.vaadin.helper.addColumnSeq
import br.com.astrosoft.framework.view.vaadin.helper.columnGrid
import br.com.astrosoft.framework.view.vaadin.helper.shiftSelect
import br.com.astrosoft.produto.model.beans.*
import br.com.astrosoft.produto.model.planilha.PlanilhaPrecificacao
import br.com.astrosoft.produto.viewmodel.precificacao.ITabPrecificacaoViewModel
import br.com.astrosoft.produto.viewmodel.precificacao.TabPrecificacaoEntradaViewModel
import com.github.mvysny.karibudsl.v10.*
import com.vaadin.flow.component.HasComponents
import com.vaadin.flow.component.button.ButtonVariant
import com.vaadin.flow.component.grid.Grid
import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.component.select.Select
import com.vaadin.flow.component.textfield.IntegerField
import com.vaadin.flow.component.textfield.NumberField
import com.vaadin.flow.component.textfield.TextField
import com.vaadin.flow.component.textfield.TextFieldVariant
import com.vaadin.flow.data.value.ValueChangeMode
import org.vaadin.stefan.LazyDownloadButton
import java.io.ByteArrayInputStream
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class TabPrecificacaoEntrada(val viewModel: TabPrecificacaoEntradaViewModel) : TabPanelGrid<Precificacao>
  (Precificacao::class),
  ITabPrecificacaoViewModel {
  private lateinit var cmbLoja: Select<Loja>
  private lateinit var edtCodigo: IntegerField
  private lateinit var edtListVend: TextField
  private lateinit var edtType: TextField
  private lateinit var edtCl: IntegerField
  private lateinit var edtTributacao: TextField
  private lateinit var cmbPontos: Select<EMarcaPonto>
  private lateinit var edtQuery: TextField
  private lateinit var selectImposto: Select<ETipoImposto>
  private lateinit var percentualImposto: NumberField
  private lateinit var selectDiferenca: Select<EDifImposto>

  fun init() {
    val lojas = viewModel.findAllLojas()
    cmbLoja.setItems(lojas)
    cmbLoja.value = lojas.firstOrNull { it.no == 10 } ?: lojas.firstOrNull()
    cmbLoja.width = "8rem"
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

    edtQuery = textField("Pesquisa") {
      this.valueChangeMode = ValueChangeMode.LAZY
      addValueChangeListener {
        viewModel.updateView()
      }
    }

    edtCodigo = integerField("Código") {
      this.valueChangeMode = ValueChangeMode.LAZY
      this.width = "5rem"
      addValueChangeListener {
        viewModel.updateView()
      }
    }

    selectImposto = select("Imposto") {
      width = "7rem"
      setItems(ETipoImposto.entries)
      setItemLabelGenerator { tipo ->
        tipo.descricao
      }
      this.value = ETipoImposto.IPI

      addValueChangeListener {
        viewModel.updateView()
      }
    }

    percentualImposto = numberField("Percentual") {
      this.valueChangeMode = ValueChangeMode.LAZY
      this.width = "80px"
      this.value = null
      this.isClearButtonVisible = true
      this.addThemeVariants(TextFieldVariant.LUMO_ALIGN_RIGHT)

      addValueChangeListener {
        viewModel.updateView()
      }
    }

    selectDiferenca = select("Diferença") {
      width = "7rem"
      setItems(EDifImposto.entries)
      setItemLabelGenerator { tipo ->
        tipo.descricao
      }
      this.value = EDifImposto.TODOS

      addValueChangeListener {
        viewModel.updateView()
      }
    }

    button("Mudar %") {
      onClick {
        val itens = itensSelecionados()
        if (itens.isEmpty()) {
          DialogHelper.showError("Nenhum item selecionado")
        } else {
          val dialog = DialogPrecificacao(
            viewModel = viewModel,
            loja = cmbLoja.value?.no ?: 0,
            bean = BeanForm(),
            cardEntrada = true,
            cardSaida = false
          )
          dialog.open()
        }
      }
    }

    edtListVend = textField("Fornecedores") {
      this.valueChangeMode = ValueChangeMode.LAZY
      this.width = "8rem"
      this.value = "27142"
      addValueChangeListener {
        viewModel.updateView()
      }
    }

    edtTributacao = textField("Tributação") {
      this.valueChangeMode = ValueChangeMode.LAZY
      this.width = "80px"
      addValueChangeListener {
        viewModel.updateView()
      }
    }

    edtType = textField("Tipo") {
      this.valueChangeMode = ValueChangeMode.LAZY
      addValueChangeListener {
        viewModel.updateView()
      }
    }

    edtCl = integerField("Centro de Lucro") {
      this.width = "7rem"
      this.valueChangeMode = ValueChangeMode.LAZY
      addValueChangeListener {
        viewModel.updateView()
      }
    }

    cmbPontos = select("Caracteres") {
      this.width = "5rem"
      setItems(EMarcaPonto.entries)
      value = EMarcaPonto.NAO
      this.setItemLabelGenerator {
        it.descricao
      }

      addValueChangeListener {
        viewModel.updateView()
      }
    }

    downloadExcel()
  }

  private fun HasComponents.downloadExcel() {
    val button = LazyDownloadButton(VaadinIcon.TABLE.create(), { filename() }, {
      val planilha = PlanilhaPrecificacao()
      val list = itensSelecionados()
      val bytes = planilha.write(list)
      ByteArrayInputStream(bytes)
    })
    button.addThemeVariants(ButtonVariant.LUMO_SMALL)
    button.setTooltipText("Salva a planilha")
    add(button)
  }

  private fun filename(): String {
    val sdf = DateTimeFormatter.ofPattern("yyMMddHHmmss")
    val textTime = LocalDateTime.now().format(sdf)
    return "precificacao$textTime.xlsx"
  }

  override fun Grid<Precificacao>.gridPanel() {
    setSelectionMode(Grid.SelectionMode.MULTI)
    this.shiftSelect()

    addColumnSeq("Seq")
    columnGrid(property = Precificacao::codigo, header = "Cod")
    columnGrid(Precificacao::descricao, "Descrição")

    columnGrid(Precificacao::estoque, "Est")

    columnGrid(Precificacao::nfValor, "V. NF")
    columnGrid(Precificacao::pcfabrica, "P. Fab") {
      this.setHeader("P. Fab")
      this.isExpand = false
      this.isAutoWidth = false
      this.width = "100px"
    }

    columnGrid(Precificacao::nfIpi, "IPI NF")
    columnGrid(Precificacao::ipi, "IPI")

    columnGrid(Precificacao::nfIrst, "IR ST NF")
    columnGrid(Precificacao::retido, "IR ST")

    columnGrid(Precificacao::nfIcms, "ICMS NF")
    columnGrid(Precificacao::icmsp, "C. ICMS")

    columnGrid(Precificacao::nfFrete, "Frete NF")
    columnGrid(Precificacao::frete, "Frete")

    columnGrid(Precificacao::pisCofins, "Pis/Cofins")
    columnGrid(Precificacao::custoContabil, "C.Cont") {
      this.setPartNameGenerator {
        if (it.custoContabil.format() != it.precoCusto.format()) "marcaDiferenca" else null
      }
    }
    columnGrid(Precificacao::embalagem, "Emb")
    columnGrid(Precificacao::vendno, "Cod For") {
      this.isExpand = false
    }
    columnGrid(Precificacao::typeno, "Tipo") {
      this.isExpand = false
    }
    columnGrid(Precificacao::clno, "CL")
    columnGrid(Precificacao::ncm, "NCM")
    columnGrid(Precificacao::rotulo, "Rótulo")
    columnGrid(Precificacao::tributacao, "Trib")
    columnGrid(Precificacao::mvap, "MVA")
    columnGrid(Precificacao::creditoICMS, "ICMS Ent")
    columnGrid(Precificacao::freteICMSCalc, "ICMS Calc F") {
      this.setPartNameGenerator {
        if (it.freteICMS.format() != it.freteICMSCalc.format()) "marcaDiferenca" else null
      }
    }

    columnGrid(Precificacao::freteICMS, "ICMS F") {
      this.setPartNameGenerator {
        if (it.freteICMS.format() != it.freteICMSCalc.format()) "marcaDiferenca" else null
      }
    }

    columnGrid(Precificacao::cfinanceiro, "C. Fin")
    columnGrid(Precificacao::precoCusto, "P.Custo") {
      this.setPartNameGenerator {
        if (it.custoContabil.format() != it.precoCusto.format()) "marcaDiferenca" else null
      }
    }
    columnGrid(Precificacao::diferencaCusto, "Dif") {
      this.setPartNameGenerator {
        if (it.custoContabil.format() != it.precoCusto.format()) "marcaDiferenca" else null
      }
    }
  }

  override fun filtro(): FiltroPrecificacao {
    return FiltroPrecificacao(
      loja = cmbLoja.value?.no ?: 0,
      codigo = edtCodigo.value ?: 0,
      listVend = edtListVend.value?.split(",")?.mapNotNull { it.toIntOrNull() } ?: emptyList(),
      tributacao = edtTributacao.value ?: "",
      mva = "",
      typeno = edtType.value ?: "",
      clno = edtCl.value ?: 0,
      marcaPonto = cmbPontos.value ?: EMarcaPonto.TODOS,
      query = edtQuery.value ?: "",
      tipoImposto = selectImposto.value ?: ETipoImposto.IPI,
      percentualImposto = percentualImposto.value,
      ultnota = true,
      diferenca = selectDiferenca.value ?: EDifImposto.TODOS,
    )
  }

  override fun listSelected(): List<Precificacao> {
    return itensSelecionados()
  }

  override fun isAuthorized(): Boolean {
    val user = AppConfig.userLogin() as? UserSaci ?: return false
    return user.precificacaoEntrada
  }

  override val label: String
    get() = "Precificação Entrada"

  override fun updateComponent() {
    viewModel.updateView()
  }
}