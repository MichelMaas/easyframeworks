package nl.maas.wicket.framework.components.base

import de.agilecoders.wicket.extensions.markup.html.bootstrap.form.fileinput.BootstrapFileInputField
import de.agilecoders.wicket.extensions.markup.html.bootstrap.form.fileinput.FileInputConfig
import de.agilecoders.wicket.extensions.markup.html.bootstrap.form.select.BootstrapSelect
import nl.maas.wicket.framework.components.elemental.SimpleAjaxButton
import nl.maas.wicket.framework.objects.EnumChoiceRenderer
import nl.maas.wicket.framework.objects.behavior.FormValueTransformer
import nl.maas.wicket.framework.objects.behavior.JSScriptsLoader
import nl.maas.wicket.framework.panels.AbstractPanel
import nl.maas.wicket.framework.services.Translator
import nl.maas.wicket.framework.services.TranslatorPlaceHolder
import org.apache.commons.lang3.StringUtils
import org.apache.wicket.Component
import org.apache.wicket.MarkupContainer
import org.apache.wicket.ajax.AjaxEventBehavior
import org.apache.wicket.ajax.AjaxRequestTarget
import org.apache.wicket.ajax.form.AjaxFormSubmitBehavior
import org.apache.wicket.ajax.form.OnChangeAjaxBehavior
import org.apache.wicket.markup.head.IHeaderResponse
import org.apache.wicket.markup.html.basic.Label
import org.apache.wicket.markup.html.form.CheckBox
import org.apache.wicket.markup.html.form.ChoiceRenderer
import org.apache.wicket.markup.html.form.Form
import org.apache.wicket.markup.html.form.TextField
import org.apache.wicket.markup.html.form.upload.FileUpload
import org.apache.wicket.markup.html.panel.Fragment
import org.apache.wicket.markup.repeater.RepeatingView
import org.apache.wicket.model.CompoundPropertyModel
import org.apache.wicket.model.IComponentInheritedModel
import org.apache.wicket.model.IModel
import org.apache.wicket.model.Model
import java.io.Serializable
import java.math.BigDecimal
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import kotlin.reflect.KClass
import kotlin.reflect.KMutableProperty
import kotlin.reflect.KProperty1
import kotlin.reflect.javaType

open class DynamicFormComponent<T>(
    id: String,
    val formTitle: String,
    model: IComponentInheritedModel<T>,
    val translator: Translator = TranslatorPlaceHolder(),
    vararg val refreshTargets: Component
) :
    AbstractPanel(id, model) {
    private val formComponents = RepeatingView("fragments")
    private val form = InnerForm(model)
    val changedProperties = mutableListOf<String>()
    var showButons = true

    init {
        outputMarkupPlaceholderTag = true
    }

    fun isMultiPart(value: Boolean) {
        form.isMultiPart = value
    }

    fun isMultiPart(): Boolean {
        return form.isMultiPart
    }

    override fun onBeforeRender() {
        super.onBeforeRender()
        form.addOrReplace(Label("formTitle", Model.of(translator.translate(formTitle))))
        form.addOrReplace(formComponents)
        addOrReplace(form)
    }

    fun addPlainText(id: String, label: String, value: IComponentInheritedModel<String>): DynamicFormComponent<T> {
        formComponents.add(TextFragment(id, label, value))
        return this
    }

    fun addTextBox(id: String, label: String): DynamicFormComponent<T> {
        formComponents.add(TextBoxFragment<Serializable>(id, label))
        return this
    }

    fun <K : Serializable> addTextBox(
        id: String,
        label: String,
        clazz: KClass<K> = String::class as KClass<K>,
        transformer: FormValueTransformer<K>? = null,
        mask: MaskedTextField.Companion.MASKS = MaskedTextField.Companion.MASKS.NONE,
        customMask: String = ""
    ): DynamicFormComponent<T> {
        formComponents.add(MaskedTextBoxFragment<K>(id, label, transformer, mask, customMask))
        return this
    }

    fun <K : Serializable> addTextBox(
        id: String,
        label: String,
        transformer: FormValueTransformer<K>,
    ): DynamicFormComponent<T> {
        formComponents.add(MaskedTextBoxFragment<K>(id, label, transformer))
        return this
    }

    fun addDateTextBox(id: String, label: String, dateFormat: String): DynamicFormComponent<T> {
        formComponents.add(DateTextBoxFragment(id, label, dateFormat))
        return this
    }

    fun <M : Serializable> addSelect(
        id: String,
        label: String,
        options: List<M>,
        default: M
    ): DynamicFormComponent<T> {
        formComponents.addOrReplace(SelectFragment(id, label, Model.ofList(options), default))
        return this
    }

    fun <M : Enum<M>> addSelect(
        id: String,
        label: String,
        options: List<M>,
        default: M
    ): DynamicFormComponent<T> {
        formComponents.addOrReplace(EnumSelectFragment(id, label, Model.ofList(options), default))
        return this
    }

    fun addFileUploadField(
        id: String,
        label: String = "File Upload",
        placeholder: String = "Select file",
        browseLabel: String = "Browse",
        cancelLabel: String = "Cancel",
        uploadLabel: String = "Upload",
        removeLabel: String = "Remove"
    ): DynamicFormComponent<T> {
        formComponents.add(
            FileUploadFragment(
                id,
                label,
                placeholder,
                browseLabel,
                cancelLabel,
                uploadLabel,
                removeLabel
            )
        )
        isMultiPart(true)
        return this
    }

    fun addCheckbox(
        id: String,
        label: String,
        default: Boolean
    ): DynamicFormComponent<T> {
        formComponents.add(CheckBoxFragment(id, label, default))
        return this
    }

    fun addSwitch(
        id: String,
        label: String,
        default: Boolean = true
    ): DynamicFormComponent<T> {
        formComponents.add(SwitchFragment(id, label, default))
        return this
    }

    fun toggleEnabledFor(vararg toggle: Pair<String, Boolean>) {
        formComponents.filter { fc -> toggle.any { tg -> fc.id.startsWith(tg.first) } }
            .map { fc -> toggle.first { tg -> fc.id.startsWith(tg.first) } to fc }
            .forEach { it.second.isEnabled = it.first.second }
    }

    fun toggleVisibleFor(vararg toggle: Pair<String, Boolean>) {
        formComponents.filter { fc -> toggle.any { tg -> fc.id.startsWith(tg.first) } }
            .map { fc -> toggle.first { tg -> fc.id.startsWith(tg.first) } to fc }
            .forEach { it.second.isVisible = it.first.second }
    }

    open fun onSubmit(target: AjaxRequestTarget, typedModelObject: T) {
    }

    open fun onAfterSubmit(target: AjaxRequestTarget, typedModelObject: T) {
    }

    open fun onSubmitCompleted(target: AjaxRequestTarget, typedModelObject: T) {

    }

    open fun onBeforeCancel(target: AjaxRequestTarget, typedModelObject: T) {
    }

    open fun onUpdate(typedModelObject: T) {}

    open fun onAfterCancel(target: AjaxRequestTarget, typedModelObject: T) {
    }

    open fun <M> onSelectChanged(propertyName: String, value: M, target: AjaxRequestTarget) {
    }

    open fun onSwitchToggled(propertyName: String, switch: Boolean, target: AjaxRequestTarget) {
    }

    open fun onFileUpload(target: AjaxRequestTarget, fileUpload: FileUpload) {}

    private fun <T : Any> determineClass(propertyName: String): KClass<T> {
        return form.modelObject!!::class.members.filterIsInstance<KMutableProperty<*>>()
            .first { propertyName.equals(it.name) }.getter.returnType.classifier as KClass<T>
    }

    private inner class TextFragment(
        propertyName: String,
        val label: String,
        val value: IComponentInheritedModel<String>
    ) :
        ResettableFormFragment<String>(
            "${propertyName}-${formComponents.newChildId()}",
            "textFragment",
            this,
            value.`object`
        ) {

        override fun onBeforeRender() {
            super.onBeforeRender()
            addOrReplace(Label("text", value))
            addOrReplace(Label("textLabel", Model.of(translator.translate(label))))
        }

        override fun onReset(target: AjaxRequestTarget, originalValue: String?) {
            defaultModelObject = originalValue
            target.add(this)
        }
    }

    private inner class CheckBoxFragment(
        val propertyName: String,
        val label: String,
        val default: Boolean
    ) : ResettableFormFragment<Boolean>(
        "${propertyName}-${formComponents.newChildId()}",
        "checkBoxFragment",
        this,
        this@DynamicFormComponent.readInstanceProperty(form.modelObject, propertyName).toBoolean()
    ) {
        override fun onReset(target: AjaxRequestTarget, originalValue: Boolean?) {
            defaultModelObject = originalValue
            target.add(this)
        }

        override fun onBeforeRender() {
            super.onBeforeRender()
            val m = readInstanceProperty(form.modelObject, propertyName).toBoolean()
            val checkBox = object : CheckBox("checkBox", CompoundPropertyModel.of(m)) {
                override fun onModelChanged() {
                    super.onModelChanged()
                    form.modelObject::class.members.filterIsInstance<KMutableProperty<*>>()
                        .find { propertyName.equals(it.name) }?.let {
                            it.setter.call(form.modelObject, modelObject)
                        }
                    changedProperties.add(propertyName)
                }
            }
//            checkBox.label = Model.of(translator.translate(label))
            addOrReplace(checkBox, Label("label", translator.translate(label)))
        }

    }

    private inner class SwitchFragment(
        val propertyName: String,
        val label: String,
        val default: Boolean
    ) : ResettableFormFragment<Boolean>(
        "${propertyName}-${formComponents.newChildId()}",
        "switchFragment",
        this,
        this@DynamicFormComponent.readInstanceProperty(form.modelObject, propertyName).toBoolean()
    ) {
        override fun onReset(target: AjaxRequestTarget, originalValue: Boolean?) {
            defaultModelObject = originalValue
            target.add(this)
        }

        override fun onBeforeRender() {
            super.onBeforeRender()
            val m = readInstanceProperty(form.modelObject, propertyName).toBoolean()
            val checkBox = object : Switch("switch", CompoundPropertyModel.of(m), label) {
                override fun onUpdate(target: AjaxRequestTarget, modelObject: Boolean) {
                    super.onUpdate(target, modelObject)
                    form.modelObject!!::class.members.filterIsInstance<KMutableProperty<*>>()
                        .find { propertyName.equals(it.name) }?.let {
                            it.setter.call(form.modelObject, modelObject)
                        }
                    changedProperties.add(propertyName)
                    onSwitchToggled(propertyName, modelObject, target)
                }
            }
            addOrReplace(checkBox)
        }

    }

    private inner class TextBoxFragment<M : Serializable>(
        val propertyName: String,
        val label: String,
        val dateFormat: String = "",
    ) :
        ResettableFormFragment<M>(
            "${propertyName}-${formComponents.newChildId()}",
            "textBoxFragment",
            this,
            this@DynamicFormComponent.readInstanceProperty(form.modelObject, propertyName) as M
        ) {
        override fun onBeforeRender() {
            super.onBeforeRender()
            val m = readInstanceProperty(form.modelObject, propertyName)
            addOrReplace(object : TextField<String>("textBox", CompoundPropertyModel.of(m)) {
                override fun onModelChanged() {
                    super.onModelChanged()
                    form.modelObject::class.members.filterIsInstance<KMutableProperty<*>>()
                        .find { propertyName.equals(it.name) }?.let {
                            if (String::class.isInstance(it.getter.call(form.modelObject))) {
                                it.setter.call(form.modelObject, modelObject ?: StringUtils.EMPTY)
                            } else if (BigDecimal::class.isInstance(it.getter.call(form.modelObject))) {
                                it.setter.call(form.modelObject, BigDecimal(modelObject ?: "0"))
                            } else if (LocalDate::class.isInstance(it.getter.call(form.modelObject))) {
                                it.setter.call(
                                    form.modelObject,
                                    LocalDate.parse(modelObject, DateTimeFormatter.ofPattern(dateFormat))
                                )
                            }
                        }
                    changedProperties.add(propertyName)
                }

            })
            addOrReplace(Label("textBoxLabel", translator.translate(label)))
        }

        override fun onReset(target: AjaxRequestTarget, originalValue: M?) {
            defaultModelObject = originalValue
            target.add(this)
        }

    }

    private inner class MaskedTextBoxFragment<M : Serializable>(
        val propertyName: String,
        val label: String,
        val transformer: FormValueTransformer<M>? = null,
        val mask: MaskedTextField.Companion.MASKS = MaskedTextField.Companion.MASKS.NONE,
        val customMask: String = ""
    ) :
        ResettableFormFragment<M>(
            "${propertyName}-${formComponents.newChildId()}",
            "textBoxFragment",
            this,
            if (transformer == null) this@DynamicFormComponent.readInstanceProperty(
                form.modelObject,
                propertyName
            ) as M else
                transformer.fromString(
                    this@DynamicFormComponent.readInstanceProperty(
                        form.modelObject,
                        propertyName
                    )
                )
        ) {
        override fun onBeforeRender() {
            super.onBeforeRender()
            val m = convertedValue(
                determineClass(propertyName),
                readInstanceProperty(form.modelObject, propertyName),
                transformer,
                customMask
            ) as M

            addOrReplace(object :
                MaskedTextField<M>("textBox", m, mask) {
                override fun onModelChanged() {
                    super.onModelChanged()
                    form.modelObject::class.members.filterIsInstance<KMutableProperty<*>>()
                        .find { propertyName.equals(it.name) }?.let {
                            it.setter.call(
                                form.modelObject,
                                typedModelObject
                            )
                        }
                    changedProperties.add(propertyName)
                }

                override val typedModelObject: M
                    get() = modelObject as M

                override fun convertInput() {
                    convertedInput = convertedValue(determineClass(propertyName), input, transformer, customMask)
                }


            })
            addOrReplace(Label("textBoxLabel", translator.translate(label)))
        }

        override fun onReset(target: AjaxRequestTarget, originalValue: M?) {
            defaultModelObject = originalValue
            target.add(this)
        }

    }

    private inner class DateTextBoxFragment(
        val propertyName: String,
        val label: String,
        val mask: String = ""
    ) :
        ResettableFormFragment<LocalDate>(
            "${propertyName}-${formComponents.newChildId()}",
            "textBoxFragment",
            this,
            LocalDate.parse(
                this@DynamicFormComponent.readInstanceProperty(form.modelObject, propertyName),
                DateTimeFormatter.ofPattern(mask)
            )
        ) {
        override fun onBeforeRender() {
            super.onBeforeRender()
            val m = readInstanceProperty(form.modelObject, propertyName)
            val propertyKClass = LocalDate::class
            addOrReplace(object :
                DateTextField("textBox", convertedValue(propertyKClass, m, mask = mask), mask) {
                override fun onModelChanged() {
                    super.onModelChanged()
                    form.modelObject::class.members.filterIsInstance<KMutableProperty<*>>()
                        .find { propertyName.equals(it.name) }?.let {
                            it.setter.call(
                                form.modelObject,
                                typedModelObject
                            )
                        }
                    changedProperties.add(propertyName)
                }
            })
            addOrReplace(Label("textBoxLabel", translator.translate(label)))
        }

        override fun onReset(target: AjaxRequestTarget, originalValue: LocalDate?) {
            defaultModelObject = originalValue
            target.add(this)
        }

    }

    private fun <T : Any> convertedValue(
        modelClass: KClass<T>,
        modelObject: String,
        transformer: FormValueTransformer<*>? = null,
        mask: String = ""
    ): T {
        if (transformer != null) {
            return transformer.fromString(modelObject) as T
        } else {
            when (modelClass) {
                BigDecimal::class ->
                    return BigDecimal(modelObject) as T

                LocalDate::class ->
                    return LocalDate.parse(modelObject, DateTimeFormatter.ofPattern(mask)) as T

                else ->
                    return modelObject as T

            }
        }
    }

    private inner class FileUploadFragment(
        propertyName: String,
        val fieldLabel: String,
        val placeholder: String,
        val browseLabel: String,
        val cancelLabel: String,
        val uploadLabel: String,
        val removeLabel: String
    ) :
        ResettableFormFragment<FileUpload>(
            "${propertyName}-${formComponents.newChildId()}",
            "fileUploadFragment",
            this,
            null
        ) {
        @Transient
        var fileUpload: FileUpload? = null
        private val bootstrapFileInputField = object : BootstrapFileInputField(
            "file",
            Model.ofList(mutableListOf()),
            FileInputConfig().showPreview(false).maxFileCount(1)
                .withLocale(translator.language)
        ) {
            override fun onSubmit(target: AjaxRequestTarget) {
                super.onSubmit(target)
                this@FileUploadFragment.fileUpload = this.fileUpload
                this.isEnabled = false
                onFileUpload(target, fileUpload)
            }
        }

        override fun onBeforeRender() {
            super.onBeforeRender()
            addOrReplace(bootstrapFileInputField)
        }

        override fun renderHead(response: IHeaderResponse) {
            super.renderHead(response)
            JSScriptsLoader.load(
                response,
                browseButtonJS(), cancelButtonJS(), placeholderJS(), fileLabelJS(), uploadButtonJS(), removeButtonJS()
            )
        }

        fun browseButtonJS() = "\$( \"span:contains('Browse')\" ).text('${translator.translate(browseLabel)}');"

        fun cancelButtonJS() = "\$( \"span:contains('Cancel')\" ).text('${translator.translate(cancelLabel)}');"

        fun uploadButtonJS() = "\$( \"span:contains('Upload')\" ).text('${translator.translate(uploadLabel)}');"

        fun removeButtonJS() = "\$( \"span:contains('Remove')\" ).text('${translator.translate(removeLabel)}');"

        fun placeholderJS() =
            "\$(\$('#${this@DynamicFormComponent.form.markupId}').find(\"input[placeholder='Select file...']\")[0]).attr('placeholder','${placeholder}')"

        fun fileLabelJS() = "\$('label:contains(\"File\")').text('${translator.translate(fieldLabel)}')"

        fun onFileUpload(target: AjaxRequestTarget) {
            this@DynamicFormComponent.onFileUpload(target, this.fileUpload!!)
        }

        override fun onReset(target: AjaxRequestTarget, fileUpload: FileUpload?) {
            this.fileUpload = null
            bootstrapFileInputField.isEnabled = true
            target.add(this)
        }
    }

    private inner class EnumSelectFragment<M : Enum<M>>(
        val propertyName: String,
        val label: String,
        val options: IModel<List<M>>,
        val default: M
    ) :
        ResettableFormFragment<M>(
            "${propertyName}-${formComponents.newChildId()}",
            "selectFragment",
            this
        ) {

        @OptIn(ExperimentalStdlibApi::class)
        override fun onBeforeRender() {
            super.onBeforeRender()

            val choiceRenderer = EnumChoiceRenderer(translator)
            addOrReplace(object :
                BootstrapSelect<M>("select", CompoundPropertyModel.of(default), options, choiceRenderer) {
                init {
                    add(object : OnChangeAjaxBehavior() {
                        override fun onUpdate(target: AjaxRequestTarget) {
                            var enumValue: M? = null
                            form.modelObject::class.members.filterIsInstance<KMutableProperty<*>>()
                                .find { propertyName.equals(it.name) }?.let {
                                    val typeName = it.getter.returnType.javaType.typeName
                                    enumValue = getEnumValue(typeName, modelObject.toString()) as M
                                    it.setter.call(form.modelObject, enumValue)
                                }
                            changedProperties.add(propertyName)
                            onSelectChanged(propertyName, enumValue, target)
                        }

                    })
                }

                override fun getChoicesModel(): IModel<out MutableList<out M>> {
                    return Model.ofList(super.getChoicesModel().`object`.sortedBy { translator.translate(it.name) })
                }

                override fun onModelChanged() {
                    super.onModelChanged()
                    var enumValue: M? = null
                    form.modelObject::class.members.filterIsInstance<KMutableProperty<*>>()
                        .find { propertyName.equals(it.name) }?.let {
                            val typeName = it.getter.returnType.javaType.typeName
                            enumValue = getEnumValue(typeName, modelObject.toString()) as M
                            it.setter.call(form.modelObject, enumValue)
                        }
                    changedProperties.add(propertyName)
                }


                override fun getDefaultChoice(selectedValue: String): CharSequence {
                    println(default.name)
                    return default.toString()
                }
            })
            addOrReplace(Label("selectLabel", translator.translate(label)))
        }

        fun getEnumValue(enumClassName: String, enumValue: String): Any {
            val enumClz = Class.forName(enumClassName).enumConstants as Array<Enum<*>>
            return enumClz.first { it.name.uppercase().equals(enumValue.uppercase()) }
        }

        override fun onReset(target: AjaxRequestTarget, originalValue: M?) {
            defaultModelObject = originalValue
            target.add(this)
        }

    }


    private inner class SelectFragment<M : Serializable>(
        val propertyName: String,
        val label: String,
        val options: IModel<List<M>>,
        val default: M
    ) :
        ResettableFormFragment<M>(
            "${propertyName}-${formComponents.newChildId()}",
            "selectFragment",
            this,
            this@DynamicFormComponent.readInstanceProperty(form.modelObject, propertyName) as M
        ) {

        val choiceRenderer = object : ChoiceRenderer<Serializable>() {
            override fun getDisplayValue(value: Serializable): String {
                return translator.translate(value.toString())
            }

            override fun getIdValue(value: Serializable, index: Int): String {
                return value.toString()
            }

            override fun getObject(id: String, choices: IModel<out MutableList<out Serializable>>): Serializable {
                return choices.`object`.first { it.toString().equals(id) }
            }
        }

        @OptIn(ExperimentalStdlibApi::class)
        override fun onBeforeRender() {
            super.onBeforeRender()
            addOrReplace(object :
                BootstrapSelect<Serializable>("select", CompoundPropertyModel.of(default), options, choiceRenderer) {

                init {
                    add(object : OnChangeAjaxBehavior() {
                        override fun onUpdate(target: AjaxRequestTarget) {
                            var value: M? = null
                            form.modelObject::class.members.filterIsInstance<KMutableProperty<*>>()
                                .find { propertyName.equals(it.name) }?.let {
                                    val typeName = it.getter.returnType.javaType.typeName
                                    value = modelObject as M
                                    it.setter.call(form.modelObject, value)
                                }
                            changedProperties.add(propertyName)
                            onSelectChanged(propertyName, value, target)
                        }

                    })
                }

                override fun getChoicesModel(): IModel<out MutableList<out Serializable>> {
                    return Model.ofList(super.getChoicesModel().`object`.sortedBy { translator.translate(it.toString()) })
                }

                override fun onModelChanged() {
                    super.onModelChanged()
                    form.modelObject::class.members.filterIsInstance<KMutableProperty<*>>()
                        .find { propertyName.equals(it.name) }?.let {
                            it.setter.call(form.modelObject, modelObject)
                        }
                    changedProperties.add(propertyName)
                }

                override fun getDefaultChoice(selectedValue: String): CharSequence {
                    println(default.toString())
                    return default.toString()
                }
            })
            addOrReplace(Label("selectLabel", translator.translate(label)))
        }

        override fun onReset(target: AjaxRequestTarget, originalValue: M?) {
            defaultModelObject = originalValue
            target.add(this)
        }

    }

    private inner class InnerForm(model: IComponentInheritedModel<T>) : Form<T>("dynamicForm", model) {
        private var submitCalled = false

        init {
            add(object : SimpleAjaxButton("submit", translator.translate("Submit")) {

                init {
                    add(object : AjaxFormSubmitBehavior(form, "click") {
                        override fun onSubmit(target: AjaxRequestTarget) {
                            super.onSubmit(target)
                            submitCalled = true
                            this@DynamicFormComponent.onSubmit(target, typedModelObject)
                        }

                        override fun onAfterSubmit(target: AjaxRequestTarget) {
                            super.onAfterSubmit(target)
                            this@DynamicFormComponent.onAfterSubmit(target, typedModelObject)
                        }

                    }, object : AjaxEventBehavior("focusout") {
                        override fun onEvent(target: AjaxRequestTarget) {
                            if (submitCalled) {
                                this@DynamicFormComponent.onSubmitCompleted(target, typedModelObject)
                                target.add(this@DynamicFormComponent, *refreshTargets)
                            }
                        }
                    })
                }

                override fun onClick(target: AjaxRequestTarget) {
                    // Should be handled by the submit behavior
                }

                override fun renderHead(response: IHeaderResponse) {
                    super.renderHead(response)
                    if (!showButons) {
                        JSScriptsLoader.load(
                            response,
                            "$('#${this@DynamicFormComponent.markupId}').find('.form-row').attr('hidden','')"
                        )
                    }
                }

                override fun onModelChanged() {
                    onUpdate(typedModelObject)
                }

                override fun onBeforeRender() {
                    super.onBeforeRender()
                    addOrReplace(
                        Label(
                            "submitName",
                            translator.translate("confirm")
                        )
                    )
                    submitCalled = false
                }


            })
            add(object : SimpleAjaxButton("reset", translator.translate("Reset")) {


                override fun onClick(target: AjaxRequestTarget) {
                    this@DynamicFormComponent.onBeforeCancel(target, typedModelObject)
                    target.add(this@InnerForm)
                    clearInput()
                    this@DynamicFormComponent.onAfterCancel(target, typedModelObject)
                }

            })

        }

        private val typedModelObject get() = defaultModelObject as T

        private fun fetchFileUploadFragments(): List<FileUploadFragment> {
            return formComponents.filter { FileUploadFragment::class.isInstance(it) }
                .toList() as List<FileUploadFragment>
        }

    }

    abstract inner class ResettableFormFragment<T>(
        id: String?,
        markupId: String?,
        markupProvider: MarkupContainer?,
        open val originalValue: T? = null
    ) : Fragment(id, markupId, markupProvider) {

        fun reset(target: AjaxRequestTarget) {
            onReset(target, originalValue)
        }

        protected abstract fun onReset(target: AjaxRequestTarget, originalValue: T?)
    }

    @Suppress("UNCHECKED_CAST")
    private fun <I> readInstanceProperty(instance: I, propertyName: String): String {
        val property = instance!!::class.members
            .first { it.name == propertyName } as KProperty1<Any, *>
        val get = property.get(instance)
        return if (get != null) get.toString() else "NONE"
    }
}