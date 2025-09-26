import { api } from "src/boot/axios";

/**
 * ${table.comment!}API
 */
export const ${entity?uncap_first}Api = {
  /**
   * 分页查询${table.comment!}列表
   */
  getList(params) {
    return api.get('/${cfg.moduleName}/${cfg.businessName}/page', { params })
  },

  /**
   * 查询所有${table.comment!}列表
   */
  getAllList(params) {
    return api.get('/${cfg.moduleName}/${cfg.businessName}/list', { params })
  },

  /**
   * 根据ID查询${table.comment!}详情
   */
  getById(id) {
    return api.get(`/${cfg.moduleName}/${cfg.businessName}/${r'${id}'}`)
  },

  /**
   * 新增${table.comment!}
   */
  create(data) {
    return api.post('/${cfg.moduleName}/${cfg.businessName}', data)
  },

  /**
   * 修改${table.comment!}
   */
  update(data) {
    return api.put('/${cfg.moduleName}/${cfg.businessName}', data)
  },

  /**
   * 删除${table.comment!}
   */
  delete(id) {
    return api.delete(`/${cfg.moduleName}/${cfg.businessName}/${r'${id}'}`)
  },

  /**
   * 批量删除${table.comment!}
   */
  batchDelete(ids) {
    return api.delete('/${cfg.moduleName}/${cfg.businessName}/batch', { data: ids })
  },

  /**
   * 导出${table.comment!}
   */
  export(params) {
    return api.post('/${cfg.moduleName}/${cfg.businessName}/export', params, {
      responseType: 'blob'
    })
  }
}
