package br.com.astrosoft.produto.view.precificacao

import br.com.astrosoft.framework.model.config.AppConfig
import br.com.astrosoft.framework.view.vaadin.TabPanelGrid
import br.com.astrosoft.framework.view.vaadin.helper.DialogHelper
import br.com.astrosoft.framework.view.vaadin.helper.addColumnSeq
import br.com.astrosoft.framework.view.vaadin.helper.shiftSelect
import br.com.astrosoft.produto.model.beans.*
import br.com.astrosoft.produto.model.planilha.PlanilhaPrecificacao
import br.com.astrosoft.produto.view.precificacao.columns.PrecificacaoColumns.promocaoCS
import br.com.astrosoft.produto.view.precificacao.columns.PrecificacaoColumns.promocaoClno
import br.com.astrosoft.produto.view.precificacao.columns.PrecificacaoColumns.promocaoCodigo
import br.com.astrosoft.produto.view.precificacao.columns.PrecificacaoColumns.promocaoContabil
import br.com.astrosoft.produto.view.precificacao.columns.PrecificacaoColumns.promocaoCpmf
import br.com.astrosoft.produto.view.precificacao.columns.PrecificacaoColumns.promocaoDescricao
import br.com.astrosoft.produto.view.precificacao.columns.PrecificacaoColumns.promocaoDesp
import br.com.astrosoft.produto.view.precificacao.columns.PrecificacaoColumns.promocaoEmbalagem
import br.com.astrosoft.produto.view.precificacao.columns.PrecificacaoColumns.promocaoFCP
import br.com.astrosoft.produto.view.precificacao.columns.PrecificacaoColumns.promocaoFrete
import br.com.astrosoft.produto.view.precificacao.columns.PrecificacaoColumns.promocaoIR
import br.com.astrosoft.produto.view.precificacao.columns.PrecificacaoColumns.promocaoIcms
import br.com.astrosoft.produto.view.precificacao.columns.PrecificacaoColumns.promocaoIcmsEnt
import br.com.astrosoft.produto.view.precificacao.columns.PrecificacaoColumns.promocaoIcmsSai
import br.com.astrosoft.produto.view.precificacao.columns.PrecificacaoColumns.promocaoIpi
import br.com.astrosoft.produto.view.precificacao.columns.PrecificacaoColumns.promocaoLucro
import br.com.astrosoft.produto.view.precificacao.columns.PrecificacaoColumns.promocaoMva
import br.com.astrosoft.produto.view.precificacao.columns.PrecificacaoColumns.promocaoNcm
import br.com.astrosoft.produto.view.precificacao.columns.PrecificacaoColumns.promocaoOut
import br.com.astrosoft.produto.view.precificacao.columns.PrecificacaoColumns.promocaoPDif
import br.com.astrosoft.produto.view.precificacao.columns.PrecificacaoColumns.promocaoPFabrica
import br.com.astrosoft.produto.view.precificacao.columns.PrecificacaoColumns.promocaoPRef
import br.com.astrosoft.produto.view.precificacao.columns.PrecificacaoColumns.promocaoPSug
import br.com.astrosoft.produto.view.precificacao.columns.PrecificacaoColumns.promocaoPis
import br.com.astrosoft.produto.view.precificacao.columns.PrecificacaoColumns.promocaoRetido
import br.com.astrosoft.produto.view.precificacao.columns.PrecificacaoColumns.promocaoRotulo
import br.com.astrosoft.produto.view.precificacao.columns.PrecificacaoColumns.promocaoTributacao
import br.com.astrosoft.produto.view.precificacao.columns.PrecificacaoColumns.promocaoTypeno
import br.com.astrosoft.produto.view.precificacao.columns.PrecificacaoColumns.promocaoVendno
import br.com.astrosoft.produto.viewmodel.precificacao.ITabPrecificacaoViewModel
import br.com.astrosoft.produto.viewmodel.precificacao.TabPrecificacaoViewModel
import com.github.mvysny.karibudsl.v10.*
import com.vaadin.flow.component.HasComponents
import com.vaadin.flow.component.button.ButtonVariant
import com.vaadin.flow.component.grid.Grid
import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.component.select.Select
import com.vaadin.flow.component.textfield.BigDecimalField
import com.vaadin.flow.component.textfield.IntegerField
import com.vaadin.flow.component.textfield.TextField
import com.vaadin.flow.component.textfield.TextFieldVariant
import com.vaadin.flow.data.value.ValueChangeMode
import org.vaadin.stefan.LazyDownloadButton
import java.io.ByteArrayInputStream
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class TabPrecificacao(val viewModel: TabPrecificacaoViewModel) : TabPanelGrid<Precificacao>(Precificacao::class),
  ITabPrecificacaoViewModel {
  private lateinit var edtCodigo: IntegerField
  private lateinit var edtListVend: TextField
  private lateinit var edtType: TextField
  private lateinit var edtCl: IntegerField
  private lateinit var edtTributacao: TextField
  private lateinit var edtMVA: TextField
  private lateinit var cmbPontos: Select<EMarcaPonto>
  private lateinit var edtQuery: TextField

  override fun HorizontalLayout.toolBarConfig() {
    edtQuery = textField("Pesquisa") {
      this.valueChangeMode = ValueChangeMode.LAZY
      addValueChangeListener {
        viewModel.updateView()
      }
    }

    edtCodigo = integerField("Código") {
      this.valueChangeMode = ValueChangeMode.LAZY
      addValueChangeListener {
        viewModel.updateView()
      }
    }

    edtListVend = textField("Fornecedores") {
      this.valueChangeMode = ValueChangeMode.LAZY
      this.width = "250px"
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

    edtMVA = textField("MVA") {
      this.valueChangeMode = ValueChangeMode.LAZY
      this.width = "80px"
      this.addThemeVariants(TextFieldVariant.LUMO_ALIGN_RIGHT)
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
      this.valueChangeMode = ValueChangeMode.LAZY
      addValueChangeListener {
        viewModel.updateView()
      }
    }

    cmbPontos = select("Caracteres Especiais") {
      setItems(EMarcaPonto.values().toList())
      value = EMarcaPonto.TODOS
      this.setItemLabelGenerator {
        it.descricao
      }

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
          val dialog = DialogPrecificacao(viewModel, BeanForm(), cardEntrada = true, cardSaida = true)
          dialog.open()
        }
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
    promocaoCodigo()
    promocaoDescricao()
    promocaoVendno() //promocaoFornecedor()
    promocaoTypeno()
    promocaoClno()
    promocaoNcm()
    promocaoRotulo()
    promocaoTributacao()
    promocaoMva()
    promocaoIcmsEnt()
    promocaoPFabrica()
    promocaoIpi()
    promocaoEmbalagem()
    promocaoRetido()
    promocaoIcms()
    promocaoFrete()
    promocaoContabil()
    promocaoIcmsSai()
    promocaoFCP()
    promocaoPis()
    promocaoIR()
    promocaoCS()
    promocaoCpmf()
    promocaoDesp()
    promocaoOut()
    promocaoLucro()
    promocaoPSug()
    promocaoPRef()
    promocaoPDif()
  }

  override fun filtro(): FiltroPrecificacao {
    return FiltroPrecificacao(
      codigo = edtCodigo.value ?: 0,
      listVend = edtListVend.value?.split(",")?.mapNotNull { it.toIntOrNull() } ?: emptyList(),
      tributacao = edtTributacao.value ?: "",
      typeno = edtType.value ?: "",
      clno = edtCl.value ?: 0,
      mva = edtMVA.value?.replace(',', '.') ?: "",
      marcaPonto = cmbPontos.value ?: EMarcaPonto.TODOS,
      query = edtQuery.value ?: "",
    )
  }

  override fun listSelected(): List<Precificacao> {
    return itensSelecionados()
  }

  override fun isAuthorized(): Boolean {
    val user = AppConfig.userLogin() as? UserSaci ?: return false
    return user.precificacaoPrecificacao
  }

  override val label: String
    get() = "Precificação"

  override fun updateComponent() {
    viewModel.updateView()
  }
}