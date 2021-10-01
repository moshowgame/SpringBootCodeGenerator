var baseURL = "../../";

//工具集合Tools
window.T = {};

// 获取请求参数
// 使用示例
// location.href = http://localhost:8080/index.html?id=123
// T.p('id') --> 123;
var url = function(name) {
	var reg = new RegExp("(^|&)"+ name +"=([^&]*)(&|$)");
	var r = window.location.search.substr(1).match(reg);
	if(r!=null)return  unescape(r[2]); return null;
};
T.p = url;

//全局配置
$.ajaxSetup({
	dataType: "json",
	cache: false
});

//重写alert
window.alert = function(msg, callback){
	//重写为msg而不是alert
	parent.layer.msg(msg, {icon: 6});
}
window.error = function(msg, callback){
	//重写为msg而不是alert
	parent.layer.msg(msg, {icon: 5});
}
//重写confirm式样框
window.confirm = function(msg, callback){
	parent.layer.confirm(msg, {btn: ['确定','取消']},
	function(){//确定事件
		if(typeof(callback) === "function"){
			callback("ok");
		}
	});
}
//判断是否为空
function isBlank(value) {
    return !value || !/\S/.test(value)
}

function setCookie(key, val, expire_second) {
	var d = new Date();
	var expires ="";
	if (expire_second){
		d.setDate(d.getTime()+(expire_second*1000));
		expires='; expires=' + d.toGMTSring();
	}
	document.cookie = key + "="+ val + expires;
}

function getCookie(name) {
	var data = "";
	if (document.cookie){
		var arr = document.cookie.split(';');
		for (var str of arr) {
			var temp = str.split("=")
			if (temp[0].replace(/(^\s*)/g,'') === name){
				data = unescape(temp[1]);
				break
			}
		}
	}
	return data;
}