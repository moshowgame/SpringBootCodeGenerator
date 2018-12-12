$(function () {

    /**
     * 初始化 table sql 3
     */
    var ddlSqlArea = CodeMirror.fromTextArea(document.getElementById("ddlSqlArea"), {
        lineNumbers: true,
        matchBrackets: true,
        mode: "text/x-sql",
        lineWrapping:false,
        readOnly:false,
        foldGutter: true,
        gutters:["CodeMirror-linenumbers", "CodeMirror-foldgutter"]
    });
    ddlSqlArea.setSize('auto','auto');
    // controller_ide
    var genCodeArea = CodeMirror.fromTextArea(document.getElementById("genCodeArea"), {
        lineNumbers: true,
        matchBrackets: true,
        mode: "text/x-java",
        lineWrapping:true,
        readOnly:true,
        foldGutter: true,
        gutters:["CodeMirror-linenumbers", "CodeMirror-foldgutter"]
    });
    genCodeArea.setSize('auto','auto');

    var codeData;

    /**
     * 生成代码
     */
    $('#btnGenCode').click(function ()  {

        var tableSql = ddlSqlArea.getValue();
        $.ajax({
            type: 'POST',
            url: base_url + "/genCode",
            data: {
                "tableSql": tableSql,
                "packageName":$("#packageName").val(),
                "authorName":$("#authorName").val()
            },
            dataType: "json",
            success: function (data) {
                if (data.code == 200) {
                    layer.open({
                        icon: '1',
                        content: "代码生成成功",
                        end: function () {

                            codeData = data.data;
                            genCodeArea.setValue(codeData.swaggerui);
                            genCodeArea.setSize('auto', 'auto');

                        }
                    });
                } else {
                    layer.open({
                        icon: '2',
                        content: (data.msg || '代码生成失败')
                    });
                }
            }
        });
    });
    /**
     * 按钮事件组
     */
    $('#swaggerui').click(function ()  {
        if(!$.isEmptyObject(codeData)){
            genCodeArea.setValue(codeData.swaggerui);
            genCodeArea.setSize('auto', 'auto');
        }
    });
    $('#entity').click(function ()  {
        if(!$.isEmptyObject(codeData)) {
            genCodeArea.setValue(codeData.entity);
            genCodeArea.setSize('auto', 'auto');
        }
    });
    $('#repository').click(function ()  {
        if(!$.isEmptyObject(codeData)) {
            genCodeArea.setValue(codeData.repository);
            genCodeArea.setSize('auto', 'auto');
        }
    });
    $('#jpacontroller').click(function ()  {
        if(!$.isEmptyObject(codeData)) {
            genCodeArea.setValue(codeData.jpacontroller);
            genCodeArea.setSize('auto', 'auto');
        }
    });
    $('#model').click(function ()  {
        if(!$.isEmptyObject(codeData)) {
            genCodeArea.setValue(codeData.model);
            genCodeArea.setSize('auto', 'auto');
        }
    });
    $('#mybatis').click(function ()  {
        if(!$.isEmptyObject(codeData)) {
            genCodeArea.setValue(codeData.mybatis);
            genCodeArea.setSize('auto', 'auto');
        }
    });
    $('#dao').click(function ()  {
        if(!$.isEmptyObject(codeData)) {
            genCodeArea.setValue(codeData.dao);
            genCodeArea.setSize('auto', 'auto');
        }
    });
    $('#service').click(function ()  {
        if(!$.isEmptyObject(codeData)) {
            genCodeArea.setValue(codeData.service);
            genCodeArea.setSize('auto', 'auto');
        }
    });
    $('#service_impl').click(function ()  {
        if(!$.isEmptyObject(codeData)) {
            genCodeArea.setValue(codeData.service_impl);
            genCodeArea.setSize('auto', 'auto');
        }
    });
    $('#controller').click(function ()  {
        if(!$.isEmptyObject(codeData)) {
            genCodeArea.setValue(codeData.controller);
            genCodeArea.setSize('auto', 'auto');
        }
    });
    $('#jtdao').click(function ()  {
        if(!$.isEmptyObject(codeData)) {
            genCodeArea.setValue(codeData.jtdao);
            genCodeArea.setSize('auto', 'auto');
        }
    });
    $('#jtdaoimpl').click(function ()  {
        if(!$.isEmptyObject(codeData)) {
            genCodeArea.setValue(codeData.jtdaoimpl);
            genCodeArea.setSize('auto', 'auto');
        }
    });
    $('#beetlcontroller').click(function ()  {
        if(!$.isEmptyObject(codeData)) {
            genCodeArea.setValue(codeData.beetlcontroller);
            genCodeArea.setSize('auto', 'auto');
        }
    });
    $('#beetlmd').click(function ()  {
        if(!$.isEmptyObject(codeData)) {
            genCodeArea.setValue(codeData.beetlmd);
            genCodeArea.setSize('auto', 'auto');
        }
    });
    $('#beetlentity').click(function ()  {
        if(!$.isEmptyObject(codeData)) {
            genCodeArea.setValue(codeData.beetlentity);
            genCodeArea.setSize('auto', 'auto');
        }
    });
    $('#bootstrap').click(function ()  {
        if(!$.isEmptyObject(codeData)) {
            genCodeArea.setValue(codeData.bootstrap);
            genCodeArea.setSize('auto', 'auto');
        }
    });
    $('#element-ui').click(function ()  {
        if(!$.isEmptyObject(codeData)) {
            genCodeArea.setValue(codeData.elementui);
            genCodeArea.setSize('auto', 'auto');
        }
    });
    $('#pluscontroller').click(function ()  {
        if(!$.isEmptyObject(codeData)) {
            genCodeArea.setValue(codeData.pluscontroller);
            genCodeArea.setSize('auto', 'auto');
        }
    });
    $('#plusmapper').click(function ()  {
        if(!$.isEmptyObject(codeData)) {
            genCodeArea.setValue(codeData.plusmapper);
            genCodeArea.setSize('auto', 'auto');
        }
    });

});