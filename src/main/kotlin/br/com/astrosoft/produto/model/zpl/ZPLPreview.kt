package br.com.astrosoft.produto.model.zpl

import com.github.mvysny.karibudsl.v10.icon
import com.vaadin.flow.component.html.IFrame
import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import com.vaadin.flow.component.select.Select
import com.vaadin.flow.server.InputStreamFactory
import com.vaadin.flow.server.StreamResource
import com.vaadin.flow.server.VaadinSession
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import org.claspina.confirmdialog.ButtonOption
import org.claspina.confirmdialog.ConfirmDialog
import java.io.ByteArrayInputStream
import java.io.InputStream

object ZPLPreview {
  fun createPdf(zpl: String, size: String): ByteArray? {
    val url = "http://api.labelary.com/v1/printers/8dpmm/labels/$size/"
    val client: OkHttpClient = OkHttpClient.Builder().build()
    val body = zpl.toRequestBody()
    val request: Request = Request.Builder().addHeader("Accept", "application/pdf").url(url).post(body).build()
    val response: Response = client.newCall(request).execute()

    return if (response.isSuccessful) response.body?.bytes() else null
  }

  fun showZPLPreview(impressora: Set<String>, zplCode: String, printRunnable: (impressoras: List<String>) -> Unit) {
    val image = createPdf(zplCode, "3x1")
    if (image != null) showImage(impressora, image, printRunnable)
  }

  private fun showImage(impressoras: Set<String>, image: ByteArray, printRunnable: (impressoras: List<String>) -> Unit) {
    val filename = "etiqueta${System.currentTimeMillis()}.pdf"
    val resource = StreamResource(filename, InputStreamFactoryImage(image))
    val registration = VaadinSession.getCurrent().resourceRegistry.registerResource(resource)

    val cmbPrint = Select<String>().apply {
      this.label = "Impressora"
      this.icon(VaadinIcon.PRINT)
      this.setWidthFull()
      setItems(impressoras)
      this.value = impressoras.firstOrNull()
    }

    val embedded = IFrame(registration.resourceUri.toString())
    val form = VerticalLayout().apply {
      setSizeFull()
      add(cmbPrint)
      addAndExpand(embedded)
    }

    embedded.setSizeFull()
    ConfirmDialog.create()
      .withMessage(form)
      .withCaption("Impressão (${impressoras.joinToString(", ")})")
      .withNoButton({
        val printer = cmbPrint.value
        printRunnable(listOf(printer))
      }, ButtonOption.caption("Imprimir"), ButtonOption.icon(VaadinIcon.PRINT))
      .withCancelButton(ButtonOption.caption("Cancelar"))
      .open()
  }
}

class InputStreamFactoryImage(val image: ByteArray) : InputStreamFactory {
  override fun createInputStream(): InputStream {
    return ByteArrayInputStream(image)
  }
}