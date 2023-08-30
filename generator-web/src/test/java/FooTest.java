import com.softdev.system.generator.util.StringUtils;

public class FooTest {

    public static void main(String[] args) {
        // String updateTime = StringUtils.underlineToCamelCase("updateTime");
        // System.out.println(updateTime);


        // System.out.println(CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, "userName"));
        // System.out.println(CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, "userNAme-UUU"));

        System.out.println(StringUtils.toUnderline("userName",false));
        System.out.println(StringUtils.toUnderline("UserName",false));
        System.out.println(StringUtils.toUnderline("user_NameGgg_x-UUU",false));
        System.out.println(StringUtils.toUnderline("username",false));

        System.out.println(StringUtils.toUnderline("userName",true));
        System.out.println(StringUtils.toUnderline("UserName",true));
        System.out.println(StringUtils.toUnderline("user_NameGgg_x-UUU",true));
        System.out.println(StringUtils.toUnderline("username",true));
    }


}
