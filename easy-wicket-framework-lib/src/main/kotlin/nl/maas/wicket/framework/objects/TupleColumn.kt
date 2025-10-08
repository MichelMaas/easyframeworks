package nl.maas.wicket.framework.objects

import nl.maas.wicket.framework.components.elemental.TooltipLabel
import nl.maas.wicket.framework.services.Translator
import nl.maas.wicket.framework.services.TranslatorPlaceHolder
import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator
import org.apache.wicket.extensions.markup.html.repeater.data.table.AbstractColumn
import org.apache.wicket.extensions.markup.html.repeater.data.table.IStyledColumn
import org.apache.wicket.extensions.markup.html.repeater.data.table.export.IExportableColumn
import org.apache.wicket.markup.repeater.Item
import org.apache.wicket.model.IModel
import org.apache.wicket.model.Model

class TupleColumn(
    val columnName: Model<String>,
    val translator: Translator = TranslatorPlaceHolder(),
    val maxTextLength: Int,
    val translateValue: Boolean = true
) :
    AbstractColumn<Tuple, java.io.Serializable>(columnName),
    IExportableColumn<Tuple, java.io.Serializable>, IStyledColumn<Tuple, java.io.Serializable> {

    override fun getDisplayModel(): IModel<String> {
        return Model.of(translator.translate(columnName.`object`))
    }

    override fun populateItem(cellItem: Item<ICellPopulator<Tuple>>, componentId: String, rowModel: IModel<Tuple>) {
        val raw = rowModel.`object`.getValueForColumn(columnName.`object`).toString()
        val value = if (translateValue) translator.translate(raw) else raw
        cellItem.addOrReplace(TooltipLabel(componentId, value, maxTextLength))
    }

    override fun getDataModel(rowModel: IModel<Tuple>): IModel<*> {
        return Model.of(rowModel.`object`.getValueForColumn(columnName.`object`))
    }
}