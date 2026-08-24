package br.com.astrosoft.produto.view.precificacao

import br.com.astrosoft.framework.model.config.AppConfig
import br.com.astrosoft.framework.view.vaadin.TabPanelGrid
import br.com.astrosoft.framework.view.vaadin.helper.addColumnSeq
import br.com.astrosoft.framework.view.vaadin.helper.columnGrid
import br.com.astrosoft.framework.view.vaadin.helper.columnGroup
import br.com.astrosoft.produto.model.beans.DadosPrecificacao
import br.com.astrosoft.produto.model.beans.ECampoPrecificacao
import br.com.astrosoft.produto.model.beans.ELojaProcificcao
import br.com.astrosoft.produto.model.beans.EOperacaoPrecificacao
import br.com.astrosoft.produto.model.beans.FiltroDadosPrecificacao
import br.com.astrosoft.produto.model.beans.FiltroValoresPrecificacao
import br.com.astrosoft.produto.model.beans.UserSaci
import br.com.astrosoft.produto.viewmodel.precificacao.ITabPrecificacaoDadosViewModel
import br.com.astrosoft.produto.viewmodel.precificacao.TabPrecificacaoDadosViewModel
import com.github.mvysny.karibudsl.v10.select
import com.github.mvysny.karibudsl.v10.textField
import com.vaadin.flow.component.grid.Grid
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.component.select.Select
import com.vaadin.flow.component.textfield.TextField
import com.vaadin.flow.data.value.ValueChangeMode
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class TabPrecificacaoDados(val viewModel: TabPrecificacaoDadosViewModel) :
  TabPanelGrid<DadosPrecificacao>(DadosPrecificacao::class),
  ITabPrecificacaoDadosViewModel {

  private lateinit var edtQuery: TextField
  private lateinit var selectCampo: Select<ECampoPrecificacao>
  private lateinit var selectLojaRef: Select<ELojaProcificcao>
  private lateinit var selectOper: Select<EOperacaoPrecificacao>
  private lateinit var selectLoja: Select<ELojaProcificcao>

  override fun HorizontalLayout.toolBarConfig() {
    edtQuery = textField("Pesquisa") {
      this.valueChangeMode = ValueChangeMode.LAZY
      addValueChangeListener {
        viewModel.updateView()
      }
    }

    selectCampo = select("Compra") {
      this.setItems(ECampoPrecificacao.entries)
      this.value = ECampoPrecificacao.PRECO
      this.setItemLabelGenerator {campo->
        campo.descricao
      }
      this.addValueChangeListener {
        viewModel.updateView()
      }
    }

    selectLojaRef = select("Loja Ref") {
      this.setItems(ELojaProcificcao.entries)
      this.value = ELojaProcificcao.TODAS
      this.setItemLabelGenerator {campo->
        campo.sigla
      }
      this.addValueChangeListener {
        viewModel.updateView()
      }
    }

    selectOper = select("Situação") {
      this.setItems(EOperacaoPrecificacao.entries)
      this.value = EOperacaoPrecificacao.IGUAL
      this.setItemLabelGenerator {campo->
        campo.oper
      }
      this.addValueChangeListener {
        viewModel.updateView()
      }
    }

    selectLoja = select("Loja") {
      this.setItems(ELojaProcificcao.entries)
      this.value = ELojaProcificcao.TODAS
      this.setItemLabelGenerator {campo->
        campo.sigla
      }
      this.addValueChangeListener {
        viewModel.updateView()
      }
    }
  }

  private fun filename(): String {
    val sdf = DateTimeFormatter.ofPattern("yyMMddHHmmss")
    val textTime = LocalDateTime.now().format(sdf)
    return "precificacao$textTime.xlsx"
  }

  override fun Grid<DadosPrecificacao>.gridPanel() {
    selectionMode = Grid.SelectionMode.MULTI

    columnGroup("Produto") {
      this.addColumnSeq("Seq")
      columnGrid(DadosPrecificacao::codigo, "Código")
      columnGrid(DadosPrecificacao::descricao, "Descrição")
      columnGrid(DadosPrecificacao::taxno, "Trib")
    }

    columnGroup("Preço Fabrica") {
      columnGrid(DadosPrecificacao::precoFabrica10, "ADM")
      columnGrid(DadosPrecificacao::precoFabrica04, "MF")
      columnGrid(DadosPrecificacao::precoFabrica05, "PK")
      columnGrid(DadosPrecificacao::precoFabrica03, "MR")
      columnGrid(DadosPrecificacao::precoFabrica02, "DS")
      columnGrid(DadosPrecificacao::precoFabrica08, "TM")
    }

    columnGroup("IPI") {
      columnGrid(DadosPrecificacao::percentualIPI10, "ADM")
      columnGrid(DadosPrecificacao::percentualIPI04, "MF")
      columnGrid(DadosPrecificacao::percentualIPI05, "PK")
      columnGrid(DadosPrecificacao::percentualIPI03, "MR")
      columnGrid(DadosPrecificacao::percentualIPI02, "DS")
      columnGrid(DadosPrecificacao::percentualIPI08, "TM")
    }

    columnGroup("Crédito de ICMS") {
      columnGrid(DadosPrecificacao::creditoICMS10, "ADM")
      columnGrid(DadosPrecificacao::creditoICMS04, "MF")
      columnGrid(DadosPrecificacao::creditoICMS05, "PK")
      columnGrid(DadosPrecificacao::creditoICMS03, "MR")
      columnGrid(DadosPrecificacao::creditoICMS02, "DS")
      columnGrid(DadosPrecificacao::creditoICMS08, "TM")
    }

    columnGroup("Imposto Retido") {
      columnGrid(DadosPrecificacao::retido10, "ADM")
      columnGrid(DadosPrecificacao::retido04, "MF")
      columnGrid(DadosPrecificacao::retido05, "PK")
      columnGrid(DadosPrecificacao::retido03, "MR")
      columnGrid(DadosPrecificacao::retido02, "DS")
      columnGrid(DadosPrecificacao::retido08, "TM")
    }

    columnGroup("Custo Contabil") {
      columnGrid(DadosPrecificacao::custoContabil10, "ADM")
      columnGrid(DadosPrecificacao::custoContabil04, "MF")
      columnGrid(DadosPrecificacao::custoContabil05, "PK")
      columnGrid(DadosPrecificacao::custoContabil03, "MR")
      columnGrid(DadosPrecificacao::custoContabil02, "DS")
      columnGrid(DadosPrecificacao::custoContabil08, "TM")
    }

    columnGroup("Crédito Pis/Confin") {
      columnGrid(DadosPrecificacao::creditoPisCofins10, "ADM")
      columnGrid(DadosPrecificacao::creditoPisCofins04, "MF")
      columnGrid(DadosPrecificacao::creditoPisCofins05, "PK")
      columnGrid(DadosPrecificacao::creditoPisCofins03, "MR")
      columnGrid(DadosPrecificacao::creditoPisCofins02, "DS")
      columnGrid(DadosPrecificacao::creditoPisCofins08, "TM")
    }

    columnGroup("Frete Deduzido") {
      columnGrid(DadosPrecificacao::frete10, "ADM")
      columnGrid(DadosPrecificacao::frete04, "MF")
      columnGrid(DadosPrecificacao::frete05, "PK")
      columnGrid(DadosPrecificacao::frete03, "MR")
      columnGrid(DadosPrecificacao::frete02, "DS")
      columnGrid(DadosPrecificacao::frete08, "TM")
    }
  }

  override fun filtro(): FiltroDadosPrecificacao {
    return FiltroDadosPrecificacao(
      pesquisa = edtQuery.value ?: "",
    )
  }

  override fun filtroValores(): FiltroValoresPrecificacao {
    return FiltroValoresPrecificacao(
      lojaRef = selectLojaRef.value ?: ELojaProcificcao.TODAS,
      loja = selectLoja.value ?: ELojaProcificcao.TODAS,
      campo = selectCampo.value ?: ECampoPrecificacao.PRECO,
      operacao = selectOper.value ?: EOperacaoPrecificacao.IGUAL,
    )
  }

  override fun isAuthorized(): Boolean {
    val user = AppConfig.userLogin() as? UserSaci ?: return false
    return user.precificacaoDados
  }

  override val label: String
    get() = "Precificação Dados"

  override fun updateComponent() {
    viewModel.updateView()
  }
}