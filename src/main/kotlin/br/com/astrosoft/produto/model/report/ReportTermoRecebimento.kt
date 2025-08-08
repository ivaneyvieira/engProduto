package br.com.astrosoft.produto.model.report

import br.com.astrosoft.framework.model.reports.*
import br.com.astrosoft.framework.util.format
import br.com.astrosoft.produto.model.beans.TermoRecebimento
import net.sf.dynamicreports.jasper.builder.JasperReportBuilder
import net.sf.dynamicreports.report.builder.DynamicReports.*
import net.sf.dynamicreports.report.builder.component.ComponentBuilder
import net.sf.dynamicreports.report.builder.component.HorizontalListBuilder
import net.sf.dynamicreports.report.constant.HorizontalTextAlignment
import net.sf.dynamicreports.report.constant.HorizontalTextAlignment.RIGHT
import net.sf.dynamicreports.report.constant.PageOrientation.PORTRAIT
import net.sf.dynamicreports.report.constant.PageType.A4
import net.sf.dynamicreports.report.constant.TextAdjust
import net.sf.jasperreports.engine.export.JRPdfExporter
import net.sf.jasperreports.export.SimpleExporterInput
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput
import java.io.ByteArrayOutputStream

class ReportTermoRecebimento(val termo: TermoRecebimento) {
  private fun HorizontalListBuilder.quadro(titulo: String, conteudo: List<String>) {
    this.verticalList {
      if (titulo.isNotEmpty()) {
        this.text(text = titulo, horizontalTextAlignment = HorizontalTextAlignment.CENTER) {
          setStyle(Templates.fieldFontTituloQuadro)
        }
      } else {
        this.text("")
      }
      if (conteudo.isNotEmpty()) {
        conteudo.forEach { item ->
          this.text(text = item, horizontalTextAlignment = HorizontalTextAlignment.LEFT) {
            setStyle(Templates.fieldFontQuadro)
            this.setTextAdjust(TextAdjust.SCALE_FONT)
          }
        }
      }
    }
  }

  private fun titleBuiderPedido(): ComponentBuilder<*, *> {
    return verticalBlock {
      this.text(text = "Termo de Recebimento", horizontalTextAlignment = HorizontalTextAlignment.CENTER) {
        setStyle(Templates.fieldFontTitulo)
      }
      val paragrafo = """        Declaramos para os devidos fins que recebemos os volumes constante nas notas 
        |fiscais e CT-e informados nos dados fiscais abaixo para conferência posterior e sujeitos as notificações de 
        |irregularidade no recebimento tais como: Produto avaria no transporte, falta de volume e ou falta do produto
        |dentro do volume, produto próximo ao vencimento ou produto vencido, produto com defeito de fabricação, 
        |produto em desacordo com o pedido de compra etc.
""".trimMargin().replace("\n", " ")
      this.text("")
      this.text(text = paragrafo, horizontalTextAlignment = HorizontalTextAlignment.JUSTIFIED) {
        setStyle(Templates.fieldFontTermo)
      }
      this.text("")
      this.text("Dados Cadastrais", horizontalTextAlignment = HorizontalTextAlignment.CENTER) {
        setStyle(Templates.fieldFontTitulo)
      }
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
      this.text("")
      this.text("Dados Fiscais", horizontalTextAlignment = HorizontalTextAlignment.CENTER) {
        setStyle(Templates.fieldFontTitulo)
      }
      this.horizontalList {
        quadro(
          titulo = "Fornecedor",
          conteudo = listOf(
            "Nota Fiscal: ${termo.dadosFornecedor.notaFiscal}",
            "Emissão: ${termo.dadosFornecedor.emissao?.format()}",
            "Recebimento: ${termo.dadosFornecedor.recebimento?.format()}",
            "Valor: R$ ${termo.dadosFornecedor.valor.format()}",
            "Volumes: ${termo.volumesRec.format()}",
            "Peso Bruto: ${termo.pesoBruto.format()} kg"
          )
        )
        quadro(
          titulo = "Transportadora",
          conteudo = listOf(
            "CT-e: ${termo.dadosTransportadora.cte}",
            "Emissão: ${termo.dadosTransportadora.emissao?.format()}",
            "Recebimento: ${termo.dadosTransportadora.recebimento?.format()}",
            "Valor: R$ ${termo.dadosTransportadora.valor.format()}",
            "Volumes: ${termo.volumesRec.format()}",
            "Peso Bruto: ${termo.pesoBruto.format()} kg"
          )
        )
        quadro(titulo = "", conteudo = listOf())
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