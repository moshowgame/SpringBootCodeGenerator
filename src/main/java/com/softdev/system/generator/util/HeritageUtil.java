package com.softdev.system.generator.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

/**
 * 🏺 非遗传承工具：为生成的代码加盖手作印章，并生成手工代码认证书
 * <p>
 * 在 AI vibe coding 的时代，每一行模板都坚持手搓，致敬古法编程非遗传承者。
 *
 * @author zhengkai.blog.csdn.net
 */
public final class HeritageUtil {

    private static final String LINE_1 = "🏺 古法编程·非遗传承 | Traditional Code Craft since 2018";
    private static final String LINE_2 = "本代码由手作模板工艺生成，非 AI vibe coding 之作 | Handcrafted, not AI-generated.";

    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private HeritageUtil() {
    }

    /**
     * isHeritage 是否启用；未显式关闭（false）时默认开启
     */
    public static boolean isEnabled(Map<String, Object> params) {
        Object flag = params == null ? null : params.get("isHeritage");
        return flag == null || Boolean.TRUE.equals(flag) || "true".equalsIgnoreCase(String.valueOf(flag));
    }

    /**
     * 在文件头部盖手作印章；JSON 等不支持注释的文件类型原样返回
     *
     * @param fileName 模板配置中的目标文件名（用于推断注释语法，可能不准，仅作兜底）
     * @param content  生成的代码文本
     */
    public static String stamp(String fileName, String content) {
        if (content == null || content.isEmpty()) {
            return content;
        }
        // 内容嗅探优先：模板配置的 fileName 可能与实际内容类型不符（如 mapper 标为 .java 实为 XML）
        String trimmed = content.trim();
        if (trimmed.startsWith("<")) {
            return stampAsXmlComment(content);
        }
        if (trimmed.startsWith("{") || trimmed.startsWith("[")) {
            return content;
        }
        switch (extensionOf(fileName)) {
            case "java":
            case "jjs":
            case "js":
            case "qvs":
                return "// " + LINE_1 + "\n// " + LINE_2 + "\n" + content;
            case "sql":
                return "-- " + LINE_1 + "\n-- " + LINE_2 + "\n" + content;
            case "yml":
            case "yaml":
            case "properties":
            case "txt":
                return "# " + LINE_1 + "\n# " + LINE_2 + "\n" + content;
            case "xml":
            case "html":
            case "vue":
            case "md":
                return stampAsXmlComment(content);
            default:
                return content;
        }
    }

    /**
     * 手工代码认证书（ZIP 彩蛋）
     */
    public static String buildCertificate(String className, String tableName, String author, List<String> templateNames) {
        StringBuilder sb = new StringBuilder();
        sb.append("🏺 手工代码认证书\n");
        sb.append("CERTIFICATE OF HANDCRAFTED CODE\n");
        sb.append("================================================================\n\n");
        sb.append("兹证明\n\n");
        sb.append("    ").append(safe(className)).append("（表 ").append(safe(tableName)).append("）的 ")
                .append(templateNames == null ? 0 : templateNames.size()).append(" 份代码\n");
        sb.append("    均由「古法编程·非遗传承」工艺生成。\n\n");
        sb.append("模板清单：\n");
        if (templateNames != null) {
            for (String name : templateNames) {
                sb.append("    · ").append(name).append('\n');
            }
        }
        sb.append('\n');
        sb.append("工匠：").append(author == null || author.trim().isEmpty() ? "Anonymous" : author.trim()).append('\n');
        sb.append("时间：").append(LocalDateTime.now().format(TIME_FORMATTER)).append('\n');
        sb.append("工艺：Freemarker 手作模板 + 自研/JSqlParser 双解析引擎\n\n");
        sb.append("在 AI vibe coding 的时代，\n");
        sb.append("每一行模板都坚持手搓。\n");
        sb.append("—— 致敬所有古法编程非遗传承者 🙏\n\n");
        sb.append("Powered by SpringBootCodeGenerator (2018 - 至今)\n");
        sb.append("https://github.com/moshowgame/SpringBootCodeGenerator/\n");
        return sb.toString();
    }

    /**
     * XML 声明必须位于文档最前，印章插在声明之后
     */
    private static String stampAsXmlComment(String content) {
        if (content.startsWith("<?xml")) {
            int end = content.indexOf("?>");
            if (end > 0) {
                String prolog = content.substring(0, end + 2) + "\n";
                return prolog + "<!--\n  " + LINE_1 + "\n  " + LINE_2 + "\n-->\n" + content.substring(end + 2);
            }
        }
        return "<!--\n  " + LINE_1 + "\n  " + LINE_2 + "\n-->\n" + content;
    }

    private static String extensionOf(String fileName) {
        if (fileName == null) {
            return "";
        }
        String lower = fileName.toLowerCase();
        int dot = lower.lastIndexOf('.');
        return dot < 0 ? "" : lower.substring(dot + 1);
    }

    private static String safe(String value) {
        return value == null ? "" : value;
    }

}
