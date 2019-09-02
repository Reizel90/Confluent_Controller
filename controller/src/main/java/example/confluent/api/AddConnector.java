package example.confluent.api;

import org.apache.http.HttpResponse;

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
}
