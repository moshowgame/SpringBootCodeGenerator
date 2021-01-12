<!DOCTYPE html>
<html lang="utf-8">
<head>
    <meta charset="UTF-8">
    <title>SQL转Java JPA、MYBATIS实现类代码生成平台</title>
    <meta name="keywords" content="sql转实体类,sql转DAO,SQL转service,SQL转JPA实现,SQL转MYBATIS实现">

    <#import "common/common-import.ftl" as netCommon>
    <@netCommon.commonStyle />
    <@netCommon.commonScript />

<script>

    <@netCommon.viewerCounter />

    $(function () {
        /**
         * 初始化SQL AREA
         */
        const ddlSqlArea = CodeMirror.fromTextArea(document.getElementById("ddlSqlArea"), {
            lineNumbers: true,
            matchBrackets: true,
            mode: "text/x-sql",
            lineWrapping: false,
            readOnly: false,
            foldGutter: true,
            //keyMap:"sublime",
            gutters: ["CodeMirror-linenumbers", "CodeMirror-foldgutter"]
        });
        ddlSqlArea.setSize('auto','auto');
        /**
         * 初始化CODE AREA
         */
        const genCodeArea = CodeMirror.fromTextArea(document.getElementById("genCodeArea"), {
            lineNumbers: true,
            matchBrackets: true,
            mode: "text/x-java",
            lineWrapping: true,
            readOnly: false,
            foldGutter: true,
            //keyMap:"sublime",
            gutters: ["CodeMirror-linenumbers", "CodeMirror-foldgutter"]
        });
        genCodeArea.setSize('auto','auto');

        let codeData;
        // 使用：var jsonObj = $("#formId").serializeObject();
        $.fn.serializeObject = function()
        {
            const o = {};
            const a = this.serializeArray();
            $.each(a, function() {
                if (o[this.name]) {
                    if (!o[this.name].push) {
                        o[this.name] = [o[this.name]];
                    }
                    o[this.name].push(this.value || '');
                } else {
                    o[this.name] = this.value || '';
                }
            });
            return o;
        };
        let historyCount = 0;
        //初始化清除session
        if (window.sessionStorage){
            //修复当F5刷新的时候，session没有清空各个值，但是页面的button没了。
            sessionStorage.clear();
        }
        /**
         * 生成代码
         */
        $('#btnGenCode').click(function ()  {
            const jsonData = {
                "tableSql": ddlSqlArea.getValue(),
                "packageName": $("#packageName").val(),
                "returnUtil": $("#returnUtil").val(),
                "authorName": $("#authorName").val(),
                "dataType": $("#dataType").val(),
                "tinyintTransType": $("#tinyintTransType").val(),
                "nameCaseType": $("#nameCaseType").val(),
                "swagger": $("#isSwagger").val(),
                "timeTransType": $("#timeTransType").val(),
                "packageType": $("#packageType").val()
            };
            $.ajax({
                type: 'POST',
                url: base_url + "/genCode",
                data:(JSON.stringify(jsonData)),
                dataType: "json",
                contentType: "application/json",
                success: function (data) {
                    if (data.code === 200) {
                        codeData = data.data;
                        genCodeArea.setValue(codeData.beetlentity);
                        genCodeArea.setSize('auto', 'auto');
                        $.toast("√ 代码生成成功");
                        //添加历史记录
                        addHistory(codeData);
                    } else {
                        $.toast("× 代码生成失败 :"+data.msg);
                    }
                }
            });
            return false;
        });
        /**
         * 切换历史记录
         */
        function getHistory(tableName){
            if (window.sessionStorage){
                const valueSession = sessionStorage.getItem(tableName);
                codeData = JSON.parse(valueSession);
                $.toast("$ 切换历史记录成功:"+tableName);
                genCodeArea.setValue(codeData.entity);
            }else{
                console.log("浏览器不支持sessionStorage");
            }
        }
        /**
         * 添加历史记录
         */
        function addHistory(data){
            if (window.sessionStorage){
                //console.log(historyCount);
                if(historyCount>=9){
                    $("#history").find(".btn:last").remove();
                    historyCount--;
                }
                const tableName = data.tableName;
                const valueSession = sessionStorage.getItem(tableName);
                if(valueSession!==undefined && valueSession!=null){
                    sessionStorage.removeItem(tableName);
                }else{
                    $("#history").prepend('<button id="his-'+tableName+'" type="button" class="btn">'+tableName+'</button>');
                    //$("#history").prepend('<button id="his-'+tableName+'" onclick="getHistory(\''+tableName+'\');" type="button" class="btn">'+tableName+'</button>');
                    $("#his-"+tableName).bind('click', function () {getHistory(tableName)});
                }
                sessionStorage.setItem(tableName,JSON.stringify(data));
                historyCount++;
            }else{
                console.log("浏览器不支持sessionStorage");
            }
        }

        /**
         * 按钮事件组
         */
        $('.generator').bind('click', function () {
            if (!$.isEmptyObject(codeData)) {
                const id = this.id;
                genCodeArea.setValue(codeData[id]);
                genCodeArea.setSize('auto', 'auto');
            }
        });
        /**
         * 捐赠
         */
        function donate(){
            if($("#donate").attr("show")=="no"){
                $("#donate").html('<img src="https://raw.githubusercontent.com/moshowgame/SpringBootCodeGenerator/master/donate.png"></img>');
                $("#donate").attr("show","yes");
            }else{
                $("#donate").html('<p>谢谢赞赏！</p>');
                $("#donate").attr("show","no");
            }
        }
        $('#donate1').on('click', function(){
            donate();
        });
        $('#donate2').on('click', function(){
            donate();
        });
        $('#btnCopy').on('click', function(){
            if(!$.isEmptyObject(genCodeArea.getValue())&&!$.isEmptyObject(navigator)&&!$.isEmptyObject(navigator.clipboard)){
                navigator.clipboard.writeText(genCodeArea.getValue());
                $.toast("√ 复制成功");
            }
        });

        function getVersion(){
            let gitVersion;
            $.ajax({
                type: 'GET',
                url: "https://raw.githubusercontent.com/moshowgame/SpringBootCodeGenerator/master/generator-web/src/main/resources/static/version.json",
                dataType: "json",
                success: function (data) {
                    gitVersion = data.version;
                    $.ajax({
                        type: 'GET',
                        url: base_url + "/static/version.json",
                        dataType: "json",
                        success: function (data) {
                            $.toast("#当前版本:"+data.version+" | github:"+gitVersion);
                        }
                    });
                }
            });
        }
        $('#version').on('click', function(){
            getVersion();
        });
    });
</script>
</head>
<body style="background-color: #e9ecef">
<#--布局修改为4-8分-->
<div class="container">
    <div class="row">
        <#--选择区域-->
        <div class="col-sm-4">
            <!-- Example row of columns -->
            <div class="row" style="margin-top: 10px;">
                <div class="btn-toolbar col-md-5" role="toolbar" aria-label="Toolbar with button groups">
                    <div class="input-group">
                        <div class="input-group-prepend">
                            <div class="btn btn-secondary disabled setWidth" id="btnGroupAddon">通用实体</div>
                        </div>
                    </div>
                    <div class="btn-group" role="group" aria-label="First group">
                        <button type="button" class="btn btn-default generator" id="model">entity(set/get)</button>
                        <button type="button" class="btn btn-default generator" id="beetlentity">entity(lombok)</button>
                    </div>
                </div>
                <div class="btn-toolbar col-md-7" role="toolbar" aria-label="Toolbar with button groups">
                    <div class="input-group">
                        <div class="input-group-prepend">
                            <div class="btn btn-secondary disabled setWidth" id="btnGroupAddon">常用Util</div>
                        </div>
                    </div>
                    <div class="btn-group" role="group" aria-label="First group">
                        <button type="button" class="btn btn-default generator" id="beanutil">BeanUtil</button>
                        <button type="button" class="btn btn-default generator" id="sql">SQL(CRUD)</button>
                        <button type="button" class="btn btn-default generator" id="json">JSON</button>
                        <button type="button" class="btn btn-default generator" id="xml">XML</button>
                    </div>
                </div>
            </div>
            <!-- Example row of columns -->
            <div class="row" style="margin-top: 10px;">
                <div class="btn-toolbar col-md-5" role="toolbar" aria-label="Toolbar with button groups">
                    <div class="input-group">
                        <div class="input-group-prepend">
                            <div class="btn btn-secondary disabled setWidth" id="btnGroupAddon">MybatisPlus</div>
                        </div>
                    </div>
                    <div class="btn-group" role="group" aria-label="First group">
                        <button type="button" class="btn btn-default generator" id="plusentity">entity</button>
                        <button type="button" class="btn btn-default generator" id="plusmapper">mapper</button>
                        <button type="button" class="btn btn-default generator" id="pluscontroller">controller</button>
                    </div>
                </div>

                <div class="btn-toolbar col-md-7" role="toolbar" aria-label="Toolbar with button groups">
                    <div class="input-group">
                        <div class="input-group-prepend">
                            <div class="btn btn-secondary disabled setWidth" id="btnGroupAddon">UI</div>
                        </div>
                    </div>
                    <div class="btn-group" role="group" aria-label="First group">
                        <button type="button" class="btn btn-default generator" id="swagger-ui">swagger-ui</button>
                        <button type="button" class="btn btn-default generator" id="element-ui">element-ui</button>
                        <button type="button" class="btn btn-default generator" id="bootstrap-ui">bootstrap-ui</button>
                        <button type="button" class="btn btn-default generator" id="layui-edit">layui-edit</button>
                        <button type="button" class="btn btn-default generator" id="layui-list">layui-list</button>
                    </div>
                </div>
            </div>

            <div class="row" style="margin-top: 10px;">
                <div class="btn-toolbar col-md-5" role="toolbar" aria-label="Toolbar with button groups">
                    <div class="input-group">
                        <div class="input-group-prepend">
                            <div class="btn btn-secondary disabled setWidth" id="btnGroupAddon">BeetlSQL</div>
                        </div>
                    </div>
                    <div class="btn-group" role="group" aria-label="First group">
                        <button type="button" class="btn btn-default generator" id="beetlmd">beetlmd</button>
                        <button type="button" class="btn btn-default generator" id="beetlcontroller">beetlcontroller</button>
                    </div>
                </div>
                <div class="btn-toolbar col-md-5" role="toolbar" aria-label="Toolbar with button groups">
                    <div class="input-group">
                        <div class="input-group-prepend">
                            <div class="btn btn-secondary disabled setWidth" id="btnGroupAddon">JPA</div>
                        </div>
                    </div>
                    <div class="btn-group" role="group" aria-label="First group">
                        <button type="button" class="btn btn-default generator" id="entity">jpa-entity</button>
                        <button type="button" class="btn btn-default generator" id="repository">repository</button>
                        <button type="button" class="btn btn-default generator" id="jpacontroller">controller</button>
                    </div>
                </div>
            </div>
            <div class="row" style="margin-top: 10px;">
                <div class="btn-toolbar col-md-5" role="toolbar" aria-label="Toolbar with button groups">
                    <div class="input-group">
                        <div class="input-group-prepend">
                            <div class="btn btn-secondary disabled setWidth" id="btnGroupAddon">JdbcTemplate</div>
                        </div>
                    </div>
                    <div class="btn-group" role="group" aria-label="First group">
                        <button type="button" class="btn btn-default generator" id="jtdaoimpl">daoimpl</button>
                        <button type="button" class="btn btn-default generator" id="jtdao">dao</button>
                    </div>
                </div>
                <div class="btn-toolbar col-md-7" role="toolbar" aria-label="Toolbar with button groups">
                    <div class="input-group">
                        <div class="input-group-prepend">
                            <div class="btn btn-secondary disabled setWidth" id="btnGroupAddon">Mybatis</div>
                        </div>
                    </div>
                    <div class="btn-group" role="group" aria-label="First group">
                        <button type="button" class="btn btn-default generator" id="mybatis">ibatisXml</button>
                        <button type="button" class="btn btn-default generator" id="mapper">mapper</button>
                        <button type="button" class="btn btn-default generator" id="mapper2">mapper2</button>
                        <button type="button" class="btn btn-default generator" id="service">service</button>
                        <button type="button" class="btn btn-default generator" id="service_impl">serviceImpl</button>
                        <button type="button" class="btn btn-default generator" id="controller">controller</button>
                    </div>
                </div>
            </div>
        </div>
        <#--核心工作区-->

        <div class="col-sm-8">
            <!-- Main jumbotron for a primary marketing message or call to action -->
            <div class="jumbotron jumbotron-fluid">
                <div class="container">
                    <h1 class="display-4">Spring Boot Code Generator!</h1>
                    <p class="lead">
                        √基于SpringBoot2+Freemarker的<a class="lead" href="https://github.com/moshowgame/SpringBootCodeGenerator">代码生成器</a><br>
                        √以解放双手为目的，减少大量重复的CRUD工作<br>
                        √支持mysql/oracle/pgsql三大数据库<br>
                        √用DDL-SQL语句生成JPA/JdbcTemplate/Mybatis/MybatisPlus/BeetlSQL相关代码。<br>
                        如果发现有SQL语句不能识别，请<a href="https://github.com/moshowgame/SpringBootCodeGenerator/issues">留言</a>，同时欢迎大家提<a href="https://github.com/moshowgame/SpringBootCodeGenerator/pulls">PR</a>和<a href="#" id="donate1">赞赏</a>，谢谢！<a id="version" href="#">查看版本</a>
                    </p>
                    <div id="donate" class="container" show="no"></div>
                    <hr>
                    <div class="input-group mb-3">
                        <div class="input-group-prepend">
                            <span class="input-group-text">数据类型</span>
                        </div>
                        <select type="text" class="form-control" id="dataType"
                                name="dataType">
                            <option value="sql">ddl-sql</option>
                            <option value="json">json</option>
                            <option value="insert-sql">insert-sql</option>
                            <#--<option value="sql-regex">sql-regex</option>-->
                        </select>
                        <div class="input-group-prepend">
                            <span class="input-group-text">作者</span>
                        </div>
                        <input type="text" class="form-control" id="authorName" name="authorName" value="zhengkai.blog.csdn.net">
                        <div class="input-group-prepend">
                            <span class="input-group-text">返回</span>
                        </div>
                        <input type="text" class="form-control" id="returnUtil" name="returnUtil" value="ReturnT">
                        <div class="input-group-prepend">
                            <span class="input-group-text">包名</span>
                        </div>
                        <input type="text" class="form-control" id="packageName" name="packageName" value="com.softdev.system">
                    </div>
                    <div class="input-group mb-3">
                        <div class="input-group-prepend">
                            <span class="input-group-text">tinyint转换</span>
                        </div>
                        <select type="text" class="form-control" id="tinyintTransType"
                                name="tinyintTransType">
                            <option value="boolean">boolean</option>
                            <option value="Boolean">Boolean</option>
                            <option value="Integer">Integer</option>
                            <option value="int">int</option>
                            <option value="String">String</option>
                            <option value="Short">Short</option>
                        </select>
                        <div class="input-group-prepend">
                            <span class="input-group-text">时间类型</span>
                        </div>
                        <select type="text" class="form-control" id="timeTransType"
                                name="timeTransType">
                            <option value="Date">Date</option>
                            <option value="DateTime">DateTime</option>
                            <option value="Time">Time</option>
                            <option value="Timestamp">Timestamp</option>
                            <option value="Calendar">Calendar</option>
                            <option value="LocalDate">LocalDate</option>
                            <option value="LocalDateTime">LocalDateTime</option>
                            <option value="LocalTime">LocalTime</option>
                        </select>
                        <div class="input-group-prepend">
                            <span class="input-group-text">命名规则</span>
                        </div>
                        <select type="text" class="form-control" id="nameCaseType"
                                name="nameCaseType">
                            <option value="CamelCase">驼峰</option>
                            <option value="UnderScoreCase">下划线</option>
                            <#--<option value="UpperUnderScoreCase">大写下划线</option>-->
                        </select>
                        <div class="input-group-prepend">
                            <span class="input-group-text">包装类型</span>
                        </div>
                        <select type="text" class="form-control" id="packageType"
                                name="packageType">
                            <option value="true">启用</option>
                            <option value="false">关闭</option>
                        </select>
                        <div class="input-group-prepend">
                            <span class="input-group-text">swagger-ui</span>
                        </div>
                        <select type="text" class="form-control" id="isSwagger"
                                name="isSwagger">
                            <option value="false">关闭</option>
                            <option value="true">启用</option>
                        </select>
                    </div>
                    <textarea id="ddlSqlArea" placeholder="请输入表结构信息..." class="form-control btn-lg" style="height: 250px;">
CREATE TABLE 'userinfo' (
  'user_id' int(11) NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  'user_name' varchar(255) NOT NULL COMMENT '用户名',
  'addtime' datetime NOT NULL COMMENT '创建时间',
  PRIMARY KEY ('user_id')
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='用户信息'
        </textarea><br>
                    <p><button class="btn btn-primary btn-lg disabled" id="btnGenCode" role="button" data-toggle="popover" data-content="">开始生成 »</button> <button class="btn alert-secondary" id="btnCopy">一键复制</button></p>
                    <div id="history" class="btn-group" role="group" aria-label="Basic example"></div>
                    <hr>

                    <hr>
                    <textarea id="genCodeArea" class="form-control btn-lg" ></textarea>
                </div>
            </div>
        </div>
    </div>
</div>




    <#--<@netCommon.commonFooter />-->
</body>
</html>
