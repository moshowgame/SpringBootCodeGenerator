<el-form :inline="true" :model="submitData" class="demo-form-inline" :rules="rules" ref="ruleForm">
    <el-card class="box-card">
        <div slot="header" class="header clearfix">
            <span>${classInfo.classComment}</span>
            <el-button v-if="!ischeck && !isFind" class="fr" type="primary" @click="validate('ruleForm')">提交</el-button>
            <el-button v-else class="fr" type="primary" @click="goBack">返回</el-button>
        </div>
        <#if classInfo.fieldList?exists && classInfo.fieldList?size gt 0>
            <#list classInfo.fieldList as fieldItem >
             <el-form-item label="${fieldItem.fieldComment}" prop="${fieldItem.fieldName}">
                 <el-input placeholder="请输入${fieldItem.fieldComment}" v-model="formData.${fieldItem.fieldName}"></el-input>
             </el-form-item>
            </#list>
        </#if>
    </el-card>
</el-form>