package br.com.astrosoft.produto.model.zpl

import com.vaadin.flow.component.html.IFrame
import com.vaadin.flow.component.icon.VaadinIcon
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

object ZPLPreview {/*
  fun createPdf(zpl: String, size: String): ByteArray? {
    val uri = URI.create("http://api.labelary.com/v1/printers/8dpmm/labels/$size/")
    val request =
            HttpRequest.newBuilder(uri)
              .header("Accept", "application/pdf")
              .POST(HttpRequest.BodyPublishers.ofString(zpl))
              .build()
    val client = HttpClient.newHttpClient()
    val response = client.send(request, HttpResponse.BodyHandlers.ofByteArray())

    return if (response.statusCode() == 200) {
      val body : ByteArray? = response.body()
      body
    }
    else {
      null
    }
  }
*/

  fun createPdf(zpl: String, size: String): ByteArray? {
    val url = "http://api.labelary.com/v1/printers/8dpmm/labels/$size/"
    val client: OkHttpClient = OkHttpClient.Builder().build()
    val body = zpl.toRequestBody()
    val request: Request = Request.Builder().addHeader("Accept", "application/pdf").url(url).post(body).build()
    val response: Response = client.newCall(request).execute()

    return if (response.isSuccessful) response.body?.bytes() else null
  }

  fun showZPLPreview(impressora: String, zplCode: String, printRunnable: () -> Unit) {
    val image = ZPLPreview.createPdf(zplCode, "3x1")
    if (image != null) showImage(impressora, image, printRunnable)
  }

  fun showImage(impressora: String, image: ByteArray, printRunnable: () -> Unit) {

    val filename = "etiqueta${System.currentTimeMillis()}.pdf"
    val resource = StreamResource(filename, InputStreamFactoryImage(image))
    val registration = VaadinSession.getCurrent().resourceRegistry.registerResource(resource)

    val embedded = IFrame(registration.resourceUri.toString())
    embedded.setSizeFull()
    ConfirmDialog.create()
      .withMessage(embedded)
      .withCaption("Impressão ($impressora)")
      .withNoButton(printRunnable, ButtonOption.caption("Imprimir"), ButtonOption.icon(VaadinIcon.PRINT))
      .withCancelButton(ButtonOption.caption("Cancelar"))
      .open()
  }
}

class InputStreamFactoryImage(val image: ByteArray) : InputStreamFactory {
  override fun createInputStream(): InputStream {
    return ByteArrayInputStream(image)
  }
}