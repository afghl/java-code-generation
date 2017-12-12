import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;
import org.codehaus.jackson.map.ObjectMapper;

import javax.lang.model.element.Modifier;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public class GenerateShopBase {
    public static void main(String[] args) throws Exception {
        Map spec = getSpec();
        List<Map> attrSpecs = (List<Map>) spec.get("attrs");

        TypeSpec.Builder clazz = TypeSpec.classBuilder((String) spec.get("name"))
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL);

        attrSpecs.forEach(attrSpec -> {
            FieldSpec fieldSpec;
            if (attrSpec.get("type").equals("enum")) {
                fieldSpec = generateEnum(attrSpec);
            } else {
                fieldSpec = FieldSpec.builder(attrSpec.get("type").equals("String") ? String.class : Double.class, (String) attrSpec.get("fieldName")).addModifiers(Modifier.PRIVATE).build();
            }

            clazz.addField(fieldSpec);
        });
        JavaFile javaFile = JavaFile.builder(String.format("com.example.%s", spec.get("name")), clazz.build())
                .build();

        javaFile.writeTo(System.out);
    }

    private static FieldSpec generateEnum(Map attrSpec) {
        FieldSpec fieldSpec = FieldSpec.builder(String.class, (String) attrSpec.get("fieldName")).addModifiers(Modifier.PRIVATE).build();
        return fieldSpec;
    }

    private static Map getSpec() throws IOException {
        String s = "{\"name\":\"ShopBase\",\"type\":\"single\",\"attrs\":[{\"fieldName\":\"name\",\"type\":\"String\"},{\"fieldName\":\"longitude\",\"type\":\"Double\"},{\"fieldName\":\"latitude\",\"type\":\"Double\"},{\"fieldName\":\"status\",\"type\":\"enum\",\"values\":[\"VALID\",\"INVALID\"]}]}";

        ObjectMapper objectMapper = new ObjectMapper();
        Map map = objectMapper.readValue(s, Map.class);

        System.out.println(map);
        return map;
    }
}
