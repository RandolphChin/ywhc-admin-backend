<template>
  <q-dialog v-model="visible" persistent class="edit-dialog">
    <q-card class="dialog-card" style="min-width: 800px; max-width: 1200px; max-height: 90vh">
      <!-- Header -->
      <q-card-section class="dialog-header">
        <div class="flex items-center justify-between">
          <div class="flex items-center">
            <div class="text-h6">
              <span v-if="isReadonly">查看${table.comment!}</span>
              <span v-else-if="isEdit">编辑${table.comment!}</span>
              <span v-else>添加${table.comment!}</span>
            </div>
          </div>
          <div class="flex items-center q-gutter-sm">
            <q-btn
              flat
              round
              icon="close"
              color="grey-7"
              @click="handleClose"
            >
              <q-tooltip>关闭</q-tooltip>
            </q-btn>
          </div>
        </div>
      </q-card-section>

      <q-separator />

      <q-card-section class="dialog-content">
        <div class="edit-form">
          <q-form ref="formRef" @submit="handleSubmit" class="q-gutter-md">
            <div class="row q-col-gutter-md">
<#list table.fields as field>
  <#if !field.keyFlag && field.propertyName != "createTime" && field.propertyName != "updateTime">
              <div class="col-12 col-md-6">
                <div class="edit-field-inline">
                  <span class="field-label<#if !field.nullable> required</#if>">${field.comment!field.propertyName}：</span>
    <#if field.propertyType == "String">
      <#if field.columnType?? && field.columnType?contains("text")>
                  <q-input
                    v-model="formData.${field.propertyName}"
                    placeholder="${field.comment!field.propertyName}"
                    type="textarea"
                    outlined
                    dense
                    rows="3"
                    :readonly="isReadonly"
        <#if !field.nullable>
                    :rules="[rules.required('${field.comment!field.propertyName}')]"
        </#if>
                    class="field-input"
                  />
      <#else>
                  <q-input
                    v-model="formData.${field.propertyName}"
                    placeholder="${field.comment!field.propertyName}"
                    outlined
                    dense
                    :readonly="isReadonly"
        <#if !field.nullable>
                    :rules="[rules.required('${field.comment!field.propertyName}')]"
        </#if>
                    class="field-input"
                  />
      </#if>
    <#elseif field.propertyType == "Integer" || field.propertyType == "Long">
      <#if field.propertyName == "status">
                  <q-select
                    v-model="formData.${field.propertyName}"
                    :options="statusOptions"
                    placeholder="${field.comment!field.propertyName}"
                    outlined
                    dense
                    emit-value
                    map-options
                    :readonly="isReadonly"
        <#if !field.nullable>
                    :rules="[rules.required('${field.comment!field.propertyName}')]"
        </#if>
                    class="field-input"
                  />
      <#elseif field.propertyName?contains("sort") || field.propertyName?contains("order")>
                  <q-input
                    v-model.number="formData.${field.propertyName}"
                    placeholder="${field.comment!field.propertyName}"
                    type="number"
                    outlined
                    dense
                    :readonly="isReadonly"
        <#if !field.nullable>
                    :rules="[rules.required('${field.comment!field.propertyName}'), rules.number]"
        </#if>
                    class="field-input"
                  />
      <#else>
                  <q-input
                    v-model.number="formData.${field.propertyName}"
                    placeholder="${field.comment!field.propertyName}"
                    type="number"
                    outlined
                    dense
                    :readonly="isReadonly"
        <#if !field.nullable>
                    :rules="[rules.required('${field.comment!field.propertyName}')]"
        </#if>
                    class="field-input"
                  />
      </#if>
    <#elseif field.propertyType == "LocalDateTime" || field.propertyType == "LocalDate">
                  <q-input
                    v-model="formData.${field.propertyName}"
                    placeholder="${field.comment!field.propertyName}"
                    outlined
                    dense
                    :readonly="isReadonly"
      <#if field.propertyType == "LocalDateTime">
                    mask="####-##-## ##:##:##"
      <#else>
                    mask="####-##-##"
      </#if>
        <#if !field.nullable>
                    :rules="[rules.required('${field.comment!field.propertyName}')]"
        </#if>
                    class="field-input"
                  >
                    <template v-slot:append>
                      <q-icon name="event" class="cursor-pointer">
                        <q-popup-proxy cover transition-show="scale" transition-hide="scale">
      <#if field.propertyType == "LocalDateTime">
                          <q-date
                            v-model="formData.${field.propertyName}"
                            mask="YYYY-MM-DD HH:mm:ss"
                          >
                            <div class="row items-center justify-end">
                              <q-btn v-close-popup label="关闭" color="primary" flat />
                            </div>
                          </q-date>
      <#else>
                          <q-date
                            v-model="formData.${field.propertyName}"
                            mask="YYYY-MM-DD"
                          >
                            <div class="row items-center justify-end">
                              <q-btn v-close-popup label="关闭" color="primary" flat />
                            </div>
                          </q-date>
      </#if>
                        </q-popup-proxy>
                      </q-icon>
                    </template>
                  </q-input>
    <#else>
                  <q-input
                    v-model="formData.${field.propertyName}"
                    placeholder="${field.comment!field.propertyName}"
                    outlined
                    dense
                    :readonly="isReadonly"
        <#if !field.nullable>
                    :rules="[rules.required('${field.comment!field.propertyName}')]"
        </#if>
                    class="field-input"
                  />
    </#if>
                </div>
              </div>
  </#if>
</#list>
            </div>
          </q-form>
        </div>
      </q-card-section>

      <q-separator />

      <!-- Footer Actions -->
      <q-card-actions class="dialog-footer q-pa-md bg-grey-1">
        <div class="flex items-center justify-end full-width">
          <div class="q-gutter-sm">
            <q-btn
              flat
              label="取消"
              color="grey-7"
              @click="handleClose"
              class="q-px-lg"
            />
            <q-btn
              v-if="!isReadonly"
              color="primary"
              label="保存"
              @click="handleSubmit"
              class="q-px-lg"
            />
          </div>
        </div>
      </q-card-actions>
    </q-card>
  </q-dialog>
</template>

<script setup>
import { computed, watch, ref } from 'vue'

const props = defineProps({
  modelValue: {
    type: Boolean,
    default: false
  },
  ${entity?uncap_first}Data: {
    type: Object,
    default: () => ({})
  },
  isEdit: {
    type: Boolean,
    default: false
  },
  isReadonly: {
    type: Boolean,
    default: false
  }
})

const emit = defineEmits(['update:modelValue', 'submit'])

const visible = computed({
  get: () => props.modelValue,
  set: (value) => emit('update:modelValue', value)
})

const formData = ref({
<#list table.fields as field>
  ${field.propertyName}: <#if field.propertyType == "String">''<#elseif field.propertyType == "Integer" || field.propertyType == "Long">null<#elseif field.propertyType == "Boolean">false<#else>null</#if>,
</#list>
})

const rules = {
  required: (fieldName) => (val) => !!val || `${r'${fieldName}'}不能为空`,
  number: (val) => /^(0|[1-9]\d*)$/.test(val) || '请输入有效数字'
}

<#list table.fields as field>
  <#if field.propertyName == "status">
const statusOptions = [
  { label: '正常', value: 1 },
  { label: '禁用', value: 0 }
]
    <#break>
  </#if>
</#list>

watch(() => props.${entity?uncap_first}Data, (newData) => {
  if (newData) {
    formData.value = { ...newData }
  } else {
    // 重置表单
    formData.value = {
<#list table.fields as field>
      ${field.propertyName}: <#if field.propertyType == "String">''<#elseif field.propertyType == "Integer" || field.propertyType == "Long">null<#elseif field.propertyType == "Boolean">false<#else>null</#if>,
</#list>
    }
  }
}, { deep: true, immediate: true })

const formRef = ref(null)

const handleSubmit = () => {
  if (props.isReadonly) return

  formRef.value.validate().then((success) => {
    if (success) {
      emit('submit', formData.value)
    }
  })
}

const handleClose = () => {
  visible.value = false
}
</script>

<style lang="scss" scoped>

</style>
