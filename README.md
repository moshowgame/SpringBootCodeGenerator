![image](https://img.shields.io/badge/SpringBoot-%E2%98%85%E2%98%85%E2%98%85-green.svg)
![image](https://img.shields.io/badge/CodeGenerator-%E2%98%85%E2%98%85%E2%98%85-green.svg)
SpringBootCodeGenerator
----
基于SpringBoot2+xxl-codegenerator的代码生成器。用于生成mybatis和jpa相关代码，目前jpa的为主，mybatis的后续进行优化。
<br><br>
感谢bejson三叔将他部署在http://java.bejson.com/generator上
<br><br>
<table><tbody>
<tr><td>访问路径</td> <td>http://127.0.0.1:1234/generator</td></tr>
<tr><td>在线地址</td> <td>http://java.bejson.com/generator</td></tr>
<tr><td>CSDN博客</td> <td>http://blog.csdn.net/moshowgame</td></tr>
<tr><td></td> <td></td></tr>
<tr><td>更新日期</td> <td>更新内容</td></tr>
<tr><td>20180916-2<td>优化oracle支持，空格/"/'的支持，以及多余内容的剔除。</td></tr>
<tr><td>20180916-1<td>补充char/clob/blob/json等类型，如果类型未知，默认为String。</td></tr>
<tr><td>20180915<td>新增Swagger-UI模板。修复一些命名和导入问题。JPA的Entity默认第一个字段为Id，如果不是请手工修改。</td></tr>
<tr><td>20180913<td>修复字段没有描述以及类型为DATE型导致的问题。新增JPA的Controller模板。</td></tr>
<tr><td>20180831<td>初始化项目。新增JPA系列Entity+Repository模板。</td></tr>
</tbody></table>
<img src="./codegenerator1.png">
<img src="./codegenerator2.png">
<img src="./codegenerator3.png">
<img src="./codegenerator4.png">
<table>