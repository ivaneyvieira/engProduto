package br.com.astrosoft.framework.model.reports

import net.sf.dynamicreports.report.builder.DynamicReports.cmp
import net.sf.dynamicreports.report.builder.component.HorizontalListBuilder
import net.sf.dynamicreports.report.builder.component.TextFieldBuilder
import net.sf.dynamicreports.report.builder.component.VerticalListBuilder
import net.sf.dynamicreports.report.constant.HorizontalTextAlignment
import net.sf.dynamicreports.report.constant.HorizontalTextAlignment.LEFT

fun horizontalBlock(block: (HorizontalListBuilder) -> HorizontalListBuilder): HorizontalListBuilder {
  val hrizontal = cmp.horizontalFlowList()
  block(hrizontal)
  return hrizontal
}

fun verticalBlock(block: VerticalListBuilder.() -> Unit): VerticalListBuilder {
  val vertical = cmp.verticalList()
  block(vertical)
  return vertical
}

fun VerticalListBuilder.horizontalList(block: HorizontalListBuilder.() -> Unit) {
  val horizontal = cmp.horizontalList()
  block(horizontal)
  this.add(horizontal)
}

fun VerticalListBuilder.verticalList(block: VerticalListBuilder.() -> Unit) {
  val vertical = cmp.verticalList()
  block(vertical)
  this.add(vertical)
}

fun HorizontalListBuilder.horizontalList(block: HorizontalListBuilder.() -> Unit) {
  val horizontal = cmp.horizontalList()
  block(horizontal)
  this.add(horizontal)
}

fun HorizontalListBuilder.verticalList(block: VerticalListBuilder.() -> Unit) {
  val vertical = cmp.verticalList()
  block(vertical)
  this.add(vertical)
}

fun VerticalListBuilder.breakLine() {
  this.add(cmp.text(""))
}

fun HorizontalListBuilder.text(text: String,
                               horizontalTextAlignment: HorizontalTextAlignment = LEFT,
                               width: Int = 0,
                               block: TextFieldBuilder<String>.() -> Unit = {}): TextFieldBuilder<String> {
  val textString = cmp.text(text).setHorizontalTextAlignment(horizontalTextAlignment)
  if (width > 0) textString.setFixedWidth(width)
  textString.block()
  this.add(textString)
  return textString
}

fun VerticalListBuilder.text(text: String,
                             horizontalTextAlignment: HorizontalTextAlignment = LEFT,
                             width: Int = 0,
                             block: TextFieldBuilder<String>.() -> Unit = {}): TextFieldBuilder<String> {
  val textString = cmp.text(text).setHorizontalTextAlignment(horizontalTextAlignment)
  if (width > 0) textString.setFixedWidth(width)
  textString.block()
  this.add(textString)
  return textString
}