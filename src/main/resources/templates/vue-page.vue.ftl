<template>
  <q-page>
    <!-- 搜索和操作栏 -->
    <q-card>
      <q-card-section>
        <div class="row q-gutter-sm items-center">
<#list table.fields as field>
  <#if field.propertyType == "String" && !field.keyFlag && field.propertyName != "createTime" && field.propertyName != "updateTime">
          <q-input
            v-model="queryForm.${field.propertyName}"
            label="${field.comment!field.propertyName}"
            outlined
            dense
            clearable
            style="width: 160px;"
          />
  </#if>
</#list>
          <q-btn color="primary" outline icon="search" label="搜索" @click="load${entity}s" />
          <q-btn color="grey-6" outline icon="refresh" label="重置" @click="resetQuery" />
        </div>
        <div class="row q-mt-xs q-gutter-sm">  
          <q-btn
            color="primary"
            outline
            icon="add"
            label="新增"
            @click="show${entity}Create"
            v-permission="'${cfg.moduleName}:${cfg.businessName}:add'"
          />
          <q-btn
            color="primary"
            outline
            icon="delete"
            label="批量删除"
            @click="batchDelete"
            :disable="selectedRows.length === 0"
            v-permission="'${cfg.moduleName}:${cfg.businessName}:remove'"
          />
          <q-btn
            color="primary"
            outline
            icon="download"
            label="导出"
            @click="export${entity}s"
            v-permission="'${cfg.moduleName}:${cfg.businessName}:export'"
          />
        </div>
      </q-card-section>
    </q-card>

    <!-- ${table.comment!}表格 -->
    <q-card>
      <q-card-section>
        <q-table
          class="compact-checkbox-table"
          :rows="${entity?uncap_first}s"
          :columns="columns"
          row-key="id"
          :loading="loading"
          v-model:pagination="pagination"
          @request="onRequest"
          binary-state-sort
          :rows-per-page-options="rowsPerPageOptions"
          :no-data-label="'暂无数据'"
          :no-results-label="'未找到匹配的记录'"
          :loading-label="'加载中...'"
          :rows-per-page-label="'每页显示:'"
          selection="multiple"
          v-model:selected="selectedRows"
        >
<#list table.fields as field>
  <#if field.propertyName == "status">
          <template v-slot:body-cell-status="props">
            <q-td :props="props">
              <q-badge
                :color="props.row.status == 1 ? 'positive' : 'negative'"
                :label="props.row.status == 1 ? '正常' : '禁用'"
              />
            </q-td>
          </template>
  </#if>
</#list>

          <template v-slot:body-cell-actions="props">
            <q-td :props="props">
              <q-btn
                flat
                dense
                size="sm"
                color="primary"
                icon="visibility"
                @click="show${entity}Detail(props.row)"
              >
                <q-tooltip>查看详情</q-tooltip>
              </q-btn>
              <q-btn
                flat
                dense
                size="sm"
                color="primary"
                icon="edit"
                @click="show${entity}Edit(props.row)"
                v-permission="'${cfg.moduleName}:${cfg.businessName}:edit'"
              >
                <q-tooltip>编辑</q-tooltip>
              </q-btn>
              <q-btn
                flat
                dense
                size="sm"
                color="primary"
                icon="delete"
                @click="delete${entity}(props.row)"
                v-permission="'${cfg.moduleName}:${cfg.businessName}:remove'"
              >
                <q-tooltip>删除</q-tooltip>
              </q-btn>
            </q-td>
          </template>

          <template v-slot:bottom>
            <DataTablePagination
              :pagination="pagination"
              :rows-per-page-options="rowsPerPageOptions"
              @rows-per-page-change="onRowsPerPageChange"
              @page-change="onPageChange"
            />
          </template>
        </q-table>
      </q-card-section>
    </q-card>

    <!-- ${table.comment!}对话框 -->
    <${entity}EditDialog 
      v-model="${entity?uncap_first}Dialog" 
      :${entity?uncap_first}-data="current${entity}" 
      :is-edit="dialogMode === 'edit'"
      :is-readonly="dialogMode === 'view'"
      @submit="handleSubmit"
    />
  </q-page>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ${entity?uncap_first}Api } from 'src/api'
import { useQuasar } from 'quasar'
import DataTablePagination from 'src/components/DataTablePagination.vue'
import ${entity}EditDialog from './${entity}EditDialog.vue'
import { formatTime } from 'src/utils/index'

defineOptions({
  name: '${cfg.moduleName?cap_first}${entity}Page'
})

const $q = useQuasar()

const loading = ref(false)
const ${entity?uncap_first}Dialog = ref(false)
const dialogMode = ref('view') // 'view', 'edit', 'create'
const ${entity?uncap_first}s = ref([])
const current${entity} = ref(null)
const selectedRows = ref([])

const queryForm = ref({
<#list table.fields as field>
  <#if field.propertyType == "String" && !field.keyFlag && field.propertyName != "createTime" && field.propertyName != "updateTime">
  ${field.propertyName}: '',
  </#if>
</#list>
})

const pagination = ref({
  sortBy: 'createTime',
  descending: true,
  page: 1,
  rowsPerPage: 10,
  rowsNumber: 0
})

const columns = [
<#list table.fields as field>
  <#if !field.keyFlag>
  {
    name: '${field.propertyName}',
    label: '${field.comment!field.propertyName}',
    field: '${field.propertyName}',
    align: 'left',
    <#if field.propertyName == "createTime" || field.propertyName == "updateTime">
    format: (val) => formatTime(val, 'YYYY-MM-DD HH:mm:ss'),
    </#if>
    sortable: true
  },
  </#if>
</#list>
  {
    name: 'actions',
    label: '操作',
    field: 'actions',
    align: 'center'
  }
]

const rowsPerPageOptions = [5, 10, 20, 50, 100]

const load${entity}s = async (props) => {
  loading.value = true
  
  try {
    const { page, rowsPerPage, sortBy, descending } = props?.pagination || pagination.value
    
    const params = {
      current: page,
      size: rowsPerPage,
      orderBy: sortBy || 'createTime',
      orderDirection: descending ? 'desc' : 'asc',
      ...queryForm.value
    }
    
    const response = await ${entity?uncap_first}Api.getList(params)
    const pageData = response.data.data
    const records = pageData.records || []
    const total = pageData.total || 0

    ${entity?uncap_first}s.value = records
  
    pagination.value.rowsNumber = total
    pagination.value.page = page
    pagination.value.rowsPerPage = rowsPerPage
    pagination.value.sortBy = sortBy
    pagination.value.descending = descending
 
  } catch (error) {
    console.error('加载${table.comment!}列表失败:', error)
  } finally {
    loading.value = false
  }
}

const onRequest = (props) => {
  load${entity}s(props)
}

const onRowsPerPageChange = (newRowsPerPage) => {
  pagination.value.rowsPerPage = newRowsPerPage
  pagination.value.page = 1
  load${entity}s()
}

const onPageChange = (newPage) => {
  pagination.value.page = newPage
  onRequest({ pagination: pagination.value })
}

const resetQuery = () => {
  queryForm.value = {
<#list table.fields as field>
  <#if field.propertyType == "String" && !field.keyFlag && field.propertyName != "createTime" && field.propertyName != "updateTime">
    ${field.propertyName}: '',
  </#if>
</#list>
  }
  load${entity}s()
}

const show${entity}Detail = (${entity?uncap_first}) => {
  current${entity}.value = ${entity?uncap_first}
  dialogMode.value = 'view'
  ${entity?uncap_first}Dialog.value = true
}

const show${entity}Edit = (${entity?uncap_first}) => {
  current${entity}.value = ${entity?uncap_first}
  dialogMode.value = 'edit'
  ${entity?uncap_first}Dialog.value = true
}

const show${entity}Create = () => {
  current${entity}.value = null
  dialogMode.value = 'create'
  ${entity?uncap_first}Dialog.value = true
}

const delete${entity} = (${entity?uncap_first}) => {
  $q.dialog({
    title: '确认删除',
    message: `确定要删除这条${table.comment!}记录吗？`,
    cancel: true,
    persistent: true
  }).onOk(async () => {
    try {
      await ${entity?uncap_first}Api.delete(${entity?uncap_first}.id)
      $q.notify({
        type: 'positive',
        message: '${table.comment!}删除成功'
      })
      load${entity}s()
    } catch (error) {
      $q.notify({
        type: 'negative',
        message: error.response?.data?.message || '删除失败'
      })
    }
  })
}

const batchDelete = () => {
  $q.dialog({
    title: '确认批量删除',
    message: `确定要删除选中的 ${r'${selectedRows.value.length}'} 条${table.comment!}记录吗？`,
    cancel: true,
    persistent: true
  }).onOk(async () => {
    try {
      const ids = selectedRows.value.map(row => row.id)
      await ${entity?uncap_first}Api.batchDelete(ids)
      $q.notify({
        type: 'positive',
        message: '${table.comment!}批量删除成功'
      })
      selectedRows.value = []
      load${entity}s()
    } catch (error) {
      $q.notify({
        type: 'negative',
        message: error.response?.data?.message || '批量删除失败'
      })
    }
  })
}

const export${entity}s = async () => {
  try {
    const response = await ${entity?uncap_first}Api.export(queryForm.value)
    
    // 从响应头中提取文件名
    let fileName = `${table.comment!}_${r'${new Date().getTime()}'}.xlsx` // 默认文件名
    const contentDisposition = response.headers['content-disposition']
    if (contentDisposition) {
      const fileNameMatch = contentDisposition.match(/filename[^;=\n]*=((['"]).*?\2|[^;\n]*)/)
      if (fileNameMatch && fileNameMatch[1]) {
        fileName = fileNameMatch[1].replace(/['"]/g, '') // 移除引号
      }
    }
    
    const blob = new Blob([response.data], { type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' })
    const url = window.URL.createObjectURL(blob)
    const link = document.createElement('a')
    link.href = url
    link.download = fileName
    link.click()
    window.URL.revokeObjectURL(url)
    
    $q.notify({
      type: 'positive',
      message: '${table.comment!}导出成功'
    })
  } catch (error) {
    $q.notify({
      type: 'negative',
      message: error.response?.data?.message || '导出失败'
    })
  }
}

const handleRefresh = () => {
  load${entity}s()
}

const handleSubmit = async (${entity?uncap_first}Data) => {
  try {
    if (${entity?uncap_first}Data.id) {
      await ${entity?uncap_first}Api.update(${entity?uncap_first}Data)
      $q.notify({
        type: 'positive',
        message: '${table.comment!}更新成功'
      })
    } else {
      await ${entity?uncap_first}Api.create(${entity?uncap_first}Data)
      $q.notify({
        type: 'positive',
        message: '${table.comment!}创建成功'
      })
    }
    ${entity?uncap_first}Dialog.value = false
    load${entity}s()
  } catch (error) {
    $q.notify({
      type: 'negative',
      message: error.response?.data?.message || '操作失败'
    })
  }
}

onMounted(() => {
  load${entity}s()
})
</script>

<style lang="scss" scoped>
</style>