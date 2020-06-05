import org.apache.camel.AggregationStrategy;
import org.apache.camel.CamelContext;
import org.apache.camel.ConsumerTemplate;
import org.apache.camel.Exchange;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.DefaultCamelContext;

public class EnrichAggregationEx {

public static void main(String[] args) throws Exception {
	
	CamelContext con = new DefaultCamelContext();
	con.addRoutes(new RouteBuilder() {
		
		@Override
		public void configure() throws Exception {
			// TODO Auto-generated method stub
			AggregationStrategy agg = new AggregationStrategy() {
				
				@Override
				public Exchange aggregate(Exchange oldExchange, Exchange newExchange) {

				
						// TODO Auto-generated method stub
						Object originalBody = oldExchange.getIn().getBody();
						System.out.println(">> " +originalBody);
				        Object resourceResponse = newExchange.getIn().getBody();
				        Object mergeResult = null ;//=  // combine original body and resource response

				        if (oldExchange.getPattern().isOutCapable()) {
				        	oldExchange.getOut().setBody(mergeResult);
				        } else {
				        	oldExchange.getIn().setBody(mergeResult);
				        }
				        
				        return oldExchange;
					
				}
			};
			
			from("direct:start")
			.enrich("direct:resource", agg).log("${body}")
			.from("direct:resource")
			.to("seda:end");
			
			con.start();
		}
	});
	
	ProducerTemplate pt = con.createProducerTemplate();
	pt.sendBody("direct:start", "123456");
	System.out.println("\n\nsent message as >> 123456" );
	
	ConsumerTemplate ct = con.createConsumerTemplate();
	String recvdString = ct.receiveBody("seda:end",  String.class);
	
	System.out.println("\nrecieved message as >> "+ recvdString);

	}
}
