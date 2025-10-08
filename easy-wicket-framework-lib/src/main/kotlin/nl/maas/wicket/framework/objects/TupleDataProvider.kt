package nl.maas.wicket.framework.objects

import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import nl.maas.wicket.framework.services.Translator
import nl.maas.wicket.framework.services.TranslatorPlaceHolder
import org.apache.wicket.extensions.markup.html.repeater.util.SortableDataProvider
import org.apache.wicket.model.IModel
import org.apache.wicket.model.Model

class TupleDataProvider private constructor(
    private var tuples: List<Tuple>,
    private val translator: Translator = TranslatorPlaceHolder(),
    private val maxTextLength: Int,
    private vararg val translateValues: String = arrayOf()
) :
    SortableDataProvider<Tuple, java.io.Serializable>() {

    companion object {
        fun toTupleDataProvider(
            tuples: List<Tuple>,
            translator: Translator = TranslatorPlaceHolder(),
            maxTextLength: Int,
            vararg translateValues: String = arrayOf()
        ): TupleDataProvider {
            require(tuples.all { it.columns.keys.containsAll(tuples.first().columns.keys) })
            return TupleDataProvider(tuples, translator, maxTextLength, *translateValues)
        }
    }

    val columns
        get() = tuples.firstOrNull()?.columns?.map {
            runBlocking {
                async {
                    TupleColumn(
                        Model.of(it.key),
                        translator,
                        maxTextLength,
                        translateValues.isEmpty() || translateValues.contains(it.key)
                    )
                }.await()
            }
        } ?: listOf(
            TupleColumn(
                Model.of("Nothing"), translator, maxTextLength
            )
        )

    override fun iterator(first: Long, count: Long): MutableIterator<Tuple> =
        tuples.subList(first.toInt(), (first.plus(count)).toInt()).toMutableList().listIterator()

    override fun size(): Long = tuples.size.toLong()

    override fun model(tuple: Tuple): IModel<Tuple> = Model.of(tuple)

    override fun equals(other: Any?): Boolean {
        if (!this::class.java.isInstance(other)) {
            return false
        }
        val odp = other!! as TupleDataProvider
        return tuplesEqual(odp.tuples)
    }

    fun tuplesEqual(tuples: List<Tuple>): Boolean {
        return this.tuples.containsAll(tuples) && tuples.containsAll(this.tuples)
    }

    fun update(tuples: List<Tuple>): TupleDataProvider {
        return toTupleDataProvider(tuples, translator, maxTextLength, *translateValues)
    }
}