package org.tonylin.wiremock.redfish.extensions;

import java.util.Objects;

import com.github.tomakehurst.wiremock.client.ResponseDefinitionBuilder;
import com.github.tomakehurst.wiremock.common.FileSource;
import com.github.tomakehurst.wiremock.extension.Parameters;
import com.github.tomakehurst.wiremock.extension.StubMappingTransformer;
import com.github.tomakehurst.wiremock.http.Fault;
import com.github.tomakehurst.wiremock.http.ResponseDefinition;
import com.github.tomakehurst.wiremock.stubbing.StubMapping;

public class FaultStubMappingTransformer extends StubMappingTransformer {


    @Override
    public String getName() {
        return "FaultStubMappingTransformer";
    }

    private Fault getFault(ResponseDefinition responseDef) {
    	String body = Objects.toString(responseDef.getBody(), "");
    	try {
			return Fault.valueOf(body);
    	} catch (IllegalArgumentException e) {
			return null;
		}
    }
    
    private void setupFaultResponseDef(StubMapping stubMapping) {
    	ResponseDefinition responseDef = stubMapping.getResponse();
    	Fault fault = getFault(responseDef);
    	if( fault == null )
    		return;
    	
    	ResponseDefinition faultResponseDef = ResponseDefinitionBuilder.like(responseDef).withFault(fault).build();
		stubMapping.setResponse(faultResponseDef);
    }
    
	@Override
	public StubMapping transform(StubMapping stubMapping, FileSource files, Parameters parameters) {
		ResponseDefinition responseDef = stubMapping.getResponse();
		
		if( responseDef.getStatus() == 500) {
			setupFaultResponseDef(stubMapping);
		}
		return stubMapping;
	}
}
