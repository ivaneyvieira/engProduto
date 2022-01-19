package br.com.astrosoft.framework.view

import com.vaadin.flow.component.Component
import com.vaadin.flow.component.HasComponents
import com.vaadin.flow.component.html.Div
import com.vaadin.flow.component.html.Image
import com.vaadin.flow.server.InputStreamFactory
import com.vaadin.flow.server.StreamResource
import java.io.ByteArrayInputStream
import java.io.IOException
import javax.imageio.ImageIO

private fun createComponent(fileName: String, byteArray: ByteArray): Component {
  when {
    fileName.endsWith("pdf", ignoreCase = true) -> {
      val resourcePDF = StreamResource(fileName, ConverteByte(byteArray))
      return PDFViewer(resourcePDF)
    }

    fileName.endsWith("jpg", ignoreCase = true) || fileName.endsWith(
      "jpeg",
      ignoreCase = true
    ) || fileName.endsWith(
      "png",
      ignoreCase = true
    ) -> {
      val image = Image()
      try {
        image.element.setAttribute("src",
          StreamResource(fileName, InputStreamFactory { ByteArrayInputStream(byteArray) })
        )
        ImageIO.createImageInputStream(ByteArrayInputStream(byteArray)).use { `in` ->
          val readers = ImageIO.getImageReaders(`in`)
          if (readers.hasNext()) {
            val reader = readers.next()
            try {
              reader.input = `in`
              image.width = reader.getWidth(0).toString() + "px"
              image.height = reader.getHeight(0).toString() + "px"
            } finally {
              reader.dispose()
            }
          }
        }
      } catch (e: IOException) {
        e.printStackTrace()
      }
      return image
    }

    else -> {
      return Div()
    }
  }
}

fun HasComponents.showOutput(fileName: String, byteArray: ByteArray) {
  val component = createComponent(fileName, byteArray)
  showOutput(component)
}

fun HasComponents.showOutput(content: Component) {
  this.add(content)
}