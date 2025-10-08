package nl.maas.wicket.framework.components.elemental

import org.apache.wicket.markup.ComponentTag
import org.apache.wicket.markup.MarkupStream
import org.apache.wicket.markup.html.basic.Label

class TitleLabel(id: String, text: String, headerType: HEADER_TYPE = HEADER_TYPE.H1) : Label(id, text) {
    companion object {
        public final enum class HEADER_TYPE {
            H1, H2, H3
        }
    }

    override fun onComponentTagBody(markupStream: MarkupStream, openTag: ComponentTag) {
        super.onComponentTagBody(markupStream, openTag)

    }
}