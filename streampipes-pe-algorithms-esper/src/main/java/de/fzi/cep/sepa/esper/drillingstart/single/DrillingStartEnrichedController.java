package de.fzi.cep.sepa.esper.drillingstart.single;


import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import de.fzi.cep.sepa.commons.Utils;
import de.fzi.cep.sepa.esper.config.EsperConfig;
import de.fzi.cep.sepa.sdk.helpers.EpRequirements;
import de.fzi.cep.sepa.model.impl.EpaType;
import de.fzi.cep.sepa.model.impl.EventSchema;
import de.fzi.cep.sepa.model.impl.EventStream;
import de.fzi.cep.sepa.model.impl.Response;
import de.fzi.cep.sepa.model.impl.eventproperty.EventProperty;
import de.fzi.cep.sepa.model.impl.eventproperty.EventPropertyPrimitive;
import de.fzi.cep.sepa.model.impl.graph.SepaDescription;
import de.fzi.cep.sepa.model.impl.graph.SepaInvocation;
import de.fzi.cep.sepa.model.impl.output.AppendOutputStrategy;
import de.fzi.cep.sepa.model.impl.output.OutputStrategy;
import de.fzi.cep.sepa.model.impl.staticproperty.FreeTextStaticProperty;
import de.fzi.cep.sepa.model.impl.staticproperty.MappingPropertyUnary;
import de.fzi.cep.sepa.model.impl.staticproperty.StaticProperty;
import de.fzi.cep.sepa.model.util.SepaUtils;
import de.fzi.cep.sepa.model.vocabulary.MhWirth;
import de.fzi.cep.sepa.model.vocabulary.XSD;
import de.fzi.cep.sepa.runtime.flat.declarer.FlatEpDeclarer;
import de.fzi.cep.sepa.client.util.StandardTransportFormat;

public class DrillingStartEnrichedController extends FlatEpDeclarer<DrillingStartEnrichedParameters>{

	@Override
	public Response invokeRuntime(SepaInvocation sepa) {
		
		int minRpm = Integer.parseInt(SepaUtils.getFreeTextStaticPropertyValue(sepa, "rpm"));
		int minTorque = Integer.parseInt(SepaUtils.getFreeTextStaticPropertyValue(sepa, "torque"));
		
		String latPropertyName = SepaUtils.getMappingPropertyName(sepa, "rpm");
		String lngPropertyName = SepaUtils.getMappingPropertyName(sepa, "torque");	
	
		System.out.println(minRpm +", " +minTorque +", " +latPropertyName +", " +lngPropertyName);
		DrillingStartEnrichedParameters staticParam = new DrillingStartEnrichedParameters(
				sepa, 
				minRpm,
				minTorque,
				latPropertyName,
				lngPropertyName);

		return submit(staticParam, DrillingStartEnriched::new, sepa);

	}
	
	@Override
	public SepaDescription declareModel() {
		EventStream stream1 = new EventStream();
		
		EventSchema schema1 = new EventSchema();
		EventPropertyPrimitive p1 = EpRequirements.domainPropertyReq(MhWirth.Rpm);
		schema1.addEventProperty(p1);
		
		EventPropertyPrimitive p2 = EpRequirements.domainPropertyReq(MhWirth.Torque);
		schema1.addEventProperty(p2);
		
		
		SepaDescription desc = new SepaDescription("drillingstartenriched", "Drilling Status", "Detects a status change in a drilling process (drilling and cooling)");
		desc.setIconUrl(EsperConfig.iconBaseUrl + "/Drilling_Start_HQ.png");
		desc.setCategory(Arrays.asList(EpaType.ALGORITHM.name()));
		
		stream1.setUri(EsperConfig.serverUrl +"/" +Utils.getRandomString());
		stream1.setEventSchema(schema1);
		desc.addEventStream(stream1);
		
		List<OutputStrategy> strategies = new ArrayList<OutputStrategy>();
		List<EventProperty> appendProperties = new ArrayList<EventProperty>();			
		
		EventProperty result = new EventPropertyPrimitive(XSD._boolean.toString(),
				"drillingStatus", "", de.fzi.cep.sepa.commons.Utils.createURI(MhWirth.DrillingStatus));;
	
		appendProperties.add(result);
		strategies.add(new AppendOutputStrategy(appendProperties));
		desc.setOutputStrategies(strategies);
		
		List<StaticProperty> staticProperties = new ArrayList<StaticProperty>();
		
		FreeTextStaticProperty rpmThreshold = new FreeTextStaticProperty("rpm", "RPM threshold", "");
		FreeTextStaticProperty torqueThreshold = new FreeTextStaticProperty("torque", "Torque threshold", "");
		staticProperties.add(rpmThreshold);
		staticProperties.add(torqueThreshold);
		
		staticProperties.add(new MappingPropertyUnary(URI.create(p1.getElementName()), "rpm", "Select RPM Mapping", ""));
		staticProperties.add(new MappingPropertyUnary(URI.create(p2.getElementName()), "torque", "Select Torque Mapping", ""));
		desc.setStaticProperties(staticProperties);
		desc.setSupportedGrounding(StandardTransportFormat.getSupportedGrounding());
		return desc;
	}
	
}