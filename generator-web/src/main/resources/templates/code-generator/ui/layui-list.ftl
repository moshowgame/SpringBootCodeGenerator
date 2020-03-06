<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8">
    <title>layuimini</title>
    <meta name="renderer" content="webkit">
    <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
    <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1">
    <link rel="stylesheet" href="request.contextPath/static/lib/layui-v2.5.5/css/layui.css" media="all">
    <link rel="stylesheet" href="request.contextPath/static/css/public.css" media="all">
</head>
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
                            <label class="layui-form-label">${classInfo.classComment}Name</label>
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
                <button class="layui-btn layui-btn-sm data-add-btn"> 添加${classInfo.classComment} </button>
                <button class="layui-btn layui-btn-sm layui-btn-danger data-delete-btn"> 删除${classInfo.classComment} </button>
            </div>
        </script>

        <table class="layui-hide" id="currentTableId" lay-filter="currentTableFilter"></table>

        <script type="text/html" id="currentTableBar">
            <a class="layui-btn layui-btn-xs data-count-edit" lay-event="edit">编辑</a>
            <a class="layui-btn layui-btn-xs layui-btn-danger data-count-delete" lay-event="delete">删除</a>
        </script>

        <script type="text/html" id="statusTemplate">
            {{#  if(d.status = 1){ }}
            启用
            {{#  } else { }}
            停用
            {{#  } }}
        </script>
    </div>
</div>
<script src="request.contextPath/static/lib/layui-v2.5.5/layui.js" charset="utf-8"></script>
<script>
    layui.use(['form', 'table'], function () {
        var $ = layui.jquery,
            form = layui.form,
            table = layui.table,
            layuimini = layui.layuimini;

        table.render({
            elem: '#currentTableId',
            method: 'post',
            url: 'request.contextPath/${classInfo.className?uncap_first}/list',
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
                    {field: '${fieldItem.fieldName}', width: 150, title: '${fieldItem.fieldComment}', sort: true}, <#if fieldItem_has_next> </#if>
                </#list>
                </#if>
                {title: '操作', minWidth: 50, templet: '#currentTableBar', fixed: "right", align: "center"}
            ]],
            limits: [5, 20, 50 , 100],
            limit: 5,
            page: true
        });

        var result;
        // 监听搜索操作
        form.on('submit(data-search-btn)', function (data) {
            result = JSON.stringify(data.field);
            // layer.alert(result, {
            //     title: '最终的搜索信息'
            // });

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
        // 监听添加操作
        $(".data-add-btn").on("click", function () {
            var index = layer.open({
                title: '添加${classInfo.classComment}',
                type: 2,
                shade: 0.2,
                maxmin:true,
                shadeClose: true,
                area: ['800px', '500px'],
                content: 'request.contextPath/${classInfo.className?uncap_first}/edit?id=0',
            });
            $(window).on("resize", function () {
                obj.del();
                layer.full(index);
            });
            return false;
        });

        // 监听删除操作
        $(".data-delete-btn").on("click", function () {
            var checkStatus = table.checkStatus('currentTableId')
                , data = checkStatus.data;
            layer.alert(JSON.stringify(data));
        });

        //监听表格复选框选择
        table.on('checkbox(currentTableFilter)', function (obj) {
            console.log(obj)
        });

        //监听表格编辑删除等功能按钮
        table.on('tool(currentTableFilter)', function (obj) {
            var data = obj.data;
            if (obj.event === 'edit') {
                var index = layer.open({
                    title: '编辑用户',
                    type: 2,
                    shade: 0.2,
                    maxmin:true,
                    shadeClose: true,
                    area: ['800px', '500px'],
                    content: 'request.contextPath/${classInfo.className?uncap_first}/edit?id='+obj.data.${classInfo.className?uncap_first}Id,
                });
                $(window).on("resize", function () {
                    obj.del();
                    layer.full(index);
                });
                return false;
            } else if (obj.event === 'delete') {
                layer.confirm('真的删除行么', function (index) {
                    $.ajax({
                        type: 'POST',
                        url: "request.contextPath/${classInfo.className?uncap_first}/delete",
                        data:{"id":obj.data.${classInfo.className?uncap_first}Id},
                        success: function (responseData) {
                            if (responseData.code === 200) {
                                layer.msg(responseData.msg, function () {
                                    obj.del();
                                });
                            } else {
                                layer.msg(responseData.msg, function () {
                                    //window.location = '/index.html';
                                });
                            }
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