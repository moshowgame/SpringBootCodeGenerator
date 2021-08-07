<template>
    <el-dialog
            :title="!dataForm.${classInfo.className?uncap_first}Id ? '新增' : '修改'"
            :close-on-click-modal="false"
            :visible.sync="visible">
        <el-form :model="dataForm" :rules="dataRule" ref="dataForm" @keyup.enter.native="dataFormSubmit()" label-width="80px">
            <#if classInfo.fieldList?exists && classInfo.fieldList?size gt 0>
                <#list classInfo.fieldList as fieldItem >
                    <el-form-item label="${fieldItem.fieldComment}" prop="${fieldItem.fieldName}">
                        <el-input v-model="dataForm.${fieldItem.fieldName}" placeholder="${fieldItem.fieldComment}"></el-input>
                    </el-form-item>
                </#list>
            </#if>
        </el-form>
        <span slot="footer" class="dialog-footer">
      <el-button @click="visible = false">取消</el-button>
      <el-button type="primary" @click="dataFormSubmit()">确定</el-button>
    </span>
    </el-dialog>
</template>

<script>
    export default {
        data () {
            return {
                visible: false,
                dataForm: {
            <#if classInfo.fieldList?exists && classInfo.fieldList?size gt 0>
            <#list classInfo.fieldList as fieldItem >
                    ${fieldItem.fieldName}: ''<#if fieldItem_has_next>,</#if>
            </#list>
            </#if>
        },
            dataRule: {
                <#if classInfo.fieldList?exists && classInfo.fieldList?size gt 0>
                <#list classInfo.fieldList as fieldItem >
                ${fieldItem.fieldName}: [{ required: true, message: '${fieldItem.fieldComment}不能为空', trigger: 'blur' }]<#if fieldItem_has_next>,</#if>
                </#list>
                </#if>
            }
        }
        },
        methods: {
            init (id) {
                this.dataForm.${classInfo.className?uncap_first}Id = id || 0
                this.visible = true
                this.￥nextTick(() => {
                    this.￥refs['dataForm'].resetFields()
                    // <!-- 请把 ${classInfo.className?uncap_first}Id 替换成正确的ID -->
                    if (this.dataForm.${classInfo.className?uncap_first}Id) {
                        this.￥http({
                            url: this.￥http.adornUrl(`/generator/${classInfo.className?uncap_first}/info/￥{this.dataForm.${classInfo.className?uncap_first}Id}`),
                            method: 'get',
                            params: this.￥http.adornParams()
                    }).then(({data}) => {
                            if (data && data.code === 0) {
                                <#if classInfo.fieldList?exists && classInfo.fieldList?size gt 0>
                                <#list classInfo.fieldList as fieldItem >
                                this.dataForm.${fieldItem.fieldName} = data.${classInfo.className?uncap_first}.${fieldItem.fieldName}
                                </#list>
                                </#if>
                            }
                        })
                    }
                })
            },
            // 表单提交
            dataFormSubmit () {
                this.￥refs['dataForm'].validate((valid) => {
                    if (valid) {
                    this.￥http({
                        url: this.￥http.adornUrl(`/generator/${classInfo.className?uncap_first}/￥{this.dataForm.${classInfo.className?uncap_first}Id? 'save' : 'update'}`),
                        method: 'post',
                        data: this.￥http.adornData({
                        <#if classInfo.fieldList?exists && classInfo.fieldList?size gt 0>
                        <#list classInfo.fieldList as fieldItem >
                            '${fieldItem.fieldName}': '${fieldItem.fieldName}' || undefined<#if fieldItem_has_next>,</#if>
                        </#list>
                        </#if>
                        })
                }).then(({data}) => {
                        if (data && data.code === 0) {
                            this.￥message({
                                message: '操作成功',
                                type: 'success',
                                duration: 1500,
                                onClose: () => {
                                    this.visible = false
                                    this.￥emit('refreshDataList')
                                }
                        })
                        } else {
                            this.￥message.error(data.msg)
                        }
                    })
                }
                })
            }
        }
    }
</script>
