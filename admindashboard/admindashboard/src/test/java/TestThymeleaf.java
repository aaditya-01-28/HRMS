import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.templateresolver.FileTemplateResolver;
import org.thymeleaf.exceptions.TemplateInputException;

public class TestThymeleaf {
    public static void main(String[] args) {
        FileTemplateResolver resolver = new FileTemplateResolver();
        resolver.setPrefix("d:/WCG/HRMS_WCG/admindashboard/admindashboard/src/main/resources/templates/");
        resolver.setSuffix(".html");
        resolver.setTemplateMode("HTML");
        resolver.setCacheable(false);

        TemplateEngine engine = new TemplateEngine();
        engine.setTemplateResolver(resolver);

        try {
            engine.process("hr-workflow", new Context());
            System.out.println("Success! No parsing errors.");
        } catch (TemplateInputException e) {
            System.err.println("TemplateInputException: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
