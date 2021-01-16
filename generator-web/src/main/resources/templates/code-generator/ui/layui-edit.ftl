<!DOCTYPE html>
<html>
<body>
<div class="layui-form layuimini-form">
    <input type="hidden" name="${classInfo.className?uncap_first}Id" value="" class="layui-input">


<#if classInfo.fieldList?exists && classInfo.fieldList?size gt 0>
    <#list classInfo.fieldList as fieldItem >
    <div class="layui-form-item">
        <label class="layui-form-label required">${fieldItem.fieldComment}</label>
        <div class="layui-input-block">
            <input type="text" name="${fieldItem.fieldName}" lay-verify="required" lay-reqtext="${fieldItem.fieldComment}不能为空" placeholder="请输入${fieldItem.fieldComment}" value="￥{(${classInfo.className?uncap_first}.${fieldItem.fieldName})!!}" class="layui-input">
            <#--<tip>${fieldItem.fieldComment}</tip>-->
        </div>
    </div>
    </#list>
</#if>

    <div class="layui-form-item">
        <div class="layui-input-block">
            <button class="layui-btn" lay-submit lay-filter="saveBtn">确认保存</button>
        </div>
    </div>
</div>
</div>
<script src="￥{request.contextPath}/static/lib/layui-v2.5.5/layui.js" charset="utf-8"></script>
<script>
    layui.use(['form'], function () {
        var form = layui.form,
            layer = layui.layer,
            $ = layui.$;

        //监听提交
        form.on('submit(saveBtn)', function (data) {
            $.ajax({
                type: 'POST',
                url: "￥{request.contextPath}/${classInfo.className?uncap_first}/save",
                data:JSON.stringify(data.field),
                dataType: "json",
                contentType: "application/json",
                success: function (responseData) {
                    if (responseData.code === 200) {
                        layer.msg(responseData.msg, function () {
                            // 关闭弹出层
                            //layer.close(index);
                            var iframeIndex = parent.layer.getFrameIndex(window.name);
                            parent.layer.close(iframeIndex);
                            parent.searchBtn.click();
                        });
                    } else {
                        layer.msg(responseData.msg, function () {
                            //window.location = '/index.html';
                        });
                    }
                }
            });
            return false;
        });

    });
</script>
</body>
</html>