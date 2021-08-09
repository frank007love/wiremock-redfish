package org.tonylin.wiremock.redfish.extensions.redfishevent;

import com.github.tomakehurst.wiremock.common.FileSource;
import com.github.tomakehurst.wiremock.extension.Parameters;
import com.github.tomakehurst.wiremock.extension.StubMappingTransformer;
import com.github.tomakehurst.wiremock.stubbing.StubMapping;

public class RedfishEventStubMappingTransformer extends StubMappingTransformer {

	@Override
	public String getName() {
		return "RedfishEventStubMappingTransformer";
	}

	@Override
	public StubMapping transform(StubMapping stubMapping, FileSource files, Parameters parameters) {
		// TODO Auto-generated method stub
		return null;
	}

}
