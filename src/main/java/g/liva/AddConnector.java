package g.liva;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;

import java.io.IOException;

import static org.toilelibre.libe.curl.Curl.curl;
import static org.toilelibre.libe.curl.Curl.$;

public class AddConnector {

    private String url;

    public AddConnector(String url) {
        this.url = url;
    }

    public HttpResponse AddConnector(String config) {
        String cmd = String.format("curl -X POST -H \"Accept:application/json\" -H \"Content-Type:application/json\" http://%s/connectors/ -d '%s'", this.url, config);
        HttpResponse response;
        System.out.println(cmd);
        response = curl(cmd);
        return response;
    }

    public static void usage() throws IOException {
        String exampleConfig = "{\"name\": \"jdbc-connector\",\n" +
                "    \"config\": {\n" +
                "      \"connector.class\": \"io.confluent.connect.jdbc.JdbcSourceConnector\",\n" +
                "      \"timestamp.column.name\": \"date\",\n" +
                "      \"incrementing.column.name\": \"id\",\n" +
                "      \"connection.user\": \"example\",\n" +
                "      \"connection.password\": \"example\",\n" +
                "      \"validate.non.null\": \"true\",\n" +
                "      \"tasks.max\": \"1\",\n" +
                "      \"table.whitelist\": \"account\",\n" +
                "      \"mode\": \"incrementing\",\n" +
                "      \"topic.prefix\": \"db_connector\",\n" +
                "      \"poll.interval.ms\": \"1\",\n" +
                "      \"name\": \"jdbc-connector\",\n" +
                "      \"connection.url\": \"jdbc:postgresql://143.205.114.69:5432/postgres\"\n" +
                "    } }";
        exampleConfig = exampleConfig.replace("\n","");
        exampleConfig = exampleConfig.replace(" ","");
        exampleConfig = exampleConfig.replace("\t","");
        System.out.println(exampleConfig);
        AddConnector ac = new AddConnector("localhost:8083");
        HttpResponse r = ac.AddConnector(exampleConfig);
        HttpEntity e = r.getEntity();
        System.out.println(EntityUtils.toString(e));
    }
}