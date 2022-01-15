package demo;

import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MainRouteBuilder extends RouteBuilder {

	@Autowired
	private MainConfiguration configuration;
	
	@Override
	public void configure() throws Exception {

		fromF("jetty:http://0.0.0.0:%d/joke", configuration.getPort())
		.removeHeaders("*")
		.setHeader(Exchange.HTTP_METHOD).constant("GET")
		.setBody().constant("")
		.to("https://api.chucknorris.io/jokes/random")
		.convertBodyTo(String.class)
		.unmarshal().json(JsonLibrary.Jackson)
		.setBody().jsonpath("$.value")
		.end();
		
		fromF("jetty:http://0.0.0.0:%d/categories", configuration.getPort())
		.removeHeaders("*")
		.setHeader(Exchange.HTTP_METHOD).constant("GET")
		.setBody().constant("")
		.to("https://api.chucknorris.io/jokes/categories")
		.convertBodyTo(String.class)
		.unmarshal().json(JsonLibrary.Jackson)
		.setBody().jsonpath("$")
		.end();
		
		fromF("jetty:http://0.0.0.0:%d/jokes/?matchOnUriPrefix=true", configuration.getPort())
		.setHeader("category").header(Exchange.HTTP_PATH)
		.removeHeaders("*", "category")
		.setHeader(Exchange.HTTP_METHOD).constant("GET")
		.setBody().constant("")
		.setHeader(Exchange.HTTP_QUERY)
		.simple("category=${header.category}")
		//.to("log:DEBUG?showBody=true&showHeaders=true")
		.to("https://api.chucknorris.io/jokes/random")
		.convertBodyTo(String.class)
		.unmarshal().json(JsonLibrary.Jackson)
		.setBody().jsonpath("$.value")
		.end();
		
		fromF("jetty:http://0.0.0.0:%d/search/?matchOnUriPrefix=true", configuration.getPort())
		.setHeader("query").header(Exchange.HTTP_PATH)
		.removeHeaders("*", "query")
		.setHeader(Exchange.HTTP_METHOD).constant("GET")
		.setBody().constant("")
		.setHeader(Exchange.HTTP_QUERY)
		.simple("query=${header.query}")
		//.to("log:DEBUG?showBody=true&showHeaders=true")
		.to("https://api.chucknorris.io/jokes/search")
		.convertBodyTo(String.class)
		.unmarshal().json(JsonLibrary.Jackson)
		.setBody().jsonpath("$.result")
		.setBody(exchange -> {
			List<Object> items = exchange.getIn().getBody(List.class);
			if (items.size() > 0) {
				String[] list = new String[items.size()];
				for (int i = 0; i < items.size(); i++) {
					Map<String, Object> item = (Map<String, Object>) items.get(i);
					Result result = new Result(item.get("categories"), item.get("value"));
					list[i] = result.toString();
				}
				return list;
			}

			return new String[] {null, null};
		})
		.end();
	}

}
