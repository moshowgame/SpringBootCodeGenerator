![image](https://img.shields.io/badge/SpringBoot-%E2%98%85%E2%98%85%E2%98%85-green.svg)
![image](https://img.shields.io/badge/CodeGenerator-%E2%98%85%E2%98%85%E2%98%85-green.svg)
SpringBootCodeGenerator
----
基于SpringBoot2+xxl-codegenerator的代码生成器。用DDL语句生成JPA/JdbcTemplate/Mybatis相关代码，目前以JPA的为主，各大模板也在陆续优化。
<br><br>
感谢bejson三叔将他部署在http://java.bejson.com/generator上
<br><br>
<table><tbody>
<tr><td>访问路径</td> <td>http://127.0.0.1:1234/generator</td></tr>
<tr><td>在线地址</td> <td>http://java.bejson.com/generator</td></tr>
<tr><td>CSDN博客</td> <td>http://blog.csdn.net/moshowgame</td></tr>
<tr><td></td> <td></td></tr>
<tr><td>更新日期</td> <td>更新内容</td></tr>
<tr><td>20180917<td>全新首页，静态文件全部采用CDN。新增jdbcTemplate模块。</td></tr>
<tr><td>20180916-2<td>优化oracle支持，优化DDL语句中"或者'或者空格的支持。</td></tr>
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