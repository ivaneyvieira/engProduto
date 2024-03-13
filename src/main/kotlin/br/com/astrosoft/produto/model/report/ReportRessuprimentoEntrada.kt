package br.com.astrosoft.produto.model.report

import br.com.astrosoft.framework.model.reports.PropriedadeRelatorio
import br.com.astrosoft.framework.model.reports.ReportBuild
import br.com.astrosoft.produto.model.beans.ProdutoRessuprimento
import net.sf.dynamicreports.report.builder.DynamicReports.grid
import net.sf.dynamicreports.report.constant.HorizontalTextAlignment
import net.sf.dynamicreports.report.constant.PageOrientation

class ReportRessuprimentoEntrada(private val ressuprimentoTitle: String) : ReportBuild<ProdutoRessuprimento>() {

  init {

    val grupoDados = grid.titleGroup(
      "Dados da Nota",
      columnReport(ProdutoRessuprimento::codigo, "Código", width = 40, aligment = HorizontalTextAlignment.RIGHT),
      columnReport(ProdutoRessuprimento::descricao, "Descrição"){
        this.scaleFont()
      },
      columnReport(ProdutoRessuprimento::grade, "Grade", width = 50){
        this.scaleFont()
      },
      columnReport(ProdutoRessuprimento::qtQuantNF, "Qnt NF", width = 45)
    )

    val grupoRecebimento = grid.titleGroup(
      "Dados do Recebimento",
      columnReport(ProdutoRessuprimento::qtRecebido, "Recebido", width = 45),
      columnReport(ProdutoRessuprimento::codigoCorrecao, "Código", width = 45, aligment = HorizontalTextAlignment.RIGHT),
      columnReport(ProdutoRessuprimento::descricaoCorrecao, "Descrição"){
        this.scaleFont()
      },
      columnReport(ProdutoRessuprimento::gradeCorrecao, "Grade", width = 50){
        this.scaleFont()
      },
      columnReport(ProdutoRessuprimento::qtEntregue, "Entregue", width = 45)
    )

    addGrupo(grupoDados)
    addGrupo(grupoRecebimento)
  }

  override fun config(itens: List<ProdutoRessuprimento>): PropriedadeRelatorio {
    return PropriedadeRelatorio(
      titulo = "Relatório com Divergência no Recebimento do Ressuprimento da $ressuprimentoTitle",
      subTitulo = "",
      detailFonteSize = 8,
      pageOrientation = PageOrientation.PORTRAIT,
      margem = 10
    )
  }
}