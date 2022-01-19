package br.com.astrosoft.framework.view

import com.flowingcode.vaadin.addons.fontawesome.FontAwesome
import com.github.mvysny.karibudsl.v10.VaadinDsl
import com.github.mvysny.karibudsl.v10.init
import com.vaadin.flow.component.Component
import com.vaadin.flow.component.HasComponents
import org.vaadin.gatanaso.MultiselectComboBox
import org.vaadin.stefan.LazyDownloadButton
import java.io.ByteArrayInputStream
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@VaadinDsl
fun <T> (@VaadinDsl HasComponents).multiselectComboBox(block: (@VaadinDsl MultiselectComboBox<T>).() -> Unit = {}) =
  init(MultiselectComboBox(), block)

/*
private fun buttonPlanilha(fornecedores: () -> List<Fornecedor>): LazyDownloadButton {
  return LazyDownloadButton("Planilha", FontAwesome.Solid.FILE_EXCEL.create(), ::filename) {
    val notas = fornecedores().flatMap { it.notas }
    ByteArrayInputStream(viewModel.geraPlanilha(notas, serie))
  }
}
 */

@VaadinDsl
fun (@VaadinDsl HasComponents).lazyDownloadButton(
  text: String? = null,
  prefixo: String,
  extensao: String,
  icon: Component? = null,
  byteArray: () -> ByteArray,
): LazyDownloadButton {
  val ldb =
    LazyDownloadButton(
      text,
      icon,
      {
        val sdf = DateTimeFormatter.ofPattern("yyMMddHHmmss")
        val textTime = LocalDateTime.now().format(sdf)
        "$prefixo$textTime.$extensao"
      },
      {
        ByteArrayInputStream(byteArray())
      },
    )
  return init(ldb)
}

@VaadinDsl
fun (@VaadinDsl HasComponents).lazyDownloadButtonXlsx(
  text: String? = null,
  prefixo: String,
  byteArray: () -> ByteArray,
) =
  lazyDownloadButton(text, prefixo, ".xlsx", FontAwesome.Solid.FILE_EXCEL.create(), byteArray)
