import org.apache.camel.CamelContext;
import org.apache.camel.ConsumerTemplate;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.DefaultCamelContext;

public class PollEnrichExample {
	
	public static void main(String[] abc) throws Exception{
		
		CamelContext context = new DefaultCamelContext();
		context.addRoutes(new RouteBuilder() {
			
			@Override
			public void configure() throws Exception {
				// TODO Auto-generated method stub
				 
				//************Below is the sample code for poll enricher********			
				from("direct:start")
				  .pollEnrich("file:input?fileName=data.txt")
				  .to("direct:result");
				
				from("direct:result")
				.to("seda:result");
				
				context.start();
				
			}
		});
		
		ProducerTemplate pt = context.createProducerTemplate();
		pt.sendBody("direct:start", "123456");
		System.out.println("\n\nsent message as >> 123456" );
		
		ConsumerTemplate ct = context.createConsumerTemplate();
		String recvdString = ct.receiveBody("seda:result",  String.class);
		
		System.out.println("\nrecieved message as >> "+ recvdString);
		
	}

}
