# SpringBootCodeGenerator
![image](https://img.shields.io/badge/SpringBoot2-%E2%98%85%E2%98%85%E2%98%85%E2%98%85%E2%98%85-blue.svg)
![image](https://img.shields.io/badge/Freemarker-%E2%98%85%E2%98%85%E2%98%85%E2%98%85%E2%98%85-blue.svg)
![image](https://img.shields.io/badge/CodeGenerator-%E2%98%85%E2%98%85%E2%98%85%E2%98%85%E2%98%85-blue.svg)
[![Build Status](https://travis-ci.org/moshowgame/SpringBootCodeGenerator.svg?branch=master)](https://travis-ci.org/moshowgame/SpringBootCodeGenerator)


# Description
- √ 基于SpringBoot2+Freemarker+Bootstrap
- √ 以解放双手为目的，减少大量重复的CRUD工作
- √ 支持mysql/oracle/pgsql三大数据库
- √ 用DDL-SQL语句生成JPA/JdbcTemplate/Mybatis/MybatisPlus/BeetlSQL相关代码.

# Advantage 
- 支持DDL SQL/INSERT SQL/SIMPLE JSON生成模式
- 自动记忆最近生成的内容，最多保留9个
- 提供众多通用模板，易于使用，复制粘贴加简单修改即可完成CRUD操作
- 支持特殊字符模板(`#`请用`井`代替;`$`请用`￥`代替)
- 根据comment=(mysql)或者comment on table(pgsql/oracle)生成类名注释

# Url

|访问路径|http://localhost:1234/generator|
|----|----|
|在线地址|http://java.bejson.com/generator|
|CSDN博客|http://zhengkai.blog.csdn.net|
|最新Jar包|https://github.com/moshowgame/SpringBootCodeGenerator/releases|

感谢bejson三叔将他部署在[BEJSON](www.bejson.com)上，目前是besjon专供的金牌工具(线上版本不一定是最新的，会有延迟，请谅解，谢谢).


# Update

|更新日期|更新内容|
|----|----|
|20200621|修复FreemarkerUtil的Path问题导致JAR包运行时无法获取template的问题。|
|20200525|1.一些fix,关于封装工具类以及layui模板优化等.<br> 2.优化表备注的获取逻辑.<br> 3.生成时间格式改为yyyy-MM-dd,移除具体的时间,只保留日期|
|20200522|1.新增insert-sql模式,支持对"insert into table (xxx) values (xxx)"语句进行处理,生成java代码(感谢三叔的建议).|
|20200517|1.代码重构！异常处理优化,Freemarker相关工具类优化,简化模板生成部分,通过template.json来配置需要生成的模板,不需要配置java文件.<br> 2.修复包含comment关键字时注释无法识别的问题.(感谢@1nchaos的反馈).<br> 3.赞赏优化,感谢大家的赞赏.<br> 4.新增mapper2(Mybatis-Annotation模板)(感谢@baisi525和@CHKEGit的建议).|
|20200503|1.优化对特殊字符的处理,对于包含#和$等特殊字符的,在模板使用井和￥代替便可,escapeString方法会自动处理.<br> 2.优化mybatisplus实体类相关(感谢@chunchengmeigui的反馈).<br> 3.修优化对所有类型的判断(感谢@cnlw的反馈).<br> 4.移除swagger-entity,该功能已经包含在‘swagger-ui’的下拉选项中  <br> 5.升级hutool和lombok版本|
|20200306|1.提交一套layuimini+mybatisplus的模板.<br> 2.修复mybatisplus一些相关问题. |
|20200206|1.新增历史记录功能,自动保存最近生成的对象.<br> 2.新增swagger开关选项和修复@Column带name参数(感谢@liuyu-struggle的建议).<br> 3.去除mybatis模板中的方括号[]和修改模板里的类注释样式(感谢@gaohanghang的PR)|
|20191229|1.修复bejson安全防护策略拦截问题(感谢@liangbintao和@1808083642的反馈).<br> 2.优化字段名含date字符串的处理(感谢@smilexzh的反馈).<br> 3.控制台动态输出项目访问地址(感谢@gaohanghang的提交)|
|20191128|1.修复支持string-copy导致的以n结尾的字母不显示问题.<br> 2.jpa-entity新增swagger@ApiModel@ApiModelProperty注解和SQL字段@Column注解(感谢@yjq907的建议) |   
|20191126|1.springboot2内置tomcat更换为性能更强大的undertow.<br> 2.修复tinyintTransType参数丢失问题 |   
|20191124|1.java代码结构优化.<br> 2.新增简单的json生成模式.<br> 3.新增简单的正则表达式匹配模式(感谢@ydq的贡献).<br> 4.新增对复制String代码中的乱SQL代码的支持 5.优化对JSON的父子节点/处理,JSONObject和JSONArray节点处理,子节点缺失'{'头处理|   
|20191123|1.移除频繁出错和被过滤的layer,改为jquery-toast.<br> 2.Util功能优化,新增json和xml.|   
|20191116|优化对primary关键字的处理(感谢@liujiansgit的反馈). |   
|20191115|1.添加tinyint类型转换(感谢@lixiliang&@liujiansgit的Suggestion).<br> 2.添加一键复制功能(感谢@gaohanghang的Suggestion).<br> 3.Mybatis的insert增加keyProperty="id"用于返回自增id(感谢@88888888888888888888的Suggestion).<br> 4.优化date类型的支持(感谢@SteveLsf的反馈).<br> 5.其他一些优化. | 
|20191015|修复jdbcTemplates中insert语句第一个字段丢失的问题. |   
|20190915|1.添加对象getset模板.<br> 2.添加sql模板.<br> 3.启动类添加日志输出,方便项目使用(感谢@gaohanghang 的pull request) |   
|20190910|优化以及更新Maven依赖,减少打包体积.<br> 1.修复mapper接口load方法,但是xml中方法不匹配问题.<br> 2.移除mapper中CRUD时的@param 注解,会影响xml的解析(感谢@caojiantao的反馈).<br> 3.优化MyBatis的xml文件对Oracle的支持.(感谢@wylove1992的反馈).<br> 4.新增对boolean的处理(感谢@violinxsc的反馈)以及优化tinyint类型生成boolean类型问题(感谢@hahaYhui的反馈) |   
|20190909|添加是否下划线转换为驼峰的选择(感谢@youngking28 的pull request).|   
|20190518|1.优化注释.<br> 2.修改 mybatis模板中 controller注解.<br> 3.修改 mybatis模板中 dao文件使用为 mapper文件.<br> 4.修改 mybatis模板中 service实现类中的一个 bug.<br> 5.修改 index.ftl文件中 mybatis模板的 dao -> mapper(感谢@unqin的pull request)|
|20190511|优化mybatis模块的dao和xml模板,修改dao接口注解为@Repository,所有dao参数改为包装类,删除update语句最后的UpdateTime = NOW(),修改dao接口文件的方法注释使其更符合javaDoc的标准,修改insert语句增加插入行主键的返回,修改load的方法名为selectByPrimaryKey,修改xml的update语句新增动态if判空,修改xml的insert语句新增动态插入判空,更符合mybatisGenerator标准(感谢@Archer-Wen的贡献 ).|
|20190429|新增返回封装工具类设置.<br> 优化对oracle注释comment on column的支持(感谢@liukex反馈).<br> 优化对普通和特殊storage关键字的判断(感谢@AhHeadFloating的反馈 ).|
|20190211|提交gitignore,解决StringUtils.lowerCaseFirst潜在的NPE异常,校验修改为@RequestParam参数校验,lombok之@Data和@Slf4j优化,fix JdbcDAO模板类名显示为中文问题,WebMvcConfig整合MessageConverter,模板代码分类(感谢@liutf和@tfgzs的pull request).|
|20190210|实体生成规则切换为包装类型,不再采用基本数据类型,为实体类生成添加显示的默认构造方法(感谢@h2so的pull request).|
|20190106|修复处理number/decimal(x,x)类型的逻辑(感谢@arthaschan的反馈).<br> 修复JdbcTemplates模板两处错误(感谢@everflourish的反馈).|
|20181212|首页UI优化.<br> 新增MybatisPlus模块(感谢@三叔同事的建议).<br> 修复作者名和包名获取失败问题(感谢@Yanch1994的反馈).|
|20181122|优化正则表达式点号的处理,优化处理字段类型,对number类型增加int,long,BigDecimal的区分判断(感谢@lshz0088的指导).|
|20181108|修复非字段描述"KEY FK_xxxx (xxxx)"导致生成KEY字段情况(感谢@tornadoorz反馈).|
|20181018|支持double(x,x)的类型,以及comment里面包含一些特殊字符的处理(感谢@tanwubo的反馈).|
|20181010|CDN变更,修复CDN不稳定导致网页js报错问题.|
|20181003|新增element-ui/bootstrap生成.|
|20181002|修复公共CDN之Layer.js404问题,导致项目无法生成.|
|20180927|优化COMMENT提取逻辑,支持多种复杂情况的注释(感谢@raodeming的反馈).|
|20180926|全新BeetlSQL模块,以及一些小细节优化(感谢@三叔同事的建议).|
|20180925|优化SQL表和字段备注的推断,包括pgsql/oralce的comment on column/table情况处理等.|
|20180918|优化SQL类型推断.<br> 优化PrimaryKey判断.<br> 修复jpacontroller中Repository拼写错误问题.|
|20180917|全新首页,静态文件全部采用CDN.新增jdbcTemplate模块.|
|20180916|1.优化oracle支持,优化DDL语句中"或者'或者空格的支持.<br> 2.补充char/clob/blob/json等类型,如果类型未知,默认为String.|
|20180915|新增Swagger-UI模板.修复一些命名和导入问题.JPA的Entity默认第一个字段为Id,如果不是请手工修改.|
|20180913|修复字段没有描述以及类型为DATE型导致的问题.新增JPA的Controller模板.|
|20180831|初始化项目.新增JPA系列Entity+Repository模板.|

# ClassInfo/TableInfo
|字段名|说明|
|-|-|
|packageName|自定义的包名|
|authorName|自定义的作者名|
|returnUtil|自定义的返回Util|
|tableName|sql中的表名|
|className|java类名|
|classComment|sql表备注/java类备注|
|fieldName|字段名|
|fieldComment|字段备注|

# how to add a new template
1. code-generator中找到对应分类，新增一个.ftl文件
2. 根据类信息编写freemarker模板.ftl文件
3. 修改template.json文件，新增模板信息
4. index页面增加一个button
5. reload,test,complete

<img src="./codegenerator1.png">
<img src="./codegenerator2.png">
<img src="./codegenerator3.png">
<img src="./codegenerator4.png">
<img src="./donate.png">

