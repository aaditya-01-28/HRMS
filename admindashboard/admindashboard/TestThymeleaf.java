import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.FileTemplateResolver;

public class TestThymeleaf {
    public static void main(String[] args) {
        try {
            FileTemplateResolver resolver = new FileTemplateResolver();
            resolver.setTemplateMode(TemplateMode.HTML);
            resolver.setPrefix("src/main/resources/templates/");
            resolver.setSuffix(".html");
            resolver.setCacheable(false);
            
            TemplateEngine engine = new TemplateEngine();
            engine.setTemplateResolver(resolver);
            
            Context context = new Context();
            String result = engine.process("senior_hr-lms", context);
            System.out.println("Success! Output length: " + result.length());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
