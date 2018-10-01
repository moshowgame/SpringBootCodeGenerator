<#macro commonStyle>

<#-- favicon -->
<link rel="icon" href="favicon.ico" />

<meta charset="utf-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<!-- Tell the browser to be responsive to screen width -->
<meta content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no" name="viewport">
<!-- Bootstrap 4 -->
<link href="https://cdn.bootcss.com/bootstrap/4.1.1/css/bootstrap.min.css" rel="stylesheet">
<!-- Font Awesome -->
<link href="https://cdn.bootcss.com/font-awesome/4.7.0/css/font-awesome.min.css" rel="stylesheet">
<!-- Ionicons -->
<link href="https://cdn.bootcss.com/ionicons/4.1.2/css/ionicons.min.css" rel="stylesheet">

<link href="https://cdn.bootcss.com/codemirror/5.38.0/codemirror.min.css" rel="stylesheet">

<!-- HTML5 Shim and Respond.js IE8 support of HTML5 elements and media queries -->
<!-- WARNING: Respond.js doesn't work if you view the page via file:// -->
<!--[if lt IE 9]>
<script src="https://oss.maxcdn.com/html5shiv/3.7.3/html5shiv.min.js"></script>
<script src="https://oss.maxcdn.com/respond/1.4.2/respond.min.js"></script>
<![endif]-->

<script>var base_url = '${request.contextPath}';</script>

</#macro>

<#macro commonScript>

<!-- jQuery -->
<script src="https://cdn.bootcss.com/jquery/3.3.1/jquery.min.js"></script>
<!-- Bootstrap -->
<script src="https://cdn.bootcss.com/bootstrap/4.1.1/js/bootstrap.min.js"></script>
<!-- FastClick -->
<script src="https://cdn.bootcss.com/fastclick/1.0.6/fastclick.min.js"></script>
<#-- jquery.slimscroll -->
<script src="https://cdn.bootcss.com/jQuery-slimScroll/1.3.8/jquery.slimscroll.min.js"></script>
<#-- layer -->
<#--<script src="https://cdn.bootcss.com/layer/3.1.0/layer.js"></script>-->
<#--<script src="http://res.layui.com/layui/dist/layui.js""></script>-->
<script src="https://cdn.bootcss.com/codemirror/5.38.0/codemirror.min.js"></script>
<script src="https://cdn.bootcss.com/codemirror/5.38.0/addon/display/placeholder.min.js"></script>
<script src="https://cdn.bootcss.com/codemirror/5.38.0/mode/clike/clike.min.js"></script>
<script src="https://cdn.bootcss.com/codemirror/5.38.0/mode/sql/sql.min.js"></script>
<script src="https://cdn.bootcss.com/codemirror/5.38.0/mode/xml/xml.min.js"></script>
</#macro>


<#macro commonFooter >
<footer class="main-footer">
    <div class="container">
        Powered by <b>Spring Boot Code Generator</b> base on XXL Code Generator
        <div class="pull-right hidden-xs">
            <strong>Copyright &copy; 2018-${.now?string('yyyy')} &nbsp;
                <a href="https://github.com/moshowgame/SpringBootCodeGenerator" target="_blank" >SpringBootCodeGenerator</a>
                <a href="https://github.com/xuxueli/xxl-code-generator" target="_blank" >xxl-code-generator</a>
            </strong><!-- All rights reserved. -->
        </div>
    </div>
</footer>
</#macro>