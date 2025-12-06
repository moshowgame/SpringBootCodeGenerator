<!DOCTYPE html>
<html>
<body>
<div class="layuimini-container">
    <div class="layuimini-main">

        <fieldset class="table-search-fieldset">
            <legend>搜索信息</legend>
            <div style="margin: 10px 10px 10px 10px">
                <form class="layui-form layui-form-pane" action="">
                    <div class="layui-form-item">
                        <div class="layui-inline">
                            <label class="layui-form-label">${classInfo.classComment}Id</label>
                            <div class="layui-input-inline">
                                <input type="text" name="${classInfo.className?uncap_first}Id" autocomplete="off" class="layui-input">
                            </div>
                        </div>
                        <div class="layui-inline">
                            <label class="layui-form-label">${classInfo.classComment}名称</label>
                            <div class="layui-input-inline">
                                <input type="text" name="${classInfo.className?uncap_first}Name" autocomplete="off" class="layui-input">
                            </div>
                        </div>
                        <div class="layui-inline">
                            <button id="searchBtn" type="submit" class="layui-btn layui-btn-primary" lay-submit  lay-filter="data-search-btn"><i class="layui-icon"></i> 搜 索</button>
                        </div>
                    </div>
                </form>
            </div>
        </fieldset>

        <script type="text/html" id="toolbarDemo">
            <div class="layui-btn-container">
                <button class="layui-btn layui-btn-normal layui-btn-sm data-add-btn" lay-event="add">  <i class="layui-icon layui-icon-addition"></i>${classInfo.classComment} </button>
               <#-- <button class="layui-btn layui-btn-normal layui-btn-sm layui-btn-danger data-delete-btn" lay-event="del"> 删除${classInfo.classComment} </button>-->
            </div>
        </script>

        <table class="layui-hide" id="currentTableId" lay-filter="currentTableFilter"></table>

        <script type="text/html" id="currentTableBar">
            <a class="layui-btn layui-btn-xs data-count-edit" lay-event="edit">编辑</a>
            <a class="layui-btn layui-btn-xs layui-btn-danger data-count-delete" lay-event="delete">删除</a>
        </script>

        <script type="text/html" id="typeTemplate">
            {{#  if(d.type == '1'){ }}
            常规
            {{#  } else if(d.type =='2') { }}
            专项
            {{#  } else { }}
            其它
            {{#  } }}
        </script>
        <script type="text/html" id="statusTemplate">
            {{#  if(d.status == '1' ){ }}
            <i class="layui-icon layui-icon-ok"></i>已发布
            {{#  } else { }}
            - 未发布
            {{#  } }}
        </script>
    </div>
</div>
<script src="￥{request.contextPath}/static/lib/layui-v2.5.5/layui.js" charset="utf-8"></script>
<script>
    layui.use(['form', 'table'], function () {
        var $ = layui.jquery,
            form = layui.form,
            table = layui.table;

        table.render({
            elem: '#currentTableId',
            method: 'post',
            url: '￥{request.contextPath}/${classInfo.className?uncap_first}/list',
            toolbar: '#toolbarDemo',
            defaultToolbar: ['filter', 'exports', 'print', {
                title: '提示',
                layEvent: 'LAYTABLE_TIPS',
                icon: 'layui-icon-tips'
            }],
            cols: [[
                {type: "checkbox", width: 50, fixed: "left"},
                <#if classInfo.fieldList?exists && classInfo.fieldList?size gt 0>
                <#list classInfo.fieldList as fieldItem >
                    {field: '${fieldItem.fieldName}', title: '${fieldItem.fieldComment}', sort: true}, <#if fieldItem_has_next> </#if>
                </#list>
                </#if>
                /* 需要时间请自行解封
                {title: '创建时间', sort: true,templet: "<div>{{layui.util.toDateString(d.createTime, 'yyyy-MM-dd')}}</div>"},
                {title: '修改时间', sort: true,templet: "<div>{{layui.util.toDateString(d.updateTime, 'yyyy-MM-dd')}}</div>"},
                */
                {title: '操作', minWidth: 400, templet: '#currentTableBar', fixed: "right", align: "center"}
            ]],
            limits: [20 , 50 , 100],
            limit: 20,
            page: true
        });

        var result;
        /**
         * submit(data-search-btn):监听搜索操作
         */
        form.on('submit(data-search-btn)', function (data) {
            result = JSON.stringify(data.field);

            //执行搜索重载
            table.reload('currentTableId', {
                page: {
                    curr: 1
                }
                , where: {
                    searchParams: result
                }
            }, 'data');

            return false;
        });

        var searchBtn = $("#searchBtn");
        /**
         * toolbar监听事件:表格添加按钮
         */
        table.on('toolbar(currentTableFilter)', function (obj) {
            if (obj.event === 'add') {
                var index = layer.open({
                    title: '添加',
                    type: 2,
                    shade: 0.2,
                    maxmin:true,
                    shadeClose: true,
                    area: ['1000px', '700px'],
                    content: '￥{request.contextPath}/${classInfo.className?uncap_first}/edit?id=0',
                });
                return false;
            }else if(obj.event === 'del') {
                var checkStatus = table.checkStatus('currentTableId')
                    , data = checkStatus.data;
                layer.alert(JSON.stringify(data));
            }
        });
        /**
         * checkbox(currentTableFilter):表格复选框选择
         */
        table.on('checkbox(currentTableFilter)', function (obj) {
            //console.log(obj)
        });

        /**
         * tool监听事件:表格编辑删除等功能按钮
         */
        table.on('tool(currentTableFilter)', function (obj) {
            var data = obj.data;
            if (obj.event === 'edit') {
                var index = layer.open({
                    title: '编辑',
                    type: 2,
                    shade: 0.2,
                    maxmin:true,
                    shadeClose: true,
                    area: ['1000px', '700px'],
                    content: '￥{request.contextPath}/${classInfo.className?uncap_first}/edit?id='+obj.data.${classInfo.className?uncap_first}Id,
                });
                return false;
            } else if (obj.event === 'delete') {
                layer.confirm('确认删除该记录吗？', function (index) {
                    $.ajax({
                        type: 'POST',
                        url: "￥{request.contextPath}/${classInfo.className?uncap_first}/delete",
                        data:{"id":obj.data.${classInfo.className?uncap_first}Id},
                        success: function (responseData) {
                            if (responseData.code === 200) {
                                layer.msg(responseData.msg, function () {
                                    obj.del();
                                });
                            } else {
                                layer.msg(responseData.msg, function () {
                                });
                            }
                        }
                    });
                    layer.close(index);
                });
            }else if (obj.event === 'publish') {
                layer.confirm('确定要发布吗？', function (index) {
                    $.ajax({
                        type: 'POST',
                        url: "￥{request.contextPath}/${classInfo.className?uncap_first}/publish",
                        data:{"id":obj.data.${classInfo.className?uncap_first}Id,"status":"1"},
                        success: function (responseData) {
                            searchBtn.click();
                            layer.msg(responseData.msg, function () {
                            });
                        }
                    });
                    layer.close(index);
                });
            }else if (obj.event === 'unpublish') {
                layer.confirm('确定要停止吗？', function (index) {
                    $.ajax({
                        type: 'POST',
                        url: "￥{request.contextPath}/${classInfo.className?uncap_first}/publish",
                        data:{"id":obj.data.${classInfo.className?uncap_first}Id,"status":"0"},
                        success: function (responseData) {
                            searchBtn.click();
                            layer.msg(responseData.msg, function () {
                            });
                        }
                    });
                    layer.close(index);
                });
            }
        });

    });
</script>
<script>

</script>

</body>
</html>