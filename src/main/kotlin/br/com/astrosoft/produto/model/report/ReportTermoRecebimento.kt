package br.com.astrosoft.produto.model.report

import br.com.astrosoft.framework.model.reports.Templates
import br.com.astrosoft.framework.model.reports.Templates.fieldFont
import br.com.astrosoft.framework.model.reports.horizontalBlock
import br.com.astrosoft.framework.model.reports.horizontalList
import br.com.astrosoft.framework.model.reports.text
import br.com.astrosoft.framework.model.reports.verticalBlock
import br.com.astrosoft.framework.model.reports.verticalList
import br.com.astrosoft.produto.model.beans.TermoRecebimento
import com.vaadin.copilot.javarewriter.JavaStyleRewriter.setStyle
import net.sf.dynamicreports.jasper.builder.JasperReportBuilder
import net.sf.dynamicreports.report.builder.DynamicReports.*
import net.sf.dynamicreports.report.builder.component.ComponentBuilder
import net.sf.dynamicreports.report.builder.component.HorizontalListBuilder
import net.sf.dynamicreports.report.constant.HorizontalTextAlignment
import net.sf.dynamicreports.report.constant.HorizontalTextAlignment.RIGHT
import net.sf.dynamicreports.report.constant.PageOrientation.PORTRAIT
import net.sf.dynamicreports.report.constant.PageType.A4
import net.sf.dynamicreports.report.constant.StretchType
import net.sf.dynamicreports.report.constant.TextAdjust
import net.sf.jasperreports.engine.export.JRPdfExporter
import net.sf.jasperreports.export.SimpleExporterInput
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput
import okhttp3.internal.http2.StreamResetException
import java.io.ByteArrayOutputStream

class ReportTermoRecebimento(val termo: TermoRecebimento) {
  private fun HorizontalListBuilder.quadro(titulo: String, conteudo: List<String>) {
    this.verticalList {
      this.text(text = titulo, horizontalTextAlignment = HorizontalTextAlignment.CENTER) {
        setStyle(Templates.fieldFontTituloQuadro)
      }
      conteudo.forEach { item ->
        this.text(text = item, horizontalTextAlignment = HorizontalTextAlignment.LEFT) {
          setStyle(Templates.fieldFontQuadro)
          this.setTextAdjust(TextAdjust.SCALE_FONT)
        }
      }
    }
  }

  private fun titleBuiderPedido(): ComponentBuilder<*, *> {
    return verticalBlock {
      this.text(text = "Termo de Recebimento", horizontalTextAlignment = HorizontalTextAlignment.CENTER) {
        setStyle(Templates.fieldFontTitulo)
      }
      val paragrafo = """        Declaramos para devidos fins quer recebemos os volumes dados abaixo da nota fiscal 
        |de origem e Ct-e para posterior conferência sujeito a notificações de irregularidades no recebimento, como: 
        |Avaria no Transporte, Falha no Transporte, Falta de Fabrica, Validade, Defeito de fabricação, 
        |Sem Identificação e Em Desacordo.""".trimMargin().replace("\n", " ")
      this.text("")
      this.text(text = paragrafo, horizontalTextAlignment = HorizontalTextAlignment.JUSTIFIED) {
        setStyle(Templates.fieldFontTermo)
      }
      this.text("")
      this.horizontalList {
        quadro(
          titulo = "Dados do Fornecedor",
          conteudo = listOf(
            termo.dadosFornecedor.nome,
            "CNPJ: ${termo.dadosFornecedor.cnpj}",
            "End: ${termo.dadosFornecedor.endereco}",
            "Bairro: ${termo.dadosFornecedor.bairro}",
            "Cidade: ${termo.dadosFornecedor.cidade}",
            "Estado: ${termo.dadosFornecedor.uf}"
          )
        )
        quadro(
          titulo = "Dados da Transportadora",
          conteudo = listOf(
            termo.dadosTransportadora.nome,
            "CNPJ: ${termo.dadosTransportadora.cnpj}",
            "End: ${termo.dadosTransportadora.endereco}",
            "Bairro: ${termo.dadosTransportadora.bairro}",
            "Cidade: ${termo.dadosTransportadora.cidade}",
            "Estado: ${termo.dadosTransportadora.uf}"
          )
        )
        quadro(
          titulo = "Dados do Cliente",
          conteudo = listOf(
            termo.dadosCliente.nome,
            "CNPJ: ${termo.dadosCliente.cnpj}",
            "End: ${termo.dadosCliente.endereco}",
            "Bairro: ${termo.dadosCliente.bairro}",
            "Cidade: ${termo.dadosCliente.cidade}",
            "Estado: ${termo.dadosCliente.uf}"
          )
        )
      }
    }
  }

  fun makeReport(): JasperReportBuilder? {
    val itens = listOf(termo)
    val pageOrientation = PORTRAIT
    return report()
      .title(titleBuiderPedido())
      .setTemplate(Templates.reportTemplate)
      .setColumnStyle(stl.style().setFontSize(7))
      .setDataSource(itens.toList())
      .setPageFormat(A4, pageOrientation)
      .setPageMargin(margin(28))
      .setSubtotalStyle(stl.style().setFontSize(8).setPadding(2).setTopBorder(stl.pen1Point()))
      .pageFooter(cmp.pageNumber().setHorizontalTextAlignment(RIGHT).setStyle(stl.style().setFontSize(8)))
  }

  companion object {
    fun processaRelatorio(termo: TermoRecebimento): ByteArray? {
      val print = ReportTermoRecebimento(termo).makeReport()?.toJasperPrint() ?: return null
      val exporter = JRPdfExporter()
      val out = ByteArrayOutputStream()
      exporter.setExporterInput(SimpleExporterInput.getInstance(listOf(print)))

      exporter.exporterOutput = SimpleOutputStreamExporterOutput(out)

      exporter.exportReport()
      return out.toByteArray()
    }
  }
}